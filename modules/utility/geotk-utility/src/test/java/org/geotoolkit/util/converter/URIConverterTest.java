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

import org.junit.*;
import static org.apache.sis.test.Assert.*;


/**
 * Tests the various {@link URIConverter} implementations.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.01
 *
 * @since 3.01
 */
public final strictfp class URIConverterTest {
    /**
     * Tests conversions to string values.
     *
     * @throws NonconvertibleObjectException Should never happen.
     * @throws URISyntaxException Should never happen.
     */
    @Test
    public void testString() throws NonconvertibleObjectException, URISyntaxException {
        final ObjectConverter<URI,String> c = URIConverter.String.INSTANCE;
        assertEquals("file:/home/user/index.txt", c.convert(new URI("file:/home/user/index.txt")));
        assertSame(c, assertSerializedEquals(c));
    }

    /**
     * Tests conversions to URL values.
     *
     * @throws NonconvertibleObjectException Should never happen.
     * @throws MalformedURLException Should never happen.
     * @throws URISyntaxException Should never happen.
     */
    @Test
    public void testURL() throws NonconvertibleObjectException, MalformedURLException, URISyntaxException {
        final ObjectConverter<URI,URL> c = URIConverter.URL.INSTANCE;
        assertEquals(new URL("file:/home/user/index.txt"), c.convert(new URI("file:/home/user/index.txt")));
        assertSame(c, assertSerializedEquals(c));
    }

    /**
     * Tests conversions to File values.
     *
     * @throws NonconvertibleObjectException Should never happen.
     * @throws URISyntaxException Should never happen.
     */
    @Test
    public void testFile() throws NonconvertibleObjectException, URISyntaxException {
        final ObjectConverter<URI,File> c = URIConverter.File.INSTANCE;
        assertEquals(new File("/home/user/index.txt"), c.convert(new URI("file:/home/user/index.txt")));
        assertSame(c, assertSerializedEquals(c));
    }
}
