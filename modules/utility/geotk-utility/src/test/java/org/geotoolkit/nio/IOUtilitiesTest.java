/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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
package org.geotoolkit.nio;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.*;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests the {@link IOUtilities} class.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @author Johann Sorel (Geomatys)
 * @version 3.20
 *
 * @since 3.00
 */
public final strictfp class IOUtilitiesTest {
    /**
     * Tests the {@link IOUtilities#commonParent} method.
     */
    @Test
    public void testCommonParent() {
        final File root  = new File("/home/root/subdirectory");
        final File other = new File("/home/root/data/other");
        assertEquals(new File("/home/root").toPath(), IOUtilities.commonParent(root.toPath(), other.toPath()));
    }

    /**
     * Tests the {@link IOUtilities#changeExtension} method.
     *
     * @throws IOException Should not happen.
     *
     * @since 3.07
     */
    @Test
    public void testChangeExtension() throws IOException {
        assertEquals("Picture.tiff",
                IOUtilities.changeExtension("Picture.png", "tiff"));
        assertEquals("Users/name/Picture.tiff",
                IOUtilities.changeExtension("Users/name/Picture.png", "tiff"));
        assertEquals(new File("Picture.tiff"),
                IOUtilities.changeExtension(new File("Picture.png"), "tiff"));
        assertEquals(new File("Users/name/Picture.tiff"),
                IOUtilities.changeExtension(new File("Users/name/Picture.png"), "tiff"));

        final File file = new File("Users/name/Picture.png");
        assertSame(file.toPath(), IOUtilities.changeExtension(file.toPath(), "png"));
    }

    @Test
    public void testFileParsing() throws IOException {
        final Path tiffFile = Paths.get("Users/name/Picture.tiff");
        final Path pngFile = Paths.get("image.png");
        final Path png2File = Paths.get("image.test.png");

        assertEquals("tiff",  org.apache.sis.internal.storage.IOUtilities.extension(tiffFile));
        assertEquals("png",  org.apache.sis.internal.storage.IOUtilities.extension(pngFile));
        assertEquals("png",  org.apache.sis.internal.storage.IOUtilities.extension(png2File));

        assertEquals("Picture", IOUtilities.filenameWithoutExtension(tiffFile));
        assertEquals("image", IOUtilities.filenameWithoutExtension(pngFile));
        assertEquals("image.test", IOUtilities.filenameWithoutExtension(png2File));
    }

    @Test
    public void testFileRW() throws IOException {
        final Path file = Files.createTempFile("geotk", null);

        StringBuilder sb = new StringBuilder();
        sb.append("Some content String line 1").append('\n');
        sb.append("Some content String line 2").append('\n');
        sb.append("Some content String line 3").append('\n');

        //write testing
        IOUtilities.writeString(sb.toString(), file);
        assertTrue(Files.exists(file));

        final List<String> lines = Files.readAllLines(file, Charset.forName("UTF-8"));
        assertEquals(3, lines.size());
        assertEquals("Some content String line 1", lines.get(0));
        assertEquals("Some content String line 2", lines.get(1));
        assertEquals("Some content String line 3", lines.get(2));

        //read testing
        //from path
        final String readFile = IOUtilities.toString(file);
        assertEquals(sb.toString(), readFile);

        //from stream
        final String readStream = IOUtilities.toString(Files.newInputStream(file));
        assertEquals(sb.toString(), readStream);
    }


    @Test
    public void testPropertiesRW() throws IOException {
        final Path file = Files.createTempFile("geotk", ".properties");

        Properties props = new Properties();
        props.put("Key1", "value1");
        props.put("Key2", "2");
        props.put("Key3", "true");

        //write testing
        IOUtilities.storeProperties(props, file, "");
        assertTrue(Files.exists(file));

        //read testing
        final List<String> lines = Files.readAllLines(file, Charset.forName("UTF-8"));
        assertEquals(5, lines.size()); // properties plus 2 comments lines

        final Properties readProps = IOUtilities.getPropertiesFromFile(file);
        for (Object key : props.keySet()) {
            assertEquals(props.get(key), readProps.get(key));
        }
    }

    @Test
    public void emptyFileTest() throws IOException {
        final Path file = Files.createTempFile("geotk", null);

        StringBuilder sb = new StringBuilder();
        sb.append("Some content String line 1").append('\n');
        sb.append("Some content String line 2").append('\n');
        sb.append("Some content String line 3").append('\n');

        IOUtilities.writeString(sb.toString(), file);
        assertTrue(Files.exists(file));
        assertEquals(3, Files.readAllLines(file, Charset.forName("UTF-8")).size());

        IOUtilities.emptyFile(file);
        assertTrue(Files.exists(file));
        assertEquals(0, Files.readAllLines(file, Charset.forName("UTF-8")).size());

        //not existing file
        Files.delete(file);
        assertFalse(Files.exists(file));
        IOUtilities.emptyFile(file);
        assertTrue(Files.exists(file));
    }

    @Test
    public void appendFileTest() throws IOException {
        final Path file = Files.createTempFile("geotk", null);

        IOUtilities.writeString("Some content String line 1", file);
        assertTrue(Files.exists(file));
        assertEquals(1, Files.readAllLines(file, Charset.forName("UTF-8")).size());

        IOUtilities.appendToFile("Some content String line 2", file);
        assertEquals(2, Files.readAllLines(file, Charset.forName("UTF-8")).size());

        IOUtilities.appendToFile("Some content String line 3", file);
        final List<String> lines = Files.readAllLines(file, Charset.forName("UTF-8"));
        assertEquals(3, lines.size());
        assertEquals("Some content String line 1", lines.get(0));
        assertEquals("Some content String line 2", lines.get(1));
        assertEquals("Some content String line 3", lines.get(2));
    }

    @Test
    public void canProcessAsPathTest() throws IOException {
        //valid candidates
        assertTrue(IOUtilities.canProcessAsPath(Paths.get("./somePath"))); //from path
        assertTrue(IOUtilities.canProcessAsPath(new File("myFile")));  //from File
        assertTrue(IOUtilities.canProcessAsPath(URI.create("file:/tmp"))); // from URI
        assertTrue(IOUtilities.canProcessAsPath(new URL("file:/tmp"))); //from URL

        //invalid candidates
        assertFalse(IOUtilities.canProcessAsPath("/tmp")); //from Sting
        assertFalse(IOUtilities.canProcessAsPath(1)); //from int
    }

    @Test
    public void toPathTest() throws IOException {
        Path pathObj = Files.createTempFile("path", ".tmp");
        File fileObj = File.createTempFile("file", ".tmp");
        URI uriObj = URI.create("file:/tmp");
        URL urlFileObj = new URL("file:/tmp");

        try {
            //convert to Path
            assertNull(IOUtilities.toPath(null));
            assertSame(pathObj, IOUtilities.toPath(pathObj)); //should be same reference
            assertEquals(fileObj.toPath(), IOUtilities.toPath(fileObj));
            assertEquals(Paths.get(uriObj), IOUtilities.toPath(uriObj));
            assertEquals(Paths.get(uriObj), IOUtilities.toPath(urlFileObj));

            //valid string Object
            assertEquals(Paths.get("/tmp"), IOUtilities.toPath("/tmp"));
            assertEquals(Paths.get("/tmp"), IOUtilities.toPath("file:/tmp"));
            assertEquals(Paths.get("/tmp my file"), IOUtilities.toPath("file:/tmp my file"));
            assertEquals(Paths.get("./tmp"), IOUtilities.toPath("./tmp"));
            assertEquals(Paths.get("./tmp my file"), IOUtilities.toPath("./tmp my file"));

            try {
                IOUtilities.toPath("http://some/url");
                fail("Should raise FileSystemNotFoundException because http FileSystem is not supported by default");
            } catch (FileSystemNotFoundException e) {
                //normal exception
            }

            try {
                IOUtilities.toPath(new URL("http://some/url"));
                fail("Should raise FileSystemNotFoundException because http FileSystem is not supported by default");
            } catch (IOException e) {
                //normal exception
            }

            try {
                IOUtilities.toPath(1);
                fail("Should raise IllegalArgumentException");
            } catch (IllegalArgumentException e) {
                //normal exception
            }
        } finally {
            //clean
            Files.delete(pathObj);
            fileObj.delete();
        }
    }

    @Test
    public void tryToPathTest() throws IOException {
        Path pathObj = Files.createTempFile("path", ".tmp");
        File fileObj = File.createTempFile("file", ".tmp");
        URI uriObj = URI.create("file:/tmp");
        URL urlFileObj = new URL("file:/tmp");
        URL urlObj = new URL("http://some/url");
        String strObj = "/tmp";

        try {
            //using try
            assertNull(IOUtilities.tryToPath(null));
            assertSame(pathObj, IOUtilities.tryToPath(pathObj)); //must be same object
            assertEquals(fileObj.toPath(), IOUtilities.tryToPath(fileObj));
            assertEquals(Paths.get(uriObj), IOUtilities.tryToPath(uriObj));
            assertEquals(Paths.get(uriObj), IOUtilities.tryToPath(urlFileObj));
            assertEquals(Paths.get(strObj), IOUtilities.tryToPath(strObj));
            assertEquals(urlObj, IOUtilities.tryToPath(urlObj));// not converted because FS not supported
            assertEquals(1, IOUtilities.tryToPath(1)); // not converted
        } finally {
            //clean
            Files.delete(pathObj);
            fileObj.delete();
        }
    }

    @Test
    public void tryToFileTest() throws IOException, URISyntaxException {

        Path pathObj = Files.createTempFile("path", ".tmp");
        File fileObj = File.createTempFile("file", ".tmp");
        URI uriObj = URI.create("file:/tmp");
        URL urlFileObj = new URL("file:/tmp");
        URL urlObj = new URL("http://some/url");
        String strObj = "/tmp";
        Integer intObj = 1;

        //try file conversion
        assertNull(IOUtilities.tryToFile(null));
        assertEquals(pathObj.toFile(), IOUtilities.tryToFile(pathObj));
        assertSame(fileObj, IOUtilities.tryToFile(fileObj));
        assertEquals(new File(uriObj), IOUtilities.tryToFile(uriObj));
        assertEquals(new File(uriObj), IOUtilities.tryToFile(urlFileObj));
        assertEquals(new File(strObj), IOUtilities.tryToFile(strObj));
        assertEquals(urlObj, IOUtilities.tryToFile(urlObj)); // not converted
        assertEquals(1, IOUtilities.tryToFile(intObj)); // not converted
    }


    @Test
    public void uriFSTests() throws IOException, URISyntaxException {
        URI uriUnixFile = URI.create("file:/tmp");
        URI uriUnixFile2 = URI.create("/tmp");
        URI uriUnixFile3 = URI.create("../tmp");
        URI uriFileWin = URI.create("file://c:/shapefiles/file1");

        URI uriHTTP = URI.create("http://www.geotoolkit.org/");
        URI uriHTTPS = URI.create("https://www.geotoolkit.org/");
        URI uriFTP = URI.create("ftp://temp/test");

        String os = System.getProperty("os.name").toLowerCase();

        //Windows
        if (os.contains("win")) {
            assertFalse(IOUtilities.isFileSystemSupported(uriUnixFile));
            assertFalse(IOUtilities.isFileSystemSupported(uriUnixFile2));
            assertFalse(IOUtilities.isFileSystemSupported(uriUnixFile3));

            assertTrue(IOUtilities.isFileSystemSupported(uriFileWin));
        } else {
            //Unix/Linux/Solaris/Mac
            assertTrue(IOUtilities.isFileSystemSupported(uriUnixFile));
            assertTrue(IOUtilities.isFileSystemSupported(uriUnixFile2));
            assertTrue(IOUtilities.isFileSystemSupported(uriUnixFile3));

            assertFalse(IOUtilities.isFileSystemSupported(uriFileWin));
        }

        //other scheme
        assertFalse(IOUtilities.isFileSystemSupported(uriHTTP));
        assertFalse(IOUtilities.isFileSystemSupported(uriHTTPS));
        assertFalse(IOUtilities.isFileSystemSupported(uriFTP));
    }

    @Test
    public void childrenListTest() throws IOException, URISyntaxException {

        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        URL resourceURL = classloader.getResource("org/geotoolkit/xml/test-data");
        if (resourceURL != null) {
            Path directory = Paths.get(resourceURL.toURI());

            List<Path> result = IOUtilities.listChildren(directory);
            assertEquals(5, result.size());

            //result list should be sorted in asc
            assertEquals("Metadata-ASCAT.xml", result.get(0).getFileName().toString());
            assertEquals("Metadata-IFREMER.xml", result.get(1).getFileName().toString());
            assertEquals("Metadata-IGN.xml", result.get(2).getFileName().toString());
            assertEquals("Metadata.xml", result.get(3).getFileName().toString());
            assertEquals("NOAA.xml", result.get(4).getFileName().toString());
        }

        resourceURL = classloader.getResource("org/geotoolkit/xml/test-data/Metadata.xml");
        if (resourceURL != null) {
            Path directory = Paths.get(resourceURL.toURI());

            try {
                IOUtilities.listChildren(directory);
                fail("Should rise IllegalArgumentException on a regularFile");
            } catch (IllegalArgumentException e) {
                //expected exception
            }
        }

        Path tmpDir = Files.createTempDirectory("IOUtilitiesTest");
        List<Path> result = IOUtilities.listChildren(tmpDir);
        assertEquals(0, result.size());
        IOUtilities.deleteSilently(tmpDir);
    }

    @Test
    public void copyResourcesFileTest() {

        Path tmpWorkspace = null;
        try {
            tmpWorkspace = Files.createTempDirectory("IOUtilitiesTest");
            Files.createDirectories(tmpWorkspace);
            String resource = "org/geotoolkit/xml/test-data/Metadata.xml";

            //Copy resource File with hierarchy
            try {
                Path copiedRes = IOUtilities.copyResource(resource, null, tmpWorkspace, true);
                Path expectedResPath = tmpWorkspace.resolve(resource);
                testResourceFile(expectedResPath, resource, copiedRes);

            } catch (URISyntaxException | IOException e) {
                fail("Unable to setup test environment");
            }

            //Copy resource File without hierarchy
            try {
                Path copiedRes = IOUtilities.copyResource(resource, null, tmpWorkspace, false);
                Path expectedResPath = tmpWorkspace.resolve("Metadata.xml");
                testResourceFile(expectedResPath, resource, copiedRes);

            } catch (URISyntaxException | IOException e) {
                fail("Unable to setup test environment");
            }

            //test failures
            try {
                IOUtilities.copyResource("unknown/resource", null, tmpWorkspace, false);
                fail("Should rise FileNotFoundException");
            } catch (FileNotFoundException ex) {
                //normal exception
            } catch (URISyntaxException | IOException e) {
                fail("Wrong exception, Should rise FileNotFoundException");
            }
        } catch (IOException e) {
            fail("Unable to setup test environment");
        } finally {
            if (tmpWorkspace != null) {
                IOUtilities.deleteSilently(tmpWorkspace);
            }
        }
    }

    @Test
    public void copyResourcesDirectoryTest() {

        Path tmpWorkspace = null;
        try {
            tmpWorkspace = Files.createTempDirectory("IOUtilitiesTest");
            Files.createDirectories(tmpWorkspace);
            String resource = "org/geotoolkit/xml/test-data";

            String[] expectedFiles = new String[]{"Metadata.xml", "Metadata-ASCAT.xml", "Metadata-IFREMER.xml",
            "Metadata-IGN.xml", "NOAA.xml"};

            //Copy resource Directory with hierarchy
            try {
                Path copiedRes = IOUtilities.copyResource(resource, null, tmpWorkspace, true);
                Path expectedResPath = tmpWorkspace.resolve(resource);
                testResourceDirectory(expectedResPath, expectedFiles, copiedRes);

            } catch (URISyntaxException | IOException e) {
                fail("Unable to setup test environment");
            }


            //Copy resource Directory without hierarchy
            try {
                Path copiedRes = IOUtilities.copyResource(resource, null, tmpWorkspace, false);
                Path expectedResPath = tmpWorkspace.resolve("test-data");
                testResourceDirectory(expectedResPath, expectedFiles, copiedRes);

            } catch (URISyntaxException | IOException e) {
                fail("Unable to setup test environment");
            }
        } catch (IOException e) {
            fail("Unable to setup test environment");
        } finally {
            if (tmpWorkspace != null) {
                IOUtilities.deleteSilently(tmpWorkspace);
            }
        }
    }

    private void testResourceDirectory(Path expectedResPath, String[] resource, Path copiedRes) throws IOException {
        //test exist
        assertTrue(Files.isDirectory(copiedRes));

        //test path
        assertEquals(expectedResPath.toString(), copiedRes.toString());

        for (String file : resource) {
            Path expectedFile = expectedResPath.resolve(file);
            Path copiedFile = copiedRes.resolve(file);
            testResourceFile(expectedFile, "org/geotoolkit/xml/test-data/"+file, copiedFile);
        }

    }

    private void testResourceFile(Path expectedResPath, String resource, Path copiedRes) throws IOException {
        //test exist
        assertTrue(Files.isRegularFile(copiedRes));

        //test path
        assertEquals(expectedResPath.toString(), copiedRes.toString());

        //test content
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try (InputStream resourceStream = classLoader.getResourceAsStream(resource)) {
            String expectedContent = IOUtilities.toString(resourceStream);
            String copiedContent = IOUtilities.toString(copiedRes);
            assertEquals(expectedContent, copiedContent);
        }
    }
}
