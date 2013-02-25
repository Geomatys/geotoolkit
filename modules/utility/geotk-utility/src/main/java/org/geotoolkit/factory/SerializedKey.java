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
package org.geotoolkit.factory;

import java.io.Serializable;
import java.awt.RenderingHints;
import java.io.ObjectStreamException;
import java.io.InvalidClassException;
import java.io.NotSerializableException;
import java.lang.reflect.Field;

import org.geotoolkit.resources.Errors;
import org.apache.sis.util.Classes;


/**
 * A proxy for the serialization of {@link Hints#Key}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.05
 *
 * @since 3.05
 * @module
 */
final class SerializedKey implements Serializable {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = -7330917618436040846L;

    /**
     * The class that define the key constants.
     */
    private final Class<?> definer;

    /**
     * The static field name to query for getting the key constant.
     */
    private final String field;

    /**
     * Creates a new proxy for the given key.
     *
     * @throws NotSerializableException If the given key is not declared as a static
     *         constant in its {@linkplain Class#getEnclosingClass() enclosing class}.
     */
    SerializedKey(final RenderingHints.Key key) throws NotSerializableException {
        final Field f = Hints.fieldOf(key);
        if (f == null) {
            throw new NotSerializableException(Errors.format(
                    Errors.Keys.UNKNOWN_TYPE_$1, Classes.getShortClassName(key)));
        }
        definer = f.getDeclaringClass();
        field   = f.getName();
    }

    /**
     * On deserialization, replace this proxy by the static constant.
     *
     * @return The resolved key.
     * @throws ObjectStreamException If the key has not been found. This exception should
     *         not occur is the key is deserialized using the same Geotk version than the
     *         one that serialized the key.
     */
    protected Object readResolve() throws ObjectStreamException {
        try {
            return definer.getField(field).get(null);
        } catch (ReflectiveOperationException cause) {
            final InvalidClassException e = new InvalidClassException(definer.getName(),
                    Errors.format(Errors.Keys.ILLEGAL_KEY_$1, this));
            e.initCause(cause);
            throw e;
        }
    }

    /**
     * Returns the fully qualified name of the field (including the class name).
     */
    @Override
    public String toString() {
        return definer.getCanonicalName() + '.' + field;
    }
}
