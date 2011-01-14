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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;

import org.geotoolkit.util.logging.Logging;

import org.opengis.coverage.grid.GridCoverage;
import org.opengis.feature.Attribute;
import org.opengis.feature.ComplexAttribute;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureFactory;
import org.opengis.feature.IllegalAttributeException;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AssociationType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.ComplexType;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.feature.type.PropertyType;
import org.opengis.filter.identity.Identifier;

/**
 *
 * @version $Id$
 *
 * @author Cédric Briançon (Geomatys)
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public final class FeatureUtilities {

    private static final Logger LOGGER = Logging.getLogger(FeatureUtilities.class);

    private static final FeatureFactory FF = FactoryFinder
            .getFeatureFactory(new Hints(Hints.FEATURE_FACTORY, LenientFeatureFactory.class));

    private static final GeometryFactory GF = new GeometryFactory();

    private FeatureUtilities(){}

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

    ////////////////////////////////////////////////////////////////////////////
    // COPY OPERATIONS /////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    public static <T extends Property> T copy(final T property){
        return copy(property,false,null);
    }

    public static <T extends Property> T copy(final T property, final String newId){
        return copy(property,false,newId);
    }

    public static <T extends Property> T deepCopy(final T property){
        return copy(property,true,null);
    }

    public static <T extends Property> T deepCopy(final T property, String newId){
        return copy(property,true,null);
    }

    /**
     *
     * @param property : property to copy
     * @param deep : duplicate property value.
     * @param newId : replace current property id if newId is not null
     * @return copy of the property
     */
    public static <T extends Property> T copy(final T property, final boolean deep, final String newId){

        final Property copy;
        if(property instanceof ComplexAttribute){
            final ComplexAttribute ga = (ComplexAttribute) property;
            final String strId;
            if(newId == null){
                final Identifier id = ga.getIdentifier();
                strId = (id == null) ? null : id.getID().toString();
            }else{
                strId = newId;
            }
            final AttributeDescriptor desc = ga.getDescriptor();

            final Collection<Property> properties = ga.getProperties();
            final Collection<Property> copies = new ArrayList<Property>();
            for(final Property prop : properties){
                copies.add(copy(prop,deep,null));
            }
            copy = FF.createComplexAttribute(copies, desc, strId);

        }else if(property instanceof Attribute){
            final Attribute ga = (Attribute) property;
            final String strId;
            if(newId == null){
                final Identifier id = ga.getIdentifier();
                strId = (id == null) ? null : id.getID().toString();
            }else{
                strId = newId;
            }

            final Object value = (deep) ? duplicate(property.getValue()) : property.getValue();
            copy = FF.createAttribute(value, ga.getDescriptor(),strId);
        }else{
            throw new IllegalArgumentException("Unexpected type : "+ property.getClass());
        }

        //copy user data
        copy.getUserData().putAll(property.getUserData());
        return (T)copy;
    }

    public static Object duplicate(final Object src) {

        if (src == null) {
            return null;
        }

        // The following are things I expect
        // Features will contain.
        if (src instanceof String || src instanceof Boolean || src instanceof Character || src instanceof Number) {
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

        // We are now into diminishing returns
        // I don't expect Features to contain these often
        // (eveything is still nice and recursive)
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

        //can't find a solution to duplicate this object
        LOGGER.log(Level.WARNING, "",new SimpleIllegalAttributeException(
                "Do not know how to deep copy " + type.getName()));
        return src;
    }

    ////////////////////////////////////////////////////////////////////////////
    // TEMPLATE OPERATIONS /////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    public static Property defaultProperty(final PropertyDescriptor desc){
        return defaultProperty(desc, null);
    }

    public static Property defaultProperty(final PropertyDescriptor desc, final String id){
        final PropertyType type = desc.getType();

        if(type instanceof ComplexType){
            final AttributeDescriptor attDesc = (AttributeDescriptor) desc;
            final ComplexType ct = (ComplexType) type;

            final Collection<Property> props = new ArrayList<Property>();
            for(final PropertyDescriptor subDesc : ct.getDescriptors()){
                for(int i=0,n=subDesc.getMinOccurs();i<n;i++){
                    final Property prop = defaultProperty(subDesc);
                    if(prop != null){
                        props.add(prop);
                    }
                }
            }
            return FF.createComplexAttribute(props, attDesc, id);

        }else if(type instanceof AttributeType){
            final AttributeDescriptor attDesc = (AttributeDescriptor) desc;
            final Object value = defaultPropertyValue(desc);
            return FF.createAttribute(value, attDesc, id);

        }else if(type instanceof AssociationType){
            //can not create a default value for this
            return null;
        }

        throw new IllegalArgumentException("Unhandled type : " + type);
    }

    public static Property defaultProperty(final ComplexType type){
        return defaultProperty(type, null);
    }

    public static Property defaultProperty(final ComplexType type, final String id){

        final Collection<Property> props = new ArrayList<Property>();
        for(final PropertyDescriptor subDesc : type.getDescriptors()){
            for(int i=0,n=subDesc.getMinOccurs();i<n;i++){
                final Property prop = defaultProperty(subDesc);
                if(prop != null){
                    props.add(prop);
                }
            }
        }
        return FF.createComplexAttribute(props, type, id);
    }

    public static SimpleFeature defaultFeature(final SimpleFeatureType type, final String id){
        return (SimpleFeature)defaultProperty(type, id);
    }

    public static Feature defaultFeature(final FeatureType type, final String id){
        return (SimpleFeature)defaultProperty(type, id);
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
    public static Object defaultPropertyValue(final PropertyDescriptor attributeType)
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

}
