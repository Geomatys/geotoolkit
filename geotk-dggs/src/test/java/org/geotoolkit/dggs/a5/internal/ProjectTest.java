
package org.geotoolkit.dggs.a5.internal;

import org.geotoolkit.dggs.a5.internal.Utils;
import org.apache.sis.geometries.math.Vector2D;
import static org.geotoolkit.dggs.a5.internal.Orig.origins;
import static org.geotoolkit.dggs.a5.internal.Project.*;
import org.geotoolkit.dggs.a5.internal.Utils.Origin;
import org.geotoolkit.dggs.a5.internal.Utils.PentagonShape;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class ProjectTest {

    private static final double TOLERANCE = 1e-5;

    @Test
    public void testProjectPoint() {
        // projects points from different faces
        final record TestCase(Vector2D.Double vertex, Origin origin, int resolution, Vector2D.Double expected){}

        final TestCase[] testCases = new TestCase[]{
            new TestCase(new Vector2D.Double(1.0, 0.0), origins.get(0), 1, new Vector2D.Double(-93, 38.68195943554304)),
            new TestCase(new Vector2D.Double(0.0, 1.0), origins.get(1), 2, new Vector2D.Double(-32.95846891893504, 45.251163281983025)),
            new TestCase(new Vector2D.Double(-0.5, 0.5), origins.get(2), 3, new Vector2D.Double(-32.3123071481169, 0.8320967050080308))
        };

        for (TestCase testCase : testCases) {
            Vector2D.Double result = projectPoint(testCase.vertex, testCase.origin, testCase.resolution);
            assertEquals(result.x, testCase.expected.x, TOLERANCE);
            assertEquals(result.y, testCase.expected.y, TOLERANCE);
        }
    }

    @Test
    public void testProjectPentagon() {
        // projects pentagons from different faces
        final record TestCase(Utils.PentagonShape pentagon, Origin origin, int resolution, double[][] expected){}
        final TestCase[] testCases = new TestCase[]{
            new TestCase(new PentagonShape(
                        new Vector2D.Double(1.0, 0.0),
                        new Vector2D.Double(0.309, 0.951),
                        new Vector2D.Double(-0.809, 0.588),
                        new Vector2D.Double(-0.809, -0.588),
                        new Vector2D.Double(0.309, -0.951)
                    ),
                    origins.get(0),
                    1,
                    new double[][]{
                        {-164.99991828414556, 38.68497101752417},
                        {123.01142419979746, 38.676217006801735},
                        {50.98857580020257, 38.676217006801735},
                        {-21.000081715854407, 38.68497101752417},
                        {-93, 38.68195943554304}
                    }),
            new TestCase(new PentagonShape(
                        new Vector2D.Double(0.5, 0.0),
                        new Vector2D.Double(0.154, 0.475),
                        new Vector2D.Double(-0.404, 0.294),
                        new Vector2D.Double(-0.404, -0.294),
                        new Vector2D.Double(0.154, -0.475)
                    ),
                    origins.get(1),
                    2,
                    new double[][]{
                        {-107.74707132624515, 5.298302558481599},
                        {-121.74880868761667, 31.608092363052112},
                        {-92.97005161996529, 52.22826407766257},
                        {-64.27272477825645, 31.640683029484702},
                        {-78.24780847310419, 5.260998102115636}
                    })
        };
        for (TestCase testCase : testCases) {
            final Vector2D.Double[] result = projectPentagon(testCase.pentagon, testCase.origin, testCase.resolution);

            // Verify each vertex of the pentagon
            for (int i = 0; i < result.length; i++) {
                assertEquals(result[i].x, testCase.expected[i][0], TOLERANCE);
                assertEquals(result[i].y, testCase.expected[i][1], TOLERANCE);
            }
        }
    }
}
