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

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.geotoolkit.storage.coverage.CoverageUtilities;
import org.geotoolkit.storage.coverage.GridMosaic;
import org.geotoolkit.storage.coverage.Pyramid;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.referencing.CRS;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.util.FactoryException;

/**
 * More Mathematical exhaustive CoverageFinder.
 *
 * @author Remi Marechal (Geomatys).
 */
public class StrictlyCoverageFinder extends CoverageFinder {

    /**
     * {@inheritDoc }.
     * <p>Note : Can return null if no mosaic within {@link Envelope} parameter area exist.</p>
     */
    @Override
    public GridMosaic findMosaic(Pyramid pyramid, double resolution, double tolerance, Envelope env, Integer maxTileNumber)
            throws FactoryException {

        final MathTransform mt = CRS.findOperation(pyramid.getCoordinateReferenceSystem(), env.getCoordinateReferenceSystem(), null).getMathTransform();
        if (!mt.isIdentity()) throw new IllegalArgumentException("findMosaic : not same CoordinateReferenceSystem");
        final List<GridMosaic> mosaics = new ArrayList<>(pyramid.getMosaics());
        final List<GridMosaic> goodMosaics;

        final GeneralEnvelope findEnvelope = new GeneralEnvelope(env);
        // if crs is compound
        if (env.getDimension() > 2) {
            double bestRatio = Double.NEGATIVE_INFINITY;
            goodMosaics = new ArrayList<>();
            // find nearest gridMosaic
            for (GridMosaic gridMosaic : mosaics) {
                final Envelope gridEnvelope = gridMosaic.getEnvelope();
                // if intersection solution exist
                if (findEnvelope.intersects(gridEnvelope, true)) {
                    final double ratioTemp = CoverageUtilities.getRatioND(findEnvelope, gridEnvelope);
                    if (ratioTemp > (bestRatio + DEFAULT_EPSILON)) { // >
                        goodMosaics.clear();
                        goodMosaics.add(gridMosaic);
                        bestRatio = ratioTemp;
                    } else if ((Math.abs(ratioTemp - bestRatio)) <= DEFAULT_EPSILON) { // =
                        goodMosaics.add(gridMosaic);
                    }
                }
            }
        } else {
            goodMosaics = mosaics;
        }
        // if no coverage intersect search envelope.
        if (goodMosaics.isEmpty())   return null;

        if (goodMosaics.size() == 1) return goodMosaics.get(0);

        // find mosaic with the most appropriate scale value.
        Collections.sort(goodMosaics, SCALE_COMPARATOR);
        Collections.reverse(goodMosaics);

        GridMosaic result = null;

        for (GridMosaic candidate : goodMosaics) {// find best scale
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

            if(maxTileNumber != null && maxTileNumber > 0 && nbtileX*nbtileY > maxTileNumber){
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
}
