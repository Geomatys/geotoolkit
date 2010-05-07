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

import java.awt.Rectangle;
import java.sql.SQLException;
import java.util.Collections;

import org.geotoolkit.test.Depend;
import org.geotoolkit.internal.sql.table.CatalogTestBase;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests {@link WritableGridCoverageTable}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.12
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
     * Creates a new temporary layer in which to insert the new images.
     *
     * @throws SQLException If an error occured while creating the temporary layer.
     */
    @Before
    public void createTemporaryLayer() throws SQLException {
        final LayerTable table = getDatabase().getTable(LayerTable.class);
        assertTrue(table.createIfAbsent(TEMPORARY_LAYER));
        table.release();
    }

    /**
     * Deletes the temporary layer and all coverages included in it.
     *
     * @throws SQLException If an error occured while deleting the temporary layer.
     */
    @After
    public void deleteTemporaryLayer() throws SQLException {
        final LayerTable table = getDatabase().getTable(LayerTable.class);
        assertEquals(1, table.delete(TEMPORARY_LAYER));
        table.release();
    }

    /**
     * Tests insertion for NetCDF images with two bands but no elevation.
     * The same file contains values at many different dates.
     * <p>
     * This test creates a temporary layer and fills it with the same data than
     * the ones declared in the {@value LayerTableTest#GEOSTROPHIC_CURRENT} layer.
     *
     * @throws Exception If a SQL, I/O or referencing error occured.
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
        table.setLayer(TEMPORARY_LAYER);
        table.addEntries(Collections.singleton(toImageFile(GEOSTROPHIC_CURRENT_FILE)), 0, database, controller);
        table.release();
    }
}
