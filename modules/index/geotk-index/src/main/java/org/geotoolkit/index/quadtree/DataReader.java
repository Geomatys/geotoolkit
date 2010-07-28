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

package org.geotoolkit.index.quadtree;

import java.io.IOException;
import org.geotoolkit.index.Data;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public interface DataReader {

    /**
     * Read a single data value.
     */
    Data read(int id) throws IOException;

    /**
     * Read a buffer of values.
     */
    void read(int[] ids, Data[] buffer, int size) throws IOException;

    /**
     * Release resources.
     */
    void close() throws IOException;

}
