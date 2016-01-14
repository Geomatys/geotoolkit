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
package org.geotoolkit.parameter;

import java.util.Collections;
import java.util.Properties;
import org.opengis.parameter.*;
import org.opengis.metadata.quality.ConformanceResult;
import org.apache.sis.parameter.DefaultParameterDescriptor;
import org.apache.sis.parameter.DefaultParameterDescriptorGroup;
import javax.measure.unit.Unit;
import org.apache.sis.measure.MeasurementRange;
import org.apache.sis.measure.NumberRange;
import org.junit.*;

import static org.junit.Assert.*;
import static org.opengis.referencing.IdentifiedObject.NAME_KEY;


/**
 * Tests the static method in the {@link Parameters} utility class.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.18
 *
 * @since 3.05
 */
public final strictfp class ParametersTest {
    /**
     * Constructs a descriptor for a mandatory parameter in a range of integer values.
     *
     * @param  name         The parameter name.
     * @param  defaultValue The default value for the parameter.
     * @param  minimum      The minimum parameter value.
     * @param  maximum      The maximum parameter value.
     * @return The parameter descriptor for the given range of values.
     */
    private static DefaultParameterDescriptor<Integer> create(final String name,
            final int defaultValue, final int minimum, final int maximum)
    {
        return new DefaultParameterDescriptor<Integer>(Collections.singletonMap(NAME_KEY, name), 1, 1,
                Integer.class, NumberRange.create(minimum, true, maximum, true), null, defaultValue);
    }

    /**
     * Constructs a descriptor for a mandatory parameter in a range of floating point values.
     *
     * @param  name         The parameter name.
     * @param  defaultValue The default value for the parameter, or {@link Double#NaN} if none.
     * @param  minimum      The minimum parameter value, or {@link Double#NEGATIVE_INFINITY} if none.
     * @param  maximum      The maximum parameter value, or {@link Double#POSITIVE_INFINITY} if none.
     * @param  unit         The unit for default, minimum and maximum values.
     * @return The parameter descriptor for the given range of values.
     *
     * @since 2.5
     */
    private static DefaultParameterDescriptor<Double> create(final String name,
            final double defaultValue, final double minimum, final double maximum, final Unit<?> unit)
    {
        return new DefaultParameterDescriptor<Double>(Collections.singletonMap(NAME_KEY, name), 1, 1,
                Double.class, MeasurementRange.create(minimum, true, maximum, true, unit), null,
                Double.isNaN(defaultValue) ? null : Double.valueOf(defaultValue));
    }
    private static DefaultParameterDescriptorGroup create(final String name, final GeneralParameterDescriptor... parameters) {
        return new DefaultParameterDescriptorGroup(Collections.singletonMap(NAME_KEY, name), 1, 1, parameters);
    }

    /**
     * Tests the {@link Parameters#isValid(GeneralParameterValue, GeneralParameterDescriptor)}
     * method.
     */
    @Test
    public void testIsValid() {
        final ParameterDescriptorGroup group = create("Group",
                // A mandatory parameter with values in the [5 ... 15] range.
                create("Test1", 10, 5, 15),
                // A mandatory parameter with values in the {1, 4, 8} set.
                new DefaultParameterDescriptor<Integer>(Collections.singletonMap(NAME_KEY, "Test2"),
                        1, 1, Integer.class, null, new Integer[] {1, 4, 8}, 4));
        /*
         * Create a parameter group more flexible than the one below.
         * This is needed in order to allow us to create invalide parameters.
         */
        ParameterDescriptorGroup lenient = create("Lenient",
                create("Test1", 10, 0, 100),
                create("Test2",  8, 0, 100));
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
        lenient = create("Lenient",
                create("Test2",  8, 0, 100));
        values = lenient.createValue();
        result = Parameters.isValid(values, group);
        assertFalse(result.pass());
        assertTrue("The error message should contains the name of the missing parameter.",
                result.getExplanation().toString().contains("Test1"));
        /*
         * Create a group with an extra parameter.
         */
        lenient = create("Lenient",
                create("Test1", 10, 0, 100),
                create("Test2",  8, 0, 100),
                create("Test3",  0, 0, 100));
        values = lenient.createValue();
        result = Parameters.isValid(values, group);
        assertFalse(result.pass());
        assertTrue("The error message should contains the name of the extra parameter.",
                result.getExplanation().toString().contains("Test3"));
    }

    /**
     * Tests the {@link Parameters#copy(GeneralParameterValue, Map)} method.
     */
    @Test
    public void testCopyToMap() {
        final ParameterDescriptorGroup group = create("Group",
            create("anInteger", 10, 5, 15),
            create("aRealNumber", 0.25, 0.1, 0.5, null),
            create("SubGroup",
                create("anInteger", 2, 1, 4),
                create("aRealNumber", 1.25, 0.1, 1.4, null)),
            create("anOtherRealNumber", 0.125, 0.1, 0.4, null));

        final ParameterValueGroup values = group.createValue();
        final Properties properties = new Properties();
        Parameters.copy(values, properties);
        assertEquals(Integer.valueOf(10),    properties.remove("anInteger"));
        assertEquals(Double .valueOf(0.25),  properties.remove("aRealNumber"));
        assertEquals(Double .valueOf(0.125), properties.remove("anOtherRealNumber"));
        assertEquals(Integer.valueOf(2),     properties.remove("SubGroup:anInteger"));
        assertEquals(Double .valueOf(1.25),  properties.remove("SubGroup:aRealNumber"));
        assertTrue("Unknown properties remaining.", properties.isEmpty());
    }

    @Test
    public void parameterMapSwitchTest() {
            final ParameterDescriptorGroup group = create("Group",
                    create("anInteger", 10, 5, 15),
                    create("aRealNumber", 0.25, 0.1, 0.5, null),
                    create("SubGroup",
                            create("anInteger", 2, 1, 4),
                            create("aRealNumber", 1.25, 0.1, 1.4, null)),
                    create("anOtherRealNumber", 0.125, 0.1, 0.4, null));

        final ParameterValueGroup values = group.createValue();
        final ParameterValueGroup outputParam = Parameters.toParameter(Parameters.toMap(values), group);
        assertEquals(values, outputParam);
    }
}
