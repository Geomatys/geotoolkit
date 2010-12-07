/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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
package org.geotoolkit.internal;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests the {@link JNDI} implementation.
 *
 * @author Jody Garnett (Refractions)
 * @version 3.00
 *
 * @since 2.4
 */
public final class JNDITest {
    /**
     * Tests {@link JNDI#fixName} using simplest name or no context.
     * We avoid the tests that would require a real initial context.
     */
    @Test
    public void testFixName() {
        assertNull  (JNDI.fixName(null));
        assertEquals("simpleName", JNDI.fixName("simpleName"));
        assertEquals("jdbc:EPSG",  JNDI.fixName(null, "jdbc:EPSG"));
        assertEquals("jdbc/EPSG",  JNDI.fixName(null, "jdbc/EPSG"));
    }
}
