/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2013, Geomatys
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
package org.geotoolkit.internal.jaxb.gts;

import java.math.BigDecimal;
import java.math.BigInteger;
import javax.xml.datatype.Duration;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.bind.annotation.XmlElement;

import org.opengis.temporal.PeriodDuration;
import org.opengis.util.InternationalString;

import org.geotoolkit.factory.FactoryNotFoundException;
import org.geotoolkit.internal.TemporalUtilities;
import org.geotoolkit.internal.jaxb.XmlUtilities;
import org.geotoolkit.internal.jaxb.gco.PropertyType;
import org.geotoolkit.util.SimpleInternationalString;

import static org.geotoolkit.internal.jaxb.referencing.TM_Primitive.log;


/**
 * Wraps a {@code gts:TM_PeriodDuration} element.
 *
 * @author Guilhem Legal (Geomatys)
 */
public final class TM_PeriodDuration extends PropertyType<TM_PeriodDuration, PeriodDuration> {
    /**
     * Empty constructor for JAXB.
     */
    public TM_PeriodDuration() {
    }

    /**
     * Wraps a Temporal Period Duration value at marshalling-time.
     *
     * @param metadata The metadata value to marshall.
     */
    private TM_PeriodDuration(final PeriodDuration metadata) {
        super(metadata);
    }

    /**
     * Returns the Period Duration value wrapped by a {@code gts:TM_PeriodDuration} tags.
     *
     * @param value The value to marshall.
     * @return The adapter which wraps the metadata value.
     */
    @Override
    protected TM_PeriodDuration wrap(final PeriodDuration value) {
        return new TM_PeriodDuration(value);
    }

    /**
     * Returns the GeoAPI interface which is bound by this adapter.
     */
    @Override
    protected Class<PeriodDuration> getBoundType() {
        return PeriodDuration.class;
    }

    /**
     * Returns the {@link Duration} generated from the metadata value.
     * This method is systematically called at marshalling time by JAXB.
     *
     * @return The time period, or {@code null}.
     */
    @Override
    @XmlElement(name = "TM_PeriodDuration")
    public Duration getElement() {
        if (!skip()) try {
            /*
             * Get the DatatypeFactory first because if not available, then we don't need to parse
             * the calendar fields. This has the side effect of not validating the calendar fields
             * syntax (which should be integer values), but maybe this is what the user wants.
             */
            final DatatypeFactory factory = XmlUtilities.getDatatypeFactory();
            final PeriodDuration metadata = this.metadata;
            InternationalString value;
            BigInteger years = null;
            if ((value = metadata.getYears()) != null) {
                years = new BigInteger(value.toString());
            }
            BigInteger months = null;
            if ((value = metadata.getMonths()) != null) {
                months = new BigInteger(value.toString());
            }
            BigInteger days = null;
            if ((value = metadata.getDays()) != null) {
                days = new BigInteger(value.toString());
            }
            BigInteger hours = null;
            if ((value = metadata.getHours()) != null) {
                hours = new BigInteger(value.toString());
            }
            BigInteger minutes = null;
            if ((value = metadata.getMinutes()) != null) {
                minutes = new BigInteger(value.toString());
            }
            BigDecimal seconds = null;
            if ((value = metadata.getSeconds()) != null) {
                seconds = new BigDecimal(value.toString());
            }
            return factory.newDuration(true, years, months, days, hours, minutes, seconds);
        } catch (FactoryNotFoundException e) {
            log(TM_PeriodDuration.class, "getElement", TemporalUtilities.createLog(e));
        }
        return null;
    }

    /**
     * Sets the value from the {@link Duration}.
     * This method is called at unmarshalling time by JAXB.
     *
     * @param duration The adapter to set.
     */
    public void setElement(final Duration duration) {
        metadata = null; // Cleaned first in case of failure.
        if (duration != null) {
            InternationalString years = null;
            int value;
            if ((value = duration.getYears()) != 0) {
                years = new SimpleInternationalString(Integer.toString(value));
            }
            InternationalString months = null;
            if ((value = duration.getMonths()) != 0) {
                months = new SimpleInternationalString(Integer.toString(value));
            }
            InternationalString weeks = null; // no weeks in javax.xml.datatype.Duration
            InternationalString days = null;
            if ((value = duration.getDays()) != 0) {
                days = new SimpleInternationalString(Integer.toString(value));
            }
            InternationalString hours = null;
            if ((value = duration.getHours()) != 0) {
                hours = new SimpleInternationalString(Integer.toString(value));
            }
            InternationalString minutes = null;
            if ((value = duration.getMinutes()) != 0) {
                minutes = new SimpleInternationalString(Integer.toString(value));
            }
            InternationalString seconds = null;
            if ((value = duration.getSeconds()) != 0) {
                seconds = new SimpleInternationalString(Integer.toString(value));
            }
            try {
                metadata = TemporalUtilities.createPeriodDuration(years, months, weeks, days, hours, minutes, seconds);
            } catch (FactoryNotFoundException e) {
                log(TM_PeriodDuration.class, "setElement", TemporalUtilities.createLog(e));
            }
        }
    }
}
