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
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.referencing.operation.transform.AffineTransform2D;
import org.geotoolkit.storage.DataStoreException;
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
    public Envelope getEnvelope(){
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
    public BlockingQueue<Object> getTiles(Collection<? extends Point> positions, Map hints) throws DataStoreException{
        return getTiles(this, positions, hints);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(Classes.getShortClassName(this));
        sb.append("   scale = ").append(getScale());
        sb.append("   gridSize[").append(getGridSize().width).append(',').append(getGridSize().height).append(']');
        sb.append("   tileSize[").append(getTileSize().width).append(',').append(getTileSize().height).append(']');
        return sb.toString();
    }
        
    
    public static AffineTransform2D getTileGridToCRS(GridMosaic mosaic, Point location){
        
        final Dimension tileSize = mosaic.getTileSize();
        final Point2D upperleft = mosaic.getUpperLeftCorner();
        final double scale = mosaic.getScale();
                
        final double offsetX  = upperleft.getX() + location.x * (scale * tileSize.width) ;
        final double offsetY = upperleft.getY() - location.y * (scale * tileSize.height);

        return new AffineTransform2D(scale, 0, 0, -scale, offsetX, offsetY);
    }
    
    public static BlockingQueue<Object> getTiles(GridMosaic mosaic, Collection<? extends Point> positions, Map hints) throws DataStoreException{
        final ArrayBlockingQueue queue = new ArrayBlockingQueue(positions.size()+1);
        for(Point p : positions){
            final TileReference t = mosaic.getTile(p.x, p.y, hints);
            queue.offer(t);
        }
        queue.offer(END_OF_QUEUE);
        return queue;
    }
    
}
