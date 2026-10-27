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
 * Double-precision quaternion helpers, ported from gl-matrix v3.4.3
 * (© 2015-2021 Brandon Jones, Colin MacKenzie IV; MIT). Only the operations A5
 * uses are included; every array is a {@code double[4]} = [x, y, z, w].
 *
 * <p>Port of upstream {@code math/quat.ts}.</p>
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public final class Quat {

    private Quat() {
    }

    /**
     * Creates a new identity quat.
     */
    public static double[] create() {
        final double[] out = new double[4];
        out[3] = 1;
        return out;
    }

    /**
     * Calculates the conjugate of a quat (assumes unit length → equals inverse).
     */
    public static double[] conjugate(double[] out, double[] a) {
        out[0] = -a[0];
        out[1] = -a[1];
        out[2] = -a[2];
        out[3] = a[3];
        return out;
    }
}
