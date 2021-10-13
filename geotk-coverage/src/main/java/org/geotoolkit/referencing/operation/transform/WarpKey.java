/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2012, Geomatys
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
package org.geotoolkit.referencing.operation.transform;

import java.awt.Rectangle;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.MathTransform2D;
import org.apache.sis.referencing.operation.transform.LinearTransform;
import org.apache.sis.referencing.operation.transform.MathTransforms;


/**
 * A key in a {@link WarpCache}. The inherited {@link Rectangle} is the domain in which the
 * transform is applied. Instances of this class shall not be modified after construction.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.16
 *
 * @since 3.16
 * @module
 */
@SuppressWarnings("serial")
final class WarpKey extends Rectangle {
    /**
     * The math transform for which a {@link javax.media.jai.Warp} object has been computed.
     */
    private final MathTransform2D transform;

    /**
     * Creates a new key for the given transform and domain.
     */
    WarpKey(final MathTransform2D transform, final Rectangle domain) {
        super(domain);
        this.transform = transform;
    }

    /**
     * Returns a hash code value for this key.
     */
    @Override
    public int hashCode() {
        return super.hashCode() ^ transform.hashCode();
    }

    /**
     * Returns {@code true} if the given object is equals to this key.
     */
    @Override
    public boolean equals(final Object value) {
        if (value instanceof WarpKey) {
            final WarpKey other = (WarpKey) value;
            return super.equals(other) && transform.equals(other.transform);
        }
        return false;
    }

    /**
     * Returns a string representation of this key for debugging purpose.
     */
    @Override
    public String toString() {
        String rect = super.toString();
        rect = rect.substring(rect.indexOf('[') + 1);
        return "WarpKey[" + nonLinear(transform).getClass().getSimpleName() + ", " + rect;
    }

    /**
     * Returns the first non-linear transform, or the given argument if none were found.
     * This is used only for information purpose in the {@link #toString()} method.
     */
    private static MathTransform nonLinear(MathTransform transform) {
        for (final MathTransform step : MathTransforms.getSteps(transform)) {
            if (!(step instanceof LinearTransform)) {
                return step;
            }
        }
        return transform;
    }
}
