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
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

import org.opengis.temporal.Period;

import org.apache.sis.xml.Namespaces;
import org.apache.sis.internal.jaxb.Context;
import org.geotoolkit.internal.jaxb.gml.GMLAdapter;
import org.geotoolkit.lang.Workaround;


/**
 * The adapter for {@code "TimePeriod"}. This is an attribute of {@link TM_Primitive}.
 *
 * @author Guilhem Legal (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.00
 * @module
 *
 * @todo The namespace of this class is set to {@link Namespaces#GMD} as a workaround. Actually we
 *       do that because we already have an other class in the GML binding of Constellation, and it
 *       falls on conflict. Remove the namespace, in order to fallback on GML, when the temporal
 *       implementation will have a floor in Geotk.
 *
 * @todo A time period can also be expressed as a begin position and a period or duration.
 *       This is not yet supported in the current implementation.
 */
@XmlRootElement(name="TimePeriod")
@XmlType(name = "TimePeriodType", namespace = Namespaces.GMD, propOrder = {
    "begin",
    "end"
})
@Workaround(library="Geotk", version="3.15")
public final class TimePeriod extends GMLAdapter {
    /**
     * The start time, which may be marshalled in a GML3 way or GML2 way.
     * The GML2 way is more verbose.
     */
    @XmlElements({
        @XmlElement(type=TimePeriodBound.GML3.class, name="beginPosition", namespace=Namespaces.GML),
        @XmlElement(type=TimePeriodBound.GML2.class, name="begin",         namespace=Namespaces.GML)
    })
    TimePeriodBound begin;

    /**
     * The end time, which may be marshalled in a GML3 way or GML2 way.
     * The GML2 way is more verbose.
     */
    @XmlElements({
        @XmlElement(type=TimePeriodBound.GML3.class, name="endPosition", namespace=Namespaces.GML),
        @XmlElement(type=TimePeriodBound.GML2.class, name="end",         namespace=Namespaces.GML)
    })
    TimePeriodBound end;

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
            if (Context.isGMLVersion(Context.current(), GML_3_0)) {
                begin = new TimePeriodBound.GML3(period.getBeginning(), "before");
                end   = new TimePeriodBound.GML3(period.getEnding(), "after");
            } else {
                begin = new TimePeriodBound.GML2(period.getBeginning());
                end   = new TimePeriodBound.GML2(period.getEnding());
            }
        }
    }

    /**
     * Returns a string representation for debugging and formatting error message.
     */
    @Override
    public String toString() {
        return "TimePeriod[" + begin + " ... " + end + ']';
    }
}
