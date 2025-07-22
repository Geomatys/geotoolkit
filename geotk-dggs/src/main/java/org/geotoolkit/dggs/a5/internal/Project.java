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

import org.apache.sis.geometries.math.Matrix2D;
import org.apache.sis.geometries.math.Quaternion;
import org.apache.sis.geometries.math.Vector2D;
import static org.geotoolkit.dggs.a5.internal.CoordinateTransforms.*;
import static org.geotoolkit.dggs.a5.internal.Constants.*;
import static org.geotoolkit.dggs.a5.internal.Dodecahedron.*;
import static org.geotoolkit.dggs.a5.internal.Orig.*;
import org.geotoolkit.dggs.a5.internal.Utils.Origin;
import org.geotoolkit.dggs.a5.internal.Utils.PentagonShape;

/**
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public class Project {

    /**
     *
     * @param vertex Face
     * @param origin
     * @return LonLat
     */
    public static final Vector2D.Double projectPoint(Vector2D.Double vertex, Origin origin, int resolution) {
        final Vector2D.Double unwarped = CoordinateTransforms.toPolar(vertex);
        final Vector2D.Double point = projectDodecahedron(unwarped, origin.quat, origin.angle, resolution);
        final Origin closest = isNearestOrigin(point, origin) ? origin : findNearestOrigin(point);

        if (closest.id != origin.id) {
            // Move point to be relative to new origin
            final Vector2D.Double dodecPoint2 = new Vector2D.Double();
            final Matrix2D rotation = new Matrix2D();
            rotation.setToRotation(origin.angle);
            rotation.transform(vertex, dodecPoint2);
            final Object[] arr = movePointToFace(dodecPoint2, origin, closest);
            final Vector2D.Double offsetDodec = (Vector2D.Double) arr[0];
            final Quaternion interfaceQuat = (Quaternion) arr[1];

            double angle2 = 0.0;
            if (origin.angle != closest.angle && closest.angle != 0) {
                angle2 = -PI_OVER_5;
            }

            Vector2D.Double polar2 = toPolar(offsetDodec);
            polar2.y = polar2.y - angle2;

            // Project back to sphere
            Vector2D.Double point2 = projectDodecahedron(polar2, interfaceQuat, angle2, resolution);
            point.x = point2.x;
            point.y = point2.y;
        }

        return CoordinateTransforms.toLonLat(point);
    }

    public static final Vector2D.Double[] projectPentagon(PentagonShape pentagon, Origin origin, int resolution) {
        final Vector2D.Double[] vertices = pentagon.getVertices();
        final Vector2D.Double[] rotatedVertices = new Vector2D.Double[vertices.length];
        for (int i = 0 ; i < vertices.length; i++) {
            rotatedVertices[i] = projectPoint(vertices[i], origin, resolution);
        }

        // Normalize longitudes to handle antimeridian crossing
        PentagonShape.normalizeLongitudes(rotatedVertices);
        return rotatedVertices;
    }

}
