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
 * Handles conversions from {@link CharSequence} to {@link String}, then forward
 * to an other converter from {@link String} to various objects. Instance of this
 * converter are not registered in {@link ConverterRegistry} like other converters
 * because we avoid registering converter expecting interfaces as their source.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.02
 *
 * @since 3.02
 * @module
 */
@Immutable
final class CharSequenceConverter<T> extends SimpleConverter<CharSequence,T> implements Serializable {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = 2591675151163578878L;

    /**
     * A converter from {@link CharSequence} to {@link String}.
     */
    static final CharSequenceConverter<String> STRING =
            new CharSequenceConverter<>(String.class, IdentityConverter.STRING);

    /**
     * The target type requested by the user. We retain this type explicitly instead
     * than querying {@code next.getTargetType()} because it may be a super-class of
     * the later.
     */
    private final Class<T> targetType;

    /**
     * The converter to apply after this one.
     */
    private final ObjectConverter<? super String, ? extends T> next;

    /**
     * Creates a new converter from {@link CharSequence} to the given target type.
     *
     * @param targetType The target type requested by the user.
     * @param next The converter to apply after this one.
     */
    private CharSequenceConverter(final Class<T> targetType, final ObjectConverter<? super String, ? extends T> next) {
        this.targetType = targetType;
        this.next = next;
    }

    /**
     * Creates a new converter from {@link CharSequence} to the given target type.
     *
     * @param targetType The target type requested by the user.
     * @param next The converter to apply after this one.
     */
    @SuppressWarnings("unchecked")
    public static <T> ObjectConverter<? super CharSequence, T> create(
            final Class<T> targetType, final ObjectConverter<? super String, ? extends T> next)
    {
        assert targetType.isAssignableFrom(next.getTargetClass());
        if (next.getSourceClass().isAssignableFrom(CharSequence.class)) {
            return (ObjectConverter<? super CharSequence, T>) next;
        }
        return new CharSequenceConverter<>(targetType, next);
    }

    /**
     * Returns the source class, which is always {@link CharSequence}.
     */
    @Override
    public final Class<CharSequence> getSourceClass() {
        return CharSequence.class;
    }

    /**
     * Returns the target class.
     */
    @Override
    public final Class<T> getTargetClass() {
        return targetType;
    }

    /**
     * Converts an object to an object of the target type.
     */
    @Override
    public T convert(final CharSequence source) throws NonconvertibleObjectException {
        if (targetType.isInstance(source)) {
            return targetType.cast(source);
        }
        return next.convert(source != null ? source.toString() : null);
    }
}
