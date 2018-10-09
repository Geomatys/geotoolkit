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
package org.geotoolkit.data.multires;

import java.util.Collection;
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
     */
    double[] getScales();

    /**
     * @param index of the wanted scale, must match an available index of the scales table.
     * @return Collection<GridMosaic> available mosaics at this scale.
     * Waring : in multidimensional pyramids, multiple mosaic at the same scale
     * may exist.
     */
    Collection<? extends Mosaic> getMosaics(int index);

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
