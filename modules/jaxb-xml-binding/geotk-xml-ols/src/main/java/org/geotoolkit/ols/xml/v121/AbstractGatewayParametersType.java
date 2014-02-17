/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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

package org.geotoolkit.ols.xml.v121;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AbstractGatewayParametersType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AbstractGatewayParametersType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="RequestedQoP" type="{http://www.opengis.net/xls}QualityOfPositionType" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="locationType" default="CURRENT">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="CURRENT"/>
 *             &lt;enumeration value="LAST"/>
 *             &lt;enumeration value="CURRENT_OR_LAST"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="requestedsrsName" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;attribute name="priority" default="HIGH">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="NORMAL"/>
 *             &lt;enumeration value="HIGH"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractGatewayParametersType", propOrder = {
    "requestedQoP"
})
@XmlSeeAlso({
    OutputGatewayParametersType.class,
    InputGatewayParametersType.class
})
public abstract class AbstractGatewayParametersType {

    @XmlElement(name = "RequestedQoP")
    private QualityOfPositionType requestedQoP;
    @XmlAttribute
    private String locationType;
    @XmlAttribute
    @XmlSchemaType(name = "anyURI")
    private String requestedsrsName;
    @XmlAttribute
    private String priority;

    /**
     * Gets the value of the requestedQoP property.
     * 
     * @return
     *     possible object is
     *     {@link QualityOfPositionType }
     *     
     */
    public QualityOfPositionType getRequestedQoP() {
        return requestedQoP;
    }

    /**
     * Sets the value of the requestedQoP property.
     * 
     * @param value
     *     allowed object is
     *     {@link QualityOfPositionType }
     *     
     */
    public void setRequestedQoP(QualityOfPositionType value) {
        this.requestedQoP = value;
    }

    /**
     * Gets the value of the locationType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLocationType() {
        if (locationType == null) {
            return "CURRENT";
        } else {
            return locationType;
        }
    }

    /**
     * Sets the value of the locationType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLocationType(String value) {
        this.locationType = value;
    }

    /**
     * Gets the value of the requestedsrsName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRequestedsrsName() {
        return requestedsrsName;
    }

    /**
     * Sets the value of the requestedsrsName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRequestedsrsName(String value) {
        this.requestedsrsName = value;
    }

    /**
     * Gets the value of the priority property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPriority() {
        if (priority == null) {
            return "HIGH";
        } else {
            return priority;
        }
    }

    /**
     * Sets the value of the priority property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPriority(String value) {
        this.priority = value;
    }

}
