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

import java.awt.geom.Point2D;
import org.geotoolkit.client.map.GridMosaic;
import org.geotoolkit.wmts.xml.v100.TileMatrix;
import org.geotoolkit.wmts.xml.v100.TileMatrixLimits;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class WMTSMosaic implements GridMosaic{

    private final WMTSPyramid pyramid;
    private final TileMatrix matrix;
    private final TileMatrixLimits limit;

    public WMTSMosaic(final WMTSPyramid pyramid, final TileMatrix matrix, final TileMatrixLimits limits) {
        this.pyramid = pyramid;
        this.matrix = matrix;
        this.limit = limits;
    }
    
    public TileMatrix getMatrix() {
        return matrix;
    }
    
    @Override
    public WMTSPyramid getPyramid() {
        return pyramid;
    }

    @Override
    public Point2D getUpperLeftCorner() {
        final Point2D pt = new Point2D.Double(
                matrix.getTopLeftCorner().get(0), matrix.getTopLeftCorner().get(1));
        return pt;
    }

    @Override
    public int getWidth() {
        return matrix.getMatrixWidth();
    }

    @Override
    public int getHeight() {
        return matrix.getMatrixHeight();
    }

    @Override
    public double getTileSpanX() {
        return getTileWidth() * matrix.getScaleDenominator();
    }

    @Override
    public double getTileSpanY() {
        return getTileHeight() * matrix.getScaleDenominator();
    }

    @Override
    public int getTileWidth() {
        return matrix.getTileWidth();
    }

    @Override
    public int getTileHeight() {
        return matrix.getTileHeight();
    }

    @Override
    public boolean isMissing(int col, int row) {        
        if(limit == null) return false;
        
        //limits are exclusive
        return  col < limit.getMinTileCol() 
             || col > limit.getMaxTileCol()
             || row < limit.getMinTileRow()
             || row > limit.getMaxTileRow();
    }
    
}
