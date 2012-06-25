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
package org.geotoolkit.image.io.plugin;

import java.util.Iterator;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;

import org.opengis.test.coverage.image.ImageWriterTestCase;

import org.geotoolkit.image.io.DimensionSlice;
import org.geotoolkit.internal.io.TemporaryFile;
import org.junit.*;
import static org.geotoolkit.test.Assert.*;


/**
 * Test the {@link NetcdfImageWriter} implementation.
 *
 * @author Johann Sorel (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.20
 */
public final strictfp class NetcdfImageWriterTest extends ImageWriterTestCase {
    /**
     * The temporary file created by the test, or {@code null} if none.
     * This file will be deleted after each test execution.
     */
    private File temporaryFile;

    /**
     * Creates a new {@link NetcdfImageWriter} instance and set its output to a temporary file.
     * The temporary file will be overwritten every time this method is invoked - so only one
     * file shall be tested during each test execution.
     */
    @Override
    protected void prepareImageWriter(final boolean optionallySetOutput) throws IOException {
        if (writer == null) {
            writer = new NetcdfImageWriter(null);
        }
        if (optionallySetOutput) {
            if (temporaryFile == null) {
                temporaryFile = TemporaryFile.createTempFile("geotk", ".nc", null);
            }
            writer.setOutput(temporaryFile);
            // The reader is usually not the purpose of this method. However in the NetCDF case,
            // we have to associate the band API to the third dimension in order to be able to
            // read back the image we will write.
            if (reader == null) {
                reader = ImageIO.getImageReader(writer);
                ((NetcdfImageReader) reader).getDimensionForAPI(DimensionSlice.API.BANDS).addDimensionId(2);
            }
        }
    }

    /**
     * Tests the registration of the image writer in the Image I/O framework.
     */
    @Test
    public void testRegistrationByFormatName() {
        final Iterator<ImageWriter> it = ImageIO.getImageWritersByFormatName("NetCDF");
        assertTrue("Expected a writer.", it.hasNext());
        assertTrue(it.next() instanceof NetcdfImageWriter);
        assertFalse("Expected no more writer.", it.hasNext());
    }

    /**
     * Tests the registration by MIME type.
     * Note that more than one writer may be registered.
     */
    @Test
    public void testRegistrationByMIMEType() {
        final Iterator<ImageWriter> it = ImageIO.getImageWritersByMIMEType("application/netcdf");
        while (it.hasNext()) {
            if (it.next() instanceof NetcdfImageWriter) {
                return;
            }
        }
        fail("Writer not found.");
    }

    /**
     * Ignored for now.
     */
    @Override
    @Ignore("Seems to be handled as an unsigned type rather than a signed type.")
    public void testOneShortBand() {
    }

    /**
     * Disposes the writer and deletes the temporary file (if any).
     * This method is invoked automatically by JUnit after each test.
     *
     * @throws IOException In an error occurred while closing the output stream.
     */
    @After
    @Override
    public void close() throws IOException {
        super.close();
        if (temporaryFile != null) {
            assertTrue(TemporaryFile.delete(temporaryFile));
            temporaryFile = null;
        }
    }
}
