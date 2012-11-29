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
package org.geotoolkit.ogc.xml.v100;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import org.opengis.feature.Attribute;
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
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="fid" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FeatureIdType")
public class FeatureIdType implements FeatureId {

    @XmlAttribute(required = true)
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
    
    /**
     * Gets the value of the fid property.
     * 
     */
    public String getFid() {
        return fid;
    }

    @Override
    public String getID() {
        return fid;
    }
    
    /**
     * Sets the value of the fid property.
     * 
     */
    public void setFid(final String value) {
        this.fid = value;
    }

    @Override
    public boolean matches(final Object feature) {
        if (feature instanceof Attribute) {
            final Identifier identifier = ((Attribute)feature).getIdentifier();
            return identifier != null && fid.equals(identifier.getID());
        }
        return false;
    }
}
