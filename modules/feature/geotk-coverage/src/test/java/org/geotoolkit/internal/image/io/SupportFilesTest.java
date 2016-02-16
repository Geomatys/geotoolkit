/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2012, Geomatys
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
package org.geotoolkit.internal.image.io;

import java.io.File;
import java.io.IOException;

import org.geotoolkit.test.TestData;
import org.geotoolkit.image.io.plugin.TextMatrixImageReaderTest;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests {@link SupportFiles}. This test uses the files available in the resources directory.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.11
 *
 * @since 3.10
 */
public final strictfp class SupportFilesTest extends org.geotoolkit.test.TestBase {
    /**
     * Tests a file for which no TFW file is defined. {@code SupportFiles} should propose as
     * a fallback the {@code "ttw"} extension, which is built from the "first letter + last
     * letter + w" rule.
     *
     * @throws IOException Should not happen.
     */
    @Test
    public void testMissingTFW() throws IOException {
        final File file = TestData.file(TextMatrixImageReaderTest.class, "records.txt");
        assertEquals("ttw", SupportFiles.toSuffixTFW(file));

        Object supportFile = SupportFiles.changeExtension(file, "prj");
        assertTrue("Expected a File object.", supportFile instanceof File);
        assertEquals("records.prj", ((File) supportFile).getName());

        supportFile = SupportFiles.changeExtension(file, "tfw");
        assertTrue("Expected a File object.", supportFile instanceof File);
        assertEquals("records.ttw", ((File) supportFile).getName());
    }

    /**
     * Tests a file for which a TFW file exists. This case forces {@code SupportFiles} to test
     * almost every cases, because {@code "tfw"} is almost the last case. If the file was not
     * found, we would get {@code "ttw"} instead than {@code "tfw"}.
     *
     * @throws IOException Should not happen.
     */
    @Test
    public void testExistingTFW() throws IOException {
        final File file = TestData.file(TextMatrixImageReaderTest.class, "matrix.txt");
        assertEquals("ttw", SupportFiles.toSuffixTFW(file));

        Object supportFile = SupportFiles.changeExtension(file, "prj");
        assertTrue("Expected a File object.", supportFile instanceof File);
        assertEquals("matrix.prj", ((File) supportFile).getName());

        supportFile = SupportFiles.changeExtension(file, "tfw");
        assertTrue("Expected a File object.", supportFile instanceof File);
        assertEquals("matrix.tfw", ((File) supportFile).getName());
    }

    /**
     * Tests for a {@code ".tif"} file. The file does not exist, but it should not be
     * a concern for this test.
     *
     * @throws IOException Should not happen.
     *
     * @since 3.11
     */
    @Test
    public void testMissingTIFF() throws IOException {
        File file = TestData.file(TextMatrixImageReaderTest.class, "matrix.txt");
        file = new File(file.getParentFile(), "imagery.tif");
        assertEquals("imagery.tfw", ((File) SupportFiles.changeExtension(file, "tfw")).getName());
    }
}
