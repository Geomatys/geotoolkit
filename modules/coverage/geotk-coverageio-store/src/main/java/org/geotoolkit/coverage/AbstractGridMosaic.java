/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011-2012, Geomatys
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
package org.geotoolkit.coverage;

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.util.UUID;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.util.converter.Classes;
import org.opengis.geometry.Envelope;

/**
 * Default mosaic grid.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public abstract class AbstractGridMosaic implements GridMosaic{

    private final String id = UUID.randomUUID().toString();
    private final Pyramid pyramid;
    private final Point2D upperLeft;
    private final Dimension gridSize;
    private final Dimension tileSize;
    private final double scale;

    public AbstractGridMosaic(Pyramid pyramid, Point2D upperLeft, Dimension gridSize,
            Dimension tileSize, double scale) {
        this.pyramid = pyramid;
        this.upperLeft = (Point2D) upperLeft.clone();
        this.scale = scale;
        this.gridSize = (Dimension) gridSize.clone();
        this.tileSize = (Dimension) tileSize.clone();
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
        return (Point2D) upperLeft.clone(); //defensive copy
    }

    @Override
    public Dimension getGridSize() {
        return (Dimension) gridSize.clone(); //defensive copy
    }

    @Override
    public Dimension getTileSize() {
        return (Dimension) tileSize.clone(); //defensive copy
    }
    
    @Override
    public double getScale() {
        return scale;
    }
    
    @Override
    public Envelope getEnvelope(int row, int col) {
        final double minX = getUpperLeftCorner().getX();
        final double maxY = getUpperLeftCorner().getY();
        final double spanX = tileSize.width * scale;
        final double spanY = tileSize.height * scale;
        
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

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(Classes.getShortClassName(this));
        sb.append("   scale = ").append(getScale());
        sb.append("   gridSize[").append(getGridSize().width).append(',').append(getGridSize().height).append(']');
        sb.append("   tileSize[").append(getTileSize().width).append(',').append(getTileSize().height).append(']');
        return sb.toString();
    }
    
}
