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

import javax.measure.converter.ConversionException;
import static javax.measure.unit.SI.*;

import org.opengis.test.Validators;
import org.opengis.referencing.operation.Matrix;
import org.opengis.referencing.cs.AxisDirection;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.cs.CoordinateSystemAxis;

import org.geotoolkit.test.Depend;
import org.geotoolkit.referencing.IdentifiedObjectTest;
import org.geotoolkit.referencing.operation.matrix.GeneralMatrix;

import static org.geotoolkit.test.Assert.*;
import static org.geotoolkit.referencing.cs.DefaultTimeCS.*;
import static org.geotoolkit.referencing.cs.DefaultVerticalCS.*;
import static org.geotoolkit.referencing.cs.DefaultCartesianCS.*;
import static org.geotoolkit.referencing.cs.DefaultEllipsoidalCS.*;

import org.junit.*;
import org.geotoolkit.test.referencing.ReferencingTestBase;


/**
 * Tests {@link AbstractCS} and subclasses.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.2
 */
@Depend({IdentifiedObjectTest.class, CoordinateSystemAxisTest.class, ComparableAxisWrapperTest.class})
public final strictfp class CoordinateSystemTest extends ReferencingTestBase {
    /**
     * Validates constants.
     */
    @Test
    public void validate() {
        Validators.validate(PROJECTED);
        Validators.validate(GEOCENTRIC);
        Validators.validate(GENERIC_2D);
        Validators.validate(GENERIC_3D);
        Validators.validate(GRID);
        Validators.validate(DISPLAY);
        Validators.validate(GEODETIC_2D);
        Validators.validate(GEODETIC_3D);
        Validators.validate(ELLIPSOIDAL_HEIGHT);
        Validators.validate(GRAVITY_RELATED_HEIGHT);
        Validators.validate(DEPTH);
        Validators.validate(DAYS);
        Validators.validate(SECONDS);
        Validators.validate(MILLISECONDS);
        Validators.validate(DefaultSphericalCS.GEOCENTRIC);
    }

    /**
     * Tests the dimensions of some predefined constants.
     */
    @Test
    public void testDimensions() {
        assertEquals("Cartesian 2D",   2, PROJECTED  .getDimension());
        assertEquals("Cartesian 3D",   3, GEOCENTRIC .getDimension());
        assertEquals("Ellipsoidal 2D", 2, GEODETIC_2D.getDimension());
        assertEquals("Ellipsoidal 3D", 3, GEODETIC_3D.getDimension());
        assertEquals("Vertical",       1, DEPTH      .getDimension());
        assertEquals("Temporal",       1, DAYS       .getDimension());
    }

    /**
     * Tests serialization of various objects.
     */
    @Test
    public void testSerialization() {
        assertSerializable(PROJECTED);
        assertSerializable(GEOCENTRIC);
        assertSerializable(GEODETIC_2D);
        assertSerializable(GEODETIC_3D);
    }

    /**
     * Tests the swapping of axis.
     *
     * @throws ConversionException Should not happen.
     */
    @Test
    public void testAxisSwapping() throws ConversionException {
        CoordinateSystem cs1, cs2;
        cs1 = new DefaultEllipsoidalCS("cs1",
                DefaultCoordinateSystemAxis.GEODETIC_LONGITUDE,
                DefaultCoordinateSystemAxis.GEODETIC_LATITUDE);
        cs2 = new DefaultEllipsoidalCS("cs2",
                DefaultCoordinateSystemAxis.GEODETIC_LATITUDE,
                DefaultCoordinateSystemAxis.GEODETIC_LONGITUDE);
        assertTrue(AbstractCS.swapAndScaleAxis(cs1, cs1).isIdentity());
        assertTrue(AbstractCS.swapAndScaleAxis(cs2, cs2).isIdentity());
        compareMatrix(cs1, cs2, new double[] {
            0, 1, 0,
            1, 0, 0,
            0, 0, 1
        });

        cs1 = new DefaultEllipsoidalCS("cs1",
                DefaultCoordinateSystemAxis.GEODETIC_LONGITUDE,
                DefaultCoordinateSystemAxis.GEODETIC_LATITUDE,
                DefaultCoordinateSystemAxis.ELLIPSOIDAL_HEIGHT);
        cs2 = new DefaultEllipsoidalCS("cs2",
                DefaultCoordinateSystemAxis.GEODETIC_LATITUDE,
                DefaultCoordinateSystemAxis.GEODETIC_LONGITUDE,
                DefaultCoordinateSystemAxis.ELLIPSOIDAL_HEIGHT);
        compareMatrix(cs1, cs2, new double[] {
            0, 1, 0, 0,
            1, 0, 0, 0,
            0, 0, 1, 0,
            0, 0, 0, 1
        });

        cs1 = new DefaultCartesianCS("cs1",
                DefaultCoordinateSystemAxis.ELLIPSOIDAL_HEIGHT,
                DefaultCoordinateSystemAxis.EASTING,
                DefaultCoordinateSystemAxis.NORTHING);
        cs2 = new DefaultCartesianCS("cs2",
                DefaultCoordinateSystemAxis.SOUTHING,
                DefaultCoordinateSystemAxis.EASTING,
                DefaultCoordinateSystemAxis.ELLIPSOIDAL_HEIGHT);
        compareMatrix(cs1, cs2, new double[] {
            0, 0,-1, 0,
            0, 1, 0, 0,
            1, 0, 0, 0,
            0, 0, 0, 1
        });
    }

    /**
     * Compares the matrix computes by {@link AbstractCS#swapAndScaleAxis} with the specified one.
     *
     * @throws ConversionException Should not happen.
     */
    private static void compareMatrix(final CoordinateSystem cs1, final CoordinateSystem cs2,
            final double[] expected) throws ConversionException
    {
        final Matrix matrix = AbstractCS.swapAndScaleAxis(cs1, cs2);
        final int numRow = matrix.getNumRow();
        final int numCol = matrix.getNumCol();
        assertEquals(expected.length, numRow*numCol);
        final Matrix em = new GeneralMatrix(numRow, numCol, expected);
        assertEquals(em, matrix);
    }

    /**
     * Tests {@link AbstractCS#axisUsingUnit(Unit)}.
     *
     * @throws ConversionException Should not happen.
     */
    @Test
    public void testAxisUsingUnit() throws ConversionException {
        assertNull("Should detect that no axis change is needed", PROJECTED.axisUsingUnit(METRE, null));
        final CoordinateSystemAxis[] axis = PROJECTED.axisUsingUnit(KILOMETRE, null);
        assertNotNull(axis);
        assertEquals("Expected two-dimensional", 2, axis.length);
        assertEquals(KILOMETRE,           axis[0].getUnit());
        assertEquals(KILOMETRE,           axis[1].getUnit());
        assertEquals(AxisDirection.EAST,  axis[0].getDirection());
        assertEquals(AxisDirection.NORTH, axis[1].getDirection());
        assertEquals("Easting",           axis[0].getName().getCode());
        assertEquals("Northing",          axis[1].getName().getCode());
    }

    /**
     * Tests {@link AbstractCS#standard}.
     */
    @Test
    public void testStandards() {
        CoordinateSystem cs;
        cs = GRID;               assertSame(cs, AbstractCS.standard(cs));
        cs = GEOCENTRIC;         assertSame(cs, AbstractCS.standard(cs));
        cs = GENERIC_2D;         assertSame(cs, AbstractCS.standard(cs));
        cs = GENERIC_3D;         assertSame(cs, AbstractCS.standard(cs));
        cs = PROJECTED;          assertSame(cs, AbstractCS.standard(cs));
        cs = GEODETIC_2D;        assertSame(cs, AbstractCS.standard(cs));
        cs = GEODETIC_3D;        assertSame(cs, AbstractCS.standard(cs));
        cs = DAYS;               assertSame(cs, AbstractCS.standard(cs));
        cs = ELLIPSOIDAL_HEIGHT; assertSame(cs, AbstractCS.standard(cs));
        cs = GRAVITY_RELATED_HEIGHT;
        assertSame("\"Standard\" vertical axis should be forced to ellipsoidal height.",
                   ELLIPSOIDAL_HEIGHT, AbstractCS.standard(cs));
        cs = DefaultSphericalCS.GEOCENTRIC; assertSame(cs, AbstractCS.standard(cs));
    }
}
