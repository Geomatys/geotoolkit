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

import com.fasterxml.jackson.databind.JsonNode;
import org.geotoolkit.dggs.a5.internal.Fixtures;
import org.geotoolkit.dggs.a5.internal.lattice.lsystem.Cell;
import org.geotoolkit.dggs.a5.internal.lattice.lsystem.LSystem;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Port of upstream {@code tests/lattice/curve.test.ts}.
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public class CurveTest {

    private static final JsonNode FIXTURES = Fixtures.load("fixtures/lattice/curve.json");

    private static Triple triple(JsonNode f) {
        return new Triple(f.get("x").asInt(), f.get("y").asInt(), f.get("z").asInt());
    }

    private static Orientation orientation(JsonNode f) {
        return Orientation.valueOf(f.get("orientation").asText());
    }

    @Nested
    class sToCell {
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
    class sToTriple {
        @Test
        void matches_the_triple_part_of_sToCell() {
            for (JsonNode f : FIXTURES.get("sToCell")) {
                final Triple triple = LSystem.sToTriple(f.get("s").asLong(), f.get("resolution").asInt(), orientation(f));
                assertEquals(triple(f), triple);
            }
        }
    }

    @Nested
    class tripleParity {
        @Test
        void returns_correct_parity() {
            for (JsonNode f : FIXTURES.get("sToCell")) {
                final Triple triple = triple(f);
                assertEquals(f.get("parity").asInt(), Triples.tripleParity(triple), "parity for " + triple);
            }
        }
    }

    @Nested
    class tripleToS {
        @Test
        void round_trips_back_to_the_original_s_value() {
            for (JsonNode f : FIXTURES.get("sToCell")) {
                final Triple triple = triple(f);
                final int res = f.get("resolution").asInt();
                final long s = Triples.tripleToS(triple, res, orientation(f));
                assertEquals(f.get("s").asLong(), s, "s for " + triple + " res=" + res + " ori=" + orientation(f));
            }
        }
    }

    @Nested
    class pointToS_roundToTriple_tripleToS {
        @Test
        void locates_the_containing_cell_of_a_fractional_IJ_point() {
            for (JsonNode f : FIXTURES.get("pointToS")) {
                final double i = f.get("i").asDouble();
                final double j = f.get("j").asDouble();
                final int res = f.get("resolution").asInt();
                final long s = Triples.tripleToS(Curve.roundToTriple(new double[] {i, j}, res), res, orientation(f));
                assertEquals(f.get("s").asLong(), s, "s for (" + i + "," + j + ") res=" + res + " ori=" + orientation(f));
            }
        }
    }

    @Nested
    class tripleInBounds {
        @Test
        void validates_quintant_bounds_correctly() {
            for (JsonNode f : FIXTURES.get("tripleInBounds")) {
                final Triple triple = triple(f);
                final int maxRow = f.get("maxRow").asInt();
                assertEquals(f.get("expected").asBoolean(), Triples.tripleInBounds(triple, maxRow), triple + " maxRow=" + maxRow);
            }
        }
    }

    @Nested
    class tripleFlavor {
        @Test
        void matches_the_descent_flavor_for_every_cell_closed_form() {
            // Pin the closed form against the descent over all cells at res 0-6
            // (res 0 included: its single corner cell is flavor 2), two orientations.
            for (Orientation orientation : new Orientation[] {Orientation.uv, Orientation.wu}) {
                for (int res = 0; res <= 6; res++) {
                    final int maxRow = (1 << res) - 1;
                    for (long s = 0; s < 1L << (2 * res); s++) {
                        final Cell cell = LSystem.sToCell(s, res, orientation);
                        assertEquals(cell.flavor(), Triples.tripleFlavor(cell.triple(), maxRow));
                    }
                }
            }
        }
    }
}
