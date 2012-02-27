/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Open Source Geospatial Foundation (OSGeo)
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

import org.junit.*;
import org.geotoolkit.test.referencing.ReferencingTestBase;

import static org.junit.Assert.*;
import static org.opengis.referencing.cs.AxisDirection.*;


/**
 * Tests the {@link Directions} class.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 */
public final strictfp class DirectionsTest extends ReferencingTestBase {
    /**
     * Tests the standards directions.
     */
    @Test
    public void testCompass() {
        assertSame(NORTH, Directions.find("NORTH"));
        assertSame(SOUTH, Directions.find("South"));
        assertSame(EAST,  Directions.find("east"));
        assertSame(WEST,  Directions.find(" west "));
        assertSame(WEST,  Directions.find("W"));
        assertSame(NORTH, Directions.find(" N "));
        assertSame(SOUTH, Directions.find("s"));
    }

    /**
     * Tests mixin of the 4 compass directions.
     */
    @Test
    public void testMixin() {
        assertSame(NORTH_EAST,       Directions.find("North-East"));
        assertSame(SOUTH_SOUTH_WEST, Directions.find("South South West"));
        assertSame(NORTH_EAST,       Directions.find("NE"));
        assertSame(SOUTH_SOUTH_WEST, Directions.find("SSW"));
    }

    /**
     * Tests the geocentric X direction.
     */
    @Test
    public void testGeocentricX() {
        assertSame(GEOCENTRIC_X, Directions.find("Geocentre > equator/PM"));
        assertSame(GEOCENTRIC_X, Directions.find("Geocentre>equator / PM"));
        assertSame(GEOCENTRIC_X, Directions.find("Geocentre > equator/0°E"));
    }

    /**
     * Tests the geocentric Y direction.
     */
    @Test
    public void testGeocentricY() {
        assertSame(GEOCENTRIC_Y, Directions.find("Geocentre > equator/90°E"));
        assertSame(GEOCENTRIC_Y, Directions.find("Geocentre > equator/90dE"));
        assertSame(GEOCENTRIC_Y, Directions.find("Geocentre>equator / 90dE"));
        assertSame(GEOCENTRIC_Y, Directions.find("GEOCENTRE > EQUATOR/90dE"));
    }

    /**
     * Tests the geocentric Z direction.
     */
    @Test
    public void testGeocentricZ() {
        assertSame(GEOCENTRIC_Z, Directions.find("Geocentre > north pole"));
        assertSame(GEOCENTRIC_Z, Directions.find("Geocentre>north pole "));
    }
}
