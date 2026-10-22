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

import org.geotoolkit.dggs.a5.internal.lattice.lsystem.LSystem;

/**
 * Operations on {@link Triple} coordinates.
 *
 * <p>Port of upstream {@code lattice/triple.ts}.</p>
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public final class Triples {

    /**
     * Value returned by {@link #tripleToS(Triple, int, Orientation)} when the triple has
     * an invalid parity (upstream returns {@code null}). Valid s-values are always
     * &lt; 2<sup>2·resolution</sup> &lt;= 2<sup>60</sup>, so they are never negative.
     */
    public static final long NO_S = -1L;

    private Triples() {
    }

    /** The parity of a triple (0 or 1), equal to x + y + z. */
    public static int tripleParity(Triple t) {
        return t.x() + t.y() + t.z();
    }

    // The pentagon flavor is a CLOSED FORM of the triple. The A5 tiling is gyro
    // applied to the square grid R left when every lattice edge parallel to the
    // quintant's dodecahedron edge is deleted (A5 = g o^r D in Conway notation).
    // Bit 0 is the triangle's parity (which half of its rhombus it is); bit 1 is
    // the colour of its apex in the 2-colouring of R, coloured from a dodecahedron
    // vertex. The apex of either triangle in unit square (m, n) has colour
    // (m + n + maxRow + 1) & 1, and x + z = -(m + n). For maxRow + 1 even (every
    // resolution but 0) face centres and vertices share a colour, so bit 1 is just
    // (x + z) & 1; at resolution 0 they differ, which gives the corner cell flavor 2.
    // Verified against the descent's flavor over all cells (tests/lattice/curve.test.ts).

    /** The pentagon flavor (0-3) of a triple's cell — orientation-independent. */
    public static int tripleFlavor(Triple t, int maxRow) {
        return (t.x() + t.y() + t.z()) | (((maxRow + 1 + t.x() + t.z()) & 1) << 1);
    }

    /** Check if a triple is within valid quintant bounds. */
    public static boolean tripleInBounds(Triple t, int maxRow) {
        final int sum = t.x() + t.y() + t.z();
        if (sum != 0 && sum != 1) return false;
        final int limit = t.y() - sum;
        return t.x() <= 0 && t.z() <= 0 && t.y() >= 0 && t.y() <= maxRow && t.x() >= -limit && t.z() >= -limit;
    }

    /**
     * {@link #tripleToS(Triple, int, Orientation)} with the default {@code uv} orientation.
     */
    public static long tripleToS(Triple t, int resolution) {
        return tripleToS(t, resolution, Orientation.uv);
    }

    /**
     * Convert triple coordinates to an s-value on the A5 (L-system) curve.
     * The engine's `lattice.tripleToS` is currently the compat alias; this is the
     * pure-curve form it swaps to at the canonical cutover.
     *
     * @return s-value, or {@link #NO_S} (upstream: null) if the triple has invalid parity
     */
    public static long tripleToS(Triple t, int resolution, Orientation orientation) {
        final int sum = t.x() + t.y() + t.z();
        if (sum != 0 && sum != 1) return NO_S;
        return LSystem.tripleToSLattice(t, resolution, orientation);
    }
}
