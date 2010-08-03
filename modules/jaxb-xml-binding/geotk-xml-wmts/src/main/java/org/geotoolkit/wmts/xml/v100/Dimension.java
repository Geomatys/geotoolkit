/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2010, Geomatys
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
package org.geotoolkit.wmts.xml.v100;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.ows.xml.v110.CodeType;
import org.geotoolkit.ows.xml.v110.DescriptionType;
import org.geotoolkit.ows.xml.v110.DomainMetadataType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/ows/1.1}DescriptionType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/ows/1.1}Identifier"/>
 *         &lt;element ref="{http://www.opengis.net/ows/1.1}UOM" minOccurs="0"/>
 *         &lt;element name="UnitSymbol" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Default" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Current" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="Value" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded"/>
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
@XmlType(name = "", propOrder = {
    "identifier",
    "uom",
    "unitSymbol",
    "_default",
    "current",
    "value"
})
@XmlRootElement(name = "Dimension")
public class Dimension extends DescriptionType {

    @XmlElement(name = "Identifier", namespace = "http://www.opengis.net/ows/1.1", required = true)
    private CodeType identifier;
    @XmlElement(name = "UOM", namespace = "http://www.opengis.net/ows/1.1")
    private DomainMetadataType uom;
    @XmlElement(name = "UnitSymbol")
    private String unitSymbol;
    @XmlElement(name = "Default")
    private String _default;
    @XmlElement(name = "Current")
    private Boolean current;
    @XmlElement(name = "Value", required = true)
    private List<String> value;

    public Dimension() {

    }

    public Dimension(String identifier, String unit, String _default) {
        this.identifier = new CodeType(identifier);
        this.unitSymbol = unit;
        this._default   = _default;

    }

    public Dimension(String identifier, String unit, String _default, String value) {
        this.identifier = new CodeType(identifier);
        this.unitSymbol = unit;
        this._default   = _default;
        this.value = new ArrayList<String>();
        this.value.add(value);
    }

    /**
     * A name of dimensional axis
     * 
     * @return
     *     possible object is
     *     {@link CodeType }
     *     
     */
    public CodeType getIdentifier() {
        return identifier;
    }

    /**
     * Sets the value of the identifier property.
     * 
     * @param value
     *     allowed object is
     *     {@link CodeType }
     *     
     */
    public void setIdentifier(CodeType value) {
        this.identifier = value;
    }

    /**
     * Units of measure of dimensional axis.
     * 
     * @return
     *     possible object is
     *     {@link DomainMetadataType }
     *     
     */
    public DomainMetadataType getUOM() {
        return uom;
    }

    /**
     * Sets the value of the uom property.
     * 
     * @param value
     *     allowed object is
     *     {@link DomainMetadataType }
     *     
     */
    public void setUOM(DomainMetadataType value) {
        this.uom = value;
    }

    /**
     * Gets the value of the unitSymbol property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUnitSymbol() {
        return unitSymbol;
    }

    /**
     * Sets the value of the unitSymbol property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUnitSymbol(String value) {
        this.unitSymbol = value;
    }

    /**
     * Gets the value of the default property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDefault() {
        return _default;
    }

    /**
     * Sets the value of the default property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDefault(String value) {
        this._default = value;
    }

    /**
     * Gets the value of the current property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isCurrent() {
        return current;
    }

    /**
     * Sets the value of the current property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setCurrent(Boolean value) {
        this.current = value;
    }

    /**
     * Gets the value of the value property.
     */
    public List<String> getValue() {
        if (value == null) {
            value = new ArrayList<String>();
        }
        return this.value;
    }

    public void setValue(List<String> value) {
        this.value = value;
}

    public void setValue(String value) {
        if (this.value == null) {
            this.value = new ArrayList<String>();
        }
        this.value.add(value);
    }

}
