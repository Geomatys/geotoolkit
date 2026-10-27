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
package org.geotoolkit.dggs.a5.internal.projections;

import org.geotoolkit.dggs.a5.internal.math.Mat2d;
import org.geotoolkit.dggs.a5.internal.math.Vec2;
import org.geotoolkit.dggs.a5.internal.math.Vec3;
import static org.geotoolkit.dggs.a5.internal.core.CoordinateTransforms.barycentricToFace;
import static org.geotoolkit.dggs.a5.internal.core.CoordinateTransforms.faceToBarycentric;
import static org.geotoolkit.dggs.a5.internal.geometry.SphericalPolygon.sphericalTriangleArea;

/**
 * Equal area projection originally described by:
 * Snyder92 (AN EQUAL-AREA MAP PROJECTION FOR POLYHEDRAL GLOBES)
 * Closed form equations due to Brenton R. S. Recht
 *
 * <p>The projection maps a point V within a spherical triangle ABC onto a planar
 * point F (within a planar triangle), in an equal-area-preserving manner.</p>
 *
 * <p>The first point of the triangle (A) is known as the radiating vertex and the
 * choice of this vertex subtly modifies how the projection behaves. All three
 * choices will yield an equal area projection, but the cusps will vary.</p>
 *
 * <p>The transformation is done via an intermediate point P, which is obtained by
 * intersecting the two great circles formed by A&amp;V and B&amp;C (hence why A is special)</p>
 *
 * <p>The equal-area transformation is then done by computing the ratio of areas between
 * triangles ABP &amp; ABC</p>
 *
 * <p>The inverse follows the reverse procedure of obtaining P from the face triangle
 * by inverting the equal-area transformation, before slerping between A&amp;P to obtain V</p>
 *
 * <p>Port of upstream {@code projections/equal-area.ts}. The upstream module-scoped scratch
 * buffers ({@code _BxC}, {@code _P}, {@code _csAlpha}, {@code _weightBC}) are locals here:
 * instances are immutable and thread-safe.</p>
 *
 * <p>Spherical triangles are three Cartesian [x, y, z] unit vectors, face triangles three
 * Face [x, y] points.</p>
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public class EqualAreaProjection {

    /**
     * Constants of a spherical triangle, see {@link #computeConstants(double[][])}.
     *
     * @param AdotB A · B — the canonical ("even") B-C orientation
     * @param AdotC A · C — the mirror ("odd") orientation, with B and C swapped
     * @param alphaTransform Affine transform for alpha direction vector to p (Mat2d, do not modify)
     * @param areaABC spherical triangle area
     * @param volumeABC A · (B × C) — signed triple product (volume of parallelpiped formed)
     */
    public record TriangleConstants(
            double AdotB,
            double AdotC,
            double[] alphaTransform,
            double areaABC,
            double volumeABC) {
    }

    // By assuming that the geometry of the spherical triangle used is constant (up to
    // rotations on a sphere), a number of constants can be precomputed and reused
    private final TriangleConstants constants;

    /**
     * @param canonicalTriangle Spherical triangle, three Cartesian [x, y, z] unit vectors
     */
    public EqualAreaProjection(double[][] canonicalTriangle) {
        this.constants = EqualAreaProjection.computeConstants(canonicalTriangle);
    }

    /**
     * @param sphericalTriangle Spherical triangle, three Cartesian [x, y, z] unit vectors
     * @return the triangle constants
     */
    public static TriangleConstants computeConstants(double[][] sphericalTriangle) {
        final double[] A = sphericalTriangle[0], B = sphericalTriangle[1], C = sphericalTriangle[2];
        final double[] BxC = Vec3.create();
        Vec3.cross(BxC, B, C);
        final double AdotB = Vec3.dot(A, B);
        final double AdotC = Vec3.dot(A, C);
        final double BdotC = Vec3.dot(B, C);

        final double V = Vec3.dot(A, BxC);
        final double P = AdotC + BdotC;
        final double Q = AdotB + 1;
        final double R = AdotB * BdotC - AdotC;
        final double F = P * P - Q * Q;
        final double G = 2 * Q * R;
        final double[] alphaTransform = Mat2d.fromValues(V * V - F, -G, -2 * V * P, 2 * V * Q, V * V + F, G);

        return new TriangleConstants(AdotB, AdotC, alphaTransform, sphericalTriangleArea(A, B, C), V);
    }

    /**
     * Forward projection: converts a spherical point to face coordinates
     * @param V The spherical point to project, Cartesian [x, y, z]
     * @param sphericalTriangle The spherical triangle vertices
     * @param faceTriangle The face triangle vertices
     * @return The face coordinates [x, y]
     */
    public double[] forward(double[] V, double[][] sphericalTriangle, double[][] faceTriangle) {
        final double[] A = sphericalTriangle[0], B = sphericalTriangle[1], C = sphericalTriangle[2];
        final double areaABC = constants.areaABC(), volumeABC = constants.volumeABC();
        final double[] _BxC = Vec3.create();
        final double[] _P = Vec3.create(); // Point along BC chord

        // Compute point P, where great circles through A&V and B&C intersect
        Vec3.cross(_BxC, B, C);
        final double volumeVBC = Vec3.dot(V, _BxC);
        Vec3.scale(_P, V, volumeABC);
        Vec3.scaleAndAdd(_P, _P, A, -volumeVBC);
        final double D = Vec3.length(_P);
        final double ooD = D > 0 ? 1 / D : 1;
        Vec3.scale(_P, _P, ooD);

        // Obtain rho & alpha by ratio of areas
        final double areaABp = Math.max(0, sphericalTriangleArea(A, B, _P));
        final double alpha = areaABp / areaABC;
        final double rho = (D / volumeABC) * Math.sqrt((1 + Vec3.dot(A, _P)) / (1 + Vec3.dot(A, V)));

        // Construct barycentric triangle and map to face
        final double[] b = {1 - rho, rho * (1 - alpha), rho * alpha};
        return barycentricToFace(b, faceTriangle);
    }

    /**
     * Inverse projection: converts face coordinates back to spherical coordinates
     * @param facePoint The face coordinates [x, y]
     * @param faceTriangle The face triangle vertices
     * @param sphericalTriangle The spherical triangle vertices
     * @return The Cartesian [x, y, z] coordinates. When the point is on a triangle vertex,
     *         the vertex array of {@code sphericalTriangle} itself is returned (as upstream): do not modify.
     */
    public double[] inverse(double[] facePoint, double[][] faceTriangle, double[][] sphericalTriangle) {
        // Map from face to barycentric
        final double[] A = sphericalTriangle[0], B = sphericalTriangle[1], C = sphericalTriangle[2];
        final double[] b = faceToBarycentric(facePoint, faceTriangle);

        final double threshold = 1 - 1e-14;
        if (b[0] > threshold) return A;
        if (b[1] > threshold) return B;
        if (b[2] > threshold) return C;

        // Normalize odd (mirror-image) triangles to the canonical even orientation
        // by swapping B↔C and the matching weight b1↔b2, so alphaTransform is correct
        final double AdotB = constants.AdotB(), AdotC = constants.AdotC(), areaABC = constants.areaABC();
        final double[] alphaTransform = constants.alphaTransform();
        final double faceAdotB = Vec3.dot(A, B);
        final boolean odd = Math.abs(faceAdotB - AdotB) > Math.abs(faceAdotB - AdotC);
        final double[] _B = odd ? C : B;
        final double[] _C = odd ? B : C;
        final double b2 = odd ? b[1] : b[2];

        // Obtain rho & alpha
        final double rho = 1 - b[0];
        final double alpha = (b2 / rho) * areaABC;

        // Inverse to obtain point P (see forward)
        final double[] _csAlpha = Vec2.create(); // [cos(alpha), sin(alpha)] — input to alphaTransform
        final double[] _weightBC = Vec2.create(); // alphaTransform * _csAlpha
        final double[] _P = Vec3.create();
        _csAlpha[0] = Math.cos(alpha);
        _csAlpha[1] = Math.sin(alpha);
        Vec2.transformMat2d(_weightBC, _csAlpha, alphaTransform);
        Vec3.scale(_P, _B, _weightBC[0]);
        Vec3.scaleAndAdd(_P, _P, _C, _weightBC[1]);
        Vec3.normalize(_P, _P);

        // Compute weights for A & P
        final double s = Vec3.dot(A, _P);
        final double t = 1 + rho * rho * (s - 1);
        final double weightP = rho * Math.sqrt((1 + t) / (1 + s));
        final double weightA = t - s * weightP;

        // `out` is returned to the caller, so it must be a fresh allocation
        final double[] out = Vec3.create();
        Vec3.scale(out, A, weightA);
        Vec3.scaleAndAdd(out, out, _P, weightP);
        return out;
    }
}
