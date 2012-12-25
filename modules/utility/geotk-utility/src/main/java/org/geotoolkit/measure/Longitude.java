/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 1999-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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
package org.geotoolkit.measure;

import net.jcip.annotations.Immutable;


/**
 * A longitude angle. Positive longitudes are East, while negative longitudes are West.
 *
 * @author Martin Desruisseaux (MPO, IRD)
 * @version 3.00
 *
 * @see Latitude
 * @see AngleFormat
 *
 * @since 1.0
 * @module
 *
 * @deprecated Moved to Apache SIS {@link org.apache.sis.measure.Longitude}.
 */
@Immutable
@Deprecated
public final class Longitude extends Angle {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = -8614900608052762636L;

    /**
     * Minimum legal value for longitude (-180°).
     */
    public static final double MIN_VALUE = -180;

    /**
     * Maximum legal value for longitude (+180°).
     */
    public static final double MAX_VALUE = +180;

    /**
     * Constructs a new longitude with the specified value.
     *
     * @param theta Angle in degrees.
     */
    public Longitude(final double theta) {
        super(theta);
    }

    /**
     * Constructs a newly allocated {@code Longitude} object that
     * represents the longitude value represented by the string.   The
     * string should represents an angle in either fractional degrees
     * (e.g. 45.5°) or degrees with minutes and seconds (e.g. 45°30').
     * The hemisphere (E or W) is optional (default to East).
     *
     * @param  theta A string to be converted to a {@code Longitude}.
     * @throws NumberFormatException if the string does not contain a parsable longitude.
     */
    public Longitude(final String theta) throws NumberFormatException {
        super(theta);
    }
}
