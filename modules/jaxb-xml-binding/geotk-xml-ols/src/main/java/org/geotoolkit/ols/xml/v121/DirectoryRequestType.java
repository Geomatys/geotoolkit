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

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * ADT for a DirectoryRequest
 * 
 * <p>Java class for DirectoryRequestType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DirectoryRequestType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/xls}AbstractRequestParametersType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/xls}POILocation" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/xls}_POISelectionCriteria"/>
 *       &lt;/sequence>
 *       &lt;attribute name="sortCriteria" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="sortDirection" type="{http://www.opengis.net/xls}SortDirectionType" default="Ascending" />
 *       &lt;attribute name="distanceUnit" type="{http://www.opengis.net/xls}DistanceUnitType" default="M" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DirectoryRequestType", propOrder = {
    "poiLocation",
    "poiSelectionCriteria"
})
public class DirectoryRequestType extends AbstractRequestParametersType {

    @XmlElement(name = "POILocation")
    private POILocationType poiLocation;
    @XmlElementRef(name = "_POISelectionCriteria", namespace = "http://www.opengis.net/xls", type = JAXBElement.class)
    private JAXBElement<? extends AbstractPOISelectionCriteriaType> poiSelectionCriteria;
    @XmlAttribute
    private String sortCriteria;
    @XmlAttribute
    private SortDirectionType sortDirection;
    @XmlAttribute
    private DistanceUnitType distanceUnit;

    /**
     * Gets the value of the poiLocation property.
     * 
     * @return
     *     possible object is
     *     {@link POILocationType }
     *     
     */
    public POILocationType getPOILocation() {
        return poiLocation;
    }

    /**
     * Sets the value of the poiLocation property.
     * 
     * @param value
     *     allowed object is
     *     {@link POILocationType }
     *     
     */
    public void setPOILocation(POILocationType value) {
        this.poiLocation = value;
    }

    /**
     * Gets the value of the poiSelectionCriteria property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link AbstractPOISelectionCriteriaType }{@code >}
     *     {@link JAXBElement }{@code <}{@link POIProperties }{@code >}
     *     
     */
    public JAXBElement<? extends AbstractPOISelectionCriteriaType> getPOISelectionCriteria() {
        return poiSelectionCriteria;
    }

    /**
     * Sets the value of the poiSelectionCriteria property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link AbstractPOISelectionCriteriaType }{@code >}
     *     {@link JAXBElement }{@code <}{@link POIProperties }{@code >}
     *     
     */
    public void setPOISelectionCriteria(JAXBElement<? extends AbstractPOISelectionCriteriaType> value) {
        this.poiSelectionCriteria = ((JAXBElement<? extends AbstractPOISelectionCriteriaType> ) value);
    }

    /**
     * Gets the value of the sortCriteria property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSortCriteria() {
        return sortCriteria;
    }

    /**
     * Sets the value of the sortCriteria property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSortCriteria(String value) {
        this.sortCriteria = value;
    }

    /**
     * Gets the value of the sortDirection property.
     * 
     * @return
     *     possible object is
     *     {@link SortDirectionType }
     *     
     */
    public SortDirectionType getSortDirection() {
        if (sortDirection == null) {
            return SortDirectionType.ASCENDING;
        } else {
            return sortDirection;
        }
    }

    /**
     * Sets the value of the sortDirection property.
     * 
     * @param value
     *     allowed object is
     *     {@link SortDirectionType }
     *     
     */
    public void setSortDirection(SortDirectionType value) {
        this.sortDirection = value;
    }

    /**
     * Gets the value of the distanceUnit property.
     * 
     * @return
     *     possible object is
     *     {@link DistanceUnitType }
     *     
     */
    public DistanceUnitType getDistanceUnit() {
        if (distanceUnit == null) {
            return DistanceUnitType.M;
        } else {
            return distanceUnit;
        }
    }

    /**
     * Sets the value of the distanceUnit property.
     * 
     * @param value
     *     allowed object is
     *     {@link DistanceUnitType }
     *     
     */
    public void setDistanceUnit(DistanceUnitType value) {
        this.distanceUnit = value;
    }

}
