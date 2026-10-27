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

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntPredicate;
import org.geotoolkit.dggs.a5.internal.lattice.Triple;
import static org.geotoolkit.dggs.a5.internal.core.Constants.TWO_PI_OVER_5;
import static org.geotoolkit.dggs.a5.internal.core.Pentagon.BASIS;

/**
 * Adjacency of the dodecahedron faces.
 *
 * <p>Port of upstream {@code core/face-adjacency.ts}. Stateless, thread-safe.</p>
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public final class FaceAdjacency {

    // Computed empirically from cell boundary vertex sharing at resolution 4.
    // TODO: generate from configuration in origin.ts

    /**
     * Face adjacency table: FACE_ADJACENCY[originId][quintant] = [adjOriginId, adjQuintant]
     *
     * For each quintant on each face, this gives the primary adjacent face/quintant
     * that shares the base edge.
     *
     * (Java: shared array, never modify it.)
     */
    public static final int[][][] FACE_ADJACENCY = {
        {{1, 2}, {4, 3}, {5, 4}, {6, 0}, {11, 1}}, // origin 0
        {{2, 3}, {4, 4}, {0, 0}, {11, 0}, {10, 1}}, // origin 1
        {{9, 2}, {3, 0}, {4, 0}, {1, 0}, {10, 0}}, // origin 2
        {{2, 1}, {9, 1}, {8, 1}, {5, 1}, {4, 1}}, // origin 3
        {{2, 2}, {3, 4}, {5, 0}, {0, 1}, {1, 1}}, // origin 4
        {{4, 2}, {3, 3}, {8, 0}, {6, 1}, {0, 2}}, // origin 5
        {{0, 3}, {5, 3}, {8, 4}, {7, 1}, {11, 2}}, // origin 6
        {{11, 3}, {6, 3}, {8, 3}, {9, 4}, {10, 3}}, // origin 7
        {{5, 2}, {3, 2}, {9, 0}, {7, 2}, {6, 2}}, // origin 8
        {{8, 2}, {3, 1}, {2, 0}, {10, 4}, {7, 3}}, // origin 9
        {{2, 4}, {1, 4}, {11, 4}, {7, 4}, {9, 3}}, // origin 10
        {{1, 3}, {0, 4}, {6, 4}, {7, 0}, {10, 2}} // origin 11
    };

    private FaceAdjacency() {
    }

    // The seam across a base edge. Unfolded about the edge, quintant q of a face
    // and quintant FACE_ADJACENCY[originId][q] of its neighbor continue one
    // lattice: in each quintant's own lattice coordinates (IJ, in face units) the
    // point ij of one is the point (1, 1) - ij of the other, a half-turn about the
    // edge's midpoint. `seamTransform` and `seamTriple` are this map in face
    // coordinates and on cells.

    /**
     * The map from a face's frame into the frame of its neighbor across the base
     * edge of {@code quintant}, as [a, b, c, d, tx, ty] taking (x, y) to
     * (a x + c y + tx, b x + d y + ty). With R(k) the rotation of quintant k into
     * place and q' the neighbor's quintant, it is p -&gt; R(q') (BASIS (1, 1) - R(-q) p).
     *
     * @param originId face id (0-11)
     * @param quintant quintant (0-4)
     * @return Mat2d [a, b, c, d, tx, ty]
     */
    public static double[] seamTransform(int originId, int quintant) {
        final double angle = TWO_PI_OVER_5 * FACE_ADJACENCY[originId][quintant][1];
        final double cos = Math.cos(angle);
        final double sin = Math.sin(angle);
        // The linear part is R(q') R(-q) negated: a half-turn plus the change of quintant
        final double turn = angle - TWO_PI_OVER_5 * quintant;
        final double ex = BASIS[0] + BASIS[2];
        final double ey = BASIS[1] + BASIS[3];
        return new double[]{
            -Math.cos(turn),
            -Math.sin(turn),
            Math.sin(turn),
            -Math.cos(turn),
            cos * ex - sin * ey,
            sin * ex + cos * ey
        };
    }

    /**
     * A cell's image across the seam at its quintant's base edge ({@code maxRow} is its
     * own), in the neighbor quintant's triples. The half-turn maps the lattice to
     * itself, so the image is a cell: its pentagon is the cell's own turned
     * half-way round, which flips the flavor's parity bit. It lies just outside
     * the neighbor quintant; its neighbors inside it are the cell's neighbors
     * across the seam.
     *
     * @param t cell triple
     * @param maxRow maximum row of the quintant
     * @return image triple
     */
    public static Triple seamTriple(Triple t, int maxRow) {
        return new Triple(-maxRow - t.x(), 2 * maxRow + 1 - t.y(), -maxRow - t.z());
    }

    /**
     * Breadth-first walk over the 12 dodecahedron faces, without ring limit.
     *
     * @see #walkFaces(int[], IntPredicate, int)
     */
    public static int[] walkFaces(int[] seeds, IntPredicate expand) {
        return walkFaces(seeds, expand, Integer.MAX_VALUE);
    }

    /**
     * Breadth-first walk over the 12 dodecahedron faces (the resolution 0 cells),
     * adjacent across their edges. Starts from {@code seeds}, which are always expanded;
     * every other face is visited once and expanded only if {@code expand(face)} is true.
     * Stops after {@code maxRings} rings.
     *
     * @param seeds starting face ids
     * @param expand whether to expand a reached face
     * @param maxRings maximum number of rings ({@code Integer.MAX_VALUE} for TS Infinity)
     * @return Every face reached, seeds first
     */
    public static int[] walkFaces(int[] seeds, IntPredicate expand, int maxRings) {
        // [...new Set(seeds)]
        final List<Integer> reached = new ArrayList<>();
        for (final int seed : seeds) {
            if (!reached.contains(seed)) reached.add(seed);
        }
        List<Integer> frontier = new ArrayList<>(reached);
        for (int ring = 0; ring < maxRings && frontier.size() > 0; ring++) {
            final List<Integer> next = new ArrayList<>();
            for (final int id : frontier) {
                for (int q = 0; q < 5; q++) {
                    final int face = FACE_ADJACENCY[id][q][0];
                    if (reached.contains(face)) continue;
                    reached.add(face);
                    if (expand.test(face)) next.add(face);
                }
            }
            frontier = next;
        }
        final int[] result = new int[reached.size()];
        for (int i = 0; i < result.length; i++) result[i] = reached.get(i);
        return result;
    }
}
