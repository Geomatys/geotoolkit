/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
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

import org.geotoolkit.util.converter.ConverterRegistry;
import org.geotoolkit.util.converter.NonconvertibleObjectException;
import org.geotoolkit.util.converter.ObjectConverter;
import org.geotoolkit.util.logging.Logging;

/**
 * Utility class to converter objects.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public final class Converters {

    protected static final ConverterRegistry CONVERTERS = ConverterRegistry.system();

    private Converters(){}

    public static <T> T convert(Object candidate, Class<T> target) {
        if(candidate == null) return null;
        if(target == null) return (T) candidate;
        return (T) convert(candidate, (Class) candidate.getClass(), target);
    }

    private static <S,T> T convert(S candidate, Class<S> source, Class<T> target) {

        // handle case of source being an instance of target up front
        if (target.isAssignableFrom(source) ) {
            return (T) candidate;
        }

        final ObjectConverter<S,T> converter;
        try {
            converter = CONVERTERS.converter(source, target);
            return converter.convert(candidate);
        } catch (NonconvertibleObjectException ex) {
            Logging.recoverableException(Converters.class, "convert", ex);
            return null;
        }
    }

}
