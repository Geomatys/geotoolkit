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
import org.geotoolkit.util.MeasurementRange;
import org.geotoolkit.internal.sql.table.CatalogTestBase;
import org.geotoolkit.internal.sql.table.CatalogException;

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
@Depend(SeriesTableTest.class)
public final class LayerTableTest extends CatalogTestBase {
    /**
     * The name of the layer to be tested.
     */
    public static final String TEMPERATURE = "SST (World - 8 days)";

    /**
     * Tests the {@link LayerTableTest#getEntry} and @link LayerTableTest#getEntries} methods.
     * Also tests a few methods on the {@link LayerEntry} object.
     *
     * @throws SQLException     If the test can't connect to the database.
     * @throws CatalogException Should never happen in normal test execution.
     */
    @Test
    public void testSelectAndList() throws CatalogException, SQLException {
        final LayerTable table = new LayerTable(getDatabase());
        final LayerEntry entry = table.getEntry(TEMPERATURE);
        assertEquals(TEMPERATURE, entry.getName());
        assertSame("Should be cached.", entry, table.getEntry(TEMPERATURE));

        final Set<LayerEntry> entries = table.getEntries();
        assertFalse(entries.isEmpty());
        assertTrue(entries.contains(entry));

        final List<MeasurementRange<?>> validRanges = entry.getSampleValueRanges();
        assertNotNull(validRanges);
        assertEquals(1, validRanges.size());
        assertEquals(-2.85, validRanges.get(0).getMinimum(), 1E-8);
        assertEquals(35.25, validRanges.get(0).getMaximum(), 1E-8);
    }
}
