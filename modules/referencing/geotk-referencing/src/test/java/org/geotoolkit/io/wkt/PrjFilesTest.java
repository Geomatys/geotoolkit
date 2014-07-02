/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Open Source Geospatial Foundation (OSGeo)
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

import java.io.*;
import org.junit.*;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import static org.geotoolkit.referencing.Assert.*;
import static org.geotoolkit.referencing.crs.DefaultGeographicCRS.WGS84;


/**
 * Tests the {@link PrjFiles} class.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.15
 *
 * @since 3.05
 */
public final strictfp class PrjFilesTest {
    /**
     * The {@code WGS84} CRS as WKT on a single line.
     */
    private static final String WKT = "GEOGCS[\"WGS84(DD)\", " +
            "DATUM[\"World Geodetic System 1984\", SPHEROID[\"WGS 84\", 6378137.0, 298.257223563]], " +
            "PRIMEM[\"Greenwich\", 0.0], UNIT[\"degree\", 0.017453292519943295], " +
            "AXIS[\"Longitude\", EAST], AXIS[\"Latitude\", NORTH]]\n";

    /**
     * Tests the read operation.
     *
     * @throws IOException should not happen.
     */
    @Test
    public void testRead() throws IOException {
        final StringReader in = new StringReader(WKT);
        final CoordinateReferenceSystem crs = PrjFiles.read(new BufferedReader(in), true);
        assertEqualsIgnoreMetadata(WGS84, crs, false);
    }

    /**
     * Tests the write operation.
     *
     * @throws IOException should not happen.
     */
    @Test
    public void testWrite() throws IOException {
        final StringWriter out = new StringWriter();
        PrjFiles.write(WGS84, out);
        out.close();
        assertEquals(WKT, out.toString());
    }
}
