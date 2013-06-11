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

import java.util.Comparator;
import org.geotoolkit.coverage.CoverageUtilities;
import org.geotoolkit.coverage.GridMosaic;
import org.geotoolkit.coverage.Pyramid;
import org.geotoolkit.coverage.PyramidSet;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
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
     * Find the most appropriate pyramid in given pyramid set and given crs.
     * Returned Pyramid may not have the given crs.
     * 
     * @param set : pyramid set to search in
     * @param crs searched crs
     * @return Pyramid, never null exept if the pyramid set is empty
     */
    public final Pyramid findPyramid(final PyramidSet set, final CoordinateReferenceSystem crs) throws FactoryException {
        return CoverageUtilities.findPyramid(set, crs);
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
