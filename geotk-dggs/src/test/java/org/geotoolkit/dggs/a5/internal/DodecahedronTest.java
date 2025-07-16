
package org.geotoolkit.dggs.a5.internal;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.apache.sis.geometries.math.Vector2D;
import static org.geotoolkit.dggs.a5.internal.Dodecahedron.*;
import static org.geotoolkit.dggs.a5.internal.Orig.*;
import org.geotoolkit.dggs.a5.internal.Utils.Origin;
import static org.junit.Assert.*;
import org.junit.Test;

public class DodecahedronTest {

    private static final double TOLERANCE = 1e-3;

    private static final class TestCoord {
        public double rho;
        public double beta;
    }

    @Test
    public void round_trip_test() throws IOException {

        final TestCoord[] TEST_COORDS = new ObjectMapper().readValue(
                DodecahedronTest.class.getResource("test-polar-coordinates.json"),
                TestCoord[].class);
        final int[] resolutions = new int[]{1, 2, 3, 4, 5, 6};

        for (int resolution : resolutions) {
            for (Origin origin : origins) {
                for (TestCoord tc : TEST_COORDS) {
                    final Vector2D.Double polar = new Vector2D.Double(tc.rho, tc.beta);
                    final Vector2D.Double spherical = projectDodecahedron(polar, origin.quat, origin.angle, resolution);
                    final Vector2D.Double result = unprojectDodecahedron(spherical, origin.quat, origin.angle, resolution);
                    assertEquals(result.x, polar.x, TOLERANCE);
                    assertEquals(result.y, polar.y, TOLERANCE);
                }
            }
        }
    }
}
