/*
 *    GeotoolKit - An Open Source Java GIS Toolkit
 *    http://geotoolkit.org
 *
 *    (C) 2009, Geomatys
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


package org.geotoolkit.feature.catalog;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.apache.sis.internal.jaxb.gco.GO_GenericName;
import org.apache.sis.internal.jaxb.gco.Multiplicity;
import org.apache.sis.util.ComparisonMode;
import org.opengis.feature.catalog.Constraint;
import org.opengis.feature.catalog.FeatureAttribute;
import org.opengis.feature.catalog.FeatureType;
import org.opengis.feature.catalog.ListedValue;
import org.opengis.util.LocalName;
import org.opengis.util.TypeName;


/**
 * Characteristic of a feature type.
 *
 * <p>Java class for FC_FeatureAttribute_Type complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="FC_FeatureAttribute_Type">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.isotc211.org/2005/gfc}AbstractFC_PropertyType_Type">
 *       &lt;sequence>
 *         &lt;element name="code" type="{http://www.isotc211.org/2005/gco}CharacterString_PropertyType" minOccurs="0"/>
 *         &lt;element name="valueMeasurementUnit" type="{http://www.isotc211.org/2005/gco}UnitOfMeasure_PropertyType" minOccurs="0"/>
 *         &lt;element name="listedValue" type="{http://www.isotc211.org/2005/gfc}FC_ListedValue_PropertyType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="valueType" type="{http://www.isotc211.org/2005/gco}TypeName_PropertyType"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {
    "code",
    //"valueMeasurementUnit",
    "listedValue",
    "valueType"
})
@XmlRootElement(name="FC_FeatureAttribute")
public class FeatureAttributeImpl extends PropertyTypeImpl implements FeatureAttribute {

    private String code;
   //TODO private UnitOfMeasurePropertyType valueMeasurementUnit;
    private List<ListedValue> listedValue;

    @XmlJavaTypeAdapter(GO_GenericName.class)
    @XmlElement(required = true)
    private TypeName valueType;

     /**
     * An empty constructor used by JAXB
     */
    public FeatureAttributeImpl() {

    }

    /**
     * Clone a FeatureAttribute
     */
    public FeatureAttributeImpl(final FeatureAttribute feature) {
        super(feature);
        if (feature != null) {
            this.code        = feature.getCode();
            this.listedValue = feature.getListedValue();
            this.valueType   = feature.getValueType();
        }
    }

    /**
     * Build a new Feature Attribute
     */
    public FeatureAttributeImpl(final String id, final LocalName memberName, final String definition, final Multiplicity cardinality, final FeatureType featureType,
            final List<Constraint> constrainedBy, final String code, final List<ListedValue> listedValue, final TypeName valueType) {
        super(id, memberName, definition, cardinality, featureType, constrainedBy, null);
        this.code        = code;
        this.listedValue = listedValue;
        this.valueType   = valueType;
    }
    /**
     * Gets the value of the code property.
     */
    @Override
    public String getCode() {
        return code;
    }

    /**
     * Sets the value of the code property.
     *
     */
    public void setCode(final String value) {
        this.code = value;
    }

    /**
     * Gets the value of the valueMeasurementUnit property.
     *

    public UnitOfMeasurePropertyType getValueMeasurementUnit() {
        return valueMeasurementUnit;
    }

    /**
     * Sets the value of the valueMeasurementUnit property.

    public void setValueMeasurementUnit(UnitOfMeasurePropertyType value) {
        this.valueMeasurementUnit = value;
    }

    /**
     * Gets the value of the listedValue property.
     */
    @Override
    public List<ListedValue> getListedValue() {
        if (listedValue == null) {
            listedValue = new ArrayList<>();
        }
        return this.listedValue;
    }

     /**
     * Gets the value of the listedValue property.
     */
    public void setListedValue(final List<ListedValue> listedValue) {
        this.listedValue = listedValue;
    }

    /**
     * Gets the value of the listedValue property.
     */
    public void setListedValue(final ListedValue listedValue) {
        if (this.listedValue == null) {
            this.listedValue = new ArrayList<>();
        }
        this.listedValue.add(listedValue);
    }

    /**
     * Gets the value of the valueType property.
     *
     */
    @Override
    public TypeName getValueType() {
        return valueType;
    }

    /**
     * Sets the value of the valueType property.
     */
    public void setValueType(final TypeName value) {
        this.valueType = value;
    }

    @Override
    public FeatureAttributeImpl getReferenceableObject() {
        FeatureAttributeImpl result = new FeatureAttributeImpl(this);
        result.setReference(true);
        return result;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(super.toString());
        s.append("code: ").append(code).append('\n');
        if (listedValue != null) {
            s.append("listed values: ").append('\n');
            for (ListedValue l: listedValue){
                s.append(l).append('\n');
            }
        }
        if (valueType != null) {
            s.append("valueType: ").append(valueType).append('\n');
        }
        return s.toString();
    }

    /**
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object, final ComparisonMode mode) {
        if (object == this) {
            return true;
        }
        if (object instanceof FeatureAttributeImpl && super.equals(object, mode)) {
            final FeatureAttributeImpl that = (FeatureAttributeImpl) object;

            return Objects.equals(this.code,        that.code)        &&
                   Objects.equals(this.listedValue, that.listedValue) &&
                   Objects.equals(this.valueType,   that.valueType);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + super.hashCode();
        hash = 29 * hash + (this.code != null ? this.code.hashCode() : 0);
        return hash;
    }

}
