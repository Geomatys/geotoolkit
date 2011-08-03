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
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Reverse Geocode Request.
 * 
 * <p>Java class for ReverseGeocodeRequestType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ReverseGeocodeRequestType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/xls}AbstractRequestParametersType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/xls}Position"/>
 *         &lt;element ref="{http://www.opengis.net/xls}ReverseGeocodePreference" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReverseGeocodeRequestType", propOrder = {
    "position",
    "reverseGeocodePreference"
})
public class ReverseGeocodeRequestType extends AbstractRequestParametersType {

    @XmlElement(name = "Position", required = true)
    private PositionType position;
    @XmlElement(name = "ReverseGeocodePreference")
    private List<ReverseGeocodePreferenceType> reverseGeocodePreference;

    /**
     * Gets the value of the position property.
     * 
     * @return
     *     possible object is
     *     {@link PositionType }
     *     
     */
    public PositionType getPosition() {
        return position;
    }

    /**
     * Sets the value of the position property.
     * 
     * @param value
     *     allowed object is
     *     {@link PositionType }
     *     
     */
    public void setPosition(PositionType value) {
        this.position = value;
    }

    /**
     * Gets the value of the reverseGeocodePreference property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the reverseGeocodePreference property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getReverseGeocodePreference().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ReverseGeocodePreferenceType }
     * 
     * 
     */
    public List<ReverseGeocodePreferenceType> getReverseGeocodePreference() {
        if (reverseGeocodePreference == null) {
            reverseGeocodePreference = new ArrayList<ReverseGeocodePreferenceType>();
        }
        return this.reverseGeocodePreference;
    }

}
