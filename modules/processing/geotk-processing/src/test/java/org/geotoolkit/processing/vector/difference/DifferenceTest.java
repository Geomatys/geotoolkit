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
package org.geotoolkit.processing.vector.difference;

import org.geotoolkit.process.ProcessException;
import org.geotoolkit.processing.vector.AbstractProcessTest;
import org.opengis.util.NoSuchIdentifierException;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;
import org.apache.sis.feature.builder.AttributeRole;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.internal.feature.AttributeConvention;

import org.geotoolkit.data.FeatureStoreUtilities;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessFinder;
import org.apache.sis.referencing.CRS;

import org.junit.Test;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.util.FactoryException;

import static org.junit.Assert.*;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;

/**
 * JUnit test of clip with a FeatureCollection process
 *
 * @author Quentin Boileau
 * @module
 */
public class DifferenceTest extends AbstractProcessTest {

    private static GeometryFactory geometryFactory;
    private static FeatureType type;

    public DifferenceTest() {
        super("difference");
    }

    @Test
    public void testDifference() throws ProcessException, NoSuchIdentifierException, FactoryException {

        // Inputs
        final FeatureCollection featureList = buildFeatureList();
        final FeatureCollection featuresClip = buildFeatureClip();
        // Process
        ProcessDescriptor desc = ProcessFinder.getProcessDescriptor("vector", "difference");

        ParameterValueGroup in = desc.getInputDescriptor().createValue();
        in.parameter("feature_in").setValue(featureList);
        in.parameter("feature_diff").setValue(featuresClip);
        org.geotoolkit.process.Process proc = desc.createProcess(in);

        //Features out
        final FeatureCollection featureListOut = (FeatureCollection) proc.call().parameter("feature_out").getValue();

        //Expected Features out
        final FeatureCollection featureListResult = buildResultList();

        compare(featureListResult,featureListOut);
    }

    private static FeatureType createSimpleType() throws NoSuchAuthorityCodeException, FactoryException {
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("Building");
        ftb.addAttribute(String.class).setName(AttributeConvention.IDENTIFIER_PROPERTY);
        ftb.addAttribute(String.class).setName("name");
        ftb.addAttribute(Polygon.class).setName("position").setCRS(CRS.forCode("EPSG:3395")).addRole(AttributeRole.DEFAULT_GEOMETRY);
        ftb.addAttribute(Integer.class).setName("height");
        return ftb.build();
    }

    private static FeatureType createSimpleResultType() throws NoSuchAuthorityCodeException, FactoryException {
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("Building");
        ftb.addAttribute(String.class).setName(AttributeConvention.IDENTIFIER_PROPERTY);
        ftb.addAttribute(String.class).setName("name");
        ftb.addAttribute(Geometry.class).setName("position").setCRS(CRS.forCode("EPSG:3395")).addRole(AttributeRole.DEFAULT_GEOMETRY);
        ftb.addAttribute(Integer.class).setName("height");
        return ftb.build();
    }

    private static FeatureCollection buildFeatureList() throws FactoryException {

        type = createSimpleType();

        final FeatureCollection featureList = FeatureStoreUtilities.collection("", type);

        geometryFactory = new GeometryFactory();

        Feature myFeature1 = type.newInstance();
        LinearRing ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(6.0, 3.0),
                    new Coordinate(7.0, 3.0),
                    new Coordinate(7.0, 4.0),
                    new Coordinate(6.0, 4.0),
                    new Coordinate(6.0, 3.0)
                });
        myFeature1.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "id-01");
        myFeature1.setPropertyValue("name", "Building1");
        myFeature1.setPropertyValue("height", 12);
        myFeature1.setPropertyValue("position", geometryFactory.createPolygon(ring, null));
        featureList.add(myFeature1);

        Feature myFeature2 = type.newInstance();
        ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(8.0, 4.0),
                    new Coordinate(11.0, 4.0),
                    new Coordinate(11.0, 7.0),
                    new Coordinate(8.0, 7.0),
                    new Coordinate(8.0, 4.0)
                });
        myFeature2.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "id-02");
        myFeature2.setPropertyValue("name", "Building2");
        myFeature2.setPropertyValue("height", 12);
        myFeature2.setPropertyValue("position", geometryFactory.createPolygon(ring, null));
        featureList.add(myFeature2);

        Feature myFeature3 = type.newInstance();
        ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(6.0, -2.0),
                    new Coordinate(13.0, -2.0),
                    new Coordinate(13.0, 1.0),
                    new Coordinate(6.0, 1.0),
                    new Coordinate(6.0, -2.0)
                });
        myFeature3.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "id-03");
        myFeature3.setPropertyValue("name", "Building3");
        myFeature3.setPropertyValue("height", 12);
        myFeature3.setPropertyValue("position", geometryFactory.createPolygon(ring, null));
        featureList.add(myFeature3);

        Feature myFeature4 = type.newInstance();
        ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(0.0, 6.0),
                    new Coordinate(3.0, 6.0),
                    new Coordinate(3.0, 9.0),
                    new Coordinate(0.0, 9.0),
                    new Coordinate(0.0, 6.0)
                });
        myFeature4.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "id-04");
        myFeature4.setPropertyValue("name", "Building4");
        myFeature4.setPropertyValue("height", 12);
        myFeature4.setPropertyValue("position", geometryFactory.createPolygon(ring, null));
        featureList.add(myFeature4);

        Feature myFeature5 = type.newInstance();
        ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(-4.0, 1.0),
                    new Coordinate(-1.0, 1.0),
                    new Coordinate(-1.0, 3.0),
                    new Coordinate(-4.0, 3.0),
                    new Coordinate(-4.0, 1.0)
                });
        myFeature5.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "id-05");
        myFeature5.setPropertyValue("name", "Building5");
        myFeature5.setPropertyValue("height", 12);
        myFeature5.setPropertyValue("position", geometryFactory.createPolygon(ring, null));
        featureList.add(myFeature5);


        return featureList;
    }

    private static FeatureCollection buildFeatureClip() throws FactoryException {

        type = createSimpleType();

        final FeatureCollection featureList = FeatureStoreUtilities.collection("", type);

        geometryFactory = new GeometryFactory();

        Feature myFeature1 = type.newInstance();
        LinearRing ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(0.0, 1.0),
                    new Coordinate(4.0, 1.0),
                    new Coordinate(4.0, 3.0),
                    new Coordinate(0.0, 3.0),
                    new Coordinate(0.0, 1.0)
                });
        myFeature1.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "id-11");
        myFeature1.setPropertyValue("name", "Building11");
        myFeature1.setPropertyValue("height", 12);
        myFeature1.setPropertyValue("position", geometryFactory.createPolygon(ring, null));
        featureList.add(myFeature1);

        Feature myFeature2 = type.newInstance();
        ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(3.0, 5.0),
                    new Coordinate(4.0, 5.0),
                    new Coordinate(4.0, 6.0),
                    new Coordinate(3.0, 6.0),
                    new Coordinate(3.0, 5.0)
                });
        myFeature2.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "id-12");
        myFeature2.setPropertyValue("name", "Building11");
        myFeature2.setPropertyValue("height", 12);
        myFeature2.setPropertyValue("position", geometryFactory.createPolygon(ring, null));
        featureList.add(myFeature2);

        Feature myFeature3 = type.newInstance();
        ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(6.0, 3.0),
                    new Coordinate(9.0, 3.0),
                    new Coordinate(9.0, 6.0),
                    new Coordinate(6.0, 6.0),
                    new Coordinate(6.0, 3.0)
                });
        myFeature3.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "id-13");
        myFeature3.setPropertyValue("name", "Building13");
        myFeature3.setPropertyValue("height", 12);
        myFeature3.setPropertyValue("position", geometryFactory.createPolygon(ring, null));
        featureList.add(myFeature3);

        Feature myFeature4 = type.newInstance();
        ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(10.0, 0.0),
                    new Coordinate(11.0, 0.0),
                    new Coordinate(11.0, 4.0),
                    new Coordinate(10.0, 4.0),
                    new Coordinate(10.0, 0.0)
                });
        myFeature4.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "id-14");
        myFeature4.setPropertyValue("name", "Building14");
        myFeature4.setPropertyValue("height", 12);
        myFeature4.setPropertyValue("position", geometryFactory.createPolygon(ring, null));
        featureList.add(myFeature4);

        return featureList;
    }

    private static FeatureCollection buildResultList() throws FactoryException {

        type = createSimpleResultType();

        final FeatureCollection featureList = FeatureStoreUtilities.collection("", type);

        geometryFactory = new GeometryFactory();



        Feature myFeature2 = type.newInstance();
        LinearRing ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(8.0, 6.0),
                    new Coordinate(8.0, 7.0),
                    new Coordinate(11.0, 7.0),
                    new Coordinate(11.0, 4.0),
                    new Coordinate(10.0, 4.0),
                    new Coordinate(9.0, 4.0),
                    new Coordinate(9.0, 6.0),
                    new Coordinate(8.0, 6.0)
                });
        myFeature2.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "id-02");
        myFeature2.setPropertyValue("name", "Building2");
        myFeature2.setPropertyValue("height", 12);
        myFeature2.setPropertyValue("position", geometryFactory.createPolygon(ring, null));
        featureList.add(myFeature2);

        Feature myFeature3 = type.newInstance();
        ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(11.0, 1.0),
                    new Coordinate(13.0, 1.0),
                    new Coordinate(13.0, -2.0),
                    new Coordinate(6.0, -2.0),
                    new Coordinate(6.0, 1.0),
                    new Coordinate(10.0, 1.0),
                    new Coordinate(10.0, 0.0),
                    new Coordinate(11.0, 0.0),
                    new Coordinate(11.0, 1.0)
                });
        myFeature3.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "id-03");
        myFeature3.setPropertyValue("name", "Building3");
        myFeature3.setPropertyValue("height", 12);
        myFeature3.setPropertyValue("position", geometryFactory.createPolygon(ring, null));
        featureList.add(myFeature3);

        Feature myFeature4 = type.newInstance();
        ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(3.0, 6.0),
                    new Coordinate(0.0, 6.0),
                    new Coordinate(0.0, 9.0),
                    new Coordinate(3.0, 9.0),
                    new Coordinate(3.0, 6.0)
                });
        myFeature4.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "id-04");
        myFeature4.setPropertyValue("name", "Building4");
        myFeature4.setPropertyValue("height", 12);
        myFeature4.setPropertyValue("position", geometryFactory.createPolygon(ring, null));
        featureList.add(myFeature4);

        Feature myFeature5 = type.newInstance();
        ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(-4.0, 1.0),
                    new Coordinate(-1.0, 1.0),
                    new Coordinate(-1.0, 3.0),
                    new Coordinate(-4.0, 3.0),
                    new Coordinate(-4.0, 1.0)
                });
        myFeature5.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "id-05");
        myFeature5.setPropertyValue("name", "Building5");
        myFeature5.setPropertyValue("height", 12);
        myFeature5.setPropertyValue("position", geometryFactory.createPolygon(ring, null));
        featureList.add(myFeature5);

        return featureList;
    }
}
