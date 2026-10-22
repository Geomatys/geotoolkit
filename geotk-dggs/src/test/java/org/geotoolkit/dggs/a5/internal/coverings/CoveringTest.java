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
package org.geotoolkit.dggs.a5.internal.coverings;

import com.fasterxml.jackson.databind.JsonNode;
import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import org.geotoolkit.dggs.a5.internal.Fixtures;
import org.geotoolkit.dggs.a5.internal.core.Cell;
import static org.geotoolkit.dggs.a5.internal.core.CompactionMarker.isCompactionMarker;
import static org.geotoolkit.dggs.a5.internal.coverings.Compact.compact;
import static org.geotoolkit.dggs.a5.internal.coverings.Compact.uncompact;
import static org.geotoolkit.dggs.a5.internal.coverings.Measures.area;
import static org.geotoolkit.dggs.a5.internal.coverings.Measures.count;
import static org.geotoolkit.dggs.a5.internal.coverings.Resolution.coveringResolution;
import static org.geotoolkit.dggs.a5.internal.coverings.SetOperations.contains;
import static org.geotoolkit.dggs.a5.internal.coverings.SetOperations.difference;
import static org.geotoolkit.dggs.a5.internal.coverings.SetOperations.intersect;
import static org.geotoolkit.dggs.a5.internal.coverings.SetOperations.overlaps;
import static org.geotoolkit.dggs.a5.internal.coverings.SetOperations.union;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/**
 * Port of upstream {@code tests/covering.test.ts}.
 *
 * <p>Not ported here: {@code isCompactionMarker > should recognize compaction markers and nothing else}
 * (in {@code core.CompactionMarkerTest}), {@code isValidCell > should recognize A5 cells and nothing else}
 * (in {@code core.SerializationTest}) and {@code isValidCell > should refuse values outside 64 bits}
 * (impossible in Java: a {@code long} is always an unsigned 64 bits pattern).</p>
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public class CoveringTest {

    private static final JsonNode FIXTURES = Fixtures.load("fixtures/covering.json");

    private static long[] toCells(JsonNode hex) {
        return Fixtures.hexArray(hex);
    }

    private static long[] reverse(long[] cells) {
        final long[] r = new long[cells.length];
        for (int i = 0; i < cells.length; i++) r[i] = cells[cells.length - 1 - i];
        return r;
    }

    // ---- describe: set operations ----

    @Test
    void should_match_fixtures_for_set_operations() {
        for (final JsonNode f : FIXTURES.get("setOperations")) {
            final String name = f.get("name").asText();
            final long[] a = toCells(f.get("a"));
            final long[] b = toCells(f.get("b"));
            assertArrayEquals(toCells(f.get("union")), union(a, b), name);
            assertArrayEquals(toCells(f.get("intersect")), intersect(a, b), name);
            assertArrayEquals(toCells(f.get("difference")), difference(a, b), name);
            assertEquals(f.get("overlaps").asBoolean(), overlaps(a, b), name);
            assertEquals(f.get("overlaps").asBoolean(), overlaps(b, a), name);
            assertEquals(f.get("resolution").asInt(), coveringResolution(union(a, b)), name);
        }
    }

    @Test
    void should_refuse_sets_at_different_resolutions() {
        for (final JsonNode f : FIXTURES.get("mismatchedResolutions")) {
            final String name = f.get("name").asText();
            final long[] a = toCells(f.get("a"));
            final long[] b = toCells(f.get("b"));
            assertThrows(IllegalArgumentException.class, () -> union(a, b), name);
            assertThrows(IllegalArgumentException.class, () -> intersect(a, b), name);
            assertThrows(IllegalArgumentException.class, () -> difference(a, b), name);
            assertThrows(IllegalArgumentException.class, () -> overlaps(a, b), name);
        }
    }

    @Test
    void should_give_the_same_results_for_input_out_of_curve_order() {
        // Sorted input is merged as given; anything else is detected and sorted first
        for (final JsonNode f : FIXTURES.get("setOperations")) {
            final String name = f.get("name").asText();
            final long[] a = reverse(toCells(f.get("a")));
            final long[] b = reverse(toCells(f.get("b")));
            assertArrayEquals(toCells(f.get("union")), union(a, b), name);
            assertArrayEquals(toCells(f.get("intersect")), intersect(a, b), name);
            assertArrayEquals(toCells(f.get("difference")), difference(a, b), name);
        }
    }

    // 'should accept BigUint64Array input': no distinct array type in Java (Cells = long[]),
    // covered by should_match_fixtures_for_set_operations.

    // ---- describe: measures ----

    @Test
    void should_match_fixtures_for_measures() {
        for (final JsonNode f : FIXTURES.get("measures")) {
            final String name = f.get("name").asText();
            final long[] cells = toCells(f.get("cells"));
            final double expectedArea = f.get("area").asDouble();
            assertEquals(f.get("resolution").asInt(), coveringResolution(cells), name);
            assertEquals(new BigInteger(f.get("count").asText()), count(cells), name);
            assertTrue(Math.abs(area(cells) - expectedArea) <= 1e-10 * expectedArea, name);
        }
    }

    // ---- describe: measures of overlapping input ----

    @Test
    void should_count_every_cell_given_including_duplicates() {
        for (final JsonNode f : FIXTURES.get("measures")) {
            final String name = f.get("name").asText();
            final long[] cells = toCells(f.get("cells"));
            final BigInteger expectedCount = new BigInteger(f.get("count").asText());
            final double expectedArea = f.get("area").asDouble();
            final long[] doubled = new long[cells.length * 2];
            System.arraycopy(cells, 0, doubled, 0, cells.length);
            System.arraycopy(cells, 0, doubled, cells.length, cells.length);
            assertEquals(BigInteger.TWO.multiply(expectedCount), count(doubled), name);
            assertTrue(Math.abs(area(doubled) - 2 * expectedArea) <= 1e-10 * expectedArea, name);
            // union merges them
            assertEquals(expectedCount, count(union(cells, cells)), name);
        }
    }

    // ---- describe: contains ----

    @Test
    void should_match_fixtures_for_contains() {
        for (final JsonNode f : FIXTURES.get("contains")) {
            final String name = f.get("name").asText();
            final long[] cells = toCells(f.get("cells"));
            for (final JsonNode probe : f.get("probes")) {
                assertEquals(probe.get("expected").asBoolean(), contains(cells, Fixtures.hex(probe.get("cell"))),
                        name + " " + probe.get("cell").asText());
            }
        }
    }

    // ---- describe: contains at another resolution ----

    @Test
    void should_refuse_cells_at_another_resolution() {
        for (final JsonNode f : FIXTURES.get("mismatchedProbes")) {
            final String name = f.get("name").asText();
            final long[] cells = toCells(f.get("cells"));
            for (final JsonNode probe : f.get("probes")) {
                final long cell = Fixtures.hex(probe);
                assertThrows(IllegalArgumentException.class, () -> contains(cells, cell), name + " " + probe.asText());
            }
        }
    }

    // ---- describe: isCompactionMarker ----
    // 'should recognize compaction markers and nothing else': ported in core.CompactionMarkerTest

    @Test
    void should_record_the_resolution_of_each_compaction_marker() {
        for (final JsonNode f : FIXTURES.get("isCompactionMarker")) {
            if (!f.get("expected").asBoolean()) continue;
            final long value = Fixtures.hex(f.get("value"));
            assertTrue(isCompactionMarker(value));
            assertEquals(f.get("resolution").asInt(), coveringResolution(new long[] {value}), f.get("value").asText());
        }
    }

    @Test
    void should_have_an_empty_boundary() {
        for (final JsonNode f : FIXTURES.get("isCompactionMarker")) {
            if (f.get("expected").asBoolean()) {
                assertEquals(0, Cell.cellToBoundary(Fixtures.hex(f.get("value"))).length, f.get("value").asText());
            }
        }
    }

    // ---- describe: isValidCell ----
    // 'should recognize A5 cells and nothing else': ported in core.SerializationTest
    // 'should refuse values outside 64 bits': not applicable in Java (long is a 64 bits pattern)

    // ---- describe: invalid cells ----

    private static Map<String, Consumer<long[]>> operations() {
        final Map<String, Consumer<long[]>> operations = new LinkedHashMap<>();
        operations.put("compact", cells -> compact(cells));
        operations.put("uncompact", cells -> uncompact(cells));
        operations.put("count", cells -> count(cells));
        operations.put("area", cells -> area(cells));
        operations.put("union", cells -> union(cells, cells));
        operations.put("contains", cells -> contains(cells, cells[0]));
        return operations;
    }

    @Test
    void should_refuse_values_that_are_not_cells() {
        for (final Map.Entry<String, Consumer<long[]>> operation : operations().entrySet()) {
            for (final JsonNode value : FIXTURES.get("invalidCells")) {
                final long[] cells = {Fixtures.hex(value)};
                assertThrows(IllegalArgumentException.class, () -> operation.getValue().accept(cells),
                        operation.getKey() + " " + value.asText());
            }
        }
    }

    @Test
    void should_accept_the_valid_cells_at_the_edges_of_the_encoding() {
        for (final Map.Entry<String, Consumer<long[]>> operation : operations().entrySet()) {
            for (final JsonNode value : FIXTURES.get("validEdgeCells")) {
                final long[] cells = {Fixtures.hex(value)};
                assertDoesNotThrow(() -> operation.getValue().accept(cells), operation.getKey() + " " + value.asText());
            }
        }
    }
}
