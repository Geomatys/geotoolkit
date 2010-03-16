/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2007-2010, Geomatys
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
package org.geotoolkit.coverage.sql;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests {@link SeriesEntry}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.10
 *
 * @since 3.10 (derived from Seagis)
 */
public final class SeriesEntryTest {
    /**
     * Returns a dummy series for the given root and path.
     */
    private static SeriesEntry series(final String root, final String path) {
        return new SeriesEntry(100, root, path, "png", null, null);
    }

    /**
     * Tests a relative file name.
     *
     * @throws URISyntaxException Should never happen.
     */
    @Test
    public void testRelativeFile() throws URISyntaxException {
        final SeriesEntry entry = series("/SomeRoot/SomeSub/", "SomeSeries/2");
        final String   expected = "/SomeRoot/SomeSub/SomeSeries/2/foo.png";
        final File         file = entry.file("foo");
        final URI           uri = entry.uri ("foo");
        assertTrue  (file.isAbsolute());
        assertEquals(new File(expected), file);
        assertEquals(new URI("file://" + expected), uri);
    }

    /**
     * Tests an absolute file name.
     *
     * @throws URISyntaxException Should never happen.
     */
    @Test
    public void testAbsoluteFile() throws URISyntaxException {
        final SeriesEntry entry = series("/SomeRoot/SomeSub/", "/SomeSeries/2");
        final String   expected = "/SomeSeries/2/foo.png";
        final File         file = entry.file("foo");
        final URI           uri = entry.uri ("foo");
        assertTrue  (file.isAbsolute());
        assertEquals(new File(expected), file);
        assertEquals(new URI("file://" + expected), uri);
    }

    /**
     * Tests a relative URL.
     *
     * @throws URISyntaxException Should never happen.
     */
    @Test
    public void testRelativeURL() throws URISyntaxException {
        final SeriesEntry entry = series("ftp://localhost/SomeRoot/SomeSub/", "SomeSeries/2");
        final String   expected = "SomeRoot/SomeSub/SomeSeries/2/foo.png";
        final File         file = entry.file("foo");
        final URI           uri = entry.uri ("foo");
        assertFalse (file.isAbsolute());
        assertEquals(new File(expected), file);
        assertEquals(new URI("ftp://localhost/" + expected), uri);
    }

    /**
     * Tests an absolute URL.
     *
     * @throws URISyntaxException Should never happen.
     */
    @Test
    public void testAbsoluteURL() throws URISyntaxException {
        final SeriesEntry entry = series("ftp://localhost/SomeRoot/SomeSub/", "ftp://localhost/SomeSeries/2");
        final String   expected = "SomeSeries/2/foo.png";
        final File         file = entry.file("foo");
        final URI           uri = entry.uri ("foo");
        assertFalse (file.isAbsolute());
        assertEquals(new File(expected), file);
        assertEquals(new URI("ftp://localhost/" + expected), uri);
    }
}
