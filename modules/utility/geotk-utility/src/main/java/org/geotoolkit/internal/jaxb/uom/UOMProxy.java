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
package org.geotoolkit.internal.jaxb.uom;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;


/**
 * Stores information about the unit of measure, in order to handle format defined
 * in ISO-19103.
 *
 * @author Cédric Briançon (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.0
 *
 * @since 2.5
 * @module
 */
final class UOMProxy {
    /**
     * The value of the measure.
     */
    @XmlValue
    public Double value;

    /**
     * The unit of measure.
     */
    @XmlAttribute(required = true)
    public String uom;

    /**
     * Default empty constructor for JAXB used.
     */
    public UOMProxy() {
    }

    /**
     * Constructs a representation of the measure as defined in ISO-19103 standard.
     *
     * @param value The value of the measure.
     * @param uom The unit of measure to use.
     */
    UOMProxy(final Double value, final String uom) {
        this.value = value;
        this.uom   = uom;
    }
}
