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
 * An angle in degrees. An angle is the amount of rotation needed to bring one line or plane
 * into coincidence with another, generally measured in degrees, sexagesimal degrees or grads.
 *
 * @author Martin Desruisseaux (MPO, IRD)
 * @version 3.00
 *
 * @see Latitude
 * @see Longitude
 * @see AngleFormat
 *
 * @since 1.0
 * @module
 *
 * @deprecated Moved to Apache SIS {@link org.apache.sis.measure.Angle}.
 */
@Immutable
@Deprecated
public class Angle extends org.apache.sis.measure.Angle {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = 1158747349433104534L;

    /**
     * Constructs a new angle with the specified value.
     *
     * @param theta Angle in degrees.
     */
    public Angle(final double theta) {
        super(theta);
    }

    /**
     * Constructs a newly allocated {@code Angle} object that represents the angle value
     * represented by the string. The string should represents an angle in either fractional
     * degrees (e.g. 45.5°) or degrees with minutes and seconds (e.g. 45°30').
     *
     * @param  string A string to be converted to an {@code Angle}.
     * @throws NumberFormatException if the string does not contain a parsable angle.
     */
    public Angle(final String string) throws NumberFormatException {
        super(string);
    }
}
