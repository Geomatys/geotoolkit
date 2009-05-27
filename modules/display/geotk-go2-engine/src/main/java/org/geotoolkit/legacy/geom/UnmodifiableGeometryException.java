/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2004-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.legacy.geom;

import java.util.Locale;

import org.geotoolkit.resources.Errors;


/**
 * Throws when an attempt is made to modify a geometry, but this geometry is part of an other
 * geometry. For example the geometry may be a hole in a {@link Polygon}, or a geometry in a
 * {@link GeometryCollection}. Attempt to change those geometries may corrupt the container,
 * which is why they are not allowed. This exception may be thrown by the following methods:
 * <br><br>
 * <ul>
 *   <li>{@link Geometry#setCoordinateSystem}</li>
 *   <li>{@link Geometry#setResolution}</li>
 *   <li>{@link Geometry#compress}</li>
 *   <li>{@link Polyline#append}</li>
 *   <li>{@link Polyline#appendBorder}</li>
 *   <li>{@link Polyline#prependBorder}</li>
 *   <li>{@link Polygon#addHole}</li>
 *   <li>{@link GeometryCollection#add(Geometry)}</li>
 *   <li>{@link GeometryCollection#remove(Geometry)}</li>
 * </ul>
 * <br><br>
 * If this exception is thrown, the workaround is to {@linkplain Geometry#clone clone}
 * the geometry before to invokes one of the above methods.
 *
 * @source $URL: http://svn.geotools.org/branches/legacy/migrate/src/org/geotools/renderer/geom/UnmodifiableGeometryException.java $
 * @version $Id: UnmodifiableGeometryException.java 17672 2006-01-19 00:25:55Z desruisseaux $
 * @author Martin Desruisseaux
 */
public class UnmodifiableGeometryException extends IllegalStateException {
    /**
     * Serial number for compatibility with different versions.
     */
    private static final long serialVersionUID = 3256180011529483892L;

    /**
     * Construct an exception with an empty message.
     */
    public UnmodifiableGeometryException() {
        super();
    }

    /**
     * Construct an exception with the specified message.
     */
    public UnmodifiableGeometryException(final String message) {
        super(message);
    }

    /**
     * Construct an exception with a "Unmodifiable geometry" message in the given locale.
     *
     * @param locale The locale, or <code>null</code> for a default one.
     */
    public UnmodifiableGeometryException(final Locale locale) {
        super(Errors.getResources(locale).getString(Errors.Keys.UNMODIFIABLE_GEOMETRY));
    }
}
