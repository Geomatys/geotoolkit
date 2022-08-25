/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2020, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.storage.geojson;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.apache.sis.feature.builder.AttributeRole;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.internal.storage.ResourceOnFileSystem;
import org.apache.sis.metadata.iso.DefaultMetadata;
import org.apache.sis.parameter.Parameters;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.DataStoreProvider;
import org.apache.sis.storage.WritableFeatureSet;
import org.geotoolkit.internal.geojson.FeatureTypeUtils;
import org.geotoolkit.internal.geojson.GeoJSONParser;
import org.geotoolkit.internal.geojson.GeoJSONUtils;
import org.geotoolkit.internal.geojson.binding.GeoJSONFeature;
import org.geotoolkit.internal.geojson.binding.GeoJSONFeatureCollection;
import org.geotoolkit.internal.geojson.binding.GeoJSONGeometry;
import org.geotoolkit.internal.geojson.binding.GeoJSONGeometry.GeoJSONGeometryCollection;
import org.geotoolkit.internal.geojson.binding.GeoJSONGeometry.GeoJSONLineString;
import org.geotoolkit.internal.geojson.binding.GeoJSONGeometry.GeoJSONMultiLineString;
import org.geotoolkit.internal.geojson.binding.GeoJSONGeometry.GeoJSONMultiPoint;
import org.geotoolkit.internal.geojson.binding.GeoJSONGeometry.GeoJSONMultiPolygon;
import org.geotoolkit.internal.geojson.binding.GeoJSONGeometry.GeoJSONPoint;
import org.geotoolkit.internal.geojson.binding.GeoJSONGeometry.GeoJSONPolygon;
import org.geotoolkit.internal.geojson.binding.GeoJSONObject;
import static org.geotoolkit.storage.geojson.GeoJSONProvider.*;
import org.locationtech.jts.geom.*;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.geometry.Envelope;
import org.opengis.metadata.Metadata;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.GenericName;

/**
 *
 * @author Quentin Boileau (Geomatys)
 * @author Johann Sorel (Geomatys)
 * @version 2.0
 * @since   2.0
 * @module
 */
public final class GeoJSONStore extends DataStore implements ResourceOnFileSystem, WritableFeatureSet {

    private static final Logger LOGGER = Logger.getLogger("org.apache.sis.storage.geojson");
    private static final String DESC_FILE_SUFFIX = "_Type.json";
    public static final String ID_PROPERTY_NAME = "id";

    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();
    private final DataStoreProvider provider;
    private final ParameterValueGroup parameters;

    private GenericName name;
    private FeatureType featureType;
    private Path descFile;
    private Path jsonFile;
    private final Integer coordAccuracy;
    private final boolean isLocal;

    public GeoJSONStore(DataStoreProvider provider, final Path path, Integer coordAccuracy)
            throws DataStoreException {
        this(provider, toParameter(path.toUri(), coordAccuracy));
    }

    public GeoJSONStore(DataStoreProvider provider, final URI uri, Integer coordAccuracy)
            throws DataStoreException {
        this(provider, toParameter(uri, coordAccuracy));
    }

    public GeoJSONStore(DataStoreProvider provider, final ParameterValueGroup params) throws DataStoreException {
        super();
        this.provider = provider;
        this.parameters = params;
        this.coordAccuracy = (Integer) params.parameter(COORDINATE_ACCURACY.getName().toString()).getValue();

        final URI uri = (URI) params.parameter(PATH.getName().toString()).getValue();

        //FIXME
        this.isLocal = "file".equalsIgnoreCase(uri.getScheme());

        Path tmpFile = null;
        try {
            tmpFile = Paths.get(uri);
        } catch (FileSystemNotFoundException ex) {
            throw new DataStoreException(ex);
        }

        final String fileName = tmpFile.getFileName().toString();
        if (fileName.endsWith(DESC_FILE_SUFFIX)) {
            this.descFile = tmpFile;
            this.jsonFile = descFile.resolveSibling(fileName.replace(DESC_FILE_SUFFIX, ".json"));
        } else {
            this.jsonFile = tmpFile;
            //search for description json file
            String typeName = GeoJSONUtils.getNameWithoutExt(jsonFile);
            this.descFile = jsonFile.resolveSibling(typeName + DESC_FILE_SUFFIX);
        }
    }

    private static ParameterValueGroup toParameter(final URI uri, Integer coordAccuracy) {
        final Parameters params = Parameters.castOrWrap(GeoJSONProvider.PARAMETERS_DESCRIPTOR.createValue());
        params.getOrCreate(GeoJSONProvider.PATH).setValue(uri);
        params.getOrCreate(GeoJSONProvider.COORDINATE_ACCURACY).setValue(coordAccuracy);
        return params;
    }

    @Override
    public Optional<ParameterValueGroup> getOpenParameters() {
        return Optional.of(parameters);
    }

    @Override
    public Metadata getMetadata() throws DataStoreException {
        return new DefaultMetadata();
    }

    @Override
    public DataStoreProvider getProvider() {
        return provider;
    }

    @Override
    public Optional<GenericName> getIdentifier() throws DataStoreException {
        checkTypeExist();
        return Optional.of(name);
    }

    @Override
    public FeatureType getType() throws DataStoreException {
        checkTypeExist();
        return featureType;
    }

    public boolean isWritable() throws DataStoreException {
        return isLocal && Files.isWritable(descFile) && Files.isWritable(jsonFile);
    }

    private void checkTypeExist() throws DataStoreException {
        if (name == null || featureType == null) {
            try {
                // try to parse file only if exist and not empty
                if (Files.exists(jsonFile) && Files.size(jsonFile) != 0) {
                    featureType = readType();
                    name = featureType.getName();
                }
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, e.getMessage(), e);
            }
        }
    }

    /**
     * Read FeatureType from a JSON-Schema file if exist or directly from the
     * input JSON file.
     *
     * @return
     * @throws DataStoreException
     * @throws IOException
     */
    private FeatureType readType() throws DataStoreException, IOException {
        if (Files.exists(descFile) && Files.size(descFile) != 0) {
            // build FeatureType from description JSON.
            return FeatureTypeUtils.readFeatureType(descFile);
        } else {
            if (Files.exists(jsonFile) && Files.size(jsonFile) != 0) {
                final String name = GeoJSONUtils.getNameWithoutExt(jsonFile);

                final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
                ftb.setName(name);

                // build FeatureType from the first Feature of JSON file.
                final GeoJSONObject obj = GeoJSONParser.parse(jsonFile, true);
                if (obj == null) {
                    throw new DataStoreException("Invalid GeoJSON file " + jsonFile.toString());
                }

                CoordinateReferenceSystem crs = GeoJSONUtils.getCRS(obj);

                if (obj instanceof GeoJSONFeatureCollection) {
                    GeoJSONFeatureCollection jsonFeatureCollection = (GeoJSONFeatureCollection) obj;
                    if (!jsonFeatureCollection.hasNext()) {
                        //empty FeatureCollection error ?
                        throw new DataStoreException("Empty GeoJSON FeatureCollection " + jsonFile.toString());
                    } else {

                        // TODO should we analyse all Features from FeatureCollection to be sure
                        // that each Feature properties JSON object define exactly the same properties
                        // with the same bindings ?
                        GeoJSONFeature jsonFeature = jsonFeatureCollection.next();
                        fillTypeFromFeature(ftb, crs, jsonFeature, false);
                    }

                } else if (obj instanceof GeoJSONFeature) {
                    GeoJSONFeature jsonFeature = (GeoJSONFeature) obj;
                    fillTypeFromFeature(ftb, crs, jsonFeature, true);
                } else if (obj instanceof GeoJSONGeometry) {
                    ftb.addAttribute(String.class).setName("fid").addRole(AttributeRole.IDENTIFIER_COMPONENT);
                    ftb.addAttribute(findBinding((GeoJSONGeometry) obj)).setName("geometry").setCRS(crs).addRole(AttributeRole.DEFAULT_GEOMETRY);
                }

                return ftb.build();
            } else {
                throw new DataStoreException("Can't create FeatureType from empty/not found Json file " + jsonFile.getFileName().toString());
            }
        }
    }

    static void fillTypeFromFeature(FeatureTypeBuilder ftb, CoordinateReferenceSystem crs,
            GeoJSONFeature jsonFeature, boolean analyseGeometry) {

        // Consider anything optional for JSON data types.
        ftb.setDefaultMultiplicity(0, 1);

        if (analyseGeometry) {
            ftb.addAttribute(findBinding(jsonFeature.getGeometry())).setName("geometry").setCRS(crs).addRole(AttributeRole.DEFAULT_GEOMETRY);
        } else {
            ftb.addAttribute(Geometry.class).setName("geometry").setCRS(crs).addRole(AttributeRole.DEFAULT_GEOMETRY);
        }

        final Object id = jsonFeature.getId();
        if (id != null) {
            ftb.addAttribute(id.getClass()).setName(ID_PROPERTY_NAME).addRole(AttributeRole.IDENTIFIER_COMPONENT);
        }

        for (Map.Entry<String, Object> property : jsonFeature.getProperties().entrySet()) {
            final Object value = property.getValue();
            final Class<?> binding = value != null ? value.getClass() : String.class;
            final String name = property.getKey();
            final boolean isId = ID_PROPERTY_NAME.equals(name);
            if (isId) {
                LOGGER.warning("Feature identifier has been found in properties. It is INVALID regarding GeoJSON specification RFC-7946. Property values might be erased by Feature identifier.");
                if (id != null) {
                    if (binding.equals(id.getClass())) continue;
                    else throw new IllegalArgumentException("An id property has been found, and its type is not compatible with Feature id type.");
                }
            }

            var atb = ftb.addAttribute(binding).setName(name);
            if (isId) atb.addRole(AttributeRole.IDENTIFIER_COMPONENT);
        }

        // TODO: at this point, if no ID has been registered, should we add one optional with Object type ?
        // In case we read a single feature, this is not coherent. But it is not a very common case.
        // Most of the time, we try to define data type of a feature collection only from its first feature.
        // In such case, having no id on the first feature does not mean it will be absent on other objects.
    }

    private static Class<? extends Geometry> findBinding(GeoJSONGeometry jsonGeometry) {

        if (jsonGeometry instanceof GeoJSONPoint) {
            return Point.class;
        } else if (jsonGeometry instanceof GeoJSONLineString) {
            return LineString.class;
        } else if (jsonGeometry instanceof GeoJSONPolygon) {
            return Polygon.class;
        } else if (jsonGeometry instanceof GeoJSONMultiPoint) {
            return MultiPoint.class;
        } else if (jsonGeometry instanceof GeoJSONMultiLineString) {
            return MultiLineString.class;
        } else if (jsonGeometry instanceof GeoJSONMultiPolygon) {
            return MultiPolygon.class;
        } else if (jsonGeometry instanceof GeoJSONGeometryCollection) {
            return GeometryCollection.class;
        } else {
            throw new IllegalArgumentException("Unsupported geometry type : " + jsonGeometry);
        }

    }

    private void writeType(FeatureType featureType) throws DataStoreException {
        try {
            final boolean jsonExist = Files.exists(jsonFile);

            if (jsonExist && Files.size(jsonFile) != 0) {
                throw new DataStoreException(String.format("Non empty json file %s can't create new json file %s",
                        jsonFile.getFileName().toString(), featureType.getName()));
            }

            if (!jsonExist) {
                Files.createFile(jsonFile);
            }
            //create json with empty collection
            GeoJSONUtils.writeEmptyFeatureCollection(jsonFile);

            //json schema file
            final boolean descExist = Files.exists(descFile);

            if (descExist && Files.size(descFile) != 0) {
                throw new DataStoreException(String.format("Non empty json schema file %s can't create new json schema %s",
                        descFile.getFileName().toString(), featureType.getName()));
            }

            if (!descExist) {
                Files.createFile(descFile);
            }
            //create json schema file
            FeatureTypeUtils.writeFeatureType(featureType, descFile);

            this.featureType = featureType;
            this.name = featureType.getName();
        } catch (IOException e) {
            throw new DataStoreException(e.getMessage(), e);
        }
    }

    @Override
    public Optional<Envelope> getEnvelope() throws DataStoreException {
        Envelope envelope = null;
        rwLock.readLock().lock();
        try {
            final GeoJSONObject obj = GeoJSONParser.parse(jsonFile, true);
            final CoordinateReferenceSystem crs = GeoJSONUtils.getCRS(obj);
            envelope = GeoJSONUtils.getEnvelope(obj, crs);
        } catch (IOException e) {
            throw new DataStoreException(e.getMessage(), e);
        } finally {
            rwLock.readLock().unlock();
        }

        return Optional.ofNullable(envelope);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Stream<Feature> features(boolean parallel) throws DataStoreException {
        final GeoJSONReader reader = new GeoJSONReader(jsonFile, getType(), rwLock);
        final Stream<Feature> stream = StreamSupport.stream(Spliterators.spliteratorUnknownSize(reader, Spliterator.ORDERED), false);
        return stream.onClose(reader::close);
    }

    @Override
    public void add(Iterator<? extends Feature> features) throws DataStoreException {
        try (GeoJSONFileWriter writer = getFeatureWriter()) {
            //rewrite existing features
            while (writer.hasNext()) {
                writer.next();
            }
            //new features
            while (features.hasNext()) {
                Feature feature = features.next();
                Feature next = writer.next();
                writer.write(feature);
            }
        }
    }

    @Override
    public boolean removeIf(Predicate<? super Feature> filter) throws DataStoreException {
        boolean modified = false;
        try (GeoJSONFileWriter writer = getFeatureWriter()) {
            //rewrite existing features
            while (writer.hasNext()) {
                Feature feature = writer.next();
                if (filter.test(feature)) {
                    writer.remove();
                    modified = true;
                }
            }
        }
        return modified;
    }

    @Override
    public void replaceIf(Predicate<? super Feature> filter, UnaryOperator<Feature> updater) throws DataStoreException {
        try (GeoJSONFileWriter writer = getFeatureWriter()) {
            //rewrite existing features
            while (writer.hasNext()) {
                Feature feature = writer.next();
                if (filter.test(feature)) {
                    Feature changed = updater.apply(feature);
                    if (changed == null) {
                        writer.remove();
                    } else {
                        writer.write();
                    }
                }
            }
        }
    }

    private GeoJSONFileWriter getFeatureWriter() throws DataStoreException {
        return new GeoJSONFileWriter(jsonFile, getType(), rwLock,
                GeoJSONProvider.ENCODING, coordAccuracy);
    }

    @Override
    public void updateType(final FeatureType featureType) throws DataStoreException {
        if (!isLocal) {
            throw new DataStoreException("Cannot create FeatureType on remote GeoJSON");
        }
        GenericName typeName = featureType.getName();
        if (typeName == null) {
            throw new DataStoreException("Type name can not be null.");
        }
        if (!typeName.tip().toString().equals(GeoJSONUtils.getNameWithoutExt(jsonFile))) {
            throw new DataStoreException("New type name should be equals to file name.");
        }

        //delete previous files
        rwLock.writeLock().lock();
        try {
            Files.deleteIfExists(descFile);
            Files.deleteIfExists(jsonFile);
            Files.createFile(jsonFile);
        } catch (IOException e) {
            throw new DataStoreException("Can not delete GeoJSON schema.", e);
        } finally {
            rwLock.writeLock().unlock();
        }

        //create new type
        rwLock.writeLock().lock();
        try {
            writeType(featureType);
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    public void clearCache() {
        name = null;
        featureType = null;
    }

    @Override
    public void close() throws DataStoreException {
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Path[] getComponentFiles() throws DataStoreException {
        List<Path> files = new ArrayList<>();
        if (Files.exists(jsonFile)) {
            files.add(jsonFile);
        }
        if (Files.exists(descFile)) {
            files.add(descFile);
        }
        return files.toArray(new Path[files.size()]);
    }

}
