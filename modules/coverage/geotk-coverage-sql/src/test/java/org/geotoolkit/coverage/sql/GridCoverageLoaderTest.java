/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2012, Geomatys
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

import java.awt.Dimension;
import java.awt.Rectangle;
import java.io.IOException;
import java.sql.SQLException;
import java.awt.geom.Point2D;
import java.awt.image.RenderedImage;
import java.awt.image.IndexColorModel;

import org.opengis.referencing.crs.ProjectedCRS;

import org.apache.sis.test.DependsOn;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.ViewType;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.internal.image.ScaledColorSpace;
import org.geotoolkit.internal.sql.table.CatalogTestBase;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests {@link GridCoverageLoader}. This is actually tested indirectly, through calls
 * to {@link GridCoverageEntry} methods.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.15
 *
 * @since 3.10 (derived from Seagis)
 */
@DependsOn(GridCoverageTableTest.class)
public final strictfp class GridCoverageLoaderTest extends CatalogTestBase {
    /**
     * Creates a new test suite.
     */
    public GridCoverageLoaderTest() {
        super(GridCoverageLoader.class);
    }

    /**
     * Tests loading an image of temperature data in WGS84 CRS.
     *
     * @throws SQLException If the test can't connect to the database.
     * @throws IOException If the image can not be read.
     * @throws CoverageStoreException If a logical error occurred.
     */
    @Test
    public void testTemperature2D() throws SQLException, IOException, CoverageStoreException {
        final GridCoverageTable table = getDatabase().getTable(GridCoverageTable.class);
        table.envelope.clear();
        table.envelope.setTimeRange(LayerTableTest.SUB_START_TIME, LayerTableTest.SUB_END_TIME);
        table.setLayer(LayerTableTest.TEMPERATURE);
        final GridCoverageReference entry = table.getEntry();

        requireImageData();
        final GridCoverage2D coverage = entry.read(null, null);
        assertSame("Coverage shall be cached.", coverage, entry.getCoverage(null));
        checkTemperatureCoverage(coverage);

        final RenderedImage image = coverage.getRenderedImage();
        assertEquals(4096, image.getWidth());
        assertEquals(2048, image.getHeight());
        table.release();

        show(coverage.view(ViewType.RENDERED));
    }

    /**
     * Checks the {@code GridCoverage2D} instance for the temperature sample data.
     * Doesn't check the image size, since it depends on the requested envelope.
     * <p>
     * <b>NOTE:</b> The sample values tested by this method are those of the coverage
     * at the {@value LayerTableTest#SAMPLE_TIME} date.
     */
    static void checkTemperatureCoverage(final GridCoverage2D coverage) {
        assertEqualsApproximatively(DefaultGeographicCRS.WGS84, coverage.getCoordinateReferenceSystem2D());
        /*
         * Check the SampleDimensions.
         */
        assertSame("The coverage shall be geophysics.", coverage, coverage.view(ViewType.GEOPHYSICS));
        final GridSampleDimension[] bands = coverage.getSampleDimensions();
        assertEquals("Expected exactly one band.", 1, bands.length);
        final GridSampleDimension band = bands[0];
        assertSame("The band shall be geophysics.", band, band.geophysics(true));
        SampleDimensionTableTest.checkTemperatureDimension(band.geophysics(false));
        /*
         * Check the ColorSpace, which should be a special instance for floating point values.
         */
        RenderedImage image = coverage.getRenderedImage();
        assertTrue("Expected the Geotk ColorSpace for floating point values.",
                image.getColorModel().getColorSpace() instanceof ScaledColorSpace);
        /*
         * Ensure that the rendered view is backed by an appropriate IndexColorModel.
         */
        final GridCoverage2D rendered = coverage.view(ViewType.RENDERED);
        assertNotSame("Rendered view should not be the same than geophysics.", coverage, rendered);
        image = rendered.getRenderedImage();
        assertTrue("Rendered view shall use an IndexColorModel.",
                image.getColorModel() instanceof IndexColorModel);
        final IndexColorModel cm = (IndexColorModel) image.getColorModel();
        assertEquals(256, cm.getMapSize());
        assertEquals(8, cm.getPixelSize());
        /*
         * Check the sample values at an an arbitrary position. Note that this is okay to use
         * a large tolerance factor since the purpose is not to test the 'evaluate' accuracy,
         * but rather to check that "geophysics" and "rendered" views are not confused. The
         * actual value vary a bit for image of different resolution or slightly different date.
         */
        double[] buffer = null;
        final Point2D pos = new Point2D.Double(-10, -20);
        double value = (buffer = coverage.evaluate(pos, buffer))[0];
        assertEquals("Geophysics value.", 23.7, value, 0.8);
        value = (buffer = rendered.evaluate(pos, buffer))[0];
        assertEquals("Rendered value.", 178, value, 5);
    }

    /**
     * Tests loading a NetCDF image in Mercator CRS.
     *
     * @throws SQLException If the test can't connect to the database.
     * @throws IOException If the image can not be read.
     * @throws CoverageStoreException If a logical error occurred.
     */
    @Test
    public void testCoriolis() throws SQLException, IOException, CoverageStoreException {
        final GridCoverageTable table = getDatabase().getTable(GridCoverageTable.class);
        table.envelope.clear();
        table.envelope.setVerticalRange(100, 110);
        table.setLayer(LayerTableTest.NETCDF);
        final GridCoverageReference entry = table.getEntry();

        requireImageData();
        final GridCoverage2D coverage = entry.read(null, null);
        assertSame("Coverage shall be cached.", coverage, entry.getCoverage(null));
        checkCoriolisCoverage(coverage);

        final RenderedImage image = coverage.getRenderedImage();
        assertEquals(720, image.getWidth());
        assertEquals(499, image.getHeight());
        table.release();

        show(coverage.view(ViewType.RENDERED));
    }

    /**
     * Checks the {@code GridCoverage2D} instance for the Coriolis sample data.
     * Doesn't check the image size, since it depends on the requested envelope.
     * <p>
     * <b>NOTE:</b> The sample values tested by this method are those of the coverage
     * at the 100 metres depth.
     */
    static void checkCoriolisCoverage(final GridCoverage2D coverage) {
        assertTrue(coverage.getCoordinateReferenceSystem2D() instanceof ProjectedCRS);
        /*
         * Check the SampleDimensions.
         */
        assertSame("The coverage shall be geophysics.", coverage, coverage.view(ViewType.GEOPHYSICS));
        final GridSampleDimension[] bands = coverage.getSampleDimensions();
        assertEquals("Expected exactly one band.", 1, bands.length);
        final GridSampleDimension band = bands[0];
        assertSame("The band shall be geophysics.", band, band.geophysics(true));
        /*
         * Check the ColorSpace, which should be a special instance for floating point values.
         */
        RenderedImage image = coverage.getRenderedImage();
        assertTrue("Expected the Geotk ColorSpace for floating point values.",
                image.getColorModel().getColorSpace() instanceof ScaledColorSpace);
        /*
         * Ensure that the rendered view is backed by an appropriate IndexColorModel.
         */
        final GridCoverage2D rendered = coverage.view(ViewType.RENDERED);
        assertNotSame("Rendered view should not be the same than geophysics.", coverage, rendered);
        image = rendered.getRenderedImage();
        assertTrue("Rendered view shall use an IndexColorModel.",
                image.getColorModel() instanceof IndexColorModel);
        final IndexColorModel cm = (IndexColorModel) image.getColorModel();
        assertEquals("Color map size", 16, cm.getPixelSize());
        assertEquals("Note: if the map size is 65536, it would mean that Geotk failed " +
                "to convert signed integer values to unsigned integers.", 43002, cm.getMapSize());
        assertEquals("Transparent pixel", 0, cm.getTransparentPixel());
        /*
         * Check the sample values at an an arbitrary position. Note that this is okay to use
         * a large tolerance factor since the purpose is not to test the 'evaluate' accuracy,
         * but rather to check that "geophysics" and "rendered" views are not confused. The
         * actual value vary a bit for image of different resolution or slightly different date.
         */
        double[] buffer = null;
        final Point2D pos = new Point2D.Double(-10, -20);
        double value = (buffer = coverage.evaluate(pos, buffer))[0];
        assertEquals("Geophysics value.", 16.76, value, 0.8);
        value = (buffer = rendered.evaluate(pos, buffer))[0];
        assertEquals("Rendered value.", 19762, value, 5);
    }

    /**
     * Tests loading a tiled image.
     *
     * @throws SQLException If the test can't connect to the database.
     * @throws IOException If the image can not be read.
     * @throws CoverageStoreException If a logical error occurred.
     */
    @Test
    public void testBluemarble() throws SQLException, IOException, CoverageStoreException {
        final GridCoverageTable table = getDatabase().getTable(GridCoverageTable.class);
        table.envelope.clear();
        table.setLayer(LayerTableTest.BLUEMARBLE);
        table.envelope.setHorizontalRange(new Rectangle(-100, -40, 200, 80));
        table.envelope.setPreferredImageSize(new Dimension(100, 80));
        final GridCoverageReference entry = table.getEntry();

        requireImageData();
        final GridCoverage2D coverage = entry.read(table.envelope, null);
        checkBluemarbleCoverage(coverage);

        final RenderedImage image = coverage.getRenderedImage();
        // The image should be slightly larger than the requested side because
        // MosaicImageReader should have selected a more efficient size.
        assertEquals(134, image.getWidth());
        assertEquals(107, image.getHeight());
        table.release();

        show(coverage.view(ViewType.RENDERED));
    }

    /**
     * Checks the {@code GridCoverage2D} instance for the Bluemarble sample data.
     * Doesn't check the image size, since it depends on the requested envelope.
     */
    static void checkBluemarbleCoverage(final GridCoverage2D coverage) {
        assertEqualsApproximatively(DefaultGeographicCRS.WGS84, coverage.getCoordinateReferenceSystem2D());
        /*
         * Check the SampleDimensions.
         */
        assertSame("The coverage shall be rendered.", coverage, coverage.view(ViewType.RENDERED));
        final GridSampleDimension[] bands = coverage.getSampleDimensions();
        assertEquals("Expected RGB bands.", 3, bands.length);
    }

    /**
     * Tests a layer which contain 2 bands.
     *
     * @throws SQLException If the test can't connect to the database.
     * @throws IOException If the image can not be read.
     * @throws CoverageStoreException If a logical error occurred.
     */
    @Test
    public void testMars2D() throws SQLException, IOException, CoverageStoreException {
        final GridCoverageTable table = getDatabase().getTable(GridCoverageTable.class);
        table.envelope.clear();
        table.envelope.setTimeRange(date("2007-05-22 00:24:00"), date("2007-05-22 00:36:00"));
        table.setLayer(LayerTableTest.GEOSTROPHIC_CURRENT);
        final GridCoverageReference entry = table.getEntry();

        requireImageData();
        final GridCoverage2D coverage = entry.read(table.envelope, null);
        checkMars2DCoverage(coverage);
        table.release();

        show(coverage.view(ViewType.RENDERED));
    }

    /**
     * Checks the {@code GridCoverage2D} instance for the Mars2D sample data.
     * Doesn't check the image size, since it depends on the requested envelope.
     */
    static void checkMars2DCoverage(final GridCoverage2D coverage) {
        assertEqualsApproximatively(DefaultGeographicCRS.WGS84, coverage.getCoordinateReferenceSystem2D());
        /*
         * Check the SampleDimensions.
         */
        assertSame("The coverage shall be geophysics.", coverage, coverage.view(ViewType.GEOPHYSICS));
        final GridSampleDimension[] bands = coverage.getSampleDimensions();
        assertEquals("Expected 2 bands.", 2, bands.length);
    }
}
