/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2011, Geomatys
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
package org.geotoolkit.internal.jaxb;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Test {@link XmlUtilities}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.17
 *
 * @since 3.17
 */
public final class XmlUtilitiesTest {
    /**
     * Tests the {@link XmlUtilities#parseDateTime(String)} method.
     */
    @Test
    public void testParseDateTime() {
        assertEquals(1230786000000L, XmlUtilities.parseDateTime("2009-01-01T06:00:00+01:00").getTime());
    }
}
