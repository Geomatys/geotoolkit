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


/**
 * Tests {@link StringUtilities} methods.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
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
        StringUtilities.replace(buffer, "two", "zero");
        assertEquals("One zero three zero one", buffer.toString());
        StringUtilities.replace(buffer, "zero", "ten");
        assertEquals("One ten three ten one", buffer.toString());
    }

    /**
     * Tests the {@link StringUtilities#removeLF} method.
     */
    @Test
    public void testRemoveLF() {
        final StringBuilder buffer = new StringBuilder(" \nOne,\nTwo, \n Three Four\nFive \nSix \n");
        StringUtilities.removeLF(buffer);
        assertEquals("One,Two,Three Four Five Six", buffer.toString());
    }

    /**
     * Tests the {@link StringUtilities#separateWords} method.
     */
    @Test
    public void testSeparateWords() {
        assertEquals("Pixel interleaved sample model ",
                StringUtilities.separateWords("PixelInterleavedSampleModel").toString());
    }

    /**
     * Tests the {@link StringUtilities#splitLines} method.
     */
    @Test
    public void testSplitLines() {
        final String[] splitted = StringUtilities.splitLines("\nOne\r\nTwo\rThree\rFour\nFive\n\rSix\n");
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
     * Tests the {@link StringUtilities#equalsAcronym} method.
     */
    @Test
    public void testEqualsAcronym() {
        /*
         * Following should be accepted as acronyms...
         */
        assertTrue(StringUtilities.equalsAcronym("Open Geospatial Consortium", "OGC"));
        assertTrue(StringUtilities.equalsAcronym("Open Geospatial Consortium", "O.G.C."));
        assertTrue(StringUtilities.equalsAcronym("Open Geospatial Consortium", "OpGeoCon"));
        assertTrue(StringUtilities.equalsAcronym("Open Geospatial Consortium", "Open Geospatial Consortium"));
        assertTrue(StringUtilities.equalsAcronym("Open Geospatial Consortium", "ogc"));
        /*
         * Following should be rejected...
         */
        assertFalse(StringUtilities.equalsAcronym("Open Geospatial Consortium", "ORC"));
        assertFalse(StringUtilities.equalsAcronym("Open Geospatial Consortium", "O.C.G."));
        assertFalse(StringUtilities.equalsAcronym("Open Geospatial Consortium", "OGC2"));
        assertFalse(StringUtilities.equalsAcronym("Open Geospatial Consortium", "OG"));
        assertFalse(StringUtilities.equalsAcronym("Open Geospatial Consortium", "GC"));
        /*
         * Following are mapping of EPSG table names from MS-Access to ANSI SQL.
         * All those items must be recognized as acroynms - this is requred by DirectEpsgFactory.
         */
        assertTrue(StringUtilities.equalsAcronym("[Alias]",                                "alias"));
        assertTrue(StringUtilities.equalsAcronym("[Area]",                                 "area"));
        assertTrue(StringUtilities.equalsAcronym("[Coordinate Axis]",                      "coordinateaxis"));
        assertTrue(StringUtilities.equalsAcronym("[Coordinate Axis Name]",                 "coordinateaxisname"));
        assertTrue(StringUtilities.equalsAcronym("[Coordinate_Operation]",                 "coordoperation"));
        assertTrue(StringUtilities.equalsAcronym("[Coordinate_Operation Method]",          "coordoperationmethod"));
        assertTrue(StringUtilities.equalsAcronym("[Coordinate_Operation Parameter]",       "coordoperationparam"));
        assertTrue(StringUtilities.equalsAcronym("[Coordinate_Operation Parameter Usage]", "coordoperationparamusage"));
        assertTrue(StringUtilities.equalsAcronym("[Coordinate_Operation Parameter Value]", "coordoperationparamvalue"));
        assertTrue(StringUtilities.equalsAcronym("[Coordinate_Operation Path]",            "coordoperationpath"));
        assertTrue(StringUtilities.equalsAcronym("[Coordinate Reference System]",          "coordinatereferencesystem"));
        assertTrue(StringUtilities.equalsAcronym("[Coordinate System]",                    "coordinatesystem"));
        assertTrue(StringUtilities.equalsAcronym("[Datum]",                                "datum"));
        assertTrue(StringUtilities.equalsAcronym("[Ellipsoid]",                            "ellipsoid"));
        assertTrue(StringUtilities.equalsAcronym("[Naming System]",                        "namingsystem"));
        assertTrue(StringUtilities.equalsAcronym("[Prime Meridian]",                       "primemeridian"));
        assertTrue(StringUtilities.equalsAcronym("[Supersession]",                         "supersession"));
        assertTrue(StringUtilities.equalsAcronym("[Unit of Measure]",                      "unitofmeasure"));
        assertTrue(StringUtilities.equalsAcronym("[Version History]",                      "versionhistory"));
        assertTrue(StringUtilities.equalsAcronym("[Change]",                               "change"));
        assertTrue(StringUtilities.equalsAcronym("[Deprecation]",                          "deprecation"));
        /*
         * It is important the the following is not recognized as an acronym,
         * otherwise it leads to a confusion in DirectEpsgFactory.
         */
        assertFalse(StringUtilities.equalsAcronym("[Coordinate_Operation Method]", "coordoperation"));
    }
}
