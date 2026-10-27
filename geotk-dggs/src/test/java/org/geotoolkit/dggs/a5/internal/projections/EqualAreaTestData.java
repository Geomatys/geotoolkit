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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import org.geotoolkit.dggs.a5.internal.math.Vec3;

/**
 * Test data shared by the equal-area projection and coordinate transform tests.
 *
 * <p>Port of upstream {@code tests/projections/data/equal-area-test-data.ts}. The upstream
 * random points use {@code Math.random()}; a fixed seed is used here for reproducible runs.
 * {@code TEST_LINE_POINTS} and {@code TEST_LINE_SPHERICAL} are exported but never defined
 * upstream: they are not ported.</p>
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public final class EqualAreaTestData {

    /** Face [x, y] test points, unmodifiable list (do not modify the arrays). */
    public static final List<double[]> TEST_POINTS;

    /** Spherical triangle, three Cartesian [x, y, z] unit vectors (do not modify). */
    public static final double[][] TEST_SPHERICAL_TRIANGLE;

    /** Face triangle, three Face [x, y] points (do not modify). */
    public static final double[][] TEST_FACE_TRIANGLE = {{0, 0}, {0, 1}, {1, 0}};

    static {
        final List<double[]> points = new ArrayList<>();
        final double[][] base = {
            {0, 0},  // vertex A
            {1, 0},  // vertex B
            {0, 1},  // vertex C
            {0, 0},
            {0, 0.001},
            {0, 0.0001},
            {0, 0.9},
            {0, 0.99},
            {0, 0.999},
            {0, 0.9991},
            {0, 0.9999},
            {0, 0.99999},
            {0, 0.999999},
            {0, 0.9999999},
            {0, 0.99999999},
            {0, 0.999999999},
            {0, 0.9999999999},
            {0, 0.99999999999},
            {0, 0.999999999999},
            {0, 0.9999999999999},
            {0.0, 0.4},
            {0.2, 0.4},
            {0.4, 0.4},
            {0.0, 0.5},
            {0.0, 0.6},
            {0.0, 0.9},
            // Difficult points (thrown up by random testing)
            {0.07014313993250365, 0.9298568600674963},
            {0.9561208684797726, 0.043821975867140296},
            {0.9801671359068279, 0.011065580403455679},
            {0.8565287887089067, 0.14204220534719342},
            {0.9960934042866861, 0.002268926948860536}
        };
        Collections.addAll(points, base);

        // These points break when safeAcos is not used
        final double[][] difficult = {
            {0, 0.1},
            {0, 0.01},
            {0, 0.001},
            {0, 0.0001},
            {0, 0.00001},
            {0, 0.000001},
            {0, 0.0000001},
            {0, 0.00000001},
        };
        Collections.addAll(points, difficult);

        final List<double[]> reversed = new ArrayList<>();
        for (double[] point : points) reversed.add(new double[]{point[1], point[0]});
        points.addAll(reversed);

        final Random random = new Random(5); // upstream: Math.random()
        for (int i = 0; i < 100; i++) {
            final double x = random.nextDouble();
            final double y = (1 - x) * random.nextDouble();
            points.add(new double[]{x, y});
        }
        TEST_POINTS = Collections.unmodifiableList(points);

        // Alternative spherical triangle for testing
        final double φ = (1 + Math.sqrt(5)) / 2;
        final double[] ZEROED_CENTER = {0, 0, 1};
        final double[] ZEROED_VERTEX = {(φ - 1) / Math.cos(Math.PI / 5), φ - 1, 1};
        final double[] ZEROED_EDGE_MIDPOINT = {0, φ - 1, 1};

        TEST_SPHERICAL_TRIANGLE = new double[][]{ZEROED_CENTER, ZEROED_VERTEX, ZEROED_EDGE_MIDPOINT};
        // Project to sphere
        for (double[] p : TEST_SPHERICAL_TRIANGLE) Vec3.normalize(p, p);
        // (The unused DODEC_FACE / DODEC_FACE_CENTER / DODEC_EDGE_MIDPOINT construction of the
        // alternative, commented-out triangle is not ported.)
    }

    private EqualAreaTestData() {
    }
}
