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
package org.geotoolkit.wps.converters;

import java.util.Map;
import org.geotoolkit.util.converter.NonconvertibleObjectException;
import org.geotoolkit.util.converter.ObjectConverter;

/**
 * Interface that extend ObjectConverter interface for add a convert method with a Map of parameters used for the conversion.
 *
 * @see ObjectConverter
 * @author Quentin Boileau (Geomatys).
 */
public interface WPSObjectConverter<S, T> extends ObjectConverter<S, T> {
    
    public static final String MIME         = "mime";
    public static final String SCHEMA       = "schema";
    public static final String ENCODING     = "encoding";
    public static final String TMP_DIR_PATH = "tempDirectoryPath";
    public static final String TMP_DIR_URL  = "tempDirectoryUrl";
    public static final String IOTYPE       = "ioType";
    public static final String GMLVERSION   = "gmlVersion";

    
    /**
     * Converts an object of the {@linkplain #getSourceClass() source type}
     * to an object of the {@linkplain #getTargetClass() target type}.
     *
     * @param  source The original object, or {@code null}.
     * @param params A Map with parameters used for conversion.
     * @return An instance of target, or {@code null} if the source was null.
     * @throws NonconvertibleObjectException If the conversion can not take place.
     */
    T convert(S source, Map<String, Object> params) throws NonconvertibleObjectException;
}
