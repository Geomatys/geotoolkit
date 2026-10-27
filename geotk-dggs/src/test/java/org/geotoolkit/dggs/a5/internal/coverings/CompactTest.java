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
import org.geotoolkit.dggs.a5.internal.Fixtures;
import static org.geotoolkit.dggs.a5.internal.core.Serialization.getResolution;
import static org.geotoolkit.dggs.a5.internal.coverings.Compact.compact;
import static org.geotoolkit.dggs.a5.internal.coverings.Compact.uncompact;
import static org.geotoolkit.dggs.a5.internal.coverings.Resolution.coveringResolution;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

/**
 * Port of upstream {@code tests/compact.test.ts}.
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public class CompactTest {

    private static final JsonNode COMPACT_FIXTURES = Fixtures.load("fixtures/compact.json");

    // ---- describe: uncompact ----

    @Test
    void uncompact_should_handle_all_fixture_test_cases() {
        for (final JsonNode testCase : COMPACT_FIXTURES.get("uncompact")) {
            final String name = testCase.get("name").asText();
            final long[] input = Fixtures.hexArray(testCase.get("input"));
            final long[] result = uncompact(input);

            assertEquals(testCase.get("expectedCount").asInt(), result.length, name);
            assertArrayEquals(Fixtures.hexArray(testCase.get("expectedCells")), result, name);
            assertEquals(testCase.get("expectedResolution").asInt(), coveringResolution(input), name);

            // All results should be at the covering's resolution
            for (final long cell : result) {
                assertEquals(testCase.get("expectedResolution").asInt(), getResolution(cell), name);
            }
        }
    }

    // ---- describe: compact ----

    @Test
    void compact_should_handle_all_fixture_test_cases() {
        for (final JsonNode testCase : COMPACT_FIXTURES.get("compact")) {
            final long[] input = Fixtures.hexArray(testCase.get("input"));
            final long[] expected = Fixtures.hexArray(testCase.get("expectedOutput"));
            final long[] result = compact(input);

            // Output is canonical: cells in curve order, then the compaction marker
            assertArrayEquals(expected, result, testCase.get("name").asText());
        }
    }

    // ---- describe: compact/uncompact round-trip ----

    @Test
    void round_trip_should_handle_all_round_trip_fixture_test_cases() {
        for (final JsonNode testCase : COMPACT_FIXTURES.get("roundTrip")) {
            final String name = testCase.get("name").asText();
            final long[] initialCells = Fixtures.hexArray(testCase.get("initialCells"));
            final long[] afterCompact = Fixtures.hexArray(testCase.get("afterCompact"));

            // Verify compact result matches fixture
            assertArrayEquals(afterCompact, compact(initialCells), name);

            // Verify uncompact restores coverage, at the resolution the compaction marker records
            final long[] uncompactResult = uncompact(afterCompact);
            assertEquals(testCase.get("expectedFinalCount").asInt(), uncompactResult.length, name);
            for (final long cell : uncompactResult) {
                assertEquals(testCase.get("resolution").asInt(), getResolution(cell), name);
            }
        }
    }
}
