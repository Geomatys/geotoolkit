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
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.ogc.xml.v200.ResourceIdType;
import org.geotoolkit.util.Utilities;


/**
 * <p>Java class for CreatedOrModifiedFeatureType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="CreatedOrModifiedFeatureType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence maxOccurs="unbounded">
 *         &lt;element ref="{http://www.opengis.net/fes/2.0}ResourceId"/>
 *       &lt;/sequence>
 *       &lt;attribute name="handle" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CreatedOrModifiedFeatureType", propOrder = {
    "resourceId"
})
public class CreatedOrModifiedFeatureType {

    @XmlElement(name = "ResourceId", namespace = "http://www.opengis.net/fes/2.0", required = true)
    private List<ResourceIdType> resourceId;
    @XmlAttribute
    private String handle;

    public CreatedOrModifiedFeatureType() {

    }

    public CreatedOrModifiedFeatureType(final ResourceIdType rid, final String handle) {
        if (rid != null) {
            this.resourceId = Arrays.asList(rid);
        }
        this.handle = handle;
    }

    /**
     * Gets the value of the resourceId property.
     *
    */
    public List<ResourceIdType> getResourceId() {
        if (resourceId == null) {
            resourceId = new ArrayList<ResourceIdType>();
        }
        return this.resourceId;
    }

    /**
     * Gets the value of the handle property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getHandle() {
        return handle;
    }

    /**
     * Sets the value of the handle property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setHandle(String value) {
        this.handle = value;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("[CreatedOrModifiedFeatureType]\n");
        if (resourceId != null) {
           sb.append("resourceId: ").append('\n');
           for (ResourceIdType a : resourceId) {
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
        if (object instanceof CreatedOrModifiedFeatureType) {
            final CreatedOrModifiedFeatureType that = (CreatedOrModifiedFeatureType) object;
            return Objects.equals(this.resourceId,   that.resourceId) &&
                   Objects.equals(this.handle,   that.handle);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 13 * hash + (this.handle != null ? this.handle.hashCode() : 0);
        hash = 13 * hash + (this.resourceId != null ? this.resourceId.hashCode() : 0);
        return hash;
    }
}
