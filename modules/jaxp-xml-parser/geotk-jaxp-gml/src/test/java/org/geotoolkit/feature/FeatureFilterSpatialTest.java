/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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
package org.geotoolkit.feature;


import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;

import org.geotoolkit.feature.simple.SimpleFeatureBuilder;
import org.geotoolkit.filter.DefaultFilterFactory2;
import org.geotoolkit.geometry.GeneralDirectPosition;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.aggregate.JTSMultiPoint;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.primitive.JTSPoint;
import org.geotoolkit.referencing.CRS;

import org.junit.Test;

import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.Name;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.spatial.Equals;
import org.opengis.filter.spatial.Intersects;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import static org.junit.Assert.*;

/**
 * Testing filters used on ISO/JTS geometries.
 */
public class FeatureFilterSpatialTest {

    private static final FilterFactory FF = new DefaultFilterFactory2();

    public FeatureFilterSpatialTest() {
    }

    /**
     * Test that we get acces attributs without knowing the namespace
     */
    @Test
    public void testSpatialFilter() throws Exception {

        /*********************************************************************************************
         *                                                                                           *
         *                            AggregateGeoFeature                                            *
         *                                                                                           *
         *********************************************************************************************/
        final Name description = new DefaultName("http://www.opengis.net/gml", "description");
        final Name name = new DefaultName("http://www.opengis.net/gml", "name");
        final Name multiPointProperty = new DefaultName("http://cite.opengeospatial.org/gmlsf", "multiPointProperty");
        final Name multiCurveProperty = new DefaultName("http://cite.opengeospatial.org/gmlsf", "multiCurveProperty");
        final Name multiSurfaceProperty = new DefaultName("http://cite.opengeospatial.org/gmlsf", "multiSurfaceProperty");
        final Name doubleProperty = new DefaultName("http://cite.opengeospatial.org/gmlsf", "doubleProperty");
        final Name intRangeProperty = new DefaultName("http://cite.opengeospatial.org/gmlsf", "intRangeProperty");
        final Name strProperty = new DefaultName("http://cite.opengeospatial.org/gmlsf", "strProperty");
        final Name featureCode = new DefaultName("http://cite.opengeospatial.org/gmlsf", "featureCode");
        final Name id = new DefaultName("http://cite.opengeospatial.org/gmlsf", "id");


        final FeatureTypeBuilder sftb = new FeatureTypeBuilder();
        sftb.setName(new DefaultName("http://cite.opengeospatial.org/gmlsf", "AggregateGeoFeature"));
        sftb.add(description, String.class);
        sftb.add(name, String.class);
        sftb.add(multiPointProperty, MultiPoint.class, CRS.decode("EPSG:4326"));
        sftb.add(multiCurveProperty, MultiLineString.class, CRS.decode("EPSG:4326"));
        sftb.add(multiSurfaceProperty, MultiPolygon.class, CRS.decode("EPSG:4326"));
        sftb.add(doubleProperty, Double.class);
        sftb.add(intRangeProperty, String.class);
        sftb.add(strProperty, String.class);
        sftb.add(featureCode, String.class);
        sftb.add(id, String.class);

        final SimpleFeatureType aggregateGeoFeatureType = sftb.buildSimpleFeatureType();

        /*********************************************************************************************
         *                            AggregateGeoFeature 1                                          *
         *********************************************************************************************/
        SimpleFeatureBuilder sfb = new SimpleFeatureBuilder(aggregateGeoFeatureType);
        sfb.set(description, "description-f005");
        sfb.set(name, "name-f005");
        GeometryFactory factory = new GeometryFactory();
        Point[] points = new Point[3];
        points[0] = factory.createPoint(new Coordinate(70.83, 29.86));
        points[1] = factory.createPoint(new Coordinate(68.87, 31.08));
        points[2] = factory.createPoint(new Coordinate(71.96, 32.19));
        sfb.set(multiPointProperty, factory.createMultiPoint(points));
        sfb.set(doubleProperty, 2012.78);
        sfb.set(strProperty, "Ma quande lingues coalesce...");
        sfb.set(featureCode, "BK030");
        sfb.set(id, "f005");

        final SimpleFeature aggregateGeoFeature1 = sfb.buildFeature("f005");

        /*********************************************************************************************
         *                                                                                           *
         *                            EntitéGénérique                                                *
         *                                                                                           *
         *********************************************************************************************/
        final Name attributGeometrie  = new DefaultName("http://cite.opengeospatial.org/gmlsf", "attribut.Géométrie");
        final Name boolProperty = new DefaultName("http://cite.opengeospatial.org/gmlsf", "boolProperty");
        final Name str4Property = new DefaultName("http://cite.opengeospatial.org/gmlsf", "str4Property");
        final Name featureRef = new DefaultName("http://cite.opengeospatial.org/gmlsf", "featureRef");

        sftb.reset();

        sftb.setName(new DefaultName("http://cite.opengeospatial.org/gmlsf", "EntitéGénérique"));
        sftb.add(description, String.class);
        sftb.add(name, String.class);
        sftb.add(attributGeometrie, Geometry.class, CRS.decode("EPSG:4326"));
        sftb.add(boolProperty, Boolean.class);
        sftb.add(str4Property, String.class);
        sftb.add(featureRef, String.class);
        sftb.add(id, String.class);

        final SimpleFeatureType entiteGeneriqueType = sftb.buildSimpleFeatureType();

        sfb = new SimpleFeatureBuilder(entiteGeneriqueType);

        /*********************************************************************************************
         *                            EntitéGénérique 1                                              *
         *********************************************************************************************/
        sfb.set(description, "description-f004");
        sfb.set(name, "name-f004");

        Coordinate[] exteriorCoord = new Coordinate[5];
        exteriorCoord[0] = new Coordinate(60.5, 0);
        exteriorCoord[1] = new Coordinate(64, 0);
        exteriorCoord[2] = new Coordinate(64, 6.25);
        exteriorCoord[3] = new Coordinate(60.5, 6.25);
        exteriorCoord[4] = new Coordinate(60.5, 0);

        LinearRing exterior = factory.createLinearRing(exteriorCoord);

        Coordinate[] interiorCoord = new Coordinate[4];
        interiorCoord[0] = new Coordinate(61.5, 2);
        interiorCoord[1] = new Coordinate(62.5, 2);
        interiorCoord[2] = new Coordinate(62, 4);
        interiorCoord[3] = new Coordinate(61.5, 2);

        LinearRing interior = factory.createLinearRing(interiorCoord);
        LinearRing[] interiors = new LinearRing[1];
        interiors[0] = interior;

        sfb.set(attributGeometrie, factory.createPolygon(exterior, interiors));
        sfb.set(boolProperty, false);
        sfb.set(str4Property, "abc3");
        sfb.set(featureRef, "name-f003");
        sfb.set(id, "f004");

        final SimpleFeature entiteGenerique1 = sfb.buildFeature("f004");

        sfb.reset();

        /*********************************************************************************************
         *                            EntitéGénérique 2                                              *
         *********************************************************************************************/

        sfb.set(description, "description-f007");
        sfb.set(name, "name-f007");

        Coordinate[] exteriorCoord2 = new Coordinate[6];
        exteriorCoord2[0] = new Coordinate(35, 15);
        exteriorCoord2[1] = new Coordinate(40, 16);
        exteriorCoord2[2] = new Coordinate(39, 20);
        exteriorCoord2[3] = new Coordinate(37, 22.5);
        exteriorCoord2[4] = new Coordinate(36, 18);
        exteriorCoord2[5] = new Coordinate(35, 15);

        LinearRing exterior2 = factory.createLinearRing(exteriorCoord);

        Coordinate[] interiorCoord2 = new Coordinate[7];
        interiorCoord2[0] = new Coordinate(37.1, 17.5);
        interiorCoord2[1] = new Coordinate(37.2, 17.6);
        interiorCoord2[2] = new Coordinate(37.3, 17.7);
        interiorCoord2[3] = new Coordinate(37.4, 17.8);
        interiorCoord2[4] = new Coordinate(37.5, 17.9);
        interiorCoord2[5] = new Coordinate(37,   17.9);
        interiorCoord2[6] = new Coordinate(37.1, 17.5);

        LinearRing interior2 = factory.createLinearRing(interiorCoord);
        LinearRing[] interiors2 = new LinearRing[1];
        interiors2[0] = interior;

        sfb.set(attributGeometrie, factory.createPolygon(exterior2, interiors2));
        sfb.set(boolProperty, false);
        sfb.set(str4Property, "def4");
        sfb.set(id, "f007");

        final SimpleFeature entiteGenerique2 = sfb.buildFeature("f007");

        sfb.reset();

        /*********************************************************************************************
         *                            EntitéGénérique 3                                              *
         *********************************************************************************************/
        sfb.set(description, "description-f017");
        sfb.set(name, "name-f017");

        Coordinate[] lineCoord = new Coordinate[5];
        lineCoord[0] = new Coordinate(50.174, 4.899);
        lineCoord[1] = new Coordinate(52.652, 5.466);
        lineCoord[2] = new Coordinate(53.891, 6.899);
        lineCoord[3] = new Coordinate(54.382, 7.780);
        lineCoord[4] = new Coordinate(54.982, 8.879);
       

        sfb.set(attributGeometrie, factory.createLineString(lineCoord));
        sfb.set(boolProperty, false);
        sfb.set(str4Property, "qrst");
        sfb.set(featureRef, "name-f015");
        sfb.set(id, "f017");

        final SimpleFeature entiteGenerique3 = sfb.buildFeature("f017");

        /*
         * Filter equals on aggregateGeoFeature1
         */
        CoordinateReferenceSystem crs = CRS.decode("EPSG:4326");

        JTSMultiPoint multiPoint = new JTSMultiPoint();
        multiPoint.getElements().add(new JTSPoint(new GeneralDirectPosition(70.83, 29.86), crs));
        multiPoint.getElements().add(new JTSPoint(new GeneralDirectPosition(68.87, 31.08), crs));
        multiPoint.getElements().add(new JTSPoint(new GeneralDirectPosition(71.96, 32.19), crs));
        Equals equalsfilter = FF.equals("{http://cite.opengeospatial.org/gmlsf}multiPointProperty", multiPoint);
        boolean match = equalsfilter.evaluate(aggregateGeoFeature1);
        assertTrue(match);

        /*
         * Filter intersects on entitiGenerique*
         */

        multiPoint = new JTSMultiPoint();
        multiPoint.getElements().add(new JTSPoint(new GeneralDirectPosition(38.83, 16.22), crs));
        multiPoint.getElements().add(new JTSPoint(new GeneralDirectPosition(62.07, 2.48), crs));
        
        Intersects intfilter = FF.intersects("{http://cite.opengeospatial.org/gmlsf}attribut.Géométrie", multiPoint);
        match = intfilter.evaluate(entiteGenerique1);
        assertFalse(match);

        match = intfilter.evaluate(entiteGenerique2);
        assertFalse(match);

        match = intfilter.evaluate(entiteGenerique3);
        assertFalse(match);
    }

}
