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
package org.geotoolkit.wmts.model;

import java.awt.Dimension;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.geometry.GeneralDirectPosition;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.iso.Names;
import org.geotoolkit.storage.multires.AbstractTileMatrix;
import org.apache.sis.storage.tiling.Tile;
import org.geotoolkit.storage.multires.TileMatrices;
import org.geotoolkit.storage.multires.TileMatrix;
import org.apache.sis.storage.tiling.TileStatus;
import org.geotoolkit.wmts.WMTSUtilities;
import org.geotoolkit.wmts.xml.v100.TileMatrixLimits;
import org.opengis.coverage.PointOutsideCoverageException;
import org.opengis.util.GenericName;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class WMTSTileMatrix implements TileMatrix {

    private final GenericName id = Names.createLocalName(null, null, UUID.randomUUID().toString());
    private final WMTSTileMatrixSet pyramid;
    private final org.geotoolkit.wmts.xml.v100.TileMatrix matrix;
    private final TileMatrixLimits limit;
    private final double scale;
    private final GridGeometry tilingScheme;
    private Tile anyTile = null;

    public WMTSTileMatrix(final WMTSTileMatrixSet pyramid, final org.geotoolkit.wmts.xml.v100.TileMatrix matrix, final TileMatrixLimits limits) {
        this.pyramid = pyramid;
        this.matrix = matrix;
        this.limit = limits;
        this.scale = WMTSUtilities.unitsByPixel(pyramid.getMatrixset(), pyramid.getCoordinateReferenceSystem(), matrix);

        final GeneralDirectPosition ul = new GeneralDirectPosition(pyramid.getCoordinateReferenceSystem());
        ul.setCoordinate(0, matrix.getTopLeftCorner().get(0));
        ul.setCoordinate(1, matrix.getTopLeftCorner().get(1));
        this.tilingScheme = TileMatrices.toTilingScheme(ul,
                new Dimension(matrix.getMatrixWidth(), matrix.getMatrixHeight()), scale,
                new int[]{matrix.getTileWidth(), matrix.getTileHeight()});
    }

    public org.geotoolkit.wmts.xml.v100.TileMatrix getMatrix() {
        return matrix;
    }

    @Override
    public GenericName getIdentifier() {
        return id;
    }

    @Override
    public GridGeometry getTilingScheme() {
        return tilingScheme;
    }

    public WMTSTileMatrixSet getPyramid() {
        return pyramid;
    }

    @Override
    public int[] getTileSize() {
        return new int[]{
                matrix.getTileWidth(),
                matrix.getTileHeight()};
    }

    @Override
    public TileStatus getTileStatus(long... indices) throws PointOutsideCoverageException {
        if (indices[0] < 0 || indices[1] < 0 || indices[0] > matrix.getMatrixWidth() || indices[1] > matrix.getMatrixHeight()) {
            return TileStatus.OUTSIDE_EXTENT;
        }
        if(limit == null) return TileStatus.EXISTS;

        //limits are exclusive
        return (indices[0] < limit.getMinTileCol()
             || indices[0] > limit.getMaxTileCol()
             || indices[1] < limit.getMinTileRow()
             || indices[1] > limit.getMaxTileRow()) ?
                TileStatus.MISSING : TileStatus.EXISTS;
    }

    @Override
    public Optional<Tile> getTile(long... indices) throws DataStoreException {
        return getTile(indices, null);
    }

    public Optional<Tile> getTile(long[] indices, Map hints) throws DataStoreException {
        if (hints == null) hints = new HashMap();

        return pyramid.getPyramidSet().getTile(pyramid, this, indices, hints);
    }

    @Override
    public Stream<Tile> getTiles(GridExtent indicesRanges, boolean parallel) throws DataStoreException {
        final HashMap hints = new HashMap();
        if (indicesRanges == null) indicesRanges = getTilingScheme().getExtent();
        final java.util.List<long[]> points = TileMatrices.pointStream(indicesRanges).collect(Collectors.toList());
        return pyramid.getPyramidSet().getTiles(pyramid, this, points, hints);
    }

    @Override
    public synchronized Tile anyTile() throws DataStoreException {
        if (anyTile == null) {
            if (limit == null) {
                anyTile = getTile(0, 0).orElse(null);
            } else {
                anyTile = getTile(limit.getMinTileCol(), limit.getMinTileRow()).orElse(null);
            }
        }
        return anyTile;
    }

    @Override
    public String toString() {
        return AbstractTileMatrix.toString(this);
    }
}
