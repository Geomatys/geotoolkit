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
 * Double-precision mat2d helper (2x3 affine = [a, b, c, d, tx, ty]), ported
 * from gl-matrix v3.4.3 (© 2015-2021 Brandon Jones, Colin MacKenzie IV; MIT).
 * Every array is a {@code double[6]}.
 *
 * <p>Port of upstream {@code math/mat2d.ts}.</p>
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public final class Mat2d {

    private Mat2d() {
    }

    /**
     * Create a new mat2d with the given values.
     */
    public static double[] fromValues(double a, double b, double c, double d, double tx, double ty) {
        final double[] out = new double[6];
        out[0] = a;
        out[1] = b;
        out[2] = c;
        out[3] = d;
        out[4] = tx;
        out[5] = ty;
        return out;
    }
}
