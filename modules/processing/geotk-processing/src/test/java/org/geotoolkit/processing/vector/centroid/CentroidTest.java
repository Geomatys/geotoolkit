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
package org.geotoolkit.processing.vector.centroid;

import org.geotoolkit.process.ProcessException;
import org.geotoolkit.processing.vector.AbstractProcessTest;
import org.opengis.util.NoSuchIdentifierException;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Point;

import org.geotoolkit.data.FeatureStoreUtilities;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessFinder;
import org.apache.sis.referencing.CRS;
import org.apache.sis.feature.builder.AttributeRole;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.internal.feature.AttributeConvention;

import org.junit.Test;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.util.FactoryException;

import static org.junit.Assert.*;


/**
 * JUnit test of Centroid process
 *
 * @author Quentin Boileau
 * @module
 */
public class CentroidTest extends AbstractProcessTest {

    private static final GeometryFactory geometryFactory = new GeometryFactory();
    private static FeatureType type;

    public CentroidTest() {
        super("centroid");
    }

    @Test
    public void testCentroid() throws ProcessException, NoSuchIdentifierException, FactoryException {

        // Features in
        final FeatureCollection featureList = buildFeatureList();

        // Process
        ProcessDescriptor desc = ProcessFinder.getProcessDescriptor("vector", "centroid");

        ParameterValueGroup in = desc.getInputDescriptor().createValue();
        in.parameter("feature_in").setValue(featureList);
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
        ftb.addAttribute(LinearRing.class).setName("position").setCRS(CRS.forCode("EPSG:3395")).addRole(AttributeRole.DEFAULT_GEOMETRY);
        ftb.addAttribute(Integer.class).setName("height");
        return ftb.build();
    }

    private static FeatureType createSimpleResultType() throws NoSuchAuthorityCodeException, FactoryException {
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("Building");
        ftb.addAttribute(String.class).setName(AttributeConvention.IDENTIFIER_PROPERTY);
        ftb.addAttribute(String.class).setName("name");
        ftb.addAttribute(Point.class).setName("position").setCRS(CRS.forCode("EPSG:3395")).addRole(AttributeRole.DEFAULT_GEOMETRY);
        ftb.addAttribute(Integer.class).setName("height");
        return ftb.build();
    }

    private static FeatureCollection buildFeatureList() throws FactoryException {

        type = createSimpleType();

        final FeatureCollection featureList = FeatureStoreUtilities.collection("", type);

        for (int i = 0; i < 5; i++) {
            Feature myFeature = type.newInstance();
            myFeature.setPropertyValue("@identifier", "id-0" + i);
            myFeature.setPropertyValue("name", "Building" + i);
            myFeature.setPropertyValue("height", 12);
            myFeature.setPropertyValue("position", geometryFactory.createLinearRing(
                    new Coordinate[]{
                        new Coordinate(5.0, 18.0),
                        new Coordinate(10.0, 23.0),
                        new Coordinate(10.0, 26.0),
                        new Coordinate(5.0, 18.0)
                    }));
            featureList.add(myFeature);
        }

        return featureList;
    }

    private static FeatureCollection buildResultList() throws FactoryException {

        type = createSimpleResultType();

        final FeatureCollection featureList = FeatureStoreUtilities.collection("", type);

        for (int i = 0; i < 5; i++) {

            Feature myFeature = type.newInstance();
            LinearRing ring = geometryFactory.createLinearRing(
                    new Coordinate[]{
                        new Coordinate(5.0, 18.0),
                        new Coordinate(10.0, 23.0),
                        new Coordinate(10.0, 26.0),
                        new Coordinate(5.0, 18.0)
                    });
            myFeature.setPropertyValue("@identifier", "id-0" + i);
            myFeature.setPropertyValue("name", "Building" + i);
            myFeature.setPropertyValue("height", 12);
            myFeature.setPropertyValue("position", ring.getCentroid());
            featureList.add(myFeature);
        }

        return featureList;
    }
}
