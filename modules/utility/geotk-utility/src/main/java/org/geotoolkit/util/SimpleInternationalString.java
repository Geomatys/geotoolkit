/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2012, Open Source Geospatial Foundation (OSGeo)
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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Locale;
import java.util.Objects;
import net.jcip.annotations.Immutable;

import org.opengis.util.InternationalString;

import static org.apache.sis.util.ArgumentChecks.ensureNonNull;


/**
 * An {@linkplain InternationalString international string} consisting of a single string
 * for all {@linkplain Locale locales}. For such a particular case, this implementation is more
 * effective than other implementations provided in this package.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.06
 *
 * @since 2.1
 * @module
 *
 * @deprecated Moved to Apache SIS {@link org.apache.sis.util.iso.SimpleInternationalString}.
 */
@Deprecated
@Immutable
public class SimpleInternationalString extends AbstractInternationalString implements Serializable {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = 3543963804501667578L;

    /**
     * Creates a new instance of international string.
     *
     * @param message The string for all locales.
     */
    public SimpleInternationalString(final String message) {
        ensureNonNull("message", message);
        defaultValue = message;
    }

    /**
     * Returns the given characters sequence as an international string. If the given sequence is
     * null or an instance of {@link InternationalString}, this this method returns it unchanged.
     * Otherwise, this method wraps the sequence in a new {@code SimpleInternationalString}
     * instance and returns it.
     *
     * @param  string The characters sequence to wrap, or {@code null}.
     * @return The given sequence as an international string, or {@code null} if the
     *         given sequence was null.
     */
    public static InternationalString wrap(final CharSequence string) {
        if (string == null || string instanceof InternationalString) {
            return (InternationalString) string;
        }
        return new SimpleInternationalString(string.toString());
    }

    /**
     * Returns the given array of {@code CharSequence}s as an array of {@code InternationalString}s.
     * If the given array is null or an instance of {@code InternationalString[]}, then this method
     * returns it unchanged. Otherwise a new array of type {@code InternationalString[]} is created
     * and every elements from the given array is {@linkplain #wrap(CharSequence) wrapped} in the
     * new array.
     * <p>
     * If a defensive copy of the {@code strings} array is wanted, then the caller needs to check
     * if the returned array is the same instance than the one given in argument to this method.
     *
     * @param  strings The characters sequences to wrap, or {@code null}.
     * @return The given array as an array of type {@code InternationalString[]},
     *         or {@code null} if the given array was null.
     *
     * @since 3.06
     */
    public static InternationalString[] wrap(final CharSequence... strings) {
        if (strings == null || strings instanceof InternationalString[]) {
            return (InternationalString[]) strings;
        }
        final InternationalString[] copy = new InternationalString[strings.length];
        for (int i=0; i<strings.length; i++) {
            copy[i] = wrap(strings[i]);
        }
        return copy;
    }

    /**
     * Returns the string representation, which is unique for all locales.
     */
    @Override
    public String toString() {
        return defaultValue;
    }

    /**
     * Returns the same string for all locales. This is the string given to the constructor.
     */
    @Override
    public String toString(final Locale locale) {
        return defaultValue;
    }

    /**
     * Compares this international string with the specified object for equality.
     *
     * @param object The object to compare with this international string.
     * @return {@code true} if the given object is equal to this string.
     */
    @Override
    public boolean equals(final Object object) {
        if (object != null && object.getClass() == getClass()) {
            final SimpleInternationalString that = (SimpleInternationalString) object;
            return Objects.equals(this.defaultValue, that.defaultValue);
        }
        return false;
    }

    /**
     * Returns a hash code value for this international text.
     */
    @Override
    public int hashCode() {
        return (int) serialVersionUID ^ defaultValue.hashCode();
    }

    /**
     * Write the string. This is required since {@link #defaultValue} is not serialized.
     */
    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeUTF(defaultValue);
    }

    /**
     * Read the string. This is required since {@link #defaultValue} is not serialized.
     */
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        defaultValue = in.readUTF();
    }
}
