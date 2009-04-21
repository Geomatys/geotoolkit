/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.internal.jaxb.uom;

import java.util.Date;
import javax.xml.bind.annotation.adapters.XmlAdapter;


/**
 * JAXB adapter wrapping the date value (as milliseconds ellapsed since January 1st, 1970)
 * in a {@code <gco:Date>} element, for ISO-19139 compliance.
 * <p>
 * The {@link Long#MIN_VALUE} is used as a sentinal value meaning "no date".
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.0
 *
 * @since 3.0
 * @module
 */
public final class DateAsLongAdapter extends XmlAdapter<DateAdapter, Long> {
    /**
     * Empty constructor for JAXB only.
     */
    public DateAsLongAdapter() {
    }

    /**
     * Converts a date read from a XML stream to the object which will contains
     * the value. JAXB calls automatically this method at unmarshalling time.
     *
     * @param value The adapter for this metadata value.
     * @return A {@linkplain Long long} which represents the metadata value.
     */
    @Override
    public Long unmarshal(final DateAdapter value) {
        if (value != null) {
            final long time = value.date.getTime();
            if (time != Long.MIN_VALUE) {
                return time;
            }
        }
        return null;
    }

    /**
     * Converts the {@linkplain Long long} to the object to be marshalled in a XML
     * file or stream. JAXB calls automatically this method at marshalling time.
     *
     * @param value The date value as a long.
     * @return The adapter for the date.
     */
    @Override
    public DateAdapter marshal(final Long value) {
        if (value != null) {
            final long time = value;
            if (time != Long.MIN_VALUE) {
                return new DateAdapter(new Date(time));
            }
        }
        return new DateAdapter(null);
    }
}
