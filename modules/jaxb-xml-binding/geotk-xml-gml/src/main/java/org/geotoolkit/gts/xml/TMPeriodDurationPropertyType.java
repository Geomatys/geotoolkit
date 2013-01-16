/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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

package org.geotoolkit.gts.xml;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.Duration;


/**
 * <p>Java class for TM_PeriodDuration_PropertyType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TM_PeriodDuration_PropertyType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence minOccurs="0">
 *         &lt;element ref="{http://www.isotc211.org/2005/gts}TM_PeriodDuration"/>
 *       &lt;/sequence>
 *       &lt;attribute ref="{http://www.isotc211.org/2005/gco}nilReason"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TM_PeriodDuration_PropertyType", propOrder = {
    "tmPeriodDuration"
})
public class TMPeriodDurationPropertyType {

    @XmlElement(name = "TM_PeriodDuration")
    protected Duration tmPeriodDuration;
    @XmlAttribute(namespace = "http://www.isotc211.org/2005/gco")
    protected List<String> nilReason;

    /**
     * Gets the value of the tmPeriodDuration property.
     * 
     * @return
     *     possible object is
     *     {@link Duration }
     *     
     */
    public Duration getTMPeriodDuration() {
        return tmPeriodDuration;
    }

    /**
     * Sets the value of the tmPeriodDuration property.
     * 
     * @param value
     *     allowed object is
     *     {@link Duration }
     *     
     */
    public void setTMPeriodDuration(Duration value) {
        this.tmPeriodDuration = value;
    }

    /**
     * Gets the value of the nilReason property.
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     */
    public List<String> getNilReason() {
        if (nilReason == null) {
            nilReason = new ArrayList<String>();
        }
        return this.nilReason;
    }

}
