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

import org.geotoolkit.dggs.a5.internal.core.Origin;
import org.geotoolkit.dggs.a5.internal.math.Vec2;
import org.geotoolkit.dggs.a5.internal.math.Vec3;
import static org.geotoolkit.dggs.a5.internal.core.Constants.PI_OVER_5;
import static org.geotoolkit.dggs.a5.internal.core.Constants.TWO_PI_OVER_5;
import static org.geotoolkit.dggs.a5.internal.core.Constants.distanceToEdge;
import static org.geotoolkit.dggs.a5.internal.core.Constants.interhedralAngle;
import static org.geotoolkit.dggs.a5.internal.core.CoordinateTransforms.toCartesian;
import static org.geotoolkit.dggs.a5.internal.core.CoordinateTransforms.toFace;
import static org.geotoolkit.dggs.a5.internal.core.CoordinateTransforms.toPolar;
import static org.geotoolkit.dggs.a5.internal.core.CoordinateTransforms.toSpherical;
import static org.geotoolkit.dggs.a5.internal.core.Origins.origins;
import static org.geotoolkit.dggs.a5.internal.core.Tiling.getQuintantVertices;

/**
 * Dodecahedron projection: between spherical coordinates and the face coordinates of one origin.
 *
 * <p>Port of upstream {@code projections/dodecahedron.ts}.</p>
 *
 * <p><b>Thread safety:</b> upstream instances are shared singletons (in cell, cap, line,
 * triple-cells modules) filling their face and spherical triangle caches lazily on demand.
 * Here both caches are filled eagerly in the constructor (30 face triangles, 240 spherical
 * triangles, the same values the lazy fill produces) and never modified afterwards: instances
 * are immutable and safe to share between threads. The module-level {@code crs} is a
 * static final instance.</p>
 *
 * <p>The TS types {@code FaceTriangleIndex} (0-9) and {@code OriginId} (0-11) are {@code int}.</p>
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public class DodecahedronProjection {

    private static final CRS crs = new CRS();

    /**
     * Face triangles (FaceTriangle: three Face [x, y]), indexed by face triangle index
     * (+10 when reflected, +20 when reflected and squashed). Shared, never modified.
     */
    private final double[][][] faceTriangles = new double[30][][];
    /**
     * Spherical triangles (three Cartesian [x, y, z]), indexed by {@code 10 * originId + faceTriangleIndex}
     * (+120 when reflected). Shared, never modified.
     */
    private final double[][][] sphericalTriangles = new double[240][][];
    private final EqualAreaProjection equalArea;
    private final GnomonicProjection gnomonic;

    public DodecahedronProjection() {
        this.equalArea = new EqualAreaProjection(crs.getCanonicalTriangle());
        this.gnomonic = new GnomonicProjection();
        // Thread safety: eager fill of the upstream lazy caches. Face triangles first,
        // the spherical triangles are built from the (reflected squashed) face triangles.
        for (int faceTriangleIndex = 0; faceTriangleIndex < 10; faceTriangleIndex++) {
            this.faceTriangles[faceTriangleIndex] = this.getFaceTriangleInternal(faceTriangleIndex);
            this.faceTriangles[faceTriangleIndex + 10] = this.getReflectedFaceTriangle(faceTriangleIndex, false);
            this.faceTriangles[faceTriangleIndex + 20] = this.getReflectedFaceTriangle(faceTriangleIndex, true);
        }
        for (int originId = 0; originId < 12; originId++) {
            for (int faceTriangleIndex = 0; faceTriangleIndex < 10; faceTriangleIndex++) {
                final int index = 10 * originId + faceTriangleIndex; // 0-119
                this.sphericalTriangles[index] = this.getSphericalTriangleInternal(faceTriangleIndex, originId, false);
                this.sphericalTriangles[index + 120] = this.getSphericalTriangleInternal(faceTriangleIndex, originId, true);
            }
        }
    }

    /**
     * Projects spherical coordinates to face coordinates using dodecahedron projection
     * @param spherical Spherical coordinates [theta, phi]
     * @param originId Origin ID
     * @return Face coordinates [x, y]
     */
    public double[] forward(double[] spherical, int originId) {
        return this.forwardCartesian(toCartesian(spherical), originId);
    }

    /**
     * Same as {@code forward} but takes a Cartesian unit vector — skips the
     * {@code toCartesian} round-trip when the caller already has the Cartesian
     * form.
     * @param unprojected Cartesian [x, y, z] unit vector
     * @param originId Origin ID
     * @return Face coordinates [x, y]
     */
    public double[] forwardCartesian(double[] unprojected, int originId) {
        final Origin origin = origins.get(originId);

        // Transform back origin space
        final double[] out = Vec3.create();
        Vec3.transformQuat(out, unprojected, origin.inverseQuat());

        // Unproject gnomonically to polar coordinates in origin space
        final double[] projectedSpherical = toSpherical(out);
        final double[] polar = this.gnomonic.forward(projectedSpherical);

        // Rotate around face axis to remove origin rotation
        polar[1] = (polar[1] - origin.angle());
        final int faceTriangleIndex = this.getFaceTriangleIndex(polar);

        final boolean reflect = this.shouldReflect(polar);
        final double[][] faceTriangle = this.getFaceTriangle(faceTriangleIndex, reflect, false);
        final double[][] sphericalTriangle = this.getSphericalTriangle(faceTriangleIndex, originId, reflect);
        return this.equalArea.forward(unprojected, sphericalTriangle, faceTriangle);
    }

    /**
     * Unprojects face coordinates to spherical coordinates using dodecahedron projection
     * @param face Face coordinates [x, y]
     * @param originId Origin ID
     * @return Spherical coordinates [theta, phi]
     */
    public double[] inverse(double[] face, int originId) {
        final double[] polar = toPolar(face);
        final int faceTriangleIndex = this.getFaceTriangleIndex(polar);

        final boolean reflect = this.shouldReflect(polar);
        final double[][] faceTriangle = this.getFaceTriangle(faceTriangleIndex, reflect, false);
        final double[][] sphericalTriangle = this.getSphericalTriangle(faceTriangleIndex, originId, reflect);
        final double[] unprojected = this.equalArea.inverse(face, faceTriangle, sphericalTriangle);
        return toSpherical(unprojected);
    }

    /**
     * Detects when point is beyond the edge of the dodecahedron face
     * In the standard case (reflect = false), the face and spherical triangle can be
     * used directly.
     * In the reflected case (reflect = true), the point is beyond the edge of the dodecahedron face,
     * and so the face triangle is squashed to unproject correctly onto the neighboring dodecahedron face.
     * @param polar Polar coordinates
     * @return True if point is beyond the edge of the dodecahedron face
     */
    private boolean shouldReflect(double[] polar) {
        final double rho = polar[0], gamma = polar[1];
        final double D = toFace(new double[]{rho, this.normalizeGamma(gamma)})[0];
        return D > distanceToEdge;
    }

    /**
     * Given a polar coordinate, returns the index of the face triangle it belongs to
     * @param polar Polar coordinates
     * @return Face triangle index, value from 0 to 9
     */
    private int getFaceTriangleIndex(double[] polar) {
        final double gamma = polar[1];
        return (int) ((Math.floor(gamma / PI_OVER_5) + 10) % 10);
    }

    /**
     * Gets the face triangle for a given polar coordinate
     * @param faceTriangleIndex Face triangle index, value from 0 to 9
     * @param reflected default false
     * @param squashed default false
     * @return FaceTriangle: 3 vertices in counter-clockwise order (shared, do not modify)
     */
    private double[][] getFaceTriangle(int faceTriangleIndex, boolean reflected, boolean squashed) {
        int index = faceTriangleIndex;
        if (reflected) {
            index += squashed ? 20 : 10;
        }
        // Precomputed in the constructor (upstream: lazily cached)
        return this.faceTriangles[index];
    }

    private double[][] getFaceTriangleInternal(int faceTriangleIndex) {
        final int quintant = ((faceTriangleIndex + 1) / 2) % 5;

        final double[][] quintantVertices = getQuintantVertices(quintant).getVertices();
        final double[] vCenter = quintantVertices[0], vCorner1 = quintantVertices[1], vCorner2 = quintantVertices[2];
        //const vVertex = [distanceToEdge, distanceToEdge] as Face;
        final double[] vEdgeMidpoint = Vec2.create();
        Vec2.lerp(vEdgeMidpoint, vCorner1, vCorner2, 0.5);

        // Sign of gamma determines which triangle we want to use, and thus vertex order
        final boolean even = faceTriangleIndex % 2 == 0;

        // Note: center & midpoint compared to DGGAL implementation are swapped
        // as we are using a dodecahedron, rather than a icosahedron.
        return even ? new double[][]{vCenter, vEdgeMidpoint, vCorner1} : new double[][]{vCenter, vCorner2, vEdgeMidpoint};
    }

    private double[][] getReflectedFaceTriangle(int faceTriangleIndex, boolean squashed) {
        // First obtain ordinary unreflected triangle
        final double[][] triangle = this.getFaceTriangleInternal(faceTriangleIndex);
        final double[] A = Vec2.clone(triangle[0]), B = Vec2.clone(triangle[1]), C = Vec2.clone(triangle[2]);

        // Reflect dodecahedron center (A) across edge (BC)
        final boolean even = faceTriangleIndex % 2 == 0;
        Vec2.negate(A, A);
        final double[] midpoint = even ? B : C;

        // Squashing is important. A squashed triangle when unprojected will yield the correct spherical triangle.
        Vec2.scaleAndAdd(A, A, midpoint, squashed ? 1 + 1 / Math.cos(interhedralAngle) : 2);

        // Swap midpoint and corner to maintain correct vertex order
        return new double[][]{A, C, B};
    }

    /**
     * Gets the spherical triangle for a given face triangle index and origin.
     * Package-private for tests (upstream tests call the private method).
     * @param faceTriangleIndex Face triangle index
     * @param originId Origin ID
     * @param reflected default false
     * @return Spherical triangle, three Cartesian [x, y, z] (shared, do not modify)
     */
    double[][] getSphericalTriangle(int faceTriangleIndex, int originId, boolean reflected) {
        int index = 10 * originId + faceTriangleIndex; // 0-119
        if (reflected) {
            index += 120;
        }
        // Precomputed in the constructor (upstream: lazily cached)
        return this.sphericalTriangles[index];
    }

    private double[][] getSphericalTriangleInternal(int faceTriangleIndex, int originId, boolean reflected) {
        final Origin origin = origins.get(originId);
        final double[][] faceTriangle = this.getFaceTriangle(faceTriangleIndex, reflected, true);

        final double[][] sphericalTriangle = new double[faceTriangle.length][];
        for (int i = 0; i < faceTriangle.length; i++) {
            final double[] polar = toPolar(faceTriangle[i]);
            final double rho = polar[0], gamma = polar[1];
            final double[] rotatedPolar = {rho, gamma + origin.angle()};
            final double[] rotated = toCartesian(this.gnomonic.inverse(rotatedPolar));
            Vec3.transformQuat(rotated, rotated, origin.quat());
            sphericalTriangle[i] = crs.getVertex(rotated);
        }
        return sphericalTriangle;
    }

    /**
     * Normalizes gamma to the range [-PI_OVER_5, PI_OVER_5]
     * @param gamma The gamma value to normalize, in radians
     * @return Normalized gamma value, in radians
     */
    public double normalizeGamma(double gamma) {
        final double segment = gamma / TWO_PI_OVER_5;
        final double sCenter = Math.round(segment);
        final double sOffset = segment - sCenter;

        // Azimuthal angle from triangle bisector
        final double beta = sOffset * TWO_PI_OVER_5;
        return beta;
    }
}
