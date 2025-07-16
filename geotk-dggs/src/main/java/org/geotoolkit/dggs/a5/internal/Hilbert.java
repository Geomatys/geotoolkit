/**
 * Copyright (C) 2025 Geomatys and Felix Palmer
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
package org.geotoolkit.dggs.a5.internal;

import java.util.Arrays;
import org.apache.sis.geometries.math.Vector2D;
import org.apache.sis.util.ArraysExt;

/**
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public final class Hilbert {

    private Hilbert(){}

    public static final int YES = -1;
    public static final int NO = 1;

    public static final class Anchor {
        public final int k; //Quaternary
        public final Vector2D.Double offset;
        public final int[] flips;

        public Anchor(int k, Vector2D.Double offset, int[] flips) {
            this.k = k;
            this.offset = offset;
            this.flips = flips;
        }

        @Override
        public String toString() {
            return "Anchor{" + "k=" + k + ", offset=" + offset + ", flips=" + Arrays.toString(flips) + '}';
        }
    }

    // Anchor offset is specified in ij units, the eigenbasis of the Hilbert curve
    // Define k as the vector i + j, as it means vectors u & v are of unit length
    public static Vector2D.Double IJToKJ(Vector2D.Double ij) {
        return new Vector2D.Double(ij.x + ij.y, ij.y);
    }

    public static Vector2D.Double KJToIJ(Vector2D.Double kj) {
        return new Vector2D.Double(kj.x - kj.y, kj.y);
    }

    /**
     * Orientation of the Hilbert curve. The curve fills a space defined by the triangle with vertices
     * u, v & w. The orientation describes which corner the curve starts and ends at, e.g. wv is a
     * curve that starts at w and ends at v.
     */
    public static enum Orientation {
        uv,
        vu,
        uw,
        wu,
        vw,
        wv
    }

    // Using KJ allows simplification of definitions
    public static final Vector2D.Double kPos = new Vector2D.Double(1, 0); // k
    public static final Vector2D.Double jPos = new Vector2D.Double(0, 1); // j
    public static final Vector2D.Double kNeg = new Vector2D.Double(-1,0);
    public static final Vector2D.Double jNeg = new Vector2D.Double(0,-1);
    public static final Vector2D.Double ZERO = new Vector2D.Double(0, 0);

    public static Vector2D.Double quaternaryToKJ(int n, int ... flips) {
        // Indirection to allow for flips
        Vector2D.Double p = ZERO;
        Vector2D.Double q = ZERO;

        if (flips[0] == NO && flips[1] == NO) {
            p = kPos;
            q = jPos;
        } else if (flips[0] == YES && flips[1] == NO) {
            // Swap and negate
            p = jNeg;
            q = kNeg;
        } else if (flips[0] == NO && flips[1] == YES) {
            // Swap only
            p = jPos;
            q = kPos;
        } else if (flips[0] == YES && flips[1] == YES) {
            // Negate only
            p = kNeg;
            q = jNeg;
        }

        switch(n) {
            case 0:
                return ZERO; // Length 0
            case 1:
                return p; // Length 1
            case 2:
                return q.copy().add(p); // Length SQRT2
            case 3:
                return q.copy().add(p.copy().scale(2)); // Length SQRT5
            default:
                throw new IllegalArgumentException("Invalid Quaternary value " + n);
        }
    }

    public static int[] quaternaryToFlips(int n) {
        switch(n) {
            case 0:
                return new int[]{NO, NO};
            case 1:
                return new int[]{NO, YES};
            case 2:
                return new int[]{NO, NO};
            case 3:
                return new int[]{YES, NO};
            default:
                throw new IllegalArgumentException("Invalid Quaternary value " + n);
        }
    }

    private static final Vector2D.Double FLIP_SHIFT = new Vector2D.Double(-1, 1); //IJ

    // Patterns used to rearrange the cells when shifting. This adjusts the layout so that
    // children always overlap with their parent cells.
    private static int[] reversePattern(int[] pattern) {
        final int[] reverse = new int[pattern.length];
        loop:
        for (int i = 0; i < reverse.length; i++) {
            for (int k = 0; k < pattern.length; k++) {
                if (pattern[k] == i) {
                    reverse[i] = k;
                    continue loop;
                }
            }
            throw new IllegalStateException();
        }
        return reverse;
    }

    private static final int[] PATTERN = new int[]{0, 1, 3, 4, 5, 6, 7, 2};
    private static final int[] PATTERN_FLIPPED = new int[]{0, 1, 2, 7, 3, 4, 5, 6};
    private static final int[] PATTERN_REVERSED = reversePattern(PATTERN);
    private static final int[] PATTERN_FLIPPED_REVERSED = reversePattern(PATTERN_FLIPPED);

    private static void shiftDigits(
            int[] digits, //Quaternary
            int i,
            int[] flips,
            boolean invertJ,
            int[] pattern
          ){
        if (i <= 0) return;

        final int parentK = i > digits.length ? 0 : digits[i];
        final int childK = digits[i - 1];
        final int F = flips[0] + flips[1];

        // Detect when cells need to be shifted
        boolean needsShift = true;
        boolean first = true;

        // The value of F which cells need to be shifted
        // The rule is flipped depending on the orientation, specifically on the value of invertJ
        if (invertJ != (F == 0)) {
            needsShift = parentK == 1 || parentK == 2; // Second & third pentagons only
            first = parentK == 1; // Second pentagon is first
        } else {
            needsShift = parentK < 2; // First two pentagons only
            first = parentK == 0; // First pentagon is first
        }
        if (!needsShift) return;

        // Apply the pattern by setting the digits based on the value provided
        int src = first ? childK : childK + 4;
        int dst = pattern[src];
        digits[i - 1] = dst % 4;
        digits[i] = (parentK + 4 + (int)Math.floor(dst / 4) - (int)Math.floor(src / 4)) % 4;
    }

    public static Anchor sToAnchor(long s, int resolution, Orientation orientation) {
        long input = s;
        final boolean reverse = orientation == Orientation.vu || orientation == Orientation.wu || orientation == Orientation.vw;
        final boolean invertJ = orientation == Orientation.wv || orientation == Orientation.vw;
        final boolean flipIJ = orientation == Orientation.wu || orientation == Orientation.uw;
        if (reverse) {
            input = (1l << (long)(2 * resolution)) - input - 1l;
        }
        final Anchor anchor = sToAnchor(input, resolution, invertJ, flipIJ);
        if (flipIJ) {
            final double ox = anchor.offset.x;
            final double oy = anchor.offset.y;
            anchor.offset.x = oy;
            anchor.offset.y = ox;

            // The flips moved the origin of the cell, shift to compensate
            if (anchor.flips[0] == YES) anchor.offset.add(FLIP_SHIFT);
            if (anchor.flips[1] == YES) anchor.offset.subtract(FLIP_SHIFT);
        }
        if (invertJ) {
            final double i = anchor.offset.x;
            final double _j = anchor.offset.y;

            double j = (1 << resolution) - (i + _j);
            anchor.offset.y = j;
            anchor.flips[0] = -anchor.flips[0];
        }
        return anchor;
    }

    public static Anchor sToAnchor(long s, int resolution, boolean invertJ, boolean flipIJ) {
        final Vector2D.Double offset = new Vector2D.Double();
        final int[] flips = new int[]{NO, NO};
        long input = s;

        // Get all quaternary digits first
        int[] digits = new int[0];
        while (input > 0 || digits.length < resolution) {
            digits = ArraysExt.concatenate(digits, new int[]{(int)(input % 4l)});
            input = input >> 2l;
        }

        final int[] pattern = flipIJ ? PATTERN_FLIPPED : PATTERN;

        // Process digits from left to right (most significant first)
        for (int i = digits.length - 1; i >= 0; i--) {
            shiftDigits(digits, i, flips, invertJ, pattern);
            int[] q = quaternaryToFlips(digits[i]);
            flips[0] *= q[0];
            flips[1] *= q[1];
        }

        flips[0] = NO; flips[1] = NO; // Reset flips for the next loop
        for (int i = digits.length - 1; i >= 0; i--) {
            // Scale up existing anchor
            offset.scale(2);

            // Get child anchor and combine with current anchor
            Vector2D.Double childOffset = quaternaryToKJ(digits[i], flips);
            offset.add(childOffset);
            int[] q = quaternaryToFlips(digits[i]);
            flips[0] *= q[0];
            flips[1] *= q[1];
        }

        final int k = digits.length > 0 ? digits[0] : 0;
        return new Anchor(k, KJToIJ(offset), flips);
    }

    // Get the number of digits needed to represent the offset
    // As we don't know the flips we need to add 2 to include the next row
    public static int getRequiredDigits(Vector2D.Double offset) {
        double indexSum = Math.ceil(offset.x) + Math.ceil(offset.y); // TODO perhaps use floor instead
        if (indexSum == 0) return 1;
        return (int) (1 + Math.floor(Math.log(indexSum) / Math.log(2)));
    }

    // This function uses the ij basis, unlike its inverse!
    public static int IJtoQuaternary(Vector2D.Double uv, int ... flips) {
        int digit = 0;

        // Boundaries to compare against
        final double a = flips[0] == YES ? -(uv.x + uv.y) : uv.x + uv.y;
        final double b = flips[1] == YES ? -uv.x : uv.x;
        final double c = flips[0] == YES ? -uv.y : uv.y;

        // Only one flip
        if (flips[0] + flips[1] == 0) {
            if (c < 1) { digit = 0; }
            else if (b > 1) { digit = 3; }
            else if (a > 1) { digit = 2; }
            else { digit = 1; }
        // No flips or both
        } else {
            if (a < 1) { digit = 0; }
            else if (b > 1) { digit = 3; }
            else if (c > 1) { digit = 2; }
            else { digit = 1; }
        }

        return digit;
    }

    public static long IJToS(Vector2D.Double input, int resolution, Orientation orientation) {
        final boolean reverse = orientation == Orientation.vu || orientation == Orientation.wu || orientation == Orientation.vw;
        final boolean invertJ = orientation == Orientation.wv || orientation == Orientation.vw;
        final boolean flipIJ = orientation == Orientation.wu || orientation == Orientation.uw;

        final Vector2D.Double ij = new Vector2D.Double(input);
        if (flipIJ) {
            ij.x = input.y;
            ij.y = input.x;
        }
        if (invertJ) {
            ij.y = (1 << resolution) - (ij.x + ij.y);
        }

        long S = IJToS(ij, invertJ, flipIJ, resolution);
        if (reverse) {
            S = (1l << (long)(2 * resolution)) - S - 1l;
        }
        return S;
    }

    public static long IJToS (Vector2D.Double input, boolean invertJ, boolean flipIJ, int resolution) {
        // Get number of digits we need to process
        final int numDigits = resolution;
        final int[] digits = new int[numDigits];

        final int[] flips = new int[]{NO, NO};
        final Vector2D.Double pivot = new Vector2D.Double() ;

        // Process digits from left to right (most significant first)
        for (int i = numDigits - 1; i >= 0; i--) {
            final Vector2D.Double relativeOffset = input.copy().subtract(pivot);

            final int scale = 1 << i;
            final Vector2D.Double scaledOffset = relativeOffset.copy().scale(1.0 / scale);

            final int digit = IJtoQuaternary(scaledOffset, flips);
            digits[i] = digit;

            // Update running state
            final Vector2D.Double childOffset = KJToIJ(quaternaryToKJ(digit, flips));
            final Vector2D.Double upscaledChildOffset = childOffset.copy().scale(scale);
            pivot.add(upscaledChildOffset);
            int[] q = quaternaryToFlips(digit);
            flips[0] *= q[0];
            flips[1] *= q[1];
        }

        final int[] pattern = flipIJ ? PATTERN_FLIPPED_REVERSED : PATTERN_REVERSED;

        for (int i = 0; i < digits.length; i++) {
            int[] q = quaternaryToFlips(digits[i]);
            flips[0] *= q[0];
            flips[1] *= q[1];
            shiftDigits(digits, i, flips, invertJ, pattern);
        }

        long output = 0;
        for (int i = numDigits - 1; i >= 0; i--) {
            long scale = 1l << (2l * i);
            output += (long)(digits[i]) * scale;
        }

        return output;
    }
}
