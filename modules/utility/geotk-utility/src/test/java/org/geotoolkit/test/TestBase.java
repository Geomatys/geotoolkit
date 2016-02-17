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
package org.geotoolkit.test;

import org.geotoolkit.internal.io.JNDI;

import static org.junit.Assert.*;


/**
 * Base class of Geotoolkit.org tests. This base class provides some configuration that
 * are commons to all subclasses.
 *
 * @author Martin Desruisseaux (Geomatys)
 */
public abstract strictfp class TestBase extends org.apache.sis.test.TestCase {
    static {
        JNDI.install();
    }

    /**
     * Installs the customized validators defined in the {@link org.geotoolkit.test.validator} package.
     * Those validators ensures that ISO or GeoAPI restrictions apply, then checks for yet more restrictive
     * Geotk conditions. For example Geotk requires the exact same instance where GeoAPI requires only instances
     * that are {@linkplain Object#equals(Object) equal}.
     */
    static {
        final Class<?> c = org.geotoolkit.test.validator.Validators.class;
        try {
            // Force class initialization.
            assertSame(c, Class.forName(c.getName(), true, c.getClassLoader()));
        } catch (ClassNotFoundException e) {
            throw new AssertionError(e); // Should never happen.
        }
    }

    /**
     * Creates a new test case.
     */
    protected TestBase() {
    }
}
