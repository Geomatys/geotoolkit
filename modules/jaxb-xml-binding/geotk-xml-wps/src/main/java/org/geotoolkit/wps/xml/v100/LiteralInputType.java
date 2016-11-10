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
package org.geotoolkit.wps.xml.v100;

import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.ows.xml.v110.AllowedValues;
import org.geotoolkit.ows.xml.v110.AnyValue;
import org.geotoolkit.ows.xml.v110.DomainMetadataType;
import org.geotoolkit.wps.xml.LiteralDataDescription;


/**
 * Description of a process input that consists of a simple literal value (e.g., "2.1"). (Informative: This type is a subset of the ows:UnNamedDomainType defined in owsDomaintype.xsd.) 
 * 
 * <p>Java class for LiteralInputType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="LiteralInputType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/wps/1.0.0}LiteralOutputType">
 *       &lt;sequence>
 *         &lt;group ref="{http://www.opengis.net/wps/1.0.0}LiteralValuesChoice"/>
 *         &lt;element name="DefaultValue" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
@XmlType(name = "LiteralInputType", propOrder = {
    "allowedValues",
    "anyValue",
    "valuesReference",
    "defaultValue"
})
public class LiteralInputType extends LiteralOutputType implements LiteralDataDescription {

    @XmlElement(name = "AllowedValues", namespace = "http://www.opengis.net/ows/1.1")
    protected AllowedValues allowedValues;
    @XmlElement(name = "AnyValue", namespace = "http://www.opengis.net/ows/1.1")
    protected AnyValue anyValue;
    @XmlElement(name = "ValuesReference", namespace = "")
    protected ValuesReferenceType valuesReference;
    @XmlElement(name = "DefaultValue", namespace = "")
    protected String defaultValue;

    public LiteralInputType() {
        
    }
    
    public LiteralInputType(DomainMetadataType dataType, SupportedUOMsType uoMs, AllowedValues allowedValues, 
            AnyValue anyValue, ValuesReferenceType valuesReference, String defaultValue) {
        super(dataType, uoMs);
        this.allowedValues = allowedValues;
        this.anyValue = anyValue;
        this.defaultValue = defaultValue;
        this.valuesReference = valuesReference;
    }
    
    /**
     * Indicates that there are a finite set of values and ranges allowed for this input, and contains list of all the valid values and/or ranges of values. 
     * Notice that these values and ranges can be displayed to a human client. 
     * 
     * @return
     *     possible object is
     *     {@link AllowedValues }
     *     
     */
    public AllowedValues getAllowedValues() {
        return allowedValues;
    }

    /**
     * Indicates that there are a finite set of values and ranges allowed for this input, and contains list of all the valid values and/or ranges of values. 
     * Notice that these values and ranges can be displayed to a human client. 
     * 
     * @param value
     *     allowed object is
     *     {@link AllowedValues }
     *     
     */
    public void setAllowedValues(final AllowedValues value) {
        this.allowedValues = value;
    }

    /**
     * Indicates that any value is allowed for this input. This element shall be included when there are no restrictions, 
     * except for data type, on the allowable value of this input. 
     * 
     * @return
     *     possible object is
     *     {@link AnyValue }
     *     
     */
    public AnyValue getAnyValue() {
        return anyValue;
    }

    /**
     * Indicates that any value is allowed for this input. This element shall be included when there are no restrictions, 
     * except for data type, on the allowable value of this input. 
     * 
     * @param value
     *     allowed object is
     *     {@link AnyValue }
     *     
     */
    public void setAnyValue(final AnyValue value) {
        this.anyValue = value;
    }

    /**
     * Gets the value of the valuesReference property.
     * 
     * @return
     *     possible object is
     *     {@link ValuesReferenceType }
     *     
     */
    public ValuesReferenceType getValuesReference() {
        return valuesReference;
    }

    /**
     * Sets the value of the valuesReference property.
     * 
     * @param value
     *     allowed object is
     *     {@link ValuesReferenceType }
     *     
     */
    public void setValuesReference(final ValuesReferenceType value) {
        this.valuesReference = value;
    }

    /**
     * Gets the value of the defaultValue property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDefaultValue() {
        return defaultValue;
    }

    /**
     * Sets the value of the defaultValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDefaultValue(final String value) {
        this.defaultValue = value;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString()).append("\n");
        if (allowedValues != null) {
            sb.append("allowedValues:").append(allowedValues).append('\n');
        }
        if (anyValue != null) {
            sb.append("anyValue:").append(anyValue).append('\n');
        }
        if (defaultValue != null) {
            sb.append("defaultValue:").append(defaultValue).append('\n');
        }
        if (valuesReference != null) {
            sb.append("valuesReference:").append(valuesReference).append('\n');
        }
        return sb.toString();
    }
    
    /**
     * Verify that this entry is identical to the specified object.
     * @param object Object to compare
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof LiteralInputType && super.equals(object)) {
            final LiteralInputType that = (LiteralInputType) object;
            return Objects.equals(this.allowedValues, that.allowedValues) &&
                   Objects.equals(this.anyValue, that.anyValue) &&
                   Objects.equals(this.defaultValue, that.defaultValue) &&
                   Objects.equals(this.valuesReference, that.valuesReference);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.allowedValues);
        hash = 97 * hash + Objects.hashCode(this.anyValue);
        hash = 97 * hash + Objects.hashCode(this.valuesReference);
        hash = 97 * hash + Objects.hashCode(this.defaultValue);
        return hash;
    }
}
