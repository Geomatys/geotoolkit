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
package org.geotoolkit.xml;

import java.net.URI;
import java.net.URL;
import java.net.URISyntaxException;
import java.net.MalformedURLException;
import java.util.Locale;
import java.util.UUID;
import javax.measure.unit.Unit;

import org.apache.sis.measure.Units;
import org.geotoolkit.resources.Locales;
import org.geotoolkit.internal.io.IOUtilities;


/**
 * Performs conversions of objects encountered during XML (un)marshalling. Each method in this
 * class is a converter and can be invoked at (un)marshalling time. The default implementation
 * is straightforward and documented in the javadoc of each method.
 * <p>
 * This class provides a way to handle the errors which may exist in some XML documents. For
 * example a URL in the document may be malformed, causing a {@link MalformedURLException} to
 * be thrown. If this error is not handled, it will cause the (un)marshalling of the entire
 * document to fail. An application may want to change this behavior by replacing URLs that
 * are known to be erroneous by fixed versions of those URLs. Example:
 *
 * {@preformat java
 *     class URLFixer extends ObjectConverters {
 *         public URL toURL(URI uri) throws MalformedURLException {
 *             try {
 *                 return super.toURL(uri);
 *             } catch (MalformedURLException e) {
 *                 if (uri.equals(KNOWN_ERRONEOUS_URI) {
 *                     return FIXED_URL;
 *                 } else {
 *                     throw e;
 *                 }
 *             }
 *         }
 *     }
 * }
 *
 * See the {@link XML#CONVERTERS} javadoc for an example of registering a custom
 * {@code ObjectConverters} to a (un)marshaller.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.18
 *
 * @since 3.07
 * @module
 */
public class ObjectConverters {
    /**
     * The default, thread-safe and immutable instance. This instance defines the
     * converters used during every (un)marshalling if no {@code ObjectConverters}
     * was explicitly set.
     */
    public static final ObjectConverters DEFAULT = new ObjectConverters();

    /**
     * Creates a default {@code ObjectConverters}. This is for subclasses only,
     * since new instances are useful only if at least one method is overridden.
     */
    protected ObjectConverters() {
    }

    /**
     * Invoked when an exception occurred in any {@code toXXX(...)} method. The default implementation
     * does nothing and return {@code false}, which will cause the (un)marshalling process of the
     * whole XML document to fail.
     * <p>
     * This method provides a single hook that subclasses can override in order to provide their
     * own error handling for every methods defined in this class, like the example documented in
     * the {@link XML#CONVERTERS} javadoc. Subclasses also have the possibility to override individual
     * {@code toXXX(...)} methods, like the example provided in this <a href="#skip-navbar_top">class
     * javadoc</a>.
     *
     * @param  <T> The compile-time type of the {@code sourceType} argument.
     * @param  value The value that can't be converted.
     * @param  sourceType The base type of the value to convert. This is determined by the argument
     *         type of the method that caught the exception. For example the source type is always
     *         {@code URI.class} if the exception has been caught by the {@link #toURL(URI)} method.
     * @param  targetType The expected type of the converted object.
     * @param  exception The exception that occurred during the attempt to convert.
     * @return {@code true} if the (un)marshalling process should continue despite this error,
     *         or {@code false} (the default) if the exception should be propagated, thus causing
     *         the (un)marshalling to fail.
     */
    protected <T> boolean exceptionOccured(T value, Class<T> sourceType, Class<?> targetType,
            Exception exception)
    {
        return false;
    }

    /**
     * Converts the given string to a locale. The string is the language code either as the 2
     * letters or the 3 letters ISO code. It can optionally be followed by the {@code '_'}
     * character and the country code (again either as 2 or 3 letters), optionally followed
     * by {@code '_'} and the variant.
     *
     * @param  value The string to convert to a locale, or {@code null}.
     * @return The converted locale, or {@code null} if the given value was null of if an
     *         exception was thrown and {@code exceptionOccured} returned {@code true}.
     * @throws IllegalArgumentException If the given string can not be converted to a locale.
     *
     * @since 3.17
     */
    public Locale toLocale(String value) throws IllegalArgumentException {
        if (value != null && !(value = value.trim()).isEmpty()) try {
            return Locales.parse(value);
        } catch (IllegalArgumentException e) {
            if (!exceptionOccured(value, String.class, Locale.class, e)) {
                throw e;
            }
        }
        return null;
    }

    /**
     * Converts the given string to a unit. The default implementation is as below, omitting
     * the check for null value and the call to {@link #exceptionOccured exceptionOccured}
     * in case of error:
     *
     * {@preformat java
     *     return Units.valueOf(value);
     * }
     *
     * @param  value The string to convert to a unit, or {@code null}.
     * @return The converted unit, or {@code null} if the given value was null of if an
     *         exception was thrown and {@code exceptionOccured} returned {@code true}.
     * @throws IllegalArgumentException If the given string can not be converted to a unit.
     *
     * @see Units#valueOf(String)
     */
    public Unit<?> toUnit(String value) throws IllegalArgumentException {
        if (value != null && !(value = value.trim()).isEmpty()) try {
            return Units.valueOf(value);
        } catch (IllegalArgumentException e) {
            if (!exceptionOccured(value, String.class, Unit.class, e)) {
                throw e;
            }
        }
        return null;
    }

    /**
     * Converts the given string to a Universal Unique Identifier. The default implementation
     * is as below, omitting the check for null value and the call to {@link #exceptionOccured
     * exceptionOccured} in case of error:
     *
     * {@preformat java
     *     return UUID.fromString(value);
     * }
     *
     * @param  value The string to convert to a UUID, or {@code null}.
     * @return The converted UUID, or {@code null} if the given value was null of if an
     *         exception was thrown and {@code exceptionOccured} returned {@code true}.
     * @throws IllegalArgumentException If the given string can not be converted to a UUID.
     *
     * @see UUID#fromString(String)
     *
     * @since 3.13
     */
    public UUID toUUID(String value) throws IllegalArgumentException {
        if (value != null && !(value = value.trim()).isEmpty()) try {
            return UUID.fromString(value);
        } catch (RuntimeException e) { // Multi-catch: IllegalArgumentException & NumberFormatException
            if (!exceptionOccured(value, String.class, UUID.class, e)) {
                throw e;
            }
        }
        return null;
    }

    /**
     * Converts the given string to a URI. The default implementation first escapes the characters
     * that the {@link URI#URI(String) URI(String)} constructor would not accept (for example
     * replacing space by {@code %20}), then performs the following work (omitting the check for
     * null value and the call to {@link #exceptionOccured exceptionOccured} in case of error):
     *
     * {@preformat java
     *     return new URI(escapedValue);
     * }
     *
     * @param  value The string to convert to a URI, or {@code null}.
     * @return The converted URI, or {@code null} if the given value was null of if an
     *         exception was thrown and {@code exceptionOccured} returned {@code true}.
     * @throws URISyntaxException If the given string can not be converted to a URI.
     *
     * @see URI#URI(String)
     */
    public URI toURI(String value) throws URISyntaxException {
        if (value != null && !(value = value.trim()).isEmpty()) try {
            return new URI(IOUtilities.encodeURI(value));
        } catch (URISyntaxException e) {
            if (!exceptionOccured(value, String.class, URI.class, e)) {
                throw e;
            }
        }
        return null;
    }

    /**
     * Converts the given URL to a URI. The default implementation is as below, omitting
     * the check for null value and the call to {@link #exceptionOccured exceptionOccured}
     * in case of error:
     *
     * {@preformat java
     *     return value.toURI();
     * }
     *
     * @param  value The URL to convert to a URI, or {@code null}.
     * @return The converted URI, or {@code null} if the given value was null of if an
     *         exception was thrown and {@code exceptionOccured} returned {@code true}.
     * @throws URISyntaxException If the given URL can not be converted to a URI.
     *
     * @see URL#toURI()
     */
    public URI toURI(final URL value) throws URISyntaxException {
        if (value != null) try {
            return value.toURI();
        } catch (URISyntaxException e) {
            if (!exceptionOccured(value, URL.class, URI.class, e)) {
                throw e;
            }
        }
        return null;
    }

    /**
     * Converts the given URI to a URL. The default implementation is as below, omitting
     * the check for null value and the call to {@link #exceptionOccured exceptionOccured}
     * in case of error:
     *
     * {@preformat java
     *     return value.toURL();
     * }
     *
     * @param  value The URI to convert to a URL, or {@code null}.
     * @return The converted URL, or {@code null} if the given value was null of if an
     *         exception was thrown and {@code exceptionOccured} returned {@code true}.
     * @throws MalformedURLException If the given URI can not be converted to a URL.
     *
     * @see URI#toURL()
     */
    public URL toURL(final URI value) throws MalformedURLException {
        if (value != null) try {
            return value.toURL();
        } catch (MalformedURLException e) {
            if (!exceptionOccured(value, URI.class, URL.class, e)) {
                throw e;
            }
        } catch (IllegalArgumentException e) {
            if (!exceptionOccured(value, URI.class, URL.class, e)) {
                throw e;
            }
        }
        return null;
    }

    /**
     * Converts the given string to a {@code NilReason}. The default implementation is as below,
     * omitting the check for null value and the call to {@link #exceptionOccured exceptionOccured}
     * in case of error:
     *
     * {@preformat java
     *     return NilReason.valueOf(value);
     * }
     *
     * @param  value The string to convert to a nil reason, or {@code null}.
     * @return The converted nil reason, or {@code null} if the given value was null of if an
     *         exception was thrown and {@code exceptionOccured} returned {@code true}.
     * @throws URISyntaxException If the given string can not be converted to a nil reason.
     *
     * @see NilReason#valueOf(String)
     *
     * @since 3.18
     */
    public NilReason toNilReason(String value) throws URISyntaxException {
        if (value != null && !(value = value.trim()).isEmpty()) try {
            return NilReason.valueOf(value);
        } catch (URISyntaxException e) {
            if (!exceptionOccured(value, String.class, URI.class, e)) {
                throw e;
            }
        }
        return null;
    }
}
