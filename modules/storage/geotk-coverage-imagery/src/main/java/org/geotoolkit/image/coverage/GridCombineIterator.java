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
import java.util.List;
import org.apache.sis.geometry.Envelopes;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.measure.NumberRange;
import org.apache.sis.referencing.crs.DefaultEngineeringCRS;
import org.apache.sis.referencing.datum.DefaultEngineeringDatum;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.NullArgumentException;
import org.geotoolkit.coverage.grid.GeneralGridGeometry;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.internal.referencing.CRSUtilities;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.ReferencingUtilities;
import org.opengis.coverage.grid.GridCoordinates;
import org.opengis.coverage.grid.GridEnvelope;
import org.opengis.geometry.Envelope;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.EngineeringCRS;
import org.opengis.referencing.cs.CSFactory;
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
public final strictfp class GridCombineIterator implements Iterator<Envelope> {
    
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
     * An internaly {@code Integer} use to refer the current traveled dimension array.
     */
    private final int currentDimIndex;
    
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
     * @param outCRS the crs of the out envelope, may be {@code null}.
     * @param gridToCrs the specified MathTransform considered in {@link PixelInCell#CELL_CORNER} configuration. 
     * 
     * @throws NullArgumentException if any of extent or gridtocrs is {@code null}.
     * @throws MismatchedDimensionException if dimension between crs (if it is not null) dimension and {@linkplain MathTransform#getTargetDimensions() gridToCrs.getTargetDimensions()} mismatch,
     * or if dimension between extent and {@linkplain MathTransform#getSourceDimensions() gridToCrs.getSourceDimensions()} mismatch.
     */
    public GridCombineIterator(final GridEnvelope extent, final CoordinateReferenceSystem outCRS, final MathTransform gridToCrs) {
        ArgumentChecks.ensureNonNull("extent", extent);
        ArgumentChecks.ensureNonNull("gridToCrs", gridToCrs);
        this.gridToCrs = gridToCrs;
        dim            = extent.getDimension();
        
        //-- check validity of non null parameters.
        if (gridToCrs.getSourceDimensions() != dim) 
            throw new MismatchedDimensionException("GridCombineIterator : extent dimension and gridToCrs sourceDimension mismatch. "
                    + "Extent dimension = "+dim+" GridToCrs source dimension = "+gridToCrs.getSourceDimensions());
        if (outCRS != null && outCRS.getCoordinateSystem().getDimension() != dim) 
            throw new MismatchedDimensionException("GridCombineIterator : crs dimension and expected targetDimension mismatch. "
                    + "expected dimension = "+dim+" crs dimension = "+outCRS.getCoordinateSystem().getDimension());
        
        //-- fill the min max array with min and max from interested dimension iteration
        gridLow     = extent.getLow().getCoordinateValues();
        gridHigh    = extent.getHigh().getCoordinateValues();
        
        //-- build the future generate grid.
        currentGrid = (outCRS != null) ? new GeneralEnvelope(outCRS) : new GeneralEnvelope(dim);
        
        //-- set grid coordinates into output grid from the 2D Crs part.
        final int minHorizonOrdinate = CRSUtilities.firstHorizontalAxis(outCRS);
        currentGrid.setRange(minHorizonOrdinate, gridLow[minHorizonOrdinate],     gridHigh[minHorizonOrdinate]);
        currentGrid.setRange(minHorizonOrdinate + 1, gridLow[minHorizonOrdinate + 1], gridHigh[minHorizonOrdinate + 1]);
        
        //-- initialize the current dimension array index
        currentDimIndex = dim - 3; //-- in other words iterator travel first affectedOrdinate[currentDimIndex] see next() method.
        
        //-- iteration only if dimension > 2.
        if (dim >= 3) {
            if (dim != outCRS.getCoordinateSystem().getDimension())
                throw new MismatchedDimensionException("GridCombineIterator : Extent number dimension mismatch with CRS "
                        + "source Dimension. Extent dim = "+dim+" CRS dim = "+outCRS.getCoordinateSystem().getDimension());

            affectedOrdinate      = new int[dim - 2];
            int aoi = 0; 
            
            //-- fill affected ordinate which represente values at the first iteration.
            affectedOrdinateIndex = new int[dim - 2];//-- index dans l'iterateur.
            for (int i = 0; i < dim; i++) {
                if (i != minHorizonOrdinate && i != (minHorizonOrdinate + 1)) {
                    affectedOrdinate[aoi]      = i;
                    affectedOrdinateIndex[aoi++] = extent.getLow(i);//-- prepare first iteration
                }
            }
        } else {
            affectedOrdinate = affectedOrdinateIndex = null;
        }
    }
    
    /**
     * <p> Create an {@link Iterator} which travel a specified {@link GridEnvelope} dimension, 
     * which are not within the 2D part of the {@link CoordinateReferenceSystem}.<br>
     * Objects returned by {@link #next() } are {@link Envelope} type, which are built from iteration on the expected 
     * {@link GridEnvelope} dimension and transformed into the outCrs by the {@linkplain MathTransform gridToCrs}.<br><br>
     * <strong>
     * Moreover : the specified {@link MathTransform} must be from CORNER of source grid point to CORNER of destination envelope point.<br>
     * The used MathTransform is consider with {@link PixelInCell#CELL_CORNER} configuration.</strong></p>
     * 
     * @param extent the grid which will be travel by iterator.
     * @param outCRS the crs of the out envelope, may be {@code null}.
     * @param gridToCrs the specified MathTransform considered in {@link PixelInCell#CELL_CORNER} configuration. 
     * @param interestedOrdinateIndex the specified dimension on which the iterator travel.
     * 
     * @throws NullArgumentException if any of extent or gridtocrs is {@code null}.
     * @throws MismatchedDimensionException if dimension between crs (if it is not null) dimension and {@linkplain MathTransform#getTargetDimensions() gridToCrs.getTargetDimensions()} mismatch,
     * or if dimension between extent and {@linkplain MathTransform#getSourceDimensions() gridToCrs.getSourceDimensions()} mismatch.
     */
    public GridCombineIterator(final GridEnvelope extent,     final CoordinateReferenceSystem outCRS, 
                               final MathTransform gridToCrs, final int interestedOrdinateIndex) {
        ArgumentChecks.ensureNonNull("extent", extent);
        ArgumentChecks.ensureNonNull("gridToCrs", gridToCrs);
        ArgumentChecks.ensurePositive("interestedOrdinateIndex", interestedOrdinateIndex);
        this.gridToCrs = gridToCrs;
        dim            = extent.getDimension();
        
        //-- check validity of expected dimension
        if (interestedOrdinateIndex >= dim)
            throw new IndexOutOfBoundsException("GridCombineIterator : The interestedOrdinateIndex is upper or equals than maximum dimension number"
                    + " from grid dimension. Expected lower than : "+dim+", found : "+interestedOrdinateIndex);
        
        //-- check validity of non null parameters.
        if (gridToCrs.getSourceDimensions() != dim) 
            throw new MismatchedDimensionException("GridCombineIterator : extent dimension and gridToCrs sourceDimension mismatch. "
                    + "Extent dimension = "+dim+" GridToCrs source dimension = "+gridToCrs.getSourceDimensions());
        if (outCRS != null) {
            if (outCRS.getCoordinateSystem().getDimension() != dim) 
                throw new MismatchedDimensionException("GridCombineIterator : crs dimension and expected targetDimension mismatch. "
                        + "expected dimension = "+dim+" crs dimension = "+outCRS.getCoordinateSystem().getDimension());
            final int minHorizonOrdinate = CRSUtilities.firstHorizontalAxis(outCRS);
            if (interestedOrdinateIndex == minHorizonOrdinate || interestedOrdinateIndex == minHorizonOrdinate + 1)
                throw new MismatchedDimensionException("GridCombineIterator : you cannot iterate on (geographic)2D CRS part."
                        + "The interestedOrdinateIndex must be out of ["+minHorizonOrdinate+"; "+(minHorizonOrdinate + 1)+"] interval. Found : "+interestedOrdinateIndex);
        }
        
        //-- fill the min max array with min and max from interested dimension iteration
        gridLow     = extent.getLow().getCoordinateValues();
        gridHigh    = extent.getHigh().getCoordinateValues();
        
        //-- build the future generate grid.
        currentGrid = (outCRS != null) ? new GeneralEnvelope(outCRS) : new GeneralEnvelope(dim);
        
        for (int d = 0; d < dim; d++) {
            //-- set grid coordinates into output grid from other ordinates than interestedOrdinateIndex.
            if (d != interestedOrdinateIndex) 
                currentGrid.setRange(d, gridLow[d], gridHigh[d]);
        }
        
        //-- initialize the current dimension array index
        currentDimIndex = 0; //-- in other words iterator travel affectedOrdinate[currentDimIndex] see next() method.
        
        //-- iteration only if dimension > 2.
        if (dim >= 3) {
            if (outCRS != null && dim != outCRS.getCoordinateSystem().getDimension())
                throw new MismatchedDimensionException("GridCombineIterator : Extent number dimension mismatch with CRS "
                        + "source Dimension. Extent dim = "+dim+" CRS dim = "+outCRS.getCoordinateSystem().getDimension());

            //-- prepare iteration on only one axis
            affectedOrdinate      = new int[]{interestedOrdinateIndex};
            
            //-- fill affected ordinate which represente values at the first iteration.
            affectedOrdinateIndex = new int[]{extent.getLow(interestedOrdinateIndex)};
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
                
                //-- set crs into out envelope if it is not null
                if (currentGrid.getCoordinateReferenceSystem() != null) 
                    outEnv.setCoordinateReferenceSystem(currentGrid.getCoordinateReferenceSystem());
                
                return outEnv;
            }
            
            //-- build new envelope from new grid
            //-- 1 : fill gridLow and gridHigh arrays which represent grid ordinates of future returned envelope
            assert dim > 2 : "GridCombineIterator.next() : expected dimension > 2, found : dim = "+dim;
            assert (assertNext());

            //-- Set appriopriate values into grid envelope.
            for (int i = 0; i < affectedOrdinate.length; i++) {
                currentGrid.setRange(affectedOrdinate[i], affectedOrdinateIndex[i], affectedOrdinateIndex[i]);
            }
        
            final GeneralEnvelope returnedEnvelope = Envelopes.transform(gridToCrs, currentGrid);
            
            //-- prepare next iteration
            nextCursorPos(currentDimIndex);
            
            //-- set crs into out envelope if it is not null
            if (currentGrid.getCoordinateReferenceSystem() != null) 
                returnedEnvelope.setCoordinateReferenceSystem(currentGrid.getCoordinateReferenceSystem());
            
            return returnedEnvelope;
            
        } catch (TransformException ex) {
            throw new IllegalStateException("GridCombineIterator.next() : ", ex);
        }
    }
    
    /**
     * Effectuate some verifications to expected {@link #next() } comportement.
     * 
     * @return {@code true} if values are verify, else return {@code false}.
     */
    private boolean assertNext() {
        if (currentDimIndex == 0) {
            assert (affectedOrdinate.length == 1) : "GridCombineIterator.next() : expected affectedOrdinate.length = 1, found : "+affectedOrdinate.length;
            assert (affectedOrdinate.length == affectedOrdinateIndex.length) : "GridCombineIterator.next() : expected affectedOrdinateIndex.length = "+(dim - 2)+", found : "+affectedOrdinateIndex.length;
        } else {
            assert (affectedOrdinate.length == dim - 2) : "GridCombineIterator.next() : expected affectedOrdinate.length = "+(dim - 2)+", found : "+affectedOrdinate.length;
            assert (affectedOrdinate.length == affectedOrdinateIndex.length) : "GridCombineIterator.next() : expected affectedOrdinateIndex.length = "+(dim - 2)+", found : "+affectedOrdinateIndex.length;
        }
        return true;
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public void remove() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    /**
     * Returns all ordinates ranges from grid transformation of {@linkplain GridEnvelope extent} 
     * by {@linkplain MathTransform gridtocrs} on axis specified by expected one dimensional {@link CoordinateReferenceSystem}.<br><br>
     * <strong>
     * Moreover : the specified {@link MathTransform} must be from CORNER of source grid point to CORNER of destination envelope point.<br>
     * The used MathTransform is consider with {@link PixelInCell#CELL_CORNER} configuration.</strong>
     * 
     * @param gridGeometry {@link GeneralGridGeometry} which contain {@linkplain GeneralGridGeometry#getExtent() extent}
     *                     and {@linkplain GeneralGridGeometry#getGridToCRS() gridToCrs} necessary to find all expected ordinate values.
     * @param crs 1 Dimensional {@link CoordinateReferenceSystem} which represent expected interested ordinate index.
     * @return an array which contain all MINIMUM destination ordinates values from interested dimension (axis). 
     * 
     * @throws NullArgumentException if any of egridGeometry or crs is {@code null}.
     * @throws IllegalArgumentException if crs in parameter is not include in the internal gridGeometry CoordinateReferenceSystem.
     * @throws MismatchedDimensionException if the parameter crs have a dimension upper than 1.
     * @see #extractAxisRanges(org.geotoolkit.coverage.grid.GeneralGridGeometry, int) 
     */
    public static NumberRange<Double>[] extractAxisRanges(final GeneralGridGeometry gridGeometry, final CoordinateReferenceSystem crs) {
        ArgumentChecks.ensureNonNull("gridGeometry", gridGeometry);
        ArgumentChecks.ensureNonNull("crs", crs);
        
        if (crs.getCoordinateSystem().getDimension() > 1)
            throw new MismatchedDimensionException("The parameter crs which define on each axis the ordinate values are compute have a "
                    + "too great dimension number.Expected dimension number 1, found : "+crs.getCoordinateSystem().getDimension());
        
        final List<CoordinateReferenceSystem> listCrs = ReferencingUtilities.decompose(gridGeometry.getCoordinateReferenceSystem());
        int interestedOrdinateIndex = 0;
        for (final CoordinateReferenceSystem currentCrs : listCrs) {
            if (CRS.equalsIgnoreMetadata(currentCrs, crs)) break;
            interestedOrdinateIndex += currentCrs.getCoordinateSystem().getDimension();
        }
        
       /*
        * if interestedOrdinateIndex == gridgeometry dimension number -> means crs parameter 
        * not include in gridGeometry compoundCrs
        */
        if (interestedOrdinateIndex == gridGeometry.getDimension()) 
            throw new IllegalArgumentException("The crs in parameter is not include in the internal "
                    + "gridGeometry CoordinateReferenceSystem.");
        return GridCombineIterator.extractAxisRanges(gridGeometry, interestedOrdinateIndex);
    }
    
    /**
     * Returns all ordinates ranges from grid transformation of {@linkplain GridEnvelope extent} 
     * by {@linkplain MathTransform gridtocrs} from {@link GeneralGridGeometry} on axis specified by expected dimension parameter.<br>
     * One range for each grid values on interested ordinate.<br><br>
     * <strong>
     * Moreover : the specified {@link MathTransform} must be from CORNER of source grid point to CORNER of destination envelope point.<br>
     * The used MathTransform is consider with {@link PixelInCell#CELL_CORNER} configuration.</strong>
     * 
     * @param gridGeometry {@link GeneralGridGeometry} which contain {@linkplain GeneralGridGeometry#getExtent() extent}
     *                     and {@linkplain GeneralGridGeometry#getGridToCRS() gridToCrs} necessary to find all expected ordinate values.
     * @param interestedOrdinateIndex expected interested ordinate index.
     * @return an array which contain all destination ordinate values from interested dimension. 
     * 
     * @throws NullArgumentException if any of extent or gridtocrs is {@code null}.
     * @throws IllegalArgumentException if interestedOrdinateIndex is negative.
     * @throws MismatchedDimensionException if dimension between extent and
     * {@linkplain MathTransform#getSourceDimensions() gridToCrs.getSourceDimensions()} mismatch.
     * @throws IndexOutOfBoundsException if interestedOrdinateIndex is upper than extent or gridtocrs dimension.
     */
    public static NumberRange<Double>[] extractAxisRanges(final GeneralGridGeometry gridGeometry, final int interestedOrdinateIndex) {
        return GridCombineIterator.extractAxisRanges(gridGeometry.getExtent(), gridGeometry.getGridToCRS(PixelInCell.CELL_CORNER), gridGeometry.getCoordinateReferenceSystem(), interestedOrdinateIndex);
    }
    
    /**
     * Returns all ordinate ranges from grid transformation of {@linkplain GridEnvelope extent} 
     * by {@linkplain MathTransform gridtocrs} on axis specified by expected dimension parameter.<br>
     * One range for each grid values on interested ordinate.<br><br>
     * <strong>
     * Moreover : the specified {@link MathTransform} must be from CORNER of source grid point to CORNER of destination envelope point.<br>
     * The used MathTransform is consider with {@link PixelInCell#CELL_CORNER} configuration.</strong>
     * 
     * @param extent used grid to define ordinate value.
     * @param gridToCrs used gridtocrs to define destination ordinate values.
     * @param interestedOrdinateIndex expected interested ordinate index.
     * @return an array which contain all destination ordinate values from interested dimension. 
     * 
     * @throws NullArgumentException if any of extent or gridtocrs is {@code null}.
     * @throws IllegalArgumentException if interestedOrdinateIndex is negative.
     * @throws MismatchedDimensionException if dimension between extent and
     * {@linkplain MathTransform#getSourceDimensions() gridToCrs.getSourceDimensions()} mismatch.
     * @throws IndexOutOfBoundsException if interestedOrdinateIndex is upper than extent or gridtocrs dimension.
     */
    public static NumberRange<Double>[] extractAxisRanges(final GridEnvelope extent, final MathTransform gridToCrs, final int interestedOrdinateIndex) {
        return GridCombineIterator.extractAxisRanges(extent, gridToCrs, null, interestedOrdinateIndex);
    }
    
    /**
     * Returns all ordinate ranges from grid transformation of {@linkplain GridEnvelope extent} 
     * by {@linkplain MathTransform gridtocrs} on axis specified by expected dimension parameter.<br>
     * One range for each grid values on interested ordinate.<br><br>
     * <strong>
     * Moreover : the specified {@link MathTransform} must be from CORNER of source grid point to CORNER of destination envelope point.<br>
     * The used MathTransform is consider with {@link PixelInCell#CELL_CORNER} configuration.</strong>
     * 
     * @param extent used grid to define ordinate value.
     * @param gridToCrs used gridtocrs to define destination ordinate values.
     * @param crs destination {@link CoordinateReferenceSystem} of grid transformation by gridtocrs, may be {@code null}.
     * @param interestedOrdinateIndex expected interested ordinate index.
     * @return an array which contain all destination ordinate ranges from interested dimension. 
     * 
     * @throws NullArgumentException if any of extent or gridtocrs is {@code null}.
     * @throws IllegalArgumentException if interestedOrdinateIndex is negative.
     * @throws MismatchedDimensionException if dimension between crs (if it is not null) dimension and {@linkplain MathTransform#getTargetDimensions() gridToCrs.getTargetDimensions()} mismatch,
     * or if dimension between extent and {@linkplain MathTransform#getSourceDimensions() gridToCrs.getSourceDimensions()} mismatch.
     * @throws IndexOutOfBoundsException if interestedOrdinateIndex is upper than extent or gridtocrs dimension.
     */
    private static NumberRange<Double>[] extractAxisRanges(final GridEnvelope extent, final MathTransform gridToCrs, 
                                              final CoordinateReferenceSystem crs,    final int interestedOrdinateIndex) {
        ArgumentChecks.ensureNonNull("extent", extent);
        ArgumentChecks.ensureNonNull("gridToCrs", gridToCrs);
        ArgumentChecks.ensurePositive("interstedOrdinateIndex", interestedOrdinateIndex);
        
        final int dim = extent.getDimension();
        
        //-- check validity of parameters
        if (gridToCrs.getSourceDimensions() != dim) 
            throw new MismatchedDimensionException("GridCombineIterator : extent dimension and gridToCrs sourceDimension mismatch. "
                    + "Extent dimension = "+dim+" GridToCrs source dimension = "+gridToCrs.getSourceDimensions());
        if (crs != null && crs.getCoordinateSystem().getDimension() != dim) 
            throw new MismatchedDimensionException("GridCombineIterator : crs dimension and expected targetDimension mismatch. "
                    + "expected dimension = "+dim+" crs dimension = "+crs.getCoordinateSystem().getDimension());
        if (interestedOrdinateIndex >= dim)
            throw new IndexOutOfBoundsException("GridCombineIterator : The selected ordinate index exceed dimension number, it must be lesser.");
        //---------------------------------
        
        //-- define out array from asked axis value type.
        final NumberRange[] resultArray = new NumberRange[extent.getHigh(interestedOrdinateIndex) - extent.getLow(interestedOrdinateIndex) + 1];//-- +1 because getHigh() always return inclusive values.
        
        int i = 0;
        final GridCombineIterator gcint = new GridCombineIterator(extent, crs, gridToCrs, interestedOrdinateIndex);
        
        while (gcint.hasNext()) {
            final Envelope env = gcint.next();
            resultArray[i++]   = new NumberRange(Double.class, env.getMinimum(i), true, env.getMaximum(i), true);
        }
        return resultArray;
    }
}
