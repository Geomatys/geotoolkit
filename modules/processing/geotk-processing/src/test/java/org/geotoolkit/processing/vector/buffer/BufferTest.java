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
package org.geotoolkit.processing.vector.buffer;

import org.geotoolkit.process.ProcessException;
import org.opengis.util.NoSuchIdentifierException;
import java.util.ArrayList;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import java.util.concurrent.atomic.AtomicInteger;
import javax.measure.quantity.Length;
import javax.measure.Unit;
import org.apache.sis.measure.Units;
import org.apache.sis.feature.builder.AttributeRole;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.internal.feature.AttributeConvention;
import org.apache.sis.internal.feature.BiFunction;
import org.apache.sis.internal.feature.FeatureLoop;
import org.apache.sis.internal.feature.Predicate;
import org.geotoolkit.data.FeatureStoreUtilities;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessFinder;
import org.geotoolkit.processing.vector.AbstractProcessTest;
import org.apache.sis.referencing.CRS;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.util.FactoryException;


import org.junit.Test;
import static org.junit.Assert.*;
import org.opengis.feature.AttributeType;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.feature.PropertyType;


/**
 * JUnit test douglas peucker simplification on FeatureCollection
 *
 * @author Quentin Boileau
 * @module
 */
public class BufferTest extends AbstractProcessTest {

    private static GeometryFactory geometryFactory;
    private static FeatureType type;
    private static final Double distance = new Double(5);

    public BufferTest() {
        super("buffer");
    }

    /**
     * Test Buffer process Tests realized : - Same FeatureType between the output FeatureCollection and a generated
     * FeatureCollection - Same Features ID - Same FeatureCollection size - Output FeatureCollection geometry contains
     * input FeatureCollection geometry
     */
    @Test
    public void testBuffer() throws ProcessException, NoSuchIdentifierException, FactoryException {

        // Inputs
        final FeatureCollection featureList = buildFeatureCollectionInput1();
        Unit<Length> unit = Units.METRE;

        // Process
        ProcessDescriptor desc = ProcessFinder.getProcessDescriptor("vector", "buffer");

        ParameterValueGroup in = desc.getInputDescriptor().createValue();
        in.parameter("feature_in").setValue(featureList);
        in.parameter("distance_in").setValue(distance);
        //in.parameter("unit_in").setValue(unit);
        in.parameter("lenient_transform_in").setValue(true);
        org.geotoolkit.process.Process proc = desc.createProcess(in);


        //Features out
        final FeatureCollection featureListOut = (FeatureCollection) proc.call().parameter("feature_out").getValue();

        //Expected Features out
        final FeatureCollection featureListResult = buildFeatureCollectionResult();

        assertEquals(featureListResult.getFeatureType(), featureListOut.getFeatureType());
        assertEquals(featureListResult.getID(), featureListOut.getID());
        assertEquals(featureListResult.size(), featureListOut.size());

        double precision = 0.01;
        //geometry out list
        final FeatureIterator iteratorOut = featureListOut.iterator();
        final ArrayList<Geometry> geomsOut = new ArrayList<>();
        final AtomicInteger itOut = new AtomicInteger();
        while (iteratorOut.hasNext()) {
            Feature featureOut = iteratorOut.next();
            FeatureLoop.loop(featureOut, (Predicate)null, new BiFunction<PropertyType, Object, Object>() {
                @Override
                public Object apply(PropertyType t, Object u) {
                    if(t instanceof AttributeType && Geometry.class.isAssignableFrom(((AttributeType)t).getValueClass())){
                        geomsOut.add(itOut.getAndIncrement(), (Geometry) u);
                    }
                    return u;
                }
            });
        }
        //geometry input list
        final FeatureIterator listIterator = featureList.iterator();
        final ArrayList<Geometry> geomsInput = new ArrayList<>();
        final AtomicInteger itResult = new AtomicInteger();
        while (listIterator.hasNext()) {
            Feature feature = listIterator.next();
            FeatureLoop.loop(feature, (Predicate)null, new BiFunction<PropertyType, Object, Object>() {
                @Override
                public Object apply(PropertyType t, Object u) {
                    if(t instanceof AttributeType && Geometry.class.isAssignableFrom(((AttributeType)t).getValueClass())){
                        geomsInput.add(itResult.getAndIncrement(), (Geometry)u);
                    }
                    return u;
                }
            });
        }

        assertEquals(geomsInput.size(), geomsOut.size());
        for (int i = 0; i < geomsInput.size(); i++) {
            Geometry gOut = geomsOut.get(i);
            Geometry gInput = geomsInput.get(i);

            assertTrue(gOut.contains(gInput));
        }
    }

    private static FeatureType createSimpleType() throws NoSuchAuthorityCodeException, FactoryException {
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("BufferTest");
        ftb.addAttribute(String.class).setName(AttributeConvention.IDENTIFIER_PROPERTY);
        ftb.addAttribute(String.class).setName("name");
        ftb.addAttribute(Geometry.class).setName("position").setCRS(CRS.forCode("EPSG:3395")).addRole(AttributeRole.DEFAULT_GEOMETRY);

        final FeatureType sft = ftb.build();
        return sft;
    }

    private static FeatureCollection buildFeatureCollectionInput1() throws FactoryException {
        type = createSimpleType();

        final FeatureCollection featureList = FeatureStoreUtilities.collection("", type);

        geometryFactory = new GeometryFactory();

        Feature myFeature1 = type.newInstance();
        myFeature1.setPropertyValue("@identifier", "id-01");
        myFeature1.setPropertyValue("name", "Point");
        myFeature1.setPropertyValue("position", geometryFactory.createPoint(new Coordinate(-10.0, 10.0)));
        featureList.add(myFeature1);


        Feature myFeature2 = type.newInstance();
        LineString line = geometryFactory.createLineString(
                new Coordinate[]{
                    new Coordinate(30.0, 40.0),
                    new Coordinate(50.0, 60.0),
                    new Coordinate(60.0, 50.0),
                    new Coordinate(70.0, 40.0)
                });
        myFeature2.setPropertyValue("@identifier", "id-02");
        myFeature2.setPropertyValue("name", "LineString");
        myFeature2.setPropertyValue("position", line);
        featureList.add(myFeature2);


        Feature myFeature3 = type.newInstance();
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
        myFeature3.setPropertyValue("@identifier", "id-03");
        myFeature3.setPropertyValue("name", "Polygone");
        myFeature3.setPropertyValue("position", geometryFactory.createPolygon(ring2, null));
        featureList.add(myFeature3);

        return featureList;
    }

    private static FeatureCollection buildFeatureCollectionResult() throws FactoryException {
        type = createSimpleType();
        final FeatureCollection featureList = FeatureStoreUtilities.collection("", type);

        geometryFactory = new GeometryFactory();

        Feature myFeature1 = type.newInstance();
        myFeature1.setPropertyValue("@identifier", "id-01");
        myFeature1.setPropertyValue("name", "Point");
        myFeature1.setPropertyValue("position", geometryFactory.createPoint(new Coordinate(-10.0, 10.0)).buffer(distance));
        featureList.add(myFeature1);


        Feature myFeature2 = type.newInstance();
        LineString line = geometryFactory.createLineString(
                new Coordinate[]{
                    new Coordinate(30.0, 40.0),
                    new Coordinate(50.0, 60.0),
                    new Coordinate(60.0, 50.0),
                    new Coordinate(70.0, 40.0)
                });
        myFeature2.setPropertyValue("@identifier", "id-02");
        myFeature2.setPropertyValue("name", "LineString");
        myFeature2.setPropertyValue("position", line.buffer(distance));
        featureList.add(myFeature2);


        Feature myFeature3 = type.newInstance();
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

        myFeature3.setPropertyValue("@identifier", "id-03");
        myFeature3.setPropertyValue("name", "Polygone");
        myFeature3.setPropertyValue("position", geometryFactory.createPolygon(ring2, null).buffer(distance));
        featureList.add(myFeature3);

        return featureList;

    }
}
