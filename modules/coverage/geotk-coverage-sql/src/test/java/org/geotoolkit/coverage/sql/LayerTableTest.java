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
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.SortedSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.opengis.referencing.operation.Matrix;
import org.opengis.referencing.datum.PixelInCell;

import org.geotoolkit.test.Depend;
import org.geotoolkit.util.MeasurementRange;
import org.geotoolkit.coverage.grid.GeneralGridGeometry;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.internal.sql.table.CatalogTestBase;
import org.geotoolkit.referencing.operation.transform.LinearTransform;

import org.junit.*;
import static org.junit.Assert.*;
import static java.lang.Double.NEGATIVE_INFINITY;
import static java.lang.Double.POSITIVE_INFINITY;


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
     * The name of the NetCDF layer to be tested.
     */
    public static final String NETCDF = "Coriolis (temperature)";

    /**
     * The name of a NetCDF layer with two bands to be tested.
     */
    public static final String GEOSTROPHIC_CURRENT = "Mars (u,v)";

    /**
     * The start time, end time, and a sample time between them.
     */
    public static final Date START_TIME, SUB_START_TIME, SAMPLE_TIME, SUB_END_TIME, END_TIME;
    static {
        final DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CANADA);
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            START_TIME     = format.parse("1986-01-01");
            SUB_START_TIME = format.parse("1986-01-05");
            SAMPLE_TIME    = format.parse("1986-01-13");
            SUB_END_TIME   = format.parse("1986-01-20");
            END_TIME       = format.parse("1986-02-26");
        } catch (ParseException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    /**
     * Tolerance factor for comparison of floating point numbers.
     */
    private static final double EPS = 1E-8;

    /**
     * Tests the {@link LayerTableTest#getIdentifiers()} method.
     *
     * @throws SQLException If the test can't connect to the database.
     */
    @Test
    public void testGetIdentifiers() throws SQLException {
        final LayerTable table = getDatabase().getTable(LayerTable.class);
        final Set<String> names = table.getIdentifiers();
        assertTrue(names.contains(TEMPERATURE));
        assertTrue(names.contains(NETCDF));
    }

    /**
     * Tests the {@link LayerTableTest#getEntry} and @link LayerTableTest#getEntries} methods.
     * Also tests a few methods on the {@link LayerEntry} object.
     *
     * @throws SQLException If the test can't connect to the database.
     * @throws CoverageStoreException If an error occured while querying the layer.
     */
    @Test
    public void testTemperature() throws SQLException, CoverageStoreException {
        final LayerTable table = getDatabase().getTable(LayerTable.class);
        final LayerEntry entry = table.getEntry(TEMPERATURE);
        assertEquals(TEMPERATURE, entry.getName());
        assertSame("Should be cached.", entry, table.getEntry(TEMPERATURE));
        assertEquals(START_TIME, entry.getTimeRange().getMinValue());
        assertEquals(END_TIME,   entry.getTimeRange().getMaxValue());

        final Set<LayerEntry> entries = table.getEntries();
        assertFalse(entries.isEmpty());
        assertTrue(entries.contains(entry));
        /*
         * Tests the calculation of the range of sample values.
         */
        final List<MeasurementRange<?>> validRanges = entry.getSampleValueRanges();
        assertNotNull(validRanges);
        assertEquals(1, validRanges.size());
        assertEquals(-2.85, validRanges.get(0).getMinimum(), EPS);
        assertEquals(35.25, validRanges.get(0).getMaximum(), EPS);
        /*
         * Tests the calculation of typical resolution.
         */
        final double[] resolution = entry.getTypicalResolution();
        assertEquals("X resolution (deg)",  0.087890625, resolution[0], EPS);
        assertEquals("Y resolution (deg)",  0.087890625, resolution[1], EPS);
        assertEquals("Z resolution (m)",    Double.NaN,  resolution[2], EPS);
        assertEquals("T resolution (days)", 8,           resolution[3], EPS);
        /*
         * Tests the count of entries and the count of formats
         * (the later is closely related to the former).
         */
        assertEquals("Coverage count", 7, entry.getCoverageCount());
        final SortedSet<SeriesEntry> series = entry.getCountBySeries();
        assertEquals("Expected exactly one format.", 1, series.size());
        assertEquals("Coverage format", FormatTableTest.TEMPERATURE, series.first().format.identifier);

        final SortedSet<String> names = entry.getImageFormats();
        assertEquals("Expected exactly one format.", 1, names.size());
        assertEquals("Coverage format", "PNG", names.first());
        /*
         * Tests the calculation of the grid geometry.
         */
        final SortedSet<GeneralGridGeometry> geometries = entry.getGridGeometries();
        assertEquals("Expected exactly one grid geometry.", 1, geometries.size());
        final GeneralGridGeometry geom = geometries.first();
        assertArrayEquals("GridEnvelope check (lower)", new int[3],
                geom.getGridRange().getLow().getCoordinateValues());
        assertArrayEquals("GridEnvelope check (upper)", new int[] {4095, 2047, 6},
                geom.getGridRange().getHigh().getCoordinateValues());
        final Matrix matrix = ((LinearTransform) geom.getGridToCRS(PixelInCell.CELL_CORNER)).getMatrix();
        assertEquals("X translation",   -180, matrix.getElement(0, 3), EPS);
        assertEquals("Y translation",     90, matrix.getElement(1, 3), EPS);
        assertEquals("T translation",   6431, matrix.getElement(2, 3), EPS);
        assertEquals("X scale",  0.087890625, matrix.getElement(0, 0), EPS);
        assertEquals("Y scale", -0.087890625, matrix.getElement(1, 1), EPS);
        assertEquals("T scale",            8, matrix.getElement(2, 2), EPS);
        /*
         * Tests the envelope calculation.
         */
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
     * Tests the layer for NetCDF images.
     *
     * @throws SQLException If the test can't connect to the database.
     * @throws CoverageStoreException If an error occured while querying the layer.
     */
    @Test
    public void testNetCDF() throws SQLException, CoverageStoreException {
        final LayerTable table = getDatabase().getTable(LayerTable.class);
        final Layer entry = table.getEntry(NETCDF);

        final List<MeasurementRange<?>> validRanges = entry.getSampleValueRanges();
        assertNotNull(validRanges);
        assertEquals(1, validRanges.size());
        assertEquals(-3.0, validRanges.get(0).getMinimum(true), EPS);
        assertEquals(40.0, validRanges.get(0).getMaximum(true), EPS);
    }

    /**
     * Tests the layer for NetCDF images with two bands.
     *
     * @throws SQLException If the test can't connect to the database.
     * @throws CoverageStoreException If an error occured while querying the layer.
     */
    @Test
    public void testTwoBands() throws SQLException, CoverageStoreException {
        final LayerTable table = getDatabase().getTable(LayerTable.class);
        final Layer entry = table.getEntry(GEOSTROPHIC_CURRENT);

        final List<MeasurementRange<?>> validRanges = entry.getSampleValueRanges();
        assertNotNull(validRanges);
        assertEquals(2, validRanges.size());
        assertEquals(-1.91, validRanges.get(0).getMinimum(true), EPS);
        assertEquals( 1.90, validRanges.get(0).getMaximum(true), EPS);
        assertEquals(-1.91, validRanges.get(1).getMinimum(true), EPS);
        assertEquals( 1.90, validRanges.get(1).getMaximum(true), EPS);
    }
}
