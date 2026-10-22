/**
 * Copyright (C) 2026 Geomatys and A5 contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.geotoolkit.dggs.a5.internal.math;

/**
 * Double-precision vec3 helpers, ported from gl-matrix v3.4.3
 * (© 2015-2021 Brandon Jones, Colin MacKenzie IV; MIT). Only the operations A5
 * uses are included; every array is a {@code double[3]}.
 *
 * <p>Port of upstream {@code math/vec3.ts}.</p>
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public final class Vec3 {

    private Vec3() {
    }

    /**
     * Creates a new, zeroed vec3.
     */
    public static double[] create() {
        return new double[3];
    }

    /**
     * Creates a new vec3 initialized with values from an existing vector.
     */
    public static double[] clone(double[] a) {
        final double[] out = new double[3];
        out[0] = a[0];
        out[1] = a[1];
        out[2] = a[2];
        return out;
    }

    /**
     * Creates a new vec3 initialized with the given values.
     */
    public static double[] fromValues(double x, double y, double z) {
        final double[] out = new double[3];
        out[0] = x;
        out[1] = y;
        out[2] = z;
        return out;
    }

    /**
     * Set the components of a vec3 to the given values.
     */
    public static double[] set(double[] out, double x, double y, double z) {
        out[0] = x;
        out[1] = y;
        out[2] = z;
        return out;
    }

    /**
     * Adds two vec3's.
     */
    public static double[] add(double[] out, double[] a, double[] b) {
        out[0] = a[0] + b[0];
        out[1] = a[1] + b[1];
        out[2] = a[2] + b[2];
        return out;
    }

    /**
     * Subtracts vector b from vector a.
     */
    public static double[] subtract(double[] out, double[] a, double[] b) {
        out[0] = a[0] - b[0];
        out[1] = a[1] - b[1];
        out[2] = a[2] - b[2];
        return out;
    }

    /**
     * Scales a vec3 by a scalar number.
     */
    public static double[] scale(double[] out, double[] a, double b) {
        out[0] = a[0] * b;
        out[1] = a[1] * b;
        out[2] = a[2] * b;
        return out;
    }

    /**
     * Adds two vec3's after scaling the second operand by a scalar value.
     */
    public static double[] scaleAndAdd(double[] out, double[] a, double[] b, double scaleBy) {
        out[0] = a[0] + b[0] * scaleBy;
        out[1] = a[1] + b[1] * scaleBy;
        out[2] = a[2] + b[2] * scaleBy;
        return out;
    }

    /**
     * Calculates the length of a vec3.
     */
    public static double length(double[] a) {
        return hypot(a[0], a[1], a[2]);
    }

    /**
     * Calculates the euclidian distance between two vec3's.
     */
    public static double distance(double[] a, double[] b) {
        return hypot(b[0] - a[0], b[1] - a[1], b[2] - a[2]);
    }

    /**
     * Normalize a vec3.
     */
    public static double[] normalize(double[] out, double[] a) {
        final double x = a[0];
        final double y = a[1];
        final double z = a[2];
        double len = x * x + y * y + z * z;
        if (len > 0) {
            len = 1 / Math.sqrt(len);
        }
        out[0] = a[0] * len;
        out[1] = a[1] * len;
        out[2] = a[2] * len;
        return out;
    }

    /**
     * Calculates the dot product of two vec3's.
     */
    public static double dot(double[] a, double[] b) {
        return a[0] * b[0] + a[1] * b[1] + a[2] * b[2];
    }

    /**
     * Computes the cross product of two vec3's.
     */
    public static double[] cross(double[] out, double[] a, double[] b) {
        final double ax = a[0],
                ay = a[1],
                az = a[2];
        final double bx = b[0],
                by = b[1],
                bz = b[2];
        out[0] = ay * bz - az * by;
        out[1] = az * bx - ax * bz;
        out[2] = ax * by - ay * bx;
        return out;
    }

    /**
     * Performs a linear interpolation between two vec3's.
     */
    public static double[] lerp(double[] out, double[] a, double[] b, double t) {
        final double ax = a[0];
        final double ay = a[1];
        final double az = a[2];
        out[0] = ax + t * (b[0] - ax);
        out[1] = ay + t * (b[1] - ay);
        out[2] = az + t * (b[2] - az);
        return out;
    }

    /**
     * Transforms the vec3 with a quat.
     */
    public static double[] transformQuat(double[] out, double[] a, double[] q) {
        final double qx = q[0],
                qy = q[1],
                qz = q[2],
                qw = q[3];
        final double x = a[0],
                y = a[1],
                z = a[2];
        // var qvec = [qx, qy, qz]; var uv = vec3.cross([], qvec, a);
        double uvx = qy * z - qz * y,
                uvy = qz * x - qx * z,
                uvz = qx * y - qy * x;
        // var uuv = vec3.cross([], qvec, uv);
        double uuvx = qy * uvz - qz * uvy,
                uuvy = qz * uvx - qx * uvz,
                uuvz = qx * uvy - qy * uvx;
        // vec3.scale(uv, uv, 2 * w);
        final double w2 = qw * 2;
        uvx *= w2;
        uvy *= w2;
        uvz *= w2;
        // vec3.scale(uuv, uuv, 2);
        uuvx *= 2;
        uuvy *= 2;
        uuvz *= 2;
        // return vec3.add(out, a, vec3.add(out, uv, uuv));
        out[0] = x + uvx + uuvx;
        out[1] = y + uvy + uuvy;
        out[2] = z + uvz + uuvz;
        return out;
    }

    /**
     * Angle in radians between two UNIT vectors, as 2·atan2(‖a−b‖, ‖a+b‖).
     *
     * A numerically stable replacement for gl-matrix's acos(a·b): that form loses
     * half its digits when the vectors are nearly (anti)parallel and returns 0 for
     * separations below ~1e-8 rad, whereas this one keeps full precision over the
     * whole range [0, π] — the subtraction a−b is exact for nearby vectors and
     * atan2 has no sensitive endpoints. Inputs are assumed to be unit length.
     */
    public static double angle(double[] a, double[] b) {
        final double dx = a[0] - b[0],
                dy = a[1] - b[1],
                dz = a[2] - b[2];
        final double sx = a[0] + b[0],
                sy = a[1] + b[1],
                sz = a[2] + b[2];
        return 2 * Math.atan2(Math.sqrt(dx * dx + dy * dy + dz * dz), Math.sqrt(sx * sx + sy * sy + sz * sz));
    }

    /**
     * Alias for {@link #subtract(double[], double[], double[])}.
     */
    public static double[] sub(double[] out, double[] a, double[] b) {
        return subtract(out, a, b);
    }

    /**
     * Java port of the JavaScript {@code Math.hypot} algorithm as implemented by V8
     * (normalisation by the largest magnitude followed by a Kahan summation of the squares).
     * Used instead of {@link Math#hypot(double, double)} so that results are bit-identical
     * with the upstream TypeScript running on Node.js.
     */
    public static double hypot(double x, double y) {
        if (Double.isInfinite(x) || Double.isInfinite(y)) return Double.POSITIVE_INFINITY;
        if (Double.isNaN(x) || Double.isNaN(y)) return Double.NaN;
        final double ax = Math.abs(x), ay = Math.abs(y);
        final double max = Math.max(ax, ay);
        if (max == 0) return 0;
        // Kahan summation to avoid rounding errors.
        // Normalize the numbers to the largest one to avoid overflow.
        double sum = 0;
        double compensation = 0;
        double n, summand, preliminary;
        n = ax / max;
        summand = n * n - compensation;
        preliminary = sum + summand;
        compensation = (preliminary - sum) - summand;
        sum = preliminary;
        n = ay / max;
        summand = n * n - compensation;
        preliminary = sum + summand;
        sum = preliminary;
        return Math.sqrt(sum) * max;
    }

    /**
     * Three arguments variant of {@link #hypot(double, double)}.
     */
    public static double hypot(double x, double y, double z) {
        if (Double.isInfinite(x) || Double.isInfinite(y) || Double.isInfinite(z)) return Double.POSITIVE_INFINITY;
        if (Double.isNaN(x) || Double.isNaN(y) || Double.isNaN(z)) return Double.NaN;
        final double ax = Math.abs(x), ay = Math.abs(y), az = Math.abs(z);
        final double max = Math.max(Math.max(ax, ay), az);
        if (max == 0) return 0;
        // Kahan summation to avoid rounding errors.
        // Normalize the numbers to the largest one to avoid overflow.
        double sum = 0;
        double compensation = 0;
        double n, summand, preliminary;
        n = ax / max;
        summand = n * n - compensation;
        preliminary = sum + summand;
        compensation = (preliminary - sum) - summand;
        sum = preliminary;
        n = ay / max;
        summand = n * n - compensation;
        preliminary = sum + summand;
        compensation = (preliminary - sum) - summand;
        sum = preliminary;
        n = az / max;
        summand = n * n - compensation;
        preliminary = sum + summand;
        sum = preliminary;
        return Math.sqrt(sum) * max;
    }
}
