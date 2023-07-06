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

import java.util.List;
import java.util.Objects;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlID;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.geotoolkit.feature.FeatureExt;
import org.opengis.feature.Feature;
import org.opengis.filter.ResourceId;


/**
 * <p>Java class for FeatureIdType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="FeatureIdType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/ogc}AbstractIdType">
 *       &lt;attribute name="fid" use="required" type="{http://www.w3.org/2001/XMLSchema}ID" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FeatureIdType")
public class FeatureIdType extends AbstractIdType implements ResourceId {

    @XmlAttribute(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    private String fid;

    /**
     * An empty constructor used by JAXB
     */
    public FeatureIdType() {
    }

    /**
     * Build a new FeaturId with the specified ID
     */
    public FeatureIdType(final String fid) {
        this.fid = fid;
    }

    public FeatureIdType(final FeatureIdType that) {
        if (that != null) {
            this.fid = that.fid;
        }
    }

    /**
     * Gets the value of the fid property.
     */
    public String getFid() {
        return fid;
    }

    @Override
    public String getIdentifier() {
        return fid;
    }

    public void setFid(final String fid) {
        this.fid = fid;
    }

    public boolean matches(final Object feature) {
        if (feature instanceof Feature) {
            final ResourceId identifier = FeatureExt.getId((Feature)feature);
            return identifier != null && fid.equals(identifier.getIdentifier());
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[FeatureIdType]\n");
        if (fid != null) {
           sb.append("fid: ").append(fid).append('\n');
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
        if (object instanceof FeatureIdType) {
            final FeatureIdType that = (FeatureIdType) object;
            return Objects.equals(this.fid,  that.fid);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + (this.fid != null ? this.fid.hashCode() : 0);
        return hash;
    }

    @Override
    public Class getResourceClass() {
        return null;
    }

    @Override
    public List getExpressions() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean test(Object object) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
