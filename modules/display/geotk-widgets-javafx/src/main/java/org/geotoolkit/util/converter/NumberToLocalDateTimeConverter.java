/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import org.apache.sis.util.UnconvertibleObjectException;
import org.geotoolkit.feature.util.converter.SimpleConverter;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class NumberToLocalDateTimeConverter extends SimpleConverter<Double, LocalDateTime> {

    @Override
    public Class<Double> getSourceClass() {
        return Double.class;
    }

    @Override
    public Class<LocalDateTime> getTargetClass() {
        return LocalDateTime.class;
    }

    @Override
    public LocalDateTime apply(Double object) throws UnconvertibleObjectException {
        final Instant instant = Instant.ofEpochMilli(object.longValue());
        return LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
    }


}
