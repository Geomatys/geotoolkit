/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2011, Geomatys
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


package org.geotoolkit.wfs.xml.v200;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.util.Utilities;
import org.geotoolkit.wfs.xml.DescribeStoredQueriesResponse;


/**
 * <p>Java class for DescribeStoredQueriesResponseType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DescribeStoredQueriesResponseType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="StoredQueryDescription" type="{http://www.opengis.net/wfs/2.0}StoredQueryDescriptionType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DescribeStoredQueriesResponseType", propOrder = {
    "storedQueryDescription"
})
@XmlRootElement(name="DescribeStoredQueriesResponse", namespace="http://www.opengis.net/wfs/2.0")
public class DescribeStoredQueriesResponseType implements DescribeStoredQueriesResponse {

    @XmlElement(name = "StoredQueryDescription")
    private List<StoredQueryDescriptionType> storedQueryDescription;

    public DescribeStoredQueriesResponseType() {
        
    }
    
    public DescribeStoredQueriesResponseType(final List<StoredQueryDescriptionType> storedQueryDescription) {
        this.storedQueryDescription = storedQueryDescription;
    }
    
    /**
     * Gets the value of the storedQueryDescription property.
     * 
     */
    public List<StoredQueryDescriptionType> getStoredQueryDescription() {
        if (storedQueryDescription == null) {
            storedQueryDescription = new ArrayList<StoredQueryDescriptionType>();
        }
        return this.storedQueryDescription;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("[DescribeStoredQueriesResponseType]\n");
        if (storedQueryDescription != null) {
           sb.append("storedQueryDescription: ").append('\n');
           for (StoredQueryDescriptionType a : storedQueryDescription) {
                sb.append(a).append('\n');
           }
        }
        return sb.toString();
    }

    /**
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof DescribeStoredQueriesResponseType) {
            final DescribeStoredQueriesResponseType that = (DescribeStoredQueriesResponseType) object;
            return Utilities.equals(this.storedQueryDescription,   that.storedQueryDescription);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 13 * hash + (this.storedQueryDescription != null ? this.storedQueryDescription.hashCode() : 0);
        return hash;
    }
}
