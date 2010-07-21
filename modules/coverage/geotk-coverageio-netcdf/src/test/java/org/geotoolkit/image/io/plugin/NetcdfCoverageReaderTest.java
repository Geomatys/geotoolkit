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
package org.geotoolkit.image.io.plugin;

import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.junit.*;

import org.geotoolkit.coverage.io.ImageCoverageReader;
import org.geotoolkit.coverage.io.CoverageStoreException;

import static org.junit.Assert.*;


/**
 * Tests {@link NetcdfImageReader} wrapped in a {@link ImageCoverageReader}.
 *
 * @todo Partially disabled for now because Coriolis has irregular latitude axis.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.11
 *
 * @since 3.11
 */
public final class NetcdfCoverageReaderTest extends NetcdfTestBase {
    /**
     * Tests a {@link ImageCoverageReader#read} operation.
     *
     * @throws CoverageStoreException If an error occurred while reading the NetCDF file.
     */
    @Test
    public void testRead() throws CoverageStoreException {
        final ImageCoverageReader reader = new ImageCoverageReader();
        reader.setInput(getTestFile());
        assertArrayEquals(new String[] {"temperature", "pct_variance"}, reader.getCoverageNames().toArray());
        if (false) {
            // TODO: enable this test when we will able to process irregular axis.
            final GridCoverage2D coverage = reader.read(0, null);
            assertNotNull(coverage);
        }
        reader.dispose();
    }
}
