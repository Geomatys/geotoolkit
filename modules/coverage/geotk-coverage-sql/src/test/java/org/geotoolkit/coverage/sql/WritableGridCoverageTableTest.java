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
    @Ignore
    public void testGeostrophicCurrent2D() throws Exception {
        final WritableGridCoverageTable table = getDatabase().getTable(WritableGridCoverageTable.class);
        table.setLayer(TEMPORARY_LAYER);
        requireImageData();
        table.addEntries(Collections.singleton(toImageFile(GEOSTROPHIC_CURRENT_FILE)), 0);
        table.release();
    }
}
