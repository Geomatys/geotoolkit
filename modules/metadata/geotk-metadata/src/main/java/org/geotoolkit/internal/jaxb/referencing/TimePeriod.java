/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2011, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.internal.jaxb.referencing;

import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.datatype.XMLGregorianCalendar;

import org.opengis.temporal.Period;
import org.opengis.temporal.Instant;
import org.opengis.temporal.Position;

import org.geotoolkit.xml.Namespaces;
import org.geotoolkit.internal.jaxb.XmlUtilities;
import org.geotoolkit.lang.Workaround;


/**
 * The adapter for {@code "TimePeriod"}. This is an attribute of {@link TemporalPrimitiveAdapter}.
 *
 * @author Guilhem Legal (Geomatys)
 * @version 3.03
 *
 * @since 3.00
 * @module
 *
 * @todo The namespace of this class is set to {@link Namespaces#GMD} as a workaround. Actually we
 *       do that because we already have an other class in the GML binding of Constellation, and it
 *       falls on conflict. Remove the namespace, in order to fallback on GML, when the temporal
 *       implementation will have a floor in Geotk.
 */
@XmlType(name = "TimePeriodType", propOrder = {"beginPosition", "endPosition", "begin", "end"}, namespace = Namespaces.GMD)
@Workaround(library="Geotk", version="3.15")
public final class TimePeriod {
    /**
     * The start time. This element is part of GML 3.1.1 specification.
     * If non-null, then this field has precedence over {@link #begin}.
     */
    @XmlElement(namespace = Namespaces.GML)
    public XMLGregorianCalendar beginPosition;

    /**
     * The end time. This element is part of GML 3.1.1 specification.
     * If non-null, then this field has precedence over {@link #end}.
     */
    @XmlElement(namespace = Namespaces.GML)
    public XMLGregorianCalendar endPosition;

    /**
     * The start time. This element is part of GML 2.1.1 specification
     * and is used only if {@link #beginPosition} (from GML 3) is null.
     */
    @XmlElement(namespace = Namespaces.GML)
    public TimeInstantPropertyType begin;

    /**
     * The end time. This element is part of GML 2.1.1 specification
     * and is used only if {@link #endPosition} (from GML 3) is null.
     */
    @XmlElement(namespace = Namespaces.GML)
    public TimeInstantPropertyType end;

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
        beginPosition = toDate(period.getBeginning());
        endPosition   = toDate(period.getEnding());
    }

    /**
     * Creates a XML Gregorian Calendar from the given instants, if non-null.
     * Otherwise returns {@code null}.
     */
    private static XMLGregorianCalendar toDate(final Instant instant) {
        if (instant != null) {
            final Position position = instant.getPosition();
            if (position != null) {
                return XmlUtilities.toXML(position.getDate());
            }
        }
        return null;
    }

    /**
     * The {@code gml:id}, which is mandatory.
     *
     * @return The {@code "extent"} ID.
     */
    @XmlID
    @XmlAttribute(name = "id", required = true, namespace = Namespaces.GML)
    public String getID() {
        return "extent";
    }

    /**
     * Returns a string representation for debugging and formatting error message.
     */
    @Override
    public String toString() {
        return "TimePeriod[" + select(beginPosition, begin) + " ... " + select(endPosition, end) + ']';
    }

    /**
     * Returns the given position if non-null, or the position extracted from
     * the given type otherwise. In neither is defined, returns {@code null}.
     */
    static XMLGregorianCalendar select(XMLGregorianCalendar position, final TimeInstantPropertyType type) {
        if (position == null && type != null) {
            final TimeInstant t = type.timeInstant;
            if (t != null) {
                position = t.timePosition;
            }
        }
        return position;
    }
}
