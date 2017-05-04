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

import java.util.List;
import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.wps.xml.ComplexDataTypeDescription;


/**
 * Formats, encodings, and schemas supported by a process input or output.
 *
 * <p>Java class for SupportedComplexDataType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="SupportedComplexDataType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Default" type="{http://www.opengis.net/wps/1.0.0}ComplexDataCombinationType"/>
 *         &lt;element name="Supported" type="{http://www.opengis.net/wps/1.0.0}ComplexDataCombinationsType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SupportedComplexDataType", propOrder = {
    "_default",
    "supported"
})
@XmlSeeAlso({
    SupportedComplexDataInputType.class
})
public class SupportedComplexDataType implements ComplexDataTypeDescription {

    @XmlElement(name = "Default", namespace = "", required = true)
    protected ComplexDataCombinationType _default;
    @XmlElement(name = "Supported", namespace = "", required = true)
    protected ComplexDataCombinationsType supported;

    public SupportedComplexDataType() {

    }

    public SupportedComplexDataType(ComplexDataDescriptionType defaultFormat, List<ComplexDataDescriptionType> supported) {
        if (defaultFormat != null) {
            this._default = new ComplexDataCombinationType(defaultFormat);
        }
        if (supported != null) {
            this.supported = new ComplexDataCombinationsType(supported);
        }
    }

    /**
     * Gets the value of the default property.
     *
     * @return
     *     possible object is
     *     {@link ComplexDataCombinationType }
     *
     */
    public ComplexDataCombinationType getDefault() {
        return _default;
    }

    /**
     * Sets the value of the default property.
     *
     * @param value
     *     allowed object is
     *     {@link ComplexDataCombinationType }
     *
     */
    public void setDefault(final ComplexDataCombinationType value) {
        this._default = value;
    }

    /**
     * Gets the value of the supported property.
     *
     * @return
     *     possible object is
     *     {@link ComplexDataCombinationsType }
     *
     */
    public ComplexDataCombinationsType getSupported() {
        return supported;
    }

    /**
     * Sets the value of the supported property.
     *
     * @param value
     *     allowed object is
     *     {@link ComplexDataCombinationsType }
     *
     */
    public void setSupported(final ComplexDataCombinationsType value) {
        this.supported = value;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[").append(this.getClass().getSimpleName()).append("]\n");
        if (_default != null) {
            sb.append("_default:").append(_default).append('\n');
        }
        if (supported != null) {
            sb.append("supported:").append(supported).append('\n');
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
        if (object instanceof SupportedComplexDataType) {
            final SupportedComplexDataType that = (SupportedComplexDataType) object;
            return Objects.equals(this._default, that._default) &&
                   Objects.equals(this.supported, that.supported);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + Objects.hashCode(this._default);
        hash = 79 * hash + Objects.hashCode(this.supported);
        return hash;
    }
}
