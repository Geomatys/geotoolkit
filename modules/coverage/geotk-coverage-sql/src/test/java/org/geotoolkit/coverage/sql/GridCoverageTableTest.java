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

import java.sql.SQLException;
import java.util.Date;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;

import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.GeographicCRS;

import org.geotoolkit.test.Depend;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.internal.sql.table.CatalogTestBase;

import org.junit.*;
import static org.junit.Assert.*;
import static org.geotoolkit.referencing.CRS.getHorizontalCRS;


/**
 * Tests {@link GridCoverageTable}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.10
 *
 * @since 3.10 (derived from Seagis)
 */
@Depend(LayerTableTest.class)
public final class GridCoverageTableTest extends CatalogTestBase {
    /**
     * The name of the coverage to be tested.
     */
    public static final String SAMPLE_NAME = "198602";

    /**
     * Tests the {@link GridCoverageTable#getAvailableCentroids()} and derived methods
     * (e.g. {@link GridCoverageTable#getAvailableTimes()}).
     *
     * @throws SQLException If the test can't connect to the database.
     */
    @Test
    public void testAvailability() throws SQLException {
        final GridCoverageTable table = getDatabase().getTable(GridCoverageTable.class);
        table.reset();
        table.setLayer(LayerTableTest.TEMPERATURE);
        final SortedSet<Date> allTimes = table.getAvailableTimes();
        assertEquals(7, allTimes.size());
        assertTrue(allTimes.first().after (LayerTableTest.START_TIME));
        assertTrue(allTimes.last ().before(LayerTableTest.END_TIME));
        assertTrue(allTimes.contains(LayerTableTest.SAMPLE_TIME));
        /*
         * Test available centroids. This is related to the test of available
         * times, since the times were derived from the centroids.
         */
        final SortedMap<Date, SortedSet<Number>> centroids = table.getAvailableCentroids();
        assertEquals("Evailable times should be the key set.", allTimes, centroids.keySet());
        final Set<Number> depths = centroids.get(LayerTableTest.SAMPLE_TIME);
        assertNotNull("Expected an entry at the requested time.", depths);
        assertTrue("Expected no elevation.", depths.isEmpty());
        /*
         * Tests the available elevations.
         */
        final Set<Number> elevations = table.getAvailableElevations();
        assertTrue(elevations.isEmpty());
        /*
         * Reduce the time range and tests again available times.
         */
        table.setTimeRange(LayerTableTest.SUB_START_TIME, LayerTableTest.SUB_END_TIME);
        final Set<Date> availableTimes = table.getAvailableTimes();
        assertEquals(3, availableTimes.size());
        assertTrue(allTimes.containsAll(availableTimes));
        assertFalse(availableTimes.containsAll(allTimes));
        /*
         * Reset the full range and test again.
         */
        table.setTimeRange(LayerTableTest.START_TIME, LayerTableTest.END_TIME);
        assertEquals(allTimes, table.getAvailableTimes());
        table.release();
    }

    /**
     * Tests the {@link GridCoverageTable#getEntry(Comparable)} method. This method is actually
     * not used in typical {@link GridCoverageTable} usage, but we test it because it is the
     * most direct way to test the creation of a particular {@link GridCoverageEntry}.
     *
     * @throws SQLException If the test can't connect to the database.
     */
    @Test
    public void testSelect() throws SQLException {
        final GridCoverageTable table = getDatabase().getTable(GridCoverageTable.class);
        table.reset();
        table.setLayer(LayerTableTest.TEMPERATURE);
        final GridCoverageReference entry = table.getEntry(SAMPLE_NAME);
        assertEquals(SAMPLE_NAME + ":1", entry.getName());
        assertSame(entry, table.getEntry(SAMPLE_NAME));
        /*
         * Test the sample dimension.
         */
        final GridSampleDimension[] bands = entry.getSampleDimensions();
        assertEquals(1, bands.length);
        final GridSampleDimension band = bands[0];
        assertSame("Should be geophysics.", band, band.geophysics(true));
        SampleDimensionTableTest.checkTemperatureDimension(band.geophysics(false));
        /*
         * Test the envelope.
         */
        final Envelope envelope = entry.getEnvelope();
        assertTrue(getHorizontalCRS(envelope.getCoordinateReferenceSystem()) instanceof GeographicCRS);
        assertEquals(-180, envelope.getMinimum(0), 0.0);
        assertEquals(+180, envelope.getMaximum(0), 0.0);
        assertEquals( -90, envelope.getMinimum(1), 0.0);
        assertEquals( +90, envelope.getMaximum(1), 0.0);
        assertEquals(6439, envelope.getMinimum(2), 0.0);
        assertEquals(6447, envelope.getMaximum(2), 0.0);
        table.release();
    }

    /**
     * Tests the {@link GridCoverageTable#getEntries()} and {@link GridCoverageTable#getEntry()}
     * methods.
     *
     * @throws SQLException If the test can't connect to the database.
     */
    @Test
    public void testList() throws SQLException {
        final GridCoverageTable table = getDatabase().getTable(GridCoverageTable.class);
        table.reset();
        table.setLayer(LayerTableTest.TEMPERATURE);
        /*
         * Get the set of entries in the layer.
         */
        table.setTimeRange(LayerTableTest.SUB_START_TIME, LayerTableTest.SUB_END_TIME);
        final Set<GridCoverageEntry> entries = table.getEntries();
        assertEquals(3, entries.size());
        final GridCoverageReference entry = table.getEntry(SAMPLE_NAME);
        assertTrue(entries.contains(entry));
        /*
         * Test the selection of an "appropriate" entry.
         * Should select the one which is in the middle of the requested range.
         */
        assertSame(entry, table.getEntry());
        table.release();
    }
}
