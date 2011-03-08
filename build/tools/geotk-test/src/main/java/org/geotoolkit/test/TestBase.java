/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2011, Geomatys
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

import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.logging.Level;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Base class of Geotoolkit.org tests. This base class provides some configuration that
 * are commons to all subclasses.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.17
 *
 * @since 3.16
 */
public abstract class TestBase {
    /**
     * {@code true} if {@link #loggingSetup()} has been invoked.
     */
    private static boolean initialized;

    /**
     * Set to {@code true} for sending debugging information to the standard output stream.
     * It is up to subclasses to use this field. If {@code true}, the {@linkplain System#out
     * standard output stream} shall be used for better integration with Maven output at build
     * time.
     */
    protected boolean verbose = false;

    /**
     * Date parser, created when first needed.
     */
    private transient DateFormat dateFormat;

    /**
     * Creates a new test case.
     */
    protected TestBase() {
    }

    /**
     * Configures the logging handler and the logging level to use for the test suite.
     * This method uses reflection for installing the handler provided in Geotk.
     * This method is invoked automatically by JUnit and doesn't need to be invoked explicitely.
     */
    @BeforeClass
    public static synchronized void loggingSetup() {
        if (!initialized) try {
            initialized = true; // Initialize only once even in case of failure.
            final Class<?> logging = Class.forName("org.geotoolkit.util.logging.Logging");
            logging.getMethod("forceMonolineConsoleOutput", Level.class).invoke(
                    logging.getField("GEOTOOLKIT").get(null), new Object[] {null});
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    /**
     * Returns the date format.
     */
    private DateFormat getDateFormat() {
        DateFormat df = dateFormat;
        if (df == null) {
            dateFormat = df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CANADA);
            df.setTimeZone(TimeZone.getTimeZone("UTC"));
            df.setLenient(false);
        }
        return df;
    }

    /**
     * Parses the date for the given string using the {@code "yyyy-MM-dd HH:mm:ss"} pattern
     * in UTC timezone.
     *
     * @param  date The date as a {@link String}.
     * @return The date as a {@link Date}.
     *
     * @since 3.15
     */
    protected final synchronized Date date(final String date) {
        assertNotNull("A date must be specified", date);
        final DateFormat dateFormat = getDateFormat();
        try {
            return dateFormat.parse(date);
        } catch (ParseException e) {
            throw new AssertionError(e);
        }
    }

    /**
     * Formats the given date using the {@code "yyyy-MM-dd HH:mm:ss"} pattern in UTC timezone.
     *
     * @param  date The date to format.
     * @return The date as a {@link String}.
     *
     * @since 3.17
     */
    protected final synchronized String format(final Date date) {
        assertNotNull("A date must be specified", date);
        return getDateFormat().format(date);
    }
}
