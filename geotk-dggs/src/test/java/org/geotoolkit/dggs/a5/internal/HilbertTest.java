
package org.geotoolkit.dggs.a5.internal;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.sis.geometries.math.Vector2D;
import static org.geotoolkit.dggs.a5.internal.Hilbert.*;
import org.geotoolkit.dggs.a5.internal.Hilbert.Anchor;
import org.geotoolkit.dggs.a5.internal.Hilbert.Orientation;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.jupiter.api.Disabled;

public class HilbertTest {

    private static final double TOLERANCE = 1e-15;

    @Test
    public void returns_correct_offsets_for_base_cases() {
        // Test first corner (0)
        final Vector2D.Double offset0 = quaternaryToKJ(0, NO, NO);
        assertArrayEquals(offset0.toArrayDouble(), new double[]{0,0}, 0);
        final int[] flips0 = quaternaryToFlips(0);
        assertArrayEquals(flips0, new int[]{NO,NO});

        // Test second corner (1)
        final Vector2D.Double offset1 = quaternaryToKJ(1, NO, NO);
        assertArrayEquals(offset1.toArrayDouble(), new double[]{1,0}, 0);
        final int[] flips1 = quaternaryToFlips(1);
        assertArrayEquals(flips1, new int[]{NO,YES});

        // Test third corner (2)
        final Vector2D.Double offset2 = quaternaryToKJ(2, NO, NO);
        assertArrayEquals(offset2.toArrayDouble(), new double[]{1,1}, 0);
        final int[] flips2 = quaternaryToFlips(2);
        assertArrayEquals(flips2, new int[]{NO,NO});

        // Test fourth corner (3)
        final Vector2D.Double offset3 = quaternaryToKJ(3, NO, NO);
        assertArrayEquals(offset3.toArrayDouble(), new double[]{2,1}, 0);
        final int[] flips3 = quaternaryToFlips(3);
        assertArrayEquals(flips3, new int[]{YES,NO});
    }

    @Test
    public void respects_flips_in_offset_calculation() {
        // Test with x-flip
        final Vector2D.Double offsetX = quaternaryToKJ(1, YES, NO);
        assertArrayEquals(offsetX.toArrayDouble(), new double[]{-0,-1}, 0);

        // Test with y-flip
        final Vector2D.Double offsetY = quaternaryToKJ(1, NO, YES);
        assertArrayEquals(offsetY.toArrayDouble(), new double[]{0,1}, 0);

        // Test with both flips
        final Vector2D.Double offsetXY = quaternaryToKJ(1, YES, YES);
        assertArrayEquals(offsetXY.toArrayDouble(), new double[]{-1,-0}, 0);
    }

    @Test
    public void output_flips_depend_only_on_input_n() {
        final int[][] EXPECTED_FLIPS = new int[][]{
            {NO, NO},
            {NO, YES},
            {NO, NO},
            {YES, NO}
        };
        for (int n = 0; n < 4; n++) {
            final int[] flips = quaternaryToFlips(n);
            assertArrayEquals(flips, EXPECTED_FLIPS[n]);
        }
    }

    private record TestRecord(double x, double y, int fx, int fy){};

    @Test
    public void generates_correct_sequence() {
        // Test first few indices
        final Anchor anchor0 = sToAnchor(0, 0, Orientation.uv);
        assertArrayEquals(anchor0.offset.toArrayDouble(), new double[]{0,0}, 0);
        assertArrayEquals(anchor0.flips, new int[]{NO, NO});

        final Anchor anchor1 = sToAnchor(1, 0, Orientation.uv);
        assertEquals(anchor1.flips[1], YES);

        final Anchor anchor4 = sToAnchor(4, 0, Orientation.uv);
        assertTrue(anchor4.offset.length() > 1); // Should be scaled up

        // Test that sequence length grows exponentially
        final List<Anchor> anchors = new ArrayList<>();
        for (int i = 0; i < 16; i++) anchors.add(sToAnchor(i, 0, Orientation.uv));

        final Set<Vector2D.Double> uniqueOffsets = new HashSet<>();
        for (Anchor a : anchors) uniqueOffsets.add(a.offset.copy());
        assertEquals(uniqueOffsets.size(), 12);


        final Set<Object> uniqueAnchors = new HashSet<>();
        for (Anchor a : anchors) uniqueAnchors.add(new TestRecord(a.offset.x, a.offset.y, a.flips[0], a.flips[1]));
        assertEquals(uniqueAnchors.size(), 15);
    }

    @Test
    public void neighboring_anchors_are_adjacent() {
        // Test that combining anchors preserves orientation rules
        final Anchor anchor1 = sToAnchor(0, 0, Orientation.uv);;
        final Anchor anchor2 = sToAnchor(1, 0, Orientation.uv);;
        final Anchor anchor3 = sToAnchor(2, 0, Orientation.uv);;

        // Check that relative positions make sense
        final Vector2D.Double diff = anchor2.offset.copy().subtract(anchor1.offset);
        assertEquals(diff.length(), 1, 0); // Should be adjacent
        final Vector2D.Double diff2 = anchor3.offset.copy().subtract(anchor2.offset);
        assertEquals(diff2.length(), Math.sqrt(2), 0); // Should be adjacent
    }

    @Test
    public void generates_correct_anchors_for_all_indices() {
        final Object[][] EXPECTED_ANCHORS = new Object[][]{
                {0l, new double[]{0, 0}, new int[]{NO, NO}},
                {9l, new double[]{3, 1}, new int[]{YES, YES}},
                {16l, new double[]{2, 2}, new int[]{NO, NO}},
                {17l, new double[]{3, 2}, new int[]{NO, YES}},
                {31l, new double[]{1, 3}, new int[]{YES, NO}},
                {77l, new double[]{7, 5}, new int[]{NO, NO}},
                {100l, new double[]{3, 7}, new int[]{YES, YES}},
                {101l, new double[]{2, 7}, new int[]{YES, NO}},
                {170l, new double[]{10, 1}, new int[]{NO, NO}},
                {411l, new double[]{7, 13}, new int[]{YES, NO}},
                {1762l, new double[]{7, 31}, new int[]{YES, NO}},
                {481952l, new double[]{96, 356}, new int[]{YES, YES}},
                {192885192l, new double[]{13183, 1043}, new int[]{NO, NO}},
                {4719283155l, new double[]{37190, 46076}, new int[]{NO, YES}},
                {7123456789l, new double[]{29822, 40293}, new int[]{NO, YES}},
            };

        for (Object[] r : EXPECTED_ANCHORS) {
            Anchor anchor = sToAnchor((long)r[0], 20, Orientation.uv);
            assertArrayEquals(anchor.offset.toArrayDouble(), (double[])r[1], 0.0);
            assertArrayEquals(anchor.flips, (int[])r[2]);
        }
    }

    @Test
    public void converts_ij_coordinates_to_kj_coordinates() {
        // Test some basic conversions
        final Map<Vector2D.Double, Vector2D.Double> testCases = new LinkedHashMap();
        testCases.put(new Vector2D.Double(0, 0), new Vector2D.Double(0, 0));    // Origin
        testCases.put(new Vector2D.Double(1, 0), new Vector2D.Double(1, 0));    // Unit i
        testCases.put(new Vector2D.Double(0, 1), new Vector2D.Double(1, 1));    // Unit j -> k=i+j=1, j=1
        testCases.put(new Vector2D.Double(1, 1), new Vector2D.Double(2, 1));    // i + j -> k=2, j=1
        testCases.put(new Vector2D.Double(2, 3), new Vector2D.Double(5, 3));    // 2i + 3j -> k=5, j=3

        for (Entry<Vector2D.Double, Vector2D.Double> entry : testCases.entrySet()) {
            final Vector2D.Double result = IJToKJ(entry.getKey());
            assertEquals(entry.getValue(), result);
        }

    }

    @Test
    public void converts_kj_coordinates_to_ij_coordinates() {
        // Test some basic conversions
        final Map<Vector2D.Double, Vector2D.Double> testCases = new LinkedHashMap();
        testCases.put(new Vector2D.Double(0, 0), new Vector2D.Double(0, 0));     // Origin
        testCases.put(new Vector2D.Double(1, 0), new Vector2D.Double(1, 0));     // Pure k -> i=1, j=0
        testCases.put(new Vector2D.Double(1, 1), new Vector2D.Double(0, 1));     // k=1, j=1 -> i=0, j=1
        testCases.put(new Vector2D.Double(2, 1), new Vector2D.Double(1, 1));     // k=2, j=1 -> i=1, j=1
        testCases.put(new Vector2D.Double(5, 3), new Vector2D.Double(2, 3));     // k=5, j=3 -> i=2, j=3

        for (Entry<Vector2D.Double, Vector2D.Double> entry : testCases.entrySet()) {
          final Vector2D.Double result = KJToIJ(entry.getKey());
            assertEquals(entry.getValue(), result);
        }
    }

    @Test
    public void IJToKJ_and_KJToIJ_are_inverses() {
        // Test that converting back and forth gives the original coordinates
        final List<Vector2D.Double> testPoints = List.of(
            new Vector2D.Double(0, 0),
            new Vector2D.Double(1, 0),
            new Vector2D.Double(0, 1),
            new Vector2D.Double(1, 1),
            new Vector2D.Double(2, 3),
            new Vector2D.Double(-1, 2),
            new Vector2D.Double(3, -2)
        );

        testPoints.forEach((point) -> {
            final Vector2D.Double kj = IJToKJ(point);
            final Vector2D.Double ij = KJToIJ(kj);
            assertEquals(point, ij);
        });
    }

    @Disabled
    @Test
    public void correctly_identifies_quaternary_digits_from_offsets() {
        //marked TODO in typescript code
    }

    @Test
    public void correctly_determines_number_of_digits_needed() {
        final Map<Vector2D.Double, Integer> testCases = new LinkedHashMap();
        testCases.put(new Vector2D.Double(0, 0), 1);
        testCases.put(new Vector2D.Double(1, 0), 1);
        testCases.put(new Vector2D.Double(2, 1), 2);
        testCases.put(new Vector2D.Double(4, 0), 3);
        testCases.put(new Vector2D.Double(8, 8), 5);
        testCases.put(new Vector2D.Double(16, 0), 5);
        testCases.put(new Vector2D.Double(32, 32), 7);

        for (Entry<Vector2D.Double, Integer> entry : testCases.entrySet()) {
            assertEquals(getRequiredDigits(entry.getKey()), (int)entry.getValue());
        }
    }

    @Test
    public void matches_actual_digits_needed_in_sToAnchor_output() {
        // Test that getRequiredDigits matches the number of digits
        // actually used in sToAnchor's output
        final List<Integer> testValues = List.of(0, 1, 2, 3, 4, 9, 16, 17, 31, 77, 100);

        for (Integer s : testValues) {
            final Anchor anchor = sToAnchor(s, 0, Orientation.uv);;
            final int requiredDigits = getRequiredDigits(anchor.offset);
            final int actualDigits = Integer.toString(s, 4).length();
            assertTrue(requiredDigits >= actualDigits);
            assertTrue(requiredDigits <= actualDigits + 1);
        }
    }

    @Test
    public void computes_s_from_anchor() {
        final Object[][] testValues = new Object[][]{
            // First quadrant
            {0, new Vector2D.Double(0, 0)},
            {0, new Vector2D.Double(0.999, 0)},
            {1, new Vector2D.Double(0.6, 0.6)},
            {7, new Vector2D.Double(0.000001, 1.1)},

            {2, new Vector2D.Double(1.2, 0.5)},
            {2, new Vector2D.Double(1.9999, 0)},

            // Recursive cases, 2nd quadrant, flipY
            {3, new Vector2D.Double(1.9999, 0.001)},
            {4, new Vector2D.Double(1.1, 1.1)},
            {5, new Vector2D.Double(1.999, 1.999)},
            {6, new Vector2D.Double(0.99, 1.99)},

            // 3rd quadrant, no flips
            {28, new Vector2D.Double(0.999, 2.000001)},
            {29, new Vector2D.Double(0.9, 2.5)},
            {30, new Vector2D.Double(0.5, 3.1)},
            {31, new Vector2D.Double(1.3, 2.5)},

            // 4th quadrant, flipX
            {8, new Vector2D.Double(2.00001, 1.001)},
            {9, new Vector2D.Double(2.8, 0.5)},
            {10, new Vector2D.Double(2.00001, 0.5)},
            {11, new Vector2D.Double(3.5, 0.2)},

            // Next level, just sample a few as flips are the same as before
            {15, new Vector2D.Double(2.5, 1.5)},
            {21, new Vector2D.Double(3.999, 3.999)},

            // Finally, both flips
            {24, new Vector2D.Double(1.999, 3.999)},
            {25, new Vector2D.Double(1.2, 3.5)},
            {26, new Vector2D.Double(1.9, 2.2)},
            {27, new Vector2D.Double(0.1, 3.9)}
        };

        for (Object[] test : testValues) {
            final long s = (int)test[0];
            final Vector2D.Double offset = (Vector2D.Double)test[1];
            assertEquals(s, IJToS(offset, 3, Orientation.uv));
        }
    }

    @Test
    public void IJToSTest() {

        final List<Integer> testValues = List.of(0, 1, 2, 3, 4, 9, 16, 17, 31, 77, 100, 101, 170, 411, 1762, 4410, 12387, 41872, 410922, 1247878, 88889182);
        final int resolution = 20;
        final Orientation[] orientations = new Orientation[]{Orientation.uv, Orientation.vu, Orientation.uw, Orientation.wu, Orientation.vw, Orientation.wv};
        for (Orientation orientation : orientations) {
            for (Integer s : testValues) {
                final Anchor anchor = sToAnchor(s, resolution, orientation);

                // Nudge the offset away from the edge of the triangle
                final int flipX = anchor.flips[0];
                final int flipY = anchor.flips[1];
                if (flipX == NO && flipY == NO) {
                    anchor.offset.x += 0.1; anchor.offset.y += 0.1;
                } else if (flipX == YES && flipY == NO) {
                    anchor.offset.x += 0.1; anchor.offset.y += -0.2;
                } else if (flipX == NO && flipY == YES) {
                    anchor.offset.x += -0.1; anchor.offset.y += 0.2;
                } else if (flipX == YES && flipY == YES) {
                    anchor.offset.x += -0.1; anchor.offset.y += -0.1;
                }

                assertEquals(s.longValue(), IJToS(anchor.offset, resolution, orientation));
            }
        }
    }

}
