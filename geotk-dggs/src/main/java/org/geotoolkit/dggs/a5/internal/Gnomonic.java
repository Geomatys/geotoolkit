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

import org.apache.sis.geometries.math.Vector2D;

/**
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public final class Gnomonic {

    private Gnomonic(){}

    /**
     * @param p Polar
     * @return Spherical
     */
    public static final Vector2D.Double projectGnomonic(Vector2D.Double p) {
        return new Vector2D.Double(p.y, Math.atan(p.x));
    }

    /**
     * @param s Spherical
     * @return Polar
     */
    public static final Vector2D.Double unprojectGnomonic(Vector2D.Double s) {
        return new Vector2D.Double(Math.tan(s.y), s.x);
    }
}
