/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2001-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.util;

import java.awt.Graphics2D;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.IOException;

import org.geotoolkit.lang.Static;
import org.apache.sis.io.LineAppender;


/**
 * Utilities methods for dealing with exceptions. Those methods can reformat the stack
 * trace for console output, paint the stack trace in a {@link Graphics2D} handler, or show
 * the stack trace in a <cite>Swing</cite> component.
 * <p>
 * The <cite>Swing</cite> component is available only if the {@code geotk-widgets-swing} module
 * is available in the classpath. It looks like the following picture:
 *
 * <p>&nbsp;</p>
 * <p align="center"><img src="../gui/swing/doc-files/ExceptionMonitor.png"></p>
 * <p>&nbsp;</p>
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Johann Sorel (Geomatys)
 *
 * @see org.apache.sis.util.Exceptions
 */
public final class Exceptions extends Static {
    /**
     * Number of spaces to leave between each tab.
     */
    private static final int TAB_WIDTH = 4;

    /**
     * Do not allow instantiation of this class.
     */
    private Exceptions() {
    }

    /**
     * Returns {@code true} if the given message is non-null and holds a reliable information.
     * The message shall not be null, empty (ignoring white spaces) or equals to the string
     * {@code "null"} (ignoring case).
     * <p>
     * This method is intended for use in assertions like below:
     *
     * {@preformat java
     *     assert Exceptions.isValidMessage(message);
     *     throw new SomeException(message);
     * }
     *
     * @param message The message argument to check.
     * @return {@code true} if the message is informative, or {@code false} otherwise.
     *
     * @since 3.21
     */
    public static boolean isValidMessage(String message) {
        if (message == null) {
            return false;
        }
        message = message.trim();
        return !message.isEmpty() && !message.equalsIgnoreCase("null");
    }

    /**
     * Returns the exception trace as a string. This method get the stack trace using the
     * {@link Throwable#printStackTrace(PrintWriter)} method, then replaces the tabulation
     * characters by 4 white spaces.
     *
     * @param exception The exception to format.
     * @return A string representation of the given exception.
     */
    public static String formatStackTrace(final Throwable exception) {
        final StringWriter writer = new StringWriter();
        exception.printStackTrace(new PrintWriter(writer));
        final StringBuilder buffer = new StringBuilder();
        final LineAppender formatter = new LineAppender(buffer);
        formatter.setTabulationWidth(TAB_WIDTH);
        try {
            formatter.append(writer.toString());
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        return buffer.toString();
    }
}
