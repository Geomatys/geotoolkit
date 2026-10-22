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

import java.util.Arrays;
import org.geotoolkit.dggs.a5.internal.geometry.PentagonShape;
import org.geotoolkit.dggs.a5.internal.lattice.Orientation;
import org.geotoolkit.dggs.a5.internal.lattice.lsystem.Cell;
import org.geotoolkit.dggs.a5.internal.lattice.lsystem.LSystem;
import static org.geotoolkit.dggs.a5.internal.core.Tiling.getPentagonVertices;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;

/**
 * Port of upstream {@code tests/hierarchy.test.ts}.
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public class HierarchyTest {

    private static double[][][] generateCells(int resolution, Orientation orientation) {
        final int count = (int) Math.pow(4, resolution);
        final double[][][] cells = new double[count][][];
        for (int s = 0; s < count; s++) {
            final Cell cell = LSystem.sToCell(s, resolution, orientation);
            cells[s] = getPentagonVertices(resolution, 0, cell.triple(), cell.flavor()).getVertices();
        }
        return cells;
    }

    private static void verifyHierarchy(int resolution, Orientation orientation) {
        final double[][][] level1Cells = generateCells(resolution, orientation);
        final double[][][] level2Cells = generateCells(resolution + 1, orientation);

        PentagonShape failedPentagon = null;
        double[][] failedChild = null;
        for (int i = 0; i < level2Cells.length; i++) {
            final double[][] child = level2Cells[i];
            final double[][] parent = level1Cells[i / 4];
            final PentagonShape pentagon = new PentagonShape(parent);
            boolean contained = false;
            for (final double[] vertex : child) {
                if (pentagon.containsPoint(vertex) > 0) {
                    contained = true;
                    break;
                }
            }
            if (!contained) {
                failedPentagon = pentagon;
                failedChild = child;
            }
        }
        final String msg = failedPentagon == null ? null
                : "Pentagon: " + Arrays.deepToString(failedPentagon.getVertices())
                + " did not contain any of: " + Arrays.deepToString(failedChild);
        assertNull(failedPentagon, msg);
        assertNull(failedChild, msg);
    }

    // ---- describe: Cell Hierarchy ----
    @Test
    void Hierarchy_correct_for_resolution_1_to_6() {
        final Orientation[] orientations = {Orientation.uv, Orientation.vu, Orientation.uw, Orientation.wu, Orientation.vw, Orientation.wv};
        for (final Orientation orientation : orientations) {
            for (final int resolution : new int[]{1, 2, 3, 4, 5, 6}) {
                verifyHierarchy(resolution, orientation);
            }
        }
    }
}
