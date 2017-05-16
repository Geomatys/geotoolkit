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
package org.geotoolkit.storage.coverage;

import java.awt.*;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.apache.sis.referencing.operation.transform.MathTransforms;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.geometry.GeneralDirectPosition;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.internal.referencing.j2d.AffineTransform2D;
import org.apache.sis.util.Classes;
import org.geotoolkit.metadata.iso.spatial.PixelTranslation;
import org.geotoolkit.referencing.operation.matrix.GeneralMatrix;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.operation.MathTransform;

/**
 * Default mosaic grid.
 *
 * @author Johann Sorel (Geomatys)
 * @module
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

    /**
     * {@inheritDoc}
     */
    @Override
    public String getId() {
        return id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pyramid getPyramid() {
        return pyramid;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DirectPosition getUpperLeftCorner() {
        return new GeneralDirectPosition(upperLeft); //defensive copy
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Dimension getGridSize() {
        return (Dimension) gridSize.clone(); //defensive copy
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Dimension getTileSize() {
        return (Dimension) tileSize.clone(); //defensive copy
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getScale() {
        return scale;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Envelope getEnvelope(final int col, final int row) {
        final GeneralDirectPosition ul = new GeneralDirectPosition(getUpperLeftCorner());
        final int xAxis = Math.max(CoverageUtilities.getMinOrdinate(ul.getCoordinateReferenceSystem()), 0);
        final int yAxis = xAxis + 1;
        final double minX = ul.getOrdinate(xAxis);
        final double maxY = ul.getOrdinate(yAxis);
        final double spanX = tileSize.width * scale;
        final double spanY = tileSize.height * scale;

        final GeneralEnvelope envelope = new GeneralEnvelope(ul,ul);
        envelope.setRange(xAxis, minX + col*spanX, minX + (col+1)*spanX);
        envelope.setRange(yAxis, maxY - (row+1)*spanY, maxY - row*spanY);

        return envelope;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Envelope getEnvelope() {
        final GeneralDirectPosition ul = new GeneralDirectPosition(getUpperLeftCorner());
        final int xAxis = Math.max(CoverageUtilities.getMinOrdinate(ul.getCoordinateReferenceSystem()), 0);
        final int yAxis = xAxis + 1;
        final double minX = ul.getOrdinate(xAxis);
        final double maxY = ul.getOrdinate(yAxis);
        final double spanX = getTileSize().width * getGridSize().width * getScale();
        final double spanY = getTileSize().height* getGridSize().height* getScale();

        final GeneralEnvelope envelope = new GeneralEnvelope(ul,ul);
        envelope.setRange(xAxis, minX, minX + spanX);
        envelope.setRange(yAxis, maxY - spanY, maxY );

        return envelope;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isMissing(int col, int row) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BlockingQueue<Object> getTiles(Collection<? extends Point> positions, Map hints) throws DataStoreException{
        return getTiles(this, positions, hints);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Rectangle getDataExtent() {

        Point start = null;
        for (int y = 0; y < gridSize.height; y++) {
            for (int x = 0; x < gridSize.width; x++) {
                if (!isMissing(x,y)) {
                     start = new Point(x,y);
                     break;//--get only the first point of the grid
                }
            }
            if (start != null) break;//--get only the first point of the grid
        }

        if (start != null) {
            Point end = null;
            for (int y = gridSize.height-1; y >= start.y; y--) {
                for (int x = gridSize.width-1; x >= start.x; x--) {
                    if (!isMissing(x, y)) {
                        end = new Point(x, y);
                        break; //-- get only the last tile grid
                    }
                }
                if (end != null) break; //-- get only the last tile grid
            }

            assert end.x >= start.x;
            assert end.y >= start.y;

            return new Rectangle(start.x*tileSize.width, start.y*tileSize.height,
                                (end.x - start.x + 1) * tileSize.width,
                                (end.y - start.y + 1) * tileSize.height);
        } else {
            //all mosaic tiles are missing
            return new Rectangle(0,0, gridSize.width*tileSize.width, gridSize.height * tileSize.height);
        }
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
     * Grid to CRS N dimension. CORNER transform
     *
     * @param mosaic not null
     * @param location not null
     * @return MathTransform never null
     */
    public static MathTransform getTileGridToCRS(GridMosaic mosaic, Point location){
        return getTileGridToCRS(mosaic, location, PixelInCell.CELL_CORNER);
    }

    /**
     * Grid to CRS N dimension. CORNER transform
     *
     * @param mosaic not null
     * @param location not null
     * @param orientation pixel orientation
     * @return MathTransform never null
     */
    public static MathTransform getTileGridToCRS(GridMosaic mosaic, Point location, PixelInCell orientation){
        final DirectPosition upperleft = mosaic.getUpperLeftCorner();
        return getTileGridToCRSND(mosaic, location, upperleft.getDimension(), orientation);
    }

    /**
     * Grid to CRS N dimension. CORNER Transform.
     * This allows to create a transform ignoring last axis transform.
     *
     * @param mosaic not null
     * @param location not null
     * @param nbDim : number of dimension wanted. value must be in range [2...crsNbDim]
     * @return MathTransform never null
     */
    public static MathTransform getTileGridToCRSND(GridMosaic mosaic, Point location, int nbDim){
        return getTileGridToCRSND(mosaic, location, nbDim, PixelInCell.CELL_CORNER);
    }

    /**
     * Grid to CRS N dimension.
     * This allows to create a transform ignoring last axis transform.
     *
     * @param mosaic not null
     * @param location not null
     * @param nbDim : number of dimension wanted. value must be in range [2...crsNbDim]
     * @param orientation pixel orientation
     * @return MathTransform never null
     */
    public static MathTransform getTileGridToCRSND(GridMosaic mosaic, Point location, int nbDim, PixelInCell orientation){

        final AffineTransform2D trs2d = getTileGridToCRS2D(mosaic, location, orientation);
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
        return getTileGridToCRS2D(mosaic, location, PixelInCell.CELL_CORNER);
    }

    /**
     * Grid to CRS 2D part.
     *
     * @param mosaic not null
     * @param location not null
     * @param orientation pixel orientation
     * @return AffineTransform2D never null.
     */
    public static AffineTransform2D getTileGridToCRS2D(GridMosaic mosaic, Point location, PixelInCell orientation){

        final Dimension tileSize = mosaic.getTileSize();
        final DirectPosition upperleft = mosaic.getUpperLeftCorner();
        final double scale = mosaic.getScale();

        final double offsetX  = upperleft.getOrdinate(0) + location.x * (scale * tileSize.width) ;
        final double offsetY = upperleft.getOrdinate(1) - location.y * (scale * tileSize.height);
        AffineTransform2D transform2D = new AffineTransform2D(scale, 0, 0, -scale, offsetX, offsetY);
        if (orientation.equals(PixelInCell.CELL_CENTER)) {
            return (AffineTransform2D) PixelTranslation.translate(transform2D, PixelInCell.CELL_CORNER, PixelInCell.CELL_CENTER);
        }
        return transform2D;
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
