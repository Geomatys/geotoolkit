/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2012, Geomatys
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


package org.geotoolkit.gml.xml.v321;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for IndirectEntryType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="IndirectEntryType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/gml/3.2}DefinitionProxy"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "IndirectEntryType", propOrder = {
    "definitionProxy"
})
public class IndirectEntryType {

    @XmlElement(name = "DefinitionProxy", required = true)
    private DefinitionProxyType definitionProxy;

    /**
     * Gets the value of the definitionProxy property.
     *
     * @return
     *     possible object is
     *     {@link DefinitionProxyType }
     *
     */
    public DefinitionProxyType getDefinitionProxy() {
        return definitionProxy;
    }

    /**
     * Sets the value of the definitionProxy property.
     *
     * @param value
     *     allowed object is
     *     {@link DefinitionProxyType }
     *
     */
    public void setDefinitionProxy(DefinitionProxyType value) {
        this.definitionProxy = value;
    }

}
