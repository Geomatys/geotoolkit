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

package org.geotoolkit.swes.xml.v200;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.swes.xml.SOSResponse;
import org.geotoolkit.util.Utilities;



/**
 * <p>Java class for ExtensibleResponseType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ExtensibleResponseType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="extension" type="{http://www.w3.org/2001/XMLSchema}anyType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ExtensibleResponseType", propOrder = {
    "extension"
})
@XmlSeeAlso({
    // GetObservationResponseType.class,
    // GetFeatureOfInterestResponseType.class,
    // InsertResultTemplateResponseType.class,
    // GetResultTemplateResponseType.class,
    // GetObservationByIdResponseType.class,
    // GetResultResponseType.class,
    // InsertResultResponseType.class,
    // InsertObservationResponseType.class,
    DeleteSensorResponseType.class,
    InsertSensorResponseType.class,
    DescribeSensorResponseType.class,
    UpdateSensorDescriptionResponseType.class
})
public abstract class ExtensibleResponseType implements SOSResponse {

    private List<Object> extension;

    public ExtensibleResponseType() {
        
    }
    
    public ExtensibleResponseType(final List<Object> extension) {
        this.extension = extension;
    }
    
    /**
     * Gets the value of the extension property.
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     * 
     */
    public List<Object> getExtension() {
        if (extension == null) {
            extension = new ArrayList<Object>();
        }
        return this.extension;
    }
    
    public void setExtension(final List<Object> extension) {
        this.extension = extension;
    }
    
    @Override
    public String getSpecificationVersion() {
        return "2.0.0";
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + (this.extension != null ? this.extension.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof ExtensibleResponseType) {
            final ExtensibleResponseType that = (ExtensibleResponseType) obj;
            return Utilities.equals(this.extension, that.extension);
        }
        return false;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("[").append(this.getClass().getSimpleName()).append("]\n");
        if (extension != null) {
            sb.append("extension:\n");
            for (Object ext : extension) {
                sb.append(ext).append('\n');
            }
        }
        return sb.toString();
    }
}
