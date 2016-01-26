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

import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.logging.Logger;
import java.util.logging.Handler;
import java.util.logging.ConsoleHandler;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.io.Console;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Base class of Geotoolkit.org tests. This base class provides some configuration that
 * are commons to all subclasses.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.18
 *
 * @since 3.16
 */
public abstract strictfp class TestBase {
    /**
     * The separator characters used for reporting the verbose output.
     */
    private static final String SEPARATOR = "▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬";

    /**
     * The name of a system property for setting whatever the tests should provide verbose output.
     * If the value returned by the following is {@code true}, then the {@link #out} field will be
     * set to a non-null value:
     *
     * {@preformat java
     *     Boolean.getBoolean(VERBOSE_KEY);
     * }
     *
     * The value of this property key is {@value}.
     *
     * @see org.geotoolkit.test.gui.SwingTestBase#SHOW_PROPERTY_KEY
     *
     * @since 3.18
     */
    public static final String VERBOSE_KEY = "org.geotoolkit.test.verbose";

    /**
     * The name of a system property for setting the encoding of test output.
     * This property is used only if the {@link #VERBOSE_KEY} property is set
     * to "{@code true}". If this property is not set, then the system encoding
     * will be used.
     *
     * @since 3.18
     */
    public static final String ENCODING_KEY = "org.geotoolkit.test.encoding";

    /**
     * If verbose output are enabled, the output stream where to print the output.
     * Otherwise {@code null}.
     *
     * @since 3.18
     */
    protected static final PrintWriter out;

    /**
     * The buffer which is backing the {@linkplain #out} stream, or {@code null} if none.
     */
    private static final StringWriter buffer;

    /**
     * Configures the logging handler and the logging level to use for the test suite.
     */
    static {
        if (Boolean.getBoolean(VERBOSE_KEY)) {
            out = new PrintWriter(buffer = new StringWriter());
        } else {
            buffer = null;
            out = null;
        }
        /*
         * Now set the encoding of console output, if it was specified. Note that we look
         * specifically for ConsoleHandler; we do not generalize to StreamHandler because
         * the log files may not be intended for being show in the console.
         *
         * In case of failure to use the given encoding, we will just print a short error
         * message and left the encoding unchanged.
         */
        final String encoding = System.getProperty(ENCODING_KEY);
        if (encoding != null) try {
            for (Logger logger=Logger.getLogger("org.geotoolkit"); logger!=null; logger=logger.getParent()) {
                for (final Handler handler : logger.getHandlers()) {
                    if (handler instanceof ConsoleHandler) {
                        ((ConsoleHandler) handler).setEncoding(encoding);
                    }
                }
                if (!logger.getUseParentHandlers()) {
                    break;
                }
            }
        } catch (UnsupportedEncodingException e) {
            System.err.println(e);
        }
    }

    /**
     * Installs the customized validators defined in the {@link org.geotoolkit.test.validator}
     * package. Those validators ensures that ISO or GeoAPI restrictions apply, then checks for
     * yet more restrictive Geotk conditions. For example Geotk requires the exact same instance
     * where GeoAPI requires only instances that are {@linkplain Object#equals(Object) equal}.
     */
    static {
        final Class<?> c = org.geotoolkit.test.validator.Validators.class;
        try {
            // Force class initialization.
            assertSame(c, Class.forName(c.getName(), true, c.getClassLoader()));
        } catch (ClassNotFoundException e) {
            throw new AssertionError(e); // Should never happen.
        }
    }

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
     * If verbose output were enabled, flush the {@link #out} stream to the console.
     * This method is invoked automatically by JUnit and doesn't need to be invoked
     * explicitely.
     */
    @AfterClass
    public static void flushVerboseOutput() {
        System.out.flush();
        System.err.flush();
        if (out != null) {
            out.flush();
            /*
             * Get the text content and remove the trailing spaces
             * (including line feeds), if any.
             */
            String content = buffer.toString();
            int length = content.length();
            do if (length == 0) return;
            while (Character.isWhitespace(content.charAt(--length)));
            content = content.substring(0, ++length);
            /*
             * Get the output writer, using the specified encoding if any.
             */
            PrintWriter writer = null;
            final String encoding = System.getProperty(ENCODING_KEY);
            if (encoding == null) {
                final Console console = System.console();
                if (console != null) {
                    writer = console.writer();
                }
            }
            if (writer == null) {
                if (encoding != null) try {
                    writer = new PrintWriter(new OutputStreamWriter(System.out, encoding));
                } catch (UnsupportedEncodingException e) {
                    // Ignore. We will use the default encoding.
                }
                if (writer == null) {
                    writer = new PrintWriter(System.out);
                }
            }
            writer.println(SEPARATOR);
            writer.println(content);
            writer.println(SEPARATOR);
            writer.flush();
            buffer.getBuffer().setLength(0);
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
