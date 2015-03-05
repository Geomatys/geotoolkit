/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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

package org.geotoolkit.coverage;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.sis.util.logging.Logging;
import org.geotoolkit.coverage.io.CoverageReader;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.feature.type.Name;

/**
 * Abstract coverage reference which recycle readers.
 *
 * @author Johann Sorel (Geomatys)
 */
public abstract class RecyclingCoverageReference extends AbstractCoverageReference{

    private static final Logger LOGGER = Logging.getLogger(RecyclingCoverageReference.class);

    /**
     * Maximum number of readers to keep.
     */
    private static final int MAX_ELEMENTS = 3;

    /**
     * The pool of coverage reader. This pool is initially empty and will be filled with elements as needed.
     * readers (if any) shall be fetched using the {@link Deque#poll()} method and, after use,
     * given back to the pool using the {@link Deque#push(Object)} method.
     *
     * <p>This queue must be a thread-safe implementation, since it will not be invoked in
     * synchronized block.</p>
     *
     * @see #acquireReader()
     * @see #recycle(GridCoverageReader)
     */
    private final ConcurrentLinkedDeque<CoverageReader> readers = new ConcurrentLinkedDeque<>();

    public RecyclingCoverageReference(CoverageStore store, Name name) {
        super(store, name);
    }

    /**
     * {@inheritDoc}
     * @return new GridCoverageReader or an a recycled one.
     */
    @Override
    public final GridCoverageReader acquireReader() throws CoverageStoreException {
        GridCoverageReader reader = (GridCoverageReader) readers.poll();
        if (reader == null) {
            reader = createReader();
        }
        return reader;
    }

    /**
     * Check if reader to recycle is reusable and then cache it for other users.
     * If input reader can't be reused, for example he was closed or lost his input, it will
     * be disposed and not make available for other users.
     *
     * @param reader that can be reused
     */
    @Override
    public final void recycle(CoverageReader reader) {
        try {
            checkReader(reader);
            readers.push(reader);
            removeExpired(readers);
        } catch (CoverageStoreException e) {
            LOGGER.log(Level.WARNING, "Reader not recycled will be disposed. Not recycled cause : "+e.getMessage(), e);
            dispose(reader);
        }
    }

    /**
     * Create a new reader.
     */
    protected abstract GridCoverageReader createReader() throws CoverageStoreException;

    /**
     * Verify if reader is still operational and usable.
     * For example if reader is not valid if it was disposed or closed by CoverageReader user.
     * This method must ensure that input CoverageReader can be reused safely by another user.
     *
     * @param reader CoverageReader to test
     * @throws CoverageStoreException if reader is not reusable or something
     * goes wrong during reader test.
     */
    protected void checkReader(CoverageReader reader) throws CoverageStoreException {
    }

    /**
     * Removes expired readers from the given queue.
     *
     * @param  <T>   {@code GridCoverageReader} type.
     * @param  queue The queue from which to remove expired readers.
     * @param  now   Current value of {@link System#nanoTime()}.
     * @return {@code true} if the queue became empty as a result of this method call.
     */
    private boolean removeExpired(final Deque<CoverageReader> queue) {
        while (queue.size()>MAX_ELEMENTS) {
            final CoverageReader next = queue.pollLast();
            if(next==null)continue;
            dispose(next);
        }

        return true;
    }

    /**
     * Dispose all readers in the queue if any.
     *
     * @throws Throwable
     */
    @Override
    protected void finalize() throws Throwable {
        while(!readers.isEmpty()){
            final CoverageReader next = readers.pollLast();
            if(next==null)continue;
            dispose(next);
        }
        super.finalize();
    }

}
