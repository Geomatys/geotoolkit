/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2011, Geomatys
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

import org.opengis.parameter.*;
import org.opengis.metadata.quality.ConformanceResult;
import org.junit.*;

import static org.junit.Assert.*;


/**
 * Tests the static method in the {@link Parameters} utility class.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.05
 *
 * @since 3.05
 */
public final class ParameteresTest {
    /**
     * Tests the {@link Parameters#cast(ParameterDescriptor, Class)} methods.
     */
    @Test
    public void testCast() {
        final ParameterDescriptor<Integer> descriptor = DefaultParameterDescriptor.create("Test", 10, 5, 15);
        assertSame(descriptor, Parameters.cast(descriptor, Integer.class));
        try {
            assertSame(descriptor, Parameters.cast(descriptor, Double.class));
            fail("Expected a ClassCastException.");
        } catch (ClassCastException e) {
            assertTrue("The error message should contains the name of the parameter.",
                    e.getLocalizedMessage().contains("Test"));
        }
        /*
         * Tests the cast of values.
         */
        final ParameterValue<Integer> value = descriptor.createValue();
        assertEquals("Expected a parameter initialized to the default value.", 10, value.intValue());
        assertSame(value, Parameters.cast(value, Integer.class));
        try {
            assertSame(value, Parameters.cast(value, Double.class));
            fail("Expected a ClassCastException.");
        } catch (ClassCastException e) {
            assertTrue("The error message should contains the name of the invalid parameter.",
                    e.getLocalizedMessage().contains("Test"));
        }
    }

    /**
     * Tests the {@link Parameters#isValid(GeneralParameterValue, GeneralParameterDescriptor)}
     * method.
     */
    @Test
    public void testIsValid() {
        final ParameterDescriptorGroup group = new DefaultParameterDescriptorGroup("Group",
                // A mandatory parameter with values in the [5 ... 15] range.
                DefaultParameterDescriptor.create("Test1", 10, 5, 15),
                // A mandatory parameter with values in the {1, 4, 8} set.
                new DefaultParameterDescriptor<Integer>("Test2", Integer.class, new Integer[] {1, 4, 8}, 4));
        /*
         * Create a parameter group more flexible than the one below.
         * This is needed in order to allow us to create invalide parameters.
         */
        ParameterDescriptorGroup lenient = new DefaultParameterDescriptorGroup("Lenient",
                DefaultParameterDescriptor.create("Test1", 10, 0, 100),
                DefaultParameterDescriptor.create("Test2",  8, 0, 100));
        /*
         * Now test the values, starting with the default values (which are expected to pass).
         */
        ParameterValueGroup values = lenient.createValue();
        ConformanceResult result = Parameters.isValid(values, group);
        assertTrue("Default values should pass the test.", result.pass());
        /*
         * Set an invalid parameter value and test again.
         */
        values.parameter("Test2").setValue(0);
        result = Parameters.isValid(values, group);
        assertFalse(result.pass());
        assertTrue("The error message should contains the name of the invalid parameter.",
                result.getExplanation().toString().contains("Test2"));
        /*
         * Reset the invalid parameter value to its default value.
         */
        values.parameter("Test2").setValue(null);
        result = Parameters.isValid(values, group);
        assertTrue(result.pass());
        /*
         * Create a group with a missing parameter.
         */
        lenient = new DefaultParameterDescriptorGroup("Lenient",
                DefaultParameterDescriptor.create("Test2",  8, 0, 100));
        values = lenient.createValue();
        result = Parameters.isValid(values, group);
        assertFalse(result.pass());
        assertTrue("The error message should contains the name of the missing parameter.",
                result.getExplanation().toString().contains("Test1"));
        /*
         * Create a group with an extra parameter.
         */
        lenient = new DefaultParameterDescriptorGroup("Lenient",
                DefaultParameterDescriptor.create("Test1", 10, 0, 100),
                DefaultParameterDescriptor.create("Test2",  8, 0, 100),
                DefaultParameterDescriptor.create("Test3",  0, 0, 100));
        values = lenient.createValue();
        result = Parameters.isValid(values, group);
        assertFalse(result.pass());
        assertTrue("The error message should contains the name of the extra parameter.",
                result.getExplanation().toString().contains("Test3"));
    }
}
