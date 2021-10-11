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
package org.geotoolkit.index;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

/**
 * Tag interface for collection that must be closed
 *
 * @author jesse
 * @module
 */
public interface CloseableCollection<T> extends Collection<T>{

    /**
     * Close the collection so it cleans up its resources
     */
    void close() throws IOException;
    /**
     * Close the collection so it cleans up its resources
     */
    void closeIterator(Iterator<T> iter) throws IOException;
}
