/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2015, Geomatys
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
package org.geotoolkit.wps.xml.v100.ext;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

/**
 *
 * @author Theo Zozime
 */
@XmlRegistry
public class ObjectFactory {

    private static final QName _GeoJSON_QNAME = new QName("http://geotoolkit.org", "GeoJSON");

    public ObjectFactory() {
    }

    public GeoJSONType createGeoJSONType() {
        return new GeoJSONType();
    }

    @XmlElementDecl(namespace = "http://geotoolkit.org", name = "GeoJSON")
    public JAXBElement<GeoJSONType> createGeoJSON(final GeoJSONType value) {
        return new JAXBElement<GeoJSONType>(_GeoJSON_QNAME, GeoJSONType.class, null, value);
    }
}
