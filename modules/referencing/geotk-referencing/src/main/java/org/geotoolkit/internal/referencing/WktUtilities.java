/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2001-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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
package org.geotoolkit.internal.referencing;

import javax.measure.unit.SI;
import org.opengis.referencing.cs.CartesianCS;
import org.opengis.referencing.cs.AxisDirection;

import org.geotoolkit.lang.Static;
import org.geotoolkit.referencing.cs.DefaultCartesianCS;
import org.geotoolkit.referencing.cs.DefaultCoordinateSystemAxis;


/**
 * A set of static methods related to Well Known Text (WKT).
 * <p>
 * <strong>Do not rely on this API!</strong> It may change in incompatible way
 * in any future release.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.0
 * @module
 */
@Static
public final class WktUtilities {
    /**
     * A three-dimensional cartesian CS with the legacy set of geocentric axes.
     * Those axes were defined in OGC 01-009 as <var>Other</var>,
     * <var>{@linkplain DefaultCoordinateSystemAxis#EASTING Easting}</var>,
     * <var>{@linkplain DefaultCoordinateSystemAxis#NORTHING Northing}</var>
     * in metres, where the "Other" axis is toward prime meridian.
     */
    public static final DefaultCartesianCS LEGACY = new DefaultCartesianCS("Legacy",
            new DefaultCoordinateSystemAxis("X", AxisDirection.OTHER, SI.METRE),
            new DefaultCoordinateSystemAxis("Y", AxisDirection.EAST,  SI.METRE),
            new DefaultCoordinateSystemAxis("Z", AxisDirection.NORTH, SI.METRE));

    /**
     * Do not allow creation of instances of this class.
     */
    private WktUtilities() {
    }

    /**
     * Returns the axes to use instead of the ones in the given coordinate system.
     * If the coordinate system axes should be used as-is, returns {@code cs}.
     *
     * @param  cs The coordinate system for which to compare the axis directions.
     * @param  legacy {@code true} for replacing ISO directions by the legacy ones,
     *         or {@code false} for the other way around.
     * @return The axes to use instead of the ones in the given CS,
     *         or {@code cs} if the CS axes should be used as-is.
     */
    public static CartesianCS replace(final CartesianCS cs, final boolean legacy) {
        final CartesianCS check = legacy ? DefaultCartesianCS.GEOCENTRIC : LEGACY;
        final int dimension = check.getDimension();
        if (cs.getDimension() != dimension) {
            return cs;
        }
        for (int i=0; i<dimension; i++) {
            if (!cs.getAxis(i).getDirection().equals(check.getAxis(i).getDirection())) {
                return cs;
            }
        }
        return legacy ? LEGACY : DefaultCartesianCS.GEOCENTRIC;
    }
}
