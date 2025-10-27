/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2023, Geomatys
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
package org.geotoolkit.storage.coverage;

import java.awt.Rectangle;
import java.util.Arrays;
import java.util.function.BiFunction;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.coverage.grid.GridOrientation;
import org.apache.sis.feature.builder.AttributeRole;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.storage.MemoryFeatureSet;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.storage.FeatureSet;
import org.junit.Assert;
import org.junit.Test;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.impl.PackedCoordinateSequence;
import org.opengis.feature.Feature;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FeatureSetToCoverageTileGeneratorTest {

    private static final CoordinateReferenceSystem CRS84 = CommonCRS.WGS84.normalizedGeographic();
    private static final GridGeometry WORLD_GRIDGEOMETRY = new GridGeometry(new GridExtent(360, 180), CRS.getDomainOfValidity(CRS84), GridOrientation.REFLECTION_Y);
    private static final FeatureSet FEATURE_SET;
    static {
        //create FeatureSet
        final var ftb = new FeatureTypeBuilder();
        ftb.setName("test");
        ftb.addAttribute(Polygon.class).setName("geom").addRole(AttributeRole.DEFAULT_GEOMETRY);
        ftb.addAttribute(Double.class).setName("physics");
        final var featureType = ftb.build();
        final var geometryFactory = new GeometryFactory();
        final var feature1 = featureType.newInstance();
        final var feature2 = featureType.newInstance();
        final var polygon1 = geometryFactory.createPolygon(new PackedCoordinateSequence.Double(new double[]{0,0, 10,0, 10,10, 0,10, 0,0}, 2, 0));
        final var polygon2 = geometryFactory.createPolygon(new PackedCoordinateSequence.Double(new double[]{10,0, 20,0, 20,10, 10,10, 10,0}, 2, 0));
        polygon1.setUserData(CRS84);
        polygon2.setUserData(CRS84);
        feature1.setPropertyValue("geom", polygon1);
        feature2.setPropertyValue("geom", polygon2);
        feature1.setPropertyValue("physics", 123.456);
        feature2.setPropertyValue("physics", 789.123);
        FEATURE_SET = new MemoryFeatureSet(null, featureType, Arrays.asList(feature1, feature2));
    }

    /**
     * Test bitmask creation
     */
    @Test
    public void testBitMask() throws Exception {

        //generate coverage
        final var generator = new FeatureSetToCoverageTileGenerator();
        generator.setFeatureSet(FEATURE_SET);
        generator.setAntialiasing(false);

        //check coverage values
        final var coverage = generator.generate(WORLD_GRIDGEOMETRY);
        final var image = coverage.render(null).getData();

        final Rectangle inPolygon = new Rectangle(180, 80, 20, 10);
        for (int y=0;y<180;y++) {
            for (int x=0;x<360;x++) {
                final double s = image.getSampleDouble(x, y, 0);
                if (inPolygon.contains(x, y)) {
                    Assert.assertEquals("at " + x + ","  + y, 1, s, 0.0);
                } else {
                    Assert.assertEquals("at " + x + ","  + y, 0, s, 0.0);
                }
            }
        }
    }

    /**
     * Test graymask creation
     */
    @Test
    public void testGrayMask() throws Exception {

        //generate coverage
        final var generator = new FeatureSetToCoverageTileGenerator();
        generator.setFeatureSet(FEATURE_SET);
        generator.setAntialiasing(true);

        //check coverage values
        final var coverage = generator.generate(WORLD_GRIDGEOMETRY);
        final var image = coverage.render(null).getData();

        final Rectangle inPolygon = new Rectangle(180, 80, 19, 9);
        final Rectangle inAliasedPolygon = new Rectangle(179, 79, 22, 12);
        for (int y=0;y<180;y++) {
            for (int x=0;x<360;x++) {
                final double s = image.getSampleDouble(x, y, 0);
                if (inPolygon.contains(x, y)) {
                    Assert.assertEquals("at " + x + ","  + y, 255, s, 0.0);
                } else if (inAliasedPolygon.contains(x, y)) {
                    //gray area
                    Assert.assertTrue("at " + x + ","  + y + " sample " + s, s >= 0 && s <= 255);
                }else {
                    Assert.assertEquals("at " + x + ","  + y, 0, s, 0.0);
                }
            }
        }
    }

    /**
     * Test separate features creation
     */
    @Test
    public void testTransformedMask() throws Exception {

        final var sd = new SampleDimension.Builder().setName("test").build();
        final var transform = new BiFunction<Feature, Integer, double[]>() {
            @Override
            public double[] apply(Feature t, Integer u) {
                if (t == null) return new double[]{-999.99};
                return new double[]{(Double)t.getPropertyValue("physics")};
            }
        };

        //generate coverage
        final var generator = new FeatureSetToCoverageTileGenerator();
        generator.setFeatureSet(FEATURE_SET);
        generator.setAntialiasing(false);
        generator.setSampleDimensions(Arrays.asList(sd) );
        generator.setFeatureToSamples(transform);

        //check coverage values
        final var coverage = generator.generate(WORLD_GRIDGEOMETRY);
        final var image = coverage.render(null).getData();

        //feature 2 overlaps (pixel rounding...)
        final Rectangle inFeature1 = new Rectangle(180, 80, 10, 10);
        final Rectangle inFeature2 = new Rectangle(190, 80, 10, 10);
        for (int y=0;y<180;y++) {
            for (int x=0;x<360;x++) {
                final double s = image.getSampleDouble(x, y, 0);
                if (inFeature1.contains(x, y)) {
                    //first feature
                    Assert.assertEquals("at " + x + ","  + y, 123.456, s, 0.0);
                } else if (inFeature2.contains(x, y)) {
                    // second feature
                    Assert.assertEquals("at " + x + ","  + y, 789.123, s, 0.0);
                }else {
                    //no feature value
                    Assert.assertEquals("at " + x + ","  + y, -999.99, s, 0.0);
                }
            }
        }
    }
}
