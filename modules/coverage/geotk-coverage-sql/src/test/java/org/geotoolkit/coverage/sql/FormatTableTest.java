/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2007-2010, Geomatys
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
package org.geotoolkit.coverage.sql;

import java.util.Set;
import java.util.List;
import java.sql.SQLException;

import org.geotoolkit.test.Depend;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.internal.sql.table.CatalogTestBase;
import org.geotoolkit.internal.sql.table.CatalogException;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests {@link FormatTable}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.09
 *
 * @since 3.09 (derived from Seagis)
 */
@Depend(SampleDimensionTableTest.class)
public class FormatTableTest extends CatalogTestBase {
    /**
     * The name of the temperature format to be tested.
     */
    public static final String TEMPERATURE = "PNG Temperature [-3 … 32.25]°C";

    /**
     * The name of an other format, this one having two bands.
     */
    public static final String CURRENT = "Mars (u,v)";

    /**
     * Tests the {@link FormatTable#getEntry} and {@link FormatTable#getEntries} methods.
     *
     * @throws SQLException     If the test can't connect to the database.
     * @throws CatalogException Should never happen in normal test execution.
     */
    @Test
    public void testSelectAndList() throws CatalogException, SQLException {
        final FormatTable table = new FormatTable(getDatabase());
        final FormatEntry entry = table.getEntry(TEMPERATURE);
        assertEquals("Unexpected format read from the database.", TEMPERATURE, entry.getIdentifier());
        assertSame("Expected the cached instance.", entry, table.getEntry(TEMPERATURE));
        assertEquals("Wrong image format.", "PNG", entry.imageFormat);
        /*
         * Check the sample dimensions.
         */
        final List<GridSampleDimension> bands = entry.getSampleDimensions();
        SampleDimensionTableTest.checkTemperatureDimension(bands.toArray(new GridSampleDimension[0]));
        assertSame("Expected the cached instance.", bands, entry.getSampleDimensions());
        /*
         * Ask for every format, and ensure that our instance is in the list.
         */
        final Set<FormatEntry> entries = table.getEntries();
        assertFalse(entries.isEmpty());
        assertTrue(entries.contains(entry));
    }

    /**
     * Tests a for an entry having two bands
     *
     * @throws SQLException     If the test can't connect to the database.
     * @throws CatalogException Should never happen in normal test execution.
     */
    @Test
    public void testTwoBands() throws CatalogException, SQLException {
        final FormatTable table = new FormatTable(getDatabase());
        final FormatEntry entry = table.getEntry(CURRENT);
        assertEquals("Unexpected format read from the database.", CURRENT, entry.getIdentifier());
        assertSame("Expected the cached instance.", entry, table.getEntry(CURRENT));
        assertEquals("Wrong image format.", "NetCDF", entry.imageFormat);
        /*
         * Check the sample dimensions.
         */
        final List<GridSampleDimension> bands = entry.getSampleDimensions();
        assertEquals(2, bands.size());
        assertFalse(bands.get(0).equals(bands.get(1)));
        assertSame("Expected the cached instance.", bands, entry.getSampleDimensions());
        /*
         * Ask for every format, and ensure that our instance is in the list.
         */
        final Set<FormatEntry> entries = table.getEntries();
        assertFalse(entries.isEmpty());
        assertTrue(entries.contains(entry));
    }
}
