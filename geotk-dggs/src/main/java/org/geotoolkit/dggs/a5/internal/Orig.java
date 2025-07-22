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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import org.apache.sis.geometries.math.Quaternion;
import org.apache.sis.geometries.math.Vector2D;
import org.apache.sis.geometries.math.Vector3D;
import static org.geotoolkit.dggs.a5.internal.CoordinateTransforms.*;
import static org.geotoolkit.dggs.a5.internal.Constants.*;
import org.geotoolkit.dggs.a5.internal.Hilbert.Orientation;
import org.geotoolkit.dggs.a5.internal.Utils.Origin;

/**
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public class Orig {

    private static final Vector3D.Double UP = new Vector3D.Double(0, 0, 1);

    // Quintant layouts (clockwise & counterclockwise)
    public static final Orientation[] clockwiseFan = new Orientation[]{Orientation.vu, Orientation.uw, Orientation.vw, Orientation.vw, Orientation.vw};
    public static final Orientation[] clockwiseStep = new Orientation[]{Orientation.wu, Orientation.uw, Orientation.vw, Orientation.vu, Orientation.uw};
    public static final Orientation[] counterStep = new Orientation[]{Orientation.wu, Orientation.uv, Orientation.wv, Orientation.wu, Orientation.uw};
    public static final Orientation[] counterJump = new Orientation[]{Orientation.vu, Orientation.uv, Orientation.wv, Orientation.wu, Orientation.uw};

    public static final Orientation[][] QUINTANT_ORIENTATIONS = new Orientation[][]{
        clockwiseFan,   // 0 Arctic
        counterJump,    // 1 North America
        counterStep,    // 2 South America

        clockwiseStep,  // 3 North Atlantic & Western Europe & Africa
        counterStep,    // 4 South Atlantic & Africa
        counterJump,    // 5 Europe, Middle East & CentralAfrica

        counterStep,    // 6 Indian Ocean
        clockwiseStep,  // 7 Asia
        clockwiseStep,  // 8 Australia

        clockwiseStep,  // 9 North Pacific
        counterJump,    // 10 South Pacific
        counterJump,    // 11 Antarctic
    };

    // Within each face, these are the indices of the first quintant
    private static final int[] QUINTANT_FIRST = new int[]{4, 2, 3,  2, 0, 4,  3, 2, 2,  0, 3, 0};

    // Placements of dodecahedron faces along the Hilbert curve
    private static final List<Integer> ORIGIN_ORDER = List.of(0, 1, 2,  4, 3, 5,  7, 8, 6,  11, 10, 9);

    public static final List<Origin> origins = new ArrayList<>();

    static {
        final AtomicInteger originId = new AtomicInteger(0);

        final BiConsumer<Vector2D.Double, Double> addOrigin = new BiConsumer<Vector2D.Double, Double>() {
            @Override
            public void accept(Vector2D.Double axis, Double angle) {
                final int oid = originId.get();
                final Origin origin = new Origin(
                    oid,
                    axis,
                    quatFromSpherical(axis),
                    angle,
                    QUINTANT_ORIENTATIONS[oid],
                    QUINTANT_FIRST[oid]
                );
                origins.add(origin);
                originId.incrementAndGet();
            }
        };

        // North pole
        addOrigin.accept(new Vector2D.Double(0,0), 0.0);
        // Middle band
        for (int i = 0; i < 5; i++) {
            final double alpha = i * TWO_PI_OVER_5;
            final double alpha2 = alpha + PI_OVER_5;
            addOrigin.accept(new Vector2D.Double(alpha, interhedralAngle), PI_OVER_5);
            addOrigin.accept(new Vector2D.Double(alpha2, Math.PI - interhedralAngle), PI_OVER_5);
        }
        // South pole
        addOrigin.accept(new Vector2D.Double(0, Math.PI), 0.0);

        // Reorder origins to match the order of the hilbert curve
        origins.sort((Origin a, Origin b) -> ORIGIN_ORDER.indexOf(a.id) - ORIGIN_ORDER.indexOf(b.id));
        for (int i = 0, n = origins.size(); i < n; i++) {
            Origin o = origins.get(i);
            origins.set(i, new Origin(i, o.axis, o.quat, o.angle, o.orientation, o.firstQuintant));
        }
    }

    /**
     * @return [segment, Orientation]
     */
    public static Object[] quintantToSegment(int quintant, Origin origin) {
        // Lookup winding direction of this face
        final Orientation[] layout = origin.orientation;
        final int step = (layout == clockwiseFan || layout == clockwiseStep) ? -1 : 1;

        // Find (CCW) delta from first quintant of this face
        final int delta = (quintant - origin.firstQuintant + 5) % 5;

        // To look up the orientation, we need to use clockwise/counterclockwise counting
        final int faceRelativeQuintant = (step * delta + 5) % 5;
        final Orientation orientation = layout[faceRelativeQuintant];
        final int segment = (origin.firstQuintant + faceRelativeQuintant) % 5;

        return new Object[]{segment, orientation};
    }

    /**
     * @return [quintant, Orientation]
     */
    public static Object[] segmentToQuintant(int segment, Origin origin) {
        // Lookup winding direction of this face
        final Orientation[] layout = origin.orientation;
        final int step = (layout == clockwiseFan || layout == clockwiseStep) ? -1 : 1;

        final int faceRelativeQuintant = (segment - origin.firstQuintant + 5) % 5;
        final Orientation orientation = layout[faceRelativeQuintant];
        final int quintant = (origin.firstQuintant + step * faceRelativeQuintant + 5) % 5;

        return new Object[]{quintant, orientation};
    }

    /**
     * Move a point defined in the coordinate system of one dodecahedron face to the coordinate system of another face
     * @param point The point to move, Face
     * @param fromOrigin The origin of the current face
     * @param toOrigin The origin of the target face
     * @returns The new point and the quaternion representing the transform
     *          [Face, Quaternion]
     */
    public static Object[] movePointToFace(Vector2D.Double point, Origin fromOrigin, Origin toOrigin) {
        final Quaternion inverseQuat = fromOrigin.quat.copy().inverse();

        final Vector3D.Double toAxis = toCartesian(toOrigin.axis);

        // Transform destination axis into face space
        final Vector3D.Double localToAxis = new Vector3D.Double();
        inverseQuat.rotate(toAxis, localToAxis);

        // Flatten axis to XY plane to obtain direction, scale to get distance to new origin
        final Vector2D.Double direction = new Vector2D.Double(localToAxis.x, localToAxis.y).normalize();
        direction.scale(2 * distanceToEdge);

        // Move point to be relative to new origin
        final Vector2D.Double offsetPoint = point.copy().subtract(direction);

        // Construct relative transform from old origin to new origin
        Quaternion interfaceQuat = new Quaternion();
        interfaceQuat.fromUnitVectors(UP, localToAxis);
        interfaceQuat = fromOrigin.quat.copy().multiply(interfaceQuat);

        return new Object[]{offsetPoint, interfaceQuat};
    }

    /**
     * Find the nearest origin to a point on the sphere
     * Uses haversine formula to calculate great-circle distance
     * @param point Spherical
     */
    public static Origin findNearestOrigin(Vector2D.Double point) {
        double minDistance = Double.POSITIVE_INFINITY;
        Origin nearest = origins.get(0);
        for (Origin origin : origins) {
            double distance = haversine(point, origin.axis);
            if (distance < minDistance) {
                minDistance = distance;
                nearest = origin;
            }
        }
        return nearest;
    }

    /**
     * @param point Spherical
     * @param origin
     * @return
     */
    public static boolean isNearestOrigin(Vector2D.Double point, Origin origin) {
        return haversine(point, origin.axis) > 0.49999999;
    }

    /**
     * Modified haversine formula to calculate great-circle distance.
     * Retruns the "angle" between the two points. We need to minimize this to find the nearest origin
     * TODO figure out derivation!
     * @param point The point to calculate distance from, Spherical
     * @param axis The axis to calculate distance to, Spherical
     * @returns The "angle" between the two points
     */
    public static double haversine(Vector2D.Double point, Vector2D.Double axis) {
        final double theta = point.x;
        final double phi = point.y;
        final double theta2 = axis.x;
        final double phi2 = axis.y;
        final double dtheta = theta2 - theta;
        final double dphi = phi2 - phi;
        final double A1 = Math.sin(dphi / 2);
        final double A2 = Math.sin(dtheta / 2);
        final double angle = A1 * A1 + A2 * A2 * Math.sin(phi) * Math.sin(phi2);
        return angle;
    }
}
