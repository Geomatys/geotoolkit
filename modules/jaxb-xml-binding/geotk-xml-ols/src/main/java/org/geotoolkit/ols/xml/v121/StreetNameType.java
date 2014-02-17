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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import org.geotoolkit.gml.xml.v311.CompassPointEnumeration;


/**
 * The data elements that make up the name of a street. There are two valid methods for encoding this information: 1). Use the structured elements and attributes. 2). The element value may contain a simplified string (e.g. West 83rd. Street).
 * An example:     
 * <pre>
 * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;Street xmlns="http://www.w3.org/2001/XMLSchema" xmlns:gml="http://www.opengis.net/gml" xmlns:xls="http://www.opengis.net/xls" directionalPrefix="W" officialName="83RD" typeSuffix="ST"/&gt;
 * </pre>
 * 
 * 			
 * 
 * <p>Java class for StreetNameType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="StreetNameType">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *       &lt;attribute name="directionalPrefix" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="typePrefix" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="officialName" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="typeSuffix" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="directionalSuffix" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="muniOctant" type="{http://www.opengis.net/gml}CompassPointEnumeration" />
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StreetNameType", propOrder = {
    "value"
})
public class StreetNameType {

    @XmlValue
    private String value;
    @XmlAttribute
    private String directionalPrefix;
    @XmlAttribute
    private String typePrefix;
    @XmlAttribute
    private String officialName;
    @XmlAttribute
    private String typeSuffix;
    @XmlAttribute
    private String directionalSuffix;
    @XmlAttribute
    private CompassPointEnumeration muniOctant;

    /**
     * Gets the value of the value property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Gets the value of the directionalPrefix property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDirectionalPrefix() {
        return directionalPrefix;
    }

    /**
     * Sets the value of the directionalPrefix property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDirectionalPrefix(String value) {
        this.directionalPrefix = value;
    }

    /**
     * Gets the value of the typePrefix property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTypePrefix() {
        return typePrefix;
    }

    /**
     * Sets the value of the typePrefix property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTypePrefix(String value) {
        this.typePrefix = value;
    }

    /**
     * Gets the value of the officialName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOfficialName() {
        return officialName;
    }

    /**
     * Sets the value of the officialName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOfficialName(String value) {
        this.officialName = value;
    }

    /**
     * Gets the value of the typeSuffix property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTypeSuffix() {
        return typeSuffix;
    }

    /**
     * Sets the value of the typeSuffix property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTypeSuffix(String value) {
        this.typeSuffix = value;
    }

    /**
     * Gets the value of the directionalSuffix property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDirectionalSuffix() {
        return directionalSuffix;
    }

    /**
     * Sets the value of the directionalSuffix property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDirectionalSuffix(String value) {
        this.directionalSuffix = value;
    }

    /**
     * Gets the value of the muniOctant property.
     * 
     * @return
     *     possible object is
     *     {@link CompassPointEnumeration }
     *     
     */
    public CompassPointEnumeration getMuniOctant() {
        return muniOctant;
    }

    /**
     * Sets the value of the muniOctant property.
     * 
     * @param value
     *     allowed object is
     *     {@link CompassPointEnumeration }
     *     
     */
    public void setMuniOctant(CompassPointEnumeration value) {
        this.muniOctant = value;
    }

}
