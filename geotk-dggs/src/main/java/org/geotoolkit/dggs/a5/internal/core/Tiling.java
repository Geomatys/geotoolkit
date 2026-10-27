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
package org.geotoolkit.dggs.a5.internal.core;

import org.geotoolkit.dggs.a5.internal.geometry.PentagonShape;
import org.geotoolkit.dggs.a5.internal.lattice.Triple;
import org.geotoolkit.dggs.a5.internal.math.Mat2;
import org.geotoolkit.dggs.a5.internal.math.Vec2;
import static org.geotoolkit.dggs.a5.internal.core.Constants.TWO_PI_OVER_5;
import static org.geotoolkit.dggs.a5.internal.core.Pentagon.BASIS;
import static org.geotoolkit.dggs.a5.internal.core.Pentagon.PENTAGON;
import static org.geotoolkit.dggs.a5.internal.core.Pentagon.TRIANGLE;
import static org.geotoolkit.dggs.a5.internal.core.Pentagon.v;

/**
 * Pentagons of the cells in face coordinates.
 *
 * <p>Port of upstream {@code core/tiling.ts}. Tables are built in the static initialiser and the
 * module-level scratch vectors ({@code translation}, {@code refIJ}) are locals: thread-safe.</p>
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public final class Tiling {

    private static final boolean TRIANGLE_MODE = false;

    /**
     * Define transforms for each pentagon in the primitive unit
     * Using pentagon vertices and angle as the basis for the transform
     */
    private static final double[][] QUINTANT_ROTATIONS = new double[5][];

    // Center of the base PENTAGON under each flavor's orientation ops. The vertex
    // mean is linear, so an oriented pentagon's center is the transformed base
    // center — no need to construct the five vertices when only the center is
    // wanted (see getPentagonCenter).
    private static final double[][] FLAVOR_CENTERS = new double[4][];

    // The base PENTAGON under each flavor's orientation ops, flattened to
    // [x0,y0,...,x4,y4] for the allocation-free containment test below.
    private static final double[][] FLAVOR_PENTAGONS = new double[4][];

    static {
        for (int quintant = 0; quintant < 5; quintant++) {
            final double[] rotation = Mat2.create();
            Mat2.fromRotation(rotation, TWO_PI_OVER_5 * quintant);
            QUINTANT_ROTATIONS[quintant] = rotation;
        }
        for (int flavor = 0; flavor < 4; flavor++) {
            final PentagonShape p = PENTAGON.clone();
            if ((flavor & 1) != 0) p.rotate180();
            if ((flavor & 2) != 0) p.reflectY();
            FLAVOR_CENTERS[flavor] = p.getCenter();
        }
        for (int flavor = 0; flavor < 4; flavor++) {
            final PentagonShape p = PENTAGON.clone();
            if ((flavor & 1) != 0) p.rotate180();
            if ((flavor & 2) != 0) p.reflectY();
            final double[][] verts = p.getVertices();
            final double[] flat = new double[10];
            for (int i = 0; i < 5; i++) {
                flat[i * 2] = verts[i][0];
                flat[i * 2 + 1] = verts[i][1];
            }
            FLAVOR_PENTAGONS[flavor] = flat;
        }
    }

    private Tiling() {
    }

    /**
     * Signed containment margin of a point in the pentagon of (triple, flavor)
     * (&gt; 0 ⇔ strictly inside; the most-violated-edge cross product otherwise),
     * tested in the SCALED quintant-0 frame (face coords rotated into quintant 0 and scaled
     * by 2^resolution — the frame {@code sphericalToCell} works in). In this frame the
     * cell's pentagon is the flavor-oriented base pentagon translated by
     * BASIS·(x+y, -x+(flavor&amp;1)), so the test needs no curve decode, no
     * re-projection, and — pentagons being unit-size here — stays well-conditioned
     * at every resolution.
     *
     * @param px point x, scaled quintant-0 frame
     * @param py point y, scaled quintant-0 frame
     * @param x triple x
     * @param y triple y
     * @param flavor pentagon flavor (0-3)
     * @return signed margin
     */
    public static double cellMarginScaled(double px, double py, double x, double y, int flavor) {
        final double rx = x + y;
        final double ry = -x + (flavor & 1);
        final double tx = BASIS[0] * rx + BASIS[2] * ry;
        final double ty = BASIS[1] * rx + BASIS[3] * ry;
        final double[] pent = FLAVOR_PENTAGONS[flavor];
        double margin = Double.POSITIVE_INFINITY;
        for (int i = 0; i < 5; i++) {
            final int j = i == 4 ? 0 : i + 1;
            final double v1x = pent[i * 2] + tx;
            final double v1y = pent[i * 2 + 1] + ty;
            final double v2x = pent[j * 2] + tx;
            final double v2y = pent[j * 2 + 1] + ty;
            // (v1 - v2) × (p - v1): < 0 ⇒ strictly outside this edge
            final double cross = (v1x - v2x) * (py - v1y) - (v1y - v2y) * (px - v1x);
            if (cross < margin) margin = cross;
        }
        return margin;
    }

    /**
     * Get pentagon vertices for a cell, with the default (pentagon) mode.
     *
     * @see #getPentagonVertices(int, int, Triple, int, boolean)
     */
    public static PentagonShape getPentagonVertices(int resolution, int quintant, Triple triple, int flavor) {
        return getPentagonVertices(resolution, quintant, triple, flavor, TRIANGLE_MODE);
    }

    /**
     * Get pentagon vertices for a cell.
     *
     * A cell's pentagon is one of exactly FOUR orientations of the base PENTAGON
     * (the Cairo-like metatile): flavor bit 0 is a 180° rotation, bit 1 a Y
     * reflection. The oriented pentagon sits at the triple-derived lattice point
     * ref = (x+y, -x) in IJ, shifted by one j unit for the rotated flavors.
     * The flavor is a 1:1 function of the cell's L-system jigsaw piece and is
     * produced by the descent (sToCell); the placement was derived and verified
     * exhaustively against the pentagon geometry.
     *
     * @param resolution The resolution level
     * @param quintant The quintant index (0-4)
     * @param triple The cell's triple coordinates
     * @param flavor The cell's pentagon flavor (0-3)
     * @param triangleMode use the triangle instead of the pentagon
     * @return A pentagon shape with transformed vertices
     */
    public static PentagonShape getPentagonVertices(int resolution, int quintant, Triple triple, int flavor, boolean triangleMode) {
        final PentagonShape pentagon = (triangleMode ? TRIANGLE : PENTAGON).clone();

        if ((flavor & 1) != 0) pentagon.rotate180();
        if ((flavor & 2) != 0) pentagon.reflectY();

        // Position within quintant: ref(triple), plus (0, 1) for the rotated flavors
        final double[] refIJ = Vec2.create();
        final double[] translation = Vec2.create();
        Vec2.set(refIJ, triple.x() + triple.y(), -triple.x() + (flavor & 1));
        Vec2.transformMat2(translation, refIJ, BASIS);
        pentagon.translate(translation);
        pentagon.scale(1 / Math.pow(2, resolution));
        pentagon.transform(QUINTANT_ROTATIONS[quintant]);

        return pentagon;
    }

    /**
     * The center of a cell's pentagon, without constructing the pentagon.
     *
     * @see #getPentagonCenter(int, int, Triple, int, double[])
     */
    public static double[] getPentagonCenter(int resolution, int quintant, Triple triple, int flavor) {
        return getPentagonCenter(resolution, quintant, triple, flavor, Vec2.create());
    }

    /**
     * The center of a cell's pentagon, without constructing the pentagon —
     * O(1) via the precomputed flavor centers. Equivalent to
     * {@code getPentagonVertices(...).getCenter()} (up to float associativity).
     * Written to {@code out} when given.
     *
     * @param resolution The resolution level
     * @param quintant The quintant index (0-4)
     * @param triple The cell's triple coordinates
     * @param flavor The cell's pentagon flavor (0-3)
     * @param out Face [x, y] receiving the result
     * @return {@code out}
     */
    public static double[] getPentagonCenter(int resolution, int quintant, Triple triple, int flavor, double[] out) {
        final double[] c = FLAVOR_CENTERS[flavor];
        final double[] refIJ = Vec2.create();
        final double[] translation = Vec2.create();
        Vec2.set(refIJ, triple.x() + triple.y(), -triple.x() + (flavor & 1));
        Vec2.transformMat2(translation, refIJ, BASIS);
        Vec2.set(out, (c[0] + translation[0]) / Math.pow(2, resolution), (c[1] + translation[1]) / Math.pow(2, resolution));
        return Vec2.transformMat2(out, out, QUINTANT_ROTATIONS[quintant]);
    }

    // TODO: memoize these two functions?
    /**
     * @param quintant The quintant index (0-4)
     * @return the triangle of the quintant
     */
    public static PentagonShape getQuintantVertices(int quintant) {
        final PentagonShape triangle = TRIANGLE.clone();
        triangle.transform(QUINTANT_ROTATIONS[quintant]);
        return triangle;
    }

    /**
     * @return the pentagon of the face
     */
    public static PentagonShape getFaceVertices() {
        final double[][] vertices = new double[5][];
        for (int i = 0; i < 5; i++) {
            final double[] rotation = QUINTANT_ROTATIONS[i];
            // Need to reverse to obtain correct winding order
            vertices[4 - i] = Vec2.transformMat2(Vec2.create(), v, rotation);
        }
        return new PentagonShape(vertices);
    }

    /**
     * @param polar Polar [rho, gamma]
     * @return quintant (0-4)
     */
    public static int getQuintantPolar(double[] polar) {
        final double gamma = polar[1];
        return (int) ((Math.round(gamma / TWO_PI_OVER_5) + 5) % 5);
    }
}
