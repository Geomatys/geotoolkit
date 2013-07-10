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

import java.util.*;
import java.io.IOException;
import java.io.StringWriter;

import org.opengis.parameter.*;
import org.geotoolkit.test.referencing.ParameterTestBase;

import org.junit.*;
import static org.junit.Assert.*;
import static org.opengis.test.Validators.*;


/**
 * Tests the {@link ParameterGroup} class.
 *
 * @author Martin Desruisseaux (IRD)
 * @author Johann Sorel (Geomatys)
 * @version 3.18
 *
 * @since 2.1
 */
@SuppressWarnings("serial")
public final strictfp class ParameterGroupTest extends ParameterTestBase {
    /**
     * Tests parameter values group.
     *
     * @throws IOException Should never happen.
     */
    @Test
    @SuppressWarnings("serial")
    public void testGroup() throws IOException {
        final Integer ONE = 1;
        final Class<Integer> I = Integer.class;
        final ParameterDescriptor<Integer> p1, p2, p3, p4;
        p1 = new DefaultParameterDescriptor<>(name("1"), I, null, ONE, null, null, null, true);
        p2 = new DefaultParameterDescriptor<>(name("2"), I, null, ONE, null, null, null, true);
        p3 = new DefaultParameterDescriptor<>(name("3"), I, null, ONE, null, null, null, false);
        p4 = new DefaultParameterDescriptor<Integer>(name("4"), I, null, ONE, null, null, null, false) {
            /**
             * We are cheating here:  {@code maximumOccurs} should always be 1 for
             * {@code ParameterValue}. However, the Geotk implementation should be
             * robust enough to accept other values. We will test that.
             */
            @Override
            public int getMaximumOccurs() {
                return 2;
            }
        };

        final Parameter<Integer> v1, v2, v3, v4, v1b, v2b, v3b, v4b;
        v1  = new Parameter<>(p1); v1 .setValue( 10);
        v2  = new Parameter<>(p2); v2 .setValue( 20);
        v3  = new Parameter<>(p3); v3 .setValue( 30);
        v4  = new Parameter<>(p4); v4 .setValue( 40);
        v1b = new Parameter<>(p1); v1b.setValue(-10);
        v2b = new Parameter<>(p2); v2b.setValue(-20);
        v3b = new Parameter<>(p3); v3b.setValue(-30);
        v4b = new Parameter<>(p4); v4b.setValue(-40);

        validate(v1);
        validate(v2);
        validate(v3);
        try {
            validate(v4);
            fail("Validation methods should have detected that the descriptor is invalid.");
        } catch (AssertionError e) {
            // This is the expected exception.
        }
        validate(v1b);
        validate(v2b);
        validate(v3b);
        try {
            validate(v4b);
            fail("Validation methods should have detected that the descriptor is invalid.");
        } catch (AssertionError e) {
            // This is the expected exception.
        }

        Map<String,?>                     properties;
        ParameterGroup                    group;
        ParameterDescriptorGroup          descriptor;
        ParameterValue<Integer>           automatic;
        Collection<GeneralParameterValue> values;
        Collection<? extends GeneralParameterDescriptor> content;

        /* --------------------------------------------- *
         * Case (v1, v2, v3) where:
         *    - v1   is mandatory
         *    - v2   is mandatory
         *    - v3   is optional
         * --------------------------------------------- */
        properties = name("group");
        group      = new ParameterGroup(properties, v1, v2, v3);
        descriptor = group.getDescriptor();
        content    = descriptor.descriptors();

        final ParameterWriter writer = new ParameterWriter(new StringWriter());
        writer.format(group); // Ensure there is no exception there.

        assertEquals("name", "group", descriptor.getName().getCode());
        assertEquals("descriptors", 3, content.size());
        assertTrue  ("contains(p1)",  content.contains(p1));
        assertTrue  ("contains(p2)",  content.contains(p2));
        assertTrue  ("contains(p3)",  content.contains(p3));
        assertFalse ("contains(p4)",  content.contains(p4));
        assertSame  ("descriptor(\"1\")",  p1, descriptor.descriptor("1"));
        assertSame  ("descriptor(\"2\")",  p2, descriptor.descriptor("2"));
        assertSame  ("descriptor(\"3\")",  p3, descriptor.descriptor("3"));

        // Checks default values
        values = group.values();
        assertEquals("values.size()",  3, values.size());
        assertTrue  ("contains(v1)",      values.contains(v1 ));
        assertTrue  ("contains(v2)",      values.contains(v2 ));
        assertTrue  ("contains(v3)",      values.contains(v3 ));
        assertFalse ("contains(v4)",      values.contains(v4 ));
        assertFalse ("contains(v1b)",     values.contains(v1b));
        assertFalse ("contains(v2b)",     values.contains(v2b));
        assertFalse ("contains(v3b)",     values.contains(v3b));
        assertSame  ("parameter(\"1\")",  v1, group.parameter("1"));
        assertSame  ("parameter(\"2\")",  v2, group.parameter("2"));
        assertSame  ("parameter(\"3\")",  v3, group.parameter("3"));
        assertEquals("parameter(\"1\")",  10, group.parameter("1").intValue());
        assertEquals("parameter(\"2\")",  20, group.parameter("2").intValue());
        assertEquals("parameter(\"3\")",  30, group.parameter("3").intValue());

        // Tests the replacement of some values
        assertFalse("remove(v1b)", values.remove(v1b));
        try {
            assertTrue(values.remove(v1));
            fail("v1 is a mandatory parameter; it should not be removeable.");
        } catch (InvalidParameterCardinalityException e) {
            // This is the expected exception.
            assertEquals("1", e.getParameterName());
            assertNotNull(e.getMessage());
        }
        try {
            assertTrue(values.add(v4));
            fail("v4 is not a parameter for this group.");
        } catch (InvalidParameterNameException e) {
            // This is the expected exception.
            assertEquals("4", e.getParameterName());
            assertNotNull(e.getMessage());
        }
        assertTrue  ("add(v1b)", values.add(v1b));
        assertTrue  ("add(v2b)", values.add(v2b));
        assertTrue  ("add(v3b)", values.add(v3b));
        assertFalse ("add(v1b)", values.add(v1b)); // Already present
        assertFalse ("add(v2b)", values.add(v2b)); // Already present
        assertFalse ("add(v3b)", values.add(v3b)); // Already present
        assertEquals("parameter(\"1b\")", -10, group.parameter("1").intValue());
        assertEquals("parameter(\"2b\")", -20, group.parameter("2").intValue());
        assertEquals("parameter(\"3b\")", -30, group.parameter("3").intValue());
        assertEquals("values.size()", 3, values.size());

        // Tests equality
        assertEquals("new", group, group = new ParameterGroup(descriptor, v1b, v2b, v3b));

        /* --------------------------------------------- *
         * Case (v1, v2) where:
         *    - v1   is mandatory
         *    - v2   is mandatory
         *    - v3   is optional and initially omitted
         * --------------------------------------------- */
        group      = new ParameterGroup(descriptor, v1, v2);
        descriptor = group.getDescriptor();
        values     = group.values();
        automatic  = v3.getDescriptor().createValue();
        writer.format(group); // Ensure there is no exception there.
        assertEquals   ("values.size()", 2, values.size());
        assertTrue     ("contains(v1)",     values.contains(v1 ));
        assertTrue     ("contains(v2)",     values.contains(v2 ));
        assertFalse    ("contains(v3)",     values.contains(v3 ));
        assertFalse    ("contains(v4)",     values.contains(v4 ));
        assertFalse    ("contains(v1b)",    values.contains(v1b));
        assertFalse    ("contains(v2b)",    values.contains(v2b));
        assertFalse    ("contains(v3b)",    values.contains(v3b));
        assertSame     ("parameter(\"1\")", v1, group.parameter ("1"));
        assertSame     ("parameter(\"2\")", v2, group.parameter ("2"));
        assertFalse    ("contains(automatic)",  values.contains(automatic));
        assertNotEquals("parameter(\"3\")", v3, group.parameter ("3")); // Should have automatically created.
        assertTrue     ("contains(automatic)",  values.contains(automatic));
        try {
            assertNotNull(group.parameter("4"));
            fail("v4 parameter should not be allowed in this group.");
        } catch (ParameterNotFoundException e) {
            // This is the expected exception.
            assertEquals("4", e.getParameterName());
            assertNotNull(e.getMessage());
        }

        // Tests the replacement of some values
        assertFalse("remove(v1b)",  values.remove(v1b));       assertEquals("values.size()", 3, values.size());
        assertFalse("remove(v3)",   values.remove(v3));        assertEquals("values.size()", 3, values.size());
        assertTrue ("remove(auto)", values.remove(automatic)); assertEquals("values.size()", 2, values.size());
        try {
            assertTrue(values.remove(v1));
            fail("v1 is a mandatory parameter; it should not be removeable.");
        } catch (InvalidParameterCardinalityException e) {
            // This is the expected exception.
            assertEquals("1", e.getParameterName());
            assertNotNull(e.getMessage());
        }

        assertEquals("values.size()", 2, values.size());
        assertTrue  ("add(v1b)", values.add(v1b));
        assertTrue  ("add(v2b)", values.add(v2b));
        assertTrue  ("add(v3b)", values.add(v3b));
        assertFalse ("add(v1b)", values.add(v1b)); // Already present
        assertFalse ("add(v2b)", values.add(v2b)); // Already present
        assertFalse ("add(v3b)", values.add(v3b)); // Already present
        assertEquals("parameter(\"1b\")", -10, group.parameter("1").intValue());
        assertEquals("parameter(\"2b\")", -20, group.parameter("2").intValue());
        assertEquals("parameter(\"3b\")", -30, group.parameter("3").intValue());
        assertEquals("values.size()", 3, values.size());

        /* --------------------------------------------- *
         * Case (v1, v4, v3, v4b) where:
         *    - v1   is mandatory
         *    - v3   is optional
         *    - v4   is optional and can be included twice.
         * --------------------------------------------- */
        try {
            group = new ParameterGroup(properties, v1, v3, v4, v3b);
            fail("Adding two 'v3' value should not be allowed");
        } catch (InvalidParameterCardinalityException e) {
            // This is the expected exception.
            assertEquals("3", e.getParameterName());
            assertNotNull(e.getMessage());
        }
        group      = new ParameterGroup(properties, v1, v4, v3, v4b);
        descriptor = group.getDescriptor();
        values     = group.values();
        automatic  = v3.getDescriptor().createValue();
        writer.format(group); // Ensure there is no exception there.
        assertEquals   ("values.size()", 4, values.size());
        assertTrue     ("contains(v1)",     values.contains(v1 ));
        assertFalse    ("contains(v2)",     values.contains(v2 ));
        assertTrue     ("contains(v3)",     values.contains(v3 ));
        assertTrue     ("contains(v4)",     values.contains(v4 ));
        assertFalse    ("contains(v1b)",    values.contains(v1b));
        assertFalse    ("contains(v2b)",    values.contains(v2b));
        assertFalse    ("contains(v3b)",    values.contains(v3b));
        assertTrue     ("contains(v4b)",    values.contains(v4b));
        assertSame     ("parameter(\"1\")", v1, group.parameter ("1"));
        assertSame     ("parameter(\"3\")", v3, group.parameter ("3"));
        assertSame     ("parameter(\"4\")", v4, group.parameter ("4"));
        assertTrue     ("remove(v3)",       values.remove(v3));
        assertFalse    ("contains(automatic)", values.contains(automatic));
        assertNotEquals("parameter(\"3\")", v3, group.parameter ("3")); // Should have automatically created.
        assertTrue     ("contains(automatic)", values.contains(automatic));

        try {
            new ParameterGroup(descriptor, v4, v3);
            fail("Parameter 1 was mandatory.");
        } catch (InvalidParameterCardinalityException exception) {
            // This is the expected exception.
            assertEquals("1", exception.getParameterName());
        }
        try {
            new ParameterGroup(descriptor, v1, v4, v3, v3b);
            fail("Parameter 3 was not allowed to be inserted twice.");
        } catch (InvalidParameterCardinalityException exception) {
            // This is the expected exception.
            assertEquals("3", exception.getParameterName());
        }
        try {
            new ParameterGroup(descriptor, v1, v3, v1b);
            fail("Parameter 1 was not allowed to be inserted twice.");
        } catch (InvalidParameterCardinalityException exception) {
            // This is the expected exception.
            assertEquals("1", exception.getParameterName());
        }

        /* --------------------------------------------- *
         * Case (v1, v2) where:
         *    - v1   is mandatory
         *    - v2   is mandatory
         * --------------------------------------------- */
        group      = new ParameterGroup(properties, v1, v2);
        descriptor = group.getDescriptor();
        content    = descriptor.descriptors();
        writer.format(group); // Ensure there is no exception there.
        assertEquals("name", "group", descriptor.getName().getCode());
        assertEquals("descriptors.size()", 2, content.size());
        assertTrue  ("contains(p1)",          content.contains(p1));
        assertTrue  ("contains(p2)",          content.contains(p2));
        assertFalse ("contains(p3)",          content.contains(p3));
        assertSame  ("descriptor(\"1\")", p1, descriptor.descriptor("1"));
        assertSame  ("descriptor(\"2\")", p2, descriptor.descriptor("2"));
        try {
            assertSame("p3", p3, descriptor.descriptor("3"));
            fail("p3 should not exists.");
        } catch (ParameterNotFoundException e) {
            // This is the expected exception
            assertEquals("3", e.getParameterName());
        }

        values = group.values();
        assertEquals("values.size()", 2, values.size());
        assertTrue  ("contains(v1)",     values.contains(v1 ));
        assertTrue  ("contains(v2)",     values.contains(v2 ));
        assertFalse ("contains(v3)",     values.contains(v3 ));
        assertFalse ("contains(v1b)",    values.contains(v1b));
        assertFalse ("contains(v2b)",    values.contains(v2b));
        assertFalse ("contains(v3b)",    values.contains(v3b));
        assertSame  ("parameter(\"1\")", v1, group.parameter("1"));
        assertSame  ("parameter(\"2\")", v2, group.parameter("2"));
        try {
            assertSame("parameter(\"3\")", v3, group.parameter("3"));
            fail("v3 should not exists");
        } catch (ParameterNotFoundException e) {
            // This is the expected exception
            assertEquals("3", e.getParameterName());
        }

        /* --------------------------------------------- *
         * Case (v1, v3) where:
         *    - v1   is mandatory
         *    - v3   is optional
         * --------------------------------------------- */
        group      = new ParameterGroup(properties, v1, v3);
        descriptor = group.getDescriptor();
        content    = descriptor.descriptors();
        writer.format(group); // Ensure there is no exception there.
        assertEquals("name", "group", descriptor.getName().getCode());
        assertEquals("descriptors.size()", 2, content.size());
        assertTrue  ("contains(p1)",       content.contains(p1));
        assertFalse ("contains(p2)",       content.contains(p2));
        assertTrue  ("contains(p3)",       content.contains(p3));
        assertSame  ("descriptor(\"1\")",  p1, descriptor.descriptor("1"));
        assertSame  ("descriptor(\"3\")",  p3, descriptor.descriptor("3"));
        try {
            assertSame("descriptor(\"2\")", p2, descriptor.descriptor("2"));
            fail("p2 should not exists");
        } catch (ParameterNotFoundException e) {
            // This is the expected exception
            assertEquals("2", e.getParameterName());
        }

        values = group.values();
        assertEquals("values.size()", 2, values.size());
        assertTrue  ("contains(v1)",  values.contains(v1 ));
        assertFalse ("contains(v2)",  values.contains(v2 ));
        assertTrue  ("contains(v3)",  values.contains(v3 ));
        assertFalse ("contains(v1b)", values.contains(v1b));
        assertFalse ("contains(v2b)", values.contains(v2b));
        assertFalse ("contains(v3b)", values.contains(v3b));
        assertSame  ("parameter(\"1\")", v1, group.parameter("1"));
        assertSame  ("parameter(\"3\")", v3, group.parameter("3"));
        try {
            assertSame("parameter(\"2\")", v2, group.parameter("2"));
            fail("v2 should not exists");
        } catch (ParameterNotFoundException e) {
            // This is the expected exception
            assertEquals("2", e.getParameterName());
        }

        /* --------------------------------------------- *
         * Construction tests
         * --------------------------------------------- */
        group = new ParameterGroup(properties, v1, v2, v3, v4, v4b);
        writer.format(group); // Ensure there is no exception there.
        assertEquals("values.size()", 5, group.values().size());
        try {
            new ParameterGroup(properties, v1, v2, v3, v3b);
            fail("Parameter 3 was not allowed to be inserted twice.");
        } catch (InvalidParameterCardinalityException e) {
            // This is the expected exception.
            assertEquals("3", e.getParameterName());
        }
        try {
            new ParameterGroup(properties, v1, v3, v1b);
            fail("Parameter 1 was not allowed to be inserted twice.");
        } catch (InvalidParameterCardinalityException e) {
            // This is the expected exception.
            assertEquals("1", e.getParameterName());
        }
    }

    /**
     * Tests the {@link DefaultParameterValueGroup#addGroup} method. Ensures the descriptor is
     * found and the new value correctly insert.
     *
     * @since 3.18
     */
    @Test
    public void testAddGroup(){
        final ParameterDescriptorGroup subGroupDesc = new DefaultParameterDescriptorGroup(
                Collections.singletonMap("name", "cxparam"), 0, 10);
        final ParameterDescriptorGroup groupDesc = new DefaultParameterDescriptorGroup(
                "config", new GeneralParameterDescriptor[] {subGroupDesc});

        final ParameterValueGroup values = groupDesc.createValue();
        final ParameterValueGroup sub = values.addGroup("cxparam");
        assertNotNull(sub);
        assertEquals(1, values.values().size());
        assertEquals(values.values().get(0), sub);
    }

    /**
     * Ensures that the specified objects are not equals.
     */
    private static void assertNotEquals(final String message, final Object o1, final Object o2) {
        assertNotNull(message, o1);
        assertNotNull(message, o2);
        assertNotSame(message, o1, o2);
        assertFalse  (message, o1.equals(o2));
    }

    /**
     * Returns a map with only one entry, which is {@code "name"}=<var>name</var>.
     */
    private static Map<String,String> name(final String name) {
        return Collections.singletonMap(ParameterDescriptor.NAME_KEY, name);
    }
}
