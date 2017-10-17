/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2017, Geomatys
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CancellationException;
import org.apache.sis.geometry.Envelopes;
import org.apache.sis.geometry.GeneralDirectPosition;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.operation.matrix.Matrices;
import org.apache.sis.referencing.operation.matrix.MatrixSIS;
import org.apache.sis.referencing.operation.transform.MathTransforms;
import org.geotoolkit.coverage.GridCoverageStack;
import org.geotoolkit.coverage.grid.GeneralGridEnvelope;
import org.geotoolkit.coverage.grid.GeneralGridGeometry;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.DisjointCoverageDomainException;
import org.geotoolkit.coverage.io.GridCoverageReadParam;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.math.XMath;
import org.geotoolkit.referencing.ReferencingUtilities;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.coverage.grid.GridEnvelope;
import org.opengis.geometry.Envelope;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.Matrix;
import org.opengis.referencing.operation.NoninvertibleTransformException;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;
import org.opengis.util.GenericName;

/**
 * Simplified GridCoverageReader which ensures the given GridCoverageReadParam
 * is not null and in the coverage CoordinateReferenceSystem.
 *
 * @author Johann Sorel (Geomatys)
 */
public abstract class GeoReferencedGridCoverageReader extends GridCoverageReader {

    protected final CoverageResource ref;

    protected GeoReferencedGridCoverageReader(CoverageResource ref){
        this.ref = ref;
    }

    @Override
    public List<? extends GenericName> getCoverageNames() throws CoverageStoreException, CancellationException {
        return Collections.singletonList(ref.getName());
    }

    /**
     * {@inheritDoc }
     *
     * Checks parameters envelope, CRS and resolution and create or fix them to match
     * this coverage CRS.
     */
    @Override
    public final GridCoverage read(int index, GridCoverageReadParam param) throws CoverageStoreException, CancellationException {
        if (index!=ref.getImageIndex()) throw new CoverageStoreException("Unvalid image index "+index);

        final GeneralGridGeometry gridGeometry = getGridGeometry(index);
        final CoordinateReferenceSystem coverageCrs = gridGeometry.getCoordinateReferenceSystem();

        try {
            //find requested envelope
            Envelope queryEnv = param == null ? null : param.getEnvelope();
            if(queryEnv == null && param != null && param.getCoordinateReferenceSystem()!= null){
                queryEnv = Envelopes.transform(gridGeometry.getEnvelope(), param.getCoordinateReferenceSystem());
            }

            //convert resolution to coverage crs
            final double[] queryRes = param == null ? null : param.getResolution();
            double[] coverageRes = queryRes;
            if (queryRes != null) {
                try {
                    //this operation works only for 2D CRS
                    coverageRes = ReferencingUtilities.convertResolution(queryEnv, queryRes, coverageCrs);
                } catch (TransformException | IllegalArgumentException ex) {
                    //more general case, less accurate
                    coverageRes = convertCentralResolution(queryRes, queryEnv, coverageCrs);
                }
            }

            //if no envelope is defined, use the full extent
            final Envelope coverageEnv;
            if (queryEnv==null) {
                coverageEnv = gridGeometry.getEnvelope();
            } else {
                final GeneralEnvelope genv = new GeneralEnvelope(Envelopes.transform(queryEnv, coverageCrs));
                //clip to coverage envelope
                genv.intersect(gridGeometry.getEnvelope());
                coverageEnv = genv;

                //check for disjoint envelopes
                int dimension = 0;
                for (int i=genv.getDimension(); --i>=0;) {
                    if (genv.getSpan(i) > 0) {
                        dimension++;
                    }
                }
                if (dimension < 2) {
                    throw new DisjointCoverageDomainException("No coverage matched parameters");
                }
            }


            final GridCoverageReadParam cparam = new GridCoverageReadParam();
            cparam.setCoordinateReferenceSystem(coverageEnv.getCoordinateReferenceSystem());
            cparam.setEnvelope(coverageEnv);
            cparam.setResolution(coverageRes);
            cparam.setDestinationBands((param == null) ? null : param.getDestinationBands());
            cparam.setSourceBands((param == null) ? null : param.getSourceBands());
            cparam.setDeferred((param == null) ? false : param.isDeferred());

            return readInNativeCRS(cparam);
        } catch (TransformException | FactoryException ex) {
            throw new CoverageStoreException(ex.getMessage(), ex);
        }
    }

    /**
     * Read coverage,
     *
     * If this method is not overloaded, the default implementation will fall back
     * on
     *
     *
     * @param param Parameters are guarantee to be in coverage CRS.
     */
    protected GridCoverage readInNativeCRS(GridCoverageReadParam param) throws CoverageStoreException, TransformException, CancellationException {

        final Envelope coverageEnv = param.getEnvelope();
        final double[] coverageRes = param.getResolution();

        final GeneralGridGeometry gridGeom = getGridGeometry(0);

        final GridEnvelope extent = gridGeom.getExtent();
        final MathTransform gridToCRS = gridGeom.getGridToCRS(PixelInCell.CELL_CORNER);
        final MathTransform crsToGrid;
        try {
            crsToGrid = gridToCRS.inverse();
        } catch (NoninvertibleTransformException ex) {
            throw new CoverageStoreException(ex.getMessage(), ex);
        }
        final int dim = extent.getDimension();

        // prepare image readInGridCRS param
        final int[] areaLower = new int[dim];
        final int[] areaUpper = new int[dim];
        final int[] subsampling = new int[dim];

        // convert envelope CS to image CS
        final GeneralEnvelope imgEnv;
        double[] imgRes = null;
        imgEnv = Envelopes.transform(crsToGrid,coverageEnv);
        if (coverageRes != null) {
            imgRes = new double[dim];
            final Matrix derivative;
            try {
                derivative = crsToGrid.derivative(new GeneralDirectPosition(dim));
            } catch (MismatchedDimensionException ex) {
                throw new CoverageStoreException(ex.getMessage(), ex);
            }
            for (int i = 0; i < dim; i++) {
                // Y scale is often negative, but we need positie values for image resolution.
                imgRes[i] = Math.abs(coverageRes[i] * derivative.getElement(i, i));
            }
        }else{
            imgRes = new double[dim];
            Arrays.fill(imgRes, 1.0);
        }

        // convert image resolution to subsampling
        for(int i=0;i<dim;i++){
            subsampling[i] = (int)Math.floor(imgRes[i]);
            if(subsampling[i]<1) subsampling[i] = 1;
        }

        // clamp region from data coverage raster boundary
        int min,max;
        for(int i=0;i<dim;i++){
            min = extent.getLow(i);
            max = extent.getHigh(i)+1;//+1 for upper exclusive
            areaLower[i] = XMath.clamp((int)Math.floor(imgEnv.getMinimum(i)), min, max);
            areaUpper[i] = XMath.clamp((int)Math.floor(imgEnv.getMaximum(i))+1,  min, max);
        }

        return readInGridCRS(areaLower,areaUpper,subsampling, param);
    }

    /**
     * Read a coverage with defined image area.
     *
     * @param areaLower readInGridCRS lower corner, inclusive
     * @param areaUpper readInGridCRS upper corner, exclusive
     * @param subsampling image subsampling in pixels
     * @param param grid coverage features parameters in native CRS
     * @throws CoverageStoreException if Coverage readInGridCRS failed
     * @throws CancellationException if reading operation has been canceled
     */
    protected GridCoverage readInGridCRS(int[] areaLower, int[] areaUpper, int[] subsampling, GridCoverageReadParam param) throws CoverageStoreException, TransformException, CancellationException {

        //ensure we readInGridCRS at least 3x3 pixels otherwise the gridgeometry won't be
        //able to identify the 2D composant of the grid to crs transform.
        for(int i=0;i<2;i++){
            int width = (areaUpper[i]-areaLower[i]+subsampling[i]-1) / subsampling[i];
            if(width<2){
                subsampling[i] = 1;
                if(areaLower[i]==0) areaUpper[i]=3;
                else {areaLower[i]--;areaUpper[i]++;}
            }
        }

        // find if we need to readInGridCRS more then one slice
        int cubeDim = -1;
        for(int i=0;i<subsampling.length;i++){
            final int width = (areaUpper[i]-areaLower[i]+subsampling[i]-1) / subsampling[i];
            if(i>1 && width>1){
                cubeDim = i;
                break;
            }
        }

        if(cubeDim == -1){
            //read a single slice
            return readGridSlice(areaLower, areaUpper, subsampling, param);
        }else{
            //read an Nd cube
            final List<GridCoverage> coverages = new ArrayList<>();
            final int lower = areaLower[cubeDim];
            final int upper = areaUpper[cubeDim];
            for(int i=lower;i<upper;i++){
                areaLower[cubeDim] = i;
                areaUpper[cubeDim] = i+1;
                coverages.add(readInGridCRS(areaLower, areaUpper, subsampling, param));
            }
            areaLower[cubeDim] = lower;
            areaUpper[cubeDim] = upper;

            try {
                return new GridCoverageStack(ref.getName().toString(), coverages, cubeDim);
            } catch (IOException | TransformException | FactoryException ex) {
                throw new CoverageStoreException(ex.getMessage(), ex);
            }
        }
    }

    /**
     * Read a coverage slice with defined image area.
     *
     * @param param grid coverage features parameters in native CRS
     */
    protected GridCoverage readGridSlice(int[] areaLower, int[] areaUpper, int[] subsampling, GridCoverageReadParam param) throws CoverageStoreException, TransformException, CancellationException {
        throw new UnsupportedOperationException("Subclass must implement either : read, readCoverage, readImage or readSlice methods");
    }

    /**
     * Convert resolution from one CRS to another at the center of given envelope.
     */
    private static double[] convertCentralResolution(final double[] resolution, final Envelope area,
            final CoordinateReferenceSystem targetCRS) throws FactoryException, TransformException {
        final CoordinateReferenceSystem areaCrs = area.getCoordinateReferenceSystem();
        if (areaCrs.equals(targetCRS)) {
            //nothing to do.
            return resolution;
        }

        final GeneralDirectPosition center = new GeneralDirectPosition(area.getDimension());
        for (int i=center.getDimension(); --i >= 0;) {
            center.setOrdinate(i, area.getMedian(i));
        }
        final Matrix derivative = CRS.findOperation(areaCrs, targetCRS, null).getMathTransform().derivative(center);
        final Matrix vector = Matrices.createZero(resolution.length, 1);
        for (int i=0; i<resolution.length; i++) {
            vector.setElement(i, 0, resolution[i]);
        }
        final Matrix result = Matrices.multiply(derivative, vector);
        return MatrixSIS.castOrCopy(result).getElements();
    }

    /**
     * Calculate the final size of each dimension.
     *
     * @param areaLower image features lower corner
     * @param areaUpper image features upper corner
     * @param subsampling image subsampling
     */
    public static int[] getResultExtent(int[] areaLower, int[] areaUpper, int[] subsampling) {

        //calculate output size
        final int[] outExtent = new int[areaLower.length];
        for(int i=0;i<outExtent.length;i++){
            outExtent[i] = (areaUpper[i]-areaLower[i]+subsampling[i]-1) / subsampling[i];
        }

        return outExtent;
    }

    /**
     * Derivate a grid geometry from the origina grid geometry and the features
     * image parameters.
     *
     * @param gridGeom original grid geometry
     * @param areaLower image features lower corner
     * @param areaUpper image features upper corner
     * @param subsampling image subsampling
     * @return derivated grid geometry.
     */
    public static GeneralGridGeometry getGridGeometry(GeneralGridGeometry gridGeom,
            int[] areaLower, int[] areaUpper, int[] subsampling) {

        //calculate output size
        final int[] outExtent = getResultExtent(areaLower, areaUpper, subsampling);

        //build grid geometry
        int dim = areaLower.length;
        final Matrix matrix = Matrices.createDiagonal(dim+1, dim+1);
        for(int i=0;i<dim;i++){
            matrix.setElement(i, i, subsampling[i]);
            matrix.setElement(i, dim, areaLower[i]);
        }
        final MathTransform ssToGrid = MathTransforms.linear(matrix);
        final MathTransform ssToCrs = MathTransforms.concatenate(ssToGrid, gridGeom.getGridToCRS());
        final GridEnvelope extent = new GeneralGridEnvelope(new int[outExtent.length], outExtent, false);
        return new GeneralGridGeometry(extent, ssToCrs, gridGeom.getCoordinateReferenceSystem());
    }

}
