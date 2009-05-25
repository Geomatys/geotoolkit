/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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
package org.geotoolkit.util.converter;

import java.io.Serializable;
import org.geotoolkit.test.Depend;
import org.geotoolkit.gui.swing.tree.TreesTest;

import org.junit.*;
import static org.junit.Assert.*;
import static org.geotoolkit.test.Commons.*;


/**
 * Tests the {@link ConverterRegistry} implementation.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 */
@Depend({TreesTest.class, StringConverterTest.class, NumberConverterTest.class, FallbackConverterTest.class})
public final class ConverterRegistryTest {
    /**
     * The registry being tested.
     */
    private ConverterRegistry registry;

    /**
     * A particular converter being tested.
     */
    private ObjectConverter<?,?> converter;

    /**
     * Ensures that the current {@linkplain #converter} is registered.
     */
    private void assertRegistered() {
        assertRegistered(converter.getTargetClass());
    }

    /**
     * Ensures that the current {@linkplain #converter} is registered for the given target.
     *
     * @param target The target to ensure that the {@linkplain #converter} is registered for.
     */
    private void assertRegistered(final Class<?> target) {
        final Class<?> source = converter.getSourceClass();
        final ObjectConverter<?,?> actual;
        try {
            actual = registry.converter(source, target);
        } catch (NonconvertibleObjectException e) {
            fail(e.toString());
            return;
        }
        assertSame(converter, actual);
    }

    /**
     * Ensures that the current {@linkplain #converter} is not registered for the given target.
     *
     * @param target The target to ensure that the {@linkplain #converter} is not registered for.
     */
    private void assertUnregistered(final Class<?> target) {
        final Class<?> source = converter.getSourceClass();
        final ObjectConverter<?,?> actual;
        try {
            actual = registry.converter(source, target);
        } catch (NonconvertibleObjectException e) {
            // This is the expected exception
            return;
        }
        fail("Unexpected converter: " + actual);
    }

    /**
     * Asserts that the converter to the given target is a fallback having the given string
     * representation.
     *
     * @param expected The expected string representation of the fallback.
     */
    private void assertFallback(final Class<?> target, final String expected) {
        final Class<?> source = converter.getSourceClass();
        try {
            converter = registry.converter(source, target);
        } catch (NonconvertibleObjectException e) {
            fail(e.toString());
            return;
        }
        assertMultilinesEquals(expected, converter.toString());
    }

    /**
     * Tests conversions from {@link String} to miscellaneous objects.
     */
    @Test
    public void testStringToMiscellaneous() {
        registry = new ConverterRegistry();
        converter = StringConverter.Short.INSTANCE;
        registry.register(converter);
        assertRegistered  (Short       .class);
        assertRegistered  (Number      .class);
        assertUnregistered(Object      .class);
        assertUnregistered(Cloneable   .class);
        assertUnregistered(Comparable  .class);
        assertUnregistered(Serializable.class);
        /*
         * Adds String ⇨ Long
         * Expected side-effect: creation of FallbackConverter[String ⇨ Number]
         */
        converter = StringConverter.Long.INSTANCE;
        assertUnregistered(Long.class);
        registry.register(converter);
        assertRegistered  (Long        .class);
        assertUnregistered(Object      .class);
        assertUnregistered(Cloneable   .class);
        assertUnregistered(Comparable  .class);
        assertUnregistered(Serializable.class);
        converter = StringConverter.Short.INSTANCE;
        assertRegistered(Short.class);
        assertFallback(Number.class,
                "String ⇨ Number\n" +
                "├───String ⇨ Short\n" +
                "└───String ⇨ Long\n");
        /*
         * Adds String ⇨ Boolean
         * Expected side-effect: none since Boolean is not a Number
         */
        final ObjectConverter<?,?> fallback = converter;
        converter = StringConverter.Boolean.INSTANCE;
        assertUnregistered(Boolean.class);
        registry.register(converter);
        assertRegistered  (Boolean     .class);
        assertUnregistered(Object      .class);
        assertUnregistered(Cloneable   .class);
        assertUnregistered(Comparable  .class);
        assertUnregistered(Serializable.class);
        converter = StringConverter.Short.INSTANCE;
        assertRegistered(Short.class);
        converter = StringConverter.Long.INSTANCE;
        assertRegistered(Long.class);
        converter = fallback;
        assertRegistered(Number.class);
        /*
         * Adds String ⇨ Number
         * Expected side-effect: replacement of the FallbackConverter
         */
        converter = StringConverter.Number.INSTANCE;
        registry.register(converter);
        assertRegistered  (Number      .class);
        assertUnregistered(Object      .class);
        assertUnregistered(Cloneable   .class);
        assertUnregistered(Comparable  .class);
        assertUnregistered(Serializable.class);
        converter = StringConverter.Short.INSTANCE;
        assertRegistered(Short.class);
        converter = StringConverter.Long.INSTANCE;
        assertRegistered(Long.class);
        converter = StringConverter.Boolean.INSTANCE;
        assertRegistered(Boolean.class);
        /*
         * Adds String ⇨ Float
         * Expected side-effect: none
         */
        converter = StringConverter.Float.INSTANCE;
        assertUnregistered(Float.class);
        registry.register(converter);
        assertRegistered  (Float       .class);
        assertUnregistered(Object      .class);
        assertUnregistered(Cloneable   .class);
        assertUnregistered(Comparable  .class);
        assertUnregistered(Serializable.class);
        converter = StringConverter.Short.INSTANCE;
        assertRegistered(Short.class);
        converter = StringConverter.Long.INSTANCE;
        assertRegistered(Long.class);
        converter = StringConverter.Boolean.INSTANCE;
        assertRegistered(Boolean.class);
        converter = StringConverter.Number.INSTANCE;
        assertRegistered(Number.class);
    }

    /**
     * Tests conversions from {@link Number} to miscellaneous objects.
     */
    @Test
    public void testNumberToMiscellaneous() {
        registry = new ConverterRegistry();
        converter = NumberConverter.String.INSTANCE;
        registry.register(converter);
        assertRegistered  (String      .class);
        assertUnregistered(Object      .class);
        assertUnregistered(Cloneable   .class);
        assertUnregistered(Comparable  .class);
        assertUnregistered(Serializable.class);
        assertRegistered  (CharSequence.class);
        /*
         * Adds Number ⇨ Boolean
         * Expected side-effect: none
         */
        converter = NumberConverter.Boolean.INSTANCE;
        assertUnregistered(Boolean.class);
        registry.register(converter);
        assertRegistered  (Boolean     .class);
        assertUnregistered(Object      .class);
        assertUnregistered(Cloneable   .class);
        assertUnregistered(Comparable  .class);
        assertUnregistered(Serializable.class);
        converter = NumberConverter.String.INSTANCE;
        assertRegistered(String.class);
        assertRegistered(CharSequence.class);
        /*
         * Adds String ⇨ Number
         * Expected side-effect: none
         */
        converter = StringConverter.Number.INSTANCE;
        registry.register(converter);
        assertRegistered(Number.class);
        converter = NumberConverter.String.INSTANCE;
        assertRegistered(String.class);
        assertRegistered(CharSequence.class);
        /*
         * Adds Number ⇨ Float
         * Expected side-effect: none
         */
        converter = NumberConverter.Float.INSTANCE;
        registry.register(converter);
        assertRegistered(Float.class);
        converter = NumberConverter.Boolean.INSTANCE;
        assertRegistered(Boolean.class);
    }

    /**
     * Inspects the system registry.
     */
    @Test
    public void testSystem() {
        registry = ConverterRegistry.system();
        converter = NumberConverter.String .INSTANCE; assertRegistered(); assertRegistered(CharSequence.class);
        converter = NumberConverter.Double .INSTANCE; assertRegistered();
        converter = NumberConverter.Float  .INSTANCE; assertRegistered();
        converter = NumberConverter.Long   .INSTANCE; assertRegistered();
        converter = NumberConverter.Integer.INSTANCE; assertRegistered();
        converter = NumberConverter.Short  .INSTANCE; assertRegistered();
        converter = NumberConverter.Byte   .INSTANCE; assertRegistered();
        converter = NumberConverter.Boolean.INSTANCE; assertRegistered();
        converter = NumberConverter.Color  .INSTANCE; assertRegistered();
        converter = StringConverter.Number .INSTANCE; assertRegistered();
        converter = StringConverter.Byte   .INSTANCE; assertRegistered();
        converter = StringConverter.Short  .INSTANCE; assertRegistered();
        converter = StringConverter.Integer.INSTANCE; assertRegistered();
        converter = StringConverter.Long   .INSTANCE; assertRegistered();
        converter = StringConverter.Float  .INSTANCE; assertRegistered();
        converter = StringConverter.Double .INSTANCE; assertRegistered();
        converter = DateConverter  .Long   .INSTANCE; assertRegistered(); assertRegistered(Number.class);
        converter = LongConverter  .Date   .INSTANCE; assertRegistered();
    }
}
