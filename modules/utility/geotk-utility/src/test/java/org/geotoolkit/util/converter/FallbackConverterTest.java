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

import org.junit.*;
import org.apache.sis.test.DependsOn;
import static org.geotoolkit.test.Assert.*;


/**
 * Tests {@link FallbackConverter}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 */
@DependsOn(StringConverterTest.class)
public final strictfp class FallbackConverterTest {
    /**
     * Tests a chain of fallback converters. The initial fallback will understand {@link Short}
     * and {@link Long} types. Later we will add other types like {@link Boolean} and {@link Float}.
     *
     * @throws NonconvertibleObjectException Should never happen.
     */
    @Test
    public void testChain() throws NonconvertibleObjectException {
        @SuppressWarnings({"unchecked","rawtypes"}) // Generic array creation.
        final ObjectConverter<String,?>[] converters = new ObjectConverter[] {
            StringConverter.Short  .INSTANCE,
            StringConverter.Long   .INSTANCE,
            StringConverter.Float  .INSTANCE,
            StringConverter.Integer.INSTANCE,
            StringConverter.Boolean.INSTANCE,
            StringConverter.Double .INSTANCE
        };
        // Index at which some kind of values are expected.
        final int LONG    = 1;
        final int FLOAT   = 2;
        final int DOUBLE  = 5;
        final int BOOLEAN = 4;
        assertEquals(Long.class,    converters[LONG]   .getTargetClass());
        assertEquals(Float.class,   converters[FLOAT]  .getTargetClass());
        assertEquals(Double.class,  converters[DOUBLE] .getTargetClass());
        assertEquals(Boolean.class, converters[BOOLEAN].getTargetClass());
        /*
         * Create the fallback chain and check at every steps. The source class should never
         * change. But the target type will be relaxed more and more as we add new fallbacks.
         * The initial target will be Short, then Number, and finally Object.
         */
        ObjectConverter<String,?> c = null;
        for (int i=0; i<converters.length; i++) {
            c = FallbackConverter.create(c, converters[i]);
            final ObjectConverter<String,?>[] expected;
            final Class<?> targetClass;
            if (i == 0) {
                targetClass = Short.class;
                expected    = converters;
            } else if (i < BOOLEAN) {
                targetClass = Number.class;
                expected    = converters;
            } else if (i < DOUBLE) {
                targetClass = Object.class;
                expected    = converters;
            } else {
                targetClass = Object.class;
                expected    = converters.clone();
                expected[BOOLEAN] = converters[DOUBLE];
                expected[DOUBLE]  = converters[BOOLEAN];
                // Boolean is expected to be last.
            }
            assertEquals(targetClass, c.getTargetClass());
            assertEquals(String.class, c.getSourceClass());
            assertEquals(i+1, count(c, expected, 0));
            /*
             * Try conversions. Parsing numbers should returns a Short, Long or Float because
             * they were the first converters declared. Parsing a boolean should succeed only
             * after we registered the boolean converter.
             */
            assertConvert(c, "5",                      Short.valueOf((short) 5));
            assertConvert(c, "80000", (i >= LONG)    ? Long.valueOf(80000) : null);
            assertConvert(c, "5.0",   (i >= FLOAT)   ? Float.valueOf(5f) : null);
            assertConvert(c, "2.5",   (i >= FLOAT)   ? Float.valueOf(2.5f) : null);
            assertConvert(c, "1E+20", (i >= FLOAT)   ? Float.valueOf(1E+20f) : null);
            assertConvert(c, "1E+80", (i >= FLOAT)   ? Float.POSITIVE_INFINITY : null);
            assertConvert(c, "true",  (i >= BOOLEAN) ? Boolean.TRUE : null);
        }
        /*
         * Adds a generic converter for number. It is going to change everything.
         * This converter is expected to be registered before any other number
         * converter. Same tests than above will return different objects.
         */
        @SuppressWarnings({"unchecked","rawtypes"}) // Generic array creation.
        final ObjectConverter<String,?>[] expected = new ObjectConverter[converters.length + 1];
        System.arraycopy(converters, 0, expected, 1, converters.length);
        expected[BOOLEAN + 1] = converters[DOUBLE];
        expected[DOUBLE  + 1] = converters[BOOLEAN];
        c = FallbackConverter.create(c, expected[0] = StringConverter.Number.INSTANCE);
        assertEquals(String.class, c.getSourceClass());
        assertEquals(Object.class, c.getTargetClass());
        assertEquals(expected.length, count(c, expected, 0));
        assertConvert(c, "5",     Byte.valueOf((byte) 5));
        assertConvert(c, "80000", Integer.valueOf(80000));
        assertConvert(c, "5.0",   Byte.valueOf((byte) 5));
        assertConvert(c, "2.5",   Float.valueOf(2.5f));
        assertConvert(c, "1E+20", Double.valueOf(1E+20));
        assertConvert(c, "1E+80", Double.valueOf(1E+80));
        assertConvert(c, "true",  Boolean.TRUE);
        assertTrue(c.hasRestrictions());
        /*
         * Compares the string representations. In theory this is not needed, since the
         * above tests performs the same check in a more programmatic way. However this
         * is a convenient visual check and a useful debugging tool.
         */
        assertMultilinesEquals(
                "String ⇨ Object\n" +
                "├───String ⇨ Number\n" +
                "│   ├───String ⇨ Number\n" +
                "│   ├───String ⇨ Short\n" +
                "│   ├───String ⇨ Long\n" +
                "│   ├───String ⇨ Float\n" +
                "│   ├───String ⇨ Integer\n" +
                "│   └───String ⇨ Double\n" +
                "└───String ⇨ Boolean\n", c.toString());
    }

    /**
     * Converts the given value and compares the result with the expected one.
     *
     * @param converter The converter to use.
     * @param value     The value to convert.
     * @param expected  The expected result, or {@code null} if the conversion is expected to
     *                  thrown an exception.
     *
     * @throws NonconvertibleObjectException if an exception was not expected but occurred.
     */
    private static void assertConvert(final ObjectConverter<String,?> converter,
            final String value, final Object expected) throws NonconvertibleObjectException
    {
        final Object result;
        try {
            result = converter.convert(value);
        } catch (NonconvertibleObjectException exception) {
            if (expected != null) {
                throw exception;
            }
            return;
        }
        assertNotNull(result);
        if (expected == null) {
            fail("Expected an exception but got " + result);
        }
        assertEquals(expected, result);
    }

    /**
     * Counts the number of converters, descending down in the tree if necessary.
     *
     * @param converter The converter for which to count underlying converters.
     * @param expected  Expected converters while we go down the tree, or {@code null}.
     * @param offset    The index of the first element to consider in the array.
     */
    private static int count(final ObjectConverter<?,?> converter,
                             final ObjectConverter<?,?>[] expected, final int offset)
    {
        if (converter instanceof FallbackConverter<?,?>) {
            final FallbackConverter<?,?> fallback = (FallbackConverter<?,?>) converter;
            synchronized (fallback) {
                final int c = count(fallback.converter(true), expected, offset);
                return c + count(fallback.converter(false), expected, offset + c);
            }
        }
        if (expected != null) {
            assertEquals(String.valueOf(offset), expected[offset], converter);
        }
        return 1;
    }
}
