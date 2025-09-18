/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2025, Geomatys
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
package org.geotoolkit.storage.rs;

import org.geotoolkit.referencing.rs.ReferenceSystems;
import java.util.List;
import org.apache.sis.geometry.GeneralDirectPosition;
import org.apache.sis.util.ArraysExt;
import org.geotoolkit.referencing.dggs.DiscreteGlobalGridReferenceSystem;
import org.geotoolkit.referencing.dggs.Zone;
import org.opengis.geometry.DirectPosition;
import org.opengis.referencing.ReferenceSystem;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 * Holds the ordinates for geometry/area/zone/point within some reference system.
 *
 * @author Johann Sorel (Geomatys)
 */
public interface Address {

    /**
     * The reference system (RS) in which the location tuple is given.
     * May be {@code null} if this particular {@code Location} is included in a larger object
     * with such a reference to a {@linkplain ReferenceSystem reference system}.
     * In this case, the reference system is implicitly assumed to take on the value
     * of the containing object's <abbr>RS</abbr>.
     *
     * <h4>Default implementation</h4>
     * The default implementation returns {@code null}. Implementations should override
     * this method if the <abbr>RS</abbr> is known or can be taken from the containing object.
     *
     * @return the reference system (RS), or {@code null}.
     */
    default ReferenceSystem getReferenceSystem() {
        return null;
    }

    /**
     * The length of ordinate sequence (the number of entries). This is determined by the
     * {@linkplain #getReferenceSystem() reference system}.
     *
     * @return the dimensionality of this location.
     */
    int getDimension();

    /**
     * A <b>copy</b> of the ordinates stored as an array of object values.
     * Changes to the returned array will not affect this {@code Location}.
     * The array length shall be equal to the {@linkplain #getDimension() dimension}.
     *
     * <h4>Default implementation</h4>
     * The default implementation invokes {@link #getOrdinate(int)} for all indices
     * from 0 inclusive to {@link #getDimension()} exclusive, and stores the values
     * in a newly created array.
     *
     * @return a copy of the ordinates. Changes in the returned array will not be reflected back
     *         in this {@code Location} object.
     */
    default Object[] getOrdinates() {
        final Object[] ordinates = new Object[getDimension()];
        for (int i=0; i<ordinates.length; i++) {
            ordinates[i] = getOrdinate(i);
        }
        return ordinates;
    }

    /**
     * Returns the ordinate at the specified dimension.
     *
     * @param  dimension  the dimension in the range 0 to {@linkplain #getDimension dimension}−1.
     * @return the ordinate at the specified dimension.
     * @throws IndexOutOfBoundsException if the given index is negative or is equal or greater
     *         than the {@linkplain #getDimension() number of dimensions}.
     */
    Object getOrdinate(int dimension);

    default DirectPosition toDirectPosition() throws TransformException, FactoryException {
        final ReferenceSystem rs = getReferenceSystem();
        double[] position = new double[0];
        final List<ReferenceSystem> singleComponents = ReferenceSystems.getSingleComponents(rs, true);
        for (int i = 0; i < singleComponents.size(); i++) {
            final ReferenceSystem srs = singleComponents.get(i);
            if (srs instanceof CoordinateReferenceSystem crs) {
                position = ArraysExt.concatenate(position, new double[]{((Number)getOrdinate(i)).doubleValue()});
            } else if (srs instanceof DiscreteGlobalGridReferenceSystem dggrs) {
                Zone zone = dggrs.getGridSystem().getHierarchy().getZone(getOrdinate(i));
                position = ArraysExt.concatenate(position, zone.getPosition().getCoordinates());
            } else {
                throw new UnsupportedOperationException("todo");
            }
        }
        final GeneralDirectPosition dp = new GeneralDirectPosition(position);
        dp.setCoordinateReferenceSystem(ReferenceSystems.getLeaningCRS(rs));
        return dp;
    }
}
