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

import java.awt.Color;
import java.io.File;
import java.net.URI;
import java.net.URL;
import java.net.URISyntaxException;
import java.net.MalformedURLException;
import java.util.Date;
import java.sql.Timestamp;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Set;
import java.util.List;
import java.util.Arrays;
import java.util.Collection;

import org.opengis.util.CodeList;
import org.opengis.util.InternationalString;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.metadata.citation.PresentationForm;


import org.apache.sis.test.DependsOn;
import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests a few conversions using the system converter.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.19
 *
 * @since 3.00
 */
@DependsOn(ConverterRegistryTest.class)
public final strictfp class SystemConverterTest {
    /**
     * Tests the conversion of collections.
     *
     * @throws NonconvertibleObjectException Should not happen.
     */
    @Test
    public void testCollections() throws NonconvertibleObjectException {
        final ConverterRegistry system = ConverterRegistry.system();
        List<String> list = Arrays.asList("Test 1", "Test 2", "Test 3");
        assertSame(list, system.converter(Collection.class, List.class).convert(list));

        final Set<?> set = system.converter(List.class, Set.class).convert(list);
        assertEquals(list.size(), set.size());
        assertTrue(set.containsAll(list));
        assertEquals(list, system.converter(Collection.class, List.class).convert(set));
    }

    /**
     * Tests the conversion of {@linl String} to various code lists.
     *
     * @throws NonconvertibleObjectException Should not happen.
     */
    @Test
    public void testCodeList() throws NonconvertibleObjectException {
        final ConverterRegistry system = ConverterRegistry.system();
        ObjectConverter<String, ? extends CodeList<?>> converter;
        converter = system.converter(String.class, PixelInCell.class);
        assertSame(PixelInCell.CELL_CENTER, converter.convert("CELL_CENTER"));
        assertSame(PixelInCell.CELL_CENTER, converter.convert("cell center"));
        assertSame("Previously fetched converters should be cached.",
                converter, system.converter(String.class, PixelInCell.class));

        converter = system.converter(String.class, PresentationForm.class);
        assertSame(PresentationForm.MAP_DIGITAL, converter.convert("MAP_DIGITAL"));
        assertSame("Previously fetched converters should be cached.",
                converter, system.converter(String.class, PresentationForm.class));
    }

    /**
     * Tests conversions from character sequences to various objects.
     *
     * @throws NonconvertibleObjectException Should not happen.
     */
    @Test
    public void testCharSequence() throws NonconvertibleObjectException {
        final ConverterRegistry system = ConverterRegistry.system();
        assertEquals("A string for test.", system.converter(CharSequence.class, String.class ).convert("A string for test."));
        assertEquals(Integer.valueOf(300), system.converter(CharSequence.class, Integer.class).convert("300"));

        ObjectConverter<CharSequence,InternationalString> converter = system.converter(CharSequence.class, InternationalString.class);
        assertEquals(CharSequence       .class, converter.getSourceClass());
        assertEquals(InternationalString.class, converter.getTargetClass());
        InternationalString i18n = converter.convert("A localized string.");
        assertEquals("A localized string.", i18n.toString());
        assertSame("Should detect that the source is already of appropriate type.", i18n, converter.convert(i18n));
    }

    /**
     * Tests conversions from strings to colors.
     *
     * @throws NonconvertibleObjectException Should not happen.
     *
     * @since 3.19
     */
    @Test
    public void testStringToColor() throws NonconvertibleObjectException {
        final ConverterRegistry system = ConverterRegistry.system();
        assertEquals(0xD2787034, system.converter(String.class, Color.class).convert("#D2787034").getRGB());
        assertEquals(0xAA11BB88, system.converter(String.class, Color.class).convert("#A1B8").getRGB());
    }

    /**
     * Tests conversions from strings to numbers.
     *
     * @throws NonconvertibleObjectException Should not happen.
     */
    @Test
    public void testStringToNumber() throws NonconvertibleObjectException {
        final ConverterRegistry system = ConverterRegistry.system();
        assertEquals(Short.valueOf((short) 300), system.converter(String.class, Short.class).convert("300"));
        assertEquals(Float.valueOf((float) 0.5), system.converter(String.class, Float.class).convert("0.5"));
    }

    /**
     * Tests conversions from numbers to strings.
     *
     * @throws NonconvertibleObjectException Should not happen.
     */
    @Test
    public void testNumberToString() throws NonconvertibleObjectException {
        final ConverterRegistry system = ConverterRegistry.system();
        assertEquals("12",  system.converter(Integer.class, String.class).convert(12));
        assertEquals("2.5", system.converter(Number.class,  String.class).convert(2.5));
    }

    /**
     * Tests conversions from numbers to numbers.
     *
     * @throws NonconvertibleObjectException Should not happen.
     */
    @Test
    public void testNumberToNumber() throws NonconvertibleObjectException {
        final ConverterRegistry system = ConverterRegistry.system();
        assertEquals(Float.valueOf(2.0f), system.converter(Integer.class, Float.class).convert(2));
        assertEquals(Float.valueOf(3.0f), system.converter(Number .class, Float.class).convert(3));
        assertEquals(Float.valueOf(0.5f), system.converter(Double .class, Float.class).convert(0.5));
        assertEquals(Float.valueOf(2.5f), system.converter(Number .class, Float.class).convert(2.5));

        assertEquals(BigInteger.valueOf(2),   system.converter(Number.class, BigInteger.class).convert(2));
        assertEquals(BigDecimal.valueOf(2.5), system.converter(Number.class, BigDecimal.class).convert(2.5));
    }

    /**
     * Tests conversions from dates to timestamps.
     *
     * @throws NonconvertibleObjectException Should not happen.
     */
    @Test
    public void testDateToTimestamp() throws NonconvertibleObjectException {
        final ConverterRegistry system = ConverterRegistry.system();
        final Timestamp converted = system.converter(Date.class, Timestamp.class).convert(new Date(2000));
        assertEquals(Timestamp.class, converted.getClass());
        assertEquals(new Timestamp(2000), converted);
    }

    /**
     * Tests conversions from timestamps to SQL dates.
     *
     * @throws NonconvertibleObjectException Should not happen.
     *
     * @since 3.17
     */
    @Test
    public void testTimestampToSqlDate() throws NonconvertibleObjectException {
        final ConverterRegistry system = ConverterRegistry.system();
        final java.sql.Date converted = system.converter(Timestamp.class, java.sql.Date.class).convert(new Timestamp(2000));
        assertEquals(java.sql.Date.class, converted.getClass());
        assertEquals(new java.sql.Date(2000), converted);
    }

    /**
     * Tests the conversion of number and string objects to {@link Comparable} objects.
     * Actually this is a test of {@link IdentityConverter}.
     *
     * @throws NonconvertibleObjectException Should not happen.
     */
    @Test
    public void testPrimitiveToComparable() throws NonconvertibleObjectException {
        final ConverterRegistry system = ConverterRegistry.system();
        assertEquals("12", system.converter(String .class, Comparable.class).convert("12"));
        assertEquals(12,   system.converter(Integer.class, Comparable.class).convert(12));
        assertEquals(12.5, system.converter(Double .class, Comparable.class).convert(12.5));
        assertEquals(12,   system.converter(Number .class, Comparable.class).convert(12));
        assertEquals(12.5, system.converter(Number .class, Comparable.class).convert(12.5));
    }

    /**
     * Tests the conversion of miscellaneous objects to {@link Comparable} objects.
     * {@link URL} is not comparable and consequently implies a conversion to an other object.
     *
     * @throws NonconvertibleObjectException Should not happen.
     * @throws URISyntaxException Should not happen.
     * @throws MalformedURLException Should not happen.
     */
    @Test
    public void testFileToComparable() throws NonconvertibleObjectException, URISyntaxException, MalformedURLException {
        final ConverterRegistry system = ConverterRegistry.system();
        final URI uri = new URI("file:/home/user/index.html");
        assertSame  (uri, system.converter(URI.class,  Comparable.class).convert(uri));
        assertEquals(uri, system.converter(URI.class,  Comparable.class).convert(new URI("file:/home/user/index.html")));
        assertEquals(uri, system.converter(URL.class,  Comparable.class).convert(new URL("file:/home/user/index.html")));
    }

    /**
     * Tests the {@link ConverterRegistry#findCommonTarget} method.
     */
    @Test
    public void testFindCommonTarget() {
        final ConverterRegistry system = ConverterRegistry.system();
        assertEquals(Double.class, system.findCommonTarget(Number.class, Long.class, Double.class));
        assertEquals(Short .class, system.findCommonTarget(Number.class, Byte.class, Short .class));
        /*
         * Given two possibility which could be interchanged (Long and Date),
         * should select the one which appear first in the argument list.
         */
        assertEquals(Long.class, system.findCommonTarget(Comparable.class, Long  .class, Date.class));
        assertEquals(Date.class, system.findCommonTarget(Comparable.class, Date  .class, Long.class));
        assertEquals(Long.class, system.findCommonTarget(Comparable.class, Number.class, Date.class));
        // Actually the last test below match current implementation, but this behavior is not
        // necessarily the most appropriate one. We could also return conservatively Double.class
        // since Number can be anything.
        /*
         * Following tests use the fact that URL does not implement Comparable,
         * while File and URI does.
         */
        assertEquals(File.class, system.findCommonTarget(Comparable.class, File.class, URL .class));
        assertEquals(URI .class, system.findCommonTarget(Comparable.class, URI .class, URL .class));
        assertEquals(File.class, system.findCommonTarget(Comparable.class, File.class, URI .class));
        assertEquals(URI .class, system.findCommonTarget(Comparable.class, URI .class, File.class));
    }
}
