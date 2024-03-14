/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2020, Geomatys
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
package org.geotoolkit.storage.multires;

import java.awt.Dimension;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Map.Entry;
import java.util.UUID;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.coverage.grid.GridOrientation;
import org.apache.sis.coverage.grid.IncompleteGridGeometryException;
import org.apache.sis.geometry.GeneralDirectPosition;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.referencing.operation.matrix.Matrices;
import org.apache.sis.referencing.operation.matrix.MatrixSIS;
import org.apache.sis.referencing.operation.transform.LinearTransform;
import org.apache.sis.referencing.operation.transform.MathTransforms;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.iso.Names;
import org.geotoolkit.coverage.grid.EstimatedGridGeometry;
import org.geotoolkit.coverage.grid.GridGeometryIterator;
import org.geotoolkit.internal.referencing.CRSUtilities;
import org.geotoolkit.util.NamesExt;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.metadata.spatial.DimensionNameType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.util.FactoryException;
import org.opengis.util.GenericName;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public final class TileMatrixSetBuilder {


    public static enum Iteration {
        /**
         * The first tile matrix will have one or two tiles.
         * Each next level will be computed using the scale factor.
         * The last tile matrix will have a resolution just lower then
         * the original grid geometry preserving the scale factor.
         */
        TOP_TO_BOTTOM_STRICT,
        /**
         * The first tile matrix will have one or two tiles.
         * Each next level will be computed using the scale factor.
         * The last tile matrix will have the exact resolution of the original grid geometry.
         * The last tile matrix is unlikely to preserve the regular scale factor.
         */
        TOP_TO_BOTTOM_LASTEXACT,
        /**
         * The first tile matrix will have one or two tiles.
         * Each next level will be computed using the scale factor.
         * The last tile matrix will have a resolution just higher then
         * the original grid geometry preserving the scale factor.
         */
        TOP_TO_BOTTOM_EXTRAPOLATE,
        /**
         * The first tile matrix will have the same resolution as the original grid geometry.
         * Each lower level will be computed using the inverse scale factor.
         * The last tile matrix will have one or two tiles.
         */
        BOTTOM_TO_TOP
    }

    private int[] tileSize = new int[]{256, 256};
    private GridGeometry gridGeometry = new GridGeometry(null, CRS.getDomainOfValidity(CommonCRS.WGS84.normalizedGeographic()), GridOrientation.HOMOTHETY);
    private boolean isSlice = true;
    private double scaleFactor = 2.0;
    private Iteration iteration = Iteration.TOP_TO_BOTTOM_EXTRAPOLATE;
    private int nbTileThreshold = 2;
    private double[] scales = null;

    /**
     * Set tile size.
     *
     * @param tileSize not null
     * @return this
     */
    public TileMatrixSetBuilder setTileSize(int[] tileSize) {
        ArgumentChecks.ensureNonNull("tileSize", tileSize);
        for (int i=0;i<tileSize.length;i++) {
            ArgumentChecks.ensurePositive("tile size", tileSize[i]);
        }
        this.tileSize = tileSize.clone();
        return this;
    }

    /**
     * Returns the tile size.
     * @return tile size, never null.
     */
    public int[] getTileSize() {
        return tileSize.clone();
    }

    /**
     * Set scale factor between each tile matrix.
     *
     * @param scaleFactor
     * @return
     */
    public TileMatrixSetBuilder setScaleFactor(double scaleFactor) {
        if (scaleFactor <= 1 || !Double.isFinite(scaleFactor)) {
            throw new IllegalArgumentException("Step ratio must be greater then 1 but was " + scaleFactor);
        }
        this.scaleFactor = scaleFactor;
        return this;
    }

    /**
     * Returns the scale factor between each tile matrix.
     * Default scale factor is 2.
     *
     * @return scale factor
     */
    public double getScaleFactor() {
        return scaleFactor;
    }

    /**
     * Define iteration of scales to create tile matrices.
     *
     * @param iteration
     * @return this
     */
    public TileMatrixSetBuilder setIteration(Iteration iteration) {
        ArgumentChecks.ensureNonNull("iteration", iteration);
        this.iteration = iteration;
        return this;
    }

    /**
     * Returns scale iteration method.
     *
     * @return iteration method, never null.
     */
    public Iteration getIteration() {
        return iteration;
    }

    /**
     * Define the number of number of tiles limit for top tile matrice.
     *
     * @param nbTileThreshold
     * @return this
     */
    public TileMatrixSetBuilder setNbTileThreshold(int nbTileThreshold) {
        ArgumentChecks.ensurePositive("nbTileThreshold", nbTileThreshold);
        this.nbTileThreshold = nbTileThreshold;
        return this;
    }

    /**
     * Set tile matrice area to create.
     *
     * @param gridGeom
     * @return this
     */
    public TileMatrixSetBuilder setDomain(GridGeometry gridGeom) {
        ArgumentChecks.ensureNonNull("Grid geometry", gridGeom);

        if (!gridGeom.isDefined(GridGeometry.EXTENT)) {
            if (gridGeom.isDefined(GridGeometry.RESOLUTION) || gridGeom instanceof EstimatedGridGeometry) {
                //create extent
                final Envelope envelope = gridGeom.getEnvelope();
                final double[] resolution = gridGeom.getResolution(true);
                final long[] low = new long[resolution.length];
                final long[] high = new long[resolution.length];

                for (int i = 0; i < resolution.length; i++) {
                    high[i] = (long) Math.ceil(envelope.getSpan(i) / resolution[i]);
                }

                final GridExtent extent = new GridExtent(null, low, high, true);
                gridGeom = new GridGeometry(extent, envelope, GridOrientation.HOMOTHETY);
            } else {
                throw new IllegalArgumentException("Grid geometry extent and resolution are undefined.");
            }
        }

        this.gridGeometry = gridGeom;
        this.isSlice = isSlice(gridGeometry.getExtent());
        return this;
    }

    /**
     * Transform the given grid geometry to provided crs and use it in this builder.
     *
     * @param gridGeom
     * @param crs target coordinate system
     * @return this
     */
    public TileMatrixSetBuilder setDomain(GridGeometry gridGeom, CoordinateReferenceSystem crs) throws FactoryException {
        if (crs == null) {
            return setDomain(gridGeom);
        } else {
            if (gridGeom.isDefined(GridGeometry.EXTENT)) {
                MathTransform gridToCRS = gridGeom.getGridToCRS(PixelInCell.CELL_CENTER);
                final MathTransform crsToCrs = CRS.findOperation(gridGeom.getCoordinateReferenceSystem(), crs, null).getMathTransform();
                gridToCRS = MathTransforms.concatenate(gridToCRS, crsToCrs);
                gridGeom = new GridGeometry(gridGeom.getExtent(), PixelInCell.CELL_CENTER, gridToCRS, crs);
                return setDomain(gridGeom);
            } else {
                if (gridGeom.isDefined(GridGeometry.RESOLUTION) || gridGeom instanceof EstimatedGridGeometry) {
                    //create extent
                    final Envelope envelope = gridGeom.getEnvelope();
                    final double[] resolution = gridGeom.getResolution(true);
                    final long[] low = new long[resolution.length];
                    final long[] high = new long[resolution.length];

                    for (int i = 0; i < resolution.length; i++) {
                        high[i] = (long) Math.ceil(envelope.getSpan(i) / resolution[i]);
                    }

                    final GridExtent extent = new GridExtent(null, low, high, true);
                    return setDomain(new GridGeometry(extent, envelope, GridOrientation.HOMOTHETY), crs);
                } else {
                    throw new IllegalArgumentException("Grid geometry extent and resolution are undefined.");
                }
            }
        }
    }

    /**
     * Set tile matrice area to create.
     *
     * @param envelope
     * @param resolution
     * @return this
     */
    public TileMatrixSetBuilder setDomain(Envelope envelope, double resolution) {

        final long[] high = new long[envelope.getDimension()];
        for (int i = 0; i < high.length; i++) {
            high[i] = (long) Math.ceil(envelope.getSpan(i) / resolution);
            if (high[i] == 0) {
                //slice, we need at least one value
                high[i] = 1;
            }
        }

        final GridExtent extent = new GridExtent(null, null, high, false);
        final GridGeometry gridGeom = new GridGeometry(extent, envelope, GridOrientation.HOMOTHETY);
        return setDomain(gridGeom);
    }

    /**
     * Set exact scales to use when building the tile matrices.
     * Iteration method and most accurate resolution of the grid geometry will be ignored.
     *
     * @param scales, can be null
     * @return this
     */
    public TileMatrixSetBuilder setScales(double[] scales) {
        this.scales = scales == null ? null : scales.clone();
        if (this.scales != null) {
            Arrays.sort(this.scales);
            for (int i = 0; i < this.scales.length / 2; i++) {
                double j = this.scales[i];
                this.scales[i] = this.scales[this.scales.length - i - 1];
                this.scales[this.scales.length - i - 1] = j;
            }
        }
        return this;
    }

    /**
     * User defined scales.
     *
     * @return scales or null.
     */
    public double[] getScales() {
        return scales;
    }

    /**
     * Create multi dimension template from grid geometry and parameters.
     * If scales are defined they will be used otherwise scales will be computed
     * using the interation method defined.
     *
     * @return created tile matrix set
     * @throws IllegalArgumentException if template could not be
     *         created because grid geometry or parameters doesn't have enough information.
     */
    public DefiningTileMatrixSet build() {
        ArgumentChecks.ensureNonNull("gridGeom", gridGeometry);

        final CoordinateReferenceSystem crs = gridGeometry.getCoordinateReferenceSystem();
        final DefiningTileMatrixSet pyramid = new DefiningTileMatrixSet(crs);

        //loop on all dimensions
        try {
            final GridGeometryIterator ite = new GridGeometryIterator(gridGeometry);
            while (ite.hasNext()) {
                final GridGeometry slice = ite.next();
                final Envelope envelope = slice.getEnvelope();

                final DirectPosition upperLeft = new GeneralDirectPosition(crs);
                //-- We found the second horizontale axis dimension.
                final int horizontalOrdinate = CRSUtilities.firstHorizontalAxis(crs);
                for (int d = 0; d < crs.getCoordinateSystem().getDimension(); d++) {
                    final double v = (d == horizontalOrdinate+1) ? envelope.getMaximum(d) : envelope.getMinimum(d);
                    upperLeft.setCoordinate(d, v);
                }

                final double spanX = envelope.getSpan(horizontalOrdinate);
                final double spanY = envelope.getSpan(horizontalOrdinate+1);

                if (scales != null) {
                    int idinc = 0;
                    for (double scale : scales) {
                        final String name = isSlice ? "" + idinc++ : UUID.randomUUID().toString();
                        pyramid.createTileMatrix(createTileMatrix(Names.createLocalName(null, null, name), upperLeft, scale, spanX, spanY));
                    }
                } else {

                    final double[] allRes;
                    try {
                        allRes = slice.getResolution(true);
                    } catch (IncompleteGridGeometryException ex) {
                        throw new IllegalArgumentException("TileMatrix resolution could not be computed");
                    }
                    if (Double.isNaN(allRes[horizontalOrdinate])) {
                        throw new IllegalArgumentException("Horizontal resolution is undefined on axis " + horizontalOrdinate);
                    }
                    if (Double.isNaN(allRes[horizontalOrdinate+1])) {
                        throw new IllegalArgumentException("Horizontal resolution is undefined on axis " + (horizontalOrdinate+1));
                    }
                    final double resolution = Double.min(allRes[horizontalOrdinate], allRes[horizontalOrdinate+1]);

                    switch (iteration) {
                        case BOTTOM_TO_TOP :
                            {
                                double res = resolution;
                                DefiningTileMatrix m = createTileMatrix(NamesExt.createRandomUUID(), upperLeft, res, spanX, spanY);
                                Dimension gridSize = TileMatrices.getGridSize(m);
                                pyramid.createTileMatrix(m);

                                //multiply resolution by given ratio until we reach one or two tiles.
                                while (gridSize.width * gridSize.height > nbTileThreshold) {
                                    res *= scaleFactor;
                                    m = createTileMatrix(NamesExt.createRandomUUID(), upperLeft, res, spanX, spanY);
                                    gridSize = TileMatrices.getGridSize(m);
                                    pyramid.createTileMatrix(m);
                                }
                            }
                            break;
                        case TOP_TO_BOTTOM_STRICT :
                            buildTopToBottom(pyramid, upperLeft, spanX, spanY, resolution);
                            break;
                        case TOP_TO_BOTTOM_LASTEXACT :
                            {
                                final Entry<Double, Integer> last = buildTopToBottom(pyramid, upperLeft, spanX, spanY, resolution);
                                if (last.getKey() > resolution) {
                                    //add a tile matrix with exact resolution
                                    final String name = isSlice ? "" + (last.getValue() + 1) : UUID.randomUUID().toString();
                                    pyramid.createTileMatrix(createTileMatrix(Names.createLocalName(null, null, name), upperLeft, resolution, spanX, spanY));
                                }
                            }
                            break;
                        case TOP_TO_BOTTOM_EXTRAPOLATE :
                            {
                                final Entry<Double, Integer> last = buildTopToBottom(pyramid, upperLeft, spanX, spanY, resolution);
                                if (last.getKey() != resolution) {
                                    //add a tile matrix with next resolution
                                    final String name = isSlice ? "" + (last.getValue() + 1) : UUID.randomUUID().toString();
                                    pyramid.createTileMatrix(createTileMatrix(Names.createLocalName(null, null, name), upperLeft, last.getKey() / scaleFactor, spanX, spanY));
                                }
                            }
                            break;
                        default:
                            throw new IllegalArgumentException("Unsupported iteration mode : " + iteration);
                    }
                }

            }
            return pyramid;
        } catch (IncompleteGridGeometryException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    private Entry<Double, Integer> buildTopToBottom(DefiningTileMatrixSet pyramid, DirectPosition upperLeft, double spanX, double spanY, double resolution) {
        final double resX = spanX / tileSize[0];
        final double resY = spanY / tileSize[1];
        double res = resX;
        if (resX < resY) {
            //compute number of tiles on Y
            double nbY = (int) Math.ceil((spanY / resX) / tileSize[1]);
            res = (nbY <= nbTileThreshold) ? resX : resY;
        } else if (resY < resX) {
            //compute number of tiles on X
            double nbX = (int) Math.ceil((spanX / resY) / tileSize[0]);
            res = (nbX <= nbTileThreshold) ? resY : resX;
        }

        int idinc = 0;
        String name = isSlice ? "" + idinc : UUID.randomUUID().toString();
        pyramid.createTileMatrix(createTileMatrix(Names.createLocalName(null, null, name), upperLeft, res, spanX, spanY));

        double lastRes = res;
        res /= scaleFactor;
        while (res >= resolution) {
            idinc++;
            lastRes = res;
            name = isSlice ? "" + idinc : UUID.randomUUID().toString();
            pyramid.createTileMatrix(createTileMatrix(Names.createLocalName(null, null, name), upperLeft, res, spanX, spanY));
            res /= scaleFactor;
        }

        return new AbstractMap.SimpleEntry<>(lastRes, idinc);
    }

    private DefiningTileMatrix createTileMatrix(GenericName id, DirectPosition upperLeft, double resolution, double spanX, double spanY) {
        final double nbX = (spanX / resolution) / tileSize[0];
        final double nbY = (spanY / resolution) / tileSize[1];
        final Dimension gridSize = new Dimension(
                (int) Math.ceil(nbX),
                (int) Math.ceil(nbY)
        );
        return new DefiningTileMatrix(id,
                TileMatrices.toTilingScheme(upperLeft, gridSize, resolution, tileSize),
                tileSize);
    }

    /**
     * Returns true if given grid geometry is a slice.
     */
    private static boolean isSlice(GridExtent extent) {
        int size = 0;
        for (int i = 0, n = extent.getDimension(); i < n  && size <= 2; i++) {
            if (extent.getSize(i) > 1) size++;
        }
        return size <= 2;
    }

    /**
     * Create a build working by subdivision of a top tilingScheme.
     *
     * @param env not null, transformed in a TilingScheme of a single tile.
     * @return builder not null
     */
    public static TopBuilder fromTop(Envelope env) {
        final GridExtent extent = new GridExtent(null, new long[env.getDimension()], new long[env.getDimension()], true);
        final GridGeometry tilingScheme = new GridGeometry(extent, env, GridOrientation.HOMOTHETY);
        return fromTop(tilingScheme);
    }

    /**
     * Create a build working by subdivision of a top tilingScheme.
     *
     * @param tilingScheme, top level tiling scheme.
     * @return builder not null
     */
    public static TopBuilder fromTop(GridGeometry tilingScheme) {
        return new TopBuilder(tilingScheme);
    }

    public static class TopBuilder {

        private final GridGeometry tilingScheme;
        private int lod = 1;
        private int[] axis;
        private int[] tileSize = new int[]{256, 256};

        private TopBuilder(GridGeometry tilingScheme) {
            this.tilingScheme = tilingScheme;
            axis = new int[tilingScheme.getCoordinateReferenceSystem().getCoordinateSystem().getDimension()];
            for (int i = 0; i < axis.length; i++) {
                axis[i] = i;
            }
        }

        /**
         * Set number of LOD to generate.
         * @param lod must be at least one.
         */
        public TopBuilder subdivide(int lod) {
            ArgumentChecks.ensureStrictlyPositive("lod", lod);
            this.lod = lod;
            return this;
        }

        /**s
         * @return number of LOD to generate.
         */
        public int getSubdivision() {
            return lod;
        }

        /**
         * Define axis to subdivide.
         * @param axis not null
         */
        public TopBuilder axis(int ... axis) {
            this.axis = axis.clone();
            return this;
        }

        /**
         * Get axis which are subdivided.
         */
        public int[] getAxis() {
            return axis.clone();
        }

        public DefiningTileMatrixSet build() {
            final CoordinateReferenceSystem crs = tilingScheme.getCoordinateReferenceSystem();
            final DefiningTileMatrixSet tms = new DefiningTileMatrixSet(crs);

            final GridExtent extent = tilingScheme.getExtent();
            final MathTransform gridToCRS = tilingScheme.getGridToCRS(PixelInCell.CELL_CORNER);
            final int dimension = extent.getDimension();

            final DimensionNameType[] axisNames = new DimensionNameType[dimension];
            for (int k = 0; k < dimension; k++) {
                axisNames[k] = extent.getAxisType(k).orElse(null);
            }

            for (int i = 0; i < lod; i++) {

                final int subdivision = 1 << i; //same as Math.pow(2, i);
                final MatrixSIS matrix = Matrices.createDiagonal(dimension+1, dimension+1);

                final long[] high = new long[dimension];
                for (int k = 0; k < dimension; k++) {
                    high[k] = (extent.getHigh(k) + 1);
                    if (Arrays.binarySearch(axis, k) >= 0) {
                        high[k] *= subdivision;
                        matrix.setElement(k, k, 1.0/subdivision);
                    }
                }
                final GridExtent subExtent = new GridExtent(axisNames, extent.getLow().getCoordinateValues(), high, false);

                final LinearTransform division = MathTransforms.linear(matrix);
                final MathTransform subGridToCrs = MathTransforms.concatenate(division, gridToCRS);

                final GridGeometry subTilingScheme = new GridGeometry(subExtent, PixelInCell.CELL_CORNER, subGridToCrs, crs);
                tms.createTileMatrix(new DefiningTileMatrix(Names.createLocalName(null, null, Integer.toString(i)), subTilingScheme, tileSize));
            }

            return tms;
        }

    }

}
