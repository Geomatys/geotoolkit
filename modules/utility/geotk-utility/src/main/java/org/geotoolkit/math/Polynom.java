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
package org.geotoolkit.math;

import java.io.Serializable;
import java.util.Arrays;
import static java.lang.Math.*;

import org.apache.sis.util.ArraysExt;
import org.geotoolkit.util.converter.Classes;


/**
 * The coefficients of a polynomial equation. The equation must be in the form
 *
 * <code>y = c<sub>0</sub> +
 *           c<sub>1</sub>&times;<var>x</var> +
 *           c<sub>2</sub>&times;<var>x</var><sup>2</sup> +
 *           c<sub>3</sub>&times;<var>x</var><sup>3</sup> + ... +
 *           c<sub>n</sub>&times;<var>x</var><sup>n</sup></code>.
 * <p>
 * The static method {@link #roots(double[])} can be used for computing the root of a polynomial
 * equation without creating a {@code Polynom} object.
 *
 * @author Martin Desruisseaux (IRD)
 * @author Ken Turkiwski (for algorithmic inspiration)
 * @version 3.00
 *
 * @since 2.0
 * @module
 */
public class Polynom implements Serializable {
    /**
     * Serial version UID for compatibility with different versions.
     */
    private static final long serialVersionUID = 6825019711186108990L;

    /**
     * The coefficients for the polynomial equation.
     */
    private final double[] c;

    /**
     * The roots of the polynomial equation. Will be computed only when first requested.
     */
    private transient double[] roots;

    /**
     * Constructs a polynomial equation with the specified coefficients.
     *
     * @param c The coefficients. This array is copied.
     */
    public Polynom(final double[] c) {
        int n = c.length;
        while (n != 0 && c[--n] == 0) {
            // Empty on purpose.
        }
        if (n == 0) {
            this.c = ArraysExt.EMPTY_DOUBLE;
        } else {
            this.c = new double[n];
            System.arraycopy(c, 0, this.c, 0, n);
        }
    }

    /**
     * Evaluates the polynomial equation for the specified <var>x</var> value.
     * More specifically, this method computes
     *
     * <code>c<sub>0</sub> +
     *       c<sub>1</sub>&times;<var>x</var> +
     *       c<sub>2</sub>&times;<var>x</var><sup>2</sup> +
     *       c<sub>3</sub>&times;<var>x</var><sup>3</sup> + ... +
     *       c<sub>n</sub>&times;<var>x</var><sup>n</sup></code>.
     *
     * @param x The value where to evaluate the polynomial equation.
     * @return The result of the evaluation at the given value.
     */
    public final double y(final double x) {
        double sum = 0;
        for (int i=c.length; --i>=0;) {
            sum = sum*x + c[i];
        }
        return sum;
    }

    /**
     * Finds the roots of a quadratic equation.
     * More specifically, this method solves the following equation:
     *
     * <blockquote><code>
     * c0 +
     * c1*<var>x</var> +
     * c2*<var>x</var><sup>2</sup> == 0
     * </code></blockquote>
     *
     * @return The roots. The length may be 1 or 2.
     */
    private static double[] quadraticRoots(double c0, double c1, double c2) {
        double d = c1*c1 - 4*c2*c0;
        if (d > 0) {
            // Two real, distinct roots
            d = sqrt(d);
            if (c1 < 0) {
                d = -d;
            }
            final double q = 0.5*(d - c1);
            return new double[] {
                q/c2,
                (q!=0) ? c0/q : -0.5*(d + c1)/c2
            };
        } else if (d == 0) {
            // One real double root
            return new double[] {
                -c1 / (2*c2)
            };
        } else {
            // Two complex conjugate roots
            return ArraysExt.EMPTY_DOUBLE;
        }
    }

    /**
     * Finds the roots of a cubic equation.
     * More specifically, this method solves the following equation:
     *
     * <blockquote><code>
     * c0 +
     * c1*<var>x</var> +
     * c2*<var>x</var><sup>2</sup> +
     * c3*<var>x</var><sup>3</sup> == 0
     * </code></blockquote>
     *
     * @return The roots. The length may be 1 or 3.
     */
    private static double[] cubicRoots(double c0, double c1, double c2, double c3) {
        c2 /= c3;
        c1 /= c3;
        c0 /= c3;
        final double Q  = (c2*c2 - 3*c1) / 9;
        final double R = (2*c2*c2*c2 - 9*c2*c1 + 27*c0) / 54;
        final double Qcubed = Q*Q*Q;
        final double d = Qcubed - R*R;

        c2 /= 3;
        if (d >= 0) {
            final double theta = acos(R / sqrt(Qcubed)) / 3;
            final double scale = -2 * sqrt(Q);
            final double[] roots = new double[] {
                scale * cos(theta              ) - c2,
                scale * cos(theta + PI*2/3) - c2,
                scale * cos(theta + PI*4/3) - c2
            };
            assert abs(roots[0]*roots[1]*roots[2] + c0  ) < 1E-6;
            assert abs(roots[0]+roots[1]+roots[2] + c2*3) < 1E-6;
            assert abs(roots[0]*roots[1] +
                       roots[0]*roots[2] +
                       roots[1]*roots[2] - c1) < 1E-6;
            return roots;
        } else {
            double e = cbrt(sqrt(-d) + abs(R));
            if (R > 0) {
                e = -e;
            }
            return new double[] {
                (e + Q/e) - c2
            };
        }
    }

    /**
     * Finds the roots of the polynomial equation.
     *
     * @return The roots.
     */
    public double[] roots() {
        if (roots == null) {
            roots = roots(c);
        }
        return roots.clone();
    }

    /**
     * Finds the roots of a polynomial equation. More specifically,
     * this method solve the following equation:
     *
     * <blockquote><code>
     * c[0] +
     * c[1]*<var>x</var> +
     * c[2]*<var>x</var><sup>2</sup> +
     * c[3]*<var>x</var><sup>3</sup> +
     * ... +
     * c[n]*<var>x</var><sup>n</sup> == 0
     * </code></blockquote>
     *
     * where <var>n</var> is the array length minus 1.
     *
     * @param  c The coefficients for the polynomial equation.
     * @return The roots. This array may have any length up to {@code n-1}.
     * @throws UnsupportedOperationException if there is more coefficients than this method
     *         can handle.
     */
    public static double[] roots(final double[] c) {
        int n = c.length;
        while (n != 0 && c[--n] == 0) {
            // Empty on purpose.
        }
        switch (n) {
            case 0:  return ArraysExt.EMPTY_DOUBLE;
            case 1:  return new double[] {-c[0]/c[1]};
            case 2:  return quadraticRoots(c[0], c[1], c[2]);
            case 3:  return cubicRoots(c[0], c[1], c[2], c[3]);
            default: throw new UnsupportedOperationException(String.valueOf(n));
        }
    }

    /**
     * Returns a hash value for this polynom.
     */
    @Override
    public int hashCode() {
        return Arrays.hashCode(c) ^ (int) serialVersionUID;
    }

    /**
     * Compares this polynom with the specified object for equality.
     *
     * @param object The object to compare with this polynom.
     * @return {@code true} if both objects are equal.
     */
    @Override
    public boolean equals(final Object object) {
        if (object != null && object.getClass() == getClass()) {
            final Polynom that = (Polynom) object;
            return Arrays.equals(this.c, that.c);
        }
        return false;
    }

    /**
     * Returns a string representation of this polynom.
     */
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder(Classes.getShortClassName(this));
        buffer.append('[');
        for (int i=0; i<c.length; i++) {
            if (i != 0) {
                buffer.append(", ");
            }
            buffer.append(c[i]);
        }
        return buffer.append(']').toString();
    }
}
