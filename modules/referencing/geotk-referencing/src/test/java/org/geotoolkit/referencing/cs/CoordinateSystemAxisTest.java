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

import org.opengis.util.InternationalString;
import org.opengis.referencing.cs.AxisDirection;
import org.junit.*;
import org.geotoolkit.test.referencing.ReferencingTestBase;

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
public final strictfp class CoordinateSystemAxisTest extends ReferencingTestBase {
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
}
