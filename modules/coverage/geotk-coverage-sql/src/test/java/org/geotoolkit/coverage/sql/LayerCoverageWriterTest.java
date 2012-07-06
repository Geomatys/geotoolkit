/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2012, Geomatys
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
import java.awt.image.BufferedImage;

import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GridCoverageBuilder;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.internal.sql.table.CatalogTestBase;
import org.geotoolkit.internal.sql.table.ConfigurationKey;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.test.Depend;

import org.junit.*;
import static org.geotoolkit.test.Assert.*;


/**
 * Tests {@link LayerCoverageWriter}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.20
 */
@Depend(CoverageDatabaseTest.class)
public final strictfp class LayerCoverageWriterTest extends CatalogTestBase {
    /**
     * Creates a new test suite.
     */
    public LayerCoverageWriterTest() {
        super(LayerCoverageWriter.class);
    }

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
     * Creates a grid coverage wrapping an empty image.
     * We don't care about the image content - only the ability to write it is of interest.
     */
    private static GridCoverage2D createCoverage() {
        final GridCoverageBuilder builder = new GridCoverageBuilder();
        builder.setRenderedImage(new BufferedImage(12, 8, BufferedImage.BITMASK));
        builder.setCoordinateReferenceSystem(DefaultGeographicCRS.WGS84);
        builder.setEnvelope(-18, -6, 18, 6);
        return builder.getGridCoverage2D();
    }

    /**
     * Tests writing a empty PNG image with default series, directory, and image format.
     *
     * @throws CoverageStoreException If an error occurred.
     */
    @Test
    @Ignore("Missing SRID")
    public void testDefaultWrite() throws CoverageStoreException {
        final CoverageDatabase database = getCoverageDatabase();
        final LayerCoverageWriter writer = database.createGridCoverageWriter("WriterTest");
        try {
            writer.write(createCoverage(), null);
        } finally {
            // Delete the temporary images created by the test.
            final File directory = new File(database.database.getProperty(ConfigurationKey.ROOT_DIRECTORY), "WriterTest");
            final File[] files = directory.listFiles();
            if (files != null) {
                for (final File file : files) {
                    assertTrue(file.getPath(), file.delete());
                }
            }
        }
    }
}
