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

import com.vividsolutions.jts.geom.*;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.data.*;
import org.geotoolkit.data.geojson.binding.*;
import org.geotoolkit.data.geojson.utils.FeatureTypeUtils;
import org.geotoolkit.data.geojson.utils.GeoJSONParser;
import org.geotoolkit.data.geojson.utils.GeoJSONUtils;
import org.geotoolkit.data.query.*;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.feature.Feature;
import org.geotoolkit.feature.type.*;
import org.opengis.filter.Filter;
import org.opengis.filter.identity.FeatureId;
import org.opengis.geometry.Envelope;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import static org.geotoolkit.data.geojson.GeoJSONFeatureStoreFactory.*;
import static org.geotoolkit.data.geojson.binding.GeoJSONGeometry.*;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Quentin Boileau (Geomatys)
 */
public class GeoJSONFeatureStore extends AbstractFeatureStore {

    private static final Logger LOGGER = Logging.getLogger(GeoJSONFeatureStore.class);
    private static final String DESC_FILE_SUFFIX = "_Type.json";
    private static final String GEOMETRY_FIELD = "geometry";

    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();
    private final ReadWriteLock tmpLock = new ReentrantReadWriteLock();

    private final QueryCapabilities capabilities = new DefaultQueryCapabilities(false, false);
    private Name name;
    private FeatureType featureType;
    private File descFile;
    private File jsonFile;
    private Integer coordAccuracy;
    private boolean isLocal = true;

    public GeoJSONFeatureStore(final URL url, final String namespace, Integer coordAccuracy)
            throws DataStoreException {
        this(toParameter(url, namespace, coordAccuracy));
    }

    public GeoJSONFeatureStore (final ParameterValueGroup params) throws DataStoreException {
        super(params);
        this.coordAccuracy = (Integer) params.parameter(COORDINATE_ACCURACY.getName().toString()).getValue();

        final URL url = (URL) params.parameter(URLP.getName().toString()).getValue();
        this.isLocal = url.toExternalForm().toLowerCase().startsWith("file:");

        File tmpFile = null;
        try {
            tmpFile = new File(url.toURI());
        } catch (URISyntaxException ex) {
            throw new DataStoreException(ex);
        }

        if (tmpFile.getName().endsWith(DESC_FILE_SUFFIX)) {
            this.descFile = tmpFile;
            this.jsonFile = new File(descFile.getParentFile(), tmpFile.getName().replace(DESC_FILE_SUFFIX, ".json"));
        } else {
            this.jsonFile = tmpFile;
            //search for description json file
            String typeName = GeoJSONUtils.getNameWithoutExt(jsonFile);
            this.descFile = new File(jsonFile.getParent(), typeName + DESC_FILE_SUFFIX);
        }
    }

    private static ParameterValueGroup toParameter(final URL url, final String namespace, Integer coordAccuracy){
        final ParameterValueGroup params = GeoJSONFeatureStoreFactory.PARAMETERS_DESCRIPTOR.createValue();
        Parameters.getOrCreate(GeoJSONFeatureStoreFactory.URLP, params).setValue(url);
        Parameters.getOrCreate(GeoJSONFeatureStoreFactory.NAMESPACE, params).setValue(namespace);
        Parameters.getOrCreate(GeoJSONFeatureStoreFactory.COORDINATE_ACCURACY, params).setValue(coordAccuracy);
        return params;
    }

    @Override
    public FeatureStoreFactory getFactory() {
        return FeatureStoreFinder.getFactoryById(GeoJSONFeatureStoreFactory.NAME);
    }

    @Override
    public boolean isWritable(final Name typeName) throws DataStoreException {
        return isLocal && descFile.canWrite() && jsonFile.canWrite();
    }

    public Name getName() throws DataStoreException{
        checkTypeExist();
        return name;
    }

    public FeatureType getFeatureType() throws DataStoreException{
        checkTypeExist();
        return featureType;
    }

    private void checkTypeExist() throws DataStoreException {
        if (name != null && featureType != null) {
            return;
        } else {
            try {
                // try to parse file only if exist and not empty
                if (jsonFile.exists() && jsonFile.length() != 0) {
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
        if (descFile.exists() && descFile.length() != 0) {
            // build FeatureType from description JSON.
            return FeatureTypeUtils.readFeatureType(descFile);
        } else {
            if(jsonFile.exists() && jsonFile.length() != 0) {
                final String name = GeoJSONUtils.getNameWithoutExt(jsonFile);

                final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
                ftb.setName(name);

                // build FeatureType from the first Feature of JSON file.
                final GeoJSONParser parser = new GeoJSONParser(true);
                final GeoJSONObject obj = parser.parse(jsonFile);

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
                    ftb.add(GEOMETRY_FIELD, findBinding((GeoJSONGeometry) obj), crs);
                }

                return ftb.buildFeatureType();
            } else {
                throw new DataStoreException("Can't create FeatureType from empty/not found Json file "+jsonFile.getName());
            }
        }
    }


    private void fillTypeFromFeature(FeatureTypeBuilder ftb, CoordinateReferenceSystem crs, GeoJSONFeature jsonFeature,
                                     boolean analyseGeometry) {
        if (analyseGeometry) {
            ftb.add(GEOMETRY_FIELD, findBinding(jsonFeature.getGeometry()), crs);
        } else {
            ftb.add(GEOMETRY_FIELD, Geometry.class, crs);
        }
        for (Map.Entry<String, Object> property : jsonFeature.getProperties().entrySet()) {
            Object value = property.getValue();
            Class binding = value != null ? value.getClass() : String.class;
            ftb.add(property.getKey(), binding);
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
            if (!jsonFile.exists() || jsonFile.length() == 0) {
                jsonFile.createNewFile();
                GeoJSONUtils.writeEmptyFeatureCollection(jsonFile);
            } else {
                throw new DataStoreException(String.format("Non empty json file %s can't create new json schema %s",
                        jsonFile.getName(), featureType.getName()));
            }

            //json schema file
            if (!descFile.exists() || descFile.length() == 0) {
                descFile.createNewFile();
                FeatureTypeUtils.writeFeatureType(featureType, descFile);
            } else {
                throw new DataStoreException(String.format("Non empty json schema file %s can't create new json schema %s",
                        descFile.getName(), featureType.getName()));
            }

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
    public Set<Name> getNames() throws DataStoreException {
        Name name = getName();

        if (name != null) {
            return Collections.singleton(getName());
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
        typeCheck(query.getTypeName());

        if(QueryUtilities.queryAll(query)){
            try {
                rwLock.readLock().lock();
                final GeoJSONParser parser = new GeoJSONParser(true);
                final GeoJSONObject obj = parser.parse(jsonFile);

                final CoordinateReferenceSystem crs = GeoJSONUtils.getCRS(obj);
                final Envelope envelope = GeoJSONUtils.getEnvelope(obj, crs);
                if (envelope != null) {
                    return envelope;
                }

                rwLock.readLock().unlock();
            } catch (IOException e) {
                throw new DataStoreException(e.getMessage(), e);
            }
        }
        //fallback
        return super.getEnvelope(query);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureReader getFeatureReader(final Query query) throws DataStoreException {
        typeCheck(query.getTypeName());

        final FeatureReader fr = new GeoJSONReader(jsonFile, featureType, rwLock);
        return handleRemaining(fr, query);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureWriter getFeatureWriter(final Name typeName, final Filter filter, final Hints hints) throws DataStoreException {
        typeCheck(typeName);

        final FeatureWriter fw = new GeoJSONFileWriter(jsonFile, featureType, rwLock, tmpLock,
                GeoJSONFeatureStoreFactory.ENCODING, coordAccuracy);
        return handleRemaining(fw, filter);
    }

    ////////////////////////////////////////////////////////////////////////////
    // FeatureType manipulation /////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureType getFeatureType(final Name typeName) throws DataStoreException {
        checkTypeExist();
        if (featureType == null) {
            throw  new DataStoreException("No FeatureType found for type name : "+typeName);
        }

        return featureType;
    }

    @Override
    public void createFeatureType(final Name typeName, final FeatureType featureType) throws DataStoreException {
        if (!isLocal) {
            throw new DataStoreException("Cannot create FeatureType on remote GeoJSON");
        }

        if(typeName == null){
            throw new DataStoreException("Type name can not be null.");
        }

        if(this.featureType != null){
            throw new DataStoreException("Can only have one feature type in GeoJSON dataStore.");
        }

        if (!typeName.getLocalPart().equals(GeoJSONUtils.getNameWithoutExt(jsonFile))) {
            throw new DataStoreException("New type name should be equals to file name.");
        }

        try{
            rwLock.writeLock().lock();
            writeType(featureType);
        }finally{
            rwLock.writeLock().unlock();
        }

        fireSchemaAdded(typeName, featureType);
    }


    /**
     * {@inheritDoc }
     */
    @Override
    public void updateFeatureType(final Name typeName, final FeatureType featureType) throws DataStoreException {
        deleteFeatureType(typeName);
        createFeatureType(typeName, featureType);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void deleteFeatureType(final Name typeName) throws DataStoreException {
        if (!getNames().contains(typeName)) {
            throw new DataStoreException("Type name doesn't exist in FeatureStore.");
        }

        if (!isLocal) {
            throw new DataStoreException("Cannot create FeatureType on remote GeoJSON");
        }

        if(typeName == null){
            throw new DataStoreException("Type name can not be null.");
        }

        if (!typeName.getLocalPart().equals(GeoJSONUtils.getNameWithoutExt(jsonFile))) {
            throw new DataStoreException("New type name should be equals to file name.");
        }

        try{
            rwLock.writeLock().lock();
            descFile.delete();
            jsonFile.createNewFile();
        } catch (IOException e) {
            throw new DataStoreException("Can not delete GeoJSON schema.", e);
        } finally{
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
    public List<FeatureId> addFeatures(final Name groupName, final Collection<? extends Feature> newFeatures, 
            final Hints hints) throws DataStoreException {
        return handleAddWithFeatureWriter(groupName, newFeatures, hints);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void updateFeatures(final Name groupName, final Filter filter, final Map<? extends PropertyDescriptor, ? extends Object> values) throws DataStoreException {
        handleUpdateWithFeatureWriter(groupName, filter, values);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void removeFeatures(final Name groupName, final Filter filter) throws DataStoreException {
        handleRemoveWithFeatureWriter(groupName, filter);
    }

    @Override
    public void refreshMetaModel() {
        name = null;
        featureType = null;
    }
}
