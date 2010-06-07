/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2001-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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
package org.geotoolkit.internal;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.Rectangle2D;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.UIManager;

import org.geotoolkit.lang.Static;
import org.geotoolkit.io.ExpandedTabWriter;
import org.geotoolkit.util.logging.Logging;


/**
 * A set of utilities methods for painting in a {@link Graphics2D} handle.
 * Method in this class was used to be in {@link org.geotoolkit.gui.swing.ExceptionMonitor}.
 * We had to extract them in a separated class in order to avoid dependencies of renderer
 * module toward the GUI one, especially since the extracted methods are not Swing specific.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.12
 *
 * @since 2.0
 * @module
 */
@Static
public final class GraphicsUtilities {
    /**
     * Number of spaces to leave between each tab.
     */
    private static final int TAB_WIDTH = 4;

    /**
     * The creation of {@code GraphicsUtilities} class objects is forbidden.
     */
    private GraphicsUtilities() {
    }

    /**
     * Writes the specified exception trace in the specified graphics
     * context.  This method is useful when an exception has occurred
     * inside a {@link java.awt.Component#paint} method and we want to
     * write it rather than leaving an empty window.
     *
     * @param exception Exception whose trace we want to write.
     * @param graphics Graphics context in which to write exception.  The
     *        graphics context should be in its initial state (default affine
     *        transform, default colour, etc...)
     * @param widgetBounds Size of the trace which was being drawn.
     */
    public static void paintStackTrace(final Graphics2D graphics,
                                       final Rectangle  widgetBounds,
                                       final Throwable  exception)
    {
        /*
         * Obtains the exception trace in the form of a character chain.
         * The carriage returns in this chain can be "\r", "\n" or "r\n".
         */
        final String message = printStackTrace(exception);
        /*
         * Examines the character chain line by line.
         * "Glyphs" will be created as we go along and we will take advantage
         * of this to calculate the necessary space.
         */
        double width = 0, height = 0;
        final List<GlyphVector> glyphs = new ArrayList<GlyphVector>();
        final List<Rectangle2D> bounds = new ArrayList<Rectangle2D>();
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
        float xpos = (float) (0.5 * (widgetBounds.width - width));
        float ypos = (float) (0.5 * (widgetBounds.height - height));
        final int size = glyphs.size();
        for (int i = 0; i < size; i++) {
            final GlyphVector line = glyphs.get(i);
            final Rectangle2D rect = bounds.get(i);
            ypos += rect.getHeight();
            graphics.drawGlyphVector(line, xpos, ypos);
        }
    }

    /**
     * Returns an exception trace. All tabs will have been replaced by 4 white spaces.
     * This method was used to be a private one in {@link org.geotoolkit.gui.swing.ExceptionMonitor}.
     * Do not rely on it.
     *
     * @param exception The exception for which to get the stack trace.
     * @return The stack trace with tab replaced by spaces.
     */
    public static String printStackTrace(final Throwable exception) {
        final StringWriter writer = new StringWriter();
        exception.printStackTrace(new PrintWriter(new ExpandedTabWriter(writer, TAB_WIDTH)));
        return writer.toString();
    }

    /**
     * Sets the Swing Look and Feel to the default value used in Geotk. This method exists
     * in order to have a central place where this setting can be performed, so we can change
     * the setting in a consistent fashion for the whole library.
     *
     * @param caller The class calling this method. Used only for logging purpose.
     * @param method The method invoking this one.  Used only for logging purpose.
     */
    public static void setLookAndFeel(final Class<?> caller, final String method) {
        String laf = System.getProperty("swing.defaultlaf"); // Documented in UIManager.
        if (laf != null) {
            if (laf.equalsIgnoreCase("Nimbus")) {
                laf = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
            } else {
                // Do not change the user-supplied setting.
                return;
            }
        } else if (OS.MAC_OS.equals(OS.current())) {
            // MacOS come with a default L&F which is different than in standard JDK.
            return;
        } else {
            laf = UIManager.getSystemLookAndFeelClassName();
        }
        try {
            UIManager.setLookAndFeel(laf);
        } catch (Exception e) {
            Logging.recoverableException(caller, method, e);
        }
    }
}
