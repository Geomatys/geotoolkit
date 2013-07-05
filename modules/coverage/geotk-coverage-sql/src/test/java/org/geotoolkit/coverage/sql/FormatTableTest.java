/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2007-2012, Geomatys
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
import java.util.Arrays;
import java.sql.SQLException;
import javax.measure.unit.SI;

import org.apache.sis.test.DependsOn;
import org.geotoolkit.coverage.Category;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.internal.sql.table.CatalogTestBase;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests {@link FormatTable}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.13
 *
 * @since 3.09 (derived from Seagis)
 */
@DependsOn(SampleDimensionTableTest.class)
public final strictfp class FormatTableTest extends CatalogTestBase {
    /**
     * The name of the temperature format to be tested.
     */
    public static final String TEMPERATURE = "PNG Temperature [-3 … 32.25]°C";

    /**
     * The name of an other format, this one having two bands.
     */
    public static final String CURRENT = "Mars (u,v)";

    /**
     * Creates a new test suite.
     */
    public FormatTableTest() {
        super(FormatTable.class);
    }

    /**
     * Tests the list of identifiers.
     *
     * @throws SQLException If the test can't connect to the database.
     */
    @Test
    public void testListID() throws SQLException {
        final FormatTable table = getDatabase().getTable(FormatTable.class);
        final Set<String> identifiers = table.getIdentifiers();
        assertTrue(identifiers.contains(TEMPERATURE));
        assertTrue(identifiers.contains(CURRENT));
        assertTrue(identifiers.contains("TIFF"));
        assertTrue(identifiers.contains("PNG"));
        table.release();
    }

    /**
     * Tests the {@link FormatTable#getEntry} and {@link FormatTable#getEntries} methods.
     *
     * @throws SQLException If the test can't connect to the database.
     */
    @Test
    public void testSelectAndList() throws SQLException {
        final FormatTable table = getDatabase().getTable(FormatTable.class);
        final FormatEntry entry = table.getEntry(TEMPERATURE);
        assertEquals("Unexpected format read from the database.", TEMPERATURE, entry.identifier);
        assertSame("Expected the cached instance.", entry, table.getEntry(TEMPERATURE));
        assertEquals("Wrong image format.", "PNG", entry.imageFormat);
        assertEquals("Wrong color palette.", "rainbow", entry.paletteName);
        /*
         * Check the sample dimensions.
         */
        final List<GridSampleDimension> bands = entry.sampleDimensions;
        SampleDimensionTableTest.checkTemperatureDimension(bands.toArray(new GridSampleDimension[0]));
        /*
         * Ask for every format, and ensure that our instance is in the list.
         */
        table.setImageFormats(entry.getImageFormats());
        final Set<FormatEntry> entries = table.getEntries();
        assertFalse(entries.isEmpty());
        assertTrue(entries.contains(entry));
        table.release();
    }

    /**
     * Tests a for an entry having two bands
     *
     * @throws SQLException If the test can't connect to the database.
     */
    @Test
    public void testTwoBands() throws SQLException {
        final FormatTable table = getDatabase().getTable(FormatTable.class);
        final FormatEntry entry = table.getEntry(CURRENT);
        assertEquals("Unexpected format read from the database.", CURRENT, entry.identifier);
        assertSame("Expected the cached instance.", entry, table.getEntry(CURRENT));
        assertEquals("Wrong image format.", "NetCDF", entry.imageFormat);
        assertEquals("Wrong color palette.", "white-cyan-red", entry.paletteName);
        /*
         * Check the sample dimensions.
         */
        final List<GridSampleDimension> bands = entry.sampleDimensions;
        assertEquals(2, bands.size());
        assertFalse(bands.get(0).equals(bands.get(1)));
        /*
         * Ask for every format, and ensure that our instance is in the list.
         */
        table.setImageFormats(entry.getImageFormats());
        final Set<FormatEntry> entries = table.getEntries();
        assertFalse(entries.isEmpty());
        assertTrue(entries.contains(entry));
        table.release();
    }

    /**
     * Tests the {@lik FormatTable#exists} method.
     *
     * @throws SQLException If the test can't connect to the database.
     */
    @Test
    public void testExists() throws SQLException {
        final FormatTable table = getDatabase().getTable(FormatTable.class);
        assertTrue ("PNG",   table.exists("PNG"));
        assertFalse("Dummy", table.exists("Dummy"));
        table.release();
    }

    /**
     * Tests the {@lik FormatTable#find} method.
     *
     * @throws SQLException If the test can't connect to the database.
     */
    @Test
    public void testFind() throws SQLException {
        final Category[] categories = {
            new Category("No data",     null, 0),
            new Category("Temperature", null, 1, 256, 0.15, -3)
        };
        final FormatTable table = getDatabase().getTable(FormatTable.class);
        /*
         * Following entry should be found for the PNG format only.
         */
        GridSampleDimension search = new GridSampleDimension("Temperature", categories, SI.CELSIUS);
        FormatEntry found = table.find("NetCDF", Arrays.asList(search));
        assertNull("Should be defined for the PNG format, not NetCDF.", found);
        found = table.find("PNG", Arrays.asList(search));
        assertNotNull("Should be defined for the PNG format.", found);
        assertEquals(TEMPERATURE, found.getIdentifier());
        /*
         * Replace the category by a different one.
         * The entry should not be found anymore.
         */
        categories[1] = new Category("Temperature", null, 1, 256, 0.15, -4);
        search = new GridSampleDimension("Temperature", categories, SI.CELSIUS);
        found = table.find("PNG", Arrays.asList(search));
        assertNull("Should not found because the transfer function is different.", found);

        categories[1] = new Category("Temperature", null, 1, 255, 0.15, -3);
        search = new GridSampleDimension("Temperature", categories, SI.CELSIUS);
        found = table.find("PNG", Arrays.asList(search));
        assertNull("Should not found because the range is different.", found);

        categories[1] = new Category("Temperature", null, 1, 256, 0.15, -3);
        search = new GridSampleDimension("Temperature", categories, SI.CELSIUS);
        found = table.find("PNG", Arrays.asList(search));
        assertNotNull("Should found since the category has been restored.", found);
        assertEquals(TEMPERATURE, found.getIdentifier());
        table.release();
    }

    /**
     * Tests the {@link FormatTable#findOrCreate} method.
     *
     * @throws SQLException If the test can't connect to the database.
     */
    @Test
    public void testFindOrCreate() throws SQLException {
        final String formatName = "New format test";
        final List<GridSampleDimension> bands = Arrays.asList(
            new GridSampleDimension("Temperature",
                new Category[] {
                    new Category("No data",     null, 0),
                    new Category("Clouds",      null, 3),
                    new Category("Land",        null, 7),
                    new Category("Temperature", null, 10, 255, 0.1, 5)
                }, SI.CELSIUS),
            new GridSampleDimension("Quality",
                new Category[] {
                    new Category("No data", null, 0),
                    new Category("Good",    null, 1),
                    new Category("Bad",     null, 2)
                }, null));

        final FormatTable table = getDatabase().getTable(FormatTable.class);
        assertEquals(formatName, table.findOrCreate(formatName, "PNG", bands));
        assertEquals(1, table.delete(formatName));
        table.release();
    }
}
