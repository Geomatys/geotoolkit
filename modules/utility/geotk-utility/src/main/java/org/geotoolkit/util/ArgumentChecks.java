/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2011-2012, Geomatys
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

import org.geotoolkit.lang.Static;
import org.geotoolkit.resources.Errors;
import org.apache.sis.util.NullArgumentException;


/**
 * Provides static methods for performing argument checks.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.17
 * @module
 */
public final class ArgumentChecks extends Static {
    /**
     * Forbid object creation.
     */
    private ArgumentChecks() {
    }

    /**
     * Makes sure that an argument is non-null. If the given {@code object} is null, then a
     * {@link NullArgumentException} is thrown with a localized message containing the given name.
     *
     * @param  name The name of the argument to be checked. Used only in case an exception is thrown.
     * @param  object The user argument to check against null value.
     * @throws NullArgumentException if {@code object} is null.
     *
     * @deprecated Moved to Apache SIS {@link org.apache.sis.util.ArgumentChecks}.
     */
    @Deprecated
    public static void ensureNonNull(final String name, final Object object)
            throws NullArgumentException
    {
        org.apache.sis.util.ArgumentChecks.ensureNonNull(name, object);
    }

    /**
     * Makes sure that an array element is non-null. If {@code array[index]} is null, then a
     * {@link NullArgumentException} is thrown with a localized message containing the given name.
     *
     * @param  name The name of the argument to be checked. Used only in case an exception is thrown.
     * @param  index Index of the element to check.
     * @param  array The user argument to check against null element.
     * @throws NullArgumentException if {@code array} or {@code array[index]} is null.
     */
    public static void ensureNonNull(final String name, final int index, final Object[] array)
            throws NullArgumentException
    {
        if (array == null) {
            throw new NullArgumentException(Errors.format(Errors.Keys.NULL_ARGUMENT_$1, name));
        }
        if (array[index] == null) {
            throw new NullArgumentException(Errors.format(
                    Errors.Keys.NULL_ARGUMENT_$1, name + '[' + index + ']'));
        }
    }
}
