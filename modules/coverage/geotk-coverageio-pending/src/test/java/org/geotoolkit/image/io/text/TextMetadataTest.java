/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.image.io.text;

import java.io.IOException;

import java.io.PrintWriter;
import java.net.URL;
import org.geotoolkit.coverage.io.AmbiguousMetadataException;
import org.geotoolkit.image.io.metadata.GeographicMetadata;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests the {@link DefaultTextMetadataParser} class.
 *
 * @module pending
 * @since 2.5
 * @version $Id$
 * @author Cédric Briançon
 */
public class TextMetadataTest {
    /**
     * The input resource. It should be an xml file which contains metadata declaration.
     */
    private final URL in = getClass().getResource("metadata.txt");

    /**
     * Set to a non-null value for printing some diagnostic message to the standard output.
     */
    private static PrintWriter out;

    /**
     * Tests the addition of alias.
     *
     * @throws IOException If an I/O operation was required and failed.
     */
    @Test
    @Ignore
    public void testAlias() throws IOException {
        final DefaultTextMetadataParser parser = new DefaultTextMetadataParser();
        parser.setGeographicMetadata(new GeographicMetadata());
        /*
         * Tests "add" operations.
         */
        parser.add("Alias 1", "Value 1");
        parser.add("Alias 2", "Value 2");
        try {
            parser.add("  alias  1", "Value X");
            fail(); // We should not get there.
        } catch (AmbiguousMetadataException exception) {
            // This is the expected exception.
            if (out != null) {
                out.println(exception);
            }
        }
        parser.add("Alias 1", "Value 1"); // Already defined
        parser.add("Alias 3", "Value 3");
        /*
         * Tests "addAlias" operations.
         */
        parser.addAlias(TextMetadataParser.X_RESOLUTION, "Alias 1");
        parser.addAlias(TextMetadataParser.Y_RESOLUTION, "Alias 2");
        parser.addAlias(TextMetadataParser.Y_RESOLUTION, "Alias 2bis");
        parser.addAlias(TextMetadataParser.X_RESOLUTION, "Alias 1bis");
        parser.addAlias(TextMetadataParser.X_RESOLUTION, "Alias 1"); // Already defined
        try {
            parser.addAlias(TextMetadataParser.X_RESOLUTION, "Alias 2");
            fail(); // We should not get there.
        } catch (AmbiguousMetadataException exception) {
            // This is the expected exception.
            if (out != null) {
                out.println(exception);
            }
        }
        parser.add("Alias 2bis", "Value 2");
        try {
            parser.add("Alias 1bis", "Value 2");
            fail(); // We should not get there.
        } catch (AmbiguousMetadataException exception) {
            // This is the expected exception.
            if (out != null) {
                out.println(exception);
            }
        }
    }

    /**
     * Read metadata information from a txt file.
     */
    @Test
    public void testMetadata() throws IOException {
        final DefaultTextMetadataParser parser = new DefaultTextMetadataParser();
        parser.setGeographicMetadata(new GeographicMetadata());
        assertNotNull(in);
        parser.addAlias(TextMetadataParser.X_MINIMUM,          "XMinimum");
        parser.addAlias(TextMetadataParser.X_MAXIMUM,          "XMaximum");
        parser.addAlias(TextMetadataParser.Y_MINIMUM,          "YMinimum");
        parser.addAlias(TextMetadataParser.Y_MAXIMUM,          "YMaximum");
        parser.addAlias(TextMetadataParser.Z_MINIMUM,          "ZMinimum");
        parser.addAlias(TextMetadataParser.Z_MAXIMUM,          "ZMaximum");
        parser.addAlias(TextMetadataParser.X_RESOLUTION,       "XResolution");
        parser.addAlias(TextMetadataParser.Y_RESOLUTION,       "YResolution");
        parser.addAlias(TextMetadataParser.Z_RESOLUTION,       "ZResolution");
        parser.addAlias(TextMetadataParser.UNIT,               "Unit");
        parser.addAlias(TextMetadataParser.PROJECTION,         "Projection");
        parser.addAlias(TextMetadataParser.CENTRAL_MERIDIAN,   "Central meridian");
        parser.addAlias(TextMetadataParser.LATITUDE_OF_ORIGIN, "Latitude of origin");
        parser.addAlias(TextMetadataParser.FALSE_EASTING,      "False easting");
        parser.addAlias(TextMetadataParser.FALSE_NORTHING,     "False northing");
        parser.addAlias(TextMetadataParser.ELLIPSOID,          "Ellipsoid");
        parser.addAlias(TextMetadataParser.DATUM,              "Datum");
        parser.addAlias(TextMetadataParser.WIDTH,              "Width");
        parser.addAlias(TextMetadataParser.HEIGHT,             "Height");
        parser.addAlias(TextMetadataParser.DEPTH,              "Depth");
        parser.load(in);
        assertNotNull(parser);
        /*assertTrue(text.toString().contains("Ellipsoid          = Clarke 1866"));
        assertEquals(text.getAsDouble(TextMetadataParser.X_MINIMUM), 217904.31, 1E-6);*/
    }
}
