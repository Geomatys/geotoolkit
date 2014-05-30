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

import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.geotoolkit.feature.Attribute;
import org.opengis.filter.identity.FeatureId;
import org.opengis.filter.identity.Identifier;


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
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FeatureIdType")
public class FeatureIdType extends AbstractIdType implements FeatureId {

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
    public String getID() {
        return fid;
    }

    public void setFid(final String fid) {
        this.fid = fid;
    }

    @Override
    public boolean matches(final Object feature) {
        if (feature instanceof Attribute) {
            final Identifier identifier = ((Attribute)feature).getIdentifier();
            return identifier != null && fid.equals(identifier.getID());
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
}
