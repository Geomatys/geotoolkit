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
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import org.geotoolkit.coverage.GridMosaic;
import org.geotoolkit.coverage.Pyramid;
import org.geotoolkit.coverage.PyramidSet;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.metadata.iso.extent.DefaultGeographicBoundingBox;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.ReferencingUtilities;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.util.ArgumentChecks;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.metadata.extent.GeographicExtent;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.cs.CartesianCS;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.cs.EllipsoidalCS;
import org.opengis.referencing.cs.SphericalCS;
import org.opengis.util.FactoryException;

/**
 * Define {@link Coverage} search rules in Pyramid.
 * 
 * @author Rémi Marechal (Geomatys).
 * @author Johann Sorel  (Geomatys).
 */
public abstract class CoverageFinder {

    protected static final double EPSILON = 1E-12; 
    
    protected CoverageFinder() {
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
    public abstract GridMosaic findMosaic(final Pyramid pyramid, final double resolution, 
            final double tolerance, final Envelope env, int maxTileNumber) throws FactoryException;
    
    /**
     * <p>Compute ratio on each ordinate, not within 2D part of {@link CoordinateReferenceSystem},
     * which represent recovery of searchEnvelope on gridEnvelope.</p>
     * 
     * @param searchEnvelope user coverage area search. 
     * @param gridEnvelope mosaic envelope.
     * @return computed ratio.
     */
    protected final double getRatioND(Envelope searchEnvelope, Envelope gridEnvelope) {
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
                if (Math.abs(ges) <= EPSILON) continue;
                sumRatio += intersection.getSpan(d) / ges;
            }
        }
        return sumRatio;
    }
    
    /**
     * Return {@link CoordinateReferenceSystem} validity domain.
     * 
     * @param crs
     * @return {@link CoordinateReferenceSystem} validity domain.
     */
    protected final List<GeneralEnvelope> getValidityDomain(CoordinateReferenceSystem crs) {
        ArgumentChecks.ensureNonNull("crs", crs);
        List<GeneralEnvelope> crsBounds = null;
        if (crs.getDomainOfValidity() == null) return crsBounds;
        for (GeographicExtent geog : crs.getDomainOfValidity().getGeographicElements()) {
            if (geog instanceof DefaultGeographicBoundingBox) {
                if (crsBounds == null) crsBounds = new ArrayList<GeneralEnvelope>();
                final DefaultGeographicBoundingBox dgbb = (DefaultGeographicBoundingBox) geog;
                final GeneralEnvelope envTemp = new GeneralEnvelope(DefaultGeographicCRS.WGS84);
                envTemp.setEnvelope(dgbb.getWestBoundLongitude(), dgbb.getSouthBoundLatitude(), dgbb.getEastBoundLongitude(), dgbb.getNorthBoundLatitude());
                crsBounds.add(envTemp);
            }
        }
        return crsBounds;
    }
    
    /**
     * Find and return 2D geographic {@link CoordinateReferenceSystem}, from another multi-dimensional {@link CoordinateReferenceSystem}. 
     * 
     * @param crs
     * @return 2D geographic {@link CoordinateReferenceSystem}, from another multi-dimensional {@link CoordinateReferenceSystem}.
     */
    protected final CoordinateReferenceSystem get2Dpart(CoordinateReferenceSystem crs) {
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
     * Find the most appropriate pyramid in given pyramid set and given crs.
     * Returned Pyramid may not have the given crs.
     * 
     * @param set : pyramid set to search in
     * @param crs searched crs
     * @return Pyramid, never null exept if the pyramid set is empty
     */
    public final Pyramid findPyramid(final PyramidSet set, final CoordinateReferenceSystem crs) throws FactoryException{
        final List<GeneralEnvelope> crsBounds = getValidityDomain(crs);
        double ratio = Double.NEGATIVE_INFINITY;
        // envelope with crs geographic.
        final GeneralEnvelope intersection = new GeneralEnvelope(DefaultGeographicCRS.WGS84);
        final List<Pyramid> results = new ArrayList<Pyramid>();
        if (crsBounds != null) {
            noValidityDomainFind :
            for(Pyramid pyramid : set.getPyramids()) {
                double ratioTemp = 0;
                final List<GeneralEnvelope> pyramidBounds = getValidityDomain(pyramid.getCoordinateReferenceSystem());
                if (pyramidBounds == null) {
                    results.add(pyramid);
                    continue noValidityDomainFind;
                }
                // compute som of recovery ratio 
                // from crs validity domain area on pyramid crs validity domain area
                for(GeneralEnvelope crsBound : crsBounds) {
                    for(GeneralEnvelope pyramidBound : pyramidBounds) {
                        if (!pyramidBound.intersects(crsBound, true)) continue;// no intersection
                        intersection.setEnvelope(crsBound);
                        intersection.intersect(pyramidBound);
                        for (int d = 0; d < 2; d++) {// dim = 2 because extend geographic2D.
                            final double pbs = pyramidBound.getSpan(d);
                            // if intersect a slice part of gridEnvelope.
                            // avoid divide by zero
                            if (pbs <= 1E-12) continue;
                            ratioTemp += intersection.getSpan(d) / pbs;
                        }
                    }
                }
                //gerer les ratio et renvoyer la pyramide correspondant au ratio le plus elevé
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
            
        //paranoaic test
        if (results.isEmpty()) throw new IllegalStateException("pyramid not find");
        if (results.size() == 1) return results.get(0);
        // if several equal ratio.
        final CoordinateReferenceSystem crs2d = get2Dpart(crs);
        for (Pyramid pyramid : results) {
            final CoordinateReferenceSystem pyCrs = get2Dpart(pyramid.getCoordinateReferenceSystem());
            if (CRS.findMathTransform(pyCrs, crs2d).isIdentity() 
             || CRS.equalsIgnoreMetadata(crs2d, pyCrs) 
             || CRS.equalsApproximatively(crs2d, pyCrs)) {
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
