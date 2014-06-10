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
import java.lang.reflect.Array;
import java.util.AbstractMap;
import java.util.HashMap;
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
    private final Map<Map.Entry<Class, Class>, ObjectConverter> convertersCache = new HashMap<Map.Entry<Class, Class>, ObjectConverter>();

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
                    if (value != null) {
                        Class valueClass = value.getClass();

                        if (valueClass.isArray()) {
                            Class base = value.getClass().getComponentType();

                            if (!Map.class.isAssignableFrom(base)) {
                                LOGGER.log(Level.WARNING, "Invalid complex property value " + value);
                            }

                            final int size = Array.getLength(value);
                            if (size > 0) {

                                //list of objects
                                for (int i = 0; i < size; i++) {
                                    final ComplexAttribute subComplexAttribute = FeatureUtilities.defaultProperty((ComplexType) type);
                                    fillFeature(subComplexAttribute, (Map) Array.get(value, i));
                                    attribute.getProperties().add(subComplexAttribute);
                                }
                            }
                        } else if (value instanceof Map) {
                            final ComplexAttribute subComplexAttribute = FeatureUtilities.defaultProperty((ComplexType) type);
                            fillFeature(subComplexAttribute, (Map) value);
                            attribute.getProperties().add(subComplexAttribute);
                        }
                    }
                    continue;

            } else if(type instanceof AttributeType) {

                Property property;
                if (isSimple) {
                    property = attribute.getProperty(desc.getName().getLocalPart());
                } else {
                    property = FeatureUtilities.defaultProperty(desc);
                }

                fillProperty(property, value);

                if (!isSimple) {
                    attribute.getProperties().add(property);
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
        try {
            if (value != null) {
                PropertyType propertyType = prop.getType();
                Class binding = propertyType.getBinding();

                if (value.getClass().isArray() && binding.isArray()) {

                    int nbdim = 1;
                    Class base = value.getClass().getComponentType();
                    while (base.isArray()) {
                        base = base.getComponentType();
                        nbdim++;
                    }

                    convertValue = rebuildArray(value, base, nbdim);

                } else {
                    convertValue = convert(value, binding);
                }
            }
        } catch (NonconvertibleObjectException e1) {
            throw new FeatureStoreRuntimeException(String.format("Inconvertible property %s : %s",
                    prop.getName().getLocalPart(), e1.getMessage()), e1);
        }

        prop.setValue(convertValue);
    }

    /**
     * Rebuild nDim arrays recursively
     * @param candidate
     * @param componentType
     * @param depth
     * @return Array object
     * @throws NonconvertibleObjectException
     */
    private Object rebuildArray(Object candidate, Class componentType, int depth) throws NonconvertibleObjectException {
        if(candidate==null) return null;

        if(candidate.getClass().isArray()){
            final int size = Array.getLength(candidate);
            final int[] dims = new int[depth];
            dims[0] = size;
            final Object rarray = Array.newInstance(componentType, dims);
            depth--;
            for(int k=0; k<size; k++){
                Array.set(rarray, k, rebuildArray(Array.get(candidate, k), componentType, depth));
            }
            return rarray;
        }else{
            return convert(candidate, componentType);
        }
    }

    /**
     * Convert value object into binding class
     * @param value
     * @param binding
     * @return
     * @throws NonconvertibleObjectException
     */
    private Object convert(Object value, Class binding) throws NonconvertibleObjectException {
        AbstractMap.SimpleEntry<Class, Class> key = new AbstractMap.SimpleEntry<Class, Class>(value.getClass(), binding);
        ObjectConverter converter = convertersCache.get(key);

        if (converter == null) {
            converter = CONVERTER_REGISTRY.converter(value.getClass(), binding);
            convertersCache.put(key, converter);
        }
        return converter.convert(value);
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
