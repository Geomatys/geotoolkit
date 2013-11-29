/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2012, Open Source Geospatial Foundation (OSGeo)
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

import javax.measure.unit.SI;

import org.opengis.referencing.cs.AxisDirection;
import org.opengis.referencing.cs.CoordinateSystem;

import org.junit.*;
import org.geotoolkit.test.referencing.ReferencingTestBase;

import static org.geotoolkit.referencing.Assert.*;


/**
 * Tests the {@link DefaultCartesianCS} class.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.20
 *
 * @since 2.2
 */
public final strictfp class CartesianTest extends ReferencingTestBase {
    /**
     * Tests {@link AbstractCS#standard} with Cartesian CS, especially
     * the ones that leads to the creation of right-handed CS.
     */
    @Test
    public void testStandard() {
        // ----------- Axis to test ------ Expected axis --
        assertOrdered("East", "North",    "East", "North");
        assertOrdered("North", "East",    "East", "North");
        assertOrdered("South", "East",    "East", "North");
        assertOrdered("South", "West",    "East", "North");

        assertOrdered("East",                       "North");
        assertOrdered("South-East",                 "North-East");
        assertOrdered("North along  90 deg East",   "North along   0 deg");
        assertOrdered("North along  90 deg East",   "North along   0 deg");
        assertOrdered("North along  75 deg West",   "North along 165 deg West");
        assertOrdered("South along  90 deg West",   "South along   0 deg");
        assertOrdered("South along 180 deg",        "South along  90 deg West");
        assertOrdered("North along 130 deg West",   "North along 140 deg East");
    }

    /**
     * Creates an axis for testing purpose for the specified direction.
     */
    private static DefaultCoordinateSystemAxis create(final AxisDirection direction) {
        if (direction.equals(AxisDirection.NORTH)) {
            return DefaultCoordinateSystemAxis.NORTHING;
        }
        if (direction.equals(AxisDirection.EAST)) {
            return DefaultCoordinateSystemAxis.EASTING;
        }
        if (direction.equals(AxisDirection.SOUTH)) {
            return DefaultCoordinateSystemAxis.SOUTHING;
        }
        if (direction.equals(AxisDirection.WEST)) {
            return DefaultCoordinateSystemAxis.WESTING;
        }
        return new DefaultCoordinateSystemAxis("Test", direction, SI.METRE);
    }

    /**
     * Creates a coordinate system with the specified axis directions.
     */
    private static DefaultCartesianCS create(final AxisDirection x, final AxisDirection y) {
        return new DefaultCartesianCS("Test", create(x), create(y));
    }

    /**
     * Creates a coordinate system with the specified axis directions.
     */
    private static DefaultCartesianCS create(final String x, final String y) {
        return create(DefaultCoordinateSystemAxis.getDirection(x),
                      DefaultCoordinateSystemAxis.getDirection(y));
    }

    /**
     * Tests ordering with a CS created from the specified axis.
     */
    private static void assertOrdered(final String expectedX, final String expectedY) {
        assertOrdered(expectedY, expectedX, expectedX, expectedY);
        assertOrdered(expectedX, expectedY, expectedX, expectedY);
    }

    /**
     * Creates a Cartesian CS using the provided test axis, invoke {@link AbstractCS#standard}
     * with it and compare with the expected axis.
     */
    private static void assertOrdered(final String testX,     final String testY,
                                      final String expectedX, final String expectedY)
    {
        final CoordinateSystem cs = AbstractCS.standard(create(testX, testY));
        assertEqualsIgnoreMetadata(create(expectedX, expectedY), cs, false);
    }
}
