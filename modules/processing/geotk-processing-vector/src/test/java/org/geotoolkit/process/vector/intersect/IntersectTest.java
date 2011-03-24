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
package org.geotoolkit.process.vector.intersect;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiPoint;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.geotoolkit.data.DataUtilities;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.feature.simple.SimpleFeatureBuilder;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessFinder;
import org.geotoolkit.referencing.CRS;

import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.util.FactoryException;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Junit test of intersect process
 * @author Quentin Boileau
 * @module pending
 */
public class IntersectTest {

    private static SimpleFeatureBuilder sfb;
    private static final GeometryFactory geometryFactory = new GeometryFactory();
    private static SimpleFeatureType type;

    @Test
    public void testIntersect() {

        // Inputs
        final FeatureCollection<?> featureList = buildFeatureList();
        final Geometry geom = buildIntersectionGeometry();

        // Process
        ProcessDescriptor desc = ProcessFinder.getProcessDescriptor("vector", "intersect");
        org.geotoolkit.process.Process proc = desc.createProcess();

        ParameterValueGroup in = desc.getInputDescriptor().createValue();
        in.parameter("feature_in").setValue(featureList);
        in.parameter("geometry_in").setValue(geom);

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
        ftb.setName("IntersectTest");
        ftb.add("name", String.class);
        ftb.add("geom1", Geometry.class, CRS.decode("EPSG:3395"));
        ftb.add("geom2", Geometry.class, CRS.decode("EPSG:3395"));

        ftb.setDefaultGeometry("geom1");
        final SimpleFeatureType sft = ftb.buildSimpleFeatureType();
        return sft;
    }

    private static FeatureCollection<?> buildFeatureList() {

        try {
            type = createSimpleType();
        } catch (NoSuchAuthorityCodeException ex) {
            Logger.getLogger(IntersectTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FactoryException ex) {
            Logger.getLogger(IntersectTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        final FeatureCollection<Feature> featureList = DataUtilities.collection("", type);


        Feature myFeature1;
        LinearRing ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(3.0, 3.0),
                    new Coordinate(3.0, 4.0),
                    new Coordinate(4.0, 4.0),
                    new Coordinate(4.0, 3.0),
                    new Coordinate(3.0, 3.0)
                });
        sfb = new SimpleFeatureBuilder(type);
        sfb.set("name", "feature1");
        sfb.set("geom1", geometryFactory.createPolygon(ring, null));
        sfb.set("geom2", geometryFactory.createPoint(new Coordinate(3.5, 3.5)));
        myFeature1 = sfb.buildFeature("id-01");
        featureList.add(myFeature1);

        Feature myFeature2;
        ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(5.0, 6.0),
                    new Coordinate(5.0, 7.0),
                    new Coordinate(6.0, 7.0),
                    new Coordinate(6.0, 6.0),
                    new Coordinate(5.0, 6.0)
                });
        MultiPoint multPt = geometryFactory.createMultiPoint(
                new Coordinate[]{
                    new Coordinate(5.0, 4.0),
                    new Coordinate(5.0, 10.0),
                    new Coordinate(3.0, 6.0),
                    new Coordinate(4.0, 7.0),
                    new Coordinate(5.5, 6.5)
                });
        sfb = new SimpleFeatureBuilder(type);
        sfb.set("name", "feature2");
        sfb.set("geom1", geometryFactory.createPolygon(ring, null));
        sfb.set("geom2", multPt);
        myFeature2 = sfb.buildFeature("id-02");
        featureList.add(myFeature2);

        Feature myFeature3;
        ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(9.0, 4.0),
                    new Coordinate(9.0, 5.0),
                    new Coordinate(10.0, 5.0),
                    new Coordinate(10.0, 4.0),
                    new Coordinate(9.0, 4.0)
                });
        LineString line = geometryFactory.createLineString(
                new Coordinate[]{
                    new Coordinate(7.0, 0.0),
                    new Coordinate(9.0, 3.0)
                });
        sfb = new SimpleFeatureBuilder(type);
        sfb.set("name", "feature3");
        sfb.set("geom1", geometryFactory.createPolygon(ring, null));
        sfb.set("geom2", line);
        myFeature3 = sfb.buildFeature("id-03");
        featureList.add(myFeature3);

        return featureList;
    }

    private static FeatureCollection<?> buildResultList() {


        try {
            type = createSimpleType();
        } catch (NoSuchAuthorityCodeException ex) {
            Logger.getLogger(IntersectTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FactoryException ex) {
            Logger.getLogger(IntersectTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        final FeatureCollection<Feature> featureList = DataUtilities.collection("", type);


        Feature myFeature1;
        LinearRing ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(3.0, 3.0),
                    new Coordinate(3.0, 4.0),
                    new Coordinate(4.0, 4.0),
                    new Coordinate(4.0, 3.0),
                    new Coordinate(3.0, 3.0)
                });
        sfb = new SimpleFeatureBuilder(type);
        sfb.set("name", "feature1");
        sfb.set("geom1", geometryFactory.createPolygon(ring, null));
        sfb.set("geom2", geometryFactory.createPoint(new Coordinate(3.5, 3.5)));
        myFeature1 = sfb.buildFeature("id-01");
        featureList.add(myFeature1);

        Feature myFeature2;
        ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(5.0, 6.0),
                    new Coordinate(5.0, 7.0),
                    new Coordinate(6.0, 7.0),
                    new Coordinate(6.0, 6.0),
                    new Coordinate(5.0, 6.0)
                });
        MultiPoint multPt = geometryFactory.createMultiPoint(
                new Coordinate[]{
                    new Coordinate(5.0, 4.0),
                    new Coordinate(5.0, 10.0),
                    new Coordinate(3.0, 6.0),
                    new Coordinate(4.0, 7.0),
                    new Coordinate(5.5, 6.5)
                });
        sfb = new SimpleFeatureBuilder(type);
        sfb.set("name", "feature2");
        sfb.set("geom1", geometryFactory.createPolygon(ring, null));
        sfb.set("geom2", multPt);
        myFeature2 = sfb.buildFeature("id-02");
        featureList.add(myFeature2);


        return featureList;
    }

    private Geometry buildIntersectionGeometry() {

        LinearRing ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(2.0, 2.0),
                    new Coordinate(2.0, 5.0),
                    new Coordinate(7.0, 5.0),
                    new Coordinate(7.0, 2.0),
                    new Coordinate(2.0, 2.0)
                });

        return geometryFactory.createPolygon(ring, null);
    }
}
