/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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

/**
 * The adapter for encapsulate a time Instant.
 *
 * @author Guilhem Legal (Geomatys)
 * @version 3.02
 *
 * @since 3.02
 * @module
 *
 * TODO: The namespace of this class is set to {@link Namespaces#GMD} as a workaround. Actually we do
 *       that because we already have an other class in the GML binding of Constellation, and it falls on conflict.
 *       Remove the namespace, in order to fallback on GML, when the temporal implementation will have a floor
 *       in Geotk.
 */
@XmlType(name = "TimeInstantPropertyType", propOrder = {"timeInstant"}, namespace = Namespaces.GMD)
public class TimeInstantPropertyType {

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

    /**
     * Creates a new Time Period bounded by the begin and end time specified in the given object.
     *
     * @param period The period to use for initializing this object.
     */
    public TimeInstantPropertyType(final TimeInstant instant) {
        this.timeInstant = instant;
    }

}
