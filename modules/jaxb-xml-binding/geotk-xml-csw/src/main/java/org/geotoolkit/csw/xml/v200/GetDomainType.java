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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;
import org.geotoolkit.csw.xml.GetDomain;


/**
 * 
 * Requests the actual values of some specified property or data element.
 *         
 * 
 * <p>Java class for GetDomainType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetDomainType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/cat/csw}RequestBaseType">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;element name="PropertyName" type="{http://www.w3.org/2001/XMLSchema}QName"/>
 *           &lt;element name="ParameterName" type="{http://www.w3.org/2001/XMLSchema}QName"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetDomainType", propOrder = {
    "propertyName",
    "parameterName"
})
public class GetDomainType extends RequestBaseType implements GetDomain {

    @XmlElement(name = "PropertyName")
    private QName propertyName;
    @XmlElement(name = "ParameterName")
    private String parameterName;

    /**
     * An empty constructor used by JAXB
     */
    public GetDomainType() {

    }

    /**
     * Build a new GetDomain request.
     * One of propertyName or parameterName must be null
     *
     * @param service
     * @param version
     * @param propertyName
     */
    public GetDomainType(String service, String version, QName propertyName, String parameterName) {
        super(service, version);
        if (propertyName != null && parameterName != null) {
            throw new IllegalArgumentException("One of propertyName or parameterName must be null");
        }
        this.propertyName  = propertyName;
        this.parameterName = parameterName;
    }

    /**
     * Gets the value of the propertyName property.
     * 
     */
    public String getPropertyName() {
        if (propertyName != null)
            return propertyName.getPrefix() + ":" + propertyName.getLocalPart();
        return null;
    }

    /**
     * Sets the value of the propertyName property.
     * 
     */
    public void setPropertyName(QName value) {
        this.propertyName = value;
    }

    /**
     * Gets the value of the parameterName property.
     * 
     */
    public String getParameterName() {
        return parameterName;
    }

    /**
     * Sets the value of the parameterName property.
     * 
     */
    public void setParameterName(String value) {
        this.parameterName = value;
    }

    public String getOutputFormat() {
        return "application/xml";
    }

    public void setOutputFormat(String value) {}

}
