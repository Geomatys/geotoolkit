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
package org.geotoolkit.ogc.xml.v110;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.util.Utilities;


/**
 * <p>Java class for SortByType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SortByType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="SortProperty" type="{http://www.opengis.net/ogc}SortPropertyType" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SortByType", propOrder = {
    "sortProperty"
})
public class SortByType {

    @XmlElement(name = "SortProperty", required = true)
    private List<SortPropertyType> sortProperty;

    /**
     * An empty constructor used by JAXB
     */
    public SortByType() {
        
    }
    
    /**
     * Build a new sort by list.
     */
    public SortByType(List<SortPropertyType> sortProperty) {
        this.sortProperty = sortProperty;
    }
    
    /**
     * Gets the value of the sortProperty property.
     * (unmodifiable)
     */
    public List<SortPropertyType> getSortProperty() {
        if (sortProperty == null) {
            sortProperty = new ArrayList<SortPropertyType>();
        }
        return Collections.unmodifiableList(sortProperty);
    }

     /**
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof SortByType) {
            final SortByType that = (SortByType) object;
            return Utilities.equals(this.sortProperty,  that.sortProperty);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + (this.sortProperty != null ? this.sortProperty.hashCode() : 0);
        return hash;
    }
}
