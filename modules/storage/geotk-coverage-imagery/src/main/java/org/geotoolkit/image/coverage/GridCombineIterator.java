/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2015, Geomatys
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
package org.geotoolkit.image.coverage;

import java.util.Iterator;
import org.apache.sis.geometry.Envelopes;
import org.apache.sis.geometry.GeneralEnvelope;
import org.geotoolkit.coverage.grid.GeneralGridGeometry;
import org.geotoolkit.internal.referencing.CRSUtilities;
import org.opengis.coverage.grid.GridCoordinates;
import org.opengis.coverage.grid.GridEnvelope;
import org.opengis.geometry.Envelope;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

/**
 * {@link Iterator} to iterate on each {@link Envelope} dimensions from {@link GeneralGridGeometry}.<br>
 * For each steps it return a slice part of the source envelope.<br>
 * The right way to use this iterator is into a while loop, like follow : <br><br>
 * {@code 
 * while (iterator.hasNext()) {
 *      Envelope myEnvelope = iterator.next();
 * } }
 * 
 * @author Remi Marechal (Geomatys).
 * @version 4.0
 * @since   4.0
 */
public strictfp class GridCombineIterator implements Iterator<Envelope> {

    /**
     * Associate {@link MathTransform} with the grid.
     */
    private final MathTransform gridToCrs;
    
    /**
     * Grid space dimension.
     */
    private final int dim;
    
    /**
     * Current affected ordinate by iterator.
     */
    private final int[] affectedOrdinateIndex;
    
    /**
     * The minimum grid values on all dimensions.
     * 
     * @see GridEnvelope#getLow() 
     * @see GridCoordinates#getCoordinateValues() 
     */
    private final int[] gridLow;
    
    /**
     * The maximum grid values on all dimensions.
     * 
     * @see GridEnvelope#getHigh()  
     * @see GridCoordinates#getCoordinateValues() 
     */
    private final int[] gridHigh;
    
    /**
     * Define if exist another iteration.
     * @see #hasNext() 
     */
    private boolean finish = false;
    
    /**
     * The destination base grid which will be use to build destination {@link Envelope}.
     * 
     * @see #next() 
     */
    private final GeneralEnvelope currentGrid;
    
    /**
     * An internaly array which define on which ordinate dimension this iterator work.
     * 
     * @see #nextCursorPos(int) 
     */
    private final int[] affectedOrdinate;
    
    /**
     * <p> Create an {@link Iterator} on each {@link GridEnvelope} dimensions, 
     * which are not within the 2D part of the {@link CoordinateReferenceSystem}.<br>
     * Objects returned by {@link #next() } are {@link Envelope} type, which are built from iteration on each 
     * {@link GridEnvelope} dimensions and transformed into the outCrs by the {@linkplain MathTransform gridToCrs}.<br><br>
     * <strong>
     * Moreover : the specified {@link MathTransform} must be from CORNER of source grid point to CORNER of destination envelope point.<br>
     * The used MathTransform is consider with {@link PixelInCell#CELL_CORNER} configuration.</strong></p>
     * 
     * @param extent the grid which will be travel by iterator.
     * @param outCRS the crs of the out envelope.
     * @param gridToCrs the specified MathTransform considered in {@link PixelInCell#CELL_CORNER} configuration. 
     */
    public GridCombineIterator(final GridEnvelope extent, final CoordinateReferenceSystem outCRS, final MathTransform gridToCrs) {
        this.gridToCrs = gridToCrs;
        dim            = extent.getDimension();
        
        //-- fill the min max array with min and max from interested dimension iteration
        gridLow     = extent.getLow().getCoordinateValues();
        gridHigh    = extent.getHigh().getCoordinateValues();
        
        //-- build the future generate grid.
        currentGrid = new GeneralEnvelope(outCRS);
        
        //-- set grid coordinates into output grid from the 2D Crs part.
        final int minHorizonOrdinate = CRSUtilities.firstHorizontalAxis(outCRS);
        currentGrid.setRange(minHorizonOrdinate, gridLow[minHorizonOrdinate],     gridHigh[minHorizonOrdinate]);
        currentGrid.setRange(minHorizonOrdinate + 1, gridLow[minHorizonOrdinate + 1], gridHigh[minHorizonOrdinate + 1]);
        
        //-- iteration only if dimension > 2.
        if (dim >= 3) {
            if (dim != outCRS.getCoordinateSystem().getDimension())
                throw new MismatchedDimensionException("GridCombineIterator : Extent number dimension mismatch with CRS "
                        + "source Dimension. Extent dim = "+dim+" CRS dim = "+outCRS.getCoordinateSystem().getDimension());

            affectedOrdinate      = new int[dim - 2];//-- gerer le cas envelope 2D c a d dim - 2 = 0.
            int aoi = 0; 
            
            //-- fill affected ordinate which represente values at the first iteration.
            affectedOrdinateIndex = new int[dim - 2];//-- index dans l'iterateur.
            for (int i = 0; i < dim; i++) {
                if (i != minHorizonOrdinate && i != (minHorizonOrdinate + 1)) {
                    affectedOrdinate[aoi]      = i;
                    affectedOrdinateIndex[aoi] = extent.getLow(i);//-- prepare first iteration
                }
            }
        } else {
            affectedOrdinate = affectedOrdinateIndex = null;
        }
    }
    
    /**
     * Create an {@link Iterator} on each {@link Envelope} dimensions from {@link GeneralGridGeometry#getEnvelope() }, 
     * which are not within the 2D part of the {@link CoordinateReferenceSystem} from {@link GeneralGridGeometry#getCoordinateReferenceSystem() }.
     * 
     * @param gridGeom the grid Geometry which contain all needed informations to iterate on its envelope dimension.
     */
    public GridCombineIterator(final GeneralGridGeometry gridGeom) {
        this(gridGeom.getExtent(), gridGeom.getCoordinateReferenceSystem(), gridGeom.getGridToCRS(PixelInCell.CELL_CORNER));
    }
    
    /**
     * Pass to the next dimension cursor position.
     * 
     * @param currentDim current traveled dimension.
     */
    private void nextCursorPos(final int currentDim) {
         if (currentDim < 0) {
             finish = true;
             return;
         }
         assert currentDim >= 0 : "CombineIterator.nextCursorPos() : current dimension should be into N space. "
                 + "But it is into Z space. currentDim = "+currentDim; 
         affectedOrdinateIndex[currentDim]++;
         if (affectedOrdinateIndex[currentDim] > gridHigh[affectedOrdinate[currentDim]]) { //-- test if travel all current dimension values
             affectedOrdinateIndex[currentDim] = gridLow[affectedOrdinate[currentDim]]; //-- initialize current dimension value
             nextCursorPos(currentDim - 1);//-- pass to the next dimension
         }
    }

    /**
     * Returns {@code true} if an other slice {@link Envelope} may be computed else {@code false}.
     * 
     * @return {@code true} if an other slice {@link Envelope} may be computed else {@code false}.
     */
    @Override
    public boolean hasNext() {
        return !finish;
    }

    /**
     * Return if exist, the next multi-dimensional slice {@link Envelope}.
     * 
     * @return the next multi-dimensional slice {@link Envelope}.
     * @throws IllegalStateException if it doesn't exist any next iteration.
     * @throws IllegalStateException if problem during gridToCrs transformation.
     */
    @Override
    public Envelope next() {
        
        if (finish) throw new IllegalStateException("GridCombineIterator : Iteration on all dimensions is finished.");
        
        try {
            //-- if 2D
            if (dim < 3) {
                finish = true;
                final GeneralEnvelope outEnv = Envelopes.transform(gridToCrs, currentGrid);
                //-- set crs into out envelope
                outEnv.setCoordinateReferenceSystem(currentGrid.getCoordinateReferenceSystem());
                return outEnv;
            }
            
            //-- build new envelope from new grid
            //-- 1 : fill gridLow and gridHigh arrays which represent grid ordinates of future returned envelope
            assert dim > 2 : "GridCombineIterator.next() : expected dimension > 2, found : dim = "+dim;
            assert (affectedOrdinate.length == dim - 2) : "GridCombineIterator.next() : expected affectedOrdinate.length = "+(dim - 2)+", found : "+affectedOrdinate.length;
            assert (affectedOrdinate.length == affectedOrdinateIndex.length) : "GridCombineIterator.next() : expected affectedOrdinateIndex.length = "+(dim - 2)+", found : "+affectedOrdinateIndex.length;

            //-- Set appriopriate values into grid envelope.
            for (int i = 0; i < affectedOrdinate.length; i++) {
                currentGrid.setRange(affectedOrdinate[i], affectedOrdinateIndex[i], affectedOrdinateIndex[i]);
            }
        
            final GeneralEnvelope returnedEnvelope = Envelopes.transform(gridToCrs, currentGrid);
            nextCursorPos(dim - 3);
            returnedEnvelope.setCoordinateReferenceSystem(currentGrid.getCoordinateReferenceSystem());
            return returnedEnvelope;
            
        } catch (TransformException ex) {
            throw new IllegalStateException("GridCombineIterator.next() : ", ex);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void remove() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
