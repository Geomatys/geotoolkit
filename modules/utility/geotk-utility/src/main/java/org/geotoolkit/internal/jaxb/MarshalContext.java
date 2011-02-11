/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2011, Geomatys
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
package org.geotoolkit.internal.jaxb;

import java.util.Map;
import java.util.Arrays;
import java.util.Locale;
import org.geotoolkit.xml.ObjectConverters;


/**
 * Thread-local status of a marshalling or unmarshalling process.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.17
 *
 * @since 3.07
 * @module
 */
public final class MarshalContext {
    /**
     * The thread-local context.
     */
    private static final ThreadLocal<MarshalContext> CURRENT = new ThreadLocal<MarshalContext>();

    /**
     * The object converters currently in use, or {@code null} for {@link ObjectConverters#DEFAULT}.
     */
    private ObjectConverters converters;

    /**
     * The base URL of ISO 19139 (or other standards) schemas. The valid values
     * are documented in the {@link org.geotoolkit.xml.XML#SCHEMAS} property.
     *
     * @since 3.17
     */
    private Map<String, String> schemas;

    /**
     * The locale to use for marshalling, or {@code null} if no locale were explicitly specified.
     * The locale to use is the first element in the array. Other elements were values previously
     * pushed, to be pulled later.
     * <p>
     * This array is usually very short (typically no more than 3 elements).
     *
     * @since 3.17
     */
    private Locale[] locale;

    /**
     * {@code true} if a marshalling process is under progress.
     * The value is unchanged for unmarshalling processes.
     */
    private boolean isMarshalling;

    /**
     * Do not allow instantiation outside this class.
     */
    private MarshalContext() {
    }

    /**
     * Returns the object converters in use for the current marshalling or unmarshalling process. If
     * no converter were explicitely set, then this method returns {@link ObjectConverters#DEFAULT}.
     *
     * @return The current object converters (never null).
     */
    public static ObjectConverters converters() {
        final MarshalContext current = CURRENT.get();
        if (current != null) {
            final ObjectConverters converters = current.converters;
            if (converters != null) {
                return converters;
            }
        }
        return ObjectConverters.DEFAULT;
    }

    /**
     * Returns the base URL of ISO 19139 (or other standards) schemas. The valid values
     * are documented in the {@link org.geotoolkit.xml.XML#SCHEMAS} property.
     *
     * @param  key One of the value documented in the "<cite>Map key</cite>" column of
     *         {@link org.geotoolkit.xml.XML#SCHEMAS}.
     * @return The base URL of the schema, or {@code null} if none were specified.
     *
     * @since 3.17
     */
    public static String schema(final String key) {
        final MarshalContext current = CURRENT.get();
        if (current != null) {
            final Map<String,String> schemas = current.schemas;
            if (schemas != null) {
                return schemas.get(key);
            }
        }
        return null;
    }

    /**
     * Returns whatever a marshalling process is under progress.
     *
     * @return {@code true} if a marshalling process is in progress.
     *
     * @see #setMarshalling()
     */
    public static boolean isMarshalling() {
        final MarshalContext current = CURRENT.get();
        return (current != null) ? current.isMarshalling : false;
    }

    /**
     * Returns the locale to use for marshalling, or {@code null} if no locale were explicitly
     * specified. A {@code null} value means that some locale-neutral language should be used
     * if available, or an implementation-default locale (typically English) otherwise.
     * <p>
     * When this method returns a null locale, callers shall select a default locale as documented
     * in the {@link org.geotoolkit.util.DefaultInternationalString#toString(Locale)} javadoc.
     *
     * @return The locale, or {@code null} is unspecified.
     *
     * @since 3.17
     */
    public static Locale getLocale() {
        final MarshalContext current = CURRENT.get();
        return (current != null) ? getLocale(current.locale) : null;
    }

    /**
     * Returns the first locale from the given array, or {@code null} if none.
     */
    private static Locale getLocale(final Locale[] locale) {
        return (locale != null && locale.length != 0) ? locale[0] : null;
    }

    /**
     * Sets the locale to the given value. The old locales are remembered and will
     * be restored by the next call to {@link #pullLocale()}.
     *
     * @param locale The locale to set, or {@code null}.
     *
     * @since 3.17
     */
    public static void pushLocale(Locale locale) {
        final MarshalContext current = current();
        final Locale[] array = current.locale;
        if (locale != null || (locale = getLocale(array)) != null) {
            final int length = (array != null) ? array.length : 0;
            final Locale[] copy = new Locale[length + 1];
            if (array != null) {
                System.arraycopy(array, 0, copy, 1, length);
            }
            copy[0] = locale;
            current.locale = copy;
        }
    }

    /**
     * Restores the locale which was used prior the call to {@link #pushLocale(Locale)}.
     *
     * @since 3.17
     */
    public static void pullLocale() {
        final MarshalContext current = CURRENT.get();
        if (current != null) {
            final Locale[] array = current.locale;
            if (array != null) {
                final int length = array.length;
                current.locale = (length >= 2) ? Arrays.copyOfRange(array, 1, length) : null;
            }
        }
    }

    /**
     * Returns the {@code MarshalContext} for the current thread, creating a new one if necessary.
     */
    private static MarshalContext current() {
        MarshalContext current = CURRENT.get();
        if (current == null) {
            current = new MarshalContext();
            CURRENT.set(current);
        }
        return current;
    }

    /**
     * Invoked when a marshalling or unmarshalling process is about to begin.
     * Must be followed by a call to {@link #finish()} in a {@code finally} block.
     *
     * {@preformat java
     *     MarshalContext ctx = begin(converters);
     *     try {
     *         ...
     *     } finally {
     *         ctx.finish();
     *     }
     * }
     *
     * @param  converters The converters in use.
     * @param  schemas    The schemas root URL, or {@code null} if none.
     * @param  locale     The locale, or {@code null} if none.
     * @return The context on which to invoke {@link #finish()} when the (un)marshalling is finished.
     */
    public static MarshalContext begin(final ObjectConverters converters,
            final Map<String,String> schemas, final Locale locale)
    {
        final MarshalContext current = current();
        current.converters = converters;
        current.schemas    = schemas; // NOSONAR: No clone, because this method is internal.
        current.locale     = (locale != null) ? new Locale[] {locale} : null;
        return current;
    }

    /**
     * Declares that the work which is about to begin is a marshalling, and
     * returns the previous value of the {@link #isMarshalling()} flag.
     *
     * @return The old value.
     *
     * @see #isMarshalling()
     */
    public boolean setMarshalling() {
        final boolean old = isMarshalling;
        isMarshalling = true;
        return old;
    }

    /**
     * Invoked in a {@code finally} block when a marshalling process is finished.
     *
     * @param marshalling The value to restore for the {@link #isMarshalling()} flag.
     */
    public void finish(final boolean marshalling) {
        isMarshalling = marshalling;
        finish();
    }

    /**
     * Invoked in a {@code finally} block when a unmarshalling process is finished.
     */
    public void finish() {
        converters = null;
        locale     = null;
        schemas    = null;
        // Intentionally leave isMarshalling unmodified.
    }
}
