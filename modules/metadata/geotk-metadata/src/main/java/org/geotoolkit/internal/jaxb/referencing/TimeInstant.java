/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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

import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.datatype.XMLGregorianCalendar;

import org.opengis.temporal.Instant;
import org.opengis.temporal.Position;

import org.apache.sis.xml.Namespaces;
import org.geotoolkit.lang.Workaround;
import org.geotoolkit.internal.jaxb.XmlUtilities;
import org.geotoolkit.internal.jaxb.gml.GMLAdapter;


/**
 * Encapsulates a {@code gml:TimeInstant}. This element may be used alone, or included in a
 * {@link TimePeriodBound.GML2} object. The later is itself included in {@link TimePeriod}.
 * Note that GML3 does not anymore include {@code TimeInstant} inside {@code TimePeriod}.
 *
 * @author Guilhem Legal (Geomatys)
 * @version 3.20
 *
 * @since 3.03
 * @module
 *
 * @todo The namespace of this class is set to {@link Namespaces#GMD} as a workaround. Actually we
 *       do that because we already have an other class in the GML binding of Constellation, and it
 *       falls on conflict. Remove the namespace, in order to fallback on GML, when the temporal
 *       implementation will have a floor in Geotk.
 */
@XmlRootElement(name="TimeInstant")
@XmlType(name = "TimeInstantType", namespace = Namespaces.GMD)
@Workaround(library="Geotk", version="3.15")
public final class TimeInstant extends GMLAdapter {
    /**
     * The date, optionally with its time component. The time component is omitted
     * if the hours, minutes, seconds and milliseconds fields are all set to 0.
     * <p>
     * <strong>WARNING: The timezone information may be lost!</strong> This is because this field
     * is derived from a {@link java.util.Date}, in which case we don't know if the time is really
     * 0 or just unspecified. This class assumes that a time of zero means "unspecified". This will
     * be revised after we implemented ISO 19108.
     */
    @XmlElement(namespace = Namespaces.GML)
    public XMLGregorianCalendar timePosition;

    /**
     * Empty constructor used by JAXB.
     */
    public TimeInstant() {
    }

    /**
     * Creates a new time instant initialized to the given value.
     *
     * @param instant The initial instant value.
     */
    public TimeInstant(final Instant instant) {
        timePosition = toDate(instant);
    }

    /**
     * Creates a XML Gregorian Calendar from the given instants, if non-null.
     * Otherwise returns {@code null}.
     * <p>
     * <strong>WARNING: The timezone information may be lost!</strong> This is because this field
     * is derived from a {@link java.util.Date}, in which case we don't know if the time is really
     * 0 or just unspecified. This class assumes that a time of zero means "unspecified". This will
     * be revised after we implemented ISO 19108.
     */
    static XMLGregorianCalendar toDate(final Instant instant) {
        if (instant != null) {
            final Position position = instant.getPosition();
            if (position != null) {
                final XMLGregorianCalendar date = XmlUtilities.toXML(position.getDate());
                if (date != null) {
                    XmlUtilities.trimTime(date, false);
                    return date;
                }
            }
        }
        return null;
    }

    /**
     * The {@code gml:id}, which is mandatory.
     *
     * @return The {@code "extent"} ID.
     *
     * @deprecated This duplicate the {@link GMLAdapter#id} field.
     */
    @XmlID
    @Deprecated
    @XmlAttribute(name = "id", required = true, namespace = Namespaces.GML)
    public String getID() {
        return "extent";
    }

    /**
     * Returns a string representation for debugging and formatting error message.
     */
    @Override
    public String toString() {
        return "TimeInstant[" + timePosition + ']';
    }
}
