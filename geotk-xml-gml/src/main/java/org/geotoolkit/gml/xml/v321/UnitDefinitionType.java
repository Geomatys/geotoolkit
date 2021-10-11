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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for UnitDefinitionType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="UnitDefinitionType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml/3.2}DefinitionType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/gml/3.2}quantityType" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/gml/3.2}quantityTypeReference" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/gml/3.2}catalogSymbol" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UnitDefinitionType", propOrder = {
    "quantityType",
    "quantityTypeReference",
    "catalogSymbol"
})
@XmlSeeAlso({
    DerivedUnitType.class,
    ConventionalUnitType.class,
    BaseUnitType.class
})
public class UnitDefinitionType
    extends DefinitionType
{

    private StringOrRefType quantityType;
    private ReferenceType quantityTypeReference;
    private CodeType catalogSymbol;

    /**
     * Gets the value of the quantityType property.
     *
     * @return
     *     possible object is
     *     {@link StringOrRefType }
     *
     */
    public StringOrRefType getQuantityTypeRef() {
        return quantityType;
    }

    /**
     * Sets the value of the quantityType property.
     *
     * @param value
     *     allowed object is
     *     {@link StringOrRefType }
     *
     */
    public void setQuantityType(StringOrRefType value) {
        this.quantityType = value;
    }

    public String getQuantityType() {
        if (quantityType != null) {
            return quantityType.getHref();
        }
        return null;
    }

    /**
     * Gets the value of the quantityTypeReference property.
     *
     * @return
     *     possible object is
     *     {@link ReferenceType }
     *
     */
    public ReferenceType getQuantityTypeReference() {
        return quantityTypeReference;
    }

    /**
     * Sets the value of the quantityTypeReference property.
     *
     * @param value
     *     allowed object is
     *     {@link ReferenceType }
     *
     */
    public void setQuantityTypeReference(ReferenceType value) {
        this.quantityTypeReference = value;
    }

    /**
     * Gets the value of the catalogSymbol property.
     *
     * @return
     *     possible object is
     *     {@link CodeType }
     *
     */
    public CodeType getCatalogSymbol() {
        return catalogSymbol;
    }

    /**
     * Sets the value of the catalogSymbol property.
     *
     * @param value
     *     allowed object is
     *     {@link CodeType }
     *
     */
    public void setCatalogSymbol(CodeType value) {
        this.catalogSymbol = value;
    }

}
