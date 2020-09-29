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

import java.text.DecimalFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.geotoolkit.feature.util.converter.SimpleConverter;
import org.apache.sis.util.UnconvertibleObjectException;

/**
 * Convert a String to an array of float.
 * Double in String should be separated by a coma like this : "13.6, 5.4, 182.1, 88.0".
 * Return an empty array if source is null or empty.
 *
 * @author Quentin Boileau
 */
public class StringToFloatArrayConverter extends SimpleConverter<String, float[]> {

    @Override
    public Class<String> getSourceClass() {
        return String.class;
    }

    @Override
    public Class<float[]> getTargetClass() {
        return float[].class;
    }

    @Override
    public float[] apply(String source) throws UnconvertibleObjectException {

        if (source != null) {
            source = source.trim();
            if (!source.isEmpty()) {

                final List<Number> values = new ArrayList<>();

                final int length = source.length();
                final DecimalFormat nf = (DecimalFormat) DecimalFormat.getInstance(Locale.ENGLISH);
                nf.setParseIntegerOnly(false);
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
                    final float[] outArray = new float[values.size()];
                    for (int i = 0; i < outArray.length; i++) {
                        outArray[i] = values.get(i).floatValue();
                    }
                    return outArray;
                } else {
                    throw new UnconvertibleObjectException("Invalid source String : "+source);
                }
            }
        }

        return new float[0];
    }

}
