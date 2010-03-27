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

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import org.geotoolkit.test.Depend;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.io.GridCoverageReader;
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
        table.envelope.setTimeRange(LayerTableTest.SUB_START_TIME, LayerTableTest.SUB_END_TIME);
        table.setLayer(LayerTableTest.TEMPERATURE);
        final GridCoverageReference entry = table.getEntry();

        requireImageData();
        final GridCoverage2D coverage = entry.getCoverage(null);
        assertSame("Coverage shall be cached.", coverage, entry.getCoverage(null));
        checkTemperatureCoverage(coverage);
        /*
         * Tests fetching the loader.
         */
        final GridCoverageReader reader = entry.getReader();
        try {
            reader.setInput(null);
            fail("setInput should not be allowed.");
        } catch (CoverageStoreException e) {
            // This is the expected exception.
        }
        final File input = (File) reader.getInput();
        assertEquals("198602.png", input.getName());
        reader.dispose();
    }

    /**
     * Checks the {@code GridCoverage2D} instance for the temperature sample data.
     */
    static void checkTemperatureCoverage(final GridCoverage2D coverage) {
        final GridSampleDimension[] bands = coverage.getSampleDimensions();
        assertEquals(1, bands.length);
        final GridSampleDimension band = bands[0];
        assertSame("Should be geophysics.", band, band.geophysics(true));
        SampleDimensionTableTest.checkTemperatureDimension(band.geophysics(false));
    }
}
