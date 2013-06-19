/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2003-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.gui.swing;

import java.awt.Color;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.geotoolkit.test.gui.SwingTestBase;
import org.apache.sis.util.logging.Logging;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests the {@link LoggingPanel}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.02
 *
 * @since 3.01
 */
public final strictfp class LoggingPanelTest extends SwingTestBase<LoggingPanel> {
    /**
     * The logger to use for the test.
     */
    private final Logger logger;

    /**
     * Constructs the test case.
     */
    public LoggingPanelTest() {
        super(LoggingPanel.class);
        logger = Logging.getLogger(LoggingPanelTest.class);
        if (isDisplayEnabled()) {
            logger.setLevel(Level.ALL);
        }
    }

    /**
     * Tests fetching the foreground and background colors. This is a convenient
     * way to test that the insertion of colors, and the search of colors for a
     * given level, have been performed correctly.
     */
    @Test
    public void testGetColors() {
        final LoggingPanel test = new LoggingPanel(logger);
        assertNull  (              test.getBackground(Level.FINE));
        assertEquals(Color.GRAY,   test.getForeground(Level.FINE));
        assertNull  (              test.getBackground(Level.CONFIG));
        assertNull  (              test.getForeground(Level.CONFIG));
        assertNull  (              test.getBackground(Level.INFO));
        assertNull  (              test.getForeground(Level.INFO));
        assertEquals(Color.YELLOW, test.getBackground(Level.WARNING));
        assertNull  (              test.getForeground(Level.WARNING));
        assertEquals(Color.RED,    test.getBackground(Level.SEVERE));
        assertEquals(Color.WHITE,  test.getForeground(Level.SEVERE));
    }

    /**
     * Creates the widget.
     */
    @Override
    protected LoggingPanel create(final int index) {
        final LoggingPanel test = new LoggingPanel(logger);
        if (true) {
            test.setColumnVisible(LoggingPanel.Column.LOGGER, false);
            test.setColumnVisible(LoggingPanel.Column.CLASS,  false);
            test.setColumnVisible(LoggingPanel.Column.METHOD, false);
        }
        test.setCapacity(100);
        return test;
    }

    /**
     * Displays some dummy logging messages in the panel.
     *
     * @throws Exception If an exception occurred while creating the widget.
     */
    @Test
    @Override
    public void display() throws Exception {
        super.display();
        if (!isDisplayEnabled()) {
            return;
        }
        // Increase the 20 value to 200 if you want to test buffer rolling.
        for (int i=0; i<20; i++) {
            String message = "Message #" + i;
            if ((i % 3) == 0) {
                message += "\nThis is a multiline message" +
                           "\nSo we put yet an other line.";
            }
            switch (i) {
                case  8: // fall through
                case 12: // fall through
                case 18: logger.finest (message); break;
                case 14: logger.finer  (message); break;
                case 17: logger.fine   (message); break;
                case  3: logger.config (message); break;
                case  5: // fall through
                case 15: logger.warning(message); break;
                case 10: logger.severe (message); break;
                default: logger.info   (message); break;
            }
            Thread.sleep(500);
        }
    }
}
