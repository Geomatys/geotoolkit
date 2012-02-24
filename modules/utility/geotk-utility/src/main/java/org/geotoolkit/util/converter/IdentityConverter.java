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
package org.geotoolkit.util.converter;

import java.io.Serializable;
import net.jcip.annotations.Immutable;


/**
 * A converter which returns the source unchanged.
 *
 * @param <T> The base type of source and converted objects.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.02
 *
 * @since 3.01
 * @module
 */
@Immutable
final class IdentityConverter<T> extends SimpleConverter<T,T> implements Serializable {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = -7203549932226245206L;

    /**
     * An identity converter for {@link CharSequence} objects.
     *
     * @since 3.02
     */
    static final IdentityConverter<CharSequence> CHAR_SEQUENCE = new IdentityConverter<>(CharSequence.class);

    /**
     * An identity converter for {@link String} objects.
     *
     * @since 3.02
     */
    static final IdentityConverter<String> STRING = new IdentityConverter<>(String.class);

    /**
     * Returns an identity converter for the given type.
     *
     * @since 3.02
     */
    @SuppressWarnings("unchecked")
    public static <T> IdentityConverter<T> create(final Class<T> type) {
        if (type == String.class) {
            return (IdentityConverter<T>) STRING;
        }
        if (type == CharSequence.class) {
            return (IdentityConverter<T>) CHAR_SEQUENCE;
        }
        return new IdentityConverter<>(type);
    }

    /**
     * The type of source and coverted objects.
     */
    private final Class<T> type;

    /**
     * Creates a new identity converter.
     *
     * @param type The type of source and coverted objects.
     */
    private IdentityConverter(final Class<T> type) {
        this.type = type;
    }

    /**
     * Returns the type for source objects.
     */
    @Override
    public Class<? super T> getSourceClass() {
        return type;
    }

    /**
     * Returns the type of converted objects.
     */
    @Override
    public Class<? extends T> getTargetClass() {
        return type;
    }

    /**
     * Returns the given object unchanged.
     */
    @Override
    public T convert(final T source) {
        return source;
    }
}
