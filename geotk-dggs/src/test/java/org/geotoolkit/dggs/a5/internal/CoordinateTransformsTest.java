
package org.geotoolkit.dggs.a5.internal;

import java.util.List;
import org.apache.sis.geometries.math.Vector2D;
import org.apache.sis.geometries.math.Vector3D;
import static org.geotoolkit.dggs.a5.internal.CoordinateTransforms.*;
import static org.junit.Assert.*;
import org.junit.Test;


public class CoordinateTransformsTest {

    private static final double TOLERANCE = 1e-14;
    private static final List<Vector2D.Double> TEST_POINTS = List.of(
        new Vector2D.Double(0, 0),     // Equator
        new Vector2D.Double(90, 0),    // Equator
        new Vector2D.Double(180, 0),   // Equator
        new Vector2D.Double(0, 45),    // Mid latitude
        new Vector2D.Double(0, -45),   // Mid latitude
        new Vector2D.Double(-90, -45), // West hemisphere mid-latitude
        new Vector2D.Double(180, 45),  // Date line mid-latitude
        new Vector2D.Double(90, 45),   // East hemisphere mid-latitude
        new Vector2D.Double(0, 90),    // North pole
        new Vector2D.Double(0, -90),   // South pole
        new Vector2D.Double(123, 45)   // Arbitrary point
        );

    @Test
    public void converts_degrees_to_radians() {
        assertEquals(degToRad(180), Math.PI, 0.0);
        assertEquals(degToRad(90), Math.PI/2, 0.0);
        assertEquals(degToRad(0.0), 0.0, 0.0);
    }

    @Test
    public void converts_radians_to_degrees() {
        assertEquals(radToDeg(Math.PI), 180.0, 0.0);
        assertEquals(radToDeg(Math.PI/2), 90.0, 0.0);
        assertEquals(radToDeg(0.0), 0.0, 0.0);
    }

    @Test
    public void converts_spherical_to_cartesian_coordinates() {
        // Test north pole
        final Vector3D.Double northPole = toCartesian(new Vector2D.Double(0,0));
        assertEquals(northPole.x, 0.0, TOLERANCE);
        assertEquals(northPole.y, 0.0, TOLERANCE);
        assertEquals(northPole.z, 1.0, TOLERANCE);

        // Test equator at 0 longitude
        final Vector3D.Double equator0 = toCartesian(new Vector2D.Double(0, Math.PI/2));
        assertEquals(equator0.x, 1.0, TOLERANCE);
        assertEquals(equator0.y, 0.0, TOLERANCE);
        assertEquals(equator0.z, 0.0, TOLERANCE);

        // Test equator at 90° longitude
        final Vector3D.Double equator90 = toCartesian(new Vector2D.Double(Math.PI/2, Math.PI/2));
        assertEquals(equator90.x, 0.0, TOLERANCE);
        assertEquals(equator90.y, 1.0, TOLERANCE);
        assertEquals(equator90.z, 0.0, TOLERANCE);
    }

    @Test
    public void converts_cartesian_to_spherical_coordinates() {
        // Test round trip conversion
        final Vector2D.Double original = new Vector2D.Double(Math.PI/4, Math.PI/6);
        final Vector3D.Double cartesian = toCartesian(original);
        final Vector2D.Double spherical = toSpherical(cartesian);

        assertEquals(spherical.x, original.x, TOLERANCE);
        assertEquals(spherical.y, original.y, TOLERANCE);
    }

    @Test
    public void converts_longitudelatitude_to_spherical_coordinates() {
        // Test Greenwich equator
        final Vector2D.Double greenwich = fromLonLat(new Vector2D.Double(0, 0));
        // Match OFFSET_LON: 93
        assertEquals(greenwich.x, degToRad(93.0), TOLERANCE);
        assertEquals(greenwich.y, Math.PI/2, TOLERANCE);  // 90° colatitude = equator

        // Test north pole
        final Vector2D.Double northPole = fromLonLat(new Vector2D.Double(0, 90));
        assertEquals(northPole.y, 0.0, TOLERANCE);  // 0° colatitude = north pole

        // Test south pole
        final Vector2D.Double southPole = fromLonLat(new Vector2D.Double(0, -90));
        assertEquals(southPole.y, Math.PI, TOLERANCE);  // 180° colatitude = south pole
    }

    @Test
    public void converts_spherical_to_longitudelatitude_coordinates() {
        // Test round trip conversion
        for (Vector2D.Double original : TEST_POINTS) {
            final Vector2D.Double spherical = fromLonLat(original);
            final Vector2D.Double lonLat = toLonLat(spherical);

            assertEquals(lonLat.x, original.x, TOLERANCE);
            assertEquals(lonLat.y, original.y, TOLERANCE);
        }
    }

}
