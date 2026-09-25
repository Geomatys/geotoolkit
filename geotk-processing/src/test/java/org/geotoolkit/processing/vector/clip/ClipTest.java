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
package org.geotoolkit.processing.vector.clip;

import java.util.List;
import org.apache.sis.feature.builder.AttributeRole;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.feature.internal.shared.AttributeConvention;
import org.apache.sis.referencing.CRS;
import org.apache.sis.storage.FeatureSet;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.process.ProcessFinder;
import org.geotoolkit.processing.GeotkProcessingRegistry;
import org.geotoolkit.processing.vector.AbstractProcessTest;
import org.geotoolkit.storage.memory.InMemoryFeatureSet;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.util.FactoryException;
import org.opengis.util.NoSuchIdentifierException;



/**
 * JUnit test of clip with a FeatureCollection process
 *
 * @author Quentin Boileau
 * @module
 */
public class ClipTest extends AbstractProcessTest {

    private static GeometryFactory geometryFactory;
    private static FeatureType type;

    public ClipTest() {
        super("vector:clip");
    }

    @Test
    public void testClip() throws ProcessException, NoSuchIdentifierException, FactoryException {

        // Inputs
        final FeatureSet featureList = buildFeatureList();
        final FeatureSet featuresClip = buildFeatureClip();

        // Process
        ProcessDescriptor desc = ProcessFinder.getProcessDescriptor(GeotkProcessingRegistry.NAME,"vector:clip");

        ParameterValueGroup in = desc.getInputDescriptor().createValue();
        in.parameter("feature_in").setValue(featureList);
        in.parameter("feature_clip").setValue(featuresClip);
        org.geotoolkit.process.Process proc = desc.createProcess(in);

        //Features out
        final FeatureSet featureListOut = (FeatureSet) proc.call().parameter("feature_out").getValue();


        //Expected Features out
        final FeatureSet featureListResult = buildResultList();

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

    private static FeatureSet buildFeatureList() throws FactoryException {

        type = createSimpleType();

        final InMemoryFeatureSet featureList = new InMemoryFeatureSet("noname", type);

        geometryFactory = org.geotoolkit.geometry.jts.JTS.getFactory();

        Feature myFeature1 = type.newInstance();
        LinearRing ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(6.0, 3.0),
                    new Coordinate(7.0, 3.0),
                    new Coordinate(7.0, 4.0),
                    new Coordinate(6.0, 4.0),
                    new Coordinate(6.0, 3.0)
                });
        myFeature1.setPropertyValue("sis:identifier", "id-01");
        myFeature1.setPropertyValue("name", "Building1");
        myFeature1.setPropertyValue("height", 12);
        myFeature1.setPropertyValue("position", geometryFactory.createPolygon(ring, null));

        Feature myFeature2 = type.newInstance();
        ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(8.0, 4.0),
                    new Coordinate(11.0, 4.0),
                    new Coordinate(11.0, 7.0),
                    new Coordinate(8.0, 7.0),
                    new Coordinate(8.0, 4.0)
                });
        myFeature2.setPropertyValue("sis:identifier", "id-02");
        myFeature2.setPropertyValue("name", "Building2");
        myFeature2.setPropertyValue("height", 12);
        myFeature2.setPropertyValue("position", geometryFactory.createPolygon(ring, null));

        Feature myFeature3 = type.newInstance();
        ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(6.0, -2.0),
                    new Coordinate(13.0, -2.0),
                    new Coordinate(13.0, 1.0),
                    new Coordinate(6.0, 1.0),
                    new Coordinate(6.0, -2.0)
                });
        myFeature3.setPropertyValue("sis:identifier", "id-03");
        myFeature3.setPropertyValue("name", "Building3");
        myFeature3.setPropertyValue("height", 12);
        myFeature3.setPropertyValue("position", geometryFactory.createPolygon(ring, null));

        Feature myFeature4 = type.newInstance();
        ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(0.0, 6.0),
                    new Coordinate(3.0, 6.0),
                    new Coordinate(3.0, 9.0),
                    new Coordinate(0.0, 9.0),
                    new Coordinate(0.0, 6.0)
                });
        myFeature4.setPropertyValue("sis:identifier", "id-04");
        myFeature4.setPropertyValue("name", "Building4");
        myFeature4.setPropertyValue("height", 12);
        myFeature4.setPropertyValue("position", geometryFactory.createPolygon(ring, null));

        Feature myFeature5 = type.newInstance();
        ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(-4.0, 1.0),
                    new Coordinate(-1.0, 1.0),
                    new Coordinate(-1.0, 3.0),
                    new Coordinate(-4.0, 3.0),
                    new Coordinate(-4.0, 1.0)
                });
        myFeature5.setPropertyValue("sis:identifier", "id-05");
        myFeature5.setPropertyValue("name", "Building5");
        myFeature5.setPropertyValue("height", 12);
        myFeature5.setPropertyValue("position", geometryFactory.createPolygon(ring, null));


        featureList.add(List.of(myFeature1, myFeature2, myFeature3, myFeature4, myFeature5).iterator());
        return featureList;
    }

    private static FeatureSet buildFeatureClip() throws FactoryException {

        type = createSimpleType();

        final InMemoryFeatureSet featureList = new InMemoryFeatureSet("noname", type);

        geometryFactory = org.geotoolkit.geometry.jts.JTS.getFactory();

        Feature myFeature1 = type.newInstance();
        LinearRing ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(0.0, 1.0),
                    new Coordinate(4.0, 1.0),
                    new Coordinate(4.0, 3.0),
                    new Coordinate(0.0, 3.0),
                    new Coordinate(0.0, 1.0)
                });
        myFeature1.setPropertyValue("sis:identifier", "id-11");
        myFeature1.setPropertyValue("name", "Building11");
        myFeature1.setPropertyValue("height", 12);
        myFeature1.setPropertyValue("position", geometryFactory.createPolygon(ring, null));

        Feature myFeature2 = type.newInstance();
        ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(3.0, 5.0),
                    new Coordinate(4.0, 5.0),
                    new Coordinate(4.0, 6.0),
                    new Coordinate(3.0, 6.0),
                    new Coordinate(3.0, 5.0)
                });
        myFeature2.setPropertyValue("sis:identifier", "id-12");
        myFeature2.setPropertyValue("name", "Building11");
        myFeature2.setPropertyValue("height", 12);
        myFeature2.setPropertyValue("position", geometryFactory.createPolygon(ring, null));

        Feature myFeature3 = type.newInstance();
        ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(6.0, 3.0),
                    new Coordinate(9.0, 3.0),
                    new Coordinate(9.0, 6.0),
                    new Coordinate(6.0, 6.0),
                    new Coordinate(6.0, 3.0)
                });
        myFeature3.setPropertyValue("sis:identifier", "id-13");
        myFeature3.setPropertyValue("name", "Building13");
        myFeature3.setPropertyValue("height", 12);
        myFeature3.setPropertyValue("position", geometryFactory.createPolygon(ring, null));

        Feature myFeature4 = type.newInstance();
        ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(10.0, 0.0),
                    new Coordinate(11.0, 0.0),
                    new Coordinate(11.0, 4.0),
                    new Coordinate(10.0, 4.0),
                    new Coordinate(10.0, 0.0)
                });
        myFeature4.setPropertyValue("sis:identifier", "id-14");
        myFeature4.setPropertyValue("name", "Building14");
        myFeature4.setPropertyValue("height", 12);
        myFeature4.setPropertyValue("position", geometryFactory.createPolygon(ring, null));

        featureList.add(List.of(myFeature1, myFeature2, myFeature3, myFeature4).iterator());
        return featureList;
    }

    private static FeatureSet buildResultList() throws FactoryException {

        type = createSimpleResultType();

        final InMemoryFeatureSet featureList = new InMemoryFeatureSet("noname", type);

        geometryFactory = org.geotoolkit.geometry.jts.JTS.getFactory();

        Feature myFeature1 = type.newInstance();
        LinearRing ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(7.0, 3.0),
                    new Coordinate(6.0, 3.0),
                    new Coordinate(6.0, 4.0),
                    new Coordinate(7.0, 4.0),
                    new Coordinate(7.0, 3.0)
                });
        myFeature1.setPropertyValue("sis:identifier", "id-01");
        Polygon poly = geometryFactory.createPolygon(ring, null);
        myFeature1.setPropertyValue("name", "Building1");
        myFeature1.setPropertyValue("height", 12);
        myFeature1.setPropertyValue("position", poly);

        Feature myFeature2 = type.newInstance();
        ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(9.0, 4.0),
                    new Coordinate(8.0, 4.0),
                    new Coordinate(8.0, 6.0),
                    new Coordinate(9.0, 6.0),
                    new Coordinate(9.0, 4.0)
                });
        poly = geometryFactory.createPolygon(ring, null);
        LineString lineString = geometryFactory.createLineString(
                new Coordinate[]{
                    new Coordinate(10.0, 4.0),
                    new Coordinate(11.0, 4.0)
                });
        myFeature2.setPropertyValue("sis:identifier", "id-02");
        myFeature2.setPropertyValue("name", "Building2");
        myFeature2.setPropertyValue("height", 12);
        myFeature2.setPropertyValue("position", geometryFactory.createGeometryCollection(new Geometry[]{poly, lineString}));

        Feature myFeature3 = type.newInstance();
        ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(10.0, 1.0),
                    new Coordinate(11.0, 1.0),
                    new Coordinate(11.0, 0.0),
                    new Coordinate(10.0, 0.0),
                    new Coordinate(10.0, 1.0)
                });
        poly = geometryFactory.createPolygon(ring, null);
        myFeature3.setPropertyValue("sis:identifier", "id-03");
        myFeature3.setPropertyValue("name", "Building3");
        myFeature3.setPropertyValue("height", 12);
        myFeature3.setPropertyValue("position", poly);

        Feature myFeature4 = type.newInstance();
        myFeature4.setPropertyValue("sis:identifier", "id-04");
        Point pt = geometryFactory.createPoint(new Coordinate(3.0, 6.0));
        myFeature4.setPropertyValue("name", "Building4");
        myFeature4.setPropertyValue("height", 12);
        myFeature4.setPropertyValue("position", pt);

        featureList.add(List.of(myFeature1, myFeature2, myFeature3, myFeature4).iterator());
        return featureList;
    }
}
