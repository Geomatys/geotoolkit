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
package org.geotoolkit.googlemaps.model;

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.awt.image.RenderedImage;
import java.io.InputStream;
import org.geotoolkit.coverage.AbstractGridMosaic;
import org.geotoolkit.coverage.Pyramid;
import org.geotoolkit.storage.DataStoreException;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class GoogleMapsMosaic extends AbstractGridMosaic{

    private final int scaleLevel;
    
    public GoogleMapsMosaic(Pyramid pyramid, Point2D upperLeft, Dimension gridSize,
            Dimension tileSize, double scale, int scaleLevel) {
        super(pyramid,upperLeft,gridSize,tileSize,scale);
        this.scaleLevel = scaleLevel;
    }

    public int getScaleLevel() {
        return scaleLevel;
    }

    @Override
    public RenderedImage getTile(String mimetype, int col, int row) throws DataStoreException {
        return ((GoogleMapsPyramidSet)getPyramid().getPyramidSet()).getTile(this, mimetype, col, row);
    }

    @Override
    public InputStream getTileStream(String mimetype, int col, int row) throws DataStoreException {
        return ((GoogleMapsPyramidSet)getPyramid().getPyramidSet()).getTileStream(this, mimetype, col, row);
    }
    
}
