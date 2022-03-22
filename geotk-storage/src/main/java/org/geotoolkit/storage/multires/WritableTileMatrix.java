/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2018-2022, Geomatys
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

import java.util.stream.Stream;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.ReadOnlyStorageException;


/**
 * A {@code TileMatrix} that can write and delete tiles.
 *
 * <p>All methods in this interface expect non-null arguments.</p>
 *
 * @author  Johann Sorel (Geomatys)
 * @author  Martin Desruisseaux (Geomatys)
 */
public interface WritableTileMatrix extends TileMatrix {
    /**
     * Writes a stream of tiles. The caller must ensure that all tiles are compatible
     * with the {@linkplain #getTilingScheme() tiling scheme} of this tile matrix set.
     * If a tile already exists, it will be overwritten.
     *
     * @param  tiles  the tiles to write.
     * @throws ReadOnlyStorageException if this tile matrix is not writable. It may be caused by insufficient credentials.
     * @throws IncompatibleResourceException if a tile is not compatible with the tiling scheme of this tile matrix.
     * @throws DataStoreException if writing the tiles failed for another reason.
     */
    void writeTiles(Stream<Tile> tiles) throws DataStoreException;

    /**
     * Deletes all existing tiles in the given region.
     * After this method call, the status of all tiles in the given region become {@link TileStatus#MISSING}.
     * Tiles that were already missing are silently ignored.
     *
     * @param  indicesRanges  ranges of tile indices in all dimensions, or {@code null} for all tiles.
     * @return number of tiles deleted (i.e. not counting the tiles that were already missing).
     * @throws ReadOnlyStorageException if this tile matrix is not writable. It may be caused by insufficient credentials.
     * @throws DataStoreException if deleting the tile failed for another reason.
     */
    long deleteTiles(GridExtent indicesRanges) throws DataStoreException;
}
