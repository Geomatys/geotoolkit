/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
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

package org.geotoolkit.util.collection;

import java.io.Closeable;
import java.util.Iterator;

/**
 * An iterator that must be close when not needed anymore.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public interface CloseableIterator<T> extends Iterator<T>, Closeable {

    /**
     * Release the underlying resources associated with this iterator.
     */
    @Override
    void close();

}
