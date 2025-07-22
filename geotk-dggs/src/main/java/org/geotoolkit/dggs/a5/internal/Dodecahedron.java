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

import org.apache.sis.geometries.math.Quaternion;
import org.apache.sis.geometries.math.Vector2D;
import org.apache.sis.geometries.math.Vector3D;
import org.geotoolkit.dggs.a5.internal.Constants.WarpType;
import static org.geotoolkit.dggs.a5.internal.CoordinateTransforms.*;
import static org.geotoolkit.dggs.a5.internal.Gnomonic.*;
import static org.geotoolkit.dggs.a5.internal.Warp.*;

/**
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public final class Dodecahedron {

    private Dodecahedron() {}

    public static WarpType getWarpType(int resolution) {
        return (resolution < 5) ? WarpType.low : WarpType.high;
    }

    /**
     * @param unwarped Polar
     * @param originTransform
     * @param originRotation radians
     * @return Spherical
     */
    public static final Vector2D.Double projectDodecahedron(Vector2D.Double unwarped, Quaternion originTransform, double originRotation, int resolution) {
        // Warp in polar space to minimize area variation across sphere
        final Vector2D.Double p = warpPolar(unwarped, getWarpType(resolution));

        // Rotate around face axis to match origin rotation
        final Vector2D.Double polar = new Vector2D.Double(p.x, p.y + originRotation);

        // Project gnomically onto sphere and obtain cartesian coordinates
        final Vector2D.Double projectedSpherical = projectGnomonic(polar);
        final Vector3D.Double projected = toCartesian(projectedSpherical);

        // Rotate to correct orientation on globe and return spherical coordinates
        originTransform.rotate(projected, projected);
        return toSpherical(projected);
    }

    /**
     * @param spherical
     * @param originTransform
     * @param originRotation radians
     * @return Polar
     */
    public static final Vector2D.Double unprojectDodecahedron(Vector2D.Double spherical, Quaternion originTransform, double originRotation, int resolution) {
        // Transform back origin space
        final Vector3D.Double xyz = toCartesian(spherical);
        final Quaternion inverseQuat = originTransform.copy().inverse();
        final Vector3D.Double out = new Vector3D.Double(0, 0, 0);
        inverseQuat.rotate(xyz, out);

        // Unproject gnomonically to polar coordinates in origin space
        final Vector2D.Double projectedSpherical = toSpherical(out);
        final Vector2D.Double polar = unprojectGnomonic(projectedSpherical);

        // Rotate around face axis to remove origin rotation
        polar.y = (polar.y - originRotation);

        // Unwarp the polar coordinates to obtain points in lattice space
        return unwarpPolar(polar, getWarpType(resolution));
    }

}
