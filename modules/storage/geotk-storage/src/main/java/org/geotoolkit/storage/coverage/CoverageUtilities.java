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

import java.awt.Dimension;
import java.awt.Point;
import java.awt.image.DataBuffer;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.geometry.GeneralDirectPosition;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.operation.transform.TransformSeparator;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.Utilities;
import org.geotoolkit.coverage.SampleDimensionType;
import org.geotoolkit.internal.referencing.CRSUtilities;
import org.geotoolkit.referencing.ReferencingUtilities;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.storage.coverage.finder.CoverageFinder;
import org.geotoolkit.storage.coverage.finder.StrictlyCoverageFinder;
import org.geotoolkit.storage.multires.DefiningTileMatrix;
import org.geotoolkit.storage.multires.DefiningTileMatrixSet;
import org.geotoolkit.storage.multires.MultiResolutionResource;
import org.geotoolkit.storage.multires.TileMatrices;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.cs.CartesianCS;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.cs.EllipsoidalCS;
import org.opengis.referencing.cs.SphericalCS;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;
import org.geotoolkit.storage.multires.TileMatrixSet;
import org.geotoolkit.storage.multires.TileMatrix;


/**
 * Utility functions for coverage and mosaic.
 *
 * @author Johann Sorel  (Geomatys)
 * @author Rémi Marechal (Geomatys)
 * @author Quentin Boileau (Geomatys)
 * @module
 */
public final class CoverageUtilities {

    private CoverageUtilities() {}

    /**
     * Find the most appropriate pyramid in given pyramid set and given crs.
     * Returned Pyramid may not have the given crs.
     *
     * @param set pyramid set to search in
     * @param crs searched crs
     * @return Pyramid, never null except if the pyramid set is empty
     * TODO: Is it really OK ? If we search a Lambert pyramid, and we've only got polar ones...
     * @deprecated use {@link org.geotoolkit.coverage.finder.StrictlyCoverageFinder#findPyramid(PyramidSet, org.opengis.referencing.crs.CoordinateReferenceSystem)}
     */
    public static TileMatrixSet findPyramid(final MultiResolutionResource set, final CoordinateReferenceSystem crs) throws FactoryException, DataStoreException {
        CoverageFinder finder = new StrictlyCoverageFinder();
        return finder.findPyramid(set, crs);
    }

    /**
     * Find the most appropriate mosaic in the pyramid with the given information.
     *
     * @deprecated use {@link org.geotoolkit.coverage.finder.StrictlyCoverageFinder#findMosaic(Pyramid, double, double, org.opengis.geometry.Envelope, Integer)}
     */
    public static TileMatrix findMosaic(final TileMatrixSet pyramid, final double resolution,
            final double tolerance, final Envelope env, int maxTileNumber) throws FactoryException
    {
        CoverageFinder finder = new StrictlyCoverageFinder();
        return finder.findMosaic(pyramid, resolution, tolerance, env, maxTileNumber);
    }

    /**
     * Search in the given pyramid all of the mosaics which fit the given parameters. 2 modes
     * are possible :
     * - Contains only : Suitable mosaics must be CONTAINED (or equal) to given spatial filter.
     * - Intersection  : Suitable mosaics must INTERSECT given filter.
     *
     * TODO port this method in CoverageFinder
     *
     * @param toSearchIn The pyramid to get mosaics from.
     * @param filter The {@link Envelope} to use to  specify spatial position of wanted mosaics.
     * @param containOnly True if you want 'Contains only' mode, false if you want 'Intersection' mode.
     * @return A list containing all the mosaics which fit the given envelope. Never null, but can be empty.
     * @throws TransformException If input filter {@link CoordinateReferenceSystem} is not compatible with
     * input mosaics one.
     */
    public static List<TileMatrix> findMosaics(final TileMatrixSet toSearchIn, Envelope filter, boolean containOnly) throws TransformException {
        final ArrayList<TileMatrix> result = new ArrayList<TileMatrix>();

        // Rebuild filter envelope from pyramid CRS
        final GeneralEnvelope tmpFilter = new GeneralEnvelope(
                ReferencingUtilities.transform(filter, toSearchIn.getCoordinateReferenceSystem()));

        for (TileMatrix source : toSearchIn.getTileMatrices()) {
            final Envelope sourceEnv = source.getEnvelope();
            if ((containOnly && tmpFilter.contains(sourceEnv, true))
                    || (!containOnly && tmpFilter.intersects(sourceEnv, true))) {
                result.add(source);
            }
        }
        return result;
    }

    /**
     * Compute ratio on each ordinate, not within 2D part of {@link CoordinateReferenceSystem},
     * which represent recovery from each ordinate of searchEnvelope on gridEnvelope.
     *
     * @param searchEnvelope user coverage area search.
     * @param gridEnvelope mosaic envelope.
     * @return computed ratio.
     */
    public static double getRatioND(Envelope searchEnvelope, Envelope gridEnvelope) {
        ArgumentChecks.ensureNonNull("gridEnvelope", gridEnvelope);
        ArgumentChecks.ensureNonNull("findEnvelope", searchEnvelope);
        final CoordinateReferenceSystem crs = gridEnvelope.getCoordinateReferenceSystem();
        //find index ordinate of crs2D part of this crs.
        int minOrdinate2D = 0;
        boolean find = false;
        for (CoordinateReferenceSystem ccrrss : CRS.getSingleComponents(crs)) {
            final CoordinateSystem cs = ccrrss.getCoordinateSystem();
            if ((cs instanceof CartesianCS) || (cs instanceof SphericalCS) || (cs instanceof EllipsoidalCS)) {
                find = true;
                break;
            }
            minOrdinate2D += cs.getDimension();
        }
        final int maxOrdinate2D = minOrdinate2D + 1;
        // compute distance
        if (!find) throw new IllegalArgumentException("CRS 2D part, not find");
        final GeneralEnvelope intersection = new GeneralEnvelope(searchEnvelope);
        intersection.intersect(gridEnvelope);
        double sumRatio = 0;
        final int dimension = crs.getCoordinateSystem().getDimension();
        for (int d = 0; d < dimension; d++) {
            if (d != minOrdinate2D && d != maxOrdinate2D) {
                final double ges = gridEnvelope.getSpan(d);
                // if intersect a slice part of gridEnvelope.
                // avoid divide by zero
                if (Math.abs(ges) <= 1E-12) continue;
                sumRatio += intersection.getSpan(d) / ges;
            }
        }
        return sumRatio;
    }

    /**
     * Retrieve index of the first axis of the geographic component in the input {@link CoordinateReferenceSystem}.
     *
     * @param crs {@link CoordinateReferenceSystem} which is analysed.
     * @return Index of the first horizontal axis in this CRS
     * @throws java.lang.IllegalArgumentException if input CRS has no horizontal component.
     *
     * @deprecated moved to {@link org.geotoolkit.internal.referencing.CRSUtilities#firstHorizontalAxis(org.opengis.referencing.crs.CoordinateReferenceSystem)}
     */
    public static int getMinOrdinate(final CoordinateReferenceSystem crs) {
        int tempOrdinate = 0;
        for (CoordinateReferenceSystem ccrrss : CRS.getSingleComponents(crs)) {
            final CoordinateSystem cs = ccrrss.getCoordinateSystem();
            if((cs instanceof CartesianCS) || (cs instanceof SphericalCS) || (cs instanceof EllipsoidalCS)) {
                return tempOrdinate;
            }
            tempOrdinate += cs.getDimension();
        }
        throw new IllegalArgumentException("crs doesn't have any horizontal crs");
    }

    /**
     * Copy a set of pyramid pointed by source coverage reference into destination
     * reference.
     * @param sourceRef The {@link PyramidalCoverageResource} to copy data from.
     * @param targetRef The {@link PyramidalCoverageResource} to copy data to.
     * @throws DataStoreException If a problem occurs at pyramid access.
     * @throws IOException If a problem occurs at image reading/writing.
     */
    public static void copyPyramidReference(MultiResolutionResource sourceRef, MultiResolutionResource targetRef) throws DataStoreException, IOException {
        final Collection<? extends TileMatrixSet> pyramids = TileMatrices.getTileMatrixSets(sourceRef);

        // Create pyramids
        for (TileMatrixSet sP : pyramids) {
            final TileMatrixSet tP = (TileMatrixSet) targetRef.createModel(new DefiningTileMatrixSet(sP.getCoordinateReferenceSystem()));

            //create mosaics
            for (TileMatrix sM : sP.getTileMatrices()) {
                final TileMatrix tM = tP.createTileMatrix(sM);
                final int height = sM.getGridSize().height;
                final int width = sM.getGridSize().width;

                // Write tiles
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        if (!sM.isMissing(x, y)) {
                            final ImageTile sT = (ImageTile) sM.getTile(x, y);
                            final RenderedImage sourceImg = sT.getImage();
                            tM.writeTiles(Stream.of(new DefaultImageTile(sourceImg, new Point(x, y))), null);
                        }
                    }
                }
            }
        }
    }

    public static int getDataType(final GridCoverage coverage) {
        GridGeometry grid = coverage.getGridGeometry();
        GridGeometry query = grid.derive().sliceByRatio(0, 0, 1).build();
        RenderedImage image = coverage.render(query.getExtent());
        return image.getSampleModel().getDataType();
    }

    @Deprecated
    public static int getDataType(SampleDimensionType sdt) {
        if (SampleDimensionType.REAL_32BITS.equals(sdt)) {
            return DataBuffer.TYPE_FLOAT;
        } else if (SampleDimensionType.REAL_64BITS.equals(sdt)) {
            return DataBuffer.TYPE_DOUBLE;
        } else if (SampleDimensionType.SIGNED_8BITS.equals(sdt)) {
            return DataBuffer.TYPE_BYTE;
        } else if (SampleDimensionType.SIGNED_16BITS.equals(sdt)) {
            return DataBuffer.TYPE_SHORT;
        } else if (SampleDimensionType.SIGNED_32BITS.equals(sdt)) {
            return DataBuffer.TYPE_INT;
        } else if (SampleDimensionType.UNSIGNED_1BIT.equals(sdt)) {
            return DataBuffer.TYPE_BYTE;
        } else if (SampleDimensionType.UNSIGNED_2BITS.equals(sdt)) {
            return DataBuffer.TYPE_BYTE;
        } else if (SampleDimensionType.UNSIGNED_4BITS.equals(sdt)) {
            return DataBuffer.TYPE_BYTE;
        } else if (SampleDimensionType.UNSIGNED_8BITS.equals(sdt)) {
            return DataBuffer.TYPE_BYTE;
        } else if (SampleDimensionType.UNSIGNED_16BITS.equals(sdt)) {
            return DataBuffer.TYPE_USHORT;
        } else if (SampleDimensionType.UNSIGNED_32BITS.equals(sdt)) {
            return DataBuffer.TYPE_INT;
        }else {
            throw new IllegalArgumentException("Unexprected data type : "+sdt);
        }
    }

    /**
     * Get or create a pyramid and it's mosaic for the given envelope and scales.
     */
    public static TileMatrixSet getOrCreatePyramid(MultiResolutionResource container,
            Envelope envelope, Dimension tileSize, double[] scales) throws DataStoreException
    {
        // Find if we already have a pyramid in the given CRS
        TileMatrixSet pyramid = null;
        final CoordinateReferenceSystem crs = envelope.getCoordinateReferenceSystem();
        for (TileMatrixSet candidate : TileMatrices.getTileMatrixSets(container)) {
            if (Utilities.equalsApproximately(crs, candidate.getCoordinateReferenceSystem())) {
                pyramid = candidate;
                break;
            }
        }
        if (pyramid == null) {
            // We didn't find a pyramid, create one
            pyramid = (TileMatrixSet) container.createModel(new DefiningTileMatrixSet(crs));
        }

        // Those parameters can change if another mosaic already exist
        final DirectPosition newUpperleft = new GeneralDirectPosition(crs);
        // We found the second horizontale axis dimension.
        final int maxHorizOrdinate = CRSUtilities.firstHorizontalAxis(crs) + 1;
        for (int d = 0; d < crs.getCoordinateSystem().getDimension(); d++) {
            final double v = (d == maxHorizOrdinate) ? envelope.getMaximum(d) : envelope.getMinimum(d);
            newUpperleft.setOrdinate(d, v);
        }

        // Generate each mosaic
        for (final double scale : scales) {
            final double gridWidth  = envelope.getSpan(0) / (scale*tileSize.width);
            final double gridHeight = envelope.getSpan(1) / (scale*tileSize.height);

            final int dataPixelWidth  = (int) (envelope.getSpan(0) / scale);        // Fully filled area
            final int dataPixelHeight = (int) (envelope.getSpan(1) / scale);

            Dimension tileDim = tileSize;
            Dimension gridSize = new Dimension( (int)(Math.ceil(gridWidth)), (int)(Math.ceil(gridHeight)));

            // Check if we already have a mosaic at this scale
            boolean mosaicFound = false;
            int index = 0;
            for (TileMatrix m : pyramid.getTileMatrices()) {
                if (m.getScale() == scale) {
                    mosaicFound = true;
                    break;
                }
            }
            if (!mosaicFound) {
                // Create a new mosaic
                final DefiningTileMatrix dm = new DefiningTileMatrix(
                        null,
                        newUpperleft,
                        scale,
                        tileSize,
                        gridSize,
                        new GridExtent(dataPixelWidth, dataPixelHeight));
                pyramid.createTileMatrix(dm);
            }
        }
        return pyramid;
    }

    /**
     * Compute Pyramid envelope.
     *
     * @param pyramid shouldn't be null
     * @return pyramid Envelope or null if no mosaic found.
     */
    public static GeneralEnvelope getPyramidEnvelope(org.geotoolkit.storage.multires.TileMatrixSet pyramid) {
        ArgumentChecks.ensureNonNull("pyramid", pyramid);
        GeneralEnvelope pyramidEnv = null;
        for (TileMatrix mosaic : pyramid.getTileMatrices()) {
            if (pyramidEnv == null) {
                pyramidEnv = new GeneralEnvelope(mosaic.getEnvelope());
            } else {
                pyramidEnv.add(mosaic.getEnvelope());
            }
        }
        return pyramidEnv;
    }

    /**
     * Returns the math transform for two dimensions of the specified transform. This methods
     * search for all grid dimensions in the given grid envelope having a length greater than
     * 1 pixel. The corresponding CRS dimensions are inferred from the transform itself.
     *
     * @param  gridToCRS The transform, or {@code null} if none.
     * @param  extent The extent of grid coordinates in a grid coverage, or {@code null} if unknown.
     * @param  dimensions An array of length 4 initialized to 0. This is the array where to store
     *         {@link #gridDimensionX}, {@link #gridDimensionY}, {@link #axisDimensionX} and
     *         {@link #axisDimensionY} values. This argument is actually a workaround for a
     *         Java language limitation (no multiple return values). If we could, we would
     *         have returned directly the arrays computed in the body of this method.
     * @return The {@link MathTransform2D} part of {@code transform}, or {@code null}
     *         if and only if {@code gridToCRS} was null..
     * @throws IllegalArgumentException if the 2D part is not separable.
     */
    public static MathTransform2D getMathTransform2D(final MathTransform gridToCRS,
            final GridExtent extent, final int[] dimensions) throws IllegalArgumentException
    {
        if (gridToCRS != null) {
            if (extent != null) {
                if (extent.getDimension() != gridToCRS.getSourceDimensions()) {
                    throw new MismatchedDimensionException(Errors.format(
                            Errors.Keys.MismatchedDimension_3, "extent", extent.getDimension(), gridToCRS.getSourceDimensions()));
                }
            }
        }
        if (gridToCRS == null || gridToCRS instanceof MathTransform2D) {
            dimensions[1] = dimensions[3] = 1; // Identity: (0,1) --> (0,1)
            return (MathTransform2D) gridToCRS;
        }
        /*
         * Finds the axis for the two dimensional parts. We infer them from the grid envelope.
         * If no grid envelope were specified, then we assume that they are the 2 first dimensions.
         */
        final TransformSeparator filter = new TransformSeparator(gridToCRS);
        long dimAdded = 0;
        if (extent != null) {
            final int dimension = extent.getDimension();
            for (int i=0; i<dimension; i++) {
                if (extent.getSize(i) > 1) {
                    if (i >= Long.SIZE) {
                        throw new ArithmeticException();
                    }
                    dimAdded |= (1L << i);
                }
            }
        }
        if (dimAdded < 3) {
            dimAdded = 3;           // If we have only one of dimension 0 and 1, or non of them, add both of them.
        }
        while (dimAdded != 0) {
            final int i = Long.numberOfTrailingZeros(dimAdded);
            filter.addSourceDimensions(i);
            dimAdded &= ~(1L << i);
        }
        Exception cause = null;
        int[] srcDim = filter.getSourceDimensions();
        /*
         * Select a math transform that operate only on the two dimensions chosen above.
         * If such a math transform doesn't have exactly 2 output dimensions, then select
         * the same output dimensions than the input ones.
         */
        MathTransform candidate;
        if (srcDim.length == 2) {
            dimensions[0] = srcDim[0]; // gridDimensionX
            dimensions[1] = srcDim[1]; // gridDimensionY
            try {
                candidate = filter.separate();
                if (candidate.getTargetDimensions() != 2) {
                    filter.clear();
                    filter.addSourceDimensions(srcDim);
                    filter.addTargetDimensions(srcDim);
                    candidate = filter.separate();
                }
                srcDim = filter.getTargetDimensions();
                dimensions[2] = srcDim[0]; // axisDimensionX
                dimensions[3] = srcDim[1]; // axisDimensionY
                try {
                    return (MathTransform2D) candidate;
                } catch (ClassCastException exception) {
                    cause = exception;
                }
            } catch (FactoryException exception) {
                cause = exception;
            }
        }
        throw new IllegalArgumentException(Errors.format(Errors.Keys.NoTransform2dAvailable), cause);
    }
}
