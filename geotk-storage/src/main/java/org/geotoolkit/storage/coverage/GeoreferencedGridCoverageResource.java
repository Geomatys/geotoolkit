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

import java.awt.image.RenderedImage;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridDerivation;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.coverage.grid.GridRoundingMode;
import org.apache.sis.coverage.grid.IllegalGridGeometryException;
import org.apache.sis.storage.AbstractGridCoverageResource;
import org.apache.sis.referencing.operation.matrix.Matrices;
import org.apache.sis.referencing.operation.transform.MathTransforms;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.Resource;
import org.apache.sis.storage.event.StoreListeners;
import org.geotoolkit.coverage.io.DisjointCoverageDomainException;
import org.opengis.coverage.CannotEvaluateException;
import org.apache.sis.coverage.grid.PixelInCell;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.Matrix;
import org.opengis.referencing.operation.TransformException;

/**
 * Simplified GridCoverageReader which ensures the given GridCoverageReadParam
 * is not null and in the coverage CoordinateReferenceSystem.
 *
 * @author Johann Sorel (Geomatys)
 */
public abstract class GeoreferencedGridCoverageResource extends AbstractGridCoverageResource {

    protected GeoreferencedGridCoverageResource(Resource resource) {
        super(resource instanceof StoreListeners ? (StoreListeners) resource : null, false);
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
     * @param extent Extent representing selected domain in source (full-resolution) grid. Never null.
     * @param subsampling image subsampling in pixels
     */
    protected GridCoverage readInGridCRS(GridGeometry resultGrid, GridExtent extent, int[] subsampling, int ... range)
            throws DataStoreException, TransformException {

//        int[] areaLower,
//                int[] areaUpper

        // find if we need to readInGridCRS more then one slice
        final long unsqueezableDimensions = IntStream.range(0, extent.getDimension())
                .mapToLong(idx -> extent.getSize(idx) / subsampling[idx])
                .filter(size -> size > 1)
                .count();

        if (unsqueezableDimensions < 3) {
            //read a single slice
            return readGridSlice(resultGrid, getLow(extent), getHigh(extent), subsampling, range);
        } else {
            return new GridCoverageSelection(resultGrid, getSampleDimensions(), subsampling, range);
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

    private final class GridCoverageSelection extends GridCoverage {

        final int[] subsampling;
        final int[] bands;

        private GridCoverageSelection(GridGeometry domain, List<? extends SampleDimension> ranges, int[] subsampling, int[] bands) {
            super(domain, (bands == null || bands.length < 1) ? ranges : IntStream.of(bands).mapToObj(ranges::get).collect(Collectors.toList()));
            this.subsampling = subsampling;
            this.bands = bands;
        }

        @Override
        public RenderedImage render(GridExtent sliceExtent) throws CannotEvaluateException {
            final GridGeometry targetGeometry = getGridGeometry().derive().subgrid(sliceExtent).build();
            try {
                final GridExtent fullDomainIntersection = GeoreferencedGridCoverageResource.this.getGridGeometry().derive()
                        .subgrid(targetGeometry)
                        .getIntersection();

                final GridCoverage coverage = readGridSlice(targetGeometry, getLow(fullDomainIntersection), getHigh(fullDomainIntersection), subsampling, bands);
                return coverage.render(null);
            } catch (DataStoreException e) {
                throw new CannotEvaluateException("Error while loading slice", e);
            }
        }
    }
}
