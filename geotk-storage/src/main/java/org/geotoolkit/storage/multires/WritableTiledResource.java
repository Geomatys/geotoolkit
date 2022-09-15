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

import java.util.Collection;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.ReadOnlyStorageException;
import org.apache.sis.storage.tiling.WritableTileMatrixSet;


/**
 * A {@code TiledResource} that can write and delete tile matrix sets.
 *
 * <p>All methods in this interface expect non-null arguments are return non-null values.</p>
 *
 * @author  Johann Sorel (Geomatys)
 * @author  Martin Desruisseaux (Geomatys)
 */
public interface WritableTiledResource extends TiledResource, org.apache.sis.storage.tiling.WritableTiledResource {
    /**
     * Returns the collection of all available tile matrix sets in this resource.
     * The returned collection is unmodifiable but live: additions or removals of
     * tile matrix sets in this resource are reflected in the returned collection.
     *
     * @return an unmodifiable view of all {@link TileMatrixSet} instances in this resource.
     * @throws DataStoreException if an error occurred while fetching the tile matrix sets.
     */
    @Override
    Collection<? extends WritableTileMatrixSet> getTileMatrixSets() throws DataStoreException;

    /**
     * Adds the given tile matrix set to this resource and returns a writable instance for later completion.
     * Typically the given {@link TileMatrixSet} instance contains no tile and is used only as a template.
     * If the {@code TileMatrixSet} is not empty, then the tiles that it contains are written immediately.
     *
     * <p>This method returns a writable tile matrix set with the same tiles than the given {@code TileMatrixSet}.
     * The identifier and the envelope of the returned set may be different, but the CRS and tiling scheme shall
     * be equivalent with a tolerance for rounding errors.</p>
     *
     * @param  tiles  the (potentially empty) tile matrix set to create.
     * @return a writable tile matrix set to use for adding more tiles.
     * @throws ReadOnlyStorageException if this resource is not writable. It may be caused by insufficient credentials.
     * @throws IncompatibleResourceException if the given tile matrix set is incompatible with this resource.
     * @throws DataStoreException if creating the tile matrix set failed for another reason.
     */
    @Override
    WritableTileMatrixSet createTileMatrixSet(org.apache.sis.storage.tiling.TileMatrixSet tiles) throws DataStoreException;

}
