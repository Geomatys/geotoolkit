/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2019, Geomatys
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

package org.geotoolkit.dif.xml.v102;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 *
 *
 *             | DIF 9    | ECHO 10 | UMM            | DIF 10   | Notes         |
 *             | -------- | ------- | -------------- | -------- | ------------- |
 *             | Location | Keyword | SpatialKeyword | Location | No change     |
 *
 *
 * <p>Classe Java pour LocationType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="LocationType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Location_Category" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Location_Type" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Location_Subregion1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Location_Subregion2" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Location_Subregion3" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Detailed_Location" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="uuid" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}UuidType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LocationType", propOrder = {
    "locationCategory",
    "locationType",
    "locationSubregion1",
    "locationSubregion2",
    "locationSubregion3",
    "detailedLocation"
})
public class LocationType {

    @XmlElement(name = "Location_Category", required = true)
    protected String locationCategory;
    @XmlElement(name = "Location_Type")
    protected String locationType;
    @XmlElement(name = "Location_Subregion1")
    protected String locationSubregion1;
    @XmlElement(name = "Location_Subregion2")
    protected String locationSubregion2;
    @XmlElement(name = "Location_Subregion3")
    protected String locationSubregion3;
    @XmlElement(name = "Detailed_Location")
    protected String detailedLocation;
    @XmlAttribute(name = "uuid")
    protected String uuid;

    public LocationType() {

    }

    public LocationType(String detailedLocation) {
        this.detailedLocation = detailedLocation;
    }

    /**
     * Obtient la valeur de la propriété locationCategory.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getLocationCategory() {
        return locationCategory;
    }

    /**
     * Définit la valeur de la propriété locationCategory.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setLocationCategory(String value) {
        this.locationCategory = value;
    }

    /**
     * Obtient la valeur de la propriété locationType.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getLocationType() {
        return locationType;
    }

    /**
     * Définit la valeur de la propriété locationType.
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
     * Obtient la valeur de la propriété locationSubregion1.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getLocationSubregion1() {
        return locationSubregion1;
    }

    /**
     * Définit la valeur de la propriété locationSubregion1.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setLocationSubregion1(String value) {
        this.locationSubregion1 = value;
    }

    /**
     * Obtient la valeur de la propriété locationSubregion2.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getLocationSubregion2() {
        return locationSubregion2;
    }

    /**
     * Définit la valeur de la propriété locationSubregion2.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setLocationSubregion2(String value) {
        this.locationSubregion2 = value;
    }

    /**
     * Obtient la valeur de la propriété locationSubregion3.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getLocationSubregion3() {
        return locationSubregion3;
    }

    /**
     * Définit la valeur de la propriété locationSubregion3.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setLocationSubregion3(String value) {
        this.locationSubregion3 = value;
    }

    /**
     * Obtient la valeur de la propriété detailedLocation.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getDetailedLocation() {
        return detailedLocation;
    }

    /**
     * Définit la valeur de la propriété detailedLocation.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setDetailedLocation(String value) {
        this.detailedLocation = value;
    }

    /**
     * Obtient la valeur de la propriété uuid.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Définit la valeur de la propriété uuid.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setUuid(String value) {
        this.uuid = value;
    }

}
