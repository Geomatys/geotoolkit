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
package org.geotoolkit.dggs.a5.internal.projections;

import java.util.ArrayList;
import java.util.List;
import org.geotoolkit.dggs.a5.internal.core.Origin;
import org.geotoolkit.dggs.a5.internal.math.Vec3;
import static org.geotoolkit.dggs.a5.internal.core.Constants.distanceToEdge;
import static org.geotoolkit.dggs.a5.internal.core.Constants.distanceToVertex;
import static org.geotoolkit.dggs.a5.internal.core.CoordinateTransforms.toCartesian;
import static org.geotoolkit.dggs.a5.internal.core.Origins.origins;

/**
 * The Coordinate Reference System (CRS) of the dodecahedron is a set of 62 vertices:
 * <ul>
 *   <li>12 face centers</li>
 *   <li>20 vertices</li>
 *   <li>30 edge midpoints</li>
 * </ul>
 *
 * <p>The vertices are used as a rigid frame of reference for the dodecahedron in the
 * dodecahedron projection. By constructing them once, we can avoid recalculating
 * and be sure of their correctness.</p>
 *
 * <p>Port of upstream {@code projections/crs.ts}. Instances are immutable once constructed
 * (vertices are Cartesian [x, y, z] unit vectors shared by reference: never modify them),
 * hence thread-safe.</p>
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public class CRS {

    // Upstream: a frozen array once constructed
    private final List<double[]> vertices = new ArrayList<>();

    /**
     * A canonical spherical face triangle (face center, edge midpoint, vertex)
     * of the dodecahedron, taken from origin 0's CRS vertices. All face
     * triangles used by DodecahedronProjection are congruent and consistently
     * wound with this one, so it serves as the fixed source of the
     * EqualAreaProjection shape constants — independent of projection call order.
     *
     * <p>The indices rely on the construction order above: vertices[0] is origin
     * 0's face center, vertices[12] its first corner (after the 12 centers) and
     * vertices[32] its first edge midpoint (after the 20 corners). The corner
     * and midpoint are adjacent (π/5 apart), forming a genuine face triangle —
     * the constants-agreement test verifies this against every face triangle.</p>
     *
     * @return spherical triangle, three Cartesian [x, y, z] unit vectors (shared, do not modify)
     */
    public double[][] getCanonicalTriangle() {
        return new double[][]{this.vertices.get(0), this.vertices.get(32), this.vertices.get(12)};
    }

    public CRS() {
        this.addFaceCenters(); // 12 centers
        this.addVertices(); // 20 vertices
        this.addMidpoints(); // 30 midpoints
        if (this.vertices.size() != 62) {
            throw new IllegalStateException("Failed to construct CRS: vertices length is not 62");
        }
    }

    /**
     * Package-private access to the vertices for tests (upstream tests read the private field).
     *
     * @return the 62 vertices, in construction order (live arrays, do not modify)
     */
    List<double[]> vertices() {
        return java.util.Collections.unmodifiableList(vertices);
    }

    /**
     * @param point Cartesian [x, y, z]
     * @return the CRS vertex within 1e-5 of the point (shared array, do not modify)
     * @throws IllegalArgumentException if no vertex is close enough
     */
    public double[] getVertex(double[] point) {
        for (final double[] vertex : this.vertices) {
            if (Vec3.distance(point, vertex) < 1e-5) {
                return vertex;
            }
        }

        throw new IllegalArgumentException("Failed to find vertex in CRS");
    }

    private void addFaceCenters() {
        origins.forEach(origin -> this.add(toCartesian(origin.axis())));
    }

    private void addVertices() {
        final double phiVertex = Math.atan(distanceToVertex);

        for (final Origin origin : origins) {
            for (int i = 0; i < 5; i++) {
                final double thetaVertex = ((2 * i + 1) * Math.PI) / 5;
                final double[] vertex = toCartesian(new double[]{thetaVertex + origin.angle(), phiVertex});
                Vec3.transformQuat(vertex, vertex, origin.quat());
                this.add(vertex);
            }
        }
    }

    private void addMidpoints() {
        final double phiMidpoint = Math.atan(distanceToEdge);

        for (final Origin origin : origins) {
            for (int i = 0; i < 5; i++) {
                final double thetaMidpoint = (2 * i * Math.PI) / 5;
                final double[] midpoint = toCartesian(new double[]{thetaMidpoint + origin.angle(), phiMidpoint});
                Vec3.transformQuat(midpoint, midpoint, origin.quat());
                this.add(midpoint);
            }
        }
    }

    private boolean add(double[] newVertex) {
        final double[] normalized = Vec3.normalize(Vec3.create(), newVertex);
        for (final double[] existingVertex : this.vertices) {
            if (Vec3.distance(normalized, existingVertex) < 1e-5) {
                return false;
            }
        }
        this.vertices.add(normalized);
        return true;
    }
}
