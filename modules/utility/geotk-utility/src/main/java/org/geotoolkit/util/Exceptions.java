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

import java.awt.Font;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.Rectangle2D;
import java.sql.SQLException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.ArrayList;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;

import org.geotoolkit.lang.Static;
import org.geotoolkit.io.ExpandedTabWriter;
import org.geotoolkit.resources.Errors;


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
 * @version 3.21
 *
 * @since 2.0
 * @module
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
     * Returns an exception of the same kind and with the same stack trace than the given
     * exception, but with a different message. This method simulates the functionality
     * that we would had if {@link Throwable} defined a {@code setMessage(String)} method.
     * We use this method when an external library throws an exception of the right type,
     * but with too few details.
     * <p>
     * This method try to create a new exception using reflection. The exception class needs
     * to provide a public constructor expecting a single {@link String} argument. If the
     * exception class does not provide such constructor, then the given exception is returned
     * unchanged.
     *
     * @param <T>       The type of the exception.
     * @param exception The exception to copy with a different message.
     * @param message   The message to set in the exception to be returned.
     * @param append    If {@code true}, the existing message in the original exception (if any)
     *                  will be happened after the provided message.
     * @return A new exception with the given message, or the given exception if the exception
     *         class does not provide public {@code Exception(String)} constructor.
     *
     * @since 3.20
     *
     * @deprecated Moved to Apache SIS {@link org.apache.sis.util.Exceptions#setMessage}.
     */
    @Deprecated
    public static <T extends Throwable> T setMessage(final T exception, String message, final boolean append) {
        return org.apache.sis.util.Exceptions.setMessage(exception, message, append);
    }

    /**
     * Returns a string which contain the given message on the first line, followed by the
     * {@linkplain Throwable#getLocalizedMessage() localized message} of the given exception
     * on the next line. If the exception has a {@linkplain Throwable#getCause() causes}, then
     * the localized message of the cause is formatted on the next line and the process is
     * repeated for the whole cause chain.
     * <p>
     * {@link SQLException} is handled especially in order to process the
     * {@linkplain SQLException#getNextException() next exception} instead than the cause.
     * <p>
     * This method does not format the stack trace.
     *
     * @param  header The message to insert on the first line, or {@code null} if none.
     * @param  cause  The exception, or {@code null} if none.
     * @return The formatted message, or {@code null} if both the header was {@code null}
     *         and no exception provide a message.
     *
     * @since 3.01
     *
     * @deprecated Moved to Apache SIS {@link org.apache.sis.util.Exceptions#formatChainedMessages}.
     */
    @Deprecated
    public static String formatChainedMessages(String header, Throwable cause) {
        return org.apache.sis.util.Exceptions.formatChainedMessages(null, header, cause);
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
        exception.printStackTrace(new PrintWriter(new ExpandedTabWriter(writer, TAB_WIDTH)));
        return writer.toString();
    }

    /**
     * Writes the specified exception trace in the specified graphics context. This method is
     * useful when an exception has occurred inside a {@link java.awt.Component#paint} method
     * and we want to write it rather than leaving an empty window.
     *
     * @param graphics Graphics context in which to write exception. The graphics context shall
     *        be in its initial state (default affine transform, default color, <i>etc.</i>)
     * @param widgetBounds Size of the trace which was being drawn.
     * @param exception Exception whose trace we want to write.
     */
    public static void paintStackTrace(final Graphics2D graphics,
                                       final Rectangle  widgetBounds,
                                       final Throwable  exception)
    {
        /*
         * Obtains the exception trace in the form of a character chain.
         * The carriage returns in this chain can be "\r", "\n" or "r\n".
         */
        final String message = formatStackTrace(exception);
        /*
         * Examines the character chain line by line.
         * "Glyphs" will be created as we go along and we will take advantage
         * of this to calculate the necessary space.
         */
        double width = 0, height = 0;
        final List<GlyphVector> glyphs = new ArrayList<>();
        final List<Rectangle2D> bounds = new ArrayList<>();
        final int length = message.length();
        final Font font = graphics.getFont();
        final FontRenderContext context = graphics.getFontRenderContext();
        for (int i = 0; i < length;) {
            int ir = message.indexOf('\r', i);
            int in = message.indexOf('\n', i);
            if (ir < 0) ir = length;
            if (in < 0) in = length;
            final int irn = Math.min(ir, in);
            final GlyphVector line = font.createGlyphVector(context, message.substring(i, irn));
            final Rectangle2D rect = line.getVisualBounds();
            final double w = rect.getWidth();
            if (w > width) width = w;
            height += rect.getHeight();
            glyphs.add(line);
            bounds.add(rect);
            i = (Math.abs(ir - in) <= 1 ? Math.max(ir, in) : irn) + 1;
        }
        /*
         * Proceeds to draw all the previously calculated glyphs.
         */
        float xpos = widgetBounds.x + (float) (0.5 * Math.max(0, widgetBounds.width  - width));
        float ypos = widgetBounds.y + (float) (0.5 * Math.max(0, widgetBounds.height - height));
        final int size = glyphs.size();
        for (int i = 0; i < size; i++) {
            final GlyphVector line = glyphs.get(i);
            final Rectangle2D rect = bounds.get(i);
            ypos += rect.getHeight();
            graphics.drawGlyphVector(line, xpos, ypos);
        }
    }

    /**
     * Displays an error message for the specified exception. Note that this method can
     * be called from any thread (not necessarily the <cite>Swing</cite> thread).
     *
     * @param  owner Component in which the exception is produced, or {@code null} if unknown.
     * @param  exception Exception which has been thrown and is to be reported to the user.
     * @throws UnsupportedOperationException If the {@code geotk-widgets-swing} module is
     *         not found on the classpath.
     *
     * @see org.jdesktop.swingx.JXErrorPane
     * @since 3.18 (derived from 1.0)
     */
    public static void show(final Component owner, final Throwable exception) throws UnsupportedOperationException {
        show(new Object[] {owner, exception});
    }

    /**
     * Displays an error message for the specified exception. Note that this method can
     * be called from any thread (not necessarily the <cite>Swing</cite> thread).
     *
     * @param  owner Component in which the exception is produced, or {@code null} if unknown.
     * @param  exception Exception which has been thrown and is to be reported to the user.
     * @param  message Message to display. If this parameter is {@code null}, then
     *         {@link Exception#getLocalizedMessage} will be called to obtain the message.
     * @throws UnsupportedOperationException If the {@code geotk-widgets-swing} module is
     *         not found on the classpath.
     *
     * @see org.jdesktop.swingx.JXErrorPane
     * @since 3.18 (derived from 1.0)
     */
    public static void show(final Component owner, final Throwable exception, final String message)
            throws UnsupportedOperationException
    {
        show(new Object[] {owner, exception, message});
    }

    /**
     * Delegates to the {@code show} method in the {@code geotk-widgets-swing} module, if present.
     */
    @SuppressWarnings("fallthrough")
    private static void show(final Object[] arguments) throws UnsupportedOperationException {
        final Class<?>[] types = new Class<?>[arguments.length];
        switch (types.length) {
            default: types[2] = String.class;
            case 2:  types[1] = Throwable.class;
            case 1:  types[0] = Component.class;
            case 0:  break;
        }
        try {
            Class.forName("org.geotoolkit.internal.swing.ExceptionMonitor").getMethod("show", types).invoke(null, arguments);
        } catch (ClassNotFoundException e) {
            // This is the expected exception if the widget module is not available.
            throw new UnsupportedOperationException(Errors.format(
                    Errors.Keys.MISSING_MODULE_1, "geotk-widgets-swing"), e);
        } catch (NoSuchMethodException e) {
            // Should never happen, since have control on our implementation.
            throw new AssertionError(e);
        } catch (IllegalAccessException e) {
            // Should never happen, since have control on our implementation.
            throw new AssertionError(e);
        } catch (InvocationTargetException e) {
            final Throwable cause = e.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            }
            if (cause instanceof Error) {
                throw (Error) cause;
            }
            // The invoked method does not declare any checked exception,
            // so if we reach this point we have an unexpected exception.
            throw new UndeclaredThrowableException(cause);
        }
    }
}
