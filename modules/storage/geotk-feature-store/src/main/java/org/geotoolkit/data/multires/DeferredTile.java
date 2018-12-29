/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2018, Geomatys
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
package org.geotoolkit.data.multires;

import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.Resource;

/**
 * A Tile which is a pointer or reference toward the real resource contained in the tile.
 * It can be seens as a deferred resource, this allow to implement common resource
 * interfaces without opening the data. ResourceOnFileSystem is often implemented
 * to access the files without reading them.
 *
 * @author Johann Sorel (Geomatys)
 */
public interface DeferredTile {

    /**
     * Open the real resource.
     *
     * @return loaded resource
     * @throws org.apache.sis.storage.DataStoreException
     */
    Resource open() throws DataStoreException;

}
