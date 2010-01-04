/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
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

package org.geotoolkit.data;

/**
 * Generator of Feature id for datastores.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public interface FeatureIDReader {

    /**
     * Returns whether another fid exists for this reader.
     *
     * @return <code>true</code> if more content exists
     */
    String next() throws DataStoreException;

    /**
     * Returns whether another fid exists for this reader.
     *
     * @return <code>true</code> if more content exists
     */
    boolean hasNext() throws DataStoreException;

    /**
     * Release any resources associated with this reader
     */
    void close() throws DataStoreException;

}
