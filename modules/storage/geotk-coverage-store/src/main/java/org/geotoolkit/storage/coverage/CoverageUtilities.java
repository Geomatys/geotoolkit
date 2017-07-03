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
import java.awt.image.DataBuffer;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.util.*;
import javax.imageio.ImageReader;

import org.apache.sis.geometry.GeneralDirectPosition;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.ArgumentChecks;

import org.geotoolkit.coverage.GridCoverageStack;
import org.geotoolkit.coverage.finder.CoverageFinder;
import org.geotoolkit.coverage.finder.StrictlyCoverageFinder;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.image.io.XImageIO;
import org.geotoolkit.internal.referencing.CRSUtilities;
import org.geotoolkit.referencing.ReferencingUtilities;

import org.opengis.coverage.SampleDimensionType;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.cs.CartesianCS;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.cs.EllipsoidalCS;
import org.opengis.referencing.cs.SphericalCS;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 * Utility functions for coverage and mosaic.
 *
 * @author Johann Sorel  (Geomatys)
 * @author Rémi Marechal (Geomatys)
 * @author Quentin Boileau (Geomatys)
 * @module
 */
public final class CoverageUtilities {

    private CoverageUtilities(){}

    /**
     * Find the most appropriate pyramid in given pyramid set and given crs.
     * Returned Pyramid may not have the given crs.
     *
     * @param set : pyramid set to search in
     * @param crs searched crs
     * @return Pyramid, never null except if the pyramid set is empty
     * TODO : Is it really OK ? If we search a Lambert pyramid, and we've only got polar ones...
     * @deprecated use {@link org.geotoolkit.coverage.finder.StrictlyCoverageFinder#findPyramid(PyramidSet, org.opengis.referencing.crs.CoordinateReferenceSystem)}
     */
    public static Pyramid findPyramid(final PyramidSet set, final CoordinateReferenceSystem crs) throws FactoryException {
        CoverageFinder finder = new StrictlyCoverageFinder();
        return finder.findPyramid(set, crs);
    }

    /**
     * Find the most appropriate mosaic in the pyramid with the given information.
     *
     * @param pyramid
     * @param resolution
     * @param tolerance
     * @param env
     * @return GridMosaic
     * @deprecated use {@link org.geotoolkit.coverage.finder.StrictlyCoverageFinder#findMosaic(Pyramid, double, double, org.opengis.geometry.Envelope, Integer)}
     */
    public static GridMosaic findMosaic(final Pyramid pyramid, final double resolution,
            final double tolerance, final Envelope env, int maxTileNumber) throws FactoryException{
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
    public static List<GridMosaic> findMosaics(final Pyramid toSearchIn, Envelope filter, boolean containOnly) throws TransformException {
        final ArrayList<GridMosaic> result = new ArrayList<GridMosaic>();

        //Rebuild filter envelope from pyramid CRS
        final GeneralEnvelope tmpFilter = new GeneralEnvelope(
                ReferencingUtilities.transform(filter, toSearchIn.getCoordinateReferenceSystem()));

        for (GridMosaic source : toSearchIn.getMosaics()) {
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
        for(CoordinateReferenceSystem ccrrss : ReferencingUtilities.decompose(crs)) {
            final CoordinateSystem cs = ccrrss.getCoordinateSystem();
            if((cs instanceof CartesianCS)
            || (cs instanceof SphericalCS)
            || (cs instanceof EllipsoidalCS)) {
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
        for(CoordinateReferenceSystem ccrrss : ReferencingUtilities.decompose(crs)) {
            final CoordinateSystem cs = ccrrss.getCoordinateSystem();
            if((cs instanceof CartesianCS)
            || (cs instanceof SphericalCS)
            || (cs instanceof EllipsoidalCS)) return tempOrdinate;
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
    public static void copyPyramidReference(PyramidalCoverageResource sourceRef, PyramidalCoverageResource targetRef) throws DataStoreException, IOException {
        final Collection<Pyramid> pyramids = sourceRef.getPyramidSet().getPyramids();

        //create pyramids
        for (Pyramid sP : pyramids) {
            final Pyramid tP = targetRef.createPyramid(sP.getCoordinateReferenceSystem());

            //create mosaics
            for (GridMosaic sM : sP.getMosaics()) {
                final GridMosaic tM = targetRef.createMosaic(tP.getId(), sM.getGridSize(), sM.getTileSize(), sM.getUpperLeftCorner(), sM.getScale());

                final int height = sM.getGridSize().height;
                final int width = sM.getGridSize().width;

                //Write tiles
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        if (!sM.isMissing(x, y)) {
                            final TileReference sT = sM.getTile(x, y, null);

                            final RenderedImage sourceImg;
                            ImageReader reader = null;
                            try {
                                if (sT.getInput() instanceof RenderedImage) {
                                    sourceImg = (RenderedImage) sT.getInput();
                                } else {
                                    final int imgIdx = sT.getImageIndex();
                                    reader = sT.getImageReader();
                                    sourceImg = sT.getImageReader().read(imgIdx);
                                }
                            } finally {
                                if (reader != null) {
                                    XImageIO.dispose(reader);
                                }
                            }

                            targetRef.writeTile(tP.getId(), tM.getId(), x, y, sourceImg);
                        }
                    }
                }
            }
        }
    }

    public static int getDataType(SampleDimensionType sdt){
        if(SampleDimensionType.REAL_32BITS.equals(sdt)){
            return DataBuffer.TYPE_FLOAT;
        }else if(SampleDimensionType.REAL_64BITS.equals(sdt)){
            return DataBuffer.TYPE_DOUBLE;
        }else if(SampleDimensionType.SIGNED_8BITS.equals(sdt)){
            return DataBuffer.TYPE_BYTE;
        }else if(SampleDimensionType.SIGNED_16BITS.equals(sdt)){
            return DataBuffer.TYPE_SHORT;
        }else if(SampleDimensionType.SIGNED_32BITS.equals(sdt)){
            return DataBuffer.TYPE_INT;
        }else if(SampleDimensionType.UNSIGNED_1BIT.equals(sdt)){
            return DataBuffer.TYPE_BYTE;
        }else if(SampleDimensionType.UNSIGNED_2BITS.equals(sdt)){
            return DataBuffer.TYPE_BYTE;
        }else if(SampleDimensionType.UNSIGNED_4BITS.equals(sdt)){
            return DataBuffer.TYPE_BYTE;
        }else if(SampleDimensionType.UNSIGNED_8BITS.equals(sdt)){
            return DataBuffer.TYPE_BYTE;
        }else if(SampleDimensionType.UNSIGNED_16BITS.equals(sdt)){
            return DataBuffer.TYPE_USHORT;
        }else if(SampleDimensionType.UNSIGNED_32BITS.equals(sdt)){
            return DataBuffer.TYPE_INT;
        }else {
            throw new IllegalArgumentException("Unexprected data type : "+sdt);
        }
    }

    /**
     * Get or create a pyramid and it's mosaic for the given envelope and scales.
     *
     * @param container
     * @param envelope
     * @param tileSize
     * @param scales
     * @return
     * @throws DataStoreException
     */
    public static Pyramid getOrCreatePyramid(PyramidalCoverageResource container,
            Envelope envelope, Dimension tileSize, double[] scales) throws DataStoreException{

        //find if we already have a pyramid in the given CRS
        Pyramid pyramid = null;
        final CoordinateReferenceSystem crs = envelope.getCoordinateReferenceSystem();
        for (Pyramid candidate : container.getPyramidSet().getPyramids()) {
            if (org.geotoolkit.referencing.CRS.equalsApproximatively(crs, candidate.getCoordinateReferenceSystem())) {
                pyramid = candidate;
                break;
            }
        }

        if (pyramid == null) {
            //we didn't find a pyramid, create one
            pyramid = container.createPyramid(crs);
        }

        //-- those parameters can change if another mosaic already exist
        final DirectPosition newUpperleft = new GeneralDirectPosition(crs);
        //-- We found the second horizontale axis dimension.
        final int maxHorizOrdinate = CRSUtilities.firstHorizontalAxis(crs) + 1;
        for (int d = 0; d < crs.getCoordinateSystem().getDimension(); d++) {
            final double v = (d == maxHorizOrdinate) ? envelope.getMaximum(d) : envelope.getMinimum(d);
            newUpperleft.setOrdinate(d, v);
        }

        //generate each mosaic
        for (final double scale : scales) {
            final double gridWidth  = envelope.getSpan(0) / (scale*tileSize.width);
            final double gridHeight = envelope.getSpan(1) / (scale*tileSize.height);

            final int dataPixelWidth  = (int) (envelope.getSpan(0) / scale);//-- fully filled area
            final int dataPixelHeight = (int) (envelope.getSpan(1) / scale);

            Dimension tileDim = tileSize;
            Dimension gridSize = new Dimension( (int)(Math.ceil(gridWidth)), (int)(Math.ceil(gridHeight)));

            //check if we already have a mosaic at this scale
            boolean mosaicFound = false;
            int index = 0;
            for (GridMosaic m : pyramid.getMosaics()) {
                if (m.getScale() == scale) {
                    mosaicFound = true;
                    break;
                }
                index++;
            }

            if (!mosaicFound) {
                //create a new mosaic
                container.createMosaic(pyramid.getId(),gridSize, tileDim, new Dimension(dataPixelWidth, dataPixelHeight), newUpperleft, scale);
            }
        }

        return pyramid;
    }

    /**
     * Extract recursively the first GridCoverage2D from a GridCoverage object.
     *
     * @param coverage a GridCoverage2D or GridCoverageStack
     * @return first GridCoverage2D. Can't be null.
     * @throws CoverageStoreException if GridCoverage2D not found or a empty GridCoverageStack
     */
    public static GridCoverage2D firstSlice(GridCoverage coverage) throws CoverageStoreException {

        if (coverage instanceof GridCoverage2D) {
            return (GridCoverage2D) coverage;
        } else if (coverage instanceof GridCoverageStack) {
            GridCoverageStack coverageStack = (GridCoverageStack) coverage;
            if (coverageStack.getStackSize() > 0) {
                return firstSlice((GridCoverage) coverageStack.coverageAtIndex(0));
            } else {
                throw new CoverageStoreException("Empty coverage list");
            }
        }
        throw new CoverageStoreException("Unknown GridCoverage");
    }


    /**
     * Compute Pyramid envelope.
     *
     * @param pyramid shouldn't be null
     * @return pyramid Envelope or null if no mosaic found.
     */
    public static GeneralEnvelope getPyramidEnvelope(Pyramid pyramid) {
        ArgumentChecks.ensureNonNull("pyramid", pyramid);
        GeneralEnvelope pyramidEnv = null;
        for (GridMosaic mosaic : pyramid.getMosaics()) {
            if (pyramidEnv == null) {
                pyramidEnv = new GeneralEnvelope(mosaic.getEnvelope());
            } else {
                pyramidEnv.add(mosaic.getEnvelope());
            }
        }
        return pyramidEnv;
    }

}
