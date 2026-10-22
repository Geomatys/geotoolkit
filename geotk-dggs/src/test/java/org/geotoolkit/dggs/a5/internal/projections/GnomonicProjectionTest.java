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
import static org.geotoolkit.dggs.a5.internal.Fixtures.assertCloseToArray;
import static org.geotoolkit.dggs.a5.internal.Fixtures.doubles;
import org.junit.jupiter.api.Test;

/**
 * Port of upstream {@code tests/projections/gnomonic.test.ts}.
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public class GnomonicProjectionTest {

    private static final JsonNode TEST_DATA = Fixtures.load("projections/fixtures/gnomonic.json");

    private static final GnomonicProjection gnomonic = new GnomonicProjection();

    // ---- describe: GnomonicProjectionForward ----
    @Test
    void forward_projections() {
        for (JsonNode testCase : TEST_DATA.get("forward")) {
            final double[] result = gnomonic.forward(doubles(testCase.get("input")));
            assertCloseToArray(doubles(testCase.get("expected")), result);
        }
    }

    @Test
    void round_trip_forward_projections() {
        for (JsonNode testCase : TEST_DATA.get("forward")) {
            final double[] spherical = doubles(testCase.get("input"));
            final double[] polar = gnomonic.forward(spherical);
            final double[] result = gnomonic.inverse(polar);
            assertCloseToArray(spherical, result);
        }
    }

    // ---- describe: GnomonicProjectionInverse ----
    @Test
    void inverse_projections() {
        for (JsonNode testCase : TEST_DATA.get("inverse")) {
            final double[] result = gnomonic.inverse(doubles(testCase.get("input")));
            assertCloseToArray(doubles(testCase.get("expected")), result);
        }
    }

    @Test
    void round_trip_inverse_projections() {
        for (JsonNode testCase : TEST_DATA.get("inverse")) {
            final double[] polar = doubles(testCase.get("input"));
            final double[] spherical = gnomonic.inverse(polar);
            final double[] result = gnomonic.forward(spherical);
            assertCloseToArray(polar, result);
        }
    }
}
