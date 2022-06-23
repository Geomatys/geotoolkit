/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2022, Geomatys
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
package org.geotoolkit.storage.coverage.tiling;

import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.Resource;
import org.apache.sis.storage.tiling.Tile;
import org.apache.sis.storage.tiling.TileStatus;
import org.apache.sis.util.ArgumentChecks;

/**
 * A tile which holds a reference to a Resource.
 * No verifications on the tile indices and it's grid geometry coherency are made.
 *
 * @author Johann Sorel (Geomatys)
 */
public final class MemoryTile implements Tile {

    private final long[] indices;
    private final Resource resource;

    public MemoryTile(long[] indices, Resource resource) {
        ArgumentChecks.ensureNonNull("indices", indices);
        ArgumentChecks.ensureNonNull("resource", resource);
        this.indices = indices.clone();
        this.resource = resource;
    }

    @Override
    public long[] getIndices() {
        return indices.clone();
    }

    @Override
    public TileStatus getStatus() {
        return TileStatus.EXISTS;
    }

    @Override
    public Resource getResource() throws DataStoreException {
        return resource;
    }

}
