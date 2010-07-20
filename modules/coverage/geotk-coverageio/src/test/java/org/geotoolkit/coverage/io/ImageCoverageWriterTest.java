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
package org.geotoolkit.coverage.io;

import java.io.IOException;
import java.io.StringWriter;

import org.geotoolkit.coverage.grid.GridCoverage2D;

import org.geotoolkit.test.Depend;
import org.geotoolkit.test.TestData;
import org.geotoolkit.image.io.plugin.TextMatrixImageReaderTest;

import org.junit.*;


/**
 * Tests {@link ImageCoverageWriter}. This test will read the {@code "matrix.txt"} file
 * defined in the {@code org/geotoolkit/image/io/plugin/test-data} directory because it
 * is the easiest one to debug.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.14
 *
 * @since 3.14
 */
@Depend(ImageCoverageReaderTest.class)
public class ImageCoverageWriterTest {
    /**
     * Registers a "matrix" reader forced to the US format.
     */
    @BeforeClass
    public static void registerReaderUS() {
        ImageCoverageReaderTest.registerReaderUS();
    }

    /**
     * Deregisters the reader defined by {@link #registerReaderUS()}.
     */
    @AfterClass
    public static void deregisterReaderUS() {
        ImageCoverageReaderTest.deregisterReaderUS();
    }

    /**
     * Creates a {@link GridCoverage2D} from the given file.
     */
    private static GridCoverage2D read(final String file) throws IOException, CoverageStoreException {
        final ImageCoverageReader reader = new ImageCoverageReader();
        reader.setInput(TestData.file(TextMatrixImageReaderTest.class, file));
        final GridCoverage2D coverage = reader.read(0, null);
        reader.dispose();
        return coverage;
    }

    /**
     * Writes the full image.
     *
     * @throws IOException If the text file can not be open (should not happen).
     * @throws CoverageStoreException Should not happen.
     */
    @Test
    public void writeFull() throws IOException, CoverageStoreException {
        final GridCoverage2D coverage = read("matrix.txt");
        final ImageCoverageWriter writer = new ImageCoverageWriter();
        final StringWriter buffer = new StringWriter();
        // TODO writer.write(coverage, null);
    }
}
