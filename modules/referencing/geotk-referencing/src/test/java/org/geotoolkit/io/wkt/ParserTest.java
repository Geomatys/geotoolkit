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

import java.util.HashSet;
import java.util.Collection;
import java.text.ParseException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.LineNumberReader;
import java.io.FileNotFoundException;
import javax.measure.unit.NonSI;

import org.opengis.util.FactoryException;
import org.opengis.referencing.crs.ProjectedCRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.apache.sis.util.ComparisonMode;
import org.apache.sis.util.LenientComparable;
import org.apache.sis.io.wkt.Symbols;
import org.apache.sis.io.wkt.Convention;
import org.apache.sis.io.wkt.FormattableObject;
import org.apache.sis.test.DependsOn;
import org.geotoolkit.test.TestData;
import org.apache.sis.referencing.datum.DefaultPrimeMeridian;
import org.geotoolkit.referencing.operation.projection.FormattingTest;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests the {@link Parser} implementations.
 *
 * @author Yann Cézard (IRD)
 * @author Rémi Eve (IRD)
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.20
 *
 * @since 2.0
 */
@DependsOn(FormattingTest.class)
public final strictfp class ParserTest {
    /**
     * Parses all elements from the specified file. Parsing creates a set of
     * geographic objects. No special processing are done with them; we just
     * check if the parsing work without error and produces distinct objects.
     *
     * @throws FactoryException Should never happen.
     * @throws ParseException Should never happen.
     */
    private static void parse(final String filename) throws IOException, ParseException {
        final WKTFormat parser = new WKTFormat();
        try (LineNumberReader reader = TestData.openReader(ParserTest.class, filename)) {   // LGPL
            if (reader == null) {
                throw new FileNotFoundException(filename);
            }
            final Collection<Object> pool = new HashSet<>();
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                /*
                 * Parses a line. If the parse fails, then dump the WKT and rethrow the exception.
                 */
                final Object parsed;
                try {
                    parsed = parser.parseObject(line);
                } catch (ParseException exception) {
                    final PrintStream out = System.err;
                    out.println();
                    out.println("-----------------------------");
                    out.println("Parse failed. Dump WKT below.");
                    out.println("-----------------------------");
                    out.println(line);
                    out.println();
                    throw exception;
                }
                assertNotNull("Parsing returns null.",                 parsed);
                assertEquals("Inconsistent equals method",             parsed, parsed);
                assertSame("Parsing twice returns different objects.", parsed, parser.parseObject(line));
                assertTrue("An identical object already exists.",      pool.add(parsed));
                assertTrue("Inconsistent hashCode or equals method.",  pool.contains(parsed));
                /*
                 * Formats the object and parse it again.
                 * Ensures that the result is consistent.
                 */
                if (!(parsed instanceof FormattableObject)) return;
                String formatted = ((FormattableObject) parsed).toString(Convention.WKT1);
                final Object again;
                try {
                    again = parser.parseObject(formatted);
                } catch (ParseException exception) {
                    final PrintStream out = System.err;
                    out.println();
                    out.println("------------------------------------");
                    out.println("Second parse failed. Dump WKT below.");
                    out.println("------------------------------------");
                    out.println(line);
                    out.println();
                    out.println("------ Reformatted WKT below -------");
                    out.println();
                    out.println(formatted);
                    out.println();
                    throw exception;
                }
                final LenientComparable c = (LenientComparable) parsed;
                try {
                    assertTrue  ("Second parsing produced different objects.", c.equals(again, ComparisonMode.DEBUG));
                    assertEquals("Second parsing produced different objects.", c, again);
                    assertTrue  ("Inconsistent hashCode or equals method.", pool.contains(again));
                } catch (AssertionError e) {
                    System.err.println("Error at line " + reader.getLineNumber());
                    throw e;
                }
            }
        }
    }

    /**
     * Tests parsing of coordinate reference systems.
     *
     * @throws IOException Should never happen.
     * @throws ParseException Should never happen.
     */
    @Test
    @Ignore
    public void testCoordinateReferenceSystem() throws IOException, ParseException {
        parse("CoordinateReferenceSystem.txt");
    }

    /**
     * Tests a WKT having an unquoted integer authority code instead than a quoted string.
     * This test has been added after a test case provided by a user on the mailing list.
     *
     * @throws ParseException Should never happen.
     */
    @Test
    public void testIntegerAuthorityCodeWKT() throws ParseException {
        final String wkt =
            "PROJCS[\"North_Pole_Stereographic\"," +
              "GEOGCS[\"GCS_WGS_1984\"," +
                "DATUM[\"D_WGS_1984\"," +
                  "SPHEROID[\"WGS_1984\",6378137.0,298.257223563]]," +
                "PRIMEM[\"Greenwich\",0.0]," +
                "UNIT[\"Degree\",0.0174532925199433]]," +
              "PROJECTION[\"Stereographic\"]," +
              "PARAMETER[\"False_Easting\",0.0]," +
              "PARAMETER[\"False_Northing\",0.0]," +
              "PARAMETER[\"Central_Meridian\",0.0]," +
              "PARAMETER[\"Scale_Factor\",1.0]," +
              "PARAMETER[\"Latitude_Of_Origin\",90.0]," +
              "UNIT[\"Meter\",1.0]," +
              "AUTHORITY[\"ESRI\",102018]]";

        final WKTFormat parser = new WKTFormat();
        CoordinateReferenceSystem crs = (CoordinateReferenceSystem) parser.parseObject(wkt);
        assertEquals(1, crs.getIdentifiers().size());
        assertEquals("102018", crs.getIdentifiers().iterator().next().getCode());
    }

    /**
     * Tests the Oracle variant of WKT.
     *
     * @throws ParseException Should never happen.
     */
    @Test
    @Ignore("Oracle format no longer supported.")
    public void testOracleWKT() throws ParseException {
        final String wkt =
            "PROJCS[\"Datum 73 / Modified Portuguese Grid\"," +
             " GEOGCS [ \"Datum 73\"," +
               " DATUM[\"Datum 73 (EPSG ID 6274)\"," +
                 " SPHEROID [\"International 1924 (EPSG ID 7022)\", 6378388, 297]," +
                 " -231, 102.6, 29.8, 0.615, -0.198, 0.881, .99999821]," +
               " PRIMEM [ \"Greenwich\", 0.000000 ]," +
               " UNIT [\"Decimal Degree\", 0.01745329251994328]]," +
             " PROJECTION[\"Transverse_Mercator\"]," +
//           " PROJECTION[\"Modified Portuguese Grid (EPSG OP 19974)\"]," +
// TODO: The real projection is "Modified Portugues", but it is not yet implemented in Geotk.
             " PARAMETER[\"Latitude_Of_Origin\", 39.666666666666667]," +
             " PARAMETER[\"Central_Meridian\", -8.13190611111111]," +
             " PARAMETER[\"Scale_Factor\", 1]," +
             " PARAMETER [\"False_Easting\", 180.598]," +
             " PARAMETER[\"False_Northing\", -86.99]," +
             " UNIT [\"Meter\", 1]]";

        assertFalse(Symbols.getDefault().containsAxis(wkt));
        final WKTFormat parser = new WKTFormat();
        final CoordinateReferenceSystem crs1 = (CoordinateReferenceSystem) parser.parseObject(wkt);
        final String check = ((FormattableObject) crs1).toString(Convention.WKT1);
        assertTrue(check.contains("TOWGS84[-231"));
        final CoordinateReferenceSystem crs2 = (CoordinateReferenceSystem) parser.parseObject(check);
        assertTrue(((LenientComparable) crs1).equals(crs2, ComparisonMode.DEBUG));
        assertTrue(((LenientComparable) crs1).equals(crs2, ComparisonMode.BY_CONTRACT));
        assertEquals(crs1, crs2);
        assertFalse(check.contains("semi_major"));
        assertFalse(check.contains("semi_minor"));
    }

    /**
     * A coordinate reference system defined by IGNF. This WKT is not strictly standard-compliant,
     * in that it wrongly (from the WKT standard point of view) define the {@code PRIMEM} in degrees
     * unit, while it shall be in gradian unit.
     *
     * @since 3.20
     */
    static final String IGNF_LAMBE =
        "PROJCS[\"Lambert II étendu\"," +
            " GEOGCS[\"Nouvelle Triangulation Française Paris grades\"," +
            " DATUM[\"NTF\"," +
              " SPHEROID[\"Clarke 1880 IGN\", 6378249.2, 293.466021, AUTHORITY[\"IGNF\",\"ELG010\"]]," +
              " TOWGS84[-168,-60,320,0,0,0,0]," +
              " AUTHORITY[\"IGNF\",\"REG002\"]]," +
            " PRIMEM[\"Paris\", 2.337229167, AUTHORITY[\"IGNF\",\"LGO02\"]]," + // Should be PRIMEM["Paris", 2.5969213] because of gradian unit.
            " UNIT[\"grad\", 0.01570796326794897]," +
            " AXIS[\"Longitude\", EAST]," +
            " AXIS[\"Latitude\", NORTH]," +
            " AUTHORITY[\"IGNF\",\"NTFP\"]]," +
          " PROJECTION[\"Lambert_Conformal_Conic_1SP\", AUTHORITY[\"IGNF\",\"PRC0120\"]]," +
          " PARAMETER[\"semi_major\", 6378249.2]," +
          " PARAMETER[\"semi_minor\", 6356515.0]," +
          " PARAMETER[\"latitude_of_origin\", 46.8]," + // Should be 52 in gradian unit.
          " PARAMETER[\"central_meridian\", 0.0]," +
          " PARAMETER[\"scale_factor\", 0.99987742]," +
          " PARAMETER[\"false_easting\", 600000.0]," +
          " PARAMETER[\"false_northing\", 2200000.0]," +
          " UNIT[\"metre\",1]," +
          " AXIS[\"Easting\",EAST]," +
          " AXIS[\"Northing\",NORTH]," +
          " AUTHORITY[\"IGNF\",\"LAMBE\"]]";

    /**
     * Verifies the CRS parsed from the {@link #IGNF_LAMBE} string.
     *
     * @param crs The parsed CRS.
     */
    static void verifyLambertII(final ProjectedCRS crs, final boolean forceDegreeUnit) {
        assertEquals(forceDegreeUnit ? "Expected the real prime meridian value, in degrees." :
                "Expected a value converted from gradians to degrees, despite resulting to a unintented value.",
                forceDegreeUnit ? 2.337229167 : 2.103506250,
                ((DefaultPrimeMeridian) crs.getDatum().getPrimeMeridian()).getGreenwichLongitude(NonSI.DEGREE_ANGLE), 1E-9);

        assertEquals(forceDegreeUnit ? "Expected the real latitude of origin value, in degrees." :
                "Expected a value converted from gradians to degrees, despite resulting to a unintented value.",
                forceDegreeUnit ? 46.8 : 42.12,
                crs.getConversionFromBase().getParameterValues().parameter("latitude_of_origin").doubleValue(NonSI.DEGREE_ANGLE), 0.01);
    }

    /**
     * Tests the parsing of a WKT using ESRI conventions.
     *
     * @throws ParseException Should never happen.
     *
     * @since 3.20
     */
    @Test
    public void testEsriConventions() throws ParseException {
        final WKTFormat parser = new WKTFormat();
        verifyLambertII((ProjectedCRS) parser.parseObject(IGNF_LAMBE), false);
        /*
         * Now force the angular unit to degrees, and test again.
         */
        parser.setConvention(Convention.WKT1_COMMON_UNITS);
        verifyLambertII((ProjectedCRS) parser.parseObject(IGNF_LAMBE), true);
    }
}
