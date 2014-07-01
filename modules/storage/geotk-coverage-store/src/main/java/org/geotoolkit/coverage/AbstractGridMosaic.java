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
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.geometry.GeneralDirectPosition;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.internal.referencing.j2d.AffineTransform2D;
import org.apache.sis.util.Classes;
import org.apache.sis.referencing.operation.transform.MathTransforms;
import org.geotoolkit.referencing.operation.matrix.GeneralMatrix;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.operation.MathTransform;

/**
 * Default mosaic grid.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public abstract class AbstractGridMosaic implements GridMosaic{

    private final String id;
    private final Pyramid pyramid;
    private final DirectPosition upperLeft;
    private final Dimension gridSize;
    private final Dimension tileSize;
    private final double scale;

    public AbstractGridMosaic(Pyramid pyramid, DirectPosition upperLeft, Dimension gridSize,
            Dimension tileSize, double scale) {
        this(null,pyramid,upperLeft,gridSize,tileSize,scale);
    }

    public AbstractGridMosaic(String id, Pyramid pyramid, DirectPosition upperLeft, Dimension gridSize,
            Dimension tileSize, double scale) {
        this.pyramid = pyramid;
        this.upperLeft = new GeneralDirectPosition(upperLeft);
        this.scale = scale;
        this.gridSize = (Dimension) gridSize.clone();
        this.tileSize = (Dimension) tileSize.clone();

        if(id == null){
            this.id = UUID.randomUUID().toString();
        }else{
            this.id = id;
        }

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
    public DirectPosition getUpperLeftCorner() {
        return new GeneralDirectPosition(upperLeft); //defensive copy
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
    public Envelope getEnvelope(final int col, final int row) {
        final GeneralDirectPosition ul = new GeneralDirectPosition(getUpperLeftCorner());
        final double minX = ul.getOrdinate(0);
        final double maxY = ul.getOrdinate(1);
        final double spanX = tileSize.width * scale;
        final double spanY = tileSize.height * scale;

        final GeneralEnvelope envelope = new GeneralEnvelope(ul,ul);
        envelope.setRange(0, minX + col*spanX, minX + (col+1)*spanX);
        envelope.setRange(1, maxY - (row+1)*spanY, maxY - row*spanY);

        return envelope;
    }

    @Override
    public Envelope getEnvelope(){
        final GeneralDirectPosition ul = new GeneralDirectPosition(getUpperLeftCorner());
        final double minX = ul.getOrdinate(0);
        final double maxY = ul.getOrdinate(1);
        final double spanX = getTileSize().width * getGridSize().width * getScale();
        final double spanY = getTileSize().height* getGridSize().height* getScale();

        final GeneralEnvelope envelope = new GeneralEnvelope(ul,ul);
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

    /**
     * Grid to CRS N dimension.
     *
     * @param mosaic not null
     * @param location not null
     * @return MathTransform never null
     */
    public static MathTransform getTileGridToCRS(GridMosaic mosaic, Point location){
        final DirectPosition upperleft = mosaic.getUpperLeftCorner();
        return getTileGridToCRSND(mosaic, location, upperleft.getDimension());
    }

    /**
     * Grid to CRS N dimension.
     * This allows to create a transform ignoring last axis transform.
     *
     * @param mosaic not null
     * @param location not null
     * @param nbDim : number of dimension wanted. value must be in range [2...crsNbDim]
     * @return MathTransform never null
     */
    public static MathTransform getTileGridToCRSND(GridMosaic mosaic, Point location, int nbDim){

        final AffineTransform2D trs2d = getTileGridToCRS2D(mosaic, location);
        final DirectPosition upperleft = mosaic.getUpperLeftCorner();

        if(upperleft.getDimension()==2){
            return trs2d;
        }else{
            final int dim = nbDim+1;
            final GeneralMatrix gm = new GeneralMatrix(dim);
            gm.setElement(0, 0, trs2d.getScaleX());
            gm.setElement(1, 1, trs2d.getScaleY());
            gm.setElement(0, dim-1, trs2d.getTranslateX());
            gm.setElement(1, dim-1, trs2d.getTranslateY());
            for(int i=2;i<dim-1;i++){
                gm.setElement(i, i, 1);
                gm.setElement(i, dim-1, upperleft.getOrdinate(i));
            }
            return MathTransforms.linear(gm);
        }
    }

    /**
     * Grid to CRS 2D part. 
     * Transform correspond to the CORNER.
     *
     * @param mosaic not null
     * @param location not null
     * @return AffineTransform2D never null.
     */
    public static AffineTransform2D getTileGridToCRS2D(GridMosaic mosaic, Point location){

        final Dimension tileSize = mosaic.getTileSize();
        final DirectPosition upperleft = mosaic.getUpperLeftCorner();
        final double scale = mosaic.getScale();

        final double offsetX  = upperleft.getOrdinate(0) + location.x * (scale * tileSize.width) ;
        final double offsetY = upperleft.getOrdinate(1) - location.y * (scale * tileSize.height);
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
