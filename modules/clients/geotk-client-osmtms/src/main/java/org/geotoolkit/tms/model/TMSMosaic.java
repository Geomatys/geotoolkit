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
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.storage.coverage.ImageTile;
import org.geotoolkit.storage.multires.AbstractMosaic;
import org.geotoolkit.storage.multires.Pyramid;
import org.geotoolkit.storage.multires.Tile;
import org.opengis.geometry.DirectPosition;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class TMSMosaic extends AbstractMosaic {

    private final TMSPyramidSet set;
    private final int scaleLevel;

    public TMSMosaic(TMSPyramidSet set, Pyramid pyramid, DirectPosition upperLeft, Dimension gridSize,
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
        return set.getTile(getPyramid(), this, col, row, hints);
    }

    @Override
    public BlockingQueue<Object> getTiles(Collection<? extends Point> positions, Map hints) throws DataStoreException {
        return set.getTiles(getPyramid(), this, positions, hints);
    }

    @Override
    public Optional<Tile> anyTile() throws DataStoreException {
        return Optional.ofNullable(getTile(0, 0, null));
    }

}
