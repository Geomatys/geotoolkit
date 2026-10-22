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

/**
 * The descent state below a cell (see {@link LSystem#curveChild}, {@link LSystem#tripleToCurveNode}).
 *
 * <p>Port of the {@code CurveNode} interface of upstream {@code lattice/lsystem/index.ts}.
 * Mutable (it is written in place by {@code curveChild}); {@code equals} compares field values
 * (the upstream tests compare nodes with {@code toEqual}).</p>
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public final class CurveNode {

    public int motif;
    public int flip;
    /** Turtle position (exact integer value), in units of the children's level. */
    public double posA;
    /** Turtle position (exact integer value), in units of the children's level. */
    public double posB;

    /** Creates a zero node. */
    public CurveNode() {
    }

    public CurveNode(int motif, int flip, double posA, double posB) {
        this.motif = motif;
        this.flip = flip;
        this.posA = posA;
        this.posB = posB;
    }

    /** Copies all fields of {@code other} in this node. */
    public void set(CurveNode other) {
        this.motif = other.motif;
        this.flip = other.flip;
        this.posA = other.posA;
        this.posB = other.posB;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof CurveNode)) return false;
        final CurveNode o = (CurveNode) obj;
        return motif == o.motif && flip == o.flip && posA == o.posA && posB == o.posB;
    }

    @Override
    public int hashCode() {
        // +0.0 normalises -0.0 so that equal nodes hash equal
        return 31 * (31 * (31 * motif + flip) + Double.hashCode(posA + 0.0)) + Double.hashCode(posB + 0.0);
    }

    @Override
    public String toString() {
        return "CurveNode{motif=" + motif + ", flip=" + flip + ", posA=" + posA + ", posB=" + posB + '}';
    }
}
