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
package org.geotoolkit.io;

import org.junit.*;
import static org.junit.Assert.*;
import static org.geotoolkit.io.X364.*;


/**
 * Tests the {@link X364} class.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 */
@Deprecated
public final strictfp class X364Test {
    /**
     * Tests the {@link X364#plain} method.
     */
    @Test
    public void testPlain() {
        String colored, plain;
        colored = "Some plain text";
        plain   = "Some plain text";
        assertEquals(plain, plain(colored));
        assertEquals(plain.length(), lengthOfPlain(colored));

        plain   = "With blue in the middle";
        colored = "With " + FOREGROUND_BLUE.sequence() +
                  "blue"  + FOREGROUND_DEFAULT.sequence() + " in the middle";
        assertEquals(plain, plain(colored));
        assertEquals(plain.length(), lengthOfPlain(colored));
    }
}
