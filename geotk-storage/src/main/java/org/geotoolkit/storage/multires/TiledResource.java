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
import org.apache.sis.storage.Resource;

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
public interface TiledResource extends Resource {

    /**
     * Returns the collection of available {@linkplain TileMatrixSet}.
     *
     * @return Collection of available TileMatrixSet, never null, can be empty.
     * @throws org.apache.sis.storage.DataStoreException
     */
    Collection<? extends TileMatrixSet> getTileMatrixSets() throws DataStoreException;

    /**
     * Create a new {@linkplain TileMatrixSet} based on given model.
     * The created model may have differences.
     * Model identifier may be preserved or not, behavior is implementation specific.
     * If the id is already used a new one will be generated instead.
     *
     * @param template a template model which structure will be used as reference.
     * @return created {@linkplain  TileMatrixSet}
     * @throws DataStoreException
     */
    TileMatrixSet createTileMatrixSet(TileMatrixSet template) throws DataStoreException;

    /**
     * Remove an existing {@linkplain TileMatrixSet}.
     *
     * @param identifier not null
     * @throws DataStoreException
     */
    void removeTileMatrixSet(String identifier) throws DataStoreException;

    /**
     * Get a description of the inner storage of tiles.
     *
     * @return Tile format description, never null but may not contain any useful information.
     */
    default TileFormat getTileFormat() {
        return TileFormat.UNDEFINED;
    }

}
