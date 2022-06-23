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
package org.geotoolkit.internal.image.io;

import java.util.Arrays;
import java.util.Locale;
import java.util.Iterator;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.spi.IIOServiceProvider;
import javax.imageio.spi.ImageInputStreamSpi;
import javax.imageio.spi.ImageReaderWriterSpi;
import javax.imageio.stream.ImageInputStream;
import org.geotoolkit.factory.Factories;
import org.geotoolkit.lang.Static;
import org.apache.sis.util.ArraysExt;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.resources.Vocabulary;


/**
 * Utility methods about image formats.
 */
public final class Formats extends Static {
    /**
     * Do not allow instantiation of this class.
     */
    private Formats() {
    }

    /**
     * Returns the image reader provider for the given format name.
     *
     * @param  format The name of the provider to fetch, or {@code null}.
     * @param  exclude Base class of readers to exclude, or {@code null} if none.
     * @return The reader provider for the given format, or {@code null} if {@code format} is null.
     * @throws IllegalArgumentException If no provider is found for the given format.
     */
    public static ImageReaderSpi getReaderByFormatName(final String format,
            final Class<? extends ImageReaderSpi> exclude) throws IllegalArgumentException
    {
        return getByFormatName(ImageReaderSpi.class, format, exclude);
    }

    /**
     * Returns the image writer provider for the given format name.
     *
     * @param  format The name of the provider to fetch, or {@code null}.
     * @param  exclude Base class of writers to exclude, or {@code null} if none.
     * @return The writer provider for the given format, or {@code null} if {@code format} is null.
     * @throws IllegalArgumentException If no provider is found for the given format.
     */
    public static ImageWriterSpi getWriterByFormatName(final String format,
            final Class<? extends ImageWriterSpi> exclude) throws IllegalArgumentException
    {
        return getByFormatName(ImageWriterSpi.class, format, exclude);
    }

    /**
     * Implementation of {@link #getReaderByFormatName} and {@link #getWriterByFormatName}.
     */
    private static <T extends ImageReaderWriterSpi> T getByFormatName(
            final Class<T> type, String format, final Class<? extends T> exclude)
            throws IllegalArgumentException
    {
        if (format == null) {
            return null;
        }
        format = format.trim();
        T fallback = null;
        final IIORegistry registry = IIORegistry.getDefaultInstance();
        final Iterator<T> it=orderForClassLoader(registry.getServiceProviders(type, true));
        while (it.hasNext()) {
            final T provider = it.next();
            if (exclude != null && exclude.isInstance(provider)) {
                continue;
            }
            if (ArraysExt.contains(provider.getFormatNames(), format)) {
                if (fallback == null) {
                    fallback = provider;
                }
            }
        }
        if (fallback != null) {
            return fallback;
        }
        throw new IllegalArgumentException(Errors.format(Errors.Keys.UnknownImageFormat_1, format));
    }

    /**
     * Returns the name of the given provider, or {@code null} if the name is unknown.
     * If the provider declares many names, the longest name is selected. If many names
     * have the same length, the one having at largest number of upper-case characters is
     * selected. This allows this method to return {@code "PNG"} instead than {@code "png"}.
     * <p>
     * If no format name has been found, then this method fallback on the shortest MIME type.
     * Note that the use of shortest MIME type is the opposite of the longest name, but this
     * is done that way in order to prefer {@code "image/png"} rather than {@code "image/x-png"}
     * for example.
     *
     * @param  provider The provider for which we want the name, or {@code null}.
     * @return The name of the given provider, or {@code null} if none.
     *
     * @since 3.07
     */
    public static String getDisplayName(final ImageReaderWriterSpi provider) {
        String name = null;
        if (provider != null) {
            String[] formats = provider.getFormatNames();
            if (formats != null) {
                for (final String candidate : formats) {
                    int d = candidate.length();
                    if (d != 0) {
                        if (name != null) {
                            d -= name.length();
                        }
                        if (d >= 0) {
                            if (d == 0) {
                                int na=0, nb=0;
                                for (int i=candidate.length(); --i>=0;) {
                                    if (Character.isUpperCase(candidate.charAt(i))) na++;
                                    if (Character.isUpperCase(name   .charAt(i))) nb++;
                                }
                                if (na <= nb) {
                                    continue;
                                }
                            }
                            name = candidate;
                        }
                    }
                }
            }
            /*
             * If no format has been found, fallback on MIME types.
             */
            if (name == null) {
                formats = provider.getMIMETypes();
                if (formats != null) {
                    for (final String candidate : formats) {
                        final int length = candidate.length();
                        if (length != 0 && (name == null || length < name.length())) {
                            name = candidate;
                        }
                    }
                }
            }
        }
        return name;
    }

    /**
     * Simplifies the given array of format names, MIME types or file suffixes.
     * This method sorts the elements by alphabetical order, ignoring cases,
     * and remove duplicated values.
     *
     * @param choices The array to simplify.
     * @return The simplified array.
     *
     * @since 3.10
     */
    public static String[] simplify(String... choices) {
        if (choices != null) {
            Arrays.sort(choices, String.CASE_INSENSITIVE_ORDER);
            int count = 0;
            for (int i=1; i<choices.length; i++) {
                final String o1 = choices[i-1];
                final String o2 = choices[i];
                if (!o1.equalsIgnoreCase(o2)) {
                    choices[count++] = o1;
                } else if (o1.compareTo(o2) > 0) {
                    choices[i-1] = o2; // Order lower-cases before upper-cases.
                    choices[i]   = o1;
                }
            }
            choices = ArraysExt.resize(choices, count);
        }
        return choices;
    }

    /**
     * Formats a description from the information provided in the given provider.
     *
     * @param spi      The provider from which to extract the information.
     * @param locale   The locale to use for localizing the description.
     * @param appendTo The buffer where to append the description.
     *
     * @since 3.15
     */
    public static void formatDescription(final IIOServiceProvider spi, final Locale locale,
            final StringBuilder appendTo)
    {
        final Vocabulary resources = Vocabulary.getResources(locale);
        String text = spi.getDescription(locale);
        if (text == null) {
            text = resources.getString(Vocabulary.Keys.Unknown);
        }
        appendTo.append(text);
        text = spi.getVersion();
        if (text != null) {
            appendTo.append(" (").append(resources.getString(Vocabulary.Keys.Version_1, text));
            text = spi.getVendorName();
            if (text != null) {
                appendTo.append(", ").append(text);
            }
            appendTo.append(')');
        }
    }

    /**
     * Wraps the given input in an {@link ImageInputStream}, given preference to uncached streams
     * if possible. It may be faster when reading small images or when reading just the first few
     * bytes (for example in order to determine if a file is in a known format).
     * <p>
     * If no uncached stream can be created, this method fallbacks on the default cached stream.
     *
     * @param  input The input for which we want an image input stream.
     * @return The image input stream, or {@code null} if no suitable stream were found.
     * @throws IOException If an error occurred while creating the stream.
     *
     * @since 3.07
     */
    public static ImageInputStream createUncachedImageInputStream(final Object input) throws IOException {
        ImageInputStreamSpi fallback = null;
        final Iterator<ImageInputStreamSpi> it = orderForClassLoader(
                IIORegistry.getDefaultInstance().getServiceProviders(ImageInputStreamSpi.class, true));
        while (it.hasNext()) {
            final ImageInputStreamSpi spi = it.next();
            if (spi.getInputClass().isInstance(input)) {
                if (!spi.needsCacheFile()) {
                    return spi.createInputStreamInstance(input, false, ImageIO.getCacheDirectory());
                }
                if (fallback == null) {
                    fallback = spi;
                }
            }
        }
        if (fallback != null) {
            return fallback.createInputStreamInstance(input, false, ImageIO.getCacheDirectory());
        }
        return null;
    }

    /**
     * Returns an iterator giving precedence to classes loaded by the Geotk class loader or one
     * of its children.
     */
    private static <T> Iterator<T> orderForClassLoader(final Iterator<T> iterator) {
        return Factories.orderForClassLoader(Formats.class.getClassLoader(), iterator);
    }
}
