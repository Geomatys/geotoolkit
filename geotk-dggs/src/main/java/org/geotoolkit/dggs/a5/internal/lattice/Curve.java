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

import static org.geotoolkit.dggs.a5.internal.lattice.lsystem.Tables.POW2;

/**
 * Public A5 space-filling curve: point -> triple, using the L-system curve
 * ({@code lattice.lsystem}). The s &lt;-&gt; cell mappings live in
 * {@link org.geotoolkit.dggs.a5.internal.lattice.lsystem.LSystem} (sToCell / sToTriple)
 * and {@link Triples} (tripleToS).
 *
 * <p>Port of upstream {@code lattice/curve.ts}.</p>
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public final class Curve {

    private Curve() {
    }

    /**
     * Locate the lattice triangle containing a fractional IJ point, as a triple.
     *
     * The triples tile the IJ plane as triangles: the unit square (m, n) =
     * (floor(i), floor(j)) splits along the diagonal u+v = 1 into a lower triangle
     * (the parity-0 cell (-n, m+n, -m), centroid (m+1/3, n+1/3)) and an upper
     * triangle (the parity-1 cell (-n, m+n+1, -m), centroid (m+2/3, n+2/3)) — the
     * centroid correspondences were derived from the exact IJ &lt;-&gt; corner-sum
     * affine map target = (12*(i+j), -12*j) and validated against the old-engine
     * discretization over all resolutions and orientations. Point location is two floors + one
     * diagonal comparison. Points exactly on a triangle edge have no unique cell;
     * the &gt;= tie-break below is the fixed convention.
     *
     * The result is clamped into quintant bounds (m &gt;= 0, n &gt;= 0, m+n+parity &lt;=
     * maxRow, equivalent to tripleInBounds): a point slightly outside the quintant
     * (as float noise can produce near quintant edges) must still map to a
     * valid cell for the exact encode.
     *
     * @param ij IJ coordinates [i, j] (fractional, in units of the resolution's lattice)
     * @param resolution curve level
     * @return containing triple
     */
    public static Triple roundToTriple(double[] ij, int resolution) {
        final double maxRow = POW2[resolution] - 1;
        double m = Math.floor(ij[0]);
        double n = Math.floor(ij[1]);
        int parity = ij[0] - m + (ij[1] - n) >= 1 ? 1 : 0;
        if (m < 0) m = 0;
        if (n < 0) n = 0;
        if (m + n + parity > maxRow) {
            parity = 0;
            if (m + n > maxRow) {
                final double over = m + n - maxRow;
                final double dm = Math.min(m, over);
                m -= dm;
                n -= over - dm;
            }
        }
        return new Triple((int) -n, (int) (m + n + parity), (int) -m);
    }
}
