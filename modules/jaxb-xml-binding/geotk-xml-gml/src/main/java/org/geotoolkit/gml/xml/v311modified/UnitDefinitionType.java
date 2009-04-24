/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2007 - 2008, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 3 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.gml.xml.v311modified;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.util.Utilities;


/**
 * <p>Java class for UnitDefinitionType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UnitDefinitionType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml}DefinitionType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/gml}quantityType" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/gml}quantityTypeReference" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/gml}catalogSymbol" minOccurs="0"/>
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
@XmlSeeAlso({ BaseUnitType.class }) 
public class UnitDefinitionType extends DefinitionType {

    private StringOrRefType quantityType;
    private ReferenceEntry quantityTypeReference;
    private CodeType catalogSymbol;

    /**
     * Gets the value of the quantityType property.
     */
    public StringOrRefType getQuantityType() {
        return quantityType;
    }

    /**
     * Gets the value of the quantityTypeReference property.
     */
    public ReferenceEntry getQuantityTypeReference() {
        return quantityTypeReference;
    }

    /**
     * Gets the value of the catalogSymbol property.
     */
    public CodeType getCatalogSymbol() {
        return catalogSymbol;
    }

    /**
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof UnitDefinitionType && super.equals(object)) {
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
