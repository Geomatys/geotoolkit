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

package org.geotoolkit.feature.calculated;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.LineString;

import java.util.Collection;
import java.util.ArrayList;

import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.feature.AttributeDescriptorBuilder;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.feature.FeatureTypeBuilder;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import org.opengis.feature.ComplexAttribute;
import org.opengis.feature.Feature;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.FeatureFactory;
import org.opengis.feature.Property;
import org.opengis.feature.type.ComplexType;
import org.opengis.feature.type.GeometryDescriptor;

import static org.junit.Assert.*;
import org.opengis.feature.type.PropertyDescriptor;

/**
 * Test calculated linestring attribut.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class CalculatedLineStringAttributeTest {

    private static final double DELTA = 0.00000001d;
    private final FeatureFactory FF = FactoryFinder.getFeatureFactory(null);
    private final GeometryFactory GF = new GeometryFactory();

    public CalculatedLineStringAttributeTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Test
    public void testDepthZeroFeature() {
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("test");
        ftb.add("points", Point.class, 0,Integer.MAX_VALUE,false,null);
        final FeatureType sft = ftb.buildFeatureType();

        final Collection<Property> props = new ArrayList<Property>();
        props.add(FF.createAttribute(GF.createPoint(new Coordinate(5, 5)), (AttributeDescriptor) sft.getDescriptor("points"), null));
        props.add(FF.createAttribute(GF.createPoint(new Coordinate(30, 12)), (AttributeDescriptor) sft.getDescriptor("points"), null));
        props.add(FF.createAttribute(GF.createPoint(new Coordinate(41, 56)), (AttributeDescriptor) sft.getDescriptor("points"), null));
        final Feature feature = FF.createFeature(props, sft, "id");

        final AttributeDescriptorBuilder adb = new AttributeDescriptorBuilder();
        final GeometryDescriptor desc = (GeometryDescriptor) adb.create(new DefaultName("calc"), LineString.class, 1, 1, false, null);
        
        final CalculatedLineStringAttribute att = new CalculatedLineStringAttribute(desc, new DefaultName("points"));

        //test related correctly set
        assertNull(att.getRelated());
        att.setRelated(feature);
        assertNotNull(att.getRelated());

        Object val = att.getValue();
        assertTrue(val instanceof LineString);
        LineString line = (LineString) val;
        assertEquals(5, line.getCoordinateN(0).x, DELTA);
        assertEquals(5, line.getCoordinateN(0).y, DELTA);
        assertEquals(30, line.getCoordinateN(1).x, DELTA);
        assertEquals(12, line.getCoordinateN(1).y, DELTA);
        assertEquals(41, line.getCoordinateN(2).x, DELTA);
        assertEquals(56, line.getCoordinateN(2).y, DELTA);
        assertEquals(3, line.getNumPoints());
    }

    @Test
    public void testDepthOneFeature() {
        final AttributeDescriptorBuilder adb = new AttributeDescriptorBuilder();
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();

        ftb.setName("sub");
        ftb.add("point", Point.class, 1,1,false,null);
        final ComplexType ct = ftb.buildType();


        ftb.reset();
        ftb.setName("test");
        ftb.add(ct, new DefaultName("subAtts"), null, 0, Integer.MAX_VALUE, true, null);
        final FeatureType ft = ftb.buildFeatureType();
        final AttributeDescriptor attDesc = (AttributeDescriptor) ft.getDescriptor("subAtts");

        final Collection<Property> props = new ArrayList<Property>();
        props.add(FF.createAttribute(GF.createPoint(new Coordinate(5, 5)), (AttributeDescriptor) ct.getDescriptor("point"), null));
        final ComplexAttribute ca1 = FF.createComplexAttribute(props, attDesc, "sid1");

        props.clear();
        props.add(FF.createAttribute(GF.createPoint(new Coordinate(30, 12)), (AttributeDescriptor) ct.getDescriptor("point"), null));
        final ComplexAttribute ca2 = FF.createComplexAttribute(props, attDesc, "sid2");

        props.clear();
        props.add(FF.createAttribute(GF.createPoint(new Coordinate(41, 56)), (AttributeDescriptor) ct.getDescriptor("point"), null));
        final ComplexAttribute ca3 = FF.createComplexAttribute(props, attDesc, "sid3");

        props.clear();
        props.add(ca1);
        props.add(ca2);
        props.add(ca3);
        final Feature feature = FF.createFeature(props, ft, "id");


        final GeometryDescriptor desc = (GeometryDescriptor) adb.create(new DefaultName("calc"), LineString.class, 1, 1, false, null);

        final CalculatedLineStringAttribute att = new CalculatedLineStringAttribute(desc,
                new DefaultName("subAtts"), new DefaultName("point"));

        //test related correctly set
        assertNull(att.getRelated());
        att.setRelated(feature);
        assertNotNull(att.getRelated());

        Object val = att.getValue();
        assertTrue(val instanceof LineString);
        LineString line = (LineString) val;
        assertEquals(5, line.getCoordinateN(0).x, DELTA);
        assertEquals(5, line.getCoordinateN(0).y, DELTA);
        assertEquals(30, line.getCoordinateN(1).x, DELTA);
        assertEquals(12, line.getCoordinateN(1).y, DELTA);
        assertEquals(41, line.getCoordinateN(2).x, DELTA);
        assertEquals(56, line.getCoordinateN(2).y, DELTA);
        assertEquals(3, line.getNumPoints());
    }

}
