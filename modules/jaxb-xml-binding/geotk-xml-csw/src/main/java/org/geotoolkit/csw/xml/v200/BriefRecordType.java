/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Geomatys
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
package org.geotoolkit.csw.xml.v200;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.dublincore.xml.v1.elements.SimpleLiteral;


/**
 * 
 *       This type defines a brief representation of the common record format. 
 *       It extends AbstractRecordType to include only the dc:identifier and 
 *       dc:type properties.
 *       
 * 
 * <p>Java class for BriefRecordType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BriefRecordType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/cat/csw}AbstractRecordType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.purl.org/dc/elements/1.1/}identifier"/>
 *         &lt;element ref="{http://www.purl.org/dc/elements/1.1/}type" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BriefRecordType", propOrder = {
    "identifier",
    "type"
})
public class BriefRecordType extends AbstractRecordType {

    @XmlElement(name = "identifier", namespace = "http://www.purl.org/dc/elements/1.1/")
    private SimpleLiteral identifier;
    @XmlElement(namespace = "http://www.purl.org/dc/elements/1.1/")
    private SimpleLiteral type;

    /**
     * Gets the value of the identifier property.
     * 
     */
    public SimpleLiteral getIdentifier() {
        return identifier;
    }

    /**
     * Sets the value of the identifier property.
     * 
     */
    public void setIdentifier(final SimpleLiteral identifier) {
        this.identifier = identifier;
    }

    /**
     * Gets the value of the type property.
     * 
     */
    public SimpleLiteral getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     */
    public void setType(final SimpleLiteral value) {
        this.type = value;
    }

}
