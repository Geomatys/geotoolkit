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
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for QualityOfPositionType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="QualityOfPositionType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="HorizontalAcc" type="{http://www.opengis.net/xls}HorAccType"/>
 *         &lt;element name="VerticalAcc" type="{http://www.opengis.net/xls}VerAccType"/>
 *       &lt;/sequence>
 *       &lt;attribute name="responseReq" default="Delay_Tol">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="No_Delay"/>
 *             &lt;enumeration value="Low_Delay"/>
 *             &lt;enumeration value="Delay_Tol"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="responseTimer" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "QualityOfPositionType", propOrder = {
    "horizontalAcc",
    "verticalAcc"
})
public class QualityOfPositionType {

    @XmlElement(name = "HorizontalAcc", required = true)
    private HorAccType horizontalAcc;
    @XmlElement(name = "VerticalAcc", required = true)
    private VerAccType verticalAcc;
    @XmlAttribute
    private String responseReq;
    @XmlAttribute
    private String responseTimer;

    /**
     * Gets the value of the horizontalAcc property.
     * 
     * @return
     *     possible object is
     *     {@link HorAccType }
     *     
     */
    public HorAccType getHorizontalAcc() {
        return horizontalAcc;
    }

    /**
     * Sets the value of the horizontalAcc property.
     * 
     * @param value
     *     allowed object is
     *     {@link HorAccType }
     *     
     */
    public void setHorizontalAcc(HorAccType value) {
        this.horizontalAcc = value;
    }

    /**
     * Gets the value of the verticalAcc property.
     * 
     * @return
     *     possible object is
     *     {@link VerAccType }
     *     
     */
    public VerAccType getVerticalAcc() {
        return verticalAcc;
    }

    /**
     * Sets the value of the verticalAcc property.
     * 
     * @param value
     *     allowed object is
     *     {@link VerAccType }
     *     
     */
    public void setVerticalAcc(VerAccType value) {
        this.verticalAcc = value;
    }

    /**
     * Gets the value of the responseReq property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResponseReq() {
        if (responseReq == null) {
            return "Delay_Tol";
        } else {
            return responseReq;
        }
    }

    /**
     * Sets the value of the responseReq property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResponseReq(String value) {
        this.responseReq = value;
    }

    /**
     * Gets the value of the responseTimer property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResponseTimer() {
        return responseTimer;
    }

    /**
     * Sets the value of the responseTimer property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResponseTimer(String value) {
        this.responseTimer = value;
    }

}
