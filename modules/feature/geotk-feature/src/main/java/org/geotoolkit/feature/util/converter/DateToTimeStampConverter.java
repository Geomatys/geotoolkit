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
package org.geotoolkit.feature.util.converter;

import java.util.Date;
import java.sql.Timestamp;
import org.apache.sis.util.UnconvertibleObjectException;


/**
 * Converter from Date to Timestamp
 *
 * @module pending
 */
public class DateToTimeStampConverter extends SimpleConverter<Date, Timestamp>{
    @Override
    public Class<Date> getSourceClass() {
        return Date.class;
    }

    @Override
    public Class<Timestamp> getTargetClass() {
        return Timestamp.class;
    }

    @Override
    public Timestamp apply(final Date s) throws UnconvertibleObjectException {
        if (s != null) {
            return new Timestamp(s.getTime());
        }
        return null;
    }
}
