/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2007 - 2008, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 3 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */


@javax.xml.bind.annotation.XmlSchema(namespace = "http://www.opengis.net/sensorML/1.0", elementFormDefault = javax.xml.bind.annotation.XmlNsForm.QUALIFIED,
xmlns = { @javax.xml.bind.annotation.XmlNs(prefix = "gml",   namespaceURI= "http://www.opengis.net/gml"),
          @javax.xml.bind.annotation.XmlNs(prefix = "swe",   namespaceURI= "http://www.opengis.net/swe/1.0"),
          @javax.xml.bind.annotation.XmlNs(prefix = "xlink", namespaceURI= "http://www.w3.org/1999/xlink"),
          @javax.xml.bind.annotation.XmlNs(prefix = "sml",   namespaceURI= "http://www.opengis.net/sensorML/1.0"),
          @javax.xml.bind.annotation.XmlNs(prefix = "ism",   namespaceURI= "urn:us:gov:ic:ism:v2" )})
package org.geotoolkit.sml.xml.v100;
