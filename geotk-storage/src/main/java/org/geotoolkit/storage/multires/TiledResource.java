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
package org.geotoolkit.storage.multires;

import java.util.Collection;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.tiling.TileMatrixSet;

/**
 * A TiledResource is a ressource which content can be accessed by
 * smaller chunks called Tiles.
 * <p>
 * The resource may expose multiple differents {@linkplain TileMatrixSet},
 * each one with a different {@linkplain CoordinateReferenceSystem}.
 * </p>
 * Most format specification only support a single {@linkplain TileMatrixSet},
 * but a few ones like WMTS may have several.
 *
 * @author Johann Sorel (Geomatys)
 */
public interface TiledResource extends org.apache.sis.storage.tiling.TiledResource {

    /**
     * Returns the collection of available {@linkplain TileMatrixSet}.
     *
     * @return Collection of available TileMatrixSet, never null, can be empty.
     * @throws org.apache.sis.storage.DataStoreException
     */
    Collection<? extends TileMatrixSet> getTileMatrixSets() throws DataStoreException;

    /**
     * Get a description of the inner storage of tiles.
     *
     * @return Tile format description, never null but may not contain any useful information.
     */
    default TileFormat getTileFormat() {
        return TileFormat.UNDEFINED;
    }

}
