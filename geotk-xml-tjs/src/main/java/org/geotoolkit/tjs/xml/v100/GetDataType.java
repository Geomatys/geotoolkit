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
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 * Request to get a specified portion of the data residing on this server.
 *
 * @author Quentin Bialota (Geomatys)
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetDataType", propOrder = {
    "frameworkURI", "datasetURI", "attributes", "linkageKeys", "filterColumn", "filterValue", "xsl"})
@XmlRootElement(name = "GetData", namespace = "http://www.opengis.net/tjs/1.0")
public class GetDataType extends RequestBaseType {

    @XmlElement(name = "FrameworkURI", required = true)
    private String frameworkURI;

    @XmlElement(name = "DatasetURI", required = true)
    private String datasetURI;

    @XmlElement(name = "Attributes")
    private String attributes;

    @XmlElement(name = "LinkageKeys")
    private String linkageKeys;

    @XmlElement(name = "FilterColumn")
    private String filterColumn;

    @XmlElement(name = "FilterValue")
    private String filterValue;

    @XmlElement(name = "XSL")
    private String xsl;

    @XmlAttribute
    private Boolean aid = Boolean.FALSE;

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

    public String getFilterColumn() {
        return filterColumn;
    }

    public void setFilterColumn(String filterColumn) {
        this.filterColumn = filterColumn;
    }

    public String getFilterValue() {
        return filterValue;
    }

    public void setFilterValue(String filterValue) {
        this.filterValue = filterValue;
    }

    public String getXsl() {
        return xsl;
    }

    public void setXsl(String xsl) {
        this.xsl = xsl;
    }

    public Boolean getAid() {
        return aid;
    }

    public void setAid(Boolean aid) {
        this.aid = aid;
    }
}
