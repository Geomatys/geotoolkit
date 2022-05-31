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
package org.geotoolkit.storage.coverage;

import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.GridCoverageResource;
import org.apache.sis.storage.Resource;
import org.apache.sis.storage.tiling.Tile;
import org.apache.sis.storage.tiling.TileStatus;

/**
 * A tile which holds a reference to a GridCoverageResource.
 * No verifications on the tile indices and it's grid geometry coherency are made.
 *
 * @author Johann Sorel (Geomatys)
 */
public final class CoverageResourceTile implements Tile {

    private final long[] indices;
    private final GridCoverageResource resource;

    public CoverageResourceTile(long[] indices, GridCoverageResource resource) {
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
