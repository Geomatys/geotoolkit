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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.sis.geometry.GeneralEnvelope;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.ReferencingUtilities;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.apache.sis.util.ArgumentChecks;
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
 * @author Rémi Marechal (Geomatys).
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
     * @return Pyramid, never null exept if the pyramid set is empty
     */
    public static Pyramid findPyramid(final PyramidSet set, final CoordinateReferenceSystem crs) throws FactoryException {
        final CoordinateReferenceSystem crs2D = get2Dpart(crs);
        final Envelope crsBound1 = CRS.getEnvelope(crs2D);
        double ratio = Double.NEGATIVE_INFINITY;
        // envelope with crs geographic.
        final GeneralEnvelope intersection = new GeneralEnvelope(DefaultGeographicCRS.WGS84);
        final List<Pyramid> results = new ArrayList<Pyramid>();
        if (crsBound1 != null) {
            final GeneralEnvelope crsBound = new GeneralEnvelope(crsBound1);
            noValidityDomainFound :
            for(Pyramid pyramid : set.getPyramids()) {
                double ratioTemp = 0;
                Envelope pyramidBound = CRS.getEnvelope(get2Dpart(pyramid.getCoordinateReferenceSystem()));
                if (pyramidBound == null) {
                    results.add(pyramid);
                    continue noValidityDomainFound;
                }
                // compute sum of recovery ratio 
                // from crs validity domain area on pyramid crs validity domain area
                try {
                    pyramidBound = CRS.transform(pyramidBound, crs2D);
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
            final CoordinateReferenceSystem pyCrs = get2Dpart(pyramid.getCoordinateReferenceSystem());
            if (CRS.findMathTransform(pyCrs, crs2D).isIdentity() 
             || CRS.equalsIgnoreMetadata(crs2D, pyCrs) 
             || CRS.equalsApproximatively(crs2D, pyCrs)) {
                return pyramid;
            }
        }
        // return first in list. impossible to define the most appropriate crs.
        return results.get(0);
    }
    
    /**
     * Find and return 2D part from multi-dimensional {@link CoordinateReferenceSystem}.
     * 
     * @param crs {@link CoordinateReferenceSystem} which is study.
     * @return 2D part from multi-dimensional {@link CoordinateReferenceSystem}.
     */
    public static CoordinateReferenceSystem get2Dpart(CoordinateReferenceSystem crs) {
        for(CoordinateReferenceSystem ccrrss : ReferencingUtilities.decompose(crs)) {
            final CoordinateSystem cs = ccrrss.getCoordinateSystem();
            if((cs instanceof CartesianCS) 
            || (cs instanceof SphericalCS) 
            || (cs instanceof EllipsoidalCS)) {
                return ccrrss;
            }
        }
        throw new IllegalArgumentException("no 2D part exist in this crs : "+crs.toString());
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
             
        final MathTransform mt = CRS.findMathTransform(pyramid.getCoordinateReferenceSystem(), env.getCoordinateReferenceSystem());
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
}
