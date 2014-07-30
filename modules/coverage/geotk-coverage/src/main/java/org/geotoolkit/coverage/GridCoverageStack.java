/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.sis.referencing.operation.transform.MathTransforms;
import org.geotoolkit.coverage.grid.GeneralGridEnvelope;
import org.geotoolkit.coverage.grid.GeneralGridGeometry;
import org.apache.sis.referencing.operation.transform.PassThroughTransform;
import org.geotoolkit.referencing.operation.transform.DimensionFilter;
import org.geotoolkit.referencing.operation.transform.LinearInterpolator1D;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.coverage.grid.GridEnvelope;
import org.opengis.coverage.grid.GridGeometry;
import org.opengis.coverage.grid.GridNotEditableException;
import org.opengis.coverage.grid.GridPacking;
import org.opengis.coverage.grid.GridRange;
import org.opengis.coverage.grid.InvalidRangeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.operation.*;
import org.opengis.util.FactoryException;

/**
 * Subclass of CoverageStack for regular grid ND coverage.
 *
 *
 * @author Johann Sorel (Geomatys)
 * @author Quentin Boileau (Geomatys)
 */
public class GridCoverageStack extends CoverageStack implements GridCoverage {

    private GridGeometry gridGeometry = null;

    public GridCoverageStack(CharSequence name, final Collection<? extends GridCoverage> coverages)
            throws IOException, TransformException, FactoryException {
        super(name, coverages);
        buildGridGeometry();
    }

    public GridCoverageStack(CharSequence name, final Collection<? extends GridCoverage> coverages, Integer zDimension)
            throws IOException, TransformException, FactoryException {
        super(name, coverages, zDimension);
        buildGridGeometry();
    }

    public GridCoverageStack(CharSequence name, final CoordinateReferenceSystem crs, final Collection<? extends Element> elements)
            throws IOException, TransformException, FactoryException {
        super(name, crs, elements);
        buildGridGeometry();
    }

    public GridCoverageStack(CharSequence name, final CoordinateReferenceSystem crs, final Collection<? extends Element> elements, Integer zDimension)
            throws IOException, TransformException, FactoryException {
        super(name, crs, elements, zDimension);
        buildGridGeometry();
    }

    private void buildGridGeometry() throws IOException, TransformException, FactoryException{

        final Element[] elements = getElements();
        final CoordinateReferenceSystem crs = getCoordinateReferenceSystem();
        final int nbDim = crs.getCoordinateSystem().getDimension();

        if(elements.length==0){
            throw new IOException("Coverages list is empty");
        }

        //build the grid geometry
        final int[] gridLower = new int[nbDim];
        final int[] gridUpper = new int[nbDim];
        final double[] zAxisSteps = new double[elements.length];
        MathTransform baseGridToCRS = null;
        int k=0;
        for(Element element : elements){
            final GridCoverage coverage = (GridCoverage) element.getCoverage(null);

            final GridGeometry gg = coverage.getGridGeometry();
            final GridEnvelope ext = gg.getExtent();
            final MathTransform trs = gg.getGridToCRS();

            //we expect the axisIndex dimension to be a slice, low == high
            if(ext.getLow(zDimension) != ext.getHigh(zDimension)){
                throw new IOException("Last dimension of the coverage is not a slice.");
            }

            if(baseGridToCRS == null){
                for(int i=0; i<nbDim; i++){
                    gridLower[i] = ext.getLow(i);
                    gridUpper[i] = ext.getHigh(i);
                }
                baseGridToCRS = gg.getGridToCRS();
            }

            //find the real value
            final double[] coord = new double[gridUpper.length];
            for(int i=0;i<gridUpper.length;i++) {
                coord[i] = ext.getLow(i);
            }
            trs.transform(coord,0,coord,0,1);
            zAxisSteps[k] = coord[zDimension];

            //increment number of slices
            gridUpper[zDimension]++;

            k++;
        }

        // reduce by one, values are inclusive
        gridUpper[zDimension]--;

        int remainingDimensions = nbDim - zDimension - 1;

        /*
         * Rebuild GridGeometry gridToCRS by extracting MT parts before and after MT1D on current zDimension axis.
         * This is done in order to keep GridCoverage2D slice with all there dimension instead of truncate there dimensions
         * on each CoverageStack level.
         *
         * TODO replace this hack by a GridCoverageStackBuilder that rebuild global gridGeometry and propagate it to every level
         * and GridCoverage2D.
         */

        //extract MT [0, zDim[
        DimensionFilter df = new DimensionFilter();
        df.addSourceDimensionRange(0, zDimension);
        MathTransform firstMT = df.separate(baseGridToCRS);
        firstMT = PassThroughTransform.create(0, firstMT, nbDim - zDimension);

        //create dimension pass through transform with linear
        final MathTransform lastAxisTrs;
        if(zAxisSteps.length==1){
            lastAxisTrs = MathTransforms.linear(1, zAxisSteps[0]);
        }else{
            lastAxisTrs = LinearInterpolator1D.create(zAxisSteps);
        }
        final MathTransform dimLinear = PassThroughTransform.create(zDimension, lastAxisTrs, remainingDimensions);

        //extract MT [zDim+1, nbDim[
        MathTransform lastPart = null;
        if (remainingDimensions > 0) {
            df = new DimensionFilter();
            df.addSourceDimensionRange(zDimension+1, nbDim);
            lastPart = df.separate(baseGridToCRS);
            lastPart = PassThroughTransform.create(zDimension+1, lastPart, 0);
        }

        //build final gridToCRS
        final MathTransform gridToCRS;
        if (lastPart == null) {
            gridToCRS = MathTransforms.concatenate(firstMT, dimLinear);
        } else {
            gridToCRS = MathTransforms.concatenate(firstMT, dimLinear, lastPart);
        }

        //build gridGeometry
        final PixelInCell pixelInCell = PixelInCell.CELL_CENTER;
        final GeneralGridEnvelope gridEnv = new GeneralGridEnvelope(gridLower, gridUpper, true);
        gridGeometry = new GeneralGridGeometry(gridEnv, pixelInCell, gridToCRS, crs);
    }

    @Override
    public boolean isDataEditable() {
        return false;
    }

    @Override
    public GridPacking getGridPacking() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public GridGeometry getGridGeometry() {
        return gridGeometry;
    }

    @Override
    public int[] getOptimalDataBlockSizes() {
        return null;
    }

    @Override
    public int getNumOverviews() {
        return 0;
    }

    @Override
    public GridGeometry getOverviewGridGeometry(int index) throws IndexOutOfBoundsException {
        throw new IndexOutOfBoundsException("No overviews available");
    }

    @Override
    public GridCoverage getOverview(int index) throws IndexOutOfBoundsException {
        throw new IndexOutOfBoundsException("No overviews available");
    }

    @Override
    public List<GridCoverage> getSources() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public boolean[] getDataBlock(GridRange range, boolean[] destination) throws InvalidRangeException, ArrayIndexOutOfBoundsException {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public byte[] getDataBlock(GridRange range, byte[] destination) throws InvalidRangeException, ArrayIndexOutOfBoundsException {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public short[] getDataBlock(GridRange range, short[] destination) throws InvalidRangeException, ArrayIndexOutOfBoundsException {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int[] getDataBlock(GridRange range, int[] destination) throws InvalidRangeException, ArrayIndexOutOfBoundsException {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public float[] getDataBlock(GridRange range, float[] destination) throws InvalidRangeException, ArrayIndexOutOfBoundsException {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public double[] getDataBlock(GridRange range, double[] destination) throws InvalidRangeException, ArrayIndexOutOfBoundsException {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public byte[] getPackedDataBlock(GridRange range) throws InvalidRangeException {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void setDataBlock(GridRange range, boolean[] values) throws InvalidRangeException, GridNotEditableException, ArrayIndexOutOfBoundsException {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void setDataBlock(GridRange range, byte[] values) throws InvalidRangeException, GridNotEditableException, ArrayIndexOutOfBoundsException {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void setDataBlock(GridRange range, short[] values) throws InvalidRangeException, GridNotEditableException, ArrayIndexOutOfBoundsException {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void setDataBlock(GridRange range, int[] values) throws InvalidRangeException, GridNotEditableException, ArrayIndexOutOfBoundsException {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void setDataBlock(GridRange range, float[] values) throws InvalidRangeException, GridNotEditableException, ArrayIndexOutOfBoundsException {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void setDataBlock(GridRange range, double[] values) throws InvalidRangeException, GridNotEditableException, ArrayIndexOutOfBoundsException {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
