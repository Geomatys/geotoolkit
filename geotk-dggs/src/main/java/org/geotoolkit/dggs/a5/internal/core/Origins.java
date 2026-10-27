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

import java.util.ArrayList;
import java.util.List;
import org.geotoolkit.dggs.a5.internal.lattice.Orientation;
import org.geotoolkit.dggs.a5.internal.math.Quat;
import static org.geotoolkit.dggs.a5.internal.core.Constants.PI_OVER_5;
import static org.geotoolkit.dggs.a5.internal.core.Constants.TWO_PI_OVER_5;
import static org.geotoolkit.dggs.a5.internal.core.Constants.interhedralAngle;
import static org.geotoolkit.dggs.a5.internal.core.DodecahedronQuaternions.quaternions;
import static org.geotoolkit.dggs.a5.internal.lattice.Orientation.uv;
import static org.geotoolkit.dggs.a5.internal.lattice.Orientation.uw;
import static org.geotoolkit.dggs.a5.internal.lattice.Orientation.vu;
import static org.geotoolkit.dggs.a5.internal.lattice.Orientation.vw;
import static org.geotoolkit.dggs.a5.internal.lattice.Orientation.wu;
import static org.geotoolkit.dggs.a5.internal.lattice.Orientation.wv;

/**
 * The 12 origins (dodecahedron faces) of the A5 DGGS, in curve order.
 *
 * <p>Port of upstream {@code core/origin.ts}, named {@code Origins} since {@link Origin} is the record.
 * The origins are built once in the static initialiser and are immutable (their arrays are
 * shared: never modify them), so this class is thread-safe.</p>
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public final class Origins {

    /**
     * Result of {@link #quintantToSegment(int, Origin)}.
     *
     * @param segment segment (0-4) of the quintant
     * @param orientation orientation of the curve in the quintant
     */
    public record SegmentOrientation(int segment, Orientation orientation) {
    }

    /**
     * Result of {@link #segmentToQuintant(int, Origin)}.
     *
     * @param quintant quintant (0-4) of the segment
     * @param orientation orientation of the curve in the quintant
     */
    public record QuintantOrientation(int quintant, Orientation orientation) {
    }

    // Quintant layouts. Every face threads its quintants with one of two patterns,
    // entering at the vertex shared by its first two quintants and passing through
    // the face center twice. In orientation terms u is the quintant's apex (the
    // face center), v and w its outer vertices, so 'vu' runs from v to the apex.
    // - jump: exits two vertices against the winding from where it entered
    // - step: exits one vertex against the winding
    // Clockwise faces use the mirror image (v <-> w) of the counterclockwise
    // pattern. Every counterclockwise face is threaded with the jump, so the step
    // only occurs clockwise: three layouts in all.
    // (Java: shared arrays, compared by reference in faceStep: never modify them.)
    public static final Orientation[] counterJump = {vu, uv, wv, wu, uw};
    public static final Orientation[] clockwiseJump = {wu, uw, vw, vu, uv};
    public static final Orientation[] clockwiseStep = {wu, uw, vw, vu, uw};

    private static final Orientation[][] QUINTANT_ORIENTATIONS = {
        clockwiseStep, // 0 Arctic
        counterJump, // 1 North America
        counterJump, // 2 South America

        clockwiseStep, // 3 North Atlantic & Western Europe & Africa
        counterJump, // 4 South Atlantic & Africa
        counterJump, // 5 Europe, Middle East & CentralAfrica

        counterJump, // 6 Indian Ocean
        clockwiseJump, // 7 Asia
        clockwiseStep, // 8 Australia

        clockwiseJump, // 9 North Pacific
        counterJump, // 10 South Pacific
        counterJump // 11 Antarctic
    };

    // Within each face, these are the indices of the first quintant
    private static final int[] QUINTANT_FIRST = {4, 2, 2, 2, 0, 4, 3, 2, 1, 0, 3, 0};

    // Placements of dodecahedron faces along the curve
    private static final int[] ORIGIN_ORDER = {0, 1, 2, 4, 3, 5, 7, 8, 6, 11, 10, 9};

    /**
     * The 12 origins, in curve order: {@code origins.get(i).id() == i}. Unmodifiable list.
     */
    public static final List<Origin> origins;

    static {
        final List<Origin> list = new ArrayList<>(12);
        generateOrigins(list);

        // Reorder origins to match the order of the curve
        list.sort((a, b) -> indexOf(ORIGIN_ORDER, a.id()) - indexOf(ORIGIN_ORDER, b.id()));
        // TS mutates origin.id = i : records are immutable, rebuild them
        for (int i = 0; i < list.size(); i++) {
            final Origin o = list.get(i);
            list.set(i, new Origin(i, o.axis(), o.quat(), o.inverseQuat(), o.angle(), o.orientation(), o.firstQuintant()));
        }
        origins = List.copyOf(list);
    }

    private Origins() {
    }

    private static int indexOf(int[] array, int value) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == value) return i;
        }
        return -1;
    }

    private static void generateOrigins(List<Origin> origins) {
        // North pole
        addOrigin(origins, new double[]{0, 0}, 0, quaternions[0]);

        // Middle band
        for (int i = 0; i < 5; i++) {
            final double alpha = (i * TWO_PI_OVER_5);
            final double alpha2 = (alpha + PI_OVER_5);
            addOrigin(origins, new double[]{alpha, interhedralAngle}, PI_OVER_5, quaternions[i + 1]);
            addOrigin(origins, new double[]{alpha2, Math.PI - interhedralAngle}, PI_OVER_5, quaternions[((i + 3) % 5) + 6]);
        }

        // South pole
        addOrigin(origins, new double[]{0, Math.PI}, 0, quaternions[11]);
    }

    /**
     * TS module-level {@code originId} counter : the number of origins already added.
     */
    private static void addOrigin(List<Origin> origins, double[] axis, double angle, double[] quaternion) {
        final int originId = origins.size();
        if (originId > 11) {
            throw new IllegalStateException("Too many origins: " + originId);
        }
        final double[] inverseQuat = Quat.create();
        Quat.conjugate(inverseQuat, quaternion); // quaternion is a unit quaternion, so conjugate is the inverse
        final Origin origin = new Origin(
                originId,
                axis,
                quaternion,
                inverseQuat,
                angle,
                QUINTANT_ORIENTATIONS[originId],
                QUINTANT_FIRST[originId]);
        origins.add(origin);
    }

    /**
     * Direction of travel around a face: 1 for counterclockwise faces, -1 for clockwise
     *
     * @param origin face
     * @return 1 or -1
     */
    public static int faceStep(Origin origin) {
        final Orientation[] layout = origin.orientation();
        return layout == clockwiseJump || layout == clockwiseStep ? -1 : 1;
    }

    /**
     * @param quintant quintant (0-4)
     * @param origin face
     * @return segment and orientation of the quintant
     */
    public static SegmentOrientation quintantToSegment(int quintant, Origin origin) {
        // Lookup winding direction of this face
        final Orientation[] layout = origin.orientation();
        final int step = faceStep(origin);

        // Find (CCW) delta from first quintant of this face
        final int delta = (quintant - origin.firstQuintant() + 5) % 5;

        // To look up the orientation, we need to use clockwise/counterclockwise counting
        final int faceRelativeQuintant = (step * delta + 5) % 5;
        final Orientation orientation = layout[faceRelativeQuintant];
        final int segment = (origin.firstQuintant() + faceRelativeQuintant) % 5;

        return new SegmentOrientation(segment, orientation);
    }

    /**
     * @param segment segment (0-4)
     * @param origin face
     * @return quintant and orientation of the segment
     */
    public static QuintantOrientation segmentToQuintant(int segment, Origin origin) {
        // Lookup winding direction of this face
        final Orientation[] layout = origin.orientation();
        final int step = faceStep(origin);

        final int faceRelativeQuintant = (segment - origin.firstQuintant() + 5) % 5;
        final Orientation orientation = layout[faceRelativeQuintant];
        final int quintant = (origin.firstQuintant() + step * faceRelativeQuintant + 5) % 5;

        return new QuintantOrientation(quintant, orientation);
    }

    /**
     * The {@code count} origins nearest to a point, by haversine distance, nearest first.
     * Used by the boundary resolution in sphericalToCell: a point on (or within
     * float noise of) a face seam or dodecahedron vertex may belong to a cell of
     * the 2nd- or 3rd-nearest face.
     *
     * @param point Spherical [theta, phi] in radians
     * @param count number of origins to return
     * @return new list of the nearest origins, nearest first
     */
    public static List<Origin> findNearestOrigins(double[] point, int count) {
        final List<Origin> sorted = new ArrayList<>(origins);
        // Stable sort on the sign of the difference, as the JS comparator
        sorted.sort((a, b) -> {
            final double d = haversine(point, a.axis()) - haversine(point, b.axis());
            return d < 0 ? -1 : (d > 0 ? 1 : 0);
        });
        // sorted.slice(0, count), including the JS meaning of a negative count
        final int end = count < 0 ? Math.max(sorted.size() + count, 0) : Math.min(count, sorted.size());
        return new ArrayList<>(sorted.subList(0, end));
    }

    /**
     * Find the nearest origin to a point on the sphere
     * Uses haversine formula to calculate great-circle distance
     *
     * @param point Spherical [theta, phi] in radians
     * @return nearest origin
     */
    public static Origin findNearestOrigin(double[] point) {
        double minDistance = Double.POSITIVE_INFINITY;
        Origin nearest = origins.get(0);
        for (final Origin origin : origins) {
            final double distance = haversine(point, origin.axis());
            if (distance < minDistance) {
                minDistance = distance;
                nearest = origin;
            }
        }

        return nearest;
    }

    /**
     * @param point Spherical [theta, phi] in radians
     * @param origin face
     * @return value of upstream {@code isNearestOrigin}
     */
    public static boolean isNearestOrigin(double[] point, Origin origin) {
        return haversine(point, origin.axis()) > 0.49999999;
    }

    /**
     * Modified haversine formula to calculate great-circle distance.
     * Retruns the "angle" between the two points. We need to minimize this to find the nearest origin
     * TODO figure out derivation!
     *
     * @param point The point to calculate distance from, Spherical [theta, phi] in radians
     * @param axis The axis to calculate distance to, Spherical [theta, phi] in radians
     * @return The "angle" between the two points
     */
    public static double haversine(double[] point, double[] axis) {
        final double theta = point[0], phi = point[1];
        final double theta2 = axis[0], phi2 = axis[1];
        final double dtheta = (theta2 - theta);
        final double dphi = (phi2 - phi);
        final double A1 = Math.sin(dphi / 2);
        final double A2 = Math.sin(dtheta / 2);
        final double angle = A1 * A1 + A2 * A2 * Math.sin(phi) * Math.sin(phi2);
        return angle;
    }
}
