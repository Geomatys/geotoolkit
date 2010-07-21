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

import java.io.File;
import java.awt.Rectangle;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Locale;
import java.util.Set;

import org.geotoolkit.test.Depend;
import org.geotoolkit.test.TestData;
import org.geotoolkit.internal.sql.table.CatalogTestBase;
import org.geotoolkit.image.io.plugin.WorldFileImageReader;
import org.geotoolkit.image.io.plugin.TextMatrixImageReader;
import org.geotoolkit.coverage.grid.ViewType;
import org.geotoolkit.util.MeasurementRange;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests {@link WritableGridCoverageTable}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.14
 *
 * @since 3.12
 */
@Depend(GridCoverageTableTest.class)
public final class WritableGridCoverageTableTest extends CatalogTestBase {
    /**
     * The file which contains geostrophic current data.
     */
    public static final String GEOSTROPHIC_CURRENT_FILE = "Iroise/champs.r3_23-05-2007.nc";

    /**
     * A temporary layer in which images will be inserted.
     */
    private static final String TEMPORARY_LAYER = "Temporary";

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
                assertEquals(new Rectangle(20, 42), reference.imageBounds);
                assertEquals(3395,                  reference.horizontalSRID);
                assertEquals(0,                     reference.verticalSRID);
                assertNull  (                       reference.verticalValues);
                assertNull  (                       reference.dateRanges);
            }
        };
        final Set<File> files = Collections.singleton(TestData.file(TextMatrixImageReader.class, "matrix.txt"));
        table.setLayer(TEMPORARY_LAYER);
        /*
         * TODO: Ugly patch below: set the locale the an anglo-saxon one, so we can parse the
         * matrix numbers. We should find an other way to provide the locale to the reader...
         */
        final Locale locale = Locale.getDefault();
        try {
            Locale.setDefault(Locale.CANADA);
            assertEquals(1, table.addEntries(files, 0, database, controller));
        } finally {
            Locale.setDefault(locale);
        }
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

        assertEquals("matrix",    format.identifier);
        assertEquals("matrix",    format.imageFormat);
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
                assertEquals("Iroise",                reference.path.getName());
                assertEquals("champs.r3_23-05-2007",  reference.filename);
                assertEquals("nc",                    reference.extension);
                assertEquals("NetCDF",                reference.format);
                assertEquals(new Rectangle(273, 423), reference.imageBounds);
                assertEquals("TODO", 0,               reference.horizontalSRID);
                assertEquals(0,                       reference.verticalSRID);
                assertNull  (                         reference.verticalValues);
                assertNull  ("TODO",                  reference.dateRanges);

                reference.format = "Mars (u,v)";
                reference.horizontalSRID = 4326; // TODO: need to auto-detect.
            }
        };
        requireImageData();
        final Set<File> files = Collections.singleton(toImageFile(GEOSTROPHIC_CURRENT_FILE));
        table.setLayer(TEMPORARY_LAYER);
        assertEquals(1, table.addEntries(files, 0, database, controller));
        table.release();
    }
}
