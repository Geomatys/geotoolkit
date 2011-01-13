/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2011, Open Source Geospatial Foundation (OSGeo)
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

/**
 * JAXB adapters for primitive types.
 * <p>
 * JAXB can write directly Java primitive type at marshalling time "as is". However
 * ISO-19139 specifies this kind of values has to be surrounded by elements representing
 * the data type. The role of these adapters is to add these elements around the value.
 * <p>
 * For example, a {@link java.lang.String} value has to be marshalled this way:
 *
 * {@preformat text
 *     <gco:CharacterString>my text</gco:CharacterString>
 * }
 *
 * In this example, {@code gco} is the prefix for the {@code http://www.isotc211.org/2005/gco}
 * namespace URL.
 *
 * @author Cédric Briançon (Geomatys)
 * @version 3.00
 *
 * @see javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter
 *
 * @since 2.5
 * @module
 */
@XmlSchema(elementFormDefault= XmlNsForm.QUALIFIED,
namespace = Namespaces.GCO,
xmlns = {
    @XmlNs(prefix = "gco", namespaceURI = Namespaces.GCO)
})
@XmlAccessorType(XmlAccessType.NONE)
package org.geotoolkit.internal.jaxb.primitive;

import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import org.geotoolkit.xml.Namespaces;
