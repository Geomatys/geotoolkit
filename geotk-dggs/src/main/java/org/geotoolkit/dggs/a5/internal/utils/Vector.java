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
package org.geotoolkit.dggs.a5.internal.utils;

import org.geotoolkit.dggs.a5.internal.math.Vec3;

/**
 * Vector utilities on Cartesian [x, y, z] coordinates.
 *
 * <p>Port of upstream {@code utils/vector.ts}.</p>
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public final class Vector {

    private Vector() {
    }

    /**
     * Computes the scalar triple product A · (B × C).
     * Written out fully (same operation order as vec3.cross followed by vec3.dot,
     * so results are bit-identical) to avoid a scratch-vector store on hot paths.
     *
     * @param A Cartesian [x, y, z]
     * @param B Cartesian [x, y, z]
     * @param C Cartesian [x, y, z]
     * @return scalar triple product
     */
    public static double tripleProduct(double[] A, double[] B, double[] C) {
        return A[0] * (B[1] * C[2] - B[2] * C[1]) + A[1] * (B[2] * C[0] - B[0] * C[2]) + A[2] * (B[0] * C[1] - B[1] * C[0]);
    }

    /**
     * Cached {@code gamma} and {@code sin(gamma)} for a fixed (A, B) pair, so loops that
     * slerp many times along the same arc don't re-run {@code vec3.angle} and {@code Math.sin}.
     * Build with {@code precomputeSlerp(A, B)} and pass to {@code slerp} as the optional {@code ctx}.
     */
    public record SlerpContext(double gamma, double sinGamma) {
    }

    /**
     * @param A Cartesian [x, y, z] unit vector
     * @param B Cartesian [x, y, z] unit vector
     * @return precomputed slerp context for the arc A→B
     */
    public static SlerpContext precomputeSlerp(double[] A, double[] B) {
        final double gamma = Vec3.angle(A, B);
        return new SlerpContext(gamma, Math.sin(gamma));
    }

    /**
     * Spherical linear interpolation between two vectors.
     *
     * @param out The target vector to write the result to
     * @param A The first vector
     * @param B The second vector
     * @param t The interpolation parameter (0 to 1)
     * @return The interpolated vector (same as out)
     */
    public static double[] slerp(double[] out, double[] A, double[] B, double t) {
        return slerp(out, A, B, t, null);
    }

    /**
     * Spherical linear interpolation between two vectors.
     *
     * @param out The target vector to write the result to
     * @param A The first vector
     * @param B The second vector
     * @param t The interpolation parameter (0 to 1)
     * @param ctx Optional (nullable) precomputed {@code {gamma, sinGamma}}; supply when slerping
     *            many {@code t} values along the same arc to avoid recomputing them
     * @return The interpolated vector (same as out)
     */
    public static double[] slerp(double[] out, double[] A, double[] B, double t, SlerpContext ctx) {
        final double gamma = ctx != null ? ctx.gamma() : Vec3.angle(A, B);
        if (gamma < 1e-12) {
            return Vec3.lerp(out, A, B, t);
        }
        final double sinGamma = ctx != null ? ctx.sinGamma() : Math.sin(gamma);
        final double weightA = Math.sin((1 - t) * gamma) / sinGamma;
        final double weightB = Math.sin(t * gamma) / sinGamma;
        out[0] = weightA * A[0] + weightB * B[0];
        out[1] = weightA * A[1] + weightB * B[1];
        out[2] = weightA * A[2] + weightB * B[2];
        return out;
    }
}
