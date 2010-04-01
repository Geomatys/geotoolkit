/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010, Geomatys
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

import java.util.Date;
import java.util.Set;
import java.util.List;
import java.util.SortedSet;

import org.geotoolkit.test.Depend;
import org.geotoolkit.util.DateRange;
import org.geotoolkit.util.MeasurementRange;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.internal.sql.table.CatalogTestBase;

import org.junit.*;
import static org.junit.Assert.*;
import static org.geotoolkit.coverage.sql.LayerTableTest.*;
import static org.geotoolkit.coverage.sql.CoverageDatabase.now;


/**
 * Tests {@link CoverageDatabaseTest}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.10
 *
 * @since 3.10
 */
@Depend(GridCoverageLoaderTest.class)
public final class CoverageDatabaseTest extends CatalogTestBase {
    /**
     * Small tolerance factor for comparison of floating point numbers.
     */
    private static final double EPS = 1E-8;

    /**
     * The coverage database.
     */
    private static CoverageDatabase database;

    /**
     * Creates the database when first needed.
     */
    private static synchronized CoverageDatabase getCoverageDatabase() {
        if (database == null) {
            database = new CoverageDatabase((TableFactory) getDatabase());
        }
        return database;
    }

    /**
     * Disposes the database.
     */
    @AfterClass
    public static synchronized void dispose() {
        if (database != null) {
            database.dispose();
            database = null;
        }
    }

    /**
     * Tests {@link CoverageDatabase#getLayers()}.
     *
     * @throws CoverageStoreException If some data can not be read.
     */
    @Test
    public void testGetLayers() throws CoverageStoreException {
        final CoverageDatabase database = getCoverageDatabase();
        final Set<String> names = now(database.getLayers());
        assertTrue(names.contains(TEMPERATURE));
        assertTrue(names.contains(NETCDF));
    }

    /**
     * Tests {@link CoverageDatabase#getLayer(String)}.
     *
     * @throws CoverageStoreException If some data can not be read.
     */
    @Test
    public void testGetLayer() throws CoverageStoreException {
        final CoverageDatabase database = getCoverageDatabase();
        final Layer layer = now(database.getLayer(TEMPERATURE));
        assertEquals(TEMPERATURE, layer.getName());
    }

    /**
     * Tests {@link CoverageDatabase#getTimeRange(String)}.
     *
     * @throws CoverageStoreException If some data can not be read.
     */
    @Test
    public void testGetTimeRange() throws CoverageStoreException {
        final CoverageDatabase database = getCoverageDatabase();
        final DateRange range = now(database.getTimeRange(TEMPERATURE));
        assertEquals(START_TIME, range.getMinValue());
        assertEquals(END_TIME,   range.getMaxValue());
    }

    /**
     * Tests {@link CoverageDatabase#getAvailableTimes(String)}.
     *
     * @throws CoverageStoreException If some data can not be read.
     */
    @Test
    public void testGetAvailableTimes() throws CoverageStoreException {
        final CoverageDatabase database = getCoverageDatabase();
        final SortedSet<Date> allTimes = now(database.getAvailableTimes(TEMPERATURE));
        assertEquals(7, allTimes.size());
        assertTrue(allTimes.first().after (LayerTableTest.START_TIME));
        assertTrue(allTimes.last ().before(LayerTableTest.END_TIME));
        assertTrue(allTimes.contains(LayerTableTest.SAMPLE_TIME));
    }

    /**
     * Tests {@link CoverageDatabase#getAvailableElevations(String)}.
     *
     * @throws CoverageStoreException If some data can not be read.
     */
    @Test
    public void testGetAvailableElevations() throws CoverageStoreException {
        final CoverageDatabase database = getCoverageDatabase();
        final SortedSet<Number> z = now(database.getAvailableElevations(NETCDF));
        LayerEntryTest.checkCoriolisElevations(z);
    }

    /**
     * Tests {@link CoverageDatabase#getSampleValueRanges(String)}.
     *
     * @throws CoverageStoreException If some data can not be read.
     */
    @Test
    public void testGetSampleValueRanges() throws CoverageStoreException {
        final CoverageDatabase database = getCoverageDatabase();
        final List<MeasurementRange<?>> ranges = now(database.getSampleValueRanges(TEMPERATURE));
        assertEquals("Expected only one band.", 1, ranges.size());
        final MeasurementRange<?> range = ranges.get(0);
        assertEquals(-2.85, range.getMinimum(), EPS);
        assertEquals(35.25, range.getMaximum(), EPS);
    }

    /**
     * Tests {@link CoverageDatabase#readSlice}.
     *
     * @throws CoverageStoreException If some data can not be read.
     */
    @Test
    public void testReadSlice() throws CoverageStoreException {
        final CoverageDatabase database = getCoverageDatabase();
        final CoverageQuery query = new CoverageQuery(database);
        query.setLayer(TEMPERATURE);
        requireImageData();
        final GridCoverage2D coverage = now(database.readSlice(query));
        GridCoverageLoaderTest.checkTemperatureCoverage(coverage);
    }
}
