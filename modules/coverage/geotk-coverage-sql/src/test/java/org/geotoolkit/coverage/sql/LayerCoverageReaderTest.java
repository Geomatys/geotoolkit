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

import org.geotoolkit.test.Depend;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.io.GridCoverageReadParam;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.internal.sql.table.CatalogTestBase;

import org.junit.*;
import static org.junit.Assert.*;
import static org.geotoolkit.coverage.sql.CoverageDatabase.now;


/**
 * Tests {@link LayerCoverageReader}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.10
 *
 * @since 3.10 (derived from Seagis)
 */
@Depend(CoverageDatabaseTest.class)
public final class LayerCoverageReaderTest extends CatalogTestBase {
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
        final Layer layer = now(database.getLayer(LayerTableTest.TEMPERATURE));
        final LayerCoverageReader reader = new LayerCoverageReader(database);
        reader.setInput(layer);

        final CoverageEnvelope envelope = layer.getEnvelope(null, null);
        envelope.setTimeRange(LayerTableTest.SUB_START_TIME, LayerTableTest.SUB_END_TIME);

        final GridCoverageReadParam param = new GridCoverageReadParam();
        param.setEnvelope(envelope);
        param.setResolution(0.703125, 0.703125, 0, 0); // Image of size (512, 256).

        requireImageData();
        final GridCoverage2D coverage = (GridCoverage2D) reader.read(0, param);
        assertEquals(512, coverage.getRenderedImage().getWidth());
        assertEquals(256, coverage.getRenderedImage().getHeight());
        GridCoverageLoaderTest.checkTemperatureCoverage(coverage);
    }
}
