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
import org.geotoolkit.ows.xml.v110.DomainMetadataType;


/**
 * Identifies a UOM supported for this input or output.
 * 
 * <p>Java class for UOMsType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UOMsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/ows/1.1}UOM" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UOMsType", propOrder = {
    "uom"
})
public class UOMsType {

    @XmlElement(name = "UOM", namespace = "http://www.opengis.net/ows/1.1", required = true)
    protected List<DomainMetadataType> uom;

    public UOMsType() {
        
    }
    
    public UOMsType(List<DomainMetadataType> uom) {
        this.uom = uom;
    }
    
    /**
     * Reference to a UOM supported for this input or output. Gets the value of the uom property.
     * 
     * @return Objects of the following type(s) are allowed in the list
     * {@link DomainMetadataType }
     * 
     * 
     */
    public List<DomainMetadataType> getUOM() {
        if (uom == null) {
            uom = new ArrayList<>();
        }
        return this.uom;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[").append(this.getClass().getSimpleName()).append("]\n");
        if (uom != null) {
            sb.append("uom:");
            for (DomainMetadataType u : uom) {
                sb.append(u).append('\n');
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
        if (object instanceof UOMsType) {
            final UOMsType that = (UOMsType) object;
            return Objects.equals(this.uom, that.uom);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + Objects.hashCode(this.uom);
        return hash;
    }
}
