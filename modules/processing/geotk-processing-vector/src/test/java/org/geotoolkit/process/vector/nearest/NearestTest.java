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
package org.geotoolkit.process.vector.nearest;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;

import org.geotoolkit.data.DataUtilities;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.feature.FeatureUtilities;
import org.geotoolkit.feature.simple.SimpleFeatureBuilder;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessFinder;
import org.geotoolkit.process.vector.AbstractProcessTest;
import org.geotoolkit.referencing.CRS;

import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.FactoryException;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Junit test of nearest process
 * @author Quentin Boileau
 * @module pending
 */
public class NearestTest extends AbstractProcessTest{

    private static SimpleFeatureBuilder sfb;
    private static final GeometryFactory geometryFactory = new GeometryFactory();
    private static SimpleFeatureType type;

    public NearestTest() {
        super("nearest");
    }


    /**
     * Test nearest process
     */
    @Test
    public void testNearest() throws FactoryException {

        // Inputs
        final FeatureCollection<?> featureList = buildFeatureList();
        final Geometry geom = buildIntersectionGeometry();

        // Process
        ProcessDescriptor desc = ProcessFinder.getProcessDescriptor("vector", "nearest");
        org.geotoolkit.process.Process proc = desc.createProcess();

        ParameterValueGroup in = desc.getInputDescriptor().createValue();
        in.parameter("feature_in").setValue(featureList);
        in.parameter("geometry_in").setValue(geom);

        proc.setInput(in);

        proc.run();

        //Features out
        final FeatureCollection<?> featureListOut = (FeatureCollection<?>) proc.getOutput().parameter("feature_out").getValue();

        //Expected Features out
        final FeatureCollection<?> featureListResult = buildResultList();

        assertEquals(featureListOut.getFeatureType(), featureListResult.getFeatureType());
        assertEquals(featureListOut.size(), featureListResult.size());
        assertTrue(featureListOut.containsAll(featureListResult));
    }

    private static SimpleFeatureType createSimpleType() throws  FactoryException {
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("IntersectTest");
        ftb.add("name", String.class);
        ftb.add("geom1", Geometry.class, CRS.decode("EPSG:3395"));

        ftb.setDefaultGeometry("geom1");
        final SimpleFeatureType sft = ftb.buildSimpleFeatureType();
        return sft;
    }

    private static FeatureCollection<?> buildFeatureList() throws FactoryException {

        type = createSimpleType();
        

        final FeatureCollection<Feature> featureList = DataUtilities.collection("nearest", type);

        final Feature feature1 = FeatureUtilities.defaultFeature(type, "id-1");
        feature1.getProperty("name").setValue("feature1");
        feature1.getProperty("geom1").setValue(geometryFactory.createPoint(new Coordinate(2, 2)));
        featureList.add(feature1);

        final Feature feature2 = FeatureUtilities.defaultFeature(type, "id-2");
        feature2.getProperty("name").setValue("feature2");
        feature2.getProperty("geom1").setValue(geometryFactory.createPoint(new Coordinate(3, 1)));
        featureList.add(feature2);

        final Feature feature3 = FeatureUtilities.defaultFeature(type, "id-3");
        feature3.getProperty("name").setValue("feature3");
        feature3.getProperty("geom1").setValue(geometryFactory.createPoint(new Coordinate(3, 4)));
        featureList.add(feature3);

        final Feature feature4 = FeatureUtilities.defaultFeature(type, "id-4");
        feature4.getProperty("name").setValue("feature4");
        feature4.getProperty("geom1").setValue(geometryFactory.createPoint(new Coordinate(5, 4)));
        featureList.add(feature4);

        final Feature feature5 = FeatureUtilities.defaultFeature(type, "id-5");
        feature5.getProperty("name").setValue("feature5");
        feature5.getProperty("geom1").setValue(geometryFactory.createPoint(new Coordinate(6, 2)));
        featureList.add(feature5);

        final Feature feature6 = FeatureUtilities.defaultFeature(type, "id-6");
        feature6.getProperty("name").setValue("feature6");
        feature6.getProperty("geom1").setValue(geometryFactory.createPoint(new Coordinate(6, 1)));
        featureList.add(feature6);

        final Feature feature7 = FeatureUtilities.defaultFeature(type, "id-7");
        feature7.getProperty("name").setValue("feature7");
        feature7.getProperty("geom1").setValue(geometryFactory.createPoint(new Coordinate(7, 3)));
        featureList.add(feature7);

        return featureList;
    }

    private static FeatureCollection<?> buildResultList() throws FactoryException {

        type = createSimpleType();
      
        final FeatureCollection<Feature> featureList = DataUtilities.collection("nearest", type);

        final Feature feature4 = FeatureUtilities.defaultFeature(type, "id-4");
        feature4.getProperty("name").setValue("feature4");
        feature4.getProperty("geom1").setValue(geometryFactory.createPoint(new Coordinate(5, 4)));
        featureList.add(feature4);

        final Feature feature5 = FeatureUtilities.defaultFeature(type, "id-5");
        feature5.getProperty("name").setValue("feature5");
        feature5.getProperty("geom1").setValue(geometryFactory.createPoint(new Coordinate(6, 2)));
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
        geom.setUserData(CRS.decode("EPSG:3395"));
        return geom;
    }
}
