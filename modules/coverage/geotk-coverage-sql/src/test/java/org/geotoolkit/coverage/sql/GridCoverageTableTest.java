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
import java.util.SortedSet;

import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.crs.ProjectedCRS;
import org.opengis.referencing.operation.TransformException;
import org.opengis.metadata.extent.GeographicBoundingBox;

import org.geotoolkit.test.Depend;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.internal.sql.table.CatalogTestBase;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;

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
     * Small tolerance factor for comparison of floating point numbers.
     */
    private static final double EPS = 1E-8;

    /**
     * Tests the {@link GridCoverageTable#getAvailableTimes()} method.
     *
     * @throws SQLException If the test can't connect to the database.
     */
    @Test
    public void testAvailability() throws SQLException {
        final GridCoverageTable table = getDatabase().getTable(GridCoverageTable.class);
        table.setLayer(LayerTableTest.TEMPERATURE);
        final SortedSet<Date> allTimes = table.getAvailableTimes();
        assertEquals(7, allTimes.size());
        assertTrue(allTimes.first().after (LayerTableTest.START_TIME));
        assertTrue(allTimes.last ().before(LayerTableTest.END_TIME));
        assertTrue(allTimes.contains(LayerTableTest.SAMPLE_TIME));
        /*
         * Reduce the time range and tests again available times.
         */
        table.envelope.setTimeRange(LayerTableTest.SUB_START_TIME, LayerTableTest.SUB_END_TIME);
        final Set<Date> availableTimes = table.getAvailableTimes();
        assertEquals(3, availableTimes.size());
        assertTrue(allTimes.containsAll(availableTimes));
        assertFalse(availableTimes.containsAll(allTimes));
        /*
         * Reset the full range and test again.
         */
        table.envelope.setTimeRange(LayerTableTest.START_TIME, LayerTableTest.END_TIME);
        assertEquals(allTimes, table.getAvailableTimes());
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
        table.setLayer(LayerTableTest.TEMPERATURE);
        /*
         * Get the set of entries in the layer.
         */
        table.envelope.setTimeRange(LayerTableTest.SUB_START_TIME, LayerTableTest.SUB_END_TIME);
        final Set<GridCoverageEntry> entries = table.getEntries();
        assertEquals(3, entries.size());
        final GridCoverageReference entry = table.getEntry(SAMPLE_NAME);
        assertTrue(entries.contains(entry));
        /*
         * Test the selection of an "appropriate" entry.
         * Should select the one which is in the middle of the requested range.
         */
        assertSame(entry, table.getEntry());
    }

    /**
     * Tests the table for NetCDF images. They use a Mercator projection.
     *
     * @throws SQLException If the test can't connect to the database.
     */
    @Test
    public void testNetCDF() throws SQLException {
        final GridCoverageTable table = getDatabase().getTable(GridCoverageTable.class);
        table.setLayer(LayerTableTest.NETCDF);
        final Set<Date> availableTimes = table.getAvailableTimes();
        assertEquals(3, availableTimes.size());
        /*
         * Tests a single entry.
         */
        final GridCoverageReference entry = table.getEntry();
        assertEquals(1, entry.getSampleDimensions().length);
        /*
         * Tests the envelope, which may be projected.
         */
        final Envelope envelope = entry.getEnvelope();
        assertTrue(getHorizontalCRS(envelope.getCoordinateReferenceSystem()) instanceof ProjectedCRS);
        assertEquals(-2.00375E7, envelope.getMinimum(0), 100.0);
        assertEquals( 2.00375E7, envelope.getMaximum(0), 100.0);
        assertEquals(-1.38176E7, envelope.getMinimum(1), 100.0);
        assertEquals( 1.38176E7, envelope.getMaximum(1), 100.0);
        /*
         * Tests the geographic envelope, which must be geographic.
         */
        final GeographicBoundingBox bbox = entry.getGeographicBoundingBox();
        assertEquals(-180, bbox.getWestBoundLongitude(), EPS);
        assertEquals(+180, bbox.getEastBoundLongitude(), EPS);
        assertEquals( -77, bbox.getSouthBoundLatitude(), 0.1);
        assertEquals( +77, bbox.getNorthBoundLatitude(), 0.1);
    }

    /**
     * Tests the table for a NetCDF image with two bands.
     *
     * @throws SQLException If the test can't connect to the database.
     */
    @Test
    public void testTwoBands() throws SQLException {
        final GridCoverageTable table = getDatabase().getTable(GridCoverageTable.class);
        table.setLayer(LayerTableTest.GEOSTROPHIC_CURRENT);
        final GridCoverageReference entry = table.getEntry();
        assertEquals(2, entry.getSampleDimensions().length);
    }

    /**
     * Tests the request for the bounding box.
     *
     * @throws SQLException If the test can't connect to the database.
     * @throws TransformException Should not happen.
     */
    @Test
    public void testBoundingBox() throws SQLException, TransformException {
        final GridCoverageTable table = getDatabase().getTable(GridCoverageTable.class);
        table.setLayer(LayerTableTest.TEMPERATURE);
        GeneralEnvelope search = new GeneralEnvelope(table.envelope.getCoordinateReferenceSystem());
        search.setToInfinite();
        search.setRange(0, -200, 200);
        search.setRange(1, -100, 100);
        table.envelope.setEnvelope(search);

        Envelope envelope = table.envelope;
        assertEquals(-200, envelope.getMinimum(0), 0.0);
        assertEquals(+200, envelope.getMaximum(0), 0.0);
        assertEquals(-100, envelope.getMinimum(1), 0.0);
        assertEquals(+100, envelope.getMaximum(1), 0.0);

        table.trimEnvelope();
        envelope = table.envelope;
        assertEquals(-180, envelope.getMinimum(0), 0.0);
        assertEquals(+180, envelope.getMaximum(0), 0.0);
        assertEquals( -90, envelope.getMinimum(1), 0.0);
        assertEquals( +90, envelope.getMaximum(1), 0.0);
        assertEquals(6431, envelope.getMinimum(3), 0.0);
        assertEquals(6487, envelope.getMaximum(3), 0.0);

        /*
         * Following test intentionally define an two-dimensional envelope instead than the
         * expected three-dimensional spatio-temporal envelope. The time ordinates should be
         * left unchanged.
         */
        search = new GeneralEnvelope(DefaultGeographicCRS.WGS84);
        search.setRange(0, -100, 120);
        search.setRange(1,  -80,  60);
        table.envelope.setEnvelope(search);

        table.trimEnvelope();
        envelope = table.envelope;
        assertEquals(-100, envelope.getMinimum(0), 0.0);
        assertEquals(+120, envelope.getMaximum(0), 0.0);
        assertEquals( -80, envelope.getMinimum(1), 0.0);
        assertEquals( +60, envelope.getMaximum(1), 0.0);
        assertEquals(6431, envelope.getMinimum(3), 0.0);
        assertEquals(6487, envelope.getMaximum(3), 0.0);
    }
}
