/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2011, Geomatys
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
import com.vividsolutions.jts.geom.Point;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.test.Commons;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.FeatureType;
import static org.junit.Assert.*;

/**
 * Testing feature compliance.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class FeatureTest {

    private static final LenientFeatureFactory FF = new LenientFeatureFactory();
    private static final GeometryFactory GF = new GeometryFactory();

    public FeatureTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testSimpleFeatureCreation(){
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("test");
        ftb.add("geom", Point.class, DefaultGeographicCRS.WGS84);
        ftb.add("string", String.class);
        ftb.setDefaultGeometry("geom");
        final FeatureType type = ftb.buildFeatureType();
        assertTrue(type instanceof SimpleFeatureType);
        assertNotNull(type.getGeometryDescriptor());


        final Collection<Property> properties = new ArrayList<Property>();
        properties.add(FF.createAttribute(GF.createPoint(new Coordinate(50, 60)), type.getGeometryDescriptor(), null));
        properties.add(FF.createAttribute("line1", (AttributeDescriptor) type.getDescriptor("string"), null));
        final Feature feature = FF.createFeature(properties, type, "one-ID");
        assertTrue(feature instanceof SimpleFeature);

        assertNotNull(feature.getProperty("geom"));
        assertNotNull(feature.getProperty("geom").getValue());
        assertTrue(GF.createPoint(new Coordinate(50, 60)).equalsExact((Geometry) feature.getProperty("geom").getValue()));

        assertNotNull(feature.getDefaultGeometryProperty());
        assertNotNull(feature.getDefaultGeometryProperty().getValue());
        assertTrue(GF.createPoint(new Coordinate(50, 60)).equalsExact((Geometry) feature.getDefaultGeometryProperty().getValue()));

        //test serialize
        Commons.serialize(feature);

        //test it doesn't have a descriptor
        assertNull(feature.getDescriptor());

    }

    @Test
    public void testComplexFeatureCreation(){
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("test");
        ftb.add("geom", Point.class, DefaultGeographicCRS.WGS84);
        ftb.add("string", String.class, 0,Integer.MAX_VALUE,false,null);
        ftb.setDefaultGeometry("geom");
        final FeatureType type = ftb.buildFeatureType();
        assertFalse(type instanceof SimpleFeatureType);
        assertNotNull(type.getGeometryDescriptor());


        final Collection<Property> properties = new ArrayList<Property>();
        properties.add(FF.createAttribute(GF.createPoint(new Coordinate(50, 60)), type.getGeometryDescriptor(), null));
        properties.add(FF.createAttribute("line1", (AttributeDescriptor) type.getDescriptor("string"), null));
        properties.add(FF.createAttribute("line2", (AttributeDescriptor) type.getDescriptor("string"), null));
        properties.add(FF.createAttribute("line3", (AttributeDescriptor) type.getDescriptor("string"), null));
        final Feature feature = FF.createFeature(properties, type, "one-ID");
        assertFalse(feature instanceof SimpleFeature);

        assertNotNull(feature.getProperty("geom"));
        assertNotNull(feature.getProperty("geom").getValue());
        assertTrue(GF.createPoint(new Coordinate(50, 60)).equalsExact((Geometry) feature.getProperty("geom").getValue()));

        assertNotNull(feature.getDefaultGeometryProperty());
        assertNotNull(feature.getDefaultGeometryProperty().getValue());
        assertTrue(GF.createPoint(new Coordinate(50, 60)).equalsExact((Geometry) feature.getDefaultGeometryProperty().getValue()));

        //test serialize
        Commons.serialize(feature);

        //test it doesn't have a descriptor
        assertNull(feature.getDescriptor());
    }

    @Test
    public void testSimpleFeatureAccess(){
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("{http://test.com}test");
        ftb.add("{http://test.com}geom", Point.class, DefaultGeographicCRS.WGS84);
        ftb.add("{http://test.com}att_String", String.class);
        ftb.setDefaultGeometry("geom");
        final SimpleFeatureType type = ftb.buildSimpleFeatureType();

        final Point pt = GF.createPoint(new Coordinate(50, 60));

        final Collection<Property> properties = new ArrayList<Property>();
        properties.add(FF.createAttribute(pt, type.getGeometryDescriptor(), null));
        properties.add(FF.createAttribute("line1", (AttributeDescriptor) type.getDescriptor("att_String"), null));
        final SimpleFeature feature = (SimpleFeature) FF.createFeature(properties, type, "one-ID");

        Collection<Property> attributs = feature.getProperties();
        assertEquals(2, attributs.size());
        assertEquals(2, feature.getAttributeCount());
        assertEquals(2, feature.getAttributes().size());
        Iterator<Property> ite;
        Property attribut;
        Object attributValue;

        ////////////////////////////////////////////////////////////////////////
        //test accessing a single value property ///////////////////////////////

        attributs = feature.getProperties("geom");
        assertNotNull(attributs);
        assertEquals(1, attributs.size());
        ite = attributs.iterator();
        assertEquals(pt, ite.next().getValue());
        assertFalse(ite.hasNext());

        attributs = feature.getProperties("{http://test.com}geom");
        assertNotNull(attributs);
        assertEquals(1, attributs.size());
        ite = attributs.iterator();
        assertEquals(pt, ite.next().getValue());
        assertFalse(ite.hasNext());

        attributs = feature.getProperties("http://test.com:geom");
        assertNotNull(attributs);
        assertEquals(1, attributs.size());
        ite = attributs.iterator();
        assertEquals(pt, ite.next().getValue());
        assertFalse(ite.hasNext());

        attributs = feature.getProperties(new DefaultName("http://test.com", "geom"));
        assertNotNull(attributs);
        assertEquals(1, attributs.size());
        ite = attributs.iterator();
        assertEquals(pt, ite.next().getValue());
        assertFalse(ite.hasNext());

        attribut = feature.getProperty("geom");
        assertNotNull(attribut);
        assertEquals(pt, attribut.getValue());

        attribut = feature.getProperty("{http://test.com}geom");
        assertNotNull(attribut);
        assertEquals(pt, attribut.getValue());

        attribut = feature.getProperty("http://test.com:geom");
        assertNotNull(attribut);
        assertEquals(pt, attribut.getValue());

        attribut = feature.getProperty(new DefaultName("http://test.com", "geom"));
        assertNotNull(attribut);
        assertEquals(pt, attribut.getValue());

        attributValue = feature.getAttribute(0);
        assertEquals(pt, attributValue);
        attributValue = feature.getAttributes().get(0);
        assertEquals(pt, attributValue);
        attributValue = feature.getAttribute("geom");
        assertEquals(pt, attributValue);
        attributValue = feature.getAttribute("{http://test.com}geom");
        assertEquals(pt, attributValue);
        attributValue = feature.getAttribute("http://test.com:geom");
        assertEquals(pt, attributValue);
        attributValue = feature.getAttribute(new DefaultName("http://test.com", "geom"));
        assertEquals(pt, attributValue);


        ////////////////////////////////////////////////////////////////////////
        //test accessing a multi value property ////////////////////////////////

        attributs = feature.getProperties("att_String");
        assertNotNull(attributs);
        assertEquals(1, attributs.size());
        ite = attributs.iterator();
        assertEquals("line1", ite.next().getValue());
        assertFalse(ite.hasNext());

        attributs = feature.getProperties("{http://test.com}att_String");
        assertNotNull(attributs);
        assertEquals(1, attributs.size());
        ite = attributs.iterator();
        assertEquals("line1", ite.next().getValue());
        assertFalse(ite.hasNext());

        attributs = feature.getProperties("http://test.com:att_String");
        assertNotNull(attributs);
        assertEquals(1, attributs.size());
        ite = attributs.iterator();
        assertEquals("line1", ite.next().getValue());
        assertFalse(ite.hasNext());

        attributs = feature.getProperties(new DefaultName("http://test.com", "att_String"));
        assertNotNull(attributs);
        assertEquals(1, attributs.size());
        ite = attributs.iterator();
        assertEquals("line1", ite.next().getValue());
        assertFalse(ite.hasNext());

        attribut = feature.getProperty("att_String");
        assertNotNull(attribut);
        assertEquals("line1", attribut.getValue());

        attribut = feature.getProperty("{http://test.com}att_String");
        assertNotNull(attribut);
        assertEquals("line1", attribut.getValue());

        attribut = feature.getProperty("http://test.com:att_String");
        assertNotNull(attribut);
        assertEquals("line1", attribut.getValue());

        attribut = feature.getProperty(new DefaultName("http://test.com", "att_String"));
        assertNotNull(attribut);
        assertEquals("line1", attribut.getValue());

        attributValue = feature.getAttribute(1);
        assertEquals("line1", attributValue);
        attributValue = feature.getAttributes().get(1);
        assertEquals("line1", attributValue);
        attributValue = feature.getAttribute("att_String");
        assertEquals("line1", attributValue);
        attributValue = feature.getAttribute("{http://test.com}att_String");
        assertEquals("line1", attributValue);
        attributValue = feature.getAttribute("http://test.com:att_String");
        assertEquals("line1", attributValue);
        attributValue = feature.getAttribute(new DefaultName("http://test.com", "att_String"));
        assertEquals("line1", attributValue);

    }

    @Test
    public void testComplexFeatureAccess(){
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("{http://test.com}test");
        ftb.add("{http://test.com}geom", Point.class, DefaultGeographicCRS.WGS84);
        ftb.add("{http://test.com}att_String", String.class, 0,Integer.MAX_VALUE,false,null);
        ftb.setDefaultGeometry("geom");
        final FeatureType type = ftb.buildFeatureType();

        final Point pt = GF.createPoint(new Coordinate(50, 60));

        final Collection<Property> properties = new ArrayList<Property>();
        properties.add(FF.createAttribute(pt, type.getGeometryDescriptor(), null));
        properties.add(FF.createAttribute("line1", (AttributeDescriptor) type.getDescriptor("att_String"), null));
        properties.add(FF.createAttribute("line2", (AttributeDescriptor) type.getDescriptor("att_String"), null));
        properties.add(FF.createAttribute("line3", (AttributeDescriptor) type.getDescriptor("att_String"), null));
        final Feature feature = FF.createFeature(properties, type, "one-ID");

        Collection<Property> attributs = feature.getProperties();
        Iterator<Property> ite;
        assertEquals(4, attributs.size());
        Property attribut;

        ////////////////////////////////////////////////////////////////////////
        //test accessing a single value property ///////////////////////////////

        attributs = feature.getProperties("geom");
        assertNotNull(attributs);
        assertEquals(1, attributs.size());
        ite = attributs.iterator();
        assertEquals(pt, ite.next().getValue());
        assertFalse(ite.hasNext());

        attributs = feature.getProperties("{http://test.com}geom");
        assertNotNull(attributs);
        assertEquals(1, attributs.size());
        ite = attributs.iterator();
        assertEquals(pt, ite.next().getValue());
        assertFalse(ite.hasNext());

        attributs = feature.getProperties("http://test.com:geom");
        assertNotNull(attributs);
        assertEquals(1, attributs.size());
        ite = attributs.iterator();
        assertEquals(pt, ite.next().getValue());
        assertFalse(ite.hasNext());

        attributs = feature.getProperties(new DefaultName("http://test.com", "geom"));
        assertNotNull(attributs);
        assertEquals(1, attributs.size());
        ite = attributs.iterator();
        assertEquals(pt, ite.next().getValue());
        assertFalse(ite.hasNext());

        attribut = feature.getProperty("geom");
        assertNotNull(attribut);
        assertEquals(pt, attribut.getValue());

        attribut = feature.getProperty("{http://test.com}geom");
        assertNotNull(attribut);
        assertEquals(pt, attribut.getValue());

        attribut = feature.getProperty("http://test.com:geom");
        assertNotNull(attribut);
        assertEquals(pt, attribut.getValue());

        attribut = feature.getProperty(new DefaultName("http://test.com", "geom"));
        assertNotNull(attribut);
        assertEquals(pt, attribut.getValue());

        ////////////////////////////////////////////////////////////////////////
        //test accessing a multi value property ////////////////////////////////

        attributs = feature.getProperties("att_String");
        assertNotNull(attributs);
        assertEquals(3, attributs.size());
        ite = attributs.iterator();
        assertEquals("line1", ite.next().getValue());
        assertEquals("line2", ite.next().getValue());
        assertEquals("line3", ite.next().getValue());
        assertFalse(ite.hasNext());

        attributs = feature.getProperties("{http://test.com}att_String");
        assertNotNull(attributs);
        assertEquals(3, attributs.size());
        ite = attributs.iterator();
        assertEquals("line1", ite.next().getValue());
        assertEquals("line2", ite.next().getValue());
        assertEquals("line3", ite.next().getValue());
        assertFalse(ite.hasNext());

        attributs = feature.getProperties("http://test.com:att_String");
        assertNotNull(attributs);
        assertEquals(3, attributs.size());
        ite = attributs.iterator();
        assertEquals("line1", ite.next().getValue());
        assertEquals("line2", ite.next().getValue());
        assertEquals("line3", ite.next().getValue());
        assertFalse(ite.hasNext());

        attributs = feature.getProperties(new DefaultName("http://test.com", "att_String"));
        assertNotNull(attributs);
        assertEquals(3, attributs.size());
        ite = attributs.iterator();
        assertEquals("line1", ite.next().getValue());
        assertEquals("line2", ite.next().getValue());
        assertEquals("line3", ite.next().getValue());
        assertFalse(ite.hasNext());

        attribut = feature.getProperty("att_String");
        assertNotNull(attribut);
        assertEquals("line1", attribut.getValue());

        attribut = feature.getProperty("{http://test.com}att_String");
        assertNotNull(attribut);
        assertEquals("line1", attribut.getValue());

        attribut = feature.getProperty("http://test.com:att_String");
        assertNotNull(attribut);
        assertEquals("line1", attribut.getValue());

        attribut = feature.getProperty(new DefaultName("http://test.com", "att_String"));
        assertNotNull(attribut);
        assertEquals("line1", attribut.getValue());

    }

}
