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

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.HashMap;
import java.util.Map;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;

import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.util.logging.Logging;

import org.opengis.filter.FilterFactory2;
import org.opengis.util.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;


/**
 *
 * @author Johann Sorel (Geomatys)
 * @author Cédric Briançon (Geomatys)
 * @module pending
 */
public class FilterTestConstants {

    private static final Logger LOGGER = Logging.getLogger(FilterTestConstants.class);

    public static final FilterFactory2 FF = (FilterFactory2)
            FactoryFinder.getFilterFactory(new Hints(Hints.FILTER_FACTORY, FilterFactory2.class));
    public static final GeometryFactory GF = new GeometryFactory();

    public static final Geometry RIGHT_GEOMETRY;
    public static final Geometry WRONG_GEOMETRY;
    public static final Date DATE;
    public static final Object CANDIDATE_1;

    static{
        CoordinateReferenceSystem crs = null;
        try {
            crs = CRS.decode("EPSG:4326");
        } catch (NoSuchAuthorityCodeException ex) {
            LOGGER.log(Level.WARNING, null, ex);
        } catch (FactoryException ex) {
            LOGGER.log(Level.WARNING, null, ex);
        }

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

        DATE = new Date();

        // Builds the test candidate
        final Map<String,Object> candidate = new HashMap<String, Object>();
        candidate.put("@id", "testFeatureType.1");
        candidate.put("testGeometry", RIGHT_GEOMETRY);
        candidate.put("testBoolean", Boolean.TRUE);
        candidate.put("testCharacter", 't');
        candidate.put("testByte", Byte.valueOf((byte)101));
        candidate.put("testShort", Short.valueOf((short)101));
        candidate.put("testInteger", Integer.valueOf(101));
        candidate.put("testLong", Long.valueOf(101l));
        candidate.put("testFloat", Float.valueOf(101f));
        candidate.put("testDouble", Double.valueOf(101d));
        candidate.put("testString", "test string data");
        candidate.put("testString2", "cow $10");
        candidate.put("date", new java.sql.Date(DATE.getTime()));
        candidate.put("time", new java.sql.Time(DATE.getTime()));
        candidate.put("datetime1", DATE);
        candidate.put("datetime2", new java.sql.Timestamp(DATE.getTime()));
        candidate.put("testNull", null);
        candidate.put("attribut.Géométrie", "POINT(45,32)");

        // assign the candidate
        CANDIDATE_1 = candidate;
    }

}
