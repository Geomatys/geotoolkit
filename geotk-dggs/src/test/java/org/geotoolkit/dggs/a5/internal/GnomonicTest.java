
package org.geotoolkit.dggs.a5.internal;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.sis.geometries.math.Vector2D;
import static org.geotoolkit.dggs.a5.internal.Gnomonic.*;
import static org.junit.Assert.*;
import org.junit.Test;

public class GnomonicTest {

    private static final double TOLERANCE = 1e-9;

    @Test
    public void project() {

        final Map<Vector2D.Double, Vector2D.Double> testValues = new LinkedHashMap();
        testValues.put(new Vector2D.Double(0.001, 0), new Vector2D.Double(0, 0.001));
        testValues.put(new Vector2D.Double(0.001, 0.321), new Vector2D.Double(0.321, 0.001));
        testValues.put(new Vector2D.Double(1, Math.PI), new Vector2D.Double(Math.PI, Math.PI / 4));
        testValues.put(new Vector2D.Double(0.5, 0.777), new Vector2D.Double(0.777, Math.atan(0.5)));


        for (Entry<Vector2D.Double, Vector2D.Double> test : testValues.entrySet()) {
            final Vector2D.Double result = projectGnomonic(test.getKey());
            assertEquals(result.x, test.getValue().x, TOLERANCE);
            assertEquals(result.y, test.getValue().y, TOLERANCE);
        }

        for (Entry<Vector2D.Double, Vector2D.Double> test : testValues.entrySet()) {
            final Vector2D.Double result = unprojectGnomonic(test.getValue());
            assertEquals(result.x, test.getKey().x, TOLERANCE);
            assertEquals(result.y, test.getKey().y, TOLERANCE);
        }

        Vector2D.Double polar = new Vector2D.Double(0.3, 0.4);
        Vector2D.Double spherical = projectGnomonic(polar);
        Vector2D.Double result = unprojectGnomonic(spherical);
        assertEquals(result.x, polar.x, TOLERANCE);
        assertEquals(result.y, polar.y, TOLERANCE);
    }

    @Test
    public void polar_coordinates_round_trip() {
        for (Vector2D.Double polar : TestPolarCoordinates.COORDINATES) {
            final Vector2D.Double spherical = projectGnomonic(polar);
            final Vector2D.Double result = unprojectGnomonic(spherical);
            // Check that result values are close to original
            assertEquals(result.x, polar.x, TOLERANCE);
            assertEquals(result.y, polar.y, TOLERANCE);
        }
    }

}
