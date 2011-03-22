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
package org.geotoolkit.process.vector.buffer;

import java.util.ArrayList;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.measure.quantity.Length;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;
import org.geotoolkit.data.DataUtilities;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.feature.simple.SimpleFeatureBuilder;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessFinder;
import org.geotoolkit.referencing.CRS;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.util.FactoryException;


import org.junit.Test;
import org.opengis.feature.Property;
import org.opengis.feature.type.GeometryDescriptor;
import static org.junit.Assert.*;

/**
 * Junit test douglas peucker simplification on FeatureCollection
 * @author Quentin Boileau
 * @module pending
 */
public class BufferTest {

    private static SimpleFeatureBuilder sfb;
    private static GeometryFactory geometryFactory;
    private static SimpleFeatureType type;
    private static final Double distance = new Double(5);

    /**
     * Test Buffer process
     * Tests realized :
     * - Same FeatureType between the output FeatureCollection and a generated FeatureCollection
     * - Same Features ID
     * - Same FeatureCollection size
     * - Output FeatureCollection geometry contains input FeatureCollection geometry
     */
    @Test
    public void testBuffer() {

        // Inputs
        final FeatureCollection<?> featureList = buildFeatureCollectionInput1();
        Unit<Length> unit = SI.METRE;

        // Process
        ProcessDescriptor desc = ProcessFinder.getProcessDescriptor("vector", "buffer");
        org.geotoolkit.process.Process proc = desc.createProcess();

        ParameterValueGroup in = desc.getInputDescriptor().createValue();
        in.parameter("feature_in").setValue(featureList);
        in.parameter("distance_in").setValue(distance);
        in.parameter("unit_in").setValue(unit);
        in.parameter("lenient_transform_in").setValue(false);

        proc.setInput(in);

        proc.run();

        //Features out
        final FeatureCollection<?> featureListOut = (FeatureCollection<?>) proc.getOutput().parameter("feature_out").getValue();

        //Expected Features out
        final FeatureCollection<?> featureListResult = buildFeatureCollectionResult();

        assertEquals(featureListResult.getFeatureType(), featureListOut.getFeatureType());
        assertEquals(featureListResult.getID(), featureListOut.getID());
        assertEquals(featureListResult.size(), featureListOut.size());

        double precision = 0.01;
        //geometry out list
        FeatureIterator<?> iteratorOut = featureListOut.iterator();
        ArrayList<Geometry> geomsOut = new ArrayList<Geometry>();
        int itOut = 0;
        while (iteratorOut.hasNext()) {
            Feature featureOut = iteratorOut.next();

            for (Property propertyOut : featureOut.getProperties()) {
                if (propertyOut.getDescriptor() instanceof GeometryDescriptor) {
                    geomsOut.add(itOut++, (Geometry) propertyOut.getValue());
                }
            }
        }
        //geometry input list
        FeatureIterator<?> listIterator = featureList.iterator();
        ArrayList<Geometry> geomsInput = new ArrayList<Geometry>();
        int itResult = 0;
        while (listIterator.hasNext()) {
            Feature feature = listIterator.next();

            for (Property property : feature.getProperties()) {
                if (property.getDescriptor() instanceof GeometryDescriptor) {
                    geomsInput.add(itResult++, (Geometry) property.getValue());
                }
            }
        }
        
        assertEquals(geomsInput.size(), geomsOut.size());
        for (int i = 0; i < geomsInput.size(); i++) {
            Geometry gOut = geomsOut.get(i);
            Geometry gInput = geomsInput.get(i);

            assertTrue(gOut.contains(gInput));
        }
    }

    private static SimpleFeatureType createSimpleType() throws NoSuchAuthorityCodeException, FactoryException {
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("BufferTest");
        ftb.add("name", String.class);
        ftb.add("position", Geometry.class, CRS.decode("EPSG:3395"));

        ftb.setDefaultGeometry("position");
        final SimpleFeatureType sft = ftb.buildSimpleFeatureType();
        return sft;
    }

    private static FeatureCollection<Feature> buildFeatureCollectionInput1() {
        try {
            type = createSimpleType();
        } catch (NoSuchAuthorityCodeException ex) {
            Logger.getLogger(BufferTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FactoryException ex) {
            Logger.getLogger(BufferTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        final FeatureCollection<Feature> featureList = DataUtilities.collection("", type);

        geometryFactory = new GeometryFactory();

        Feature myFeature1;

        sfb = new SimpleFeatureBuilder(type);
        sfb.set("name", "Point");
        sfb.set("position", geometryFactory.createPoint(new Coordinate(-10.0, 10.0)));
        myFeature1 = sfb.buildFeature("id-01");
        featureList.add(myFeature1);


        Feature myFeature2;
        LineString line = geometryFactory.createLineString(
                new Coordinate[]{
                    new Coordinate(30.0, 40.0),
                    new Coordinate(50.0, 60.0),
                    new Coordinate(60.0, 50.0),
                    new Coordinate(70.0, 40.0)
                });
        sfb = new SimpleFeatureBuilder(type);
        sfb.set("name", "LineString");
        sfb.set("position", line);
        myFeature2 = sfb.buildFeature("id-02");
        featureList.add(myFeature2);


        Feature myFeature3;
        LinearRing ring2 = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(-10.0, -10.0),
                    new Coordinate(0.0, -30.0),
                    new Coordinate(-20.0, -20.0),
                    new Coordinate(-30.0, 10.0),
                    new Coordinate(-20.0, 30.0),
                    new Coordinate(0.0, 20.0),
                    new Coordinate(10.0, 10.0),
                    new Coordinate(20.0, -20.0),
                    new Coordinate(10.0, -20.0),
                    new Coordinate(-10.0, -10.0)
                });
        sfb = new SimpleFeatureBuilder(type);
        sfb.set("name", "Polygone");
        sfb.set("position", geometryFactory.createPolygon(ring2, null));
        myFeature3 = sfb.buildFeature("id-03");
        featureList.add(myFeature3);

        return featureList;
    }

    private static FeatureCollection<Feature> buildFeatureCollectionResult() {
        try {
            type = createSimpleType();
        } catch (NoSuchAuthorityCodeException ex) {
            Logger.getLogger(BufferTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FactoryException ex) {
            Logger.getLogger(BufferTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        final FeatureCollection<Feature> featureList = DataUtilities.collection("", type);

        geometryFactory = new GeometryFactory();

        Feature myFeature1;

        sfb = new SimpleFeatureBuilder(type);
        sfb.set("name", "Point");
        sfb.set("position", geometryFactory.createPoint(new Coordinate(-10.0, 10.0)).buffer(distance));
        myFeature1 = sfb.buildFeature("id-01");
        featureList.add(myFeature1);


        Feature myFeature2;
        LineString line = geometryFactory.createLineString(
                new Coordinate[]{
                    new Coordinate(30.0, 40.0),
                    new Coordinate(50.0, 60.0),
                    new Coordinate(60.0, 50.0),
                    new Coordinate(70.0, 40.0)
                });
        sfb = new SimpleFeatureBuilder(type);
        sfb.set("name", "LineString");
        sfb.set("position", line.buffer(distance));
        myFeature2 = sfb.buildFeature("id-02");
        featureList.add(myFeature2);


        Feature myFeature3;
        LinearRing ring2 = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(-10.0, -10.0),
                    new Coordinate(0.0, -30.0),
                    new Coordinate(-20.0, -20.0),
                    new Coordinate(-30.0, 10.0),
                    new Coordinate(-20.0, 30.0),
                    new Coordinate(0.0, 20.0),
                    new Coordinate(10.0, 10.0),
                    new Coordinate(20.0, -20.0),
                    new Coordinate(10.0, -20.0),
                    new Coordinate(-10.0, -10.0)
                });

        sfb = new SimpleFeatureBuilder(type);
        sfb.set("name", "Polygone");
        sfb.set("position", geometryFactory.createPolygon(ring2, null).buffer(distance));
        myFeature3 = sfb.buildFeature("id-03");
        featureList.add(myFeature3);

        return featureList;

    }
}
