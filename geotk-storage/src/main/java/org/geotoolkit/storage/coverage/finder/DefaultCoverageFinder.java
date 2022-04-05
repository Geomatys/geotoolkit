/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012-2014, Geomatys
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
package org.geotoolkit.storage.coverage.finder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.sis.coverage.grid.DisjointExtentException;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridRoundingMode;
import org.geotoolkit.storage.multires.TileMatrix;
import org.geotoolkit.storage.multires.TileMatrixSet;
import org.opengis.geometry.Envelope;
import org.opengis.util.FactoryException;

/**
 * Define a default CoverageFinder adapted for projects.
 * In attempt to replace this class by {@link StrictlyCoverageFinder}.
 *
 * @author Remi Marechal (Geomatys).
 * @author Johann Sorel (Geomatys).
 * @see {@link StrictlyCoverageFinder}
 */
@Deprecated
public final class DefaultCoverageFinder extends CoverageFinder {

    /**
     * {@inheritDoc }.
     */
    @Override
    public TileMatrix findMosaic(TileMatrixSet tileMatrixSet, double resolution, double tolerance, Envelope env, Integer maxTileNumber) throws FactoryException {
        final List<TileMatrix> tileMatrices = new ArrayList<>(tileMatrixSet.getTileMatrices().values());
        Collections.sort(tileMatrices, SCALE_COMPARATOR);
        Collections.reverse(tileMatrices);
        TileMatrix result = null;
        mosaicLoop:
        for (TileMatrix tileMatrix : tileMatrices) {
            //check the mosaic intersect the searched envelope

            GridExtent intersection;
            try {
                intersection = tileMatrix.getTilingScheme().derive().rounding(GridRoundingMode.ENCLOSING).subgrid(env).getIntersection();
            } catch (DisjointExtentException ex) {
                continue;
            }

            final Envelope ul = tileMatrix.getTilingScheme().getEnvelope();

            if (result == null) {
                //set the highest mosaic as base
                result = tileMatrix;
            } else {
                //check additional axis
                for (int i = 2, n = ul.getDimension(); i < n; i++) {
                    final double median = env.getMedian(i);
                    final double currentDistance = Math.abs(
                            result.getTilingScheme().getEnvelope().getMedian(i) - median);
                    final double candidateDistance = Math.abs(
                            ul.getMedian(i) - median);

                    if (candidateDistance < currentDistance) {
                        //better mosaic
                        break;
                    } else if (candidateDistance > currentDistance) {
                        //less accurate
                        continue mosaicLoop;
                    }
                    //continue on other axes
                }
            }

            //check if it will not require too much tiles
            final double[] scale = tileMatrix.getResolution();
            double nbtileX = intersection.getSize(0);
            double nbtileY = intersection.getSize(1);

            //if the envelope has some NaN, we presume it's a square
            if (Double.isNaN(nbtileX) || Double.isInfinite(nbtileX)) {
                nbtileX = nbtileY;
            } else if (Double.isNaN(nbtileY) || Double.isInfinite(nbtileY)) {
                nbtileY = nbtileX;
            }

            if (maxTileNumber != null && maxTileNumber > 0 && nbtileX * nbtileY > maxTileNumber) {
                //we haven't reach the best resolution, it would require
                //too much tiles, we use the previous scale level
                break;
            }

            result = tileMatrix;

            if ((scale[0] * (1 - tolerance)) < resolution) {
                //we found the most accurate resolution
                break;
            }
        }
        return result;
    }
}
