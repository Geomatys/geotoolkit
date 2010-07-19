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
import java.awt.geom.Rectangle2D;

import org.geotoolkit.test.Depend;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.io.GridCoverageReadParam;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.internal.sql.table.CatalogTestBase;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests {@link LayerCoverageReader}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.14
 *
 * @since 3.10 (derived from Seagis)
 */
@Depend(CoverageDatabaseTest.class)
public final class LayerCoverageReaderTest extends CatalogTestBase {
    /**
     * Whatever we should show the image in images (for debugging purpose only).
     */
    private static final boolean SHOW = false;

    /**
     * The coverage database.
     */
    private static CoverageDatabase database;

    /**
     * Creates the database when first needed.
     */
    private static synchronized CoverageDatabase getCoverageDatabase() {
        if (database == null) {
            database = new CoverageDatabase((TableFactory) getDatabase());
        }
        return database;
    }

    /**
     * Disposes the database.
     */
    @AfterClass
    public static synchronized void dispose() {
        if (database != null) {
            database.dispose();
            database = null;
        }
    }

    /**
     * Tests loading an image of temperature data in WGS84 CRS.
     *
     * @throws SQLException If the test can't connect to the database.
     * @throws IOException If the image can not be read.
     * @throws CoverageStoreException If a logical error occured.
     */
    @Test
    public void testTemperature() throws SQLException, IOException, CoverageStoreException {
        final CoverageDatabase database = getCoverageDatabase();
        final LayerCoverageReader reader = database.createGridCoverageReader(LayerTableTest.TEMPERATURE);

        final Layer layer = reader.getInput();
        assertEquals(LayerTableTest.TEMPERATURE, layer.getName());

        final CoverageEnvelope envelope = layer.getEnvelope(null, null);
        envelope.setTimeRange(LayerTableTest.SUB_START_TIME, LayerTableTest.SUB_END_TIME);

        final GridCoverageReadParam param = new GridCoverageReadParam();
        param.setEnvelope(envelope);
        param.setResolution(0.703125, 0.703125, 0, 0); // Image of size (512, 256).

        requireImageData();
        GridCoverage2D coverage = (GridCoverage2D) reader.read(0, param);
        assertEquals(512, coverage.getRenderedImage().getWidth());
        assertEquals(256, coverage.getRenderedImage().getHeight());
        GridCoverageLoaderTest.checkTemperatureCoverage(coverage);

        if (SHOW) show(coverage);

        // Read one more time, in order to ensure that recycling LayerCoverageReader work.
        // Before we fixed this test, we got an "Input not set" exception in such situation.
        param.setResolution(1.40625, 1.40625, 0, 0);
        coverage = (GridCoverage2D) reader.read(0, param);
        assertEquals(256, coverage.getRenderedImage().getWidth());
        assertEquals(128, coverage.getRenderedImage().getHeight());
        GridCoverageLoaderTest.checkTemperatureCoverage(coverage);
    }

    /**
     * Tests loading an image of temperature data in Mercator CRS.
     *
     * @throws SQLException If the test can't connect to the database.
     * @throws IOException If the image can not be read.
     * @throws CoverageStoreException If a logical error occured.
     */
    @Test
    public void testCoriolis() throws SQLException, IOException, CoverageStoreException {
        final CoverageDatabase database = getCoverageDatabase();
        final LayerCoverageReader reader = database.createGridCoverageReader(LayerTableTest.NETCDF);

        final Layer layer = reader.getInput();
        assertEquals(LayerTableTest.NETCDF, layer.getName());

        final CoverageEnvelope envelope = layer.getEnvelope(null, 100);
        envelope.setHorizontalRange(new Rectangle2D.Double(-40, -40, 80, 80));

        final GridCoverageReadParam param = new GridCoverageReadParam();
        param.setEnvelope(envelope);

        requireImageData();
        final GridCoverage2D coverage = (GridCoverage2D) reader.read(0, param);
        assertEquals(160, coverage.getRenderedImage().getWidth());
        assertEquals(175, coverage.getRenderedImage().getHeight());
        GridCoverageLoaderTest.checkCoriolisCoverage(coverage);

        if (SHOW) show(coverage);
    }

    /**
     * Tests loading a tiled image.
     *
     * @throws SQLException If the test can't connect to the database.
     * @throws IOException If the image can not be read.
     * @throws CoverageStoreException If a logical error occured.
     */
    @Test
    public void testBluemarble() throws SQLException, IOException, CoverageStoreException {
        final CoverageDatabase database = getCoverageDatabase();
        final LayerCoverageReader reader = database.createGridCoverageReader(LayerTableTest.BLUEMARBLE);

        final Layer layer = reader.getInput();
        assertEquals(LayerTableTest.BLUEMARBLE, layer.getName());

        final CoverageEnvelope envelope = layer.getEnvelope(null, 100);
        envelope.setHorizontalRange(new Rectangle2D.Double(-40, -40, 80, 80));

        final GridCoverageReadParam param = new GridCoverageReadParam();
        param.setEnvelope(envelope);

        requireImageData();
        final GridCoverage2D coverage = (GridCoverage2D) reader.read(0, param);
        assertEquals(640, coverage.getRenderedImage().getWidth());
        assertEquals(640, coverage.getRenderedImage().getHeight());
        GridCoverageLoaderTest.checkBluemarbleCoverage(coverage);

        if (SHOW) show(coverage);
    }
}
