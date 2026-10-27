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
package org.geotoolkit.dggs.a5.internal.core;

import org.geotoolkit.dggs.a5.internal.lattice.Orientation;
import org.geotoolkit.dggs.a5.internal.lattice.Triple;
import static org.geotoolkit.dggs.a5.internal.core.Origins.faceStep;
import static org.geotoolkit.dggs.a5.internal.core.Origins.quintantToSegment;
import static org.geotoolkit.dggs.a5.internal.core.Serialization.FIRST_CURVE_RESOLUTION;
import static org.geotoolkit.dggs.a5.internal.core.Serialization.deserialize;
import static org.geotoolkit.dggs.a5.internal.core.Serialization.serialize;
import static org.geotoolkit.dggs.a5.internal.lattice.Compat.compatSToTriple;
import static org.geotoolkit.dggs.a5.internal.lattice.Orientation.uv;
import static org.geotoolkit.dggs.a5.internal.lattice.Orientation.uw;
import static org.geotoolkit.dggs.a5.internal.lattice.Orientation.vu;
import static org.geotoolkit.dggs.a5.internal.lattice.Orientation.vw;
import static org.geotoolkit.dggs.a5.internal.lattice.Orientation.wu;
import static org.geotoolkit.dggs.a5.internal.lattice.Orientation.wv;
import static org.geotoolkit.dggs.a5.internal.lattice.Triples.tripleToS;

/**
 * Migration of cell ids from the v0 index (a5 &lt;= 0.10) to the v1 index.
 *
 * <p>Port of upstream {@code core/migrate.ts}. Tables are static final: thread-safe.</p>
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public final class Migrate {

    /**
     * A v0 face layout.
     *
     * @param orientation quintant orientations (shared, never modify)
     * @param firstQuintant first quintant of the face
     */
    private record V0Layout(Orientation[] orientation, int firstQuintant) {
    }

    // The v0 face layouts, by origin id (curve order): the quintant orientations
    // and first quintant of every face. Windings are the same as in v1.
    private static final Orientation[] FAN = {vu, uw, vw, vw, vw};
    private static final Orientation[] COUNTER_STEP = {wu, uv, wv, wu, uw};
    private static final Orientation[] COUNTER_JUMP = {vu, uv, wv, wu, uw};
    private static final Orientation[] CLOCKWISE_STEP = {wu, uw, vw, vu, uw};
    private static final V0Layout[] V0_LAYOUTS = {
        new V0Layout(FAN, 4),
        new V0Layout(COUNTER_JUMP, 2),
        new V0Layout(COUNTER_STEP, 3),
        new V0Layout(COUNTER_STEP, 0),
        new V0Layout(CLOCKWISE_STEP, 2),
        new V0Layout(COUNTER_JUMP, 4),
        new V0Layout(CLOCKWISE_STEP, 2),
        new V0Layout(CLOCKWISE_STEP, 2),
        new V0Layout(COUNTER_STEP, 3),
        new V0Layout(COUNTER_JUMP, 0),
        new V0Layout(COUNTER_JUMP, 3),
        new V0Layout(CLOCKWISE_STEP, 0)
    };

    private Migrate() {
    }

    /**
     * Migrates a cell id from the v0 index (a5 &lt;= 0.10) to the v1 index. Both
     * versions share the same cells; the v1 index threads the curve differently:
     * a new curve within each quintant, and a new quintant order on some faces.
     * So the cell is located in v0 terms (quintant + lattice triple, via the
     * original curve) and re-encoded in v1 terms.
     *
     * @param cell A cell id in the v0 index
     * @return The id of the same cell in the v1 index
     */
    public static long migrate(long cell) {
        final A5Cell a5cell = deserialize(cell);
        final Origin origin = a5cell.origin();
        final int segment = a5cell.segment();
        final long S = a5cell.S();
        final int resolution = a5cell.resolution();
        if (resolution < FIRST_CURVE_RESOLUTION - 1) return cell;

        // Locate the cell's quintant in the v0 layout. Windings are unchanged, so
        // the v0 face shares the v1 face's direction of travel.
        final V0Layout v0 = V0_LAYOUTS[origin.id()];
        final int step = faceStep(origin);
        final int faceRelativeQuintant = (segment - origin.firstQuintant() + 5) % 5;
        final int quintant = (v0.firstQuintant() + step * faceRelativeQuintant + 5) % 5;
        final Origins.SegmentOrientation target = quintantToSegment(quintant, origin);
        if (resolution == FIRST_CURVE_RESOLUTION - 1) {
            return serialize(new A5Cell(origin, target.segment(), 0L, resolution));
        }

        final int curveLevel = 1 + resolution - FIRST_CURVE_RESOLUTION;
        final Triple triple = compatSToTriple(S, curveLevel, v0.orientation()[faceRelativeQuintant]);
        final long newS = tripleToS(triple, curveLevel, target.orientation());
        return serialize(new A5Cell(origin, target.segment(), newS, resolution));
    }
}
