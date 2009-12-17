/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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
 * See the {@link Catching} javadoc for an example of registering a custom
 * {@code ObjectConverters} to a (un)marshaller.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.07
 *
 * @since 3.07
 * @module
 */
public class ObjectConverters {
    /**
     * The default, thread-safe and immutable instance. This instance defines the
     * converters used during every (un)marshalling if no {@code ObjectConverters}
     * was {@linkplain Catching#setObjectConverters explicitly set}.
     */
    public static final ObjectConverters DEFAULT = new ObjectConverters();

    /**
     * The {@link ObjectConverters} to use for the ungoing (un)marshalling process.
     */
    static final ThreadLocal<ObjectConverters> CURRENT = new ThreadLocal<ObjectConverters>();

    /**
     * Creates a default {@code ObjectConverters}. This is for subclasses only,
     * since new instances are useful only if at least one method is overriden.
     */
    protected ObjectConverters() {
    }

    /**
     * Returns the converters to use for the ungoing (un)marshalling process.
     * This method is typically invoked in JAXB adapters only.
     *
     * @return The converters to use for the ungoing (un)marshalling process,
     *         or {@link #DEFAULT} if no (un)marshalling process is under way.
     *
     * @level advanced
     */
    public static ObjectConverters current() {
        final ObjectConverters converters = CURRENT.get();
        return (converters != null) ? converters : DEFAULT;
    }

    /**
     * Invoked when an exception occured in any {@code toXXX(...)} method. The default implementation
     * does nothing and return {@code false}, which will cause the (un)marshalling process of the
     * whole XML document to fail.
     * <p>
     * This method provides a single hook that subclasses can override in order to provide their
     * own error handling for every methods defined in this class, like the example documented in
     * the {@link Catching} javadoc. Subclasses also have the possibility to override individual
     * {@code toXXX(...)} methods, like the example provided in this <a href="#skip-navbar_top">class
     * javadoc</a>.
     *
     * @param  <T> The compile-time type of the {@code sourceType} argument.
     * @param  value The value that can't be converted.
     * @param  sourceType The base type of the value to convert. This is determined by the argument
     *         type of the method that catched the exception. For example the source type is always
     *         {@code URI.class} if the exception has been catched by the {@link #toURL(URI)} method.
     * @param  targetType The expected type of the converted object.
     * @param  exception The exception that occured during the attempt to convert.
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
}
