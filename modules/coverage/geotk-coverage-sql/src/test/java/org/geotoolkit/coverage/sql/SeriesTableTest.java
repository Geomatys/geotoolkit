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
import java.sql.SQLException;

import org.apache.sis.test.DependsOn;
import org.geotoolkit.internal.sql.table.CatalogTestBase;
import org.geotoolkit.internal.sql.table.Database;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests {@link SeriesTable}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.10
 *
 * @since 3.10 (derived from Seagis)
 */
@DependsOn({FormatTableTest.class, SeriesEntryTest.class})
public final strictfp class SeriesTableTest extends CatalogTestBase {
    /**
     * The identifier of the series to be tested.
     */
    public static final Integer TEMPERATURE_ID = 100;

    /**
     * The path to the image files of the series to be tested.
     * Those files don't need to exist for this test.
     */
    public static final String TEMPERATURE_PATH = "World/SST/8-days";

    /**
     * Creates a new test suite.
     */
    public SeriesTableTest() {
        super(SeriesTable.class);
    }

    /**
     * Tests the {@link SeriesTable#getEntry} and {@link SeriesTable#getEntries} methods.
     *
     * @throws SQLException If the test can't connect to the database.
     */
    @Test
    public void testSelectAndList() throws SQLException {
        final SeriesTable table = getDatabase().getTable(SeriesTable.class);
        final SeriesEntry entry = table.getEntry(TEMPERATURE_ID);
        assertEquals("Expected the identifier that we requested.", TEMPERATURE_ID, entry.getIdentifier());
        assertSame("The entry should be cached.", entry, table.getEntry(TEMPERATURE_ID));

        table.setLayer(LayerTableTest.TEMPERATURE);
        final Set<SeriesEntry> entries = table.getEntries();
        assertTrue("Expected a set including the test entry.", entries.contains(entry));
        assertSame("The cache should still valid.", entry, table.getEntry(TEMPERATURE_ID));
        int found = 0;
        for (final SeriesEntry e : entries) {
            if (e == entry) {
                found++;
            }
        }
        assertEquals("The set should contain the cached element.", 1, found);
        table.release();
    }

    /**
     * Tests the {@link SeriesTable#find} methods.
     *
     * @throws SQLException If the test can't connect to the database.
     */
    @Test
    public void testFind() throws SQLException {
        final SeriesTable table = getDatabase().getTable(SeriesTable.class);
        table.setLayer(LayerTableTest.TEMPERATURE);
        assertEquals("Search the existing entry.", TEMPERATURE_ID, table.find(
                TEMPERATURE_PATH, "png", FormatTableTest.TEMPERATURE));

        assertNull("Wrong path",   table.find("World/SST/4-days", "png", FormatTableTest.TEMPERATURE));
        assertNull("Wrong suffix", table.find(TEMPERATURE_PATH,   "gif", FormatTableTest.TEMPERATURE));
        assertNull("Wrong format", table.find(TEMPERATURE_PATH,   "png", "Dummy"));
        table.release();
    }

    /**
     * Tests the {@link SeriesTable#findOrCreate} methods.
     *
     * @throws SQLException If the test can't connect to the database.
     */
    @Test
    public void testFindOrCreate() throws SQLException {
        final SeriesTable table = getDatabase().getTable(SeriesTable.class);
        table.setLayer(LayerTableTest.TEMPERATURE);

        final String path = "World/SST/4-days";
        final String ext  = "png";
        final int id = table.findOrCreate(path, ext, FormatTableTest.TEMPERATURE);
        assertFalse("Should not be the existing ID.", id == TEMPERATURE_ID.intValue());
        assertEquals("Should find the existing entry.", Integer.valueOf(id),
                table.find(path, ext, FormatTableTest.TEMPERATURE));
        assertEquals("Should have deleted the entry.", 1, table.delete(id));
        table.release();
    }

    /**
     * Tests the {@link SeriesTable#deleteAll} method.
     * Note that this test implies the creation of a temporary layer.
     *
     * @throws SQLException If the test can't connect to the database.
     */
    @Test
    public void testDeleteAll() throws SQLException {
        final Database  database = getDatabase();
        final LayerTable  layers = database.getTable(LayerTable.class);
        final SeriesTable series = database.getTable(SeriesTable.class);
        final String       layer = "Dummy layer";
        series.setLayer(layer);
        assertTrue("The layer should not exist before this test.", layers.createIfAbsent(layer));
        assertNull("Should not take entry from an other layer.", series.find(TEMPERATURE_PATH, "png", FormatTableTest.TEMPERATURE));
        assertFalse(series.findOrCreate(TEMPERATURE_PATH, "png", FormatTableTest.TEMPERATURE) == TEMPERATURE_ID);
        assertEquals("Should have deleted the singleton series.", 1, series.deleteAll());
        assertEquals("Should have deleted the singleton layer.",  1, layers.delete(layer));
        series.release();
        layers.release();
    }
}
