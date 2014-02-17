/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
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
package org.geotoolkit.util;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.Adler32;
import java.util.zip.Checksum;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Samuel Andrés
 */
public class ZipUtilitiesTest {

    private final static Checksum CHECKSUM = new Adler32();

    public ZipUtilitiesTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void fileTestStored() throws IOException {

        File file1 = File.createTempFile("file1", ".txt");
        File archive = File.createTempFile("archive", ".zip");
        file1.deleteOnExit();
        archive.deleteOnExit();

        FileUtilities.zip(archive, CHECKSUM, file1);
        FileUtilities.unzip(archive, CHECKSUM);

        List<String> zipContent = listContent(archive);
        assertEquals(zipContent.get(0), file1.getName());
    }

    @Test
    public void fileTest() throws IOException {

        File file1 = File.createTempFile("file1", ".txt");
        File archive = File.createTempFile("archive", ".zip");
        file1.deleteOnExit();
        archive.deleteOnExit();

        FileUtilities.zip(archive, ZipOutputStream.DEFLATED, 9, CHECKSUM, file1);
        FileUtilities.unzip(archive, CHECKSUM);

        List<String> zipContent = listContent(archive);
        assertEquals(zipContent.get(0), file1.getName());
    }

    @Test
    public void directoryTest() throws IOException {

        final File dir = File.createTempFile("directory", null);
        dir.delete();
        dir.mkdir();
        dir.deleteOnExit();
        final File file1 = File.createTempFile("file1", ".txt", dir);
        file1.deleteOnExit();
        final File file2 = File.createTempFile("file2", ".txt", dir);
        file2.deleteOnExit();
        final File dir2 = new File(dir, "directory2");
        dir2.mkdir();
        dir2.deleteOnExit();
        final File file3 = File.createTempFile("file3", ".txt", dir2);
        file3.deleteOnExit();
        final File archive = File.createTempFile("archive", ".zip");
        archive.deleteOnExit();

        FileUtilities.zip(archive, ZipOutputStream.DEFLATED, 9, CHECKSUM, dir);
        final File extract = File.createTempFile("extract", null);
        extract.delete();

        FileUtilities.unzip(archive, extract, CHECKSUM);

        final List<String> files = new ArrayList<String>();
        for (String file : extract.list()) {
            files.add(file);
        }

        // Checking extracted folder location.
        assertTrue(files.contains(dir.getName()));

        // Checking dir content
        files.clear();
        File currentFile = new File(extract, dir.getName());
        assertEquals(dir.listFiles().length, currentFile.listFiles().length);
        for (String file : currentFile.list()) {
            files.add(file);
        }

        for (String element : dir.list()) {
            assertTrue(files.contains(element));
        }

        // Checking dir2 content.
        files.clear();
        currentFile = new File(currentFile, dir2.getName());
        assertEquals(dir2.listFiles().length, currentFile.listFiles().length);
        for (String file : currentFile.list()) {
            files.add(file);
        }

        for (String element : dir2.list()) {
            assertTrue(files.contains(element));
        }

        org.geotoolkit.util.FileUtilities.deleteDirectory(extract);
    }

    @Test
    public void stringTest() throws IOException {

        File file1 = File.createTempFile("file1", ".txt");
        File archive = File.createTempFile("archive", ".zip");
        file1.deleteOnExit();
        archive.deleteOnExit();

        String file1Path = file1.getAbsolutePath();
        String archivePath = archive.getAbsolutePath();

        FileUtilities.zip(archivePath, ZipOutputStream.DEFLATED, 9, CHECKSUM, file1Path);
        FileUtilities.unzip(archivePath, CHECKSUM);

        List<String> zipContent = listContent(archive);
        assertEquals(zipContent.get(0), file1.getName());
    }

    @Test
    public void urlTest() throws IOException {

        File file1 = File.createTempFile("file1", ".txt");
        File archive = File.createTempFile("archive", ".zip");
        file1.deleteOnExit();
        archive.deleteOnExit();

        URL url1 = new URL("file://" + file1.getAbsolutePath());
        URL urlArchive = new URL("file://" + archive.getAbsolutePath());

        FileUtilities.zip(archive, ZipOutputStream.DEFLATED, 9, CHECKSUM, url1);
        FileUtilities.unzip(urlArchive, CHECKSUM);

        List<String> zipContent = listContent(archive);
        assertEquals(zipContent.get(0), file1.getName());
    }

    @Test
    public void uriTest() throws IOException {

        File file1 = File.createTempFile("file1", ".txt");
        File archive = File.createTempFile("archive", ".zip");
        file1.deleteOnExit();
        archive.deleteOnExit();

        URI uri1 = URI.create("file://" + file1.getAbsolutePath());
        URI uriArchive = URI.create("file://" + archive.getAbsolutePath());

        FileUtilities.zip(archive, ZipOutputStream.DEFLATED, 9, CHECKSUM, uri1);
        FileUtilities.unzip(uriArchive, CHECKSUM);

        List<String> zipContent = listContent(archive);
        assertEquals(zipContent.get(0), file1.getName());
    }

    /**
     * <p>This method lists the content of indicated archive.</p>
     *
     * @param archive Instance of ZipFile, File or String path
     * @return a list of archive entries.
     * @throws IOException
     */
    public static List<String> listContent(final Object archive)
            throws IOException {

        final List<String> out = new ArrayList<String>();
        ZipFile zf = null;

        if (archive instanceof ZipFile) {
            zf = (ZipFile) archive;
        } else if (archive instanceof File) {
            zf = new ZipFile((File) archive);
        } else if (archive instanceof String) {
            zf = new ZipFile((String) archive);
        }

        Enumeration entries = zf.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = (ZipEntry) entries.nextElement();
            out.add(entry.getName());
        }
        return out;
    }
}
