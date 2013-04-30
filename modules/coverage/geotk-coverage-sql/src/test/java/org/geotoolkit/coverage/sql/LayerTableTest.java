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
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.opengis.geometry.Envelope;

import org.apache.sis.test.DependsOn;
import org.geotoolkit.util.MeasurementRange;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.internal.sql.table.CatalogTestBase;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests {@link LayerTable}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.16
 *
 * @since 3.10 (derived from Seagis)
 */
@DependsOn(SeriesTableTest.class)
public final strictfp class LayerTableTest extends CatalogTestBase {
    /**
     * The name of the simplest layer to be tested. This layer contains only PNG images in
     * WGS84, one image for each date. Consequently this is the simplest layer we can test.
     */
    public static final String TEMPERATURE = "SST (World - 8 days)";

    /**
     * The name of the NetCDF layer to be tested. Like {@link #TEMPERATURE}, there is
     * one different file for each date. But at the difference of {@code TEMPERATURE},
     * each files is a 3D grid which contain data at different depth. In addition, the
     * horizontal CRS is the Mercator projection.
     */
    public static final String NETCDF = "Coriolis (temperature)";

    /**
     * The name of a NetCDF layer with two bands to be tested.
     * Data are geostrophic currents, to be represented as arrow field.
     * This layer don't provide image at different depths.
     */
    public static final String GEOSTROPHIC_CURRENT = "Mars (u,v)";

    /**
     * The name of a tiled layer in WGS84 CRS, without time.
     */
    public static final String BLUEMARBLE = "BlueMarble";

    /**
     * The start time, end time, and a sample time between them.
     * They are times for the {@value #TEMPERATURE} layer.
     */
    public static final Date START_TIME, SUB_START_TIME, SAMPLE_TIME, SUB_END_TIME, END_TIME;

    /**
     * Time for a sample image from the {@value #GEOSTROPHIC_CURRENT} layer.
     * For this test, this is the sample at image index 3.
     */
    public static final Date GEOSTROPHIC_CURRENT_TIME;
    static {
        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CANADA);
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            START_TIME     = format.parse("1986-01-01");
            SUB_START_TIME = format.parse("1986-01-05");
            SAMPLE_TIME    = format.parse("1986-01-13");
            SUB_END_TIME   = format.parse("1986-01-20");
            END_TIME       = format.parse("1986-02-26");

            format.applyPattern("yyyy-MM-dd HH:mm:ss");
            GEOSTROPHIC_CURRENT_TIME = format.parse("2007-05-22 00:22:30");
        } catch (ParseException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    /**
     * Tolerance factor for comparison of floating point numbers.
     */
    static final double EPS = 1E-8;

    /**
     * Creates a new test suite.
     */
    public LayerTableTest() {
        super(LayerTable.class);
    }

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
        table.release();
    }

    /**
     * Tests the {@link LayerTableTest#getEntry} and @link LayerTableTest#getEntries} methods
     * on the simplest layer, which use WGS84 CRS. Also tests a few methods on the
     * {@link LayerEntry} object.
     *
     * @throws SQLException If the test can't connect to the database.
     * @throws CoverageStoreException If an error occurred while querying the layer.
     * @throws IOException If an error occurred while writing the legend to disk.
     */
    @Test
    public void testTemperature() throws SQLException, CoverageStoreException, IOException {
        final LayerTable table = getDatabase().getTable(LayerTable.class);
        final LayerEntry entry = table.getEntry(TEMPERATURE);
        assertEquals(TEMPERATURE, entry.getName());
        assertSame("Should be cached.", entry, table.getEntry(TEMPERATURE));
        assertEquals(START_TIME, entry.getTimeRange().getMinValue());
        assertEquals(END_TIME,   entry.getTimeRange().getMaxValue());

        image = entry.getColorRamp(0, entry.getSampleValueRanges().get(0), null);
        showCurrentImage("testTemperature");

        final Set<LayerEntry> entries = table.getEntries();
        assertFalse(entries.isEmpty());
        assertTrue(entries.contains(entry));
        table.release();
    }

    /**
     * Tests the layer for BlueMarble images, which are tiled.
     *
     * @throws SQLException If the test can't connect to the database.
     * @throws CoverageStoreException If an error occurred while querying the layer.
     */
    @Test
    public void testBluemarble() throws SQLException, CoverageStoreException {
        final LayerTable table = getDatabase().getTable(LayerTable.class);
        final Layer entry = table.getEntry(BLUEMARBLE);

        final Envelope envelope = entry.getEnvelope(null, null);
        assertEquals(-180, envelope.getMinimum(0), EPS);
        assertEquals(+180, envelope.getMaximum(0), EPS);
        assertEquals( -90, envelope.getMinimum(1), EPS);
        assertEquals( +90, envelope.getMaximum(1), EPS);

        image = entry.getColorRamp(0, null, null);
        assertNull(image);

        table.release();
    }

    /**
     * Tests the layer for NetCDF images, which use the Mercator CRS and has depths.
     *
     * @throws SQLException If the test can't connect to the database.
     * @throws CoverageStoreException If an error occurred while querying the layer.
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
        table.release();
    }

    /**
     * Tests the layer for NetCDF images with two bands.
     *
     * @throws SQLException If the test can't connect to the database.
     * @throws CoverageStoreException If an error occurred while querying the layer.
     */
    @Test
    public void testGeostrophicCurrent() throws SQLException, CoverageStoreException {
        final LayerTable table = getDatabase().getTable(LayerTable.class);
        final Layer entry = table.getEntry(GEOSTROPHIC_CURRENT);

        final List<MeasurementRange<?>> validRanges = entry.getSampleValueRanges();
        assertNotNull(validRanges);
        assertEquals(2, validRanges.size());
        assertEquals(-1.91, validRanges.get(0).getMinimum(true), EPS);
        assertEquals( 1.90, validRanges.get(0).getMaximum(true), EPS);
        assertEquals(-1.91, validRanges.get(1).getMinimum(true), EPS);
        assertEquals( 1.90, validRanges.get(1).getMaximum(true), EPS);
        table.release();
    }

    /**
     * Tests the {@link LayerTable#searchFreeIdentifier(String)} method.
     *
     * @throws SQLException If the test can't connect to the database.
     */
    @Test
    public void testSearchFreeIdentifier() throws SQLException {
        final LayerTable table = getDatabase().getTable(LayerTable.class);
        assertEquals("Non existent", table.searchFreeIdentifier("Non existent"));
        assertEquals(TEMPERATURE + "-1", table.searchFreeIdentifier(TEMPERATURE));
        table.release();
    }
}
