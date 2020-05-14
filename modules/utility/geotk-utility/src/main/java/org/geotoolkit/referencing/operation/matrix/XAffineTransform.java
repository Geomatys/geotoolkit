/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2001-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.referencing.operation.matrix;

import java.awt.geom.AffineTransform;

import static java.lang.Math.*;


/**
 * Utility methods for affine transforms.
 */
public final class XAffineTransform {
    /**
     * Do not allow instantiation of this class.
     */
    private XAffineTransform() {
    }

    /**
     * Returns {@code true} if the specified affine transform is an identity transform up to the
     * specified tolerance. This method is equivalent to computing the difference between this
     * matrix and an identity matrix (as created by {@link AffineTransform#AffineTransform()
     * new AffineTransform()}) and returning {@code true} if and only if all differences are
     * smaller than or equal to {@code tolerance}.
     * <p>
     * This method is used for working around rounding error in affine transforms resulting
     * from a computation, as in the example below:
     *
     * {@preformat text
     *     ┌                                                     ┐
     *     │ 1.0000000000000000001  0.0                      0.0 │
     *     │ 0.0                    0.999999999999999999999  0.0 │
     *     │ 0.0                    0.0                      1.0 │
     *     └                                                     ┘
     * }
     *
     * @param tr The affine transform to be checked for identity.
     * @param tolerance The tolerance value to use when checking for identity.
     * @return {@code true} if this transformation is close enough to the
     *         identity, {@code false} otherwise.
     *
     * @since 2.3.1
     */
    // LGPL
    public static boolean isIdentity(final AffineTransform tr, double tolerance) {
        if (tr.isIdentity()) {
            return true;
        }
        tolerance = abs(tolerance);
        return abs(tr.getScaleX() - 1) <= tolerance &&
               abs(tr.getScaleY() - 1) <= tolerance &&
               abs(tr.getShearX())     <= tolerance &&
               abs(tr.getShearY())     <= tolerance &&
               abs(tr.getTranslateX()) <= tolerance &&
               abs(tr.getTranslateY()) <= tolerance;
    }
}
