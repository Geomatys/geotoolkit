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
import java.lang.reflect.Array;
import java.nio.file.Path;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.sis.feature.privy.AttributeConvention;
import org.apache.sis.util.Numbers;
import org.apache.sis.util.ObjectConverter;
import org.apache.sis.util.ObjectConverters;
import org.apache.sis.util.UnconvertibleObjectException;
import org.apache.sis.util.collection.BackingStoreException;
import org.geotoolkit.feature.FeatureExt;
import org.geotoolkit.internal.geojson.GeoJSONParser;
import org.geotoolkit.internal.geojson.GeoJSONUtils;
import org.geotoolkit.internal.geojson.binding.GeoJSONFeature;
import org.geotoolkit.internal.geojson.binding.GeoJSONFeatureCollection;
import org.geotoolkit.internal.geojson.binding.GeoJSONGeometry;
import org.geotoolkit.internal.geojson.binding.GeoJSONObject;
import static org.geotoolkit.storage.geojson.GeoJSONConstants.TYPE;
import org.locationtech.jts.geom.Geometry;
import org.opengis.feature.Attribute;
import org.opengis.feature.AttributeType;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureAssociationRole;
import org.opengis.feature.FeatureType;
import org.opengis.feature.PropertyNotFoundException;
import org.opengis.feature.PropertyType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * @author Quentin Boileau (Geomatys)
 * @author Johann Sorel (Geomatys)
 * @version 2.0
 * @since   2.0
 * @module
 */
class GeoJSONReader implements Iterator<Feature>, AutoCloseable {

    private static final Logger LOGGER = Logger.getLogger("org.apache.sis.storage.geojson");
    private final Map<Map.Entry<Class, Class>, ObjectConverter> convertersCache = new HashMap<>();

    private GeoJSONObject jsonObj;
    private Boolean toRead = true;

    protected final ReadWriteLock rwlock;
    protected final FeatureType featureType;
    protected final Path jsonFile;
    protected Feature current;
    protected int currentFeatureIdx;

    protected Map<FeatureType, FTypeInformation> ftInfos = new HashMap<>();

    /**
     * Data stucture recording information about Feature Type :
     *  - identifier convertion method
     *  - crs
     *  - geometric property name
     */
    protected static class FTypeInformation {
       /**
        * A flag indicating if we should read identifiers from read stream. it's
        * activated if the feature type given at built contains an
        * {@link AttributeConvention#IDENTIFIER_PROPERTY}.
        */
        public final boolean hasIdentifier;
        public final Function idConverter;
        public final CoordinateReferenceSystem crs;
        public final String geometryName;

        FTypeInformation(FeatureType featureType) {
            hasIdentifier = GeoJSONUtils.hasIdentifier(featureType);
            if (hasIdentifier) {
                idConverter = GeoJSONUtils.getIdentifierConverter(featureType);
            } else {
                // It should not be used, but we don't set it to null in case someons use it by mistake.
                idConverter = input -> input;
            }
            CoordinateReferenceSystem crs = null;
            String geometryName = null;
            try {
                final PropertyType defaultGeometry = FeatureExt.getDefaultGeometry(featureType);
                crs = FeatureExt.getCRS(defaultGeometry);
                geometryName = defaultGeometry.getName().toString();
            } catch (PropertyNotFoundException ex) {
                // not mandatory to have a geometric property
            }
            this.crs = crs;
            this.geometryName = geometryName;
        }
    }

    public GeoJSONReader(Path jsonFile, FeatureType featureType, ReadWriteLock rwLock) {
        ftInfos.put(featureType, new FTypeInformation(featureType));
        this.jsonFile = jsonFile;
        this.featureType = featureType;
        this.rwlock = rwLock;
    }

    GeoJSONReader(GeoJSONObject jsonObj, FeatureType featureType, ReadWriteLock rwLock) {
        ftInfos.put(featureType, new FTypeInformation(featureType));
        this.jsonFile = null;
        this.jsonObj = jsonObj;
        this.toRead = false;
        this.featureType = featureType;
        this.rwlock = rwLock;
    }

    public FeatureType getFeatureType() {
        return featureType;
    }

    @Override
    public boolean hasNext() throws BackingStoreException {
        read();
        return current != null;
    }

    @Override
    public Feature next() throws BackingStoreException {
        read();
        final Feature ob = current;
        current = null;
        if (ob == null) {
            throw new BackingStoreException("No more records.");
        }
        return ob;
    }

    private void read() throws BackingStoreException {
        if (current != null) return;

        //first call
        if (toRead) {
            rwlock.readLock().lock();
            try {
                jsonObj = GeoJSONParser.parse(jsonFile, true);
            } catch (IOException e) {
                throw new BackingStoreException(e);
            } finally {
                toRead = false;
                rwlock.readLock().unlock();
            }
        }

        if (jsonObj instanceof GeoJSONFeatureCollection) {
            final GeoJSONFeatureCollection fc = (GeoJSONFeatureCollection) jsonObj;
            rwlock.readLock().lock();
            try {
                if (fc.hasNext()) {
                    current = toFeature(fc.next());
                }
            } finally {
                rwlock.readLock().unlock();
            }
        } else if (jsonObj instanceof GeoJSONFeature) {
            current = toFeature((GeoJSONFeature) jsonObj);
            jsonObj = null;
        } else if (jsonObj instanceof GeoJSONGeometry) {
            current = toFeature((GeoJSONGeometry) jsonObj);
            jsonObj = null;
        }
    }

    /**
     * Convert a GeoJSONFeature to geotk Feature.
     *
     * @param jsonFeature
     * @return
     */
    Feature toFeature(GeoJSONFeature jsonFeature) throws BackingStoreException {
        return toFeature(jsonFeature, featureType);
    }

    /**
     * Convert a GeoJSONFeature to geotk Feature.
     *
     * @param jsonFeature
     * @param ft
     * @return
     */
    Feature toFeature(GeoJSONFeature jsonFeature, FeatureType ft) throws BackingStoreException {
        FTypeInformation fti = ftInfos.computeIfAbsent(ft, FTypeInformation::new);

        //create empty feature
        final Feature feature = ft.newInstance();

        if (fti.geometryName != null) {
            //Build geometry
            final Geometry geom = GeoJSONGeometry.toJTS(jsonFeature.getGeometry(), fti.crs);
            feature.setPropertyValue(fti.geometryName, geom);
        }

        //recursively fill other properties
        final Map<String, Object> properties = jsonFeature.getProperties();
        fillFeature(feature, properties);

        // Fill identifier after properties, to ensure that feature identifier has priority over any property named "id".
        if (fti.hasIdentifier) {
            Object id = jsonFeature.getId();
            if (id != null) feature.setPropertyValue(AttributeConvention.IDENTIFIER, fti.idConverter.apply(id));
        }

        currentFeatureIdx++;
        return feature;
    }

    /**
     * Recursively fill a ComplexAttribute with properties map
     *
     * @param feature
     * @param properties
     */
    private void fillFeature(Feature feature, Map<String, Object> properties) throws BackingStoreException {
        final FeatureType featureType = feature.getType();

        for (final PropertyType type : featureType.getProperties(true)) {

            final String attName = type.getName().toString();
            final Object value = properties.get(attName);
            if (value == null) {
                continue;
            }

            if (type instanceof FeatureAssociationRole) {
                final FeatureAssociationRole asso = (FeatureAssociationRole) type;
                final FeatureType assoType = asso.getValueType();
                final Class<?> valueClass = value.getClass();

                if (valueClass.isArray()) {
                    Class<?> base = value.getClass().getComponentType();

                    if (!Map.class.isAssignableFrom(base)) {
                        LOGGER.log(Level.WARNING, "Invalid complex property value " + value);
                    }

                    final int size = Array.getLength(value);
                    if (size > 0) {
                        //list of objects
                        final List<Feature> subs = new ArrayList<>();
                        for (int i = 0; i < size; i++) {
                            Object subValue = Array.get(value, i);
                            final Feature subComplexAttribute;
                            if (subValue instanceof Map) {
                                subComplexAttribute = assoType.newInstance();
                                fillFeature(subComplexAttribute, (Map) Array.get(value, i));
                            } else if (subValue instanceof GeoJSONFeature) {
                                subComplexAttribute = toFeature((GeoJSONFeature)subValue, assoType);
                            } else {
                                throw new IllegalArgumentException("Sub value must be a GeoJSONFeature or a map");
                            }
                            subs.add(subComplexAttribute);
                        }
                        feature.setPropertyValue(attName, subs);
                    }
                } else if (value instanceof Map) {
                    final Feature subComplexAttribute = assoType.newInstance();
                    fillFeature(subComplexAttribute, (Map) value);
                    feature.setPropertyValue(attName, subComplexAttribute);
                } else if (value instanceof GeoJSONFeature) {
                    final Feature subComplexAttribute = toFeature((GeoJSONFeature)value, assoType);
                    feature.setPropertyValue(attName, subComplexAttribute);
                } else if (value instanceof GeoJSONFeatureCollection) {
                    GeoJSONFeatureCollection collection = (GeoJSONFeatureCollection) value;
                    final List<Feature> subFeatures = new ArrayList<>();
                    for (GeoJSONFeature subFeature : collection.getFeatures()) {
                        subFeatures.add(toFeature(subFeature, assoType));
                    }
                    feature.setPropertyValue(attName, subFeatures);
                 } else {
                    LOGGER.warning("Unexpected attribute value type:" + value.getClass());
                }

            } else if (type instanceof AttributeType) {
                final Attribute<?> property = (Attribute<?>) feature.getProperty(type.getName().toString());
                fillProperty(property, value);
            }
        }
    }

    /**
     * Try to convert value as expected in PropertyType description.
     *
     * @param prop
     * @param value
     */
    private void fillProperty(Attribute prop, Object value) throws BackingStoreException {

        Object convertValue = null;
        try {
            if (value instanceof GeoJSONGeometry) {
                convertValue = GeoJSONGeometry.toJTS((GeoJSONGeometry)value, null);
            } else if (value != null) {
                final AttributeType<?> propertyType = prop.getType();
                final Class binding = propertyType.getValueClass();

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
        } catch (UnconvertibleObjectException e1) {
            throw new BackingStoreException(String.format("Inconvertible property %s : %s",
                    prop.getName().tip().toString(), e1.getMessage()), e1);
        }

        prop.setValue(convertValue);
    }

    /**
     * Rebuild nDim arrays recursively
     *
     * @param candidate
     * @param componentType
     * @param depth
     * @return Array object
     * @throws UnconvertibleObjectException
     */
    private Object rebuildArray(Object candidate, Class componentType, int depth) throws UnconvertibleObjectException {
        if (candidate == null) {
            return null;
        }

        if (candidate.getClass().isArray()) {
            final int size = Array.getLength(candidate);
            final int[] dims = new int[depth];
            dims[0] = size;
            final Object rarray = Array.newInstance(componentType, dims);
            depth--;
            for (int k = 0; k < size; k++) {
                Array.set(rarray, k, rebuildArray(Array.get(candidate, k), componentType, depth));
            }
            return rarray;
        } else if (componentType.isInstance(candidate)) {
            return candidate;
        } else return convert(candidate, componentType);
    }

    /**
     * Convert value object into binding class
     *
     * @param value
     * @param binding
     * @return
     * @throws UnconvertibleObjectException
     */
    private Object convert(Object value, Class binding) throws UnconvertibleObjectException {
        final Class<?> valueClass = Numbers.primitiveToWrapper(value.getClass());
        binding = Numbers.primitiveToWrapper(binding);
        AbstractMap.SimpleEntry<Class, Class> key = new AbstractMap.SimpleEntry<>(valueClass, binding);
        ObjectConverter converter = convertersCache.get(key);

        if (converter == null) {
            converter = ObjectConverters.find(valueClass, binding);
            convertersCache.put(key, converter);
        }
        return converter.apply(value);
    }

    /**
     * Convert a GeoJSONGeometry to Feature.
     *
     * @param jsonGeometry
     * @return
     */
    protected Feature toFeature(GeoJSONGeometry jsonGeometry) {
        final FTypeInformation fti = ftInfos.get(featureType);
        final Feature feature = featureType.newInstance();
        final Geometry geom = GeoJSONGeometry.toJTS(jsonGeometry, fti.crs);
        feature.setPropertyValue(fti.geometryName, geom);
        return feature;
    }

    @Override
    public void remove() {
        throw new BackingStoreException("Not supported on reader.");
    }

    @Override
    public void close() {
        try {
            // If our object is a feature collection, it could get an opened connexion to a file. We must dispose it.
            if (jsonObj instanceof AutoCloseable) {
                ((AutoCloseable) jsonObj).close();
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Cannot close a read resource.", e);
        }
    }
}
