/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.referencing.cs;

import java.util.Locale;

import org.opengis.test.Validators;
import org.opengis.util.InternationalString;
import org.opengis.referencing.cs.AxisDirection;

import org.apache.sis.util.ComparisonMode;
import org.geotoolkit.referencing.IdentifiedObjectTest;
import org.geotoolkit.internal.referencing.AxisDirections;

import org.junit.*;
import org.geotoolkit.test.Depend;
import org.geotoolkit.test.referencing.ReferencingTestBase;

import static java.lang.StrictMath.*;
import static org.geotoolkit.referencing.Assert.*;
import static org.geotoolkit.referencing.cs.DefaultCoordinateSystemAxis.*;


/**
 * Tests {@link DefaultCoordinateSystemAxis}.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.18
 *
 * @since 2.2
 */
@Depend({IdentifiedObjectTest.class, DirectionAlongMeridianTest.class})
public final strictfp class CoordinateSystemAxisTest extends ReferencingTestBase {
    /**
     * For floating point number comparisons.
     */
    private static final double EPS = 1E-10;

    /**
     * Validates the constants.
     */
    @Test
    public void validate() {
        Validators.validate(GEODETIC_LONGITUDE);
        Validators.validate(GEODETIC_LATITUDE);
        Validators.validate(LONGITUDE);
        Validators.validate(LATITUDE);
        Validators.validate(ELLIPSOIDAL_HEIGHT);
        Validators.validate(GRAVITY_RELATED_HEIGHT);
        Validators.validate(ALTITUDE);
        Validators.validate(DEPTH);
        Validators.validate(GEOCENTRIC_RADIUS);
        Validators.validate(SPHERICAL_LONGITUDE);
        Validators.validate(SPHERICAL_LATITUDE);
        Validators.validate(X);
        Validators.validate(Y);
        Validators.validate(Z);
        Validators.validate(GEOCENTRIC_X);
        Validators.validate(GEOCENTRIC_Y);
        Validators.validate(GEOCENTRIC_Z);
        Validators.validate(EASTING);
        Validators.validate(WESTING);
        Validators.validate(NORTHING);
        Validators.validate(SOUTHING);
        Validators.validate(TIME);
        Validators.validate(COLUMN);
        Validators.validate(ROW);
        Validators.validate(DISPLAY_X);
        Validators.validate(DISPLAY_Y);
    }

    /**
     * Tests WKT formatting of predefined constants.
     */
    @Test
    public void testWKT() {
        assertWktEquals(X,                   "AXIS[“x”, EAST]");
        assertWktEquals(Y,                   "AXIS[“y”, NORTH]");
        assertWktEquals(Z,                   "AXIS[“z”, UP]");
        assertWktEquals(LONGITUDE,           "AXIS[“Longitude”, EAST]");
        assertWktEquals(LATITUDE,            "AXIS[“Latitude”, NORTH]");
        assertWktEquals(ALTITUDE,            "AXIS[“Altitude”, UP]");
        assertWktEquals(TIME,                "AXIS[“Time”, FUTURE]");
        assertWktEquals(GEODETIC_LONGITUDE,  "AXIS[“Geodetic longitude”, EAST]");
        assertWktEquals(SPHERICAL_LONGITUDE, "AXIS[“Spherical longitude”, EAST]");
        assertWktEquals(GEODETIC_LATITUDE,   "AXIS[“Geodetic latitude”, NORTH]");
        assertWktEquals(SPHERICAL_LATITUDE,  "AXIS[“Spherical latitude”, NORTH]");
    }

    /**
     * Tests localization of predefined constants.
     */
    @Test
    public void testLocalization() {
        InternationalString s = TIME.getAlias().iterator().next().toInternationalString();
        assertEquals("English", "Time",  s.toString(Locale.ENGLISH));
        assertEquals("French",  "Temps", s.toString(Locale.FRENCH ));
    }

    /**
     * Tests the comparison of some axis, ignoring metadata.
     */
    @Test
    public void testEqualsIgnoreMetadata() {
        assertFalse("X",         X        .equals(GEOCENTRIC_X,        ComparisonMode.IGNORE_METADATA));
        assertFalse("Longitude", LONGITUDE.equals(GEODETIC_LONGITUDE,  ComparisonMode.STRICT));
        assertFalse("Longitude", LONGITUDE.equals(SPHERICAL_LONGITUDE, ComparisonMode.STRICT));
        assertFalse("Longitude", LONGITUDE.equals(SPHERICAL_LONGITUDE, ComparisonMode.IGNORE_METADATA));

        // Tests aliases in the special "longitude" and "latitude" cases.
        assertTrue ("Longitude", LONGITUDE.equals(GEODETIC_LONGITUDE,  ComparisonMode.IGNORE_METADATA));
        assertTrue ("Latitude",  LATITUDE .equals(GEODETIC_LATITUDE,   ComparisonMode.IGNORE_METADATA));
        assertFalse("Lon/Lat",   LATITUDE .equals(LONGITUDE,           ComparisonMode.IGNORE_METADATA));
    }

    /**
     * Tests serialization.
     */
    @Test
    public void testSerialization() {
        assertSerializable(X);
        assertSerializable(GEOCENTRIC_X);
        assertSerializable(GEODETIC_LONGITUDE);
    }

    /**
     * Tests the {@link DefaultCoordinateSystemAxis#nameMatches} method.
     */
    @Test
    public void testNameMatches() {
        assertTrue (LONGITUDE.nameMatches(GEODETIC_LONGITUDE.getName().getCode()));
        assertFalse(LONGITUDE.nameMatches(GEODETIC_LATITUDE .getName().getCode()));
        assertFalse(LONGITUDE.nameMatches(ALTITUDE          .getName().getCode()));
        assertFalse(X        .nameMatches(LONGITUDE         .getName().getCode()));
        assertFalse(X        .nameMatches(EASTING           .getName().getCode()));
        assertFalse(X        .nameMatches(NORTHING          .getName().getCode()));
    }

    /**
     * Tests the {@link DefaultCoordinateSystemAxis#getPredefined(String)} method.
     */
    @Test
    public void testPredefined() {
        assertNull(getPredefined("Dummy", null));

        // Tests some abbreviations shared by more than one axis.
        // We should get the axis with the ISO 19111 name.
        assertSame(GEODETIC_LATITUDE,  getPredefined("\u03C6", null));
        assertSame(GEODETIC_LONGITUDE, getPredefined("\u03BB", null));
        assertSame(ELLIPSOIDAL_HEIGHT, getPredefined("h",      null));
        assertSame(GEOCENTRIC_X,       getPredefined("X", AxisDirection.GEOCENTRIC_X));
        assertSame(GEOCENTRIC_Y,       getPredefined("Y", AxisDirection.GEOCENTRIC_Y));
        assertSame(GEOCENTRIC_Z,       getPredefined("Z", AxisDirection.GEOCENTRIC_Z));
        assertNull(getPredefined("X", AxisDirection.GEOCENTRIC_Y));
        assertNull(getPredefined("Y", AxisDirection.GEOCENTRIC_Z));
        assertNull(getPredefined("Z", AxisDirection.GEOCENTRIC_X));

        // The following abbreviations are used by WKT parsing
        assertSame(GEOCENTRIC_X, getPredefined("X",   AxisDirection.OTHER));
        assertSame(GEOCENTRIC_Y, getPredefined("Y",   AxisDirection.EAST));
        assertSame(GEOCENTRIC_Z, getPredefined("Z",   AxisDirection.NORTH));
        assertSame(LONGITUDE,    getPredefined("Lon", AxisDirection.EAST));
        assertSame(LATITUDE,     getPredefined("Lat", AxisDirection.NORTH));
        assertSame(X,            getPredefined("X",   AxisDirection.EAST));
        assertSame(Y,            getPredefined("Y",   AxisDirection.NORTH));
        assertSame(Z,            getPredefined("Z",   AxisDirection.UP));

        // Tests from names
        assertSame(LATITUDE,           getPredefined("Latitude",           null));
        assertSame(LONGITUDE,          getPredefined("Longitude",          null));
        assertSame(GEODETIC_LATITUDE,  getPredefined("Geodetic latitude",  null));
        assertSame(GEODETIC_LONGITUDE, getPredefined("Geodetic longitude", null));
        assertSame(NORTHING,           getPredefined("Northing",           null));
        assertSame(NORTHING,           getPredefined("N",                  null));
        assertSame(EASTING,            getPredefined("Easting",            null));
        assertSame(EASTING,            getPredefined("E",                  null));
        assertSame(SOUTHING,           getPredefined("Southing",           null));
        assertSame(SOUTHING,           getPredefined("S",                  null));
        assertSame(WESTING,            getPredefined("Westing",            null));
        assertSame(WESTING,            getPredefined("W",                  null));
        assertSame(GEOCENTRIC_X,       getPredefined("X",                  null));
        assertSame(GEOCENTRIC_Y,       getPredefined("Y",                  null));
        assertSame(GEOCENTRIC_Z,       getPredefined("Z",                  null));
        assertSame(X,                  getPredefined("x",                  null));
        assertSame(Y,                  getPredefined("y",                  null));
        assertSame(Z,                  getPredefined("z",                  null));
    }

    /**
     * Tests the {@link DefaultCoordinateSystemAxis#getPredefined(CoordinateSystemAxis)} method.
     */
    @Test
    public void testPredefinedAxis() {
        // A few hard-coded tests for debugging convenience.
        assertSame(LATITUDE,          getPredefined(LATITUDE));
        assertSame(GEODETIC_LATITUDE, getPredefined(GEODETIC_LATITUDE));

        // Tests all constants.
        final DefaultCoordinateSystemAxis[] values = DefaultCoordinateSystemAxis.values();
        for (int i=0; i<values.length; i++) {
            final DefaultCoordinateSystemAxis axis = values[i];
            final String message = "values[" + i + ']';
            assertNotNull(message, axis);
            assertSame(message, axis, getPredefined(axis));
        }
    }

    /**
     * Makes sure that the compass directions in {@link AxisDirection} are okay.
     */
    @Test
    public void testCompass() {
        final AxisDirection[] compass = new AxisDirection[] {
            AxisDirection.NORTH,
            AxisDirection.NORTH_NORTH_EAST,
            AxisDirection.NORTH_EAST,
            AxisDirection.EAST_NORTH_EAST,
            AxisDirection.EAST,
            AxisDirection.EAST_SOUTH_EAST,
            AxisDirection.SOUTH_EAST,
            AxisDirection.SOUTH_SOUTH_EAST,
            AxisDirection.SOUTH,
            AxisDirection.SOUTH_SOUTH_WEST,
            AxisDirection.SOUTH_WEST,
            AxisDirection.WEST_SOUTH_WEST,
            AxisDirection.WEST,
            AxisDirection.WEST_NORTH_WEST,
            AxisDirection.NORTH_WEST,
            AxisDirection.NORTH_NORTH_WEST
        };
        assertEquals(compass.length, COMPASS_DIRECTION_COUNT);
        final int base = AxisDirection.NORTH.ordinal();
        final int h = compass.length / 2;
        for (int i=0; i<compass.length; i++) {
            final String index = "compass[" + i +']';
            final AxisDirection c = compass[i];
            double angle = i * (360.0/compass.length);
            if (angle > 180) {
                angle -= 360;
            }
            assertEquals(index, base + i, c.ordinal());
            assertEquals(index, base + i + (i<h ? h : -h), AxisDirections.opposite(c).ordinal());
            assertEquals(index, 0, getAngle(c, c), EPS);
            assertEquals(index, 180, abs(getAngle(c, AxisDirections.opposite(c))), EPS);
            assertEquals(index, angle, getAngle(c, AxisDirection.NORTH), EPS);
        }
    }

    /**
     * Tests {@link DefaultCoordinateSystemAxis#getAngle}.
     */
    @Test
    public void testAngle() {
        assertEquals( 90.0, getAngle(AxisDirection.WEST,             AxisDirection.SOUTH),      EPS);
        assertEquals(-90.0, getAngle(AxisDirection.SOUTH,            AxisDirection.WEST),       EPS);
        assertEquals( 45.0, getAngle(AxisDirection.SOUTH,            AxisDirection.SOUTH_EAST), EPS);
        assertEquals(-22.5, getAngle(AxisDirection.NORTH_NORTH_WEST, AxisDirection.NORTH),      EPS);
    }

    /**
     * Tests {@link DefaultCoordinateSystemAxis#getAngle} using textual directions.
     */
    @Test
    public void testAngle2() {
        compareAngle( 90.0, "West", "South");
        compareAngle(-90.0, "South", "West");
        compareAngle( 45.0, "South", "South-East");
        compareAngle(-22.5, "North-North-West", "North");
        compareAngle(-22.5, "North_North_West", "North");
        compareAngle(-22.5, "North North West", "North");
        compareAngle( 90.0, "North along 90 deg East", "North along 0 deg");
        compareAngle( 90.0, "South along 180 deg", "South along 90 deg West");
    }

    /**
     * Compare the angle between the specified directions.
     */
    private static void compareAngle(final double expected, final String source, final String target) {
        final AxisDirection dir1 = getDirection(source);
        final AxisDirection dir2 = getDirection(target);
        assertNotNull(dir1);
        assertNotNull(dir2);
        assertEquals(expected, getAngle(dir1, dir2), EPS);
    }
}
