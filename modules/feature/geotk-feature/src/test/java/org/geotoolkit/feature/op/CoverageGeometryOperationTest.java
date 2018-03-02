/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2018, Geomatys
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

package org.geotoolkit.feature.op;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Polygon;
import java.awt.image.BufferedImage;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.referencing.CommonCRS;
import org.geotoolkit.coverage.grid.GridCoverageBuilder;
import org.geotoolkit.util.NamesExt;
import org.junit.AfterClass;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.opengis.coverage.Coverage;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;

/**
 * Test calculated coverage geometry.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class CoverageGeometryOperationTest {

    public CoverageGeometryOperationTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Test
    public void testGetValue() {

        //create type
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("test");
        ftb.addAttribute(Coverage.class).setName("coverage");
        ftb.addProperty(new CoverageGeometryOperation(NamesExt.create(null, "contour"), "coverage"));
        final FeatureType type = ftb.build();

        //create coverage
        final GeneralEnvelope env = new GeneralEnvelope(CommonCRS.WGS84.normalizedGeographic());
        env.setRange(0, 10, 20);
        env.setRange(1, 20, 30);
        final GridCoverageBuilder gcb = new GridCoverageBuilder();
        gcb.setEnvelope(env);
        gcb.setRenderedImage(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB));
        final GridCoverage coverage = gcb.build();

        //create feature
        final Feature sf = type.newInstance();
        sf.setPropertyValue("coverage", coverage);

        //test operation
        final Object value = sf.getPropertyValue("contour");
        Assert.assertTrue(value instanceof Polygon);
        final Polygon polygon = (Polygon) value;
        assertEquals(CommonCRS.WGS84.normalizedGeographic(), polygon.getUserData());
        final Coordinate[] coordinates = polygon.getCoordinates();
        assertEquals(5, coordinates.length);
        assertEquals(new Coordinate(10, 20), coordinates[0]);
        assertEquals(new Coordinate(10, 30), coordinates[1]);
        assertEquals(new Coordinate(20, 30), coordinates[2]);
        assertEquals(new Coordinate(20, 20), coordinates[3]);
        assertEquals(new Coordinate(10, 20), coordinates[4]);

    }

}
