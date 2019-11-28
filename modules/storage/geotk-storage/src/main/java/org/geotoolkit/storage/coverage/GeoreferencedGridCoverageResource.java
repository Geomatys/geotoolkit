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
        final GridExtent extent;
        final int[] subsampling;
        try {
            if (domain != null) {
                GridDerivation derived = gg.derive()
                        .rounding(GridRoundingMode.ENCLOSING)
                        .subgrid(domain);
                extent = derived.getIntersection();
                subsampling = derived.getSubsamplings();
            } else {
                GridDerivation derived = gg.derive();
                extent = derived.getIntersection();
                subsampling = derived.getSubsamplings();
            }
        } catch (IllegalGridGeometryException ex) {
            throw new DisjointCoverageDomainException(ex.getMessage(), ex);
        }

        final int[] areaLower = new int[extent.getDimension()];
        final int[] areaUpper = new int[extent.getDimension()];
        for (int i = 0; i < areaLower.length; i++) {
            areaLower[i] = Math.toIntExact(extent.getLow(i));
            areaUpper[i] = Math.toIntExact(extent.getHigh(i)+1l);
        }

        try {
            return readInGridCRS(areaLower, areaUpper, subsampling, range);
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
    protected GridCoverage readInGridCRS(int[] areaLower, int[] areaUpper, int[] subsampling, int ... range)
            throws DataStoreException, TransformException {

        //ensure we readInGridCRS at least 3x3 pixels otherwise the gridgeometry won't be
        //able to identify the 2D composant of the grid to crs transform.
        for (int i=0; i<2; i++) {
            int width = (areaUpper[i] - areaLower[i] + subsampling[i] - 1) / subsampling[i];
            if (width < 2) {
                subsampling[i] = 1;
                if (areaLower[i] == 0) {
                    areaUpper[i] = 3;
                } else {
                    areaLower[i]--;
                    areaUpper[i]++;
                }
            }
        }

        // find if we need to readInGridCRS more then one slice
        int cubeDim = -1;
        for (int i=0; i<subsampling.length; i++) {
            final int width = (areaUpper[i] - areaLower[i] + subsampling[i] - 1) / subsampling[i];
            if (i>1 && width>1) {
                cubeDim = i;
                break;
            }
        }

        if (cubeDim == -1) {
            //read a single slice
            return readGridSlice(areaLower, areaUpper, subsampling);
        } else {
            //read an Nd cube
            final List<GridCoverage> coverages = new ArrayList<>();
            final int lower = areaLower[cubeDim];
            final int upper = areaUpper[cubeDim];
            for(int i=lower;i<upper;i++){
                areaLower[cubeDim] = i;
                areaUpper[cubeDim] = i+1;
                coverages.add(readInGridCRS(areaLower, areaUpper, subsampling));
            }
            areaLower[cubeDim] = lower;
            areaUpper[cubeDim] = upper;

            try {
                return new GridCoverageStack(getIdentifier().toString(), coverages, cubeDim);
            } catch (IOException | TransformException | FactoryException ex) {
                throw new DataStoreException(ex.getMessage(), ex);
            }
        }
    }

    /**
     * Read a coverage slice with defined image area.
     */
    protected abstract GridCoverage readGridSlice(int[] areaLower, int[] areaUpper, int[] subsampling, int ... range) throws DataStoreException;

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
        final MathTransform ssToCrs = MathTransforms.concatenate(ssToGrid, gridGeom.getGridToCRS(PixelInCell.CELL_CENTER));
        final GridExtent extent = new GridExtent(null, null, outExtent, false);
        return new GridGeometry(extent, PixelInCell.CELL_CENTER, ssToCrs, gridGeom.getCoordinateReferenceSystem());
    }
}
