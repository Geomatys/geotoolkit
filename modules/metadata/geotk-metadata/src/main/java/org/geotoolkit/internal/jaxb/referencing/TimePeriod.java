/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2012, Open Source Geospatial Foundation (OSGeo)
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

import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.datatype.XMLGregorianCalendar;

import org.opengis.temporal.Period;

import org.geotoolkit.xml.Namespaces;
import org.geotoolkit.internal.jaxb.gml.GMLAdapter;
import org.geotoolkit.lang.Workaround;


/**
 * The adapter for {@code "TimePeriod"}. This is an attribute of {@link TM_Primitive}.
 *
 * @author Guilhem Legal (Geomatys)
 * @version 3.20
 *
 * @since 3.00
 * @module
 *
 * @todo The namespace of this class is set to {@link Namespaces#GMD} as a workaround. Actually we
 *       do that because we already have an other class in the GML binding of Constellation, and it
 *       falls on conflict. Remove the namespace, in order to fallback on GML, when the temporal
 *       implementation will have a floor in Geotk.
 */
@XmlType(name = "TimePeriodType", namespace = Namespaces.GMD, propOrder = {
    "beginPosition",
    "endPosition",
    "begin",
    "end"
})
@Workaround(library="Geotk", version="3.15")
public final class TimePeriod extends GMLAdapter {
    /**
     * The start time. This element is part of GML 3.1.1 specification.
     * If non-null, then this field has precedence over {@link #begin}.
     */
    @XmlElement(namespace = Namespaces.GML)
    public XMLGregorianCalendar beginPosition;

    /**
     * The end time. This element is part of GML 3.1.1 specification.
     * If non-null, then this field has precedence over {@link #end}.
     * <p>
     * <strong>WARNING: The timezone information may be lost!</strong> This is because this field
     * is derived from a {@link java.util.Date}, in which case we don't know if the time is really
     * 0 or just unspecified. This class assumes that a time of zero means "unspecified". This will
     * be revised after we implemented ISO 19108.
     */
    @XmlElement(namespace = Namespaces.GML)
    public XMLGregorianCalendar endPosition;

    /**
     * The start time. This element is part of GML 2.1.1 specification
     * and is used only if {@link #beginPosition} (from GML 3) is null.
     * <p>
     * <strong>WARNING: The timezone information may be lost!</strong> This is because this field
     * is derived from a {@link java.util.Date}, in which case we don't know if the time is really
     * 0 or just unspecified. This class assumes that a time of zero means "unspecified". This will
     * be revised after we implemented ISO 19108.
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
        super(period);
        if (period != null) {
            beginPosition = TimeInstant.toDate(period.getBeginning());
            endPosition   = TimeInstant.toDate(period.getEnding());
        }
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
     * the given type otherwise. If neither is defined, returns {@code null}.
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
