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
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;

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
    public void conversionTests() throws IOException, URISyntaxException {

        Path pathObj = Files.createTempFile("path", ".tmp");
        File fileObj = File.createTempFile("file", ".tmp");
        URI uriObj = URI.create("file:/tmp");
        URL urlObj = new URL("http://some/url");
        String strObj = "/tmp";
        Integer intObj = 1;

        //tests
        assertTrue(IOUtilities.canProcessAsPath(pathObj));
        assertTrue(IOUtilities.canProcessAsPath(fileObj));
        assertTrue(IOUtilities.canProcessAsPath(uriObj));
        assertTrue(IOUtilities.canProcessAsPath(urlObj));
        assertTrue(IOUtilities.canProcessAsPath(strObj));
        assertFalse(IOUtilities.canProcessAsPath(intObj));

        //convert to Path
        assertSame(pathObj, IOUtilities.toPath(pathObj)); //must be same object
        assertEquals(fileObj.toPath(), IOUtilities.toPath(fileObj));
        assertEquals(Paths.get(uriObj), IOUtilities.toPath(uriObj));
        assertEquals(Paths.get(strObj), IOUtilities.toPath(strObj));

        try {
            IOUtilities.toPath(urlObj);
            fail("Should raise FileSystemNotFoundException because http FileSystem is not supported by default");
        } catch (IOException e) {
            //normal exception
        }

        try {
            IOUtilities.toPath(intObj);
            fail("Should raise IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            //normal exception
        }

        //using try
        assertSame(pathObj, IOUtilities.tryToPath(pathObj)); //must be same object
        assertEquals(fileObj.toPath(), IOUtilities.tryToPath(fileObj));
        assertEquals(Paths.get(uriObj), IOUtilities.tryToPath(uriObj));
        assertEquals(Paths.get(strObj), IOUtilities.tryToPath(strObj));
        assertEquals(urlObj, IOUtilities.tryToPath(urlObj));// not converted because FS not supported
        assertEquals(1, IOUtilities.tryToPath(intObj)); // not converted

        //try file conversion
        assertEquals(pathObj.toFile(), IOUtilities.tryToFile(pathObj));
        assertSame(fileObj, IOUtilities.tryToFile(fileObj));
        assertEquals(new File(uriObj), IOUtilities.tryToFile(uriObj));
        assertEquals(new File(strObj), IOUtilities.tryToFile(strObj));
        assertEquals(urlObj, IOUtilities.tryToFile(urlObj)); // not converted
        assertEquals(1, IOUtilities.tryToFile(intObj)); // not converted
    }

}
