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
import com.vividsolutions.jts.geom.Point;
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
import org.opengis.feature.FeatureType;

/**
 * Test calculated linestring attribut.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class CalculatedLineStringOperationTest {

    private static final double DELTA = 0.00000001d;
    private final GeometryFactory GF = new GeometryFactory();

    public CalculatedLineStringOperationTest() {
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
        PropertyTypeBuilder ptb = ftb.addAttribute(Point.class).setName("points").setMinimumOccurs(0).setMaximumOccurs(Integer.MAX_VALUE);
        ftb.addProperty(new CalculateLineStringOperation(NamesExt.create(null, "calc"), ptb.getName()));
        final FeatureType sft = ftb.build();

        final Feature feature = sft.newInstance();
        feature.setPropertyValue("points", Arrays.asList(
                GF.createPoint(new Coordinate(5, 5)),
                GF.createPoint(new Coordinate(30, 12)),
                GF.createPoint(new Coordinate(41, 56))
                ));


        final Object val = feature.getPropertyValue("calc");
        assertTrue(val instanceof LineString);
        final LineString line = (LineString) val;
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
        FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("sub");
        final PropertyTypeBuilder pointProp = ftb.addAttribute(Point.class).setName("point");
        final FeatureType ct = ftb.build();

        ftb = new FeatureTypeBuilder();
        ftb.setName("test");
        final PropertyTypeBuilder subProp = ftb.addAssociation(ct).setName("subAtts").setMinimumOccurs(0).setMaximumOccurs(Integer.MAX_VALUE);
        ftb.addProperty(new CalculateLineStringOperation(NamesExt.create(null, "calc"), subProp.getName(), pointProp.getName()));
        final FeatureType ft = ftb.build();


        final Feature ca1 = ct.newInstance();
        ca1.setPropertyValue("point", GF.createPoint(new Coordinate(5, 5)));
        final Feature ca2 = ct.newInstance();
        ca2.setPropertyValue("point", GF.createPoint(new Coordinate(30, 12)));
        final Feature ca3 = ct.newInstance();
        ca3.setPropertyValue("point", GF.createPoint(new Coordinate(41, 56)));

        final Feature feature = ft.newInstance();
        feature.setPropertyValue("subAtts", Arrays.asList(ca1,ca2,ca3));


        Object val = feature.getPropertyValue("calc");
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
