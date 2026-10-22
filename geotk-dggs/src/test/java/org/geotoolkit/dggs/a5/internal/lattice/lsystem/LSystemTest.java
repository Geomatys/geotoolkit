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

import com.fasterxml.jackson.databind.JsonNode;
import java.util.ArrayDeque;
import java.util.Deque;
import org.geotoolkit.dggs.a5.internal.Fixtures;
import org.geotoolkit.dggs.a5.internal.lattice.Curve;
import org.geotoolkit.dggs.a5.internal.lattice.Orientation;
import org.geotoolkit.dggs.a5.internal.lattice.Triple;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Port of upstream {@code tests/lattice/lsystem.test.ts}.
 *
 * The non-self-intersecting L-system curve — the planned FUTURE canonical
 * curve (a breaking change of all cell IDs). These fixtures pin its behavior
 * ahead of the canonical swap: when the swap happens, curve.json is
 * regenerated and must equal these values.
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public class LSystemTest {

    private static final JsonNode FIXTURES = Fixtures.load("fixtures/lattice/lsystem.json");

    private static Triple triple(JsonNode f) {
        return new Triple(f.get("x").asInt(), f.get("y").asInt(), f.get("z").asInt());
    }

    private static Orientation orientation(JsonNode f) {
        return Orientation.valueOf(f.get("orientation").asText());
    }

    @Nested
    class lsystem_sToCell {
        @Test
        void produces_correct_triple_coordinates_and_pentagon_flavor() {
            for (JsonNode f : FIXTURES.get("sToCell")) {
                final long s = f.get("s").asLong();
                final int res = f.get("resolution").asInt();
                final Orientation ori = orientation(f);
                final Cell cell = LSystem.sToCell(s, res, ori);
                final String ctx = " for s=" + s + " res=" + res + " ori=" + ori;
                assertEquals(f.get("x").asInt(), cell.triple().x(), "x" + ctx);
                assertEquals(f.get("y").asInt(), cell.triple().y(), "y" + ctx);
                assertEquals(f.get("z").asInt(), cell.triple().z(), "z" + ctx);
                assertEquals(f.get("flavor").asInt(), cell.flavor(), "flavor" + ctx);
            }
        }
    }

    @Nested
    class lsystem_sToTriple {
        @Test
        void matches_the_triple_part_of_sToCell() {
            for (JsonNode f : FIXTURES.get("sToCell")) {
                final Triple triple = LSystem.sToTriple(f.get("s").asLong(), f.get("resolution").asInt(), orientation(f));
                assertEquals(triple(f), triple);
            }
        }
    }

    @Nested
    class lsystem_tripleToSLattice {
        @Test
        void round_trips_back_to_the_original_s_value() {
            for (JsonNode f : FIXTURES.get("sToCell")) {
                final Triple triple = triple(f);
                final int res = f.get("resolution").asInt();
                final long s = LSystem.tripleToSLattice(triple, res, orientation(f));
                assertEquals(f.get("s").asLong(), s, "s for " + triple + " res=" + res + " ori=" + orientation(f));
            }
        }
    }

    @Nested
    class lsystem_pointToS_roundToTriple_tripleToSLattice {
        @Test
        void locates_the_containing_cell_of_a_fractional_IJ_point() {
            for (JsonNode f : FIXTURES.get("pointToS")) {
                final double i = f.get("i").asDouble();
                final double j = f.get("j").asDouble();
                final int res = f.get("resolution").asInt();
                final long s = LSystem.tripleToSLattice(Curve.roundToTriple(new double[] {i, j}, res), res, orientation(f));
                assertEquals(f.get("s").asLong(), s, "s for (" + i + "," + j + ") res=" + res + " ori=" + orientation(f));
            }
        }
    }

    @Nested
    class curveChild_tripleToCurveNode {

        private CurveNode root(Orientation orientation) {
            return LSystem.tripleToCurveNode(new Triple(0, 0, 0), 0, orientation).node();
        }

        private record Step(CurveNode node, long s) {
        }

        // Step to the child with `digit`, checking it against sToCell, and the
        // descent state against tripleToCurveNode's from the child's triple
        private Step step(CurveNode node, long s, int digit, int resolution, Orientation orientation) {
            final int[] t = {0, 0, 0};
            final CurveNode below = new CurveNode(0, 0, 0, 0);
            final int flavor = LSystem.curveChild(node, digit, resolution, orientation, t, below);
            final long childS = s * 4 + digit;
            final Triple triple = new Triple(t[0], t[1], t[2]);
            assertEquals(LSystem.sToCell(childS, resolution, orientation), new Cell(triple, flavor));
            assertEquals(new LSystem.CurveNodeResult(childS, flavor, below), LSystem.tripleToCurveNode(triple, resolution, orientation));
            return new Step(below, childS);
        }

        private record Entry(CurveNode node, long s, int resolution) {
        }

        @Test
        void should_step_down_the_hierarchy_in_agreement_with_sToCell_and_tripleToCurveNode() {
            for (Orientation orientation : Orientation.values()) {
                // Every cell through level 3, then one deep path to level 29 (A5 resolution 30)
                final Deque<Entry> stack = new ArrayDeque<>();
                stack.push(new Entry(root(orientation), 0, 0));
                while (!stack.isEmpty()) {
                    final Entry e = stack.pop();
                    if (e.resolution == 3) continue;
                    for (int digit = 0; digit < 4; digit++) {
                        final Step child = step(e.node, e.s, digit, e.resolution + 1, orientation);
                        stack.push(new Entry(child.node, child.s, e.resolution + 1));
                    }
                }
                Step child = new Step(root(orientation), 0);
                for (int resolution = 1; resolution <= 29; resolution++) {
                    final int digit = (resolution * 7 + orientation.name().charAt(0)) % 4;
                    child = step(child.node, child.s, digit, resolution, orientation);
                }
            }
        }
    }
}
