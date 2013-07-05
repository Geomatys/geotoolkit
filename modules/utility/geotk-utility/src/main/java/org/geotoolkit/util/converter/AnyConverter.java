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

import net.jcip.annotations.NotThreadSafe;
import org.apache.sis.util.logging.Logging;


/**
 * An utility for converting arbitrary objects to arbitrary <cite>target</cite> types without
 * the need to handle {@link ObjectConverter} and {@link ConverterRegistry} explicitly. This
 * is a convenience class for converting a few instances of unknown type.
 *
 * {@note If a large amount of objects of the same type need to be converted to the same target
 *        type, or if the source type is known at compile-time, then it is often more advisable
 *        to use <code>ObjectConverter</code> directly.}
 *
 * {@section Thread safety}
 * This class is <strong>not</strong> thread safe. If conversions need to be performed by
 * concurrent threads, then each thread shall use its own {@code AnyConverter} instance.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.04
 *
 * @since 3.04
 * @module
 */
@NotThreadSafe
public class AnyConverter {
    /**
     * The converter registry, or {@code null} for the default one.
     */
    private ConverterRegistry registry;

    /**
     * The last converter used, or {@code null}. This is stored on the assumption
     * that the same converter will often be reused for consecutive conversions.
     */
    private transient ObjectConverter<?,?> converter;

    /**
     * The last source and target classes requested, or {@code null}. This is not necessarily
     * the same than the classes returned by {@code converter.get[Source|Target]Class()}.
     */
    private transient Class<?> source, target;

    /**
     * Creates a new {@code AnyConverter} which will use the
     * {@linkplain ConverterRegistry#system() system converter registry}.
     */
    public AnyConverter() {
    }

    /**
     * Creates a new {@code AnyConverter} which will use the given converter registry.
     *
     * @param registry The converter registry to use, or {@code null} for the
     *        {@linkplain ConverterRegistry#system() system} one.
     */
    public AnyConverter(final ConverterRegistry registry) {
        this.registry = registry;
    }

    /**
     * Converts the given value to the given type.
     *
     * @param  <T> The parameterized type of {@code targetType}.
     * @param  value The value to convert (can be {@code null}).
     * @param  targetType The desired type.
     * @return The given object converted to the given type.
     * @throws NonconvertibleObjectException If the conversion can not be performed.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public <T> T convert(Object value, final Class<T> targetType) throws NonconvertibleObjectException {
        if (value != null) {
            final Class<?> sourceType = value.getClass();
            if (!targetType.isAssignableFrom(sourceType)) {
                ObjectConverter<?,?> converter = this.converter;
                if (converter == null || source != sourceType || target != targetType) {
                    ConverterRegistry registry = this.registry;
                    if (registry == null) {
                        this.registry = registry = ConverterRegistry.system();
                    }
                    this.converter = converter = registry.converter(sourceType, targetType);
                    this.source = sourceType; // Assign only after success.
                    this.target = targetType;
                }
                value = ((ObjectConverter) converter).convert(value);
            }
        }
        return (T) value;
    }

    /**
     * Tries to convert the given value to the given type. If the conversion fail, then this method
     * invokes {@link #conversionFailed conversionFailed} and returns the value unchanged.
     *
     * @param  value The value to convert (can be {@code null}).
     * @param  targetType The desired type.
     * @return The given object converted to the given type.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public Object tryConvert(Object value, final Class<?> targetType) {
        if (value != null) {
            final Class<?> sourceType = value.getClass();
            if (!targetType.isAssignableFrom(sourceType)) {
                ObjectConverter<?,?> converter = this.converter;
                if (source != sourceType || target != targetType) {
                    ConverterRegistry registry = this.registry;
                    if (registry == null) {
                        this.registry = registry = ConverterRegistry.system();
                    }
                    try {
                        converter = registry.converter(sourceType, targetType);
                    } catch (NonconvertibleObjectException e) {
                        conversionFailed(e);
                        converter = null;
                    }
                    this.converter = converter;
                    this.source = sourceType;
                    this.target = targetType;
                }
                if (converter != null) try {
                    value = ((ObjectConverter) converter).convert(value);
                } catch (NonconvertibleObjectException e) {
                    conversionFailed(e);
                }
            }
        }
        return value;
    }

    /**
     * Invoked by {@link #tryConvert tryConvert} when an object can not be converted. The default
     * implementation logs the exception at the {@link java.util.logging.Level#FINE FINE} level.
     * Subclasses can override this method if they want to logs the message in a different way.
     *
     * @param exception The exception that occurred while trying to convert an object.
     */
    protected void conversionFailed(final NonconvertibleObjectException exception) {
        Logging.recoverableException(AnyConverter.class, "tryConvert", exception);
    }

    /**
     * Returns the last converter used. This is only for testing purpose.
     */
    final ObjectConverter<?,?> getLastConverter() {
        return converter;
    }
}
