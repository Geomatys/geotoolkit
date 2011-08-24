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
package org.geotoolkit.util;

import org.junit.*;


/**
 * Tests the {@link Exceptions} utility methods.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.18
 *
 * @since 3.18
 */
public final strictfp class ExceptionsTest {
    /**
     * Tests the {@link Exceptions#show(Component, Throwable)} method. This operation is expected
     * to fails if the {@code geotk-widgets-swing} module is not available on the classpath (this
     * is the case during the Maven buid).
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testShow() {
        Exceptions.show(null, new Exception());
    }
}
