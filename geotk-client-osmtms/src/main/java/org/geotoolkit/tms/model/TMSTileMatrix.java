/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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
package org.geotoolkit.tms.model;

import java.awt.Dimension;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.storage.multires.AbstractTileMatrix;
import org.apache.sis.storage.tiling.Tile;
import org.apache.sis.storage.tiling.TileMatrixSet;
import org.geotoolkit.storage.multires.TileMatrices;
import org.opengis.geometry.DirectPosition;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class TMSTileMatrix extends AbstractTileMatrix {

    private final TMSTileMatrixSets set;
    private final int scaleLevel;
    private Tile anyTile = null;

    public TMSTileMatrix(TMSTileMatrixSets set, TileMatrixSet pyramid, DirectPosition upperLeft, Dimension gridSize,
            int[] tileSize, double scale, int scaleLevel) {
        super(null, pyramid,upperLeft,gridSize,tileSize,scale);
        this.scaleLevel = scaleLevel;
        this.set = set;
    }

    public int getScaleLevel() {
        return scaleLevel;
    }

    @Override
    public Optional<Tile> getTile(long... indices) throws DataStoreException {
        return set.getTile(getTileMatrixSet(), this, indices, null);
    }

    @Override
    public Stream<Tile> getTiles(GridExtent indicesRanges, boolean parallel) throws DataStoreException {
        if (indicesRanges == null) indicesRanges = getTilingScheme().getExtent();
        final List<long[]> points = TileMatrices.pointStream(indicesRanges).collect(Collectors.toList());
        return set.getTiles(getTileMatrixSet(), this, points, null);
    }

    @Override
    public synchronized Tile anyTile() throws DataStoreException {
        if (anyTile == null) {
            anyTile = getTile(0, 0).orElse(null);
        }
        return anyTile;
    }

}
