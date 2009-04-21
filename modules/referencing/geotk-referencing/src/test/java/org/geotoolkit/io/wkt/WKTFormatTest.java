/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2009, Open Source Geospatial Foundation (OSGeo)
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

import java.text.ParseException;

import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.cs.CartesianCS;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

import org.geotoolkit.factory.Hints;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.cs.DefaultCartesianCS;
import org.geotoolkit.referencing.crs.DefaultProjectedCRS;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.referencing.crs.DefaultGeocentricCRS;
import org.geotoolkit.referencing.operation.DefaultMathTransformFactory;
import org.geotoolkit.referencing.factory.DatumAliasesTest;
import org.geotoolkit.metadata.iso.citation.Citations;
import org.geotoolkit.test.Depend;

import org.junit.*;
import static org.junit.Assert.*;
import static org.geotoolkit.test.Commons.*;


/**
 * Tests the {@link WKTFormat} implementation.
 *
 * @author Yann Cézard (IRD)
 * @author Rémi Eve (IRD)
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.0
 *
 * @since 2.0
 */
@Depend({ParserTest.class, DatumAliasesTest.class})
public final class WKTFormatTest {
    /**
     * Test a hard coded version of a WKT. This is more convenient for debugging.
     *
     * @throws ParseException Should never happen.
     */
    @Test
    public void testHardCoded() throws ParseException {
        final WKTFormat wktFormat = new WKTFormat();
        String              wkt1, wkt2;
        DefaultProjectedCRS crs1, crs2;
        ParameterValueGroup param;
        /*
         * First, rather simple Mercator projection.
         * Uses standard units and axis order.
         */
        wkt1 = "PROJCS[“Mercator test”,\n" +
               "  GEOGCS[“WGS84”,\n" +
               "    DATUM[“WGS84”,\n" +
               "      SPHEROID[“WGS84”, 6378137.0, 298.257223563]],\n" +
               "    PRIMEM[“Greenwich”, 0.0],\n" +
               "    UNIT[“degree”, 0.017453292519943295],\n" +
               "    AXIS[“Longitude”, EAST],\n" +
               "    AXIS[“Latitude”, NORTH]],\n" +
               "  PROJECTION[“Mercator_1SP”],\n" +
               "  PARAMETER[“central_meridian”, -20.0],\n" +
               "  PARAMETER[“scale_factor”, 1.0],\n" +
               "  PARAMETER[“false_easting”, 500000.0],\n" +
               "  PARAMETER[“false_northing”, 0.0],\n" +
               "  UNIT[“metre”, 1.0],\n" +
               "  AXIS[“x”, EAST],\n" +
               "  AXIS[“y”, NORTH]]\n";
        wkt1 = decodeQuotes(wkt1);
        assertTrue(Symbols.DEFAULT.containsAxis(wkt1));
        crs1  = (DefaultProjectedCRS) wktFormat.parseObject(wkt1);
        wkt2  = wktFormat.format(crs1);
        crs2  = (DefaultProjectedCRS) wktFormat.parseObject(wkt2);
        param = crs1.getConversionFromBase().getParameterValues();
        assertEquals(crs1, crs2);
        assertEquals("Mercator_1SP", crs1.getConversionFromBase().getMethod().getName().getCode());
        assertTrue(crs1.getConversionFromBase().getMathTransform().toWKT().startsWith("PARAM_MT[\"Mercator_1SP\""));
        assertFalse (wkt2.contains("semi_major"));
        assertFalse (wkt2.contains("semi_minor"));
        assertEquals("semi_major",   6378137.0, param.parameter("semi_major"      ).doubleValue(), 1E-4);
        assertEquals("semi_minor",   6356752.3, param.parameter("semi_minor"      ).doubleValue(), 1E-1);
        assertEquals("central_meridian", -20.0, param.parameter("central_meridian").doubleValue(), 1E-8);
        assertEquals("scale_factor",       1.0, param.parameter("scale_factor"    ).doubleValue(), 1E-8);
        assertEquals("false_easting", 500000.0, param.parameter("false_easting"   ).doubleValue(), 1E-4);
        assertEquals("false_northing",     0.0, param.parameter("false_northing"  ).doubleValue(), 1E-4);
        /*
         * Same Mercator projection as above, but
         * switch longitude and latitude axis.
         */
        wkt1 = "PROJCS[“Mercator test”,\n" +
               "  GEOGCS[“WGS84”,\n" +
               "    DATUM[“WGS84”,\n" +
               "      SPHEROID[“WGS84”, 6378137.0, 298.257223563]],\n" +
               "    PRIMEM[“Greenwich”, 0.0],\n" +
               "    UNIT[“degree”, 0.017453292519943295],\n" +
               "    AXIS[“Latitude”, NORTH],\n" +
               "    AXIS[“Longitude”, EAST]],\n" +
               "  PROJECTION[“Mercator_1SP”],\n" +
               "  PARAMETER[“central_meridian”, -20.0],\n" +
               "  PARAMETER[“scale_factor”, 1.0],\n" +
               "  PARAMETER[“false_easting”, 500000.0],\n" +
               "  PARAMETER[“false_northing”, 0.0],\n" +
               "  UNIT[“metre”, 1.0],\n" +
               "  AXIS[“x”, EAST],\n" +
               "  AXIS[“y”, NORTH]]\n";
        wkt1 = decodeQuotes(wkt1);
        assertTrue(Symbols.DEFAULT.containsAxis(wkt1));
        crs1  = (DefaultProjectedCRS) wktFormat.parseObject(wkt1);
        wkt2  = wktFormat.format(crs1);
        crs2  = (DefaultProjectedCRS) wktFormat.parseObject(wkt2);
        param = crs1.getConversionFromBase().getParameterValues();
        assertEquals(crs1, crs2);
        assertEquals("Mercator_1SP", crs1.getConversionFromBase().getMethod().getName().getCode());
        assertTrue(crs1.getConversionFromBase().getMathTransform().toWKT().startsWith("CONCAT_MT[PARAM_MT["));
        assertFalse (wkt2.contains("semi_major"));
        assertFalse (wkt2.contains("semi_minor"));
        assertEquals("semi_major",   6378137.0, param.parameter("semi_major"      ).doubleValue(), 1E-4);
        assertEquals("semi_minor",   6356752.3, param.parameter("semi_minor"      ).doubleValue(), 1E-1);
        assertEquals("central_meridian", -20.0, param.parameter("central_meridian").doubleValue(), 1E-8);
        assertEquals("scale_factor",       1.0, param.parameter("scale_factor"    ).doubleValue(), 1E-8);
        assertEquals("false_easting", 500000.0, param.parameter("false_easting"   ).doubleValue(), 1E-4);
        assertEquals("false_northing",     0.0, param.parameter("false_northing"  ).doubleValue(), 1E-4);
        /*
         * Try an other projection (Transverse Mercator).
         */
        wkt1 = "PROJCS[“OSGB 1936 / British National Grid”,\n" +
               "  GEOGCS[“OSGB 1936”,\n" +
               "    DATUM[“OSGB_1936”,\n" +
               "      SPHEROID[“Airy 1830”, 6377563.396, 299.3249646, AUTHORITY[“EPSG”,”7001”]],\n" +
               "      TOWGS84[375.0, -111.0, 431.0, 0.0, 0.0, 0.0, 0.0],\n" +
               "      AUTHORITY[“EPSG”,”6277”]],\n" +
               "    PRIMEM[“Greenwich”,0.0, AUTHORITY[“EPSG”,”8901”]],\n" +
               "    UNIT[“DMSH”,0.0174532925199433, AUTHORITY[“EPSG”,”9108”]],\n" +
               "    AXIS[“Lat”,NORTH],AXIS[“Long”,EAST], AUTHORITY[“EPSG”,”4277”]],\n" +
               "  PROJECTION[“Transverse_Mercator”],\n" +
               "  PARAMETER[“latitude_of_origin”, 49.0],\n" +
               "  PARAMETER[“central_meridian”, -2.0],\n" +
               "  PARAMETER[“scale_factor”, 0.999601272],\n" +
               "  PARAMETER[“false_easting”, 400000.0],\n" +
               "  PARAMETER[“false_northing”, -100000.0],\n" +
               "  UNIT[“metre”, 1.0, AUTHORITY[“EPSG”,”9001”]],\n" +
               "  AXIS[“E”,EAST],\n" +
               "  AXIS[“N”,NORTH],\n" +
               "  AUTHORITY[“EPSG”,”27700”]]\n";
        wkt1 = decodeQuotes(wkt1);
        assertTrue(Symbols.DEFAULT.containsAxis(wkt1));
        crs1  = (DefaultProjectedCRS) wktFormat.parseObject(wkt1);
        wkt2  = wktFormat.format(crs1);
        crs2  = (DefaultProjectedCRS) wktFormat.parseObject(wkt2);
        param = crs1.getConversionFromBase().getParameterValues();
        assertEquals(crs1, crs2);
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
        /*
         * Try a projection with feet units.
         */
        wkt1 = "PROJCS[“TransverseMercator”,\n" +
               "  GEOGCS[“Sphere”,\n" +
               "    DATUM[“Sphere”,\n" +
               "      SPHEROID[“Sphere”, 6370997.0, 0.0],\n" +
               "      TOWGS84[0, 0, 0, 0, 0, 0, 0]],\n" +
               "    PRIMEM[“Greenwich”, 0.0],\n" +
               "    UNIT[“degree”, 0.017453292519943295],\n" +
               "    AXIS[“Longitude”, EAST],\n" +
               "    AXIS[“Latitude”, NORTH]],\n" +
               "  PROJECTION[“Transverse_Mercator”,\n" +
               "    AUTHORITY[“OGC”,”Transverse_Mercator”]],\n" +
               "  PARAMETER[“central_meridian”, 170.0],\n" +
               "  PARAMETER[“latitude_of_origin”, 50.0],\n" +
               "  PARAMETER[“scale_factor”, 0.95],\n" +
               "  PARAMETER[“false_easting”, 0.0],\n" +
               "  PARAMETER[“false_northing”, 0.0],\n" +
               "  UNIT[“feet”, 0.304800609601219],\n" +
               "  AXIS[“x”, EAST],\n" +
               "  AXIS[“y”, NORTH]]";
        wkt1 = decodeQuotes(wkt1);
        assertTrue(Symbols.DEFAULT.containsAxis(wkt1));
        crs1  = (DefaultProjectedCRS) wktFormat.parseObject(wkt1);
        wkt2  = wktFormat.format(crs1);
        crs2  = (DefaultProjectedCRS) wktFormat.parseObject(wkt2);
        param = crs1.getConversionFromBase().getParameterValues();
        assertEquals(crs1, crs2);
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
        /*
         * Try with a number using scientific notation.
         */
        wkt1 = "GEOGCS[“NAD83 / NFIS Seconds”,DATUM[“North_American_Datum_1983”,\n" +
               "  SPHEROID[“GRS 1980”, 6378137, 298.257222101]],\n" +
               "  PRIMEM[“Greenwich”, 0],\n" +
               "  UNIT[“Decimal_Second”, 4.84813681109536e-06],\n" +
               "  AUTHORITY[“EPSG”, ”100001”]]";
        wkt1 = decodeQuotes(wkt1);
        assertFalse(Symbols.DEFAULT.containsAxis(wkt1));
        wkt2 = wktFormat.format(wktFormat.parseObject(wkt1));
        assertFalse(wkt2.contains("semi_major"));
        assertFalse(wkt2.contains("semi_minor"));
    }

    /**
     * Tests parsing with custom axis length. At the difference of the previous test,
     * the WKT formatting in this test should include the axis length as parameter values.
     *
     * @throws FactoryException Should never happen.
     * @throws ParseException Should never happen.
     */
    @Test
    public void testCustomAxisLength() throws FactoryException, ParseException {
        DefaultMathTransformFactory factory = new DefaultMathTransformFactory();
        ParameterValueGroup parameters = factory.getDefaultParameters("Lambert_Conformal_Conic_2SP");

        final double majorAxis = 6.3712e+6;
        final double minorAxis = 6.3712e+6;
        parameters.parameter("semi_major").setValue(majorAxis);
        parameters.parameter("semi_minor").setValue(minorAxis);
        parameters.parameter("latitude_of_origin").setValue(25.0);
        parameters.parameter("standard_parallel_1").setValue(25.0);
        parameters.parameter("standard_parallel_2").setValue(25.0);
        parameters.parameter("central_meridian").setValue(-95.0);
        parameters.parameter("false_easting").setValue(0.0);
        parameters.parameter("false_northing").setValue(0.0);

        GeographicCRS base = DefaultGeographicCRS.WGS84;
        MathTransform mt   = factory.createParameterizedTransform(parameters);
        CartesianCS cs = DefaultCartesianCS.PROJECTED;
        CoordinateReferenceSystem crs = new DefaultProjectedCRS("Lambert", base, mt, cs);

        final String wkt = crs.toWKT();
        assertTrue(wkt.contains("semi_major"));
        assertTrue(wkt.contains("semi_minor"));
        final ReferencingParser parser = new ReferencingParser(Symbols.DEFAULT, (Hints) null);
        CoordinateReferenceSystem check = parser.parseCoordinateReferenceSystem(wkt);
        assertEquals(wkt, check.toWKT());
    }

    /**
     * Tests the Geocentric case, which requires a conversion of axis direction
     * between ISO 19111 and OGC 01-009 values.
     *
     * @throws ParseException Should never happen.
     */
    @Test
    public void testGeocentric() throws ParseException {
        final WKTFormat wktFormat = new WKTFormat();
        /*
         * First try the formatting as internal WKT. Geotoolkit
         * uses internally the ISO 19111 axis directions.
         */
        final String name = DefaultGeocentricCRS.CARTESIAN.getName().getCode();
        final DefaultGeocentricCRS crs = DefaultGeocentricCRS.CARTESIAN;
        String wkt = decodeQuotes(
                "GEOCCS[“" + name + "”,\n" +
                "  DATUM[“WGS84”,\n" +
                "    SPHEROID[“WGS84”, 6378137.0, 298.257223563]],\n" +
                "  PRIMEM[“Greenwich”, 0.0],\n" +
                "  UNIT[“metre”, 1.0],\n" +
                "  AXIS[“Geocentric X”, GEOCENTRIC_X],\n" +
                "  AXIS[“Geocentric Y”, GEOCENTRIC_Y],\n" +
                "  AXIS[“Geocentric Z”, GEOCENTRIC_Z]]");
        wktFormat.setAuthority(DefaultGeocentricCRS.INTERNAL);
        assertMultilinesEquals(wkt, wktFormat.format(crs));
        assertTrue(CRS.equalsIgnoreMetadata(crs, wktFormat.parseObject(wkt)));
        /*
         * Now try the fomatting as standard WKT.
         */
        wkt = decodeQuotes(
                "GEOCCS[“" + name + "”,\n" +
                "  DATUM[“WGS84”,\n" +
                "    SPHEROID[“WGS84”, 6378137.0, 298.257223563]],\n" +
                "  PRIMEM[“Greenwich”, 0.0],\n" +
                "  UNIT[“metre”, 1.0],\n" +
                "  AXIS[“X”, OTHER],\n" +
                "  AXIS[“Y”, EAST],\n" +
                "  AXIS[“Z”, NORTH]]");
        wktFormat.setAuthority(DefaultGeocentricCRS.OGC);
        assertMultilinesEquals(wkt, wktFormat.format(crs));
        assertTrue(CRS.equalsIgnoreMetadata(crs, wktFormat.parseObject(wkt)));
    }

    /**
     * Tests the Equidistant Cylindrical projected CRS. This one is a special case because it
     * is simplified to an affine transform. The referencing module should be able to find the
     * projection parameters from the affine transform.
     *
     * @throws ParseException Should never happen.
     */
    @Test
    public void testEquidistantCylindrical() throws ParseException {
        final WKTFormat wktFormat = new WKTFormat();
        String wkt = decodeQuotes(
                "PROJCS[“Equidistant Cylindrical”,\n" +
                "  GEOGCS[“WGS 84”,\n" +
                "    DATUM[“World Geodetic System 1984”,\n" +
                "      SPHEROID[“WGS 84”, 6378137.0, 298.257223563]],\n" +
                "    PRIMEM[“Greenwich”, 0.0],\n" +
                "    UNIT[“degree”, 0.017453292519943295],\n" +
                "    AXIS[“Geodetic latitude”, NORTH],\n" +
                "    AXIS[“Geodetic longitude”, EAST]],\n" +
                "  PROJECTION[“Equidistant_Cylindrical”],\n" +
                "  PARAMETER[“central_meridian”, 0.0],\n" +
                "  PARAMETER[“latitude_of_origin”, 10.0],\n" +
                "  PARAMETER[“false_easting”, 1000.0],\n" +
                "  PARAMETER[“false_northing”, 2000.0],\n" +
                "  UNIT[“metre”, 1.0],\n" +
                "  AXIS[“Easting”, EAST],\n" +
                "  AXIS[“Northing”, NORTH]]\n");
        CoordinateReferenceSystem crs = wktFormat.parse(wkt, 0, CoordinateReferenceSystem.class);
        assertMultilinesEquals(wkt, wktFormat.format(crs));
    }

    /**
     * Tests the formatting using the name of different authorities.
     *
     * @throws FactoryException Should never happen.
     */
    @Test
    public void testAliases() throws FactoryException {
        final DefaultProjectedCRS crs = (DefaultProjectedCRS) CRS.parseWKT(decodeQuotes(
            "PROJCS[“OSGB 1936 / British National Grid”,\n" +
            "  GEOGCS[“OSGB 1936”,\n" +
            "    DATUM[“OSGB_1936”,\n" +
            "      SPHEROID[“Airy 1830”, 6377563.396, 299.3249646],\n" +
            "      TOWGS84[375.0, -111.0, 431.0, 0.0, 0.0, 0.0, 0.0]],\n" +
            "    PRIMEM[“Greenwich”,0.0],\n" +
            "    UNIT[“DMSH”,0.0174532925199433],\n" +
            "    AXIS[“Lat”,NORTH],AXIS[“Long”,EAST]],\n" +
            "  PROJECTION[“Transverse_Mercator”],\n" +
            "  PARAMETER[“latitude_of_origin”, 49.0],\n" +
            "  PARAMETER[“central_meridian”, -2.0],\n" +
            "  PARAMETER[“scale_factor”, 0.999601272],\n" +
            "  PARAMETER[“false_easting”, 400000.0],\n" +
            "  PARAMETER[“false_northing”, -100000.0],\n" +
            "  UNIT[“metre”, 1.0],\n" +
            "  AXIS[“E”,EAST],\n" +
            "  AXIS[“N”,NORTH]]"));
        /*
         * Formats using OGC identifiers. Should be the same than above.
         */
        assertMultilinesEquals(decodeQuotes(
            "PROJCS[“OSGB 1936 / British National Grid”,\n" +
            "  GEOGCS[“OSGB 1936”,\n" +
            "    DATUM[“OSGB_1936”,\n" +
            "      SPHEROID[“Airy 1830”, 6377563.396, 299.3249646],\n" +
            "      TOWGS84[375.0, -111.0, 431.0, 0.0, 0.0, 0.0, 0.0]],\n" +
            "    PRIMEM[“Greenwich”, 0.0],\n" +
            "    UNIT[“degree”, 0.017453292519943295],\n" +
            "    AXIS[“Latitude”, NORTH],\n" +
            "    AXIS[“Longitude”, EAST]],\n" +
            "  PROJECTION[“Transverse_Mercator”],\n" +
            "  PARAMETER[“central_meridian”, -2.0],\n" +
            "  PARAMETER[“latitude_of_origin”, 49.0],\n" +
            "  PARAMETER[“scale_factor”, 0.999601272],\n" +
            "  PARAMETER[“false_easting”, 400000.0],\n" +
            "  PARAMETER[“false_northing”, -100000.0],\n" +
            "  UNIT[“metre”, 1.0],\n" +
            "  AXIS[“Easting”, EAST],\n" +
            "  AXIS[“Northing”, NORTH]]"),
            crs.toWKT(Citations.OGC, 2));
        /*
         * Formats using GeoTiff identifiers. We should get different strings in PROJECTION[...]
         * and PARAMETER[...] elements, but the other ones (especially DATUM[...]) are unchanged.
         * The changes in UNIT[...] and AXIS[...] are related to the way those objects are built
         * and are independants of the Citation argument.
         */
        assertMultilinesEquals(decodeQuotes(
            "PROJCS[“OSGB 1936 / British National Grid”,\n" +
            "  GEOGCS[“OSGB 1936”,\n" +
            "    DATUM[“OSGB_1936”,\n" +
            "      SPHEROID[“Airy 1830”, 6377563.396, 299.3249646],\n" +
            "      TOWGS84[375.0, -111.0, 431.0, 0.0, 0.0, 0.0, 0.0]],\n" +
            "    PRIMEM[“Greenwich”, 0.0],\n" +
            "    UNIT[“degree”, 0.017453292519943295],\n" +
            "    AXIS[“Latitude”, NORTH],\n" +
            "    AXIS[“Longitude”, EAST]],\n" +
            "  PROJECTION[“CT_TransverseMercator”],\n" +
            "  PARAMETER[“NatOriginLong”, -2.0],\n" +
            "  PARAMETER[“NatOriginLat”, 49.0],\n" +
            "  PARAMETER[“ScaleAtNatOrigin”, 0.999601272],\n" +
            "  PARAMETER[“FalseEasting”, 400000.0],\n" +
            "  PARAMETER[“FalseNorthing”, -100000.0],\n" +
            "  UNIT[“metre”, 1.0],\n" +
            "  AXIS[“Easting”, EAST],\n" +
            "  AXIS[“Northing”, NORTH]]"),
            crs.toWKT(Citations.GEOTIFF, 2));
        /*
         * Formats using ESRI identifiers. The most important change we are looking for is
         * the name inside DATUM[...].
         */
        assertMultilinesEquals(decodeQuotes(
            "PROJCS[“OSGB 1936 / British National Grid”,\n" +
            "  GEOGCS[“OSGB 1936”,\n" +
            "    DATUM[“D_OSGB_1936”,\n" +
            "      SPHEROID[“Airy 1830”, 6377563.396, 299.3249646],\n" +
            "      TOWGS84[375.0, -111.0, 431.0, 0.0, 0.0, 0.0, 0.0]],\n" +
            "    PRIMEM[“Greenwich”, 0.0],\n" +
            "    UNIT[“degree”, 0.017453292519943295],\n" +
            "    AXIS[“Latitude”, NORTH],\n" +
            "    AXIS[“Longitude”, EAST]],\n" +
            "  PROJECTION[“Transverse_Mercator”],\n" +
            "  PARAMETER[“Central_Meridian”, -2.0],\n" +
            "  PARAMETER[“Latitude_Of_Origin”, 49.0],\n" +
            "  PARAMETER[“Scale_Factor”, 0.999601272],\n" +
            "  PARAMETER[“False_Easting”, 400000.0],\n" +
            "  PARAMETER[“False_Northing”, -100000.0],\n" +
            "  UNIT[“meter”, 1.0],\n" +
            "  AXIS[“Easting”, EAST],\n" +
            "  AXIS[“Northing”, NORTH]]"),
            crs.toWKT(Citations.ESRI, 2));
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
            "    PRIMEM[“Greenwich”, 0.0],\n" +
            "    UNIT[“degree”, 0.017453292519943295],\n" +
            "    AXIS[“Latitude”, NORTH],\n" +
            "    AXIS[“Longitude”, EAST]],\n" +
            "  PROJECTION[“Transverse Mercator”],\n" +
            "  PARAMETER[“Longitude of natural origin”, -2.0],\n" +
            "  PARAMETER[“Latitude of natural origin”, 49.0],\n" +
            "  PARAMETER[“Scale factor at natural origin”, 0.999601272],\n" +
            "  PARAMETER[“False easting”, 400000.0],\n" +
            "  PARAMETER[“False northing”, -100000.0],\n" +
            "  UNIT[“metre”, 1.0],\n" +
            "  AXIS[“Easting”, EAST],\n" +
            "  AXIS[“Northing”, NORTH]]"),
            crs.toWKT(Citations.EPSG, 2));
    }
}
