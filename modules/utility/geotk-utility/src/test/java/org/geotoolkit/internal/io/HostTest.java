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
package org.geotoolkit.internal.io;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests the {@link Host} class.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.11
 *
 * @since 3.11
 */
public final strictfp class HostTest extends org.geotoolkit.test.TestBase {
    /**
     * Tests JDBC URL.
     */
    @Test
    public void testJDBC() {
        final Integer port = 5432;

        Host host = new Host("jdbc:postgresql://db.server.com:5432/database");
        assertEquals("db.server.com", host.host);
        assertEquals(port,            host.port);
        assertEquals("database",      host.path);

        host = new Host("jdbc:postgresql://db.server.com/database");
        assertEquals("db.server.com", host.host);
        assertNull  (                 host.port);
        assertEquals("database",      host.path);

        host = new Host("jdbc:postgresql://db.server.com:5432/");
        assertEquals("db.server.com", host.host);
        assertEquals(port,            host.port);
        assertNull  (                 host.path);

        host = new Host("jdbc:postgresql://db.server.com:5432");
        assertEquals("db.server.com", host.host);
        assertEquals(port,            host.port);
        assertNull  (                 host.path);

        host = new Host("jdbc:postgresql://db.server.com");
        assertEquals("db.server.com", host.host);
        assertNull  (                 host.port);
        assertNull  (                 host.path);
    }
}
