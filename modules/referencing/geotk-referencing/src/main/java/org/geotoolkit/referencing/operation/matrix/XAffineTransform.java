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
import org.apache.sis.referencing.operation.matrix.AffineTransforms2D;

import static java.lang.Math.*;


/**
 * Utility methods for affine transforms.
 */
@Deprecated
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

    /**
     * Returns a global scale factor for the specified affine transform. This scale factor combines
     * {@link #getScaleX0 getScaleX0(tr)} and {@link #getScaleY0 getScaleY0(tr)}. The way to compute
     * such a "global" scale is somewhat arbitrary and may change in a future version.
     *
     * @param tr The affine transform to inspect.
     * @return The magnitude of scale factory.
     */
    public static double getScale(final AffineTransform tr) {
        return 0.5 * (AffineTransforms2D.getScaleX0(tr) + AffineTransforms2D.getScaleY0(tr));
    }

    /**
     * If scale and shear coefficients are close to integers, replaces their current values by
     * their rounded values. The scale and shear coefficients are handled in a "all or nothing"
     * way; either all of them are rounded, or either none of them. The translation terms are
     * handled separately, provided that the scale and shear coefficients has been rounded.
     * <p>
     * This rounding up is useful for example in order to speedup image displays.
     *
     * @param tr The matrix to round. Rounding will be applied in place.
     * @param tolerance The maximal departure from integers in order to allow rounding.
     *        It is typically a small number like {@code 1E-6}.
     *
     * @see org.geotoolkit.image.io.metadata.MetadataHelper#adjustForRoundingError(double)
     *
     * @since 3.14 (derived from 2.3.1)
     */
    // LGPL (only the 'tolerance' argument actually)
    public static void roundIfAlmostInteger(final AffineTransform tr, final double tolerance) {
        double r;
        final double m00, m01, m10, m11;
        if (abs((m00 = rint(r=tr.getScaleX())) - r) <= tolerance &&
            abs((m01 = rint(r=tr.getShearX())) - r) <= tolerance &&
            abs((m11 = rint(r=tr.getScaleY())) - r) <= tolerance &&
            abs((m10 = rint(r=tr.getShearY())) - r) <= tolerance)
        {
            /*
             * At this point the scale and shear coefficients can been rounded to integers.
             * Continue only if this rounding doesn't lead to a non-invertible transform.
             */
            if ((m00!=0 || m01!=0) && (m10!=0 || m11!=0)) {
                double m02, m12;
                if (abs((r = rint(m02=tr.getTranslateX())) - m02) <= tolerance) m02=r;
                if (abs((r = rint(m12=tr.getTranslateY())) - m12) <= tolerance) m12=r;
                tr.setTransform(m00, m10, m01, m11, m02, m12);
            }
        }
    }
}
