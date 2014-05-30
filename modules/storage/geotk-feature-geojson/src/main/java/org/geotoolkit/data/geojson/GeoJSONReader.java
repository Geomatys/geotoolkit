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

import com.vividsolutions.jts.geom.Geometry;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.FeatureStoreRuntimeException;
import org.geotoolkit.data.geojson.binding.GeoJSONFeature;
import org.geotoolkit.data.geojson.binding.GeoJSONFeatureCollection;
import org.geotoolkit.data.geojson.binding.GeoJSONGeometry;
import org.geotoolkit.data.geojson.binding.GeoJSONObject;
import org.geotoolkit.data.geojson.utils.GeoJSONParser;
import org.geotoolkit.data.geojson.utils.GeometryUtils;
import org.geotoolkit.feature.FeatureUtilities;
import org.geotoolkit.feature.simple.SimpleFeatureType;
import org.geotoolkit.util.converter.ConverterRegistry;
import org.geotoolkit.util.converter.NonconvertibleObjectException;
import org.geotoolkit.util.converter.ObjectConverter;
import org.geotoolkit.feature.*;
import org.geotoolkit.feature.type.*;
import org.geotoolkit.feature.type.FeatureType;
import org.geotoolkit.feature.type.AttributeType;
import org.geotoolkit.feature.type.PropertyType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Quentin Boileau (Geomatys)
 */
public class GeoJSONReader implements FeatureReader<FeatureType, Feature> {

    private final static Logger LOGGER = Logging.getLogger(GeoJSONReader.class);
    private final static ConverterRegistry CONVERTER_REGISTRY = ConverterRegistry.system();

    private GeoJSONParser parser = new GeoJSONParser(true);
    private GeoJSONObject jsonObj = null;
    private Boolean toRead = true;

    protected ReadWriteLock rwlock;
    protected FeatureType featureType;
    protected File jsonFile;
    protected Feature current = null;
    protected int currentFeatureIdx = 0;

    public GeoJSONReader(File jsonFile, FeatureType featureType, ReadWriteLock rwLock) {
        this.jsonFile = jsonFile;
        this.featureType = featureType;
        this.rwlock = rwLock;
        rwlock.readLock().lock();
    }

    @Override
    public FeatureType getFeatureType() {
        return featureType;
    }

    @Override
    public boolean hasNext() throws FeatureStoreRuntimeException {
        read();
        return current != null;
    }

    @Override
    public Feature next() throws FeatureStoreRuntimeException {
        read();
        final Feature ob = current;
        current = null;
        if(ob == null){
            throw new FeatureStoreRuntimeException("No more records.");
        }
        return ob;
    }

    private void read() throws FeatureStoreRuntimeException {
        if(current != null) return;

        //first call
        if (toRead) {
            try {
                jsonObj = parser.parse(jsonFile);
            } catch (IOException e) {
                throw new FeatureStoreRuntimeException(e);
            } finally {
                toRead = false;
            }
        }

        current = null;

        if (jsonObj instanceof GeoJSONFeatureCollection && ((GeoJSONFeatureCollection)jsonObj).hasNext()) {
            GeoJSONFeature feature = ((GeoJSONFeatureCollection)jsonObj).next();
            String id = "id-"+currentFeatureIdx;
            if (feature.getId() != null) {
                id = feature.getId();
            }
            current = toFeature(feature, id);
            currentFeatureIdx++;
            return;
        }

        if (jsonObj instanceof GeoJSONFeature) {
            GeoJSONFeature feature = (GeoJSONFeature)jsonObj;
            String id = "id-0";
            if (feature.getId() != null) {
                id = feature.getId();
            }
            current = toFeature(feature, id);
            jsonObj = null;
            return;
        }

        if (jsonObj instanceof GeoJSONGeometry) {
            current = toFeature((GeoJSONGeometry)jsonObj, "id-0");
            jsonObj = null;
        }
    }

    /**
     * Convert a GeoJSONFeature to geotk Feature.
     * @param jsonFeature
     * @param featureId
     * @return
     */
    protected Feature toFeature(GeoJSONFeature jsonFeature, String featureId) throws FeatureStoreRuntimeException {
        Map<String, Object> properties = jsonFeature.getProperties();

        //Build and add geometry to properties
        CoordinateReferenceSystem crs = featureType.getGeometryDescriptor().getCoordinateReferenceSystem();
        Geometry geom = GeometryUtils.toJTS(jsonFeature.getGeometry(), crs);
        properties.put(featureType.getGeometryDescriptor().getLocalName(), geom);

        //empty feature
        final Feature feature = FeatureUtilities.defaultFeature(featureType, featureId);
        //recursively fill other properties
        fillFeature(feature, properties);

        return feature;
    }

    /**
     * Recursively fill a ComplexAttribute with properties map
     * @param attribute
     * @param properties
     */
    private void fillFeature(ComplexAttribute attribute, Map<String, Object> properties) throws FeatureStoreRuntimeException {

        ComplexType complexType = attribute.getType();

        //clear attribute properties
        boolean isSimple = complexType instanceof SimpleFeatureType;
        if(!isSimple)attribute.getProperties().clear();

        PropertyType type;
        String attName;
        Object value;
        for(final PropertyDescriptor desc : complexType.getDescriptors()) {

            type = desc.getType();
            attName = type.getName().toString();
            value = properties.get(attName);

            if (type instanceof ComplexType ) {

                    if (value instanceof List) {
                        List valueList = (List) value;
                        if (!valueList.isEmpty()) {
                            int listSize = valueList.size();
                            Object firstElement = valueList.get(0);

                            //list of objects
                            if (firstElement instanceof Map) {
                                for (int i = 0; i < listSize; i++) {
                                    final ComplexAttribute subComplexAttribute = FeatureUtilities.defaultProperty((ComplexType) type);
                                    fillFeature(subComplexAttribute, (Map) valueList.get(i));
                                    attribute.getProperties().add(subComplexAttribute);
                                }
                            } else {
                                LOGGER.log(Level.WARNING, "Invalid complex property value "+value);
                            }
                        }
                    } else if (value instanceof Map) {
                        final ComplexAttribute subComplexAttribute = FeatureUtilities.defaultProperty((ComplexType) type);
                        fillFeature(subComplexAttribute, (Map) value);
                        attribute.getProperties().add(subComplexAttribute);
                    }
                    continue;

            } else if(type instanceof AttributeType) {

                if(isSimple){
                    attribute.getProperty(desc.getName().getLocalPart()).setValue(value);
                }else{
                    Property prop = FeatureUtilities.defaultProperty(desc);
                    fillProperty(prop, value);
                    attribute.getProperties().add(prop);
                }

            }
        }
    }

    /**
     * Try to convert value as expected in PropertyType description.
     * @param prop
     * @param value
     */
    private void fillProperty(Property prop, Object value) throws FeatureStoreRuntimeException {

        Object convertValue = null;
        if (value != null) {
            AttributeType propertyType = (AttributeType)prop.getType();
            Class binding = propertyType.getValueClass();

            try {
                ObjectConverter converter = CONVERTER_REGISTRY.converter(value.getClass(), binding);
                convertValue = converter.convert(value);
            } catch (NonconvertibleObjectException e) {
                throw new FeatureStoreRuntimeException(String.format("Inconvertible property %s : %s",
                        prop.getName().getLocalPart(), e.getMessage()), e);
            }
        }

        prop.setValue(convertValue);
    }

    /**
     * Convert a GeoJSONGeometry to Feature.
     * @param jsonGeometry
     * @param featureId
     * @return
     */
    protected Feature toFeature(GeoJSONGeometry jsonGeometry, String featureId) {

        final Feature feature = FeatureUtilities.defaultFeature(featureType, featureId);

        CoordinateReferenceSystem crs = featureType.getGeometryDescriptor().getCoordinateReferenceSystem();
        Geometry geom = GeometryUtils.toJTS(jsonGeometry, crs);
        feature.getDefaultGeometryProperty().setValue(geom);

        return feature;
    }

    @Override
    public void remove() {
        throw new FeatureStoreRuntimeException("Not supported on reader.");
    }

    @Override
    public void close() {
        rwlock.readLock().unlock();
    }

}
