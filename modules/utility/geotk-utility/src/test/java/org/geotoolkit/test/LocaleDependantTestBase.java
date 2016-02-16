/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2012, Geomatys
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
package org.geotoolkit.test;

import java.util.Locale;
import java.util.TimeZone;
import java.util.ResourceBundle;

import org.junit.*;


/**
 * Base class of tests that are local-dependant. This base class set the default locale
 * to a fixed value ({@link Locale#FRANCE} for now, but it may change in future versions)
 * and restores the default locale after the test.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.17
 *
 * @since 3.17
 */
public abstract strictfp class LocaleDependantTestBase extends TestBase {
    /**
     * The previous locale before the test is run.
     * This is usually the default locale.
     */
    private static Locale defaultLocale;

    /**
     * The previous timezone before the test is run.
     * This is usually the default timezone.
     */
    private static TimeZone defaultTimezone;

    /**
     * Sets a constant locale for the purpose of this test.
     */
    @BeforeClass
    public static void setLocale() {
        defaultLocale = Locale.getDefault();
        Locale.setDefault(Locale.FRANCE);
        defaultTimezone = TimeZone.getDefault();
        TimeZone.setDefault(TimeZone.getTimeZone("CET"));
        ResourceBundle.clearCache();
    }

    /**
     * Restores the locale to its default value after the test.
     */
    @AfterClass
    public static void restoreLocale() {
        Locale.setDefault(defaultLocale);
        TimeZone.setDefault(defaultTimezone);
        ResourceBundle.clearCache();
    }

    /**
     * Creates a new test case.
     */
    protected LocaleDependantTestBase() {
    }
}
