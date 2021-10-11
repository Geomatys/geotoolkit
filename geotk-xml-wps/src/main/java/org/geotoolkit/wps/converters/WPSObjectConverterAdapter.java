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
import java.util.Set;
import org.apache.sis.math.FunctionProperty;
import org.apache.sis.util.UnconvertibleObjectException;
import org.apache.sis.util.ObjectConverter;

/**
 * Convenient adapter to use an standard {@link ObjectConverter} like a {@link WPSObjectConverter}.
 *
 * @see ObjectConverter
 * @see WPSObjectConverter
 * @author Quentin Boileau (Geomatys).
 */
public class WPSObjectConverterAdapter<S, T> implements WPSObjectConverter<S, T> {

    final ObjectConverter<S, T> converter;

    public WPSObjectConverterAdapter(final ObjectConverter<S, T> converter) {
        this.converter = converter;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Class<S> getSourceClass() {
        return converter.getSourceClass();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Class<T> getTargetClass() {
        return converter.getTargetClass();
    }

    @Override
    public Set<FunctionProperty> properties() {
        return converter.properties();
    }

    @Override
    public ObjectConverter<T, S> inverse() throws UnsupportedOperationException {
        return converter.inverse();
    }

    /**
     * No used for wps converters. Use {@link WPSObjectConverter#convert(java.lang.Object, java.util.Map) } instead.
     *
     * @param source
     * @return
     * @throws UnconvertibleObjectException
     */
    @Override
    public T apply(S source) throws UnconvertibleObjectException {
        return this.convert(source, null);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public T convert(S source, Map<String, Object> params) throws UnconvertibleObjectException {
        return converter.apply(source);
    }
}
