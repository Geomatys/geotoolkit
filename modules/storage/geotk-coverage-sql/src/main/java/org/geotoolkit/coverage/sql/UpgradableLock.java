/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2019, Geomatys
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

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.StampedLock;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.ArgumentChecks;

/**
 * An abstraction over {@link StampedLock} to provide upgradable locks. /!\ Non re-entrant !
 *
 * @author Alexis Manin (Geomatys)
 */
final class UpgradableLock {

    private final StampedLock accessLock;

    private final long lockTimeout;
    private final TimeUnit lockTimeoutUnit;

    UpgradableLock(final long lockTimeout, final TimeUnit lockTimeoutUnit) {
        ArgumentChecks.ensureStrictlyPositive("Lock timeout", lockTimeout);
        ArgumentChecks.ensureNonNull("Lock timeout unit", lockTimeoutUnit);
        accessLock = new StampedLock();
        this.lockTimeout = lockTimeout;
        this.lockTimeoutUnit = lockTimeoutUnit;
    }

    /**
     * Perform a given action in a thread-locked environment.
     * @param operator The operation to execute thread-safely.
     * @param exclusive If true, an exclusive lock is acquired before execution, ensuring that no other locked action
     * can be executed in the same time. Otherwise, any other locked action marked as non-exclusive can be executed in
     * parallel.
     * @throws DataStoreException If we cannot acquire a lock safely, or given operation fails.
     */
    void doLocked(final DataStoreOperation operator, final boolean exclusive) throws DataStoreException {
        doLocked(stamp -> {operator.apply(stamp);return null;}, exclusive);
    }

    /**
     * Perform a given action in a thread-locked environment.
     * @param operator The operation to execute thread-safely.
     * @param exclusive If true, an exclusive lock is acquired before execution, ensuring that no other locked action
     * can be executed in the same time. Otherwise, any other locked action marked as non-exclusive can be executed in
     * parallel.
     * @return Result of the input operator.
     * @throws DataStoreException If we cannot acquire a lock safely, or given operation fails.
     */
    <T> T doLocked(final DataStoreFunction<T> operator, final boolean isWrite) throws DataStoreException {
        try (final StampImpl stamp = new StampImpl(isWrite)) {
            return operator.apply(stamp);
        }
    }

    private long lock(final boolean exclusive) throws DataStoreException {
        final long stamp;
        try {
            stamp = exclusive ? accessLock.tryWriteLock(lockTimeout, lockTimeoutUnit)
                              : accessLock.tryReadLock(lockTimeout, lockTimeoutUnit);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();         // Set the thread interrupt status.
            throw new DataStoreException("Interrupted while waiting for exclusive lock", e);
        }
        if (stamp == 0) {
            throw new DataStoreException("Timeout while waiting for a lock");
        }
        return stamp;
    }

    interface Stamp {
        void tryUpgrade() throws DataStoreException;
        void tryDowngrade() throws DataStoreException;
    }

    private class StampImpl implements Stamp, AutoCloseable {
        long stamp;
        private boolean exclusive;
        private boolean isBroken = false;

        StampImpl(final boolean exclusive) throws DataStoreException {
            stamp = lock(exclusive);
            this.exclusive = exclusive;
        }

        @Override
        public void tryUpgrade() throws DataStoreException {
            if (exclusive) return;
            final long newStamp = accessLock.tryConvertToWriteLock(stamp);
            if (newStamp == 0) {
                accessLock.unlockRead(stamp);
                isBroken = true;
                stamp = lock(true);
                isBroken = false;
            } else {
                stamp = newStamp;
            }

            exclusive = true;
        }

        @Override
        public void tryDowngrade() throws DataStoreException {
            if (!exclusive) return;
            final long newStamp = accessLock.tryConvertToReadLock(stamp);
            if (newStamp == 0) {
                accessLock.unlockWrite(stamp);
                isBroken = true;
                stamp = lock(false);
                isBroken = false;
            } else {
                stamp = newStamp;
            }

            exclusive = false;
        }

        @Override
        public void close() {
            if (isBroken) return;
            try {
                if (exclusive) accessLock.unlockWrite(stamp);
                else accessLock.unlockRead(stamp);
            } catch (IllegalMonitorStateException e) {
                try {
                    accessLock.unlock(stamp);
                } catch (Exception bis) {
                    e.addSuppressed(bis);
                }

                throw e;
            }
        }
    }

    @FunctionalInterface
    static interface DataStoreOperation {
        void apply(final Stamp lock) throws DataStoreException;
    }

    @FunctionalInterface
    static interface DataStoreFunction<T> {
        T apply(final Stamp lock) throws DataStoreException;
    }
}
