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
import static org.geotoolkit.dggs.a5.internal.Fixtures.assertCloseToArray;
import static org.geotoolkit.dggs.a5.internal.utils.Vector.slerp;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/**
 * Port of upstream {@code tests/utils/vector.test.ts}.
 * The gl-matrix {@code vec3.create()} used upstream is replaced by {@link Vec3#create()}.
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public class VectorTest {

    // ---- describe: Slerp ----

    @Test
    void interpolates_between_two_vectors() {
        final double[] A = {1, 0, 0};
        final double[] B = {0, 1, 0};
        final double[] out = Vec3.create();

        final double[] result = slerp(out, A, B, 0.5);

        assertSame(out, result);
        assertCloseToArray(new double[]{1 / Math.sqrt(2), 1 / Math.sqrt(2), 0}, result, 6);
    }

    @Test
    void returns_first_vector_at_t_0() {
        final double[] A = {1, 0, 0};
        final double[] B = {0, 1, 0};
        final double[] out = Vec3.create();

        final double[] result = slerp(out, A, B, 0);

        assertSame(out, result);
        assertCloseToArray(new double[]{1, 0, 0}, result, 6);
    }

    @Test
    void returns_second_vector_at_t_1() {
        final double[] A = {1, 0, 0};
        final double[] B = {0, 1, 0};
        final double[] out = Vec3.create();

        final double[] result = slerp(out, A, B, 1);

        assertSame(out, result);
        assertCloseToArray(new double[]{0, 1, 0}, result, 6);
    }

    @Test
    void handles_identical_vectors() {
        final double[] A = {1, 0, 0};
        final double[] B = {1, 0, 0};
        final double[] out = Vec3.create();

        final double[] result = slerp(out, A, B, 0.5);

        assertSame(out, result);
        // For identical vectors, slerp should return the same vector
        // Note: The current implementation may have issues with identical vectors
        // due to division by zero in the angle calculation
        assertCloseToArray(new double[]{1, 0, 0}, result, 6);
    }

    @Test
    void interpolates_at_different_t_values() {
        final double[] A = {1, 0, 0};
        final double[] B = {0, 1, 0};
        final double[] out1 = Vec3.create();
        final double[] out2 = Vec3.create();

        final double[] result1 = slerp(out1, A, B, 0.25);
        final double[] result2 = slerp(out2, A, B, 0.75);

        // At 0.25, should be closer to A (larger x component)
        assertTrue(result1[0] > result1[1]);

        // At 0.75, should be closer to B (larger y component)
        assertTrue(result2[1] > result2[0]);
    }
}
