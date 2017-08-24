/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2016, Geomatys
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
package org.geotoolkit.storage;

import org.apache.sis.metadata.MetadataCopier;
import org.apache.sis.metadata.MetadataStandard;
import org.apache.sis.storage.DataStoreException;
import org.opengis.metadata.Metadata;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public abstract class DataStore extends org.apache.sis.storage.DataStore {

    /**
     * Cached value for the store metadata. Initialized when first queried. See
     * {@link #getMetadata() } for more information.
     */
    private Metadata metadata;

    /**
     * A lock to synchronize metadata initialization. See {@link #getMetadata() }
     * for further information.
     */
    private final Object mdLock = new Object();

    /**
     * Get the factory which created this source.
     *
     * @return this source original factory
     */
    public abstract DataStoreFactory getFactory();

    @Override
    public Metadata getMetadata() throws DataStoreException {
        final Metadata mdRef;
        synchronized (mdLock) {
            if (metadata == null)
                metadata = createMetadata();
            mdRef = metadata;
        }
        return mdRef == null? null : new MetadataCopier(MetadataStandard.ISO_19115).copy(Metadata.class, mdRef);
    }

    /**
     * Create a new metadata containing information about this datastore and the
     * data it contains.
     *
     * Note : Analysis should be restricted to report only information currently
     * available in this dataset. Further computing should be performed externally.
     *
     * @return Created metadata. Can be null if there's no information available.
     * @throws DataStoreException If an error occurs while analyzing underlying
     * data.
     */
    protected Metadata createMetadata() throws DataStoreException {
        return null;
    }

}
