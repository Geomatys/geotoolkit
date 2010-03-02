/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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
import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.spi.ImageInputStreamSpi;
import javax.imageio.spi.ImageReaderWriterSpi;
import javax.imageio.stream.ImageInputStream;

import org.geotoolkit.lang.Static;
import org.geotoolkit.util.XArrays;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.internal.io.IOUtilities;


/**
 * Utility methods about image formats.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.08
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
     * {@link ReadCall#read(ImageReader)} for each of them until a call succeed. If every
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
        /*
         * If the given input is a file, URL or URI with a suffix, extract the suffix.
         * We will try the ImageReader for that suffix before to try any other readers.
         */
        final String suffix = IOUtilities.extension(input);
        boolean useSuffix = (suffix != null && suffix.length() != 0);
        int nextProviderForSuffix = 0; // Used for sorting first the providers for the suffix.
        /*
         * The list of providers that we have found during the first execution of the
         * loop below. For every loop execution after the first one, we will use that
         * list instead of the IIORegistry iterator.
         */
        final List<ImageReaderSpi> providers = new LinkedList<ImageReaderSpi>();
        boolean useProvidersList = false;
        /*
         * The state of this method (whatever we have found a reader, or failed).
         */
        boolean         success = false;
        IOException     failure = null;
        ImageInputStream stream = null;
        Object    inputOrStream = input;
attmpt: while (true) {
            /*
             * On first execution, iterate over the provider given by IIORegistry.
             * For all other execution, iterate over the providers in our list.
             */
            int index = 0;
            final Iterator<ImageReaderSpi> it = useProvidersList ? providers.iterator() :
                    IIORegistry.getDefaultInstance().getServiceProviders(ImageReaderSpi.class, true);
            while (it.hasNext()) {
                final ImageReaderSpi provider = it.next();
                /*
                 * If this is the first iteration, then add the provider in the list. We add
                 * the providers in iteration order, except if the suffixes match in which case
                 * we add the providers at the begining of the list (so we try them first if we
                 * perform an other iteration later).
                 *
                 * Note: useSuffix and useProvidersList are not modified in this inner loop.
                 */
                if (!useProvidersList) {
                    if (!useSuffix) {
                        providers.add(provider);
                    } else if (XArrays.contains(provider.getFileSuffixes(), suffix)) {
                        providers.add(nextProviderForSuffix++, provider);
                    } else {
                        providers.add(provider);
                        continue; // Suffixes doesn't match: skip (for now) this provider.
                    }
                } else if (useSuffix) {
                    /*
                     * This block is executed during the second iteration. We are still trying
                     * the ImageReader for the suffix, but this time using an ImageInputStream
                     * input. Remove the providers since we will not try them again in case of
                     * failure.
                     */
                    if (index++ == nextProviderForSuffix) {
                        break; // Reached the first provider which doesn't have the right suffix.
                    }
                    it.remove();
                }
                /*
                 * If the provider thinks that the input can't be read, skip it for now.
                 * It may be tried again with a different input in the next loop execution.
                 */
                if (!provider.canDecodeInput(inputOrStream)) {
                    continue;
                }
                /*
                 * Configure the ImageReader, then tries to read the image. In case of success,
                 * we are done and will exit from the loop. In case of failure, we will report
                 * the error and continue with the next providers.
                 */
                final ImageReader reader = provider.createReaderInstance();
                if (inputOrStream instanceof ImageInputStream) {
                    ((ImageInputStream) inputOrStream).mark();
                }
                reader.setInput(inputOrStream, true, false);
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
                    /*
                     * Reset the stream to its initial state if:
                     *
                     * - There is a failure, because we will need the
                     *   stream again for trying the next ImageReaders.
                     *
                     * - Or if the stream was provided by the caller,
                     *   because the caller may want to use it again.
                     */
                    reader.dispose();
                    if (!success || inputOrStream == input) {
                        if (inputOrStream instanceof ImageInputStream) try {
                            ((ImageInputStream) inputOrStream).reset();
                        } catch (IOException e) {
                            // If the stream was provided by the caller, we can not
                            // create a new one. So consider the error as fatal.
                            if (inputOrStream == input) {
                                throw e;
                            }
                            // Failed to reset the stream, but we created
                            // it ourself. So let just create an other one.
                            callback.recoverableException(e);
                            ((ImageInputStream) inputOrStream).close();
                            inputOrStream = stream = ImageIO.createImageInputStream(input);
                            if (stream == null) {
                                throw e;
                            }
                        }
                    }
                }
            }
            /*
             * At this point we finished to iterate over every ImageReader providers, but
             * we didn't found any suitable one. Switch the function to the next state,
             * which are in order:
             *
             *   1) Only ImageReaders for the suffix, using the input supplied by the caller.
             *   2) Only ImageReaders for the suffix, using a new ImageInputStream input.
             *   3) All ImageReaders, using the input supplied by the caller.
             *   4) All ImageReaders, using the ImageInputStream created at step 2.
             *   5) End of the attempts: failure.
             *
             * The next state is inferred from the previous state. A simple 'switch' statement
             * using a state number is not convenient because any of the above states may be
             * skipped, for example if no ImageReaders were found for the suffix (in which case
             * we try immediately all ImageReaders), or if the caller input is already an
             * ImageInputStream.
             */
            if (inputOrStream instanceof ImageInputStream) {
                /*
                 * If we have run the most extensive case (all available
                 * ImageReaders with an ImageInputStream input), give up.
                 */
                if (!useSuffix) {
                    break;
                }
                /*
                 * The previous run was using a limited set of ImageReaders. Try again,
                 * but now using all remaining ImageReaders. Note that when we switched
                 * the 'useSuffix' flag to 'false', we never switch it back to 'true'.
                 *
                 * If we were using an ImageInputStream, try with the original input.
                 * Note that we keep the ImageInputStream (referenced by the 'stream'
                 * variable) in order to use it again if the upcomming try fails.
                 */
                useSuffix = false;
                if (inputOrStream != input) {
                    inputOrStream = input;
                }
            } else {
                /*
                 * The input can not be used directly. We may need to create an ImageInputStream.
                 * But before doing so, check if we tried at least one ImageReader. If not, try
                 * all ImageReaders with the original input before to create an ImageInputStream.
                 */
                if (useSuffix && nextProviderForSuffix == 0) {
                    useSuffix = false;
                } else {
                    /*
                     * Failed to read the image using the caller input.
                     * Wraps it in an ImageInputStream and try again.
                     */
                    if (stream == null) {
                        stream = ImageIO.createImageInputStream(input);
                        if (stream == null) {
                            if (!useSuffix) {
                                break;
                            }
                            useSuffix = false;
                            inputOrStream = input;
                        }
                    }
                    inputOrStream = stream;
                }
            }
            useProvidersList = true;
        }
        /*
         * We got a success, or we tried every image readers. Close the
         * stream only if we created it ourself (i.e. inputOrStream != input).
         */
        if (stream != null) {
            stream.close();
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
     * <b>NOTE:</b> The rule for preferring a reader are the same ones than the rules implemented
     * by {@link org.geotoolkit.image.jai.Registry#setDefaultCodecPreferences()}. If the rule in
     * the above methods are modified, the rules in this method shall be modified accordingly.
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
     * Returns the image writer provider for the given format name. This method prefers
     * standard writers instead than JAI ones, except for the TIFF format for which the
     * JAI writer is preferred.
     * <p>
     * <b>NOTE:</b> The rule for preferring a writer are the same ones than the rules implemented
     * by {@link org.geotoolkit.image.jai.Registry#setDefaultCodecPreferences()}. If the rule in
     * the above methods are modified, the rules in this method shall be modified accordingly.
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
        final boolean preferJAI = format.equalsIgnoreCase("TIFF");
        final IIORegistry registry = IIORegistry.getDefaultInstance();
        final Iterator<T> it=registry.getServiceProviders(type, true);
        while (it.hasNext()) {
            final T provider = it.next();
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
        throw new IllegalArgumentException(Errors.format(Errors.Keys.UNKNOWN_IMAGE_FORMAT_$1, format));
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
            if (Character.isUpperCase(name.charAt(i))) {
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
