/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

@XmlSchema(namespace = "http://www.opengis.net/sos/2.0", elementFormDefault = XmlNsForm.QUALIFIED,
xmlns = { @XmlNs(prefix = "sos", namespaceURI= "http://www.opengis.net/sos/2.0"),
          @XmlNs(prefix = "gml", namespaceURI= "http://www.opengis.net/gml"),
          @XmlNs(prefix = "ogc", namespaceURI= "http://www.opengis.net/ogc"),
          @XmlNs(prefix = "swe", namespaceURI= "http://www.opengis.net/swe/1.0.1"),
          @XmlNs(prefix = "ows", namespaceURI= "http://www.opengis.net/ows/1.1"),
          @XmlNs(prefix = "om",  namespaceURI= "http://www.opengis.net/om/1.0"),
          @XmlNs(prefix = "sml", namespaceURI= "http://www.opengis.net/sensorML/1.0")})
package org.geotoolkit.sos.xml.v200;

import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;
