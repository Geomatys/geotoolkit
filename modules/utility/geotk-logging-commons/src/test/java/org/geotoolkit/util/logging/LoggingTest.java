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
import org.apache.sis.util.logging.Logging;

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
     * Tests the redirection to Commons-logging.
     */
    @Test
    public void testCommonsLogging() {
        assertTrue(Logging.getLoggerFactory() instanceof CommonsLoggerFactory);
        Logger logger = Logging.getLogger("org.geotoolkit");
        assertTrue(logger instanceof CommonsLogger);
        /*
         * Tests level setting, ending with OFF in order to avoid
         * polluting the standard output stream with this test.
         */
        final org.apache.log4j.Logger log4j = org.apache.log4j.Logger.getLogger("org.geotoolkit");
        final org.apache.log4j.Level oldLevel = log4j.getLevel();

        log4j.setLevel(org.apache.log4j.Level.WARN);
        assertEquals(Level.WARNING, logger.getLevel());
        assertTrue (logger.isLoggable(Level.WARNING));
        assertTrue (logger.isLoggable(Level.SEVERE));
        assertFalse(logger.isLoggable(Level.CONFIG));

        log4j.setLevel(org.apache.log4j.Level.DEBUG);
        assertEquals(Level.FINE, logger.getLevel());
        assertTrue(logger.isLoggable(Level.FINE));
        assertTrue(logger.isLoggable(Level.SEVERE));

        log4j.setLevel(org.apache.log4j.Level.OFF);
        assertEquals(Level.OFF, logger.getLevel());

        logger.finest ("Message to Commons-logging at FINEST level.");
        logger.finer  ("Message to Commons-logging at FINER level.");
        logger.fine   ("Message to Commons-logging at FINE level.");
        logger.config ("Message to Commons-logging at CONFIG level.");
        logger.info   ("Message to Commons-logging at INFO level.");
        logger.warning("Message to Commons-logging at WARNING level.");
        logger.severe ("Message to Commons-logging at SEVERE level.");
        log4j.setLevel(oldLevel);
    }
}
