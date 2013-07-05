/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotoolkit.wps.converters;

import java.util.Map;
import java.util.logging.Logger;
import org.geotoolkit.resources.Errors;
import org.apache.sis.util.Classes;
import org.geotoolkit.util.converter.NonconvertibleObjectException;
import org.apache.sis.util.logging.Logging;

/**
 * Default abstract class for {@link WPSObjectConverter}.
 * @author Quentin Boileau (Geomatys).
 */
public abstract class WPSDefaultConverter<S, T> implements WPSObjectConverter<S, T> {

    protected static final Logger LOGGER = Logging.getLogger(WPSObjectConverter.class);
    
    /**
     * Default constructor.
     */
    protected WPSDefaultConverter() {
    }
    
   /**
     * Returns {@code false} by default, assuming that this converter does not have any
     * restriction. Subclasses may override.
     *
     * @return {@code true} if this converter accepts only a subset of source values.
     */
    @Override
    public boolean hasRestrictions() {
        return false;
    }

    /**
     * Returns {@code true} by default, assuming this converter preserves order.
     * Subclasses may override.
     *
     * @return {@code true} if this converter preserve order.
     */
    @Override
    public boolean isOrderPreserving() {
        return true;
    }

    /**
     * Returns {@code false} by default, assuming this converter preserves order.
     * Subclasses may override.
     *
     * @return {@code true} if this converter reverse order.
     */
    @Override
    public boolean isOrderReversing() {
        return false;
    }

    /**
     * Formats an error message for a value that can't be converted.
     *
     * @param  name  The parameter name.
     * @param  value The parameter value.
     * @param  cause The cause for the failure, or {@code null} if none.
     * @return The error message.
     */
    static String formatErrorMessage(final String name, final Object value, final Exception cause) {
        String message = Errors.format(Errors.Keys.ILLEGAL_ARGUMENT_2, name, value);
        if (cause != null) {
            final String cm = cause.getLocalizedMessage();
            if (cm != null) {
                message = message + System.getProperty("line.separator", "\n") + cm;
            }
        }
        return message;
    }

    /**
     * Returns a string representation of this converter for debugging purpose.
     */
    @Override
    public String toString() {
        return Classes.getShortClassName(this) + '[' + getSourceClass().getSimpleName() +
                "\u00A0\u21E8\u00A0" + getTargetClass().getSimpleName() + ']';
    }
    
    /**
     * No used for wps converters. 
     * Use {@link WPSObjectConverter#convert(java.lang.Object, java.util.Map) } instead.
     * 
     * @param source
     * @return
     * @throws NonconvertibleObjectException 
     */
    @Override
    public final T convert(S source) throws NonconvertibleObjectException {
        throw new UnsupportedOperationException("Operation not allowed. Use ObjectConverter.convert(source, parameters) instead.");
    }
}
