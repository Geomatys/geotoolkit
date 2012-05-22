/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.measure;

import javax.measure.unit.Unit;
import javax.measure.quantity.Quantity;
import javax.measure.converter.UnitConverter;
import static javax.measure.unit.Unit.ONE;
import static javax.measure.unit.SI.CELSIUS;
import static javax.measure.unit.SI.METRE;
import static javax.measure.unit.SI.KILOMETRE;
import static javax.measure.unit.SI.RADIAN;
import static javax.measure.unit.NonSI.CENTIRADIAN;
import static javax.measure.unit.NonSI.DEGREE_ANGLE;
import static javax.measure.unit.NonSI.MINUTE_ANGLE;
import static javax.measure.unit.NonSI.SECOND_ANGLE;
import static javax.measure.unit.NonSI.GRADE;
import static javax.measure.unit.NonSI.DAY;
import static javax.measure.unit.NonSI.SPHERE;
import static javax.measure.unit.NonSI.ATMOSPHERE;
import static javax.measure.unit.NonSI.NAUTICAL_MILE;

import org.junit.*;
import static org.geotoolkit.measure.Units.*;
import static org.geotoolkit.test.Assert.*;


/**
 * Test conversions using the units declared in {@link Units}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 2.5
 */
public final strictfp class UnitsTest {
    /**
     * Compares two values for equality.
     */
    private static <Q extends Quantity> void checkConversion(
            final double expected, final Unit<Q> unitExpected,
            final double actual,   final Unit<Q> unitActual)
    {
        UnitConverter converter = unitActual.getConverterTo(unitExpected);
        assertEquals(expected, converter.convert(actual), 1E-6);
        converter = converter.inverse();
        assertEquals(actual, converter.convert(expected), 1E-6);
    }

    /**
     * Checks the conversions using {@link Units#SEXAGESIMAL_DMS}.
     */
    @Test
    public void testSexagesimal() {
        checkConversion(10.00, DEGREE_ANGLE, 10.0000, SEXAGESIMAL_DMS);
        checkConversion(10.01, DEGREE_ANGLE, 10.0036, SEXAGESIMAL_DMS);
        checkConversion(10.50, DEGREE_ANGLE, 10.3000, SEXAGESIMAL_DMS);
        checkConversion(10.99, DEGREE_ANGLE, 10.5924, SEXAGESIMAL_DMS);
    }

    /**
     * Tests serialization of units.
     */
    @Test
    public void testSerialization() {
        assertEquals(DEGREE_ANGLE,         assertSerializable(DEGREE_ANGLE));
        assertEquals(SEXAGESIMAL_DMS,      assertSerializable(SEXAGESIMAL_DMS));
        assertEquals(DEGREE_MINUTE_SECOND, assertSerializable(DEGREE_MINUTE_SECOND));
        assertEquals(PPM,                  assertSerializable(PPM));
    }

    /**
     * Tests {@link Units#isTemporal}.
     */
    @Test
    public void testIsTemporal() {
        // Standard units
        assertFalse(isTemporal(null));
        assertFalse(isTemporal(ONE));
        assertFalse(isTemporal(METRE));
        assertFalse(isTemporal(RADIAN));
        assertFalse(isTemporal(CENTIRADIAN));
        assertFalse(isTemporal(DEGREE_ANGLE));
        assertFalse(isTemporal(MINUTE_ANGLE));
        assertFalse(isTemporal(SECOND_ANGLE));
        assertFalse(isTemporal(GRADE));
        assertTrue (isTemporal(DAY));
        assertFalse(isTemporal(SPHERE));
        assertFalse(isTemporal(ATMOSPHERE));
        assertFalse(isTemporal(NAUTICAL_MILE));

        // Additional units
        assertFalse(isTemporal(PPM));
        assertTrue (isTemporal(MILLISECOND));
        assertFalse(isTemporal(SEXAGESIMAL_DMS));
        assertFalse(isTemporal(DEGREE_MINUTE_SECOND));
    }

    /**
     * Tests {@link Units#isLinear}.
     */
    @Test
    public void testIsLinear() {
        // Standard units
        assertFalse(isLinear(null));
        assertFalse(isLinear(ONE));
        assertTrue (isLinear(METRE));
        assertFalse(isLinear(RADIAN));
        assertFalse(isLinear(CENTIRADIAN));
        assertFalse(isLinear(DEGREE_ANGLE));
        assertFalse(isLinear(MINUTE_ANGLE));
        assertFalse(isLinear(SECOND_ANGLE));
        assertFalse(isLinear(GRADE));
        assertFalse(isLinear(DAY));
        assertFalse(isLinear(SPHERE));
        assertFalse(isLinear(ATMOSPHERE));
        assertTrue (isLinear(NAUTICAL_MILE));

        // Additional units
        assertFalse(isLinear(PPM));
        assertFalse(isLinear(MILLISECOND));
        assertFalse(isLinear(SEXAGESIMAL_DMS));
        assertFalse(isLinear(DEGREE_MINUTE_SECOND));
    }

    /**
     * Tests {@link Units#isAngular}.
     */
    @Test
    public void testIsAngular() {
        // Standard units
        assertFalse(isAngular(null));
        assertFalse(isAngular(ONE));
        assertFalse(isAngular(METRE));
        assertTrue (isAngular(RADIAN));
        assertTrue (isAngular(CENTIRADIAN));
        assertTrue (isAngular(DEGREE_ANGLE));
        assertTrue (isAngular(MINUTE_ANGLE));
        assertTrue (isAngular(SECOND_ANGLE));
        assertTrue (isAngular(GRADE));
        assertFalse(isAngular(DAY));
        assertFalse(isAngular(SPHERE));
        assertFalse(isAngular(ATMOSPHERE));
        assertFalse(isAngular(NAUTICAL_MILE));

        // Additional units
        assertFalse(isAngular(PPM));
        assertFalse(isAngular(MILLISECOND));
        assertTrue (isAngular(SEXAGESIMAL_DMS));
        assertTrue (isAngular(DEGREE_MINUTE_SECOND));
    }

    /**
     * Tests {@link Units#isScale}.
     */
    @Test
    public void testIsScale() {
        // Standard units
        assertFalse(isScale(null));
        assertTrue (isScale(ONE));
        assertFalse(isScale(METRE));
        assertFalse(isScale(RADIAN));
        assertFalse(isScale(CENTIRADIAN));
        assertFalse(isScale(DEGREE_ANGLE));
        assertFalse(isScale(MINUTE_ANGLE));
        assertFalse(isScale(SECOND_ANGLE));
        assertFalse(isScale(GRADE));
        assertFalse(isScale(DAY));
        assertFalse(isScale(SPHERE));
        assertFalse(isScale(ATMOSPHERE));
        assertFalse(isScale(NAUTICAL_MILE));

        // Additional units
        assertTrue (isScale(PPM));
        assertFalse(isScale(MILLISECOND));
        assertFalse(isScale(SEXAGESIMAL_DMS));
        assertFalse(isScale(DEGREE_MINUTE_SECOND));
    }

    /**
     * Tests {@link Units#isPressure}.
     *
     * @since 3.20
     */
    @Test
    public void testIsPressure() {
        assertFalse(isPressure(null));
        assertFalse(isPressure(METRE));
        assertTrue (isPressure(ATMOSPHERE));
    }

    /**
     * Tests {@link Units#toStandardUnit}.
     */
    @Test
    public void testToStandardUnit() {
        assertEquals(1000.0,               Units.toStandardUnit(KILOMETRE),    1E-15);
        assertEquals(0.017453292519943295, Units.toStandardUnit(DEGREE_ANGLE), 1E-15);
    }

    /**
     * Tests {@link Units#multiply}.
     */
    @Test
    public void testMultiply() {
        assertSame(KILOMETRE,    Units.multiply(METRE,  1000));
        assertSame(DEGREE_ANGLE, Units.multiply(RADIAN, 0.017453292519943295));
    }

    /**
     * Tests {@link Units#valueOf}.
     */
    @Test
    public void testValueOf() {
        assertSame(DEGREE_ANGLE, Units.valueOf("°"));
        assertSame(DEGREE_ANGLE, Units.valueOf("deg"));
        assertSame(DEGREE_ANGLE, Units.valueOf("degree"));
        assertSame(DEGREE_ANGLE, Units.valueOf("degrees"));
        assertSame(DEGREE_ANGLE, Units.valueOf("degrées"));
        assertSame(DEGREE_ANGLE, Units.valueOf("DEGREES"));
        assertSame(DEGREE_ANGLE, Units.valueOf("DEGRÉES"));
        assertSame(DEGREE_ANGLE, Units.valueOf("degrees_east"));
        assertSame(DEGREE_ANGLE, Units.valueOf("degrées_north"));
        assertSame(DEGREE_ANGLE, Units.valueOf("decimal_degree"));
        assertSame(RADIAN,       Units.valueOf("rad"));
        assertSame(RADIAN,       Units.valueOf("radian"));
        assertSame(RADIAN,       Units.valueOf("radians"));
        assertSame(METRE,        Units.valueOf("m"));
        assertSame(METRE,        Units.valueOf("metre"));
        assertSame(METRE,        Units.valueOf("meter"));
        assertSame(METRE,        Units.valueOf("metres"));
        assertSame(METRE,        Units.valueOf("mètres"));
        assertSame(METRE,        Units.valueOf("meters"));
        assertSame(KILOMETRE,    Units.valueOf("km"));
        assertSame(KILOMETRE,    Units.valueOf("kilometre"));
        assertSame(KILOMETRE,    Units.valueOf("kilometer"));
        assertSame(KILOMETRE,    Units.valueOf("kilometres"));
        assertSame(KILOMETRE,    Units.valueOf("kilomètres"));
        assertSame(KILOMETRE,    Units.valueOf("kilometers"));
        assertSame(CELSIUS,      Units.valueOf("Celsius"));
        assertSame(CELSIUS,      Units.valueOf("degree Celsius"));
        assertSame(CELSIUS,      Units.valueOf("degree_Celcius"));
    }

    /**
     * Tests {@link Units#valueOfEPSG}.
     *
     * @since 3.20
     */
    @Test
    public void testValueOfEPSG() {
        assertSame(METRE,        Units.valueOfEPSG(9001));
        assertSame(DEGREE_ANGLE, Units.valueOfEPSG(9102));
        assertSame(METRE,        Units.valueOf("EPSG:9001"));
        assertSame(DEGREE_ANGLE, Units.valueOf(" epsg : 9102"));
    }
}
