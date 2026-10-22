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
 * Double-precision vec2 helpers, ported from gl-matrix v3.4.3
 * (© 2015-2021 Brandon Jones, Colin MacKenzie IV; MIT). Only the operations A5
 * uses are included; every array is a {@code double[2]}.
 *
 * <p>Port of upstream {@code math/vec2.ts}.</p>
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public final class Vec2 {

    private Vec2() {
    }

    /**
     * Creates a new, zeroed vec2.
     */
    public static double[] create() {
        return new double[2];
    }

    /**
     * Copy the values from one vec2 to another.
     */
    public static double[] copy(double[] out, double[] a) {
        out[0] = a[0];
        out[1] = a[1];
        return out;
    }

    /**
     * Creates a new vec2 initialized with values from an existing vector.
     */
    public static double[] clone(double[] a) {
        final double[] out = new double[2];
        out[0] = a[0];
        out[1] = a[1];
        return out;
    }

    /**
     * Creates a new vec2 initialized with the given values.
     */
    public static double[] fromValues(double x, double y) {
        final double[] out = new double[2];
        out[0] = x;
        out[1] = y;
        return out;
    }

    /**
     * Set the components of a vec2 to the given values.
     */
    public static double[] set(double[] out, double x, double y) {
        out[0] = x;
        out[1] = y;
        return out;
    }

    /**
     * Adds two vec2's.
     */
    public static double[] add(double[] out, double[] a, double[] b) {
        out[0] = a[0] + b[0];
        out[1] = a[1] + b[1];
        return out;
    }

    /**
     * Scales a vec2 by a scalar number.
     */
    public static double[] scale(double[] out, double[] a, double b) {
        out[0] = a[0] * b;
        out[1] = a[1] * b;
        return out;
    }

    /**
     * Adds two vec2's after scaling the second operand by a scalar value.
     */
    public static double[] scaleAndAdd(double[] out, double[] a, double[] b, double scaleBy) {
        out[0] = a[0] + b[0] * scaleBy;
        out[1] = a[1] + b[1] * scaleBy;
        return out;
    }

    /**
     * Negates the components of a vec2.
     */
    public static double[] negate(double[] out, double[] a) {
        out[0] = -a[0];
        out[1] = -a[1];
        return out;
    }

    /**
     * Calculates the length of a vec2.
     * Uses the JavaScript {@code Math.hypot} algorithm, see {@link Vec3#hypot(double, double)}.
     */
    public static double length(double[] a) {
        return Vec3.hypot(a[0], a[1]);
    }

    /**
     * Performs a linear interpolation between two vec2's.
     */
    public static double[] lerp(double[] out, double[] a, double[] b, double t) {
        final double ax = a[0],
                ay = a[1];
        out[0] = ax + t * (b[0] - ax);
        out[1] = ay + t * (b[1] - ay);
        return out;
    }

    /**
     * Rotate a vec2 by an angle (radians) about origin {@code b}.
     */
    public static double[] rotate(double[] out, double[] a, double[] b, double rad) {
        final double p0 = a[0] - b[0],
                p1 = a[1] - b[1],
                sinC = Math.sin(rad),
                cosC = Math.cos(rad);
        out[0] = p0 * cosC - p1 * sinC + b[0];
        out[1] = p0 * sinC + p1 * cosC + b[1];
        return out;
    }

    /**
     * Transforms the vec2 with a mat2.
     */
    public static double[] transformMat2(double[] out, double[] a, double[] m) {
        final double x = a[0],
                y = a[1];
        out[0] = m[0] * x + m[2] * y;
        out[1] = m[1] * x + m[3] * y;
        return out;
    }

    /**
     * Transforms the vec2 with a mat2d.
     */
    public static double[] transformMat2d(double[] out, double[] a, double[] m) {
        final double x = a[0],
                y = a[1];
        out[0] = m[0] * x + m[2] * y + m[4];
        out[1] = m[1] * x + m[3] * y + m[5];
        return out;
    }
}
