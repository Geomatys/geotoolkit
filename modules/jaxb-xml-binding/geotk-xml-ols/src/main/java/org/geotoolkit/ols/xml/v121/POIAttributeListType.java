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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for POIAttributeListType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="POIAttributeListType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/xls}ReferenceSystem" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/xls}POIInfoList" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "POIAttributeListType", propOrder = {
    "referenceSystem",
    "poiInfoList"
})
public class POIAttributeListType {

    @XmlElement(name = "ReferenceSystem")
    private ReferenceSystemType referenceSystem;
    @XmlElement(name = "POIInfoList")
    private POIInfoListType poiInfoList;

    /**
     * Gets the value of the referenceSystem property.
     * 
     * @return
     *     possible object is
     *     {@link ReferenceSystemType }
     *     
     */
    public ReferenceSystemType getReferenceSystem() {
        return referenceSystem;
    }

    /**
     * Sets the value of the referenceSystem property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReferenceSystemType }
     *     
     */
    public void setReferenceSystem(ReferenceSystemType value) {
        this.referenceSystem = value;
    }

    /**
     * Gets the value of the poiInfoList property.
     * 
     * @return
     *     possible object is
     *     {@link POIInfoListType }
     *     
     */
    public POIInfoListType getPOIInfoList() {
        return poiInfoList;
    }

    /**
     * Sets the value of the poiInfoList property.
     * 
     * @param value
     *     allowed object is
     *     {@link POIInfoListType }
     *     
     */
    public void setPOIInfoList(POIInfoListType value) {
        this.poiInfoList = value;
    }

}
