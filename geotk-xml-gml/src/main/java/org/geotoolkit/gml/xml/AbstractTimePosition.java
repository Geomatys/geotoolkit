/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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

package org.geotoolkit.gml.xml;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.xml.bind.annotation.XmlTransient;
import java.time.temporal.Temporal;
import java.util.Optional;
import static org.geotoolkit.gml.xml.TimeIndeterminateValueType.AFTER;
import static org.geotoolkit.gml.xml.TimeIndeterminateValueType.BEFORE;
import static org.geotoolkit.gml.xml.TimeIndeterminateValueType.NOW;
import org.geotoolkit.temporal.object.ISODateParser;
import org.opengis.temporal.IndeterminateValue;
import org.opengis.temporal.Instant;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
@XmlTransient
public abstract class AbstractTimePosition implements Instant {

    protected static final Logger LOGGER = Logger.getLogger("org.geotoolkit.gml.xml");

    protected static final List<DateFormat> FORMATTERS = new ArrayList<DateFormat>();

    static {
        FORMATTERS.add(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS"));
        FORMATTERS.add(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"));
        FORMATTERS.add(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        FORMATTERS.add(new SimpleDateFormat("yyyy-MM-dd"));
        FORMATTERS.add(new SimpleDateFormat("yyyy"));
    }

    @Override
    public Temporal getPosition() {
        return org.apache.sis.temporal.TemporalDate.toTemporal(getDate());
    }

    public abstract Date getDate();

    protected Date parseDate(final String value) {
        if (value != null && !value.isEmpty()) {

            //test iso date first
            final ISODateParser parser = new ISODateParser();
            try {
                return parser.parseToDate(value);
            }catch(NumberFormatException ex){
            }
            //fallback types
            for (DateFormat df : FORMATTERS) {
                try {
                    synchronized (df) {
                        return df.parse(value);
                    }
                } catch (ParseException ex) {
                    continue;
                }
            }
            LOGGER.log(Level.WARNING, "Unable to parse date value:{0}", value);
        }
        return null;
    }

    @Override
    public Optional<IndeterminateValue> getIndeterminatePosition() {
        var v = getIndeterminateValue();
        if (v == null) {
            return Optional.empty();
        }
        IndeterminateValue c;
        switch (v) {
            case BEFORE: c = IndeterminateValue.BEFORE;  break;
            case AFTER:  c = IndeterminateValue.AFTER;   break;
            case NOW:    c = IndeterminateValue.NOW;     break;
            default:     c = IndeterminateValue.UNKNOWN; break;
        }
        return Optional.of(c);
    }

    public abstract TimeIndeterminateValueType getIndeterminateValue();

    static AbstractTimePosition of(final Instant instant) {
        return new AbstractTimePosition() {
            @Override public Temporal getPosition() {
                return instant.getPosition();
            }

            @Override public Date getDate() {
                return org.apache.sis.temporal.TemporalDate.toDate(instant.getPosition());
            }

            @Override public TimeIndeterminateValueType getIndeterminateValue() {
                IndeterminateValue v = instant.getIndeterminatePosition().orElse(null);
                if (v == null) {
                    return null;
                }
                TimeIndeterminateValueType c;
                if (v.equals(IndeterminateValue.BEFORE)) c = TimeIndeterminateValueType.BEFORE;
                else if (v.equals(IndeterminateValue.AFTER)) c = TimeIndeterminateValueType.AFTER;
                else if (v.equals(IndeterminateValue.NOW)) c = TimeIndeterminateValueType.NOW;
                else  c = TimeIndeterminateValueType.UNKNOWN;
                return c;
            }
        };
    }
}
