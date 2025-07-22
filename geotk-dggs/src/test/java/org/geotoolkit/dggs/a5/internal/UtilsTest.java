
package org.geotoolkit.dggs.a5.internal;

import org.apache.sis.geometries.math.Vector2D;
import org.geotoolkit.dggs.a5.internal.Utils.PentagonShape;
import static org.junit.Assert.*;
import org.junit.Test;

public class UtilsTest {

    private static final double TOLERANCE = 1e-15;

    // PentagonShape ////////////////////

    // Create a simple pentagon for testing
    private static final PentagonShape pentagon = new PentagonShape(
        new Vector2D.Double(0, 2),   // top
        new Vector2D.Double(2, 1),   // upper right
        new Vector2D.Double(1, -2),  // lower right
        new Vector2D.Double(-1, -2), // lower left
        new Vector2D.Double(-2, 1)  // upper left
    );

    @Test
    public void returns_true_for_points_inside_pentagon() {
        // Test center
        assertTrue(pentagon.containsPoint(new Vector2D.Double(0, 0)) == -1);

        // Test points in different triangular regions
        assertTrue(pentagon.containsPoint(new Vector2D.Double(0, 1.5)) == -1);  // Upper triangle
        assertTrue(pentagon.containsPoint(new Vector2D.Double(1, 0)) == -1);    // Right triangle
        assertTrue(pentagon.containsPoint(new Vector2D.Double(-1, 0)) == -1);   // Left triangle
    }

    @Test
    public void returns_false_for_points_outside_pentagon() {
        // Test points clearly outside
        assertEquals(pentagon.containsPoint(new Vector2D.Double(0, 3)), 2, 0.0);
        assertEquals(pentagon.containsPoint(new Vector2D.Double(3, 0)), 2.82842, 1e-5);
        assertEquals(pentagon.containsPoint(new Vector2D.Double(0, -3)), 1.41421, 1e-5);
        assertEquals(pentagon.containsPoint(new Vector2D.Double(-3, 0)), 1.41421, 1e-5);

        // Test points just outside edges
        assertEquals(pentagon.containsPoint(new Vector2D.Double(0, 2.1)), 2, 0.0);
        assertEquals(pentagon.containsPoint(new Vector2D.Double(2.1, 1)), 0.042993, 1e-5);
    }

    @Test
    public void handles_edge_cases_correctly() {
        // Points on vertices
        assertTrue(pentagon.containsPoint(new Vector2D.Double(0, 2)) == -1);
        assertTrue(pentagon.containsPoint(new Vector2D.Double(1.9999, 0.9999)) == -1);

        // Points on edges
        assertTrue(pentagon.containsPoint(new Vector2D.Double(1, 1.49999)) == -1);  // Right edge
        assertTrue(pentagon.containsPoint(new Vector2D.Double(-1, 1.49999)) == -1); // Left edge

        // containsPointSmall
        final PentagonShape smallPentagon = new PentagonShape(
            new Vector2D.Double(0.005584805117118508, 0.007421763173983242),
            new Vector2D.Double(0.007142475800174408, 0.01035468366141623),
            new Vector2D.Double(0.010413195654227048, 0.01092979424101126),
            new Vector2D.Double(0.011970866337282948, 0.00799687375357827),
            new Vector2D.Double(0.008855524971171091, 0.006846652594388214)
          );

        final Vector2D.Double redPoint = new Vector2D.Double(0.008777835727200756, 0.007709318463780757);
        assertTrue(smallPentagon.containsPoint(redPoint) == -1);

        // containsPointOnEdge
        // Singapore pentagon, resolution 4 (in Face coordiantes, origin 8
        final PentagonShape singaporePentagon = new PentagonShape(
            new Vector2D.Double(0.24999999999999994, -0.406149620291133),
            new Vector2D.Double(0.1761431542833664, -0.48255778435927743),
            new Vector2D.Double(0.19098300562505247, -0.5877852522924732),
            new Vector2D.Double(0.29564604095473646, -0.6061887908395137),
            new Vector2D.Double(0.2998454618577896, -0.500003075888989)
         );

        final Vector2D.Double singapore = new Vector2D.Double(0.22395879916296305, -0.5770707674730963);
        assertTrue(singaporePentagon.containsPoint(singapore) == -1);
    }

    // normalizeLongitudes //////////////////////////////////

    @Test
    public void handles_simple_contour_without_wrapping() {
        final Vector2D.Double[] contour = new Vector2D.Double[]{
            new Vector2D.Double(0, 0),
            new Vector2D.Double(10, 0),
            new Vector2D.Double(10, 10),
            new Vector2D.Double(0, 10),
            new Vector2D.Double(0, 0)
        };
        final Vector2D.Double[] normalized = new Vector2D.Double[]{
            new Vector2D.Double(0, 0),
            new Vector2D.Double(10, 0),
            new Vector2D.Double(10, 10),
            new Vector2D.Double(0, 10),
            new Vector2D.Double(0, 0)
        };
        PentagonShape.normalizeLongitudes(normalized);
        assertArrayEquals(normalized, contour);
    };

    //@Test //TODO skipped in typescript code
    public void normalizes_contour_crossing_antimeridian() {
        final Vector2D.Double[] contour = new Vector2D.Double[]{
            new Vector2D.Double(170, 0),
            new Vector2D.Double(175, 0),
            new Vector2D.Double(180, 0),
            new Vector2D.Double(-175, 0),  // This should become 185
            new Vector2D.Double(-170, 0),  // This should become 190
        };
        PentagonShape.normalizeLongitudes(contour);
        assertEquals(contour[3].x, 185, TOLERANCE);
        assertEquals(contour[4].x, 190, TOLERANCE);
    };


    @Test
    public void normalizes_contour_crossing_antimeridian_in_opposite_direction() {
        final Vector2D.Double[] contour = new Vector2D.Double[]{
            new Vector2D.Double(-170, 0),
            new Vector2D.Double(-175, 0),
            new Vector2D.Double(-180, 0),
            new Vector2D.Double(175, 0),   // This should become -185
            new Vector2D.Double(170, 0),   // This should become -190
        };
        PentagonShape.normalizeLongitudes(contour);
        assertEquals(contour[3].x, -185, TOLERANCE);
        assertEquals(contour[4].x, -190, TOLERANCE);
    }

}
