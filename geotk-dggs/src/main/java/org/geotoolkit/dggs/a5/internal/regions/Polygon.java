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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.geotoolkit.dggs.a5.internal.geometry.PreparedPolygon;
import org.geotoolkit.dggs.a5.internal.regions.PolygonBoundary.Boundary;
import org.geotoolkit.dggs.a5.internal.regions.PolygonBoundary.SampledBoundary;
import org.geotoolkit.dggs.a5.internal.utils.IntList;
import org.geotoolkit.dggs.a5.internal.utils.LongList;
import static org.geotoolkit.dggs.a5.internal.core.Cell.cellToSpherical;
import static org.geotoolkit.dggs.a5.internal.core.CoordinateTransforms.fromLonLat;
import static org.geotoolkit.dggs.a5.internal.core.CoordinateTransforms.toCartesian;
import static org.geotoolkit.dggs.a5.internal.core.Serialization.FIRST_CURVE_RESOLUTION;
import static org.geotoolkit.dggs.a5.internal.core.Serialization.MAX_RESOLUTION;
import static org.geotoolkit.dggs.a5.internal.core.Serialization.WORLD_CELL;
import static org.geotoolkit.dggs.a5.internal.core.Serialization.cellToChildren;
import static org.geotoolkit.dggs.a5.internal.core.Serialization.getResolution;
import static org.geotoolkit.dggs.a5.internal.coverings.SlotRuns.slotRunsToCovering;
import static org.geotoolkit.dggs.a5.internal.coverings.SlotRuns.toCovering;
import static org.geotoolkit.dggs.a5.internal.geometry.PreparedPolygon.pointInPreparedPolygon;
import static org.geotoolkit.dggs.a5.internal.geometry.PreparedPolygon.preparePolygon;
import static org.geotoolkit.dggs.a5.internal.regions.CurveRuns.fillByCurveRuns;
import static org.geotoolkit.dggs.a5.internal.regions.InteriorFlood.fillByFlood;
import static org.geotoolkit.dggs.a5.internal.regions.InteriorFlood.prefersFlood;
import static org.geotoolkit.dggs.a5.internal.regions.PolygonBoundary.boundaryOutput;
import static org.geotoolkit.dggs.a5.internal.regions.PolygonBoundary.classifyBoundary;
import static org.geotoolkit.dggs.a5.internal.regions.PolygonBoundary.sampleBoundary;
import static org.geotoolkit.dggs.a5.internal.traversal.TripleCells.cellIdsToTriples;

/**
 * Polygon to cells.
 *
 * <p>Port of upstream {@code regions/polygon.ts}. The TS {@code LonLat[] | LonLat[][]} union
 * is two overloads ({@code double[][]} single ring, {@code double[][][]} rings
 * {@code [outer, ...holes]}), the options object {@code {containment}} an optional
 * {@link PolygonContainment} argument (default {@link PolygonContainment#center}).
 * Results are {@code long[]} of unsigned 64 bits cell ids. Stateless, thread-safe.</p>
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public final class Polygon {

    /**
     * How a cell is judged to belong to the polygon.
     */
    public enum PolygonContainment {
        /** A cell is included iff its center lies inside the polygon. */
        center,
        /**
         * Additionally include every cell that overlaps the polygon
         * boundary, giving gap-free coverage (a superset of {@code center}).
         */
        overlapping
    }

    private Polygon() {
    }

    /**
     * Find all cells within a polygon given as a single ring, with {@code center} containment.
     *
     * @param ring a single ring of [longitude, latitude] vertices in degrees
     * @param resolution Target resolution (0..30)
     * @return Compacted cells sorted in curve order, then the compaction marker
     * @see #polygonToCells(double[][][], int, PolygonContainment)
     */
    public static long[] polygonToCells(double[][] ring, int resolution) {
        return polygonToCells(ring, resolution, PolygonContainment.center);
    }

    /**
     * Find all cells within a polygon given as a single ring (shorthand for a polygon with no holes).
     *
     * @param ring a single ring of [longitude, latitude] vertices in degrees
     * @param resolution Target resolution (0..30)
     * @param containment {@code center} or {@code overlapping}
     * @return Compacted cells sorted in curve order, then the compaction marker
     * @see #polygonToCells(double[][][], int, PolygonContainment)
     */
    public static long[] polygonToCells(double[][] ring, int resolution, PolygonContainment containment) {
        // Normalize: a flat ring is shorthand for a polygon with no holes.
        return polygonToCells(new double[][][]{ring}, resolution, containment);
    }

    /**
     * Find all cells within a polygon, with {@code center} containment.
     *
     * @param polygon GeoJSON-style rings {@code [outer, ...holes]} of [longitude, latitude] vertices
     * @param resolution Target resolution (0..30)
     * @return Compacted cells sorted in curve order, then the compaction marker
     * @see #polygonToCells(double[][][], int, PolygonContainment)
     */
    public static long[] polygonToCells(double[][][] polygon, int resolution) {
        return polygonToCells(polygon, resolution, PolygonContainment.center);
    }

    /**
     * Find all cells within a polygon. The result is compacted, with a compaction marker
     * cell recording the resolution — use {@code uncompact} to expand it.
     *
     * @param polygon GeoJSON-style rings {@code [outer, ...holes]} of [longitude, latitude]
     *        vertices in degrees, where cells inside a hole are excluded. Rings may be open or
     *        closed (GeoJSON-style, first vertex repeated at the end) — closure is automatic
     *        either way. Holes with fewer than 3 distinct vertices are ignored.
     *        (Upstream also accepts a single ring: see {@link #polygonToCells(double[][], int, PolygonContainment)}.)
     * @param resolution Target resolution (0..30)
     * @param containment selects {@code center} (default, cell center inside the polygon) or
     *        {@code overlapping} (any cell touching the polygon, for gap-free coverage).
     * @return Compacted cells sorted in curve order, then the compaction marker
     */
    public static long[] polygonToCells(double[][][] polygon, int resolution, PolygonContainment containment) {
        final double[][][] inputRings = polygon;

        if (inputRings.length == 0) return toCovering(new long[0], resolution);
        final double[][] outer = stripClosing(inputRings[0]);
        if (outer.length < 3) return toCovering(new long[0], resolution);
        final List<double[][]> ringList = new ArrayList<>();
        ringList.add(outer);
        for (int r = 1; r < inputRings.length; r++) {
            final double[][] hole = stripClosing(inputRings[r]);
            if (hole.length >= 3) ringList.add(hole);
        }
        final double[][][] rings = ringList.toArray(new double[0][][]);

        // Authalic-sphere ring vectors — A5's internal sphere, so cell centers
        // compare directly with no geodetic↔authalic round-trip.
        final double[][][] ringVecsList = new double[rings.length][][];
        for (int r = 0; r < rings.length; r++) {
            final double[][] ring = rings[r];
            final double[][] ringVecs = new double[ring.length][];
            for (int i = 0; i < ring.length; i++) {
                ringVecs[i] = toCartesian(fromLonLat(ring[i]));
            }
            ringVecsList[r] = ringVecs;
        }

        final PreparedPolygon prep = preparePolygon(ringVecsList);
        // Both modes need every cell the boundary touches, each with every segment
        // touching it: the fills classify a cell next to the boundary by the
        // segments its boundary neighbor records (see `insideNextTo`), so a segment
        // the sampling skipped flips that class, and the fill leaks out of the
        // polygon.
        final boolean overlapping = containment == PolygonContainment.overlapping;
        final SampledBoundary sampled = sampleBoundary(rings, resolution);

        // Res 30 covers only quintants 0-41 (elsewhere A5 answers at res 29, see
        // serialize), so a polygon reaching past them is filled at res 29: mixing the
        // two lattices would leave the fill without a consistent grid.
        if (resolution == MAX_RESOLUTION) {
            for (long cell : sampled.cells()) {
                if (getResolution(cell) != resolution) {
                    return polygonToCells(polygon, resolution - 1, containment);
                }
            }
        }

        final Boundary boundary = classifyBoundary(sampled, ringVecsList, prep);

        // Resolutions 0 and 1 have no lattice (a quintant is a single cell): every
        // cell off the boundary is in or out by its center, and there are at most 60
        // of them.
        if (resolution < FIRST_CURVE_RESOLUTION) {
            final LongList out = boundaryOutput(boundary, overlapping);
            for (long cell : cellToChildren(WORLD_CELL, resolution)) {
                if (!boundary.set().contains(cell) && pointInPreparedPolygon(toCartesian(cellToSpherical(cell)), prep)) out.push(cell);
            }
            return toCovering(out.toArray(), resolution);
        }

        // A quintant holding no boundary cells is wholly inside or outside; it can
        // only be inside when the polygon's bounding cap holds a quintant's area (4π/60)
        final boolean capHoldsQuintant = 2 * Math.PI * (1 - prep.cap().minDot()) >= (4 * Math.PI) / 60;
        final IntList triples = cellIdsToTriples(boundary.cells());
        if (prefersFlood(ringVecsList, boundary.cells().length, resolution, capHoldsQuintant)) {
            return toCovering(fillByFlood(boundary, triples, resolution, overlapping), resolution);
        }
        return slotRunsToCovering(fillByCurveRuns(boundary, triples, resolution, overlapping, capHoldsQuintant), resolution);
    }

    // GeoJSON rings repeat the first vertex at the end — drop the duplicate.
    private static double[][] stripClosing(double[][] ring) {
        final int last = ring.length - 1;
        return last > 0 && ring[0][0] == ring[last][0] && ring[0][1] == ring[last][1] ? Arrays.copyOf(ring, last) : ring;
    }
}
