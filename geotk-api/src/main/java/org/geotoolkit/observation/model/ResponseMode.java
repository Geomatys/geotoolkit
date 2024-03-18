/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2023, Geomatys
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
package org.geotoolkit.observation.model;

import java.util.Arrays;
import java.util.stream.Collectors;


public enum ResponseMode {

    INLINE("inline"),
    ATTACHED("attached"),
    OUT_OF_BAND("out-of-band"),
    RESULT_TEMPLATE("resultTemplate");
    private final String value;

    ResponseMode(final String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ResponseMode fromValue(final String v) {
        for (ResponseMode c: ResponseMode.values()) {
            if (c.value.equalsIgnoreCase(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(String.format(
             "Unknown response mode: %s. Accepted modes: %s",
             v, Arrays.stream(ResponseMode.values()).map(ResponseMode::value).collect(Collectors.joining(", "))));

    }

}
