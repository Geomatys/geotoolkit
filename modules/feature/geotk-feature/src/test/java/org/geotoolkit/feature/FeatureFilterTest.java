/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 * 
 *    (C) 2003-2008, Open Source Geospatial Foundation (OSGeo)
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
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import org.geotoolkit.feature.simple.SimpleFeatureBuilder;
import org.geotoolkit.feature.simple.SimpleFeatureTypeBuilder;
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

import static org.junit.Assert.*;


public class FeatureFilterTest {

    private static final FilterFactory2 FF = new DefaultFilterFactory2();

    public FeatureFilterTest() {
    }

    /**
     * Test that we get acces attributs without knowing the namespace
     */
    @Test
    public void testRetrieve() {

        final SimpleFeatureTypeBuilder sftb = new SimpleFeatureTypeBuilder();
        sftb.setName("testing");
        sftb.add(new DefaultName("http://test1.com", "att_string"), String.class);
        sftb.add(new DefaultName("http://test2.com", "att_string"), String.class);
        sftb.add(new DefaultName(null, "att_double"), String.class);

        final SimpleFeatureType sft = sftb.buildFeatureType();

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


        final SimpleFeatureTypeBuilder sftb = new SimpleFeatureTypeBuilder();
        sftb.setName("testing");
        sftb.add(att_1, String.class);
        sftb.add(att_2, String.class);
        sftb.add(att_3, Double.class);
        final SimpleFeatureType sft = sftb.buildFeatureType();

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


        final SimpleFeatureTypeBuilder sftb = new SimpleFeatureTypeBuilder();
        sftb.setName("testing");
        sftb.add(att_1, String.class);
        sftb.add(att_2, String.class);
        sftb.add(att_3, Double.class);
        final SimpleFeatureType sft = sftb.buildFeatureType();

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


        final SimpleFeatureTypeBuilder sftb = new SimpleFeatureTypeBuilder();
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

        final SimpleFeatureType sft = sftb.buildFeatureType();

        final SimpleFeatureBuilder sfb = new SimpleFeatureBuilder(sft);
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

        Literal geometry = FF.literal(factory.createMultiPoint(points));
        PropertyName property = FF.property(multiPointProperty);
        Equals filter = FF.equal(property, geometry);
        boolean match = filter.evaluate(sf);
        assertTrue(match);
    }

}
