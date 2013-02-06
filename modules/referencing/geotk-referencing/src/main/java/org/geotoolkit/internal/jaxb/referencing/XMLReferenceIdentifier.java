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
import javax.xml.bind.annotation.XmlAttribute;
import org.opengis.referencing.ReferenceIdentifier;
import org.apache.sis.util.StringBuilders;


/**
 * A simple adapter for {@link ReferenceIdentifier} holding only the code (as the XML value)
 * and codespace of the identifier.
 *
 * @author Cédric Briançon (Geomatys)
 * @version 3.06
 *
 * @since 3.00
 * @module
 */
@XmlType
public final class XMLReferenceIdentifier {
    /**
     * The XML value.
     */
    @XmlValue
    protected String code;

    /**
     * The code space as a XML attribute. This is often {@code "EPSG"} with the version in use.
     */
    @XmlAttribute
    protected String codeSpace;

    /**
     * Empty constructor for JAXB only.
     */
    public XMLReferenceIdentifier() {
    }

    /**
     * Creates an identifier initialized to the values of the given identifier.
     *
     * @param identifier The identifier from which to get the values.
     */
    XMLReferenceIdentifier(final ReferenceIdentifier identifier) {
        code     = identifier.getCode();
        codeSpace = identifier.getCodeSpace();
        if (codeSpace == null) {
            codeSpace = "";
        }
        String version = identifier.getVersion();
        if (version != null) {
            final StringBuilder buffer = new StringBuilder(codeSpace);
            if (buffer.length() != 0) {
                buffer.append('_');
            }
            StringBuilders.remove(buffer.append('v').append(version), ".");
            codeSpace = buffer.toString();
        }
    }
}
