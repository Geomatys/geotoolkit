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
package org.geotoolkit.parameter;

import java.util.Set;
import java.util.Arrays;
import java.util.HashSet;
import javax.measure.unit.Unit;
import static javax.measure.unit.SI.*;
import static javax.measure.unit.NonSI.DEGREE_ANGLE;

import org.opengis.parameter.*;
import org.opengis.referencing.cs.AxisDirection;

import org.apache.sis.test.DependsOn;
import org.geotoolkit.internal.referencing.VerticalDatumTypes;
import org.geotoolkit.test.referencing.ParameterTestBase;

import org.junit.*;
import static org.apache.sis.test.Assert.*;
import static org.opengis.test.Validators.*;
import static java.lang.StrictMath.*;


/**
 * Tests the {@link Parameter} class.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.1
 */
@DependsOn(ParameterDescriptorTest.class)
public final strictfp class ParameterTest extends ParameterTestBase {
    /**
     * Strict tolerance factor for floating point comparisons. In the particular
     * case of this test suite, we can afford to be strict since we will perform
     * arithmetic only on integer values.
     */
    private static final double STRICT = 0.0;

    /**
     * Small tolerance factor for floating point comparisons resulting from
     * some calculation.
     */
    private static final double EPS = 1E-10;

    /**
     * Tests {@link Parameter} for a unitless integer value.
     */
    @Test
    public void testIntegerType() {
        final Parameter<Integer> parameter  = Parameters.create("Test", 14);
        final ParameterDescriptor<Integer> descriptor = parameter.getDescriptor();
        validate(parameter);

        assertNull  ("unit",                             parameter.getUnit());
        assertEquals("intValue",    14,                  parameter.intValue());
        assertEquals("doubleValue", 14,                  parameter.doubleValue(), STRICT);
        assertEquals("getValue",    Integer.valueOf(14), parameter.getValue());
        assertEquals("type",        Integer.class,       descriptor.getValueClass());
        assertEquals("name",        "Test",              descriptor.getName().getCode());
        assertNull  ("defaultValue",                     descriptor.getDefaultValue());
        assertNull  ("minimum",                          descriptor.getMinimumValue());
        assertNull  ("maximum",                          descriptor.getMaximumValue());
        assertNull  ("unit",                             descriptor.getUnit());
        assertNull  ("validValues",                      descriptor.getValidValues());
        try {
            parameter.doubleValue(METRE);
            fail("doubleValue(METRE)");
        } catch (IllegalStateException exception) {
            // This is the expected exception.
        }
        try {
            parameter.stringValue();
            fail("stringValue()");
        } catch (InvalidParameterTypeException exception) {
            // This is the expected exception.
            assertEquals("Test", exception.getParameterName());
        }
        assertNotSame(parameter, assertSerializedEquals(parameter));
    }

    /**
     * Creates a parameter bounded by some range of integer numbers, and tests values
     * inside and outside that range. Tests also the usage of values of the wrong type.
     */
    @Test
    public void testIntegerRange() {
        final Parameter<Integer> parameter =
                new Parameter<>(DefaultParameterDescriptor.create("Range", 15, -30, +40));
        assertEquals(Integer.class, parameter.getDescriptor().getValueClass());
        assertEquals(   "intValue", 15, parameter.intValue());
        assertEquals("doubleValue", 15, parameter.doubleValue(), STRICT);
        assertEquals(   "getValue", Integer.valueOf(15), parameter.getValue());
        validate(parameter);

        parameter.setValue(12);
        assertEquals(   "intValue", 12, parameter.intValue());
        assertEquals("doubleValue", 12, parameter.doubleValue(), STRICT);
        assertEquals(   "getValue", Integer.valueOf(12), parameter.getValue());
        validate(parameter);

        try {
            parameter.setValue(50);
            fail("setValue(> max)");
        } catch (InvalidParameterValueException exception) {
            // This is the expected exception.
            assertEquals("Range", exception.getParameterName());
        }
        try {
            parameter.setValue(-40);
            fail("setValue(< min)");
        } catch (InvalidParameterValueException exception) {
            // This is the expected exception.
            assertEquals("Range", exception.getParameterName());
        }
        try {
            parameter.setValue(10.0);
            fail("setValue(double)");
        } catch (InvalidParameterValueException exception) {
            // This is the expected exception.
            assertEquals("Range", exception.getParameterName());
        }
        assertEquals("Clone not equals: ", parameter, parameter.clone());
    }

    /**
     * Tests {@link Parameter} for double value with a unit of measurement.
     */
    @Test
    public void testDoubleType() {
        final Parameter<Double> parameter = Parameters.create("Test", 3, METRE);
        final ParameterDescriptor<Double> descriptor = parameter.getDescriptor();
        validate(parameter);

        assertEquals("intValue",      3,               parameter.intValue());
        assertEquals("doubleValue",   3,               parameter.doubleValue(), STRICT);
        assertEquals("doubleValue", 300,               parameter.doubleValue(CENTIMETRE), STRICT);
        assertEquals("getValue",    Double.valueOf(3), parameter.getValue());
        assertEquals("name",        "Test",            descriptor.getName().getCode());
        assertEquals("unit",        METRE,             descriptor.getUnit());
        assertNull  ("defaultValue",                   descriptor.getDefaultValue());
        assertNull  ("minimum",                        descriptor.getMinimumValue());
        assertNull  ("maximum",                        descriptor.getMaximumValue());
        assertNull  ("validValues",                    descriptor.getValidValues());
        try {
            parameter.stringValue();
            fail("stringValue()");
        } catch (InvalidParameterTypeException exception) {
            // This is the expected exception.
            assertEquals("Test", exception.getParameterName());
        }
        assertNotSame(parameter, assertSerializedEquals(parameter));
    }

    /**
     * Creates a parameter bounded by some range of floating point numbers, and tests values
     * inside and outside that range. Tests also the usage of values of the wrong type.
     */
    @Test
    public void testDoubleRange() {
        final Parameter<Double> parameter =
                new Parameter<>(DefaultParameterDescriptor.create("Range", 15.0, -30.0, +40.0, null));
        assertEquals(Double.class, parameter.getDescriptor().getValueClass());
        assertEquals(   "intValue", 15, parameter.intValue());
        assertEquals("doubleValue", 15, parameter.doubleValue(), STRICT);
        assertEquals(   "getValue", Double.valueOf(15), parameter.getValue());
        validate(parameter);

        parameter.setValue(12.0);
        assertEquals(   "intValue", 12, parameter.intValue());
        assertEquals("doubleValue", 12, parameter.doubleValue(), STRICT);
        assertEquals(   "getValue", Double.valueOf(12), parameter.getValue());
        validate(parameter);

        try {
            parameter.setValue(50.0);
            fail("setValue(> max)");
        } catch (InvalidParameterValueException exception) {
            // This is the expected exception.
            assertEquals("Range", exception.getParameterName());
        }
        try {
            parameter.setValue(-40.0);
            fail("setValue(< min)");
        } catch (InvalidParameterValueException exception) {
            // This is the expected exception.
            assertEquals("Range", exception.getParameterName());
        }
        try {
            parameter.setValue("12");
            fail("setValue(String)");
        } catch (InvalidParameterValueException exception) {
            // This is the expected exception.
            assertEquals("Range", exception.getParameterName());
        }
        assertEquals("equals(clone)", parameter, parameter.clone());
    }

    /**
     * Tests {@link Parameter} for a code list.
     */
    @Test
    public void testCodeList() {
        final Parameter<AxisDirection> parameter = Parameters.create("Test", AxisDirection.class, AxisDirection.NORTH);
        final ParameterDescriptor<AxisDirection> descriptor = parameter.getDescriptor();
        final Set<AxisDirection> validValues = descriptor.getValidValues();
        validate(parameter);

        assertEquals("value", AxisDirection.NORTH, parameter.getValue());
        assertEquals("name", "Test", descriptor.getName().getCode());
        assertNull  ("unit",         descriptor.getUnit());
        assertNull  ("defaultValue", descriptor.getDefaultValue());
        assertNull  ("minimum",      descriptor.getMinimumValue());
        assertNull  ("maximum",      descriptor.getMaximumValue());
        assertTrue  ("validValues",  validValues.contains(AxisDirection.NORTH));
        assertTrue  ("validValues",  validValues.contains(AxisDirection.SOUTH));
        assertTrue  ("validValues",  validValues.contains(AxisDirection.DISPLAY_LEFT));
        assertTrue  ("validValues",  validValues.contains(AxisDirection.PAST));
        assertEquals("validValues",  new HashSet<>(Arrays.asList(AxisDirection.values())), validValues);
        try {
            parameter.doubleValue();
            fail("doubleValue should not be allowed on AxisDirection");
        } catch (InvalidParameterTypeException exception) {
            // This is the expected exception.
            assertEquals("Test", exception.getParameterName());
        }
        assertNotSame(parameter, assertSerializedEquals(parameter));
    }

    /**
     * Tests parameter for a code list. Tries to inserts invalid values. Tries also to insert
     * a new code list. This operation should fail if the new code list is created after the
     * parameter.
     */
    @Test
    public void testCodeListAddition() {
        Parameter<AxisDirection> p = Parameters.create("Test", AxisDirection.class, AxisDirection.DISPLAY_DOWN);
        ParameterDescriptor<AxisDirection> d = p.getDescriptor();
        Set<AxisDirection> validValues = d.getValidValues();
        validate(p);

        assertNull ("default value", d.getDefaultValue());
        assertEquals("Valid values", new HashSet<>(Arrays.asList(AxisDirection.values())), validValues);
        assertEquals("Actual value", AxisDirection.DISPLAY_DOWN, p.getValue());
        p.setValue(AxisDirection.DOWN);
        try {
            p.setValue(VerticalDatumTypes.ELLIPSOIDAL);
            fail("setValue(VerticalDatumType)");
        } catch (InvalidParameterValueException exception) {
            // This is the expected exception.
            assertEquals("Test", exception.getParameterName());
        }
        AxisDirection dummy = AxisDirection.valueOf("Dummy");
        try {
            p.setValue(dummy);
            fail("setValue(AxisDirection)");
        } catch (InvalidParameterValueException exception) {
            // This is the expected exception.
            assertEquals("Test", exception.getParameterName());
        }
        /*
         * Recreates the parameter. Because the dummy axis direction has been created in the
         * above step, it should be accepted now.
         */
        p = Parameters.create("Test", AxisDirection.class, AxisDirection.COLUMN_POSITIVE);
        validate(p);

        assertEquals("Actual value", AxisDirection.COLUMN_POSITIVE, p.getValue());
        p.setValue(dummy); // Should not fails.
        assertEquals("Actual value", dummy, p.getValue());
        assertEquals("equals(clone)", p, p.clone());
    }

    /**
     * Tests integer and floating point values in a wide range of values. Some on those
     * values are cached (e.g. 0, 90, 360) because frequently used. It should be transparent
     * to the user. Test also unit conversions (degrees to radians in this case).
     */
    @Test
    public void testSequence() {
        Parameter<? extends Number> p;
        ParameterDescriptor<? extends Number> d;
        for (int i=-500; i<=500; i++) {
            p = Parameters.create("Unitlesss integer value", i);
            d = p.getDescriptor();
            validate(p);

            assertNotNull("Expected a descriptor.",       d);
            assertNull   ("Expected no default value.",   d.getDefaultValue());
            assertNull   ("Expected no minimal value.",   d.getMinimumValue());
            assertNull   ("Expected no maximal value.",   d.getMaximumValue());
            assertNull   ("Expected no enumeration.",     d.getValidValues());
            assertEquals ("Expected integer type.",       Integer.class, d.getValueClass());
            assertTrue   ("Expected integer type.",       p.getValue() instanceof Integer);
            assertNull   ("Expected unitless parameter.", p.getUnit());
            assertEquals ("Expected integer value", i,    p.intValue());
            assertEquals ("Expected integer value", i,    p.doubleValue(), STRICT);

            p = Parameters.create("Unitlesss double value", i, null);
            d = p.getDescriptor();
            validate(p);

            assertNotNull("Expected a descriptor.",       d);
            assertNull   ("Expected no default value.",   d.getDefaultValue());
            assertNull   ("Expected no minimal value.",   d.getMinimumValue());
            assertNull   ("Expected no maximal value.",   d.getMaximumValue());
            assertNull   ("Expected no enumeration.",     d.getValidValues());
            assertEquals ("Expected double type.",        Double.class, d.getValueClass());
            assertTrue   ("Expected double type.",        p.getValue() instanceof Double);
            assertNull   ("Expected unitless parameter.", p.getUnit());
            assertEquals ("Expected integer value", i,    p.intValue());
            assertEquals ("Expected integer value", i,    p.doubleValue(), STRICT);

            p = Parameters.create("Dimensionless double value", i, Unit.ONE);
            d = p.getDescriptor();
            validate(p);

            assertNotNull("Expected a descriptor.",       d);
            assertNull   ("Expected no default value.",   d.getDefaultValue());
            assertNull   ("Expected no minimal value.",   d.getMinimumValue());
            assertNull   ("Expected no maximal value.",   d.getMaximumValue());
            assertNull   ("Expected no enumeration.",     d.getValidValues());
            assertEquals ("Expected double type.",        Double.class, d.getValueClass());
            assertTrue   ("Expected double type.",        p.getValue() instanceof Double);
            assertEquals ("Expected dimensionless.",      Unit.ONE, p.getUnit());
            assertEquals ("Expected integer value", i,    p.intValue());
            assertEquals ("Expected integer value", i,    p.doubleValue(), STRICT);

            p = Parameters.create("Angular double value", i, DEGREE_ANGLE);
            d = p.getDescriptor();
            validate(p);

            assertNotNull("Expected a descriptor.",       d);
            assertNull   ("Expected no default value.",   d.getDefaultValue());
            assertNull   ("Expected no minimal value.",   d.getMinimumValue());
            assertNull   ("Expected no maximal value.",   d.getMaximumValue());
            assertNull   ("Expected no enumeration.",     d.getValidValues());
            assertEquals ("Expected double type.",        Double.class, d.getValueClass());
            assertTrue   ("Expected double type.",        p.getValue() instanceof Double);
            assertEquals ("Expected angular unit.",       DEGREE_ANGLE, p.getUnit());
            assertEquals ("Expected integer value", i,    p.intValue());
            assertEquals ("Expected integer value", i,    p.doubleValue(), STRICT);
            assertEquals ("Expected unit conversion.", toRadians(i), p.doubleValue(RADIAN), EPS);
        }
    }
}
