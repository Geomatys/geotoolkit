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

import java.util.Date;
import java.util.List;
import java.util.TreeSet;
import java.util.SortedSet;
import java.sql.SQLException;

import org.opengis.referencing.operation.Matrix;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.crs.GeographicCRS;

import org.apache.sis.test.DependsOn;
import org.apache.sis.measure.MeasurementRange;
import org.geotoolkit.coverage.grid.GeneralGridGeometry;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.internal.sql.table.CatalogTestBase;
import org.geotoolkit.referencing.operation.transform.LinearTransform;

import org.junit.*;
import static org.junit.Assert.*;
import static java.lang.Double.NEGATIVE_INFINITY;
import static java.lang.Double.POSITIVE_INFINITY;
import static org.geotoolkit.coverage.sql.LayerTableTest.*;


/**
 * Tests {@link LayerEntry}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.15
 *
 * @since 3.10 (derived from Seagis)
 */
@DependsOn(LayerTableTest.class)
public final strictfp class LayerEntryTest extends CatalogTestBase {
    /**
     * Creates a new test suite.
     */
    public LayerEntryTest() {
        super(LayerEntry.class);
    }

    /**
     * Returns the later entry used for the test.
     */
    private static LayerEntry getLayer(final String name) throws SQLException {
        final LayerTable table = getDatabase().getTable(LayerTable.class);
        final LayerEntry entry = table.getEntry(name);
        table.release();
        return entry;
    }

    /**
     * Tests the calculation of the range of sample values.
     *
     * @throws SQLException If the test can't connect to the database.
     * @throws CoverageStoreException If an error occurred while querying the layer.
     */
    @Test
    public void testSampleValueRanges() throws SQLException, CoverageStoreException {
        final LayerEntry entry = getLayer(TEMPERATURE);
        final List<MeasurementRange<?>> validRanges = entry.getSampleValueRanges();
        assertNotNull(validRanges);
        assertEquals(1, validRanges.size());
        assertEquals(-2.85, validRanges.get(0).getMinDouble(), EPS);
        assertEquals(35.25, validRanges.get(0).getMaxDouble(), EPS);
        assertSame("Shall be cached.", validRanges, entry.getSampleValueRanges());
    }

    /**
     * Tests the calculation of typical resolution.
     *
     * @throws SQLException If the test can't connect to the database.
     * @throws CoverageStoreException If an error occurred while querying the layer.
     */
    @Test
    public void testTypicalResolution() throws SQLException, CoverageStoreException {
        final LayerEntry entry = getLayer(TEMPERATURE);
        final double[] resolution = entry.getTypicalResolution();
        assertEquals("X resolution (deg)",  0.087890625, resolution[0], EPS);
        assertEquals("Y resolution (deg)",  0.087890625, resolution[1], EPS);
        assertEquals("Z resolution (m)",    Double.NaN,  resolution[2], EPS);
        assertEquals("T resolution (days)", 8,           resolution[3], EPS);
    }

    /**
     * Tests the count of entries.
     *
     * @throws SQLException If the test can't connect to the database.
     * @throws CoverageStoreException If an error occurred while querying the layer.
     */
    @Test
    public void testCoverageCount() throws SQLException, CoverageStoreException {
        final LayerEntry entry = getLayer(TEMPERATURE);
        assertEquals("Coverage count", 7, entry.getCoverageCount());
        final SortedSet<SeriesEntry> series = entry.getCountBySeries();
        assertEquals("Expected exactly one format.", 1, series.size());
        assertEquals("Coverage format", FormatTableTest.TEMPERATURE, series.first().format.identifier);
    }

    /**
     * Tests the count of formats.
     *
     * @throws SQLException If the test can't connect to the database.
     * @throws CoverageStoreException If an error occurred while querying the layer.
     */
    @Test
    public void testImageFormats() throws SQLException, CoverageStoreException {
        final LayerEntry entry = getLayer(TEMPERATURE);
        final SortedSet<String> names = entry.getImageFormats();
        assertEquals("Expected exactly one format.", 1, names.size());
        assertEquals("Coverage format", "PNG", names.first());
    }

    /**
     * Tests the calculation of the grid geometry.
     *
     * @throws SQLException If the test can't connect to the database.
     * @throws CoverageStoreException If an error occurred while querying the layer.
     */
    @Test
    public void testGridGeometries() throws SQLException, CoverageStoreException {
        final LayerEntry entry = getLayer(TEMPERATURE);
        final SortedSet<GeneralGridGeometry> geometries = entry.getGridGeometries();
        assertEquals("Expected exactly one grid geometry.", 1, geometries.size());
        final GeneralGridGeometry geom = geometries.first();
        assertArrayEquals("GridEnvelope check (lower)", new int[3],
                geom.getExtent().getLow().getCoordinateValues());
        assertArrayEquals("GridEnvelope check (upper)", new int[] {4095, 2047, 6},
                geom.getExtent().getHigh().getCoordinateValues());
        final Matrix matrix = ((LinearTransform) geom.getGridToCRS(PixelInCell.CELL_CORNER)).getMatrix();
        assertEquals("X translation",   -180, matrix.getElement(0, 3), EPS);
        assertEquals("Y translation",     90, matrix.getElement(1, 3), EPS);
        assertEquals("T translation",   6431, matrix.getElement(2, 3), EPS);
        assertEquals("X scale",  0.087890625, matrix.getElement(0, 0), EPS);
        assertEquals("Y scale", -0.087890625, matrix.getElement(1, 1), EPS);
        assertEquals("T scale",            8, matrix.getElement(2, 2), EPS);
        assertSame("Shall be cached.", geometries, entry.getGridGeometries());
    }

    /**
     * Tests the calculation of the grid geometry for a layer that doesn't contain time axis.
     * We expect a plain {@link GeographicCRS}.
     *
     * @throws SQLException If the test can't connect to the database.
     * @throws CoverageStoreException If an error occurred while querying the layer.
     *
     * @since 3.15
     */
    @Test
    public void testGridGeometriesNoTime() throws SQLException, CoverageStoreException {
        final LayerEntry entry = getLayer(BLUEMARBLE);
        final SortedSet<GeneralGridGeometry> geometries = entry.getGridGeometries();
        assertEquals("Expected exactly one grid geometry.", 1, geometries.size());
        final GeneralGridGeometry geom = geometries.first();
        assertTrue(geom.getCoordinateReferenceSystem() instanceof GeographicCRS);
        assertArrayEquals("GridEnvelope check (lower)", new int[2],
                geom.getExtent().getLow().getCoordinateValues());
        assertArrayEquals("GridEnvelope check (upper)", new int[] {2879, 1439},
                geom.getExtent().getHigh().getCoordinateValues());
        final Matrix matrix = ((LinearTransform) geom.getGridToCRS(PixelInCell.CELL_CORNER)).getMatrix();
        assertEquals("X translation", -180, matrix.getElement(0, 2), EPS);
        assertEquals("Y translation",   90, matrix.getElement(1, 2), EPS);
        assertEquals("X scale",      0.125, matrix.getElement(0, 0), EPS);
        assertEquals("Y scale",     -0.125, matrix.getElement(1, 1), EPS);
        assertSame("Shall be cached.", geometries, entry.getGridGeometries());
    }

    /**
     * Tests the envelope calculation.
     *
     * @throws SQLException If the test can't connect to the database.
     * @throws CoverageStoreException If an error occurred while querying the layer.
     */
    @Test
    public void testEnvelope() throws SQLException, CoverageStoreException {
        final LayerEntry entry = getLayer(TEMPERATURE);
        final CoverageEnvelope envelope = entry.getEnvelope(SAMPLE_TIME, null);
        assertEquals(-180,              envelope.getMinimum(0), 0.0);
        assertEquals(+180,              envelope.getMaximum(0), 0.0);
        assertEquals( -90,              envelope.getMinimum(1), 0.0);
        assertEquals( +90,              envelope.getMaximum(1), 0.0);
        assertEquals(NEGATIVE_INFINITY, envelope.getMinimum(2), 0.0);
        assertEquals(POSITIVE_INFINITY, envelope.getMaximum(2), 0.0);
        assertEquals(6439,              envelope.getMinimum(3), 0.0);
        assertEquals(6447,              envelope.getMaximum(3), 0.0);
    }

    /**
     * Tests the date of available data.
     *
     * @throws SQLException If the test can't connect to the database.
     * @throws CoverageStoreException If an error occurred while querying the layer.
     */
    @Test
    public void testAvailableTimes() throws SQLException, CoverageStoreException {
        final LayerEntry entry = getLayer(TEMPERATURE);
        final SortedSet<Date> times = entry.getAvailableTimes();
        assertTrue(times.contains(SAMPLE_TIME));
        assertSame(times, times.subSet(START_TIME, END_TIME));
        assertSame("Shall be cached.", times, entry.getAvailableTimes());

        final SortedSet<Date> sub = times.subSet(SUB_START_TIME, SUB_END_TIME);
        assertEquals(7, times.size());
        assertEquals(2, sub.size());
        assertTrue(times.containsAll(sub));
        assertFalse(sub.containsAll(times));

        assertEquals("Testing UnmodifiableArraySortedSet using TreeSet as a reference",
                sub, new TreeSet<>(times).subSet(SUB_START_TIME, SUB_END_TIME));
    }

    /**
     * Tests the altitude of available data.
     *
     * @throws SQLException If the test can't connect to the database.
     * @throws CoverageStoreException If an error occurred while querying the layer.
     */
    @Test
    public void testAvailableElevations() throws SQLException, CoverageStoreException {
        LayerEntry entry = getLayer(TEMPERATURE);
        assertTrue(entry.getAvailableElevations().isEmpty());

        entry = getLayer(NETCDF);
        final SortedSet<Number> elevations = entry.getAvailableElevations();
        GridGeometryTableTest.checkCoriolisElevations(elevations);
        assertSame("Shall be cached.", elevations, entry.getAvailableElevations());
    }
}
