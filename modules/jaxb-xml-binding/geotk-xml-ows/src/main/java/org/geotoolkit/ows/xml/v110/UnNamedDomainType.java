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
package org.geotoolkit.ows.xml.v110;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.util.Utilities;


/**
 * Valid domain (or allowed set of values) of one quantity, with needed metadata but without a quantity name or identifier. 
 * 
 * <p>Java class for UnNamedDomainType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UnNamedDomainType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;group ref="{http://www.opengis.net/ows/1.1}PossibleValues"/>
 *         &lt;element ref="{http://www.opengis.net/ows/1.1}DefaultValue" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/ows/1.1}Meaning" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/ows/1.1}DataType" minOccurs="0"/>
 *         &lt;group ref="{http://www.opengis.net/ows/1.1}ValuesUnit" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/ows/1.1}Metadata" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * @author Guilhem Legal
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UnNamedDomainType", propOrder = {
    "allowedValues",
    "anyValue",
    "noValues",
    "valuesReference",
    "defaultValue",
    "meaning",
    "dataType",
    "uom",
    "referenceSystem",
    "metadata"
})
@XmlSeeAlso({
    DomainType.class
})
public class UnNamedDomainType {

    @XmlElement(name = "AllowedValues")
    private AllowedValues allowedValues;
    @XmlElement(name = "AnyValue")
    private AnyValue anyValue;
    @XmlElement(name = "NoValues")
    private NoValues noValues;
    @XmlElement(name = "ValuesReference")
    private ValuesReference valuesReference;
    @XmlElement(name = "DefaultValue")
    private ValueType defaultValue;
    @XmlElement(name = "Meaning")
    private DomainMetadataType meaning;
    @XmlElement(name = "DataType")
    private DomainMetadataType dataType;
    @XmlElement(name = "UOM")
    private DomainMetadataType uom;
    @XmlElement(name = "ReferenceSystem")
    private DomainMetadataType referenceSystem;
    @XmlElement(name = "Metadata")
    private List<MetadataType> metadata;

    /**
     * Empty constructor used by JAXB.
     */
    UnNamedDomainType() {
        
    }
    
    /**
     * Empty constructor used by JAXB.
     */
    public UnNamedDomainType(AnyValue anyValue) {
        if (anyValue == null) {
            this.anyValue = new AnyValue();
        } else {
            this.anyValue = anyValue;
        }
    }
    
    
    /**
     * Build a new Un-named Domain.
     */
    public UnNamedDomainType(AllowedValues allowedValues, AnyValue anyValue, ValuesReference valuesReference, 
            NoValues noValues, ValueType defaultValue, DomainMetadataType meaning, DomainMetadataType dataType,
            DomainMetadataType uom, DomainMetadataType referenceSystem,List<MetadataType> metadata) {
        this.allowedValues   = allowedValues;
        this.anyValue        = anyValue;
        this.dataType        = dataType;
        this.defaultValue    = defaultValue;
        this.meaning         = meaning;
        this.metadata        = metadata;
        this.noValues        = noValues;
        this.referenceSystem = referenceSystem;
        this.uom             = uom;
        this.valuesReference = valuesReference;
        
    }
            
    /**
     * Gets the value of the allowedValues property.
     */
    public AllowedValues getAllowedValues() {
        return allowedValues;
    }
    
    /**
     * Gets the value of the allowedValues property.
     */
    public void setAllowedValues(AllowedValues allowedValues) {
        this.allowedValues = allowedValues;
    }

    /**
     * Gets the value of the anyValue property.
     */
    public AnyValue getAnyValue() {
        return anyValue;
    }

    /**
     * Gets the value of the noValues property.
     */
    public NoValues getNoValues() {
        return noValues;
    }

    /**
     * Gets the value of the valuesReference property.
     */
    public ValuesReference getValuesReference() {
        return valuesReference;
    }

    /**
     * Optional default value for this quantity, 
     * which should be included when this quantity has a default value. 
     */
    public ValueType getDefaultValue() {
        return defaultValue;
    }

    /**
     * Meaning metadata should be referenced or included for each quantity. 
     */
    public DomainMetadataType getMeaning() {
        return meaning;
    }

    /**
     * This data type metadata should be referenced or included for each quantity. 
     */
    public DomainMetadataType getDataType() {
        return dataType;
    }

    /**
     * Identifier of unit of measure of this set of values. 
     * Should be included then this set of values has units (and not a more complete reference system). 
     * 
     */
    public DomainMetadataType getUOM() {
        return uom;
    }

    /**
     * Identifier of reference system used by this set of values. 
     * Should be included then this set of values has a reference system (not just units). 
     */
    public DomainMetadataType getReferenceSystem() {
        return referenceSystem;
    }

    /**
     * Optional unordered list of other metadata about this quantity. 
     * A list of required and optional other metadata elements for this quantity should be specified in the Implementation Specification for this service. 
     * Gets the value of the metadata property.
     */
    public List<MetadataType> getMetadata() {
        if (metadata == null) {
            metadata = new ArrayList<MetadataType>();
        }
        return metadata;
    }
    
    /**
     * Verify that this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof UnNamedDomainType) {
            final UnNamedDomainType that = (UnNamedDomainType) object;

            return Utilities.equals(this.allowedValues,   that.allowedValues)   &&
                   Utilities.equals(this.anyValue,        that.anyValue)        &&
                   Utilities.equals(this.dataType,        that.dataType)        &&
                   Utilities.equals(this.defaultValue,    that.defaultValue)    &&
                   Utilities.equals(this.meaning,         that.meaning)         &&
                   Utilities.equals(this.metadata,        that.metadata)        &&
                   Utilities.equals(this.noValues,        that.noValues)        &&
                   Utilities.equals(this.referenceSystem, that.referenceSystem) &&
                   Utilities.equals(this.uom,             that.uom)             &&
                   Utilities.equals(this.valuesReference, that.valuesReference);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + (this.allowedValues != null ? this.allowedValues.hashCode() : 0);
        hash = 83 * hash + (this.anyValue != null ? this.anyValue.hashCode() : 0);
        hash = 83 * hash + (this.noValues != null ? this.noValues.hashCode() : 0);
        hash = 83 * hash + (this.valuesReference != null ? this.valuesReference.hashCode() : 0);
        hash = 83 * hash + (this.defaultValue != null ? this.defaultValue.hashCode() : 0);
        hash = 83 * hash + (this.meaning != null ? this.meaning.hashCode() : 0);
        hash = 83 * hash + (this.dataType != null ? this.dataType.hashCode() : 0);
        hash = 83 * hash + (this.uom != null ? this.uom.hashCode() : 0);
        hash = 83 * hash + (this.referenceSystem != null ? this.referenceSystem.hashCode() : 0);
        hash = 83 * hash + (this.metadata != null ? this.metadata.hashCode() : 0);
        return hash;
    }
    
    @Override
    public String toString(){
        StringBuilder s = new StringBuilder();
        if (allowedValues != null) 
            s.append(allowedValues.toString()).append('\n');
        
        if (anyValue != null) 
            s.append(anyValue.toString()).append('\n');
        
        if (noValues != null) 
            s.append(noValues.toString()).append('\n');
       
        if (valuesReference != null) 
            s.append(valuesReference.toString()).append('\n');
       
        if (defaultValue != null) 
            s.append(defaultValue.toString()).append('\n');
        
        if (meaning != null) 
            s.append(meaning.toString()).append('\n');
        
        if (dataType != null) 
            s.append(dataType.toString()).append('\n');
        
        if (uom != null) 
            s.append(uom.toString()).append('\n');
        
        if (referenceSystem != null) 
            s.append(referenceSystem.toString()).append('\n');
       
        if (metadata != null) {        
            for (MetadataType m:metadata) {
                s.append(m.toString()).append('\n');
            }
        }
        return s.toString();
    }
}
