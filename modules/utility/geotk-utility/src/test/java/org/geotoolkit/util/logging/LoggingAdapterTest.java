/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2011, Geomatys
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

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests the {@link LoggingAdapter} class.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.4
 */
public final strictfp class LoggingAdapterTest {
    /**
     * Tests the {@link LoggerAdapter#log(Level,String)} method.
     * This is of special interest because of the switch cases used in implementation.
     */
    @Test
    public void testLog() {
        final DummyLogger logger = new DummyLogger();
        final Object[] levels = new Object[] {
            Level.FINE,    "apple",
            Level.INFO,    "orange",
            Level.FINEST,  "yellow",
            Level.CONFIG,  "yeti",
            Level.SEVERE,  "ouch!",
            Level.WARNING, "caution",
            Level.FINEST,  "don't mind",
        };
        for (int i=0; i<levels.length; i++) {
            final Level  level   = (Level)  levels[i];
            final String message = (String) levels[++i];
            logger.clear();
            logger.log(level, message);
            assertEquals(level, logger.level);
            assertEquals(message, logger.last);
        }
        // Actually, Level.OFF has the highest intValue.
        // LoggerAdapter can easily match this level to a no-op.
        logger.clear();
        logger.log(Level.OFF, "off");
        assertEquals(Level.OFF, logger.level);

        // Actually, Level.ALL has the smallest intValue.
        // LoggerAdapter has no easy match for this level.
        logger.clear();
        logger.log(Level.ALL, "all");
        assertEquals(Level.OFF, logger.level);
    }
}
