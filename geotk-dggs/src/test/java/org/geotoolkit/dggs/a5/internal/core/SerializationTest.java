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
import java.util.ArrayList;
import java.util.List;
import org.geotoolkit.dggs.a5.internal.Fixtures;
import static org.geotoolkit.dggs.a5.internal.core.Hex.u64ToHex;
import static org.geotoolkit.dggs.a5.internal.core.Origins.origins;
import static org.geotoolkit.dggs.a5.internal.core.Serialization.FIRST_CURVE_RESOLUTION;
import static org.geotoolkit.dggs.a5.internal.core.Serialization.MAX_RESOLUTION;
import static org.geotoolkit.dggs.a5.internal.core.Serialization.WORLD_CELL;
import static org.geotoolkit.dggs.a5.internal.core.Serialization.cellToChildren;
import static org.geotoolkit.dggs.a5.internal.core.Serialization.cellToParent;
import static org.geotoolkit.dggs.a5.internal.core.Serialization.deserialize;
import static org.geotoolkit.dggs.a5.internal.core.Serialization.getRes0Cells;
import static org.geotoolkit.dggs.a5.internal.core.Serialization.getResolution;
import static org.geotoolkit.dggs.a5.internal.core.Serialization.isChildOf;
import static org.geotoolkit.dggs.a5.internal.core.Serialization.serialize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * Port of upstream {@code tests/serialization.test.ts}.
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public class SerializationTest {

    private static final JsonNode FIXTURES = Fixtures.load("fixtures/serialization.json");
    private static final List<String> RESOLUTION_MASKS = new ArrayList<>();
    private static final List<String> TEST_IDS = new ArrayList<>();
    static {
        FIXTURES.get("resolutionMasks").forEach(n -> RESOLUTION_MASKS.add(n.asText()));
        FIXTURES.get("testIds").forEach(n -> TEST_IDS.add(n.asText()));
    }

    private static final Origin origin0 = origins.get(0); // Use first origin for most tests

    private static String toBinary64(long value) {
        final String bin = Long.toBinaryString(value);
        return "0".repeat(64 - bin.length()) + bin;
    }

    private static long parseBinary(String binary) {
        return Long.parseUnsignedLong(binary, 2);
    }

    private static boolean contains(long[] array, long value) {
        for (long v : array) {
            if (v == value) return true;
        }
        return false;
    }

    private static void assertThrowsMessage(String message, Runnable run) {
        final IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, run::run);
        assertTrue(ex.getMessage().contains(message), () -> "Unexpected message: " + ex.getMessage());
    }

    // ---- describe: serialize ----
    @Test
    void Correct_number_of_masks() {
        assertEquals(MAX_RESOLUTION + 1, RESOLUTION_MASKS.size());
    }

    @Test
    void encodes_resolution_correctly_for_different_values() {
        for (int i = 0; i < RESOLUTION_MASKS.size(); i++) {
            // Origin 0 has first quintant 4, so start use segment 4 to obtain start of the curve
            final A5Cell input = new A5Cell(origin0, 4, 0L, i);
            final long serialized = serialize(input);
            assertEquals(RESOLUTION_MASKS.get(i), toBinary64(serialized), "resolution " + i);
        }
    }

    @Test
    void correctly_extracts_resolution() {
        for (int i = 0; i < RESOLUTION_MASKS.size(); i++) {
            final String binary = RESOLUTION_MASKS.get(i);
            final int bitCount = binary.length();
            assertEquals(64, bitCount);
            final long N = parseBinary(binary);
            final int resolution = getResolution(N);
            assertEquals(i, resolution);
        }
    }

    @Test
    void encodes_origin_segment_and_S_correctly() {
        // Origin 0 has first quintant 4, so start use segment 4 to obtain start of the curve
        final A5Cell cell = new A5Cell(origin0, 4, 0L, MAX_RESOLUTION - 1);
        final long serialized = serialize(cell);
        assertEquals(0b10L, serialized);
    }

    @Test
    void throws_error_when_S_is_too_large_for_resolution() {
        final A5Cell cell = new A5Cell(origin0, 0, 16L /* Too large for resolution 1 (max is 15) */, 3);
        assertThrowsMessage("S (16) is too large for resolution level 3", () -> serialize(cell));
    }

    @Test
    void throws_error_when_resolution_exceeds_maximum() {
        final A5Cell cell = new A5Cell(origin0, 0, 0L, 31 /* MAX_RESOLUTION is 30 */);
        assertThrowsMessage("Resolution (31) is too large", () -> serialize(cell));
    }

    // ---- describe: serialize > round trip ----
    @Test
    @Disabled("test.skip upstream")
    void resolution_masks() {
        for (final String binary : RESOLUTION_MASKS) {
            final long serialized = parseBinary(binary);
            final A5Cell deserialized = deserialize(serialized);
            final long reserialized = serialize(deserialized);
            assertEquals(serialized, reserialized);
        }
    }

    @Test
    void resolution_masks_with_origin_n() {
        for (int n = 1; n < 12; n++) {
            final String bin = Integer.toBinaryString(5 * n);
            final String originSegmentId = "0".repeat(6 - bin.length()) + bin;
            // Exclude res 30 as it has a different bit layout (5-bit quintant)
            for (final String binary : RESOLUTION_MASKS.subList(FIRST_CURVE_RESOLUTION, MAX_RESOLUTION)) {
                final long serialized = parseBinary(originSegmentId + binary.substring(6));
                final A5Cell deserialized = deserialize(serialized);
                final long reserialized = serialize(deserialized);
                assertEquals(serialized, reserialized, "origin " + n + " (" + originSegmentId + ")");
            }
        }
    }

    @Test
    void test_ids() {
        for (final String id : TEST_IDS) {
            final long serialized = Fixtures.hex(id);
            final A5Cell deserialized = deserialize(serialized);
            final long reserialized = serialize(deserialized);
            assertEquals(serialized, reserialized, id);
        }
    }

    // ---- describe: hierarchy ----
    @Test
    void round_trip_between_cellToParent_and_cellToChildren() {
        for (final String id : TEST_IDS) {
            final long cell = Fixtures.hex(id);
            final int resolution = getResolution(cell);
            // Skip res 30 (no children) and res 29 with out-of-bounds quintants
            // (res 30 children fall back to res 29)
            if (resolution >= MAX_RESOLUTION) continue;
            final long child = cellToChildren(cell)[0];
            if (getResolution(child) != resolution + 1) continue;

            final long parent = cellToParent(child);
            assertEquals(cell, parent, id);

            final long[] children = cellToChildren(cell);
            for (long c : children) {
                assertEquals(cell, cellToParent(c), id);
            }
        }
    }

    @Test
    void cellToChildren_with_same_resolution_returns_original_cell() {
        for (final String id : TEST_IDS) {
            final long cell = Fixtures.hex(id);
            final int currentResolution = getResolution(cell);

            // Test with explicit childResolution equal to current resolution
            final long[] children = cellToChildren(cell, currentResolution);

            // Should return array with just the original cell
            assertEquals(1, children.length);
            assertEquals(cell, children[0]);
        }
    }

    @Test
    void cellToParent_with_same_resolution_returns_original_cell() {
        for (final String id : TEST_IDS) {
            final long cell = Fixtures.hex(id);
            final int currentResolution = getResolution(cell);

            // Test with explicit parentResolution equal to current resolution
            final long parent = cellToParent(cell, currentResolution);

            // Should return the original cell
            assertEquals(cell, parent, id);
        }
    }

    @Test
    void cellToParent_throws_for_a_resolution_finer_than_the_cell() {
        for (final JsonNode target : FIXTURES.get("finerParentTargets")) {
            final long cell = Fixtures.hex(target.get("cell"));
            final int resolution = target.get("resolution").asInt();
            final IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> cellToParent(cell, resolution), () -> target.get("cell").asText() + " -> " + resolution);
            assertTrue(ex.getMessage().contains("must be equal to or less than current resolution"), ex.getMessage());
        }
    }

    @Test
    void cellToChildren_returns_children_in_ascending_ID_order() {
        for (final int res : new int[]{0, 1, 2}) assertTrue(ascending(cellToChildren(WORLD_CELL, res)));
        for (final long cell : getRes0Cells()) {
            for (final int res : new int[]{1, 2, 3}) assertTrue(ascending(cellToChildren(cell, res)));
        }
    }

    private static boolean ascending(long[] cells) {
        for (int i = 1; i < cells.length; i++) {
            if (Long.compareUnsigned(cells[i - 1], cells[i]) >= 0) return false;
        }
        return true;
    }

    @Test
    void non_curve_to_non_curve_hierarchy() {
        // Test resolution 0 to 1 (both non-curve)
        final long cell = serialize(new A5Cell(origin0, 0, 0L, 0));
        final long[] children = cellToChildren(cell);
        assertEquals(5, children.length);
        for (long child : children) {
            final long parent = cellToParent(child);
            assertEquals(cell, parent);
        }
    }

    @Test
    void non_curve_to_curve_hierarchy() {
        // Test resolution 1 to 2 (non-curve to curve)
        final long cell = serialize(new A5Cell(origin0, 0, 0L, 1));
        final long[] children = cellToChildren(cell);
        assertEquals(4, children.length);
        for (long child : children) {
            final long parent = cellToParent(child);
            assertEquals(cell, parent);
        }
    }

    @Test
    void curve_to_non_curve_hierarchy() {
        // Test resolution 2 to 1 (curve to non-curve)
        final long cell = serialize(new A5Cell(origin0, 0, 0L, 2));
        final long parent = cellToParent(cell, 1);
        final long[] children = cellToChildren(parent);
        assertEquals(4, children.length);
        assertTrue(contains(children, cell));
    }

    @Test
    void low_resolution_hierarchy_chain() {
        // Test a chain of resolutions from 0 to 4
        final int[] resolutions = {0, 1, 2, 3, 4};
        final long[] cells = new long[resolutions.length];
        for (int i = 0; i < resolutions.length; i++) {
            cells[i] = serialize(new A5Cell(origin0, 0, 0L, resolutions[i]));
        }

        // Test parent relationships
        for (int i = 1; i < cells.length; i++) {
            final long parent = cellToParent(cells[i]);
            assertEquals(cells[i - 1], parent);
        }

        // Test children relationships
        for (int i = 0; i < cells.length - 1; i++) {
            final long[] children = cellToChildren(cells[i]);
            assertTrue(contains(children, cells[i + 1]));
        }
    }

    @Test
    void base_cell_division_counts() {
        // Start with the base cell (resolution 0)
        final long baseCell = serialize(new A5Cell(origin0, 0, 0L, -1));
        List<Long> currentCells = List.of(baseCell);
        final int[] expectedCounts = {12, 60, 240, 960}; // 12, 12*5, 12*5*4, 12*5*4*4

        // Test each resolution level up to 4
        for (int resolution = 0; resolution < 4; resolution++) {
            // Get all children of current cells
            final List<Long> allChildren = new ArrayList<>();
            for (long cell : currentCells) {
                for (long child : cellToChildren(cell)) allChildren.add(child);
            }

            // Verify the total number of cells matches expected
            assertEquals(expectedCounts[resolution], allChildren.size());

            // Update current cells for next iteration
            currentCells = allChildren;
        }
    }

    // ---- describe: isChildOf ----
    @Test
    void returns_true_for_self_at_same_resolution_curve_range() {
        for (int res = FIRST_CURVE_RESOLUTION; res < MAX_RESOLUTION; res++) {
            final long cell = serialize(new A5Cell(origin0, 0, 0L, res));
            assertTrue(isChildOf(cell, cell, res));
        }
    }

    @Test
    void returns_true_for_direct_children() {
        for (int res = FIRST_CURVE_RESOLUTION; res < MAX_RESOLUTION - 1; res++) {
            final long parent = serialize(new A5Cell(origin0, 0, 0L, res));
            final long[] children = cellToChildren(parent);
            for (long child : children) {
                assertTrue(isChildOf(child, parent, res));
            }
        }
    }

    @Test
    void returns_true_for_deep_descendants() {
        final long parent = serialize(new A5Cell(origin0, 0, 0L, 5));
        List<Long> descendants = List.of(parent);
        for (int r = 6; r < 10; r++) {
            final List<Long> next = new ArrayList<>();
            for (long c : descendants) {
                for (long child : cellToChildren(c)) next.add(child);
            }
            descendants = next;
        }
        for (long d : descendants) {
            assertTrue(isChildOf(d, parent, 5));
        }
    }

    @Test
    void returns_false_for_siblings_same_resolution_different_cells() {
        final long parent = serialize(new A5Cell(origin0, 0, 0L, 4));
        final long[] children = cellToChildren(parent);
        // Each child is not a descendant of any other child at res 5.
        for (int i = 0; i < children.length; i++) {
            for (int j = 0; j < children.length; j++) {
                if (i == j) continue;
                assertFalse(isChildOf(children[i], children[j], 5));
            }
        }
    }

    @Test
    void returns_false_when_child_is_at_shallower_resolution_than_parent() {
        final long grandparent = serialize(new A5Cell(origin0, 0, 0L, 4));
        final long parent = cellToChildren(grandparent)[0]; // res 5
        assertFalse(isChildOf(grandparent, parent, 5));
    }

    // ---- describe: getRes0Cells ----
    @Test
    void returns_12_resolution_0_cells() {
        final long[] res0Cells = getRes0Cells();
        assertEquals(12, res0Cells.length);

        // Each cell should have resolution 0
        for (long cell : res0Cells) {
            assertEquals(0, getResolution(cell));
        }

        // Expected hex values for the 12 resolution 0 cells
        final String[] hexes = {"2", "6", "a", "e", "12", "16", "1a", "1e", "22", "26", "2a", "2e"};
        for (int i = 0; i < hexes.length; i++) {
            final int length = i < 4 ? 15 : 16;
            final String expected = hexes[i] + "0".repeat(length - hexes[i].length());
            // Verify each cell matches the expected hex value
            assertEquals(expected, u64ToHex(res0Cells[i]));
        }
    }

    // ---- describe: resolution 30 ----
    @Test
    void getResolution_detects_res_30_from_LSB() {
        // Any odd bigint (LSB=1) that isn't 0 is resolution 30
        assertEquals(30, getResolution(1L));
        assertEquals(30, getResolution(3L));
        assertEquals(30, getResolution(0xffffffffffffffffL));
    }

    @Test
    void serialize_deserialize_round_trip_for_valid_quintants_0_41() {
        // Quintants 0-31 use ...1, 32-39 use ...100, 40-41 use ...10000
        for (int q = 0; q < 42; q++) {
            final int originId = q / 5;
            final Origin origin = origins.get(originId);
            final int segmentN = q % 5;
            final int segment = (segmentN + origin.firstQuintant()) % 5;

            final A5Cell cell = new A5Cell(origin, segment, 0L, 30);
            final long serialized = serialize(cell);
            assertEquals(30, getResolution(serialized));

            // Verify correct tag pattern
            if (q <= 31) {
                assertEquals(1L, serialized & 1L); // ...1 encoding
            } else if (q <= 39) {
                assertEquals(0b100L, serialized & 0b111L); // ...100 encoding
            } else {
                assertEquals(0b10000L, serialized & 0b11111L); // ...10000 encoding
            }

            final A5Cell deserialized = deserialize(serialized);
            assertEquals(originId, deserialized.origin().id());
            assertEquals(segment, deserialized.segment());
            assertEquals(0L, deserialized.S());
            assertEquals(30, deserialized.resolution());

            // Round trip
            final long reserialized = serialize(deserialized);
            assertEquals(serialized, reserialized);
        }
    }

    @Test
    void serialize_deserialize_round_trip_with_non_zero_S() {
        final Origin origin = origins.get(0);
        final int segment = (0 + origin.firstQuintant()) % 5; // segmentN=0

        final long[] testSValues = {0L, 1L, 42L, (1L << 58) - 1L}; // min, small, medium, max
        for (final long S : testSValues) {
            final A5Cell cell = new A5Cell(origin, segment, S, 30);
            final long serialized = serialize(cell);
            final A5Cell deserialized = deserialize(serialized);
            assertEquals(S, deserialized.S());
            assertEquals(30, deserialized.resolution());
            assertEquals(serialized, serialize(deserialized));
        }
    }

    @Test
    void bit_layout_1_encoding_quintant_0_31() {
        final Origin origin = origins.get(0);
        final int segment = (0 + origin.firstQuintant()) % 5; // quintant=0

        // Quintant 0, S=0 → just the tag bit
        final long cell0 = serialize(new A5Cell(origin, segment, 0L, 30));
        assertEquals(1L, cell0);

        // Quintant 0, S=1 → tag + S shifted left by 1
        final long cell1 = serialize(new A5Cell(origin, segment, 1L, 30));
        assertEquals(0b11L, cell1); // S=1 at bit 1, tag at bit 0
    }

    @Test
    void bit_layout_10000_encoding_quintant_40_41() {
        // Origin 8, segmentN=0 → quintant 40
        final Origin origin = origins.get(8);
        final int segment = (0 + origin.firstQuintant()) % 5;

        // Quintant 40, S=0 → (40-40)=0 in top 1 bit, tag 10000
        final long cell0 = serialize(new A5Cell(origin, segment, 0L, 30));
        assertEquals(0b10000L, cell0); // just the tag

        // Quintant 40, S=1 → S shifted left by 5 + tag
        final long cell1 = serialize(new A5Cell(origin, segment, 1L, 30));
        assertEquals(0b110000L, cell1); // S=1 at bit 5, tag 10000 at bits 4-0
    }

    @Test
    void bit_layout_100_encoding_quintant_32_39() {
        // Origin 6, segmentN=0 → quintant 30... need quintant >= 32
        // Origin 6 has quintants 30-34, so segmentN=2 gives quintant 32
        final Origin origin = origins.get(6);
        final int segmentN = 2;
        final int segment = (segmentN + origin.firstQuintant()) % 5;

        // Quintant 32, S=0 → (32-32)=0 in top 3 bits, tag 100
        final long cell0 = serialize(new A5Cell(origin, segment, 0L, 30));
        assertEquals(0b100L, cell0); // just the tag

        // Quintant 32, S=1 → S shifted left by 3 + tag
        final long cell1 = serialize(new A5Cell(origin, segment, 1L, 30));
        assertEquals(0b1100L, cell1); // S=1 at bit 3, tag 100 at bits 2-0
    }

    @Test
    void serialize_deserialize_round_trip_with_non_zero_S_extended_encoding() {
        // Use quintant 35 (origin 7, segmentN=0) for ...100 encoding
        final Origin origin = origins.get(7);
        final int segment = (0 + origin.firstQuintant()) % 5;

        final long[] testSValues = {0L, 1L, 42L, (1L << 58) - 1L};
        for (final long S : testSValues) {
            final A5Cell cell = new A5Cell(origin, segment, S, 30);
            final long serialized = serialize(cell);
            assertEquals(0b100L, serialized & 0b111L); // ...100 tag
            final A5Cell deserialized = deserialize(serialized);
            assertEquals(S, deserialized.S());
            assertEquals(30, deserialized.resolution());
            assertEquals(serialized, serialize(deserialized));
        }
    }

    @Test
    void falls_back_to_res_29_for_quintant_gt_41() {
        // Origin 9 has quintants 45-49, all > 41
        final Origin origin = origins.get(9);
        final int segment = (0 + origin.firstQuintant()) % 5;
        final long cell = serialize(new A5Cell(origin, segment, 0L, 30));
        assertEquals(29, getResolution(cell));

        // With non-zero S, the parent S should be S >> 2
        final long cell2 = serialize(new A5Cell(origin, segment, 7L, 30));
        assertEquals(29, getResolution(cell2));
        assertEquals(1L, deserialize(cell2).S()); // 7 >> 2 = 1
    }

    @Test
    void falls_back_to_res_29_for_out_of_bounds_quintant_eg_55() {
        // Origin 11 has quintants 55-59, all > 41
        final Origin origin = origins.get(11);
        final int segmentN = 0;
        final int segment = (segmentN + origin.firstQuintant()) % 5;
        final long cell = serialize(new A5Cell(origin, segment, 100L, 30));
        assertEquals(29, getResolution(cell));
        assertEquals(25L, deserialize(cell).S()); // 100 >> 2 = 25
        assertEquals(11, deserialize(cell).origin().id());
    }

    @Test
    void throws_for_S_too_large() {
        final Origin origin = origins.get(0);
        final int segment = (0 + origin.firstQuintant()) % 5;
        assertThrowsMessage("too large for resolution level 30", () -> serialize(new A5Cell(origin, segment, 1L << 58, 30)));
    }

    /**
     * Java only: S values whose 64 bits product with the slot count wraps (BigInt never wraps).
     */
    @Test
    void throws_for_S_overflowing_64_bits() {
        final Origin origin = origins.get(0);
        final int segment = (0 + origin.firstQuintant()) % 5;
        // 2^8 * 2^56 = 2^64 wraps to 0 at resolution 2
        assertThrowsMessage("is too large for resolution level 2", () -> serialize(new A5Cell(origin, segment, 1L << 8, 2)));
        assertThrowsMessage("(18446744073709551615) is too large", () -> serialize(new A5Cell(origin, segment, -1L, 30)));
    }

    @Test
    void cellToParent_from_res_30_to_res_29() {
        final Origin origin = origins.get(0);
        final int segment = (0 + origin.firstQuintant()) % 5;

        // Create 4 children at res 30 (S=0..3), they should share the same res 29 parent (S=0)
        for (int i = 0; i < 4; i++) {
            final long child = serialize(new A5Cell(origin, segment, i, 30));
            final long parent = cellToParent(child);
            assertEquals(29, getResolution(parent));

            final A5Cell parentCell = deserialize(parent);
            assertEquals(0L, parentCell.S());
        }
    }

    @Test
    void cellToChildren_from_res_29_to_res_30() {
        final Origin origin = origins.get(0);
        final int segment = (0 + origin.firstQuintant()) % 5;
        final long parent = serialize(new A5Cell(origin, segment, 0L, 29));
        final long[] children = cellToChildren(parent, 30);

        assertEquals(4, children.length);
        for (int i = 0; i < children.length; i++) {
            assertEquals(30, getResolution(children[i]));
            final A5Cell childCell = deserialize(children[i]);
            assertEquals(i, childCell.S());
        }
    }

    @Test
    void cellToChildren_cellToParent_round_trip() {
        final Origin origin = origins.get(0);
        final int segment = (0 + origin.firstQuintant()) % 5;
        final long parent = serialize(new A5Cell(origin, segment, 42L, 29));
        final long[] children = cellToChildren(parent, 30);

        assertEquals(4, children.length);
        for (long child : children) {
            assertEquals(parent, cellToParent(child));
        }
    }

    @Test
    void serialize_deserialize_round_trip_with_non_zero_S_10000_encoding() {
        // Use quintant 40 (origin 8, segmentN=0) for ...10000 encoding
        final Origin origin = origins.get(8);
        final int segment = (0 + origin.firstQuintant()) % 5;

        final long[] testSValues = {0L, 1L, 42L, (1L << 58) - 1L};
        for (final long S : testSValues) {
            final A5Cell cell = new A5Cell(origin, segment, S, 30);
            final long serialized = serialize(cell);
            assertEquals(0b10000L, serialized & 0b11111L); // ...10000 tag
            final A5Cell deserialized = deserialize(serialized);
            assertEquals(S, deserialized.S());
            assertEquals(30, deserialized.resolution());
            assertEquals(serialized, serialize(deserialized));
        }
    }

    @Test
    void cellToChildren_cellToParent_round_trip_10000_encoding() {
        // Origin 8 (quintant 40) uses the ...10000 encoding
        final Origin origin = origins.get(8);
        final int segment = (0 + origin.firstQuintant()) % 5;
        final long parent = serialize(new A5Cell(origin, segment, 10L, 29));
        final long[] children = cellToChildren(parent, 30);

        assertEquals(4, children.length);
        for (long child : children) {
            assertEquals(30, getResolution(child));
            assertEquals(0b10000L, child & 0b11111L); // ...10000 tag
            assertEquals(parent, cellToParent(child));
        }
    }

    @Test
    void cellToChildren_cellToParent_round_trip_100_encoding() {
        // Origin 7 (quintant 35) uses the ...100 encoding
        final Origin origin = origins.get(7);
        final int segment = (0 + origin.firstQuintant()) % 5;
        final long parent = serialize(new A5Cell(origin, segment, 10L, 29));
        final long[] children = cellToChildren(parent, 30);

        assertEquals(4, children.length);
        for (long child : children) {
            assertEquals(30, getResolution(child));
            assertEquals(0b100L, child & 0b111L); // ...100 tag
            assertEquals(parent, cellToParent(child));
        }
    }

    @Test
    void cellToChildren_of_res_30_throws_max_resolution() {
        final Origin origin = origins.get(0);
        final int segment = (0 + origin.firstQuintant()) % 5;
        final long cell = serialize(new A5Cell(origin, segment, 0L, 30));
        assertThrowsMessage("exceeds maximum resolution", () -> cellToChildren(cell));
    }

    // ---- describe: resolution 30 locations ----
    @Test
    void round_trip_for_res_30_location_cells() {
        for (final JsonNode loc : FIXTURES.get("res30Locations")) {
            final long cell = Fixtures.hex(loc.get("hex"));
            final A5Cell deserialized = deserialize(cell);
            final long reserialized = serialize(deserialized);
            assertEquals(cell, reserialized, loc.get("name").asText());
        }
    }

    @Test
    void out_of_bounds_quintants_fall_back_to_res_29() {
        int count = 0;
        for (final JsonNode loc : FIXTURES.get("res30Locations")) {
            if (loc.get("resolution").asInt() != 29) continue;
            count++;
            final long cell = Fixtures.hex(loc.get("hex"));
            assertEquals(29, getResolution(cell));
        }
        assertTrue(count > 0);
    }

    @Test
    void in_bounds_quintants_encode_at_res_30() {
        int count = 0;
        for (final JsonNode loc : FIXTURES.get("res30Locations")) {
            if (loc.get("resolution").asInt() != 30) continue;
            count++;
            final long cell = Fixtures.hex(loc.get("hex"));
            assertEquals(30, getResolution(cell));
        }
        assertTrue(count > 0);
    }

    // ---- Java only: unsigned checks, with the isValidCell fixtures of upstream covering.test.ts ----
    @Test
    void isValidCell_recognizes_A5_cells_and_nothing_else() {
        final JsonNode fixtures = Fixtures.load("fixtures/covering.json").get("isValidCell");
        for (final JsonNode f : fixtures) {
            final long cell = Fixtures.hex(f.get("value"));
            final boolean expected = f.get("expected").asBoolean();
            final String msg = f.get("value").asText();
            assertEquals(expected, Serialization.isValidCell(cell), msg);
            if (expected) {
                // checkedResolution and cellFirstSlot accept exactly the valid cells
                assertEquals(getResolution(cell), Serialization.checkedResolution(cell), msg);
                Serialization.cellFirstSlot(cell);
            } else {
                assertThrows(IllegalArgumentException.class, () -> Serialization.checkedResolution(cell), msg);
                assertThrows(IllegalArgumentException.class, () -> Serialization.cellFirstSlot(cell), msg);
            }
        }
    }

    @Test
    void slots_of_cells_above_2_pow_63() {
        // Quintant 59, resolution 2..29: first slot and slot count, unsigned
        final Origin origin = origins.get(11);
        final int segment = (4 + origin.firstQuintant()) % 5; // quintant 59
        for (int res = FIRST_CURVE_RESOLUTION; res < MAX_RESOLUTION; res++) {
            final long cell = serialize(new A5Cell(origin, segment, 0L, res));
            assertEquals(59L << 58, Serialization.cellFirstSlot(cell));
            assertEquals(59L << 58, Serialization.cellFirstSlotUnchecked(cell));
            assertEquals(Serialization.SLOT_COUNTS[res], Serialization.cellSlotCount(cell));
            assertEquals(cell, Serialization.slotToCell(59L << 58, res));
            assertEquals(res, Serialization.checkedResolution(cell));
        }
        // Resolution 30 quintant 31 (5-bit field, top bits set) and 41 (bit 63 set)
        final long q31 = serialize(new A5Cell(origins.get(6), (1 + origins.get(6).firstQuintant()) % 5, 5L, 30));
        assertEquals((31L << 58) | 5L, Serialization.cellFirstSlot(q31));
        final long q41 = serialize(new A5Cell(origins.get(8), (1 + origins.get(8).firstQuintant()) % 5, 5L, 30));
        assertEquals((1L << 63) | (5L << 5) | 0b10000L, q41);
        assertEquals((41L << 58) | 5L, Serialization.cellFirstSlot(q41));
        assertEquals(1L, Serialization.cellSlotCount(q41));
        assertEquals(Serialization.WORLD_SLOTS, Serialization.cellSlotCount(WORLD_CELL));
        assertEquals("f000000000000000", u64ToHex(Serialization.WORLD_SLOTS));
        // value 2^63: tag bit 63 is not a resolution tag
        assertFalse(Serialization.isValidCell(1L << 63));
        assertThrows(IllegalArgumentException.class, () -> Serialization.cellFirstSlot(1L << 63));
    }
}
