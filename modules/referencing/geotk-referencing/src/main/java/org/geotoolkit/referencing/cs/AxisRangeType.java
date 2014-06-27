/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Open Source Geospatial Foundation (OSGeo)
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
 *
 *    This package contains documentation from OpenGIS specifications.
 *    OpenGIS consortium's work is fully acknowledged here.
 */
package org.geotoolkit.referencing.cs;

import org.opengis.referencing.cs.AxisDirection;
import org.opengis.referencing.cs.EllipsoidalCS;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.cs.CoordinateSystemAxis;
import org.apache.sis.internal.referencing.AxisDirections;


/**
 * High-level characteristics about the range of ordinate values expected in a coordinate system.
 * For example this enumeration provides a way to specify whatever the range of longitude values
 * is expected to be positive (typically [0 … 360]°) or if the range mixes positive and negative
 * values (typically [-180 … +180]°).
 * <p>
 * The range of axis values usually don't have any impact on coordinate transformations.
 * However, they have an impact on methods that verify the <cite>domain of validity</cite>,
 * for example {@link org.apache.sis.geometry.GeneralEnvelope#reduceToDomain(boolean)}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @see DefaultEllipsoidalCS#shiftAxisRange(AxisRangeType)
 * @see org.geotoolkit.referencing.crs.DefaultGeographicCRS#shiftAxisRange(AxisRangeType)
 * @see org.geotoolkit.referencing.crs.DefaultCompoundCRS#shiftAxisRange(AxisRangeType)
 *
 * @since 3.20
 * @module
 *
 * @deprecated Replaced by {@link org.apache.sis.referencing.cs.AxesConvention}
 */
@Deprecated
public enum AxisRangeType {
    /**
     * The coordinate system uses positive and negative longitude values, typically
     * in the [-180 … +180]° range. The exact range and the angular units may vary.
     */
    SPANNING_ZERO_LONGITUDE,

    /**
     * The coordinate system uses positive longitude values, typically in the [0 … 360]° range.
     * The exact range and the angular units may vary.
     */
    POSITIVE_LONGITUDE;

    /*
     * If more enum are added, please edit the {@code AXIS_RANGE_*} constants in the
     * org.geotoolkit.internal.referencing.CRSUtilities class.
     */

    /**
     * Finds the dimension of the first axis having a range of values described by this type.
     * If no axis uses this type, returns -1.
     *
     * @param  cs The coordinate system in which to search for an axis having this type of range.
     * @return Dimension of the first axis having this type of range, of -1 if none.
     */
    public int indexIn(final CoordinateSystem cs) {
        if (cs instanceof DefaultCompoundCS) {
            for (final CoordinateSystem component : ((DefaultCompoundCS) cs).getComponents()) {
                final int i = indexIn(component);
                if (i >= 0) return i;
            }
        }
        if (cs instanceof EllipsoidalCS) {
            final int i = AxisDirections.indexOfColinear(cs, AxisDirection.EAST);
            if (i >= 0) {
                final CoordinateSystemAxis axis = cs.getAxis(i);
                final boolean positive = axis.getMinimumValue() >= 0;
                if ((this == POSITIVE_LONGITUDE) == positive) {
                    return i;
                }
            }
        }
        return -1;
    }
}
