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
package org.geotoolkit.util.converter;

import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.Collection;
import java.io.Serializable;
import org.geotoolkit.test.Depend;
import org.geotoolkit.gui.swing.tree.TreesTest;
import org.apache.sis.util.Classes;

import org.junit.*;
import static org.geotoolkit.test.Assert.*;


/**
 * Tests the {@link ConverterRegistry} implementation. Every tests in this class except
 * {@link #testSystem()} uses their own instance of {@link ConverterRegistry}, so they
 * are not affected by whatever new converter may be added or removed from the system-wide
 * registry.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.00
 */
@Depend({TreesTest.class, StringConverterTest.class, NumberConverterTest.class, URIConverterTest.class,
        URLConverterTest.class, FileConverterTest.class, FallbackConverterTest.class})
public final strictfp class ConverterRegistryTest {
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
        assertRegisteredTarget(converter.getTargetClass());
    }

    /**
     * Ensures that the current {@linkplain #converter} is registered for the given target.
     *
     * @param target The target to ensure that the {@linkplain #converter} is registered for.
     */
    private void assertRegisteredTarget(final Class<?> target) {
        final Class<?> source = converter.getSourceClass();
        final ObjectConverter<?,?> actual;
        try {
            actual = registry.converter(source, target);
        } catch (NonconvertibleObjectException e) {
            fail(e.toString());
            return;
        }
        assertFalse(actual instanceof IdentityConverter<?>);
        assertSame(converter, actual);
    }

    /**
     * Ensures that the current {@linkplain #converter} is not registered for the given target.
     *
     * @param target The target to ensure that the {@linkplain #converter} is not registered for.
     */
    private void assertUnregisteredTarget(final Class<?> target) {
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
     * Ensures that the current {@linkplain #converter} returns the
     * {@linkplain IdentityConverter identity converter} for the given target.
     *
     * @param target The target for which an identity converter should be obtained.
     */
    private void assertIdentityForTarget(final Class<?> target) {
        final Class<?> source = converter.getSourceClass();
        final ObjectConverter<?,?> actual;
        try {
            actual = registry.converter(source, target);
        } catch (NonconvertibleObjectException e) {
            fail(e.toString());
            return;
        }
        assertTrue(Classes.getShortClassName(actual), actual instanceof IdentityConverter<?>);
        assertSame(source, actual.getSourceClass());
        assertSame(source, actual.getTargetClass());
        assertTrue(target.isAssignableFrom(source));
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
        assertRegisteredTarget  (Short       .class);
        assertRegisteredTarget  (Number      .class);
        assertIdentityForTarget (Object      .class);
        assertUnregisteredTarget(Cloneable   .class);
        assertIdentityForTarget (Comparable  .class);
        assertIdentityForTarget (Serializable.class);
        /*
         * Adds String ⇨ Long
         * Expected side-effect: creation of FallbackConverter[String ⇨ Number]
         */
        converter = StringConverter.Long.INSTANCE;
        assertUnregisteredTarget(Long.class);
        registry.register(converter);
        assertRegisteredTarget  (Long        .class);
        assertIdentityForTarget (Object      .class);
        assertUnregisteredTarget(Cloneable   .class);
        assertIdentityForTarget (Comparable  .class);
        assertIdentityForTarget (Serializable.class);
        converter = StringConverter.Short.INSTANCE;
        assertRegisteredTarget(Short.class);
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
        assertUnregisteredTarget(Boolean.class);
        registry.register(converter);
        assertRegisteredTarget  (Boolean     .class);
        assertIdentityForTarget (Object      .class);
        assertUnregisteredTarget(Cloneable   .class);
        assertIdentityForTarget (Comparable  .class);
        assertIdentityForTarget (Serializable.class);
        converter = StringConverter.Short.INSTANCE;
        assertRegisteredTarget(Short.class);
        converter = StringConverter.Long.INSTANCE;
        assertRegisteredTarget(Long.class);
        converter = fallback;
        assertRegisteredTarget(Number.class);
        /*
         * Adds String ⇨ Number
         * Expected side-effect: replacement of the FallbackConverter
         */
        converter = StringConverter.Number.INSTANCE;
        registry.register(converter);
        assertRegisteredTarget  (Number      .class);
        assertIdentityForTarget (Object      .class);
        assertUnregisteredTarget(Cloneable   .class);
        assertIdentityForTarget (Comparable  .class);
        assertIdentityForTarget (Serializable.class);
        converter = StringConverter.Short.INSTANCE;
        assertRegisteredTarget(Short.class);
        converter = StringConverter.Long.INSTANCE;
        assertRegisteredTarget(Long.class);
        converter = StringConverter.Boolean.INSTANCE;
        assertRegisteredTarget(Boolean.class);
        /*
         * Adds String ⇨ Float
         * Expected side-effect: none
         */
        converter = StringConverter.Float.INSTANCE;
        assertUnregisteredTarget(Float.class);
        registry.register(converter);
        assertRegisteredTarget  (Float       .class);
        assertIdentityForTarget (Object      .class);
        assertUnregisteredTarget(Cloneable   .class);
        assertIdentityForTarget (Comparable  .class);
        assertIdentityForTarget (Serializable.class);
        converter = StringConverter.Short.INSTANCE;
        assertRegisteredTarget(Short.class);
        converter = StringConverter.Long.INSTANCE;
        assertRegisteredTarget(Long.class);
        converter = StringConverter.Boolean.INSTANCE;
        assertRegisteredTarget(Boolean.class);
        converter = StringConverter.Number.INSTANCE;
        assertRegisteredTarget(Number.class);
    }

    /**
     * Tests conversions from {@link Number} to miscellaneous objects.
     */
    @Test
    public void testNumberToMiscellaneous() {
        registry = new ConverterRegistry();
        converter = NumberConverter.String.INSTANCE;
        registry.register(converter);
        assertRegisteredTarget  (String      .class);
        assertIdentityForTarget (Object      .class);
        assertUnregisteredTarget(Cloneable   .class);
        assertUnregisteredTarget(Comparable  .class);
        assertIdentityForTarget (Serializable.class);
        assertRegisteredTarget  (CharSequence.class);
        /*
         * Adds Number ⇨ Boolean
         * Expected side-effect: none
         */
        converter = NumberConverter.Boolean.INSTANCE;
        assertUnregisteredTarget(Boolean.class);
        registry.register(converter);
        assertRegisteredTarget  (Boolean     .class);
        assertIdentityForTarget (Object      .class);
        assertUnregisteredTarget(Cloneable   .class);
        assertUnregisteredTarget(Comparable  .class);
        assertIdentityForTarget (Serializable.class);
        // Previous registration should stay unchanged.
        converter = NumberConverter.String.INSTANCE;
        assertRegisteredTarget(String.class);
        assertRegisteredTarget(CharSequence.class);
        /*
         * Adds String ⇨ Number
         * Expected side-effect: none
         */
        converter = StringConverter.Number.INSTANCE;
        registry.register(converter);
        assertRegisteredTarget(Number.class);
        // Previous registration should stay unchanged.
        converter = NumberConverter.String.INSTANCE;
        assertRegisteredTarget(String.class);
        assertRegisteredTarget(CharSequence.class);
        /*
         * Adds Number ⇨ Float
         * Expected side-effect: none
         */
        converter = NumberConverter.Float.INSTANCE;
        registry.register(converter);
        assertRegisteredTarget(Float.class);
        // Previous registration should stay unchanged.
        converter = NumberConverter.Boolean.INSTANCE;
        assertRegisteredTarget(Boolean.class);
    }

    /**
     * Inspects the system registry. This is the only test depending on
     * {@link ConverterRegistry#system()}, which is tested in a read-only way.
     */
    @Test
    public void testSystem() {
        registry  = ConverterRegistry.system();
        converter = NumberConverter.String .INSTANCE; assertRegistered(); assertRegisteredTarget(CharSequence.class);
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
        converter = DateConverter  .Long   .INSTANCE; assertRegistered(); assertRegisteredTarget(Number.class);
        converter = LongConverter  .Date   .INSTANCE; assertRegistered();
    }

    /**
     * Tests {@link ConverterRegistry#getConvertibleTypes()}.
     *
     * @since 3.20
     */
    @Test
    public void testGetConvertibleTypes() {
        final Map<Class<?>, Set<Class<?>>> convertibleTypes = ConverterRegistry.system().getConvertibleTypes();
        Set<Class<?>> set = convertibleTypes.get(String.class);
        assertNotNull("Missing converters from the String type.", set);
        assertTrue("Missing String to Float",   set.contains(Float.class));
        assertTrue("Missing String to Integer", set.contains(Integer.class));
        set = convertibleTypes.get(Collection.class);
        assertNotNull("Missing converters from the Collection type.", set);
        assertTrue("Missing Collection to List", set.contains(List.class));
        assertTrue("Missing Collection to Set",  set.contains(Set.class));
        assertSame("Expected cached value", convertibleTypes, ConverterRegistry.system().getConvertibleTypes());
    }
}
