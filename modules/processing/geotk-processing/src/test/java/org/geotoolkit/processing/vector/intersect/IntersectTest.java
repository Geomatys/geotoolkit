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
package org.geotoolkit.processing.vector.intersect;

import org.apache.sis.feature.builder.AttributeRole;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.internal.feature.AttributeConvention;
import org.apache.sis.referencing.CRS;
import org.apache.sis.storage.FeatureSet;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.process.ProcessFinder;
import org.geotoolkit.processing.GeotkProcessingRegistry;
import org.geotoolkit.processing.vector.AbstractProcessTest;
import org.geotoolkit.storage.feature.FeatureCollection;
import org.geotoolkit.storage.feature.FeatureStoreUtilities;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.MultiPoint;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.util.FactoryException;
import org.opengis.util.NoSuchIdentifierException;

/**
 * JUnit test of intersect process
 *
 * @author Quentin Boileau
 * @module
 */
public class IntersectTest extends AbstractProcessTest {

    private static final GeometryFactory geometryFactory = new GeometryFactory();
    private static FeatureType type;

    public IntersectTest() {
        super("vector:intersect");
    }

    @Test
    public void testIntersect() throws ProcessException, NoSuchIdentifierException, FactoryException {

        // Inputs
        final FeatureSet featureList = buildFeatureList();
        final Geometry geom = buildIntersectionGeometry();

        // Process
        ProcessDescriptor desc = ProcessFinder.getProcessDescriptor(GeotkProcessingRegistry.NAME,"vector:intersect");

        ParameterValueGroup in = desc.getInputDescriptor().createValue();
        in.parameter("feature_in").setValue(featureList);
        in.parameter("geometry_in").setValue(geom);
        org.geotoolkit.process.Process proc = desc.createProcess(in);

        //Features out
        final FeatureSet featureListOut = (FeatureSet) proc.call().parameter("feature_out").getValue();
        //Expected Features out
        final FeatureSet featureListResult = buildResultList();
        compare(featureListResult,featureListOut);
    }

    private static FeatureType createSimpleType() throws NoSuchAuthorityCodeException, FactoryException {
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("IntersectTest");
        ftb.addAttribute(String.class).setName(AttributeConvention.IDENTIFIER_PROPERTY);
        ftb.addAttribute(String.class).setName("name");
        ftb.addAttribute(Geometry.class).setName("geom1").setCRS(CRS.forCode("EPSG:3395")).addRole(AttributeRole.DEFAULT_GEOMETRY);
        ftb.addAttribute(Geometry.class).setName("geom2").setCRS(CRS.forCode("EPSG:3395"));
        return ftb.build();
    }

    private static FeatureSet buildFeatureList() throws FactoryException {

        type = createSimpleType();

        final FeatureCollection featureList = FeatureStoreUtilities.collection("noname", type);


        Feature myFeature1 = type.newInstance();
        LinearRing ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(3.0, 3.0),
                    new Coordinate(3.0, 4.0),
                    new Coordinate(4.0, 4.0),
                    new Coordinate(4.0, 3.0),
                    new Coordinate(3.0, 3.0)
                });
        myFeature1.setPropertyValue(AttributeConvention.IDENTIFIER, "id-01");
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
                    new Coordinate(5.0, 10.0),
                    new Coordinate(3.0, 6.0),
                    new Coordinate(4.0, 7.0),
                    new Coordinate(5.5, 6.5)
                });
        myFeature2.setPropertyValue(AttributeConvention.IDENTIFIER, "id-02");
        myFeature2.setPropertyValue("name", "feature2");
        myFeature2.setPropertyValue("geom1", geometryFactory.createPolygon(ring, null));
        myFeature2.setPropertyValue("geom2", multPt);
        featureList.add(myFeature2);

        Feature myFeature3 = type.newInstance();
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
        myFeature3.setPropertyValue(AttributeConvention.IDENTIFIER, "id-03");
        myFeature3.setPropertyValue("name", "feature3");
        myFeature3.setPropertyValue("geom1", geometryFactory.createPolygon(ring, null));
        myFeature3.setPropertyValue("geom2", line);
        featureList.add(myFeature3);

        return featureList;
    }

    private static FeatureSet buildResultList() throws FactoryException {


        type = createSimpleType();

        final FeatureCollection featureList = FeatureStoreUtilities.collection("IntersectTest", type);


        Feature myFeature1 = type.newInstance();
        LinearRing ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(3.0, 3.0),
                    new Coordinate(3.0, 4.0),
                    new Coordinate(4.0, 4.0),
                    new Coordinate(4.0, 3.0),
                    new Coordinate(3.0, 3.0)
                });
        myFeature1.setPropertyValue(AttributeConvention.IDENTIFIER, "id-01");
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
                    new Coordinate(5.0, 10.0),
                    new Coordinate(3.0, 6.0),
                    new Coordinate(4.0, 7.0),
                    new Coordinate(5.5, 6.5)
                });
        myFeature2.setPropertyValue(AttributeConvention.IDENTIFIER, "id-02");
        myFeature2.setPropertyValue("name", "feature2");
        myFeature2.setPropertyValue("geom1", geometryFactory.createPolygon(ring, null));
        myFeature2.setPropertyValue("geom2", multPt);
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
