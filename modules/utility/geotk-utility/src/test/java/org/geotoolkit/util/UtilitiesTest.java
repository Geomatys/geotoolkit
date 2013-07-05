/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.util;

import org.junit.*;
import static org.junit.Assert.*;
import static java.lang.Double.NaN;


/**
 * Tests the {@link Utilities} static methods.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.20
 *
 * @since 2.5
 */
public final strictfp class UtilitiesTest {
    /**
     * Tests {@link Utilities#equals(double, double)}.
     */
    @Test
    public void testEquals() {
        assertTrue (Utilities.equals(4.0, 4.0));
        assertFalse(Utilities.equals(4.0, 4.1));
        assertTrue (Utilities.equals(NaN, NaN));
    }
}
