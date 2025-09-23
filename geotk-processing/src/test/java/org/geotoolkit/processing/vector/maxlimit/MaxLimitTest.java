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
package org.geotoolkit.processing.vector.maxlimit;

import java.util.stream.Stream;
import org.apache.sis.feature.builder.AttributeRole;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.feature.internal.shared.AttributeConvention;
import org.apache.sis.referencing.CRS;
import org.apache.sis.storage.FeatureSet;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessFinder;
import org.geotoolkit.processing.GeotkProcessingRegistry;
import org.geotoolkit.processing.vector.AbstractProcessTest;
import org.geotoolkit.storage.feature.FeatureCollection;
import org.geotoolkit.storage.feature.FeatureStoreUtilities;
import static org.junit.Assert.*;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Polygon;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.util.FactoryException;

/**
 * JUnit test of MaxLimit process
 *
 * @author Quentin Boileau
 * @module
 */
public class MaxLimitTest extends AbstractProcessTest {

    private static final GeometryFactory geometryFactory = org.geotoolkit.geometry.jts.JTS.getFactory();
    private static FeatureType type;

    public MaxLimitTest() {
        super("vector:maxlimit");
    }

    @Test
    public void testLimit() throws Exception {

        // Inputs
        final FeatureCollection featureList = buildFeatureList();

        // Process
        ProcessDescriptor desc = ProcessFinder.getProcessDescriptor(GeotkProcessingRegistry.NAME,"vector:maxlimit");

        ParameterValueGroup in = desc.getInputDescriptor().createValue();
        in.parameter("feature_in").setValue(featureList);
        in.parameter("max_in").setValue(5);
        org.geotoolkit.process.Process proc = desc.createProcess(in);

        //Features out
        final FeatureSet featureListOut = (FeatureSet) proc.call().parameter("feature_out").getValue();

        try (Stream<Feature> stream = featureListOut.features(true)) {
            assertEquals(5, stream.count());
        }
    }

    private static FeatureType createSimpleResultType() throws NoSuchAuthorityCodeException, FactoryException {
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("MaxTest");
        ftb.addAttribute(String.class).setName(AttributeConvention.IDENTIFIER_PROPERTY);
        ftb.addAttribute(String.class).setName("name");
        ftb.addAttribute(Geometry.class).setName("geom1").setCRS(CRS.forCode("EPSG:3395")).addRole(AttributeRole.DEFAULT_GEOMETRY);
        return ftb.build();
    }

    private static FeatureCollection buildFeatureList() throws FactoryException {

        type = createSimpleResultType();

        final FeatureCollection featureList = FeatureStoreUtilities.collection("noname", type);


        Feature myFeature = type.newInstance();
        LinearRing ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(4, 7),
                    new Coordinate(6, 7),
                    new Coordinate(6, 5),
                    new Coordinate(4, 5),
                    new Coordinate(4, 7)
                });
        myFeature.setPropertyValue(AttributeConvention.IDENTIFIER, "id-01 U id-11");
        myFeature.setPropertyValue("name", "feature1");
        myFeature.setPropertyValue("geom1", geometryFactory.createPolygon(ring, null));
        featureList.add(myFeature);

        myFeature = type.newInstance();
        ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(3, 5),
                    new Coordinate(3, 7),
                    new Coordinate(4, 7),
                    new Coordinate(4, 5),
                    new Coordinate(3, 5)
                });
        myFeature.setPropertyValue(AttributeConvention.IDENTIFIER, "id-01");
        myFeature.setPropertyValue("name", "feature1");
        myFeature.setPropertyValue("geom1", geometryFactory.createPolygon(ring, null));
        featureList.add(myFeature);

        myFeature = type.newInstance();
        ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(7, 5),
                    new Coordinate(8, 5),
                    new Coordinate(8, 4),
                    new Coordinate(7, 4),
                    new Coordinate(7, 5)
                });
        myFeature.setPropertyValue(AttributeConvention.IDENTIFIER, "id-03 U id-12");
        myFeature.setPropertyValue("name", "feature3");
        myFeature.setPropertyValue("geom1", geometryFactory.createPolygon(ring, null));
        featureList.add(myFeature);

        myFeature = type.newInstance();
        ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(6, 4),
                    new Coordinate(6, 5),
                    new Coordinate(7, 5),
                    new Coordinate(7, 4),
                    new Coordinate(6, 4)
                });
        myFeature.setPropertyValue(AttributeConvention.IDENTIFIER, "id-03 U id-11");
        myFeature.setPropertyValue("name", "feature3");
        myFeature.setPropertyValue("geom1", geometryFactory.createPolygon(ring, null));
        featureList.add(myFeature);

        myFeature = type.newInstance();
        ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(6, 2),
                    new Coordinate(6, 4),
                    new Coordinate(8, 4),
                    new Coordinate(8, 2),
                    new Coordinate(6, 2)
                });
        myFeature.setPropertyValue(AttributeConvention.IDENTIFIER, "id-03 U id-13");
        myFeature.setPropertyValue("name", "feature3");
        myFeature.setPropertyValue("geom1", geometryFactory.createPolygon(ring, null));
        featureList.add(myFeature);

        myFeature = type.newInstance();
        ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(7, 7),
                    new Coordinate(8, 7),
                    new Coordinate(8, 5),
                    new Coordinate(7, 5),
                    new Coordinate(7, 7)
                });
        myFeature.setPropertyValue(AttributeConvention.IDENTIFIER, "id-02 U id-12");
        myFeature.setPropertyValue("name", "feature2");
        myFeature.setPropertyValue("geom1", geometryFactory.createPolygon(ring, null));
        featureList.add(myFeature);

        myFeature = type.newInstance();
        ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(6, 5),
                    new Coordinate(6, 7),
                    new Coordinate(7, 7),
                    new Coordinate(7, 5),
                    new Coordinate(6, 5)
                });
        myFeature.setPropertyValue(AttributeConvention.IDENTIFIER, "id-02 U id-11");
        myFeature.setPropertyValue("name", "feature2");
        myFeature.setPropertyValue("geom1", geometryFactory.createPolygon(ring, null));
        featureList.add(myFeature);

        myFeature = type.newInstance();
        ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(2, 3),
                    new Coordinate(2, 4),
                    new Coordinate(3, 4),
                    new Coordinate(3, 3),
                    new Coordinate(2, 3)
                });
        myFeature.setPropertyValue(AttributeConvention.IDENTIFIER, "id-04");
        myFeature.setPropertyValue("name", "feature4");
        myFeature.setPropertyValue("geom1", geometryFactory.createPolygon(ring, null));
        featureList.add(myFeature);

        myFeature = type.newInstance();
        ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(7, 7),
                    new Coordinate(7, 8),
                    new Coordinate(9, 8),
                    new Coordinate(9, 4),
                    new Coordinate(8, 4),
                    new Coordinate(8, 5),
                    new Coordinate(8, 7),
                    new Coordinate(7, 7)
                });
        myFeature.setPropertyValue(AttributeConvention.IDENTIFIER, "id-12");
        myFeature.setPropertyValue("name", "feature12");
        myFeature.setPropertyValue("geom1", geometryFactory.createPolygon(ring, null));
        featureList.add(myFeature);

        myFeature = type.newInstance();
        ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(8, 4),
                    new Coordinate(9, 4),
                    new Coordinate(9, 2),
                    new Coordinate(8, 2),
                    new Coordinate(8, 4)
                });
        myFeature.setPropertyValue(AttributeConvention.IDENTIFIER, "id-13");
        myFeature.setPropertyValue("name", "feature13");
        myFeature.setPropertyValue("geom1", geometryFactory.createPolygon(ring, null));
        featureList.add(myFeature);

        myFeature = type.newInstance();
        ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(4, 2),
                    new Coordinate(4, 3),
                    new Coordinate(5, 3),
                    new Coordinate(5, 2),
                    new Coordinate(4, 2)
                });
        myFeature.setPropertyValue(AttributeConvention.IDENTIFIER, "id-14");
        myFeature.setPropertyValue("name", "feature14");
        myFeature.setPropertyValue("geom1", geometryFactory.createPolygon(ring, null));
        featureList.add(myFeature);

        myFeature = type.newInstance();
        ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(4, 4),
                    new Coordinate(4, 5),
                    new Coordinate(6, 5),
                    new Coordinate(6, 4),
                    new Coordinate(4, 4)
                });
        LinearRing ring2 = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(4, 7),
                    new Coordinate(4, 8),
                    new Coordinate(7, 8),
                    new Coordinate(7, 7),
                    new Coordinate(6, 7),
                    new Coordinate(4, 7)
                });
        Polygon poly1 = geometryFactory.createPolygon(ring, null);
        Polygon poly2 = geometryFactory.createPolygon(ring2, null);
        myFeature.setPropertyValue(AttributeConvention.IDENTIFIER, "id-11");
        myFeature.setPropertyValue("name", "feature11");
        myFeature.setPropertyValue("geom1", geometryFactory.createMultiPolygon(new Polygon[]{poly1, poly2}));
        featureList.add(myFeature);

        return featureList;
    }
}
