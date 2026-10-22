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

import com.fasterxml.jackson.databind.JsonNode;
import org.geotoolkit.dggs.a5.internal.Fixtures;
import static org.geotoolkit.dggs.a5.internal.Fixtures.assertCloseTo;
import static org.geotoolkit.dggs.a5.internal.Fixtures.assertCloseToArray;
import static org.geotoolkit.dggs.a5.internal.Fixtures.doubles;
import static org.geotoolkit.dggs.a5.internal.utils.GreatCircle.greatCircleDistance;
import static org.geotoolkit.dggs.a5.internal.utils.GreatCircle.sampleGreatCircleArc;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/**
 * Port of upstream {@code tests/utils/great-circle.test.ts}.
 * Upstream generates one test per fixture entry; here each describe loops over its
 * fixtures, with the fixture name in assertion messages.
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public class GreatCircleTest {

    private static final JsonNode fixtures = Fixtures.load("fixtures/utils/great-circle.json");

    // ---- describe: greatCircleDistance precision ----
    // Near-degenerate cases with analytically-known distances (Kahan §12):
    // acos(a·b) would fail these — it returns 0 for separations below ~1e-8 rad
    // and errs by ~0.1 m near the antipode. The stable 2·atan2 form must match
    // the analytic value to within 1e-10 relative (or 1e-9 m absolute near zero).
    @Test
    void greatCircleDistance_precision() {
        int count = 0;
        for (JsonNode f : fixtures.get("distances")) {
            final String name = f.get("name").asText();
            final double distance = f.get("distance").asDouble();
            final double d = greatCircleDistance(doubles(f.get("aVec")), doubles(f.get("bVec")));
            final double tolerance = Math.max(1e-9, 1e-10 * distance);
            assertTrue(Math.abs(d - distance) <= tolerance, () -> name + ": " + d + " != " + distance);
            count++;
        }
        assertTrue(count > 0);
    }

    // ---- describe: sampleGreatCircleArc ----
    @Test
    void sampleGreatCircleArc_fixtures() {
        int count = 0;
        for (JsonNode f : fixtures.get("sampleGreatCircleArc")) {
            final String name = f.get("name").asText();
            final double[] aVec = doubles(f.get("aVec"));
            final double[] bVec = doubles(f.get("bVec"));

            // Distance check is the cheapest signal that endpoints round-tripped.
            assertCloseTo(f.get("distance").asDouble(), greatCircleDistance(aVec, bVec), 6);

            // Tolerance 6 matches the rest of the vector tests — gl-matrix defaults
            // to Float32 unless a sibling module has called setMatrixArrayType.
            final double[][] samples = sampleGreatCircleArc(aVec, bVec, f.get("sampleInterval").asDouble());
            assertEquals(f.get("sampleCount").asInt(), samples.length, name);
            final JsonNode expected = f.get("samples");
            for (int i = 0; i < samples.length; i++) {
                assertCloseToArray(doubles(expected.get(i)), samples[i], 6);
            }
            count++;
        }
        assertTrue(count > 0);
    }
}
