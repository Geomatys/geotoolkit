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
import org.apache.sis.geometries.math.Vector2D;
import org.apache.sis.referencing.operation.matrix.NoninvertibleMatrixException;
import static org.geotoolkit.dggs.a5.internal.Constants.*;
import org.geotoolkit.dggs.a5.internal.Utils.PentagonShape;

/**
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public class Pentagon {

    // Pentagon vertex angles in degrees
    public static final double A = 72;
    public static final double B = 127.94543761193603;
    public static final double C = 108;
    public static final double D = 82.29202980963508;
    public static final double E = 149.7625318412527;

    static final Vector2D.Double a = new Vector2D.Double(0, 0);
    static final Vector2D.Double b = new Vector2D.Double(0, 1);
    // c & d calculated by circle intersections. Perhaps can obtain geometrically.
    static final Vector2D.Double c = new Vector2D.Double(0.7885966681787006, 1.6149108024237764);
    static final Vector2D.Double d = new Vector2D.Double(1.6171013659387945, 1.054928690397459);
    static final Vector2D.Double e = new Vector2D.Double(Math.cos(PI_OVER_10), Math.sin(PI_OVER_10));


    // Distance to edge midpoint
    public static final double edgeMidpointD;

    // Lattice growth direction is AC, want to rotate it so that it is parallel to x-axis
    public static final double BASIS_ROTATION; // -27.97 degrees

    /**
     * Definition of pentagon used for tiling the plane.
     * While this pentagon is not equilateral, it forms a tiling with 5 fold
     * rotational symmetry and thus can be used to tile a regular pentagon.
     */
    public static final PentagonShape PENTAGON;

    public static final double bisectorAngle;

    // Define triangle also, as UVW
    public static final Vector2D.Double u;
    public static final double L;

    public static final double V;
    public static final Vector2D.Double v;

    public static final double W;
    public static final Vector2D.Double w;
    public static final PentagonShape TRIANGLE; // TODO hacky, don't pretend this is pentagon

    /**
     * Basis vectors used to layout primitive unit
     */
    public static final Matrix2D BASIS;
    public static final Matrix2D BASIS_INVERSE;

    static {
        edgeMidpointD = 2 * c.length() * Math.cos(PI_OVER_5);
        BASIS_ROTATION = PI_OVER_5 - Math.atan2(c.y, c.x); // -27.97 degrees

        // Scale to match unit sphere
        final double scale = (2 * distanceToEdge) / edgeMidpointD;
        for (Vector2D.Double v : new Vector2D.Double[]{a,b,c,d,e}) {
            v.scale(scale);
            v.rotate(new Vector2D.Double(0,0), BASIS_ROTATION);
        }

        PENTAGON = new PentagonShape(a, b, c, d, e);
        bisectorAngle = Math.atan2(c.y, c.x) - PI_OVER_5;

        // Define triangle also, as UVW
        u  = new Vector2D.Double(0, 0);
        L = distanceToEdge / Math.cos(PI_OVER_5);

        V = bisectorAngle + PI_OVER_5;
        v = new Vector2D.Double(L * Math.cos(V), L * Math.sin(V));

        W = bisectorAngle - PI_OVER_5;
        w = new Vector2D.Double(L * Math.cos(W), L * Math.sin(W));
        TRIANGLE = new PentagonShape(u, v, w); // TODO hacky, don't pretend this is pentagon

        /**
         * Basis vectors used to layout primitive unit
         */
        BASIS = new Matrix2D(v.x, w.x, v.y, w.y); //warning : SIS matrix parameter order is row first, gl-matrix is column first
        try {
            BASIS_INVERSE = Matrix2D.castOrCopy(BASIS.inverse());
        } catch (NoninvertibleMatrixException ex) {
            throw new IllegalStateException();
        }
    }

}
