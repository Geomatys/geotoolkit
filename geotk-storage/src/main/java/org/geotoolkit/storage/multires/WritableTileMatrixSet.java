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

import java.util.SortedMap;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.NoSuchDataException;
import org.apache.sis.storage.ReadOnlyStorageException;
import org.opengis.util.GenericName;


/**
 * A {@code TileMatrixSet} that can write and delete tile matrices.
 *
 * <p>All methods in this interface expect non-null arguments are return non-null values.</p>
 *
 * @author  Johann Sorel (Geomatys)
 * @author  Martin Desruisseaux (Geomatys)
 */
public interface WritableTileMatrixSet extends TileMatrixSet {
    /**
     * Returns all {@link WritableTileMatrix} instances in this set, together with their identifiers.
     * For each value in the map, the associated key is {@link WritableTileMatrix#getIdentifier()}.
     * Entries are sorted from coarser resolution (highest scale denominator)
     * to most detailed resolution (lowest scale denominator).
     *
     * <p>The returned view is unmodifiable but live: creations or removals of tile matrices
     * in this set will be reflected on the returned map.</p>
     *
     * @return an unmodifiable view of all {@code WritableTileMatrix} instances with their identifiers.
     */
    @Override
    SortedMap<GenericName, ? extends WritableTileMatrix> getTileMatrices();

    /**
     * Adds the given tile matrix to this set and returns a writable instance for later completion.
     * Typically the given {@link TileMatrix} instance contains no tile and is used only as a template.
     * If the {@code TileMatrix} is not empty, then the tiles that it contains are written immediately.
     *
     * <p>The {@linkplain TileMatrix#getTilingScheme() tiling scheme} of the given tile matrix must
     * be compatible with this set. In particular, it must use the same CRS than the value returned
     * by {@link #getCoordinateReferenceSystem()}. If not, an {@link IncompatibleResourceException}
     * is thrown.</p>
     *
     * <p>This method returns a writable tile matrix with the same tiles than the given {@code TileMatrix}.
     * However the identifier of the returned tile matrix may be different.</p>
     *
     * @param  tiles  the (potentially empty) tile matrix to create.
     * @return a writable tile matrix to use for adding more tiles.
     * @throws ReadOnlyStorageException if this tile matrix set is not writable. It may be caused by insufficient credentials.
     * @throws IncompatibleResourceException if the tiling scheme of the given tile matrix is not compatible with this set.
     * @throws DataStoreException if creating the tile matrix failed for another reason.
     */
    WritableTileMatrix createTileMatrix(TileMatrix tiles) throws DataStoreException;

    /**
     * Deletes a {@code TileMatrix} identified by the given name. The given identifier shall be the
     * <code>{@linkplain TileMatrix#getIdentifier()}.toString()</code> value of the tile matrix to delete.
     *
     * @param  identifier  identifier of the {@link TileMatrix} to delete.
     * @throws NoSuchDataException if there is no tile matrix associated to the given identifier in this set.
     * @throws ReadOnlyStorageException if this tile matrix set is not writable. It may be caused by insufficient credentials.
     * @throws DataStoreException if deleting the tile matrix failed for another reason.
     */
    void deleteTileMatrix(String identifier) throws DataStoreException;

}
