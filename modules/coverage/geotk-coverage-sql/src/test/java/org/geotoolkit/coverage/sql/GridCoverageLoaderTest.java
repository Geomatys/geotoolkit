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

import java.io.IOException;
import java.sql.SQLException;
import java.awt.image.RenderedImage;

import org.opengis.referencing.crs.ProjectedCRS;

import org.geotoolkit.test.Depend;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.internal.sql.table.CatalogTestBase;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests {@link GridCoverageLoader}. This is actually tested indirectly, through calls
 * to {@link GridCoverageEntry} methods.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.10
 *
 * @since 3.10 (derived from Seagis)
 */
@Depend(GridCoverageTableTest.class)
public final class GridCoverageLoaderTest extends CatalogTestBase {
    /**
     * Tests loading an image of temperature data in WGS84 CRS.
     *
     * @throws SQLException If the test can't connect to the database.
     * @throws IOException If the image can not be read.
     * @throws CoverageStoreException If a logical error occured.
     */
    @Test
    public void testTemperature() throws SQLException, IOException, CoverageStoreException {
        final GridCoverageTable table = getDatabase().getTable(GridCoverageTable.class);
        table.envelope.clear();
        table.envelope.setTimeRange(LayerTableTest.SUB_START_TIME, LayerTableTest.SUB_END_TIME);
        table.setLayer(LayerTableTest.TEMPERATURE);
        final GridCoverageReference entry = table.getEntry();

        requireImageData();
        final GridCoverage2D coverage = entry.getCoverage(null);
        assertSame("Coverage shall be cached.", coverage, entry.getCoverage(null));
        checkTemperatureCoverage(coverage);

        final RenderedImage image = coverage.getRenderedImage();
        assertEquals(4096, image.getWidth());
        assertEquals(2048, image.getHeight());
        table.release();
    }

    /**
     * Checks the {@code GridCoverage2D} instance for the temperature sample data.
     * Doesn't check the image size, since it depends on the requested envelope.
     */
    static void checkTemperatureCoverage(final GridCoverage2D coverage) {
        assertTrue(CRS.equalsIgnoreMetadata(DefaultGeographicCRS.WGS84,
                coverage.getCoordinateReferenceSystem2D()));

        final GridSampleDimension[] bands = coverage.getSampleDimensions();
        assertEquals(1, bands.length);
        final GridSampleDimension band = bands[0];
        assertSame("Should be geophysics.", band, band.geophysics(true));
        SampleDimensionTableTest.checkTemperatureDimension(band.geophysics(false));
    }

    /**
     * Tests loading a NetCDF image in Mercator CRS.
     *
     * @throws SQLException If the test can't connect to the database.
     * @throws IOException If the image can not be read.
     * @throws CoverageStoreException If a logical error occured.
     */
    @Test
    public void testNetCDF() throws SQLException, IOException, CoverageStoreException {
        final GridCoverageTable table = getDatabase().getTable(GridCoverageTable.class);
        table.envelope.clear();
        table.setLayer(LayerTableTest.NETCDF);
        final GridCoverageReference entry = table.getEntry();

        requireImageData();
        final GridCoverage2D coverage = entry.getCoverage(null);
        assertSame("Coverage shall be cached.", coverage, entry.getCoverage(null));
        checkCoriolisCoverage(coverage);

        final RenderedImage image = coverage.getRenderedImage();
        assertEquals(720, image.getWidth());
        assertEquals(499, image.getHeight());
        table.release();
    }

    /**
     * Checks the {@code GridCoverage2D} instance for the coriolis sample data.
     * Doesn't check the image size, since it depends on the requested envelope.
     */
    static void checkCoriolisCoverage(final GridCoverage2D coverage) {
        assertTrue(coverage.getCoordinateReferenceSystem2D() instanceof ProjectedCRS);

        final GridSampleDimension[] bands = coverage.getSampleDimensions();
        assertEquals(1, bands.length);
        final GridSampleDimension band = bands[0];
        assertSame("Should be geophysics.", band, band.geophysics(true));
    }
}
