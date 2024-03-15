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
package org.geotoolkit.processing.vector.reproject;

import org.geotoolkit.process.ProcessException;
import org.geotoolkit.processing.vector.AbstractProcessTest;
import org.opengis.util.NoSuchIdentifierException;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.MultiPoint;
import org.apache.sis.feature.builder.AttributeRole;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.feature.privy.AttributeConvention;

import org.geotoolkit.storage.feature.FeatureStoreUtilities;
import org.geotoolkit.storage.feature.FeatureCollection;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessFinder;
import org.apache.sis.referencing.CRS;

import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.util.FactoryException;

import org.apache.sis.referencing.CommonCRS;
import org.geotoolkit.processing.GeotkProcessingRegistry;
import org.junit.Test;
import static org.junit.Assert.*;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;

/**
 * JUnit test of Reproject process
 *
 * @author Quentin Boileau
 * @module
 */
public class ReprojectTest extends AbstractProcessTest {

    private static final GeometryFactory geometryFactory = org.geotoolkit.geometry.jts.JTS.getFactory();
    private static FeatureType type;

    public ReprojectTest() {
        super("vector:reproject");
    }

    @Test
    public void testReprojection() throws FactoryException, ProcessException, NoSuchIdentifierException {
        // Inputs
        final FeatureCollection featureList = buildFeatureList();
        // Process
        ProcessDescriptor desc = ProcessFinder.getProcessDescriptor(GeotkProcessingRegistry.NAME,"vector:reproject");

        ParameterValueGroup in = desc.getInputDescriptor().createValue();
        in.parameter("feature_in").setValue(featureList);
        in.parameter("crs_in").setValue(CRS.forCode("EPSG:27582"));
        org.geotoolkit.process.Process proc = desc.createProcess(in);

        //FeatureCollection out
        final FeatureCollection resultFC = (FeatureCollection) proc.call().parameter("feature_out").getValue();

        assertEquals(featureList.getIdentifier(), resultFC.getIdentifier());
        assertEquals(resultFC.size(), resultFC.size());
    }

    private static FeatureType createSimpleType() throws NoSuchAuthorityCodeException, FactoryException {
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("IntersectTest");
        ftb.addAttribute(String.class).setName(AttributeConvention.IDENTIFIER_PROPERTY);
        ftb.addAttribute(String.class).setName("name");
        ftb.addAttribute(Geometry.class).setName("geom1").setCRS(CommonCRS.WGS84.geographic()).addRole(AttributeRole.DEFAULT_GEOMETRY);
        //ftb.addAttribute("geom2", Geometry.class, CommonCRS.WGS84.geographic());
        return ftb.build();
    }

    private static FeatureCollection buildFeatureList() throws FactoryException {

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
        //sfb.set("geom2", geometryFactory.createPoint(new Coordinate(3.5, 3.5)));
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
        myFeature1.setPropertyValue(AttributeConvention.IDENTIFIER, "id-02");
        myFeature2.setPropertyValue("name", "feature2");
        myFeature2.setPropertyValue("geom1", geometryFactory.createPolygon(ring, null));
        //sfb.set("geom2", multPt);
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
        myFeature3.setPropertyValue(AttributeConvention.IDENTIFIER, "id-03");
        myFeature3.setPropertyValue("name", "feature3");
        myFeature3.setPropertyValue("geom1", geometryFactory.createPolygon(ring, null));
        //sfb.set("geom2", line);
        featureList.add(myFeature3);

        return featureList;
    }
}
