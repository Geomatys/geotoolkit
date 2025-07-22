
package org.geotoolkit.dggs.a5.internal;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import static org.geotoolkit.dggs.a5.internal.Orig.*;
import org.geotoolkit.dggs.a5.internal.Utils.Origin;
import static org.geotoolkit.dggs.a5.internal.Serialization.*;
import org.geotoolkit.dggs.a5.internal.Utils.A5Cell;
import static org.junit.Assert.*;
import org.junit.Test;

public class SerializationTest {

//    import TEST_IDS from './test-ids.json';
//    import { cellToParent, cellToChildren } from 'a5/core/serialization';


    private static final String[] RESOLUTION_MASKS = new String[]{
        // Non-Hilbert resolutions
        "0000000000000000000000000000000000000000000000000000000000000000", // Globe
        "0000001000000000000000000000000000000000000000000000000000000000", // Dodecahedron faces
        "0000000100000000000000000000000000000000000000000000000000000000", // Quintants
        // Hilbert resolutions
        "0000000010000000000000000000000000000000000000000000000000000000",
        "0000000000100000000000000000000000000000000000000000000000000000",
        "0000000000001000000000000000000000000000000000000000000000000000",
        "0000000000000010000000000000000000000000000000000000000000000000",
        "0000000000000000100000000000000000000000000000000000000000000000",
        "0000000000000000001000000000000000000000000000000000000000000000",
        "0000000000000000000010000000000000000000000000000000000000000000",
        "0000000000000000000000100000000000000000000000000000000000000000",
        "0000000000000000000000001000000000000000000000000000000000000000",
        "0000000000000000000000000010000000000000000000000000000000000000",
        "0000000000000000000000000000100000000000000000000000000000000000",
        "0000000000000000000000000000001000000000000000000000000000000000",
        "0000000000000000000000000000000010000000000000000000000000000000",
        "0000000000000000000000000000000000100000000000000000000000000000",
        "0000000000000000000000000000000000001000000000000000000000000000",
        "0000000000000000000000000000000000000010000000000000000000000000",
        "0000000000000000000000000000000000000000100000000000000000000000",
        "0000000000000000000000000000000000000000001000000000000000000000",
        "0000000000000000000000000000000000000000000010000000000000000000",
        "0000000000000000000000000000000000000000000000100000000000000000",
        "0000000000000000000000000000000000000000000000001000000000000000",
        "0000000000000000000000000000000000000000000000000010000000000000",
        "0000000000000000000000000000000000000000000000000000100000000000",
        "0000000000000000000000000000000000000000000000000000001000000000",
        "0000000000000000000000000000000000000000000000000000000010000000",
        "0000000000000000000000000000000000000000000000000000000000100000",
        "0000000000000000000000000000000000000000000000000000000000001000",
        "0000000000000000000000000000000000000000000000000000000000000010",
        // Point level
        //'0000000000000000000000000000000000000000000000000000000000000001', // TODO
    };

    private static final Origin origin0 = origins.get(0); // Use first origin for most tests

    private static String pad(String str, int size, char fill) {
        final int length = str.length();
        if (length >= size) return str;
        return (""+fill).repeat(size-length) + str;
    }

    // serialize //////////////////////////////////////////

    @Test
    public void correct_number_of_masks() {
        assertEquals(RESOLUTION_MASKS.length, MAX_RESOLUTION); // TODO add point level
    }


    @Test
    public void removal_mask_is_correct() {
        final BigInteger expected = new BigInteger("0".repeat(6) + "1".repeat(58),2);
        assertEquals(REMOVAL_MASK,expected.longValue());
    }

    @Test
    public void encodes_resolution_correctly_for_different_values() {

        final List<A5Cell> testCases = new ArrayList<>();
        for (int i = 0; i < RESOLUTION_MASKS.length; i++) {
            // Origin 0 has first quintant 4, so start use segment 4 to obtain start of Hilbert curve
            testCases.add(new A5Cell(origin0, 4, 0l, i));
        }

        for (int i = 0; i < testCases.size(); i++) {
            final long serialized = serialize(testCases.get(i));
            assertEquals(RESOLUTION_MASKS[i], pad(Long.toString(serialized, 2),64,'0'));
        }
    }

    @Test
    public void correctly_extracts_resolution() {
        for (int i = 0; i < RESOLUTION_MASKS.length; i++) {
            final String binary = RESOLUTION_MASKS[i];
            final int bitCount = binary.length();
            assertEquals(bitCount,64);
            final long N = Long.parseLong(binary,2);
            final int resolution = getResolution(N);
            assertEquals(resolution,i);
        }
    }

    @Test
    public void encodes_origin_segment_and_S_correctly() {
        // Origin 0 has first quintant 4, so start use segment 4 to obtain start of Hilbert curve
        final A5Cell cell = new A5Cell(origin0, 4, 0l, 30);
        final long serialized = serialize(cell);
        assertEquals(serialized, 0b10);
    }

    @Test
    public void throws_error_when_S_is_too_large_for_resolution() {
        final A5Cell cell = new A5Cell(
          origin0,
          0,
          16l, // Too large for resolution 2 (max is 15)
          4);

        assertThrows(IllegalArgumentException.class, () -> {
            serialize(cell);
        });
    }

    @Test
    public void throws_error_when_resolution_exceeds_maximum() {
        final A5Cell cell = new A5Cell(
            origin0,
            0,
            0l,
            32 // MAX_RESOLUTION is 31
        );

        assertThrows(IllegalArgumentException.class, () -> {
            serialize(cell);
        });
    }

    // round trip  //////////////////////////////////////////

    @Test
    public void resolution_masks() {
        for (int i = 0; i < RESOLUTION_MASKS.length; i++) {
            final String binary = RESOLUTION_MASKS[i];
            final long serialized = Long.parseUnsignedLong(binary, 2);
            final A5Cell deserialized = deserialize(serialized);
            final long reserialized = serialize(deserialized);
            assertEquals(reserialized, serialized);
        }
    }

    @Test
    public void resolution_masks_with_origin() {
        for (int n = 1; n < 12; n++) {
            final String originSegmentId = pad(Long.toString(5 * n, 2), 6, '0');
            for (int i = FIRST_HILBERT_RESOLUTION; i < RESOLUTION_MASKS.length; i++) {
                final String binary = RESOLUTION_MASKS[i];
                final long serialized = Long.parseUnsignedLong(originSegmentId + binary.substring(6), 2);
                final A5Cell deserialized = deserialize(serialized);
                final long reserialized = serialize(deserialized);
                assertEquals(reserialized, serialized);
            }
        }
    }

    @Test
    public void test_ids() {
        for (String id : TestIds.IDS) {
            final long serialized = Long.parseUnsignedLong(id, 16);
            final A5Cell deserialized = deserialize(serialized);
            final long reserialized = serialize(deserialized);
            assertEquals(reserialized, serialized);
        }
    }

    // hierarchy  //////////////////////////////////////////

    @Test
    public void round_trip_between_cellToParent_and_cellToChildren() {
        for (String id : TestIds.IDS) {
            final long cell = Long.parseUnsignedLong(id, 16);
            final long child = cellToChildren(cell).get(0);
            final long parent = cellToParent(child);
            assertEquals(parent,cell);

            for (Long c : cellToChildren(cell)) {
                final long p = cellToParent(c);
                assertEquals(p, cell);
            }
        }
    }

    @Test
    public void non_Hilbert_to_non_Hilbert_hierarchy() {
        // Test resolution 1 to 2 (both non-Hilbert)
        final long cell = serialize(new A5Cell(origin0, 0, 0l, 1));
        final List<Long> children = cellToChildren(cell);
        assertEquals(children.size(), 5);

        for (Long child : children) {
            final long parent = cellToParent(child);
            assertEquals(parent, cell);
        }
    }

    @Test
    public void non_Hilbert_to_Hilbert_hierarchy() {
        // Test resolution 2 to 3 (non-Hilbert to Hilbert)
        final long cell = serialize(new A5Cell(origin0, 0, 0l, 2));
        final List<Long> children = cellToChildren(cell);
        assertEquals(children.size(), 4);

        for (Long child : children) {
            final long parent = cellToParent(child);
            assertEquals(parent, cell);
        }
    }

    @Test
    public void hilbert_to_non_Hilbert_hierarchy() {
        // Test resolution 3 to 2 (Hilbert to non-Hilbert)
        final long cell = serialize(new A5Cell(origin0, 0, 0l, 3));
        final long parent = cellToParent(cell, 2);
        final List<Long> children = cellToChildren(parent);
        assertEquals(children.size(),4);
        assertTrue(children.contains(cell));
    }

    @Test
    public void low_resolution_hierarchy_chain() {
        // Test a chain of resolutions from 0 to 4
        final int[] resolutions = new int[]{0, 1, 2, 3, 4};
        final long[] cells = new long[resolutions.length];
        for (int i = 0; i < cells.length; i++) {
            cells[i] = serialize(new A5Cell(origin0, 0, 0l, resolutions[i]));
        }

        // Test parent relationships
        for (int i = 1; i < cells.length; i++) {
            final long parent = cellToParent(cells[i]);
            assertEquals(parent, cells[i-1]);
        }

        // Test children relationships
        for (int i = 0; i < cells.length - 1; i++) {
            final List<Long> children = cellToChildren(cells[i]);
            assertTrue(children.contains(cells[i+1]));
        }
    }

    @Test
    public void base_cell_division_counts() {
        // Start with the base cell (resolution 0)
        final long baseCell = serialize(new A5Cell(origin0, 0, 0l, 0));
        List<Long> currentCells = List.of(baseCell);
        final int[] expectedCounts = new int[]{1, 12, 60, 240, 960}; // 1, 12, 12*5, 12*5*4, 12*5*4*4

        // Test each resolution level up to 4
        for (int resolution = 0; resolution < 4; resolution++) {
            // Get all children of current cells
            List<Long> allChildren = currentCells.stream().flatMap((cell) -> cellToChildren(cell).stream()).toList();

            // Verify the total number of cells matches expected
            assertEquals(allChildren.size(), expectedCounts[resolution + 1]);

            // Update current cells for next iteration
            currentCells = allChildren;
        }
    }

}
