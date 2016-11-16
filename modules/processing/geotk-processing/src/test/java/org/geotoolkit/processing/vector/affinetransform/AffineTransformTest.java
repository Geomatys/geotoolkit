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
package org.geotoolkit.processing.vector.affinetransform;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiPoint;
import java.awt.geom.AffineTransform;
import org.apache.sis.feature.builder.AttributeRole;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.internal.feature.AttributeConvention;

import org.geotoolkit.data.FeatureStoreUtilities;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.process.ProcessFinder;
import org.geotoolkit.processing.vector.AbstractProcessTest;
import org.apache.sis.referencing.CRS;

import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.util.FactoryException;

import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.util.NoSuchIdentifierException;


/**
 * JUnit test of AffineTransform process
 * @author Quentin Boileau
 * @module
 */
@Ignore //TODO : transform is now handle with operations, rewrite whole test
public class AffineTransformTest extends AbstractProcessTest {

    private static final GeometryFactory GF = new GeometryFactory();
    private static FeatureType type;

    public AffineTransformTest() {
        super("affinetransform");
    }

    @Test
    public void testAffineTransform() throws ProcessException, NoSuchIdentifierException, FactoryException {

        // Inputs
        final FeatureCollection featureList = buildFeatureList();
        final AffineTransform transform = new AffineTransform();
        transform.setToTranslation(100, 100);

        // Process
        final ProcessDescriptor desc = ProcessFinder.getProcessDescriptor("vector", "affinetransform");

        final ParameterValueGroup in = desc.getInputDescriptor().createValue();
        in.parameter("feature_in").setValue(featureList);
        in.parameter("transform_in").setValue(transform);
        final org.geotoolkit.process.Process proc = desc.createProcess(in);

        //Features out
        final FeatureCollection featureListOut = (FeatureCollection) proc.call().parameter("feature_out").getValue();

        //Expected Features out
        final FeatureCollection featureListResult = buildResultList();
        compare(featureListResult,featureListOut);
    }

    private static FeatureType createSimpleType() throws NoSuchAuthorityCodeException, FactoryException {
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("AffineTransformTest");
        ftb.addAttribute(String.class).setName(AttributeConvention.IDENTIFIER_PROPERTY);
        ftb.addAttribute(String.class).setName("type");
        ftb.addAttribute(Geometry.class).setName("geom1").setCRS(CRS.forCode("EPSG:3395")).addRole(AttributeRole.DEFAULT_GEOMETRY);
        ftb.addAttribute(Geometry.class).setName("geom2").setCRS(CRS.forCode("EPSG:3395"));
        ftb.addAttribute(String.class).setName("color");
        ftb.addAttribute(Integer.class).setName("height");
        return ftb.build();
    }

    private static FeatureCollection buildFeatureList() throws FactoryException {

        type = createSimpleType();

        final FeatureCollection featureList = FeatureStoreUtilities.collection("", type);


        Feature myFeature1;
        LinearRing ring = GF.createLinearRing(
                new Coordinate[]{
                    new Coordinate(3.0, 3.0),
                    new Coordinate(3.0, 4.0),
                    new Coordinate(4.0, 4.0),
                    new Coordinate(4.0, 3.0),
                    new Coordinate(3.0, 3.0)
                });
        myFeature1 = type.newInstance();
        myFeature1.setPropertyValue("@identifier", "id-01");
        myFeature1.setPropertyValue("color", "grey");
        myFeature1.setPropertyValue("height", 9);
        myFeature1.setPropertyValue("type", "church");
        myFeature1.setPropertyValue("geom1", GF.createPolygon(ring, null));
        myFeature1.setPropertyValue("geom2", GF.createPoint(new Coordinate(3.5, 3.5)));
        featureList.add(myFeature1);

        Feature myFeature2;
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
        myFeature2 = type.newInstance();
        myFeature2.setPropertyValue("@identifier", "id-02");
        myFeature2.setPropertyValue("color", "blue");
        myFeature2.setPropertyValue("height", 3);
        myFeature2.setPropertyValue("type", "office");
        myFeature2.setPropertyValue("geom1", GF.createPolygon(ring, null));
        myFeature2.setPropertyValue("geom2", multPt);
        featureList.add(myFeature2);

        Feature myFeature3;
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
        myFeature3 = type.newInstance();
        myFeature3.setPropertyValue("@identifier", "id-03");
        myFeature3.setPropertyValue("color", "black");
        myFeature3.setPropertyValue("height", 2);
        myFeature3.setPropertyValue("type", "office");
        myFeature3.setPropertyValue("geom1", GF.createPolygon(ring, null));
        myFeature3.setPropertyValue("geom2", line);
        featureList.add(myFeature3);

        Feature myFeature4;
        ring = GF.createLinearRing(
                new Coordinate[]{
                    new Coordinate(2.0, 2.0),
                    new Coordinate(2.0, 3.0),
                    new Coordinate(3.0, 3.0),
                    new Coordinate(3.0, 2.0),
                    new Coordinate(2.0, 2.0)
                });
        myFeature4 = type.newInstance();
        myFeature4.setPropertyValue("@identifier", "id-04");
        myFeature4.setPropertyValue("color", "yellow");
        myFeature4.setPropertyValue("height", 2);
        myFeature4.setPropertyValue("type", "post office");
        myFeature4.setPropertyValue("geom1", GF.createPolygon(ring, null));
        myFeature4.setPropertyValue("geom2", GF.createPoint(new Coordinate(10, 5)));
        featureList.add(myFeature4);

        Feature myFeature5;
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
        myFeature5 = type.newInstance();
        myFeature5.setPropertyValue("@identifier", "id-05");
        myFeature5.setPropertyValue("color", "yellow");
        myFeature5.setPropertyValue("height", 9);
        myFeature5.setPropertyValue("type", "office");
        myFeature5.setPropertyValue("geom1", GF.createPolygon(ring, null));
        myFeature5.setPropertyValue("geom2", line);
        featureList.add(myFeature5);

        Feature myFeature6;
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
        myFeature6 = type.newInstance();
        myFeature6.setPropertyValue("@identifier", "id-06");
        myFeature6.setPropertyValue("color", "black");
        myFeature6.setPropertyValue("height", 2);
        myFeature6.setPropertyValue("type", "church");
        myFeature6.setPropertyValue("geom1", GF.createPolygon(ring, null));
        myFeature6.setPropertyValue("geom2",GF.createPolygon(ring2, null));
        featureList.add(myFeature6);

        return featureList;
    }


    private static FeatureCollection buildResultList() throws FactoryException {

        type = createSimpleType();
        final FeatureCollection featureList = FeatureStoreUtilities.collection("", type);

        Feature myFeature1;
        LinearRing ring = GF.createLinearRing(
                new Coordinate[]{
                    new Coordinate(103.0, 103.0),
                    new Coordinate(103.0, 104.0),
                    new Coordinate(104.0, 104.0),
                    new Coordinate(104.0, 103.0),
                    new Coordinate(103.0, 103.0)
                });
        myFeature1 = type.newInstance();
        myFeature1.setPropertyValue("@identifier", "id-01");
        myFeature1.setPropertyValue("color", "grey");
        myFeature1.setPropertyValue("height", 9);
        myFeature1.setPropertyValue("type", "church");
        myFeature1.setPropertyValue("geom1", GF.createPolygon(ring, null));
        myFeature1.setPropertyValue("geom2", GF.createPoint(new Coordinate(103.5, 103.5)));
        featureList.add(myFeature1);

        Feature myFeature2;
        ring = GF.createLinearRing(
                new Coordinate[]{
                    new Coordinate(105.0, 106.0),
                    new Coordinate(105.0, 107.0),
                    new Coordinate(106.0, 107.0),
                    new Coordinate(106.0, 106.0),
                    new Coordinate(105.0, 106.0)
                });
        MultiPoint multPt = GF.createMultiPoint(
                new Coordinate[]{
                    new Coordinate(105.0, 104.0),
                    new Coordinate(103.0, 106.0),
                    new Coordinate(104.0,107.0),
                    new Coordinate(105.5,106.5)
                });
        myFeature2 = type.newInstance();
        myFeature2.setPropertyValue("@identifier", "id-02");
        myFeature2.setPropertyValue("color", "blue");
        myFeature2.setPropertyValue("height", 3);
        myFeature2.setPropertyValue("type", "office");
        myFeature2.setPropertyValue("geom1", GF.createPolygon(ring, null));
        myFeature2.setPropertyValue("geom2", multPt);
        featureList.add(myFeature2);

        Feature myFeature3;
        ring = GF.createLinearRing(
                new Coordinate[]{
                    new Coordinate(109.0, 104.0),
                    new Coordinate(109.0, 105.0),
                    new Coordinate(111.0, 105.0),
                    new Coordinate(111.0, 104.0),
                    new Coordinate(109.0, 104.0)
                });
        LineString line = GF.createLineString(
                new Coordinate[]{
                    new Coordinate(107.0, 100.0),
                    new Coordinate(109.0, 103.0)
                });
        myFeature3 = type.newInstance();
        myFeature3.setPropertyValue("@identifier", "id-03");
        myFeature3.setPropertyValue("color", "black");
        myFeature3.setPropertyValue("height", 2);
        myFeature3.setPropertyValue("type", "office");
        myFeature3.setPropertyValue("geom1", GF.createPolygon(ring, null));
        myFeature3.setPropertyValue("geom2", line);
        featureList.add(myFeature3);

        Feature myFeature4;
        ring = GF.createLinearRing(
                new Coordinate[]{
                    new Coordinate(102.0, 102.0),
                    new Coordinate(102.0, 103.0),
                    new Coordinate(103.0, 103.0),
                    new Coordinate(103.0, 102.0),
                    new Coordinate(102.0, 102.0)
                });
        myFeature4 = type.newInstance();
        myFeature4.setPropertyValue("@identifier", "id-04");
        myFeature4.setPropertyValue("color", "yellow");
        myFeature4.setPropertyValue("height", 2);
        myFeature4.setPropertyValue("type", "post office");
        myFeature4.setPropertyValue("geom1", GF.createPolygon(ring, null));
        myFeature4.setPropertyValue("geom2", GF.createPoint(new Coordinate(110, 105)));
        featureList.add(myFeature4);

        Feature myFeature5;
        ring = GF.createLinearRing(
                new Coordinate[]{
                   new Coordinate(106.0, 107.0),
                    new Coordinate(106.0, 108.0),
                    new Coordinate(107.0, 108.0),
                    new Coordinate(107.0, 107.0),
                    new Coordinate(106.0, 107.0)
                });
        line = GF.createLineString(
                new Coordinate[]{
                    new Coordinate(108.0, 100.0),
                    new Coordinate(105.0, 103.0)
                });
        myFeature5 = type.newInstance();
        myFeature5.setPropertyValue("@identifier", "id-05");
        myFeature5.setPropertyValue("color", "yellow");
        myFeature5.setPropertyValue("height", 9);
        myFeature5.setPropertyValue("type", "office");
        myFeature5.setPropertyValue("geom1", GF.createPolygon(ring, null));
        myFeature5.setPropertyValue("geom2", line);
        featureList.add(myFeature5);

        Feature myFeature6;
        ring = GF.createLinearRing(
                new Coordinate[]{
                     new Coordinate(115.0, 110.0),
                    new Coordinate(115.0, 111.0),
                    new Coordinate(116.0, 111.0),
                    new Coordinate(116.0, 110.0),
                    new Coordinate(115.0, 110.0)
                });
        LinearRing ring2 = GF.createLinearRing(
                new Coordinate[]{
                    new Coordinate(108.0, 100.0),
                    new Coordinate(109.0, 106.0),
                    new Coordinate(110.0, 102.0),
                    new Coordinate(108.0, 100.0)
                });
        myFeature6 = type.newInstance();
        myFeature6.setPropertyValue("@identifier", "id-06");
        myFeature6.setPropertyValue("color", "black");
        myFeature6.setPropertyValue("height", 2);
        myFeature6.setPropertyValue("type", "church");
        myFeature6.setPropertyValue("geom1", GF.createPolygon(ring, null));
        myFeature6.setPropertyValue("geom2",GF.createPolygon(ring2, null));
        featureList.add(myFeature6);

        return featureList;
    }

}
