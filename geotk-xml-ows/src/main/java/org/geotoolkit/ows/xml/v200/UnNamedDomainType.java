/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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

package org.geotoolkit.ows.xml.v200;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * Valid domain (or allowed set of values) of one quantity,
 *       with needed metadata but without a quantity name or
 *       identifier.
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
 *         &lt;group ref="{http://www.opengis.net/ows/2.0}PossibleValues"/>
 *         &lt;element ref="{http://www.opengis.net/ows/2.0}DefaultValue" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/ows/2.0}Meaning" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/ows/2.0}DataType" minOccurs="0"/>
 *         &lt;group ref="{http://www.opengis.net/ows/2.0}ValuesUnit" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/ows/2.0}Metadata" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
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
    @XmlElementRef(name = "Metadata", namespace = "http://www.opengis.net/ows/2.0", type = JAXBElement.class)
    private List<JAXBElement<? extends MetadataType>> metadata;

    UnNamedDomainType() {

    }

    public UnNamedDomainType(final List<String> allowedvalues) {
        if (allowedvalues != null) {
            this.allowedValues = new AllowedValues(allowedvalues);
        }
    }

    public UnNamedDomainType(final NoValues nv, final ValueType defaultvalue) {
        this.noValues     = nv;
        this.defaultValue = defaultvalue;
    }

    public UnNamedDomainType(final UnNamedDomainType that) {
        if (that != null) {
            if (that.allowedValues != null) {
                this.allowedValues   = new AllowedValues(that.allowedValues);
            }
            if (that.anyValue != null) {
                this.anyValue        = new AnyValue(that.anyValue);
            }
            if (that.dataType != null) {
                this.dataType        = new DomainMetadataType(that.dataType);
            }
            if (that.defaultValue != null) {
                this.defaultValue    = new ValueType(that.defaultValue);
            }
            if (that.meaning != null) {
                this.meaning         = new DomainMetadataType(that.meaning);
            }
            if (that.metadata != null) {
                final ObjectFactory factory = new ObjectFactory();
                this.metadata        = new ArrayList<>();
                for (JAXBElement<? extends MetadataType>  jb : that.metadata) {

                    this.metadata.add(factory.createMetadata(new MetadataType(jb.getValue())));
                }
            }
            if (that.noValues != null) {
                this.noValues        = that.noValues;
            }
            if (that.referenceSystem != null) {
                this.referenceSystem = new DomainMetadataType(that.referenceSystem);
            }
            if (that.uom != null) {
                this.uom             = new DomainMetadataType(that.uom);
            }
            if (that.valuesReference != null) {
                this.valuesReference = new ValuesReference(that.valuesReference);
            }
        }
    }

    public UnNamedDomainType(final AnyValue anyValue) {
        if (anyValue == null) {
            this.anyValue = new AnyValue();
        } else {
            this.anyValue = anyValue;
        }
    }

    public UnNamedDomainType(final AllowedValues value) {
        this.allowedValues = value;
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
     * Gets the value of the noValues property.
     *
     * @return
     *     possible object is
     *     {@link NoValues }
     *
     */
    public NoValues getNoValues() {
        return noValues;
    }

    /**
     * Sets the value of the noValues property.
     *
     * @param value
     *     allowed object is
     *     {@link NoValues }
     *
     */
    public void setNoValues(NoValues value) {
        this.noValues = value;
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
     * Optional default value for this quantity, which
     *           should be included when this quantity has a default
     *           value.
     *
     * @return
     *     possible object is
     *     {@link ValueType }
     *
     */
    public String getDefaultValue() {
        if (defaultValue != null) {
            return defaultValue.getValue();
        }
        return null;
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

    public void setDefaultValue(final String value) {
        if (value != null) {
            this.defaultValue = new ValueType(value);
        } else {
            this.defaultValue = null;
        }
    }

    /**
     * Meaning metadata should be referenced or included for
     *           each quantity.
     *
     * @return
     *     possible object is
     *     {@link DomainMetadataType }
     *
     */
    public DomainMetadataType getMeaning() {
        return meaning;
    }

    /**
     * Sets the value of the meaning property.
     *
     * @param value
     *     allowed object is
     *     {@link DomainMetadataType }
     *
     */
    public void setMeaning(DomainMetadataType value) {
        this.meaning = value;
    }

    /**
     * This data type metadata should be referenced or
     *           included for each quantity.
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
     * Identifier of unit of measure of this set of values.
     *           Should be included then this set of values has units (and not a more
     *           complete reference system).
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
     * Identifier of reference system used by this set of
     *           values. Should be included then this set of values has a reference
     *           system (not just units).
     *
     * @return
     *     possible object is
     *     {@link DomainMetadataType }
     *
     */
    public DomainMetadataType getReferenceSystem() {
        return referenceSystem;
    }

    /**
     * Sets the value of the referenceSystem property.
     *
     * @param value
     *     allowed object is
     *     {@link DomainMetadataType }
     *
     */
    public void setReferenceSystem(DomainMetadataType value) {
        this.referenceSystem = value;
    }

    /**
     * Optional unordered list of other metadata about this
     *           quantity. A list of required and optional other metadata elements
     *           for this quantity should be specified in the Implementation
     *           Specification for this service.Gets the value of the metadata property.
     *
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link MetadataType }{@code >}
     * {@link JAXBElement }{@code <}{@link AdditionalParametersType }{@code >}
     *
     *
     */
    public List<JAXBElement<? extends MetadataType>> getRealMetadata() {
        if (metadata == null) {
            metadata = new ArrayList<>();
        }
        return this.metadata;
    }

    public List<MetadataType> getMetadata() {
        if (metadata == null) {
            metadata = new ArrayList<>();
        }
        final List<MetadataType> result = new ArrayList<>();
        for (JAXBElement<? extends MetadataType> jb : metadata) {
            result.add(jb.getValue());
        }
        return result;
    }

}
