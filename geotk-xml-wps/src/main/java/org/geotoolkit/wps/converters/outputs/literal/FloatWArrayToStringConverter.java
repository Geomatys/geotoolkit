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
package org.geotoolkit.wps.converters.outputs.literal;

import org.geotoolkit.feature.util.converter.SimpleConverter;
import org.apache.sis.util.UnconvertibleObjectException;

/**
 *
 * @author Quentin Boileau (Geomatys)
 */
public class FloatWArrayToStringConverter extends SimpleConverter<Float[], String> {

    @Override
    public Class<Float[]> getSourceClass() {
        return Float[].class;
    }

    @Override
    public Class<String> getTargetClass() {
        return String.class;
    }

    @Override
    public String apply(final Float[] source) throws UnconvertibleObjectException {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < source.length; i++) {
            sb.append(source[i].toString());
            if (i < source.length-1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }

}
