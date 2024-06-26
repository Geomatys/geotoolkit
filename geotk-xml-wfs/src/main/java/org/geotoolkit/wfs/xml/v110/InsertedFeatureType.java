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
package org.geotoolkit.wfs.xml.v110;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import org.geotoolkit.ogc.xml.v110.FeatureIdType;


/**
 * <p>Java class for InsertedFeatureType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="InsertedFeatureType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/ogc}FeatureId" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *       &lt;attribute name="handle" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InsertedFeatureType", propOrder = {
    "featureId"
})
public class InsertedFeatureType {

    @XmlElement(name = "FeatureId", namespace = "http://www.opengis.net/ogc", required = true)
    private List<FeatureIdType> featureId;
    @XmlAttribute
    private String handle;

    public InsertedFeatureType() {

    }

    public InsertedFeatureType(final List<FeatureIdType> featureId, final String handle) {
        this.featureId = featureId;
        this.handle    = handle;
    }

    public InsertedFeatureType(final FeatureIdType featureId, final String handle) {
        this.featureId = Arrays.asList(featureId);
        this.handle    = handle;
    }

    /**
     * This is the feature identifier for the newly created feature.
     * The feature identifier may be generated by the WFS or provided by the client
     * (depending on the value of the idgen attribute).
     * In all cases of idgen values, the feature id must be reported here.
     * Gets the value of the featureId property.
     *
     */
    public List<FeatureIdType> getFeatureId() {
        if (featureId == null) {
            featureId = new ArrayList<FeatureIdType>();
        }
        return this.featureId;
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
    public void setHandle(final String value) {
        this.handle = value;
    }

     @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[InsertedFeatureType]\n");
        if (featureId != null) {
           sb.append("featureId: ").append('\n');
           for (FeatureIdType a : featureId) {
                sb.append(a).append('\n');
           }
        }
        if (handle != null) {
           sb.append("handle: ").append(handle).append('\n');
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
        if (object instanceof InsertedFeatureType) {
            final InsertedFeatureType that = (InsertedFeatureType) object;
            return Objects.equals(this.featureId,  that.featureId) &&
                   Objects.equals(this.handle,     that.handle);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + (this.featureId != null ? this.featureId.hashCode() : 0);
        hash = 59 * hash + (this.handle != null ? this.handle.hashCode() : 0);
        return hash;
    }
}
