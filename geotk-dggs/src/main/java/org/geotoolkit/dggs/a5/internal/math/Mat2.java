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
package org.geotoolkit.dggs.a5.internal.math;

/**
 * Double-precision mat2 helpers (column-major 2x2 = [m00, m01, m10, m11]),
 * ported from gl-matrix v3.4.3 (© 2015-2021 Brandon Jones, Colin MacKenzie IV;
 * MIT). Every array is a {@code double[4]}.
 *
 * <p>Port of upstream {@code math/mat2.ts}.</p>
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public final class Mat2 {

    private Mat2() {
    }

    /**
     * Creates a new identity mat2.
     */
    public static double[] create() {
        final double[] out = new double[4];
        out[0] = 1;
        out[3] = 1;
        return out;
    }

    /**
     * Creates a mat2 from a given angle (radians).
     */
    public static double[] fromRotation(double[] out, double rad) {
        final double s = Math.sin(rad);
        final double c = Math.cos(rad);
        out[0] = c;
        out[1] = s;
        out[2] = -s;
        out[3] = c;
        return out;
    }
}
