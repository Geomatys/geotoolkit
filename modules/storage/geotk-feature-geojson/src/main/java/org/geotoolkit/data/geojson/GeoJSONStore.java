/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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

package org.geotoolkit.data.geojson;

import org.geotoolkit.storage.feature.FeatureWriter;
import org.geotoolkit.storage.feature.FeatureReader;
import org.geotoolkit.storage.feature.FeatureStoreRuntimeException;
import org.geotoolkit.storage.feature.FeatureStreams;
import org.geotoolkit.storage.feature.AbstractFeatureStore;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.sis.feature.builder.AttributeRole;
import org.apache.sis.feature.builder.AttributeTypeBuilder;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.internal.storage.ResourceOnFileSystem;
import org.apache.sis.parameter.Parameters;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.DataStoreProvider;
import org.apache.sis.storage.Query;
import org.apache.sis.storage.UnsupportedQueryException;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.data.*;
import static org.geotoolkit.storage.feature.AbstractFileFeatureStoreFactory.PATH;
import static org.geotoolkit.data.geojson.GeoJSONProvider.*;
import org.geotoolkit.data.geojson.binding.*;
import org.geotoolkit.data.geojson.binding.GeoJSONGeometry.GeoJSONGeometryCollection;
import org.geotoolkit.data.geojson.binding.GeoJSONGeometry.GeoJSONLineString;
import org.geotoolkit.data.geojson.binding.GeoJSONGeometry.GeoJSONMultiLineString;
import org.geotoolkit.data.geojson.binding.GeoJSONGeometry.GeoJSONMultiPoint;
import org.geotoolkit.data.geojson.binding.GeoJSONGeometry.GeoJSONMultiPolygon;
import org.geotoolkit.data.geojson.binding.GeoJSONGeometry.GeoJSONPoint;
import org.geotoolkit.data.geojson.binding.GeoJSONGeometry.GeoJSONPolygon;
import org.geotoolkit.data.geojson.utils.FeatureTypeUtils;
import org.geotoolkit.data.geojson.utils.GeoJSONParser;
import org.geotoolkit.data.geojson.utils.GeoJSONUtils;
import org.geotoolkit.storage.feature.query.DefaultQueryCapabilities;
import org.geotoolkit.storage.feature.query.QueryCapabilities;
import org.geotoolkit.storage.feature.query.QueryUtilities;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.factory.HintsPending;
import org.geotoolkit.storage.DataStores;
import org.geotoolkit.util.NamesExt;
import org.locationtech.jts.geom.*;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.identity.FeatureId;
import org.opengis.geometry.Envelope;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.GenericName;

/**
 *
 * @author Quentin Boileau (Geomatys)
 */
public class GeoJSONStore extends AbstractFeatureStore implements ResourceOnFileSystem {

    private static final Logger LOGGER = Logging.getLogger("org.geotoolkit.data.geojson");
    private static final String DESC_FILE_SUFFIX = "_Type.json";

    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();

    private final QueryCapabilities capabilities = new DefaultQueryCapabilities(false, false);
    private GenericName name;
    private FeatureType featureType;
    private Path descFile;
    private Path jsonFile;
    private Integer coordAccuracy;
    private boolean isLocal = true;

    public GeoJSONStore(final Path path, Integer coordAccuracy)
            throws DataStoreException {
        this(toParameter(path.toUri(), coordAccuracy));
    }

    public GeoJSONStore(final URI uri, Integer coordAccuracy)
            throws DataStoreException {
        this(toParameter(uri, coordAccuracy));
    }

    public GeoJSONStore (final ParameterValueGroup params) throws DataStoreException {
        super(params);
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

    private static ParameterValueGroup toParameter(final URI uri, Integer coordAccuracy){
        final Parameters params = Parameters.castOrWrap(GeoJSONProvider.PARAMETERS_DESCRIPTOR.createValue());
        params.getOrCreate(GeoJSONProvider.PATH).setValue(uri);
        params.getOrCreate(GeoJSONProvider.COORDINATE_ACCURACY).setValue(coordAccuracy);
        return params;
    }

    @Override
    public DataStoreProvider getProvider() {
        return DataStores.getProviderById(GeoJSONProvider.NAME);
    }

    @Override
    public boolean isWritable(final String typeName) throws DataStoreException {
        return isLocal && Files.isWritable(descFile) && Files.isWritable(jsonFile);
    }

    public GenericName getName() throws DataStoreException{
        checkTypeExist();
        return name;
    }

    public FeatureType getFeatureType() throws DataStoreException{
        checkTypeExist();
        return featureType;
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
     * Read FeatureType from a JSON-Schema file if exist or directly from the input JSON file.
     * @return
     * @throws DataStoreException
     * @throws IOException
     */
    private FeatureType readType() throws DataStoreException, IOException {
        if (Files.exists(descFile) && Files.size(descFile) != 0) {
            // build FeatureType from description JSON.
            return FeatureTypeUtils.readFeatureType(descFile);
        } else {
            if(Files.exists(jsonFile) && Files.size(jsonFile) != 0) {
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
                    HashMap<Object, Object> userData = new HashMap<>();
                    userData.put(HintsPending.PROPERTY_IS_IDENTIFIER,Boolean.TRUE);
                    ftb.addAttribute(String.class).setName("fid").addRole(AttributeRole.IDENTIFIER_COMPONENT);
                    ftb.addAttribute(findBinding((GeoJSONGeometry) obj)).setName("geometry").setCRS(crs).addRole(AttributeRole.DEFAULT_GEOMETRY);
                }

                return ftb.build();
            } else {
                throw new DataStoreException("Can't create FeatureType from empty/not found Json file "+jsonFile.getFileName().toString());
            }
        }
    }


    private void fillTypeFromFeature(FeatureTypeBuilder ftb, CoordinateReferenceSystem crs,
                                     GeoJSONFeature jsonFeature, boolean analyseGeometry) {
        if (analyseGeometry) {
            ftb.addAttribute(findBinding(jsonFeature.getGeometry())).setName("geometry").setCRS(crs).addRole(AttributeRole.DEFAULT_GEOMETRY);
        } else {
            ftb.addAttribute(Geometry.class).setName("geometry").setCRS(crs).addRole(AttributeRole.DEFAULT_GEOMETRY);
        }
        for (Map.Entry<String, Object> property : jsonFeature.getProperties().entrySet()) {
            final Object value = property.getValue();
            final Class binding = value != null ? value.getClass() : String.class;
            final GenericName name = NamesExt.create(property.getKey());
            final AttributeTypeBuilder atb = ftb.addAttribute(binding).setName(name);
            if ("id".equals(property.getKey()) || "fid".equals(property.getKey())) {
                atb.addRole(AttributeRole.IDENTIFIER_COMPONENT);
            }
        }
    }

    private Class findBinding(GeoJSONGeometry jsonGeometry) {

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

            if (!jsonExist) Files.createFile(jsonFile);
            //create json with empty collection
            GeoJSONUtils.writeEmptyFeatureCollection(jsonFile);

            //json schema file
            final boolean descExist = Files.exists(descFile);

            if (descExist && Files.size(descFile) != 0) {
                throw new DataStoreException(String.format("Non empty json schema file %s can't create new json schema %s",
                        descFile.getFileName().toString(), featureType.getName()));
            }

            if (!descExist) Files.createFile(descFile);
            //create json schema file
            FeatureTypeUtils.writeFeatureType(featureType, descFile);

            this.featureType = featureType;
            this.name = featureType.getName();
        } catch (IOException e) {
            throw new DataStoreException(e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Set<GenericName> getNames() throws DataStoreException {
        GenericName name = getName();

        if (name != null) {
            return Collections.singleton(name);
        } else {
            return Collections.EMPTY_SET;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public QueryCapabilities getQueryCapabilities() {
        return capabilities;
    }

    @Override
    public Envelope getEnvelope(final Query query) throws DataStoreException, FeatureStoreRuntimeException {
        if (!(query instanceof org.geotoolkit.storage.feature.query.Query)) throw new UnsupportedQueryException();

        final org.geotoolkit.storage.feature.query.Query gquery = (org.geotoolkit.storage.feature.query.Query) query;
        typeCheck(gquery.getTypeName());

        if (QueryUtilities.queryAll(gquery)) {
            rwLock.readLock().lock();
            try {
                final GeoJSONObject obj = GeoJSONParser.parse(jsonFile, true);
                final CoordinateReferenceSystem crs = GeoJSONUtils.getCRS(obj);
                final Envelope envelope = GeoJSONUtils.getEnvelope(obj, crs);
                if (envelope != null) {
                    return envelope;
                }

            } catch (IOException e) {
                throw new DataStoreException(e.getMessage(), e);
            } finally {
                rwLock.readLock().unlock();
            }
        }

        //fallback
        return super.getEnvelope(gquery);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureReader getFeatureReader(final Query query) throws DataStoreException {
        if (!(query instanceof org.geotoolkit.storage.feature.query.Query)) throw new UnsupportedQueryException();

        final org.geotoolkit.storage.feature.query.Query gquery = (org.geotoolkit.storage.feature.query.Query) query;
        typeCheck(gquery.getTypeName());

        final FeatureReader fr = new GeoJSONReader(jsonFile, featureType, rwLock);
        return FeatureStreams.subset(fr, gquery);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureWriter getFeatureWriter(Query query) throws DataStoreException {
        if (!(query instanceof org.geotoolkit.storage.feature.query.Query)) throw new UnsupportedQueryException();

        final org.geotoolkit.storage.feature.query.Query gquery = (org.geotoolkit.storage.feature.query.Query) query;
        typeCheck(gquery.getTypeName());
        final FeatureWriter fw = new GeoJSONFileWriter(jsonFile, featureType, rwLock,
                GeoJSONProvider.ENCODING, coordAccuracy);
        return FeatureStreams.filter(fw, gquery.getFilter());
    }

    ////////////////////////////////////////////////////////////////////////////
    // FeatureType manipulation /////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureType getFeatureType(final String typeName) throws DataStoreException {
        checkTypeExist();
        if (featureType == null) {
            throw  new DataStoreException("No FeatureType found for type name : "+typeName);
        }

        return featureType;
    }

    @Override
    public void createFeatureType(final FeatureType featureType) throws DataStoreException {
        if (!isLocal) {
            throw new DataStoreException("Cannot create FeatureType on remote GeoJSON");
        }

        GenericName typeName = featureType.getName();
        if(typeName == null){
            throw new DataStoreException("Type name can not be null.");
        }

        if(this.featureType != null){
            throw new DataStoreException("Can only have one feature type in GeoJSON dataStore.");
        }

        if (!typeName.tip().toString().equals(GeoJSONUtils.getNameWithoutExt(jsonFile))) {
            throw new DataStoreException("New type name should be equals to file name.");
        }

        rwLock.writeLock().lock();
        try {
            writeType(featureType);
        } finally {
            rwLock.writeLock().unlock();
        }

        fireSchemaAdded(typeName, featureType);
    }


    /**
     * {@inheritDoc }
     */
    @Override
    public void updateFeatureType(final FeatureType featureType) throws DataStoreException {
        deleteFeatureType(featureType.getName().toString());
        createFeatureType(featureType);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void deleteFeatureType(final String typeName) throws DataStoreException {
        typeCheck(typeName);

        if (!isLocal) {
            throw new DataStoreException("Cannot create FeatureType on remote GeoJSON");
        }

        if(typeName == null){
            throw new DataStoreException("Type name can not be null.");
        }

        if (!typeName.equals(GeoJSONUtils.getNameWithoutExt(jsonFile))) {
            throw new DataStoreException("New type name should be equals to file name.");
        }

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
    }

    ////////////////////////////////////////////////////////////////////////////
    //Fallback on iterative reader and writer //////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    /**
     * {@inheritDoc }
     */
    @Override
    public List<FeatureId> addFeatures(final String groupName, final Collection<? extends Feature> newFeatures,
            final Hints hints) throws DataStoreException {
        return handleAddWithFeatureWriter(groupName, newFeatures, hints);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void updateFeatures(final String groupName, final Filter filter, final Map<String, ? extends Object> values) throws DataStoreException {
        handleUpdateWithFeatureWriter(groupName, filter, values);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void removeFeatures(final String groupName, final Filter filter) throws DataStoreException {
        handleRemoveWithFeatureWriter(groupName, filter);
    }


    @Override
    public void refreshMetaModel() {
        name = null;
        featureType = null;
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
