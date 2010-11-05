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
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;

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

/**
 * Test calculated multi line string attribute.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class CalculatedMultiLineStringAttributeTest {

    private static final double DELTA = 0.00000001d;
    private final FeatureFactory FF = FactoryFinder.getFeatureFactory(null);
    private final GeometryFactory GF = new GeometryFactory();

    public CalculatedMultiLineStringAttributeTest() {
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
        ftb.add("lines", LineString.class, 0,Integer.MAX_VALUE,false,null);
        final FeatureType sft = ftb.buildFeatureType();

        final LineString line1 = GF.createLineString(new Coordinate[]{new Coordinate(5, 5), new Coordinate(30, 12)});
        final LineString line2 = GF.createLineString(new Coordinate[]{new Coordinate(12, 31), new Coordinate(11, 21)});
        final LineString line3 = GF.createLineString(new Coordinate[]{new Coordinate(43, 56), new Coordinate(38, 89)});


        final Collection<Property> props = new ArrayList<Property>();
        props.add(FF.createAttribute(line1, (AttributeDescriptor) sft.getDescriptor("lines"), null));
        props.add(FF.createAttribute(line2, (AttributeDescriptor) sft.getDescriptor("lines"), null));
        props.add(FF.createAttribute(line3, (AttributeDescriptor) sft.getDescriptor("lines"), null));
        final Feature feature = FF.createFeature(props, sft, "id");

        final AttributeDescriptorBuilder adb = new AttributeDescriptorBuilder();
        final GeometryDescriptor desc = (GeometryDescriptor) adb.create(new DefaultName("calc"), MultiLineString.class, 1, 1, false, null);

        final CalculatedMultiLineStringAttribute att = new CalculatedMultiLineStringAttribute(desc, new DefaultName("lines"));

        //test related correctly set
        assertNull(att.getRelated());
        att.setRelated(feature);
        assertNotNull(att.getRelated());

        Object val = att.getValue();
        assertTrue(val instanceof MultiLineString);
        MultiLineString mline = (MultiLineString) val;
        assertEquals(3, mline.getNumGeometries());
        assertTrue(line1.equalsExact(mline.getGeometryN(0)));
        assertTrue(line2.equalsExact(mline.getGeometryN(1)));
        assertTrue(line3.equalsExact(mline.getGeometryN(2)));
    }

    @Test
    public void testDepthOneFeature() {
        final AttributeDescriptorBuilder adb = new AttributeDescriptorBuilder();
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();

        ftb.setName("sub");
        ftb.add("line", LineString.class, 1,1,false,null);
        final ComplexType ct = ftb.buildType();

        final LineString line1 = GF.createLineString(new Coordinate[]{new Coordinate(5, 5), new Coordinate(30, 12)});
        final LineString line2 = GF.createLineString(new Coordinate[]{new Coordinate(12, 31), new Coordinate(11, 21)});
        final LineString line3 = GF.createLineString(new Coordinate[]{new Coordinate(43, 56), new Coordinate(38, 89)});


        ftb.reset();
        ftb.setName("test");
        ftb.add(ct, new DefaultName("subAtts"), null, 0, Integer.MAX_VALUE, true, null);
        final FeatureType ft = ftb.buildFeatureType();
        final AttributeDescriptor sebDesc = (AttributeDescriptor) ft.getDescriptor("subAtts");

        final Collection<Property> props = new ArrayList<Property>();
        props.add(FF.createAttribute(line1, (AttributeDescriptor) ct.getDescriptor("line"), null));
        final ComplexAttribute ca1 = FF.createComplexAttribute(props, sebDesc, "sid1");

        props.clear();
        props.add(FF.createAttribute(line2, (AttributeDescriptor) ct.getDescriptor("line"), null));
        final ComplexAttribute ca2 = FF.createComplexAttribute(props, sebDesc, "sid2");

        props.clear();
        props.add(FF.createAttribute(line3, (AttributeDescriptor) ct.getDescriptor("line"), null));
        final ComplexAttribute ca3 = FF.createComplexAttribute(props, sebDesc, "sid3");

        props.clear();
        props.add(ca1);
        props.add(ca2);
        props.add(ca3);
        final Feature feature = FF.createFeature(props, ft, "id");


        final GeometryDescriptor desc = (GeometryDescriptor) adb.create(
                new DefaultName("calc"), MultiLineString.class, 1, 1, false, null);

        final CalculatedMultiLineStringAttribute att = new CalculatedMultiLineStringAttribute(desc,
                new DefaultName("subAtts"), new DefaultName("line"));

        //test related correctly set
        assertNull(att.getRelated());
        att.setRelated(feature);
        assertNotNull(att.getRelated());

        Object val = att.getValue();
        assertTrue(val instanceof MultiLineString);
        MultiLineString mline = (MultiLineString) val;
        assertEquals(3, mline.getNumGeometries());
        assertTrue(line1.equalsExact(mline.getGeometryN(0)));
        assertTrue(line2.equalsExact(mline.getGeometryN(1)));
        assertTrue(line3.equalsExact(mline.getGeometryN(2)));
    }

}