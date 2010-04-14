/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010, Geomatys
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

import java.sql.SQLException;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.concurrent.CancellationException;


/**
 * A temporary collection of {@link GridCoverageEntry} used in order to pipe the entries
 * from the thread querying the database to the thread processing them. The purpose of
 * using different thread is not that much to paralellize the processing (while it is a
 * nice bonus), but rather to ensure that the SQL queries are executed from one of the
 * thread managed by {@link CoverageDatabase#executor}, while the rest of the processing
 * can be executed from whatever the client thread is.
 * <p>
 * This implementation is tuned for the way {@link SingletonTable#getEntries(Collection)}
 * and {@link GridCoverageTable#getEntry()} use it - we synchronize only the methods that
 * are used by the above.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.11
 *
 * @since 3.11
 * @module
 */
@SuppressWarnings("serial")
final class GridCoverageEntries extends ArrayList<GridCoverageEntry> implements Runnable {
    /**
     * The table which created this list of entries.
     */
    private final GridCoverageTable table;

    /**
     * Set to {@code true} when we have finished to fill the list.
     */
    private boolean isDone;

    /**
     * If the {@link #run} method failed because of an exception, the exception.
     * Otherwise, {@code null}.
     */
    SQLException exception;

    /**
     * Creates a new collection instance.
     */
    GridCoverageEntries(final GridCoverageTable table) {
        super(8);
        this.table = table;
    }

    /**
     * Fills this array in a background thread.
     */
    @Override
    public void run() {
        try {
            table.getEntries(this);
        } catch (SQLException e) {
            exception = e;
        } finally {
            synchronized (this) {
                isDone = true;
                notifyAll();
            }
        }
    }

    /**
     * Returns the number of elements in this list.
     */
    @Override
    public synchronized int size() {
        return super.size();
    }

    /**
     * Adds the given entry to this list, and notifies the iterator that a new entry is available.
     */
    @Override
    public synchronized boolean add(final GridCoverageEntry entry) {
        final boolean r = super.add(entry);
        notify();
        return r;
    }

    /**
     * Gets the element at the given index. This method blocks
     * if the given element is in process of being calculated.
     */
    @Override
    public synchronized GridCoverageEntry get(final int index) {
        while (index >= super.size() && !isDone) {
            waitOrCancel();
        }
        return super.get(index);
    }

    /**
     * Returns {@code true} if there is more element. This method is invoked by
     * {@link Iterator#hasNext()} and may block if an entry is in process of
     * being calculated.
     *
     * @param index The iterator position.
     */
    synchronized boolean hasMore(final int index) {
        while (index >= super.size()) {
            if (isDone) {
                return false;
            }
            waitOrCancel();
        }
        return true;
    }

    /**
     * Waits for notification, or cancel if the waiting thread has been interrupted.
     */
    private void waitOrCancel() {
        try {
            wait();
        } catch (InterruptedException e) {
            CancellationException ex = new CancellationException();
            ex.initCause(e);
            throw ex;
        }
    }

    /**
     * Returns the usual {@link ArrayList} iterator. This method should be
     * invoked only when we known that the filling of this array is completed.
     */
    final Iterator<GridCoverageEntry> defaultIterator() {
        if (!isDone) {
            throw new IllegalStateException();
        }
        return super.iterator();
    }

    /**
     * Returns an iterator over the entries in this list. At the difference of
     * the standard iterator, the returned iterator is tolerant to values added
     * in the list while we are iterating.
     */
    @Override
    public Iterator<GridCoverageEntry> iterator() {
        return new Iter();
    }

    /**
     * The iterator, which block on the enclosing {@link GridCoverageEntries}
     * if we reached the end of the list while we are waiting for more elements
     * from the thread querying the database.
     */
    private final class Iter implements Iterator<GridCoverageEntry> {
        /**
         * Index of the next element to return.
         */
        private int index;

        @Override
        public boolean hasNext() {
            return hasMore(index);
        }

        @Override
        public GridCoverageEntry next() {
            return get(index++);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
