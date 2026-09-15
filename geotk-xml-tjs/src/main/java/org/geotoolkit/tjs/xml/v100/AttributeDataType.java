/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2026, Geomatys
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
package org.geotoolkit.tjs.xml.v100;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 * Attribute data to be joined to the spatial framework: either a URL returning a GetData
 * response, or an inline XML-encoded GetData request.
 *
 * @author Quentin Bialota (Geomatys)
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AttributeDataType", propOrder = {"getDataURL", "getDataXML"})
public class AttributeDataType {

    @XmlElement(name = "GetDataURL")
    private String getDataURL;

    @XmlElement(name = "GetDataXML")
    private GetDataXMLType getDataXML;

    public String getGetDataURL() {
        return getDataURL;
    }

    public void setGetDataURL(String getDataURL) {
        this.getDataURL = getDataURL;
    }

    public GetDataXMLType getGetDataXML() {
        return getDataXML;
    }

    public void setGetDataXML(GetDataXMLType getDataXML) {
        this.getDataXML = getDataXML;
    }
}
