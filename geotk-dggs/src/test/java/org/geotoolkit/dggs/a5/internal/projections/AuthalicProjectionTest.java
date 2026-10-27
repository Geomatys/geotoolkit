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

import com.fasterxml.jackson.databind.JsonNode;
import org.geotoolkit.dggs.a5.internal.Fixtures;
import static org.geotoolkit.dggs.a5.internal.Fixtures.assertCloseTo;
import org.junit.jupiter.api.Test;

/**
 * Port of upstream {@code tests/projections/authalic.test.ts}.
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public class AuthalicProjectionTest {

    private static final JsonNode TEST_DATA = Fixtures.load("projections/fixtures/authalic.json");

    private static final AuthalicProjection authalic = new AuthalicProjection();

    // ---- describe: AuthalicProjectionForward ----
    @Test
    void forward_projections() {
        for (JsonNode testCase : TEST_DATA.get("forward")) {
            final double result = authalic.forward(testCase.get("input").asDouble());
            assertCloseTo(testCase.get("expected").asDouble(), result, 10);
        }
    }

    @Test
    void round_trip_forward_projections() {
        for (JsonNode testCase : TEST_DATA.get("forward")) {
            final double input = testCase.get("input").asDouble();
            final double polar = authalic.forward(input);
            final double result = authalic.inverse(polar);
            assertCloseTo(input, result, 15);
        }
    }

    // ---- describe: AuthalicProjectionInverse ----
    @Test
    void inverse_projections() {
        for (JsonNode testCase : TEST_DATA.get("inverse")) {
            final double result = authalic.inverse(testCase.get("input").asDouble());
            assertCloseTo(testCase.get("expected").asDouble(), result, 10);
        }
    }

    @Test
    void round_trip_inverse_projections() {
        for (JsonNode testCase : TEST_DATA.get("inverse")) {
            final double input = testCase.get("input").asDouble();
            final double spherical = authalic.inverse(input);
            final double result = authalic.forward(spherical);
            assertCloseTo(input, result, 15);
        }
    }
}
