/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2011, Geomatys
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


/**
 * Redirects logging to two loggers. This is used only when more than one {@link LoggerFactory}
 * is found on the classpath. This should never happen, but if it happen anyway we will send the
 * log records to all registered loggers in order to have a behavior slightly more determinist
 * than picking an arbitrary logger.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.20
 * @module
 */
final class DualLogger extends LoggerAdapter {
    /**
     * The two loggers.
     */
    private final Logger first, second;

    /**
     * Creates a new logger which will redirects the event to the two specified loggers.
     */
    DualLogger(final String name, final Logger first, final Logger second) {
        super(name);
        this.first  = first;
        this.second = second;
    }

    /**
     * Set the level for the two loggers.
     */
    @Override
    public void setLevel(final Level level) {
        first .setLevel(level);
        second.setLevel(level);
    }

    /**
     * Returns the finest level from the two loggers.
     */
    @Override
    public Level getLevel() {
        final Level v1 = first .getLevel();
        final Level v2 = second.getLevel();
        return (v1.intValue() < v2.intValue()) ? v1 : v2;
    }

    /**
     * Returns {@code true} if the specified level is loggable by at least one logger.
     */
    @Override
    public boolean isLoggable(final Level level) {
        return first.isLoggable(level) || second.isLoggable(level);
    }

    /**
     * Logs a record at the specified level.
     */
    @Override
    public void log(final Level level, final String message) {
        first .log(level, message);
        second.log(level, message);
    }

    /**
     * Logs a record at the specified level.
     */
    @Override
    public void log(final Level level, final String message, final Throwable thrown) {
        first .log(level, message, thrown);
        second.log(level, message, thrown);
    }

    @Override public void severe (String message) {first.severe (message); second.severe (message);}
    @Override public void warning(String message) {first.warning(message); second.warning(message);}
    @Override public void info   (String message) {first.info   (message); second.info   (message);}
    @Override public void config (String message) {first.config (message); second.config (message);}
    @Override public void fine   (String message) {first.fine   (message); second.fine   (message);}
    @Override public void finer  (String message) {first.finer  (message); second.finer  (message);}
    @Override public void finest (String message) {first.finest (message); second.finest (message);}
}
