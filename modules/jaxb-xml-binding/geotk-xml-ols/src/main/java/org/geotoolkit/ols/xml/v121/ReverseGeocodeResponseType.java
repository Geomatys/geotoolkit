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
 * ADT for a ReverseGeocodeResponse. One or more addresses may be returned
 * 
 * <p>Java class for ReverseGeocodeResponseType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ReverseGeocodeResponseType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/xls}AbstractResponseParametersType">
 *       &lt;sequence>
 *         &lt;element name="ReverseGeocodedLocation" type="{http://www.opengis.net/xls}ReverseGeocodedLocationType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReverseGeocodeResponseType", propOrder = {
    "reverseGeocodedLocation"
})
public class ReverseGeocodeResponseType extends AbstractResponseParametersType {

    @XmlElement(name = "ReverseGeocodedLocation")
    private List<ReverseGeocodedLocationType> reverseGeocodedLocation;

    /**
     * Gets the value of the reverseGeocodedLocation property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the reverseGeocodedLocation property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getReverseGeocodedLocation().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ReverseGeocodedLocationType }
     * 
     * 
     */
    public List<ReverseGeocodedLocationType> getReverseGeocodedLocation() {
        if (reverseGeocodedLocation == null) {
            reverseGeocodedLocation = new ArrayList<ReverseGeocodedLocationType>();
        }
        return this.reverseGeocodedLocation;
    }

}
