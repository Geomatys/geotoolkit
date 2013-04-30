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

import java.io.File;
import java.awt.Rectangle;
import java.awt.image.RenderedImage;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.List;
import java.util.Set;

import org.opengis.geometry.Envelope;

import org.apache.sis.test.DependsOn;
import org.geotoolkit.test.TestData;
import org.geotoolkit.internal.sql.table.CatalogTestBase;
import org.geotoolkit.image.io.plugin.WorldFileImageReader;
import org.geotoolkit.image.io.plugin.TextMatrixImageReader;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.ViewType;
import org.apache.sis.geometry.AbstractEnvelope;
import org.geotoolkit.util.MeasurementRange;
import org.apache.sis.measure.Range;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests {@link WritableGridCoverageTable}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.16
 *
 * @since 3.12
 */
@DependsOn(GridCoverageTableTest.class)
public final strictfp class WritableGridCoverageTableTest extends CatalogTestBase {
    /**
     * The file which contains geostrophic current data.
     */
    public static final String GEOSTROPHIC_CURRENT_FILE = "Iroise/champs.r3_23-05-2007.nc";

    /**
     * The NcML file which is an aggregation of 6 NetCDF files (2 variables at 3 different dates).
     */
    public static final String AGGREGATION_FILE = "World/Coriolis/Aggregation.ncml";

    /**
     * A temporary layer in which images will be inserted.
     */
    private static final String TEMPORARY_LAYER = "Temporary";

    /**
     * Tolerance factor for floating point comparisons.
     */
    private static final double EPS = 1E-7;

    /**
     * {@code true} if {@link #createTemporaryLayer()} has created a temporary layer.
     */
    private boolean layerCreated;

    /**
     * Registers <cite>World File Readers</cite>.
     * They are required by {@link #textMatrix()}.
     */
    @BeforeClass
    public static void registerWorldFiles() {
        WorldFileImageReader.Spi.registerDefaults(null);
    }

    /**
     * Unregisters the <cite>World File Readers</cite>.
     */
    @AfterClass
    public static void unregisterWorldFiles() {
        WorldFileImageReader.Spi.unregisterDefaults(null);
    }

    /**
     * Creates a new test suite.
     */
    public WritableGridCoverageTableTest() {
        super(WritableGridCoverageTable.class);
    }

    /**
     * Creates a new temporary layer in which to insert the new images.
     *
     * @throws SQLException If an error occurred while creating the temporary layer.
     */
    @Before
    public void createTemporaryLayer() throws SQLException {
        if (canTest()) {
            final LayerTable table = getDatabase().getTable(LayerTable.class);
            assertTrue(table.createIfAbsent(TEMPORARY_LAYER));
            table.release();
            layerCreated = true;
        }
    }

    /**
     * Deletes the temporary layer and all coverages included in it.
     *
     * @throws SQLException If an error occurred while deleting the temporary layer.
     */
    @After
    public void deleteTemporaryLayer() throws SQLException {
        if (layerCreated) {
            final LayerTable table = getDatabase().getTable(LayerTable.class);
            assertEquals(1, table.delete(TEMPORARY_LAYER));
            table.release();
            layerCreated = false;
        }
    }

    /**
     * Returns {@code true} if the given range is bounded, or {@code false} if unbounded.
     */
    private static boolean isBounded(final Range<?> range) {
        final Comparable<?> min = range.getMinValue();
        final Comparable<?> max = range.getMaxValue();
        assertEquals(min != null, max != null);
        return (min != null) & (max != null);
    }

    /**
     * Tests insertion for ASCII data. This test reuses the {@code "matrix.txt"} test file
     * from the {@code geotk-coverageio} module. The {@code ".txt"} file is completed by
     * {@code "matrix.tfw"} and {@code "matrix.prj"} files.
     *
     * @throws Exception If a SQL, I/O or referencing error occurred.
     *
     * @since 3.14
     */
    @Test
    public void testMatrix() throws Exception {
        final WritableGridCoverageTable table = getDatabase().getTable(WritableGridCoverageTable.class);
        final CoverageDatabase database = new CoverageDatabase((TableFactory) getDatabase());
        final CoverageDatabaseController controller = new CoverageDatabaseController() {
            /**
             * Checks the parameter values before their insertion in the database.
             */
            @Override
            public void coverageAdding(CoverageDatabaseEvent event, NewGridCoverageReference reference) {
                assertEquals("test-data",           reference.path.getName());
                assertEquals("matrix",              reference.filename);
                assertEquals("txt",                 reference.extension);
                assertEquals("matrix",              reference.format);
                assertEquals("imageIndex", 0,       reference.imageIndex);
                assertEquals(new Rectangle(20, 42), reference.imageBounds);
                assertEquals(     0,                reference.gridToCRS.getShearX(),     0);
                assertEquals(     0,                reference.gridToCRS.getShearY(),     0);
                assertEquals(  1000,                reference.gridToCRS.getScaleX(),     0);
                assertEquals( -1000,                reference.gridToCRS.getScaleY(),     0);
                assertEquals(-10000,                reference.gridToCRS.getTranslateX(), 0);
                assertEquals( 21000,                reference.gridToCRS.getTranslateY(), 0);
                assertEquals(  6001,                reference.horizontalSRID);
                assertEquals(     0,                reference.verticalSRID);
                assertNull  (                       reference.verticalValues);
                assertNull  (                       reference.dateRanges);
            }

            @Override
            public Collection<String> filterImages(List<String> images, boolean multiSelectionAllowed) throws DatabaseVetoException {
                fail("Should not need to be invoked when there is exactly one image to insert.");
                return images;
            }
        };
        final Set<File> files = Collections.singleton(TestData.file(TextMatrixImageReader.class, "matrix.txt"));
        table.setLayer(TEMPORARY_LAYER);
        /*
         * TODO: Ugly patch below: set the locale to an anglo-saxon one, so we can parse the
         * matrix numbers. We should find an other way to provide the locale to the reader...
         */
        final Locale locale = Locale.getDefault();
        try {
            Locale.setDefault(Locale.CANADA);
            assertEquals(1, table.addEntries(files, database, controller));
        } finally {
            Locale.setDefault(locale);
        }
        /*
         * At this point, the entry has been added to the database. Now read
         * the GridCoverages table in order to get the entry we just added.
         */
        final GridCoverageEntry entry = table.getEntry();
        assertEquals("matrix.txt",                entry.getFile(File.class).getName());
        assertEquals("matrix",                    entry.getImageFormat());
        assertFalse ("Expected unbounded range.", isBounded(entry.getZRange()));
        assertFalse ("Expected unbounded range.", isBounded(entry.getTimeRange()));
        final Envelope envelope = entry.getEnvelope();
        assertEquals(-10000, envelope.getMinimum(0), 0);
        assertEquals( 10000, envelope.getMaximum(0), 0);
        assertEquals(-21000, envelope.getMinimum(1), 0);
        assertEquals( 21000, envelope.getMaximum(1), 0);
        table.release();
        /*
         * Clean-up: delete the "matrix" format, so that the next execution will
         * recreate a new format. Note that we need to delete the layer first.
         * We also perform an opportunist check of the format inserted by the above code.
         */
        deleteTemporaryLayer();
        final FormatTable ft = getDatabase().getTable(FormatTable.class);
        final FormatEntry format = ft.getEntry("matrix");
        assertEquals(1, ft.delete("matrix"));
        ft.release();
        /*
         * Verify the format.
         */
        assertEquals("matrix", format.identifier);
        assertEquals("matrix", format.imageFormat);
        assertEquals("grayscale", format.paletteName);
        assertEquals(ViewType.GEOPHYSICS, format.viewType);
        final MeasurementRange<?>[] range = format.getSampleValueRanges();
        assertNotNull(range);
        assertEquals(1, range.length);
        // The minimum value is actually -1.893, but we didn't specified the pad value.
        assertEquals(-9999,  range[0].getMinimum(true), 1E-4);
        assertEquals(31.140, range[0].getMaximum(true), 1E-4);
        assertNull(range[0].getUnits());
    }

    /**
     * Tests insertion for NetCDF images with two bands but no elevation.
     * The variables declared in the files are "h0", "xe", "u" and "v".
     * This tests will insert the second variable, namely "xe".
     * The same file contains values at many different dates.
     * <p>
     * This test creates a temporary layer and fills it with the same data than
     * the ones declared in the {@value LayerTableTest#GEOSTROPHIC_CURRENT} layer.
     *
     * @throws Exception If a SQL, I/O or referencing error occurred.
     */
    @Test
    public void testGeostrophicCurrent2D() throws Exception {
        final WritableGridCoverageTable table = getDatabase().getTable(WritableGridCoverageTable.class);
        final CoverageDatabase database = new CoverageDatabase((TableFactory) getDatabase());
        final CoverageDatabaseController controller = new CoverageDatabaseController() {
            /**
             * Checks the parameter values before their insertion in the database.
             *
             * Forces also the format to one of the pre-defined ones, since we don't
             * want to test the creation of new entries in the format table.
             */
            @Override
            public void coverageAdding(CoverageDatabaseEvent event, NewGridCoverageReference reference) {
                assertEquals ("Iroise",                reference.path.getName());
                assertEquals ("champs.r3_23-05-2007",  reference.filename);
                assertEquals ("nc",                    reference.extension);
                assertEquals ("NetCDF",                reference.format);
                assertEquals ("imageIndex", 0,         reference.imageIndex);
                assertEquals (new Rectangle(273, 423), reference.imageBounds);
                assertEquals ( 0.0,                    reference.gridToCRS.getShearX(),     0.0);
                assertEquals ( 0.0,                    reference.gridToCRS.getShearY(),     0.0);
                assertEquals ( 0.0040461,              reference.gridToCRS.getScaleX(),     EPS);
                assertEquals (-0.0026991,              reference.gridToCRS.getScaleY(),     EPS);
                assertEquals (-5.3390946,              reference.gridToCRS.getTranslateX(), EPS);
                assertEquals (48.7933655,              reference.gridToCRS.getTranslateY(), EPS);
                assertEquals ("Horizontal",  4326,     reference.horizontalSRID);
                assertEquals ("Expected no z.", 0,     reference.verticalSRID);
                assertNull   ("Expected no z.",        reference.verticalValues);
                assertNotNull("Expected dates",        reference.dateRanges);

                reference.format = "Mars (u,v)";
                reference.horizontalSRID = 4326; // TODO: need to auto-detect.
            }

            @Override
            public Collection<String> filterImages(List<String> images, boolean multiSelectionAllowed) throws DatabaseVetoException {
                assertTrue("Multi-selection should be allowed for NetCDF files.", multiSelectionAllowed);
                assertArrayEquals("Expected one image for each variable.", new String[] {
                    "h0", "xe", "u", "v"
                }, images.toArray());
                // Select the "xe" variable for this test.
                return Collections.singleton(images.get(1));
            }
        };
        requireImageData();
        final Set<File> files = Collections.singleton(toImageFile(GEOSTROPHIC_CURRENT_FILE));
        table.setLayer(TEMPORARY_LAYER);
        assertEquals("Expected the addition of many entries, one for each date.",
                285, table.addEntries(files, database, controller));
        /*
         * At this point, the entries have been added to the database. Now
         * test each individual entry, including a test of coverage reading.
         */
        int imageIndex = 285;
        double lastTime = Double.POSITIVE_INFINITY;
        for (final GridCoverageEntry entry : table.getEntries()) {
            assertEquals("Every entry are expected to use the same input file.",
                         "champs.r3_23-05-2007.nc", entry.getFile(File.class).getName());
            assertEquals("Image indices are expected to be decreasing when iterating " +
                         "from the most recent image to the oldest one.",
                         imageIndex--, entry.getIdentifier().imageIndex);
            assertEquals("NetCDF", entry.getImageFormat());
            assertTrue  ("Expected bounded range.", isBounded(entry.getZRange()));
            assertTrue  ("Expected bounded range.", isBounded(entry.getTimeRange()));
            /*
             * Check the envelope, we should be the same for all entry in
             * all dimensions except the time dimension.
             */
            final Envelope envelope = entry.getEnvelope();
            assertEquals("Expected a three-dimensional coverage.", 3, envelope.getDimension());
            assertEquals(-5.3390946, envelope.getMinimum(0), EPS);
            assertEquals(-4.2345093, envelope.getMaximum(0), EPS);
            assertEquals(47.6516435, envelope.getMinimum(1), EPS);
            assertEquals(48.7933654, envelope.getMaximum(1), EPS);
            final double time = envelope.getMedian(2);
            assertTrue("Expected ordering from most recent to oldest entries.", time < lastTime);
            lastTime = time;
            /*
             * Inspect the coverage. For performance raison in this test suite,
             * we test only some images (they should all be similar anyway).
             */
            if ((imageIndex % 10) == 0) {
                final GridCoverage2D coverage = entry.getCoverage(null);
                assertSame("The coverage should be the geophysics view.",
                           coverage, coverage.view(ViewType.GEOPHYSICS));
                assertNotSame("The coverage should not be packed.",
                              coverage, coverage.view(ViewType.PACKED));
                assertTrue(((AbstractEnvelope) envelope).equals(coverage.getEnvelope(), EPS, false));
                /*
                 * Inspect the image of the rendered view. We expect two bands,
                 * despite the color model being an instance of IndexColorModel.
                 */
                final RenderedImage image = coverage.view(ViewType.RENDERED).getRenderedImage();
                assertEquals("Expected two bands.", 2, image.getSampleModel().getNumBands());
//              org.geotoolkit.gui.swing.image.OperationTreeBrowser.show(image);
            }
        }
        assertEquals("Missing entries.", 0, imageIndex);
        table.release();
    }

    /**
     * Tests insertion of a NcML file which is an aggregation of 3 NetCDF files.
     *
     * @throws Exception If a SQL, I/O or referencing error occurred.
     */
    @Test
    public void testNcML() throws Exception {
        final WritableGridCoverageTable table = getDatabase().getTable(WritableGridCoverageTable.class);
        final CoverageDatabase database = new CoverageDatabase((TableFactory) getDatabase());
        final CoverageDatabaseController controller = new CoverageDatabaseController() {
            /**
             * Checks the parameter values before their insertion in the database.
             */
            @Override
            public void coverageAdding(CoverageDatabaseEvent event, NewGridCoverageReference reference) {
                assertEquals ("Coriolis",              reference.path.getName());
                assertEquals ("Aggregation",           reference.filename);
                assertEquals ("ncml",                  reference.extension);
                assertEquals ("Coriolis (salinity)",   reference.format);
                assertEquals ("imageIndex", 0,         reference.imageIndex);
                assertEquals (new Rectangle(720, 499), reference.imageBounds);
                assertEquals (        0.00,            reference.gridToCRS.getShearX(),     0);
                assertEquals (        0.00,            reference.gridToCRS.getShearY(),     0);
                assertEquals (    55597.46,            reference.gridToCRS.getScaleX(),     0.01);
                assertEquals (   -55597.46,            reference.gridToCRS.getScaleY(),     0.01);
                assertEquals (-19959489.33,            reference.gridToCRS.getTranslateX(), 0.01);
                assertEquals ( 13843768.17,            reference.gridToCRS.getTranslateY(), 0.01);
                assertEquals ("TODO", 0,               reference.horizontalSRID);
                assertEquals ("TODO", 0,               reference.verticalSRID);
                assertNotNull("Expected depths",       reference.verticalValues);
                assertNotNull("Expected dates",        reference.dateRanges);

                reference.horizontalSRID = 3395; // TODO: need to auto-detect.
                reference.verticalSRID   = 5715; // TODO: need to auto-detect.
            }

            @Override
            public Collection<String> filterImages(List<String> images, boolean multiSelectionAllowed) throws DatabaseVetoException {
                assertTrue("Multi-selection should be allowed for NetCDF files.", multiSelectionAllowed);
                assertArrayEquals("Expected one image for each variable.", new String[] {
                    "temperature", "temperature_pct_variance", "salinity", "salinity_pct_variance"
                }, images.toArray());
                // Select the "salinity" variable for this test.
                return Collections.singleton(images.get(2));
            }
        };
        requireImageData();
        final Set<File> files = Collections.singleton(toImageFile(AGGREGATION_FILE));
        table.setLayer(TEMPORARY_LAYER);
        assertEquals("Expected the addition of many entries, one for each date.",
                3, table.addEntries(files, database, controller));
        /*
         * At this point, the entries have been added to the database. Now
         * test each individual entry, including a test of coverage reading.
         */
        final String[] expectedFilenames = {
            "OA_RTQCGL01_20070620_FLD_PSAL.nc",
            "OA_RTQCGL01_20070613_FLD_PSAL.nc",
            "OA_RTQCGL01_20070606_FLD_PSAL.nc"
        };
        int count = 0;
        double lastTime = Double.POSITIVE_INFINITY;
        for (final GridCoverageEntry entry : table.getEntries()) {
            assertEquals(expectedFilenames[count++], entry.getFile(File.class).getName());
            assertEquals(1, entry.getIdentifier().imageIndex);
            assertEquals("NetCDF", entry.getImageFormat());
            assertTrue  ("Expected bounded range.", isBounded(entry.getZRange()));
            assertTrue  ("Expected bounded range.", isBounded(entry.getTimeRange()));
            /*
             * Check the envelope, we should be the same for all entry in
             * all dimensions except the time dimension.
             */
            final Envelope envelope = entry.getEnvelope();
            assertEquals("Expected a four-dimensional coverage.", 4, envelope.getDimension());
            assertEquals(-19959489.33, envelope.getMinimum(0), 0.01);
            assertEquals( 20070684.26, envelope.getMaximum(0), 0.01);
            assertEquals(-13899365.64, envelope.getMinimum(1), 0.01);
            assertEquals( 13843768.17, envelope.getMaximum(1), 0.01);
            assertEquals(        5,    envelope.getMinimum(2), 0);
            assertEquals(     1950,    envelope.getMaximum(2), 0);
            final double time = envelope.getMedian(3);
            assertTrue("Expected ordering from most recent to oldest entries.", time < lastTime);
            lastTime = time;
            /*
             * Inspect the coverage. For performance raison, we inspect
             * only the last image (all other images should be similar).
             */
            if (count == expectedFilenames.length) {
                final GridCoverage2D coverage = entry.getCoverage(null);
                assertSame("The coverage should be the geophysics view.",
                           coverage, coverage.view(ViewType.GEOPHYSICS));
                assertNotSame("The coverage should not be packed.",
                              coverage, coverage.view(ViewType.PACKED));
                assertTrue(((AbstractEnvelope) envelope).equals(coverage.getEnvelope(), 0.01, false));
                /*
                 * Inspect the image of the rendered view. We expect two bands,
                 * despite the color model being an instance of IndexColorModel.
                 */
                final RenderedImage image = coverage.view(ViewType.RENDERED).getRenderedImage();
                assertEquals("Expected one band.", 1, image.getSampleModel().getNumBands());
//              org.geotoolkit.gui.swing.image.OperationTreeBrowser.show(image);
            }
        }
        assertEquals("Missing entries.", expectedFilenames.length, count);
        table.release();
    }
}
