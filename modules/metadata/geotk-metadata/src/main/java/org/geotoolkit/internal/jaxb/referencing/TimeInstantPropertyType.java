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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.geotoolkit.xml.Namespaces;
import org.geotoolkit.lang.Workaround;


/**
 * Encapsulates a {@code TimeInstant}. This is the type of begin and end time inside
 * {@link TimePeriod} in GML 2. It is not used for GML 3.
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
@XmlType(name = "TimeInstantPropertyType", namespace = Namespaces.GMD)
@Workaround(library="Geotk", version="3.15")
public final class TimeInstantPropertyType {
    /**
     * The time.
     */
    @XmlElement(name = "TimeInstant", namespace = Namespaces.GML)
    public TimeInstant timeInstant;

    /**
     * Empty constructor used by JAXB.
     */
    public TimeInstantPropertyType() {
    }
}
