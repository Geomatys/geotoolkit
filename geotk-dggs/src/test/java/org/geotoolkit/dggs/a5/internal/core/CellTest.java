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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import org.geotoolkit.dggs.a5.internal.Fixtures;
import static org.geotoolkit.dggs.a5.internal.Fixtures.assertCloseTo;
import static org.geotoolkit.dggs.a5.internal.Fixtures.doubles;
import static org.geotoolkit.dggs.a5.internal.core.Cell.a5cellContainsPoint;
import static org.geotoolkit.dggs.a5.internal.core.Cell.cellToBoundary;
import static org.geotoolkit.dggs.a5.internal.core.Cell.cellToLonLat;
import static org.geotoolkit.dggs.a5.internal.core.Cell.lonLatToCell;
import static org.geotoolkit.dggs.a5.internal.core.CoordinateTransforms.fromLonLat;
import static org.geotoolkit.dggs.a5.internal.core.Hex.hexToU64;
import static org.geotoolkit.dggs.a5.internal.core.Hex.u64ToHex;
import static org.geotoolkit.dggs.a5.internal.core.Serialization.MAX_RESOLUTION;
import static org.geotoolkit.dggs.a5.internal.core.Serialization.deserialize;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;

/**
 * Port of upstream {@code tests/cell.test.ts}. The upstream {@code boundaryToGeoJSON} helper
 * only decorates failure messages with a GeoJSON dump of the boundary; it is not ported
 * (the failure message lists the cell id and the point).
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public class CellTest {

    private static final JsonNode populatedPlaces = Fixtures.load("data/ne_50m_populated_places_nameonly.json");
    private static final JsonNode cellToLonLatFixtures = Fixtures.load("fixtures/cell-to-lonlat.json");
    private static final JsonNode lonLatToCellSequenceFixtures = Fixtures.load("fixtures/lonlat-to-cell-sequence.json");

    // ---- describe: Cell ID Validation Tests ----
    @Test
    void should_return_WORLD_CELL_for_resolution_minus_1() {
        final long cellId = lonLatToCell(new double[]{0, 0}, -1);
        assertEquals(0L, cellId);
    }

    @Test
    void should_return_0_0_for_WORLD_CELL_center() {
        final double[] lonLat = cellToLonLat(0L);
        assertArrayEquals(new double[]{0, 0}, lonLat);
    }

    @Test
    void should_return_empty_array_for_WORLD_CELL_boundary() {
        final double[][] boundary = cellToBoundary(0L);
        assertEquals(0, boundary.length);
    }

    // ---- describe: Antimeridian Cell Tests ----
    @Test
    void antimeridian_cell_should_have_longitude_span_less_than_180_degrees() {
        final String[] antimeridianCells = {"eb60000000000000", "2e00000000000000"};
        final int[] segments = {1, 10, Cell.CellToBoundaryOptions.AUTO};
        for (final String cellId : antimeridianCells) {
            for (final int segment : segments) {
                final long cellIdBigInt = hexToU64(cellId);
                final double[][] boundary = cellToBoundary(cellIdBigInt, new Cell.CellToBoundaryOptions(segment));

                // Check for antimeridian crossing
                double minLon = Double.POSITIVE_INFINITY, maxLon = Double.NEGATIVE_INFINITY;
                for (final double[] p : boundary) {
                    minLon = Math.min(minLon, p[0]);
                    maxLon = Math.max(maxLon, p[0]);
                }
                final double lonSpan = maxLon - minLon;
                assertTrue(lonSpan < 180, "cell " + cellId + " segments " + segment + " span " + lonSpan);
            }
        }
    }

    // ---- describe: Cell Boundary Tests ----
    @Test
    void should_contain_the_original_point_for_all_resolutions() {
        // Extract coordinates from GeoJSON features
        final JsonNode features = populatedPlaces.get("features");

        // Dictionary to store failures for each resolution and point
        final Map<String, Map<Integer, String>> failures = new LinkedHashMap<>();

        // Skipping resolution MAX_RESOLUTION as lonLatToCell is not implemented for this resolution yet (upstream)
        for (int pointIndex = 0; pointIndex < features.size(); pointIndex++) {
            final JsonNode feature = features.get(pointIndex);
            final double[] testLonlat = doubles(feature.get("geometry").get("coordinates"));
            final JsonNode name = feature.path("properties").path("name");
            final String featureName = name.isTextual() && !name.asText().isEmpty() ? name.asText() : "Unnamed " + pointIndex;
            final String pointKey = "Point " + pointIndex + " - " + featureName + " (" + testLonlat[0] + ", " + testLonlat[1] + ")";

            // Test resolutions from 0 to MAX_RESOLUTION
            for (int resolution = 1; resolution <= MAX_RESOLUTION; resolution++) {
                if (resolution == MAX_RESOLUTION) {
                    continue;
                }
                String failure = null;
                try {
                    // Get cell ID for the coordinates
                    final long cellId = lonLatToCell(testLonlat, resolution);

                    // Get cell boundary
                    cellToBoundary(cellId);

                    // Verify the original point is contained within the cell
                    final A5Cell cell = deserialize(cellId);
                    if (a5cellContainsPoint(cell, fromLonLat(testLonlat)) < 0) {
                        failure = "Cell " + u64ToHex(cellId) + " does not contain the original point";
                    }
                } catch (RuntimeException e) {
                    failure = "Unexpected error: " + e;
                }
                if (failure != null) {
                    failures.computeIfAbsent(pointKey, k -> new TreeMap<>()).put(resolution, failure);
                }
            }
        }

        // Report all failures
        if (!failures.isEmpty()) {
            final StringBuilder failureMessage = new StringBuilder("\nFailures by point and resolution:\n");
            for (final Map.Entry<String, Map<Integer, String>> e : failures.entrySet()) {
                failureMessage.append('\n').append(e.getKey()).append(":\n");
                for (final Map.Entry<Integer, String> r : e.getValue().entrySet()) {
                    failureMessage.append("  Resolution ").append(r.getKey()).append(":\n    - ").append(r.getValue()).append('\n');
                }
            }
            fail(failureMessage.toString());
        }
    }

    // ---- describe: cellToLonLat Tests ----
    @Test
    void should_return_longitude_in_minus_180_180_range() {
        for (final JsonNode fixture : cellToLonLatFixtures) {
            final long cell = hexToU64(fixture.get("cell_id").asText());
            final double[] lonLat = cellToLonLat(cell);
            final double lon = lonLat[0], lat = lonLat[1];
            assertTrue(lon >= -180);
            assertTrue(lon <= 180);
            assertTrue(lat >= -90);
            assertTrue(lat <= 90);
        }
    }

    @Test
    void should_match_fixture_values() {
        for (final JsonNode fixture : cellToLonLatFixtures) {
            final long cell = hexToU64(fixture.get("cell_id").asText());
            final double[] lonLat = cellToLonLat(cell);
            final double[] expected = doubles(fixture.get("center_lonlat"));
            assertCloseTo(expected[0], lonLat[0], 10);
            assertCloseTo(expected[1], lonLat[1], 10);
        }
    }

    // ---- describe: lonLatToCell sequence ----
    // The lookups are made in order: each must match a cold lookup of its point,
    // whatever the lookup before it was
    @Test
    void should_not_depend_on_earlier_lookups() {
        final JsonNode points = lonLatToCellSequenceFixtures.get("points");
        for (final JsonNode sequence : lonLatToCellSequenceFixtures.get("sequences")) {
            final int resolution = sequence.get("resolution").asInt();
            final JsonNode lookups = sequence.get("lookups");
            final JsonNode cells = sequence.get("cells");
            for (int i = 0; i < lookups.size(); i++) {
                final long cell = lonLatToCell(doubles(points.get(lookups.get(i).asInt())), resolution);
                assertEquals(cells.get(i).asText(), u64ToHex(cell), "resolution " + resolution + ", lookup " + i);
            }
        }
    }

    // ---- Java only ----
    /**
     * The single-entry lookup cache is per thread: concurrent sequences must give the same results.
     */
    @Test
    void lookup_sequences_are_thread_safe() throws Exception {
        final java.util.concurrent.ExecutorService executor = java.util.concurrent.Executors.newFixedThreadPool(4);
        try {
            final java.util.List<java.util.concurrent.Future<?>> futures = new java.util.ArrayList<>();
            for (int t = 0; t < 8; t++) {
                futures.add(executor.submit(() -> {
                    for (int k = 0; k < 20; k++) should_not_depend_on_earlier_lookups();
                }));
            }
            for (java.util.concurrent.Future<?> f : futures) f.get();
        } finally {
            executor.shutdown();
        }
    }
}
