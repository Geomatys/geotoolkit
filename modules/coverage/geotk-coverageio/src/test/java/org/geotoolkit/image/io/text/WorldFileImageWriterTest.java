/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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
package org.geotoolkit.image.io.text;

import java.util.Locale;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import javax.imageio.IIOImage;

import org.geotoolkit.test.TestData;
import org.geotoolkit.internal.io.IOUtilities;
import org.geotoolkit.internal.io.TemporaryFile;

import org.junit.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;
import static org.geotoolkit.test.Commons.assertMultilinesEquals;


/**
 * Tests {@link WorldFileImageWriter}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.07
 *
 * @since 3.07
 */
public final class WorldFileImageWriterTest extends TextImageWriterTestBase {
    /**
     * Creates a writer.
     */
    @Override
    protected WorldFileImageWriter createImageWriter() throws IOException {
        final TextMatrixImageWriter.Spi main = new TextMatrixImageWriter.Spi();
        main.locale  = Locale.CANADA;
        main.charset = Charset.forName("UTF-8");
        final WorldFileImageWriter.Spi spi = new WorldFileImageWriter.Spi(main);
        final WorldFileImageWriter writer = new WorldFileImageWriter(spi);
        return writer;
    }

    /**
     * Returns a new file with the same path than the given file but a different extension,
     * and ensure that this file does not exist. If a file with that name exists, the test
     * will be stopped but is will not be considered a failure.
     */
    private static File changeExtension(final File mainFile, final String extension) throws IOException {
        final File file = (File) IOUtilities.changeExtension(mainFile, extension);
        assumeTrue(file.createNewFile());
        return file;
    }

    /**
     * Tests the write operation.
     *
     * @throws IOException Should never happen.
     */
    @Test
    public void testWrite() throws IOException {
        final IIOImage image = createImage(true);
        final WorldFileImageWriter writer = createImageWriter();
        final File file = TemporaryFile.createTempFile("TEST", ".txt", null);
        File fileTFW = null;
        try {
            fileTFW = changeExtension(file, "ttw");
            writer.setOutput(file);
            writer.write(image);
            assertTrue("The main file should contains data.", file   .length() > 0);
            assertTrue("The TFW file should contains data.",  fileTFW.length() > 0);
            assertMultilinesEquals(
                "100.0\n" +
                "0.0\n" +
                "0.0\n" +
                "-100.0\n" +
                "-500.0\n" +
                "400.0\n", TestData.readLatinText(fileTFW));
        } finally {
            TemporaryFile.delete(file);
            if (fileTFW != null) {
                assertTrue(fileTFW.delete());
            }
        }
    }
}
