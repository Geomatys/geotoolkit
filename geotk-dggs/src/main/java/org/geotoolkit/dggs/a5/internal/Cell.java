/**
 * Copyright (C) 2025 Geomatys and Felix Palmer
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
package org.geotoolkit.dggs.a5.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.sis.geometries.math.Matrix2D;
import org.apache.sis.geometries.math.Vector2D;
import org.apache.sis.geometries.math.Vector3D;
import org.apache.sis.util.ArraysExt;
import static org.geotoolkit.dggs.a5.internal.CoordinateTransforms.*;
import static org.geotoolkit.dggs.a5.internal.Constants.*;
import static org.geotoolkit.dggs.a5.internal.Dodecahedron.*;
import static org.geotoolkit.dggs.a5.internal.Hilbert.*;
import org.geotoolkit.dggs.a5.internal.Hilbert.Anchor;
import org.geotoolkit.dggs.a5.internal.Hilbert.Orientation;
import static org.geotoolkit.dggs.a5.internal.Orig.*;
import org.geotoolkit.dggs.a5.internal.Utils.Origin;
import static org.geotoolkit.dggs.a5.internal.Serialization.*;
import org.geotoolkit.dggs.a5.internal.SphericalPolygon.SphericalPolygonShape;
import static org.geotoolkit.dggs.a5.internal.Tiling.*;
import org.geotoolkit.dggs.a5.internal.Utils.A5Cell;
import org.geotoolkit.dggs.a5.internal.Utils.PentagonShape;

/**
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public final class Cell {

    private static record CellCandidate(A5Cell cell, double distance){}

    private Cell(){}

    public static long lonLatToCell(Vector2D.Double lonLat, int resolution) {
        if (resolution < FIRST_HILBERT_RESOLUTION) {
            // For low resolutions there is no Hilbert curve, so we can just return as the result is exact
            return serialize(_lonLatToEstimate(lonLat, resolution));
        }

        final int hilbertResolution = 1 + resolution - FIRST_HILBERT_RESOLUTION;
        final List<Vector2D.Double> samples = new ArrayList(List.of(lonLat));
        final int N = 25;
        final double scale = 50.0 / Math.pow(2, hilbertResolution);
        for (int i = 0; i < N; i++) {
            final double R = ((double)i / N) * scale;
            final Vector2D.Double coordinate = new Vector2D.Double(Math.cos(i) * R, Math.sin(i) * R);
            coordinate.add(lonLat);
            samples.add(coordinate);
        }

        // Deduplicate estimates
        final Set<Long> estimateSet = new HashSet<>();
        final List<A5Cell> uniqueEstimates = new ArrayList<>();
        final List<CellCandidate> cells = new ArrayList();

        for (Vector2D.Double sample : samples) {
            final A5Cell estimate = _lonLatToEstimate(sample, resolution);
            final long estimateKey = serialize(estimate);
            if (!estimateSet.contains(estimateKey)) {
                // Have new estimate, add to set and list
                estimateSet.add(estimateKey);
                uniqueEstimates.add(estimate);
                // Check if we have a hit, storing distance if not
                final double distance = a5cellContainsPoint(estimate, lonLat);
                if (distance > 0) {
                    return serialize(estimate);
                } else {
                    cells.add(new CellCandidate(estimate, distance));
                }
            }
        }

        // As fallback, sort cells by distance and use the closest one
        Collections.sort(cells, (a,b) -> (int)Math.signum(b.distance - a.distance));
        return serialize(cells.get(0).cell);
    }

    // The IJToS function uses the triangular lattice which only approximates the pentagon lattice
    // Thus this function only returns an cell nearby, and we need to search the neighbourhood to find the correct cell
    // TODO: Implement a more accurate function
    private static A5Cell _lonLatToEstimate(Vector2D.Double lonLat, int resolution) {
        final Vector2D.Double spherical = fromLonLat(lonLat);
        final Origin origin = findNearestOrigin(spherical);

        final Vector2D.Double polar = unprojectDodecahedron(spherical, origin.quat, origin.angle, resolution);
        final Vector2D.Double dodecPoint = toFace(polar);
        final int quintant = getQuintantPolar(polar);
        final Object[] arr = quintantToSegment(quintant, origin);
        final int segment = (int) arr[0];
        final Orientation orientation = (Orientation) arr[1];
        if (resolution < FIRST_HILBERT_RESOLUTION) {
            // For low resolutions there is no Hilbert curve
            return new A5Cell(origin, segment, 0, resolution);
        }

        // Rotate into right fifth
        if (quintant != 0) {
            final double extraAngle = 2 * PI_OVER_5 * quintant;
            final Matrix2D rotation = new Matrix2D();
            rotation.setToRotation(-extraAngle);
            rotation.transform(dodecPoint, dodecPoint);
        }

        final int hilbertResolution = 1 + resolution - FIRST_HILBERT_RESOLUTION;
        dodecPoint.scale( Math.pow(2,hilbertResolution));

        final Vector2D.Double ij = FaceToIJ(dodecPoint);
        final long S = IJToS(ij, hilbertResolution, orientation);
        final A5Cell estimate = new A5Cell(origin, segment, S, resolution);
        return estimate;
    }

    // TODO move into tiling.ts
    private static PentagonShape _getPentagon(A5Cell cell ) {
        final Object[] arr = segmentToQuintant(cell.segment, cell.origin);
        final int quintant = (int) arr[0];
        final Orientation orientation = (Orientation) arr[1];
        if (cell.resolution == (FIRST_HILBERT_RESOLUTION - 1)) {
            final PentagonShape out = getQuintantVertices(quintant);
            return out;
        } else if (cell.resolution == (FIRST_HILBERT_RESOLUTION - 2)) {
            return getFaceVertices();
        }

        final int hilbertResolution = cell.resolution - FIRST_HILBERT_RESOLUTION + 1;
        final Anchor anchor = sToAnchor(cell.S, hilbertResolution, orientation);
        return getPentagonVertices(hilbertResolution, quintant, anchor);
    }

    public static Vector2D.Double cellToLonLat(long cellId) {
        final A5Cell cell = deserialize(cellId);
        final PentagonShape pentagon = _getPentagon(cell);
        final Vector2D.Double lonLat = Project.projectPoint(pentagon.getCenter(), cell.origin, cell.resolution);
        PentagonShape.normalizeLongitudes(new Vector2D.Double[]{lonLat});
        return lonLat;
    }

    public static class CellToBoundaryOptions{
        /**
         * Pass true to close the ring with the first point
         * @default true
         */
        public boolean closedRing;
        /**
         * Number of segments to use for each edge. Pass 'null' to use the resolution of the cell.
         * @default 'null'
         */
        public Integer segments;

        public CellToBoundaryOptions() {
            this.closedRing = true;
            this.segments = null;
        }

        public CellToBoundaryOptions(boolean closedRing, Integer segments) {
            this.closedRing = closedRing;
            this.segments = segments;
        }
    }

    public static Vector2D.Double[] cellToBoundary(long cellId, CellToBoundaryOptions options) {
        final A5Cell cell = deserialize(cellId);
        final PentagonShape pentagon = _getPentagon(cell);
        Vector2D.Double[] projectedPentagon = Project.projectPentagon(pentagon, cell.origin, cell.resolution);

        Integer segments = options.segments;
        if (segments == null) {
            segments = Math.max(1,  (int)Math.pow(2, 7 - cell.resolution));
        }
        if (segments <= 1) {
            if (options.closedRing) {
                projectedPentagon = ArraysExt.concatenate(projectedPentagon, new Vector2D.Double[]{projectedPentagon[0].copy()});
            }
            // TODO: This is a patch to make the boundary CCW, but we should fix the winding order of the pentagon
            // throughout the whole codebase
            Collections.reverse(Arrays.asList(projectedPentagon));
            return projectedPentagon;
        }

        final Vector3D.Double[] cartesianPentagon = new Vector3D.Double[projectedPentagon.length];
        for (int i = 0; i < cartesianPentagon.length; i++) cartesianPentagon[i] = toCartesian(fromLonLat(projectedPentagon[i]));

        final SphericalPolygonShape sphericalPentagon = new SphericalPolygonShape(cartesianPentagon);

        final Vector3D.Double[] curvedBoundary = sphericalPentagon.getBoundary(segments, options.closedRing);
        final Vector2D.Double[] boundary = new Vector2D.Double[curvedBoundary.length];
        for (int i = 0; i < boundary.length; i++) boundary[i] = toLonLat(toSpherical(curvedBoundary[i]));
        PentagonShape.normalizeLongitudes(boundary);
        return boundary;
    }

    public static double a5cellContainsPoint(A5Cell cell, Vector2D.Double point) {
        final Vector2D.Double[] boundary = cellToBoundary(serialize(cell), new CellToBoundaryOptions(false, 1));

        final Vector3D.Double cartesian = toCartesian(fromLonLat(point));
        final Vector3D.Double[] sphericalBoundary = new Vector3D.Double[boundary.length];
        for (int i = 0; i < sphericalBoundary.length; i++) sphericalBoundary[i] = toCartesian(fromLonLat(boundary[i]));

        final SphericalPolygonShape sphericalPentagon = new SphericalPolygonShape(sphericalBoundary);
        return sphericalPentagon.containsPoint(cartesian);
    }

}
