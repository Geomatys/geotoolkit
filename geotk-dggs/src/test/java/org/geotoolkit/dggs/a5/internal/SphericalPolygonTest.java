
package org.geotoolkit.dggs.a5.internal;

import org.apache.sis.geometries.math.Vector3D;
import org.geotoolkit.dggs.a5.internal.SphericalPolygon.SphericalPolygonShape;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class SphericalPolygonTest {

    private static final double TOLERANCE = 1e-6;
    private static final double TOLERANCE_10 = 1e-10;

    private static final double[][][] testPolygons = {
        // Simple triangle near north pole
        {
          {0.11043152607484655, 0, 0.9938837346736189},
          {-0.05521344008179805, 0.0960713857423286, 0.9938419214723649},
          {-0.05521344008179805, -0.0960713857423286, 0.9938419214723649}
        },
        // Pentagon around equator
        {
          {1, 0, 0},
          {0.3090182326136022, 0.9510561139661348, 0},
          {-0.8089090028554804, 0.58793386116072, 0},
          {-0.8089090028554804, -0.58793386116072, 0},
          {0.3090182326136022, -0.9510561139661348, 0}
        }
    };

    @Test
    public void getBoundary() {
        // returns boundary points with different segment counts
        final double[][][] expectedResults = {
          // Triangle with 1 segment
          {
            {0.11043152956009886, 0, 0.9938837342863687},
            {-0.055213440211813215, 0.0960713848509679, 0.9938419215513068},
            {-0.055213440211813215, -0.0960713848509679, 0.9938419215513068},
            {0.11043152956009886, 0, 0.9938837342863687}
          },
          // Pentagon with 2 segments
          {
            {0.9999999999999998, 0, 2.220446049250313e-16},
            {0.8090173744213622, 0.5877847292031038, -2.220446049250313e-16},
            {0.3090182226643831, 0.9510561171988461, -5.868212804571726e-9},
            {-0.3089290367688098, 0.9510850909572153, -5.868212582527121e-9},
            {-0.8089090123512248, 0.5879338480959965, 1.6546987779975098e-8},
            {-1.0000000000000002, -6.600235413767308e-9, 1.6546987668952795e-8},
            {-0.8089090123512248, -0.5879338480959965, 1.6546987779975098e-8},
            {-0.30892902454635696, -0.9510850949272814, 1.6546988224064307e-8},
            {0.3090182226643831, -0.9510561171988461, -5.868212804571726e-9},
            {0.8090173734529743, -0.5877847305359768, -5.868213026616331e-9},
            {0.9999999999999998, 0, 2.220446049250313e-16}
          }
        };

        for (int i = 0; i < testPolygons.length; i++) {
            double[][] testPolygon = testPolygons[i];
            final SphericalPolygonShape polygon = new SphericalPolygonShape(testPolygon);
            final Vector3D.Double[] result = polygon.getBoundary(i + 1, true);
            Assertions.assertEquals(result.length, expectedResults[i].length);
            for (int j = 0; j < result.length; j++) {
                Vector3D.Double point = result[j];
                assertEquals(point.x, expectedResults[i][j][0], TOLERANCE);
                assertEquals(point.y, expectedResults[i][j][1], TOLERANCE);
                assertEquals(point.z, expectedResults[i][j][2], TOLERANCE);

            }
        }
    }

    @Test
    public void slerp() {
        // interpolates between vertices

        for (int i = 0; i < testPolygons.length; i++) {
            final double[][] testPolygon = testPolygons[i];
            final SphericalPolygonShape polygon = new SphericalPolygonShape(testPolygon);

            for (double t : new double[]{0, 0.25, 0.5, 0.75, 1.0, 1.5}) {
                Vector3D.Double result = polygon.slerp(t);
                // Should be normalized
                assertEquals(result.length(), 1.0, TOLERANCE_10);

            }
        }
    }

    @Test
    public void containsPoint() {
        // correctly identifies points inside and outside polygon
        final double[][] expectedResults = {
            // Triangle results
            {
              -6.298927875819649e-9,   // Point on edge
              0.4975161666370207,      // North pole
              -0.027744911851070468    // South pole
            },
            // Pentagon results
            {
              -2.9067971274991057e-16, // Point on edge
              0.5719850412819585,      // North pole
              -0.5720993058152115      // South pole
            }
        };

        for (int i = 0; i < testPolygons.length; i++) {
            double[][] testPolygon = testPolygons[i];
            SphericalPolygonShape polygon = new SphericalPolygonShape(testPolygon);
            final Vector3D.Double[] points = {
                polygon.slerp(0.5), // Point on edge
                new Vector3D.Double(0, 0, 1), // North pole
                new Vector3D.Double(0, 0, -1), // South pole
            };

            for (int j = 0; j < points.length; j++) {
                double result = polygon.containsPoint(points[j]);
                assertEquals(result, expectedResults[i][j], TOLERANCE);
            }
        }
    }

}
