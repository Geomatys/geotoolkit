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

import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
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
public class StringToIntegerArrayConverter extends SimpleConverter<String, int[]> {

    @Override
    public Class<String> getSourceClass() {
        return String.class;
    }

    @Override
    public Class<int[]> getTargetClass() {
        return int[].class;
    }

    @Override
    public int[] apply(String source) throws UnconvertibleObjectException {

        if (source != null) {
            source = source.trim();
            if (!source.isEmpty()) {

                final List<Number> values = new ArrayList<>();

                final int length = source.length();
                final NumberFormat nf = NumberFormat.getIntegerInstance();
                nf.setParseIntegerOnly(true);
                final ParsePosition pp = new ParsePosition(0);
                int idx = 0;

                for(;;) {
                    Number number = nf.parse(source, pp);
                    if (number == null) {
                        throw new UnconvertibleObjectException("Invalid source String : "+source);
                    }
                    values.add(number);

                    idx = pp.getIndex();
                    while (idx != length) {
                        char c = source.charAt(idx);
                        if (c == ',' || c == ' ' || c == '\t' || c == '\n') {
                            idx++;
                        } else {
                            break;
                        }
                    }
                    if (idx == length) break;
                    pp.setIndex(idx);
                }

                if (!values.isEmpty()) {
                    final int[] outArray = new int[values.size()];
                    for (int i = 0; i < outArray.length; i++) {
                        outArray[i] = values.get(i).intValue();
                    }
                    return outArray;
                } else {
                    throw new UnconvertibleObjectException("Invalid source String : "+source);
                }
            }
        }

        return new int[0];
    }

}
