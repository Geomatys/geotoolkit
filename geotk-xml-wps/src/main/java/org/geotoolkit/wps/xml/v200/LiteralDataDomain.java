/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2016, Geomatys
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
package org.geotoolkit.wps.xml.v200;

import java.util.List;
import java.util.Objects;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlTransient;
import jakarta.xml.bind.annotation.XmlType;
import org.geotoolkit.ows.xml.v200.AllowedValues;
import org.geotoolkit.ows.xml.v200.AnyValue;
import org.geotoolkit.ows.xml.v200.DomainMetadataType;
import org.geotoolkit.ows.xml.v200.ValueType;
import org.geotoolkit.ows.xml.v200.ValuesReference;


/**
 *
 * A literal data domain consists of a value type and range,
 * and optionally a unit of measurement and a default value.
 *
 *
 * <p>Java class for LiteralDataDomain complex type.

 <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="LiteralDataDomain">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;element ref="{http://www.opengis.net/ows/2.0}AllowedValues"/>
 *           &lt;element ref="{http://www.opengis.net/ows/2.0}AnyValue"/>
 *           &lt;element ref="{http://www.opengis.net/ows/2.0}ValuesReference"/>
 *         &lt;/choice>
 *         &lt;element ref="{http://www.opengis.net/ows/2.0}DataType" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/ows/2.0}UOM" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/ows/2.0}DefaultValue" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlType(name = "LiteralDataDomainType", propOrder = {
    "allowedValues",
    "anyValue",
    "valuesReference",
    "dataType",
    "uom",
    "defaultValue",
    "_default"
})
public class LiteralDataDomain {

    @XmlElement(name = "AllowedValues", namespace = "http://www.opengis.net/ows/2.0")
    protected AllowedValues allowedValues;
    @XmlElement(name = "AnyValue", namespace = "http://www.opengis.net/ows/2.0")
    protected AnyValue anyValue;
    @XmlElement(name = "ValuesReference", namespace = "http://www.opengis.net/ows/2.0")
    protected ValuesReference valuesReference;
    @XmlElement(name = "DataType", namespace = "http://www.opengis.net/ows/2.0")
    protected DomainMetadataType dataType;
    @XmlElement(name = "UOM", namespace = "http://www.opengis.net/ows/2.0")
    protected DomainMetadataType uom;
    @XmlElement(name = "DefaultValue", namespace = "http://www.opengis.net/ows/2.0")
    protected ValueType defaultValue;
    @XmlAttribute(name = "default")
    protected Boolean _default;

    @XmlTransient
    private AllowedValues allowedRanges;

    public LiteralDataDomain() {

    }

    public LiteralDataDomain(org.geotoolkit.wps.json.LiteralDataDomain jLit) {
        if (jLit != null) {
            if (jLit.getAllowedValues() != null) {
                this.allowedValues = new AllowedValues(jLit.getAllowedValues().getAllowedValues());
            }
            if (jLit.getAllowedRanges()!= null) {
                // TODO this.allowedRanges = new AllowedRanges();
            }
            if (jLit.getAnyValue() != null && jLit.getAnyValue()) {
                this.anyValue = new AnyValue();
            }
            if (jLit.getDataType() != null) {
                this.dataType = new DomainMetadataType(jLit.getDataType().getName(), jLit.getDataType().getReference());
            }
            if (jLit.getUom()!= null) {
                this.uom = new DomainMetadataType(jLit.getUom().getName(), jLit.getUom().getReference());
            }
            if (jLit.getValuesReference()!= null) {
                this.valuesReference = new ValuesReference(jLit.getValuesReference(), null);
            }
            if (jLit.getDefaultValue() != null) {
                this.defaultValue = new ValueType(jLit.getDefaultValue());
            }
        }
    }

    public LiteralDataDomain(AllowedValues allowedValues, AnyValue anyValue, ValuesReference valuesReference, DomainMetadataType dataType, DomainMetadataType uom,
            String defaultValue) {
        this(allowedValues, anyValue, valuesReference, dataType, uom, defaultValue, null);
    }

    public LiteralDataDomain(AllowedValues allowedValues, AnyValue anyValue, ValuesReference valuesReference, DomainMetadataType dataType, DomainMetadataType uom,
                String defaultValue, Boolean _default) {
        this.allowedValues = allowedValues;
        this.anyValue = anyValue;
        this.dataType = dataType;
        this.uom = uom;
        this.valuesReference = valuesReference;
        if (defaultValue != null) {
            this.defaultValue = new ValueType(defaultValue);
        }
        this._default = _default;
    }

    public LiteralDataDomain(DomainMetadataType dataType, DomainMetadataType uom, AnyValue anyValue) {
        this.dataType = dataType;
        this.uom = uom;
        this.anyValue = anyValue;
    }

    /**
     * Gets the value of the allowedValues property.
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
     * Sets the value of the allowedValues property.
     *
     * @param value
     *     allowed object is
     *     {@link AllowedValues }
     *
     */
    public void setAllowedValues(AllowedValues value) {
        this.allowedValues = value;
    }

    /**
     * Gets the value of the anyValue property.
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
     * Sets the value of the anyValue property.
     *
     * @param value
     *     allowed object is
     *     {@link AnyValue }
     *
     */
    public void setAnyValue(AnyValue value) {
        this.anyValue = value;
    }

    /**
     * Gets the value of the valuesReference property.
     *
     * @return
     *     possible object is
     *     {@link ValuesReference }
     *
     */
    public ValuesReference getValuesReference() {
        return valuesReference;
    }

    /**
     * Sets the value of the valuesReference property.
     *
     * @param value
     *     allowed object is
     *     {@link ValuesReference }
     *
     */
    public void setValuesReference(ValuesReference value) {
        this.valuesReference = value;
    }

    /**
     * Gets the value of the dataType property.
     *
     * @return
     *     possible object is
     *     {@link DomainMetadataType }
     *
     */
    public DomainMetadataType getDataType() {
        return dataType;
    }

    /**
     * Sets the value of the dataType property.
     *
     * @param value
     *     allowed object is
     *     {@link DomainMetadataType }
     *
     */
    public void setDataType(DomainMetadataType value) {
        this.dataType = value;
    }

    /**
     * Gets the value of the uom property.
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
     * Gets the value of the defaultValue property.
     *
     * @return
     *     possible object is
     *     {@link ValueType }
     *
     */
    public ValueType getDefaultValue() {
        return defaultValue;
    }

    /**
     * Sets the value of the defaultValue property.
     *
     * @param value
     *     allowed object is
     *     {@link ValueType }
     *
     */
    public void setDefaultValue(ValueType value) {
        this.defaultValue = value;
    }

    /**
     * Gets the value of the default property.
     *
     * @return possible object is {@link Boolean }
     *
     */
    public Boolean isDefault() {
        return _default;
    }

    /**
     * Sets the value of the default property.
     *
     * @param value allowed object is {@link Boolean }
     *
     */
    public void setDefault(Boolean value) {
        this._default = value;
    }

    /**
     * @return the allowedRanges
     */
    public AllowedValues getAllowedRanges() {
        return allowedRanges;
    }

    /**
     * @param allowedRanges the allowedRanges to set
     */
    public void setAllowedRanges(AllowedValues allowedRanges) {
        this.allowedRanges = allowedRanges;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[").append(this.getClass().getSimpleName()).append("]\n");
        if (allowedValues != null) {
            sb.append("allowedValues:").append(allowedValues).append('\n');
        }
        if (anyValue != null) {
            sb.append("anyValue:").append(anyValue).append('\n');
        }
        if (dataType != null) {
            sb.append("dataType:").append(dataType).append('\n');
        }
        if (defaultValue != null) {
            sb.append("defaultValue:").append(defaultValue).append('\n');
        }
        if (uom != null) {
            sb.append("uom:").append(uom).append('\n');
        }
        if (valuesReference != null) {
            sb.append("valuesReference:").append(valuesReference).append('\n');
        }
        if (dataType != null) {
            sb.append("dataType:").append(dataType).append('\n');
        }
        if (_default != null) {
            sb.append("_default:").append(_default).append('\n');
        }
        if (allowedRanges != null) {
            sb.append("allowedRanges:").append(allowedRanges).append('\n');
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
        if (object instanceof LiteralDataDomain) {
            final LiteralDataDomain that = (LiteralDataDomain) object;
            return Objects.equals(this.allowedValues, that.allowedValues) &&
                   Objects.equals(this.allowedRanges, that.allowedRanges) &&
                   Objects.equals(this.anyValue, that.anyValue) &&
                   Objects.equals(this.dataType, that.dataType) &&
                   Objects.equals(this.defaultValue, that.defaultValue) &&
                   Objects.equals(this.uom, that.uom) &&
                   Objects.equals(this.valuesReference, that.valuesReference) &&
                   Objects.equals(this._default, that._default);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 11 * hash + Objects.hashCode(this.allowedValues);
        hash = 11 * hash + Objects.hashCode(this.allowedRanges);
        hash = 11 * hash + Objects.hashCode(this.anyValue);
        hash = 11 * hash + Objects.hashCode(this.valuesReference);
        hash = 11 * hash + Objects.hashCode(this.dataType);
        hash = 11 * hash + Objects.hashCode(this.uom);
        hash = 11 * hash + Objects.hashCode(this.defaultValue);
        hash = 11 * hash + Objects.hashCode(this._default);
        return hash;
    }
}
