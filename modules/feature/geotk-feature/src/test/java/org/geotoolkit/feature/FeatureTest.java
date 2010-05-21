/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
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
 *
 * @author Johann Sorel (Geomatys)
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
    public void testComplexFeature(){
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

    }

}