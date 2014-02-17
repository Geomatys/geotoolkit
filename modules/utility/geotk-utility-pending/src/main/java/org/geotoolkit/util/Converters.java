/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2013, Geomatys
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
import org.apache.sis.util.logging.Logging;

/**
 * Utility class to converter objects.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public final class Converters {

    private static final ConverterRegistry REGISTRY = ConverterRegistry.system();

    private Converters(){}

    /**
     * Try to convert given object to given class.
     * This function do not throw any exception, result will be null if convertion
     * failed.
     * 
     * @param <T> expected class of returned object
     * @param candidate : object to convert, can be null, result will be null.
     * @param targetClass : if null return candidate unchanged
     * @return converted value or null if unconvertible
     */
    public static <T> T convert(final Object candidate, final Class<T> targetClass) {
        if(candidate == null) return null;
        if(targetClass == null) return (T) candidate;
        final Class sourceClass = candidate.getClass();
        if (targetClass.isAssignableFrom(sourceClass) ) {
            return (T) candidate;
        }
        
        final ObjectConverter converter;
        try {
            converter = REGISTRY.converter(sourceClass, targetClass);
            return (T) converter.convert(candidate);
        } catch (NonconvertibleObjectException ex) {
            Logging.recoverableException(Converters.class, "convert", ex);
            return null;
        }
    }

}
