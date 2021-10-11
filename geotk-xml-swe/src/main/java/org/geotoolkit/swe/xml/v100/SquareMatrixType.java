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
package org.geotoolkit.swe.xml.v100;

import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.swe.xml.AbstractSquareMatrix;
import org.apache.sis.util.ComparisonMode;


/**
 * <p>Java class for SquareMatrixType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="SquareMatrixType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/swe/1.0}AbstractMatrixType">
 *       &lt;sequence>
 *         &lt;element name="elementType" type="{http://www.opengis.net/swe/1.0}QuantityPropertyType"/>
 *         &lt;group ref="{http://www.opengis.net/swe/1.0}EncodedValuesGroup" minOccurs="0"/>
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
@XmlType(name = "SquareMatrixType", propOrder = {
    "elementType",
    "encoding",
    "values"
})
public class SquareMatrixType extends AbstractMatrixType implements AbstractSquareMatrix {

    @XmlElement(required = true)
    private QuantityPropertyType elementType;
    private BlockEncodingPropertyType encoding;
    private DataValuePropertyType values;

    public SquareMatrixType() {

    }

    public SquareMatrixType(final AbstractSquareMatrix sm) {
        super(sm);
        if (sm != null) {
            if (sm.getElementType() != null) {
                this.elementType = new QuantityPropertyType(sm.getElementType());
            }
            if (sm.getValues() != null) {
                this.values = new DataValuePropertyType(sm.getValues());
            }
            if (sm.getEncoding() != null) {
                this.encoding = new BlockEncodingPropertyType(sm.getEncoding());
            }
        }
    }

    /**
     * Gets the value of the elementType property.
     *
     * @return
     *     possible object is
     *     {@link QuantityPropertyType }
     *
     */
    public QuantityPropertyType getElementType() {
        return elementType;
    }

    /**
     * Sets the value of the elementType property.
     *
     * @param value
     *     allowed object is
     *     {@link QuantityPropertyType }
     *
     */
    public void setElementType(final QuantityPropertyType value) {
        this.elementType = value;
    }

    /**
     * Gets the value of the encoding property.
     *
     * @return
     *     possible object is
     *     {@link BlockEncodingPropertyType }
     *
     */
    public BlockEncodingPropertyType getEncoding() {
        return encoding;
    }

    /**
     * Sets the value of the encoding property.
     *
     * @param value
     *     allowed object is
     *     {@link BlockEncodingPropertyType }
     *
     */
    public void setEncoding(final BlockEncodingPropertyType value) {
        this.encoding = value;
    }

    /**
     * Gets the value of the values property.
     *
     * @return
     *     possible object is
     *     {@link DataValuePropertyType }
     *
     */
    public DataValuePropertyType getValues() {
        return values;
    }

    /**
     * Sets the value of the values property.
     *
     * @param value
     *     allowed object is
     *     {@link DataValuePropertyType }
     *
     */
    public void setValues(final DataValuePropertyType value) {
        this.values = value;
    }

    /**
     * Verify if this entry is identical to specified object.
     */
    @Override
    public boolean equals(final Object object, final ComparisonMode mode) {
        if (object == this) {
            return true;
        }

        if (object instanceof SquareMatrixType && super.equals(object, mode)) {
            final SquareMatrixType  that = (SquareMatrixType) object;
            return Objects.equals(this.elementType, that.elementType) &&
                   Objects.equals(this.encoding,    that.encoding)    &&
                   Objects.equals(this.values,      that.values);

        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + (this.elementType != null ? this.elementType.hashCode() : 0);
        hash = 37 * hash + (this.encoding != null ? this.encoding.hashCode() : 0);
        hash = 37 * hash + (this.values != null ? this.values.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(super.toString());
        if (elementType != null) {
            s.append("elementType:").append(elementType).append('\n');
        }
        if (encoding != null) {
            s.append("encoding:").append(encoding).append('\n');
        }
        if (values != null) {
            s.append("values:").append(values).append('\n');
        }
        return s.toString();
    }

}
