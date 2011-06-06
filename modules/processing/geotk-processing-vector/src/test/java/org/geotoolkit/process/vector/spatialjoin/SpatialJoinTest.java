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
package org.geotoolkit.process.vector.spatialjoin;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.geotoolkit.data.DataUtilities;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.feature.FeatureUtilities;
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
 * JUnit test of SpatialJoin process
 * @author Quentin Boileau
 * @module pending
 */
public class SpatialJoinTest extends AbstractProcessTest{

    private static SimpleFeatureBuilder sfb;
    private static final GeometryFactory geometryFactory = new GeometryFactory();
    private static SimpleFeatureType type;

    public SpatialJoinTest() {
        super("spatialjoin");
    }


    /**
     * Test SpatialJoin process with Nearest method
     */
    @Test
    public void testSpacialJoin() {

        // Inputs
        final FeatureCollection<?> targetFeatures = buildFeatureList1();
        final FeatureCollection<?> sourceFeatures = buildFeatureList2();

        // Process
        ProcessDescriptor desc = ProcessFinder.getProcessDescriptor("vector", "spatialjoin");
        org.geotoolkit.process.Process proc = desc.createProcess();

        ParameterValueGroup in = desc.getInputDescriptor().createValue();
        in.parameter("feature_in").setValue(sourceFeatures);
        in.parameter("feature_target").setValue(targetFeatures);
        in.parameter("intersect").setValue(false);

        proc.setInput(in);

        proc.run();

        //Features out
        final FeatureCollection<?> featureListOut = (FeatureCollection<?>) proc.getOutput().parameter("feature_out").getValue();

        //Expected Features out
        final FeatureCollection<?> featureListResult = buildResultNear();

        assertEquals(featureListOut.getFeatureType(), featureListResult.getFeatureType());
        assertEquals(featureListOut.size(), featureListResult.size());
        assertTrue(featureListOut.containsAll(featureListResult));
    }

    /**
     * Test SpatialJoin process with Intersection method
     */
    @Test
    public void testSpacialJoinIntersection() {

        // Inputs
        final FeatureCollection<?> targetFeatures = buildFeatureListInter1();
        final FeatureCollection<?> sourceFeatures = buildFeatureListInter2();
       
        // Process
        ProcessDescriptor desc = ProcessFinder.getProcessDescriptor("vector", "spatialjoin");
        org.geotoolkit.process.Process proc = desc.createProcess();

        ParameterValueGroup in = desc.getInputDescriptor().createValue();
        in.parameter("feature_in").setValue(sourceFeatures);
        in.parameter("feature_target").setValue(targetFeatures);
        in.parameter("intersect").setValue(true);

        proc.setInput(in);

        proc.run();

        //Features out
        final FeatureCollection<?> featureListOut = (FeatureCollection<?>) proc.getOutput().parameter("feature_out").getValue();
        
        //Expected Features out
        final FeatureCollection<?> featureListResult = buildResultInter();
        
        assertEquals(featureListOut.getFeatureType(), featureListResult.getFeatureType());
        assertEquals(featureListOut.size(), featureListResult.size());
        assertTrue(featureListOut.containsAll(featureListResult));
    }

    /**
     * Test SpatialJoin process with Intersection method and no intersection
     */
    @Test
    public void testSpacialJoinIntersection2() {

        // Inputs
        final FeatureCollection<?> targetFeatures = buildFeatureListInter1_2();
        final FeatureCollection<?> sourceFeatures = buildFeatureListInter2();

        // Process
        ProcessDescriptor desc = ProcessFinder.getProcessDescriptor("vector", "spatialjoin");
        org.geotoolkit.process.Process proc = desc.createProcess();

        ParameterValueGroup in = desc.getInputDescriptor().createValue();
        in.parameter("feature_in").setValue(sourceFeatures);
        in.parameter("feature_target").setValue(targetFeatures);
        in.parameter("intersect").setValue(true);

        proc.setInput(in);

        proc.run();

        //Features out
        final FeatureCollection<?> featureListOut = (FeatureCollection<?>) proc.getOutput().parameter("feature_out").getValue();
        
        //Expected Features out
        final FeatureCollection<?> featureListResult = buildResultInter2();
        assertEquals(featureListOut.getFeatureType(), featureListResult.getFeatureType());
        assertEquals(featureListOut.size(), featureListResult.size());
        assertTrue(featureListOut.containsAll(featureListResult));
    }

    private static SimpleFeatureType createSimpleType1() throws NoSuchAuthorityCodeException, FactoryException {
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("SJ_Type1");
        ftb.add("name", String.class);
        ftb.add("age", Integer.class);
        ftb.add("geom1", Geometry.class, CRS.decode("EPSG:3395"));
        ftb.setDefaultGeometry("geom1");
        final SimpleFeatureType sft = ftb.buildSimpleFeatureType();
        return sft;
    }

    private static SimpleFeatureType createSimpleType2() throws NoSuchAuthorityCodeException, FactoryException {
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("SJ_Type2");
        ftb.add("type", String.class);
        ftb.add("age", Integer.class);
        ftb.add("geom1", Geometry.class, CRS.decode("EPSG:3395"));
        ftb.setDefaultGeometry("geom1");
        final SimpleFeatureType sft = ftb.buildSimpleFeatureType();
        return sft;
    }

    private static SimpleFeatureType createSimpleTypeResult() throws NoSuchAuthorityCodeException, FactoryException {
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("SJ_Type1_SJ_Type2");
        ftb.add("name", String.class);
        ftb.add("age", Integer.class);
        ftb.add("geom1", Geometry.class, CRS.decode("EPSG:3395"));
        ftb.add("type_SJ_Type2", String.class);
        ftb.add("age_SJ_Type2", Integer.class);
        ftb.setDefaultGeometry("geom1");
        final SimpleFeatureType sft = ftb.buildSimpleFeatureType();
        return sft;
    }

    private static FeatureCollection<?> buildFeatureList1() {

        try {
            type = createSimpleType1();
        } catch (NoSuchAuthorityCodeException ex) {
            Logger.getLogger(SpatialJoinTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FactoryException ex) {
            Logger.getLogger(SpatialJoinTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        final FeatureCollection<Feature> featureList = DataUtilities.collection("Target", type);

        final Feature feature1 = FeatureUtilities.defaultFeature(type, "id-1");
        feature1.getProperty("name").setValue("Human1");
        feature1.getProperty("age").setValue(20);
        feature1.getProperty("geom1").setValue(geometryFactory.createPoint(new Coordinate(3, 2)));
        featureList.add(feature1);

        final Feature feature2 = FeatureUtilities.defaultFeature(type, "id-2");
        feature2.getProperty("name").setValue("Human2");
        feature2.getProperty("age").setValue(10);
        feature2.getProperty("geom1").setValue(geometryFactory.createPoint(new Coordinate(3, 5)));
        featureList.add(feature2);

        final Feature feature3 = FeatureUtilities.defaultFeature(type, "id-3");
        feature3.getProperty("name").setValue("Human3");
        feature3.getProperty("age").setValue(35);
        feature3.getProperty("geom1").setValue(geometryFactory.createPoint(new Coordinate(6, 6)));
        featureList.add(feature3);

        final Feature feature4 = FeatureUtilities.defaultFeature(type, "id-4");
        feature4.getProperty("name").setValue("Human4");
        feature4.getProperty("age").setValue(40);
        feature4.getProperty("geom1").setValue(geometryFactory.createPoint(new Coordinate(6, 2)));
        featureList.add(feature4);

        final Feature feature5 = FeatureUtilities.defaultFeature(type, "id-5");
        feature5.getProperty("name").setValue("Human5");
        feature5.getProperty("age").setValue(23);
        feature5.getProperty("geom1").setValue(geometryFactory.createPoint(new Coordinate(7, 4)));
        featureList.add(feature5);

        final Feature feature6 = FeatureUtilities.defaultFeature(type, "id-6");
        feature6.getProperty("name").setValue("Human6");
        feature6.getProperty("age").setValue(32);
        feature6.getProperty("geom1").setValue(geometryFactory.createPoint(new Coordinate(9, 4)));
        featureList.add(feature6);

        final Feature feature7 = FeatureUtilities.defaultFeature(type, "id-7");
        feature7.getProperty("name").setValue("Human7");
        feature7.getProperty("age").setValue(28);
        feature7.getProperty("geom1").setValue(geometryFactory.createPoint(new Coordinate(9, 1)));
        featureList.add(feature7);

        return featureList;
    }

    private static FeatureCollection<?> buildFeatureList2() {

        try {
            type = createSimpleType2();
        } catch (NoSuchAuthorityCodeException ex) {
            Logger.getLogger(SpatialJoinTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FactoryException ex) {
            Logger.getLogger(SpatialJoinTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        final FeatureCollection<Feature> featureList = DataUtilities.collection("source", type);

        final Feature feature1 = FeatureUtilities.defaultFeature(type, "id-11");
        feature1.getProperty("type").setValue("Tree1");
        feature1.getProperty("age").setValue(220);
        feature1.getProperty("geom1").setValue(geometryFactory.createPoint(new Coordinate(2, 1)));
        featureList.add(feature1);

        final Feature feature2 = FeatureUtilities.defaultFeature(type, "id-12");
        feature2.getProperty("type").setValue("Tree2");
        feature2.getProperty("age").setValue(100);
        feature2.getProperty("geom1").setValue(geometryFactory.createPoint(new Coordinate(3, 6)));
        featureList.add(feature2);

        final Feature feature3 = FeatureUtilities.defaultFeature(type, "id-13");
        feature3.getProperty("type").setValue("Tree3");
        feature3.getProperty("age").setValue(5);
        feature3.getProperty("geom1").setValue(geometryFactory.createPoint(new Coordinate(5, 5)));
        featureList.add(feature3);

        final Feature feature4 = FeatureUtilities.defaultFeature(type, "id-14");
        feature4.getProperty("type").setValue("Tree4");
        feature4.getProperty("age").setValue(40);
        feature4.getProperty("geom1").setValue(geometryFactory.createPoint(new Coordinate(6, 2)));
        featureList.add(feature4);

        final Feature feature5 = FeatureUtilities.defaultFeature(type, "id-15");
        feature5.getProperty("type").setValue("Tree5");
        feature5.getProperty("age").setValue(57);
        feature5.getProperty("geom1").setValue(geometryFactory.createPoint(new Coordinate(9, 5)));
        featureList.add(feature5);

        final Feature feature6 = FeatureUtilities.defaultFeature(type, "id-16");
        feature6.getProperty("type").setValue("Tree6");
        feature6.getProperty("age").setValue(68);
        feature6.getProperty("geom1").setValue(geometryFactory.createPoint(new Coordinate(9, 3)));
        featureList.add(feature6);

        final Feature feature7 = FeatureUtilities.defaultFeature(type, "id-17");
        feature7.getProperty("type").setValue("Tree7");
        feature7.getProperty("age").setValue(94);
        feature7.getProperty("geom1").setValue(geometryFactory.createPoint(new Coordinate(11, 2)));
        featureList.add(feature7);

        return featureList;
    }

    private static FeatureCollection<?> buildFeatureListInter1() {

        try {
            type = createSimpleType1();
        } catch (NoSuchAuthorityCodeException ex) {
            Logger.getLogger(SpatialJoinTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FactoryException ex) {
            Logger.getLogger(SpatialJoinTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        final FeatureCollection<Feature> featureList = DataUtilities.collection("target", type);

        final Feature feature1 = FeatureUtilities.defaultFeature(type, "id-01");
        feature1.getProperty("name").setValue("Field");
        feature1.getProperty("age").setValue(1);

        LinearRing ring = geometryFactory.createLinearRing(new Coordinate[]{
                    new Coordinate(4, 3),
                    new Coordinate(4, 5),
                    new Coordinate(7, 5),
                    new Coordinate(7, 3),
                    new Coordinate(4, 3)
                });

        feature1.getProperty("geom1").setValue(geometryFactory.createPolygon(ring, null));
        featureList.add(feature1);

        return featureList;
    }

    private static FeatureCollection<?> buildFeatureListInter1_2() {

        try {
            type = createSimpleType1();
        } catch (NoSuchAuthorityCodeException ex) {
            Logger.getLogger(SpatialJoinTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FactoryException ex) {
            Logger.getLogger(SpatialJoinTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        final FeatureCollection<Feature> featureList = DataUtilities.collection("target", type);

        final Feature feature1 = FeatureUtilities.defaultFeature(type, "id-01");
        feature1.getProperty("name").setValue("Field");
        feature1.getProperty("age").setValue(1);

        LinearRing ring = geometryFactory.createLinearRing(new Coordinate[]{
                    new Coordinate(40, 30),
                    new Coordinate(40, 50),
                    new Coordinate(70, 50),
                    new Coordinate(70, 30),
                    new Coordinate(40, 30)
                });

        feature1.getProperty("geom1").setValue(geometryFactory.createPolygon(ring, null));
        featureList.add(feature1);

        return featureList;
    }

    private static FeatureCollection<?> buildFeatureListInter2() {

        try {
            type = createSimpleType2();
        } catch (NoSuchAuthorityCodeException ex) {
            Logger.getLogger(SpatialJoinTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FactoryException ex) {
            Logger.getLogger(SpatialJoinTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        final FeatureCollection<Feature> featureList = DataUtilities.collection("source", type);

        final Feature feature1 = FeatureUtilities.defaultFeature(type, "id-11");
        feature1.getProperty("type").setValue("something1");
        feature1.getProperty("age").setValue(1);
        LinearRing ring = geometryFactory.createLinearRing(new Coordinate[]{
                    new Coordinate(1, 3),
                    new Coordinate(1, 5),
                    new Coordinate(2, 5),
                    new Coordinate(2, 3),
                    new Coordinate(1, 3)
                });
        feature1.getProperty("geom1").setValue(geometryFactory.createPolygon(ring, null));
        featureList.add(feature1);

        final Feature feature2 = FeatureUtilities.defaultFeature(type, "id-12");
        feature2.getProperty("type").setValue("something2");
        feature2.getProperty("age").setValue(2);
        ring = geometryFactory.createLinearRing(new Coordinate[]{
                    new Coordinate(3, 2),
                    new Coordinate(3, 6),
                    new Coordinate(5, 6),
                    new Coordinate(5, 2),
                    new Coordinate(3, 2)
                });
        feature2.getProperty("geom1").setValue(geometryFactory.createPolygon(ring, null));
        featureList.add(feature2);

        final Feature feature3 = FeatureUtilities.defaultFeature(type, "id-13");
        feature3.getProperty("type").setValue("something3");
        feature3.getProperty("age").setValue(3);
        ring = geometryFactory.createLinearRing(new Coordinate[]{
                    new Coordinate(6, 4),
                    new Coordinate(6, 6),
                    new Coordinate(8, 6),
                    new Coordinate(8, 4),
                    new Coordinate(6, 4)
                });
        feature3.getProperty("geom1").setValue(geometryFactory.createPolygon(ring, null));
        featureList.add(feature3);

        final Feature feature4 = FeatureUtilities.defaultFeature(type, "id-14");
        feature4.getProperty("type").setValue("something4");
        feature4.getProperty("age").setValue(4);
        ring = geometryFactory.createLinearRing(new Coordinate[]{
                    new Coordinate(7, 2),
                    new Coordinate(7, 3),
                    new Coordinate(8, 3),
                    new Coordinate(8, 2),
                    new Coordinate(7, 2)
                });
        feature4.getProperty("geom1").setValue(geometryFactory.createPolygon(ring, null));
        featureList.add(feature4);

        return featureList;
    }

    private static FeatureCollection<?> buildResultNear() {
        try {
            type = createSimpleTypeResult();
        } catch (NoSuchAuthorityCodeException ex) {
            Logger.getLogger(SpatialJoinTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FactoryException ex) {
            Logger.getLogger(SpatialJoinTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        final FeatureCollection<Feature> featureList = DataUtilities.collection("Target", type);

        final Feature feature1 = FeatureUtilities.defaultFeature(type, "id-1_id-11");
        feature1.getProperty("name").setValue("Human1");
        feature1.getProperty("age").setValue(20);
        feature1.getProperty("geom1").setValue(geometryFactory.createPoint(new Coordinate(3, 2)));
        feature1.getProperty("type_SJ_Type2").setValue("Tree1");
        feature1.getProperty("age_SJ_Type2").setValue(220);
        featureList.add(feature1);

        final Feature feature2 = FeatureUtilities.defaultFeature(type, "id-2_id-12");
        feature2.getProperty("name").setValue("Human2");
        feature2.getProperty("age").setValue(10);
        feature2.getProperty("geom1").setValue(geometryFactory.createPoint(new Coordinate(3, 5)));
        feature2.getProperty("type_SJ_Type2").setValue("Tree2");
        feature2.getProperty("age_SJ_Type2").setValue(100);
        featureList.add(feature2);

        final Feature feature3 = FeatureUtilities.defaultFeature(type, "id-3_id-13");
        feature3.getProperty("name").setValue("Human3");
        feature3.getProperty("age").setValue(35);
        feature3.getProperty("geom1").setValue(geometryFactory.createPoint(new Coordinate(6, 6)));
        feature3.getProperty("type_SJ_Type2").setValue("Tree3");
        feature3.getProperty("age_SJ_Type2").setValue(5);
        featureList.add(feature3);

        final Feature feature4 = FeatureUtilities.defaultFeature(type, "id-4_id-14");
        feature4.getProperty("name").setValue("Human4");
        feature4.getProperty("age").setValue(40);
        feature4.getProperty("geom1").setValue(geometryFactory.createPoint(new Coordinate(6, 2)));
        feature4.getProperty("type_SJ_Type2").setValue("Tree4");
        feature4.getProperty("age_SJ_Type2").setValue(40);
        featureList.add(feature4);

        final Feature feature5 = FeatureUtilities.defaultFeature(type, "id-5_id-13");
        feature5.getProperty("name").setValue("Human5");
        feature5.getProperty("age").setValue(23);
        feature5.getProperty("geom1").setValue(geometryFactory.createPoint(new Coordinate(7, 4)));
        feature5.getProperty("type_SJ_Type2").setValue("Tree3");
        feature5.getProperty("age_SJ_Type2").setValue(5);
        featureList.add(feature5);

        final Feature feature6 = FeatureUtilities.defaultFeature(type, "id-6_id-16");
        feature6.getProperty("name").setValue("Human6");
        feature6.getProperty("age").setValue(32);
        feature6.getProperty("geom1").setValue(geometryFactory.createPoint(new Coordinate(9, 4)));
        feature6.getProperty("type_SJ_Type2").setValue("Tree6");
        feature6.getProperty("age_SJ_Type2").setValue(68);
        featureList.add(feature6);

        final Feature feature7 = FeatureUtilities.defaultFeature(type, "id-7_id-16");
        feature7.getProperty("name").setValue("Human7");
        feature7.getProperty("age").setValue(28);
        feature7.getProperty("geom1").setValue(geometryFactory.createPoint(new Coordinate(9, 1)));
        feature7.getProperty("type_SJ_Type2").setValue("Tree6");
        feature7.getProperty("age_SJ_Type2").setValue(68);
        featureList.add(feature7);

        return featureList;
    }

    private static FeatureCollection<?> buildResultInter() {

        try {
            type = createSimpleTypeResult();
        } catch (NoSuchAuthorityCodeException ex) {
            Logger.getLogger(SpatialJoinTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FactoryException ex) {
            Logger.getLogger(SpatialJoinTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        final FeatureCollection<Feature> featureList = DataUtilities.collection("source", type);

        final Feature feature1 = FeatureUtilities.defaultFeature(type, "id-01_id-12");
        feature1.getProperty("name").setValue("Field");
        feature1.getProperty("age").setValue(1);

        LinearRing ring = geometryFactory.createLinearRing(new Coordinate[]{
                    new Coordinate(4, 3),
                    new Coordinate(4, 5),
                    new Coordinate(7, 5),
                    new Coordinate(7, 3),
                    new Coordinate(4, 3)
                });

        feature1.getProperty("geom1").setValue(geometryFactory.createPolygon(ring, null));
        feature1.getProperty("type_SJ_Type2").setValue("something2");
        feature1.getProperty("age_SJ_Type2").setValue(2);
        featureList.add(feature1);

        return featureList;
    }

    private static FeatureCollection<?> buildResultInter2() {

        try {
            type = createSimpleTypeResult();
        } catch (NoSuchAuthorityCodeException ex) {
            Logger.getLogger(SpatialJoinTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FactoryException ex) {
            Logger.getLogger(SpatialJoinTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        final FeatureCollection<Feature> featureList = DataUtilities.collection("source", type);

        final Feature feature1 = FeatureUtilities.defaultFeature(type, "id-01");
        feature1.getProperty("name").setValue("Field");
        feature1.getProperty("age").setValue(1);

        LinearRing ring = geometryFactory.createLinearRing(new Coordinate[]{
                    new Coordinate(40, 30),
                    new Coordinate(40, 50),
                    new Coordinate(70, 50),
                    new Coordinate(70, 30),
                    new Coordinate(40, 30)
                });

        feature1.getProperty("geom1").setValue(geometryFactory.createPolygon(ring, null));
        feature1.getProperty("type_SJ_Type2").setValue(null);
        feature1.getProperty("age_SJ_Type2").setValue(null);
        featureList.add(feature1);

        return featureList;
    }
}
