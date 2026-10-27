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

import org.geotoolkit.dggs.a5.internal.geometry.PentagonShape;
import org.geotoolkit.dggs.a5.internal.math.Vec2;
import static org.geotoolkit.dggs.a5.internal.core.Constants.PI_OVER_10;
import static org.geotoolkit.dggs.a5.internal.core.Constants.PI_OVER_5;
import static org.geotoolkit.dggs.a5.internal.core.Constants.distanceToEdge;

/**
 * Definition of the pentagon and triangle used for tiling the plane, in Face coordinates.
 *
 * <p>Port of upstream {@code core/pentagon.ts}. All arrays are shared constants: callers
 * must not modify them (clone {@link #PENTAGON} / {@link #TRIANGLE} before transforming).</p>
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public final class Pentagon {

    private Pentagon() {
    }

    // Pentagon vertex angles (degrees)
    public static final double A = 72;
    public static final double B = 127.94543761193603;
    public static final double C = 108;
    public static final double D = 82.29202980963508;
    public static final double E = 149.7625318412527;

    /** Pentagon vertices, Face [x, y]. */
    public static final double[] a, b, c, d, e;

    /**
     * Definition of pentagon used for tiling the plane.
     * While this pentagon is not equilateral, it forms a tiling with 5 fold
     * rotational symmetry and thus can be used to tile a regular pentagon.
     */
    public static final PentagonShape PENTAGON;

    /** Triangle vertices (UVW), Face [x, y]. */
    public static final double[] u, v, w;
    /** Angle of vertex v, in radians. */
    public static final double V;
    public static final PentagonShape TRIANGLE;

    /**
     * Basis vectors used to layout primitive unit, as a column-major 2x2:
     * columns are v and w, i.e. [v.x, v.y, w.x, w.y].
     */
    public static final double[] BASIS;
    /**
     * Inverse of the fixed, provably-invertible lattice basis, computed once.
     */
    public static final double[] BASIS_INVERSE;

    static {
        a = new double[]{0, 0};
        b = new double[]{0, 1};
        // c & d calculated by circle intersections. Perhaps can obtain geometrically.
        c = new double[]{0.7885966681787006, 1.6149108024237764};
        d = new double[]{1.6171013659387945, 1.054928690397459};
        e = new double[]{Math.cos(PI_OVER_10), Math.sin(PI_OVER_10)};

        // Distance to edge midpoint
        final double edgeMidpointD = 2 * Vec2.length(c) * Math.cos(PI_OVER_5);

        // Lattice growth direction is AC, want to rotate it so that it is parallel to x-axis
        final double BASIS_ROTATION = PI_OVER_5 - Math.atan2(c[1], c[0]); // -27.97 degrees

        // Scale to match unit sphere
        final double scale = (2 * distanceToEdge) / edgeMidpointD;
        for (double[] p : new double[][]{a, b, c, d, e}) {
            Vec2.scale(p, p, scale);
            Vec2.rotate(p, p, new double[]{0, 0}, BASIS_ROTATION);
        }

        PENTAGON = new PentagonShape(new double[][]{a, b, c, d, e});

        final double bisectorAngle = Math.atan2(c[1], c[0]) - PI_OVER_5;

        // Define triangle also, as UVW
        u = new double[]{0, 0};
        final double L = distanceToEdge / Math.cos(PI_OVER_5);

        V = bisectorAngle + PI_OVER_5;
        v = new double[]{L * Math.cos(V), L * Math.sin(V)};

        final double W = bisectorAngle - PI_OVER_5;
        w = new double[]{L * Math.cos(W), L * Math.sin(W)};
        TRIANGLE = new PentagonShape(new double[][]{u, v, w});

        BASIS = new double[]{v[0], v[1], w[0], w[1]};
        // Inlined 2x2 inverse (rather than a general matrix invert) — for column-major
        // [a, b, c, d] the inverse is (1/det)·[d, -b, -c, a] with det = a·d - c·b.
        // Op order matches gl-matrix's mat2.invert so BASIS_INVERSE is bit-identical.
        final double _basisDet = 1 / (v[0] * w[1] - w[0] * v[1]);
        BASIS_INVERSE = new double[]{w[1] * _basisDet, -v[1] * _basisDet, -w[0] * _basisDet, v[0] * _basisDet};
    }
}
