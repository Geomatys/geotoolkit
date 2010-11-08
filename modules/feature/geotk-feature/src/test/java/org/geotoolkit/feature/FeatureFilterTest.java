/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 * 
 *    (C) 2003-2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;

import org.geotoolkit.feature.simple.SimpleFeatureBuilder;
import org.geotoolkit.filter.DefaultFilterFactory2;
import org.geotoolkit.filter.accessor.Accessors;
import org.geotoolkit.filter.accessor.PropertyAccessor;
import org.geotoolkit.referencing.CRS;

import org.junit.Test;

import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.Name;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.expression.Literal;
import org.opengis.filter.expression.PropertyName;
import org.opengis.filter.spatial.Equals;
import org.opengis.filter.spatial.Intersects;

import static org.junit.Assert.*;


public class FeatureFilterTest {

    private static final FilterFactory2 FF = new DefaultFilterFactory2();

    public FeatureFilterTest() {
    }

    /**
     * Test that we get acces attributs without knowing the namespace
     */
    @Test
    public void testSimpleFeatureTypeAccessor() {

        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("testing");
        ftb.add(new DefaultName("http://test1.com", "att_string"), String.class);
        ftb.add(new DefaultName("http://test2.com", "att_string"), String.class);
        ftb.add(new DefaultName(null, "att_double"), String.class);

        final SimpleFeatureType sft = ftb.buildSimpleFeatureType();

        //test a no namespace property
        PropertyAccessor accessor = Accessors.getAccessor(SimpleFeatureType.class, "att_double", AttributeDescriptor.class);
        assertNotNull(accessor);
        AttributeDescriptor desc = (AttributeDescriptor) accessor.get(sft, "att_double", AttributeDescriptor.class);

        assertNotNull(desc);
        assertEquals(desc.getName(), new DefaultName(null, "att_double"));

        //test a namespace property without namespace
        accessor = Accessors.getAccessor(SimpleFeatureType.class, "att_string", AttributeDescriptor.class);
        assertNotNull(accessor);
        desc = (AttributeDescriptor) accessor.get(sft, "att_string", AttributeDescriptor.class);

        assertNotNull(desc);
        assertEquals(desc.getName(), new DefaultName("http://test1.com", "att_string"));

        //test a namespace property with namespace
        accessor = Accessors.getAccessor(SimpleFeatureType.class, "http://test1.com:att_string", AttributeDescriptor.class);
        assertNotNull(accessor);
        desc = (AttributeDescriptor) accessor.get(sft, "http://test1.com:att_string", AttributeDescriptor.class);
        assertNotNull(desc);
        assertEquals(desc.getName(), new DefaultName("http://test1.com", "att_string"));

        accessor = Accessors.getAccessor(SimpleFeatureType.class, "http://test2.com:att_string", AttributeDescriptor.class);
        assertNotNull(accessor);
        desc = (AttributeDescriptor) accessor.get(sft, "http://test2.com:att_string", AttributeDescriptor.class);
        assertNotNull(desc);
        assertEquals(desc.getName(), new DefaultName("http://test2.com", "att_string"));

    }

    @Test
    public void testPropertyNameFeatureTypeAcces(){
        final Name att_1 = new DefaultName("http://test1.com", "att_string");
        final Name att_2 = new DefaultName("http://test2.com", "att_string");
        final Name att_3 = new DefaultName(null, "att_double");


        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("testing");
        ftb.add(att_1, String.class);
        ftb.add(att_2, String.class);
        ftb.add(att_3, Double.class);
        final SimpleFeatureType sft = ftb.buildSimpleFeatureType();

        PropertyName property = null;
        PropertyDescriptor desc = null;

        // att 1 ---------------------------------------------------------------
        property = FF.property("http://test1.com:att_string");
        desc = (PropertyDescriptor) property.evaluate(sft);
        assertNotNull(desc);
        assertEquals(att_1, desc.getName());

        property = FF.property("{http://test1.com}att_string");
        desc = (PropertyDescriptor) property.evaluate(sft);
        assertNotNull(desc);
        assertEquals(att_1, desc.getName());

        property = FF.property("att_string");
        desc = (PropertyDescriptor) property.evaluate(sft);
        assertNotNull(desc);
        assertEquals(att_1, desc.getName());

        // att 2 ---------------------------------------------------------------
        property = FF.property("http://test2.com:att_string");
        desc = (PropertyDescriptor) property.evaluate(sft);
        assertNotNull(desc);
        assertEquals(att_2, desc.getName());

        property = FF.property("{http://test2.com}att_string");
        desc = (PropertyDescriptor) property.evaluate(sft);
        assertNotNull(desc);
        assertEquals(att_2, desc.getName());

        property = FF.property("att_string");
        desc = (PropertyDescriptor) property.evaluate(sft);
        assertNotNull(desc);
        assertEquals(att_1, desc.getName()); //no name space, must return the first att, so att_1 not att_2

        //att 3 ----------------------------------------------------------------
        property = FF.property("http://test1.com:att_double");
        desc = (PropertyDescriptor) property.evaluate(sft);
        assertNull(desc);

        property = FF.property("{http://test2.com}att_double");
        desc = (PropertyDescriptor) property.evaluate(sft);
        assertNull(desc);

        property = FF.property("att_double");
        desc = (PropertyDescriptor) property.evaluate(sft);
        assertNotNull(desc);
        assertEquals(att_3, desc.getName());

    }

    @Test
    public void testPropertyNameFeatureAcces(){
        final Name att_1 = new DefaultName("http://test1.com", "att_string");
        final Name att_2 = new DefaultName("http://test2.com", "att_string");
        final Name att_3 = new DefaultName(null, "att_double");


        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("testing");
        ftb.add(att_1, String.class);
        ftb.add(att_2, String.class);
        ftb.add(att_3, Double.class);
        final SimpleFeatureType sft = ftb.buildSimpleFeatureType();

        final SimpleFeatureBuilder sfb = new SimpleFeatureBuilder(sft);
        sfb.set(att_1, "str1");
        sfb.set(att_2, "str2");
        sfb.set(att_3, 10d);
        final SimpleFeature sf = sfb.buildFeature("id");

        PropertyName property = null;
        Object value = null;

        // att 1 ---------------------------------------------------------------
        assertEquals("str1", sf.getAttribute(att_1));
        assertEquals("str1", sf.getAttribute("http://test1.com:att_string"));
        assertEquals("str1", sf.getAttribute("{http://test1.com}att_string"));
        assertEquals("str1", sf.getAttribute("att_string"));
        assertEquals("str1", sf.getProperty(att_1).getValue());
        assertEquals("str1", sf.getProperty("http://test1.com:att_string").getValue());
        assertEquals("str1", sf.getProperty("{http://test1.com}att_string").getValue());

        property = FF.property("http://test1.com:att_string");
        value = property.evaluate(sf);
        assertNotNull(value);
        assertEquals("str1", value);

        property = FF.property("{http://test1.com}att_string");
        value = property.evaluate(sf);
        assertNotNull(value);
        assertEquals("str1", value);

        property = FF.property("att_string");
        value = property.evaluate(sf);
        assertNotNull(value);
        assertEquals("str1", value);

        // att 2 ---------------------------------------------------------------
        assertEquals("str2", sf.getAttribute(att_2));
        assertEquals("str2", sf.getAttribute("http://test2.com:att_string"));
        assertEquals("str2", sf.getAttribute("{http://test2.com}att_string"));
        assertEquals("str1", sf.getAttribute("att_string")); //no name space, must return the first att, so att_1 not att_2
        assertEquals("str2", sf.getProperty(att_2).getValue());
        assertEquals("str2", sf.getProperty("http://test2.com:att_string").getValue());
        assertEquals("str2", sf.getProperty("{http://test2.com}att_string").getValue());

        property = FF.property("http://test2.com:att_string");
        value = property.evaluate(sf);
        assertNotNull(value);
        assertEquals("str2", value);

        property = FF.property("{http://test2.com}att_string");
        value = property.evaluate(sf);
        assertNotNull(value);
        assertEquals("str2", value);

        property = FF.property("att_string");
        value = property.evaluate(sf);
        assertNotNull(value);
        assertEquals("str1", value); //no name space, must return the first att, so att_1 not att_2

        //att 3 ----------------------------------------------------------------
        assertEquals(10d, sf.getAttribute(att_3));
        assertEquals(null, sf.getAttribute("http://test2.com:att_double"));
        assertEquals(null, sf.getAttribute("{http://test2.com}att_double"));
        assertEquals(10d, sf.getAttribute("att_double"));
        assertEquals(10d, sf.getProperty(att_3).getValue());
        assertEquals(10d, sf.getProperty("att_double").getValue());
        assertEquals(null, sf.getProperty("http://test2.com:att_double"));
        assertEquals(null, sf.getProperty("{http://test2.com}att_double"));

        property = FF.property("http://test1.com:att_double");
        value = property.evaluate(sf);
        assertNull(value);

        property = FF.property("{http://test2.com}att_double");
        value = property.evaluate(sf);
        assertNull(value);

        property = FF.property("att_double");
        value = property.evaluate(sf);
        assertNotNull(value);
        assertEquals(10d, value);

    }


    /**
     * Test that we get acces attributs without knowing the namespace
     */
    @Test
    public void testSpatialFilter() throws Exception {

        /*********************************************************************************************
         *                                                                                           *
         *                            AggregateGeoFeature                                            *
         *                                                                                           *
         *********************************************************************************************/
        
        final Name description = new DefaultName("http://www.opengis.net/gml", "description");
        final Name name = new DefaultName("http://www.opengis.net/gml", "name");
        final Name multiPointProperty = new DefaultName("http://cite.opengeospatial.org/gmlsf", "multiPointProperty");
        final Name multiCurveProperty = new DefaultName("http://cite.opengeospatial.org/gmlsf", "multiCurveProperty");
        final Name multiSurfaceProperty = new DefaultName("http://cite.opengeospatial.org/gmlsf", "multiSurfaceProperty");
        final Name doubleProperty = new DefaultName("http://cite.opengeospatial.org/gmlsf", "doubleProperty");
        final Name intRangeProperty = new DefaultName("http://cite.opengeospatial.org/gmlsf", "intRangeProperty");
        final Name strProperty = new DefaultName("http://cite.opengeospatial.org/gmlsf", "strProperty");
        final Name featureCode = new DefaultName("http://cite.opengeospatial.org/gmlsf", "featureCode");
        final Name id = new DefaultName("http://cite.opengeospatial.org/gmlsf", "id");


        final FeatureTypeBuilder sftb = new FeatureTypeBuilder();
        sftb.setName(new DefaultName("http://cite.opengeospatial.org/gmlsf", "AggregateGeoFeature"));
        sftb.add(description, String.class);
        sftb.add(name, String.class);
        sftb.add(multiPointProperty, MultiPoint.class, CRS.decode("EPSG:4326"));
        sftb.add(multiCurveProperty, MultiLineString.class, CRS.decode("EPSG:4326"));
        sftb.add(multiSurfaceProperty, MultiPolygon.class, CRS.decode("EPSG:4326"));
        sftb.add(doubleProperty, Double.class);
        sftb.add(intRangeProperty, String.class);
        sftb.add(strProperty, String.class);
        sftb.add(featureCode, String.class);
        sftb.add(id, String.class);

        final SimpleFeatureType sft = sftb.buildSimpleFeatureType();

        /*********************************************************************************************
         *                            AggregateGeoFeature 1                                          *
         *********************************************************************************************/
        SimpleFeatureBuilder sfb = new SimpleFeatureBuilder(sft);
        sfb.set(description, "description-f005");
        sfb.set(name, "name-f005");
        GeometryFactory factory = new GeometryFactory();
        Point[] points = new Point[3];
        points[0] = factory.createPoint(new Coordinate(70.83, 29.86));
        points[1] = factory.createPoint(new Coordinate(68.87, 31.08));
        points[2] = factory.createPoint(new Coordinate(71.96, 32.19));
        sfb.set(multiPointProperty, factory.createMultiPoint(points));
        sfb.set(doubleProperty, 2012.78);
        sfb.set(strProperty, "Ma quande lingues coalesce...");
        sfb.set(featureCode, "BK030");
        sfb.set(id, "f005");

        final SimpleFeature sf = sfb.buildFeature("id");

        /*********************************************************************************************
         *                                                                                           *
         *                            EntitéGénérique                                                *
         *                                                                                           *
         *********************************************************************************************/
        final Name attributGeometrie  = new DefaultName("http://cite.opengeospatial.org/gmlsf", "attribut.Géométrie");
        final Name boolProperty = new DefaultName("http://cite.opengeospatial.org/gmlsf", "boolProperty");
        final Name str4Property = new DefaultName("http://cite.opengeospatial.org/gmlsf", "str4Property");
        final Name featureRef = new DefaultName("http://cite.opengeospatial.org/gmlsf", "featureRef");

        sftb.reset();

        sftb.setName(new DefaultName("http://cite.opengeospatial.org/gmlsf", "EntitéGénérique"));
        sftb.add(description, String.class);
        sftb.add(name, String.class);
        sftb.add(attributGeometrie, Geometry.class, CRS.decode("EPSG:4326"));
        sftb.add(boolProperty, Boolean.class);
        sftb.add(str4Property, String.class);
        sftb.add(featureRef, String.class);
        sftb.add(id, String.class);

        final SimpleFeatureType entiteGeneriqueType = sftb.buildSimpleFeatureType();

        sfb = new SimpleFeatureBuilder(entiteGeneriqueType);

        /*********************************************************************************************
         *                            EntitéGénérique 1                                              *
         *********************************************************************************************/
        sfb.set(description, "description-f004");
        sfb.set(name, "name-f004");

        Coordinate[] exteriorCoord = new Coordinate[5];
        exteriorCoord[0] = new Coordinate(60.5, 0);
        exteriorCoord[1] = new Coordinate(64, 0);
        exteriorCoord[2] = new Coordinate(64, 6.25);
        exteriorCoord[3] = new Coordinate(60.5, 6.25);
        exteriorCoord[4] = new Coordinate(60.5, 0);

        LinearRing exterior = factory.createLinearRing(exteriorCoord);

        Coordinate[] interiorCoord = new Coordinate[4];
        interiorCoord[0] = new Coordinate(61.5, 2);
        interiorCoord[1] = new Coordinate(62.5, 2);
        interiorCoord[2] = new Coordinate(62, 4);
        interiorCoord[3] = new Coordinate(61.5, 2);

        LinearRing interior = factory.createLinearRing(interiorCoord);
        LinearRing[] interiors = new LinearRing[1];
        interiors[0] = interior;

        sfb.set(attributGeometrie, factory.createPolygon(exterior, interiors));
        sfb.set(boolProperty, false);
        sfb.set(str4Property, "abc3");
        sfb.set(featureRef, "name-f003");
        sfb.set(id, "f004");

        final SimpleFeature entiteGenerique1 = sfb.buildFeature("f004");

        sfb.reset();

        /*********************************************************************************************
         *                            EntitéGénérique 2                                              *
         *********************************************************************************************/

        sfb.set(description, "description-f007");
        sfb.set(name, "name-f007");

        Coordinate[] exteriorCoord2 = new Coordinate[6];
        exteriorCoord2[0] = new Coordinate(35, 15);
        exteriorCoord2[1] = new Coordinate(40, 16);
        exteriorCoord2[2] = new Coordinate(39, 20);
        exteriorCoord2[3] = new Coordinate(37, 22.5);
        exteriorCoord2[4] = new Coordinate(36, 18);
        exteriorCoord2[5] = new Coordinate(35, 15);

        LinearRing exterior2 = factory.createLinearRing(exteriorCoord);

        Coordinate[] interiorCoord2 = new Coordinate[7];
        interiorCoord2[0] = new Coordinate(37.1, 17.5);
        interiorCoord2[1] = new Coordinate(37.2, 17.6);
        interiorCoord2[2] = new Coordinate(37.3, 17.7);
        interiorCoord2[3] = new Coordinate(37.4, 17.8);
        interiorCoord2[4] = new Coordinate(37.5, 17.9);
        interiorCoord2[5] = new Coordinate(37,   17.9);
        interiorCoord2[6] = new Coordinate(37.1, 17.5);

        LinearRing interior2 = factory.createLinearRing(interiorCoord);
        LinearRing[] interiors2 = new LinearRing[1];
        interiors2[0] = interior;

        sfb.set(attributGeometrie, factory.createPolygon(exterior2, interiors2));
        sfb.set(boolProperty, false);
        sfb.set(str4Property, "def4");
        sfb.set(id, "f007");

        final SimpleFeature entiteGenerique2 = sfb.buildFeature("f007");

        sfb.reset();

        /*********************************************************************************************
         *                            EntitéGénérique 3                                              *
         *********************************************************************************************/
        sfb.set(description, "description-f017");
        sfb.set(name, "name-f017");

        Coordinate[] lineCoord = new Coordinate[5];
        lineCoord[0] = new Coordinate(50.174, 4.899);
        lineCoord[1] = new Coordinate(52.652, 5.466);
        lineCoord[2] = new Coordinate(53.891, 6.899);
        lineCoord[3] = new Coordinate(54.382, 7.780);
        lineCoord[4] = new Coordinate(54.982, 8.879);


        sfb.set(attributGeometrie, factory.createLineString(lineCoord));
        sfb.set(boolProperty, false);
        sfb.set(str4Property, "qrst");
        sfb.set(featureRef, "name-f015");
        sfb.set(id, "f017");

        final SimpleFeature entiteGenerique3 = sfb.buildFeature("f017");
        
        Literal geometry = FF.literal(factory.createMultiPoint(points));
        PropertyName property = FF.property(multiPointProperty);
        Equals filter = FF.equal(property, geometry);
        boolean match = filter.evaluate(sf);
        assertTrue(match);


        /*
         * Filter intersects on entitiGenerique*
         */

        Point[] filterPoints = new Point[2];
        filterPoints[0] = factory.createPoint(new Coordinate(38.83, 16.22));
        filterPoints[1] = factory.createPoint(new Coordinate(62.07, 2.48));

        geometry = FF.literal(factory.createMultiPoint(filterPoints));
        property = FF.property(attributGeometrie);
        Intersects intfilter = FF.intersects(property, geometry);
        match = intfilter.evaluate(entiteGenerique1);
        assertFalse(match);

        match = intfilter.evaluate(entiteGenerique2);
        assertFalse(match);

        match = intfilter.evaluate(entiteGenerique3);
        assertFalse(match);
    }

}
