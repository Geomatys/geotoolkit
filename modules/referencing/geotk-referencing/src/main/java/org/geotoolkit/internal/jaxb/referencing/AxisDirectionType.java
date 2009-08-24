/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
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

import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.XmlAttribute;
import org.opengis.referencing.cs.AxisDirection;


/**
 * JAXB adapter for {@link AxisDirection}, in order to integrate the value in an element
 * complying with ISO-19139 standard.
 * <p>
 * This implementation can not be merged with {@link AxisDirectionAdapter} because we
 * are not allowed to use {@code @XmlValue} annotation in a class that extend an other
 * class.
 *
 * @author Guilhem Legal (Geomatys)
 * @version 3.02
 *
 * @since 3.00
 * @module
 */
public final class AxisDirectionType {
    /**
     * The XML value.
     */
    @XmlValue
    String value;

    /**
     * The code space as a XML attribute. This is often {@code "EPSG"}.
     */
    @XmlAttribute
    String codeSpace;

    /**
     * Empty constructor for JAXB only.
     */
    public AxisDirectionType() {
    }

    /**
     * Creates a new adapter for the given value.
     */
    AxisDirectionType(final AxisDirection value) {
       this.codeSpace = "EPSG";
       this.value     = value.identifier();
    }
}
