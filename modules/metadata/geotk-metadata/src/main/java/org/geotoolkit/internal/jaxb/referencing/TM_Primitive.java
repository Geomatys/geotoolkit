/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2011, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.internal.jaxb.referencing;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import javax.xml.bind.annotation.XmlElement;

import org.opengis.temporal.Period;
import org.opengis.temporal.TemporalPrimitive;
import org.opengis.temporal.TemporalFactory;

import org.geotoolkit.resources.Errors;
import org.geotoolkit.util.logging.Logging;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.FactoryNotFoundException;
import org.geotoolkit.internal.jaxb.XmlUtilities;
import org.geotoolkit.internal.jaxb.metadata.MetadataAdapter;


/**
 * JAXB adapter for {@link TemporalPrimitive}, in order to integrate the value in an element
 * complying with OGC/ISO standard. Note that the CRS is formatted using the GML schema,
 * not the ISO 19139 one.
 *
 * @author Guilhem Legal (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.18
 *
 * @since 3.00
 * @module
 */
public final class TM_Primitive extends MetadataAdapter<TM_Primitive, TemporalPrimitive> {
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
     * Returns the {@link TemporalPrimitive} generated from the metadata value.
     * This method is systematically called at marshalling-time by JAXB.
     *
     * @return The temporal primitive, or {@code null}.
     *
     * @todo Add other TemporalPrimitive than Period.
     */
    @Override
    @XmlElement(name = "TimePeriod")
    public TimePeriod getElement() {
        if (skip()) return null;
        final TemporalPrimitive metadata = this.metadata;
        if (metadata instanceof Period) {
            return new TimePeriod((Period) metadata);
        }
        return null;
    }

    /**
     * Sets the value from the {@link TemporalPrimitive}.
     * This method is called at unmarshalling-time by JAXB.
     *
     * @param period The adapter to set.
     */
    public void setElement(final TimePeriod period) {
        metadata = null; // Cleaned first in case of failure.
        if (period != null) {
            final Date begin = XmlUtilities.toDate(TimePeriod.select(period.beginPosition, period.begin));
            final Date end   = XmlUtilities.toDate(TimePeriod.select(period.endPosition,   period.end));
            if (begin != null && end != null) {
                final LogRecord record;
                if (end.before(begin)) {
                    /*
                     * Be tolerant - we can treat such case as an empty range, which is a similar
                     * approach to what JDK does for Rectangle width and height. We will log with
                     * TemporalPrimitive as the source class, since it is the closest we can get
                     * to a public API.
                     */
                    record = Errors.getResources(null).getLogRecord(Level.WARNING,
                            Errors.Keys.BAD_RANGE_$2, begin, end);
                } else try {
                    final TemporalFactory factory = FactoryFinder.getTemporalFactory(null);
                    metadata = factory.createPeriod(
                               factory.createInstant(factory.createPosition(begin)),
                               factory.createInstant(factory.createPosition(end)));
                    period.copyIdTo(metadata);
                    return;
                } catch (FactoryNotFoundException e) {
                    record = Errors.getResources(null).getLogRecord(Level.WARNING,
                            Errors.Keys.MISSING_MODULE_$1, "geotk-temporal");
                    record.setThrown(e);
                }
                record.setSourceClassName(TemporalPrimitive.class.getName());
                record.setSourceMethodName("setTimePeriod");
                Logging.getLogger("org.geotoolkit.xml").log(record);
            }
        }
    }
}
