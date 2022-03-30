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

import java.util.Optional;
import java.util.SortedMap;
import org.apache.sis.geometry.GeneralEnvelope;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.GenericName;

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
public interface TileMatrixSet extends org.apache.sis.storage.tiling.TileMatrixSet {

    /**
     * Returns an envelope that encompasses all {@code TileMatrix} instances in this set.
     * This is the {@linkplain org.apache.sis.geometry.GeneralEnvelope#add(Envelope) union}
     * of all values returned by {@code TileMatrix.getTilingScheme().getEnvelope()}.
     * May be empty if too costly to compute.
     *
     * @return the bounding box for all tile matrices in CRS coordinates, if available.
     */
    default Optional<Envelope> getEnvelope() {
        final GeneralEnvelope env = new GeneralEnvelope(getCoordinateReferenceSystem());
        env.setToNaN();
        for (TileMatrix tileMatrix : getTileMatrices().values()) {
            if (env.isAllNaN()) {
                env.setEnvelope(tileMatrix.getTilingScheme().getEnvelope());
            } else {
                env.add(tileMatrix.getTilingScheme().getEnvelope());
            }
        }
        return Optional.of(env);
    }

    /**
     * Returns all {@link TileMatrix} instances in this set, together with their identifiers.
     * For each value in the map, the associated key is {@link TileMatrix#getIdentifier()}.
     * Entries are sorted from coarser resolution (highest scale denominator) to most detailed
     * resolution (lowest scale denominator).
     *
     * @return unmodifiable collection of all {@code TileMatrix} instances with their identifiers.
     */
    SortedMap<GenericName, ? extends TileMatrix> getTileMatrices();

}
