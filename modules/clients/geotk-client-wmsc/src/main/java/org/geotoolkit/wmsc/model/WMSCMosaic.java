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

import java.awt.Dimension;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import org.geotoolkit.coverage.GridMosaic;
import org.geotoolkit.coverage.Pyramid;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.image.io.mosaic.Tile;
import org.geotoolkit.storage.DataStoreException;
import org.geotoolkit.wms.xml.v111.BoundingBox;
import org.opengis.geometry.Envelope;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class WMSCMosaic implements GridMosaic{

    private final String id = UUID.randomUUID().toString();
    private final WMSCPyramid pyramid;
    private final double scale;
    
    private final Dimension gridSize = new Dimension();
    private final double tileSpanX;
    private final double tileSpanY;

    public WMSCMosaic(final WMSCPyramid pyramid, final double scaleLevel) {
        this.pyramid = pyramid;
        this.scale = scaleLevel;
                
        final int tileWidth = pyramid.getTileset().getWidth();
        final int tileHeight = pyramid.getTileset().getHeight();
        
        final BoundingBox env = pyramid.getTileset().getBoundingBox();
        final double spanX = env.getMaxx() - env.getMinx();
        final double spanY = env.getMaxy() - env.getMiny();
        
        gridSize.width = (int) (spanX / (scale*tileWidth));
        gridSize.height = (int) (spanY / (scale*tileHeight));
        
        tileSpanX = spanX / gridSize.width ;
        tileSpanY = spanY / gridSize.height ;   
    }

    @Override
    public String getId() {
        return id;
    }
    
    @Override
    public Pyramid getPyramid() {
        return pyramid;
    }

    @Override
    public Point2D getUpperLeftCorner() {
        return pyramid.getUpperLeftCorner();
    }

    @Override
    public Dimension getGridSize() {
        return (Dimension) gridSize.clone();
    }

    @Override
    public double getScale() {
        return scale;
    }

    @Override
    public Dimension getTileSize() {
        return new Dimension(
                pyramid.getTileset().getWidth(), 
                pyramid.getTileset().getHeight());
    }
    
    @Override
    public Envelope getEnvelope(int col, int row) {
        
        final double minX = getUpperLeftCorner().getX();
        final double maxY = getUpperLeftCorner().getY();
        final double spanX = tileSpanX;
        final double spanY = tileSpanY;
        
        final GeneralEnvelope envelope = new GeneralEnvelope(
                getPyramid().getCoordinateReferenceSystem());
        envelope.setRange(0, minX + col*spanX, minX + (col+1)*spanX);
        envelope.setRange(1, maxY - (row+1)*spanY, maxY - row*spanY);
        
        return envelope;
    }

    @Override
    public Envelope getEnvelope() {
        final double minX = getUpperLeftCorner().getX();
        final double maxY = getUpperLeftCorner().getY();
        final double spanX = getTileSize().width * getGridSize().width * getScale();
        final double spanY = getTileSize().height* getGridSize().height* getScale();
        
        final GeneralEnvelope envelope = new GeneralEnvelope(
                getPyramid().getCoordinateReferenceSystem());
        envelope.setRange(0, minX, minX + spanX);
        envelope.setRange(1, maxY - spanY, maxY );
        
        return envelope;
    }
    
    @Override
    public boolean isMissing(int col, int row) {
        return false;
    }

    @Override
    public Tile getTile(int col, int row, Map hints) throws DataStoreException {
        return ((WMSCPyramidSet)getPyramid().getPyramidSet()).getTile(this, col, row, hints);
    }

    @Override
    public InputStream getTileStream(int col, int row, Map hints) throws DataStoreException {
        return ((WMSCPyramidSet)getPyramid().getPyramidSet()).getTileStream(this, col, row, hints);
    }

    @Override
    public Iterator<Tile> getTiles(Collection<? extends Point> positions, Map hints) throws DataStoreException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
