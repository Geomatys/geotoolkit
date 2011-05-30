/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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
package org.geotoolkit.process.vector.differencegeometry;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.geotoolkit.data.DataUtilities;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.feature.simple.SimpleFeatureBuilder;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessFinder;
import org.geotoolkit.process.vector.AbstractProcessTest;
import org.geotoolkit.referencing.CRS;

import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.util.FactoryException;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * JUnit test of clip with a geometry process
 * @author Quentin Boileau
 * @module pending
 */
public class DifferenceGeometryTest extends AbstractProcessTest{

    private static SimpleFeatureBuilder sfb;
    private static GeometryFactory geometryFactory;
    private static SimpleFeatureType type;

    public DifferenceGeometryTest() {
        super("diffGeometry");
    }


    @Test
    public void testDiffGeometry() {

        // Inputs
        final FeatureCollection<?> featureList = buildFeatureList();
        final Geometry geometryClip = buildGeometryClip();
        // Process
        ProcessDescriptor desc = ProcessFinder.getProcessDescriptor("vector", "diffGeometry");
        org.geotoolkit.process.Process proc = desc.createProcess();

        ParameterValueGroup in = desc.getInputDescriptor().createValue();
        in.parameter(DifferenceGeometryDescriptor.FEATURE_IN.getName().getCode()).setValue(featureList);
        in.parameter(DifferenceGeometryDescriptor.DIFF_GEOMETRY_IN.getName().getCode()).setValue(geometryClip);

        proc.setInput(in);

        proc.run();

        //Features out
        final FeatureCollection<?> featureListOut = (FeatureCollection<?>) proc.getOutput().parameter("feature_out").getValue();


        //Expected Features out
        final FeatureCollection<?> featureListResult = buildResultList();

        assertEquals(featureListOut.getFeatureType(), featureListResult.getFeatureType());
        assertEquals(featureListOut.getID(), featureListResult.getID());
        assertEquals(featureListOut.size(), featureListResult.size());
        assertTrue(featureListOut.containsAll(featureListResult));


    }

    private static SimpleFeatureType createSimpleType() throws NoSuchAuthorityCodeException, FactoryException {
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("Building");
        ftb.add("name", String.class);
        ftb.add("position", Polygon.class, CRS.decode("EPSG:3395"));
        ftb.add("height", Integer.class);

        ftb.setDefaultGeometry("position");
        final SimpleFeatureType sft = ftb.buildSimpleFeatureType();
        return sft;
    }

    private static SimpleFeatureType createSimpleResultType() throws NoSuchAuthorityCodeException, FactoryException {
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("Building");
        ftb.add("name", String.class);
        ftb.add("position", Geometry.class, CRS.decode("EPSG:3395"));
        ftb.add("height", Integer.class);

        ftb.setDefaultGeometry("position");
        final SimpleFeatureType sft = ftb.buildSimpleFeatureType();
        return sft;
    }

    private static FeatureCollection<?> buildFeatureList() {

        try {
            type = createSimpleType();
        } catch (NoSuchAuthorityCodeException ex) {
            Logger.getLogger(DifferenceGeometryTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FactoryException ex) {
            Logger.getLogger(DifferenceGeometryTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        final FeatureCollection<Feature> featureList = DataUtilities.collection("", type);

        geometryFactory = new GeometryFactory();

        Feature myFeature1;
        LinearRing ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(6.0, 3.0),
                    new Coordinate(7.0, 3.0),
                    new Coordinate(7.0, 4.0),
                    new Coordinate(6.0, 4.0),
                    new Coordinate(6.0, 3.0)
                });
        sfb = new SimpleFeatureBuilder(type);
        sfb.set("name", "Building1");
        sfb.set("height", 12);
        sfb.set("position", geometryFactory.createPolygon(ring, null));
        myFeature1 = sfb.buildFeature("id-01");
        featureList.add(myFeature1);

        Feature myFeature2;
        ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(8.0, 4.0),
                    new Coordinate(11.0, 4.0),
                    new Coordinate(11.0, 7.0),
                    new Coordinate(8.0, 7.0),
                    new Coordinate(8.0, 4.0)
                });
        sfb = new SimpleFeatureBuilder(type);
        sfb.set("name", "Building2");
        sfb.set("height", 12);
        sfb.set("position", geometryFactory.createPolygon(ring, null));
        myFeature2 = sfb.buildFeature("id-02");
        featureList.add(myFeature2);

        Feature myFeature3;
        ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(6.0, -2.0),
                    new Coordinate(13.0, -2.0),
                    new Coordinate(13.0, 1.0),
                    new Coordinate(6.0, 1.0),
                    new Coordinate(6.0, -2.0)
                });
        sfb = new SimpleFeatureBuilder(type);
        sfb.set("name", "Building3");
        sfb.set("height", 12);
        sfb.set("position", geometryFactory.createPolygon(ring, null));
        myFeature3 = sfb.buildFeature("id-03");
        featureList.add(myFeature3);

        Feature myFeature4;
        ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(0.0, 6.0),
                    new Coordinate(3.0, 6.0),
                    new Coordinate(3.0, 9.0),
                    new Coordinate(0.0, 9.0),
                    new Coordinate(0.0, 6.0)
                });
        sfb = new SimpleFeatureBuilder(type);
        sfb.set("name", "Building4");
        sfb.set("height", 12);
        sfb.set("position", geometryFactory.createPolygon(ring, null));
        myFeature4 = sfb.buildFeature("id-04");
        featureList.add(myFeature4);

        Feature myFeature5;
        ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(-4.0, 1.0),
                    new Coordinate(-1.0, 1.0),
                    new Coordinate(-1.0, 3.0),
                    new Coordinate(-4.0, 3.0),
                    new Coordinate(-4.0, 1.0)
                });
        sfb = new SimpleFeatureBuilder(type);
        sfb.set("name", "Building5");
        sfb.set("height", 12);
        sfb.set("position", geometryFactory.createPolygon(ring, null));
        myFeature5 = sfb.buildFeature("id-05");
        featureList.add(myFeature5);


        return featureList;
    }

    private static Geometry buildGeometryClip() {
        Geometry clip;

        LinearRing ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(3.0, 1.0),
                    new Coordinate(10.0, 1.0),
                    new Coordinate(10.0, 6.0),
                    new Coordinate(3.0, 6.0),
                    new Coordinate(3.0, 1.0)
                });
        clip = geometryFactory.createPolygon(ring, null);
        return clip;
    }

    private static FeatureCollection<?> buildResultList() {

        try {
            type = createSimpleResultType();
        } catch (NoSuchAuthorityCodeException ex) {
            Logger.getLogger(DifferenceGeometryTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FactoryException ex) {
            Logger.getLogger(DifferenceGeometryTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        final FeatureCollection<Feature> featureList = DataUtilities.collection("", type);

        geometryFactory = new GeometryFactory();

        Feature myFeature2;
        LinearRing ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(8.0, 6.0),
                    new Coordinate(8.0, 7.0),
                    new Coordinate(11.0, 7.0),
                    new Coordinate(11.0, 4.0),
                    new Coordinate(10.0, 4.0),
                    new Coordinate(10.0, 6.0),
                    new Coordinate(8.0, 6.0)
                });
        sfb = new SimpleFeatureBuilder(type);
        sfb.set("name", "Building2");
        sfb.set("height", 12);
        sfb.set("position", geometryFactory.createPolygon(ring, null));
        myFeature2 = sfb.buildFeature("id-02");
        featureList.add(myFeature2);

        Feature myFeature3;
        ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(10.0, 1.0),
                    new Coordinate(13.0, 1.0),
                    new Coordinate(13.0, -2.0),
                    new Coordinate(6.0, -2.0),
                    new Coordinate(6.0, 1.0),
                    new Coordinate(10.0, 1.0)
                });
        sfb = new SimpleFeatureBuilder(type);
        sfb.set("name", "Building3");
        sfb.set("height", 12);
        sfb.set("position", geometryFactory.createPolygon(ring, null));
        myFeature3 = sfb.buildFeature("id-03");
        featureList.add(myFeature3);

        Feature myFeature4;
        ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(3.0, 6.0),
                    new Coordinate(0.0, 6.0),
                    new Coordinate(0.0, 9.0),
                    new Coordinate(3.0, 9.0),
                    new Coordinate(3.0, 6.0)
                });
        sfb = new SimpleFeatureBuilder(type);
        sfb.set("name", "Building4");
        sfb.set("height", 12);
        sfb.set("position", geometryFactory.createPolygon(ring, null));
        myFeature4 = sfb.buildFeature("id-04");
        featureList.add(myFeature4);

        Feature myFeature5;
        ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(-4.0, 1.0),
                    new Coordinate(-1.0, 1.0),
                    new Coordinate(-1.0, 3.0),
                    new Coordinate(-4.0, 3.0),
                    new Coordinate(-4.0, 1.0)
                });
        sfb = new SimpleFeatureBuilder(type);
        sfb.set("name", "Building5");
        sfb.set("height", 12);
        sfb.set("position", geometryFactory.createPolygon(ring, null));
        myFeature5 = sfb.buildFeature("id-05");
        featureList.add(myFeature5);

        return featureList;
    }
}
