/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2015, Geomatys
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

package org.apache.sis.feature.op;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import java.util.Arrays;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.feature.builder.PropertyTypeBuilder;
import org.geotoolkit.util.NamesExt;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureAssociationRole;
import org.opengis.feature.FeatureType;
import org.opengis.feature.PropertyType;

/**
 * Test calculated multi line string attribute.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class CalculatedMultiLineStringOperationTest {

    private static final double DELTA = 0.00000001d;
    private final GeometryFactory GF = new GeometryFactory();

    public CalculatedMultiLineStringOperationTest() {
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
        final PropertyTypeBuilder geomProp = ftb.addAttribute(LineString.class).setName("lines").setMinimumOccurs(0).setMaximumOccurs(Integer.MAX_VALUE);
        ftb.addProperty(new CalculateMultiLineStringOperation(NamesExt.create(null, "calc"), geomProp.getName()));
        final FeatureType sft = ftb.build();

        final LineString line1 = GF.createLineString(new Coordinate[]{new Coordinate(5, 5), new Coordinate(30, 12)});
        final LineString line2 = GF.createLineString(new Coordinate[]{new Coordinate(12, 31), new Coordinate(11, 21)});
        final LineString line3 = GF.createLineString(new Coordinate[]{new Coordinate(43, 56), new Coordinate(38, 89)});

        final Feature feature = sft.newInstance();
        feature.setPropertyValue("lines", Arrays.asList(line1,line2,line3));

        final Object val = feature.getPropertyValue("calc");
        assertTrue(val instanceof MultiLineString);
        MultiLineString mline = (MultiLineString) val;
        assertEquals(3, mline.getNumGeometries());
        assertTrue(line1.equalsExact(mline.getGeometryN(0)));
        assertTrue(line2.equalsExact(mline.getGeometryN(1)));
        assertTrue(line3.equalsExact(mline.getGeometryN(2)));
    }

    @Test
    public void testDepthOneFeature() {
        FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("sub");
        PropertyTypeBuilder geomProp = ftb.addAttribute(LineString.class).setName("line");
        final FeatureType ct = ftb.build();

        final LineString line1 = GF.createLineString(new Coordinate[]{new Coordinate(5, 5), new Coordinate(30, 12)});
        final LineString line2 = GF.createLineString(new Coordinate[]{new Coordinate(12, 31), new Coordinate(11, 21)});
        final LineString line3 = GF.createLineString(new Coordinate[]{new Coordinate(43, 56), new Coordinate(38, 89)});


        ftb = new FeatureTypeBuilder();
        ftb.setName("test");
        PropertyTypeBuilder subProp = ftb.addAssociation(ct).setName("subAtts").setMinimumOccurs(0).setMaximumOccurs(Integer.MAX_VALUE);
        ftb.addProperty(new CalculateMultiLineStringOperation(NamesExt.create(null, "calc"),
                subProp.getName(), geomProp.getName()));
        final FeatureType ft = ftb.build();

        final Feature ca1 = ct.newInstance();
        ca1.setPropertyValue("line", line1);
        final Feature ca2 = ct.newInstance();
        ca2.setPropertyValue("line", line2);
        final Feature ca3 = ct.newInstance();
        ca3.setPropertyValue("line", line3);

        final Feature feature = ft.newInstance();
        feature.setPropertyValue("subAtts", Arrays.asList(ca1,ca2,ca3));

        Object val = feature.getPropertyValue("calc");
        assertTrue(val instanceof MultiLineString);
        MultiLineString mline = (MultiLineString) val;
        assertEquals(3, mline.getNumGeometries());
        assertTrue(line1.equalsExact(mline.getGeometryN(0)));
        assertTrue(line2.equalsExact(mline.getGeometryN(1)));
        assertTrue(line3.equalsExact(mline.getGeometryN(2)));
    }

}
