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

import java.io.InputStream;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.spi.FileSystemProvider;
import java.util.Iterator;
import java.util.Optional;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class ZipFileSystemProviderTest {

    public static final byte[] EMPTY_ZIP = new byte[]{80,75,05,06,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00};

    public ZipFileSystemProviderTest() {
    }

    /**
     * Test of getScheme method, of class ZipFileSystemProvider.
     */
    @Test
    public void testGetScheme() {

        //test provider is registered
        final Optional<FileSystemProvider> opt = FileSystemProvider.installedProviders().stream()
                .filter(ZipFileSystemProvider.class::isInstance).findAny();
        assertTrue(opt.isPresent());

        assertEquals("zip", opt.get().getScheme());
    }

    /**
     * Test of newFileSystem method, of class ZipFileSystemProvider.
     */
    @Test
    public void testNewFileSystem() throws Exception {

        final Path path = Files.createTempFile("fs", ".zip");
        try (FileSystem fs = FileSystems.newFileSystem(new URI("zip:"+path.toUri().toString()), null)) {
            assertNotNull(fs);
            assertEquals("zip", fs.provider().getScheme());
        } finally {
            Files.deleteIfExists(path);
        }
    }

    /**
     * Test of getFileSystem method, of class ZipFileSystemProvider.
     */
    @Test
    public void testGetFileSystem() throws Exception {

        final Path path = Files.createTempFile("fs", ".zip");
        final URI uri = new URI("zip:"+path.toUri().toString());
        try (FileSystem fs = FileSystems.newFileSystem(uri,null)) {

            FileSystem cdt = FileSystems.getFileSystem(uri);
            assertTrue(fs == cdt);
        } finally {
            Files.deleteIfExists(path);
        }

        try {
            //should not available after closing
            FileSystem cdt = FileSystems.getFileSystem(uri);
            fail("should not available after closing");
        } catch (FileSystemNotFoundException ex) {
            //ok
        }
    }

    /**
     * Test of getPath method, of class ZipFileSystemProvider.
     */
    @Test
    public void testGetPath() throws Exception {

        final Path path = Files.createTempFile("fs", ".zip");
        final URI uri = new URI("zip:"+path.toUri().toString());
        try (FileSystem fs = FileSystems.newFileSystem(uri,null)) {

            final Path subPath1 = fs.getPath("/test.txt");
            assertEquals(uri.toString()+"!/test.txt", subPath1.toUri().toString());

            final Path subPath2 = fs.getPath("/folder1", "folder2", "test.txt");
            assertEquals(uri.toString()+"!/folder1/folder2/test.txt", subPath2.toUri().toString());
        } finally {
            Files.deleteIfExists(path);
        }

    }

    /**
     * Test of newByteChannel method, of class ZipFileSystemProvider.
     */
    @Test
    public void testNewByteChannel() throws Exception {

        final Path path = Files.createTempFile("fs", ".zip");
        Files.write(path, EMPTY_ZIP);

        final URI uri = new URI("zip:"+path.toUri().toString());
        try (FileSystem fs = FileSystems.newFileSystem(uri,null)) {

            final Path subPath1 = fs.getPath("/test.txt");

            //test writing
            try (SeekableByteChannel channel = Files.newByteChannel(subPath1, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE)) {
                channel.write(ByteBuffer.wrap(new byte[]{1,2,3,4,5,6,7,8,9}));
            }

            //test reading
            try (SeekableByteChannel channel = Files.newByteChannel(subPath1, StandardOpenOption.READ)) {
                final ByteBuffer buffer = ByteBuffer.wrap(new byte[12]);
                buffer.position(0);
                int nb = channel.read(buffer);
                assertEquals(9, nb);
                assertArrayEquals(new byte[]{1,2,3,4,5,6,7,8,9,0,0,0}, buffer.array());
                assertEquals(-1, channel.read(buffer));
            }

        } finally {
            Files.deleteIfExists(path);
        }
    }

    /**
     * Test of newDirectoryStream method, of class ZipFileSystemProvider.
     */
    @Test
    public void testNewDirectoryStream() throws Exception {

        final Path path = Files.createTempFile("fs", ".zip");
        try (InputStream in = ZipFileSystemProviderTest.class.getResourceAsStream("test.zip")) {
            Files.copy(in, path, StandardCopyOption.REPLACE_EXISTING);
        }

        final URI uri = new URI("zip:"+path.toUri().toString());
        try (FileSystem fs = FileSystems.newFileSystem(uri,null)) {

            final Path path1 = fs.getPath("/test/");
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(path1)) {
                final Iterator<Path> iterator = stream.iterator();
                final Path p1 = iterator.next();
                final Path p2 = iterator.next();
                final Path p3 = iterator.next();
                assertFalse(iterator.hasNext());
                assertEquals("1", p1.getFileName().toString());
                assertEquals("2", p2.getFileName().toString());
                assertEquals("3", p3.getFileName().toString());
                assertTrue(Files.isDirectory(p1));
                assertTrue(Files.isDirectory(p2));
                assertTrue(Files.isDirectory(p3));
            }

            final Path path2 = fs.getPath("/test", "2/");
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(path2)) {
                final Iterator<Path> iterator = stream.iterator();
                final Path p1 = iterator.next();
                final Path p2 = iterator.next();
                final Path p3 = iterator.next();
                assertFalse(iterator.hasNext());
                assertEquals("1.txt", p1.getFileName().toString());
                assertEquals("2.txt", p2.getFileName().toString());
                assertEquals("3.txt", p3.getFileName().toString());
                assertFalse(Files.isDirectory(p1));
                assertFalse(Files.isDirectory(p2));
                assertFalse(Files.isDirectory(p3));
            }

        } finally {
            Files.deleteIfExists(path);
        }
    }

    /**
     * Test of createDirectory method, of class ZipFileSystemProvider.
     */
    @Test
    public void testCreateDirectory() throws Exception {

        final Path path = Files.createTempFile("fs", ".zip");
        try (InputStream in = ZipFileSystemProviderTest.class.getResourceAsStream("test.zip")) {
            Files.copy(in, path, StandardCopyOption.REPLACE_EXISTING);
        }

        final URI uri = new URI("zip:"+path.toUri().toString());
        try (FileSystem fs = FileSystems.newFileSystem(uri,null)) {

            final Path path1 = fs.getPath("/test/");
            final Path path2 = path1.resolve("temp/");
            Files.createDirectory(path2);
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(path1)) {
                final Iterator<Path> iterator = stream.iterator();
                final Path p1 = iterator.next();
                final Path p2 = iterator.next();
                final Path p3 = iterator.next();
                final Path p4 = iterator.next();
                assertFalse(iterator.hasNext());
                assertEquals("1", p1.getFileName().toString());
                assertEquals("2", p2.getFileName().toString());
                assertEquals("3", p3.getFileName().toString());
                assertEquals("temp", p4.getFileName().toString());
                assertTrue(Files.isDirectory(p1));
                assertTrue(Files.isDirectory(p2));
                assertTrue(Files.isDirectory(p3));
                assertTrue(Files.isDirectory(p4));
            }

        } finally {
            Files.deleteIfExists(path);
        }
    }

    /**
     * Test of delete method, of class ZipFileSystemProvider.
     */
    @Test
    public void testDelete() throws Exception {

        final Path path = Files.createTempFile("fs", ".zip");
        try (InputStream in = ZipFileSystemProviderTest.class.getResourceAsStream("test.zip")) {
            Files.copy(in, path, StandardCopyOption.REPLACE_EXISTING);
        }

        final URI uri = new URI("zip:"+path.toUri().toString());
        try (FileSystem fs = FileSystems.newFileSystem(uri,null)) {

            assertTrue(Files.deleteIfExists(fs.getPath("/test/2/3.txt")));

            final Path path2 = fs.getPath("/test", "2/");
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(path2)) {
                final Iterator<Path> iterator = stream.iterator();
                final Path p1 = iterator.next();
                final Path p2 = iterator.next();
                assertFalse(iterator.hasNext());
                assertEquals("1.txt", p1.getFileName().toString());
                assertEquals("2.txt", p2.getFileName().toString());
                assertFalse(Files.isDirectory(p1));
                assertFalse(Files.isDirectory(p2));
            }

            assertFalse(Files.deleteIfExists(fs.getPath("/test/2/4.txt")));

        } finally {
            Files.deleteIfExists(path);
        }
    }

    /**
     * Test of copy method, of class ZipFileSystemProvider.
     */
    @Test
    public void testCopy() throws Exception {

        final Path path = Files.createTempFile("fs", ".zip");
        try (InputStream in = ZipFileSystemProviderTest.class.getResourceAsStream("test.zip")) {
            Files.copy(in, path, StandardCopyOption.REPLACE_EXISTING);
        }

        final URI uri = new URI("zip:"+path.toUri().toString());
        try (FileSystem fs = FileSystems.newFileSystem(uri,null)) {

            Files.copy(fs.getPath("/test/2/3.txt"), fs.getPath("/test/2/5.txt"));

            final Path path2 = fs.getPath("/test", "2/");
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(path2)) {
                final Iterator<Path> iterator = stream.iterator();
                final Path p1 = iterator.next();
                final Path p2 = iterator.next();
                final Path p3 = iterator.next();
                final Path p4 = iterator.next();
                assertFalse(iterator.hasNext());
                assertEquals("1.txt", p1.getFileName().toString());
                assertEquals("2.txt", p2.getFileName().toString());
                assertEquals("3.txt", p3.getFileName().toString());
                assertEquals("5.txt", p4.getFileName().toString());
                assertFalse(Files.isDirectory(p1));
                assertFalse(Files.isDirectory(p2));
                assertFalse(Files.isDirectory(p3));
                assertFalse(Files.isDirectory(p4));
            }

        } finally {
            Files.deleteIfExists(path);
        }
    }

    /**
     * Test of move method, of class ZipFileSystemProvider.
     */
    @Test
    public void testMove() throws Exception {

        final Path path = Files.createTempFile("fs", ".zip");
        try (InputStream in = ZipFileSystemProviderTest.class.getResourceAsStream("test.zip")) {
            Files.copy(in, path, StandardCopyOption.REPLACE_EXISTING);
        }

        final URI uri = new URI("zip:"+path.toUri().toString());
        try (FileSystem fs = FileSystems.newFileSystem(uri,null)) {

            Files.move(fs.getPath("/test/2/3.txt"), fs.getPath("/test/2/5.txt"));

            final Path path2 = fs.getPath("/test", "2/");
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(path2)) {
                final Iterator<Path> iterator = stream.iterator();
                final Path p1 = iterator.next();
                final Path p2 = iterator.next();
                final Path p3 = iterator.next();
                assertFalse(iterator.hasNext());
                assertEquals("1.txt", p1.getFileName().toString());
                assertEquals("2.txt", p2.getFileName().toString());
                assertEquals("5.txt", p3.getFileName().toString());
                assertFalse(Files.isDirectory(p1));
                assertFalse(Files.isDirectory(p2));
                assertFalse(Files.isDirectory(p3));
            }

        } finally {
            Files.deleteIfExists(path);
        }
    }

    /**
     * Test of isSameFile method, of class ZipFileSystemProvider.
     */
    @Ignore
    @Test
    public void testIsSameFile() throws Exception {
    }

    /**
     * Test of isHidden method, of class ZipFileSystemProvider.
     */
    @Ignore
    @Test
    public void testIsHidden() throws Exception {
    }

    /**
     * Test of getFileStore method, of class ZipFileSystemProvider.
     */
    @Ignore
    @Test
    public void testGetFileStore() throws Exception {
    }

    /**
     * Test of checkAccess method, of class ZipFileSystemProvider.
     */
    @Ignore
    @Test
    public void testCheckAccess() throws Exception {
    }

    /**
     * Test of getFileAttributeView method, of class ZipFileSystemProvider.
     */
    @Ignore
    @Test
    public void testGetFileAttributeView() {
    }

    /**
     * Test of readAttributes method, of class ZipFileSystemProvider.
     */
    @Ignore
    @Test
    public void testReadAttributes1() throws Exception {
    }

    /**
     * Test of readAttributes method, of class ZipFileSystemProvider.
     */
    @Ignore
    @Test
    public void testReadAttributes2() throws Exception {
    }

    /**
     * Test of setAttribute method, of class ZipFileSystemProvider.
     */
    @Ignore
    @Test
    public void testSetAttribute() throws Exception {
    }

}
