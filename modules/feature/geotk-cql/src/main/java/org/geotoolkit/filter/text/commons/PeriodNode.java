/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2006-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.filter.text.commons;

import java.security.InvalidParameterException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.expression.Literal;


/**
 * Period is constructed in the parsing process. this has convenient method to
 * deliver begin and end date of period. a period can be created from
 * date-time/date-time or date-time/duration or duration/date-time
 * <p>
 * Warning: This component is not published. It is part of module implementation.
 * Client module should not use this feature.
 * </p>
 *
 * @module pending
 * @since 2.4
 * @author Mauricio Pazos - Axios Engineering
 * @author Gabriel Roldan - Axios Engineering
 */
public class PeriodNode {
    private final Literal begin;
    private final Literal end;

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");
    
    /**
     * @see create
     *
     * @param begin
     * @param end
     */
    private PeriodNode(final Literal begin, final Literal end) {
        this.begin = begin;
        this.end   = end;
    }

    public static PeriodNode createPeriodDateAndDate(final Literal beginDate, final Literal endDate) {
        return new PeriodNode(beginDate, endDate);
    }

    public static PeriodNode createPeriodDateAndDuration(final Literal date,
        final Literal duration, final FilterFactory filterFactory)
    {
        // compute last date from duration
        // Y M D and H M S
        final Date firstDate = (Date) date.getValue();
        final String strDuration = (String) duration.getValue();

        final Date lastDate = DurationUtil.addDurationToDate(firstDate, strDuration);

        final Literal literalLastDate = filterFactory.literal(lastDate);

        return new PeriodNode(date, literalLastDate);
    }

    public static PeriodNode createPeriodDurationAndDate(final Literal duration,
        final Literal date, final FilterFactory filterFactory)
    {
        // compute first date from duration Y M D and H M S
        final Date lastDate;
        if (date.getValue() instanceof Date) {
            lastDate = (Date) date.getValue();
        } else if (date.getValue() instanceof String) {
            try {
                synchronized(DATE_FORMAT) {
                    lastDate = DATE_FORMAT.parse((String)date.getValue());
                }
            } catch (ParseException ex) {
                throw new InvalidParameterException("parameter must be Literal with Date");
            }
        } else {
            throw new InvalidParameterException("parameter must be Literal with Date");
        }
        final String strDuration = (String) duration.getValue();

        final Date firstDate = DurationUtil.subtractDurationToDate(lastDate, strDuration);

        final Literal literalFirstDate = filterFactory.literal(firstDate);

        return new PeriodNode(literalFirstDate, date);
    }

    /**
     * @return Literal with begining date of period
     */
    public Literal getBeginning() {
        return begin;
    }

    /**
     * @return with ending date of period
     */
    public Literal getEnding() {
        return end;
    }
}
