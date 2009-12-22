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
package org.geotoolkit.internal.image.io;

import java.util.Locale;
import java.util.Iterator;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.spi.ImageInputStreamSpi;
import javax.imageio.spi.ImageReaderWriterSpi;
import javax.imageio.stream.ImageInputStream;

import org.geotoolkit.lang.Static;
import org.geotoolkit.util.XArrays;
import org.geotoolkit.resources.Errors;


/**
 * Utility methods about image formats.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.07
 *
 * @since 3.01
 * @module
 */
@Static
public final class Formats {
    /**
     * Do not allow instantiation of this class.
     */
    private Formats() {
    }

    /**
     * A callback for performing an arbitrary operation using an {@link ImageReader}
     * selected from a given input. An implementation of this interface is given to
     * {@link Formats#selectImageReader Formats.selectImageReader(...)}.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.05
     *
     * @since 3.05
     * @module
     */
    public static interface ReadCall {
        /**
         * Invoked when a suitable image reader has been found. If the operation fails with
         * an {@link IOException}, then {@link Formats#selectImageReader selectImageReader}
         * will searchs for an other image reader. If none are found, then the first exception
         * will be rethrown.
         * <p>
         * This method should not retain a reference to the image reader, because it will be
         * disposed by the caller.
         *
         * @param  reader The image reader.
         * @throws IOException If an I/O error occured.
         */
        void read(ImageReader reader) throws IOException;

        /**
         * Invoked when a recoverable error occured. Implementors will typically delegate to
         * {@link org.geotoolkit.util.logging.Logging#recoverableException(Class, String, Throwable)}
         * whith appropriate class an method name.
         *
         * @param error The error which occured.
         */
        void recoverableException(Throwable error);
    }

    /**
     * Searchs {@link ImageReader}s that claim to be able to decode the given input, and call
     * {@link ReadCall#filter(ImageReader)} for each of them until a call succeed. If every
     * readers fail with an {@link IOException}, the exception of the first reader is rethrown
     * by this method.
     *
     * @param  input     The input for which image reader are searched.
     * @param  locale    The locale to set to the image readers, or {@code null} if none.
     * @param  callback  The method to call when an {@link ImageReader} seems suitable.
     * @throws IOException If no suitable image reader has been found.
     *
     * @since 3.05
     */
    public static void selectImageReader(final Object input, final Locale locale, final ReadCall callback)
            throws IOException
    {
        boolean     success = false;
        IOException failure = null;
        Object      stream  = input;
attmpt: while (stream != null) { // This loop will be executed at most twice.
            final Iterator<ImageReader> it = ImageIO.getImageReaders(stream);
            while (it.hasNext()) {
                if (stream instanceof ImageInputStream) {
                    ((ImageInputStream) stream).mark();
                }
                final ImageReader reader = it.next();
                reader.setInput(stream, true, false);
                if (locale != null) try {
                    reader.setLocale(locale);
                } catch (IllegalArgumentException e) {
                    // Unsupported locale. Not a big deal, so ignore...
                }
                try {
                    callback.read(reader);
                    success = true;
                    break attmpt;
                } catch (IOException e) {
                    if (failure == null) {
                        failure = e; // Remember only the first failure.
                    }
                } finally {
                    reader.dispose();
                    // Don't bother to reset the stream if we succeeded
                    // and the stream was created by ourself.
                    if (!success || stream == input) {
                        if (stream instanceof ImageInputStream) try {
                            ((ImageInputStream) stream).reset();
                        } catch (IOException e) {
                            if (stream == input) {
                                throw e;
                            }
                            // Failed to reset the stream, but we created
                            // it ourself. So let just create an other one.
                            callback.recoverableException(e);
                            ((ImageInputStream) stream).close();
                            stream = ImageIO.createImageInputStream(input);
                        }
                    }
                }
            }
            /*
             * If we have tried every image readers for the given input, wraps the
             * input in an ImageInputStream (if not already done) and try again.
             */
            if (stream instanceof ImageInputStream) {
                break;
            }
            stream = ImageIO.createImageInputStream(input);
        }
        /*
         * We got a success, or we tried every image readers.
         * Closes the stream only if we created it ourself.
         */
        if (stream != input && stream instanceof ImageInputStream) {
            ((ImageInputStream) stream).close();
        }
        if (!success) {
            if (failure == null) {
                if (input instanceof File && !((File) input).exists()) {
                    failure = new FileNotFoundException(Errors.format(Errors.Keys.FILE_DOES_NOT_EXIST_$1, input));
                } else {
                    failure = new IIOException(Errors.format(Errors.Keys.NO_IMAGE_READER));
                }
            }
            throw failure;
        }
    }

    /**
     * Returns the image reader provider for the given format name. This method prefers
     * standard readers instead than JAI ones, except for the TIFF format for which the
     * JAI reader is preferred.
     * <p>
     * <b>NOTE:</b> The rule for preferring a reader are the some ones than the rules implemented
     * by {@link org.geotoolkit.image.jai.Registry#setDefaultCodecPreferences()}. If the rule in
     * the above methods are modified, the rules in this method shall be modified accordingly.
     *
     * @param  format The name of the provider to fetch, or {@code null}.
     * @param  exclude Base class of readers to exclude, or {@code null} if none.
     * @return The reader provider for the given format, or {@code null} if {@code format} is null.
     * @throws IllegalArgumentException If no provider is found for the given format.
     */
    public static ImageReaderSpi getReaderByFormatName(String format,
            final Class<? extends ImageReaderSpi> exclude) throws IllegalArgumentException
    {
        if (format == null) {
            return null;
        }
        format = format.trim();
        ImageReaderSpi fallback = null;
        final boolean preferJAI = format.equalsIgnoreCase("TIFF");
        final IIORegistry registry = IIORegistry.getDefaultInstance();
        final Iterator<ImageReaderSpi> it=registry.getServiceProviders(ImageReaderSpi.class, true);
        while (it.hasNext()) {
            final ImageReaderSpi provider = it.next();
            if (exclude != null && exclude.isInstance(provider)) {
                continue;
            }
            if (XArrays.contains(provider.getFormatNames(), format)) {
                /*
                 * NOTE: The following method uses the same rule for identifying JAI codecs.
                 *       If we change the way to identify those codecs here, we should do the
                 *       same for the other method.
                 *
                 * org.geotoolkit.image.jai.Registry.setNativeCodecAllowed(String, Class, boolean)
                 */
                if (provider.getClass().getName().startsWith("com.sun.media.") == preferJAI) {
                    return provider;
                }
                if (fallback == null) {
                    fallback = provider;
                }
            }
        }
        if (fallback != null) {
            return fallback;
        }
        throw new IllegalArgumentException(Errors.format(Errors.Keys.UNKNOW_IMAGE_FORMAT_$1, format));
    }

    /**
     * Returns the name of the given provider, or {@code null} if the name is unknown.
     * If the provider declares many names, the longuest name is selected. If many names
     * have the same length, the first one having at least one upper-case character is
     * selected. This allows this method to return {@code "PNG"} instead than {@code "png"}.
     *
     * @param  provider The provider for which we want the name, or {@code null}.
     * @return The name of the given provider, or {@code null} if none.
     *
     * @since 3.07
     */
    public static String getFormatName(final ImageReaderWriterSpi provider) {
        boolean hasUpperCase = false;
        String name = null;
        if (provider != null) {
            final String[] formats = provider.getFormatNames();
            if (formats != null) {
                int length = 0;
                for (int i=0; i<formats.length; i++) {
                    final String candidate = formats[i];
                    if (candidate != null) {
                        final int lg = candidate.length();
                        if (lg > length) {
                            length = lg;
                            name = candidate;
                            hasUpperCase = hasUpperCase(name);
                        } else if (!hasUpperCase && lg == length && hasUpperCase(candidate)) {
                            hasUpperCase = true;
                            name = candidate;
                        }
                    }
                }
            }
        }
        return name;
    }

    /**
     * Returns {@code true} if the given string has at least one upper-case character.
     */
    private static boolean hasUpperCase(final String name) {
        for (int i=name.length(); --i>=0;) {
            final char c = name.charAt(i);
            if (Character.toUpperCase(c) == c) {
                return true;
            }
        }
        return false;
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
     * @throws IOException If an error occured while creating the stream.
     *
     * @since 3.07
     */
    public static ImageInputStream createUncachedImageInputStream(final Object input) throws IOException {
        ImageInputStreamSpi fallback = null;
        final Iterator<ImageInputStreamSpi> it = IIORegistry.getDefaultInstance()
                .getServiceProviders(ImageInputStreamSpi.class, true);
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
}
