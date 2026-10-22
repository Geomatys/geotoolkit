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
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Port of upstream {@code tests/lattice/compat.test.ts}.
 *
 * The compat curve reproduces the ORIGINAL (pre-L-system) A5 curve bit-for-bit
 * (established exhaustively against the original engine when it was ported);
 * these fixtures pin it against regressions.
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public class CompatTest {

    private static final JsonNode FIXTURES = Fixtures.load("fixtures/lattice/compat.json");

    @Nested
    class compatSToCell {
        @Test
        void produces_correct_triple_coordinates_and_pentagon_flavor() {
            for (JsonNode f : FIXTURES.get("sToCell")) {
                final long s = f.get("s").asLong();
                final int res = f.get("resolution").asInt();
                final Orientation ori = Orientation.valueOf(f.get("orientation").asText());
                final Cell cell = Compat.compatSToCell(s, res, ori);
                final String ctx = " for s=" + s + " res=" + res + " ori=" + ori;
                assertEquals(f.get("x").asInt(), cell.triple().x(), "x" + ctx);
                assertEquals(f.get("y").asInt(), cell.triple().y(), "y" + ctx);
                assertEquals(f.get("z").asInt(), cell.triple().z(), "z" + ctx);
                assertEquals(f.get("flavor").asInt(), cell.flavor(), "flavor" + ctx);
            }
        }
    }

    @Nested
    class compatSToTriple {
        @Test
        void matches_the_triple_part_of_compatSToCell() {
            for (JsonNode f : FIXTURES.get("sToCell")) {
                final Triple triple = Compat.compatSToTriple(f.get("s").asLong(), f.get("resolution").asInt(),
                        Orientation.valueOf(f.get("orientation").asText()));
                assertEquals(new Triple(f.get("x").asInt(), f.get("y").asInt(), f.get("z").asInt()), triple);
            }
        }
    }

    @Nested
    class compatTripleToS {
        @Test
        void round_trips_back_to_the_original_s_value() {
            for (JsonNode f : FIXTURES.get("sToCell")) {
                final Triple triple = new Triple(f.get("x").asInt(), f.get("y").asInt(), f.get("z").asInt());
                final int res = f.get("resolution").asInt();
                final Orientation ori = Orientation.valueOf(f.get("orientation").asText());
                final long s = Compat.compatTripleToS(triple, res, ori);
                assertEquals(f.get("s").asLong(), s, "s for " + triple + " res=" + res + " ori=" + ori);
            }
        }
    }

    @Nested
    class compatIJToS {
        @Test
        void locates_the_containing_cell_of_a_fractional_IJ_point() {
            for (JsonNode f : FIXTURES.get("IJToS")) {
                final double i = f.get("i").asDouble();
                final double j = f.get("j").asDouble();
                final int res = f.get("resolution").asInt();
                final Orientation ori = Orientation.valueOf(f.get("orientation").asText());
                final long s = Compat.compatIJToS(new double[] {i, j}, res, ori);
                assertEquals(f.get("s").asLong(), s, "s for (" + i + "," + j + ") res=" + res + " ori=" + ori);
            }
        }
    }
}
