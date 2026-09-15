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
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 * GetData request in XML encoding, including the name of the TJS server to be queried.
 *
 * @author Quentin Bialota (Geomatys)
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetDataXMLType", propOrder = {"frameworkURI", "datasetURI", "attributes", "linkageKeys"})
public class GetDataXMLType {

    @XmlElement(name = "FrameworkURI", required = true)
    private String frameworkURI;

    @XmlElement(name = "DatasetURI", required = true)
    private String datasetURI;

    @XmlElement(name = "Attributes")
    private String attributes;

    @XmlElement(name = "LinkageKeys")
    private String linkageKeys;

    @XmlAttribute
    private String getDataHost;

    @XmlAttribute
    private String language;

    public String getFrameworkURI() {
        return frameworkURI;
    }

    public void setFrameworkURI(String frameworkURI) {
        this.frameworkURI = frameworkURI;
    }

    public String getDatasetURI() {
        return datasetURI;
    }

    public void setDatasetURI(String datasetURI) {
        this.datasetURI = datasetURI;
    }

    public String getAttributes() {
        return attributes;
    }

    public void setAttributes(String attributes) {
        this.attributes = attributes;
    }

    public String getLinkageKeys() {
        return linkageKeys;
    }

    public void setLinkageKeys(String linkageKeys) {
        this.linkageKeys = linkageKeys;
    }

    public String getGetDataHost() {
        return getDataHost;
    }

    public void setGetDataHost(String getDataHost) {
        this.getDataHost = getDataHost;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
