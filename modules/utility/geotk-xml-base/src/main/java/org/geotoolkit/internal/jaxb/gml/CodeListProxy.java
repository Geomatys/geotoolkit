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
package org.geotoolkit.internal.jaxb.gml;

import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.XmlAttribute;
import org.opengis.util.CodeList;
import org.geotoolkit.internal.CodeLists;


/**
 * JAXB adapter for {@link GMLCodeList}, in order to integrate the value in an element
 * complying with OGC/ISO standard.
 * <p>
 * This implementation can not be merged with {@link GMLCodeList} because we are not
 * allowed to use {@code @XmlValue} annotation in a class that extend an other class.
 *
 * @author Guilhem Legal (Geomatys)
 * @version 3.20
 *
 * @since 3.20 (derived from 3.00)
 * @module
 */
public final class CodeListProxy {
    /**
     * The code space of the {@linkplain #identifier} as an URI, or {@code null}.
     */
    @XmlAttribute
    String codeSpace;

    /**
     * The code list identifier.
     */
    @XmlValue
    String identifier;

    /**
     * Empty constructor for JAXB only.
     */
    public CodeListProxy() {
    }

    /**
     * Creates a new adapter for the given value.
     */
    CodeListProxy(final String codeSpace, final CodeList<?> value) {
       this.codeSpace  = codeSpace;
       this.identifier = CodeLists.identifier(value);
    }
}
