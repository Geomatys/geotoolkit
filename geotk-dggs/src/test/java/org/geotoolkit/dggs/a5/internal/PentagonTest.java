
package org.geotoolkit.dggs.a5.internal;

import org.apache.sis.geometries.math.Matrix2D;
import org.apache.sis.geometries.math.Vector2D;
import static org.geotoolkit.dggs.a5.internal.Pentagon.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class PentagonTest {
    private static final double TOLERANCE = 1e-15;

    @Test
    public void pentagon_angles() {
        //has correct angle values
        assertEquals(A, 72, 0.0);
        assertEquals(B, 127.94543761193603, 0.0);
        assertEquals(C, 108, 0.0);
        assertEquals(D, 82.29202980963508, 0.0);
        assertEquals(E, 149.7625318412527, 0.0);
    }

    @Test
    public void pentagon_vertices() {
        //has correct vertex coordinates
        // Vertex a
        assertEquals(a.x, 0, 0.0);
        assertEquals(a.y, 0, 0.0);

        // Vertex b
        assertEquals(b.x, 0.1993818474311588, 0.0);
        assertEquals(b.y, 0.3754138223914238, 0.0);

        // Vertex c
        assertEquals(c.x, 0.6180339887498949, 0.0);
        assertEquals(c.y, 0.4490279765795854, 0.0);

        // Vertex d
        assertEquals(d.x, 0.8174158361810537, 0.0);
        assertEquals(d.y, 0.0736141541881617, 0.0);

        // Vertex e
        assertEquals(e.x, 0.418652141318736, 0.0);
        assertEquals(e.y,-0.07361415418816161, 0.0);
    }

    @Test
    public void pentagon_shape() {
        // has correct vertices
        final double[][] expected = {
            {0, 0},
            {0.1993818474311588, 0.3754138223914238},
            {0.6180339887498949, 0.4490279765795854},
            {0.8174158361810537, 0.0736141541881617},
            {0.418652141318736, -0.07361415418816161}
        };

        final Vector2D.Double[] vertices = PENTAGON.getVertices();
        for (int i = 0; i < vertices.length; i++) {
            assertEquals(vertices[i].x, expected[i][0], 0.0);
            assertEquals(vertices[i].y, expected[i][1], 0.0);
        }
    }

    @Test
    public void triangle_vertices() {
        //has correct vertex coordinates
        // Vertex u
        assertEquals(u.x, 0);
        assertEquals(u.y, 0);

        // Vertex v
        assertEquals(v.x, 0.6180339887498949);
        assertEquals(v.y, 0.4490279765795854);

        // Vertex w
        assertEquals(w.x, 0.6180339887498949);
        assertEquals(w.y, -0.4490279765795854);

        // Angle V
        assertEquals(V, 0.6283185307179586);
    }

    @Test
    public void triangle_shape() {
        // has correct vertices
        final double[][] expected = {
            {0, 0},
            {0.6180339887498949, 0.4490279765795854},
            {0.6180339887498949, -0.4490279765795854}
        };

        final Vector2D.Double[] vertices = TRIANGLE.getVertices();
        for (int i = 0; i < vertices.length; i++) {
            assertEquals(vertices[i].x, expected[i][0], 0.0);
            assertEquals(vertices[i].y, expected[i][1], 0.0);
        }
    }

    @Test
    public void basis_matrices() {
        // has correct basis and inverse
        final double[] expectedBasis = {
            0.6180339887498949,
            0.4490279765795854,
            0.6180339887498949,
            -0.4490279765795854
        };

        final double[] expectedInverse = {
            0.8090169943749473,
            0.8090169943749473,
            1.1135163644116066,
            -1.1135163644116066
        };

        assertEquals(BASIS.m00, expectedBasis[0]);
        assertEquals(BASIS.m10, expectedBasis[1]);
        assertEquals(BASIS.m01, expectedBasis[2]);
        assertEquals(BASIS.m11, expectedBasis[3]);

        assertEquals(BASIS_INVERSE.m00, expectedInverse[0]);
        assertEquals(BASIS_INVERSE.m10, expectedInverse[1]);
        assertEquals(BASIS_INVERSE.m01, expectedInverse[2]);
        assertEquals(BASIS_INVERSE.m11, expectedInverse[3]);


        // Verify BASIS * BASIS_INVERSE = Identity
        Matrix2D result = Matrix2D.castOrCopy(BASIS.clone().multiply(BASIS_INVERSE));
        assertEquals(result.m00, 1.0, TOLERANCE);
        assertEquals(result.m01, 0.0, TOLERANCE);
        assertEquals(result.m10, 0.0, TOLERANCE);
        assertEquals(result.m11, 1.0, TOLERANCE);

    }

}
