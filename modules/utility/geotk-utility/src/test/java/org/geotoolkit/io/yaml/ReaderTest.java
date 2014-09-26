/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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
package org.geotoolkit.io.yaml;

import java.text.ParseException;
import org.opengis.metadata.Metadata;
import org.apache.sis.util.ComparisonMode;
import org.junit.Test;

import static org.junit.Assert.*;


/**
 * Tests {@link Reader}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @module
 */
public final strictfp class ReaderTest {
    /**
     * Convenience method for testing {@link Reader#unescape(CharSequence, int, int)}.
     */
    private static String unescape(final String text) throws ParseException {
        return Reader.unescape(text, 0, text.length()).toString();
    }

    /**
     * Tests {@link Reader#unescape(CharSequence, int, int)}.
     *
     * @throws ParseException if a parsing error occurred.
     */
    @Test
    public void testUnescape() throws ParseException {
        assertEquals("Nothing to escape.",     unescape("Nothing to escape."));
        assertEquals("Bell (\b) and tab (\t)", unescape("Bell (\\b) and tab (\\t)"));
        assertEquals("Ignored escape: i",      unescape("Ignored escape: \\i"));
        assertEquals("Unicode A and B",        unescape("Unicode \\u0041 and \\u042"));
        assertEquals("Backslash (\\)",         unescape("Backslash (\\\\)"));
    }

    /**
     * Tests parsing of a metadata object.
     *
     * @throws ParseException If the parsing failed.
     */
    @Test
    public void testParse() throws ParseException {
        final Reader reader = new Reader(WriterTest.JSON);
        final Metadata md = (Metadata) reader.parse(Metadata.class);
        assertTrue(WriterTest.createMetadata().equals(md, ComparisonMode.DEBUG));
    }
}
