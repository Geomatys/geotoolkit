/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2025, Geomatys
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
package org.geotoolkit.storage.rs;

import org.opengis.feature.Feature;

/**
 * Referenced coverage location iterator.
 *
 * @author Johann Sorel (Geomatys)
 */
public interface CodeIterator {

    /**
     * Returns true of the iterator is writable.
     * @return may be true
     */
    default boolean isWritable() {
        return false;
    }

    /**
     * @return current location identifier
     */
    int[] getPosition();

    /**
     * Move iterator to given zone
     *
     * @param zid searched location identifier
     */
    void moveTo(int[] zid);

    /**
     * Move to next location.
     *
     * @return true if there is a next location
     */
    boolean next();

    Feature getSample();

    /**
     * Move iterator back to the starting position.
     */
    void rewind();
}
