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
package org.geotoolkit.coverage;

import java.awt.Dimension;
import java.awt.image.DataBuffer;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageReader;

import org.apache.sis.geometry.Envelope2D;
import org.apache.sis.geometry.GeneralDirectPosition;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.index.tree.QuadTree;
import org.apache.sis.measure.NumberRange;
import org.apache.sis.measure.Range;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.referencing.CRS;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.geometry.Envelopes;
import org.geotoolkit.referencing.OutOfDomainOfValidityException;
import org.geotoolkit.referencing.ReferencingUtilities;
import org.apache.sis.referencing.CommonCRS;
import org.geotoolkit.util.ImageIOUtilities;
import org.opengis.coverage.Coverage;
import org.opengis.coverage.SampleDimensionType;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.coverage.grid.GridEnvelope;
import org.opengis.coverage.grid.GridGeometry;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.metadata.identification.Resolution;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.cs.CartesianCS;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.cs.EllipsoidalCS;
import org.opengis.referencing.cs.SphericalCS;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 * Utility functions for coverage and mosaic.
 *
 * @author Johann Sorel  (Geomatys)
 * @author Rémi Marechal (Geomatys)
 * @author Quentin Boileau (Geomatys)
 * @module pending
 */
public final class CoverageUtilities {
    private static final double EPSILON = 1E-12;
    /**
     * Sort Grid Mosaic according to there scale, then on additional dimensions.
     */
    public static final Comparator<GridMosaic> SCALE_COMPARATOR = new Comparator<GridMosaic>() {
        @Override
        public int compare(final GridMosaic m1, final GridMosaic m2) {
            final double res = m1.getScale() - m2.getScale();
            if (res == 0) {
                //same scale check additional axes
                final DirectPosition m1ul = m1.getUpperLeftCorner();
                final DirectPosition m2ul = m2.getUpperLeftCorner();
                for(int i = 2, n = m1ul.getDimension(); i < n; i++){
                    final double ord1 = m1ul.getOrdinate(i);
                    final double ord2 = m2ul.getOrdinate(i);
                    final int c       = Double.valueOf(ord1).compareTo(ord2);
                    if (c != 0) return c;
                }
                return 0;
            }else if(res > 0){
                return 1;
            }else{
                return -1;
            }
        }
    };

    private CoverageUtilities(){}

    /**
     * Find the most appropriate pyramid in given pyramid set and given crs.
     * Returned Pyramid may not have the given crs.
     *
     * @param set : pyramid set to search in
     * @param crs searched crs
     * @return Pyramid, never null except if the pyramid set is empty TODO : Is it really OK ? If we search a Lambert pyramid, and we've only got polar ones...
     */
    public static Pyramid findPyramid(final PyramidSet set, final CoordinateReferenceSystem crs) throws FactoryException {
        final CoordinateReferenceSystem crs2D = CRS.getHorizontalComponent(crs);
        final Envelope crsBound1 = org.geotoolkit.referencing.CRS.getEnvelope(crs2D);
        double ratio = Double.NEGATIVE_INFINITY;
        // envelope with crs geographic.
        final GeneralEnvelope intersection = new GeneralEnvelope(CommonCRS.WGS84.normalizedGeographic());
        final List<Pyramid> results = new ArrayList<Pyramid>();
        if (crsBound1 != null) {
            final GeneralEnvelope crsBound = new GeneralEnvelope(crsBound1);
            noValidityDomainFound :
            for(Pyramid pyramid : set.getPyramids()) {
                double ratioTemp = 0;
                Envelope pyramidBound = org.geotoolkit.referencing.CRS.getEnvelope(
                        CRS.getHorizontalComponent(pyramid.getCoordinateReferenceSystem()));
                if (pyramidBound == null) {
                    results.add(pyramid);
                    continue noValidityDomainFound;
                }
                // compute sum of recovery ratio
                // from crs validity domain area on pyramid crs validity domain area
                try {
                    pyramidBound = org.geotoolkit.referencing.CRS.transform(pyramidBound, crs2D);
                } catch (TransformException ex) {
                    Logger.getLogger(CoverageUtilities.class.getName()).log(Level.WARNING, ex.getMessage(), ex);
                }
                if (!crsBound.intersects(pyramidBound, true)) continue;// no intersection
                intersection.setEnvelope(crsBound);
                intersection.intersect(pyramidBound);
                for (int d = 0; d < 2; d++) {// dim = 2 because extend geographic2D.
                    final double pbs = pyramidBound.getSpan(d);
                    // if intersect a slice part of gridEnvelope.
                    // avoid divide by zero
                    if (pbs <= 1E-12) continue;
                    ratioTemp += intersection.getSpan(d) / pbs;
                }
                 if (ratioTemp > ratio + EPSILON) {
                     ratio = ratioTemp;
                     results.clear();
                     results.add(pyramid);
                 } else if (Math.abs(ratio - ratioTemp) <= EPSILON) {
                     results.add(pyramid);
                 }
            }
        } else {
            results.addAll(set.getPyramids());
        }

        //paranoiac test
        if (results.isEmpty()){
            //could not find any proper candidates
            if(set.getPyramids().isEmpty()){
                return null;
            }else{
                return set.getPyramids().iterator().next();
            }
        }
        if (results.size() == 1) return results.get(0);
        // if several equal ratio.
        for (Pyramid pyramid : results) {
            final CoordinateReferenceSystem pyCrs = CRS.getHorizontalComponent(pyramid.getCoordinateReferenceSystem());
            if (org.geotoolkit.referencing.CRS.findMathTransform(pyCrs, crs2D).isIdentity()
             || org.geotoolkit.referencing.CRS.equalsIgnoreMetadata(crs2D, pyCrs)
             || org.geotoolkit.referencing.CRS.equalsApproximatively(crs2D, pyCrs)) {
                return pyramid;
            }
        }
        // return first in list. impossible to define the most appropriate crs.
        return results.get(0);
    }

    /**
     * Find the most appropriate mosaic in the pyramid with the given information.
     *
     * @param pyramid
     * @param resolution
     * @param tolerance
     * @param env
     * @return GridMosaic
     */
    public static GridMosaic findMosaic(final Pyramid pyramid, final double resolution,
            final double tolerance, final Envelope env, int maxTileNumber) throws FactoryException{

        final MathTransform mt = org.geotoolkit.referencing.CRS.findMathTransform(
                pyramid.getCoordinateReferenceSystem(), env.getCoordinateReferenceSystem());
        if (!mt.isIdentity()) throw new IllegalArgumentException("findMosaic : not same CoordinateReferenceSystem");
        final List<GridMosaic> mosaics = new ArrayList<GridMosaic>(pyramid.getMosaics());
        final List<GridMosaic> goodMosaics;
        final double epsilon = 1E-12;

        final GeneralEnvelope findEnvelope = new GeneralEnvelope(env);

        // if crs is compound
        if (env.getDimension() > 2) {
            double bestRatio = Double.NEGATIVE_INFINITY;
            goodMosaics = new ArrayList<GridMosaic>();
            // find nearest gridMosaic
            for (GridMosaic gridMosaic : mosaics) {
                final Envelope gridEnvelope = gridMosaic.getEnvelope();
                // if intersection solution exist
                if (findEnvelope.intersects(gridEnvelope, true)) {
                    final double ratioTemp = getRatioND(findEnvelope, gridEnvelope);
                    if (ratioTemp > (bestRatio + epsilon)) { // >
                        goodMosaics.clear();
                        goodMosaics.add(gridMosaic);
                        bestRatio = ratioTemp;
                    } else if ((Math.abs(ratioTemp - bestRatio)) <= epsilon) { // =
                        goodMosaics.add(gridMosaic);
                    }
                }
            }
        } else {
            goodMosaics = mosaics;
        }
        // if no coverage intersect search envelope.
        if (goodMosaics.isEmpty()) return null;

        if (goodMosaics.size() == 1) return goodMosaics.get(0);

        // find mosaic with the most scale value.
        Collections.sort(goodMosaics, SCALE_COMPARATOR);
        Collections.reverse(goodMosaics);

        GridMosaic result = null;

        for(GridMosaic candidate : goodMosaics){// recup meilleur scale
            final double scale = candidate.getScale();

            if(result == null){
                //set the highest mosaic as base
                result = candidate;
            }
            //check if it will not requiere too much tiles
            final Dimension tileSize = candidate.getTileSize();
            double nbtileX = env.getSpan(0) / (tileSize.width*scale);
            double nbtileY = env.getSpan(1) / (tileSize.height*scale);

            //if the envelope has some NaN, we presume it's a square
            if(Double.isNaN(nbtileX) || Double.isInfinite(nbtileX)){
                nbtileX = nbtileY;
            }else if(Double.isNaN(nbtileY) || Double.isInfinite(nbtileY)){
                nbtileY = nbtileX;
            }

            if(maxTileNumber > 0 && nbtileX*nbtileY > maxTileNumber){
                //we haven't reach the best resolution, it would requiere
                //too much tiles, we use the previous scale level
                break;
            }

            result = candidate;

            if( (scale * (1-tolerance)) < resolution){
                //we found the most accurate resolution
                break;
            }
        }
        return result;
    }

    /**
     * Search in the given pyramid all of the mosaics which fit the given parameters. 2 modes
     * are possible :
     * - Contains only : Suitable mosaics must be CONTAINED (or equal) to given spatial filter.
     * - Intersection  : Suitable mosaics must INTERSECT given filter.
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
     * <p>Compute ratio on each ordinate, not within 2D part of {@link CoordinateReferenceSystem},
     * which represent recovery from each ordinate of searchEnvelope on gridEnvelope.</p>
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
     * Return min geographic index ordinate from {@link CoordinateReferenceSystem} 2d part.
     *
     * @param crs {@link CoordinateReferenceSystem} which is study.
     * @return min geographic index ordinate from {@link CoordinateReferenceSystem} 2d part.
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
        throw new IllegalArgumentException("crs doesn't have any geoghaphic crs");
    }

    /**
     * Copy a set of pyramid pointed by source coverage reference into destination
     * reference.
     * @param sourceRef The {@link PyramidalCoverageReference} to copy data from.
     * @param targetRef The {@link PyramidalCoverageReference} to copy data to.
     * @throws DataStoreException If a problem occurs at pyramid access.
     * @throws IOException If a problem occurs at image reading/writing.
     */
    public static void copyPyramidReference(PyramidalCoverageReference sourceRef, PyramidalCoverageReference targetRef) throws DataStoreException, IOException {
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
                                    ImageIOUtilities.releaseReader(reader);
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
    public static Pyramid getOrCreatePyramid(PyramidalCoverageReference container,
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

        //generate each mosaic
        for (final double scale : scales) {
            final double gridWidth  = envelope.getSpan(0) / (scale*tileSize.width);
            final double gridHeight = envelope.getSpan(1) / (scale*tileSize.height);

            //those parameters can change if another mosaic already exist
            DirectPosition upperleft = new GeneralDirectPosition(crs);
            upperleft.setOrdinate(0, envelope.getMinimum(0));
            upperleft.setOrdinate(1, envelope.getMaximum(1));
            Dimension tileDim = tileSize;
            Dimension gridSize = new Dimension( (int)(Math.ceil(gridWidth)), (int)(Math.ceil(gridHeight)));

            //check if we already have a mosaic at this scale
            boolean mosaicFound = false;
            int index = 0;
            for (GridMosaic m : pyramid.getMosaics()) {
                if (m.getScale() == scale) {
                    //this mosaic definition replaces the given one
                    upperleft = m.getUpperLeftCorner();
                    tileDim = m.getTileSize();
                    gridSize = m.getGridSize();
                    mosaicFound = true;
                    break;
                }
                index++;
            }

            if (!mosaicFound) {
                //create a new mosaic
                container.createMosaic(pyramid.getId(),gridSize, tileDim, upperleft, scale);
            }
        }

        return pyramid;
    }

    /**
     * Extract recursively the first GridCoverage2D from a GridCoverage object.
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
     * Adapt input envelope to fit urn:ogc:def:wkss:OGC:1.0:GoogleCRS84Quad. Also give well known scales into the interval
     * given in parameter.
     *
     * As specified by WMTS draft v1.0.0 :
     * <p>
     *     [GoogleCRS84Quad] well-known scale set has been defined to allow quadtree pyramids in CRS84. Level
     * 0 allows representing the whole world in a single 256x256 pixels (where the first 64 and
     * last 64 lines of the tile are left blank). The next level represents the whole world in 2x2
     * tiles of 256x256 pixels and so on in powers of 2. Scale denominator is only accurate near
     * the equator.
     * </p>
     *
     * /!\ The well-known scales computed here have been designed for CRS:84 and Mercator projected CRS. Using it for
     * other coordinate reference systems can result in strange results.
     *
     * Note : only horizontal part of input envelope is analysed, so returned envelope will have same values as input one
     * for all additional dimension.
     *
     * @param envelope An envelope to adapt to well known scale quad-tree.
     * @param scaleLimit Minimum and maximum authorized scales. Edge inclusive. Unit must be input envelope horizontal
     *                    axis unit.
     * @return An entry with adapted envelope and its well known scales.
     */
    public static Map.Entry<Envelope, double[]> toWellKnownScale(final Envelope envelope, final NumberRange<Double> scaleLimit) throws TransformException, OutOfDomainOfValidityException {
        final CoordinateReferenceSystem targetCRS = CRS.getHorizontalComponent(envelope.getCoordinateReferenceSystem());
        if (targetCRS == null) {
            throw new IllegalArgumentException("Input envelope CRS has no defined horizontal component.");
        }

        /**
         * First, we retrieve total envelope of our Quad-tree. We try to use domain of validity of our input envelope
         * CRS. If we cannot, we'll take the world. After that, we'll perform consecutive divisions in order to find
         * minimal Quad-tree cell in which our envelope can be set. It will give us the result envelope. From this
         * envelope, we'll be able to build the final scale list.
         *
         * Note : final envelope can be the fusion of two neighbour Quad-Tree cells.
         */
        final Envelope tmpDomain = Envelopes.getDomainOfValidity(targetCRS);
        final GeneralEnvelope quadTreeCell;
        if (tmpDomain == null) {
            final GeographicCRS crs84 = CommonCRS.defaultGeographic();
            final Envelope tmpWorld = org.geotoolkit.referencing.CRS.getEnvelope(crs84);
            quadTreeCell = new GeneralEnvelope(Envelopes.transform(tmpWorld, targetCRS));
        } else {
            quadTreeCell = new GeneralEnvelope(tmpDomain);
        }

        // We check we can perform divisions on computed domain.
        double min, max;
        for (int i = 0; i < quadTreeCell.getDimension(); i++) {
            min = quadTreeCell.getMinimum(i);
            max = quadTreeCell.getMaximum(i);
            if (Double.isNaN(min) || Double.isInfinite(min) ||
                    Double.isNaN(max) || Double.isInfinite(max)) {
                throw new OutOfDomainOfValidityException("Invalid world bounds " + quadTreeCell);
            }
        }

        GeneralEnvelope targetEnv = new GeneralEnvelope(envelope);

        GeneralEnvelope cellX = quadTreeCell.subEnvelope(0, 1);
        GeneralEnvelope cellY = quadTreeCell.subEnvelope(1, 2);

        final int xAxis = getMinOrdinate(envelope.getCoordinateReferenceSystem());
        final int yAxis = xAxis + 1;
        final GeneralEnvelope tmpInput = new GeneralEnvelope(envelope);
        GeneralEnvelope inputRangeX = tmpInput.subEnvelope(xAxis, xAxis+1);
        GeneralEnvelope inputRangeY = tmpInput.subEnvelope(yAxis, yAxis+1);

        double midQuadX, midQuadY;
        boolean containX = cellX.contains(inputRangeX);
        boolean containY = cellY.contains(inputRangeY);
        while (containX || containY) {
            // Resize on longitude
            if (containX) {
                targetEnv.setRange(xAxis, cellX.getMinimum(0), cellX.getMaximum(0));
                midQuadX = cellX.getLower(0) + (cellX.getSpan(0) / 2);
                if (inputRangeX.getMinimum(0) < midQuadX) {
                    // west side
                    cellX.setRange(0, cellX.getMinimum(0), midQuadX);
                } else {
                    // east side
                    cellX.setRange(0, midQuadX, cellX.getMaximum(0));
                }

                // Update envelope test
                containX = cellX.contains(inputRangeX);
            }

            // Resize on latitude
            if (containY) {
                targetEnv.setRange(yAxis, cellY.getMinimum(0), cellY.getMaximum(0));
                midQuadY = cellY.getLower(0) + (cellY.getSpan(0) / 2);
                if (inputRangeY.getMinimum(0) < midQuadY) {
                    // south side
                    cellY.setRange(0, cellY.getMinimum(0), midQuadY);
                } else {
                    // north side
                    cellY.setRange(0, midQuadY, cellY.getMaximum(0));
                }

                // Update envelope test
                containY = cellY.contains(inputRangeY);
            }
        }

        final double lowestResolution = Math.max(scaleLimit.getMinDouble(), scaleLimit.getMaxDouble());
        final double highestResolution = Math.min(scaleLimit.getMinDouble(), scaleLimit.getMaxDouble());

        // Go to lowest authorized resolution boundary : A single 256px side tile for output envelope.
        double minScale = targetEnv.getSpan(xAxis) / 256;
        while (minScale > lowestResolution) {
            minScale /= 2;
        }

        // TODO : find a better way to compute array size ?
        int scaleCount = 0;
        double tmpScale = minScale;
        while (tmpScale > highestResolution) {
            tmpScale /= 2;
            scaleCount++;
        }

        // Save scales until finest authorized resolution.
        final double[] scales = new double[scaleCount];
        int i = 0;
        while (minScale > highestResolution) {
            scales[i++] = minScale;
            minScale /= 2;
        }

        return new AbstractMap.SimpleEntry<Envelope, double[]>(targetEnv, scales);
    }

}
