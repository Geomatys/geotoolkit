/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
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

package org.geotoolkit.filter;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;

import org.geotoolkit.referencing.CRS;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;

import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.FilterFactory2;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FilterTestConstants {

    public static final FilterFactory2 FF = new DefaultFilterFactory2();
    public static final GeometryFactory GF = new GeometryFactory();

    public static Geometry RIGHT_GEOMETRY;
    public static Geometry WRONG_GEOMETRY;
    public static Date DATE;
    public static final SimpleFeature FEATURE_1;

    static{
        SimpleFeatureTypeBuilder ftb = new SimpleFeatureTypeBuilder();
        try {
            ftb.setCRS(CRS.decode("EPSG:4326"));
        } catch (NoSuchAuthorityCodeException ex) {
            Logger.getLogger(FilterTestConstants.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FactoryException ex) {
            Logger.getLogger(FilterTestConstants.class.getName()).log(Level.SEVERE, null, ex);
        }
        ftb.setName("testFeatureType");
        ftb.add("testGeometry", Polygon.class);
        ftb.add("testBoolean", Boolean.class);
        ftb.add("testCharacter", Character.class);
        ftb.add("testByte", Byte.class);
        ftb.add("testShort", Short.class);
        ftb.add("testInteger", Integer.class);
        ftb.add("testLong", Long.class);
        ftb.add("testFloat", Float.class);
        ftb.add("testDouble", Double.class);
        ftb.add("testString", String.class);
        ftb.add("testString2", String.class);
        ftb.add("date", java.sql.Date.class);
        ftb.add("time", java.sql.Time.class);
        ftb.add("datetime1", java.util.Date.class);
        ftb.add("datetime2", java.sql.Timestamp.class);
        SimpleFeatureType testSchema = ftb.buildFeatureType();

        Coordinate[] coords = new Coordinate[5];
        coords[0] = new Coordinate(5, 5);
        coords[1] = new Coordinate(5, 10);
        coords[2] = new Coordinate(10,10);
        coords[3] = new Coordinate(10,5);
        coords[4] = new Coordinate(5,5);
        LinearRing ring = GF.createLinearRing(coords);
        RIGHT_GEOMETRY = GF.createPolygon(ring, new LinearRing[0]);

        coords = new Coordinate[4];
        coords[0] = new Coordinate(45, 8);
        coords[1] = new Coordinate(39, 12);
        coords[2] = new Coordinate(1, 9);
        coords[3] = new Coordinate(45, 8);
        ring = GF.createLinearRing(coords);
        WRONG_GEOMETRY = GF.createPolygon(ring, new LinearRing[0]);

        // Builds the test feature
        Object[] attributes = new Object[15];
        attributes[0] = RIGHT_GEOMETRY;
        attributes[1] = new Boolean(true);
        attributes[2] = new Character('t');
        attributes[3] = new Byte("101");
        attributes[4] = new Short("101");
        attributes[5] = new Integer(101);
        attributes[6] = new Long(101);
        attributes[7] = new Float(101);
        attributes[8] = new Double(101);
        attributes[9] = "test string data";
        attributes[10] = "cow $10";

        // setup date ones
        DATE = new Date();
        attributes[11] = new java.sql.Date(DATE.getTime());
        attributes[12] = new java.sql.Time(DATE.getTime());
        attributes[13] = DATE;
        attributes[14] = new java.sql.Timestamp(DATE.getTime());

        // Creates the feature
        FEATURE_1 = SimpleFeatureBuilder.build(testSchema, attributes, "testFeatureType.1");
    }

}
