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
package org.geotoolkit.dggs.a5.internal.geometry;

/**
 * Spherical polygon with exactly 3 vertices.
 *
 * <p>Port of upstream {@code geometry/spherical-triangle.ts}.</p>
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public class SphericalTriangleShape extends SphericalPolygonShape {

    /**
     * @param vertices 3 Cartesian [x, y, z] unit vectors, used directly (not copied)
     * @throws IllegalArgumentException if there are not exactly 3 vertices
     */
    public SphericalTriangleShape(double[][] vertices) {
        super(checkVertices(vertices));
    }

    private static double[][] checkVertices(double[][] vertices) {
        if (vertices.length != 3) {
            throw new IllegalArgumentException("SphericalTriangleShape requires exactly 3 vertices");
        }
        return vertices;
    }
}
