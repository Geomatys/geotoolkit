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
import java.util.Locale;
import java.util.TimeZone;
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
     *
     * @since 3.17
     */
    private Locale locale;

    /**
     * The timezone, or {@code null} if unspecified.
     * In the later case, an implementation-default (typically UTC) timezone is used.
     *
     * @since 3.17
     */
    private TimeZone timezone;

    /**
     * {@code true} if a marshalling process is under progress.
     * The value is unchanged for unmarshalling processes.
     */
    private boolean isMarshalling;

    /**
     * The context which was previously used. This form a linked list allowing
     * to push properties (e.g. {@link #pushLocale(Locale)}) and pull back the
     * context to its previous state once finished.
     */
    private final MarshalContext previous;

    /**
     * Creates a new context. The new context is immediately set in the {@link #CURRENT} field.
     */
    private MarshalContext() {
        previous = CURRENT.get();
        CURRENT.set(this);
    }

    /**
     * Inherits all configuration from the previous context, if any.
     */
    private void inherit() {
        final MarshalContext previous = this.previous;
        if (previous != null) {
            converters    = previous.converters;
            schemas       = previous.schemas;
            locale        = previous.locale;
            timezone      = previous.timezone;
            isMarshalling = previous.isMarshalling;
        }
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
     */
    public static boolean isMarshalling() {
        final MarshalContext current = CURRENT.get();
        return (current != null) ? current.isMarshalling : false;
    }

    /**
     * Returns the timezone, or {@code null} if none were explicitely defined.
     * In the later case, an implementation-default (typically UTC) timezone is used.
     *
     * @return The timezone, or {@code null} if unspecified.
     *
     * @since 3.17
     */
    public static TimeZone getTimeZone() {
        final MarshalContext current = CURRENT.get();
        return (current != null) ? current.timezone : null;
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
        return (current != null) ? current.locale : null;
    }

    /**
     * Sets the locale to the given value. The old locales are remembered and will
     * be restored by the next call to {@link #pullLocale()}.
     *
     * @param locale The locale to set, or {@code null}.
     *
     * @since 3.17
     */
    public static void pushLocale(final Locale locale) {
        final MarshalContext current = new MarshalContext();
        current.inherit();
        if (locale != null) {
            current.locale = locale;
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
            current.finish();
        }
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
     * @param  timezone   The timezone, or {@code null} if unspecified.
     * @param  locale     The locale, or {@code null} if unspecified.
     * @return The context on which to invoke {@link #finish()} when the (un)marshalling is finished.
     */
    public static MarshalContext begin(final ObjectConverters converters,
            final Map<String,String> schemas, final Locale locale, final TimeZone timezone)
    {
        final MarshalContext current = new MarshalContext();
        current.converters = converters;
        current.schemas    = schemas; // NOSONAR: No clone, because this method is internal.
        current.locale     = locale;
        current.timezone   = timezone;
        return current;
    }

    /**
     * Declares that the work which is about to begin is a marshalling.
     *
     * @see #isMarshalling()
     */
    public void setMarshalling() {
        isMarshalling = true;
    }

    /**
     * Invoked in a {@code finally} block when a unmarshalling process is finished.
     */
    public void finish() {
        if (previous != null) {
            CURRENT.set(previous);
        } else {
            CURRENT.remove();
        }
    }
}
