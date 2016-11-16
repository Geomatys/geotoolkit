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
package org.geotoolkit.processing.vector.merge;

import org.geotoolkit.process.ProcessException;
import org.opengis.util.NoSuchIdentifierException;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.Polygon;
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
 * JUnit test of merge process
 *
 * @author Quentin Boileau
 * @module
 */
public class MergeTest extends AbstractProcessTest {

    private static final GeometryFactory geometryFactory = new GeometryFactory();
    private static FeatureType type;

    public MergeTest() {
        super("merge");
    }

    /**
     * Merge Test with 4 FeatureCollection. 1 - base FeatureCollection 2 - FeatureCollection with same FeatureType as
     * base 3 - FeatureCollection with some attribute like base and with conversion needed 4 - FeatureCollection with
     * none attribute like base
     */
    @Test
    public void testMerge() throws ProcessException, NoSuchIdentifierException, FactoryException {

        // Inputs
        final FeatureCollection featureList1 = buildFeatureList1();
        final FeatureCollection featureList2 = buildFeatureList2();
        final FeatureCollection featureList3 = buildFeatureList3();
        final FeatureCollection featureList4 = buildFeatureList4();

        FeatureCollection[] FCList = new FeatureCollection[4];
        FCList[0] = featureList1;
        FCList[1] = featureList2;
        FCList[2] = featureList3;
        FCList[3] = featureList4;

        // Process
        ProcessDescriptor desc = ProcessFinder.getProcessDescriptor("vector", "merge");

        ParameterValueGroup in = desc.getInputDescriptor().createValue();
        in.parameter("features_in").setValue(FCList);
        org.geotoolkit.process.Process proc = desc.createProcess(in);

        //Features out
        final FeatureCollection featureListOut = (FeatureCollection) proc.call().parameter("feature_out").getValue();
        //Expected Features out
        final FeatureCollection featureListResult = buildResultList();
        compare(featureListResult,featureListOut);
    }

    private static FeatureType createSimpleType() throws NoSuchAuthorityCodeException, FactoryException {
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("MergeTest");
        ftb.addAttribute(String.class).setName(AttributeConvention.IDENTIFIER_PROPERTY);
        ftb.addAttribute(String.class).setName("name");
        ftb.addAttribute(Polygon.class).setName("geom1").setCRS(CRS.forCode("EPSG:3395")).addRole(AttributeRole.DEFAULT_GEOMETRY);
        ftb.addAttribute(Geometry.class).setName("geom2").setCRS(CRS.forCode("EPSG:3395"));
        return ftb.build();
    }

    private static FeatureType createSimpleType2() throws NoSuchAuthorityCodeException, FactoryException {
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("Merge2Test");
        ftb.addAttribute(String.class).setName(AttributeConvention.IDENTIFIER_PROPERTY);
        ftb.addAttribute(Integer.class).setName("name");
        ftb.addAttribute(Geometry.class).setName("geom1").setCRS(CRS.forCode("EPSG:3395")).addRole(AttributeRole.DEFAULT_GEOMETRY);
        ftb.addAttribute(String.class).setName("color");
        ftb.addAttribute(Geometry.class).setName("geom3").setCRS(CRS.forCode("EPSG:3395"));
        return ftb.build();
    }

    private static FeatureType createSimpleType3() throws NoSuchAuthorityCodeException, FactoryException {
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("Merge3Test");
        ftb.addAttribute(String.class).setName(AttributeConvention.IDENTIFIER_PROPERTY);
        ftb.addAttribute(Integer.class).setName("height");
        ftb.addAttribute(Geometry.class).setName("geom4").setCRS(CRS.forCode("EPSG:3395")).addRole(AttributeRole.DEFAULT_GEOMETRY);
        ftb.addAttribute(String.class).setName("color");
        return ftb.build();
    }

    private static FeatureCollection buildFeatureList1() throws FactoryException {

        type = createSimpleType();

        final FeatureCollection featureList = FeatureStoreUtilities.collection("", type);


        Feature myFeature1 = type.newInstance();
        LinearRing ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(3.0, 3.0),
                    new Coordinate(3.0, 4.0),
                    new Coordinate(4.0, 4.0),
                    new Coordinate(4.0, 3.0),
                    new Coordinate(3.0, 3.0)
                });
        myFeature1.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "id-01");
        myFeature1.setPropertyValue("name", "feature1");
        myFeature1.setPropertyValue("geom1", geometryFactory.createPolygon(ring, null));
        myFeature1.setPropertyValue("geom2", geometryFactory.createPoint(new Coordinate(3.5, 3.5)));
        featureList.add(myFeature1);

        Feature myFeature2 = type.newInstance();
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
                    new Coordinate(3.0, 6.0),
                    new Coordinate(4.0, 7.0),
                    new Coordinate(5.5, 6.5)
                });
        myFeature2.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "id-02");
        myFeature2.setPropertyValue("name", "feature2");
        myFeature2.setPropertyValue("geom1", geometryFactory.createPolygon(ring, null));
        myFeature2.setPropertyValue("geom2", multPt);
        featureList.add(myFeature2);

        Feature myFeature3 = type.newInstance();
        ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(9.0, 4.0),
                    new Coordinate(9.0, 5.0),
                    new Coordinate(11.0, 5.0),
                    new Coordinate(11.0, 4.0),
                    new Coordinate(9.0, 4.0)
                });
        LineString line = geometryFactory.createLineString(
                new Coordinate[]{
                    new Coordinate(7.0, 0.0),
                    new Coordinate(9.0, 3.0)
                });
        myFeature3.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "id-03");
        myFeature3.setPropertyValue("name", "feature3");
        myFeature3.setPropertyValue("geom1", geometryFactory.createPolygon(ring, null));
        myFeature3.setPropertyValue("geom2", line);
        featureList.add(myFeature3);

        return featureList;
    }

    private static FeatureCollection buildFeatureList2() throws FactoryException {

        type = createSimpleType();
        final FeatureCollection featureList = FeatureStoreUtilities.collection("", type);


        Feature myFeature1 = type.newInstance();
        LinearRing ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(1.0, 4.0),
                    new Coordinate(1.0, 5.0),
                    new Coordinate(2.0, 5.0),
                    new Coordinate(2.0, 4.0),
                    new Coordinate(1.0, 4.0)
                });
        MultiPoint multPt = geometryFactory.createMultiPoint(
                new Coordinate[]{
                    new Coordinate(1.0, 6.0), //nothing
                    new Coordinate(3.0, 6.0), //intersection with a point
                    new Coordinate(3.5, 3.5) //intersection with a polygon
                });
        myFeature1.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "id-11");
        myFeature1.setPropertyValue("name", "feature11");
        myFeature1.setPropertyValue("geom1", geometryFactory.createPolygon(ring, null));
        myFeature1.setPropertyValue("geom2", multPt);
        featureList.add(myFeature1);

        Feature myFeature2 = type.newInstance();
        ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(4.0, 2.0),
                    new Coordinate(4.0, 5.0),
                    new Coordinate(7.0, 5.0),
                    new Coordinate(7.0, 2.0),
                    new Coordinate(4.0, 2.0)
                });
        LineString line = geometryFactory.createLineString(
                new Coordinate[]{
                    new Coordinate(8.0, 4.5),
                    new Coordinate(11.0, 4.5)
                });
        myFeature2.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "id-12");
        myFeature2.setPropertyValue("name", "feature12");
        myFeature2.setPropertyValue("geom1", geometryFactory.createPolygon(ring, null));
        myFeature2.setPropertyValue("geom2", line);
        featureList.add(myFeature2);

        Feature myFeature3 = type.newInstance();
        ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(0.0, 0.0),
                    new Coordinate(0.0, 8.0),
                    new Coordinate(10.0, 8.0),
                    new Coordinate(10.0, 0.0),
                    new Coordinate(0.0, 0.0)
                });
        myFeature3.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "id-13");
        myFeature3.setPropertyValue("name", "feature13");
        myFeature3.setPropertyValue("geom1", geometryFactory.createPolygon(ring, null));
        myFeature3.setPropertyValue("geom2", null);
        featureList.add(myFeature3);

        return featureList;
    }

    private static FeatureCollection buildFeatureList3() throws FactoryException {

        type = createSimpleType2();

        final FeatureCollection featureList = FeatureStoreUtilities.collection("", type);


        Feature myFeature1 = type.newInstance();
        LinearRing ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(1.0, 4.0),
                    new Coordinate(1.0, 5.0),
                    new Coordinate(2.0, 5.0),
                    new Coordinate(2.0, 4.0),
                    new Coordinate(1.0, 4.0)
                });
        MultiPoint multPt = geometryFactory.createMultiPoint(
                new Coordinate[]{
                    new Coordinate(1.0, 6.0), //nothing
                    new Coordinate(3.0, 6.0), //intersection with a point
                    new Coordinate(3.5, 3.5) //intersection with a polygon
                });
        myFeature1.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "id-21");
        myFeature1.setPropertyValue("name", 21);
        myFeature1.setPropertyValue("color", "grey");
        myFeature1.setPropertyValue("geom1", geometryFactory.createPolygon(ring, null));
        myFeature1.setPropertyValue("geom3", multPt);
        featureList.add(myFeature1);

        Feature myFeature2 = type.newInstance();
        ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(4.0, 2.0),
                    new Coordinate(4.0, 5.0),
                    new Coordinate(7.0, 5.0),
                    new Coordinate(7.0, 2.0),
                    new Coordinate(4.0, 2.0)
                });
        LineString line = geometryFactory.createLineString(
                new Coordinate[]{
                    new Coordinate(8.0, 4.5),
                    new Coordinate(11.0, 4.5)
                });
        myFeature2.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "id-22");
        myFeature2.setPropertyValue("name", 22);
        myFeature2.setPropertyValue("color", "red");
        myFeature2.setPropertyValue("geom1", geometryFactory.createPolygon(ring, null));
        myFeature2.setPropertyValue("geom3", line);
        featureList.add(myFeature2);

        Feature myFeature3 = type.newInstance();
        ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(0.0, 0.0),
                    new Coordinate(0.0, 8.0),
                    new Coordinate(10.0, 8.0),
                    new Coordinate(10.0, 0.0),
                    new Coordinate(0.0, 0.0)
                });
        myFeature3.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "id-23");
        myFeature3.setPropertyValue("name", 23);
        myFeature3.setPropertyValue("color", "blue");
        myFeature3.setPropertyValue("geom1", geometryFactory.createPolygon(ring, null));
        myFeature3.setPropertyValue("geom3", null);
        featureList.add(myFeature3);

        return featureList;
    }

    private static FeatureCollection buildFeatureList4() throws FactoryException {

        type = createSimpleType3();

        final FeatureCollection featureList = FeatureStoreUtilities.collection("", type);


        Feature myFeature1 = type.newInstance();
        LinearRing ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(1.0, 4.0),
                    new Coordinate(1.0, 5.0),
                    new Coordinate(2.0, 5.0),
                    new Coordinate(2.0, 4.0),
                    new Coordinate(1.0, 4.0)
                });
        myFeature1.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "id-31");
        myFeature1.setPropertyValue("height", 31);
        myFeature1.setPropertyValue("color", "grey");
        myFeature1.setPropertyValue("geom4", geometryFactory.createPolygon(ring, null));
        featureList.add(myFeature1);

        Feature myFeature2 = type.newInstance();
        ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(4.0, 2.0),
                    new Coordinate(4.0, 5.0),
                    new Coordinate(7.0, 5.0),
                    new Coordinate(7.0, 2.0),
                    new Coordinate(4.0, 2.0)
                });
        myFeature2.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "id-32");
        myFeature2.setPropertyValue("height", 32);
        myFeature2.setPropertyValue("color", "red");
        myFeature2.setPropertyValue("geom4", geometryFactory.createPolygon(ring, null));
        featureList.add(myFeature2);

        Feature myFeature3 = type.newInstance();
        ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(0.0, 0.0),
                    new Coordinate(0.0, 8.0),
                    new Coordinate(10.0, 8.0),
                    new Coordinate(10.0, 0.0),
                    new Coordinate(0.0, 0.0)
                });
        myFeature3.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "id-33");
        myFeature3.setPropertyValue("height", 33);
        myFeature3.setPropertyValue("color", "blue");
        myFeature3.setPropertyValue("geom4", geometryFactory.createPolygon(ring, null));
        featureList.add(myFeature3);

        return featureList;
    }

    private static FeatureCollection buildResultList() throws FactoryException {


        type = createSimpleType();

        final FeatureCollection featureList = FeatureStoreUtilities.collection("", type);


        Feature myFeature1 = type.newInstance();
        LinearRing ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(3.0, 3.0),
                    new Coordinate(3.0, 4.0),
                    new Coordinate(4.0, 4.0),
                    new Coordinate(4.0, 3.0),
                    new Coordinate(3.0, 3.0)
                });
        myFeature1.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "id-01");
        myFeature1.setPropertyValue("name", "feature1");
        myFeature1.setPropertyValue("geom1", geometryFactory.createPolygon(ring, null));
        myFeature1.setPropertyValue("geom2", geometryFactory.createPoint(new Coordinate(3.5, 3.5)));
        featureList.add(myFeature1);

        Feature myFeature2 = type.newInstance();
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
                    new Coordinate(3.0, 6.0),
                    new Coordinate(4.0, 7.0),
                    new Coordinate(5.5, 6.5)
                });
        myFeature2.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "id-02");
        myFeature2.setPropertyValue("name", "feature2");
        myFeature2.setPropertyValue("geom1", geometryFactory.createPolygon(ring, null));
        myFeature2.setPropertyValue("geom2", multPt);
        featureList.add(myFeature2);

        Feature myFeature3 = type.newInstance();
        ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(9.0, 4.0),
                    new Coordinate(9.0, 5.0),
                    new Coordinate(11.0, 5.0),
                    new Coordinate(11.0, 4.0),
                    new Coordinate(9.0, 4.0)
                });
        LineString line = geometryFactory.createLineString(
                new Coordinate[]{
                    new Coordinate(7.0, 0.0),
                    new Coordinate(9.0, 3.0)
                });
        myFeature3.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "id-03");
        myFeature3.setPropertyValue("name", "feature3");
        myFeature3.setPropertyValue("geom1", geometryFactory.createPolygon(ring, null));
        myFeature3.setPropertyValue("geom2", line);
        featureList.add(myFeature3);

        ///////////////////////////////////////////////////////////// 2nd FeatureCollection
        myFeature1 = type.newInstance();
        ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(1.0, 4.0),
                    new Coordinate(1.0, 5.0),
                    new Coordinate(2.0, 5.0),
                    new Coordinate(2.0, 4.0),
                    new Coordinate(1.0, 4.0)
                });
        multPt = geometryFactory.createMultiPoint(
                new Coordinate[]{
                    new Coordinate(1.0, 6.0),
                    new Coordinate(3.0, 6.0),
                    new Coordinate(3.5, 3.5)
                });
        myFeature1.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "id-11");
        myFeature1.setPropertyValue("name", "feature11");
        myFeature1.setPropertyValue("geom1", geometryFactory.createPolygon(ring, null));
        myFeature1.setPropertyValue("geom2", multPt);
        featureList.add(myFeature1);

        myFeature2 = type.newInstance();
        ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(4.0, 2.0),
                    new Coordinate(4.0, 5.0),
                    new Coordinate(7.0, 5.0),
                    new Coordinate(7.0, 2.0),
                    new Coordinate(4.0, 2.0)
                });
        line = geometryFactory.createLineString(
                new Coordinate[]{
                    new Coordinate(8.0, 4.5),
                    new Coordinate(11.0, 4.5)
                });
        myFeature2.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "id-12");
        myFeature2.setPropertyValue("name", "feature12");
        myFeature2.setPropertyValue("geom1", geometryFactory.createPolygon(ring, null));
        myFeature2.setPropertyValue("geom2", line);
        featureList.add(myFeature2);

        myFeature3 = type.newInstance();
        ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(0.0, 0.0),
                    new Coordinate(0.0, 8.0),
                    new Coordinate(10.0, 8.0),
                    new Coordinate(10.0, 0.0),
                    new Coordinate(0.0, 0.0)
                });
        myFeature3.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "id-13");
        myFeature3.setPropertyValue("name", "feature13");
        myFeature3.setPropertyValue("geom1", geometryFactory.createPolygon(ring, null));
        myFeature3.setPropertyValue("geom2", null);
        featureList.add(myFeature3);
        ////////////////////////////////////////////////////////////////////// 3rd FeatureCollection
        myFeature1 = type.newInstance();
        ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(1.0, 4.0),
                    new Coordinate(1.0, 5.0),
                    new Coordinate(2.0, 5.0),
                    new Coordinate(2.0, 4.0),
                    new Coordinate(1.0, 4.0)
                });
        myFeature1.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "id-21");
        myFeature1.setPropertyValue("name", "21");
        myFeature1.setPropertyValue("geom1", geometryFactory.createPolygon(ring, null));
        myFeature1.setPropertyValue("geom2", null);
        featureList.add(myFeature1);

        myFeature2 = type.newInstance();
        ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(4.0, 2.0),
                    new Coordinate(4.0, 5.0),
                    new Coordinate(7.0, 5.0),
                    new Coordinate(7.0, 2.0),
                    new Coordinate(4.0, 2.0)
                });
        myFeature2.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "id-22");
        myFeature2.setPropertyValue("name", "22");
        myFeature2.setPropertyValue("geom1", geometryFactory.createPolygon(ring, null));
        myFeature2.setPropertyValue("geom2", null);
        featureList.add(myFeature2);

        myFeature3 = type.newInstance();
        ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(0.0, 0.0),
                    new Coordinate(0.0, 8.0),
                    new Coordinate(10.0, 8.0),
                    new Coordinate(10.0, 0.0),
                    new Coordinate(0.0, 0.0)
                });
        myFeature3.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "id-23");
        myFeature3.setPropertyValue("name", "23");
        myFeature3.setPropertyValue("geom1", geometryFactory.createPolygon(ring, null));
        myFeature3.setPropertyValue("geom2", null);
        featureList.add(myFeature3);
        ////////////////////////////////////////////////////////////////////// 4th FeatureCollection

        myFeature1 = type.newInstance();
        myFeature1.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "id-31");
        myFeature1.setPropertyValue("name", null);
        myFeature1.setPropertyValue("geom1", null);
        myFeature1.setPropertyValue("geom2", null);
        featureList.add(myFeature1);

        myFeature2 = type.newInstance();
        myFeature2.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "id-32");
        myFeature2.setPropertyValue("name", null);
        myFeature2.setPropertyValue("geom1", null);
        myFeature2.setPropertyValue("geom2", null);
        featureList.add(myFeature2);

        myFeature3 = type.newInstance();
        myFeature3.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "id-33");
        myFeature3.setPropertyValue("name", null);
        myFeature3.setPropertyValue("geom1", null);
        myFeature3.setPropertyValue("geom2", null);
        featureList.add(myFeature3);


        return featureList;
    }
}
