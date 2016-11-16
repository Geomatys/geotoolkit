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
package org.geotoolkit.processing.vector.spatialjoin;

import org.geotoolkit.process.ProcessException;
import org.opengis.util.NoSuchIdentifierException;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import org.apache.sis.feature.builder.AttributeRole;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.internal.feature.AttributeConvention;

import org.geotoolkit.data.FeatureStoreUtilities;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessFinder;
import org.geotoolkit.processing.vector.AbstractProcessTest;
import org.apache.sis.referencing.CRS;

import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.util.FactoryException;

import org.junit.Test;
import static org.junit.Assert.*;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;

/**
 * JUnit test of SpatialJoin process
 *
 * @author Quentin Boileau
 * @module
 */
public class SpatialJoinTest extends AbstractProcessTest {

    private static final GeometryFactory geometryFactory = new GeometryFactory();
    private static FeatureType type;

    public SpatialJoinTest() {
        super("spatialjoin");
    }

    /**
     * Test SpatialJoin process with Nearest method
     */
    @Test
    public void testSpacialJoin() throws ProcessException, NoSuchIdentifierException, FactoryException {

        // Inputs
        final FeatureCollection targetFeatures = buildFeatureList1();
        final FeatureCollection sourceFeatures = buildFeatureList2();

        // Process
        ProcessDescriptor desc = ProcessFinder.getProcessDescriptor("vector", "spatialjoin");

        ParameterValueGroup in = desc.getInputDescriptor().createValue();
        in.parameter("feature_in").setValue(sourceFeatures);
        in.parameter("feature_target").setValue(targetFeatures);
        in.parameter("intersect").setValue(false);
        org.geotoolkit.process.Process proc = desc.createProcess(in);

        //Features out
        final FeatureCollection featureListOut = (FeatureCollection) proc.call().parameter("feature_out").getValue();

        //Expected Features out
        final FeatureCollection featureListResult = buildResultNear();
        compare(featureListResult,featureListOut);
    }

    /**
     * Test SpatialJoin process with Intersection method
     */
    @Test
    public void testSpacialJoinIntersection() throws ProcessException, NoSuchIdentifierException, FactoryException {

        // Inputs
        final FeatureCollection targetFeatures = buildFeatureListInter1();
        final FeatureCollection sourceFeatures = buildFeatureListInter2();

        // Process
        ProcessDescriptor desc = ProcessFinder.getProcessDescriptor("vector", "spatialjoin");

        ParameterValueGroup in = desc.getInputDescriptor().createValue();
        in.parameter("feature_in").setValue(sourceFeatures);
        in.parameter("feature_target").setValue(targetFeatures);
        in.parameter("intersect").setValue(true);
        org.geotoolkit.process.Process proc = desc.createProcess(in);

        //Features out
        final FeatureCollection featureListOut = (FeatureCollection) proc.call().parameter("feature_out").getValue();

        //Expected Features out
        final FeatureCollection featureListResult = buildResultInter();
        compare(featureListOut, featureListResult);
    }

    /**
     * Test SpatialJoin process with Intersection method and no intersection
     */
    @Test
    public void testSpacialJoinIntersection2() throws ProcessException, NoSuchIdentifierException, FactoryException {

        // Inputs
        final FeatureCollection targetFeatures = buildFeatureListInter1_2();
        final FeatureCollection sourceFeatures = buildFeatureListInter2();

        // Process
        ProcessDescriptor desc = ProcessFinder.getProcessDescriptor("vector", "spatialjoin");

        ParameterValueGroup in = desc.getInputDescriptor().createValue();
        in.parameter("feature_in").setValue(sourceFeatures);
        in.parameter("feature_target").setValue(targetFeatures);
        in.parameter("intersect").setValue(true);
        org.geotoolkit.process.Process proc = desc.createProcess(in);

        //Features out
        final FeatureCollection featureListOut = (FeatureCollection) proc.call().parameter("feature_out").getValue();

        //Expected Features out
        final FeatureCollection featureListResult = buildResultInter2();
        compare(featureListResult,featureListOut);
    }

    private static FeatureType createSimpleType1() throws NoSuchAuthorityCodeException, FactoryException {
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("SJ_Type1");
        ftb.addAttribute(String.class).setName(AttributeConvention.IDENTIFIER_PROPERTY);
        ftb.addAttribute(String.class).setName("name");
        ftb.addAttribute(Integer.class).setName("age");
        ftb.addAttribute(Geometry.class).setName("geom1").setCRS(CRS.forCode("EPSG:3395")).addRole(AttributeRole.DEFAULT_GEOMETRY);
        return ftb.build();
    }

    private static FeatureType createSimpleType2() throws NoSuchAuthorityCodeException, FactoryException {
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("SJ_Type2");
        ftb.addAttribute(String.class).setName(AttributeConvention.IDENTIFIER_PROPERTY);
        ftb.addAttribute(String.class).setName("type");
        ftb.addAttribute(Integer.class).setName("age");
        ftb.addAttribute(Geometry.class).setName("geom1").setCRS(CRS.forCode("EPSG:3395")).addRole(AttributeRole.DEFAULT_GEOMETRY);
        return ftb.build();
    }

    private static FeatureType createSimpleTypeResult() throws NoSuchAuthorityCodeException, FactoryException {
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("SJ_Type1_SJ_Type2");
        ftb.addAttribute(String.class).setName(AttributeConvention.IDENTIFIER_PROPERTY);
        ftb.addAttribute(String.class).setName("name");
        ftb.addAttribute(Integer.class).setName("age");
        ftb.addAttribute(Geometry.class).setName("geom1").setCRS(CRS.forCode("EPSG:3395")).addRole(AttributeRole.DEFAULT_GEOMETRY);
        ftb.addAttribute(String.class).setName("type_SJ_Type2").setMinimumOccurs(0).setMaximumOccurs(1);
        ftb.addAttribute(Integer.class).setName("age_SJ_Type2").setMinimumOccurs(0).setMaximumOccurs(1);
        return ftb.build();
    }

    private static FeatureCollection buildFeatureList1() throws FactoryException {

        type = createSimpleType1();

        final FeatureCollection featureList = FeatureStoreUtilities.collection("Target", type);

        final Feature feature1 = type.newInstance();
        feature1.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "id-1");
        feature1.setPropertyValue("name","Human1");
        feature1.setPropertyValue("age",20);
        feature1.setPropertyValue("geom1",geometryFactory.createPoint(new Coordinate(3, 2)));
        featureList.add(feature1);

        final Feature feature2 = type.newInstance();
        feature2.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "id-2");
        feature2.setPropertyValue("name","Human2");
        feature2.setPropertyValue("age",10);
        feature2.setPropertyValue("geom1",geometryFactory.createPoint(new Coordinate(3, 5)));
        featureList.add(feature2);

        final Feature feature3 = type.newInstance();
        feature3.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "id-3");
        feature3.setPropertyValue("name","Human3");
        feature3.setPropertyValue("age",35);
        feature3.setPropertyValue("geom1",geometryFactory.createPoint(new Coordinate(6, 6)));
        featureList.add(feature3);

        final Feature feature4 = type.newInstance();
        feature4.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "id-4");
        feature4.setPropertyValue("name","Human4");
        feature4.setPropertyValue("age",40);
        feature4.setPropertyValue("geom1",geometryFactory.createPoint(new Coordinate(6, 2)));
        featureList.add(feature4);

        final Feature feature5 = type.newInstance();
        feature5.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "id-5");
        feature5.setPropertyValue("name","Human5");
        feature5.setPropertyValue("age",23);
        feature5.setPropertyValue("geom1",geometryFactory.createPoint(new Coordinate(7, 4)));
        featureList.add(feature5);

        final Feature feature6 = type.newInstance();
        feature6.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "id-6");
        feature6.setPropertyValue("name","Human6");
        feature6.setPropertyValue("age",32);
        feature6.setPropertyValue("geom1",geometryFactory.createPoint(new Coordinate(9, 4)));
        featureList.add(feature6);

        final Feature feature7 = type.newInstance();
        feature7.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "id-7");
        feature7.setPropertyValue("name","Human7");
        feature7.setPropertyValue("age",28);
        feature7.setPropertyValue("geom1",geometryFactory.createPoint(new Coordinate(9, 1)));
        featureList.add(feature7);

        return featureList;
    }

    private static FeatureCollection buildFeatureList2() throws FactoryException {

        type = createSimpleType2();

        final FeatureCollection featureList = FeatureStoreUtilities.collection("source", type);

        final Feature feature1 = type.newInstance();
        feature1.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "id-11");
        feature1.setPropertyValue("type","Tree1");
        feature1.setPropertyValue("age",220);
        feature1.setPropertyValue("geom1",geometryFactory.createPoint(new Coordinate(2, 1)));
        featureList.add(feature1);

        final Feature feature2 = type.newInstance();
        feature2.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "id-12");
        feature2.setPropertyValue("type","Tree2");
        feature2.setPropertyValue("age",100);
        feature2.setPropertyValue("geom1",geometryFactory.createPoint(new Coordinate(3, 6)));
        featureList.add(feature2);

        final Feature feature3 = type.newInstance();
        feature3.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "id-13");
        feature3.setPropertyValue("type","Tree3");
        feature3.setPropertyValue("age",5);
        feature3.setPropertyValue("geom1",geometryFactory.createPoint(new Coordinate(5, 5)));
        featureList.add(feature3);

        final Feature feature4 = type.newInstance();
        feature4.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "id-14");
        feature4.setPropertyValue("type","Tree4");
        feature4.setPropertyValue("age",40);
        feature4.setPropertyValue("geom1",geometryFactory.createPoint(new Coordinate(6, 2)));
        featureList.add(feature4);

        final Feature feature5 = type.newInstance();
        feature5.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "id-15");
        feature5.setPropertyValue("type","Tree5");
        feature5.setPropertyValue("age",57);
        feature5.setPropertyValue("geom1",geometryFactory.createPoint(new Coordinate(9, 5)));
        featureList.add(feature5);

        final Feature feature6 = type.newInstance();
        feature6.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "id-16");
        feature6.setPropertyValue("type","Tree6");
        feature6.setPropertyValue("age",68);
        feature6.setPropertyValue("geom1",geometryFactory.createPoint(new Coordinate(9, 3)));
        featureList.add(feature6);

        final Feature feature7 = type.newInstance();
        feature7.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "id-17");
        feature7.setPropertyValue("type","Tree7");
        feature7.setPropertyValue("age",94);
        feature7.setPropertyValue("geom1",geometryFactory.createPoint(new Coordinate(11, 2)));
        featureList.add(feature7);

        return featureList;
    }

    private static FeatureCollection buildFeatureListInter1() throws FactoryException {

        type = createSimpleType1();

        final FeatureCollection featureList = FeatureStoreUtilities.collection("target", type);

        final Feature feature1 = type.newInstance();
        feature1.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "id-01");
        feature1.setPropertyValue("name","Field");
        feature1.setPropertyValue("age",1);

        LinearRing ring = geometryFactory.createLinearRing(new Coordinate[]{
                    new Coordinate(4, 3),
                    new Coordinate(4, 5),
                    new Coordinate(7, 5),
                    new Coordinate(7, 3),
                    new Coordinate(4, 3)
                });

        feature1.setPropertyValue("geom1",geometryFactory.createPolygon(ring, null));
        featureList.add(feature1);

        return featureList;
    }

    private static FeatureCollection buildFeatureListInter1_2() throws FactoryException {

        type = createSimpleType1();

        final FeatureCollection featureList = FeatureStoreUtilities.collection("target", type);

        final Feature feature1 = type.newInstance();
        feature1.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "id-01");
        feature1.setPropertyValue("name","Field");
        feature1.setPropertyValue("age",1);

        LinearRing ring = geometryFactory.createLinearRing(new Coordinate[]{
                    new Coordinate(40, 30),
                    new Coordinate(40, 50),
                    new Coordinate(70, 50),
                    new Coordinate(70, 30),
                    new Coordinate(40, 30)
                });

        feature1.setPropertyValue("geom1",geometryFactory.createPolygon(ring, null));
        featureList.add(feature1);

        return featureList;
    }

    private static FeatureCollection buildFeatureListInter2() throws FactoryException {

        type = createSimpleType2();
        final FeatureCollection featureList = FeatureStoreUtilities.collection("source", type);

        final Feature feature1 = type.newInstance();
        feature1.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "id-11");
        feature1.setPropertyValue("type","something1");
        feature1.setPropertyValue("age",1);
        LinearRing ring = geometryFactory.createLinearRing(new Coordinate[]{
                    new Coordinate(1, 3),
                    new Coordinate(1, 5),
                    new Coordinate(2, 5),
                    new Coordinate(2, 3),
                    new Coordinate(1, 3)
                });
        feature1.setPropertyValue("geom1",geometryFactory.createPolygon(ring, null));
        featureList.add(feature1);

        final Feature feature2 = type.newInstance();
        feature2.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "id-12");
        feature2.setPropertyValue("type","something2");
        feature2.setPropertyValue("age",2);
        ring = geometryFactory.createLinearRing(new Coordinate[]{
                    new Coordinate(3, 2),
                    new Coordinate(3, 6),
                    new Coordinate(5, 6),
                    new Coordinate(5, 2),
                    new Coordinate(3, 2)
                });
        feature2.setPropertyValue("geom1",geometryFactory.createPolygon(ring, null));
        featureList.add(feature2);

        final Feature feature3 = type.newInstance();
        feature3.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "id-13");
        feature3.setPropertyValue("type","something3");
        feature3.setPropertyValue("age",3);
        ring = geometryFactory.createLinearRing(new Coordinate[]{
                    new Coordinate(6, 4),
                    new Coordinate(6, 6),
                    new Coordinate(8, 6),
                    new Coordinate(8, 4),
                    new Coordinate(6, 4)
                });
        feature3.setPropertyValue("geom1",geometryFactory.createPolygon(ring, null));
        featureList.add(feature3);

        final Feature feature4 = type.newInstance();
        feature4.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "id-14");
        feature4.setPropertyValue("type","something4");
        feature4.setPropertyValue("age",4);
        ring = geometryFactory.createLinearRing(new Coordinate[]{
                    new Coordinate(7, 2),
                    new Coordinate(7, 3),
                    new Coordinate(8, 3),
                    new Coordinate(8, 2),
                    new Coordinate(7, 2)
                });
        feature4.setPropertyValue("geom1",geometryFactory.createPolygon(ring, null));
        featureList.add(feature4);

        return featureList;
    }

    private static FeatureCollection buildResultNear() throws FactoryException {
        type = createSimpleTypeResult();

        final FeatureCollection featureList = FeatureStoreUtilities.collection("Target", type);

        final Feature feature1 = type.newInstance();
        feature1.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "id-1_id-11");
        feature1.setPropertyValue("name","Human1");
        feature1.setPropertyValue("age",20);
        feature1.setPropertyValue("geom1",geometryFactory.createPoint(new Coordinate(3, 2)));
        feature1.setPropertyValue("type_SJ_Type2","Tree1");
        feature1.setPropertyValue("age_SJ_Type2",220);
        featureList.add(feature1);

        final Feature feature2 = type.newInstance();
        feature2.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "id-2_id-12");
        feature2.setPropertyValue("name","Human2");
        feature2.setPropertyValue("age",10);
        feature2.setPropertyValue("geom1",geometryFactory.createPoint(new Coordinate(3, 5)));
        feature2.setPropertyValue("type_SJ_Type2","Tree2");
        feature2.setPropertyValue("age_SJ_Type2",100);
        featureList.add(feature2);

        final Feature feature3 = type.newInstance();
        feature3.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "id-3_id-13");
        feature3.setPropertyValue("name","Human3");
        feature3.setPropertyValue("age",35);
        feature3.setPropertyValue("geom1",geometryFactory.createPoint(new Coordinate(6, 6)));
        feature3.setPropertyValue("type_SJ_Type2","Tree3");
        feature3.setPropertyValue("age_SJ_Type2",5);
        featureList.add(feature3);

        final Feature feature4 = type.newInstance();
        feature4.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "id-4_id-14");
        feature4.setPropertyValue("name","Human4");
        feature4.setPropertyValue("age",40);
        feature4.setPropertyValue("geom1",geometryFactory.createPoint(new Coordinate(6, 2)));
        feature4.setPropertyValue("type_SJ_Type2","Tree4");
        feature4.setPropertyValue("age_SJ_Type2",40);
        featureList.add(feature4);

        final Feature feature5 = type.newInstance();
        feature5.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "id-5_id-13");
        feature5.setPropertyValue("name","Human5");
        feature5.setPropertyValue("age",23);
        feature5.setPropertyValue("geom1",geometryFactory.createPoint(new Coordinate(7, 4)));
        feature5.setPropertyValue("type_SJ_Type2","Tree3");
        feature5.setPropertyValue("age_SJ_Type2",5);
        featureList.add(feature5);

        final Feature feature6 = type.newInstance();
        feature6.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "id-6_id-16");
        feature6.setPropertyValue("name","Human6");
        feature6.setPropertyValue("age",32);
        feature6.setPropertyValue("geom1",geometryFactory.createPoint(new Coordinate(9, 4)));
        feature6.setPropertyValue("type_SJ_Type2","Tree6");
        feature6.setPropertyValue("age_SJ_Type2",68);
        featureList.add(feature6);

        final Feature feature7 = type.newInstance();
        feature7.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "id-7_id-16");
        feature7.setPropertyValue("name","Human7");
        feature7.setPropertyValue("age",28);
        feature7.setPropertyValue("geom1",geometryFactory.createPoint(new Coordinate(9, 1)));
        feature7.setPropertyValue("type_SJ_Type2","Tree6");
        feature7.setPropertyValue("age_SJ_Type2",68);
        featureList.add(feature7);

        return featureList;
    }

    private static FeatureCollection buildResultInter() throws FactoryException {

        type = createSimpleTypeResult();

        final FeatureCollection featureList = FeatureStoreUtilities.collection("target", type);

        final Feature feature1 = type.newInstance();
        feature1.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "id-01_id-12");
        feature1.setPropertyValue("name","Field");
        feature1.setPropertyValue("age",1);

        LinearRing ring = geometryFactory.createLinearRing(new Coordinate[]{
                    new Coordinate(4, 3),
                    new Coordinate(4, 5),
                    new Coordinate(7, 5),
                    new Coordinate(7, 3),
                    new Coordinate(4, 3)
                });

        feature1.setPropertyValue("geom1",geometryFactory.createPolygon(ring, null));
        feature1.setPropertyValue("type_SJ_Type2","something2");
        feature1.setPropertyValue("age_SJ_Type2",2);
        featureList.add(feature1);

        return featureList;
    }

    private static FeatureCollection buildResultInter2() throws FactoryException {

        type = createSimpleTypeResult();

        final FeatureCollection featureList = FeatureStoreUtilities.collection("target", type);

        final Feature feature1 = type.newInstance();
        feature1.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "id-01");
        feature1.setPropertyValue("name","Field");
        feature1.setPropertyValue("age",1);

        LinearRing ring = geometryFactory.createLinearRing(new Coordinate[]{
                    new Coordinate(40, 30),
                    new Coordinate(40, 50),
                    new Coordinate(70, 50),
                    new Coordinate(70, 30),
                    new Coordinate(40, 30)
                });

        feature1.setPropertyValue("geom1",geometryFactory.createPolygon(ring, null));
        feature1.setPropertyValue("type_SJ_Type2",null);
        feature1.setPropertyValue("age_SJ_Type2",null);
        featureList.add(feature1);

        return featureList;
    }
}
