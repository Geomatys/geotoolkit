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
import org.apache.sis.geometry.GeneralDirectPosition;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.referencing.CRS;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.io.CoverageStoreException;
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
import org.opengis.referencing.crs.CoordinateReferenceSystem;
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
     * Find the most appropriate mosaic in the pyramid with the given informations.
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
     * Serach in the given pyramid all of the mosaics which fit the given. 2 modes
     * are possible :
     * - Contains only : Suitable mosaics must be CONTAINED (or equal) into given filter.
     * - Intersection  : Suitable mosaics must INTERSECT given filter.
     *
     * @param toSearchIn The pyramid to get mosaics from.
     * @param filter The {@link Envelope} to use to  specify spatial position of wanted mosaics.
     * @param containOnly True if you want 'Contains only' mode, false if you want 'Intersection' mode.
     * @return A list containing all the mosaics which fit the given envelope. Never nulll, but can be empty.
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
            GridMosaic mosaic = null;
            int index = 0;
            for (GridMosaic m : pyramid.getMosaics()) {
                if (m.getScale() == scale) {
                    //this mosaic definition replaces the given one
                    upperleft = m.getUpperLeftCorner();
                    tileDim = m.getTileSize();
                    gridSize = m.getGridSize();
                    break;
                }
                index++;
            }

            if (mosaic == null) {
                //create a new mosaic
                mosaic = container.createMosaic(pyramid.getId(),gridSize, tileDim, upperleft, scale);
            }
        }

        return pyramid;
    }

    /**
     * Extract recursively the first GridCoverage2D from a GridCoverage object.
     * @param coverage a GridCoverage2D or GridCoverageStack
     * @return first GridCoverage2D. Can't be null.
     * @throws org.geotoolkit.coverage.io.CoverageStoreException if GridCoverage2D not found or a empty GridCoverageStack
     */
    public static GridCoverage2D firstSlice(final GridCoverage coverage) throws CoverageStoreException {

        if (coverage instanceof GridCoverage2D) {
            return (GridCoverage2D) coverage;
        } else if (coverage instanceof GridCoverageStack) {
            GridCoverageStack coverageStack = (GridCoverageStack) coverage;
            GridGeometry gridGeometry = coverageStack.getGridGeometry();
            GridEnvelope extent = gridGeometry.getExtent();
            MathTransform gridToCRS = gridGeometry.getGridToCRS();

            double[] coords = new double[extent.getDimension()];
            try {
                gridToCRS.transform(coords, 0, coords, 0, 1);
            } catch (TransformException e) {
                throw new CoverageStoreException(e.getMessage(), e);
            }

            List<Coverage> coverageList = coverageStack.coveragesAt(coords[coords.length - 1]);
            if (!coverageList.isEmpty()) {
                return firstSlice((GridCoverage)coverageList.get(0));
            } else {
                throw new CoverageStoreException("Empty coverage list");
            }
        }
        throw new CoverageStoreException("Unknown GridCoverage");
    }

}
