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

import org.apache.sis.storage.DataStoreException;

/**
 * Writable RS coverage location iterator.
 *
 * @author Johann Sorel (Geomatys)
 */
public interface WritableCodeIterator extends CodeIterator, AutoCloseable {

    /**
     * @return always true
     */
    @Override
    default boolean isWritable() {
        return true;
    }

    /**
     * Release any resource attached to the writer.
     */
    @Override
    public void close() throws DataStoreException;

}
