/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2011, Geomatys
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
package org.geotoolkit.internal.jaxb.gco;

import java.util.Date;
import javax.xml.bind.annotation.adapters.XmlAdapter;


/**
 * JAXB adapter wrapping the date value (as milliseconds elapsed since January 1st, 1970) in
 * a {@code <gco:Date>} element (<strong>not</strong> {@code <gco:DateTime>}), for ISO-19139
 * compliance.
 * <p>
 * The {@link Long#MIN_VALUE} is used as a sentinel value meaning "no date".
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.17
 *
 * @since 3.00
 * @module
 */
public final class DateAsLongAdapter extends XmlAdapter<GO_DateTime, Long> {
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
    public Long unmarshal(final GO_DateTime value) {
        if (value != null) {
            final Date date = value.getDate();
            if (date != null) {
                final long time = date.getTime();
                if (time != Long.MIN_VALUE) {
                    return time;
                }
            }
        }
        return Long.MIN_VALUE;
    }

    /**
     * Converts the {@linkplain Long long} to the object to be marshalled in a XML
     * file or stream. JAXB calls automatically this method at marshalling time.
     *
     * @param value The date value as a long.
     * @return The adapter for the date.
     */
    @Override
    public GO_DateTime marshal(final Long value) {
        if (value != null) {
            final long time = value;
            if (time != Long.MIN_VALUE) {
                return new GO_DateTime(new Date(time), false);
            }
        }
        return new GO_DateTime(null, false);
    }
}
