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
package org.geotoolkit.osmtms.model;

import java.awt.Dimension;
import java.awt.Point;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.data.multires.AbstractMosaic;
import org.geotoolkit.data.multires.Pyramid;
import org.geotoolkit.data.multires.Pyramids;
import org.geotoolkit.storage.coverage.ImageTile;
import org.opengis.geometry.DirectPosition;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class OSMTMSMosaic extends AbstractMosaic{

    private final OSMTMSPyramidSet set;
    private final int scaleLevel;

    public OSMTMSMosaic(OSMTMSPyramidSet set, Pyramid pyramid, DirectPosition upperLeft, Dimension gridSize,
            Dimension tileSize, double scale, int scaleLevel) {
        super(pyramid,upperLeft,gridSize,tileSize,scale);
        this.scaleLevel = scaleLevel;
        this.set = set;
    }

    public int getScaleLevel() {
        return scaleLevel;
    }

    @Override
    protected boolean isWritable() throws CoverageStoreException {
        return false;
    }

    @Override
    public ImageTile getTile(long col, long row, Map hints) throws DataStoreException {
        hints.put(Pyramids.HINT_FORMAT, "image/png");
        return set.getTile(getPyramid(), this, col, row, hints);
    }

    @Override
    public BlockingQueue<Object> getTiles(Collection<? extends Point> positions, Map hints) throws DataStoreException {
        hints.put(Pyramids.HINT_FORMAT, "image/png");
        return set.getTiles(getPyramid(), this, positions, hints);
    }

}
