/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2009, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.internal.jaxb.referencing;

import java.util.Date;
import java.text.DateFormat;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlAttribute;
import org.geotoolkit.internal.jaxb.XmlUtilities;

import org.geotoolkit.xml.Namespaces;
import org.opengis.temporal.Period;
import org.opengis.temporal.Instant;
import org.opengis.temporal.Position;


/**
 * The adapter for time period.
 *
 * @author Guilhem Legal (Geomatys)
 * @version 3.0
 *
 * @since 3.0
 * @module
 *
 * TODO: The namespace of this class is set to {@link Namespaces#GMD} as a workaround. Actually we do
 *       that because we already have an other class in the GML binding of Constellation, and it falls on conflict.
 *       Remove the namespace, in order to fallback on GML, when the temporal implementation will have a floor
 *       in Geotoolkit.
 */
@XmlType(name = "TimePeriodType", propOrder = {"beginPosition", "endPosition"}, namespace = Namespaces.GMD)
public final class TimePeriod {
    /**
     * The start time.
     */
    @XmlElement(namespace = Namespaces.GML)
    public String beginPosition;

    /**
     * The end time.
     */
    @XmlElement(namespace = Namespaces.GML)
    public String endPosition;

    /**
     * Empty constructor used by JAXB.
     */
    public TimePeriod() {
    }

    /**
     * Creates a new Time Period bounded by the begin and end time specified in the given object.
     *
     * @param period The period to use for initializing this object.
     */
    public TimePeriod(final Period period) {
        final DateFormat format = XmlUtilities.getDateFormat();
        beginPosition = format(period.getBeginning(), format);
        endPosition = format(period.getEnding(), format);
    }

    /**
     * Formats the given instants, or returns an empty string if null.
     */
    private static String format(final Instant instant, final DateFormat format) {
        if (instant != null) {
            final Position position = instant.getPosition();
            if (position != null) {
                final Date date = position.getDate();
                if (date != null) {
                    return format.format(date);
                }
            }
        }
        return "";
    }

    /**
     * The {@code gml:id}, which is mandatory.
     */
    @XmlID
    @XmlAttribute(name = "id", required = true, namespace = Namespaces.GML)
    private String getID() {
        return "extent";
    }

    /**
     * Returns a string representation for debugging and formatting error message.
     */
    @Override
    public String toString() {
        return "TimePeriod[" + beginPosition + " ... " + endPosition + ']';
    }
}
