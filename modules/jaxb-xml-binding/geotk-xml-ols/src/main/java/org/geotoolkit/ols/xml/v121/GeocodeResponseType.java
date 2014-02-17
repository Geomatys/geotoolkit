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
 * GeocodeResponse. The addresses returned will be normalized Address ADTs as a result of any parsing by the geocoder, etc.
 * 
 * <p>Java class for GeocodeResponseType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GeocodeResponseType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/xls}AbstractResponseParametersType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/xls}GeocodeResponseList" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GeocodeResponseType", propOrder = {
    "geocodeResponseList"
})
public class GeocodeResponseType extends AbstractResponseParametersType {

    @XmlElement(name = "GeocodeResponseList", required = true)
    protected List<GeocodeResponseListType> geocodeResponseList;

    /**
     * Gets the value of the geocodeResponseList property.
     * 
     */
    public List<GeocodeResponseListType> getGeocodeResponseList() {
        if (geocodeResponseList == null) {
            geocodeResponseList = new ArrayList<GeocodeResponseListType>();
        }
        return this.geocodeResponseList;
    }

}
