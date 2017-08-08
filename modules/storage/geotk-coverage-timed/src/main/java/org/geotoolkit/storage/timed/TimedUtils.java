/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2017, Geomatys
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
package org.geotoolkit.storage.timed;

import java.nio.file.Path;
import java.util.logging.Logger;
import org.apache.sis.util.collection.Cache;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.ImageCoverageReader;

/**
 * An utility class for operations related to {@link TimedCoverageStore}.
 *
 * @author Alexis Manin (Geomatys)
 */
class TimedUtils {

    static final Logger LOGGER = Logging.getLogger("org.geotoolkit.storage");

    /**
     * We use a cache as lock "register". We do it to ensure rtree access will
     * remain synchronized even if multiple occurences of the same resource are
     * created (Ex: we create more than one coverage store for the same path).
     */
    private static final Cache<String, Object> FILE_LOCKS = new Cache<>(10, 0, false);

    /**
     * Acquire an object to use for synchronizing accesses to the given path.
     * Note : We simply return objects to use with standard java synchronized
     * mechanism, because for now RTree implementation is not concurrent and
     * both read and writes modify its states.
     *
     * @param location The tree file.
     * @return An object for synchronizing tree access.
     */
    static Object acquireLock(final Path location) {
        final String key = location.toAbsolutePath().toUri().toString();
        Object lock = FILE_LOCKS.get(key);
        if (lock == null) {
            Cache.Handler<Object> handler = FILE_LOCKS.lock(key);
            try {
                lock = handler.peek();
                if (lock == null) {
                    lock = new Object();
                }
            } finally {
                handler.putAndUnlock(lock);
            }
        }

        return lock;
    }

    /**
     * A wrapper around {@link ImageCoverageReader} whose sole aim is to be
     * closeable.
     */
    static class CloseableCoverageReader extends ImageCoverageReader implements AutoCloseable {

        @Override
        public void close() throws CoverageStoreException {
            dispose();
        }
    }
}
