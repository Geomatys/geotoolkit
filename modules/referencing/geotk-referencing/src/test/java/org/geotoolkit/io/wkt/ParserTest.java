/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2009, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.io.wkt;

import java.util.HashSet;
import java.util.Collection;
import java.text.ParseException;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.FileNotFoundException;

import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.geotoolkit.test.Depend;
import org.geotoolkit.test.TestData;
import org.geotoolkit.referencing.operation.projection.FormattingTest;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests the {@link Parser} implementations.
 *
 * @author Yann Cézard (IRD)
 * @author Rémi Eve (IRD)
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.00
 *
 * @since 2.0
 */
@Depend({SymbolsTest.class, FormattingTest.class})
public final class ParserTest {
    /**
     * Parses all elements from the specified file. Parsing creates a set of
     * geographic objects. No special processing are done with them; we just
     * check if the parsing work without error and produces distincts objects.
     *
     * @throws FactoryException Should never happen.
     * @throws ParseException Should never happen.
     */
    private static void parse(final Parser parser, final String filename)
            throws IOException, ParseException
    {
        final LineNumberReader reader = TestData.openReader(ParserTest.class, filename);
        if (reader == null) {
            throw new FileNotFoundException(filename);
        }
        final Collection<Object> pool = new HashSet<Object>();
        String line;
        while ((line=reader.readLine()) != null) {
            line = line.trim();
            if (line.length()==0 || line.startsWith("#")) {
                continue;
            }
            /*
             * Parses a line. If the parse fails, then dump the WKT and rethrow the exception.
             */
            final Object parsed;
            try {
                parsed = parser.parseObject(line);
            } catch (ParseException exception) {
                System.err.println();
                System.err.println("-----------------------------");
                System.err.println("Parse failed. Dump WKT below.");
                System.err.println("-----------------------------");
                System.err.println(line);
                System.err.println();
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
            String formatted = parsed.toString();
            final Object again;
            try {
                again = parser.parseObject(formatted);
            } catch (ParseException exception) {
                System.err.println();
                System.err.println("------------------------------------");
                System.err.println("Second parse failed. Dump WKT below.");
                System.err.println("------------------------------------");
                System.err.println(line);
                System.err.println();
                System.err.println("------ Reformatted WKT below -------");
                System.err.println();
                System.err.println(formatted);
                System.err.println();
                throw exception;
            }
            assertEquals("Second parsing produced different objects (line " +
                    reader.getLineNumber() + ").", parsed, again);
            assertTrue("Inconsistent hashCode or equals method (line " +
                    reader.getLineNumber() + ").", pool.contains(again));
        }
        reader.close();
    }

    /**
     * Tests parsing of math transforms.
     *
     * @throws IOException Should never happen.
     * @throws ParseException Should never happen.
     */
    @Test
    public void testMathTransform() throws IOException, ParseException {
        parse(new MathTransformParser(), "MathTransform.txt");
    }

    /**
     * Tests parsing of coordinate reference systems.
     *
     * @throws IOException Should never happen.
     * @throws ParseException Should never happen.
     */
    @Test
    public void testCoordinateReferenceSystem() throws IOException, ParseException {
        parse(new ReferencingParser(), "CoordinateReferenceSystem.txt");
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

        final ReferencingParser parser = new ReferencingParser();
        CoordinateReferenceSystem crs = parser.parseCoordinateReferenceSystem(wkt);
        assertEquals(1, crs.getIdentifiers().size());
        assertEquals("102018", crs.getIdentifiers().iterator().next().getCode());
    }

    /**
     * Tests the Oracle variant of WKT.
     *
     * @throws ParseException Should never happen.
     */
    @Test
    public void testOracleWKT() throws ParseException {
        final String wkt =
            "PROJCS[\"Datum 73 / Modified Portuguese Grid\"," +
             " GEOGCS [ \"Datum 73\"," +
               " DATUM[\"Datum 73 (EPSG ID 6274)\"," +
                 " SPHEROID [\"International 1924 (EPSG ID 7022)\", 6378388, 297]," +
                 " -231, 102.6, 29.8, .6149999999999993660366746131394108039579," +
                 " -.1979999999999997958947342656936639661522," +
                  " .8809999999999990918346509498793836069706, .99999821]," +
               " PRIMEM [ \"Greenwich\", 0.000000 ]," +
               " UNIT [\"Decimal Degree\", 0.01745329251994328]]," +
             " PROJECTION[\"Transverse_Mercator\"]," +
//           " PROJECTION[\"Modified Portuguese Grid (EPSG OP 19974)\"]," +
// TODO: The real projection is "Modified Portugues", but it is not yet implemented in Geotk.
             " PARAMETER[\"Latitude_Of_Origin\", 39.66666666666666666666666666666666666667]," +
             " PARAMETER[\"Central_Meridian\", -8.13190611111111111111111111111111111111]," +
             " PARAMETER[\"Scale_Factor\", 1]," +
             " PARAMETER [\"False_Easting\", 180.598]," +
             " PARAMETER[\"False_Northing\", -86.99]," +
             " UNIT [\"Meter\", 1]]";

        assertFalse(Symbols.DEFAULT.containsAxis(wkt));
        final ReferencingParser parser = new ReferencingParser();
        CoordinateReferenceSystem crs1 = parser.parseCoordinateReferenceSystem(wkt);
        final String check = crs1.toWKT();
        assertTrue(check.indexOf("TOWGS84[-231") >= 0);
        CoordinateReferenceSystem crs2 = parser.parseCoordinateReferenceSystem(check);
        assertEquals(crs1, crs2);
        assertFalse(check.contains("semi_major"));
        assertFalse(check.contains("semi_minor"));
    }
}
