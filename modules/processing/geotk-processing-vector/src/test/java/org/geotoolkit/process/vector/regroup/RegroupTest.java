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
package org.geotoolkit.process.vector.regroup;

import org.geotoolkit.process.ProcessException;
import org.opengis.util.NoSuchIdentifierException;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

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
 * JUnit test of Regroup process
 * @author Quentin Boileau
 * @module pending
 */
public class RegroupTest extends AbstractProcessTest{

    private static SimpleFeatureBuilder sfb;
    private static final GeometryFactory GF = new GeometryFactory();
    private static SimpleFeatureType type;

    public RegroupTest() {
        super("regroup");
    }

    //No attribut
    @Test
    public void testRegroupDefault() throws ProcessException, NoSuchIdentifierException{

        // Inputs
        final FeatureCollection<?> featureList = buildFeatureList();
        // Process
        ProcessDescriptor desc = ProcessFinder.getProcessDescriptor("vector", "regroup");

        ParameterValueGroup in = desc.getInputDescriptor().createValue();
        in.parameter("feature_in").setValue(featureList);
        in.parameter("geometry_name").setValue("geom1");
        org.geotoolkit.process.Process proc = desc.createProcess(in);

        //Features out
        final FeatureCollection<?> featureListOut = (FeatureCollection<?>) proc.call().parameter("feature_out").getValue();

        /*for (Feature feature : featureListOut) {
            System.out.println(feature);
        }*/
        //Expected Features out
        /*final FeatureCollection<?> featureListResult = buildResultList2();
        assertEquals(featureListOut.getFeatureType(), featureListResult.getFeatureType());
        assertEquals(featureListOut.getID(), featureListResult.getID());
        assertEquals(featureListOut.size(), featureListResult.size());
        assertTrue(featureListOut.containsAll(featureListResult));*/
    }
    
    @Test
    public void testRegroupDefaultGeometry() throws ProcessException, NoSuchIdentifierException{

        // Inputs
        final FeatureCollection<?> featureList = buildFeatureList();
        // Process
        ProcessDescriptor desc = ProcessFinder.getProcessDescriptor("vector", "regroup");

        ParameterValueGroup in = desc.getInputDescriptor().createValue();
        in.parameter("feature_in").setValue(featureList);
        in.parameter("regroup_attribute").setValue("height");
        org.geotoolkit.process.Process proc = desc.createProcess(in);

        //Features out
        final FeatureCollection<?> featureListOut = (FeatureCollection<?>) proc.call().parameter("feature_out").getValue();

        //Expected Features out
        final FeatureCollection<?> featureListResult = buildResultList1();
        assertEquals(featureListOut.getFeatureType(), featureListResult.getFeatureType());
        assertEquals(featureListOut.getID(), featureListResult.getID());
        assertEquals(featureListOut.size(), featureListResult.size());
        assertTrue(featureListOut.containsAll(featureListResult));
    }

    @Test
    public void testRegroupGeometrySelected() throws ProcessException, NoSuchIdentifierException{

        // Inputs
        final FeatureCollection<?> featureList = buildFeatureList();
        // Process
        ProcessDescriptor desc = ProcessFinder.getProcessDescriptor("vector", "regroup");

        ParameterValueGroup in = desc.getInputDescriptor().createValue();
        in.parameter("feature_in").setValue(featureList);
        in.parameter("regroup_attribute").setValue("type");
        in.parameter("geometry_name").setValue("geom2");
        org.geotoolkit.process.Process proc = desc.createProcess(in);

        //Features out
        final FeatureCollection<?> featureListOut = (FeatureCollection<?>) proc.call().parameter("feature_out").getValue();

        //Expected Features out
        final FeatureCollection<?> featureListResult = buildResultList2();
        assertEquals(featureListOut.getFeatureType(), featureListResult.getFeatureType());
        assertEquals(featureListOut.getID(), featureListResult.getID());
        assertEquals(featureListOut.size(), featureListResult.size());
        assertTrue(featureListOut.containsAll(featureListResult));
    }
    
    

    private static SimpleFeatureType createSimpleType() throws NoSuchAuthorityCodeException, FactoryException {
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("RegroupTest");
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

    private static SimpleFeatureType createSimpleResultType2() throws NoSuchAuthorityCodeException, FactoryException {
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("RegroupTest");

        ftb.add("type", String.class);
        ftb.add("geom2", Geometry.class, CRS.decode("EPSG:3395"));

        ftb.setDefaultGeometry("geom2");
        final SimpleFeatureType sft = ftb.buildSimpleFeatureType();
        return sft;
    }

    
    private static FeatureCollection<?> buildFeatureList() {

        try {
            type = createSimpleType();
        } catch (NoSuchAuthorityCodeException ex) {
            Logger.getLogger(RegroupTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FactoryException ex) {
            Logger.getLogger(RegroupTest.class.getName()).log(Level.SEVERE, null, ex);
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

  

    private static FeatureCollection<?> buildResultList1() {


        try {
            type = createSimpleResultType();
        } catch (NoSuchAuthorityCodeException ex) {
            Logger.getLogger(RegroupTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FactoryException ex) {
            Logger.getLogger(RegroupTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        final FeatureCollection<Feature> featureList = DataUtilities.collection("", type);


        Feature myFeature1;
       LinearRing ring = GF.createLinearRing(
                new Coordinate[]{
                    new Coordinate(5.0, 6.0),
                    new Coordinate(5.0, 7.0),
                    new Coordinate(6.0, 7.0),
                    new Coordinate(6.0, 6.0),
                    new Coordinate(5.0, 6.0)
                });
        sfb = new SimpleFeatureBuilder(type);
        sfb.set("height", 3);
        sfb.set("geom1",GF.createPolygon(ring, null));
        myFeature1 = sfb.buildFeature("height-3");
        featureList.add(myFeature1);

        Feature myFeature2;
        LinearRing ring1 = GF.createLinearRing(
                new Coordinate[]{
                    new Coordinate(6.0, 7.0),
                    new Coordinate(6.0, 8.0),
                    new Coordinate(7.0, 8.0),
                    new Coordinate(7.0, 7.0),
                    new Coordinate(6.0, 7.0)
                });
        LinearRing ring2 = GF.createLinearRing(
                new Coordinate[]{
                    new Coordinate(3.0, 3.0),
                    new Coordinate(3.0, 4.0),
                    new Coordinate(4.0, 4.0),
                    new Coordinate(4.0, 3.0),
                    new Coordinate(3.0, 3.0)
                });
        Polygon poly1 = GF.createPolygon(ring1, null);
        Polygon poly2 = GF.createPolygon(ring2, null);
        sfb = new SimpleFeatureBuilder(type);
        sfb.set("height", 9);
        sfb.set("geom1", GF.createMultiPolygon(new Polygon[]{poly1,poly2}));
        myFeature2 = sfb.buildFeature("height-9");
        featureList.add(myFeature2);

        Feature myFeature3;
        ring = GF.createLinearRing(
                new Coordinate[]{
                     new Coordinate(15.0, 10.0),
                    new Coordinate(15.0, 11.0),
                    new Coordinate(16.0, 11.0),
                    new Coordinate(16.0, 10.0),
                    new Coordinate(15.0, 10.0)
                });
        ring1 = GF.createLinearRing(
                new Coordinate[]{
                    new Coordinate(9.0, 4.0),
                    new Coordinate(9.0, 5.0),
                    new Coordinate(11.0, 5.0),
                    new Coordinate(11.0, 4.0),
                    new Coordinate(9.0, 4.0)
                });
        ring2 = GF.createLinearRing(
                new Coordinate[]{
                    new Coordinate(2.0, 2.0),
                    new Coordinate(2.0, 3.0),
                    new Coordinate(3.0, 3.0),
                    new Coordinate(3.0, 2.0),
                    new Coordinate(2.0, 2.0)
                });
        poly1 = GF.createPolygon(ring, null);
        poly2 = GF.createPolygon(ring1, null);
        Polygon poly3 = GF.createPolygon(ring2, null);
        sfb = new SimpleFeatureBuilder(type);
        sfb.set("height", 2);
        sfb.set("geom1", GF.createMultiPolygon(new Polygon[]{poly1,poly2,poly3}));
        myFeature3 = sfb.buildFeature("height-2");
        featureList.add(myFeature3);


        

        return featureList;
    }

     private static FeatureCollection<?> buildResultList2() {


        try {
            type = createSimpleResultType2();
        } catch (NoSuchAuthorityCodeException ex) {
            Logger.getLogger(RegroupTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FactoryException ex) {
            Logger.getLogger(RegroupTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        final FeatureCollection<Feature> featureList = DataUtilities.collection("", type);


        Feature myFeature1;
        Point pt1 = GF.createPoint(new Coordinate(3,6));
        Point pt2 = GF.createPoint(new Coordinate(4,7));
        Point pt3 = GF.createPoint(new Coordinate(5,4));
        Point pt4 = GF.createPoint(new Coordinate(5.5,6.5));
        LineString line1 = GF.createLineString(
                new Coordinate[]{
                    new Coordinate(8.0, 0.0),
                    new Coordinate(7.4, 0.6)
                });
        LineString line2 = GF.createLineString(
                new Coordinate[]{
                    new Coordinate(7.4, 0.6),
                    new Coordinate(5.0, 3.0)
                });
        LineString line3 = GF.createLineString(
                new Coordinate[]{
                    new Coordinate(7.0, 0.0),
                    new Coordinate(7.4, 0.6)
                });
        LineString line4 = GF.createLineString(
                new Coordinate[]{
                    new Coordinate(7.4, 0.6),
                    new Coordinate(9.0, 3.0)
                });
        GeometryCollection collec = GF.createGeometryCollection(new Geometry[]{pt1,pt2,pt3,pt4,line1,line2,line3,line4});
        sfb = new SimpleFeatureBuilder(type);
        sfb.set("type", "office");
        sfb.set("geom2",collec);
        myFeature1 = sfb.buildFeature("type-office");
        featureList.add(myFeature1);

        Feature myFeature2;
        pt1 = GF.createPoint(new Coordinate(3.5,3.5));
        LinearRing ring = GF.createLinearRing(
                new Coordinate[]{
                    new Coordinate(8.0, 0.0),
                    new Coordinate(9.0, 6.0),
                    new Coordinate(10.0, 2.0),
                    new Coordinate(8.0, 0.0)
                });
        Polygon poly = GF.createPolygon(ring, null);
        sfb = new SimpleFeatureBuilder(type);
        sfb.set("type", "church");
        sfb.set("geom2", GF.createGeometryCollection(new Geometry[]{pt1,poly}));
        myFeature2 = sfb.buildFeature("type-church");
        featureList.add(myFeature2);

        Feature myFeature3;
      
        sfb = new SimpleFeatureBuilder(type);
        sfb.set("type", "post office");
        sfb.set("geom2", GF.createPoint(new Coordinate(10,5)));
        myFeature3 = sfb.buildFeature("type-post office");
        featureList.add(myFeature3);

        return featureList;
    }

}
