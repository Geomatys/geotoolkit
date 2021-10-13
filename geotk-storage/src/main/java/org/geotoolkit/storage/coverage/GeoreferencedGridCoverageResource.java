/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2019, Geomatys
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
import java.util.List;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridDerivation;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.coverage.grid.GridRoundingMode;
import org.apache.sis.coverage.grid.IllegalGridGeometryException;
import org.apache.sis.internal.storage.AbstractGridResource;
import org.apache.sis.referencing.operation.matrix.Matrices;
import org.apache.sis.referencing.operation.transform.MathTransforms;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.Resource;
import org.apache.sis.storage.event.StoreListeners;
import org.geotoolkit.coverage.grid.GridCoverageStack;
import org.geotoolkit.coverage.io.DisjointCoverageDomainException;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.Matrix;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 * Simplified GridCoverageReader which ensures the given GridCoverageReadParam
 * is not null and in the coverage CoordinateReferenceSystem.
 *
 * @author Johann Sorel (Geomatys)
 */
public abstract class GeoreferencedGridCoverageResource extends AbstractGridResource {

    protected GeoreferencedGridCoverageResource(Resource resource) {
        super(resource instanceof StoreListeners ? (StoreListeners) resource : null);
    }

    @Override
    public GridCoverage read(GridGeometry domain, int ... range) throws DataStoreException {
        final GridGeometry gg = getGridGeometry();
        final GridGeometry resultGrid;
        final GridExtent extent;
        final int[] subsampling;
        try {
            if (domain != null) {
                GridDerivation derived = gg.derive()
                        .rounding(GridRoundingMode.ENCLOSING)
                        .subgrid(domain);
                extent = derived.getIntersection();
                subsampling = derived.getSubsampling();
                resultGrid = derived.build();
            } else {
                GridDerivation derived = gg.derive();
                extent = derived.getIntersection();
                subsampling = derived.getSubsampling();
                resultGrid = derived.build();
            }
        } catch (IllegalGridGeometryException ex) {
            throw new DisjointCoverageDomainException(ex.getMessage(), ex);
        }

        try {
            return readInGridCRS(resultGrid, extent, subsampling, range);
        } catch (TransformException ex) {
            throw new DataStoreException(ex.getMessage(), ex);
        }
    }

    /**
     * Read a coverage with defined image area.
     *
     * @param areaLower readInGridCRS lower corner, inclusive
     * @param areaUpper readInGridCRS upper corner, exclusive
     * @param subsampling image subsampling in pixels
     */
    protected GridCoverage readInGridCRS(GridGeometry resultGrid, GridExtent extent, int[] subsampling, int ... range)
            throws DataStoreException, TransformException {

//        int[] areaLower,
//                int[] areaUpper

        // find if we need to readInGridCRS more then one slice
        int cubeDim = -1;
        for (int i=0; i<subsampling.length; i++) {
            final long width = (extent.getHigh(i)+1 - extent.getLow(i) + subsampling[i]-1 ) / subsampling[i];
            if (i>1 && width>1) {
                cubeDim = i;
                break;
            }
        }

        if (cubeDim == -1) {
            //read a single slice
            return readGridSlice(resultGrid, getLow(extent), getHigh(extent), subsampling);
        } else {
            //read an Nd cube
            final List<GridCoverage> coverages = new ArrayList<>();
            final long lower = extent.getLow(cubeDim);
            final long upper = extent.getHigh(cubeDim) +1;
            for(long i=lower;i<upper;i++){
                final long[] low = extent.getLow().getCoordinateValues();
                final long[] high = extent.getHigh().getCoordinateValues();
                low[cubeDim] = i;
                high[cubeDim] = i;
                final GridExtent subExtent = new GridExtent(null, low, high, true);

                final GridGeometry subGrid = new GridGeometry(subExtent, PixelInCell.CELL_CENTER,
                        resultGrid.getGridToCRS(PixelInCell.CELL_CENTER), resultGrid.getCoordinateReferenceSystem());

                coverages.add(readInGridCRS(subGrid, subExtent, subsampling));
            }

            try {
                return new GridCoverageStack(getIdentifier().toString(), coverages, cubeDim);
            } catch (IOException | TransformException | FactoryException ex) {
                throw new DataStoreException(ex.getMessage(), ex);
            }
        }
    }

    private static int[] getLow(GridExtent extent) {
        long[] values = extent.getLow().getCoordinateValues();
        final int[] array = new int[values.length];
        for(int i=0;i<values.length;i++) array[i] = (int) values[i];
        return array;
    }

    private static int[] getHigh(GridExtent extent) {
        long[] values = extent.getHigh().getCoordinateValues();
        final int[] array = new int[values.length];
        for(int i=0;i<values.length;i++) array[i] = (int) (values[i]+1);
        return array;
    }

    /**
     * Read a coverage slice with defined image area.
     */
    protected abstract GridCoverage readGridSlice(GridGeometry resultGrid, int[] areaLower, int[] areaUpper, int[] subsampling, int ... range) throws DataStoreException;

    /**
     * Calculate the final size of each dimension.
     *
     * @param areaLower image features lower corner
     * @param areaUpper image features upper corner
     * @param subsampling image subsampling
     */
    public static long[] getResultExtent(int[] areaLower, int[] areaUpper, int[] subsampling) {

        //calculate output size
        final long[] outExtent = new long[areaLower.length];
        for(int i=0;i<outExtent.length;i++){
            outExtent[i] = (areaUpper[i]-areaLower[i]+subsampling[i]-1) / subsampling[i];
        }

        return outExtent;
    }

    /**
     * Derivate a grid geometry from the original grid geometry and the features
     * image parameters.
     *
     * @param gridGeom original grid geometry
     * @param areaLower image features lower corner
     * @param areaUpper image features upper corner
     * @param subsampling image subsampling
     * @return derivated grid geometry.
     */
    public static GridGeometry getGridGeometry(GridGeometry gridGeom,
            int[] areaLower, int[] areaUpper, int[] subsampling) {

        //calculate output size
        final long[] outExtent = getResultExtent(areaLower, areaUpper, subsampling);

        //build grid geometry
        int dim = areaLower.length;
        final Matrix matrix = Matrices.createDiagonal(dim+1, dim+1);
        for(int i=0;i<dim;i++){
            matrix.setElement(i, i, subsampling[i]);
            matrix.setElement(i, dim, areaLower[i]);
        }
        final MathTransform ssToGrid = MathTransforms.linear(matrix);
        final MathTransform ssToCrs = MathTransforms.concatenate(ssToGrid, gridGeom.getGridToCRS(PixelInCell.CELL_CORNER));
        final GridExtent extent = new GridExtent(null, null, outExtent, false);
        return new GridGeometry(extent, PixelInCell.CELL_CORNER, ssToCrs, gridGeom.getCoordinateReferenceSystem());
    }
}
