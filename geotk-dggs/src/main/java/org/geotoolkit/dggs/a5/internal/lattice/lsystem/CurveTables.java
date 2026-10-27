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

import java.util.Map;

/**
 * Flat numeric tables for one grammar, consumed by the descents in {@link LSystem}.
 *
 * <p>Port of the {@code CurveTables} interface of upstream {@code lattice/lsystem/tables.ts}.
 * Instances are created by {@link Tables#compileGrammar(Map, Map)} and must be treated as
 * immutable once compiled (arrays are exposed for speed). Unsigned typed arrays
 * ({@code Uint8Array}) are {@code byte[]}: read them with {@code & 0xFF}.</p>
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public final class CurveTables {

    /** Motif token -> motif index (uppercase motifs first, then their lowercase reverses). */
    public final Map<String, Integer> motifIdx;
    // children: entry ci = motif * 4 + digit
    public final int[] childToken;
    /** uint8. */
    public final byte[] childFlip;
    public final double[] childOffA;
    public final double[] childOffB;
    // footprint hulls per (motif, flip): edge list [3*c0.a, 3*c0.b, d.a, d.b]*E
    public final double[][] fpEdges;
    // leaf tables per (motif, flip): 4 host cells as corner sums and pentagon flavors
    public final double[] leafSum;
    /** uint8. */
    public final byte[] leafFlavor;
    // Branchless child classifier per state k = motif*2+flip: 3 separating lines
    // (classSep[k*9 ..] = [nx0,ny0,c0, nx1,ny1,c1, nx2,ny2,c2]) evaluated against
    // the normalised target give a 3-bit pattern; classLut[k*8 + pat] is the child
    // digit. Replaces the exact-path 4-hull scan with 3 dot products + a LUT read.
    public final double[] classSep;
    /** uint8. */
    public final byte[] classLut;

    CurveTables(Map<String, Integer> motifIdx, int[] childToken, byte[] childFlip, double[] childOffA,
            double[] childOffB, double[][] fpEdges, double[] leafSum, byte[] leafFlavor,
            double[] classSep, byte[] classLut) {
        this.motifIdx = motifIdx;
        this.childToken = childToken;
        this.childFlip = childFlip;
        this.childOffA = childOffA;
        this.childOffB = childOffB;
        this.fpEdges = fpEdges;
        this.leafSum = leafSum;
        this.leafFlavor = leafFlavor;
        this.classSep = classSep;
        this.classLut = classLut;
    }
}
