/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;

import org.geotoolkit.feature.dummy.DummyDescriptor;
import org.geotoolkit.feature.dummy.DummyGeometryDescriptor;
import org.geotoolkit.feature.dummy.DummySimpleFeature;
import org.geotoolkit.feature.dummy.DummySimpleFeatureType;
import org.geotoolkit.referencing.CRS;

import org.geotoolkit.util.logging.Logging;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.filter.FilterFactory2;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;


/**
 *
 * @author Johann Sorel (Geomatys)
 * @author Cédric Briançon (Geomatys)
 */
public class FilterTestConstants {

    private static final Logger LOGGER = Logging.getLogger(FilterTestConstants.class);

    public static final FilterFactory2 FF = new DefaultFilterFactory2();
    public static final GeometryFactory GF = new GeometryFactory();

    public static Geometry RIGHT_GEOMETRY;
    public static Geometry WRONG_GEOMETRY;
    public static Date DATE;
    public static final SimpleFeature FEATURE_1;

    static{
        CoordinateReferenceSystem crs = null;
        try {
            crs = CRS.decode("EPSG:4326");
        } catch (NoSuchAuthorityCodeException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        } catch (FactoryException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }

        final List<AttributeDescriptor> desc = new ArrayList<AttributeDescriptor>();
        final DummyGeometryDescriptor geomDesc = new DummyGeometryDescriptor("testGeometry", Polygon.class, crs);

        desc.add(geomDesc);
        desc.add(new DummyDescriptor("testBoolean", Boolean.class));
        desc.add(new DummyDescriptor("testCharacter", Character.class));
        desc.add(new DummyDescriptor("testByte", Byte.class));
        desc.add(new DummyDescriptor("testShort", Short.class));
        desc.add(new DummyDescriptor("testInteger", Integer.class));
        desc.add(new DummyDescriptor("testLong", Long.class));
        desc.add(new DummyDescriptor("testFloat", Float.class));
        desc.add(new DummyDescriptor("testDouble", Double.class));
        desc.add(new DummyDescriptor("testString", String.class));
        desc.add(new DummyDescriptor("testString2", String.class));
        desc.add(new DummyDescriptor("date", java.sql.Date.class));
        desc.add(new DummyDescriptor("time", java.sql.Time.class));
        desc.add(new DummyDescriptor("datetime1", java.util.Date.class));
        desc.add(new DummyDescriptor("datetime2", java.sql.Timestamp.class));
        desc.add(new DummyDescriptor("testNull", String.class));

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
        Object[] attributes = new Object[16];
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
        Timestamp stamp = new java.sql.Timestamp(DATE.getTime());
        attributes[14] = stamp;
        attributes[15] = null;

        // Creates the feature
        final DummySimpleFeatureType featureType = new DummySimpleFeatureType(desc, geomDesc, crs);
        FEATURE_1 = new DummySimpleFeature(attributes, "testFeatureType.1", featureType);
    }

}
