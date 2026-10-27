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
package org.geotoolkit.dggs.a5.internal.lattice.lsystem;

import java.util.EnumMap;
import java.util.Map;
import org.geotoolkit.dggs.a5.internal.lattice.Orientation;
import org.geotoolkit.dggs.a5.internal.lattice.Triple;
import org.geotoolkit.dggs.a5.internal.lattice.lsystem.Turtle.AB;
import static org.geotoolkit.dggs.a5.internal.lattice.lsystem.Tables.POW2;

/**
 * The A5 space-filling curve, a turtle L-system on the triangular lattice.
 *
 * This replaces the earlier `shiftDigits` construction. shiftDigits was an
 * approximation of this curve: they agree exactly through resolution 4, but
 * shiftDigits self-intersects from resolution 5 on, whereas this curve never
 * crosses itself at any resolution while tiling the exact same cells with the same
 * metacell hierarchy. (The old curve remains available bit-for-bit via Compat,
 * which runs the original two-motif grammar + the shiftDigits digit recode through
 * the same descents below.)
 *
 * The curve is a vertex-to-vertex turtle L-system on the triangular lattice: 7
 * self-referential motifs (A B C M P Q R), each a clean A5 unit (2 parallelograms
 * + 2 triangles). The symbolic grammar lives in {@link Grammar} and is compiled
 * to flat tables in {@link Tables}; this class evaluates it as an O(resolution)
 * digit transducer:
 * <ul>
 *   <li>forward  s -&gt; cell   : descend the quaternary digits, accumulating a turtle
 *            position + heading, then map (a,b) -&gt; A5 triple via a fixed
 *            similarity; the leaf state also yields the cell's pentagon flavor.</li>
 *   <li>inverse  triple -&gt; s : descend picking, at each level, the child whose convex
 *            footprint (triforce / parallelogram) contains the target cell.</li>
 * </ul>
 *
 * Every turn in every rule is 180° (see Tables), so the descent
 * tracks orientation as a single flip bit; for the A5 grammar that invariant
 * is also what keeps every parallelogram cell on-axis.
 *
 * The 6 orientations are just which triforce motif is the axiom (uv-&gt;A, uw-&gt;C,
 * vw-&gt;B) + whether the curve is reversed; A and C fill the quintant from corner
 * C0, B fills it corner-to-corner so it's translated onto the quintant. See ORIENT.
 *
 * <p>Port of upstream {@code lattice/lsystem/index.ts}.
 * Upstream {@code bigint} s-values are {@code long}: s &lt; 2<sup>2·resolution</sup> &lt;= 2<sup>60</sup>,
 * so they are always positive. Upstream splits s into a 26 bits {@code lo} and a double {@code hi}
 * part to avoid BigInt arithmetic per level; this port reads and writes the digits directly in the
 * {@code long}, with identical results.</p>
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public final class LSystem {

    /**
     * Forward descent result: leaf host cell corner sum + flavor (see {@link #axiomLeafCell}).
     *
     * @param a corner sum a
     * @param b corner sum b
     * @param flavor pentagon flavor
     */
    public record LeafCell(double a, double b, int flavor) {
    }

    /**
     * Inverse descent result: the curve position and the leaf flavor
     * (upstream {@code [bigint, number]} tuple).
     *
     * @param s curve position
     * @param flavor leaf pentagon flavor
     */
    public record CurvePosition(long s, int flavor) {
    }

    /**
     * Result of {@link #tripleToCurveNode(Triple, int, Orientation)}.
     *
     * @param s curve position
     * @param flavor pentagon flavor
     * @param node descent state below the cell
     */
    public record CurveNodeResult(long s, int flavor, CurveNode node) {
    }

    /** The compiled A5 grammar. */
    private static final CurveTables A5 = Tables.compileGrammar(Grammar.RULES, Grammar.DRAWS);

    private LSystem() {
    }

    /*
     * Branchless child pick: 3 separator dot products form a 3-bit pattern that
     * indexes the per-state lookup table (no data-dependent branches). Used on the
     * exact path, where the target is strictly interior at every level.
     */
    // A real cell's corner sum lies exactly on a separator (0, exact in floats) or
    // at least 48 corner-sum units off it, at every level: the gap is between
    // integer points and fixed lines, so it does not shrink as the scale grows. A
    // threshold scaled with the level (as the footprints' BSP_EPS is)
    // would swallow that gap by level 28 and pick the wrong child.
    private static final double CLASSIFY_THRESHOLD = -1;

    private static int classify(CurveTables t, int state, double relA, double relB, double scale) {
        final double[] s = t.classSep;
        final int b = state * 9;
        final double thr = CLASSIFY_THRESHOLD;
        final int b0 = s[b] * relA + s[b + 1] * relB + s[b + 2] * scale >= thr ? 1 : 0;
        final int b1 = s[b + 3] * relA + s[b + 4] * relB + s[b + 5] * scale >= thr ? 1 : 0;
        final int b2 = s[b + 6] * relA + s[b + 7] * relB + s[b + 8] * scale >= thr ? 1 : 0;
        return t.classLut[state * 8 + (b0 | (b1 << 1) | (b2 << 2))] & 0xFF;
    }

    // s <-> quaternary digits: upstream uses two float halves (`lo` digits 0-12, `hi` the rest)
    // to cross the BigInt boundary once per call. In Java, digit idx of s is (s >>> 2*idx) & 3.

    /** Formats a number as JS would in a template literal (integers without fraction). */
    private static String num(double v) {
        return v == Math.rint(v) && Math.abs(v) < 1e21 ? Long.toString((long) v) : Double.toString(v);
    }

    // ---------- exact (a,b) corner-sum <-> A5 triple ----------
    // The turtle (a,b) lattice and A5's triple frame are two views of the same
    // triangular grid. Composing them, the √3 from each basis cancels, leaving an
    // exact rational map: from a cell's corner sum (= 3·centroid),
    //   y - z      = (2·sum.a + sum.b - 12) / 12
    //   2x - y - z = (sum.b + 4) / 4
    // and the parity x+y+z ∈ {0,1} pins x, y, z. No floating point.
    public static Triple abToTriple(double sumA, double sumB) {
        if ((2 * sumA + sumB) % 12 != 0 || sumB % 4 != 0) {
            throw new IllegalArgumentException("abToTriple: off-lattice corner sum (" + num(sumA) + "," + num(sumB) + ")");
        }
        // x = (2x - y - z + parity) / 3 is an integer, which pins the parity
        final double parity = (((-(sumB + 4) / 4) % 3) + 3) % 3;
        final int[] triple = {0, 0, 0};
        abToTripleInto(sumA, sumB, parity, triple);
        if (parity > 1 || triple[0] + triple[1] + triple[2] != parity) {
            throw new IllegalArgumentException("abToTriple: no integer triple for (" + num(sumA) + "," + num(sumB) + ")");
        }
        return new Triple(triple[0], triple[1], triple[2]);
    }

    /**
     * `abToTriple` for a corner sum whose parity is known, written to `out`: no
     * search and no checks.
     *
     * @param out triple [x, y, z] to write to
     */
    private static void abToTripleInto(double sumA, double sumB, double parity, int[] out) {
        final double x = ((sumB + 4) / 4 + parity) / 3;
        final double r = parity - x; // y + z
        final double yz = (2 * sumA + sumB - 12) / 12; // y - z
        // The quotients are exact integers (upstream truncates with `| 0`)
        out[0] = (int) x;
        out[1] = (int) ((r + yz) / 2);
        out[2] = (int) ((r - yz) / 2);
    }

    public static AB tripleToAB(Triple t) {
        final long b = 4L * (2L * t.x() - t.y() - t.z()) - 4;
        return new AB((12L * ((long) t.y() - t.z()) + 12 - b) / 2, b);
    }

    // ---------- forward: s -> leaf host cell (corner sum + flavor) ----------
    // A child placed at (parent-relative) offUnit under a `flip` frame has its
    // offset negated when flipped (180°); the child's own frame is
    // `flip XOR child.flip`. Internal; also used by Compat.
    public static LeafCell axiomLeafCell(CurveTables t, long s, int R, int axiom) {
        final int[] childToken = t.childToken;
        final byte[] childFlip = t.childFlip;
        final double[] childOffA = t.childOffA;
        final double[] childOffB = t.childOffB;
        final double[] leafSum = t.leafSum;
        final byte[] leafFlavor = t.leafFlavor;
        int motif = axiom,
            flip = 0;
        double posA = 0,
               posB = 0;
        for (int L = R; L >= 2; L--) {
            final int idx = L - 1;
            final int d = (int) ((s >>> (idx << 1)) & 3);
            final int ci = motif * 4 + d;
            final double scale = flip != 0 ? -POW2[L - 2] : POW2[L - 2];
            posA += childOffA[ci] * scale;
            posB += childOffB[ci] * scale;
            flip ^= childFlip[ci] & 0xFF;
            motif = childToken[ci];
        }
        // level 1: leaf walk (from heading 0 or 3), take the d0-th host cell
        final int d0 = R >= 1 ? (int) (s & 3) : 0;
        final int base = motif * 2 + flip;
        return new LeafCell(
            3 * posA + leafSum[base * 8 + d0 * 2],
            3 * posB + leafSum[base * 8 + d0 * 2 + 1],
            leafFlavor[base * 4 + d0] & 0xFF);
    }

    /**
     * {@link #axiomTargetToS(CurveTables, double, double, int, int, CurveNode)} without the below state.
     */
    public static CurvePosition axiomTargetToS(CurveTables t, double ta, double tb, int R, int axiom) {
        return axiomTargetToS(t, ta, tb, R, axiom, null);
    }

    // ---------- inverse: descend by the branchless child classifier ----------
    // Shared descent: the target is the corner sum of a real cell, which is
    // strictly interior at every level, so the branchless classifier is provably
    // the containing child (and the leaf resolves by exact sum match). Fractional
    // point location no longer descends at all — sphericalToCell rounds to a triple
    // (see Curve.roundToTriple). Internal; also used by Compat.
    //
    // Returns [s, leafFlavor]. Callers that only need `s` take [0]. With `below`,
    // also writes the descent state below the leaf there (see curveChild).
    public static CurvePosition axiomTargetToS(CurveTables t, double ta, double tb, int R, int axiom, CurveNode below) {
        final int[] childToken = t.childToken;
        final byte[] childFlip = t.childFlip;
        final double[] childOffA = t.childOffA;
        final double[] childOffB = t.childOffB;
        final double[] leafSum = t.leafSum;
        int motif = axiom,
            flip = 0;
        double posA = 0,
               posB = 0;
        long s = 0;
        for (int L = R; L >= 2; L--) {
            final double scale = POW2[L - 2];
            final double sign = flip != 0 ? -scale : scale;
            final int bestD = classify(t, motif * 2 + flip, ta - 3 * posA, tb - 3 * posB, scale);
            final int ci = motif * 4 + bestD;
            posA += childOffA[ci] * sign;
            posB += childOffB[ci] * sign;
            flip ^= childFlip[ci] & 0xFF;
            motif = childToken[ci];
            final int idx = L - 1;
            s |= ((long) bestD) << (idx << 1);
        }
        // level 1: pick the leaf cell by exact corner-sum match
        final int base = motif * 2 + flip;
        final double relA = ta - 3 * posA,
                     relB = tb - 3 * posB;
        int d0 = -1;
        for (int d = 0; d < 4; d++) {
            if (leafSum[base * 8 + d * 2] == relA && leafSum[base * 8 + d * 2 + 1] == relB) {
                d0 = d;
                break;
            }
        }
        if (d0 < 0) throw new IllegalArgumentException("lsystem inverse: no leaf match for corner sum (" + num(ta) + "," + num(tb) + ")");
        s |= d0;
        if (below != null) stepBelow(t, motif, flip, posA, posB, d0, below);
        return new CurvePosition(s, t.leafFlavor[base * 4 + d0] & 0xFF);
    }

    // ---------- orientation = which triforce motif is the axiom ----------
    // Each orientation is one of the three triforce motifs used as the axiom
    // (uv->A, uw->C, wv->B), with the reverse orientations (vu, wu, vw) walking the
    // same curve backward (s -> N-1-s). A and C fill the quintant from corner C0
    // (tau 0); B fills it corner-to-corner (C1<->C2), so it's translated onto the
    // quintant by tau = (-p, p, 0) in triple units, p = 2^resolution — which is
    // (+12p, -12p) in the corner-sum frame.
    private record OrientRecipe(int axiom, boolean reverse, boolean isB) {
    }

    private static final Map<Orientation, OrientRecipe> ORIENT = new EnumMap<>(Orientation.class);
    static {
        ORIENT.put(Orientation.uv, new OrientRecipe(A5.motifIdx.get("A"), false, false));
        ORIENT.put(Orientation.vu, new OrientRecipe(A5.motifIdx.get("A"), true, false));
        ORIENT.put(Orientation.uw, new OrientRecipe(A5.motifIdx.get("C"), false, false));
        ORIENT.put(Orientation.wu, new OrientRecipe(A5.motifIdx.get("C"), true, false));
        ORIENT.put(Orientation.vw, new OrientRecipe(A5.motifIdx.get("B"), true, true));
        ORIENT.put(Orientation.wv, new OrientRecipe(A5.motifIdx.get("B"), false, true));
    }

    /**
     * The flavor of the single cell at resolution 0. There the leaf is the axiom
     * itself, whose flavor reflects the axiom rather than the cell, so it is fixed
     * instead. The single cell is the quintant's corner cell [-maxRow, maxRow, 0],
     * which touches a dodecahedron vertex and is flavor 2 at every resolution, so
     * the corner cells keep one orientation all the way down. This orientation is
     * also the one where none of its 4 children lies entirely outside it, and the
     * one tripleFlavor gives it (see Triples).
     */
    public static final int LEVEL0_FLAVOR = 2;

    /**
     * {@link #sToCell(long, int, Orientation)} with the default {@code uv} orientation.
     */
    public static Cell sToCell(long s, int resolution) {
        return sToCell(s, resolution, Orientation.uv);
    }

    /**
     * The A5 curve position `s` -&gt; cell (triple coordinate + pentagon flavor), for
     * a given resolution and orientation. The triple is bijective with
     * {@link #tripleToSLattice}.
     */
    public static Cell sToCell(long s, int resolution, Orientation orientation) {
        if (resolution == 0) return new Cell(new Triple(0, 0, 0), LEVEL0_FLAVOR);
        final long N = 1L << (2 * resolution);
        final OrientRecipe rec = ORIENT.get(orientation);
        final long sAxiom = rec.reverse ? N - 1 - s : s;
        final LeafCell cell = axiomLeafCell(A5, sAxiom, resolution, rec.axiom);
        final Triple base = abToTriple(cell.a, cell.b);
        if (!rec.isB) return new Cell(base, cell.flavor);
        final int p = (int) POW2[resolution];
        return new Cell(new Triple(base.x() - p, base.y() + p, base.z()), cell.flavor);
    }

    /**
     * {@link #sToTriple(long, int, Orientation)} with the default {@code uv} orientation.
     */
    public static Triple sToTriple(long s, int resolution) {
        return sToTriple(s, resolution, Orientation.uv);
    }

    /**
     * The A5 curve position `s` -&gt; triple coordinate, for a given resolution and
     * orientation. Bijective with {@link #tripleToSLattice}.
     */
    public static Triple sToTriple(long s, int resolution, Orientation orientation) {
        return sToCell(s, resolution, orientation).triple();
    }

    /**
     * {@link #tripleToSLattice(Triple, int, Orientation)} with the default {@code uv} orientation.
     */
    public static long tripleToSLattice(Triple triple, int resolution) {
        return tripleToSLattice(triple, resolution, Orientation.uv);
    }

    /**
     * Triple coordinate -&gt; the A5 curve position `s`, for a given resolution and
     * orientation. Inverse of {@link #sToTriple}.
     *
     * @throws IllegalArgumentException if the triple is not a cell of the quintant
     */
    public static long tripleToSLattice(Triple triple, int resolution, Orientation orientation) {
        return tripleToCurve(triple, resolution, orientation, null).s;
    }

    /** `tripleToSLattice`, also giving the leaf flavor and, with `below`, the descent state below the cell. */
    private static CurvePosition tripleToCurve(Triple triple, int resolution, Orientation orientation, CurveNode below) {
        final long N = 1L << (2 * resolution);
        final OrientRecipe rec = ORIENT.get(orientation);
        final AB ab = tripleToAB(triple);
        final double tauSum = rec.isB ? 12 * POW2[resolution] : 0;
        final CurvePosition result = axiomTargetToS(A5, ab.a() - tauSum, ab.b() + tauSum, resolution, rec.axiom, below);
        if (rec.reverse) return new CurvePosition(N - 1 - result.s, result.flavor);
        return result;
    }

    // ---------- stepwise descent: the cell hierarchy in curve order ----------
    // A walk down the hierarchy reads one digit per level, so rather than a full
    // O(resolution) descent per cell it carries each cell's descent state and
    // takes each child in O(1). The state below a cell is the one its children's
    // digits are read from: the motif, flip and turtle position after the cell's
    // own digits, the position in units of the children's level (one level down,
    // it doubles).

    // The parity (x + y + z) of each leaf cell by (motif, flip, digit): a cell's
    // position only translates its leaf by lattice vectors, which keep the parity.
    private static final byte[] LEAF_PARITY = new byte[A5.leafFlavor.length];
    static {
        for (int i = 0; i < LEAF_PARITY.length; i++) {
            final Triple t = abToTriple(A5.leafSum[2 * i], A5.leafSum[2 * i + 1]);
            LEAF_PARITY[i] = (byte) (t.x() + t.y() + t.z());
        }
    }

    /**
     * The child with curve digit `digit` (0-3, the child's last digit of s) of the
     * cell whose descent state is `node`: writes its triple at `resolution` (the
     * child's) to `triple` and the descent state below it to `below` (which may be
     * `node` itself), and returns its flavor. Agrees with `sToCell` on the child's s.
     *
     * @param triple output triple as an {@code int[3]} array [x, y, z] (upstream mutable {x,y,z} object)
     */
    public static int curveChild(CurveNode node, int digit, int resolution, Orientation orientation, int[] triple, CurveNode below) {
        final double[] leafSum = A5.leafSum;
        final byte[] leafFlavor = A5.leafFlavor;
        final OrientRecipe rec = ORIENT.get(orientation);
        // A reversed curve reads s as N - 1 - s: every digit complemented
        final int d = rec.reverse ? 3 - digit : digit;
        final int motif = node.motif, flip = node.flip;
        final double posA = node.posA, posB = node.posB;
        final int base = motif * 2 + flip;
        // The parity is known up front (see LEAF_PARITY): no search
        final double sumA = 3 * posA + leafSum[base * 8 + d * 2];
        final double sumB = 3 * posB + leafSum[base * 8 + d * 2 + 1];
        abToTripleInto(sumA, sumB, LEAF_PARITY[base * 4 + d] & 0xFF, triple);
        if (rec.isB) {
            triple[0] -= (int) POW2[resolution];
            triple[1] += (int) POW2[resolution];
        }
        stepBelow(A5, motif, flip, posA, posB, d, below);
        return leafFlavor[base * 4 + d] & 0xFF;
    }

    /**
     * The descent state below the child with (axiom-order) digit `d` of the cell
     * whose descent state is (motif, flip, posA, posB), written to `below`.
     */
    private static void stepBelow(CurveTables t, int motif, int flip, double posA, double posB, int d, CurveNode below) {
        final int ci = motif * 4 + d;
        final int sign = flip != 0 ? -1 : 1;
        below.motif = t.childToken[ci];
        below.flip = flip ^ (t.childFlip[ci] & 0xFF);
        below.posA = 2 * posA + t.childOffA[ci] * sign;
        below.posB = 2 * posB + t.childOffB[ci] * sign;
    }

    /**
     * A cell's curve position `s`, flavor and the descent state below it, from its
     * triple: where a walk down to the cell by `curveChild` would arrive, in one
     * descent rather than one step per level.
     */
    public static CurveNodeResult tripleToCurveNode(Triple triple, int resolution, Orientation orientation) {
        // Below the resolution-0 cell (the whole quintant) is the axiom itself
        final CurveNode node = new CurveNode(ORIENT.get(orientation).axiom, 0, 0, 0);
        if (resolution == 0) return new CurveNodeResult(0, LEVEL0_FLAVOR, node);
        final CurvePosition r = tripleToCurve(triple, resolution, orientation, node);
        return new CurveNodeResult(r.s, r.flavor, node);
    }
}
