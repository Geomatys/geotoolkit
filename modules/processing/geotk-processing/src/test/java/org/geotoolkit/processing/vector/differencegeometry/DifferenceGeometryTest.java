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
package org.geotoolkit.processing.vector.differencegeometry;

import org.geotoolkit.process.ProcessException;
import org.geotoolkit.processing.vector.AbstractProcessTest;
import org.opengis.util.NoSuchIdentifierException;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Coordinate;
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
import org.geotoolkit.processing.GeotkProcessingRegistry;

import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.util.FactoryException;

import org.junit.Test;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;

/**
 * JUnit test of clip with a geometry process
 *
 * @author Quentin Boileau
 * @module
 */
public class DifferenceGeometryTest extends AbstractProcessTest {

    private static GeometryFactory geometryFactory;
    private static FeatureType type;

    public DifferenceGeometryTest() {
        super("vector:diffGeometry");
    }

    @Test
    public void testDiffGeometry() throws ProcessException, NoSuchIdentifierException, FactoryException {

        // Inputs
        final FeatureCollection featureList = buildFeatureList();
        final Geometry geometryClip = buildGeometryClip();
        // Process
        ProcessDescriptor desc = ProcessFinder.getProcessDescriptor(GeotkProcessingRegistry.NAME,"vector:diffGeometry");

        ParameterValueGroup in = desc.getInputDescriptor().createValue();
        in.parameter(DifferenceGeometryDescriptor.FEATURE_IN.getName().getCode()).setValue(featureList);
        in.parameter(DifferenceGeometryDescriptor.DIFF_GEOMETRY_IN.getName().getCode()).setValue(geometryClip);
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
                    new Coordinate(10.0, 6.0),
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
                    new Coordinate(10.0, 1.0),
                    new Coordinate(13.0, 1.0),
                    new Coordinate(13.0, -2.0),
                    new Coordinate(6.0, -2.0),
                    new Coordinate(6.0, 1.0),
                    new Coordinate(10.0, 1.0)
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
