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
import java.math.BigInteger;
import org.geotoolkit.dggs.a5.internal.Fixtures;
import static org.geotoolkit.dggs.a5.internal.core.CellInfo.cellArea;
import static org.geotoolkit.dggs.a5.internal.core.CellInfo.cellEdgeLengthAvg;
import static org.geotoolkit.dggs.a5.internal.core.CellInfo.getNumCells;
import static org.geotoolkit.dggs.a5.internal.core.CellInfo.getNumChildren;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/**
 * Port of upstream {@code tests/cell-info.test.ts}.
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public class CellInfoTest {

    private static final JsonNode cellInfoFixtures = Fixtures.load("fixtures/cell-info.json");
    private static final JsonNode serializationFixtures = Fixtures.load("fixtures/serialization.json");

    // ---- describe: getNumCells ----
    @Test
    void returns_correct_number_of_cells_for_all_resolutions() {
        for (final JsonNode fixture : cellInfoFixtures.get("numCells")) {
            final int resolution = fixture.get("resolution").asInt();
            assertEquals(fixture.get("count").asDouble(), getNumCells(resolution));
            assertEquals(fixture.get("countBigInt").asText(), getNumCells(BigInteger.valueOf(resolution)).toString());
        }
    }

    // ---- describe: getNumChildren ----
    @Test
    void returns_correct_number_of_children_for_parent_child_resolution_pairs() {
        for (final JsonNode fixture : cellInfoFixtures.get("numChildren")) {
            final int parent = fixture.get("parentResolution").asInt();
            final int child = fixture.get("childResolution").asInt();
            assertEquals(fixture.get("numChildren").asDouble(), getNumChildren(parent, child));
            // Java only: the bigint overload agrees
            assertEquals(fixture.get("numChildren").bigIntegerValue(),
                    getNumChildren(BigInteger.valueOf(parent), BigInteger.valueOf(child)));
        }
    }

    // ---- describe: cellArea ----
    @Test
    void returns_correct_area_for_all_resolutions() {
        for (final JsonNode fixture : cellInfoFixtures.get("cellArea")) {
            assertEquals(fixture.get("areaM2").asDouble(), cellArea(fixture.get("resolution").asInt()));
        }
    }

    // ---- describe: cellEdgeLengthAvg ----
    @Test
    void returns_correct_edge_length_for_all_resolutions() {
        for (final JsonNode fixture : cellInfoFixtures.get("cellEdgeLengthAvg")) {
            assertEquals(fixture.get("lengthM").asDouble(), cellEdgeLengthAvg(fixture.get("resolution").asInt()));
        }
    }

    @Test
    void every_boundary_edge_of_the_test_cells_is_within_10_percent_of_the_average() {
        final double DEG = Math.PI / 180;
        final java.util.function.BiFunction<double[], double[], Double> geodesic = (a, b) -> {
            final double lat1 = a[1] * DEG, lat2 = b[1] * DEG;
            final double h = Math.pow(Math.sin((lat2 - lat1) / 2), 2)
                    + Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin(((b[0] - a[0]) * DEG) / 2), 2);
            return 2 * Constants.AUTHALIC_RADIUS_EARTH * Math.asin(Math.sqrt(h));
        };

        // Sample each edge with multiple segments to measure its true curved length
        final int SEGMENTS = 10;
        for (final JsonNode hexNode : serializationFixtures.get("testIds")) {
            final String hex = hexNode.asText();
            final long cell = Hex.hexToU64(hex);
            final int resolution = Serialization.getResolution(cell);
            final double avg = cellEdgeLengthAvg(resolution);
            final double[][] boundary = Cell.cellToBoundary(cell, new Cell.CellToBoundaryOptions(true, SEGMENTS));
            final int numEdges = (boundary.length - 1) / SEGMENTS;
            for (int e = 0; e < numEdges; e++) {
                double length = 0;
                for (int i = 0; i < SEGMENTS; i++) {
                    final int idx = e * SEGMENTS + i;
                    length += geodesic.apply(boundary[idx], boundary[idx + 1]);
                }
                final double ratio = length / avg;
                final String msg = "cell " + hex + " edge " + e + " ratio " + ratio;
                assertTrue(ratio > 0.9, msg);
                assertTrue(ratio < 1.1, msg);
            }
        }
    }
}
