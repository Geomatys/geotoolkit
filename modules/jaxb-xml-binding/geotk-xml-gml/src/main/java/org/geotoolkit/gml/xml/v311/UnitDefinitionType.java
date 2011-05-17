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
package org.geotoolkit.gml.xml.v311;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.util.ComparisonMode;
import org.geotoolkit.util.Utilities;


/**
 * Definition of a unit of measure (or uom). The definition includes a quantityType property, which indicates the phenomenon to which the units apply, and a catalogSymbol, which gives the short symbol used for this unit. This element is used when the relationship of this unit to other units or units systems is unknown.
 * 
 * <p>Java class for UnitDefinitionType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UnitDefinitionType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml}DefinitionType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/gml}quantityType"/>
 *         &lt;element ref="{http://www.opengis.net/gml}catalogSymbol" minOccurs="0"/>
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
@XmlType(name = "UnitDefinitionType", propOrder = {
    "quantityType",
    "quantityTypeReference",
    "catalogSymbol"
})
@XmlSeeAlso({
    //DerivedUnitType.class,
    BaseUnitType.class//,
    //ConventionalUnitType.class
})
public class UnitDefinitionType extends DefinitionType {

    @XmlElement(required = true)
    protected StringOrRefType quantityType;
    private ReferenceType quantityTypeReference;
    protected CodeType catalogSymbol;

    /**
     * Gets the value of the quantityType property.
     * 
     * @return
     *     possible object is
     *     {@link StringOrRefType }
     *     
     */
    public StringOrRefType getQuantityType() {
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
    public void setQuantityType(final StringOrRefType value) {
        this.quantityType = value;
    }

    /**
     * Gets the value of the quantityTypeReference property.
     */
    public ReferenceType getQuantityTypeReference() {
        return quantityTypeReference;
    }

    /**
     * Sets the value of the quantityTypeReference property.
     */
    public void getQuantityTypeReference(final ReferenceType quantityTypeReference) {
        this.quantityTypeReference = quantityTypeReference;
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
    public void setCatalogSymbol(final CodeType value) {
        this.catalogSymbol = value;
    }

    /**
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object, final ComparisonMode mode) {
        if (object == this) {
            return true;
        }
        if (object instanceof UnitDefinitionType && super.equals(object, mode)) {
            final UnitDefinitionType that = (UnitDefinitionType) object;
            return Utilities.equals(this.catalogSymbol,        that.catalogSymbol)        &&
                   Utilities.equals(this.quantityType,        that.quantityType)        &&
                   Utilities.equals(this.quantityTypeReference,       that.quantityTypeReference);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 43 * hash + (this.quantityType != null ? this.quantityType.hashCode() : 0);
        hash = 43 * hash + (this.quantityTypeReference != null ? this.quantityTypeReference.hashCode() : 0);
        hash = 43 * hash + (this.catalogSymbol != null ? this.catalogSymbol.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("[UnitDefinitionType]");
        if (catalogSymbol != null)
            s.append("CatalogSymbol=").append(catalogSymbol.toString()).append('\n');
        if (quantityType != null)
            s.append("quantityType=").append(quantityType.toString()).append('\n');
        if (quantityTypeReference != null)
            s.append("quantityTypeReference=").append(quantityTypeReference).append('\n');
        return s.toString();
    }
}
