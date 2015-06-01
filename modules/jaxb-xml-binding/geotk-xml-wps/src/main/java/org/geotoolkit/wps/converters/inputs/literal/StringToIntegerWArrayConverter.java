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
 * Convert a String to an array of int.
 * Double in String should be separated by a coma like this : "13, 5, 182, 88".
 * Return an empty array if source is null or empty.
 *
 * @author Quentin Boileau
 */
public class StringToIntegerWArrayConverter extends SimpleConverter<String, Integer[]> {

    @Override
    public Class<String> getSourceClass() {
        return String.class;
    }

    @Override
    public Class<Integer[]> getTargetClass() {
        return Integer[].class;
    }

    @Override
    public Integer[] apply(final String source) throws UnconvertibleObjectException {

        if (source != null && !source.trim().isEmpty()) {

            final List<Integer> integerList = new LinkedList<Integer>();
            if (source.contains(",")) {
                final String[] sourceSplit = source.split(",");

                for (final String str : sourceSplit) {
                    try {
                        final Integer i = Integer.valueOf(str.trim());
                        if (i != null) {
                            integerList.add(i);
                        }
                    } catch (NumberFormatException ex) {
                        throw new UnconvertibleObjectException(ex.getMessage(), ex);
                    }
                }
            } else {
                 try {
                    final Integer i = Integer.valueOf(source.trim());
                    if (i != null) {
                        integerList.add(i);
                    }
                } catch (NumberFormatException ex) {
                    throw new UnconvertibleObjectException(ex.getMessage(), ex);
                }
            }

            if (!integerList.isEmpty()) {
                return integerList.toArray(new Integer[integerList.size()]);
            } else {
                throw new UnconvertibleObjectException("Invalid source String : "+source);
            }
        }

        return new Integer[0];
    }

}
