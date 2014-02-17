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

import org.geotoolkit.util.converter.NonconvertibleObjectException;
import org.geotoolkit.util.converter.ObjectConverter;

/**
 *
 * @author Quentin Boileau (Geomatys)
 */
public class DoubleWArrayToStringConverter implements ObjectConverter<Double[], String> {

    @Override
    public Class<? super Double[]> getSourceClass() {
        return Double[].class;
    }

    @Override
    public Class<? extends String> getTargetClass() {
        return String.class;
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
    public String convert(final Double[] source) throws NonconvertibleObjectException {
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
