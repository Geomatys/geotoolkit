/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.coverage.finder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;

import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.CommonCRS;
import org.geotoolkit.storage.coverage.GridMosaic;
import org.geotoolkit.storage.coverage.Pyramid;
import org.geotoolkit.storage.coverage.PyramidSet;
import org.geotoolkit.coverage.io.DisjointCoverageDomainException;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;
import org.apache.sis.geometry.Envelopes;
import org.apache.sis.util.Utilities;
import org.apache.sis.util.logging.Logging;

/**
 * Define {@link Pyramid} and {@link GridMosaic} search rules.
 *
 * @author Rémi Marechal (Geomatys).
 * @author Johann Sorel  (Geomatys).
 */
public abstract class CoverageFinder {

    /**
     * Default epsilon used to compare mosaics scales.
     */
    public static final double DEFAULT_EPSILON = 1E-12;

    protected CoverageFinder() {
    }

    /**
     * Find the most appropriate mosaic in the pyramid with the given information.
     * Result GridMosaic can be on a different scale that the requested one.
     *
     * @param pyramid
     * @param scale
     * @param tolerance
     * @param env
     * @param maxTileNumber optional max number of tile in mosaic found. If mosaic found have mode tiles
     *                      result will be null. Parameter can be null.
     * @return GridMosaic that match or null.
     */
    public abstract GridMosaic findMosaic(final Pyramid pyramid, final double scale,
            final double tolerance, final Envelope env, Integer maxTileNumber) throws FactoryException;

    /**
     * Find all mosaics in the pyramid which match given resolution and envelope.
     *
     * @param pyramid
     * @param resolution
     * @param tolerance
     * @param env
     * @return List of GridMosaic
     * @throws IllegalArgumentException if no mosaic found when requested with bad resolution.
     * @throws DisjointCoverageDomainException if no mosaic intersect requested envelope
     */
    public List<GridMosaic> findMosaics(final Pyramid pyramid, final double resolution,
            final double tolerance, final Envelope env) throws DisjointCoverageDomainException {
        final List<GridMosaic> mosaics = new ArrayList<>(pyramid.getMosaics());
        Collections.sort(mosaics, SCALE_COMPARATOR);
        Collections.reverse(mosaics);
        final List<GridMosaic> result = new ArrayList<>();

        //find the most accurate resolution
        final double[] scales = pyramid.getScales();
        if (scales.length == 0) return result;
        double bestScale = scales[0];
        for (double d : pyramid.getScales()) {
            if (d > resolution) {
                //scale is greater but closer to wanted resolution
                bestScale = d < bestScale ? d : bestScale;
            } else if ( d > bestScale ) {
                //found a better resolution
                bestScale = d;
            }
        }

        int notIntersected = 0;
        //-- search mosaics
        for (GridMosaic candidate : mosaics) {
            //-- check the mosaic intersect the searched envelope
            final GeneralEnvelope clip = new GeneralEnvelope(candidate.getEnvelope());
            if (!clip.intersects(env, true)) {
                notIntersected++;
                continue;
            }
            final double scale = candidate.getScale();
            if (scale != bestScale) continue;
            result.add(candidate);
        }

        if (result.isEmpty()) {
            //-- determine distinction between no intersection with mosaic boundaries
            if (notIntersected != 0)
                throw new DisjointCoverageDomainException("No mosaics intersects following requested envelope : "+env.toString());

            //-- and scale not found.
            throw new IllegalArgumentException("Impossible to find appropriate mosaic at following requested resolution : "+resolution
                                               +"\n The available(s) mosaic(s) resolution(s) is(are) : "+Arrays.toString(scales));
        }
        return result;
    }

    /**
     * Find the most appropriate pyramid in given pyramid set and given crs.
     * Returned Pyramid may not have the given crs.
     *
     * @param set : pyramid set to search in
     * @param crs searched crs
     * @return Pyramid, never null except if the pyramid set is empty
     */
    public final Pyramid findPyramid(final PyramidSet set, final CoordinateReferenceSystem crs) throws FactoryException {
        final CoordinateReferenceSystem crs2D = CRS.getHorizontalComponent(crs);
        final Envelope crsBound1 = CRS.getDomainOfValidity(crs2D);
        double ratio = Double.NEGATIVE_INFINITY;
        // envelope with crs geographic.
        final GeneralEnvelope intersection = new GeneralEnvelope(CommonCRS.WGS84.normalizedGeographic());
        final List<Pyramid> results = new ArrayList<>();
        if (crsBound1 != null) {
            final GeneralEnvelope crsBound = new GeneralEnvelope(crsBound1);
            noValidityDomainFound :
            for(Pyramid pyramid : set.getPyramids()) {
                double ratioTemp = 0;
                Envelope pyramidBound = CRS.getDomainOfValidity(
                        CRS.getHorizontalComponent(pyramid.getCoordinateReferenceSystem()));
                if (pyramidBound == null) {
                    results.add(pyramid);
                    continue noValidityDomainFound;
                }
                // compute sum of recovery ratio
                // from crs validity domain area on pyramid crs validity domain area
                try {
                    pyramidBound = Envelopes.transform(pyramidBound, crs2D);
                } catch (TransformException ex) {
                    Logging.getLogger("org.geotoolkit.storage.coverage").log(Level.WARNING, ex.getMessage(), ex);
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
                if (ratioTemp > ratio + DEFAULT_EPSILON) {
                    ratio = ratioTemp;
                    results.clear();
                    results.add(pyramid);
                } else if (Math.abs(ratio - ratioTemp) <= DEFAULT_EPSILON) {
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
            if (CRS.findOperation(pyCrs, crs2D, null).getMathTransform().isIdentity()
                    || Utilities.equalsIgnoreMetadata(crs2D, pyCrs)
                    || Utilities.equalsApproximatively(crs2D, pyCrs)) {
                return pyramid;
            }
        }
        // return first in list. impossible to define the most appropriate crs.
        return results.get(0);
    }

    /**
     * Sort Grid Mosaic according to there scale, then on additional dimensions.
     */
    public static final Comparator<GridMosaic> SCALE_COMPARATOR = new Comparator<GridMosaic>() {
        @Override
        public int compare(final GridMosaic m1, final GridMosaic m2) {
            final double res = m1.getScale() - m2.getScale();
            if(res == 0){
                //same scale check additional axes
                final DirectPosition m1ul = m1.getUpperLeftCorner();
                final DirectPosition m2ul = m2.getUpperLeftCorner();
                for(int i=2,n=m1ul.getDimension();i<n;i++){
                    final double ord1 = m1ul.getOrdinate(i);
                    final double ord2 = m2ul.getOrdinate(i);
                    final int c = Double.valueOf(ord1).compareTo(ord2);
                    if(c != 0) return c;
                }

                return 0;
            }else if(res > 0){
                return 1;
            }else{
                return -1;
            }
        }
    };
}
