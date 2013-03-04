/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.util.converter;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.net.URISyntaxException;
import java.net.MalformedURLException;
import org.apache.sis.util.ArraysExt;

import org.geotoolkit.test.PlatformDependentTest;

import org.junit.*;
import static org.junit.Assume.*;
import static org.geotoolkit.test.Assert.*;


/**
 * Tests the various {@link FileConverter} implementations.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.17
 *
 * @since 3.01
 */
public final strictfp class FileConverterTest {
    /**
     * Assumes that the platform file system has a Unix-style root.
     * Windows platform has driver letters instead, like "C:\\",
     * which are not correctly tested by this class.
     */
    private static void assumeUnixRoot() {
        assumeTrue(ArraysExt.contains(File.listRoots(), new File("/")));
    }

    /**
     * Tests conversions to string values.
     *
     * @throws NonconvertibleObjectException Should never happen.
     */
    @Test
    @PlatformDependentTest
    public void testString() throws NonconvertibleObjectException {
        assumeUnixRoot();
        final ObjectConverter<File,String> c = FileConverter.String.INSTANCE;
        assertEquals("/home/user/index.txt".replace('/', File.separatorChar),
                c.convert(new File("/home/user/index.txt")));
        assertSame(c, assertSerializable(c));
    }

    /**
     * Tests conversions to URI values.
     *
     * @throws NonconvertibleObjectException Should never happen.
     * @throws URISyntaxException Should never happen.
     */
    @Test
    @PlatformDependentTest
    public void testURI() throws NonconvertibleObjectException, URISyntaxException {
        assumeUnixRoot();
        final ObjectConverter<File,URI> c = FileConverter.URI.INSTANCE;
        assertEquals(new URI("file:/home/user/index.txt"), c.convert(new File("/home/user/index.txt")));
        assertSame(c, assertSerializable(c));
    }

    /**
     * Tests conversions to URL values.
     *
     * @throws NonconvertibleObjectException Should never happen.
     * @throws MalformedURLException Should never happen.
     */
    @Test
    @PlatformDependentTest
    public void testURL() throws NonconvertibleObjectException, MalformedURLException {
        assumeUnixRoot();
        final ObjectConverter<File,URL> c = FileConverter.URL.INSTANCE;
        assertEquals(new URL("file:/home/user/index.txt"), c.convert(new File("/home/user/index.txt")));
        assertSame(c, assertSerializable(c));
    }
}
