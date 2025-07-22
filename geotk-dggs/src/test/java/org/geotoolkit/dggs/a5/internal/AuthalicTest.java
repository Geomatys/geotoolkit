
package org.geotoolkit.dggs.a5.internal;

import static org.geotoolkit.dggs.a5.internal.Authalic.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class AuthalicTest {

    private static final double TOLERANCE_2 = 1e-2;
    private static final double TOLERANCE_5 = 1e-5;
    private static final double TOLERANCE_10 = 1e-10;
    private static final double TOLERANCE_15 = 1e-15;

    @Test
    public void testGeodeticToAuthalic() {
        {//converts zero latitude
            final double result = geodeticToAuthalic(0);
            assertEquals(0, result, TOLERANCE_10);
        }

        {//converts small latitudes
            final double input = Math.PI / 180; // 1 degree
            final double result = geodeticToAuthalic(input);
            assertEquals(input, result, TOLERANCE_2);
        }

        {//converts medium latitudes
            final double input = Math.PI / 4; // 45 degrees
            final double result = geodeticToAuthalic(input);
            assertEquals(input, result, TOLERANCE_2);
        }

        {//converts large latitudes
            final double input = Math.PI / 2; // 90 degrees
            final double result = geodeticToAuthalic(input);
            assertEquals(input, result, TOLERANCE_2);
        }

        {//converts negative latitudes
            final double input = -Math.PI / 4; // -45 degrees
            final double result = geodeticToAuthalic(input);
            assertEquals(input, result, TOLERANCE_2);
        }
    }

    @Test
    public void testAuthalicToGeodetic() {
        {//converts zero latitude
            final double result = authalicToGeodetic(0);
            assertEquals(0, result, TOLERANCE_10);
        }

        {//converts small latitudes
            final double input = Math.PI / 180; // 1 degree
            final double result = authalicToGeodetic(input);
            assertEquals(input, result, TOLERANCE_2);
        }

        {//converts medium latitudes
            final double input = Math.PI / 4; // 45 degrees
            final double result = authalicToGeodetic(input);
            assertEquals(input, result, TOLERANCE_2);
        }

        {//converts large latitudes
            final double input = Math.PI / 2; // 90 degrees
            final double result = authalicToGeodetic(input);
            assertEquals(input, result, TOLERANCE_2);
        }

        {//converts negative latitudes
            final double input = -Math.PI / 4; // -45 degrees
            final double result = authalicToGeodetic(input);
            assertEquals(input, result, TOLERANCE_2);
        }
    }

    @Test
    public void round_trip_conversion() {
        //preserves latitude through geodetic->authalic->geodetic conversion for all latitudes
        for (double deg = -90; deg <= 90; deg++) {
            final double lat = (deg * Math.PI / 180);
            final double authalic = geodeticToAuthalic(lat);
            final double geodetic = authalicToGeodetic(authalic);
            assertEquals(lat, geodetic, TOLERANCE_15);
        }

        //preserves latitude through authalic->geodetic->authalic conversion for all latitudes
        for (double deg = -90; deg <= 90; deg++) {
            final double lat = (deg * Math.PI / 180);
            final double geodetic = authalicToGeodetic(lat);
            final double authalic = geodeticToAuthalic(geodetic);
            assertEquals(lat, authalic, TOLERANCE_15);
        }
    }

    @Test
    public void specific_conversion_values() {
        //matches reference conversion values
        //{geodetic,authalic}
        final double[][] testCases = new double[][] {
            { -90, -90.0000 },
            { -67.5, -67.4092 },
            { -45, -44.8717 },
            { -22.5, -22.4094 },
            { 0, 0 },
            { 22.5, 22.4094 },
            { 45, 44.8717 },
            { 67.5, 67.4092 },
            { 90, 90.0000 }
        };

        for (double[] entry : testCases) {
            final double geodetic = entry[0];
            final double authalic = entry[1];
            final double geodeticRad = (geodetic * Math.PI / 180);
            final double authalicRad = (authalic * Math.PI / 180);
            final double result = geodeticToAuthalic(geodeticRad);
            assertEquals(authalicRad, result, TOLERANCE_5);
        }
    }
}
