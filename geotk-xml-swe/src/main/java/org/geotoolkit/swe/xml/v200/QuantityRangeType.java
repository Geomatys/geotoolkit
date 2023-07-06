/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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

package org.geotoolkit.swe.xml.v200;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlList;
import jakarta.xml.bind.annotation.XmlType;
import org.geotoolkit.swe.xml.AbstractQualityProperty;
import org.geotoolkit.swe.xml.AbstractQuantityRange;


/**
 * <p>Java class for QuantityRangeType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="QuantityRangeType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/swe/2.0}AbstractSimpleComponentType">
 *       &lt;sequence>
 *         &lt;element name="uom" type="{http://www.opengis.net/swe/2.0}UnitReference"/>
 *         &lt;element name="constraint" type="{http://www.opengis.net/swe/2.0}AllowedValuesPropertyType" minOccurs="0"/>
 *         &lt;element name="value" type="{http://www.opengis.net/swe/2.0}RealPair" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "QuantityRangeType", propOrder = {
    "uom",
    "constraint",
    "value"
})
public class QuantityRangeType extends AbstractSimpleComponentType implements AbstractQuantityRange {

    @XmlElement(required = true)
    private UnitReference uom;
    private AllowedValuesPropertyType constraint;
    @XmlList
    @XmlElement(type = Double.class)
    private List<Double> value;

    public QuantityRangeType() {

    }

    public QuantityRangeType(final String definition, final List<Double> value) {
        super(null, definition, null);
        this.value = value;
    }

    public QuantityRangeType(final AbstractQuantityRange q) {
        super(q);
        if (q != null) {
            this.axisID = q.getAxisID();
            if (q.getConstraint() != null) {
                this.constraint = new AllowedValuesPropertyType(q.getConstraint());
            }
            if (q.getQuality() != null) {
                this.quality = new ArrayList<>();
                for (AbstractQualityProperty qual : q.getQuality()) {
                    this.quality.add(new QualityPropertyType(qual));
                }
            }
            this.referenceFrame = q.getReferenceFrame();
            this.value = q.getValue();
            if (q.getUom() != null) {
                this.uom = new UnitReference(q.getUom());
            }
        }
    }

    /**
     * Gets the value of the uom property.
     *
     * @return
     *     possible object is
     *     {@link UnitReference }
     *
     */
    @Override
    public UnitReference getUom() {
        return uom;
    }

    /**
     * Sets the value of the uom property.
     *
     * @param value
     *     allowed object is
     *     {@link UnitReference }
     *
     */
    public void setUom(UnitReference value) {
        this.uom = value;
    }

    /**
     * Gets the value of the constraint property.
     *
     * @return
     *     possible object is
     *     {@link AllowedValuesPropertyType }
     *
     */
    @Override
    public AllowedValuesPropertyType getConstraint() {
        return constraint;
    }

    /**
     * Sets the value of the constraint property.
     *
     * @param value
     *     allowed object is
     *     {@link AllowedValuesPropertyType }
     *
     */
    public void setConstraint(AllowedValuesPropertyType value) {
        this.constraint = value;
    }

    /**
     * Gets the value of the value property.
     *
     * Objects of the following type(s) are allowed in the list
     * {@link Double }
     *
     *
     */
    @Override
    public List<Double> getValue() {
        if (value == null) {
            value = new ArrayList<>();
        }
        return this.value;
    }

    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof QuantityRangeType && super.equals(object)) {
            final QuantityRangeType that = (QuantityRangeType) object;

            return Objects.equals(this.constraint,  that.constraint) &&
                   Objects.equals(this.uom,         that.uom) &&
                   Objects.equals(this.value,       that.value);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 47 * hash + (this.constraint != null ? this.constraint.hashCode() : 0);
        hash = 47 * hash + (this.uom != null ? this.uom.hashCode() : 0);
        hash = 47 * hash + (this.value != null ? this.value.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        final StringBuilder s = new StringBuilder(super.toString());
        if (constraint != null) {
            s.append("constraint=").append(constraint).append('\n');
        }
        if (uom != null) {
            s.append("uom=").append(uom).append('\n');
        }
        if (value != null) {
            s.append("value:\n");
            for (Double q : value) {
                s.append(q).append('\n');
            }
        }
        return s.toString();
    }
}
