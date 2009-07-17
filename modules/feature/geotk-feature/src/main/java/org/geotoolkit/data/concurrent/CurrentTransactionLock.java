/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 * 
 *    (C) 2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.data.concurrent;


/**
 * A request for a Lock that last the duration of a transaction.
 *
 * <p>
 * The single instance of this class is available as
 * <code>FeatureLock.TRANSACTION</code>.
 * </p>
 * @source $URL$
 */
class CurrentTransactionLock implements FeatureLock {

    /**
     * Transaction locks do not require Authorization.
     *
     * <p>
     * Authorization is based on being on "holding" the Transaction rather than
     * supplying an authorization id.
     * </p>
     *
     * @return <code>CURRENT_TRANSACTION</code> to aid in debugging.
     *
     * @see org.geotoolkit.data.FeatureLock#getAuthorization()
     */
    @Override
    public String getAuthorization() {
        return toString();
    }

    /**
     * Transaciton locks are not held for a duration.
     *
     * <p>
     * Any locking performed against the current Transaction is expected to
     * expire when the transaction finishes with a close or rollback
     * </p>
     *
     * @return <code>-1</code> representing an invalid duration
     *
     * @see org.geotoolkit.data.FeatureLock#getDuration()
     */
    @Override
    public long getDuration() {
        return -1;
    }

    @Override
    public String toString() {
        return "CURRENT_TRANSACTION";
    }
}
