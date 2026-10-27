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

/**
 * Origin of the A5 DGGS : one of the twelve pentagonal faces of the dodecahedron.
 *
 * <p>Port of the {@code Origin} and {@code OriginId} types of upstream {@code core/utils.ts}.</p>
 *
 * @param id origin identifier, {@code OriginId} in range 0 to 11
 * @param axis Spherical [theta, phi] in radians, the face center
 * @param quat quaternion [x, y, z, w] rotating the north pole to the face center
 * @param inverseQuat inverse of {@code quat}
 * @param angle rotation angle of the face, in radians
 * @param orientation orientation of the space-filling curve for each of the five quintants
 * @param firstQuintant index of the first quintant
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public record Origin(
        int id,
        double[] axis,
        double[] quat,
        double[] inverseQuat,
        double angle,
        Orientation[] orientation,
        int firstQuintant) {
}
