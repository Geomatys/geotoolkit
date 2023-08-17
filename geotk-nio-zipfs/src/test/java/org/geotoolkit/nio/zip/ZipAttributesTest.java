/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2023, Geomatys
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
package org.geotoolkit.nio.zip;

import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class ZipAttributesTest {

    public ZipAttributesTest() {
    }

    /**
     * Test file attributes method, of class ZipAttributes.
     */
    @Test
    public void testAttributes() throws IOException, URISyntaxException {

        final Path path = Files.createTempFile("fs", ".zip");
        try (InputStream in = ZipFileSystemProviderTest.class.getResourceAsStream("test.zip")) {
            Files.copy(in, path, StandardCopyOption.REPLACE_EXISTING);
        }
        final URI uri = new URI("zip:"+path.toUri().toString());

        try (FileSystem fs = FileSystems.newFileSystem(uri,null)) {
            final Path filePath = fs.getPath("/test/2/3.txt");
            BasicFileAttributes att = fs.provider().readAttributes(filePath, BasicFileAttributes.class);
            assertFalse(att.isDirectory());
            FileTime ft = FileTime.from(1448574150, TimeUnit.MILLISECONDS);
            assertEquals(ft, att.creationTime());
            assertEquals(ft, att.lastAccessTime());
            assertEquals(ft, att.lastModifiedTime());
            assertNull(att.fileKey());
            assertFalse(att.isOther());
            assertTrue(att.isRegularFile());
            assertFalse(att.isSymbolicLink());
            assertEquals(0,att.size());
        } finally {
            Files.deleteIfExists(path);
        }
    }

}
