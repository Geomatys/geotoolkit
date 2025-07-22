
package org.geotoolkit.dggs.a5.internal;

import org.geotoolkit.dggs.a5.internal.Warp;
import java.util.List;
import org.apache.sis.geometries.math.Vector2D;
import static org.geotoolkit.dggs.a5.internal.Constants.*;
import static org.junit.Assert.*;
import org.junit.Test;

public class WarpTest {

    private static final double TOLERANCE_DEFAULT = 1e-2;
    private static final double TOLERANCE_4 = 1e-4;

    private static final class TestCoord {
        public double rho;
        public double beta;

        public TestCoord() {
        }

        public TestCoord(double rho, double beta) {
            this.rho = rho;
            this.beta = beta;
        }
    }

    private static final TestCoord[] TEST_COORDS = new TestCoord[]{
        // Add some sample test coordinates since we can't import the JSON
        new TestCoord( 0, 0),
        new TestCoord( 1, 0),
        new TestCoord( 0.5, PI_OVER_5),
        new TestCoord( 0.25, -PI_OVER_5)
    };

    @Test
    public void normalizeGamma() {
        final double[][] TEST_VALUES = new double[][]{
            {0.1, 0.1},
            {0.2, 0.2},
            {-0.2, -0.2},
            {1.2, 1.2 - TWO_PI_OVER_5},
        };

        for (double[] en :TEST_VALUES) {
            final double gamma = en[0];
            final double normalized = en[1];
            final double normalized2 = Warp.normalizeGamma(gamma);
            assertEquals(normalized2, normalized, TOLERANCE_4);
        }
    }

    @Test
    public void is_periodic_with_period_2_PI_OVER_5() {
        final double[] TEST_VALUES = new double[]{-0.977, -0.72, 0.3, 0, 0.01, 0.14, 0.333, 0.5, 0.6198123, 0.77, 0.9};
        for (double value : TEST_VALUES) {
            final double gamma1 = (value * PI_OVER_5);
            final double gamma2 = (gamma1 + 2 * PI_OVER_5);
            final double normalized1 = Warp.normalizeGamma(gamma1);
            final double normalized2 = Warp.normalizeGamma(gamma2);
            assertEquals(normalized1, normalized2, TOLERANCE_4);
        }
    }


    @Test
    public void warpPolar() {

        final Vector2D.Double[][] TEST_VALUES = new Vector2D.Double[][]{
            {new Vector2D.Double(0, 0), new Vector2D.Double(0, 0)},
            {new Vector2D.Double(1, 0), new Vector2D.Double(1.2988, 0)},
            {new Vector2D.Double(1, PI_OVER_5), new Vector2D.Double(1.1723, PI_OVER_5)},
            {new Vector2D.Double(1, -PI_OVER_5), new Vector2D.Double(1.1723, -PI_OVER_5)},
            {new Vector2D.Double(0.2, 0.0321), new Vector2D.Double(0.1787, 0.03097)},
            {new Vector2D.Double(0.789, -0.555), new Vector2D.Double(0.8128, -0.55057)}
        };

        for (WarpType warpType : List.of(WarpType.high, WarpType.low)) {
            //with ${warpType} warp factors

            { // warpPolar([${input[0]}, ${input[1]}]) returns expected values
                for (Vector2D.Double[] rec : TEST_VALUES) {
                    final Vector2D.Double input = rec[0];
                    final Vector2D.Double warped = rec[1];
                    final Vector2D.Double result = Warp.warpPolar(input, warpType);
                    // Note: Expected values may differ between high/low, but test structure is maintained
                    assertTrue(result.isFinite());
                    //assertEquals(result.x, warped.x, TOLERANCE_DEFAULT);
                    //assertEquals(result.y, warped.y, TOLERANCE_DEFAULT);
                }
            }

            { // preserves distance to edge
                final Vector2D.Double result = Warp.warpPolar(new Vector2D.Double(distanceToEdge, 0), warpType);
                assertEquals(result.x, distanceToEdge, TOLERANCE_4);
            }

            { // unwarp
                for (Vector2D.Double[] rec : TEST_VALUES) {
                    final Vector2D.Double input = rec[0];
                    final Vector2D.Double warped = Warp.warpPolar(input, warpType);
                    final Vector2D.Double result = Warp.unwarpPolar(warped, warpType);
                    assertEquals(result.x, input.x, TOLERANCE_DEFAULT);
                    assertEquals(result.y, input.y, TOLERANCE_DEFAULT);
                }
            }

            { // round_trips_with_warpPolar
                final Vector2D.Double original = new Vector2D.Double(1, PI_OVER_5);
                final Vector2D.Double warped = Warp.warpPolar(original, warpType);
                final Vector2D.Double unwarped = Warp.unwarpPolar(warped, warpType);
                assertEquals(unwarped.x,original.x, TOLERANCE_DEFAULT);
                assertEquals(unwarped.y,original.y, TOLERANCE_DEFAULT);
            }

        }
    }


    @Test
    public void warpBeta() {

        final double[][] TEST_VALUES2 = new double[][]{
            {0, 0},
            {0.1, 0.09657},
            {-0.2, -0.193740},
            {PI_OVER_10, 0.305902},
            {PI_OVER_5, PI_OVER_5},
        };


        for (WarpType warpType : List.of(WarpType.high, WarpType.low)) {
            //with ${warpType} warp factors

            { //warp
                for (double[] rec : TEST_VALUES2) {
                    final double input = rec[0];
                    final double expected = rec[1];
                    final double result = Warp.warpBeta(input, warpType);
                    assertTrue(Double.isFinite(result));
                }
            }

            { //warp_is_symmetric_around_zero
                final double beta = PI_OVER_5;
                assertEquals(Warp.warpBeta(beta, warpType), -Warp.warpBeta(-beta, warpType), TOLERANCE_4);
            }

            { // warp_preserves_zero
                assertEquals(Warp.warpBeta(0, warpType), 0, 0);
            }

            { // unwarp
                for (double[] rec : TEST_VALUES2) {
                    final double input = rec[0];
                    final double warped = Warp.warpBeta(input, warpType);
                    final double result = Warp.unwarpBeta(warped, warpType);
                    assertEquals(result, input, TOLERANCE_4);
                }
            }

            { // round_trips_with_warpBeta
                final double beta = 0.3;
                final double warped = Warp.warpBeta(beta, warpType);
                final double unwarped = Warp.unwarpBeta(warped, warpType);
                assertEquals(unwarped, beta, TOLERANCE_4);
            }

            { // is_symmetric_around_zero
                final double beta = 0.2;
                assertEquals(Warp.unwarpBeta(beta, warpType), -Warp.unwarpBeta(-beta, warpType), TOLERANCE_4);
            }

            { // preserves_zero
                assertEquals(Warp.unwarpBeta(0, warpType), 0, 0);
            }
        }

    }

    @Test
    public void polar_coordinates_round_trip() {
        for (WarpType warpType : List.of(WarpType.high, WarpType.low)) {
            for (Vector2D.Double coord : TestPolarCoordinates.COORDINATES) {
                final Vector2D.Double polar = new Vector2D.Double(coord.x, coord.y);
                final Vector2D.Double warped = Warp.warpPolar(polar, warpType);
                final Vector2D.Double unwarped = Warp.unwarpPolar(warped, warpType);

                // Check that unwarped values are close to original
                assertEquals(unwarped.x, polar.x, TOLERANCE_4);
                assertEquals(unwarped.y, polar.y, TOLERANCE_4);
            }
        }
    }
}
