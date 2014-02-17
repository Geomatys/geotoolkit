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
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AbstractMSInformationType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AbstractMSInformationType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="msIDType" default="MSISDN">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="MSISDN"/>
 *             &lt;enumeration value="MIN"/>
 *             &lt;enumeration value="IMSI"/>
 *             &lt;enumeration value="IMEI"/>
 *             &lt;enumeration value="MDN"/>
 *             &lt;enumeration value="EME_MSID"/>
 *             &lt;enumeration value="IPV4"/>
 *             &lt;enumeration value="IPV6"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="msIDValue" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="encryption" default="ASC">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="ASC"/>
 *             &lt;enumeration value="B64"/>
 *             &lt;enumeration value="CRP"/>
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
@XmlType(name = "AbstractMSInformationType")
@XmlSeeAlso({
    OutputMSInformationType.class,
    InputMSInformationType.class
})
public class AbstractMSInformationType {

    @XmlAttribute
    private String msIDType;
    @XmlAttribute
    private String msIDValue;
    @XmlAttribute
    private String encryption;

    /**
     * Gets the value of the msIDType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMsIDType() {
        if (msIDType == null) {
            return "MSISDN";
        } else {
            return msIDType;
        }
    }

    /**
     * Sets the value of the msIDType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMsIDType(String value) {
        this.msIDType = value;
    }

    /**
     * Gets the value of the msIDValue property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMsIDValue() {
        return msIDValue;
    }

    /**
     * Sets the value of the msIDValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMsIDValue(String value) {
        this.msIDValue = value;
    }

    /**
     * Gets the value of the encryption property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEncryption() {
        if (encryption == null) {
            return "ASC";
        } else {
            return encryption;
        }
    }

    /**
     * Sets the value of the encryption property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEncryption(String value) {
        this.encryption = value;
    }

}
