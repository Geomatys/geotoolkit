/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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

import java.awt.Image;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.coverage.io.GridCoverageWriter;
import org.geotoolkit.storage.StorageListener;
import org.opengis.feature.type.Name;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public interface CoverageReference {

    /**
     * Name of the coverage. act as an identifier in the coverage store
     *
     * @return Name
     */
    Name getName();
    
    /**
     * @return int image index in reader/writer.
     */
    int getImageIndex();

    /**
     * @return true if coverage is writable
     */
    boolean isWritable() throws DataStoreException;

    /**
     * Get the coverage store this coverage comes from.
     *
     * @return CoverageStore, can be null if coverage has a different kind of source.
     */
    CoverageStore getStore();

    /**
     * Get a new reader for this coverage.
     *
     * @return GridCoverageReader
     * @throws DataStoreException
     */
    GridCoverageReader createReader() throws DataStoreException;

    /**
     * Get a new writer for this coverage.
     *
     * @return GridCoverageWriter
     * @throws DataStoreException
     */
    GridCoverageWriter createWriter() throws DataStoreException;

    /**
     * Return the legend of this coverage
     * @return
     * @throws DataStoreException
     */
    Image getLegend() throws DataStoreException;

    /**
     * Add a storage listener which will be notified when structure changes or
     * when coverage data changes.
     * @param listener to add
     */
    void addStorageListener(StorageListener listener);

    /**
     * Remove a storage listener
     * @param listener to remove
     */
    void removeStorageListener(StorageListener listener);
    
}
