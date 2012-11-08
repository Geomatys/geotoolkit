/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.util.logging;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests the {@link Logging} class.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.4
 */
public final class LoggingTest {
    /**
     * Tests the redirection to Log4J.
     */
    @Test
    public void testLog4J() {
        assertTrue(Logging.GEOTOOLKIT.getLoggerFactory() instanceof Log4JLoggerFactory);
        Logger logger = Logging.getLogger("org.geotoolkit");
        assertTrue(logger instanceof Log4JLogger);
        /*
         * Tests level setting, ending with OFF in order to avoid
         * polluting the standard output stream with this test.
         */
        final Level oldLevel = logger.getLevel();
        try {
            logger.setLevel(Level.WARNING);
            assertEquals(Level.WARNING, logger.getLevel());
            assertTrue (logger.isLoggable(Level.WARNING));
            assertTrue (logger.isLoggable(Level.SEVERE));
            assertFalse(logger.isLoggable(Level.CONFIG));

            logger.setLevel(Level.FINER);
            assertEquals(Level.FINER, logger.getLevel());
            assertEquals(Level.FINER, logger.getLevel());
            assertTrue (logger.isLoggable(Level.FINER));
            assertTrue (logger.isLoggable(Level.SEVERE));

            logger.setLevel(Level.OFF);
            assertEquals(Level.OFF, logger.getLevel());

            logger.finest ("Message to Log4J at FINEST level.");
            logger.finer  ("Message to Log4J at FINER level.");
            logger.fine   ("Message to Log4J at FINE level.");
            logger.config ("Message to Log4J at CONFIG level.");
            logger.info   ("Message to Log4J at INFO level.");
            logger.warning("Message to Log4J at WARNING level.");
            logger.severe ("Message to Log4J at SEVERE level.");
        } finally {
            logger.setLevel(oldLevel);
        }
    }
}
