/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 1998-2009, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.math;

import java.io.Serializable;
import javax.vecmath.MismatchedSizeException;
import javax.vecmath.Point3d;

import org.geotoolkit.util.Cloneable;
import org.geotoolkit.util.Utilities;


/**
 * Equation of a plane in a three-dimensional space (<var>x</var>,<var>y</var>,<var>z</var>).
 * The plane equation is expressed by {@link #c}, {@link #cx} and {@link #cy} coefficients as
 * below:
 *
 * <blockquote>
 *   <var>z</var>(<var>x</var>,<var>y</var>) = <var>c</var> +
 *   <var>cx</var>&times;<var>x</var> + <var>cy</var>&times;<var>y</var>
 * </blockquote>
 *
 * Those coefficients can be set directly, or computed by a linear regression of this plane
 * through a set of three-dimensional points.
 *
 * @author Martin Desruisseaux (MPO, IRD)
 * @author Howard Freeland (MPO, for algorithmic inspiration)
 * @version 3.00
 *
 * @since 1.0
 * @module
 */
public class Plane implements Cloneable, Serializable {
    /**
     * Serial number for compatibility with different versions.
     */
    private static final long serialVersionUID = 2956201711131316723L;

    /**
     * The <var>c</var> coefficient for this plane. This coefficient appears in the place equation
     * <var><strong>c</strong></var>+<var>cx</var>*<var>x</var>+<var>cy</var>*<var>y</var>.
     */
    public double c;

    /**
     * The <var>cx</var> coefficient for this plane. This coefficient appears in the place equation
     * <var>c</var>+<var><strong>cx</strong></var>*<var>x</var>+<var>cy</var>*<var>y</var>.
     */
    public double cx;

    /**
     * The <var>cy</var> coefficient for this plane. This coefficient appears in the place equation
     * <var>c</var>+<var>cx</var>*<var>x</var>+<var><strong>cy</strong></var>*<var>y</var>.
     */
    public double cy;

    /**
     * Construct a new plane. All coefficients are set to 0.
     */
    public Plane() {
    }

    /**
     * Computes the <var>z</var> value for the specified (<var>x</var>,<var>y</var>) point.
     * The <var>z</var> value is computed using the following equation:
     *
     * {@preformat java
     *     z(x,y) = c + cx*x + cy*y
     * }
     *
     * @param x The <var>x</var> value.
     * @param y The <var>y</var> value.
     * @return  The <var>z</var> value.
     */
    public final double z(final double x, final double y) {
        return c + cx*x + cy*y;
    }

    /**
     * Computes the <var>y</var> value for the specified (<var>x</var>,<var>z</var>) point.
     * The <var>y</var> value is computed using the following equation:
     *
     * {@preformat java
     *     y(x,z) = (z - (c + cx*x)) / cy
     * }
     *
     * @param x The <var>x</var> value.
     * @param z The <var>y</var> value.
     * @return  The <var>y</var> value.
     */
    public final double y(final double x, final double z) {
        return (z - (c + cx*x)) / cy;
    }

    /**
     * Computes the <var>x</var> value for the specified (<var>y</var>,<var>z</var>) point.
     * The <var>x</var> value is computed using the following equation:
     *
     * {@preformat java
     *     x(y,z) = (z - (c + cy*y)) / cx
     * }
     *
     * @param y The <var>x</var> value.
     * @param z The <var>y</var> value.
     * @return  The <var>x</var> value.
     */
    public final double x(final double y, final double z) {
        return (z - (c + cy*y)) / cx;
    }

    /**
     * Computes the plane's coefficients from the specified points.  Three points
     * are enough for determining exactly the plan, providing that the points are
     * not colinear.
     *
     * @param P1 The first point.
     * @param P2 The second point.
     * @param P3 The third point.
     * @throws ArithmeticException If the three points are colinear.
     */
    public void setPlane(final Point3d P1, final Point3d P2, final Point3d P3)
            throws ArithmeticException
    {
        final double m00 = P2.x*P3.y - P3.x*P2.y;
        final double m01 = P3.x*P1.y - P1.x*P3.y;
        final double m02 = P1.x*P2.y - P2.x*P1.y;
        final double det = m00 + m01 + m02;
        if (det == 0) {
            throw new ArithmeticException("Points are colinear.");
        }
        c  = ((   m00   )*P1.z + (   m01   )*P2.z + (   m02   )*P3.z) / det;
        cx = ((P2.y-P3.y)*P1.z + (P3.y-P1.y)*P2.z + (P1.y-P2.y)*P3.z) / det;
        cy = ((P3.x-P2.x)*P1.z + (P1.x-P3.x)*P2.z + (P2.x-P1.x)*P3.z) / det;
    }

    /**
     * Computes the plane's coefficients from a set of points. This method use
     * a linear regression in the least-square sense. Result is undertermined
     * if all points are colinear.
     *
     * @param x vector of <var>x</var> coordinates
     * @param y vector of <var>y</var> coordinates
     * @param z vector of <var>z</var> values
     * @return An estimation of the Pearson correlation coefficient.
     * @throws MismatchedSizeException if <var>x</var>, <var>y</var> and <var>z</var>
     *         don't have the same length.
     */
    public double fit(final double[] x, final double[] y, final double[] z)
            throws MismatchedSizeException
    {
        final int length = z.length;
        if (length != x.length || length != y.length) {
            throw new MismatchedSizeException();
        }
        int n = 0;
        double sum_x  = 0, rx  = 0;
        double sum_y  = 0, ry  = 0;
        double sum_z  = 0, rz  = 0;
        double sum_xx = 0, rxx = 0;
        double sum_yy = 0, ryy = 0;
        double sum_xy = 0, rxy = 0;
        double sum_zx = 0, rzx = 0;
        double sum_zy = 0, rzy = 0;
        for (int i=0; i<length; i++) {
            double xi = x[i]; if (Double.isNaN(xi)) continue;
            double yi = y[i]; if (Double.isNaN(yi)) continue;
            double zi = z[i]; if (Double.isNaN(zi)) continue;
            double xx = xi*xi;
            double yy = yi*yi;
            double xy = xi*yi;
            double zx = zi*xi;
            double zy = zi*yi;
            // Kahan summation algorithm.
            xi += rx;  rx  = xi + (sum_x  - (sum_x  += xi));
            yi += ry;  ry  = yi + (sum_y  - (sum_y  += yi));
            zi += rz;  rz  = zi + (sum_z  - (sum_z  += zi));
            xx += rxx; rxx = xx + (sum_xx - (sum_xx += xx));
            yy += ryy; ryy = yy + (sum_yy - (sum_yy += yy));
            xy += rxy; rxy = xy + (sum_xy - (sum_xy += xy));
            zx += rzx; rzx = zx + (sum_zx - (sum_zx += zx));
            zy += rzy; rzy = zy + (sum_zy - (sum_zy += zy));
            n++;
        }
        /*
         *    ( sum_zx - sum_z*sum_x )  =  cx*(sum_xx - sum_x*sum_x) + cy*(sum_xy - sum_x*sum_y)
         *    ( sum_zy - sum_z*sum_y )  =  cx*(sum_xy - sum_x*sum_y) + cy*(sum_yy - sum_y*sum_y)
         */
        final double zx = sum_zx - sum_z*sum_x/n;
        final double zy = sum_zy - sum_z*sum_y/n;
        final double xx = sum_xx - sum_x*sum_x/n;
        final double xy = sum_xy - sum_x*sum_y/n;
        final double yy = sum_yy - sum_y*sum_y/n;
        final double den= (xy*xy - xx*yy);

        cy = (zx*xy - zy*xx) / den;
        cx = (zy*xy - zx*yy) / den;
        c  = (sum_z - (cx*sum_x + cy*sum_y)) / n;
        /*
         * At this point, the model is computed. Now computes an estimation of the Pearson
         * correlation coefficient. Note that both the z array and the z computed from the
         * model have the same average, called sum_z below (the name is not true anymore).
         */
        sum_x /= n;
        sum_y /= n;
        sum_z /= n;
        double sx=0, sy=0, p=0;
        for (int i=0; i<length; i++) {
            final double xi = z[i] - sum_z;
            if (Double.isNaN(xi)) continue;
            final double yi = cx*(x[i] - sum_x) + cy*(y[i] - sum_y);
            if (Double.isNaN(yi)) continue;
            sx += xi * xi;
            sy += yi * yi;
            p  += xi * yi;
        }
        return p / Math.sqrt(sx * sy);
    }

    /**
     * Returns a string representation of this plane.
     * The string will contains the plane's equation, as below:
     *
     * <blockquote>
     *     <var>z</var>(<var>x</var>,<var>y</var>) = {@link #c} +
     *     {@link #cx}*<var>x</var> + {@link #cy}*<var>y</var>
     * </blockquote>
     */
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder("z(x,y)= ");
        if (c==0 && cx==0 && cy==0) {
            buffer.append(0);
        } else {
            if (c != 0) {
                buffer.append(c).append(" + ");
            }
            if (cx != 0) {
                buffer.append(cx).append("*x");
                if (cy != 0) {
                    buffer.append(" + ");
                }
            }
            if (cy != 0) {
                buffer.append(cy).append("*y");
            }
        }
        return buffer.toString();
    }

    /**
     * Compares this plane with the specified object for equality.
     *
     * @param object The object to compare with this plane for equality.
     * @return {@code true} if both objects are equal.
     */
    @Override
    public boolean equals(final Object object) {
        if (object!=null && getClass().equals(object.getClass())) {
            final Plane that = (Plane) object;
            return Utilities.equals(this.c,  that.c ) &&
                   Utilities.equals(this.cx, that.cx) &&
                   Utilities.equals(this.cy, that.cy);
        } else {
            return false;
        }
    }

    /**
     * Returns a hash code value for this plane.
     */
    @Override
    public int hashCode() {
        final long code = Double.doubleToLongBits(c ) +
                    31 * (Double.doubleToLongBits(cx) +
                    31 * (Double.doubleToLongBits(cy)));
        return ((int) code) ^ ((int) (code >>> 32)) ^ (int) serialVersionUID;
    }

    /**
     * Returns a clone of this plane.
     */
    @Override
    public Plane clone() {
        try {
            return (Plane) super.clone();
        } catch (CloneNotSupportedException exception) {
            throw new AssertionError(exception);
        }
    }
}
