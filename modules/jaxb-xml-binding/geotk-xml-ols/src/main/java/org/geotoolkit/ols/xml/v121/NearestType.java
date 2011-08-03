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

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * Defines a spatial filter which selects the POI nearest to the specified location.
 * 
 * <p>Java class for NearestType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="NearestType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/xls}_Location" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *       &lt;attribute name="nearestCriterion" type="{http://www.opengis.net/xls}NearestCriterionType" default="Proximity" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NearestType", propOrder = {
    "location"
})
public class NearestType {

    @XmlElementRef(name = "_Location", namespace = "http://www.opengis.net/xls", type = JAXBElement.class)
    private List<JAXBElement<? extends AbstractLocationType>> location;
    @XmlAttribute
    private NearestCriterionType nearestCriterion;

    /**
     * Gets the value of the location property.
     * 
    */
    public List<JAXBElement<? extends AbstractLocationType>> getLocation() {
        if (location == null) {
            location = new ArrayList<JAXBElement<? extends AbstractLocationType>>();
        }
        return this.location;
    }

    /**
     * Gets the value of the nearestCriterion property.
     * 
     * @return
     *     possible object is
     *     {@link NearestCriterionType }
     *     
     */
    public NearestCriterionType getNearestCriterion() {
        if (nearestCriterion == null) {
            return NearestCriterionType.PROXIMITY;
        } else {
            return nearestCriterion;
        }
    }

    /**
     * Sets the value of the nearestCriterion property.
     * 
     * @param value
     *     allowed object is
     *     {@link NearestCriterionType }
     *     
     */
    public void setNearestCriterion(NearestCriterionType value) {
        this.nearestCriterion = value;
    }

}
