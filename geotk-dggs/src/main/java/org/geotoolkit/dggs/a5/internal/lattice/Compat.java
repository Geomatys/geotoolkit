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
package org.geotoolkit.dggs.a5.internal.lattice;

import java.util.EnumMap;
import java.util.Map;
import org.geotoolkit.dggs.a5.internal.lattice.lsystem.Cell;
import org.geotoolkit.dggs.a5.internal.lattice.lsystem.CurveTables;
import org.geotoolkit.dggs.a5.internal.lattice.lsystem.Grammar;
import org.geotoolkit.dggs.a5.internal.lattice.lsystem.LSystem;
import org.geotoolkit.dggs.a5.internal.lattice.lsystem.Tables;
import org.geotoolkit.dggs.a5.internal.lattice.lsystem.Turtle.AB;
import static org.geotoolkit.dggs.a5.internal.lattice.lsystem.LSystem.abToTriple;
import static org.geotoolkit.dggs.a5.internal.lattice.lsystem.LSystem.axiomLeafCell;
import static org.geotoolkit.dggs.a5.internal.lattice.lsystem.LSystem.axiomTargetToS;
import static org.geotoolkit.dggs.a5.internal.lattice.lsystem.LSystem.tripleToAB;
import static org.geotoolkit.dggs.a5.internal.lattice.lsystem.Tables.POW2;

/**
 * The ORIGINAL A5 curve (the shiftDigits construction), expressed on top of the
 * L-system machinery — bit-for-bit compatible with the pre-L-system library.
 *
 * The old construction is two layers, and both are preserved here exactly:
 * <ol>
 * <li>The base ordering is a simple two-motif L-system (the "original quaternary
 *    curve" the A5 curve grew out of), verified index-for-index equal to the
 *    old raw descent. Its native form uses ±60° turns
 *    (X: X-Y+X++Y--  Y: Y+X-Y--X++, draws X -&gt; E, Y -&gt; -e+), but it re-gauges
 *    into the 180°-only form the table compiler requires — the ±60° is
 *    absorbed into Z's leaf gauge (a walk-identical re-gauging):
 *      W: W+++Z---WZ   Z: Z+++W---ZW    (draws W -&gt; E, Z -&gt; +e-)</li>
 * <li>On top of it, the shiftDigits digit recode (ported verbatim below), which
 *    rearranges children so they overlap their parent cells — the "hierarchy
 *    fix" that introduced the self-intersections the new curve removes.</li>
 * </ol>
 *
 * Orientations follow the old engine exactly: reverse remaps s -&gt; N-1-s,
 * flipIJ (uw/wu) selects the flipped pattern and mirrors the raw cell
 * (x &lt;-&gt; z in triple space), invertJ (vw/wv) flips the quintant vertically
 * ((x,y,z) -&gt; (y-(n-1), x+(n-1), z), n = 2^res). Both maps are self-inverse.
 *
 * Everything here was validated bit-for-bit against the original engine:
 * 101k cells (triples, flavors, round-trips) + 84k interior encode points,
 * all orientations, and the full public API with compat wired in as the curve
 * (45k comparisons vs the pre-L-system build, zero differences). The
 * fractional point-location (compatIJToS) and the flavor derivation are the
 * old engine's own logic ported verbatim, so they agree on boundary
 * tie-breaks too. The compat fixtures (tests/fixtures/lattice/compat.json)
 * pin this behavior.
 *
 * <p>Port of upstream {@code lattice/compat.ts}.</p>
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public final class Compat {

    /** The compiled two-motif grammar of the original curve (W/Z gauge). */
    private static final CurveTables ORIGINAL = Tables.compileGrammar(
            Grammar.orderedMap("W", "W+++Z---WZ", "Z", "Z+++W---ZW"),
            Grammar.orderedMap("W", "E", "Z", "+e-"));
    private static final int AXIOM_W = ORIGINAL.motifIdx.get("W");

    private Compat() {
    }

    // ---------- shiftDigits (ported verbatim from the original construction) ----------
    // Patterns used to rearrange the cells when shifting. This adjusts the layout
    // so that children always overlap with their parent cells.
    private static int[] reversePattern(int[] pattern) {
        final int[] result = new int[pattern.length];
        for (int i = 0; i < pattern.length; i++) {
            int idx = -1;
            for (int k = 0; k < pattern.length; k++) {
                if (pattern[k] == i) {
                    idx = k;
                    break;
                }
            }
            result[i] = idx;
        }
        return result;
    }
    private static final int[] PATTERN = {0, 1, 3, 4, 5, 6, 7, 2};
    private static final int[] PATTERN_FLIPPED = {0, 1, 2, 7, 3, 4, 5, 6};
    private static final int[] PATTERN_REVERSED = reversePattern(PATTERN);
    private static final int[] PATTERN_FLIPPED_REVERSED = reversePattern(PATTERN_FLIPPED);

    private static void shiftDigits(int[] digits, int i, int[] flips, boolean invertJ, int[] pattern) {
        if (i <= 0) return;

        final int parentK = digits[i];
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
        final int src = first ? childK : childK + 4;
        final int dst = pattern[src];
        digits[i - 1] = dst % 4;
        digits[i] = (parentK + 4 + Math.floorDiv(dst, 4) - Math.floorDiv(src, 4)) % 4;
    }

    // the flips product accumulates per digit exactly as quaternaryToFlips did:
    // digit 1 flips the second component, digit 3 the first
    private static void applyDigitFlips(int[] flips, int d) {
        if (d == 1) flips[1] = -flips[1];
        else if (d == 3) flips[0] = -flips[0];
    }

    /**
     * old s digits -&gt; geometric (X/Y curve) digits, in place. LSB-first array.
     * Returns the final flips product over the shifted digits — the old engine's
     * anchor `flips` state, from which the pentagon flavor follows in closed form
     * (see compatFlavor).
     */
    private static int[] forwardShift(int[] digits, boolean invertJ, boolean flipIJ) {
        final int[] pattern = flipIJ ? PATTERN_FLIPPED : PATTERN;
        final int[] flips = {1, 1};
        for (int i = digits.length - 1; i >= 0; i--) {
            shiftDigits(digits, i, flips, invertJ, pattern);
            applyDigitFlips(flips, digits[i]);
        }
        return flips;
    }

    /**
     * geometric (X/Y curve) digits -&gt; old s digits, in place. LSB-first array.
     * The flips state starts as the product over ALL digits and each iteration
     * cancels digit i's contribution — so at step i it holds the product of the
     * digits ABOVE i, matching the forward pass's state at the same level (this
     * mirrors how the original engine carried the flips of its geometric descent
     * into its reordering loop without resetting them).
     */
    private static void inverseShift(int[] digits, boolean invertJ, boolean flipIJ) {
        final int[] pattern = flipIJ ? PATTERN_FLIPPED_REVERSED : PATTERN_REVERSED;
        final int[] flips = {1, 1};
        for (int i = 0; i < digits.length; i++) applyDigitFlips(flips, digits[i]);
        for (int i = 0; i < digits.length; i++) {
            applyDigitFlips(flips, digits[i]);
            shiftDigits(digits, i, flips, invertJ, pattern);
        }
    }

    private static int[] digitsOf(long s, int resolution) {
        // number of base-4 digits of the unsigned value s
        final int sDigits = s == 0 ? 0 : (64 - Long.numberOfLeadingZeros(s) + 1) / 2;
        final int[] digits = new int[Math.max(sDigits, resolution)];
        long v = s;
        for (int i = 0; i < digits.length; i++) {
            digits[i] = (int) (v & 3);
            v >>>= 2;
        }
        return digits;
    }

    private static long packDigits(int[] digits) {
        long s = 0;
        for (int i = digits.length - 1; i >= 0; i--) s = (s << 2) | digits[i];
        return s;
    }

    // ---------- orientations (as in the old engine) ----------
    private record CompatRecipe(boolean reverse, boolean invertJ, boolean flipIJ) {
    }

    private static final Map<Orientation, CompatRecipe> COMPAT_ORIENT = new EnumMap<>(Orientation.class);
    static {
        COMPAT_ORIENT.put(Orientation.uv, new CompatRecipe(false, false, false));
        COMPAT_ORIENT.put(Orientation.vu, new CompatRecipe(true, false, false));
        COMPAT_ORIENT.put(Orientation.uw, new CompatRecipe(false, false, true));
        COMPAT_ORIENT.put(Orientation.wu, new CompatRecipe(true, false, true));
        COMPAT_ORIENT.put(Orientation.vw, new CompatRecipe(true, true, false));
        COMPAT_ORIENT.put(Orientation.wv, new CompatRecipe(false, true, false));
    }

    /**
     * Pentagon flavor from the old engine's anchor state: the flips product over
     * the (shifted) digits and the leaf digit `q`. Ported from the old
     * getPentagonVertices orientation logic: flavor bit 0 (180° rotation) fired
     * iff `flips[1] === YES`; bit 1 (Y reflection) on the `(F, q)` predicate
     * below. This is why the compat decode needs no second (A5) descent — the
     * old engine's own fractal flips field carries the missing flavor bit.
     */
    private static int compatFlavor(int[] flips, int q) {
        final int rotate = flips[1] == -1 ? 1 : 0;
        final int F = flips[0] + flips[1];
        // Orient last two pentagons when both or neither flips are set,
        // first & last pentagons when exactly one is.
        final int reflect = (F == 0 ? q == 0 || q == 3 : q == 2 || q == 3) ? 1 : 0;
        return rotate | (reflect << 1);
    }

    /** Result of compatDescend: (triple, anchor flips, leaf digit). */
    private record Descent(Triple triple, int[] flips, int q) {
    }

    /** Shared forward descent: old s digits -&gt; (triple, anchor flips, leaf digit). */
    private static Descent compatDescend(long s, int resolution, CompatRecipe rec) {
        final long N = 1L << (2 * resolution);
        final long v = rec.reverse ? N - 1 - s : s;
        final int[] digits = digitsOf(v, resolution);
        final int[] flips = forwardShift(digits, rec.invertJ, rec.flipIJ);
        final LSystem.LeafCell raw = axiomLeafCell(ORIGINAL, packDigits(digits), resolution, AXIOM_W);
        Triple triple = abToTriple(raw.a(), raw.b());
        if (rec.flipIJ) {
            triple = new Triple(triple.z(), triple.y(), triple.x());
        }
        if (rec.invertJ) {
            final int n1 = (int) POW2[resolution] - 1;
            triple = new Triple(triple.y() - n1, triple.x() + n1, triple.z());
        }
        return new Descent(triple, flips, digits.length > 0 ? digits[0] : 0);
    }

    /**
     * {@link #compatSToTriple(long, int, Orientation)} with the default {@code uv} orientation.
     */
    public static Triple compatSToTriple(long s, int resolution) {
        return compatSToTriple(s, resolution, Orientation.uv);
    }

    /**
     * Old-curve position `s` -&gt; triple coordinate, via the ORIGINAL (W/Z) forward
     * descent + shiftDigits recode.
     */
    public static Triple compatSToTriple(long s, int resolution, Orientation orientation) {
        return compatDescend(s, resolution, COMPAT_ORIENT.get(orientation)).triple;
    }

    /**
     * {@link #compatSToCell(long, int, Orientation)} with the default {@code uv} orientation.
     */
    public static Cell compatSToCell(long s, int resolution) {
        return compatSToCell(s, resolution, Orientation.uv);
    }

    /** Old-curve position `s` -&gt; cell (triple + pentagon flavor). */
    public static Cell compatSToCell(long s, int resolution, Orientation orientation) {
        final CompatRecipe rec = COMPAT_ORIENT.get(orientation);
        final Descent desc = compatDescend(s, resolution, rec);
        final int[] flips = desc.flips;
        // As in the old engine's sToAnchor: invertJ flips the first component
        // (flipIJ leaves the flips untouched).
        if (rec.invertJ) {
            flips[0] = -flips[0];
        }
        return new Cell(desc.triple, compatFlavor(flips, desc.q));
    }

    /**
     * {@link #compatTripleToS(Triple, int, Orientation)} with the default {@code uv} orientation.
     */
    public static long compatTripleToS(Triple t, int resolution) {
        return compatTripleToS(t, resolution, Orientation.uv);
    }

    /**
     * Triple -&gt; old-curve position `s`, or {@link Triples#NO_S} (upstream: null)
     * if the triple has invalid parity.
     */
    public static long compatTripleToS(Triple t, int resolution, Orientation orientation) {
        final int sum = t.x() + t.y() + t.z();
        if (sum != 0 && sum != 1) return Triples.NO_S;
        final long N = 1L << (2 * resolution);
        final CompatRecipe rec = COMPAT_ORIENT.get(orientation);
        Triple raw = t;
        if (rec.invertJ) {
            final int n1 = (int) POW2[resolution] - 1;
            raw = new Triple(raw.y() - n1, raw.x() + n1, raw.z());
        }
        if (rec.flipIJ) {
            raw = new Triple(raw.z(), raw.y(), raw.x());
        }
        final AB ab = tripleToAB(raw);
        final long sGeo = axiomTargetToS(ORIGINAL, ab.a(), ab.b(), resolution, AXIOM_W).s();
        final int[] digits = digitsOf(sGeo, resolution);
        inverseShift(digits, rec.invertJ, rec.flipIJ);
        final long v = packDigits(digits);
        return rec.reverse ? N - 1 - v : v;
    }

    // ---------- fractional point-location (ported verbatim from the original engine) ----------
    // The old engine located a fractional point with a few sign tests per level
    // (IJToQuaternary) — far cheaper than the L-system's per-level hull scan
    // (~10-15x less work), and bit-identical by construction including its boundary
    // tie-breaks. The resulting digit stream is the geometric (X/Y curve) digit
    // stream, so the same inverseShift recode applies on top.

    /**
     * Which of the 4 children contains the scaled offset, under the current flips
     * (the old engine's IJToQuaternary, verbatim).
     */
    private static int ijToQuaternary(double u, double v, int[] flips) {
        // Boundaries to compare against
        final double a = flips[0] == -1 ? -(u + v) : u + v;
        final double b = flips[1] == -1 ? -u : u;
        final double c = flips[0] == -1 ? -v : v;

        if (flips[0] + flips[1] == 0) {
            // Only one flip
            if (c < 1) return 0;
            if (b > 1) return 3;
            return a > 1 ? 2 : 1;
        }
        // No flips or both
        if (a < 1) return 0;
        if (b > 1) return 3;
        return c > 1 ? 2 : 1;
    }

    /**
     * Child anchor offsets in IJ units, indexed by [flip combination][digit]
     * (= the old engine's KJToIJ(quaternaryToKJ(digit, flips))).
     * Flip index = (flips[0] === YES) + 2 * (flips[1] === YES).
     */
    private static final int[][][] CHILD_OFFSET_IJ = {
        {{0, 0}, {1, 0}, {0, 1}, {1, 1}},     // (NO, NO):   p = k, q = j
        {{0, 0}, {1, -1}, {0, -1}, {1, -2}},  // (YES, NO):  p = -j, q = -k
        {{0, 0}, {-1, 1}, {0, 1}, {-1, 2}},   // (NO, YES):  p = j, q = k
        {{0, 0}, {-1, 0}, {0, -1}, {-1, -1}}  // (YES, YES): p = -k, q = -j
    };

    /**
     * {@link #compatIJToS(double[], int, Orientation)} with the default {@code uv} orientation.
     */
    public static long compatIJToS(double[] ij, int resolution) {
        return compatIJToS(ij, resolution, Orientation.uv);
    }

    /**
     * Fractional IJ point -&gt; old-curve position `s` of the containing cell.
     *
     * @param ij IJ coordinates [i, j]
     */
    public static long compatIJToS(double[] ij, int resolution, Orientation orientation) {
        final long N = 1L << (2 * resolution);
        final CompatRecipe rec = COMPAT_ORIENT.get(orientation);
        double i = ij[0],
               j = ij[1];
        if (rec.flipIJ) {
            final double tmp = i;
            i = j;
            j = tmp;
        }
        if (rec.invertJ) {
            j = POW2[resolution] - (i + j);
        }

        // Geometric digits by direct point-location, most significant first.
        final int[] digits = new int[resolution];
        final int[] flips = {1, 1};
        double pivotI = 0;
        double pivotJ = 0;
        for (int lvl = resolution - 1; lvl >= 0; lvl--) {
            final double scale = 1 / POW2[lvl];
            final int digit = ijToQuaternary((i - pivotI) * scale, (j - pivotJ) * scale, flips);
            digits[lvl] = digit;

            final int fi = (flips[0] == -1 ? 1 : 0) + (flips[1] == -1 ? 2 : 0);
            final int[] offset = CHILD_OFFSET_IJ[fi][digit];
            pivotI += offset[0] * POW2[lvl];
            pivotJ += offset[1] * POW2[lvl];
            applyDigitFlips(flips, digit);
        }

        inverseShift(digits, rec.invertJ, rec.flipIJ);
        final long v = packDigits(digits);
        return rec.reverse ? N - 1 - v : v;
    }
}
