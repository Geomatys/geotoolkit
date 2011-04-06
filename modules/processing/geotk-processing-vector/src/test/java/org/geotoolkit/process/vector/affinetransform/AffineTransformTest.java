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
package org.geotoolkit.process.vector.affinetransform;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiPoint;
import java.awt.geom.AffineTransform;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.geotoolkit.data.DataUtilities;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.feature.simple.SimpleFeatureBuilder;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessFinder;
import org.geotoolkit.process.vector.AbstractProcessTest;
import org.geotoolkit.referencing.CRS;

import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.util.FactoryException;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Junit test of AffineTransform process
 * @author Quentin Boileau
 * @module pending
 */
public class AffineTransformTest extends AbstractProcessTest{

    private static SimpleFeatureBuilder sfb;
    private static final GeometryFactory GF = new GeometryFactory();
    private static SimpleFeatureType type;

    public AffineTransformTest() {
        super("affinetransform");
    }

    @Test
    public void testAffineTransform() {

        // Inputs
        final FeatureCollection<?> featureList = buildFeatureList();
        final AffineTransform transform = new AffineTransform();
        transform.setToTranslation(100, 100);

        // Process
        final ProcessDescriptor desc = ProcessFinder.getProcessDescriptor("vector", "affinetransform");
        final org.geotoolkit.process.Process proc = desc.createProcess();

        final ParameterValueGroup in = desc.getInputDescriptor().createValue();
        in.parameter("feature_in").setValue(featureList);
        in.parameter("transform_in").setValue(transform);
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
        ftb.setName("AffineTransformTest");
        ftb.add("type", String.class);
        ftb.add("geom1", Geometry.class, CRS.decode("EPSG:3395"));
        ftb.add("geom2", Geometry.class, CRS.decode("EPSG:3395"));
        ftb.add("color", String.class);
        ftb.add("height", Integer.class);

        ftb.setDefaultGeometry("geom1");
        final SimpleFeatureType sft = ftb.buildSimpleFeatureType();
        return sft;
    }

    private static SimpleFeatureType createSimpleResultType() throws NoSuchAuthorityCodeException, FactoryException {
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("RegroupTest");
        ftb.add("geom1", Geometry.class, CRS.decode("EPSG:3395"));
        ftb.add("height", Integer.class);

        ftb.setDefaultGeometry("geom1");
        final SimpleFeatureType sft = ftb.buildSimpleFeatureType();
        return sft;
    }

    private static FeatureCollection<?> buildFeatureList() {

        try {
            type = createSimpleType();
        } catch (NoSuchAuthorityCodeException ex) {
            Logger.getLogger(AffineTransformTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FactoryException ex) {
            Logger.getLogger(AffineTransformTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        final FeatureCollection<Feature> featureList = DataUtilities.collection("", type);


        Feature myFeature1;
        LinearRing ring = GF.createLinearRing(
                new Coordinate[]{
                    new Coordinate(3.0, 3.0),
                    new Coordinate(3.0, 4.0),
                    new Coordinate(4.0, 4.0),
                    new Coordinate(4.0, 3.0),
                    new Coordinate(3.0, 3.0)
                });
        sfb = new SimpleFeatureBuilder(type);
        sfb.set("color", "grey");
        sfb.set("height", "9");
        sfb.set("type", "church");
        sfb.set("geom1", GF.createPolygon(ring, null));
        sfb.set("geom2", GF.createPoint(new Coordinate(3.5, 3.5)));
        myFeature1 = sfb.buildFeature("id-01");
        featureList.add(myFeature1);

        Feature myFeature2;
        ring = GF.createLinearRing(
                new Coordinate[]{
                    new Coordinate(5.0, 6.0),
                    new Coordinate(5.0, 7.0),
                    new Coordinate(6.0, 7.0),
                    new Coordinate(6.0, 6.0),
                    new Coordinate(5.0, 6.0)
                });
        MultiPoint multPt = GF.createMultiPoint(
                new Coordinate[]{
                    new Coordinate(5.0, 4.0),
                    new Coordinate(3.0, 6.0),
                    new Coordinate(4.0, 7.0),
                    new Coordinate(5.5, 6.5)
                });
        sfb = new SimpleFeatureBuilder(type);
        sfb.set("color", "blue");
        sfb.set("height", "3");
        sfb.set("type", "office");
        sfb.set("geom1", GF.createPolygon(ring, null));
        sfb.set("geom2", multPt);
        myFeature2 = sfb.buildFeature("id-02");
        featureList.add(myFeature2);

        Feature myFeature3;
        ring = GF.createLinearRing(
                new Coordinate[]{
                    new Coordinate(9.0, 4.0),
                    new Coordinate(9.0, 5.0),
                    new Coordinate(11.0, 5.0),
                    new Coordinate(11.0, 4.0),
                    new Coordinate(9.0, 4.0)
                });
        LineString line = GF.createLineString(
                new Coordinate[]{
                    new Coordinate(7.0, 0.0),
                    new Coordinate(9.0, 3.0)
                });
        sfb = new SimpleFeatureBuilder(type);
        sfb.set("color", "black");
        sfb.set("height", "2");
        sfb.set("type", "office");
        sfb.set("geom1", GF.createPolygon(ring, null));
        sfb.set("geom2", line);
        myFeature3 = sfb.buildFeature("id-03");
        featureList.add(myFeature3);

        Feature myFeature4;
        ring = GF.createLinearRing(
                new Coordinate[]{
                    new Coordinate(2.0, 2.0),
                    new Coordinate(2.0, 3.0),
                    new Coordinate(3.0, 3.0),
                    new Coordinate(3.0, 2.0),
                    new Coordinate(2.0, 2.0)
                });
        sfb = new SimpleFeatureBuilder(type);
        sfb.set("color", "yellow");
        sfb.set("height", "2");
        sfb.set("type", "post office");
        sfb.set("geom1", GF.createPolygon(ring, null));
        sfb.set("geom2", GF.createPoint(new Coordinate(10, 5)));
        myFeature4 = sfb.buildFeature("id-04");
        featureList.add(myFeature4);

        Feature myFeature5;
        ring = GF.createLinearRing(
                new Coordinate[]{
                   new Coordinate(6.0, 7.0),
                    new Coordinate(6.0, 8.0),
                    new Coordinate(7.0, 8.0),
                    new Coordinate(7.0, 7.0),
                    new Coordinate(6.0, 7.0)
                });
        line = GF.createLineString(
                new Coordinate[]{
                    new Coordinate(8.0, 0.0),
                    new Coordinate(5.0, 3.0)
                });
        sfb = new SimpleFeatureBuilder(type);
        sfb.set("color", "yellow");
        sfb.set("height", "9");
        sfb.set("type", "office");
        sfb.set("geom1", GF.createPolygon(ring, null));
        sfb.set("geom2", line);
        myFeature5 = sfb.buildFeature("id-05");
        featureList.add(myFeature5);

        Feature myFeature6;
        ring = GF.createLinearRing(
                new Coordinate[]{
                     new Coordinate(15.0, 10.0),
                    new Coordinate(15.0, 11.0),
                    new Coordinate(16.0, 11.0),
                    new Coordinate(16.0, 10.0),
                    new Coordinate(15.0, 10.0)
                });
        LinearRing ring2 = GF.createLinearRing(
                new Coordinate[]{
                    new Coordinate(8.0, 0.0),
                    new Coordinate(9.0, 6.0),
                    new Coordinate(10.0, 2.0),
                    new Coordinate(8.0, 0.0)
                });
        sfb = new SimpleFeatureBuilder(type);
        sfb.set("color", "black");
        sfb.set("height", "2");
        sfb.set("type", "church");
        sfb.set("geom1", GF.createPolygon(ring, null));
        sfb.set("geom2",GF.createPolygon(ring2, null));
        myFeature6 = sfb.buildFeature("id-06");
        featureList.add(myFeature6);

        return featureList;
    }

  

    private static FeatureCollection<?> buildResultList() {


       try {
            type = createSimpleType();
        } catch (NoSuchAuthorityCodeException ex) {
            Logger.getLogger(AffineTransformTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FactoryException ex) {
            Logger.getLogger(AffineTransformTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        final FeatureCollection<Feature> featureList = DataUtilities.collection("", type);


        Feature myFeature1;
        LinearRing ring = GF.createLinearRing(
                new Coordinate[]{
                    new Coordinate(103.0, 103.0),
                    new Coordinate(103.0, 104.0),
                    new Coordinate(104.0, 104.0),
                    new Coordinate(104.0, 103.0),
                    new Coordinate(103.0, 103.0)
                });
        sfb = new SimpleFeatureBuilder(type);
        sfb.set("color", "grey");
        sfb.set("height", "9");
        sfb.set("type", "church");
        sfb.set("geom1", GF.createPolygon(ring, null));
        sfb.set("geom2", GF.createPoint(new Coordinate(103.5, 103.5)));
        myFeature1 = sfb.buildFeature("id-01");
        featureList.add(myFeature1);

        Feature myFeature2;
        ring = GF.createLinearRing(
                new Coordinate[]{
                    new Coordinate(105.0, 106.0),
                    new Coordinate(105.0, 107.0),
                    new Coordinate(106.0, 107.0),
                    new Coordinate(106.0, 106.0),
                    new Coordinate(105.0, 106.0)
                });
        MultiPoint multPt = GF.createMultiPoint(
                new Coordinate[]{
                    new Coordinate(105.0, 104.0),
                    new Coordinate(103.0, 106.0),
                    new Coordinate(104.0,107.0),
                    new Coordinate(105.5,106.5)
                });
        sfb = new SimpleFeatureBuilder(type);
        sfb.set("color", "blue");
        sfb.set("height", "3");
        sfb.set("type", "office");
        sfb.set("geom1", GF.createPolygon(ring, null));
        sfb.set("geom2", multPt);
        myFeature2 = sfb.buildFeature("id-02");
        featureList.add(myFeature2);

        Feature myFeature3;
        ring = GF.createLinearRing(
                new Coordinate[]{
                    new Coordinate(109.0, 104.0),
                    new Coordinate(109.0, 105.0),
                    new Coordinate(111.0, 105.0),
                    new Coordinate(111.0, 104.0),
                    new Coordinate(109.0, 104.0)
                });
        LineString line = GF.createLineString(
                new Coordinate[]{
                    new Coordinate(107.0, 100.0),
                    new Coordinate(109.0, 103.0)
                });
        sfb = new SimpleFeatureBuilder(type);
        sfb.set("color", "black");
        sfb.set("height", "2");
        sfb.set("type", "office");
        sfb.set("geom1", GF.createPolygon(ring, null));
        sfb.set("geom2", line);
        myFeature3 = sfb.buildFeature("id-03");
        featureList.add(myFeature3);

        Feature myFeature4;
        ring = GF.createLinearRing(
                new Coordinate[]{
                    new Coordinate(102.0, 102.0),
                    new Coordinate(102.0, 103.0),
                    new Coordinate(103.0, 103.0),
                    new Coordinate(103.0, 102.0),
                    new Coordinate(102.0, 102.0)
                });
        sfb = new SimpleFeatureBuilder(type);
        sfb.set("color", "yellow");
        sfb.set("height", "2");
        sfb.set("type", "post office");
        sfb.set("geom1", GF.createPolygon(ring, null));
        sfb.set("geom2", GF.createPoint(new Coordinate(110, 105)));
        myFeature4 = sfb.buildFeature("id-04");
        featureList.add(myFeature4);

        Feature myFeature5;
        ring = GF.createLinearRing(
                new Coordinate[]{
                   new Coordinate(106.0, 107.0),
                    new Coordinate(106.0, 108.0),
                    new Coordinate(107.0, 108.0),
                    new Coordinate(107.0, 107.0),
                    new Coordinate(106.0, 107.0)
                });
        line = GF.createLineString(
                new Coordinate[]{
                    new Coordinate(108.0, 100.0),
                    new Coordinate(105.0, 103.0)
                });
        sfb = new SimpleFeatureBuilder(type);
        sfb.set("color", "yellow");
        sfb.set("height", "9");
        sfb.set("type", "office");
        sfb.set("geom1", GF.createPolygon(ring, null));
        sfb.set("geom2", line);
        myFeature5 = sfb.buildFeature("id-05");
        featureList.add(myFeature5);

        Feature myFeature6;
        ring = GF.createLinearRing(
                new Coordinate[]{
                     new Coordinate(115.0, 110.0),
                    new Coordinate(115.0, 111.0),
                    new Coordinate(116.0, 111.0),
                    new Coordinate(116.0, 110.0),
                    new Coordinate(115.0, 110.0)
                });
        LinearRing ring2 = GF.createLinearRing(
                new Coordinate[]{
                    new Coordinate(108.0, 100.0),
                    new Coordinate(109.0, 106.0),
                    new Coordinate(110.0, 102.0),
                    new Coordinate(108.0, 100.0)
                });
        sfb = new SimpleFeatureBuilder(type);
        sfb.set("color", "black");
        sfb.set("height", "2");
        sfb.set("type", "church");
        sfb.set("geom1", GF.createPolygon(ring, null));
        sfb.set("geom2",GF.createPolygon(ring2, null));
        myFeature6 = sfb.buildFeature("id-06");
        featureList.add(myFeature6);

        return featureList;
    }

}
