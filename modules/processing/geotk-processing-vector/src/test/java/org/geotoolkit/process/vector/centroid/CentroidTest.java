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
package org.geotoolkit.process.vector.centroid;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Point;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.geotoolkit.data.DataUtilities;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.feature.simple.SimpleFeatureBuilder;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessFinder;
import org.geotoolkit.referencing.CRS;

import org.junit.Test;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.util.FactoryException;

import static org.junit.Assert.*;

/**
 * Junit test of Centroid process
 * @author Quentin Boileau
 * @module pending
 */
public class CentroidTest {

    private static SimpleFeatureBuilder sfb;
    private static GeometryFactory geometryFactory;
    private static SimpleFeatureType type;

    @Test
    public void testCentroid() {

        // Features in
        final FeatureCollection<?> featureList = buildFeatureList();

        // Process
        ProcessDescriptor desc = ProcessFinder.getProcessDescriptor("vector", "centroid");
        org.geotoolkit.process.Process proc = desc.createProcess();

        ParameterValueGroup in = desc.getInputDescriptor().createValue();
        in.parameter("feature_in").setValue(featureList);

        proc.setInput(in);
        proc.run();

        //Features out
        final FeatureCollection<?> featureListOut = (FeatureCollection<?>) proc.getOutput().parameter("feature_out").getValue();

        //Expected Features out
        final FeatureCollection<?> featureListResult = buildResultList();

        assertEquals(featureListOut.getFeatureType(), featureListResult.getFeatureType());
        assertEquals(featureListOut.getID(), featureListResult.getID());
        assertEquals(featureListOut.size(), featureListResult.size());
        assertTrue(featureListOut.containsAll(featureListResult));
    }

    private static SimpleFeatureType createSimpleType() throws NoSuchAuthorityCodeException, FactoryException {
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("Building");
        ftb.add("name", String.class);
        ftb.add("position", LinearRing.class, CRS.decode("EPSG:3395"));
        ftb.add("height", Integer.class);

        ftb.setDefaultGeometry("position");
        final SimpleFeatureType sft = ftb.buildSimpleFeatureType();
        return sft;
    }

    private static SimpleFeatureType createSimpleResultType() throws NoSuchAuthorityCodeException, FactoryException {
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("Building");
        ftb.add("name", String.class);
        ftb.add("position", Point.class, CRS.decode("EPSG:3395"));
        ftb.add("height", Integer.class);

        ftb.setDefaultGeometry("position");
        final SimpleFeatureType sft = ftb.buildSimpleFeatureType();
        return sft;
    }

    private static FeatureCollection<?> buildFeatureList() {

        try {
            type = createSimpleType();
        } catch (NoSuchAuthorityCodeException ex) {
            Logger.getLogger(CentroidTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FactoryException ex) {
            Logger.getLogger(CentroidTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        final FeatureCollection<Feature> featureList = DataUtilities.collection("", type);

        for (int i = 0; i < 5; i++) {

            Feature myFeature;
            geometryFactory = new GeometryFactory();



            sfb = new SimpleFeatureBuilder(type);
            sfb.set("name", "Building" + i);
            sfb.set("height", 12);
            sfb.set("position", geometryFactory.createLinearRing(
                    new Coordinate[]{
                        new Coordinate(5.0, 18.0),
                        new Coordinate(10.0, 23.0),
                        new Coordinate(10.0, 26.0),
                        new Coordinate(5.0, 18.0)
                    }));

            myFeature = sfb.buildFeature("id-0" + i);

            featureList.add(myFeature);
        }

        return featureList;
    }

    private static FeatureCollection<?> buildResultList() {

        try {
            type = createSimpleResultType();
        } catch (NoSuchAuthorityCodeException ex) {
            Logger.getLogger(CentroidTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FactoryException ex) {
            Logger.getLogger(CentroidTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        final FeatureCollection<Feature> featureList = DataUtilities.collection("", type);

        for (int i = 0; i < 5; i++) {

            Feature myFeature;
            geometryFactory = new GeometryFactory();

            LinearRing ring = geometryFactory.createLinearRing(
                    new Coordinate[]{
                        new Coordinate(5.0, 18.0),
                        new Coordinate(10.0, 23.0),
                        new Coordinate(10.0, 26.0),
                        new Coordinate(5.0, 18.0)
                    });


            sfb = new SimpleFeatureBuilder(type);
            sfb.set("name", "Building" + i);
            sfb.set("height", 12);
            sfb.set("position", ring.getCentroid());
            myFeature = sfb.buildFeature("id-0" + i);

            featureList.add(myFeature);
        }

        return featureList;
    }
}
