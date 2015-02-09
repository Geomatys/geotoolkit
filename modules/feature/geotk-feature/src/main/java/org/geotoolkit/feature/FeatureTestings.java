/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2015, Geomatys
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
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.feature.simple.SimpleFeature;
import org.geotoolkit.feature.simple.SimpleFeatureType;
import org.geotoolkit.feature.type.AssociationType;
import org.geotoolkit.feature.type.AttributeDescriptor;
import org.geotoolkit.feature.type.AttributeType;
import org.geotoolkit.feature.type.ComplexType;
import org.geotoolkit.feature.type.FeatureType;
import org.geotoolkit.feature.type.PropertyDescriptor;
import org.geotoolkit.feature.type.PropertyType;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public final class FeatureTestings {

    private static final String[] RANDOM_TEXT= new String[]{
        "Geotoolkit.org (abridged Geotk) is a free software, Java language library "+
        "for developing geospatial applications. The library can be used for desktop or server applications.",
        "Geotk is built on top of Apache SIS and is used as a laboratory for the later. The Geotk modules will be reviewed,"+
        "refactored, and - if accepted - integrated into the core Apache SIS. These modules currently provide the experimental"+
        "rendering module, a feature model, a coverage model, and the symbology extension modules.",
        "The Geotk metadata module has already migrated to Apache SIS. The Geotk referencing module is in process of being migrated to Apache SIS.",
        "The Geotk project plans to extend the library both by expanding the current modules "+
        "and providing more modules for the library. The referencing module will be extended to "+
        "handle new projections. The coverage module will be extended by formalizing an API for image operations."
    };

    private static final FeatureFactory FF = FeatureFactory.LENIENT;
    private static final GeometryFactory GF = new GeometryFactory();
    
    private FeatureTestings(){}

    /**
     * Returns a non-null random value for the class that is passed in.  This is a helper class an can't create a
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
     * @return a andom value for the given class.
     */
    public static Object testingValue(final Class type) {

        final double rand1 = Math.random();

        if (type == String.class || type == Object.class) {
            return RANDOM_TEXT[(int)rand1*(RANDOM_TEXT.length-1)];
        }
        if (type == Integer.class) {
            return (int)(rand1*100-50);
        }
        if (type == Double.class) {
            return (double)(rand1*100-50);
        }
        if (type == Long.class) {
            return (long)(rand1*100-50);
        }
        if (type == Short.class) {
            return (short)(rand1*100-50);
        }
        if (type == Float.class) {
            return (float)(rand1*100-50);
        }
        if (type == BigDecimal.class) {
            return BigDecimal.valueOf(rand1*100-50);
        }
        if (type == BigInteger.class) {
            return BigInteger.valueOf((int)(rand1*100-50));
        }
        if (type == Character.class) {
            return (char)(rand1*255);
        }
        if (type == Boolean.class) {
            return rand1>0.5;
        }
        if (type == Timestamp.class) {
            return new Timestamp((int)(rand1*1000000));
        }
        if (type == java.sql.Date.class) {
            return new java.sql.Date((int)(rand1*1000000));
        }
        if (type == java.sql.Time.class) {
            return new java.sql.Time((int)(rand1*1000000));
        }
        if (type == java.util.Date.class) {
            return new java.util.Date((int)(rand1*1000000));
        }
        final double rand2 = Math.random();

        final Coordinate crd = new Coordinate(rand1*10-5, rand2*10-5);

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


    /**
     * Provides a random value for attributeType.
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
    public static Object testingPropertyValue(final PropertyDescriptor attributeType)
            throws IllegalAttributeException {

        if(attributeType instanceof AttributeDescriptor){
            final AttributeDescriptor attDesc = (AttributeDescriptor) attributeType;
            final Class<Object> valueClass = attDesc.getType().getValueClass();
            try{
                return testingValue(valueClass);
            }catch(IllegalArgumentException ex){
                return null;
            }
        }else{
            return null;
        }
    }

    public static Property testingProperty(final PropertyDescriptor desc){
        return testingProperty(desc, null);
    }

    public static Property testingProperty(final PropertyDescriptor desc, final String id){
        final PropertyType type = desc.getType();

        if(type instanceof ComplexType){
            final AttributeDescriptor attDesc = (AttributeDescriptor) desc;
            final ComplexType ct = (ComplexType) type;

            final Collection<Property> props = new ArrayList<>();
            for(final PropertyDescriptor subDesc : ct.getDescriptors()){
                final int minOcc = subDesc.getMinOccurs();
                final int maxOcc = Math.min(subDesc.getMinOccurs()+10,subDesc.getMaxOccurs());
                final int nbOcc = minOcc + (int)Math.round(Math.random()*(maxOcc-minOcc));
                for(int i=0,n=nbOcc;i<n;i++){
                    final Property prop = testingProperty(subDesc);
                    if(prop != null){
                        props.add(prop);
                    }
                }
            }
            return FF.createComplexAttribute(props, attDesc, id);

        }else if(type instanceof AttributeType){
            final AttributeDescriptor attDesc = (AttributeDescriptor) desc;
            final Object value = testingPropertyValue(desc);
            return FF.createAttribute(value, attDesc, id);

        }else if(type instanceof AssociationType){
            //can not create a test value for this
            return null;
        }

        throw new IllegalArgumentException("Unhandled type : " + type);
    }

    public static ComplexAttribute testingProperty(final ComplexType type){
        return testingProperty(type, "");
    }

    public static ComplexAttribute testingProperty(final ComplexType type, final String id){

        ArgumentChecks.ensureNonNull("type", type);
        final Collection<Property> props = new ArrayList<>();
        for(final PropertyDescriptor subDesc : type.getDescriptors()){
            final int minOcc = subDesc.getMinOccurs();
            final int maxOcc = Math.min(subDesc.getMinOccurs()+10,subDesc.getMaxOccurs());
            final int nbOcc = minOcc + (int)Math.round(Math.random()*(maxOcc-minOcc));
            for(int i=0,n=nbOcc;i<n;i++){
                final Property prop = testingProperty(subDesc);
                if(prop != null){
                    props.add(prop);
                }
            }
        }
        return FF.createComplexAttribute(props, type, id);
    }

    public static SimpleFeature testingFeature(final SimpleFeatureType type, final String id){
        return (SimpleFeature)testingProperty(type, id);
    }

    /**
     * Create a test feature, all properties will be filled with random values.
     * This is convinient method use only for tests debugging and profiling.
     *
     * @param type feature type
     * @param id feature id
     * @return test feature.
     */
    public static Feature testingFeature(final FeatureType type, String id){
        return (Feature)testingProperty(type, id);
    }


}
