/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.internal.jaxb.referencing;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import javax.xml.bind.annotation.XmlElement;

import org.opengis.temporal.Period;
import org.opengis.temporal.Instant;
import org.opengis.temporal.TemporalPrimitive;

import org.geotoolkit.resources.Errors;
import org.geotoolkit.util.logging.Logging;
import org.geotoolkit.factory.FactoryNotFoundException;
import org.geotoolkit.internal.TemporalUtilities;
import org.geotoolkit.internal.jaxb.XmlUtilities;
import org.geotoolkit.internal.jaxb.gco.PropertyType;


/**
 * JAXB adapter for {@link TemporalPrimitive}, in order to integrate the value in an element
 * complying with OGC/ISO standard. Note that the CRS is formatted using the GML schema,
 * not the ISO 19139 one.
 *
 * @author Guilhem Legal (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.00
 * @module
 */
public final class TM_Primitive extends PropertyType<TM_Primitive, TemporalPrimitive> {
    /**
     * Empty constructor for JAXB.
     */
    public TM_Primitive() {
    }

    /**
     * Wraps a Temporal Primitive value at marshalling-time.
     *
     * @param metadata The metadata value to marshall.
     */
    private TM_Primitive(final TemporalPrimitive metadata) {
        super(metadata);
    }

    /**
     * Returns the Vertical CRS value wrapped by a {@code gml:VerticalCRS} tags.
     *
     * @param value The value to marshall.
     * @return The adapter which wraps the metadata value.
     */
    @Override
    protected TM_Primitive wrap(final TemporalPrimitive value) {
        return new TM_Primitive(value);
    }

    /**
     * Returns the GeoAPI interface which is bound by this adapter.
     */
    @Override
    protected Class<TemporalPrimitive> getBoundType() {
        return TemporalPrimitive.class;
    }

    /**
     * Returns the {@link TimePeriod} generated from the metadata value.
     * This method is systematically called at marshalling-time by JAXB.
     *
     * @return The time period, or {@code null}.
     */
    @Override
    @XmlElement(name = "TimePeriod")
    public TimePeriod getElement() {
        if (!skip()) {
            final TemporalPrimitive metadata = this.metadata;
            if (metadata instanceof Period) {
                return new TimePeriod((Period) metadata);
            }
        }
        return null;
    }

    /**
     * Returns the {@link TimeInstant} generated from the metadata value.
     * This method is systematically called at marshalling-time by JAXB.
     *
     * @return The time instant, or {@code null}.
     *
     * @since 3.20
     */
    @XmlElement(name = "TimeInstant")
    public TimeInstant getInstant() {
        if (!skip()) {
            final TemporalPrimitive metadata = this.metadata;
            if (metadata instanceof Instant) {
                return new TimeInstant((Instant) metadata);
            }
        }
        return null;
    }

    /**
     * Sets the value from the {@link TimePeriod}.
     * This method is called at unmarshalling-time by JAXB.
     *
     * @param period The adapter to set.
     */
    public void setElement(final TimePeriod period) {
        metadata = null; // Cleaned first in case of failure.
        if (period != null) {
            final Date begin = toDate(period.begin);
            final Date end   = toDate(period.end);
            if (begin != null || end != null) {
                final LogRecord record;
                if (begin != null && end != null && end.before(begin)) {
                    /*
                     * Be tolerant - we can treat such case as an empty range, which is a similar
                     * approach to what JDK does for Rectangle width and height. We will log with
                     * TemporalPrimitive as the source class, since it is the closest we can get
                     * to a public API.
                     */
                    record = Errors.getResources(null).getLogRecord(Level.WARNING,
                            Errors.Keys.ILLEGAL_RANGE_2, begin, end);
                } else try {
                    metadata = TemporalUtilities.createPeriod(begin, end);
                    period.copyIdTo(metadata);
                    return;
                } catch (FactoryNotFoundException e) {
                    record = TemporalUtilities.createLog(e);
                }
                log(TemporalPrimitive.class, "setTimePeriod", record);
            }
        }
    }

    /**
     * Sets the value from the {@link TimeInstant}.
     * This method is called at unmarshalling-time by JAXB.
     *
     * @param instant The adapter to set.
     *
     * @since 3.20
     */
    public void setInstant(final TimeInstant instant) {
        metadata = null; // Cleaned first in case of failure.
        if (instant != null) {
            final Date position = XmlUtilities.toDate(instant.timePosition);
            if (position != null) try {
                metadata = TemporalUtilities.createInstant(position);
                instant.copyIdTo(metadata);
            } catch (FactoryNotFoundException e) {
                log(TemporalPrimitive.class, "setTimeInstant", TemporalUtilities.createLog(e));
            }
        }
    }

    /**
     * Returns the date of the given bounds, or {@code null} if none.
     */
    private static Date toDate(final TimePeriodBound bound) {
        return (bound != null) ? XmlUtilities.toDate(bound.calendar()) : null;
    }

    /**
     * Logs the given record. This method is invoked in case of failure or warning.
     *
     * @param classe The class to declare in the log record.
     * @param method The name of the method to declare in the log record.
     * @param record The record to log.
     */
    public static void log(final Class<?> classe, final String method, final LogRecord record) {
        record.setSourceClassName(classe.getName());
        record.setSourceMethodName(method);
        record.setLoggerName("org.geotoolkit.xml");
        Logging.getLogger("org.geotoolkit.xml").log(record);
    }
}
