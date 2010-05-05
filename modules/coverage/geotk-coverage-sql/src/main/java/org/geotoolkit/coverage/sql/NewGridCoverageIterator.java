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

import java.awt.Point;
import java.util.Map;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;

import org.opengis.referencing.FactoryException;

import org.geotoolkit.image.io.mosaic.Tile;
import org.geotoolkit.internal.io.IOUtilities;
import org.geotoolkit.internal.sql.table.SpatialDatabase;
import org.geotoolkit.util.collection.BackingStoreException;


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
     * The image reader to use. Will be created when first needed. May never be created if every
     * inputs (the keys in the {@link #inputToAdd} iterator) are {@link ImageReader} or {@link Tile}
     * instances.
     */
    private ImageReader reader;

    /**
     * Creates an iterator for the specified files.
     *
     * @param  database   The database where new entries will be added.
     * @param  series     The series in which the images will be added, or {@code null} if unknown.
     * @param  imageIndex Index of images to read. Ignored if the inputs are {@link Tile} instances.
     * @param  inputToAdd The files to read. Iteration shall be at the second element.
     * @param  input      The first element from the given iterator.
     * @throws IOException if an I/O operation was required and failed.
     */
    NewGridCoverageIterator(final SpatialDatabase database, final SeriesEntry series,
            final int imageIndex, final Iterator<?> inputToAdd, Object input)
            throws IOException, FactoryException
    {
        this.database   = database;
        this.series     = series;
        this.imageIndex = imageIndex;
        this.inputToAdd = inputToAdd;
        do {
            next = createEntry(input);
        } while (next == null && (input = nextInput()) != null);
    }

    /**
     * Returns the unique image reader. The reader is created the first time
     * this method is invoked, and reused for every subsequent invocations.
     * If no reader can be inferred because the {@linkplain #series} is not
     * specified, then this method returns {@code null}.
     */
    private ImageReader getImageReader() throws IOException {
        if (reader == null && series != null) {
            final Iterator<ImageReader> it = ImageIO.getImageReadersByFormatName(series.format.imageFormat);
            if (it.hasNext()) {
                reader = it.next();
            }
        }
        return reader;
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
        } else {
            input = IOUtilities.tryToFile(input);
            reader = getImageReader();
            if (reader != null) {
                reader.setInput(input);
            } else {
                // Let the Tile constructor figure out the provider by itself.
                final Tile tile = new Tile(null, input, imageIndex, new Point(0,0), null);
                return new NewGridCoverageReference(database, tile);
            }
        }
        return new NewGridCoverageReference(database, reader, imageIndex);
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
        if (reader != null) {
            buffer.append("reader=").append(reader.getClass().getSimpleName()).append(", ");
        }
        return buffer.append("imageIndex=").append(imageIndex).append(']').toString();
    }
}
