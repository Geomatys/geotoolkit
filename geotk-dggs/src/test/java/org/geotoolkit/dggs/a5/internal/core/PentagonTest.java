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

import static org.geotoolkit.dggs.a5.internal.Fixtures.assertCloseToArray;
import static org.geotoolkit.dggs.a5.internal.core.Pentagon.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

/**
 * Port of upstream {@code tests/pentagon.test.ts}.
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public class PentagonTest {

    // ---- describe: PentagonAngles ----
    @Test
    void has_correct_angle_values() {
        assertEquals(72, A);
        assertEquals(127.94543761193603, B);
        assertEquals(108, C);
        assertEquals(82.29202980963508, D);
        assertEquals(149.7625318412527, E);
    }

    // ---- describe: PentagonVertices ----
    @Test
    void pentagon_vertices_has_correct_vertex_coordinates() {
        // Vertex a
        assertEquals(0, a[0]);
        assertEquals(0, a[1]);

        // Vertex b
        assertEquals(0.1993818474311588, b[0]);
        assertEquals(0.3754138223914238, b[1]);

        // Vertex c
        assertEquals(0.6180339887498949, c[0]);
        assertEquals(0.4490279765795854, c[1]);

        // Vertex d
        assertEquals(0.8174158361810537, d[0]);
        assertEquals(0.0736141541881617, d[1]);

        // Vertex e
        assertEquals(0.418652141318736, e[0]);
        assertEquals(-0.07361415418816161, e[1]);
    }

    // ---- describe: PentagonShape ----
    @Test
    void pentagon_shape_has_correct_vertices() {
        final double[][] expected = {
            {0, 0},
            {0.1993818474311588, 0.3754138223914238},
            {0.6180339887498949, 0.4490279765795854},
            {0.8174158361810537, 0.0736141541881617},
            {0.418652141318736, -0.07361415418816161}
        };

        final double[][] vertices = PENTAGON.getVertices();
        for (int i = 0; i < vertices.length; i++) {
            assertEquals(expected[i][0], vertices[i][0]);
            assertEquals(expected[i][1], vertices[i][1]);
        }
    }

    // ---- describe: TriangleVertices ----
    @Test
    void triangle_vertices_has_correct_vertex_coordinates() {
        // Vertex u
        assertEquals(0, u[0]);
        assertEquals(0, u[1]);

        // Vertex v
        assertEquals(0.6180339887498949, v[0]);
        assertEquals(0.4490279765795854, v[1]);

        // Vertex w
        assertEquals(0.6180339887498949, w[0]);
        assertEquals(-0.4490279765795854, w[1]);

        // Angle V
        assertEquals(0.6283185307179586, V);
    }

    // ---- describe: TriangleShape ----
    @Test
    void triangle_shape_has_correct_vertices() {
        final double[][] expected = {
            {0, 0},
            {0.6180339887498949, 0.4490279765795854},
            {0.6180339887498949, -0.4490279765795854}
        };

        final double[][] vertices = TRIANGLE.getVertices();
        for (int i = 0; i < vertices.length; i++) {
            assertEquals(expected[i][0], vertices[i][0]);
            assertEquals(expected[i][1], vertices[i][1]);
        }
    }

    // ---- describe: BasisMatrices ----
    @Test
    void has_correct_basis_and_inverse() {
        final double[] expectedBasis = {0.6180339887498949, 0.4490279765795854, 0.6180339887498949, -0.4490279765795854};

        final double[] expectedInverse = {0.8090169943749475, 0.8090169943749475, 1.1135163644116068, -1.1135163644116068};

        for (int i = 0; i < BASIS.length; i++) {
            assertEquals(expectedBasis[i], BASIS[i]);
        }

        for (int i = 0; i < BASIS_INVERSE.length; i++) {
            assertEquals(expectedInverse[i], BASIS_INVERSE[i]);
        }

        // Verify BASIS * BASIS_INVERSE = Identity (inline column-major 2x2 multiply)
        final double a0 = BASIS[0], a1 = BASIS[1], a2 = BASIS[2], a3 = BASIS[3];
        final double b0 = BASIS_INVERSE[0], b1 = BASIS_INVERSE[1], b2 = BASIS_INVERSE[2], b3 = BASIS_INVERSE[3];
        final double[] product = {a0 * b0 + a2 * b1, a1 * b0 + a3 * b1, a0 * b2 + a2 * b3, a1 * b2 + a3 * b3};
        assertCloseToArray(new double[]{1, 0, 0, 1}, product, 10);
    }
}
