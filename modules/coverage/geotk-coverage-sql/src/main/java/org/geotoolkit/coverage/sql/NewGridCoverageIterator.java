/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2007-2010, Geomatys
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
package org.geotoolkit.coverage.sql;

import java.util.Map;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.IIOException;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;

import org.opengis.referencing.FactoryException;

import org.geotoolkit.resources.Errors;
import org.geotoolkit.image.io.XImageIO;
import org.geotoolkit.image.io.mosaic.Tile;
import org.geotoolkit.internal.sql.table.SpatialDatabase;
import org.geotoolkit.util.collection.BackingStoreException;
import org.geotoolkit.util.converter.Classes;


/**
 * An iterator creating {@link NewGridCoverageReference} on-the-fly using different input source.
 * The iterator reuse a unique {@link ImageReader} instance when possible. Each iterator is for a
 * single {@link SeriesEntry} only.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.12
 *
 * @since 3.12 (derived from Seagis)
 * @module
 */
final class NewGridCoverageIterator implements Iterator<NewGridCoverageReference> {
    /**
     * The object which contains the listeners. While we are keeping a reference to the
     * full {@link CoverageDatabase} objects, only the listeners are of interest to this
     * iterator.
     */
    private final CoverageDatabase listeners;

    /**
     * An optional controller to invoke before the listeners, or {@code null} if none.
     */
    private final CoverageDatabaseController controller;

    /**
     * The database where new entries will be added.
     */
    private final SpatialDatabase database;

    /**
     * The series in which the images will be added, or {@code null} if unknown.
     */
    private final SeriesEntry series;

    /**
     * Index of image to read. Ignored if the inputs are {@link Tile} instances.
     */
    private final int imageIndex;

    /**
     * An iterator over the inputs to read. If elements are {@link java.util.Map.Entry}, then the
     * key is selected as the input provided that the value is equals to the {@linkplain #series}.
     * Otherwise the entry is discarted.
     * <p>
     * If {@link #series} if non-null, the {@link Iterator#remove} method will be invoked for each
     * elements which has not been omitted. This is required by {@link WritableGridCoverageTable}.
     * <p>
     * If input are {@link File} or {@link URI}, they shall be relative to current directory.
     * Inputs may also be {@link Tile} or {@link ImageReader} instances.
     */
    private final Iterator<?> inputToAdd;

    /**
     * The next entry to return, or {@code null} if we have reached the iteration end.
     */
    private NewGridCoverageReference next;

    /**
     * The image reader inferred from the information declared in the {@link SeriesEntry}.
     * Will be created when first needed.
     */
    private ImageReader seriesReader;

    /**
     * The legal input types of the {@link #seriesReader}, or {@code null}.
     * This is created when {@link #seriesReader} is initialized.
     */
    private Class<?>[] readerInputTypes;

    /**
     * Creates an iterator for the specified files.
     *
     * @param  listeners  The object which hold the {@link CoverageDatabaseListener}s. While this
     *                    argument is of kind {@link CoverageDatabase}, only the listeners are of
     *                    interest to this class.
     * @param  controller An optional controller to invoke before the listeners, or {@code null}.
     * @param  database   The database where new entries will be added. This is mandatory.
     * @param  series     The series in which the images will be added, or {@code null} if unknown.
     * @param  imageIndex Index of images to read. Ignored if the inputs are {@link Tile} instances.
     * @param  inputToAdd The files to read. Iteration shall be at the second element.
     * @param  input      The first element from the given iterator.
     * @throws IOException if an I/O operation was required and failed.
     */
    NewGridCoverageIterator(final CoverageDatabase           listeners,
                            final CoverageDatabaseController controller,
                            final SpatialDatabase            database,
                            final SeriesEntry                series,
                            final int                        imageIndex,
                            final Iterator<?>                inputToAdd,
                            Object input) throws IOException, FactoryException
    {
        this.listeners  = listeners;
        this.controller = controller;
        this.database   = database;
        this.series     = series;
        this.imageIndex = imageIndex;
        this.inputToAdd = inputToAdd;
        do {
            next = createEntry(input);
        } while (next == null && (input = nextInput()) != null);
    }

    /**
     * Returns {@code true} if there is more entry to iterate over.
     */
    @Override
    public boolean hasNext() {
        return next != null;
    }

    /**
     * Creantes an entry for the given input.
     *
     * @param  input The input, or {@code null} if none.
     * @return The entry, or {@code null} if the given input should be skipped.
     * @throws IOException if an I/O operation was required and failed.
     */
    private NewGridCoverageReference createEntry(Object input) throws IOException, FactoryException {
        if (input == null) {
            return null;
        }
        if (input instanceof NewGridCoverageReference) {
            return (NewGridCoverageReference) input;
        }
        if (input instanceof Tile) {
            return new NewGridCoverageReference(database, (Tile) input);
        }
        final ImageReader reader;
        if (input instanceof ImageReader) {
            reader = (ImageReader) input;
            input = reader.getInput();
        } else if (series == null) {
            reader = XImageIO.getReaderBySuffix(input, true, false);
        } else {
            /*
             * If we are adding into a specific series, use the reader of that series.
             * We must set the input outself, as required by the NewGridCoverageReference
             * constructor.
             */
            if (seriesReader == null) {
                final String format = series.format.imageFormat;
                final Iterator<ImageReader> it = ImageIO.getImageReadersByFormatName(format);
                if (it.hasNext()) {
                    seriesReader = it.next();
                } else {
                    throw new IIOException(Errors.format(Errors.Keys.UNKNOWN_IMAGE_FORMAT_$1, format));
                }
                final ImageReaderSpi spi = seriesReader.getOriginatingProvider();
                if (spi != null) {
                    readerInputTypes = spi.getInputTypes();
                }
            }
            reader = seriesReader;
            Object imageInput = input;
            if (!Classes.isAssignableTo(input.getClass(), readerInputTypes)) {
                if (Classes.isAssignableTo(ImageInputStream.class, readerInputTypes)) {
                    imageInput = ImageIO.createImageInputStream(input);
                }
            }
            reader.setInput(imageInput, true, false);
        }
        return new NewGridCoverageReference(database, reader, input, imageIndex);
    }

    /**
     * Returns the next entry to add to the database.
     */
    @Override
    public NewGridCoverageReference next() {
        final NewGridCoverageReference entry = next;
        if (entry == null) {
            throw new NoSuchElementException();
        }
        next = null;
        do {
            final Object input = nextInput();
            if (input == null) {
                break;
            }
            try {
                next = createEntry(input);
            } catch (IOException exception) { // TODO: multi-catch
                // Will be unwrapped by WritableGridCoverageTable.
                throw new BackingStoreException(exception);
            } catch (FactoryException exception) {
                throw new BackingStoreException(exception);
            }
        } while (next == null);
        entry.series = series;
        return entry;
    }

    /**
     * Returns the next elements (skipping {@code null} values) from the {@link #inputToAdd}
     * iterator, or {@code null} if we have reached the iteration end. The elements returned
     * by this method may be removed from the backing collection; see {@link #inputToAdd} for
     * more information.
     *
     * @return The next input, or {@code null} if we have reached iteration end.
     */
    private Object nextInput() {
        while (inputToAdd.hasNext()) {
            Object input = inputToAdd.next();
            if (input != null) {
                if (input instanceof Map.Entry<?,?>) {
                    final Map.Entry<?,?> candidate = (Map.Entry<?,?>) input;
                    if (series != null && !series.equals(candidate.getValue())) {
                        continue;
                    }
                    input = candidate.getKey();
                }
                if (series != null) {
                    inputToAdd.remove();
                }
                return input;
            }
        }
        return null;
    }

    /**
     * Invoked by {@link WritableGridCoverageTable} after a {@link NewGridCoverageReference}
     * element has been fully constructed.
     *
     * @param  isBefore {@code true} if the event is invoked before the change,
     *                  or {@code false} if the event occurs after the change.
     * @param  value    The entry which is added.
     * @throws DatabaseVetoException if {@code isBefore} is {@code true} and a listener vetoed
     *         against the change.
     */
    final void fireCoverageAdding(final boolean isBefore, final NewGridCoverageReference value)
            throws DatabaseVetoException
    {
        if (isBefore && controller != null) {
            controller.coverageAdding(new CoverageDatabaseEvent(listeners, isBefore, 1), value);
        }
        if (listeners != null) {
            listeners.fireChange(isBefore, 1, value);
        }
    }

    /**
     * Unsupported operation.
     */
    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns a string representation for debugging purpose.
     */
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder(getClass().getSimpleName());
        buffer.append('[');
        if (series != null) {
            buffer.append("series=\"").append(series).append("\", ");
        }
        if (seriesReader != null) {
            buffer.append("reader=").append(seriesReader.getClass().getSimpleName()).append(", ");
        }
        return buffer.append("imageIndex=").append(imageIndex).append(']').toString();
    }
}
