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
package org.geotoolkit.process.vector.clip;

import org.geotoolkit.process.ProcessException;
import org.opengis.util.NoSuchIdentifierException;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import org.geotoolkit.data.FeatureStoreUtilities;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.feature.FeatureBuilder;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessFinder;
import org.geotoolkit.process.vector.AbstractProcessTest;
import org.geotoolkit.referencing.CRS;

import org.junit.Test;
import org.geotoolkit.feature.Feature;
import org.geotoolkit.feature.simple.SimpleFeatureType;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.util.FactoryException;

import static org.junit.Assert.*;

/**
 * JUnit test of clip with a FeatureCollection process
 *
 * @author Quentin Boileau @module pending
 */
public class ClipTest extends AbstractProcessTest {

    private static FeatureBuilder sfb;
    private static GeometryFactory geometryFactory;
    private static SimpleFeatureType type;

    public ClipTest() {
        super("clip");
    }

    @Test
    public void testClip() throws ProcessException, NoSuchIdentifierException, FactoryException {

        // Inputs
        final FeatureCollection featureList = buildFeatureList();
        final FeatureCollection featuresClip = buildFeatureClip();

        // Process
        ProcessDescriptor desc = ProcessFinder.getProcessDescriptor("vector", "clip");

        ParameterValueGroup in = desc.getInputDescriptor().createValue();
        in.parameter("feature_in").setValue(featureList);
        in.parameter("feature_clip").setValue(featuresClip);
        org.geotoolkit.process.Process proc = desc.createProcess(in);

        //Features out
        final FeatureCollection featureListOut = (FeatureCollection) proc.call().parameter("feature_out").getValue();


        //Expected Features out
        final FeatureCollection featureListResult = buildResultList();

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

    private static FeatureCollection buildFeatureList() throws FactoryException {

        type = createSimpleType();

        final FeatureCollection featureList = FeatureStoreUtilities.collection("", type);

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
        sfb = new FeatureBuilder(type);
        sfb.setPropertyValue("name", "Building1");
        sfb.setPropertyValue("height", 12);
        sfb.setPropertyValue("position", geometryFactory.createPolygon(ring, null));
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
        sfb = new FeatureBuilder(type);
        sfb.setPropertyValue("name", "Building2");
        sfb.setPropertyValue("height", 12);
        sfb.setPropertyValue("position", geometryFactory.createPolygon(ring, null));
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
        sfb = new FeatureBuilder(type);
        sfb.setPropertyValue("name", "Building3");
        sfb.setPropertyValue("height", 12);
        sfb.setPropertyValue("position", geometryFactory.createPolygon(ring, null));
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
        sfb = new FeatureBuilder(type);
        sfb.setPropertyValue("name", "Building4");
        sfb.setPropertyValue("height", 12);
        sfb.setPropertyValue("position", geometryFactory.createPolygon(ring, null));
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
        sfb = new FeatureBuilder(type);
        sfb.setPropertyValue("name", "Building5");
        sfb.setPropertyValue("height", 12);
        sfb.setPropertyValue("position", geometryFactory.createPolygon(ring, null));
        myFeature5 = sfb.buildFeature("id-05");
        featureList.add(myFeature5);


        return featureList;
    }

    private static FeatureCollection buildFeatureClip() throws FactoryException {

        type = createSimpleType();

        final FeatureCollection featureList = FeatureStoreUtilities.collection("", type);

        geometryFactory = new GeometryFactory();

        Feature myFeature1;
        LinearRing ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(0.0, 1.0),
                    new Coordinate(4.0, 1.0),
                    new Coordinate(4.0, 3.0),
                    new Coordinate(0.0, 3.0),
                    new Coordinate(0.0, 1.0)
                });
        sfb = new FeatureBuilder(type);
        sfb.setPropertyValue("name", "Building11");
        sfb.setPropertyValue("height", 12);
        sfb.setPropertyValue("position", geometryFactory.createPolygon(ring, null));
        myFeature1 = sfb.buildFeature("id-11");
        featureList.add(myFeature1);

        Feature myFeature2;
        ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(3.0, 5.0),
                    new Coordinate(4.0, 5.0),
                    new Coordinate(4.0, 6.0),
                    new Coordinate(3.0, 6.0),
                    new Coordinate(3.0, 5.0)
                });
        sfb = new FeatureBuilder(type);
        sfb.setPropertyValue("name", "Building11");
        sfb.setPropertyValue("height", 12);
        sfb.setPropertyValue("position", geometryFactory.createPolygon(ring, null));
        myFeature2 = sfb.buildFeature("id-12");
        featureList.add(myFeature2);

        Feature myFeature3;
        ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(6.0, 3.0),
                    new Coordinate(9.0, 3.0),
                    new Coordinate(9.0, 6.0),
                    new Coordinate(6.0, 6.0),
                    new Coordinate(6.0, 3.0)
                });
        sfb = new FeatureBuilder(type);
        sfb.setPropertyValue("name", "Building13");
        sfb.setPropertyValue("height", 12);
        sfb.setPropertyValue("position", geometryFactory.createPolygon(ring, null));
        myFeature3 = sfb.buildFeature("id-13");
        featureList.add(myFeature3);

        Feature myFeature4;
        ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(10.0, 0.0),
                    new Coordinate(11.0, 0.0),
                    new Coordinate(11.0, 4.0),
                    new Coordinate(10.0, 4.0),
                    new Coordinate(10.0, 0.0)
                });
        sfb = new FeatureBuilder(type);
        sfb.setPropertyValue("name", "Building14");
        sfb.setPropertyValue("height", 12);
        sfb.setPropertyValue("position", geometryFactory.createPolygon(ring, null));
        myFeature4 = sfb.buildFeature("id-14");
        featureList.add(myFeature4);

        return featureList;
    }

    private static FeatureCollection buildResultList() throws FactoryException {

        type = createSimpleResultType();

        final FeatureCollection featureList = FeatureStoreUtilities.collection("", type);

        geometryFactory = new GeometryFactory();

        Feature myFeature1;
        LinearRing ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(7.0, 3.0),
                    new Coordinate(6.0, 3.0),
                    new Coordinate(6.0, 4.0),
                    new Coordinate(7.0, 4.0),
                    new Coordinate(7.0, 3.0)
                });
        Polygon poly = geometryFactory.createPolygon(ring, null);
        sfb = new FeatureBuilder(type);
        sfb.setPropertyValue("name", "Building1");
        sfb.setPropertyValue("height", 12);
        sfb.setPropertyValue("position", poly);
        myFeature1 = sfb.buildFeature("id-01");
        featureList.add(myFeature1);

        Feature myFeature2;
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
        sfb = new FeatureBuilder(type);
        sfb.setPropertyValue("name", "Building2");
        sfb.setPropertyValue("height", 12);
        sfb.setPropertyValue("position", geometryFactory.createGeometryCollection(new Geometry[]{poly, lineString}));
        myFeature2 = sfb.buildFeature("id-02");
        featureList.add(myFeature2);

        Feature myFeature3;
        ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(10.0, 1.0),
                    new Coordinate(11.0, 1.0),
                    new Coordinate(11.0, 0.0),
                    new Coordinate(10.0, 0.0),
                    new Coordinate(10.0, 1.0)
                });
        poly = geometryFactory.createPolygon(ring, null);
        sfb = new FeatureBuilder(type);
        sfb.setPropertyValue("name", "Building3");
        sfb.setPropertyValue("height", 12);
        sfb.setPropertyValue("position", poly);
        myFeature3 = sfb.buildFeature("id-03");
        featureList.add(myFeature3);

        Feature myFeature4;
        Point pt = geometryFactory.createPoint(new Coordinate(3.0, 6.0));
        sfb = new FeatureBuilder(type);
        sfb.setPropertyValue("name", "Building4");
        sfb.setPropertyValue("height", 12);
        sfb.setPropertyValue("position", pt);
        myFeature4 = sfb.buildFeature("id-04");
        featureList.add(myFeature4);

        return featureList;
    }
}
