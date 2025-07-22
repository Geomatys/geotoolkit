
package org.geotoolkit.dggs.a5.internal;

import org.geotoolkit.dggs.a5.internal.Hilbert;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.sis.geometries.math.Vector2D;
import org.apache.sis.geometries.math.Vector3D;
import static org.geotoolkit.dggs.a5.internal.CoordinateTransforms.*;
import static org.geotoolkit.dggs.a5.internal.Constants.*;
import static org.geotoolkit.dggs.a5.internal.Orig.*;
import org.geotoolkit.dggs.a5.internal.Utils.Origin;
import static org.junit.Assert.*;
import org.junit.Test;

public class OriginTest {

    private static final double TOLERANCE = 1e-15;

    @Test
    public void has_12_origins_for_dodecahedron_faces() {
        assertEquals(12, origins.size());
    }

    @Test
    public void each_origin_has_required_properties() {
        for (Origin origin : origins) {
            // Check properties exist
            assertNotNull(origin.axis);
            assertNotNull(origin.quat);
            assertNotNull(origin.angle);

            // Check axis is unit vector when converted to cartesian
            final Vector3D.Double cartesian = toCartesian(origin.axis);
            final double length = cartesian.length();
            assertEquals(1, length, TOLERANCE);

            // Check quaternion is normalized
            final double qLength = origin.quat.length();
            assertEquals(1, qLength, TOLERANCE);
        }
    }

    @Test
    public void finds_correct_origin_for_points_at_face_centers() {
        for (Origin origin : origins) {
            final Vector2D.Double point = origin.axis;
            final Origin nearest = findNearestOrigin(point);
            assertEquals(nearest, origin);
        }
    }

    @Test
    public void finds_correct_origin_for_points_at_face_boundaries() {
        // Test points halfway between adjacent origins
        final Map<Vector2D.Double, int[]> BOUNDARY_POINTS = new LinkedHashMap();
        // Between north pole and equatorial faces
        BOUNDARY_POINTS.put(new Vector2D.Double(0, PI_OVER_5/2), new int[]{0, 1});
        // Between equatorial faces
        BOUNDARY_POINTS.put(new Vector2D.Double(2*PI_OVER_5, PI_OVER_5), new int[]{3, 4});
        // Between equatorial and south pole
        BOUNDARY_POINTS.put(new Vector2D.Double(0, Math.PI - PI_OVER_5/2), new int[]{9, 10});

        for (Entry<Vector2D.Double,int[]> entry : BOUNDARY_POINTS.entrySet()) {
            final Origin nearest = findNearestOrigin(entry.getKey());
            assertTrue(Arrays.binarySearch(entry.getValue(), nearest.id) >= 0);
        }
    }

    @Test
    public void handles_antipodal_points() {
        // Test points opposite to face centers
        for (Origin origin : origins) {
            final double theta = origin.axis.x;
            final double phi = origin.axis.y;
            // Add π to theta and π-phi to get antipodal point
            final Vector2D.Double antipodal = new Vector2D.Double(theta + Math.PI, Math.PI - phi);

            final Origin nearest = findNearestOrigin(antipodal);
            // Should find one of the faces near the antipodal point
            assertNotEquals(nearest, origin);
        }
    }


    // haversine//////////////////////////////////////////

    @Test
    public void returns_0_for_identical_points() {
        final Vector2D.Double point = new Vector2D.Double(0, 0);
        assertEquals(haversine(point, point), 0, 0);

        final Vector2D.Double point2 = new Vector2D.Double(Math.PI/4, Math.PI/3);
        assertEquals(haversine(point2, point2), 0, 0);
    }

    @Test
    public void is_symmetric() {
        final Vector2D.Double p1 = new Vector2D.Double(0, Math.PI/4);
        final Vector2D.Double p2 = new Vector2D.Double(Math.PI/2, Math.PI/3);

        final double d1 = haversine(p1, p2);
        final double d2 = haversine(p2, p1);

        assertEquals(d1, d2, TOLERANCE);
    }

    @Test
    public void increases_with_angular_separation() {
        final Vector2D.Double origin = new Vector2D.Double(0, 0);
        final Vector2D.Double[] distances = new Vector2D.Double[]{
            new Vector2D.Double(0, Math.PI/6),      // 30°
            new Vector2D.Double(0, Math.PI/4),      // 45°
            new Vector2D.Double(0, Math.PI/3),      // 60°
            new Vector2D.Double(0, Math.PI/2),      // 90°
        };

        double lastDistance = 0;
        for (Vector2D.Double point : distances) {
            final double distance = haversine(origin, point);
            assertTrue(distance >lastDistance);
            lastDistance = distance;
        }
    }

    @Test
    public void handles_longitude_separation() {
        final double lat = Math.PI/4;  // Fixed latitude
        final Vector2D.Double p1 = new Vector2D.Double(0, lat);
        final Vector2D.Double p2 = new Vector2D.Double(Math.PI, lat);
        final Vector2D.Double p3 = new Vector2D.Double(Math.PI/2, lat);

        final double d1 = haversine(p1, p2);  // 180° separation
        final double d2 = haversine(p1, p3);  // 90° separation

        assertTrue(d1 > d2);
    }

    @Test
    public void matches_expected_values_for_known_cases() {
        // Test against some pre-calculated values
        assertEquals(haversine(new Vector2D.Double(0, 0), new Vector2D.Double(0, Math.PI/2)), 0.5, TOLERANCE); // sin²(π/4) = 0.5
        assertEquals(haversine(new Vector2D.Double(0, Math.PI/4), new Vector2D.Double(Math.PI/2, Math.PI/4)), 0.25, TOLERANCE); // For points at same latitude
    }

    // face movement ///////////////////////////////

    @Test
    public void moves_point_between_faces() {
        // First origin should be top
        final Origin origin1 = origins.get(0);
        assertEquals(origin1.axis, new Vector2D.Double(0,0));

        // Move all the way to next origin
        final Origin origin2 = origins.get(1);
        final Vector2D.Double direction = new Vector2D.Double(Math.cos(origin2.axis.x), Math.sin(origin2.axis.x));
        final Vector2D.Double point = direction.copy().scale(2 * distanceToEdge);
        final Object[] result = movePointToFace(point, origin1, origin2);

        // Result should include new point and interface quaternion
        assertNotNull(result[0]);
        assertNotNull(result[1]);

        // New point should be on second origin
        assertEquals(result[0], new Vector2D.Double(0,0));
    }

    // quintant conversion ///////////////////////////////

    @Test
    public void converts_between_quintants_and_segments() {
        final Origin origin = origins.get(0);
        for (int quintant = 0; quintant < 5; quintant++) {
            final Object[] arr = quintantToSegment(quintant, origin);
            final int segment = (int) arr[0];
            final Hilbert.Orientation orientaton = (Hilbert.Orientation) arr[1];
            final Object[] arr2 = segmentToQuintant(segment, origin);
            final int roundTripQuintant = (int) arr2[0];
            assertEquals(roundTripQuintant,quintant);
        }
    }

            record BoundaryPoint(Vector2D.Double point, Origin origin){}
    @Test
    public void testIsNearestOrigin() {
        { //returns true for points at face centers
            for (Origin origin : origins) {
                Origin nearest = findNearestOrigin(origin.axis);
                assertEquals(origin, nearest);
            }
        }

        { //returns false for points at face boundaries
            // Test points halfway between adjacent origins

            final BoundaryPoint[] points = new BoundaryPoint[]{
                // Between north pole and equatorial faces
                new BoundaryPoint(new Vector2D.Double(0, PI_OVER_5/2), origins.get(0)),
                // Between equatorial faces
                new BoundaryPoint(new Vector2D.Double(2*PI_OVER_5, PI_OVER_5), origins.get(3)),
                // Between equatorial and south pole
                new BoundaryPoint(new Vector2D.Double(0, Math.PI - PI_OVER_5/2), origins.get(9))
            };

            for (BoundaryPoint bp : points) {
                assertFalse(isNearestOrigin(bp.point, bp.origin));
            }
        }
    }

}
