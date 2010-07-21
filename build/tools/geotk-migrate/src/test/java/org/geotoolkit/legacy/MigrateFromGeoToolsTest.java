/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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
package org.geotoolkit.legacy;

import java.io.*;
import java.util.*;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests the {@link UpgradeFromGeotoolkit2} class.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 */
public final class MigrateFromGeoToolsTest {
    /**
     * Tests the migrate of the GT2 test file.
     *
     * @throws IOException If an error occurred while reading the test files.
     */
    @Test
    public void testUpgradeGT2() throws IOException {
        final Map<String,String> imports = new LinkedHashMap<String,String>();
        final MigrateFromGeoTools upgrader = new MigrateFromGeoTools(true);
        final BufferedReader gt2 = getResources("GT2.java");
        final BufferedReader gt3 = getResources("GT3.java");
        String line2, line3;
        while ((line2 = gt2.readLine()) != null) {
            final String got = upgrader.migrate(line2, imports);
            if (got != null) {
                assertNotNull(line3 = gt3.readLine());
                assertEquals(line3, got);
            }
        }
        assertNull(line3 = gt3.readLine());
        gt3.close();
        gt2.close();
    }

    /**
     * Returns the given resources as a reader.
     *
     * @param filename The name of the resource to load.
     * @return The given resources as a reader.
     * @throws IOException If an error occurred while fetching the resources.
     */
    private static BufferedReader getResources(final String filename) throws IOException {
        final InputStream stream = MigrateFromGeoToolsTest.class.getResourceAsStream(filename);
        assertNotNull(filename, stream);
        return new BufferedReader(new InputStreamReader(stream, "UTF-8"));
    }
}

