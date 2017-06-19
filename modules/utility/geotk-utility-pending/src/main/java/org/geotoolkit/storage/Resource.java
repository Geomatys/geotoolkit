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
package org.geotoolkit.storage;

import org.apache.sis.storage.DataStoreException;
import org.opengis.metadata.Identifier;
import org.opengis.metadata.Metadata;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public interface Resource {

    /**
     * Data identifier.
     *
     * @return Identifier, never null
     */
    Identifier getIdentifier();

    /**
     * Returns information about the resource. The returned metadata object, can contain
     * information such as the spatiotemporal extent of the resource, contact information about the creator
     * or distributor, data quality, update frequency, usage constraints and more.
     *
     * @return information about the resource, not null.
     * @throws DataStoreException if an error occurred while reading the data.
     */
    Metadata getMetadata() throws DataStoreException;

    /**
     * Add a storage listener which will be notified when structure changes or
     * when coverage data changes.
     *
     * @param listener to add
     */
    void addStorageListener(final StorageListener listener);

    /**
     * Remove a storage listener.
     *
     * @param listener to remove
     */
    void removeStorageListener(final StorageListener listener);

}
