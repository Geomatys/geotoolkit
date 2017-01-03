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
package org.geotoolkit.processing.vector.regroup;

import org.geotoolkit.process.ProcessException;
import org.geotoolkit.processing.vector.AbstractProcessTest;
import org.opengis.util.NoSuchIdentifierException;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import org.apache.sis.feature.builder.AttributeRole;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.internal.feature.AttributeConvention;

import org.geotoolkit.data.FeatureStoreUtilities;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessFinder;
import org.apache.sis.referencing.CRS;
import org.geotoolkit.processing.GeotkProcessingRegistry;

import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.util.FactoryException;

import org.junit.Test;
import static org.junit.Assert.*;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;

/**
 * JUnit test of Regroup process
 *
 * @author Quentin Boileau
 * @module
 */
public class RegroupTest extends AbstractProcessTest {

    private static final GeometryFactory GF = new GeometryFactory();
    private static FeatureType type;

    public RegroupTest() {
        super("vector:regroup");
    }

    @Test
    public void testRegroupDefaultGeometry() throws ProcessException, NoSuchIdentifierException, FactoryException {

        // Inputs
        final FeatureCollection featureList = buildFeatureList();
        // Process
        ProcessDescriptor desc = ProcessFinder.getProcessDescriptor(GeotkProcessingRegistry.NAME,"vector:regroup");

        ParameterValueGroup in = desc.getInputDescriptor().createValue();
        in.parameter("feature_in").setValue(featureList);
        in.parameter("regroup_attribute").setValue("height");
        org.geotoolkit.process.Process proc = desc.createProcess(in);

        //Features out
        final FeatureCollection featureListOut = (FeatureCollection) proc.call().parameter("feature_out").getValue();

        //Expected Features out
        final FeatureCollection featureListResult = buildResultList1();
        compare(featureListResult,featureListOut);
    }

    @Test
    public void testRegroupGeometrySelected() throws ProcessException, NoSuchIdentifierException, FactoryException {

        // Inputs
        final FeatureCollection featureList = buildFeatureList();
        // Process
        ProcessDescriptor desc = ProcessFinder.getProcessDescriptor(GeotkProcessingRegistry.NAME,"vector:regroup");

        ParameterValueGroup in = desc.getInputDescriptor().createValue();
        in.parameter("feature_in").setValue(featureList);
        in.parameter("regroup_attribute").setValue("type");
        in.parameter("geometry_name").setValue("geom2");
        org.geotoolkit.process.Process proc = desc.createProcess(in);

        //Features out
        final FeatureCollection featureListOut = (FeatureCollection) proc.call().parameter("feature_out").getValue();

        //Expected Features out
        final FeatureCollection featureListResult = buildResultList2();
        compare(featureListResult,featureListOut);
    }

    private static FeatureType createSimpleType() throws NoSuchAuthorityCodeException, FactoryException {
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("RegroupTest");
        ftb.addAttribute(String.class).setName(AttributeConvention.IDENTIFIER_PROPERTY);
        ftb.addAttribute(String.class).setName("type");
        ftb.addAttribute(Geometry.class).setName("geom1").setCRS(CRS.forCode("EPSG:3395")).addRole(AttributeRole.DEFAULT_GEOMETRY);
        ftb.addAttribute(Geometry.class).setName("geom2").setCRS(CRS.forCode("EPSG:3395"));
        ftb.addAttribute(String.class).setName("color");
        ftb.addAttribute(Integer.class).setName("height");
        return ftb.build();
    }

    private static FeatureType createSimpleResultType() throws NoSuchAuthorityCodeException, FactoryException {
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("RegroupTest");
        ftb.addAttribute(String.class).setName(AttributeConvention.IDENTIFIER_PROPERTY);
        ftb.addAttribute(Geometry.class).setName("geom1").setCRS(CRS.forCode("EPSG:3395")).addRole(AttributeRole.DEFAULT_GEOMETRY);
        ftb.addAttribute(Integer.class).setName("height");
        return ftb.build();
    }

    private static FeatureType createSimpleResultType2() throws NoSuchAuthorityCodeException, FactoryException {
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("RegroupTest");
        ftb.addAttribute(String.class).setName(AttributeConvention.IDENTIFIER_PROPERTY);
        ftb.addAttribute(String.class).setName("type");
        ftb.addAttribute(Geometry.class).setName("geom2").setCRS(CRS.forCode("EPSG:3395")).addRole(AttributeRole.DEFAULT_GEOMETRY);
        return ftb.build();
    }

    private static FeatureCollection buildFeatureList() throws FactoryException {

        type = createSimpleType();

        final FeatureCollection featureList = FeatureStoreUtilities.collection("", type);


        Feature myFeature1 = type.newInstance();
        LinearRing ring = GF.createLinearRing(
                new Coordinate[]{
                    new Coordinate(3.0, 3.0),
                    new Coordinate(3.0, 4.0),
                    new Coordinate(4.0, 4.0),
                    new Coordinate(4.0, 3.0),
                    new Coordinate(3.0, 3.0)
                });
        myFeature1.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "id-01");
        myFeature1.setPropertyValue("color", "grey");
        myFeature1.setPropertyValue("height", 9);
        myFeature1.setPropertyValue("type", "church");
        myFeature1.setPropertyValue("geom1", GF.createPolygon(ring, null));
        myFeature1.setPropertyValue("geom2", GF.createPoint(new Coordinate(3.5, 3.5)));
        featureList.add(myFeature1);

        Feature myFeature2 = type.newInstance();
        ring = GF.createLinearRing(
                new Coordinate[]{
                    new Coordinate(5.0, 6.0),
                    new Coordinate(5.0, 7.0),
                    new Coordinate(6.0, 7.0),
                    new Coordinate(6.0, 6.0),
                    new Coordinate(5.0, 6.0)
                });
        MultiPoint multPt = GF.createMultiPoint(
                new Coordinate[]{
                    new Coordinate(5.0, 4.0),
                    new Coordinate(3.0, 6.0),
                    new Coordinate(4.0, 7.0),
                    new Coordinate(5.5, 6.5)
                });
        myFeature2.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "id-02");
        myFeature2.setPropertyValue("color", "blue");
        myFeature2.setPropertyValue("height", 3);
        myFeature2.setPropertyValue("type", "office");
        myFeature2.setPropertyValue("geom1", GF.createPolygon(ring, null));
        myFeature2.setPropertyValue("geom2", multPt);
        featureList.add(myFeature2);

        Feature myFeature3 = type.newInstance();
        ring = GF.createLinearRing(
                new Coordinate[]{
                    new Coordinate(9.0, 4.0),
                    new Coordinate(9.0, 5.0),
                    new Coordinate(11.0, 5.0),
                    new Coordinate(11.0, 4.0),
                    new Coordinate(9.0, 4.0)
                });
        LineString line = GF.createLineString(
                new Coordinate[]{
                    new Coordinate(7.0, 0.0),
                    new Coordinate(9.0, 3.0)
                });
        myFeature3.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "id-03");
        myFeature3.setPropertyValue("color", "black");
        myFeature3.setPropertyValue("height", 2);
        myFeature3.setPropertyValue("type", "office");
        myFeature3.setPropertyValue("geom1", GF.createPolygon(ring, null));
        myFeature3.setPropertyValue("geom2", line);
        featureList.add(myFeature3);

        Feature myFeature4 = type.newInstance();
        ring = GF.createLinearRing(
                new Coordinate[]{
                    new Coordinate(2.0, 2.0),
                    new Coordinate(2.0, 3.0),
                    new Coordinate(3.0, 3.0),
                    new Coordinate(3.0, 2.0),
                    new Coordinate(2.0, 2.0)
                });
        myFeature4.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "id-04");
        myFeature4.setPropertyValue("color", "yellow");
        myFeature4.setPropertyValue("height", 2);
        myFeature4.setPropertyValue("type", "post office");
        myFeature4.setPropertyValue("geom1", GF.createPolygon(ring, null));
        myFeature4.setPropertyValue("geom2", GF.createPoint(new Coordinate(10, 5)));
        featureList.add(myFeature4);

        Feature myFeature5 = type.newInstance();
        ring = GF.createLinearRing(
                new Coordinate[]{
                    new Coordinate(6.0, 7.0),
                    new Coordinate(6.0, 8.0),
                    new Coordinate(7.0, 8.0),
                    new Coordinate(7.0, 7.0),
                    new Coordinate(6.0, 7.0)
                });
        line = GF.createLineString(
                new Coordinate[]{
                    new Coordinate(8.0, 0.0),
                    new Coordinate(5.0, 3.0)
                });
        myFeature5.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "id-05");
        myFeature5.setPropertyValue("color", "yellow");
        myFeature5.setPropertyValue("height", 9);
        myFeature5.setPropertyValue("type", "office");
        myFeature5.setPropertyValue("geom1", GF.createPolygon(ring, null));
        myFeature5.setPropertyValue("geom2", line);
        featureList.add(myFeature5);

        Feature myFeature6 = type.newInstance();
        ring = GF.createLinearRing(
                new Coordinate[]{
                    new Coordinate(15.0, 10.0),
                    new Coordinate(15.0, 11.0),
                    new Coordinate(16.0, 11.0),
                    new Coordinate(16.0, 10.0),
                    new Coordinate(15.0, 10.0)
                });
        LinearRing ring2 = GF.createLinearRing(
                new Coordinate[]{
                    new Coordinate(8.0, 0.0),
                    new Coordinate(9.0, 6.0),
                    new Coordinate(10.0, 2.0),
                    new Coordinate(8.0, 0.0)
                });
        myFeature6.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "id-06");
        myFeature6.setPropertyValue("color", "black");
        myFeature6.setPropertyValue("height", 2);
        myFeature6.setPropertyValue("type", "church");
        myFeature6.setPropertyValue("geom1", GF.createPolygon(ring, null));
        myFeature6.setPropertyValue("geom2", GF.createPolygon(ring2, null));
        featureList.add(myFeature6);

        return featureList;
    }

    private static FeatureCollection buildResultList1() throws FactoryException {

        type = createSimpleResultType();

        final FeatureCollection featureList = FeatureStoreUtilities.collection("", type);


        Feature myFeature1 = type.newInstance();
        LinearRing ring = GF.createLinearRing(
                new Coordinate[]{
                    new Coordinate(5.0, 6.0),
                    new Coordinate(5.0, 7.0),
                    new Coordinate(6.0, 7.0),
                    new Coordinate(6.0, 6.0),
                    new Coordinate(5.0, 6.0)
                });
        myFeature1.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "height-3");
        myFeature1.setPropertyValue("height", 3);
        myFeature1.setPropertyValue("geom1", GF.createPolygon(ring, null));
        featureList.add(myFeature1);

        Feature myFeature2 = type.newInstance();
        LinearRing ring1 = GF.createLinearRing(
                new Coordinate[]{
                    new Coordinate(6.0, 7.0),
                    new Coordinate(6.0, 8.0),
                    new Coordinate(7.0, 8.0),
                    new Coordinate(7.0, 7.0),
                    new Coordinate(6.0, 7.0)
                });
        LinearRing ring2 = GF.createLinearRing(
                new Coordinate[]{
                    new Coordinate(3.0, 3.0),
                    new Coordinate(3.0, 4.0),
                    new Coordinate(4.0, 4.0),
                    new Coordinate(4.0, 3.0),
                    new Coordinate(3.0, 3.0)
                });
        Polygon poly1 = GF.createPolygon(ring1, null);
        Polygon poly2 = GF.createPolygon(ring2, null);
        myFeature2.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "height-9");
        myFeature2.setPropertyValue("height", 9);
        myFeature2.setPropertyValue("geom1", GF.createMultiPolygon(new Polygon[]{poly1, poly2}));
        featureList.add(myFeature2);

        Feature myFeature3 = type.newInstance();
        ring = GF.createLinearRing(
                new Coordinate[]{
                    new Coordinate(15.0, 10.0),
                    new Coordinate(15.0, 11.0),
                    new Coordinate(16.0, 11.0),
                    new Coordinate(16.0, 10.0),
                    new Coordinate(15.0, 10.0)
                });
        ring1 = GF.createLinearRing(
                new Coordinate[]{
                    new Coordinate(9.0, 4.0),
                    new Coordinate(9.0, 5.0),
                    new Coordinate(11.0, 5.0),
                    new Coordinate(11.0, 4.0),
                    new Coordinate(9.0, 4.0)
                });
        ring2 = GF.createLinearRing(
                new Coordinate[]{
                    new Coordinate(2.0, 2.0),
                    new Coordinate(2.0, 3.0),
                    new Coordinate(3.0, 3.0),
                    new Coordinate(3.0, 2.0),
                    new Coordinate(2.0, 2.0)
                });
        poly1 = GF.createPolygon(ring, null);
        poly2 = GF.createPolygon(ring1, null);
        Polygon poly3 = GF.createPolygon(ring2, null);
        myFeature3.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "height-2");
        myFeature3.setPropertyValue("height", 2);
        myFeature3.setPropertyValue("geom1", GF.createMultiPolygon(new Polygon[]{poly1, poly2, poly3}));
        featureList.add(myFeature3);




        return featureList;
    }

    private static FeatureCollection buildResultList2() throws FactoryException {

        type = createSimpleResultType2();

        final FeatureCollection featureList = FeatureStoreUtilities.collection("", type);


        Feature myFeature1 = type.newInstance();
        Point pt1 = GF.createPoint(new Coordinate(3, 6));
        MultiPoint multPt = GF.createMultiPoint(
                new Coordinate[]{
                    new Coordinate(5.0, 4.0),
                    new Coordinate(3.0, 6.0),
                    new Coordinate(4.0, 7.0),
                    new Coordinate(5.5, 6.5)
                });
        LineString line1 = GF.createLineString(
                new Coordinate[]{
                    new Coordinate(8.0, 0.0),
                    new Coordinate(5.0, 3.0)
                });
        LineString line3 = GF.createLineString(
                new Coordinate[]{
                    new Coordinate(7.0, 0.0),
                    new Coordinate(9.0, 3.0)
                });
        GeometryCollection collec = GF.createGeometryCollection(new Geometry[]{line1, multPt, line3});
        myFeature1.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "type-office");
        myFeature1.setPropertyValue("type", "office");
        myFeature1.setPropertyValue("geom2", collec);
        featureList.add(myFeature1);

        Feature myFeature2 = type.newInstance();
        pt1 = GF.createPoint(new Coordinate(3.5, 3.5));
        LinearRing ring = GF.createLinearRing(
                new Coordinate[]{
                    new Coordinate(8.0, 0.0),
                    new Coordinate(9.0, 6.0),
                    new Coordinate(10.0, 2.0),
                    new Coordinate(8.0, 0.0)
                });
        Polygon poly = GF.createPolygon(ring, null);
        myFeature2.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "type-church");
        myFeature2.setPropertyValue("type", "church");
        myFeature2.setPropertyValue("geom2", GF.createGeometryCollection(new Geometry[]{poly, pt1}));
        featureList.add(myFeature2);

        Feature myFeature3 = type.newInstance();
        myFeature3.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "type-post office");
        myFeature3.setPropertyValue("type", "post office");
        myFeature3.setPropertyValue("geom2", GF.createPoint(new Coordinate(10, 5)));
        featureList.add(myFeature3);

        return featureList;
    }
}
