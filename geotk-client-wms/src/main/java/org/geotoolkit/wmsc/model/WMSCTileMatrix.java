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
package org.geotoolkit.wmsc.model;

import java.awt.*;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.iso.Names;
import org.geotoolkit.storage.multires.AbstractTileMatrix;
import org.geotoolkit.storage.multires.Tile;
import org.geotoolkit.storage.multires.TileMatrices;
import org.geotoolkit.storage.multires.TileMatrix;
import org.geotoolkit.storage.multires.TileMatrixSet;
import org.geotoolkit.storage.multires.TileStatus;
import org.geotoolkit.wms.xml.v111.BoundingBox;
import org.opengis.util.GenericName;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class WMSCTileMatrix implements TileMatrix {

    private final GenericName id = Names.createLocalName(null, null, UUID.randomUUID().toString());
    private final WMSCTileMatrixSet pyramid;
    private final double scale;

    private final Dimension gridSize = new Dimension();
    private final GridGeometry tilingScheme;
    private Tile anyTile = null;

    public WMSCTileMatrix(final WMSCTileMatrixSet pyramid, final double scaleLevel) {
        this.pyramid = pyramid;
        this.scale = scaleLevel;

        final int tileWidth = pyramid.getTileset().getWidth();
        final int tileHeight = pyramid.getTileset().getHeight();

        final BoundingBox env = pyramid.getTileset().getBoundingBox();
        final double spanX = env.getMaxx() - env.getMinx();
        final double spanY = env.getMaxy() - env.getMiny();

        gridSize.width = (int) (spanX / (scale*tileWidth));
        gridSize.height = (int) (spanY / (scale*tileHeight));

        tilingScheme = TileMatrices.toGridGeometry(pyramid.getUpperLeftCorner(), gridSize, scale, new Dimension(
                pyramid.getTileset().getWidth(),
                pyramid.getTileset().getHeight()));
    }

    @Override
    public GenericName getIdentifier() {
        return id;
    }

    @Override
    public GridGeometry getTilingScheme() {
        return tilingScheme;
    }

    public TileMatrixSet getPyramid() {
        return pyramid;
    }

    @Override
    public Dimension getTileSize() {
        return new Dimension(
                pyramid.getTileset().getWidth(),
                pyramid.getTileset().getHeight());
    }

    @Override
    public TileStatus getTileStatus(long... indices) {
        return TileStatus.EXISTS;
    }

    @Override
    public Optional<Tile> getTile(long... indices) throws DataStoreException {
        return pyramid.getPyramidSet().getTile(pyramid, this, indices, null);
    }

    @Override
    public Stream<Tile> getTiles(GridExtent indicesRanges, boolean parallel) throws DataStoreException {
        if (indicesRanges == null) indicesRanges = getTilingScheme().getExtent();
        final java.util.List<long[]> points = TileMatrices.pointStream(indicesRanges).collect(Collectors.toList());
        return pyramid.getPyramidSet().getTiles(pyramid, this, points, null);
    }

    @Override
    public synchronized Tile anyTile() throws DataStoreException {
        if (anyTile == null) {
            anyTile = getTile(0, 0).orElse(null);
        }
        return anyTile;
    }

    @Override
    public String toString() {
        return AbstractTileMatrix.toString(this);
    }
}
