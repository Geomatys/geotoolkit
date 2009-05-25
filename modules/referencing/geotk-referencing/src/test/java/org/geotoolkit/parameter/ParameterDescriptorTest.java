/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2009, Open Source Geospatial Foundation (OSGeo)
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

import org.junit.*;
import org.opengis.parameter.*;
import javax.measure.unit.Unit;
import javax.measure.quantity.Length;

import static org.junit.Assert.*;
import static org.opengis.test.Validators.*;
import static javax.measure.unit.SI.*;


/**
 * Tests the {@link DefaultParameterDescriptor} class.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.1
 */
public final class ParameterDescriptorTest {
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
     * Tests {@link DefaultParameterDescriptor} construction for {@link Double} type.
     * Also tests the {@link Parameter} created from the {@code createValue} method.
     */
    @Test
    public void testDoubleType() {
        ParameterDescriptor<Double> descriptor;
        ParameterValue<Double>      parameter;

        descriptor = DefaultParameterDescriptor.create("Test", 12, 4, 20, METRE);
        assertEquals("name",         "Test",               descriptor.getName().getCode());
        assertEquals("unit",         METRE,                descriptor.getUnit());
        assertEquals("class",        Double.class,         descriptor.getValueClass());
        assertEquals("defaultValue", Double.valueOf(12.0), descriptor.getDefaultValue());
        assertEquals("minimum",      Double.valueOf( 4.0), descriptor.getMinimumValue());
        assertEquals("maximum",      Double.valueOf(20.0), descriptor.getMaximumValue());
        validate(descriptor);

        parameter  = descriptor.createValue();
        assertEquals("value",    Double.valueOf(12), parameter.getValue());
        assertEquals("intValue", 12,                 parameter.intValue());
        assertEquals("unit",     METRE,              parameter.getUnit());
        validate(parameter);

        final Unit<Length> CENTIMETRE = CENTI(METRE);
        for (int i=4; i<=20; i++) {
            parameter.setValue(i);
            assertEquals("value", Double.valueOf(i), parameter.getValue());
            assertEquals("unit",  METRE,             parameter.getUnit());
            assertEquals("value", i,                 parameter.doubleValue(METRE), STRICT);
            assertEquals("value", 100*i,             parameter.doubleValue(CENTIMETRE), STRICT);
        }
        try {
            parameter.setValue(3.0);
            fail("setValue(< min)");
        } catch (InvalidParameterValueException exception) {
            // This is the expected exception.
            assertEquals("Test", exception.getParameterName());
        }
        try {
            parameter.setValue("12");
            fail("setValue(Sring)");
        } catch (InvalidParameterValueException exception) {
            // This is the expected exception.
            assertEquals("Test", exception.getParameterName());
        }
        for (int i=400; i<=2000; i+=100) {
            parameter.setValue(i, CENTIMETRE);
            assertEquals("value", Double.valueOf(i), parameter.getValue());
            assertEquals("unit",  CENTIMETRE,        parameter.getUnit());
            assertEquals("value", i/100,             parameter.doubleValue(METRE), EPS);
        }
        try {
            assertNotNull(DefaultParameterDescriptor.create("Test", 3, 4, 20));
            fail("setValue(< min)");
        } catch (InvalidParameterValueException exception) {
            // This is the expected exception.
            assertEquals("Test", exception.getParameterName());
        }
        try {
            assertNotNull(DefaultParameterDescriptor.create("Test", 12, 20, 4));
            fail("ParameterDescriptor(min > max)");
        } catch (IllegalArgumentException exception) {
            // This is the expected exception.
        }
    }
}
