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
package org.geotoolkit.swe.xml.v101;

import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.swe.xml.AbstractDataArray;
import org.apache.sis.util.ComparisonMode;


/**
 * <p>Java class for AbstractDataArrayType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="AbstractDataArrayType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/swe/1.0.1}AbstractDataComponentType">
 *       &lt;sequence>
 *         &lt;element name="elementCount">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence minOccurs="0">
 *                   &lt;element ref="{http://www.opengis.net/swe/1.0.1}Count"/>
 *                 &lt;/sequence>
 *                 &lt;attribute name="ref" type="{http://www.w3.org/2001/XMLSchema}IDREF" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
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
@XmlType(name = "AbstractDataArrayType", propOrder = {
    "elementCount"
})
@XmlSeeAlso({
    DataArrayType.class
})
public abstract class AbstractDataArrayType extends AbstractDataComponentType implements AbstractDataArray {

    @XmlElement(required = true)
    private ElementCount elementCount;

    /**
     * Empty constructor used by JAXB.
     */
    AbstractDataArrayType() {

    }

    /**
     * Clone a new Abstract Data array.
     */
    public AbstractDataArrayType(final AbstractDataArray array) {
        super(array);
        if (array != null) {
            this.elementCount = new ElementCount(array.getElementCount());
        }
    }

    /**
     * Build a new Abstract Data array with only the value.
     */
    public AbstractDataArrayType(final String id, final int count) {
        super(id, null, null);
        this.elementCount = new ElementCount(count);
    }

    /**
     * Gets the value of the elementCount property.
     */
    @Override
    public ElementCount getElementCount() {
        return elementCount;
    }

    /**
     * Sets the value of the elementCount property.
     */
    public void setElementCount(final ElementCount elementCount) {
        this.elementCount = elementCount;
    }

    /**
     * Sets the value of the elementCount property.
     */
    @Override
    public void setElementCount(final int count) {
        this.elementCount = new ElementCount(count);
    }

    /**
     * Verify that the object is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object, final ComparisonMode mode) {
        if (object == this) {
            return true;
        }
        if (object instanceof AbstractDataArrayType && super.equals(object, mode)) {
            final AbstractDataArrayType that = (AbstractDataArrayType) object;
            return Objects.equals(this.elementCount, that.elementCount);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + (this.elementCount != null ? this.elementCount.hashCode() : 0);
        return hash;
    }
}
