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

/**
 * JAXB adapters for metadata.
 * This package regroups all adapters mapping GeoAPI interfaces to their Geotoolkit
 * implementation. We must use adapters since JAXB can not annotate interfaces.
 * Consequently the purpose of these adapters is to replace interfaces.
 * <p>
 * Every time JAXB try to marshall or unmarshall an interface, the adapter will be
 * substituted to that interface.
 *
 * @author Cédric Briançon (Geomatys)
 * @version 3.0
 *
 * @see javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter
 *
 * @since 2.5
 * @module
 */
@XmlSchema(elementFormDefault = XmlNsForm.QUALIFIED,
namespace = Namespaces.GMD,
xmlns = {
    @XmlNs(prefix = "gmd", namespaceURI = Namespaces.GMD),
    @XmlNs(prefix = "gco", namespaceURI = Namespaces.GCO),
    @XmlNs(prefix = "xsi", namespaceURI = Namespaces.XSI),
    @XmlNs(prefix = "fra", namespaceURI = Namespaces.FRA)
})
@XmlAccessorType(XmlAccessType.NONE)
package org.geotoolkit.internal.jaxb.metadata;

import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import org.geotoolkit.xml.Namespaces;
