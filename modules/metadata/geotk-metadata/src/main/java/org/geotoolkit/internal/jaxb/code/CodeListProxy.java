/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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
package org.geotoolkit.internal.jaxb.code;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import org.opengis.util.CodeList;


/**
 * Stores information about {@link CodeList}, in order to handle format defined in ISO-19139
 * about the {@code CodeList} tags.
 *
 * @author Cédric Briançon (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.04
 *
 * @since 2.5
 * @module
 */
@XmlType(name = "CodeList", propOrder = { "codeListValue", "codeList" })
public final class CodeListProxy {
    /**
     * Default common URL path for the {@code codeList} attribute value.
     */
    private static final String URL = "http://www.tc211.org/ISO19139/resources/codeList.xml#";

    /**
     * The {@code codeList} attribute in the XML element.
     */
    @XmlAttribute(required = true)
    public String codeList;

    /**
     * The {@code codeListValue} attribute in the XML element.
     */
    @XmlAttribute(required = true)
    public String codeListValue;

    /**
     * Default empty constructor for JAXB.
     */
    public CodeListProxy() {
    }

    /**
     * Builds a {@link CodeList} as defined in ISO-19139 standard.
     *
     * @param codeList The {@code codeList} attribute, without the URL path.
     * @param codeListValue The {@code codeListValue} attribute.
     */
    CodeListProxy(final String codeList, final String codeListValue) {
        this.codeList = URL.concat(codeList);
        this.codeListValue = codeListValue;
    }

    /**
     * Builds a proxy instance of {@link CodeList}. It stores the values that will be
     * used for marshalling.
     *
     * @param code The code list to wrap.
     */
    CodeListProxy(final CodeList<?> code) {
        String identifier = code.identifier();
        codeListValue = (identifier != null &&
                (identifier = identifier.trim()).length() != 0) ? identifier : code.name();
        codeList = URL.concat(codeListValue);
    }
}
