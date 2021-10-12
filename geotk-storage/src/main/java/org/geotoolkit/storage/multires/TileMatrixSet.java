/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2018, Geomatys
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
package org.geotoolkit.storage.multires;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import org.apache.sis.storage.DataStoreException;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * A TileMatrixSet is a collection of TileMatrix in the same CRS but at different
 * scale levels.
 * <p>
 * Note : if the {@linkplain CoordinateReferenceSystem } of the TileMatrixSet has more
 * then two dimensions, it is possible to find TileMatrix at the same scale.
 * Each TileMatrix been located on a different slice in one of the {@linkplain CoordinateReferenceSystem }
 * axis.
 * </p>
 *
 * @author Johann Sorel (Geomatys)
 */
public interface TileMatrixSet {

    /**
     * @return unique id.
     */
    String getIdentifier();

    /**
     * Returns the mime type of tiles used in the model.
     * All tiles use the same format.
     *
     * @return time format.
     */
    String getFormat();

    /**
     * @return the crs used for all tiles in the pyramid.
     */
    CoordinateReferenceSystem getCoordinateReferenceSystem();

    /**
     * @return unmodifiable collection of all TileMatrix.
     * Waring : in multidimensional pyramids, multiple TileMatrix at the same scale
     * may exist.
     */
    Collection<? extends TileMatrix> getTileMatrices();

    /**
     * @return the different scales available in the pyramid.
     * The scale value is expressed in CRS unit by image cell (pixel usually)
     * Scales are sorted in natural order, from smallest to highest.
     */
    default double[] getScales() {
        final SortedSet<Double> scaleSet = new TreeSet<Double>();

        for(TileMatrix m : TileMatrixSet.this.getTileMatrices()){
            scaleSet.add(m.getScale());
        }

        final double[] scales = new double[scaleSet.size()];

        int i=0;
        for(Double d : scaleSet){
            scales[i] = d;
            i++;
        }

        return scales;
    }

    /**
     * @param scale the wanted scale, must match an available scale of the scales table.
     * @return Collection<TileMatrix> available TileMatrix at this scale.
     * Waring : in multidimensional pyramids, multiple TileMatrix at the same scale
     * may exist.
     */
    default Collection<TileMatrix> getTileMatrices(double scale) {
        final List<TileMatrix> candidates = new ArrayList<>();
        for (TileMatrix m : TileMatrixSet.this.getTileMatrices()) {
            if (m.getScale() == scale) {
                candidates.add(m);
            }
        }
        return candidates;
    }

    /**
     * Get pyramid envelope.
     * This is the aggregation of all TileMatrix envelopes.
     *
     * @return Envelope
     */
    Envelope getEnvelope();

    /**
     * Create new TileMatrix copied properties from template.
     *
     * @param template TileMatrix model
     * @return created TileMatrix
     * @throws DataStoreException
     */
    TileMatrix createTileMatrix(TileMatrix template) throws DataStoreException;

    /**
     * Delete given TileMatrix.
     *
     * @param tileMatrixId
     * @throws DataStoreException
     */
    void deleteTileMatrix(String tileMatrixId) throws DataStoreException;

}
