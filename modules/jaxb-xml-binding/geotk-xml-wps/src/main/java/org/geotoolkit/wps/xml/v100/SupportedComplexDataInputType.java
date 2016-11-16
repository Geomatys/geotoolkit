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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SupportedComplexDataInputType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SupportedComplexDataInputType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/wps/1.0.0}SupportedComplexDataType">
 *       &lt;attribute name="maximumMegabytes" type="{http://www.w3.org/2001/XMLSchema}integer" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SupportedComplexDataInputType")
public class SupportedComplexDataInputType extends SupportedComplexDataType {

    public SupportedComplexDataInputType() {
        
    }
    
    public SupportedComplexDataInputType(ComplexDataDescriptionType defaultFormat, List<ComplexDataDescriptionType> supported, Integer maximumMegabytes) {
        super(defaultFormat, supported);
        this.maximumMegabytes = maximumMegabytes;
        
    }
    
    @XmlAttribute
    protected Integer maximumMegabytes;

    /**
     * Gets the value of the maximumMegabytes property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getMaximumMegabytes() {
        return maximumMegabytes;
    }

    /**
     * Sets the value of the maximumMegabytes property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMaximumMegabytes(final Integer value) {
        this.maximumMegabytes = value;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString()).append("\n");
        if (maximumMegabytes != null) {
            sb.append("Maximum megabytes:").append(maximumMegabytes).append('\n');
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
        if (object instanceof SupportedComplexDataInputType && super.equals(object)) {
            final SupportedComplexDataInputType that = (SupportedComplexDataInputType) object;
            return Objects.equals(this.maximumMegabytes, that.maximumMegabytes);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.maximumMegabytes);
        return hash;
    }
}
