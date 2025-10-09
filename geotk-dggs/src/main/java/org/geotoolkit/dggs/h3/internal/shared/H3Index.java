/*
 * Copyright 2016-2021, 2024 Uber Technologies, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.geotoolkit.dggs.h3.internal.shared;

import java.util.stream.LongStream;

/**
 * Extract cell informations.
 *
 * This class do not use the JNI bindings.
 *
 *
 * @author Johann Sorel (Geomatys)
 * @see https://h3geo.org/docs/core-library/h3Indexing
 * @see https://observablehq.com/@nrabinowitz/h3-index-inspector?collection=@nrabinowitz/h3
 * @see https://github.com/uber/h3/blob/master/src/h3lib/lib/h3Index.c
 */
public final class H3Index {

    /** The number of bits in a single H3 resolution digit. */
    static final int H3_PER_DIGIT_OFFSET = 3;
    /**
     * H3 digit representing ijk+ axes direction.
     * Values will be within the lowest 3 bits of an integer.
     */
    /** H3 digit in center */
    public static final int CENTER_DIGIT = 0;
    /** H3 digit in k-axes direction */
    public static final int K_AXES_DIGIT = 1;
    /** H3 digit in j-axes direction */
    public static final int J_AXES_DIGIT = 2;
    /** H3 digit in j == k direction */
    public static final int JK_AXES_DIGIT = J_AXES_DIGIT | K_AXES_DIGIT; /* 3 */
    /** H3 digit in i-axes direction */
    public static final int I_AXES_DIGIT = 4;
    /** H3 digit in i == k direction */
    public static final int IK_AXES_DIGIT = I_AXES_DIGIT | K_AXES_DIGIT; /* 5 */
    /** H3 digit in i == j direction */
    public static final int IJ_AXES_DIGIT = I_AXES_DIGIT | J_AXES_DIGIT; /* 6 */
    /** H3 digit in the invalid direction */
    public static final int INVALID_DIGIT = 7;
    /** Valid digits will be less than this value. Same value as INVALID_DIGIT. */
    public static final int NUM_DIGITS = INVALID_DIGIT;
    /** Child digit which is skipped for pentagons */
    public static final int PENTAGON_SKIPPED_DIGIT = K_AXES_DIGIT; /* 1 */

    private static final long MASK_MODE         = 0b0111100000000000000000000000000000000000000000000000000000000000l;
    private static final long MASK_MODE_DEP     = 0b0000011100000000000000000000000000000000000000000000000000000000l;
    private static final long MASK_RESOLUTION   = 0b0000000011110000000000000000000000000000000000000000000000000000l;
    private static final long MASK_BASECELL     = 0b0000000000001111111000000000000000000000000000000000000000000000l;
    private static final long[] MASK_DIGITS     = new long[]{
        0b0000000000000000000111000000000000000000000000000000000000000000l,
        0b0000000000000000000000111000000000000000000000000000000000000000l,
        0b0000000000000000000000000111000000000000000000000000000000000000l,
        0b0000000000000000000000000000111000000000000000000000000000000000l,
        0b0000000000000000000000000000000111000000000000000000000000000000l,
        0b0000000000000000000000000000000000111000000000000000000000000000l,
        0b0000000000000000000000000000000000000111000000000000000000000000l,
        0b0000000000000000000000000000000000000000111000000000000000000000l,
        0b0000000000000000000000000000000000000000000111000000000000000000l,
        0b0000000000000000000000000000000000000000000000111000000000000000l,
        0b0000000000000000000000000000000000000000000000000111000000000000l,
        0b0000000000000000000000000000000000000000000000000000111000000000l,
        0b0000000000000000000000000000000000000000000000000000000111000000l,
        0b0000000000000000000000000000000000000000000000000000000000111000l,
        0b0000000000000000000000000000000000000000000000000000000000000111l
        };
    private static final int[] MASK_DIGITS_OFFSET     = new int[]{
        42,
        39,
        36,
        33,
        30,
        27,
        24,
        21,
        18,
        15,
        12,
        9,
        6,
        3,
        0
        };

    private H3Index(){}

    /**
     * Mode 0 : reserved and indicates an invalid H3 index.
     * Mode 1 : an H3 Cell (Hexagon/Pentagon) index.
     * Mode 2 : an H3 Directed Edge (Cell A -> Cell B) index.
     * Mode 3 : planned to be a bidirectional edge (Cell A <-> Cell B).
     * Mode 4 : an H3 Vertex (i.e. a single vertex of an H3 Cell).
     *
     * @param index cell id
     * @return mode
     */
    public static int getMode(long index) {
        return (int) ((index & MASK_MODE) >>> 59);
    }

    /**
     * Mode 0 : -
     * Mode 1 : all set to 0.
     * Mode 2 : indicate the edge (1-6) of the origin cell.
     * Mode 3 : -
     * Mode 4 : indicate the vertex number (0-5) of vertex on the owner cell.
     *
     * @param index cell id
     * @return mode specific value
     */
    public static int getModeDependent(long index) {
        return (int) ((index & MASK_MODE_DEP) >>> 56);
    }

    /**
     * Get cell resolution 0-15.
     *
     * @param index cell id
     * @return resolution
     */
    public static int getResolution(long index) {
        return (int) ((index & MASK_RESOLUTION) >>> 52);
    }

    /**
     * Set cell resolution
     *
     * @param index cell id
     * @param resolution new resolution, int range 0-15.
     * @return new cell id
     */
    public static long setResolution(long index, int resolution) {
        return (index & ~MASK_RESOLUTION) | (((long) resolution) << 52);
    }

    /**
     * 7 bits to indicate the base cell 0-121.
     *
     * @param index cell id
     * @return base cell
     */
    public static int getBaseCellNumber(long index) {
        final long v = index & MASK_BASECELL;
        return (int) (v >>> 45);
    }

    /**
     * 3 bits to indicate each subsequent digit 0-6 from resolution 1 up to the resolution
     * of the cell (45 bits total are reserved for resolutions 1-15).
     *
     * The three bits for each unused digit are set to 7.
     *
     * @param index cell id
     * @return subdivision
     */
    public static int[] getIndexDigits(long index) {
        return new int[] {
            (int) ((index & MASK_DIGITS[0] ) >>> MASK_DIGITS_OFFSET[0]),
            (int) ((index & MASK_DIGITS[1] ) >>> MASK_DIGITS_OFFSET[1]),
            (int) ((index & MASK_DIGITS[2] ) >>> MASK_DIGITS_OFFSET[2]),
            (int) ((index & MASK_DIGITS[3] ) >>> MASK_DIGITS_OFFSET[3]),
            (int) ((index & MASK_DIGITS[4] ) >>> MASK_DIGITS_OFFSET[4]),
            (int) ((index & MASK_DIGITS[5] ) >>> MASK_DIGITS_OFFSET[5]),
            (int) ((index & MASK_DIGITS[6] ) >>> MASK_DIGITS_OFFSET[6]),
            (int) ((index & MASK_DIGITS[7] ) >>> MASK_DIGITS_OFFSET[7]),
            (int) ((index & MASK_DIGITS[8] ) >>> MASK_DIGITS_OFFSET[8]),
            (int) ((index & MASK_DIGITS[9] ) >>> MASK_DIGITS_OFFSET[9]),
            (int) ((index & MASK_DIGITS[10]) >>> MASK_DIGITS_OFFSET[10]),
            (int) ((index & MASK_DIGITS[11]) >>> MASK_DIGITS_OFFSET[11]),
            (int) ((index & MASK_DIGITS[12]) >>> MASK_DIGITS_OFFSET[12]),
            (int) ((index & MASK_DIGITS[13]) >>> MASK_DIGITS_OFFSET[13]),
            (int) ((index & MASK_DIGITS[14]) >>> MASK_DIGITS_OFFSET[14])
        };
    }

    /**
     * 3 bits to indicate each subsequent digit 0-6 from resolution 1 up to the resolution
     * of the cell (45 bits total are reserved for resolutions 1-15).
     *
     * The three bits for each unused digit are set to 7.
     *
     * @param index cell id
     * @param resolution in range 1-15
     * @return subdivision
     */
    public static int getIndexDigit(long index, int resolution) {
        if (resolution < 1 || resolution > 15) throw new IllegalArgumentException("Invalid resolution level : " + resolution);
        resolution--;
        return (int) ((index & MASK_DIGITS[resolution] ) >>> MASK_DIGITS_OFFSET[resolution]);
    }

    /**
     *
     * @param index cell idx
     * @param resolution digit resolution
     * @param digit new digit value
     * @return new cell id
     */
    public static long setIndexDigit(long index, int resolution, int digit) {
        if (resolution < 1 || resolution > 15) throw new IllegalArgumentException("Invalid resolution level : " + resolution);
        resolution--;
        return (index & ~MASK_DIGITS[resolution] ) | (((long) digit) << MASK_DIGITS_OFFSET[resolution]);

    }

    /**
     * Get String cell id.
     */
    public static final String h3ToString(final long hash) {
        return Long.toHexString(hash);
    }

    /**
     * Get long cell id.
     */
    public static final long stringToH3(final CharSequence cs) {
        return Long.parseUnsignedLong(cs, 0, cs.length(), 16);
    }

    /**
     * @return true if cell is a pentagon
     */
    public static final boolean isPentagon(long index) {
        return BaseCells.isBaseCellPentagon(getBaseCellNumber(index))
            && (h3LeadingNonZeroDigit(index) == 0);
    }

    /**
    * cellToParent produces the parent index for a given H3 index
    *
    * @param index H3Index to find parent of
    * @param parentRes The resolution to switch to (parent, grandparent, etc)
    */
   public static long cellToParent(long index, int parentRes) {
       final int childRes = getResolution(index);
       if (parentRes < 0 || parentRes > Constants.MAX_H3_RES) {
           throw new IllegalArgumentException("Parent resolution not in range 0-15");
       } else if (parentRes > childRes) {
           throw new IllegalArgumentException("Parent resolution superior to cell resolution");
       } else if (parentRes == childRes) {
           return index;
       }
       long parentH = setResolution(index, parentRes);
       for (int i = parentRes + 1; i <= childRes; i++) {
           parentH = setIndexDigit(parentH, i, INVALID_DIGIT);
       }
       return parentH;
   }

   /**
    * cellToChildrenSize returns the exact number of children for a cell at a
    * given child resolution.
    *
    * @param index         H3Index to find the number of children of
    * @param childRes  The child resolution you're interested in
    */
    public static long cellToChildrenSize(long index, int childRes) {
        if (childRes >= Constants.MAX_H3_RES) return 0;
        final int n = childRes - getResolution(index);

        if (isPentagon(index)) {
            return 1 + 5 * (Constants.ipow(7, n) - 1) / 6;
        } else {
            return Constants.ipow(7, n);
        }
    }

    /**
     * cellToChildren takes the given cell id and generates all of the children at the specified resolution.
     *
     * @param index H3Index to find the children of
     * @param childRes int the child level to produce
     */
    public static LongStream cellToChildren(long index, int childRes) {
        return Iterators.iterInitParent(index, childRes);
    }

    /**
     * Returns the highest resolution non-zero digit in an H3Index.
     *
     * @param h The H3Index.
     * @return The highest resolution non-zero digit in the H3Index.
     */
    static int h3LeadingNonZeroDigit(long h) {
        for (int r = 1; r <= getResolution(h); r++) {
            final int digit = getIndexDigit(h, r);
            if (digit != 0) {
                return digit;
            }
        }
        return CENTER_DIGIT;
    }

    /**
     * Zero out index digits from start to end, inclusive.
     * No-op if start > end.
     */
    static long zeroIndexDigits(long h, int start, int end) {
        if (start > end) return h;

        long m = 0;

        m = ~m;
        m <<= H3_PER_DIGIT_OFFSET * (end - start + 1);
        m = ~m;
        m <<= H3_PER_DIGIT_OFFSET * (Constants.MAX_H3_RES - end);
        m = ~m;

        return h & m;
    }
}
