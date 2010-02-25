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
package org.geotoolkit.coverage.io;

import java.util.Locale;
import java.util.MissingResourceException;
import java.io.Closeable;
import java.io.IOException;
import java.awt.image.RenderedImage;
import javax.imageio.ImageReader;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;

import org.geotoolkit.factory.Hints;
import org.geotoolkit.coverage.CoverageFactoryFinder;
import org.geotoolkit.coverage.grid.GridCoverageFactory;
import org.geotoolkit.image.io.XImageIO;
import org.geotoolkit.internal.io.IOUtilities;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.util.XArrays;


/**
 * A {@link GridCoverageReader} implementation which use an {@link ImageReader} for reading
 * sample values. This implementation stores the sample values in a {@link RenderedImage},
 * and consequently is target toward two-dimensional slices of data.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.09
 *
 * @since 3.09 (derived from 2.2)
 * @module
 */
public abstract class ImageCoverageReader extends GridCoverageReader {
    /**
     * The {@link ImageReader} to use for decoding {@link RenderedImage}s. This reader is
     * initially {@code null} and lazily created the first time {@link #setInput(Object)}
     * is invoked. Once created, it is reused for subsequent inputs if possible.
     * <p>
     * Invoking {@link #reset()} disposes the reader and set it back to {@code null}.
     */
    protected ImageReader reader;

    /**
     * Optional parameter to be given (if non-null) to the <code>{@linkplain ImageReader#setInput(Object,
     * boolean, boolean) ImageReader.setInput}(&hellip;, seekForwardOnly, &hellip;)</code> method.
     * If {@code TRUE}, images and metadata may only be read in ascending order from the input
     * source. If {@code FALSE}, they may be read in any order. If {@code null}, then this
     * parameter is not given to the reader which is free to use a plugin-dependent default
     * (usually false).
     */
    protected Boolean seekForwardOnly;

    /**
     * Optional parameter to be given (if non-null) to the <code>{@linkplain ImageReader#setInput(Object,
     * boolean, boolean) ImageReader.setInput}(&hellip;, &hellip;, ignoreMetadata)</code> method.
     * If {@code TRUE}, metadata may be ignored during reads. If {@code FALSE}, metadata will be
     * parsed. If {@code null}, this parameter is not given to the reader which is free to use a
     * plugin-dependent default (usually false).
     */
    protected Boolean ignoreMetadata;

    /**
     * The grid coverage factory to use.
     */
    private final GridCoverageFactory factory;

    /**
     * Creates a new instance using the default factory.
     */
    public ImageCoverageReader() {
        this(null);
    }

    /**
     * Creates a new instance using the factory specified by the given set of hints.
     *
     * @param hints The hints to use for fetching a {@link GridCoverageFactory},
     *        or {@code null} for the default hints.
     */
    public ImageCoverageReader(final Hints hints) {
        this.factory = CoverageFactoryFinder.getGridCoverageFactory(hints);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLocale(final Locale locale) {
        super.setLocale(locale);
        setLocale(reader, locale);
    }

    /**
     * Sets the locale for the given {@link ImageReader}.
     */
    private static void setLocale(final ImageReader reader, final Locale locale) {
        if (reader != null) {
            final Locale[] list = reader.getAvailableLocales();
            for (int i=list.length; --i>=0;) {
                if (locale.equals(list[i])) {
                    reader.setLocale(locale);
                    return;
                }
            }
            final String language = getISO3Language(locale);
            if (language != null) {
                for (int i=list.length; --i>=0;) {
                    final Locale candidate = list[i];
                    if (language.equals(getISO3Language(candidate))) {
                        reader.setLocale(candidate);
                        return;
                    }
                }
            }
            reader.setLocale(null);
        }
    }

    /**
     * Returns the ISO language code for the specified locale, or {@code null} if not available.
     */
    private static String getISO3Language(final Locale locale) {
        try {
            return locale.getISO3Language();
        } catch (MissingResourceException exception) {
            return null;
        }
    }

    /**
     * Sets the input source to the given object. The input is usually a {@link java.io.File},
     * a {@link java.net.URL} or a {@link String} object. But some other types, especially
     * {@link ImageInputStream} and {@link ImageReader}, may be accepted too.
     */
    @Override
    public void setInput(final Object input) throws CoverageStoreException {
        try {
            close();
            if (input != null) {
                if (reader != null) {
                    final ImageReaderSpi provider = reader.getOriginatingProvider();
                    if (provider != null && canReuseImageReader(provider, input)) {
                        reader.setInput(input, seekForwardOnly, ignoreMetadata);
                        return;
                    }
                }
                final ImageReader newReader = createImageReader(input);
                final ImageReader oldReader = reader;
                reader = newReader;
                if (oldReader != null && oldReader != newReader) {
                    oldReader.dispose();
                }
            }
        } catch (IOException e) {
            throw new CoverageStoreException(error(Errors.Keys.NO_IMAGE_READER), e);
        }
    }

    /**
     * Returns {@code true} if the image reader for the given provider can be reused.
     *
     * @param  provider The provider of the image reader.
     * @param  input The input to set to the image reader.
     * @return {@code true} if the image reader can be reused.
     */
    private static boolean canReuseImageReader(final ImageReaderSpi provider, final Object input) {
        final String[] suffixes = provider.getFileSuffixes();
        return suffixes != null && XArrays.containsIgnoreCase(suffixes, IOUtilities.extension(input));
    }

    /**
     * Creates an {@link ImageReader} that claim to be able to decode the image.
     * <p>
     * The default implementation delegates to
     * {@link XImageIO#getReaderBySuffix(Object, Boolean, Boolean)}.
     *
     * @param  input The input source.
     * @return An initialized image reader for reading the given input.
     * @throws IOException If no suitable image reader has been found, or if an error occured
     *         while creating it.
     */
    private ImageReader createImageReader(final Object input) throws IOException {
        return XImageIO.getReaderBySuffix(input, seekForwardOnly, ignoreMetadata);
    }

    /**
     * Closes the input used by the {@link ImageReader}, provided that this is not the input
     * object explicitly given by the user. The {@link ImageReader} is not disposed, so it
     * can be reused for the next image to read.
     *
     * @throws IOException if an error occurs while closing the input.
     */
    private void close() throws IOException {
        if (reader != null) {
            final Object readerInput = reader.getInput();
            reader.setInput(null);
            if (readerInput != input) {
                if (readerInput instanceof Closeable) {
                    ((Closeable) readerInput).close();
                } else if (readerInput instanceof ImageInputStream) {
                    ((ImageInputStream) readerInput).close();
                }
            }
        }
        input = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reset() throws CoverageStoreException {
        final boolean ownReader = (reader != input);
        try {
            close();
        } catch (IOException e) {
            throw new CoverageStoreException(e);
        }
        if (ownReader && reader != null) {
            reader.dispose();
        }
        reader = null;
        super.reset();
    }
}
