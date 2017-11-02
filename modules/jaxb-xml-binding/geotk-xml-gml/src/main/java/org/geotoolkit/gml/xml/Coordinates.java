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
package org.geotoolkit.gml.xml;

import java.util.ArrayList;
import java.util.List;
import java.util.Spliterator;
import java.util.StringTokenizer;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * TODO : optimize by adding a method to return coordinate values as {@link DoubleStream}.
 * It will avoid unnecessary value boxing and accumulation.
 *
 * @author Guilhem Legal (Geomatys)
 */
public interface Coordinates {

    String getValue();

    default Stream<String> brutCoordinates() {
        String value = getValue();
        if (value == null || (value = value.trim()).isEmpty()) {
            return Stream.empty();
        }

        final String ts;
        if (getTs() == null) {
            ts = " ";
        } else {
            ts = getTs();
        }

        final StringTokenizer tokenizer = new StringTokenizer(value, ts);
        final Spliterator<String> spliterator = new Spliterator<String>() {
            @Override
            public boolean tryAdvance(Consumer<? super String> action) {
                final boolean moreAvailable = tokenizer.hasMoreElements();
                if (moreAvailable) {
                    action.accept(tokenizer.nextToken());
                }

                return moreAvailable;
            }

            @Override
            public Spliterator<String> trySplit() {
                return null;
            }

            @Override
            public long estimateSize() {
                return tokenizer.countTokens();
            }

            @Override
            public int characteristics() {
                return IMMUTABLE | ORDERED | SIZED;
            }
        };

        return StreamSupport.stream(spliterator, false);
    }

    default DoubleStream values() {
        return points().flatMapToDouble(array -> DoubleStream.of(array));
    }

    default Stream<double[]> points() {
        final String cs;
        if (getCs() == null) {
            cs = ",";
        } else {
            cs = getCs();
        }

        return brutCoordinates().map(str -> {
            /* Most common case is 2, but there's really rarely points above 3
             * dimensions, so setting initial size to 3 should be the better way
             * to limit list internal resize.
             */
            final List<String> parts = new ArrayList<>(3);
            int lastIdx;
            int startIdx = 0;
            do {
                lastIdx = str.indexOf(cs, startIdx);
                if (lastIdx > 0) {
                    parts.add(str.substring(startIdx, lastIdx));
                    startIdx = lastIdx + cs.length();
                }
            } while (lastIdx >= 0);
            
            parts.add(str.substring(startIdx));

            final double[] result = new double[parts.size()];
            for (int i = 0; i < result.length; i++) {
                result[i] = Double.parseDouble(parts.get(i));
            }
            return result;
        });
    }

    default List<Double> getValues() {
        return values().boxed().collect(Collectors.toList());
    }

    String getCs();

    String getTs();

    String getDecimal();
}
