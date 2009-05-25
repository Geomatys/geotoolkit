/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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
package org.geotoolkit.io.wkt;

import java.lang.reflect.Modifier;
import org.geotoolkit.resources.Errors;


/**
 * Thrown by {@link FormattableObject#toWKT} when an object can't be formatted as WKT.
 * A formatting may fails because an object is too complex for the WKT format capability
 * (for example an {@linkplain org.geotoolkit.referencing.crs.DefaultEngineeringCRS
 * engineering CRS} with different unit for each axis), or because only some specific
 * implementations can be formatted as WKT.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @see Formatter#setInvalidWKT
 *
 * @since 2.0
 * @module
 */
public class UnformattableObjectException extends UnsupportedOperationException {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = 3623766455562385536L;

    /**
     * The type of the object that can't be formatted.
     */
    private final Class<?> unformattable;

    /**
     * Constructs an exception with a default detail message.
     *
     * @param unformattable The type of the object that can't be formatted.
     *
     * @since 3.00
     */
    public UnformattableObjectException(final Class<?> unformattable) {
        super(message(unformattable));
        this.unformattable = unformattable;
    }

    /**
     * Constructs an exception with the specified detail message.
     *
     * @param message The detail message, or {@code null} if none.
     * @param unformattable The type of the object that can't be formatted.
     *
     * @since 2.4
     */
    public UnformattableObjectException(final String message, final Class<?> unformattable) {
        super(message);
        this.unformattable = unformattable;
    }

    /**
     * Creates a default message for the given unformattable object.
     *
     * @param unformattable The type of the object that can't be formatted.
     * @return An error message for the given object.
     */
    private static String message(Class<?> unformattable) {
        while (!Modifier.isPublic(unformattable.getModifiers())) {
            final Class<?> candidate = unformattable.getSuperclass();
            if (candidate == null) {
                break;
            }
            unformattable = candidate;
        }
        return Errors.format(Errors.Keys.INVALID_WKT_FORMAT_$1, unformattable);
    }

    /**
     * Returns the type of the object that can't be formatted. This is often an OpenGIS
     * interface rather than the implementation class. For example if a engineering CRS
     * uses different unit for each axis, then this method may return
     * <code>{@linkplain org.opengis.referencing.crs.CoordinateReferenceSystem}.class</code>.
     * It doesn't mean that no CRS can be formatted; only that a particular instance of it
     * can't. Other possible classes are {@link org.opengis.referencing.datum.ImageDatum},
     * {@link org.opengis.referencing.crs.ProjectedCRS}, <cite>etc</cite>.
     *
     * @return The class of the object that can't be formatted.
     *
     * @since 2.4
     */
    public Class<?> getUnformattableClass() {
        return unformattable;
    }
}
