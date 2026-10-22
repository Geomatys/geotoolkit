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
import static org.geotoolkit.dggs.a5.internal.core.CompactionMarker.compactionMarker;
import static org.geotoolkit.dggs.a5.internal.core.CompactionMarker.compactionMarkerResolution;
import static org.geotoolkit.dggs.a5.internal.core.CompactionMarker.isCompactionMarker;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/**
 * Java only tests of {@link CompactionMarker} (upstream has no dedicated test file): the
 * {@code isCompactionMarker} fixtures of upstream {@code tests/covering.test.ts}, whose test
 * is ported with the coverings layer.
 *
 * @author Johann Sorel (Geomatys)
 */
public class CompactionMarkerTest {

    @Test
    void should_recognize_compaction_markers_and_nothing_else() {
        for (final JsonNode f : Fixtures.load("fixtures/covering.json").get("isCompactionMarker")) {
            final long value = Fixtures.hex(f.get("value"));
            assertEquals(f.get("expected").asBoolean(), isCompactionMarker(value), f.get("value").asText());
            if (f.get("expected").asBoolean()) {
                assertEquals(f.get("resolution").asInt(), compactionMarkerResolution(value), f.get("value").asText());
            }
        }
    }

    @Test
    void round_trips_every_resolution() {
        for (int r = 0; r <= Serialization.MAX_RESOLUTION; r++) {
            final long marker = compactionMarker(r);
            assertTrue(isCompactionMarker(marker));
            assertEquals(r, compactionMarkerResolution(marker));
            assertFalse(Serialization.isValidCell(marker));
        }
        assertFalse(isCompactionMarker(compactionMarker(Serialization.MAX_RESOLUTION + 1)));
        assertEquals("f000000000000040", Hex.u64ToHex(compactionMarker(0)));
    }
}
