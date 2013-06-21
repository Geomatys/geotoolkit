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

import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.datatype.XMLGregorianCalendar;

import org.opengis.temporal.Instant;
import org.apache.sis.xml.Namespaces;
import org.geotoolkit.lang.Workaround;


/**
 * The {@linkplain TimePeriod#begin begin} or {@linkplain TimePeriod#end end} position in
 * a {@link TimePeriod}. This information is encoded in different way depending if we are
 * reading or formatting a GML2 or GML2 file.
 *
 * @author Guilhem Legal (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.20
 * @module
 *
 * @todo The namespace of this class is set to {@link Namespaces#GMD} as a workaround. Actually we
 *       do that because we already have an other class in the GML binding of Constellation, and it
 *       falls on conflict. Remove the namespace, in order to fallback on GML, when the temporal
 *       implementation will have a floor in Geotk.
 */
@XmlTransient
abstract class TimePeriodBound {
    /**
     * Empty constructor for subclasses only.
     */
    TimePeriodBound() {
    }

    /**
     * Returns the XML calendar, or {@code null} if none. This information is encoded
     * in different fields depending if we are reading/writing a GML2 or a GML3 file.
     */
    abstract XMLGregorianCalendar calendar();

    /**
     * Returns a string representation of this bound for debugging purpose.
     */
    @Override
    public String toString() {
        return String.valueOf(calendar());
    }

    /**
     * The begin or end position in a {@link TimePeriod}, expressed in the GML 3 way.
     * Example:
     *
     * {@preformat xml
     *   <gml:TimePeriod>
     *     <gml:beginPosition>1992-01-01T01:00:00.000+01:00</gml:beginPosition>
     *     <gml:endPosition>2007-12-31T01:00:00.000+01:00</gml:endPosition>
     *   </gml:TimePeriod>
     * }
     *
     * @author Guilhem Legal (Geomatys)
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.20
     *
     * @since 3.20
     * @module
     */
    @Workaround(library="Geotk", version="3.15")
    public static final class GML3 extends TimePeriodBound {
        /**
         * A textual indication of the time, usually {@code "before"}, {@code "after"} or
         * {@code "now"}. This attribute and the {@linkplain #value} are mutually exclusive.
         */
        @XmlAttribute
        public String indeterminatePosition;

        /**
         * The actual time position, or {@code null} for
         * {@linkplain #indeterminatePosition indeterminate position}.
         * <p>
         * <strong>WARNING: The timezone information may be lost!</strong> This is because this field
         * is derived from a {@link java.util.Date}, in which case we don't know if the time is really
         * 0 or just unspecified. This class assumes that a time of zero means "unspecified". This will
         * be revised after we implemented ISO 19108.
         */
        @XmlValue
        public XMLGregorianCalendar value;

        /**
         * Empty constructor used by JAXB.
         */
        public GML3() {
        }

        /**
         * Creates a bound initialized to the given instant.
         *
         * @param instant The instant of the new bound, or {@code null}.
         * @param indeterminate The value to give to {@link #indeterminatePosition} if the date is null.
         */
        GML3(final Instant instant, final String indeterminate) {
            value = TimeInstant.toDate(instant);
            if (value == null) {
                indeterminatePosition = indeterminate;
            }
        }

        /**
         * Returns the XML calendar, or {@code null} if none or undetermined.
         */
        @Override
        XMLGregorianCalendar calendar() {
            return value;
        }
    }

    /**
     * The begin or end position in a {@link TimePeriod}, expressed in the GML 2 way.
     * This object encapsulates a {@link TimeInstant} inside a {@code begin} or {@code end}
     * element inside a GML 2 {@link TimePeriod} in GML 2. This is not used for GML 3.
     * Example:
     *
     * {@preformat xml
     *   <gml:TimePeriod>
     *     <gml:begin>
     *       <gml:TimeInstant gml:id="begin">
     *         <gml:timePosition>1992-01-01T01:00:00.000+01:00</gml:timePosition>
     *       </gml:TimeInstant>
     *     </gml:begin>
     *     <gml:end>
     *       <gml:TimeInstant gml:id="end">
     *         <gml:timePosition>2007-12-31T01:00:00.000+01:00</gml:timePosition>
     *       </gml:TimeInstant>
     *     </gml:end>
     *   </gml:TimePeriod>
     * }
     *
     * @author Guilhem Legal (Geomatys)
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.20
     *
     * @since 3.20 (derived from 3.03)
     * @module
     */
    @XmlType(name = "TimeInstantPropertyType", namespace = Namespaces.GMD)
    @Workaround(library="Geotk", version="3.15")
    public static final class GML2 extends TimePeriodBound {
        /**
         * The time.
         */
        @XmlElement(name = "TimeInstant", namespace = Namespaces.GML)
        public TimeInstant timeInstant;

        /**
         * Empty constructor used by JAXB.
         */
        public GML2() {
        }

        /**
         * Creates a bound initialized to the given instant.
         *
         * @param instant The instant of the new bound, or {@code null}.
         */
        GML2(final Instant instant) {
            timeInstant = new TimeInstant(instant);
        }

        /**
         * Returns the XML calendar, or {@code null} if none.
         */
        @Override
        XMLGregorianCalendar calendar() {
            final TimeInstant timeInstant = this.timeInstant;
            return (timeInstant != null) ? timeInstant.timePosition : null;
        }
    }
}
