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
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.opengis.feature.type.Name;

/**
 * Abstract coverage reference which recycle readers.
 *
 * @author Johann Sorel (Geomatys)
 */
public abstract class RecyclingCoverageReference extends AbstractCoverageReference{

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
    private final ConcurrentLinkedDeque<GridCoverageReader> readers = new ConcurrentLinkedDeque<>();

    public RecyclingCoverageReference(CoverageStore store, Name name) {
        super(store, name);
    }

    @Override
    public final GridCoverageReader acquireReader() throws CoverageStoreException {
        GridCoverageReader reader = readers.poll();
        if (reader == null) {
            reader = createReader();
        }
        return reader;
    }

    @Override
    public final void recycle(GridCoverageReader reader) {
        resetReader(reader);
        readers.push(reader);
        removeExpired(readers);
    }

    /**
     * Create a new reader.
     */
    protected abstract GridCoverageReader createReader() throws CoverageStoreException;

    /**
     * Reset given reader.
     * Does not by default, override to do specific operations.
     */
    protected void resetReader(GridCoverageReader reader){

    }

    /**
     * Removes expired readers from the given queue.
     *
     * @param  <T>   {@code GridCoverageReader} type.
     * @param  queue The queue from which to remove expired readers.
     * @param  now   Current value of {@link System#nanoTime()}.
     * @return {@code true} if the queue became empty as a result of this method call.
     */
    private boolean removeExpired(final Deque<GridCoverageReader> queue) {
        while(queue.size()>MAX_ELEMENTS){
            final GridCoverageReader next = queue.pollLast();
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
            final GridCoverageReader next = readers.pollLast();
            if(next==null)continue;
            dispose(next);
        }
        super.finalize();
    }

}
