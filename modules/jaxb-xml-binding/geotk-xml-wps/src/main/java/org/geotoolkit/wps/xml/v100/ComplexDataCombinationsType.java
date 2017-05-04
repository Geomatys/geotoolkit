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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Identifies valid combinations of Format, Encoding, and Schema supported for this input or output. The process shall expect input in or produce output in this combination of Format/Encoding/Schema unless the Execute request specifies otherwise..
 *
 * <p>Java class for ComplexDataCombinationsType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="ComplexDataCombinationsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Format" type="{http://www.opengis.net/wps/1.0.0}ComplexDataDescriptionType" maxOccurs="unbounded"/>
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
@XmlType(name = "ComplexDataCombinationsType", propOrder = {
    "format"
})
public class ComplexDataCombinationsType {

    @XmlElement(name = "Format", namespace = "", required = true)
    protected List<ComplexDataDescriptionType> format;

    public ComplexDataCombinationsType() {

    }

    public ComplexDataCombinationsType(List<ComplexDataDescriptionType> format) {
        this.format = format;
    }

    /**
     * Gets the value of the format property.
     *
     *  @return Objects of the following type(s) are allowed in the list
     * {@link ComplexDataDescriptionType }
     *
     *
     */
    public List<ComplexDataDescriptionType> getFormat() {
        if (format == null) {
            format = new ArrayList<>();
        }
        return this.format;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[").append(this.getClass().getSimpleName()).append("]\n");
        if (format != null) {
            sb.append("format:");
            for (ComplexDataDescriptionType c : format) {
                sb.append(c).append('\n');
            }
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
        if (object instanceof ComplexDataCombinationsType) {
            final ComplexDataCombinationsType that = (ComplexDataCombinationsType) object;
            return Objects.equals(this.format, that.format);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + Objects.hashCode(this.format);
        return hash;
    }

}
