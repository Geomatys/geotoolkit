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

import java.awt.Dimension;
import java.util.Optional;
import java.util.stream.Stream;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.tiling.Tile;
import org.apache.sis.storage.tiling.TileStatus;
import org.opengis.coverage.PointOutsideCoverageException;
import org.opengis.util.GenericName;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class DefiningTileMatrix implements WritableTileMatrix, ImageTileMatrix {

    private final GenericName identifier;
    private final GridGeometry tilingScheme;
    private final Dimension tileSize;

    public DefiningTileMatrix(GenericName identifier, GridGeometry tilingScheme, Dimension tileSize) {
        this.identifier = identifier;
        this.tilingScheme = tilingScheme;
        this.tileSize = tileSize;
    }

    @Override
    public GenericName getIdentifier() {
        return identifier;
    }

    @Override
    public GridGeometry getTilingScheme() {
        return tilingScheme;
    }

    @Override
    public Dimension getTileSize() {
        return (Dimension) tileSize.clone(); //defensive copy
    }

    @Override
    public TileStatus getTileStatus(long... indices) throws PointOutsideCoverageException {
        return TileStatus.MISSING;
    }

    @Override
    public Optional<Tile> getTile(long... indices) throws DataStoreException {
        return Optional.empty();
    }

    @Override
    public void writeTiles(Stream<Tile> tiles) throws DataStoreException {
        throw new DataStoreException("Not supported.");
    }

    @Override
    public long deleteTiles(GridExtent indicesRanges) throws DataStoreException {
        throw new DataStoreException("Not supported");
    }

    @Override
    public Tile anyTile() throws DataStoreException {
        return null;
    }

    @Override
    public String toString() {
        return AbstractTileMatrix.toString(this);
    }
}
