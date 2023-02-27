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

import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import static org.geotoolkit.nio.zip.ZipFileSystemProviderTest.EMPTY_ZIP;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class ZipPathTest {

    public ZipPathTest() {
    }

    /**
     * Test of getFileSystem method, of class ZipPath.
     */
    @Ignore
    @Test
    public void testGetFileSystem() {
    }

    /**
     * Test of isAbsolute method, of class ZipPath.
     */
    @Ignore
    @Test
    public void testIsAbsolute() {
    }

    /**
     * Test of getRoot method, of class ZipPath.
     */
    @Ignore
    @Test
    public void testGetRoot() {
    }

    /**
     * Test of getFileName method, of class ZipPath.
     */
    @Ignore
    @Test
    public void testGetFileName() {
    }

    /**
     * Test of getParent method, of class ZipPath.
     */
    @Test
    public void testGetParent() throws Exception {

        final Path path = Files.createTempFile("fs", ".zip");
        Files.write(path, EMPTY_ZIP);

        final URI uri = new URI("zip:"+path.toUri().toString());
        try (FileSystem fs = FileSystems.newFileSystem(uri,null)) {

            //absolute paths
            final Path abs1 = fs.getPath("/test.txt");
            assertEquals(uri.toString()+"!/", abs1.getParent().toUri().toString());
            final Path abs2 = fs.getPath("/folder/test.txt");
            assertEquals(uri.toString()+"!/folder/", abs2.getParent().toUri().toString());

            //relative path, no uri available
            final Path rel1 = fs.getPath("folder/test.txt");
            assertEquals("folder/test.txt", rel1.toString());
            assertEquals("folder/", rel1.getParent().toString());
            assertEquals(null, rel1.toUri());
            assertEquals(null, rel1.getParent().toUri());
        } finally {
            Files.deleteIfExists(path);
        }
    }

    /**
     * Test of getNameCount method, of class ZipPath.
     */
    @Ignore
    @Test
    public void testGetNameCount() {
    }

    /**
     * Test of getName method, of class ZipPath.
     */
    @Ignore
    @Test
    public void testGetName() {
    }

    /**
     * Test of subpath method, of class ZipPath.
     */
    @Ignore
    @Test
    public void testSubpath() {
    }

    /**
     * Test of startsWith method, of class ZipPath.
     */
    @Ignore
    @Test
    public void testStartsWith() {
    }

    /**
     * Test of endsWith method, of class ZipPath.
     */
    @Ignore
    @Test
    public void testEndsWith() {
    }

    /**
     * Test of normalize method, of class ZipPath.
     */
    @Ignore
    @Test
    public void testNormalize() {
    }

    /**
     * Test of resolve method, of class ZipPath.
     */
    @Ignore
    @Test
    public void testResolve() {
    }

    /**
     * Test of relativize method, of class ZipPath.
     */
    @Ignore
    @Test
    public void testRelativize() {
    }

    /**
     * Test of toUri method, of class ZipPath.
     */
    @Ignore
    @Test
    public void testToUri() {
    }

    /**
     * Test of toAbsolutePath method, of class ZipPath.
     */
    @Ignore
    @Test
    public void testToAbsolutePath() {
    }

    /**
     * Test of toRealPath method, of class ZipPath.
     */
    @Ignore
    @Test
    public void testToRealPath() throws Exception {
    }

    /**
     * Test of register method, of class ZipPath.
     */
    @Ignore
    @Test
    public void testRegister() throws Exception {
    }

    /**
     * Test of compareTo method, of class ZipPath.
     */
    @Ignore
    @Test
    public void testCompareTo() {
    }

}
