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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.geotoolkit.coverage.grid.GeneralGridEnvelope;
import org.geotoolkit.coverage.grid.GeneralGridGeometry;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.referencing.operation.matrix.GeneralMatrix;
import org.apache.sis.referencing.operation.transform.MathTransforms;
import org.apache.sis.referencing.operation.transform.LinearTransform;
import org.apache.sis.referencing.operation.transform.PassThroughTransform;
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
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.MathTransform1D;
import org.opengis.referencing.operation.Matrix;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 * Subclass of CoverageStack for regular grid ND coverage.
 *
 *
 * @author Johann Sorel (Geomatys)
 */
public class GridCoverageStack extends CoverageStack implements GridCoverage {

    private GridGeometry gridGeometry = null;

    public GridCoverageStack(CharSequence name, final Collection<? extends GridCoverage> coverages) throws IOException, TransformException, FactoryException {
        super(name, coverages);
        buildGridGeometry();
    }

    public GridCoverageStack(CharSequence name, final CoordinateReferenceSystem crs, final Collection<? extends Element> elements) throws IOException, TransformException, FactoryException {
        super(name, crs, elements);
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
        final double[] lastAxisSteps = new double[elements.length];
        MathTransform baseGridToCrs = null;
        int k=0;
        for(Element element : elements){
            final GridCoverage coverage = (GridCoverage) element.getCoverage(null);
            final CoordinateReferenceSystem covcrs = coverage.getCoordinateReferenceSystem();
            final GridGeometry gg = coverage.getGridGeometry();
            final GridEnvelope ext = gg.getExtent();
            final MathTransform trs = gg.getGridToCRS();

            if(baseGridToCrs==null){
                for(int i=0;i<gridLower.length-1;i++){
                    gridLower[i] = ext.getLow(i);
                    gridUpper[i] = ext.getHigh(i);
                }

                if(trs.getSourceDimensions()==nbDim){
                    //extract the gridToCrs transform without the last column
                    if(trs instanceof LinearTransform){
                        final Matrix matrix = ((LinearTransform)trs).getMatrix();
                        //remove the last dimension
                        final GeneralMatrix base = new GeneralMatrix(nbDim+1);
                        base.setIdentity();
                        for(int y=0;y<nbDim;y++){
                            for(int x=0;x<nbDim;x++){
                                base.setElement(y, x, matrix.getElement(y, x));
                            }

                            if(y<nbDim-1){
                                //move the last column translation value
                                base.setElement(y, nbDim, matrix.getElement(y, nbDim));
                                base.setElement(y, nbDim-1, 0);
                            }
                        }
                        baseGridToCrs = FactoryFinder.getMathTransformFactory(null).createAffineTransform(base);
                    }else if (trs instanceof PassThroughTransform){
                        final PassThroughTransform passtransform = (PassThroughTransform) trs;
                        baseGridToCrs = passtransform.getSubTransform();
                        //check the modifed coordinate is the last one
                        final int[] modifiedCoord = passtransform.getModifiedCoordinates();
                        if(modifiedCoord.length!= 1 || modifiedCoord[0]!=nbDim-1){
                            throw new IOException("PassThrough transform is not the last column : " + Arrays.toString(modifiedCoord));
                        }
                    }else{
                        throw new IOException("Coverage GridToCRS can not be decomposed : " + trs.getClass());
                    }
                }else if(trs.getSourceDimensions()==nbDim-1){
                    baseGridToCrs = PassThroughTransform.create(0, trs, 1);
                }else{
                    throw new IOException("Coverage GridToCRS can not be used, was expecting "+nbDim+" or "+(nbDim-1)+" dimensions but found : " + trs.getSourceDimensions());
                }

            }

            if(trs.getSourceDimensions()==nbDim){
                //we expect the last dimension to be a slice, low == high
                if(ext.getLow(gridLower.length-1) != ext.getHigh(gridUpper.length-1)){
                    throw new IOException("Last dimension of the coverage is not a slice.");
                }

                //find the real value
                final double[] coord = new double[gridUpper.length];
                for(int i=0;i<gridUpper.length;i++) coord[i] = ext.getLow(i);
                trs.transform(coord,0,coord,0,1);

                lastAxisSteps[k] = coord[gridUpper.length-1];
            }else{
                lastAxisSteps[k] = element.getZRange().getMinDouble();
            }

            //increment number of slices
            gridUpper[gridUpper.length-1]++;

            k++;
        }

        // reduce by one, values are inclusives
        gridUpper[gridUpper.length-1]--;

        //build the grid geometry
        final MathTransform1D lastAxisTrs = LinearInterpolator1D.create(lastAxisSteps);
        final MathTransform mask = PassThroughTransform.create(nbDim-1, lastAxisTrs, 0);
        final MathTransform gridToCRS = MathTransforms.concatenate(baseGridToCrs, mask);
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
