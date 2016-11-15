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
package org.geotoolkit.processing.vector.nearest;

import org.geotoolkit.process.ProcessException;
import org.opengis.util.NoSuchIdentifierException;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import org.apache.sis.feature.builder.AttributeRole;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.internal.feature.AttributeConvention;

import org.geotoolkit.data.FeatureStoreUtilities;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessFinder;
import org.geotoolkit.processing.vector.AbstractProcessTest;
import org.apache.sis.referencing.CRS;

import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.FactoryException;

import org.junit.Test;
import static org.junit.Assert.*;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;

/**
 * JUnit test of nearest process
 * @author Quentin Boileau
 * @module pending
 */
public class NearestTest extends AbstractProcessTest {

    private static final GeometryFactory geometryFactory = new GeometryFactory();
    private static FeatureType type;

    public NearestTest() {
        super("nearest");
    }


    /**
     * Test nearest process
     */
    @Test
    public void testNearest() throws FactoryException, ProcessException, NoSuchIdentifierException {

        // Inputs
        final FeatureCollection featureList = buildFeatureList();
        final Geometry geom = buildIntersectionGeometry();

        // Process
        ProcessDescriptor desc = ProcessFinder.getProcessDescriptor("vector", "nearest");

        ParameterValueGroup in = desc.getInputDescriptor().createValue();
        in.parameter("feature_in").setValue(featureList);
        in.parameter("geometry_in").setValue(geom);
        org.geotoolkit.process.Process proc = desc.createProcess(in);

        //Features out
        final FeatureCollection featureListOut = (FeatureCollection) proc.call().parameter("feature_out").getValue();

        //Expected Features out
        final FeatureCollection featureListResult = buildResultList();

        compare(featureListResult,featureListOut);
    }

    private static FeatureType createSimpleType() throws  FactoryException {
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("IntersectTest");
        ftb.addAttribute(String.class).setName(AttributeConvention.IDENTIFIER_PROPERTY);
        ftb.addAttribute(String.class).setName("name");
        ftb.addAttribute(Geometry.class).setName("geom1").setCRS(CRS.forCode("EPSG:3395")).addRole(AttributeRole.DEFAULT_GEOMETRY);
        return ftb.build();
    }

    private static FeatureCollection buildFeatureList() throws FactoryException {

        type = createSimpleType();

        final FeatureCollection featureList = FeatureStoreUtilities.collection("nearest", type);

        final Feature feature1 = type.newInstance();
        feature1.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(),"id-1");
        feature1.setPropertyValue("name","feature1");
        feature1.setPropertyValue("geom1",geometryFactory.createPoint(new Coordinate(2, 2)));
        featureList.add(feature1);

        final Feature feature2 = type.newInstance();
        feature2.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(),"id-2");
        feature2.setPropertyValue("name","feature2");
        feature2.setPropertyValue("geom1",geometryFactory.createPoint(new Coordinate(3, 1)));
        featureList.add(feature2);

        final Feature feature3 = type.newInstance();
        feature3.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(),"id-3");
        feature3.setPropertyValue("name","feature3");
        feature3.setPropertyValue("geom1",geometryFactory.createPoint(new Coordinate(3, 4)));
        featureList.add(feature3);

        final Feature feature4 = type.newInstance();
        feature4.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(),"id-4");
        feature4.setPropertyValue("name","feature4");
        feature4.setPropertyValue("geom1",geometryFactory.createPoint(new Coordinate(5, 4)));
        featureList.add(feature4);

        final Feature feature5 = type.newInstance();
        feature5.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(),"id-5");
        feature5.setPropertyValue("name","feature5");
        feature5.setPropertyValue("geom1",geometryFactory.createPoint(new Coordinate(6, 2)));
        featureList.add(feature5);

        final Feature feature6 = type.newInstance();
        feature6.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(),"id-6");
        feature6.setPropertyValue("name","feature6");
        feature6.setPropertyValue("geom1",geometryFactory.createPoint(new Coordinate(6, 1)));
        featureList.add(feature6);

        final Feature feature7 = type.newInstance();
        feature7.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(),"id-7");
        feature7.setPropertyValue("name","feature7");
        feature7.setPropertyValue("geom1",geometryFactory.createPoint(new Coordinate(7, 3)));
        featureList.add(feature7);

        return featureList;
    }

    private static FeatureCollection buildResultList() throws FactoryException {

        type = createSimpleType();

        final FeatureCollection featureList = FeatureStoreUtilities.collection("id", type);

        final Feature feature4 = type.newInstance();
        feature4.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(),"id-4");
        feature4.setPropertyValue("name","feature4");
        feature4.setPropertyValue("geom1",geometryFactory.createPoint(new Coordinate(5, 4)));
        featureList.add(feature4);

        final Feature feature5 = type.newInstance();
        feature5.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(),"id-5");
        feature5.setPropertyValue("name","feature5");
        feature5.setPropertyValue("geom1",geometryFactory.createPoint(new Coordinate(6, 2)));
        featureList.add(feature5);

        return featureList;
    }

    private Geometry buildIntersectionGeometry() throws FactoryException {

        LinearRing ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(4.0, 2.0),
                    new Coordinate(4.0, 3.0),
                    new Coordinate(5.0, 3.0),
                    new Coordinate(5.0, 2.0),
                    new Coordinate(4.0, 2.0)
                });

        Geometry geom = geometryFactory.createPolygon(ring, null);
        geom.setUserData(CRS.forCode("EPSG:3395"));
        return geom;
    }
}
