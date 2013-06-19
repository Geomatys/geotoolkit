/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.image.io.mosaic;

import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Collections;
import java.util.Iterator;
import java.io.IOException;
import java.util.Locale;
import javax.imageio.ImageReader;
import javax.imageio.spi.ImageReaderSpi;

import org.apache.sis.util.logging.Logging;
import org.apache.sis.util.Classes;
import org.geotoolkit.internal.io.IOUtilities;
import static org.geotoolkit.image.io.mosaic.Tile.LOGGER;


/**
 * Cache the {@link ImageReader} instances used by {@link MosaicImageReader}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.18
 *
 * @since 3.18 (derived from 2.5)
 * @module
 */
final class TileReaderPool {
    /**
     * The locale to be given to the readers created by {@link #createReaderInstance(ImageReaderSpi)}.
     */
    private Locale locale;

    /**
     * An unmodifiable view of {@link #readers} keys.
     *
     * @see #setProviders(Set)
     */
    final Set<ImageReaderSpi> providers;

    /**
     * The image reader created for each provider. Keys must be the union of
     * {@link TileManager#getImageReaderSpis} at every image index and must be computed right at
     * {@link MosaicImageReader#setInput} invocation time. Values will be initially {@code null}
     * - a reader will be assigned only when first needed.
     */
    private final Map<ImageReaderSpi,ImageReader> readers;

    /**
     * The input given to each image reader. Values are {@linkplain Tile#getInput tile input}
     * before they have been wrapped in an {@linkplain ImageInputStream image input stream}.
     *
     * @see MosaicImageReadParam#readers
     */
    private final Map<ImageReader,Object> readerInputs;

    /**
     * Creates a new, initially empty, cache.
     */
    TileReaderPool() {
        readers = new HashMap<>();
        readerInputs = new IdentityHashMap<>();
        providers = Collections.unmodifiableSet(readers.keySet());
    }

    /**
     * Sets the {@linkplain #providers}.
     *
     * @param providers The new set of providers.
     */
    final void setProviders(final Set<ImageReaderSpi> providers) {
        /*
         * Closes the ImageReader that are no longuer in use given the new providers.
         */
        final Iterator<Map.Entry<ImageReaderSpi,ImageReader>> it = readers.entrySet().iterator();
        while (it.hasNext()) {
            final Map.Entry<ImageReaderSpi,ImageReader> entry = it.next();
            if (!providers.contains(entry.getKey())) {
                final ImageReader reader = entry.getValue();
                if (reader != null) {
                    /*
                     * Closes previous streams, if any. It is not a big deal if this operation
                     * fails, since we will not use anymore the old streams anyway. However it
                     * is worth to log.
                     */
                    final Object rawInput = readerInputs.remove(reader);
                    final Object tileInput = reader.getInput();
                    if (rawInput != tileInput) try {
                        IOUtilities.close(tileInput);
                    } catch (IOException exception) {
                        Logging.unexpectedException(LOGGER, TileReaderPool.class, "setInput", exception);
                    }
                    reader.dispose();
                }
                it.remove();
            }
        }
        /*
         * Adds entries for the new providers that did not existed previously.
         */
        for (final ImageReaderSpi provider : providers) {
            if (!readers.containsKey(provider)) {
                readers.put(provider, null);
            }
        }
        assert providers.equals(this.providers);
        assert readers.values().containsAll(readerInputs.keySet());
    }

    /**
     * Creates a new {@link ImageReader} from the specified provider. This method do not
     * check the cache and do not store the result in the cache. It should be invoked by
     * {@link #getTileReader} and {@link #getTileReaders} methods only.
     * <p>
     * It is technically possible to return the same {@link ImageReader} instance from
     * different {@link ImageReaderSpi}. It would broke the usual {@code ImageReaderSpi}
     * contract for no obvious reason, but technically this class should work correctly
     * even in such case.
     *
     * @param  provider The provider. Must be a member of {@link #getTileReaderSpis}.
     * @return The image reader for the given provider.
     * @throws IOException if the image reader can not be created.
     */
    private ImageReader createReaderInstance(final ImageReaderSpi provider) throws IOException {
        final ImageReader reader = provider.createReaderInstance();
        if (locale != null) {
            try {
                reader.setLocale(locale);
            } catch (IllegalArgumentException e) {
                // Invalid locale. Ignore this exception since it will not prevent the image
                // reader to work mostly as expected (warning messages may be in a different
                // locale, which is not a big deal).
                Logging.recoverableException(LOGGER, TileReaderPool.class, "getTileReader", e);
            }
        }
        return reader;
    }

    /**
     * Returns the image reader for the given provider.
     *
     * @param  provider The provider. Must be a member of {@link #getTileReaderSpis}.
     * @return The image reader for the given provider.
     * @throws IOException if the image reader can not be created.
     */
    final ImageReader getTileReader(final ImageReaderSpi provider) throws IOException {
        assert readers.containsKey(provider); // Key should exists even if the value is null.
        ImageReader reader = readers.get(provider);
        if (reader == null) {
            reader = createReaderInstance(provider);
            readers.put(provider, reader);
        }
        return reader;
    }

    /**
     * Returns every readers used for reading tiles. New readers may be created on the fly
     * by this method.  However failure to create them will be logged rather than trown as
     * an exception. In such case the information obtained by the caller may be incomplete
     * and the exception may be thrown later when {@link #getTileReader} will be invoked.
     */
    final Set<ImageReader> getTileReaders() {
        for (final Map.Entry<ImageReaderSpi,ImageReader> entry : readers.entrySet()) {
            ImageReader reader = entry.getValue();
            if (reader == null) {
                final ImageReaderSpi provider = entry.getKey();
                try {
                    reader = createReaderInstance(provider);
                } catch (IOException exception) {
                    Logging.unexpectedException(LOGGER, TileReaderPool.class, "getTileReaders", exception);
                    continue;
                }
                entry.setValue(reader);
            }
            if (!readerInputs.containsKey(reader)) {
                readerInputs.put(reader, null);
            }
        }
        assert readers.values().containsAll(readerInputs.keySet());
        return readerInputs.keySet();
    }

    /**
     * Returns a reader for the tiles, or {@code null}. This method tries to returns an instance
     * of the most specific reader class. If no suitable instance is found, then it returns
     * {@code null}.
     * <p>
     * This method is typically invoked for fetching an instance of {@code ImageReadParam}. We
     * look for the most specific class because it may contains additional parameters that are
     * ignored by super-classes. If we fail to find a suitable instance, then the caller shall
     * fallback on the {@link ImageReader} default implementation.
     */
    final ImageReader getTileReader() {
        final Set<ImageReader> readers = getTileReaders();
        Class<?> type = Classes.findSpecializedClass(readers);
        while (type!=null && ImageReader.class.isAssignableFrom(type)) {
            for (final ImageReader candidate : readers) {
                if (type.equals(candidate.getClass())) {
                    return candidate;
                }
            }
            type = type.getSuperclass();
        }
        return null;
    }

    /**
     * Sets the current locale of image readers in this cache.
     *
     * @param locale The desired locale, or {@code null}.
     */
    public void setLocale(final Locale locale) throws IllegalArgumentException {
        this.locale = locale;
        for (final ImageReader reader : readers.values()) {
            try {
                reader.setLocale(locale);
            } catch (IllegalArgumentException e) {
                // Locale not supported by the reader. It may occurs
                // if not all readers support the same set of locales.
                Logging.recoverableException(LOGGER, TileReaderPool.class, "setLocale", e);
            }
        }
    }

    /**
     * Returns the raw input (<strong>not</strong> wrapped in an image input stream) for the
     * given reader. This method is invoked by {@link Tile#getImageReader} only.
     */
    final Object getRawInput(final ImageReader reader) {
        return readerInputs.get(reader);
    }

    /**
     * Sets the raw input (<strong>not</strong> wrapped in an image input stream) for the
     * given reader. The input can be set to {@code null}. This method is invoked by
     * {@link Tile#getImageReader} only.
     */
    final void setRawInput(final ImageReader reader, final Object input) {
        readerInputs.put(reader, input);
    }

    /**
     * Closes any image input streams that may be held by tiles.
     * The streams will be opened again when they will be first needed.
     *
     * @throws IOException if error occurred while closing a stream.
     */
    public void close() throws IOException {
        for (final Map.Entry<ImageReader,Object> entry : readerInputs.entrySet()) {
            final ImageReader reader = entry.getKey();
            final Object    rawInput = entry.getValue();
            final Object       input = reader.getInput();
            entry .setValue(null);
            reader.setInput(null);
            if (input != rawInput) {
                IOUtilities.close(input);
            }
        }
    }

    /**
     * Allows any resources held by this cache to be released. This method disposes every
     * {@linkplain Tile#getImageReader tile image readers}.
     * <p>
     * It is the caller responsibility to invoke {@link #close()} before this method.
     */
    public void dispose() {
        readerInputs.clear();
        for (final ImageReader reader : readers.values()) {
            if (reader != null) {
                reader.dispose();
            }
        }
        readers.clear();
    }
}
