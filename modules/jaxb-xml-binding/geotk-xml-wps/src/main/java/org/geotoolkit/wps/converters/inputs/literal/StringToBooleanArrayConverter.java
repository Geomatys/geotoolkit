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
package org.geotoolkit.wps.converters.inputs.literal;

import java.util.LinkedList;
import java.util.List;
import org.geotoolkit.feature.util.converter.SimpleConverter;
import org.apache.sis.util.UnconvertibleObjectException;

/**
 * Convert a String to an array of boolean.
 * Boolean in String should be separated by a coma like this : "true, false, true, false".
 * Return an empty array if source is null or empty.
 *
 * @author Guilhem Legal
 */
public class StringToBooleanArrayConverter extends SimpleConverter<String, boolean[]> {

    @Override
    public Class<String> getSourceClass() {
        return String.class;
    }

    @Override
    public Class<boolean[]> getTargetClass() {
        return boolean[].class;
    }

    @Override
    public boolean[] apply(final String source) throws UnconvertibleObjectException {

        if (source != null && !source.trim().isEmpty()) {

            final List<Boolean> booleanList = new LinkedList<Boolean>();
            if (source.contains(",")) {
                final String[] sourceSplit = source.split(",");

                for (final String str : sourceSplit) {
                    booleanList.add(Boolean.parseBoolean(str.trim()));
                }
            } else {

                booleanList.add(Boolean.parseBoolean(source.trim()));
            }

            if (!booleanList.isEmpty()) {
                final boolean[] outArray = new boolean[booleanList.size()];
                for (int i = 0; i < booleanList.size(); i++) {
                    outArray[i] = booleanList.get(i);
                }
                return outArray;
            } else {
                throw new UnconvertibleObjectException("Invalid source String : "+source);
            }
        }
        return new boolean[0];
    }

}
