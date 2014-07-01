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

import java.sql.Date;
import org.geotoolkit.temporal.object.TemporalUtilities;
import org.apache.sis.util.UnconvertibleObjectException;

/**
 * Converter from String to Date
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class StringToDateSQLConverter extends SimpleConverter<String, Date>{
    @Override
    public Class<String> getSourceClass() {
        return String.class;
    }

    @Override
    public Class<Date> getTargetClass() {
        return Date.class;
    }

    @Override
    public Date apply(final String s) throws UnconvertibleObjectException {
        return new Date(TemporalUtilities.parseDateSafe(s, true).getTime());
    }
}
