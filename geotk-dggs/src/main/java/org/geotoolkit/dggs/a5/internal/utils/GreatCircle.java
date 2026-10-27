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
package org.geotoolkit.dggs.a5.internal.utils;

import java.util.ArrayList;
import java.util.List;
import org.geotoolkit.dggs.a5.internal.math.Vec3;
import static org.geotoolkit.dggs.a5.internal.core.Constants.AUTHALIC_RADIUS_EARTH;
import static org.geotoolkit.dggs.a5.internal.utils.Vector.precomputeSlerp;
import static org.geotoolkit.dggs.a5.internal.utils.Vector.slerp;

/**
 * Great-circle distance and arc sampling on the authalic sphere.
 *
 * <p>Port of upstream {@code utils/great-circle.ts}.</p>
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public final class GreatCircle {

    private GreatCircle() {
    }

    /**
     * Great-circle distance in meters between two unit vectors on the authalic sphere.
     *
     * @param a Cartesian [x, y, z] unit vector
     * @param b Cartesian [x, y, z] unit vector
     * @return distance in meters
     */
    public static double greatCircleDistance(double[] a, double[] b) {
        return Vec3.angle(a, b) * AUTHALIC_RADIUS_EARTH;
    }

    /**
     * Sample interior points along the great-circle arc from {@code a} to {@code b} at roughly
     * {@code sampleInterval} meters spacing. Endpoints are NOT included — the caller
     * already has them. Returned vectors live on the authalic unit sphere.
     *
     * @param a Cartesian [x, y, z] unit vector
     * @param b Cartesian [x, y, z] unit vector
     * @param sampleInterval spacing in meters
     * @return interior sample points, Cartesian [x, y, z] unit vectors (possibly empty)
     */
    public static double[][] sampleGreatCircleArc(double[] a, double[] b, double sampleInterval) {
        final double dist = greatCircleDistance(a, b);
        final double numSegments = Math.max(1, Math.ceil(dist / sampleInterval));
        final List<double[]> samples = new ArrayList<>();
        if (numSegments <= 1) return new double[0][];
        final Vector.SlerpContext slerpCtx = precomputeSlerp(a, b);
        for (int j = 1; j < numSegments; j++) {
            final double[] v = Vec3.create();
            slerp(v, a, b, j / numSegments, slerpCtx);
            samples.add(v);
        }
        return samples.toArray(new double[samples.size()][]);
    }
}
