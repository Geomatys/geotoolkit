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

import com.fasterxml.jackson.databind.JsonNode;
import org.geotoolkit.dggs.a5.internal.Fixtures;
import static org.geotoolkit.dggs.a5.internal.Fixtures.assertCloseToArray;
import static org.geotoolkit.dggs.a5.internal.Fixtures.doubles;
import static org.geotoolkit.dggs.a5.internal.core.Cell.cellToBoundary;
import static org.geotoolkit.dggs.a5.internal.core.Hex.hexToU64;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

/**
 * Port of upstream {@code tests/integration.test.ts}.
 *
 * <p>Only {@code wireframe-0/1/2.json} and {@code wireframe-auto-edges-0.json} were copied into
 * the test resources: the other upstream fixtures ({@code wireframe-3.json},
 * {@code wireframe-auto-edges-1/2/3.json}) are large and were intentionally not copied, so their
 * test cases (upstream indices 3, 5, 6, 7) are not ported.</p>
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public class IntegrationTest {

    // Helper to compare two arrays of coordinates with floating point values
    private static void compareCoordinates(double[][] actual, JsonNode expected, String message) {
        final int precision = 6;
        assertEquals(expected.size(), actual.length, message);
        for (int i = 0; i < actual.length; i++) {
            assertCloseToArray(doubles(expected.get(i)), actual[i], precision);
        }
    }

    /**
     * @param file fixture file
     * @param segments 1 for the first 4 upstream fixtures, 'auto' for the others
     */
    private static void matchesGeneratedBoundaries(String file, int segments) {
        final JsonNode geojson = Fixtures.load(file);

        // Check each cell in the GeoJSON
        for (final JsonNode feature : geojson.get("features")) {
            final String cellIdHex = feature.get("properties").get("cellIdHex").asText();
            final JsonNode expectedBoundary = feature.get("geometry").get("coordinates").get(0);

            // Get the boundary from cellToBoundary
            final long cellId = hexToU64(cellIdHex);
            final double[][] actualBoundary = cellToBoundary(cellId, new Cell.CellToBoundaryOptions(true, segments));

            // Compare the boundaries
            compareCoordinates(actualBoundary, expectedBoundary, "cell " + cellIdHex);
        }
    }

    // ---- describe: wireframe integration tests ----
    @Test
    void matches_generated_boundaries_0() {
        matchesGeneratedBoundaries("integration/wireframe-0.json", 1);
    }

    @Test
    void matches_generated_boundaries_1() {
        matchesGeneratedBoundaries("integration/wireframe-1.json", 1);
    }

    @Test
    void matches_generated_boundaries_2() {
        matchesGeneratedBoundaries("integration/wireframe-2.json", 1);
    }

    // matches_generated_boundaries_3 (wireframe-3.json): fixture not copied

    @Test
    void matches_generated_boundaries_4() {
        matchesGeneratedBoundaries("integration/wireframe-auto-edges-0.json", Cell.CellToBoundaryOptions.AUTO);
    }

    // matches_generated_boundaries_5..7 (wireframe-auto-edges-1/2/3.json): fixtures not copied
}
