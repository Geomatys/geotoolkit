/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.gml;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import org.geotoolkit.gml.xml.AbstractTimePosition;
import org.geotoolkit.gml.xml.v311.TimeInstantType;
import org.geotoolkit.gml.xml.v311.TimePeriodType;
import org.geotoolkit.gml.xml.v311.TimePositionType;
import org.geotoolkit.gts.xml.PeriodDurationType;
import org.geotoolkit.temporal.factory.DefaultTemporalFactory;
import org.opengis.temporal.Instant;
import org.opengis.temporal.Period;
import org.opengis.temporal.PeriodDuration;
import org.opengis.util.InternationalString;

/**
 *
 * @author Guilhem Legal (Geomatys)
 * @module
 */
public class GMLTemporalFactory extends DefaultTemporalFactory {

    @Override
    protected void setOrdering(Organizer orgnzr) {
        orgnzr.before(DefaultTemporalFactory.class, false);
    }
    
    @Override
    public Instant createInstant(final Date pstn) {
        if (pstn != null) {
            return new TimeInstantType(createPosition(pstn));
        }
        return null;
    }

    @Override
    public Period createPeriod(final Instant begin, final Instant end) {
       Date beginPosition = null;
       if (begin != null) {
            beginPosition = begin.getDate();
       }
       Date endPosition = null;
       if (end != null) {
            endPosition = end.getDate();
       }
       return new TimePeriodType(createInstant(beginPosition), createInstant(endPosition));
    }

//    @Override
    public AbstractTimePosition createPosition(final Date date) {
        if (date != null) {
            return new TimePositionType(date);
        }
        return null;
    }

    @Override
    public PeriodDuration createPeriodDuration(final InternationalString years, final InternationalString months, 
        final InternationalString week, final InternationalString days, final InternationalString hours, 
        final InternationalString minutes, final InternationalString seconds) {
        BigInteger iyears = null;
        if (years != null) {
            iyears = new BigInteger(years.toString());
        }
        BigInteger imonths = null;
        if (months != null) {
            imonths = new BigInteger(months.toString());
        }
        BigInteger idays = null;
        if (days != null) {
            idays = new BigInteger(days.toString());
        }
        BigInteger ihours = null;
        if (hours != null) {
            ihours = new BigInteger(hours.toString());
        }
        BigInteger iminutes = null;
        if (minutes != null) {
            iminutes = new BigInteger(minutes.toString());
        }
        BigDecimal iseconds = null;
        if (seconds != null) {
            iseconds = new BigDecimal(seconds.toString());
        }
        return new PeriodDurationType(true, iyears, imonths, idays, ihours, iminutes, iseconds);
    }
    
    
}
