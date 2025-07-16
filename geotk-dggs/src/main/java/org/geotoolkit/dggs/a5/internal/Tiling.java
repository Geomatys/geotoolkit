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
import static org.geotoolkit.dggs.a5.internal.Constants.*;
import static org.geotoolkit.dggs.a5.internal.Pentagon.*;
import org.geotoolkit.dggs.a5.internal.Utils.PentagonShape;

/**
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public final class Tiling {

    public static final boolean TRIANGLE_MODE = false;

    public static final Vector2D.Double shiftRight = new Vector2D.Double(w);
    public static final Vector2D.Double shiftLeft = new Vector2D.Double(-w.x, -w.y);

    /**
     * Define transforms for each pentagon in the primitive unit
     * Using pentagon vertices and angle as the basis for the transform
     */
    public static final Matrix2D[] QUINTANT_ROTATIONS = new Matrix2D[]{
        new Matrix2D(),
        new Matrix2D(),
        new Matrix2D(),
        new Matrix2D(),
        new Matrix2D()
    };
    static {
        QUINTANT_ROTATIONS[0].setToRotation(TWO_PI_OVER_5 * 0);
        QUINTANT_ROTATIONS[1].setToRotation(TWO_PI_OVER_5 * 1);
        QUINTANT_ROTATIONS[2].setToRotation(TWO_PI_OVER_5 * 2);
        QUINTANT_ROTATIONS[3].setToRotation(TWO_PI_OVER_5 * 3);
        QUINTANT_ROTATIONS[4].setToRotation(TWO_PI_OVER_5 * 4);
    }

    /**
     * Get pentagon vertices
     * @param resolution The resolution level
     * @param quintant The quintant index (0-4)
     * @param anchor The anchor information
     * @returns A pentagon shape with transformed vertices
     */
    public static PentagonShape getPentagonVertices(int resolution, int quintant, Hilbert.Anchor anchor) {
        final PentagonShape pentagon = (TRIANGLE_MODE ? TRIANGLE : PENTAGON).clone();

        final Vector2D.Double translation = new Vector2D.Double();
        BASIS.transform(anchor.offset, translation);

        // Apply transformations based on anchor properties
        if (anchor.flips[0] == Hilbert.NO && anchor.flips[1] == Hilbert.YES) {
            pentagon.rotate180();
        }

        final int k = anchor.k;
        int F = anchor.flips[0] + anchor.flips[1];
        if (
            // Orient last two pentagons when both or neither flips are YES
            ((F == -2 || F == 2) && k > 1) ||
            // Orient first & last pentagons when only one of flips is YES
            (F == 0 && (k == 0 || k == 3))
        ) {
            pentagon.reflectY();
        }

        if (anchor.flips[0] == Hilbert.YES && anchor.flips[1] == Hilbert.YES) {
            pentagon.rotate180();
        } else if (anchor.flips[0] == Hilbert.YES) {
            pentagon.translate(shiftLeft);
        } else if (anchor.flips[1] == Hilbert.YES) {
            pentagon.translate(shiftRight);
        }

        // Position within quintant
        pentagon.translate(translation);
        pentagon.scale(1.0 / (Math.pow(2, resolution)));
        pentagon.transform(QUINTANT_ROTATIONS[quintant]);

        return pentagon;
    }

    // TODO: memoize these two functions?
    public static PentagonShape getQuintantVertices(int quintant) {
        final PentagonShape triangle = TRIANGLE.clone();
        triangle.transform(QUINTANT_ROTATIONS[quintant]);
        return triangle;
    }

    public static PentagonShape getFaceVertices() {
        final Vector2D.Double f0 = new Vector2D.Double(); QUINTANT_ROTATIONS[0].transform(v, f0);
        final Vector2D.Double f1 = new Vector2D.Double(); QUINTANT_ROTATIONS[1].transform(v, f1);
        final Vector2D.Double f2 = new Vector2D.Double(); QUINTANT_ROTATIONS[2].transform(v, f2);
        final Vector2D.Double f3 = new Vector2D.Double(); QUINTANT_ROTATIONS[3].transform(v, f3);
        final Vector2D.Double f4 = new Vector2D.Double(); QUINTANT_ROTATIONS[4].transform(v, f4);
        // Need to reverse to obtain correct winding order
        return new PentagonShape(f4, f3, f2, f1, f0);
    }

    public static int getQuintantPolar(Vector2D.Double polar) {
        return (int) ((Math.round(polar.y / TWO_PI_OVER_5) + 5) % 5);
    }
}
