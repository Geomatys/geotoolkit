/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2016, Geomatys
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

package org.geotoolkit.wps.xml.v200;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/wps/2.0}DataDescriptionType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/wps/2.0}SupportedCRS" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "supportedCRS"
})
public class BoundingBoxData extends DataDescriptionType {

    @XmlElement(name = "SupportedCRS", required = true)
    protected List<SupportedCRS> supportedCRS;

    public BoundingBoxData() {

    }

    public BoundingBoxData(List<SupportedCRS> supportedCRS) {
        this.supportedCRS = supportedCRS;
    }

    /**
     * Gets the value of the supportedCRS property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the supportedCRS property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSupportedCRS().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SupportedCRS }
     *
     *
     */
    public List<SupportedCRS> getSupportedCRS() {
        if (supportedCRS == null) {
            supportedCRS = new ArrayList<>();
        }
        return this.supportedCRS;
    }

}
