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
 * A Pyramid is a collection of mosaic in the same CRS but at different
 * scale levels.
 * <p>
 * Note : if the {@linkplain CoordinateReferenceSystem } of the pyramid has more
 * then two dimensions, it is possible to find multiple mosaics at the same scale.
 * Each mosaic been located on a different slice in one of the {@linkplain CoordinateReferenceSystem }
 * axis.
 * </p>
 *
 * @author Johann Sorel (Geomatys)
 */
public interface Pyramid extends MultiResolutionModel {

    /**
     * @return the crs used for all mosaics in the pyramid.
     */
    CoordinateReferenceSystem getCoordinateReferenceSystem();

    /**
     * @return unmodifiable collection of all mosaics.
     * Waring : in multidimensional pyramids, multiple mosaic at the same scale
     * may exist.
     */
    Collection<? extends Mosaic> getMosaics();

    /**
     * @return the different scales available in the pyramid.
     * The scale value is expressed in CRS unit by image cell (pixel usually)
     * Scales are sorted in natural order, from smallest to highest.
     */
    default double[] getScales() {
        final SortedSet<Double> scaleSet = new TreeSet<Double>();

        for(Mosaic m : getMosaics()){
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
     * @return Collection<GridMosaic> available mosaics at this scale.
     * Waring : in multidimensional pyramids, multiple mosaic at the same scale
     * may exist.
     */
    default Collection<Mosaic> getMosaics(double scale) {
        final List<Mosaic> candidates = new ArrayList<>();
        for (Mosaic m : getMosaics()) {
            if (m.getScale() == scale) {
                candidates.add(m);
            }
        }
        return candidates;
    }

    /**
     * Get pyramid envelope.
     * This is the aggregation of all mosaic envelopes.
     *
     * @return Envelope
     */
    Envelope getEnvelope();

    /**
     * Create new mosaic copied properties from template.
     *
     * @param template mosaic model
     * @return created mosaic
     * @throws DataStoreException
     */
    Mosaic createMosaic(Mosaic template) throws DataStoreException;

    /**
     * Delete given mosaic.
     *
     * @param mosaicId
     * @throws DataStoreException
     */
    void deleteMosaic(String mosaicId) throws DataStoreException;

}
