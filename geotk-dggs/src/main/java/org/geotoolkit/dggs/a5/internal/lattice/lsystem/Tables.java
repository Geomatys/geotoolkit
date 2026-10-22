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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.geotoolkit.dggs.a5.internal.lattice.lsystem.Turtle.AB;
import static org.geotoolkit.dggs.a5.internal.lattice.lsystem.Grammar.expandOnce;
import static org.geotoolkit.dggs.a5.internal.lattice.lsystem.Grammar.reverseMotif;
import static org.geotoolkit.dggs.a5.internal.lattice.lsystem.Turtle.hostCorners;
import static org.geotoolkit.dggs.a5.internal.lattice.lsystem.Turtle.hostSum;
import static org.geotoolkit.dggs.a5.internal.lattice.lsystem.Turtle.netOf;
import static org.geotoolkit.dggs.a5.internal.lattice.lsystem.Turtle.walk;

/**
 * Compiles an L-system grammar into flat numeric tables, once at class init,
 * so the descents in {@link LSystem} are pure scalar arithmetic: no string
 * expansion and no object allocation per call.
 *
 * Every grammar compiled here keeps every turn inside a rule at 180°
 * (`+++`/`---`), so a child is only ever placed un-flipped or flipped (rotated
 * 180°) relative to its parent — never at 60°. compileGrammar enforces that
 * invariant and records the orientation as a single `flip` bit (180° = negate
 * on this lattice); the whole descent then tracks orientation as one boolean.
 * For the A5 grammar the invariant is also what keeps every parallelogram cell
 * on-axis; the compat W/Z grammar is the original two-motif curve re-gauged
 * into this form (see Compat).
 *
 * Motif tokens are indexed by their position in the motif list (uppercase
 * motifs first, then their lowercase reverses); a descent state is
 * (motif index, flip bit). All hot-path lookups are flat typed-array reads
 * indexed by that state.
 *
 * <p>Port of upstream {@code lattice/lsystem/tables.ts}.</p>
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public final class Tables {

    public static final double BSP_EPS = 1e-6;

    /**
     * Powers of 2 used by the descents (index by level). Must not be modified.
     */
    public static final double[] POW2 = new double[32];
    /**
     * Powers of 4 used by the descents (index by digit position). Must not be modified.
     */
    public static final double[] POW4 = new double[20];
    // powers of 2 / 4 used by the descents (index by level / digit position)
    static {
        for (int i = 0; i < 32; i++) POW2[i] = Math.pow(2, i);
        for (int i = 0; i < 20; i++) POW4[i] = Math.pow(4, i);
    }

    private Tables() {
    }

    /** Binary space partition: {leaf} | {nx, ny, c, pos, neg}. */
    private static final class Bsp {
        /** Leaf digit, or -1 if not a leaf. */
        final int leaf;
        final double nx, ny, c;
        final Bsp pos, neg;

        Bsp(int leaf) {
            this.leaf = leaf;
            this.nx = this.ny = this.c = 0;
            this.pos = this.neg = null;
        }

        Bsp(double nx, double ny, double c, Bsp pos, Bsp neg) {
            this.leaf = -1;
            this.nx = nx;
            this.ny = ny;
            this.c = c;
            this.pos = pos;
            this.neg = neg;
        }

        boolean isLeaf() {
            return leaf >= 0;
        }
    }

    /** A child digit and its footprint polygon ([number, number[][]] in TS). */
    private record ChildPoly(int d, double[][] verts) {
    }

    /** The 4 child footprint polygons for state (motif, pflip), normalised (scale-invariant). */
    private static List<ChildPoly> childPolys(CurveTables t, int motif, int pflip) {
        final int psign = pflip != 0 ? -1 : 1;
        final List<ChildPoly> out = new ArrayList<>();
        for (int d = 0; d < 4; d++) {
            final int ci = motif * 4 + d;
            final int tok = t.childToken[ci];
            final int cfl = t.childFlip[ci] & 0xFF;
            final double oa = t.childOffA[ci];
            final double ob = t.childOffB[ci];
            final double[] edges = t.fpEdges[tok * 2 + (pflip ^ cfl)];
            final List<double[]> verts = new ArrayList<>();
            for (int e = 0; e < edges.length; e += 4) verts.add(new double[] {3 * oa * psign + edges[e], 3 * ob * psign + edges[e + 1]});
            out.add(new ChildPoly(d, verts.toArray(double[][]::new)));
        }
        return out;
    }

    /** Build a child-selection BSP; children tile convexly, so a polygon edge cleanly splits them. */
    private static Bsp buildBsp(List<ChildPoly> children) {
        if (children.size() == 1) return new Bsp(children.get(0).d);
        for (final ChildPoly child : children) {
            final double[][] poly = child.verts;
            final int n = poly.length;
            for (int i = 0; i < n; i++) {
                final double x1 = poly[i][0], y1 = poly[i][1];
                final double x2 = poly[(i + 1) % n][0], y2 = poly[(i + 1) % n][1];
                final double nx = y2 - y1,
                        ny = -(x2 - x1),
                        c = -(nx * x1 + ny * y1);
                final List<ChildPoly> pos = new ArrayList<>();
                final List<ChildPoly> neg = new ArrayList<>();
                boolean ok = true;
                for (final ChildPoly cp : children) {
                    double mn = Double.POSITIVE_INFINITY,
                            mx = Double.NEGATIVE_INFINITY;
                    for (final double[] xy : cp.verts) {
                        final double val = nx * xy[0] + ny * xy[1] + c;
                        if (val < mn) mn = val;
                        if (val > mx) mx = val;
                    }
                    if (mn >= -BSP_EPS) pos.add(cp);
                    else if (mx <= BSP_EPS) neg.add(cp);
                    else {
                        ok = false;
                        break;
                    }
                }
                if (ok && !pos.isEmpty() && !neg.isEmpty()) return new Bsp(nx, ny, c, buildBsp(pos), buildBsp(neg));
            }
        }
        throw new IllegalStateException("lsystem: no clean BSP split for child set");
    }

    private static void collectSeps(Bsp tree, List<double[]> seps) {
        if (tree.isLeaf()) return;
        final double[] key = {tree.nx, tree.ny, tree.c};
        boolean found = false;
        for (final double[] s : seps) {
            if (s[0] == key[0] && s[1] == key[1] && s[2] == key[2]) {
                found = true;
                break;
            }
        }
        if (!found) seps.add(key);
        collectSeps(tree.pos, seps);
        collectSeps(tree.neg, seps);
    }

    private static int walkBsp(Bsp tree, int p, List<double[]> seps) {
        if (tree.isLeaf()) return tree.leaf;
        int idx = -1;
        for (int i = 0; i < seps.size(); i++) {
            final double[] s = seps.get(i);
            if (s[0] == tree.nx && s[1] == tree.ny && s[2] == tree.c) {
                idx = i;
                break;
            }
        }
        return walkBsp(((p >> idx) & 1) != 0 ? tree.pos : tree.neg, p, seps);
    }

    // The pentagon FLAVOR (0-3) of the cell a draw symbol hosts: which of the four
    // pentagon orientations of the Cairo-like metatile it gets. The pentagon is a
    // 1:1 function of the cell's jigsaw piece and reduces to the closed-form rule
    //   flavor = BASE[symbol] XOR isLowercase XOR (heading & 1)
    // with BASE = {S:0, D:1, E:2, T:3}; bit 0 is a 180° rotation, bit 1 a Y
    // reflection of the base pentagon (see core/tiling.ts). Derived and verified
    // exhaustively against the pentagon geometry.
    private static final Map<String, Integer> FLAVOR_BASE = Map.of("S", 0, "D", 1, "E", 2, "T", 3);

    /** A child of a motif rule. */
    private record Child(String token, AB offUnit, boolean flip) {
        // offUnit: offset from the parent origin, in net(·,1) units
    }

    /**
     * Compile a grammar (motif rules + leaf draws) into flat descent tables.
     * Lowercase motifs are the uppercase rules reversed, generated automatically.
     *
     * @param rules motif rules, in motif order (use an insertion-ordered map)
     * @param draws leaf draws
     * @return compiled tables
     */
    public static CurveTables compileGrammar(Map<String, String> rules, Map<String, String> draws) {
        final List<String> motifs = new ArrayList<>(rules.keySet());
        final List<String> allMotifs = new ArrayList<>(motifs);
        for (final String m : motifs) allMotifs.add(m.toLowerCase(Locale.ROOT));
        final int motifCount = allMotifs.size();
        final Map<String, Integer> motifIdx = new LinkedHashMap<>();
        for (int i = 0; i < allMotifs.size(); i++) motifIdx.put(allMotifs.get(i), i);

        // ---------- child tables: 4 children per motif ----------
        final Map<String, List<Child>> childrenOf = new HashMap<>();
        for (final String m : motifs) childrenOf.put(m, childTable(rules.get(m), rules, draws));
        for (final String m : motifs) childrenOf.put(m.toLowerCase(Locale.ROOT), childTable(reverseMotif(rules.get(m)), rules, draws));

        final int[] childToken = new int[motifCount * 4];
        final byte[] childFlip = new byte[motifCount * 4];
        final double[] childOffA = new double[motifCount * 4];
        final double[] childOffB = new double[motifCount * 4];
        for (final String m : allMotifs) {
            final List<Child> cs = childrenOf.get(m);
            for (int d = 0; d < 4; d++) {
                final int ci = motifIdx.get(m) * 4 + d;
                childToken[ci] = motifIdx.get(cs.get(d).token);
                childFlip[ci] = (byte) (cs.get(d).flip ? 1 : 0);
                childOffA[ci] = cs.get(d).offUnit.a();
                childOffB[ci] = cs.get(d).offUnit.b();
            }
        }

        // ---------- footprint hulls (convex hull of leaf host corners) ----------
        // per (motif, flip): edge list [3*c0.a, 3*c0.b, d.a, d.b]*E.
        // The corner is pre-tripled (the descent works in the corner-sum frame, = 3x
        // the (a,b) point frame); the edge direction stays UNIT so the containment
        // cross products stay ~O(2^R) instead of O(2^2R) — exact integer at every
        // resolution. The flipped variant is the hull negated (180° = negate,
        // winding-preserving).
        final double[][] fpEdges = new double[motifCount * 2][];
        for (final String m : allMotifs) {
            final List<AB> corners = new ArrayList<>();
            walk(toDraws(m, 1, rules, draws), new AB(0, 0), 0, (sym, from, h) -> Collections.addAll(corners, hostCorners(sym, from, h)));
            final List<AB> hull = convexHull(corners);
            for (int flip = 0; flip < 2; flip++) {
                final int sign = flip != 0 ? -1 : 1;
                final double[] edges = new double[hull.size() * 4];
                for (int i = 0; i < hull.size(); i++) {
                    final AB c0 = hull.get(i),
                            c1 = hull.get((i + 1) % hull.size());
                    edges[i * 4] = 3 * sign * c0.a();
                    edges[i * 4 + 1] = 3 * sign * c0.b();
                    edges[i * 4 + 2] = sign * (c1.a() - c0.a());
                    edges[i * 4 + 3] = sign * (c1.b() - c0.b());
                }
                fpEdges[motifIdx.get(m) * 2 + flip] = edges;
            }
        }

        // ---------- leaf tables: per (motif, flip = heading 0|3) the 4 level-1 host cells ----------
        //  - corner sums relative to the descent position: sum = 3*pos + leafSum[..]
        //  - the cell's pentagon flavor
        final double[] leafSum = new double[motifCount * 2 * 8];
        final byte[] leafFlavor = new byte[motifCount * 2 * 4];
        for (final String m : allMotifs) {
            final String drawStr = toDraws(m, 1, rules, draws);
            for (int flip = 0; flip < 2; flip++) {
                final int base = motifIdx.get(m) * 2 + flip;
                final int[] d = {0};
                walk(drawStr, new AB(0, 0), flip != 0 ? 3 : 0, (sym, from, hh) -> {
                    final AB sum = hostSum(sym, from, hh);
                    leafSum[base * 8 + d[0] * 2] = sum.a();
                    leafSum[base * 8 + d[0] * 2 + 1] = sum.b();
                    final String upper = sym.toUpperCase(Locale.ROOT);
                    if (FLAVOR_BASE.get(upper) == null) throw new IllegalStateException("lsystem: no pentagon flavor for draw symbol " + sym);
                    leafFlavor[base * 4 + d[0]] = (byte) (FLAVOR_BASE.get(upper) ^ (sym.equals(upper) ? 0 : 1) ^ (hh & 1));
                    d[0]++;
                });
            }
        }

        final CurveTables tables = new CurveTables(
                Collections.unmodifiableMap(motifIdx),
                childToken,
                childFlip,
                childOffA,
                childOffB,
                fpEdges,
                leafSum,
                leafFlavor,
                new double[motifCount * 2 * 9],
                new byte[motifCount * 2 * 8]);
        // ---------- branchless child classifier (3 line tests + LUT per state) ----------
        for (int m = 0; m < motifCount; m++) {
            for (int f = 0; f < 2; f++) {
                final int k = m * 2 + f;
                final Bsp tree = buildBsp(childPolys(tables, m, f));
                final List<double[]> seps = new ArrayList<>();
                collectSeps(tree, seps);
                for (int i = 0; i < seps.size(); i++) {
                    tables.classSep[k * 9 + i * 3] = seps.get(i)[0];
                    tables.classSep[k * 9 + i * 3 + 1] = seps.get(i)[1];
                    tables.classSep[k * 9 + i * 3 + 2] = seps.get(i)[2];
                }
                for (int p = 0; p < 8; p++) tables.classLut[k * 8 + p] = (byte) walkBsp(tree, p, seps);
            }
        }
        return tables;
    }

    // Expand a motif to a pure draw string: `level` rule passes, then one draws
    // pass (turning every remaining motif into its leaf terminal).
    private static String toDraws(String motif, int level, Map<String, String> rules, Map<String, String> draws) {
        String s = motif;
        for (int i = 0; i < level; i++) s = expandOnce(s, rules);
        return expandOnce(s, draws);
    }

    private static AB motifNet(String motif, Map<String, String> rules, Map<String, String> draws) {
        return netOf(toDraws(motif, 1, rules, draws)).disp();
    }

    private static List<Child> childTable(String rule, Map<String, String> rules, Map<String, String> draws) {
        AB pos = new AB(0, 0);
        int h = 0;
        final List<Child> children = new ArrayList<>();
        for (int i = 0, n = rule.length(); i < n; i++) {
            final char c = rule.charAt(i);
            if (c == '+') {
                h = (h + 1) % 6;
                continue;
            }
            if (c == '-') {
                h = (h + 5) % 6;
                continue;
            }
            final String ch = String.valueOf(c);
            if (rules.get(ch.toUpperCase(Locale.ROOT)) == null) continue;
            if (h != 0 && h != 3) throw new IllegalStateException("lsystem: non-180° turn (" + (60 * h) + "°) before a child in rule \"" + rule + "\"");
            final boolean flip = h == 3;
            children.add(new Child(ch, pos, flip));
            final AB nn = motifNet(ch, rules, draws);
            pos = flip ? new AB(pos.a() - nn.a(), pos.b() - nn.b()) : new AB(pos.a() + nn.a(), pos.b() + nn.b());
        }
        if (children.size() != 4) throw new IllegalStateException("lsystem: rule \"" + rule + "\" must have 4 children");
        return children;
    }

    private static long cross(AB o, AB a, AB b) {
        return (a.a() - o.a()) * (b.b() - o.b()) - (a.b() - o.b()) * (b.a() - o.a());
    }

    private static List<AB> convexHull(List<AB> pts) {
        final Map<String, AB> unique = new LinkedHashMap<>();
        for (final AB q : pts) unique.put(q.a() + "," + q.b(), q);
        final List<AB> p = new ArrayList<>(unique.values());
        p.sort((x, y) -> x.a() != y.a() ? Long.compare(x.a(), y.a()) : Long.compare(x.b(), y.b()));
        if (p.size() < 3) return p;
        final List<AB> lower = new ArrayList<>();
        for (final AB q : p) {
            while (lower.size() >= 2 && cross(lower.get(lower.size() - 2), lower.get(lower.size() - 1), q) <= 0) lower.remove(lower.size() - 1);
            lower.add(q);
        }
        final List<AB> upper = new ArrayList<>();
        for (int i = p.size() - 1; i >= 0; i--) {
            final AB q = p.get(i);
            while (upper.size() >= 2 && cross(upper.get(upper.size() - 2), upper.get(upper.size() - 1), q) <= 0) upper.remove(upper.size() - 1);
            upper.add(q);
        }
        final List<AB> result = new ArrayList<>(lower.subList(0, lower.size() - 1));
        result.addAll(upper.subList(0, upper.size() - 1));
        return result;
    }
}
