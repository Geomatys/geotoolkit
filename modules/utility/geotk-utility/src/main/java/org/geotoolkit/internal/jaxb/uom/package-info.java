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

/**
 * JAXB adapters for <cite>unit of measure</cite> as specified in the ISO-19103
 * specifications. For example, a measure marshalled with JAXB will be formatted
 * like {@code <gco:Measure uom="pixel">220.0</gco:Measure>}.
 * <p>
 * Contains also adapter for date and time, which are considered as measures
 * in a fixed unit.
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
package org.geotoolkit.internal.jaxb.uom;

import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import org.geotoolkit.xml.Namespaces;
