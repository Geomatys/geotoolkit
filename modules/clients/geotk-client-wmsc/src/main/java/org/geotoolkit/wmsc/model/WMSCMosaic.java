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

import java.awt.geom.Point2D;
import java.util.UUID;

import org.geotoolkit.client.map.GridMosaic;
import org.geotoolkit.client.map.Pyramid;
import org.geotoolkit.geometry.GeneralEnvelope;
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
    
    private final int tileWidth;
    private final int tileHeight;
    private final double tileSpanX;
    private final double tileSpanY;
    private final int gridWidth;
    private final int gridHeight;

    public WMSCMosaic(final WMSCPyramid pyramid, final double scaleLevel) {
        this.pyramid = pyramid;
        this.scale = scaleLevel;
                
        tileWidth = pyramid.getTileset().getWidth();
        tileHeight = pyramid.getTileset().getHeight();
        
        final BoundingBox env = pyramid.getTileset().getBoundingBox();
        final double spanX = env.getMaxx() - env.getMinx();
        final double spanY = env.getMaxy() - env.getMiny();
        
        gridWidth  = (int) (spanX / (scale*tileWidth));
        gridHeight = (int) (spanY / (scale*tileHeight));
        
        tileSpanX = spanX / gridWidth ;
        tileSpanY = spanY / gridHeight ;   
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
    public int getWidth() {
        return gridWidth;
    }

    @Override
    public int getHeight() {
        return gridHeight;
    }

    @Override
    public double getTileSpanX() {
        return tileSpanX;
    }

    @Override
    public double getTileSpanY() {
        return tileSpanY;
    }

    @Override
    public int getTileWidth() {
        return tileWidth;
    }

    @Override
    public int getTileHeight() {
        return tileHeight;
    }

    @Override
    public Envelope getEnvelope(int col, int row) {
        
        final double minX = getUpperLeftCorner().getX();
        final double maxY = getUpperLeftCorner().getY();
        final double spanX = getTileSpanX();
        final double spanY = getTileSpanY();
        
        final GeneralEnvelope envelope = new GeneralEnvelope(
                getPyramid().getCoordinateReferenceSystem());
        envelope.setRange(0, minX + col*spanX, minX + (col+1)*spanX);
        envelope.setRange(1, maxY - (row+1)*spanY, maxY - row*spanY);
        
        return envelope;
    }

    @Override
    public boolean isMissing(int col, int row) {
        return false;
    }
    
}
