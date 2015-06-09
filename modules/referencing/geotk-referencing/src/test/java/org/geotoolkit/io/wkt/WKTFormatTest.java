/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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
package org.geotoolkit.io.wkt;

import java.util.Collections;
import java.util.Map;
import java.text.ParseException;
import org.opengis.util.FactoryException;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.IdentifiedObject;
import org.geotoolkit.referencing.CRS;
import org.apache.sis.referencing.crs.DefaultProjectedCRS;
import org.apache.sis.io.wkt.Symbols;
import org.apache.sis.test.DependsOn;
import org.apache.sis.io.wkt.Convention;
import org.apache.sis.metadata.iso.citation.Citations;
import org.junit.*;

import static org.geotoolkit.test.Commons.*;
import static org.geotoolkit.referencing.Assert.*;


/**
 * Tests the {@link WKTFormat} implementation.
 *
 * @author Yann Cézard (IRD)
 * @author Rémi Eve (IRD)
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.20
 *
 * @since 2.0
 */
public final strictfp class WKTFormatTest {
    private static Map<String,String> name(final String name) {
        return Collections.singletonMap(IdentifiedObject.NAME_KEY, name);
    }

    /**
     * Try an other projection (Transverse Mercator).
     *
     * @throws ParseException Should never happen.
     */
    @Test
    public void parseTransverseMercator() throws ParseException {
        final String wkt1 = decodeQuotes(
               "PROJCS[“OSGB 1936 / British National Grid”,\n" +
               "  GEOGCS[“OSGB 1936”,\n" +
               "    DATUM[“OSGB_1936”,\n" +
               "      SPHEROID[“Airy 1830”, 6377563.396, 299.3249646, AUTHORITY[“EPSG”, “7001”]],\n" +
               "      TOWGS84[375.0, -111.0, 431.0, 0.0, 0.0, 0.0, 0.0],\n" +
               "      AUTHORITY[“EPSG”, “6277”]],\n" +
               "      PRIMEM[“Greenwich”,0.0, AUTHORITY[“EPSG”, “8901”]],\n" +
               "    UNIT[“DMSH”,0.0174532925199433, AUTHORITY[“EPSG”, “9108”]],\n" +
               "    AXIS[“Lat”,NORTH],AXIS[“Long”,EAST], AUTHORITY[“EPSG”, “4277”]],\n" +
               "  PROJECTION[“Transverse_Mercator”],\n" +
               "  PARAMETER[“latitude_of_origin”, 49.0],\n" +
               "  PARAMETER[“central_meridian”, -2.0],\n" +
               "  PARAMETER[“scale_factor”, 0.999601272],\n" +
               "  PARAMETER[“false_easting”, 400000.0],\n" +
               "  PARAMETER[“false_northing”, -100000.0],\n" +
               "  UNIT[“metre”, 1.0, AUTHORITY[“EPSG”, “9001”]],\n" +
               "  AXIS[“E”,EAST],\n" +
               "  AXIS[“N”,NORTH],\n" +
               "  AUTHORITY[“EPSG”, “27700”]]\n");
        assertTrue(Symbols.getDefault().containsAxis(wkt1));
        final WKTFormat wktFormat = new WKTFormat();
        wktFormat.setNameAuthority(Citations.OGC);
        final DefaultProjectedCRS crs1  = (DefaultProjectedCRS) wktFormat.parseObject(wkt1);
        final String              wkt2  = wktFormat.format(crs1);
        final DefaultProjectedCRS crs2  = (DefaultProjectedCRS) wktFormat.parseObject(wkt2);
        final ParameterValueGroup param = crs1.getConversionFromBase().getParameterValues();
//      assertEquals(crs1, crs2);
        assertFalse (wkt2.contains("semi_major"));
        assertFalse (wkt2.contains("semi_minor"));
        assertEquals("Transverse_Mercator", crs1.getConversionFromBase().getMethod().getName().getCode());
        assertEquals("semi_major",   6377563.396, param.parameter("semi_major"        ).doubleValue(), 1E-4);
        assertEquals("semi_minor",   6356256.909, param.parameter("semi_minor"        ).doubleValue(), 1E-3);
        assertEquals("latitude_of_origin",  49.0, param.parameter("latitude_of_origin").doubleValue(), 1E-8);
        assertEquals("central_meridian",    -2.0, param.parameter("central_meridian"  ).doubleValue(), 1E-8);
        assertEquals("scale_factor",      0.9996, param.parameter("scale_factor"      ).doubleValue(), 1E-5);
        assertEquals("false_easting",   400000.0, param.parameter("false_easting"     ).doubleValue(), 1E-4);
        assertEquals("false_northing", -100000.0, param.parameter("false_northing"    ).doubleValue(), 1E-4);
    }

    /**
     * Try a projection with feet units.
     *
     * @throws ParseException Should never happen.
     */
    @Test
    public void parseTransverseMercatorUsingFeet() throws ParseException {
        final String wkt1 = decodeQuotes(
               "PROJCS[“TransverseMercator”,\n" +
               "  GEOGCS[“Sphere”,\n" +
               "    DATUM[“Sphere”,\n" +
               "      SPHEROID[“Sphere”, 6370997.0, 0.0],\n" +
               "      TOWGS84[0, 0, 0, 0, 0, 0, 0]],\n" +
               "      PRIMEM[“Greenwich”, 0.0],\n" +
               "    UNIT[“degree”, 0.017453292519943295],\n" +
               "    AXIS[“Longitude”, EAST],\n" +
               "    AXIS[“Latitude”, NORTH]],\n" +
               "  PROJECTION[“Transverse_Mercator”,\n" +
               "    AUTHORITY[“OGC”, “Transverse_Mercator”]],\n" +
               "  PARAMETER[“central_meridian”, 170.0],\n" +
               "  PARAMETER[“latitude_of_origin”, 50.0],\n" +
               "  PARAMETER[“scale_factor”, 0.95],\n" +
               "  PARAMETER[“false_easting”, 0.0],\n" +
               "  PARAMETER[“false_northing”, 0.0],\n" +
               "  UNIT[“feet”, 0.304800609601219],\n" +
               "  AXIS[“x”, EAST],\n" +
               "  AXIS[“y”, NORTH]]");
        assertTrue(Symbols.getDefault().containsAxis(wkt1));
        final WKTFormat wktFormat = new WKTFormat();
        wktFormat.setNameAuthority(Citations.OGC);
        final DefaultProjectedCRS crs1  = (DefaultProjectedCRS) wktFormat.parseObject(wkt1);
        final String              wkt2  = wktFormat.format(crs1);
        final DefaultProjectedCRS crs2  = (DefaultProjectedCRS) wktFormat.parseObject(wkt2);
        final ParameterValueGroup param = crs1.getConversionFromBase().getParameterValues();
//      assertEquals(crs1, crs2);
        assertFalse (wkt2.contains("semi_major"));
        assertFalse (wkt2.contains("semi_minor"));
        assertEquals("Transverse_Mercator", crs1.getConversionFromBase().getMethod().getName().getCode());
        assertEquals("semi_major",     6370997.0, param.parameter("semi_major"        ).doubleValue(), 1E-5);
        assertEquals("semi_minor",     6370997.0, param.parameter("semi_minor"        ).doubleValue(), 1E-5);
        assertEquals("latitude_of_origin",  50.0, param.parameter("latitude_of_origin").doubleValue(), 1E-8);
        assertEquals("central_meridian",   170.0, param.parameter("central_meridian"  ).doubleValue(), 1E-8);
        assertEquals("scale_factor",        0.95, param.parameter("scale_factor"      ).doubleValue(), 1E-8);
        assertEquals("false_easting",        0.0, param.parameter("false_easting"     ).doubleValue(), 1E-8);
        assertEquals("false_northing",       0.0, param.parameter("false_northing"    ).doubleValue(), 1E-8);
    }

    /**
     * Tests the formatting using the name of different authorities.
     *
     * @throws FactoryException Should never happen.
     */
    @Test
    public void formatVariousConventions() throws FactoryException {
        final DefaultProjectedCRS crs = (DefaultProjectedCRS) CRS.parseWKT(decodeQuotes(
            "PROJCS[“OSGB 1936 / British National Grid”,\n" +
            "  GEOGCS[“OSGB 1936”,\n" +
            "    DATUM[“OSGB_1936”,\n" +
            "      SPHEROID[“Airy 1830”, 6377563.396, 299.3249646],\n" +
            "      TOWGS84[375.0, -111.0, 431.0, 0.0, 0.0, 0.0, 0.0]],\n" +
            "      PRIMEM[“Greenwich”,0.0],\n" +
            "    UNIT[“DMSH”,0.0174532925199433],\n" +
            "    AXIS[“Lat”,NORTH],AXIS[“Long”,EAST]],\n" +
            "  PROJECTION[“Transverse_Mercator”],\n" +
            "  PARAMETER[“latitude_of_origin”, 49.0],\n" +
            "  PARAMETER[“central_meridian”, -2.0],\n" +
            "  PARAMETER[“scale_factor”, 0.999601272],\n" +
            "  PARAMETER[“false_easting”, 400000.0],\n" +
            "  PARAMETER[“false_northing”, -100000.0],\n" +
            "  UNIT[“metre”, 1],\n" +
            "  AXIS[“E”,EAST],\n" +
            "  AXIS[“N”,NORTH]]"));
        /*
         * Formats using OGC identifiers. Should be the same than above.
         */
        // TODO
        if (false) assertMultilinesEquals(decodeQuotes(
            "PROJCS[“OSGB 1936 / British National Grid”,\n" +
            "  GEOGCS[“OSGB 1936”,\n" +
            "    DATUM[“OSGB_1936”,\n" +
            "      SPHEROID[“Airy 1830”, 6377563.396, 299.3249646],\n" +
            "      TOWGS84[375.0, -111.0, 431.0, 0.0, 0.0, 0.0, 0.0]],\n" +
            "      PRIMEM[“Greenwich”, 0.0],\n" +
            "    UNIT[“degree”, 0.017453292519943295],\n" +
            "    AXIS[“Latitude”, NORTH],\n" +
            "    AXIS[“Longitude”, EAST]],\n" +
            "  PROJECTION[“Transverse_Mercator”],\n" +
            "  PARAMETER[“central_meridian”, -2.0],\n" +
            "  PARAMETER[“latitude_of_origin”, 49.0],\n" +
            "  PARAMETER[“scale_factor”, 0.999601272],\n" +
            "  PARAMETER[“false_easting”, 400000.0],\n" +
            "  PARAMETER[“false_northing”, -100000.0],\n" +
            "  UNIT[“metre”, 1],\n" +
            "  AXIS[“Easting”, EAST],\n" +
            "  AXIS[“Northing”, NORTH]]"),
            crs.toString(Convention.WKT1));
        /*
         * Formats using GeoTiff identifiers. We should get different strings in PROJECTION[...]
         * and PARAMETER[...] elements, but the other ones (especially DATUM[...]) are unchanged.
         * The changes in UNIT[...] and AXIS[...] are related to the way those objects are built
         * and are independents of the Convention argument.
         */
        // TODO
        if (false) assertMultilinesEquals(decodeQuotes(
            "PROJCS[“OSGB 1936 / British National Grid”,\n" +
            "  GEOGCS[“OSGB 1936”,\n" +
            "    DATUM[“OSGB_1936”,\n" +
            "      SPHEROID[“Airy 1830”, 6377563.396, 299.3249646],\n" +
            "      TOWGS84[375.0, -111.0, 431.0, 0.0, 0.0, 0.0, 0.0]],\n" +
            "      PRIMEM[“Greenwich”, 0.0],\n" +
            "    UNIT[“degree”, 0.017453292519943295],\n" +
            "    AXIS[“Latitude”, NORTH],\n" +
            "    AXIS[“Longitude”, EAST]],\n" +
            "  PROJECTION[“CT_TransverseMercator”],\n" +
            "  PARAMETER[“NatOriginLong”, -2.0],\n" +
            "  PARAMETER[“NatOriginLat”, 49.0],\n" +
            "  PARAMETER[“ScaleAtNatOrigin”, 0.999601272],\n" +
            "  PARAMETER[“FalseEasting”, 400000.0],\n" +
            "  PARAMETER[“FalseNorthing”, -100000.0],\n" +
            "  UNIT[“metre”, 1],\n" +
            "  AXIS[“Easting”, EAST],\n" +
            "  AXIS[“Northing”, NORTH]]"),
            crs.toString(Convention.WKT1_COMMON_UNITS));
        /*
         * Formats using ESRI identifiers. The most important change we are looking for is
         * the name inside DATUM[...].
         */
        // TODO
        if (false) assertMultilinesEquals(decodeQuotes(
            "PROJCS[“OSGB 1936 / British National Grid”,\n" +
            "  GEOGCS[“OSGB 1936”,\n" +
            "    DATUM[“D_OSGB_1936”,\n" +
            "      SPHEROID[“Airy 1830”, 6377563.396, 299.3249646],\n" +
            "      TOWGS84[375.0, -111.0, 431.0, 0.0, 0.0, 0.0, 0.0]],\n" +
            "      PRIMEM[“Greenwich”, 0.0],\n" +
            "    UNIT[“degree”, 0.017453292519943295],\n" +
            "    AXIS[“Latitude”, NORTH],\n" +
            "    AXIS[“Longitude”, EAST]],\n" +
            "  PROJECTION[“Transverse_Mercator”],\n" +
            "  PARAMETER[“Central_Meridian”, -2.0],\n" +
            "  PARAMETER[“Latitude_Of_Origin”, 49.0],\n" +
            "  PARAMETER[“Scale_Factor”, 0.999601272],\n" +
            "  PARAMETER[“False_Easting”, 400000.0],\n" +
            "  PARAMETER[“False_Northing”, -100000.0],\n" +
            "  UNIT[“meter”, 1],\n" +
            "  AXIS[“Easting”, EAST],\n" +
            "  AXIS[“Northing”, NORTH]]"),
            crs.toString(Convention.WKT1_COMMON_UNITS));

        if (true) return; // TODO

        /*
         * Formats using EPSG identifiers. We expect different names in
         * DATUM[...], PROJECTION[...] and PARAMETER[...].
         */
        assertMultilinesEquals(decodeQuotes(
            "PROJCS[“OSGB 1936 / British National Grid”,\n" +
            "  GEOGCS[“OSGB 1936”,\n" +
            "    DATUM[“OSGB 1936”,\n" +
            "      SPHEROID[“Airy 1830”, 6377563.396, 299.3249646],\n" +
            "      TOWGS84[375.0, -111.0, 431.0, 0.0, 0.0, 0.0, 0.0]],\n" +
            "      PRIMEM[“Greenwich”, 0.0],\n" +
            "    ANGLEUNIT[“degree”, 0.017453292519943295],\n" +
            "    AXIS[“Latitude”, NORTH],\n" +
            "    AXIS[“Longitude”, EAST]],\n" +
            "  PROJECTION[“Transverse Mercator”],\n" +
            "  PARAMETER[“Longitude of natural origin”, -2.0],\n" +
            "  PARAMETER[“Latitude of natural origin”, 49.0],\n" +
            "  PARAMETER[“Scale factor at natural origin”, 0.999601272],\n" +
            "  PARAMETER[“False easting”, 400000.0],\n" +
            "  PARAMETER[“False northing”, -100000.0],\n" +
            "  LENGTHUNIT[“metre”, 1],\n" +
            "  AXIS[“Easting”, EAST],\n" +
            "  AXIS[“Northing”, NORTH]]"),
            crs.toString(Convention.WKT2));
    }
}
