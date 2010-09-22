/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
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
package org.geotoolkit.feature;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;

import org.geotoolkit.feature.simple.SimpleFeatureBuilder;

import org.opengis.coverage.grid.GridCoverage;
import org.opengis.feature.Attribute;
import org.opengis.feature.ComplexAttribute;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureFactory;
import org.opengis.feature.GeometryAttribute;
import org.opengis.feature.IllegalAttributeException;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.filter.identity.Identifier;

/**
 *
 * @version $Id$
 *
 * @author Cédric Briançon (Geomatys)
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class FeatureUtilities {

    protected static final FeatureFactory FF = FactoryFinder
            .getFeatureFactory(new Hints(Hints.FEATURE_FACTORY, LenientFeatureFactory.class));

    private static final GeometryFactory GF = new GeometryFactory();

    protected FeatureUtilities(){}

    /**
     * Returns a non-null default value for the class that is passed in.  This is a helper class an can't create a
     * default class for any type but it does support:
     * <ul>
     * <li>String</li>
     * <li>Object - will return empty string</li>
     * <li>Number</li>
     * <li>Character</li>
     * <li>JTS Geometries</li>
     * </ul>
     *
     *
     * @param type
     * @return a default value for the given class.
     */
    public static Object defaultValue(final Class type) {
        if (type == String.class || type == Object.class) {
            return "";
        }
        if (type == Integer.class) {
            return Integer.valueOf(0);
        }
        if (type == Double.class) {
            return Double.valueOf(0);
        }
        if (type == Long.class) {
            return Long.valueOf(0);
        }
        if (type == Short.class) {
            return Short.valueOf((short) 0);
        }
        if (type == Float.class) {
            return Float.valueOf(0.0f);
        }
        if (type == BigDecimal.class) {
            return BigDecimal.valueOf(0);
        }
        if (type == BigInteger.class) {
            return BigInteger.valueOf(0);
        }
        if (type == Character.class) {
            return Character.valueOf(' ');
        }
        if (type == Boolean.class) {
            return Boolean.FALSE;
        }
        if (type == Timestamp.class) {
            return new Timestamp(System.currentTimeMillis());
        }
        if (type == java.sql.Date.class) {
            return new java.sql.Date(System.currentTimeMillis());
        }
        if (type == java.sql.Time.class) {
            return new java.sql.Time(System.currentTimeMillis());
        }
        if (type == java.util.Date.class) {
            return new java.util.Date();
        }

        final Coordinate crd = new Coordinate(0, 0);        

        if (type == Point.class) {
            final Point pt = GF.createPoint(crd);
            return pt;
        }
        if (type == MultiPoint.class) {
            final Point pt = GF.createPoint(crd);
            return GF.createMultiPoint(new Point[]{pt});
        }
        if (type == LineString.class) {
            return GF.createLineString(new Coordinate[]{crd, crd, crd, crd});
        }
        final LinearRing linearRing = GF.createLinearRing(new Coordinate[]{crd, crd, crd, crd});
        if (type == LinearRing.class) {
            return linearRing;
        }
        if (type == MultiLineString.class) {
            return GF.createMultiLineString(new LineString[]{linearRing});
        }
        final Polygon polygon = GF.createPolygon(linearRing, new LinearRing[0]);
        if (type == Polygon.class) {
            return polygon;
        }
        if (type == MultiPolygon.class) {
            return GF.createMultiPolygon(new Polygon[]{polygon});
        }

        throw new IllegalArgumentException(type + " is not supported by this method");
    }

    public static Object duplicate(Object src) {
//JD: this method really needs to be replaced with somethign better

        if (src == null) {
            return null;
        }

        //
        // The following are things I expect
        // Features will contain.
        //
        if (src instanceof String || src instanceof Integer || src instanceof Double || src instanceof Float || src instanceof Byte || src instanceof Boolean || src instanceof Short || src instanceof Long || src instanceof Character || src instanceof Number) {
            return src;
        }

        if (src instanceof Date) {
            return new Date(((Date) src).getTime());
        }

        if (src instanceof URL || src instanceof URI) {
            return src; //immutable
        }

        if (src instanceof Object[]) {
            final Object[] array = (Object[]) src;
            final Object[] copy = new Object[array.length];

            for (int i = 0; i < array.length; i++) {
                copy[i] = duplicate(array[i]);
            }

            return copy;
        }

        if (src instanceof Geometry) {
            final Geometry geometry = (Geometry) src;

            return geometry.clone();
        }

        if (src instanceof SimpleFeature) {
            final SimpleFeature feature = (SimpleFeature) src;
            return SimpleFeatureBuilder.copy(feature);
        }

        //
        // We are now into diminishing returns
        // I don't expect Features to contain these often
        // (eveything is still nice and recursive)
        //
        final Class type = src.getClass();

        if (type.isArray() && type.getComponentType().isPrimitive()) {
            final int length = Array.getLength(src);
            final Object copy = Array.newInstance(type.getComponentType(), length);
            System.arraycopy(src, 0, copy, 0, length);

            return copy;
        }

        if (type.isArray()) {
            final int length = Array.getLength(src);
            final Object copy = Array.newInstance(type.getComponentType(), length);

            for (int i = 0; i < length; i++) {
                Array.set(copy, i, duplicate(Array.get(src, i)));
            }

            return copy;
        }

        if (src instanceof List) {
            final List list = (List) src;
            final List copy = new ArrayList(list.size());

            for (final Iterator i = list.iterator(); i.hasNext();) {
                copy.add(duplicate(i.next()));
            }

            return Collections.unmodifiableList(copy);
        }

        if (src instanceof Map) {
            final Map map = (Map) src;
            final Map copy = new HashMap(map.size());

            for (final Iterator i = map.entrySet().iterator(); i.hasNext();) {
                final Map.Entry entry = (Map.Entry) i.next();
                copy.put(entry.getKey(), duplicate(entry.getValue()));
            }

            return Collections.unmodifiableMap(copy);
        }

        if (src instanceof GridCoverage) {
            return src; // inmutable
        }


        //
        // I have lost hope and am returning the orgional reference
        // Please extend this to support additional classes.
        //
        // And good luck getting Cloneable to work
        throw new SimpleIllegalAttributeException("Do not know how to deep copy " + type.getName());
    }

    public static Feature copy(Feature feature){
        if(feature instanceof SimpleFeature){
            return SimpleFeatureBuilder.copy((SimpleFeature) feature);
        }

        final Collection<Property> properties = feature.getProperties();
        final Collection<Property> copies = new ArrayList<Property>();
        for(Property prop : properties){
            copies.add(copy(prop));
        }
        return FF.createFeature(copies, feature.getDescriptor(), feature.getIdentifier().getID());
    }

    public static Property copy(Property property){

        final Property copy;
        if(property instanceof GeometryAttribute){
            final GeometryAttribute ga = (GeometryAttribute) property;
            final Identifier id = ga.getIdentifier();
            if(id != null){
                 copy = FF.createGeometryAttribute(property.getValue(), ga.getDescriptor(),
                    ga.getIdentifier().getID().toString(), null);
            }else{
                copy = FF.createGeometryAttribute(property.getValue(), ga.getDescriptor(), null, null);
            }
            
        }else if(property instanceof Attribute){
            final Attribute ga = (Attribute) property;
            final Identifier id = ga.getIdentifier();
            if(id != null){
                 copy = FF.createAttribute(property.getValue(), ga.getDescriptor(),
                    ga.getIdentifier().getID().toString());
            }else{
                 copy = FF.createAttribute(property.getValue(), ga.getDescriptor(), null);
            }
           
        }else if(property instanceof ComplexAttribute){
            throw new IllegalArgumentException("Not yet supported : "+ property.getClass());
        }else{
            throw new IllegalArgumentException("Unexpected type : "+ property.getClass());
        }

        //must copy user data
        return copy;
    }

    public static SimpleFeature defaultFeature(SimpleFeatureType type, String id){
        return (SimpleFeature)defaultFeature((FeatureType)type, id);
    }

    public static Feature defaultFeature(FeatureType type, String id){
        final List<Property> properties = new ArrayList<Property>();
        for(PropertyDescriptor desc : type.getDescriptors()){
            properties.add(defaultProperty(desc));
        }
        return FF.createFeature(properties, type, id);
    }

    /**
     * Provides a defautlValue for attributeType.
     *
     * <p>
     * Will return null if attributeType isNillable(), or attempt to use
     * Reflection, or attributeType.parse( null )
     * </p>
     *
     * @param attributeType
     * @return null for nillable attributeType, attempt at reflection
     * @throws IllegalAttributeException If value cannot be constructed for
     *         attribtueType
     */
    public static Object defaultValue(final PropertyDescriptor attributeType)
            throws IllegalAttributeException {

        if(attributeType instanceof AttributeDescriptor){
            final Object value = ((AttributeDescriptor)attributeType).getDefaultValue();

            if (value == null && !attributeType.isNillable()) {
                return null; // sometimes there is no valid default value :-(
                // throw new IllegalAttributeException("Got null default value for non-null type.");
            }
            return value;
        }else{
            return null;
        }
    }

    public static Property defaultProperty(final PropertyDescriptor desc){
        final Object value = defaultValue(desc);
        if(desc instanceof GeometryDescriptor){
            return FF.createGeometryAttribute(value, (GeometryDescriptor)desc, null, null);
        }else if(desc instanceof AttributeDescriptor){
            return FF.createAttribute(value, (AttributeDescriptor)desc, null);
        }else{
            //todo not the correct way to do it
            return new DefaultProperty(value, desc);
        }
    }

}
