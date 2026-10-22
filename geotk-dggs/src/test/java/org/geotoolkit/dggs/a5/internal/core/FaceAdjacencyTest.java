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

import org.geotoolkit.dggs.a5.internal.lattice.Triple;
import org.geotoolkit.dggs.a5.internal.projections.DodecahedronProjection;
import static org.geotoolkit.dggs.a5.internal.core.Constants.TWO_PI_OVER_5;
import static org.geotoolkit.dggs.a5.internal.Fixtures.assertCloseTo;
import static org.geotoolkit.dggs.a5.internal.core.FaceAdjacency.FACE_ADJACENCY;
import static org.geotoolkit.dggs.a5.internal.core.FaceAdjacency.seamTransform;
import static org.geotoolkit.dggs.a5.internal.core.FaceAdjacency.seamTriple;
import static org.geotoolkit.dggs.a5.internal.core.FaceAdjacency.walkFaces;
import static org.geotoolkit.dggs.a5.internal.core.Tiling.getPentagonCenter;
import static org.geotoolkit.dggs.a5.internal.lattice.Triples.tripleFlavor;
import static org.geotoolkit.dggs.a5.internal.lattice.Triples.tripleInBounds;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

/**
 * Port of upstream {@code tests/face-adjacency.test.ts}.
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public class FaceAdjacencyTest {

    private static final DodecahedronProjection dodecahedron = new DodecahedronProjection();

    private static double[] apply(double[] map, double[] p) {
        final double x = p[0], y = p[1];
        return new double[]{
            map[0] * x + map[2] * y + map[4],
            map[1] * x + map[3] * y + map[5]
        };
    }

    // ---- describe: face seam ----
    @Test
    void seamTransform_agrees_with_the_projection_across_every_base_edge() {
        for (int o = 0; o < 12; o++) {
            for (int q = 0; q < 5; q++) {
                final int adjacentId = FACE_ADJACENCY[o][q][0];
                final int adjacentQuintant = FACE_ADJACENCY[o][q][1];
                final double[] map = seamTransform(o, q);
                // Points of the neighbor's quintant near the shared edge, projected into this face
                for (final double[] ra : new double[][]{{0.5, 0}, {0.55, -0.4}, {0.55, 0.4}}) {
                    final double r = ra[0], angle = ra[1];
                    final double gamma = adjacentQuintant * TWO_PI_OVER_5 + angle;
                    final double[] point = {r * Math.cos(gamma), r * Math.sin(gamma)};
                    final double[] landed = dodecahedron.forward(dodecahedron.inverse(point, adjacentId), o);
                    final double[] mapped = apply(map, landed);
                    assertCloseTo(point[0], mapped[0], 10);
                    assertCloseTo(point[1], mapped[1], 10);
                }
            }
        }
    }

    @Test
    void seamTriple_is_seamTransform_on_cells() {
        for (final int curveLevel : new int[]{1, 2, 5}) {
            final int maxRow = (1 << curveLevel) - 1;
            for (int q = 0; q < 5; q++) {
                final double[] map = seamTransform(0, q);
                final int adjacentQuintant = FACE_ADJACENCY[0][q][1];
                // The cells of the two rows along the base edge
                for (int y = Math.max(0, maxRow - 1); y <= maxRow; y++) {
                    for (int x = -y - 1; x <= 0; x++) {
                        for (final int parity : new int[]{0, 1}) {
                            final Triple triple = new Triple(x, y, parity - x - y);
                            if (!tripleInBounds(triple, maxRow)) continue;
                            final int flavor = tripleFlavor(triple, maxRow);
                            final Triple image = seamTriple(triple, maxRow);
                            assertEquals(flavor ^ 1, tripleFlavor(image, maxRow));
                            final double[] center = apply(map, getPentagonCenter(curveLevel, q, triple, flavor));
                            final double[] imageCenter = getPentagonCenter(curveLevel, adjacentQuintant, image, flavor ^ 1);
                            assertCloseTo(imageCenter[0], center[0], 10);
                            assertCloseTo(imageCenter[1], center[1], 10);
                        }
                    }
                }
            }
        }
    }

    // ---- Java only ----
    @Test
    void walkFaces_rings() {
        // Seeds are deduplicated and come first; ring 1 adds the 5 neighbors of face 0
        assertArrayEquals(new int[]{0, 1, 4, 5, 6, 11}, walkFaces(new int[]{0, 0}, face -> true, 1));
        // Without limit every face is reached
        assertEquals(12, walkFaces(new int[]{3}, face -> true).length);
        // Non expanded faces stop the walk
        assertEquals(6, walkFaces(new int[]{0}, face -> false).length);
    }
}
