/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2012, Geomatys
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



/**
 * A pool of {@link GridCoverageReader} and {@link GridCoverageWriter} instances.
 * This pool can be useful if many grid coverages are likely to be read or written,
 * and the readers or writers are costly to create.
 *
 * {@note In Geotk implementation, <code>GridCoverageReader</code> and <code>GridCoverageWriter</code>
 * are not directly costly to create. However they may hold references to <code>ImageReader</code> or
 * <code>ImageWriter</code> instances, and some kind of those Java I/O objects are costly to create.}
 *
 * This class is typically used as below. Note that it is not strictly necessary to invoke
 * the {@link #release(GridCoverageReader)} method in a {@code finally} block.
 *
 * {@preformat java
 *     class MyClass {
 *         private final GridCoverageStorePool pool = new GridCoverageStorePool(4);
 *
 *         GridCoverage2D getCoverage() throws CoverageStoreException {
 *             GridCoverageReader reader = pool.acquireReader();
 *             GridCoverage2D coverage = reader.read(...);
 *             pool.release(reader);
 *             return coverage;
 *         }
 *     }
 * }
 *
 * The default pool implementation creates instances of {@link ImageCoverageReader}. Subclasses
 * can create other kind of implementations by overriding the {@link #createReader()} method.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.10
 *
 * @since 3.10
 * @module
 *
 * @todo Needs a background task for disposing the readers or writers after some amount
 *       of inactivity.
 */
public class GridCoverageStorePool {
    /**
     * The grid coverage readers.
     */
    private final GridCoverageReader[] readers;

    /**
     * Number of valid elements in {@link #readers}.
     */
    private int readerCount;

    /**
     * Creates a new pool which will accept the given maximal amount of readers and writers.
     *
     * @param max The maximal amount of readers and writers to keep in the pool.
     */
    public GridCoverageStorePool(final int max) {
        readers = new GridCoverageReader[max];
    }

    /**
     * Invoked when a new image reader needs to be created. The default implementation
     * returns a new instance of {@link ImageCoverageReader}. Subclasses can override
     * this method if they want to supply an other kind of reader.
     *
     * @return A new reader instance, to be returned by {@link #acquireReader()}.
     * @throws CoverageStoreException If the reader can not be created.
     */
    protected GridCoverageReader createReader() throws CoverageStoreException {
        return new ImageCoverageReader();
    }

    /**
     * Returns a reader from the pool, or {@linkplain #createReader() creates} a new one if the
     * pool is empty. Callers shall invoke {@link #release(GridCoverageReader)} when they finished
     * using the reader, in order to return it to the pool. However it is not necessary to perform
     * the release in a {@code finally} block: if the reader is never returned to the pool, it will
     * be garbage-collected.
     *
     * @return A reader instance available for use.
     * @throws CoverageStoreException If the reader can not be created.
     */
    public GridCoverageReader acquireReader() throws CoverageStoreException {
        synchronized (readers) {
            int n = readerCount;
            if (n != 0) {
                readerCount = --n;
                final GridCoverageReader reader = readers[n];
                readers[n] = null;
                return reader;
            }
        }
        return createReader();
    }

    /**
     * Returns the given reader to the pool. This method {@linkplain GridCoverageReader#reset()
     * resets} the given reader and adds it to the pool if the pool is not full. If the pull is
     * full, then this method {@linkplain GridCoverageReader#dispose() disposes} the given reader.
     *
     * @param  reader The reader to return to the pool.
     * @throws CoverageStoreException If an error occurred while reseting or disposing the reader.
     */
    public void release(final GridCoverageReader reader) throws CoverageStoreException {
        reader.reset(); // Close the image input stream, if any.
        synchronized (readers) {
            if (readerCount != readers.length) {
                readers[readerCount++] = reader;
                return;
            }
        }
        reader.dispose();
    }
}
