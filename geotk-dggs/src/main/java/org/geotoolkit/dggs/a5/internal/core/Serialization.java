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

import java.util.List;
import static org.geotoolkit.dggs.a5.internal.core.Origins.origins;

/**
 * Encoding of A5 cells as 64 bits identifiers.
 *
 * <p>Port of upstream {@code core/serialization.ts}. Cell ids, slots and S values are TS
 * {@code bigint} values held in Java {@code long} as raw unsigned 64 bits patterns : every
 * right shift is {@code >>>}, and comparisons of values that may reach 2^63 (cell ids, slots,
 * {@link #WORLD_SLOTS}) use {@link Long#compareUnsigned(long, long)}. Everything is computed
 * in the static initialiser: this class is thread-safe.</p>
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public final class Serialization {

    public static final int FIRST_CURVE_RESOLUTION = 2;
    public static final int MAX_RESOLUTION = 30;
    // IDs below res 30 start with a 6-bit origin (res 0) or quintant, above S
    /** Shift amount (TS bigint 58n, int in Java as it is only used as a shift distance). */
    public static final int QUINTANT_SHIFT = 58;
    public static final long S_MASK = (1L << QUINTANT_SHIFT) - 1L;

    // Abstract cell that contains the whole world, has resolution -1 and 12 children,
    // which are the res0 cells.
    public static final long WORLD_CELL = 0L;

    // Resolution 30 IDs have no room for a 6-bit quintant: its field is 5, 3 or 1
    // bits wide, marked by the tag (lowest bits) ...1, ...100 or ...10000:
    //   ...1     → 5-bit quintant (0-31),  58-bit S
    //   ...100   → 3-bit quintant (32-39), 58-bit S
    //   ...10000 → 1-bit quintant (40-41), 58-bit S
    // Quintants 42-59 have no res-30 IDs.
    /** The number of quintants (in ID order) with resolution 30 IDs. */
    public static final int RES30_QUINTANTS = 42;

    /** The leaf slot of a res-30 ID: its quintant, then its 58-bit S (see Leaf slots below). */
    private static long res30ToSlot(long index) {
        if ((index & 1L) != 0) return ((index >>> 59) << QUINTANT_SHIFT) | ((index >>> 1) & S_MASK);
        if ((index & 0b100L) != 0) return (((index >>> 61) + 32L) << QUINTANT_SHIFT) | ((index >>> 3) & S_MASK);
        return (((index >>> 63) + 40L) << QUINTANT_SHIFT) | ((index >>> 5) & S_MASK);
    }

    /** The res-30 ID of a leaf slot in quintants 0-41. */
    private static long slotToRes30(long slot) {
        final long q = slot >>> QUINTANT_SHIFT; // 0-63: plain comparisons are safe
        final long s = slot & S_MASK;
        if (q < 32L) return (q << 59) | (s << 1) | 1L;
        if (q < 40L) return ((q - 32L) << 61) | (s << 3) | 0b100L;
        return ((q - 40L) << 63) | (s << 5) | 0b10000L;
    }

    // Leaf slots. The A5 curve, at resolution 30, passes through every leaf
    // (res-30) cell of the globe once: picture it as a line of slots, one per leaf
    // cell, numbered in curve order from 0 to 60 * 4^29 - 1. A leaf slot is the
    // 6-bit quintant (0-59) then the leaf's S, left-aligned below it.
    //
    // A cell at resolution r occupies 4^(30-r) consecutive slots, an aligned block
    // starting at its first slot, and the cells of a resolution step along the
    // slots in strides of that size, as the curve does. Unlike cell IDs,
    // whose layout differs at resolutions 0, 1 and 30, slots put every cell on one
    // integer line in curve order, including all 60 quintants at resolution 30.
    // A leaf slot is not a cell ID.

    public static final long QUINTANT_SLOTS = 1L << QUINTANT_SHIFT;
    public static final long ORIGIN_SLOTS = 5L * QUINTANT_SLOTS;
    /** 60 * 2^58 &gt; 2^63 : negative as a signed long, compare it unsigned. */
    public static final long WORLD_SLOTS = 60L * QUINTANT_SLOTS;

    /**
     * By resolution 0..30: the resolution tag (lowest set bit) of a cell below
     * res 30, and the number of slots a cell occupies.
     * (Java: shared arrays, never modify them.)
     */
    public static final long[] RESOLUTION_TAGS = new long[MAX_RESOLUTION + 1];
    public static final long[] SLOT_COUNTS = new long[MAX_RESOLUTION + 1];
    static {
        for (int r = 0; r <= MAX_RESOLUTION; r++) {
            RESOLUTION_TAGS[r] = r == 0 ? 1L << 57 : r == 1 ? 1L << 56 : 1L << Math.max(59 - 2 * r, 0);
            SLOT_COUNTS[r] = r == 0 ? ORIGIN_SLOTS : r == 1 ? QUINTANT_SLOTS : 1L << (60 - 2 * r);
        }
    }

    /** Res-30 IDs end in ...1, ...100 or ...10000: their tag has one of these bits */
    public static final long RES30_TAG_BITS = 0b10101L;

    /** The tags of resolutions 2-29: the odd bits 55 down to 1 */
    private static final long CURVE_TAG_BITS;
    static {
        long bits = 0L;
        for (int r = FIRST_CURVE_RESOLUTION; r < MAX_RESOLUTION; r++) {
            bits |= RESOLUTION_TAGS[r];
        }
        CURVE_TAG_BITS = bits;
    }

    private Serialization() {
    }

    /**
     * The first slot a cell occupies. Throws if the value is not an A5 cell ID:
     * its tag (lowest set bit) must be a resolution tag, and its origin (res 0) or
     * quintant (res 1-29) must exist. Every res-30 pattern decodes to an existing
     * quintant (0-41).
     *
     * @param cell cell id (unsigned)
     * @return first slot (unsigned)
     * @throws IllegalArgumentException if the value is not a cell id
     */
    public static long cellFirstSlot(long cell) {
        // The resolution tag: the lowest set bit (0 for the world cell)
        final long tag = cell & -cell;
        // unsigned: the tag of 0x8000000000000000 is 2^63
        if (Long.compareUnsigned(tag, RESOLUTION_TAGS[1]) < 0) {
            if ((tag & RES30_TAG_BITS) != 0L) return res30ToSlot(cell);
            // Resolutions 2-29, in quintants 0-59: the first slot is the ID without its tag
            if ((tag & CURVE_TAG_BITS) != 0L && Long.compareUnsigned(cell, WORLD_SLOTS) < 0) return cell - tag;
            if (tag == 0L) return 0L;
        } else {
            // Resolution 0 (tag bit 57) starts its origin's 5 quintants, 1 (bit 56) its quintant
            final long top = cell >>> QUINTANT_SHIFT;
            if (tag == RESOLUTION_TAGS[0] && top < 12L) return (5L * top) << QUINTANT_SHIFT;
            if (tag == RESOLUTION_TAGS[1] && top < 60L) return top << QUINTANT_SHIFT;
        }
        throw invalidCell(cell);
    }

    /**
     * The first slot of a cell, without checking that the value is an A5 cell ID:
     * for searches, which check the cell they land on. A value that is not a cell
     * gives a meaningless slot.
     *
     * <p>Java: like the Rust port, the meaningless slot of an invalid res-0 pattern (origin &gt; 12)
     * wraps to 64 bits where TS would produce a bigint beyond 2^64.</p>
     *
     * @param cell cell id (unsigned)
     * @return first slot (unsigned)
     */
    public static long cellFirstSlotUnchecked(long cell) {
        final long tag = cell & -cell;
        if (tag == 0L) return 0L;
        if (Long.compareUnsigned(tag, RESOLUTION_TAGS[1]) >= 0) {
            final long top = cell >>> QUINTANT_SHIFT;
            return (tag == RESOLUTION_TAGS[0] ? 5L * top : top) << QUINTANT_SHIFT;
        }
        if ((tag & RES30_TAG_BITS) != 0L) return res30ToSlot(cell);
        return cell - tag;
    }

    /**
     * Check whether a 64-bit value is an A5 cell ID: its tag (lowest set bit) must
     * be a resolution tag, and its origin (res 0) or quintant (res 1-29) must exist.
     * The world cell counts as a cell: it has no boundary, but {@code cellToParent} gives
     * it as the parent of the res-0 cells. A compaction marker is not a cell.
     *
     * @param cell cell id (unsigned)
     * @return whether the value is a cell id
     */
    public static boolean isValidCell(long cell) {
        // TS: if (cell <= 0n || cell > U64_MAX) return cell === WORLD_CELL;
        // An unsigned 64-bit long is never negative nor above U64_MAX: only 0 remains.
        if (cell == 0L) return cell == WORLD_CELL;
        final long tag = cell & -cell;
        final int bit = Long.numberOfTrailingZeros(tag);
        // Resolutions 2-29 (odd bits 55-1), in quintants 0-59, or 30 (bits 4, 2, 0)
        if (bit < 56) return bit % 2 == 1 ? Long.compareUnsigned(cell, WORLD_SLOTS) < 0 : bit <= 4;
        // Resolution 0 (bit 57) of origins 0-11, or 1 (bit 56) of quintants 0-59
        final long top = cell >>> QUINTANT_SHIFT;
        return (bit == 57 && top < 12L) || (bit == 56 && top < 60L);
    }

    /**
     * The resolution of a cell, as {@code getResolution} gives it, but throwing if the
     * value is not an A5 cell ID (see {@code isValidCell}).
     *
     * @param cell cell id (unsigned)
     * @return resolution, -1 for the world cell
     * @throws IllegalArgumentException if the value is not a cell id
     */
    public static int checkedResolution(long cell) {
        final long tag = cell & -cell;
        if (tag == 0L) return -1;
        final int bit = Long.numberOfTrailingZeros(tag);
        if (bit < 56) {
            if (bit % 2 == 1 && Long.compareUnsigned(cell, WORLD_SLOTS) < 0) return (59 - bit) >> 1;
            if (bit <= 4 && bit % 2 == 0) return MAX_RESOLUTION;
        } else {
            final long top = cell >>> QUINTANT_SHIFT;
            if (bit == 57 && top < 12L) return 0;
            if (bit == 56 && top < 60L) return 1;
        }
        throw invalidCell(cell);
    }

    private static IllegalArgumentException invalidCell(long cell) {
        return new IllegalArgumentException("Invalid cell: 0x" + Long.toUnsignedString(cell, 16));
    }

    /**
     * The number of slots a cell occupies.
     *
     * @param cell cell id (unsigned)
     * @return number of slots (unsigned, {@link #WORLD_SLOTS} for the world cell)
     */
    public static long cellSlotCount(long cell) {
        // The resolution tag: the lowest set bit (0 for the world cell)
        final long tag = cell & -cell;
        if (tag == 0L) return WORLD_SLOTS;
        if (tag == RESOLUTION_TAGS[0]) return ORIGIN_SLOTS;
        if (tag == RESOLUTION_TAGS[1]) return QUINTANT_SLOTS;
        if ((tag & RES30_TAG_BITS) != 0L) return 1L;
        // Resolutions 2-29: the slots are symmetric about the ID
        return tag << 1;
    }

    /**
     * The res-r cell whose block of slots starts at {@code slot}.
     *
     * @param slot first slot (unsigned)
     * @param resolution cell resolution
     * @return cell id (unsigned)
     */
    public static long slotToCell(long slot, int resolution) {
        if (resolution < 0) return WORLD_CELL;
        if (resolution > 0 && resolution < MAX_RESOLUTION) return slot + RESOLUTION_TAGS[resolution];
        if (resolution == 0) return (((slot >>> QUINTANT_SHIFT) / 5L) << QUINTANT_SHIFT) | RESOLUTION_TAGS[0];
        return slotToRes30(slot);
    }

    /**
     * @param index cell id (unsigned)
     * @return resolution, -1 for the world cell
     */
    public static int getResolution(long index) {
        // The resolution tag: the lowest set bit (0 for the world cell). Its position
        // gives the resolution: bit 57 is res 0, 56 res 1, 59 - 2r res r (2-29), and
        // res 30 uses the patterns ...1, ...100 and ...10000 (bits 0, 2 and 4).
        final long tag = index & -index;
        if (tag == 0L) return -1;
        final int bit = Long.numberOfTrailingZeros(tag);
        if (bit == 57) return 0;
        if (bit == 56) return 1;
        if (bit <= 4 && bit % 2 == 0) return MAX_RESOLUTION;
        return (59 - bit) >> 1;
    }

    /**
     * @param index cell id (unsigned)
     * @return decoded cell
     * @throws IllegalArgumentException if the value is not a cell id
     */
    public static A5Cell deserialize(long index) {
        final int resolution = getResolution(index);

        // Technically not a resolution, but can be useful to think of as an
        // abstract cell that contains the whole world
        if (resolution == -1) {
            return new A5Cell(origins.get(0), 0, 0L, resolution);
        }

        // The cell's first slot holds its quintant, then its S above the slots of one cell
        final long slot = cellFirstSlot(index);
        final int quintant = (int) (slot >>> QUINTANT_SHIFT);
        final Origin origin = origins.get(quintant / 5);
        if (resolution == 0) return new A5Cell(origin, 0, 0L, resolution);

        final int segment = (quintant + origin.firstQuintant()) % 5;
        // both operands < 2^58: signed division is exact
        final long S = resolution < FIRST_CURVE_RESOLUTION ? 0L : (slot & S_MASK) / SLOT_COUNTS[resolution];
        return new A5Cell(origin, segment, S, resolution);
    }

    /**
     * @param cell decoded cell
     * @return cell id (unsigned)
     * @throws IllegalArgumentException if the resolution or S is too large
     */
    public static long serialize(A5Cell cell) {
        final Origin origin = cell.origin();
        final int segment = cell.segment();
        final long S = cell.S();
        final int resolution = cell.resolution();
        if (resolution > MAX_RESOLUTION) {
            throw new IllegalArgumentException("Resolution (" + resolution + ") is too large");
        }

        if (resolution == -1) return WORLD_CELL;
        if (resolution == 0) return slotToCell((5L * origin.id()) * QUINTANT_SLOTS, 0);

        // The cell's first slot: its quintant, then S cells of this resolution into it
        final long offset = resolution >= FIRST_CURVE_RESOLUTION ? S * SLOT_COUNTS[resolution] : 0L;
        // BigInt products never overflow: the unsigned S test rejects the S whose 64 bits product
        // wraps (Rust checked_mul), it is exact since SLOT_COUNTS[r] is a power of 2 dividing QUINTANT_SLOTS.
        if (Long.compareUnsigned(offset, QUINTANT_SLOTS) >= 0
                || (resolution >= FIRST_CURVE_RESOLUTION
                    && Long.compareUnsigned(S, QUINTANT_SLOTS / SLOT_COUNTS[resolution]) >= 0)) {
            throw new IllegalArgumentException("S (" + Long.toUnsignedString(S) + ") is too large for resolution level " + resolution);
        }

        final int quintant = 5 * origin.id() + ((segment - origin.firstQuintant() + 5) % 5);
        // Quintants past RES30_QUINTANTS have no res-30 IDs: fall back to res 29
        if (resolution == MAX_RESOLUTION && quintant >= RES30_QUINTANTS) {
            return serialize(new A5Cell(origin, segment, S >>> 2, MAX_RESOLUTION - 1));
        }
        return slotToCell((((long) quintant) << QUINTANT_SHIFT) + offset, resolution);
    }

    // The segments of an origin in ID (quintant) order, by its firstQuintant
    private static final int[][] QUINTANT_SEGMENTS = new int[5][5];
    static {
        for (int first = 0; first < 5; first++) {
            for (int n = 0; n < 5; n++) {
                QUINTANT_SEGMENTS[first][n] = (n + first) % 5;
            }
        }
    }

    /**
     * The children of a cell at the next resolution, in ascending ID order.
     *
     * @param index cell id (unsigned)
     * @return children cell ids
     */
    public static long[] cellToChildren(long index) {
        return cellToChildrenInternal(index, null);
    }

    /**
     * The children of a cell at {@code childResolution}, in ascending ID order.
     *
     * @param index cell id (unsigned)
     * @param childResolution target resolution
     * @return children cell ids
     */
    public static long[] cellToChildren(long index, int childResolution) {
        return cellToChildrenInternal(index, childResolution);
    }

    /**
     * The children of a cell at {@code childResolution} (default: the next resolution),
     * in ascending ID order.
     *
     * @throws ArithmeticException if the children do not fit in a Java array
     */
    private static long[] cellToChildrenInternal(long index, Integer childResolution) {
        final A5Cell cell = deserialize(index);
        final Origin origin = cell.origin();
        final int segment = cell.segment();
        final long S = cell.S();
        final int currentResolution = cell.resolution();
        final int newResolution = childResolution != null ? childResolution : currentResolution + 1;

        if (newResolution < currentResolution) {
            throw new IllegalArgumentException(
                    "Target resolution (" + newResolution + ") must be equal to or greater than current resolution (" + currentResolution + ")");
        }

        if (newResolution > MAX_RESOLUTION) {
            throw new IllegalArgumentException("Target resolution (" + newResolution + ") exceeds maximum resolution (" + MAX_RESOLUTION + ")");
        }

        // If target resolution equals current resolution, return the original cell
        if (newResolution == currentResolution) {
            return new long[]{index};
        }

        List<Origin> newOrigins = List.of(origin);
        if (currentResolution == -1) {
            newOrigins = origins;
        }
        final boolean allSegments = (currentResolution == -1 && newResolution > 0) || currentResolution == 0;

        final int resolutionDiff = newResolution - Math.max(currentResolution, FIRST_CURVE_RESOLUTION - 1);
        // TS: Math.pow(4, resolutionDiff), its loop runs once for a negative diff (world cell to res 0)
        final long childrenCount = resolutionDiff <= 0 ? 1L : 1L << (2 * resolutionDiff);
        // TS: S << BigInt(2 * resolutionDiff), a right shift of S = 0 when negative
        final long shiftedS = resolutionDiff <= 0 ? S : S << (2 * resolutionDiff);
        final long[] children = new long[Math.toIntExact(
                Math.multiplyExact(childrenCount, (long) newOrigins.size() * (allSegments ? 5 : 1)))];
        int n = 0;
        for (final Origin newOrigin : newOrigins) {
            // An origin's quintants in ID order: the n-th is segment (n + firstQuintant) % 5
            final int[] newSegments = allSegments ? QUINTANT_SEGMENTS[newOrigin.firstQuintant()] : new int[]{segment};
            for (final int newSegment : newSegments) {
                for (long i = 0; i < childrenCount; i++) {
                    final long newS = shiftedS + i;
                    children[n++] = serialize(new A5Cell(newOrigin, newSegment, newS, newResolution));
                }
            }
        }

        return children;
    }

    /** Whether a cell is at resolution 30: its tag is one of ...1, ...100 or ...10000. */
    private static boolean isMaxResolution(long index) {
        return (index & -index & RES30_TAG_BITS) != 0L;
    }

    /**
     * Re-pack a res-30 cell into the standard res-29 bit layout (6-bit quintant
     * in [63..58], 56-bit S in [57..2], tag at bit 1). The 58-bit res-30 S is
     * truncated by 2 bits, exactly as {@code cellToParent(_, 29)} would.
     */
    private static long normalizeRes30(long index) {
        // The res-29 parent starts at the same slot, rounded down to its 4 children
        return (res30ToSlot(index) & ~3L) | 0b10L;
    }

    /**
     * The immediate parent of a cell (the world cell for a res-0 cell).
     *
     * @param index cell id (unsigned)
     * @return parent cell id (unsigned)
     */
    public static long cellToParent(long index) {
        return cellToParent(index, getResolution(index) - 1, false);
    }

    /**
     * Walk a cell up the hierarchy to a coarser resolution (or its own, giving the
     * cell). A finer resolution is an error.
     *
     * Implemented as pure bit ops over the encoded index — no deserialize /
     * serialize round-trip. The three encoding regimes (non-curve res 0/1,
     * curve res 2..29, variable-width res 30) all reduce to the same shape
     * after a small amount of normalization.
     *
     * @param index cell id (unsigned)
     * @param parentResolution target resolution
     * @return parent cell id (unsigned)
     * @throws IllegalArgumentException if the target resolution is out of range or finer than the cell
     */
    public static long cellToParent(long index, int parentResolution) {
        return cellToParent(index, parentResolution, true);
    }

    /**
     * @param check TS {@code parentResolution !== undefined}: the immediate parent (undefined)
     *        is never finer than the cell, only an explicit target needs the check.
     */
    private static long cellToParent(long index, int parentResolution, boolean check) {
        // Special case: parent of resolution 0 cells is the world cell
        if (parentResolution == -1) return WORLD_CELL;
        if (parentResolution < -1 || parentResolution > MAX_RESOLUTION) {
            throw new IllegalArgumentException("Target resolution (" + parentResolution + ") is out of range");
        }
        // A cell coarser than the target has no parent there. Each branch below
        // detects that from bits it already reads, so the common case never pays
        // for getResolution.
        if (index == WORLD_CELL) throw finerTarget(index, parentResolution);

        // Normalize res-30 children to the standard res-29 layout. After this,
        // the fast paths below treat the cell as a curve-range cell.
        long c = index;
        if (isMaxResolution(index)) {
            if (parentResolution == MAX_RESOLUTION) return index; // identity (already res 30)
            c = normalizeRes30(index);
            if (parentResolution == MAX_RESOLUTION - 1) return c;
        }

        if (parentResolution >= FIRST_CURVE_RESOLUTION) {
            // Curve-range parent: clear bits below the parent tag, set the tag.
            // Identity (parent res === child res) falls out for free: the tag lands
            // in the same position and bits below the keep cut are already zero.
            // A cell at the parent resolution or finer has its tag below the cut;
            // a coarser cell has no bits there (at resolution 30 the cut is bit 0,
            // so every cell not already res 30 is coarser).
            final int keepShift = 60 - 2 * parentResolution;
            final long kept = (c >>> keepShift) << keepShift;
            if (check && kept == c) throw finerTarget(index, parentResolution);
            // Java: parentResolution 30 never reaches this line (kept == c always, and an
            // implicit target is never 30), so the negative shift 59 - 60 does not occur.
            return kept | (1L << (59 - 2 * parentResolution));
        }

        // A res-0 cell has bit 57 set with all lower bits zero — equivalently, all
        // bottom 57 bits are zero.
        final boolean isRes0 = (c & ((1L << 57) - 1L)) == 0L;

        if (parentResolution == 1) {
            if (isRes0) throw finerTarget(index, parentResolution);
            // Top 6 bits already encode 5*originId + segmentN; only the tag moves.
            // Identity (cell already at res 1) is preserved.
            return ((c >>> 58) << 58) | (1L << 56);
        }

        // parentResolution === 0: top 6 bits change from quintant (0-59) to originId (0-11).
        // Identity (cell already at res 0) needs an explicit guard since dividing
        // an originId by 5 would corrupt it.
        if (isRes0) return c;
        return (((c >>> 58) / 5L) << 58) | (1L << 57);
    }

    /** The error for a target resolution finer than the cell's own. */
    private static IllegalArgumentException finerTarget(long index, int parentResolution) {
        return new IllegalArgumentException(
                "Target resolution (" + parentResolution + ") must be equal to or less than current resolution (" + getResolution(index) + ")");
    }

    // The 12 resolution-0 cells (dodecahedron faces) are a constant — compute once.
    // (TS lazy init, eager static final in Java.)
    private static final long[] RES0_CELLS = cellToChildren(WORLD_CELL, 0);

    /**
     * Returns resolution 0 cells of the A5 system, which serve as a starting point
     * for all higher-resolution subdivisions in the hierarchy.
     *
     * @return Array of 12 cell indices (a new copy)
     */
    public static long[] getRes0Cells() {
        return RES0_CELLS.clone();
    }

    /**
     * Bit-level descendant test: is {@code child} the same cell as {@code parent}, or one of
     * its descendants at any deeper resolution? Compares the high (quintant +
     * parent's curve) bits in a single shift, no deserialize needed.
     *
     * Restricted to the curve range: {@code parentResolution} must be in
     * [FIRST_CURVE_RESOLUTION .. MAX_RESOLUTION - 1], and {@code child} must not be
     * a resolution-30 cell (whose encoding uses a variable quintant shift).
     * Callers handling those cases should fall back to {@code cellToParent} equality.
     *
     * @param child cell id (unsigned)
     * @param parent cell id (unsigned)
     * @param parentResolution resolution of the parent
     * @return whether child is parent or one of its descendants
     */
    public static boolean isChildOf(long child, long parent, int parentResolution) {
        // Parent's identifying bits occupy positions 63..(60-2P): 6 quintant bits
        // + 2(P-1) curve bits. Bit (59-2P) is the tag, below that is zero.
        // Shifting both right by (60-2P) keeps exactly those identifying bits and
        // discards the tag, so a descendant matches iff the high bits match.
        final int shift = 60 - 2 * parentResolution;
        return child >>> shift == parent >>> shift;
    }
}
