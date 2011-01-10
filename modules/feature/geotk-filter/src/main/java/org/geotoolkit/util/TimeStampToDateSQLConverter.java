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
package org.geotoolkit.util;


import java.sql.Date;
import java.sql.Timestamp;
import org.geotoolkit.util.converter.NonconvertibleObjectException;
import org.geotoolkit.util.converter.ObjectConverter;

/**
 * Converter from sql.TimeStamp to sql.Date
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class TimeStampToDateSQLConverter implements ObjectConverter<Timestamp, Date>{

    @Override
    public Class<? super Timestamp> getSourceClass() {
        return Timestamp.class;
    }

    @Override
    public Class<? extends java.sql.Date> getTargetClass() {
        return java.sql.Date.class;
    }

    @Override
    public boolean hasRestrictions() {
        return false;
    }

    @Override
    public boolean isOrderPreserving() {
        return true;
    }

    @Override
    public boolean isOrderReversing() {
        return false;
    }

    @Override
    public Date convert(final Timestamp s) throws NonconvertibleObjectException {
        return new Date(s.getTime());
    }

}
