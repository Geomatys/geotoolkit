/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Geomatys
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
package org.geotoolkit.csw.xml.v200;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.csw.xml.RequestBase;


/**
 * 
 * XML encoded CSW operation request base, for all operations except Get Capabilities.
 *          
 * 
 * <p>Java class for RequestBaseType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RequestBaseType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="service" type="{http://www.w3.org/2001/XMLSchema}string" default="CSW" />
 *       &lt;attribute name="version" type="{http://www.w3.org/2001/XMLSchema}string" default="2.0.0" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RequestBaseType")
@XmlSeeAlso({
    GetDomainType.class,
    DescribeRecordType.class,
    GetRecordByIdType.class,
    GetRecordsType.class
})
public abstract class RequestBaseType implements RequestBase {

    @XmlAttribute
    private String service;
    @XmlAttribute
    private String version;

    /**
     * An empty constructor used by JAXB
     */
    RequestBaseType() {
        
    }
    
    /**
     * Super contructor used by thi child classes
     * 
     * @param service the name of the service (fixed to "CSW")
     * @param version the version of the service
     */
    protected RequestBaseType(String service, String version) {
        this.service = service;
        this.version = version;
    }
    
    /**
     * Gets the value of the service property.
     * 
     */
    public String getService() {
        if (service == null) {
            return "CSW";
        } else {
            return service;
        }
    }

    /**
     * Sets the value of the service property.
     * 
     */
    public void setService(String value) {
        this.service = value;
    }

    /**
     * Gets the value of the version property.
     * 
     */
    public String getVersion() {
        if (version == null) {
            return "2.0.0";
        } else {
            return version;
        }
    }

    /**
     * Sets the value of the version property.
     * 
     */
    public void setVersion(String value) {
        this.version = value;
    }

}
