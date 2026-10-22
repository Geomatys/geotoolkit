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
package org.geotoolkit.dggs.a5.internal.regions;

import org.geotoolkit.dggs.a5.internal.regions.PolygonBoundary.Boundary;
import org.geotoolkit.dggs.a5.internal.regions.PolygonBoundary.BoundaryNeighbors;
import org.geotoolkit.dggs.a5.internal.utils.IntList;
import org.geotoolkit.dggs.a5.internal.utils.LongList;
import static org.geotoolkit.dggs.a5.internal.core.CellInfo.getNumCells;
import static org.geotoolkit.dggs.a5.internal.core.CoordinateTransforms.toCartesian;
import static org.geotoolkit.dggs.a5.internal.core.Serialization.FIRST_CURVE_RESOLUTION;
import static org.geotoolkit.dggs.a5.internal.regions.PolygonBoundary.boundaryNeighbors;
import static org.geotoolkit.dggs.a5.internal.regions.PolygonBoundary.boundaryOutput;
import static org.geotoolkit.dggs.a5.internal.regions.PolygonBoundary.insideNextTo;
import static org.geotoolkit.dggs.a5.internal.regions.PolygonBoundary.polygonArea;
import static org.geotoolkit.dggs.a5.internal.traversal.LatticeFloodFill.tripleSpaceFloodFill;
import static org.geotoolkit.dggs.a5.internal.traversal.TripleCells.forEachLatticeNeighbor;
import static org.geotoolkit.dggs.a5.internal.traversal.TripleCells.tripleCellCenter;
import static org.geotoolkit.dggs.a5.internal.traversal.TripleCells.tripleCellToId;

/**
 * Polygon fill by flooding the interior: cheaper than curve runs when the
 * interior is small, as the flood costs about boundary + interior cells while
 * the runs sort a band of boundary plus ring slots.
 *
 * <p>Port of upstream {@code regions/interior-flood.ts}. Flat triple arrays are
 * {@link IntList}, the growable {@code bigint[]} output a {@link LongList}.
 * Stateless, thread-safe.</p>
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public final class InteriorFlood {

    // Below this many estimated interior cells per boundary cell, flooding the
    // interior beats splitting the curve into runs (measured crossover: ~3.3).
    private static final int FLOOD_INTERIOR_PER_BOUNDARY = 3;

    private InteriorFlood() {
    }

    /**
     * Whether to fill by flooding: the interior is small, and the polygon can't
     * swallow a quintant whole ({@code capHoldsQuintant} false), which the flood, never
     * crossing a quintant edge from the boundary, would miss.
     *
     * @param ringVecsList rings as Cartesian vectors, outer first
     * @param boundaryCount number of boundary cells
     * @param resolution target resolution
     * @param capHoldsQuintant whether the polygon might swallow a quintant whole
     * @return whether to fill by flooding
     */
    public static boolean prefersFlood(double[][][] ringVecsList, int boundaryCount, int resolution, boolean capHoldsQuintant) {
        return !capHoldsQuintant
                && (polygonArea(ringVecsList) / (4 * Math.PI)) * getNumCells(resolution) < FLOOD_INTERIOR_PER_BOUNDARY * (double) boundaryCount;
    }

    /**
     * Fill a polygon by flooding its interior, given its classified boundary and
     * the boundary cells as flat triples. Returns the cells inside, uncompacted and
     * unsorted.
     *
     * @param boundary the classified boundary
     * @param triples the boundary cells as flat triples (originId, quintant, x, y, z)
     * @param resolution target resolution (at least FIRST_CURVE_RESOLUTION)
     * @param overlapping whether the containment is 'overlapping'
     * @return the cells inside, uncompacted and unsorted (new array)
     */
    public static long[] fillByFlood(Boundary boundary, IntList triples, int resolution, boolean overlapping) {
        final int curveLevel = resolution - FIRST_CURVE_RESOLUTION + 1;
        final int maxRow = (1 << curveLevel) - 1;
        final LongList out = boundaryOutput(boundary, overlapping);

        // The shell: the flood's own moves out of the boundary (each an edge
        // neighbor), split into seeds inside and firewall outside
        final BoundaryNeighbors neighbors = boundaryNeighbors(triples,
                (b, c, visit) -> forEachLatticeNeighbor(b.get(c), b.get(c + 1), b.get(c + 2), b.get(c + 3), b.get(c + 4), maxRow, visit));
        final IntList shell = neighbors.cells();
        final IntList parents = neighbors.parents();
        final IntList seeds = new IntList();
        final IntList firewall = triples.slice();
        for (int c = 0; c < shell.length(); c += 5) {
            final IntList s = shell;
            final double[] center = toCartesian(tripleCellCenter(s.get(c), s.get(c + 1), s.get(c + 2), s.get(c + 3), s.get(c + 4), curveLevel, maxRow));
            final IntList side = insideNextTo(boundary, center, parents.get(c / 5)) ? seeds : firewall;
            for (int k = 0; k < 5; k++) side.push(s.get(c + k));
        }
        if (seeds.length() > 0) {
            for (int c = 0; c < seeds.length(); c += 5) {
                out.push(tripleCellToId(seeds.get(c), seeds.get(c + 1), seeds.get(c + 2), seeds.get(c + 3), seeds.get(c + 4), curveLevel, resolution));
            }
            final long[] interiorCells = tripleSpaceFloodFill(firewall, seeds, resolution).interiorCells();
            for (int i = 0; i < interiorCells.length; i++) out.push(interiorCells[i]);
        }
        return out.toArray();
    }
}
