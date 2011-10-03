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
package org.geotoolkit.wfs.xml.v100;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for StatusType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="StatusType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element ref="{http://www.opengis.net/wfs}SUCCESS"/>
 *         &lt;element ref="{http://www.opengis.net/wfs}FAILED"/>
 *         &lt;element ref="{http://www.opengis.net/wfs}PARTIAL"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StatusType", propOrder = {
    "success",
    "failed",
    "partial"
})
public class StatusType {

    @XmlElement(name = "SUCCESS")
    private EmptyType success;
    @XmlElement(name = "FAILED")
    private EmptyType failed;
    @XmlElement(name = "PARTIAL")
    private EmptyType partial;

    /**
     * Gets the value of the success property.
     * 
     * @return
     *     possible object is
     *     {@link EmptyType }
     *     
     */
    public EmptyType getSUCCESS() {
        return success;
    }

    /**
     * Sets the value of the success property.
     * 
     * @param value
     *     allowed object is
     *     {@link EmptyType }
     *     
     */
    public void setSUCCESS(EmptyType value) {
        this.success = value;
    }

    /**
     * Gets the value of the failed property.
     * 
     * @return
     *     possible object is
     *     {@link EmptyType }
     *     
     */
    public EmptyType getFAILED() {
        return failed;
    }

    /**
     * Sets the value of the failed property.
     * 
     * @param value
     *     allowed object is
     *     {@link EmptyType }
     *     
     */
    public void setFAILED(EmptyType value) {
        this.failed = value;
    }

    /**
     * Gets the value of the partial property.
     * 
     * @return
     *     possible object is
     *     {@link EmptyType }
     *     
     */
    public EmptyType getPARTIAL() {
        return partial;
    }

    /**
     * Sets the value of the partial property.
     * 
     * @param value
     *     allowed object is
     *     {@link EmptyType }
     *     
     */
    public void setPARTIAL(EmptyType value) {
        this.partial = value;
    }

}
