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

import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.swe.xml.AbstractCount;


/**
 * <p>Java class for CountType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="CountType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/swe/2.0}AbstractSimpleComponentType">
 *       &lt;sequence>
 *         &lt;element name="constraint" type="{http://www.opengis.net/swe/2.0}AllowedValuesPropertyType" minOccurs="0"/>
 *         &lt;element name="value" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CountType", propOrder = {
    "constraint",
    "value"
})
public class CountType extends AbstractSimpleComponentType implements AbstractCount {

    private AllowedValuesPropertyType constraint;
    private Integer value;

    public CountType() {
    }

    public CountType(final String definition, final int value) {
        this.value = value;
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
     * @return
     *     possible object is
     *     {@link Integer }
     *
     */
    @Override
    public Integer getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     *
     * @param value
     *     allowed object is
     *     {@link Integer }
     *
     */
    public void setValue(Integer value) {
        this.value = value;
    }

    /**
     * Verify if this entry is identical to specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }

        if (object instanceof CountType && super.equals(object)) {
            final CountType that = (CountType) object;
            return Objects.equals(this.constraint, that.constraint)     &&
                   Objects.equals(this.value,      that.value);
        }
        return false;
    }


    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + super.hashCode();
        hash = 47 * hash + (this.constraint != null ? this.constraint.hashCode() : 0);
        hash = 47 * hash + (this.value != null ? this.value.hashCode() : 0);
        return hash;
    }

    /**
     * Retourne une representation de l'objet.
     */

    @Override
    public String toString() {
        final StringBuilder s = new StringBuilder(super.toString());
        if(constraint != null) {
            s.append("constraint=").append(constraint).append('\n');
        }
        if(value != null) {
            s.append("value=").append(value).append('\n');
        }
        return s.toString();
    }
}
