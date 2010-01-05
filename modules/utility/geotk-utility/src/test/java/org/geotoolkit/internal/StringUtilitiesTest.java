/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.internal;

import org.junit.*;
import static org.junit.Assert.*;

import static org.geotoolkit.internal.StringUtilities.*;


/**
 * Tests {@link StringUtilities} methods.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.07
 *
 * @since 3.00
 */
public final class StringUtilitiesTest {
    /**
     * Tests the {@link StringUtilities#replace} method.
     */
    @Test
    public void testReplace() {
        final StringBuilder buffer = new StringBuilder("One two three two one");
        replace(buffer, "two", "zero");
        assertEquals("One zero three zero one", buffer.toString());
        replace(buffer, "zero", "ten");
        assertEquals("One ten three ten one", buffer.toString());
    }

    /**
     * Tests the {@link StringUtilities#remove} method.
     *
     * @since 3.06
     */
    @Test
    public void testRemove() {
        final StringBuilder buffer = new StringBuilder("EPSG.6.7");
        remove(buffer, ".");
        assertEquals("EPSG67", buffer.toString());
    }

    /**
     * Tests the {@link StringUtilities#removeLF} method.
     */
    @Test
    public void testRemoveLF() {
        final StringBuilder buffer = new StringBuilder(" \nOne,\nTwo, \n Three Four\nFive \nSix \n");
        removeLF(buffer);
        assertEquals("One,Two,Three Four Five Six", buffer.toString());
    }

    /**
     * Tests the {@link StringUtilities#separateWords} method.
     */
    @Test
    public void testSeparateWords() {
        assertEquals("Pixel interleaved sample model",
                separateWords("PixelInterleavedSampleModel").toString());
    }

    /**
     * Tests the {@link StringUtilities#splitLines} method.
     */
    @Test
    public void testSplitLines() {
        final String[] splitted = splitLines("\nOne\r\nTwo\rThree\rFour\nFive\n\rSix\n");
        assertArrayEquals(new String[] {
            "",
            "One",
            "Two",
            "Three",
            "Four",
            "Five",
            "",
            "Six",
            ""
        }, splitted);
    }

    /**
     * Tests the {@link StringUtilities#token} method.
     */
    @Test
    public void testToken() {
        assertEquals("Id4", token("..Id4  56B..", 2));
        assertEquals("56",  token("..Id4  56B..", 6));
    }

    /**
     * Tests the {@link StringUtilities#acronym} method.
     */
    @Test
    public void testAcronym() {
        assertEquals("OGC", acronym("OGC"));
        assertEquals("OGC", acronym("Open Geospatial Consortium"));
        assertEquals("E",   acronym("East"));
        assertEquals("NE",  acronym("North-East"));
        assertEquals("NE",  acronym("NORTH_EAST"));
        assertEquals("NE",  acronym("northEast"));
    }

    /**
     * Tests the {@link StringUtilities#equalsAcronym} method.
     */
    @Test
    public void testEqualsAcronym() {
        /*
         * Following should be accepted as acronyms...
         */
        assertTrue(equalsAcronym("Open Geospatial Consortium", "OGC"));
        assertTrue(equalsAcronym("Open Geospatial Consortium", "O.G.C."));
        assertTrue(equalsAcronym("Open Geospatial Consortium", "OpGeoCon"));
        assertTrue(equalsAcronym("Open Geospatial Consortium", "Open Geospatial Consortium"));
        assertTrue(equalsAcronym("Open Geospatial Consortium", "ogc"));
        /*
         * Following should be rejected...
         */
        assertFalse(equalsAcronym("Open Geospatial Consortium", "ORC"));
        assertFalse(equalsAcronym("Open Geospatial Consortium", "O.C.G."));
        assertFalse(equalsAcronym("Open Geospatial Consortium", "OGC2"));
        assertFalse(equalsAcronym("Open Geospatial Consortium", "OG"));
        assertFalse(equalsAcronym("Open Geospatial Consortium", "GC"));
        /*
         * Following are mapping of EPSG table names from MS-Access to ANSI SQL.
         * All those items must be recognized as acroynms - this is requred by DirectEpsgFactory.
         */
        assertTrue(equalsAcronym("[Alias]",                                "alias"));
        assertTrue(equalsAcronym("[Area]",                                 "area"));
        assertTrue(equalsAcronym("[Coordinate Axis]",                      "coordinateaxis"));
        assertTrue(equalsAcronym("[Coordinate Axis Name]",                 "coordinateaxisname"));
        assertTrue(equalsAcronym("[Coordinate_Operation]",                 "coordoperation"));
        assertTrue(equalsAcronym("[Coordinate_Operation Method]",          "coordoperationmethod"));
        assertTrue(equalsAcronym("[Coordinate_Operation Parameter]",       "coordoperationparam"));
        assertTrue(equalsAcronym("[Coordinate_Operation Parameter Usage]", "coordoperationparamusage"));
        assertTrue(equalsAcronym("[Coordinate_Operation Parameter Value]", "coordoperationparamvalue"));
        assertTrue(equalsAcronym("[Coordinate_Operation Path]",            "coordoperationpath"));
        assertTrue(equalsAcronym("[Coordinate Reference System]",          "coordinatereferencesystem"));
        assertTrue(equalsAcronym("[Coordinate System]",                    "coordinatesystem"));
        assertTrue(equalsAcronym("[Datum]",                                "datum"));
        assertTrue(equalsAcronym("[Ellipsoid]",                            "ellipsoid"));
        assertTrue(equalsAcronym("[Naming System]",                        "namingsystem"));
        assertTrue(equalsAcronym("[Prime Meridian]",                       "primemeridian"));
        assertTrue(equalsAcronym("[Supersession]",                         "supersession"));
        assertTrue(equalsAcronym("[Unit of Measure]",                      "unitofmeasure"));
        assertTrue(equalsAcronym("[Version History]",                      "versionhistory"));
        assertTrue(equalsAcronym("[Change]",                               "change"));
        assertTrue(equalsAcronym("[Deprecation]",                          "deprecation"));
        /*
         * It is important the the following is not recognized as an acronym,
         * otherwise it leads to a confusion in DirectEpsgFactory.
         */
        assertFalse(equalsAcronym("[Coordinate_Operation Method]", "coordoperation"));
    }
}
