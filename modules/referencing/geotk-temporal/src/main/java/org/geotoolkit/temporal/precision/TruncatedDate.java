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

import java.util.Date;


/**
 * A date that can be truncated with the given precision.
 *
 * @author Cédric Briançon (Geomatys)
 */
public class TruncatedDate extends Date {
    /**
     * Defines the precision to apply for this date.
     */
    private final DatePrecision precision;

    public TruncatedDate(final long time, final DatePrecision precision) {
        super(time);
        this.precision = precision;
    }

    /**
     * Returns the maximum precision we can get for this date.
     */
    public DatePrecision getPrecision() {
        return precision;
    }
}
