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
import org.geotoolkit.ows.xml.v110.BoundingBoxType;


/**
 * Identifies the form of this input or output value, and provides supporting information.
 *
 * <p>Java class for DataType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="DataType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element name="ComplexData" type="{http://www.opengis.net/wps/1.0.0}ComplexDataType"/>
 *         &lt;element name="LiteralData" type="{http://www.opengis.net/wps/1.0.0}LiteralDataType"/>
 *         &lt;element name="BoundingBoxData" type="{http://www.opengis.net/ows/1.1}BoundingBoxType"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DataType", propOrder = {
    "complexData",
    "literalData",
    "boundingBoxData"
})
public class DataType implements org.geotoolkit.wps.xml.DataType{

    @XmlElement(name = "ComplexData")
    protected ComplexDataType complexData;
    @XmlElement(name = "LiteralData")
    protected LiteralDataType literalData;
    @XmlElement(name = "BoundingBoxData")
    protected BoundingBoxType boundingBoxData;

    public DataType() {

    }

    public DataType(ComplexDataType complexData) {
        this.complexData = complexData;
    }

    public DataType(LiteralDataType literalData) {
        this.literalData = literalData;
    }

    public DataType(BoundingBoxType boundingBoxData) {
        this.boundingBoxData = boundingBoxData;
    }

    /**
     * Gets the value of the complexData property.
     *
     * @return
     *     possible object is
     *     {@link ComplexDataType }
     *
     */
    @Override
    public ComplexDataType getComplexData() {
        return complexData;
    }

    /**
     * Sets the value of the complexData property.
     *
     * @param value
     *     allowed object is
     *     {@link ComplexDataType }
     *
     */
    public void setComplexData(final ComplexDataType value) {
        this.complexData = value;
    }

    /**
     * Gets the value of the literalData property.
     *
     * @return
     *     possible object is
     *     {@link LiteralDataType }
     *
     */
    @Override
    public LiteralDataType getLiteralData() {
        return literalData;
    }

    /**
     * Sets the value of the literalData property.
     *
     * @param value
     *     allowed object is
     *     {@link LiteralDataType }
     *
     */
    public void setLiteralData(final LiteralDataType value) {
        this.literalData = value;
    }

    /**
     * Gets the value of the boundingBoxData property.
     *
     * @return
     *     possible object is
     *     {@link BoundingBoxType }
     *
     */
    @Override
    public BoundingBoxType getBoundingBoxData() {
        return boundingBoxData;
    }

    /**
     * Sets the value of the boundingBoxData property.
     *
     * @param value
     *     allowed object is
     *     {@link BoundingBoxType }
     *
     */
    public void setBoundingBoxData(final BoundingBoxType value) {
        this.boundingBoxData = value;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[").append(this.getClass().getSimpleName()).append("]\n");
        if (boundingBoxData != null) {
            sb.append("boundingBoxData:").append(boundingBoxData).append('\n');
        }
        if (complexData != null) {
            sb.append("complexData:").append(complexData).append('\n');
        }
        if (literalData != null) {
            sb.append("literalData:").append(literalData).append('\n');
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
        if (object instanceof DataType) {
            final DataType that = (DataType) object;
            return Objects.equals(this.boundingBoxData, that.boundingBoxData) &&
                   Objects.equals(this.complexData, that.complexData) &&
                   Objects.equals(this.literalData, that.literalData);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + Objects.hashCode(this.complexData);
        hash = 71 * hash + Objects.hashCode(this.literalData);
        hash = 71 * hash + Objects.hashCode(this.boundingBoxData);
        return hash;
    }
}
