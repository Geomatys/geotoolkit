/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.temporal.precision;

import java.text.DateFormat;


/**
 * Precision to apply on a {@linkplain TuncatedDate date}, meaning the maximum
 * precision we can obtain for a date.
 *
 * @author Cédric Briançon (Geomatys)
 */
public enum DatePrecision {
    MILLISECOND('S'),

    SECOND('s'),

    MINUTE('m'),

    HOUR('H'),

    DAY('d'),

    WEEK('w'),

    MONTH('M'),

    YEAR('y');

    /**
     * Matching {@link DateFormat} constant.
     */
    public final char pattern;

    /**
     * Make the {@link DateFormat} constant matches the enum value.
     */
    private DatePrecision(final char pattern) {
        this.pattern = pattern;
    }
}
