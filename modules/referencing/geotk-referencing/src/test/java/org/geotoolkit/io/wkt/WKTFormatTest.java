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

import java.text.ParseException;
import javax.measure.unit.NonSI;

import org.opengis.util.FactoryException;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.cs.CartesianCS;
import org.opengis.referencing.cs.AxisDirection;
import org.opengis.referencing.crs.ProjectedCRS;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

import org.geotoolkit.factory.Hints;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.cs.DefaultCartesianCS;
import org.geotoolkit.referencing.crs.DefaultProjectedCRS;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.referencing.crs.DefaultGeocentricCRS;
import org.geotoolkit.referencing.cs.DefaultCoordinateSystemAxis;
import org.geotoolkit.referencing.cs.DefaultEllipsoidalCS;
import org.geotoolkit.referencing.datum.DefaultEllipsoid;
import org.geotoolkit.referencing.datum.DefaultPrimeMeridian;
import org.geotoolkit.referencing.datum.DefaultGeodeticDatum;
import org.geotoolkit.referencing.operation.DefaultMathTransformFactory;
import org.geotoolkit.referencing.factory.DatumAliasesTest;

import org.apache.sis.io.wkt.Symbols;
import org.apache.sis.test.DependsOn;
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
@DependsOn({ParserTest.class, DatumAliasesTest.class})
public final strictfp class WKTFormatTest {
    /**
     * Test a hard coded version of a WKT. This is more convenient for debugging.
     *
     * @throws ParseException Should never happen.
     */
    @Test
    public void parseMercator() throws ParseException {
        final String wkt1 = decodeQuotes(
               "PROJCS[“Mercator test”,\n" +
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
               "  AXIS[“y”, NORTH]]\n");
        assertTrue(Symbols.DEFAULT.containsAxis(wkt1));
        final WKTFormat wktFormat = new WKTFormat();
        final DefaultProjectedCRS crs1  = (DefaultProjectedCRS) wktFormat.parseObject(wkt1);
        final String              wkt2  = wktFormat.format(crs1);
        final DefaultProjectedCRS crs2  = (DefaultProjectedCRS) wktFormat.parseObject(wkt2);
        final ParameterValueGroup param = crs1.getConversionFromBase().getParameterValues();
        assertEqualsIgnoreMetadata(crs1, crs2);
// TODO assertEquals(crs1, crs2);
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
    }

    /**
     * Same Mercator projection as above, but
     * with longitude and latitude axes swapped.
     *
     * @throws ParseException Should never happen.
     */
    @Test
    public void parseMercatorφλ() throws ParseException {
        final String wkt1 = decodeQuotes(
               "PROJCS[“Mercator test”,\n" +
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
               "  AXIS[“y”, NORTH]]\n");
        assertTrue(Symbols.DEFAULT.containsAxis(wkt1));
        final WKTFormat wktFormat = new WKTFormat();
        final DefaultProjectedCRS crs1  = (DefaultProjectedCRS) wktFormat.parseObject(wkt1);
        final String              wkt2  = wktFormat.format(crs1);
        final DefaultProjectedCRS crs2  = (DefaultProjectedCRS) wktFormat.parseObject(wkt2);
        final ParameterValueGroup param = crs1.getConversionFromBase().getParameterValues();
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
               "    PRIMEM[“Greenwich”,0.0, AUTHORITY[“EPSG”, “8901”]],\n" +
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
        assertTrue(Symbols.DEFAULT.containsAxis(wkt1));
        final WKTFormat wktFormat = new WKTFormat();
        final DefaultProjectedCRS crs1  = (DefaultProjectedCRS) wktFormat.parseObject(wkt1);
        final String              wkt2  = wktFormat.format(crs1);
        final DefaultProjectedCRS crs2  = (DefaultProjectedCRS) wktFormat.parseObject(wkt2);
        final ParameterValueGroup param = crs1.getConversionFromBase().getParameterValues();
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
               "    PRIMEM[“Greenwich”, 0.0],\n" +
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
        assertTrue(Symbols.DEFAULT.containsAxis(wkt1));
        final WKTFormat wktFormat = new WKTFormat();
        final DefaultProjectedCRS crs1  = (DefaultProjectedCRS) wktFormat.parseObject(wkt1);
        final String              wkt2  = wktFormat.format(crs1);
        final DefaultProjectedCRS crs2  = (DefaultProjectedCRS) wktFormat.parseObject(wkt2);
        final ParameterValueGroup param = crs1.getConversionFromBase().getParameterValues();
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
    }

    /**
     * Try with a number using scientific notation.
     *
     * @throws ParseException Should never happen.
     */
    @Test
    public void parseScientificNotation() throws ParseException {
        final String wkt1 = decodeQuotes(
               "GEOGCS[“NAD83 / NFIS Seconds”,DATUM[“North_American_Datum_1983”,\n" +
               "  SPHEROID[“GRS 1980”, 6378137, 298.257222101]],\n" +
               "  PRIMEM[“Greenwich”, 0],\n" +
               "  UNIT[“Decimal_Second”, 4.84813681109536e-06],\n" +
               "  AUTHORITY[“EPSG”, “100001”]]");
        assertFalse(Symbols.DEFAULT.containsAxis(wkt1));
        final WKTFormat wktFormat = new WKTFormat();
        final String wkt2 = wktFormat.format(wktFormat.parseObject(wkt1));
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
    public void parseAndFormatCustomAxisLength() throws FactoryException, ParseException {
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
        CartesianCS   cs   = DefaultCartesianCS.PROJECTED;
        CoordinateReferenceSystem crs = new DefaultProjectedCRS("Lambert", base, mt, cs);

        final String wkt = crs.toWKT();
        assertTrue(wkt.contains("semi_major"));
        assertTrue(wkt.contains("semi_minor"));
        final ReferencingParser parser = new ReferencingParser(Symbols.DEFAULT, (Hints) null);
        CoordinateReferenceSystem check = parser.parseCoordinateReferenceSystem(wkt);
        assertEquals(wkt, check.toWKT());
    }

    /**
     * Tests the Geocentric case, which requires a conversion of axis directions
     * between ISO 19111 and OGC 01-009 values.
     *
     * @throws ParseException Should never happen.
     */
    @Test
    public void parseAndFormatGeocentric() throws ParseException {
        final WKTFormat wktFormat = new WKTFormat();
        /*
         * First try the formatting as internal WKT. Geotk
         * uses internally the ISO 19111 axis directions.
         */
        final String name = DefaultGeocentricCRS.CARTESIAN.getName().getCode();
        final DefaultGeocentricCRS crs = DefaultGeocentricCRS.CARTESIAN;
        String wkt = decodeQuotes(
                "GEOCCS[“" + name + "”,\n" +
                "  DATUM[“WGS84”,\n" +
                "    SPHEROID[“WGS84”, 6378137.0, 298.257223563, AUTHORITY[“EPSG”, “7030”]],\n" +
                "    AUTHORITY[“EPSG”, “6326”]],\n" +
                "  PRIMEM[“Greenwich”, 0.0, AUTHORITY[“EPSG”, “8901”]],\n" +
                "  UNIT[“metre”, 1.0],\n" +
                "  AXIS[“Geocentric X”, GEOCENTRIC_X],\n" +
                "  AXIS[“Geocentric Y”, GEOCENTRIC_Y],\n" +
                "  AXIS[“Geocentric Z”, GEOCENTRIC_Z]]");
        wktFormat.setConvention(Convention.INTERNAL);
        assertMultilinesEquals(wkt, wktFormat.format(crs));
        assertEqualsIgnoreMetadata(crs, wktFormat.parseObject(wkt), false);
        /*
         * Now try the fomatting as standard WKT.
         */
        wkt = decodeQuotes(
                "GEOCCS[“" + name + "”,\n" +
                "  DATUM[“WGS84”,\n" +
                "    SPHEROID[“WGS84”, 6378137.0, 298.257223563, AUTHORITY[“EPSG”, “7030”]],\n" +
                "    AUTHORITY[“EPSG”, “6326”]],\n" +
                "  PRIMEM[“Greenwich”, 0.0, AUTHORITY[“EPSG”, “8901”]],\n" +
                "  UNIT[“metre”, 1.0],\n" +
                "  AXIS[“X”, OTHER],\n" +
                "  AXIS[“Y”, EAST],\n" +
                "  AXIS[“Z”, NORTH]]");
        wktFormat.setConvention(Convention.OGC);
        assertMultilinesEquals(wkt, wktFormat.format(crs));
        assertEqualsIgnoreMetadata(crs, wktFormat.parseObject(wkt), false);
    }

    /**
     * Tests the Equidistant Cylindrical projected CRS. This one is a special case because it
     * is simplified to an affine transform. The referencing module should be able to find the
     * projection parameters from the affine transform.
     * <p>
     * This method tests also indirectly the datum aliases, since the
     * “World Geodetic System 1984” datum should be formatted as “WGS84”
     * according OGC authority names.
     *
     * @throws ParseException Should never happen.
     */
    @Test
    public void parseAndFormatEquidistantCylindrical() throws ParseException {
        final WKTFormat wktFormat = new WKTFormat();
        String wkt = decodeQuotes(
                "PROJCS[“Equidistant Cylindrical”,\n" +
                "  GEOGCS[“WGS 84”,\n" +
                "    DATUM[“World Geodetic System 1984”,\n" +
                "      SPHEROID[“WGS84”, 6378137.0, 298.257223563]],\n" +
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
                "  AXIS[“Northing”, NORTH]]");
        CoordinateReferenceSystem crs = wktFormat.parse(wkt, 0, CoordinateReferenceSystem.class);
        wkt = wkt.replace("World Geodetic System 1984", "WGS84"); // DATUM name change.
        assertMultilinesEquals(wkt, wktFormat.format(crs));
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
            crs.toString(Convention.OGC));
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
            crs.toString(Convention.GEOTIFF));
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
            crs.toString(Convention.ESRI));
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
            crs.toString(Convention.EPSG));
    }

    /**
     * Formats a CRS using the Paris meridian.
     *
     * @since 3.20
     */
    @Test
    public void formatParisMeridian() {
        final DefaultGeographicCRS crs = new DefaultGeographicCRS("NTF (Paris)",
            new DefaultGeodeticDatum("Nouvelle Triangulation Francaise (Paris)",
                DefaultEllipsoid.CLARKE_1866, // Actually Clark 1880, but we don't care for this test.
                new DefaultPrimeMeridian("Paris", 2.5969213, NonSI.GRADE)),
            new DefaultEllipsoidalCS("Using grade",
                new DefaultCoordinateSystemAxis("λ", AxisDirection.EAST,  NonSI.GRADE),
                new DefaultCoordinateSystemAxis("φ", AxisDirection.NORTH, NonSI.GRADE)));

        // Standard formating
        assertMultilinesEquals(decodeQuotes(
            "GEOGCS[“NTF (Paris)”,\n" +
            "  DATUM[“Nouvelle Triangulation Francaise (Paris)”,\n" +
            "    SPHEROID[“Clarke 1866”, 6378206.4, 294.9786982138982, AUTHORITY[“EPSG”, “7008”]]],\n" +
            "  PRIMEM[“Paris”, 2.5969213],\n" +
            "  UNIT[“grade”, 0.015707963267948967],\n" +
            "  AXIS[“λ”, EAST],\n" +
            "  AXIS[“φ”, NORTH]]"),
            crs.toString(Convention.OGC));

        // ESRI flavor: prime meridian in degrees.
        assertMultilinesEquals(decodeQuotes(
            "GEOGCS[“NTF (Paris)”,\n" +
            "  DATUM[“Nouvelle Triangulation Francaise (Paris)”,\n" +
            "    SPHEROID[“Clarke 1866”, 6378206.4, 294.9786982138982, AUTHORITY[“EPSG”, “7008”]]],\n" +
            "  PRIMEM[“Paris”, 2.33722917],\n" +
            "  UNIT[“grade”, 0.015707963267948967],\n" +
            "  AXIS[“λ”, EAST],\n" +
            "  AXIS[“φ”, NORTH]]"),
            crs.toString(Convention.ESRI));
    }

    /**
     * Tests the parsing and formatting of a WKT using ESRI conventions.
     *
     * @throws ParseException Should never happen.
     *
     * @since 3.20
     */
    @Test
    public void testEsriConvention() throws ParseException {
        final WKTFormat wktFormat = new WKTFormat();
        ProjectedCRS crs = (ProjectedCRS) wktFormat.parseObject(ParserTest.IGNF_LAMBE);
        ParserTest.verifyLambertII(crs, false);
        /*
         * Now force the angular unit to degrees, and test again.
         */
        wktFormat.setConvention(Convention.ESRI);
        crs = (ProjectedCRS) wktFormat.parseObject(ParserTest.IGNF_LAMBE);
        ParserTest.verifyLambertII(crs, true);
        /*
         * When formatting using ESRI conventions, the angles shall be in degrees.
         */
        String wkt = wktFormat.format(crs);
        assertTrue(wkt, wkt.contains("PRIMEM[\"Paris\", 2.337229167"));
        assertTrue(wkt, wkt.contains("PARAMETER[\"latitude_of_origin\", 46.8"));
        /*
         * When formatting using OGC conventions, the angles shall be in gradians
         * (in the particular case of this CRS).
         */
        wktFormat.setConvention(Convention.OGC);
        wkt = wktFormat.format(crs);
        assertTrue(wkt, wkt.contains("PRIMEM[\"Paris\", 2.59692129"));
        assertTrue(wkt, wkt.contains("PARAMETER[\"latitude_of_origin\", 52.0"));
    }
}
