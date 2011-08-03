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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * Defines the list of areas, locations, and types of features in which the route should avoid passing through.
 * 
 * <p>Java class for AvoidListType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AvoidListType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/xls}AOI" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/xls}_Location" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/xls}AvoidFeature" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AvoidListType", propOrder = {
    "aoi",
    "location",
    "avoidFeature"
})
public class AvoidListType {

    @XmlElement(name = "AOI")
    private List<AreaOfInterestType> aoi;
    @XmlElementRef(name = "_Location", namespace = "http://www.opengis.net/xls", type = JAXBElement.class)
    private List<JAXBElement<? extends AbstractLocationType>> location;
    @XmlElement(name = "AvoidFeature")
    private List<AvoidFeatureType> avoidFeature;

    /**
     * List of geographic areas to avoid.Gets the value of the aoi property.
     * 
     */
    public List<AreaOfInterestType> getAOI() {
        if (aoi == null) {
            aoi = new ArrayList<AreaOfInterestType>();
        }
        return this.aoi;
    }

    /**
     * List of locations to avoid.Gets the value of the location property.
     * 
     */
    public List<JAXBElement<? extends AbstractLocationType>> getLocation() {
        if (location == null) {
            location = new ArrayList<JAXBElement<? extends AbstractLocationType>>();
        }
        return this.location;
    }

    /**
     * Gets the value of the avoidFeature property.
     */
    public List<AvoidFeatureType> getAvoidFeature() {
        if (avoidFeature == null) {
            avoidFeature = new ArrayList<AvoidFeatureType>();
        }
        return this.avoidFeature;
    }

}
