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
import org.geotoolkit.util.converter.SimpleConverter;
import org.apache.sis.util.UnconvertibleObjectException;

/**
 * Convert a String to an array of double.
 * Double in String should be separated by a coma like this : "13.5, 5.8, 182.556, 88.0".
 * Return an empty array if source is null or empty.
 *
 * @author Quentin Boileau
 */
public class StringToDoubleWArrayConverter extends SimpleConverter<String, Double[]> {

    @Override
    public Class<String> getSourceClass() {
        return String.class;
    }

    @Override
    public Class<Double[]> getTargetClass() {
        return Double[].class;
    }

    @Override
    public Double[] apply(final String source) throws UnconvertibleObjectException {

        if (source != null && !source.trim().isEmpty()) {

            final List<Double> doubleList = new LinkedList<Double>();
            if (source.contains(",")) {
                final String[] sourceSplit = source.split(",");

                for (final String str : sourceSplit) {
                    try {
                        final Double dbl = Double.valueOf(str.trim());
                        if (dbl != null) {
                            doubleList.add(dbl);
                        }
                    } catch (NumberFormatException ex) {
                        throw new UnconvertibleObjectException(ex.getMessage(), ex);
                    }
                }
            } else {
                 try {
                    final Double dbl = Double.valueOf(source.trim());
                    if (dbl != null) {
                        doubleList.add(dbl);
                    }
                } catch (NumberFormatException ex) {
                    throw new UnconvertibleObjectException(ex.getMessage(), ex);
                }
            }

            if (!doubleList.isEmpty()) {
                return doubleList.toArray(new Double[doubleList.size()]);
            } else {
                throw new UnconvertibleObjectException("Invalid source String : "+source);
            }
        }

        return new Double[0];
    }

}
