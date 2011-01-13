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
package org.geotoolkit.internal.jaxb.referencing;

import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.datatype.XMLGregorianCalendar;

import org.geotoolkit.xml.Namespaces;
import org.geotoolkit.lang.Workaround;


/**
 * Encapsulates a Time Instant. This element is contained inside a {@link TimeInstantPropertyType},
 * which is itself contained in a {@link TimePeriod} in GML 2. It is not used for GML 3.
 *
 * @author Guilhem Legal (Geomatys)
 * @version 3.03
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
@XmlType(name = "TimeInstantType", propOrder = {"timePosition"}, namespace = Namespaces.GMD)
@Workaround(library="Geotk", version="3.15")
public final class TimeInstant {
    /**
     * The time.
     */
    @XmlElement(namespace = Namespaces.GML)
    public XMLGregorianCalendar timePosition;

    /**
     * Empty constructor used by JAXB.
     */
    public TimeInstant() {
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
        return "TimeInstant[" + timePosition + ']';
    }
}
