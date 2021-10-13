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
import java.awt.Point;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.storage.coverage.ImageTile;
import org.geotoolkit.storage.multires.AbstractTileMatrix;
import org.geotoolkit.storage.multires.Tile;
import org.opengis.geometry.DirectPosition;
import org.geotoolkit.storage.multires.TileMatrixSet;

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
            Dimension tileSize, double scale, int scaleLevel) {
        super(pyramid,upperLeft,gridSize,tileSize,scale);
        this.scaleLevel = scaleLevel;
        this.set = set;
    }

    public int getScaleLevel() {
        return scaleLevel;
    }

    @Override
    protected boolean isWritable() throws DataStoreException {
        return false;
    }

    @Override
    public ImageTile getTile(long col, long row, Map hints) throws DataStoreException {
        return set.getTile(getTileMatrixSet(), this, col, row, hints);
    }

    @Override
    public BlockingQueue<Object> getTiles(Collection<? extends Point> positions, Map hints) throws DataStoreException {
        return set.getTiles(getTileMatrixSet(), this, positions, hints);
    }

    @Override
    public synchronized Tile anyTile() throws DataStoreException {
        if (anyTile == null) {
            anyTile = getTile(0, 0, null);
        }
        return anyTile;
    }

}
