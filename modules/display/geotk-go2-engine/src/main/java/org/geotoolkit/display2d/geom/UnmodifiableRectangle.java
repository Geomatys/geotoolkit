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
package org.geotoolkit.display2d.geom;

import java.awt.geom.Rectangle2D;
import java.util.Locale;

import org.geotoolkit.display.shape.XRectangle2D;


/**
 * Immutable version of a serializable, high-performance double-precision rectangle.
 *
 * @source $URL: http://svn.geotools.org/branches/legacy/migrate/src/org/geotools/renderer/geom/UnmodifiableRectangle.java $
 * @version $Id: UnmodifiableRectangle.java 17672 2006-01-19 00:25:55Z desruisseaux $
 * @author Martin Desruisseaux
 */
final class UnmodifiableRectangle extends XRectangle2D {
    /**
     * Serial number for interoperability with different versions.
     */
    private static final long serialVersionUID = -8196023373680425093L;

    /**
     * Construct a rectangle with the same coordinates than the supplied rectangle.
     *
     * @param rect The rectangle. Use {@link #INFINITY} for initializing
     *             this <code>XRectangle2D</code> with infinite bounds.
     */
    public UnmodifiableRectangle(final Rectangle2D rect) {
        if (rect != null) {
            super.setRect(rect);
        }
    }

    /**
     * Throws {@link UnmodifiableGeometryException}.
     *
     * @deprecated Should never been invoked, since this rectangle is immutable.
     */
    @Override
    public void setRect(final double x, final double y, final double width, final double height) {
        throw new UnmodifiableGeometryException((Locale)null);
    }

    /**
     * Throws {@link UnmodifiableGeometryException}.
     *
     * @deprecated Should never been invoked, since this rectangle is immutable.
     */
    @Override
    public void setRect(final Rectangle2D r) {
        throw new UnmodifiableGeometryException((Locale)null);
    }

    /**
     * Throws {@link UnmodifiableGeometryException}.
     *
     * @deprecated Should never been invoked, since this rectangle is immutable.
     */
    @Override
    public void add(final double x, final double y) {
        throw new UnmodifiableGeometryException((Locale)null);
    }

    /**
     * Throws {@link UnmodifiableGeometryException}.
     *
     * @deprecated Should never been invoked, since this rectangle is immutable.
     */
    @Override
    public void add(final Rectangle2D rect) {
        throw new UnmodifiableGeometryException((Locale)null);
    }

    /**
     * Returns a mutable version of this rectangle.
     */
    @Override
    public Object clone() {
        return new XRectangle2D(this);
    }
}
