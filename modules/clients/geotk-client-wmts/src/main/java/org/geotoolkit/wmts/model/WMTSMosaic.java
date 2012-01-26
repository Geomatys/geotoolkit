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
import java.awt.geom.Point2D;
import java.awt.image.RenderedImage;
import java.io.InputStream;
import java.util.Map;
import java.util.UUID;
import org.geotoolkit.coverage.GridMosaic;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.storage.DataStoreException;
import org.geotoolkit.wmts.xml.v100.TileMatrix;
import org.geotoolkit.wmts.xml.v100.TileMatrixLimits;
import org.opengis.geometry.Envelope;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class WMTSMosaic implements GridMosaic{
    
    private final String id = UUID.randomUUID().toString();
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
    public String getId() {
        return id;
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
    public Dimension getGridSize() {
        return new Dimension(
                matrix.getMatrixWidth(),
                matrix.getMatrixHeight());
    }

    @Override
    public double getScale() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Dimension getTileSize() {
        return new Dimension(
                matrix.getTileWidth(),
                matrix.getTileHeight());
    }
    
    @Override
    public Envelope getEnvelope(int row, int col) {
        final double minX = getUpperLeftCorner().getX();
        final double maxY = getUpperLeftCorner().getY();
        final Dimension tileSize = getTileSize();
        final double spanX = tileSize.width * matrix.getScaleDenominator();
        final double spanY = tileSize.height * matrix.getScaleDenominator();
        
        final GeneralEnvelope envelope = new GeneralEnvelope(
                getPyramid().getCoordinateReferenceSystem());
        envelope.setRange(0, minX + col*spanX, minX + (col+1)*spanX);
        envelope.setRange(1, maxY - (row+1)*spanY, maxY - row*spanY);
        
        return envelope;
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

    @Override
    public RenderedImage getTile(int col, int row, Map hints) throws DataStoreException {
        return ((WMTSPyramidSet)getPyramid().getPyramidSet()).getTile(this, col, row, hints);
    }

    @Override
    public InputStream getTileStream(int col, int row, Map hints) throws DataStoreException {
        return ((WMTSPyramidSet)getPyramid().getPyramidSet()).getTileStream(this, col, row, hints);
    }
    
}
