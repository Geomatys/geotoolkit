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
import org.geotoolkit.ows.xml.v110.CodeType;
import org.geotoolkit.ows.xml.v110.LanguageStringType;
import org.geotoolkit.wps.xml.DataOutput;


/**
 * Value of one output from a process.
 *
 * In this use, the DescriptionType shall describe this process output.
 *
 * <p>Java class for OutputDataType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="OutputDataType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/wps/1.0.0}DescriptionType">
 *       &lt;group ref="{http://www.opengis.net/wps/1.0.0}OutputDataFormChoice"/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OutputDataType", propOrder = {
    "reference",
    "data"
})
public class OutputDataType extends DescriptionType implements DataOutput {

    @XmlElement(name = "Reference")
    protected OutputReferenceType reference;
    @XmlElement(name = "Data")
    protected DataType data;

    public OutputDataType() {

    }

    public OutputDataType(CodeType identifier, LanguageStringType title, LanguageStringType _abstract, OutputReferenceType reference) {
        super(identifier, title, _abstract);
        this.reference = reference;
    }

    public OutputDataType(CodeType identifier, LanguageStringType title, LanguageStringType _abstract, DataType data) {
        super(identifier, title, _abstract);
        this.data = data;
    }

    /**
     * Gets the value of the reference property.
     *
     * @return
     *     possible object is
     *     {@link OutputReferenceType }
     *
     */
    public OutputReferenceType getReference() {
        return reference;
    }

    /**
     * Sets the value of the reference property.
     *
     * @param value
     *     allowed object is
     *     {@link OutputReferenceType }
     *
     */
    public void setReference(final OutputReferenceType value) {
        this.reference = value;
    }

    /**
     * Gets the value of the data property.
     *
     * @return
     *     possible object is
     *     {@link DataType }
     *
     */
    public DataType getData() {
        return data;
    }

    /**
     * Sets the value of the data property.
     *
     * @param value
     *     allowed object is
     *     {@link DataType }
     *
     */
    public void setData(final DataType value) {
        this.data = value;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString()).append("\n");
        if (data != null) {
            sb.append("data:").append(data).append('\n');
        }
        if (reference != null) {
            sb.append("reference:").append(reference).append('\n');
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
        if (object instanceof OutputDataType && super.equals(object)) {
            final OutputDataType that = (OutputDataType) object;
            return Objects.equals(this.data, that.data) &&
                   Objects.equals(this.reference, that.reference);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.reference);
        hash = 29 * hash + Objects.hashCode(this.data);
        return hash;
    }

}
