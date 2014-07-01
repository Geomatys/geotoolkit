/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
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
package org.geotoolkit.util.converter;


import java.util.Date;
import java.sql.Time;
import org.apache.sis.util.UnconvertibleObjectException;


/**
 * Converter from Date to Timestamp
 *
 * @module pending
 */
public class DateToTimeConverter extends SimpleConverter<Date, Time>{

    private static final long DAY = 24L * 60 * 60 * 1000;

    @Override
    public Class<Date> getSourceClass() {
        return Date.class;
    }

    @Override
    public Class<Time> getTargetClass() {
        return Time.class;
    }

    @Override
    public Time apply(final Date s) throws UnconvertibleObjectException {
        if (s != null) {
            return new Time(s.getTime() % DAY);
        }
        return null;
    }
}
