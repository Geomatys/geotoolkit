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

import java.awt.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Stream;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.geometry.GeneralDirectPosition;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.process.Monitor;
import org.geotoolkit.storage.coverage.ImageTile;
import org.geotoolkit.storage.multires.AbstractTileMatrix;
import org.geotoolkit.storage.multires.TileMatrices;
import org.geotoolkit.storage.multires.Tile;
import org.geotoolkit.wmts.WMTSUtilities;
import org.geotoolkit.wmts.xml.v100.TileMatrixLimits;
import org.opengis.coverage.PointOutsideCoverageException;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.geotoolkit.storage.multires.TileMatrix;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class WMTSTileMatrix implements TileMatrix {

    private final String id = UUID.randomUUID().toString();
    private final WMTSTileMatrixSet pyramid;
    private final org.geotoolkit.wmts.xml.v100.TileMatrix matrix;
    private final TileMatrixLimits limit;
    private final double scale;
    private Tile anyTile = null;

    public WMTSTileMatrix(final WMTSTileMatrixSet pyramid, final org.geotoolkit.wmts.xml.v100.TileMatrix matrix, final TileMatrixLimits limits) {
        this.pyramid = pyramid;
        this.matrix = matrix;
        this.limit = limits;
        this.scale = WMTSUtilities.unitsByPixel(pyramid.getMatrixset(), pyramid.getCoordinateReferenceSystem(), matrix);
    }

    public org.geotoolkit.wmts.xml.v100.TileMatrix getMatrix() {
        return matrix;
    }

    @Override
    public String getIdentifier() {
        return id;
    }

    public WMTSTileMatrixSet getPyramid() {
        return pyramid;
    }

    @Override
    public DirectPosition getUpperLeftCorner() {
        final GeneralDirectPosition ul = new GeneralDirectPosition(pyramid.getCoordinateReferenceSystem());
        ul.setOrdinate(0, matrix.getTopLeftCorner().get(0));
        ul.setOrdinate(1, matrix.getTopLeftCorner().get(1));
        return ul;
    }

    @Override
    public Dimension getGridSize() {
        return new Dimension(
                matrix.getMatrixWidth(),
                matrix.getMatrixHeight());
    }

    @Override
    public double getScale() {
        return scale;
    }

    @Override
    public Dimension getTileSize() {
        return new Dimension(
                matrix.getTileWidth(),
                matrix.getTileHeight());
    }

    @Override
    public Envelope getEnvelope() {
        final DirectPosition ul = getUpperLeftCorner();
        final double minX = ul.getOrdinate(0);
        final double maxY = ul.getOrdinate(1);
        final double spanX = getScale() * getTileSize().width * getGridSize().width;
        final double spanY = getScale() * getTileSize().height * getGridSize().height;

        final GeneralEnvelope envelope = new GeneralEnvelope(
                pyramid.getCoordinateReferenceSystem());
        envelope.setRange(0, minX, minX + spanX);
        envelope.setRange(1, maxY - spanY, maxY );

        return envelope;
    }

    @Override
    public boolean isMissing(long col, long row) throws PointOutsideCoverageException {
        if (col < 0 || row < 0 || col > matrix.getMatrixWidth() || row > matrix.getMatrixHeight()) {
            throw new PointOutsideCoverageException("Queried tile position is outside matrix dimension.", new GeneralDirectPosition(col, row));
        }
        if(limit == null) return false;

        //limits are exclusive
        return  col < limit.getMinTileCol()
             || col > limit.getMaxTileCol()
             || row < limit.getMinTileRow()
             || row > limit.getMaxTileRow();
    }

    @Override
    public ImageTile getTile(long col, long row, Map hints) throws DataStoreException {
        if (hints == null) hints = new HashMap();
        if (!hints.containsKey(TileMatrices.HINT_FORMAT)) hints.put(TileMatrices.HINT_FORMAT,"image/png");

        return pyramid.getPyramidSet().getTile(pyramid, this, col, row, hints);
    }

    @Override
    public BlockingQueue<Object> getTiles(Collection<? extends Point> positions, Map hints) throws DataStoreException {
        if(hints==null) hints = new HashMap();
        if(!hints.containsKey(TileMatrices.HINT_FORMAT)){
            hints = new HashMap(hints);
            hints.put(TileMatrices.HINT_FORMAT,"image/png");
        }
        return pyramid.getPyramidSet().getTiles(pyramid, this, positions, hints);
    }

    @Override
    public GridExtent getDataExtent() {
        Dimension tileSize = getTileSize();
        return new GridExtent(
                ((long) getGridSize().width) * tileSize.width,
                ((long) getGridSize().height) * tileSize.height);
    }

    @Override
    public void writeTiles(Stream<Tile> tiles, Monitor monitor) throws DataStoreException {
        throw new DataStoreException("WMTS is not writable");
    }

    @Override
    public void deleteTile(int tileX, int tileY) throws DataStoreException {
        throw new DataStoreException("WMTS is not writable");
    }

    @Override
    public synchronized Tile anyTile() throws DataStoreException {
        if (anyTile == null) {
            if (limit == null) {
                anyTile = getTile(0, 0, null);
            } else {
                anyTile = getTile(limit.getMinTileCol(), limit.getMinTileRow(), null);
            }
        }
        return anyTile;
    }

    @Override
    public String toString() {
        return AbstractTileMatrix.toString(this);
    }
}
