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
 * Convert a String to an array of double.
 * Double in String should be separated by a coma like this : "13.5, 5.8, 182.556, 88.0".
 * Return an empty array if source is null or empty.
 *
 * @author Quentin Boileau
 */
public class StringToDoubleArrayConverter extends SimpleConverter<String, double[]> {

    @Override
    public Class<String> getSourceClass() {
        return String.class;
    }

    @Override
    public Class<double[]> getTargetClass() {
        return double[].class;
    }

    @Override
    public double[] apply(final String source) throws UnconvertibleObjectException {

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
                final double[] outArray = new double[doubleList.size()];
                for (int i = 0; i < doubleList.size(); i++) {
                    outArray[i] = doubleList.get(i);
                }
                return outArray;
            } else {
                throw new UnconvertibleObjectException("Invalid source String : "+source);
            }
        }

        return new double[0];
    }

}
