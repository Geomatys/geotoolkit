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

import java.sql.Timestamp;

import org.geotoolkit.temporal.object.TemporalUtilities;
import org.apache.sis.util.UnconvertibleObjectException;

/**
 * Converter from String to Date
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class StringToTimeStampConverter extends SimpleConverter<String, Timestamp>{

    @Override
    public Class<String> getSourceClass() {
        return String.class;
    }

    @Override
    public Class<Timestamp> getTargetClass() {
        return Timestamp.class;
    }

    @Override
    public Timestamp apply(final String s) throws UnconvertibleObjectException {
        if (s != null && !s.isEmpty()) {
            return new Timestamp(TemporalUtilities.parseDateSafe(s, true).getTime());
        }
        return null;
    }
}
