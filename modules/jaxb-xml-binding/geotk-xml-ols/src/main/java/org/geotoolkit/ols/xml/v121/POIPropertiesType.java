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
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * Defines POI selection criteria as a list of properties
 * 
 * <p>Java class for POIPropertiesType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="POIPropertiesType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/xls}AbstractPOISelectionCriteriaType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/xls}AbstractPOIProperty" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="directoryType" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "POIPropertiesType", propOrder = {
    "abstractPOIProperty"
})
@XmlSeeAlso({
    POIProperties.class
})
public class POIPropertiesType extends AbstractPOISelectionCriteriaType {

    @XmlElementRef(name = "AbstractPOIProperty", namespace = "http://www.opengis.net/xls", type = JAXBElement.class)
    private List<JAXBElement<?>> abstractPOIProperty;
    @XmlAttribute
    private String directoryType;

    /**
     * Gets the value of the abstractPOIProperty property.
     * 
     */
    public List<JAXBElement<?>> getAbstractPOIProperty() {
        if (abstractPOIProperty == null) {
            abstractPOIProperty = new ArrayList<JAXBElement<?>>();
        }
        return this.abstractPOIProperty;
    }

    /**
     * Gets the value of the directoryType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDirectoryType() {
        return directoryType;
    }

    /**
     * Sets the value of the directoryType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDirectoryType(String value) {
        this.directoryType = value;
    }

}
