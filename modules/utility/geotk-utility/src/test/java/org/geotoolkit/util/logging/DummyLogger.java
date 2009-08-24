/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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


/**
 * A dummy implementation of {@link LoggingAdapter} class for testing purpose.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.4
 */
final class DummyLogger extends LoggerAdapter {
    /**
     * The level of the last logging event.
     */
    Level level;

    /**
     * The last logged message.
     */
    String last;

    /**
     * Creates a dummy logger.
     */
    DummyLogger() {
        super("org.geotoolkit.util.logging");
        clear();
    }

    /**
     * Clears the logger state, for testing purpose only.
     */
    public void clear() {
        level = Level.OFF;
        last  = null;
    }

    @Override
    public void setLevel(Level level) {
        this.level = level;
    }

    @Override
    public Level getLevel() {
        return level;
    }

    @Override
    public boolean isLoggable(Level level) {
        return level.intValue() > this.level.intValue();
    }

    @Override
    public void severe(String message) {
        level = Level.SEVERE;
        last  = message;
    }

    @Override
    public void warning(String message) {
        level = Level.WARNING;
        last  = message;
    }

    @Override
    public void info(String message) {
        level = Level.INFO;
        last  = message;
    }

    @Override
    public void config(String message) {
        level = Level.CONFIG;
        last  = message;
    }

    @Override
    public void fine(String message) {
        level = Level.FINE;
        last  = message;
    }

    @Override
    public void finer(String message) {
        level = Level.FINER;
        last  = message;
    }

    @Override
    public void finest(String message) {
        level = Level.FINEST;
        last  = message;
    }
}
