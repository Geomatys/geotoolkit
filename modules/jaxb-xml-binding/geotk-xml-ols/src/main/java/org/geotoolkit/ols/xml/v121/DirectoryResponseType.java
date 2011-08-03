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
 * ADT for a ProximityResponse. One or more POIs may be returned
 * 
 * <p>Java class for DirectoryResponseType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DirectoryResponseType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/xls}AbstractResponseParametersType">
 *       &lt;sequence>
 *         &lt;element name="POIContext" type="{http://www.opengis.net/xls}POIWithDistanceType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DirectoryResponseType", propOrder = {
    "poiContext"
})
public class DirectoryResponseType extends AbstractResponseParametersType {

    @XmlElement(name = "POIContext")
    private List<POIWithDistanceType> poiContext;

    /**
     * Gets the value of the poiContext property.
     */
    public List<POIWithDistanceType> getPOIContext() {
        if (poiContext == null) {
            poiContext = new ArrayList<POIWithDistanceType>();
        }
        return this.poiContext;
    }

}
