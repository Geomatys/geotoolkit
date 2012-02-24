/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2012, Open Source Geospatial Foundation (OSGeo)
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

import java.util.Locale;

import org.junit.*;
import static org.junit.Assert.*;
import static org.geotoolkit.test.Commons.*;


/**
 * Tests parsing and formatting done by the {@link AngleFormat} class.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.0
 */
public final strictfp class AngleFormatTest {
    /**
     * Tests using {@link Locale#CANADA}.
     */
    @Test
    public void testCanadaLocale() {
        final AngleFormat f = new AngleFormat("DD.ddd°", Locale.CANADA);
        assertEquals( "20.000°",  formatAndParse(f, new Angle   ( 20.000)));
        assertEquals( "20.749°",  formatAndParse(f, new Angle   ( 20.749)));
        assertEquals("-12.247°",  formatAndParse(f, new Angle   (-12.247)));
        assertEquals( "13.214°N", formatAndParse(f, new Latitude( 13.214)));
        assertEquals( "12.782°S", formatAndParse(f, new Latitude(-12.782)));
    }

    /**
     * Tests using {@link Locale#FRANCE}.
     */
    @Test
    public void testFranceLocale() {
        final AngleFormat f = new AngleFormat("DD.ddd°", Locale.FRANCE);
        assertEquals("19,457°E", formatAndParse(f, new Longitude( 19.457)));
        assertEquals("78,124°S", formatAndParse(f, new Latitude (-78.124)));
    }

    /**
     * Tests with no decimal separator.
     */
    @Test
    public void testNoSeparator() {
        final AngleFormat f = new AngleFormat("DDddd", Locale.CANADA);
        assertEquals("19457E", formatAndParse(f, new Longitude( 19.457)));
        assertEquals("78124S", formatAndParse(f, new Latitude (-78.124)));
    }

    /**
     * Tests with the degree separator.
     */
    @Test
    public void testDegreeSeparator() {
        final AngleFormat f = new AngleFormat("DD°MM.m", Locale.CANADA);
        assertEquals( "12°30.0", formatAndParse(f, new Angle( 12.50)));
        assertEquals("-10°15.0", formatAndParse(f, new Angle(-10.25)));
    }
}
