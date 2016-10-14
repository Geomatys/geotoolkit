/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2016, Geomatys
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

package org.geotoolkit.wps.xml.v200;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.wps.xml.DataDescription;


/**
 * Description type for process or input/output data items.
 * 
 * <p>Java class for DataDescriptionType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DataDescriptionType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/wps/2.0}Format" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DataDescriptionType", propOrder = {
    "format"
})
@XmlSeeAlso({
    ComplexDataType.class,
    LiteralDataType.class,
    BoundingBoxData.class
})
public abstract class DataDescriptionType implements DataDescription {

    @XmlElement(name = "Format", required = true)
    protected List<Format> format;

    public DataDescriptionType() {
        
    }
    
    public DataDescriptionType(List<Format> format) {
        this.format = format;
    }
    
    /**
     * Gets the value of the format property.
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link Format }
     * 
     * 
     */
    public List<Format> getFormat() {
        if (format == null) {
            format = new ArrayList<>();
        }
        return this.format;
    }
    
    /**
     * @return the default mime type
     */
    public String getMimeType() {
        for (Format format : getFormat()) {
            if (format.isDefault()) {
                return format.getMimeType();
            }
        }
        if (!format.isEmpty()) {
            return format.get(0).getMimeType();
        }
        return null;
    }
    
    /**
     * @return the default encoding
     */
    public String getEncoding(){
        for (Format format : getFormat()) {
            if (format.isDefault()) {
                return format.getEncoding();
            }
        }
        if (!format.isEmpty()) {
            return format.get(0).getEncoding();
        }
        return null;
    }
    
    /**
     * @return the default schema
     */
    public String getSchema(){
        for (Format format : getFormat()) {
            if (format.isDefault()) {
                return format.getSchema();
            }
        }
        if (!format.isEmpty()) {
            return format.get(0).getSchema();
        }
        return null;
    }
    
    public void setSchema(String schema) {
        if (getSchema().isEmpty()) {
            // add a new default format
            this.format.add(new Format(null, null, schema, null, true));
        } else {
            for (Format format : getFormat()) {
                if (format.isDefault()) {
                    format.setSchema(schema);
                    return;
                }
            }
            //applying to first schema
            this.format.get(0).setSchema(schema);
        }
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[").append(this.getClass().getSimpleName()).append("]\n");
        if (format != null) {
            sb.append("format:\n");
            for (Format out : format) {
                sb.append(out).append('\n');
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
        if (object instanceof DataDescriptionType) {
            final DataDescriptionType that = (DataDescriptionType) object;
            return Objects.equals(this.format, that.format);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + Objects.hashCode(this.format);
        return hash;
    }
}
