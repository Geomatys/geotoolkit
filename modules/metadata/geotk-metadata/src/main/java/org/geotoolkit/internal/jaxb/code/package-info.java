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

/**
 * JAXB adapters for code {@linkplain org.opengis.util.CodeList code lists}.
 * Every time JAXB will try to marshall or unmarshall a code list, an adapter will replace
 * the code list value (which would otherwise be written directly by JAXB) by an element like
 * below:
 * <p>
 * <ul>
 *   <li>
 *     {@linkplain org.opengis.metadata.identification.CharacterSet character set}:
 *     {@code <gmd:MD_CharacterSetCode
 *       codeList="http://www.tc211.org/ISO19139/resources/codeList.xml#utf8"
 *       codeListValue="utf8"/>}
 *   </li>
 * </ul>
 *
 * @author Cédric Briançon (Geomatys)
 * @version 3.00
 *
 * @see javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter
 * @see org.opengis.util.CodeList
 *
 * @since 2.5
 * @module
 */
@XmlSchema(elementFormDefault = XmlNsForm.QUALIFIED,
namespace = Namespaces.GMD,
xmlns = {
    @XmlNs(prefix = "gmd", namespaceURI = Namespaces.GMD),
    @XmlNs(prefix = "gco", namespaceURI = Namespaces.GCO)
})
package org.geotoolkit.internal.jaxb.code;

import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;
import org.geotoolkit.xml.Namespaces;
