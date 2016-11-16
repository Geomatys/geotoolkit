/*
 *    GeotoolKit - An Open Source Java GIS Toolkit
 *    http://geotoolkit.org
 * 
 *    (C) 2009, Geomatys
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


package org.geotoolkit.feature.catalog;

import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.opengis.feature.catalog.DefinitionReference;
import org.opengis.feature.catalog.DefinitionSource;


/**
 * Class that links a data instance to the source of its definition.
 * 
 * <p>Java class for FC_DefinitionReference_Type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FC_DefinitionReference_Type">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.isotc211.org/2005/gco}AbstractObject_Type">
 *       &lt;sequence>
 *         &lt;element name="sourceIdentifier" type="{http://www.isotc211.org/2005/gco}CharacterString_PropertyType" minOccurs="0"/>
 *         &lt;element name="definitionSource" type="{http://www.isotc211.org/2005/gfc}FC_DefinitionSource_PropertyType"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FC_DefinitionReference_Type", propOrder = {
    "sourceIdentifier",
    "definitionSource"
})
@XmlRootElement(name = "FC_DefinitionReference")
public class DefinitionReferenceImpl implements DefinitionReference {

    private String sourceIdentifier;
    @XmlElement(required = true)
    private DefinitionSource definitionSource;

    /**
     * An empty constructor used by JAXB
     */
    public DefinitionReferenceImpl() {
        
    }
    
    /**
     * Clone a DefinitionReference
     */
    public DefinitionReferenceImpl(final DefinitionReference feature) {
        if (feature != null) {
            this.definitionSource = feature.getDefinitionSource();
            this.sourceIdentifier = feature.getSourceIdentifier();
        }
    }
    
    /**
     *Build a new definition reference
     */
    public DefinitionReferenceImpl(final String sourceIdentifier, final DefinitionSource definitionSource) {

        this.definitionSource = definitionSource;
        this.sourceIdentifier = sourceIdentifier;
    }
    
    /**
     * Gets the value of the sourceIdentifier property.
     * 
     */
    public String getSourceIdentifier() {
        return sourceIdentifier;
    }

    /**
     * Sets the value of the sourceIdentifier property.
     * 
     */
    public void setSourceIdentifier(final String value) {
        this.sourceIdentifier = value;
    }

    /**
     * Gets the value of the definitionSource property.
     * 
    */
    public DefinitionSource getDefinitionSource() {
        return definitionSource;
    }

    /**
     * Sets the value of the definitionSource property.
     */
    public void setDefinitionSource(final DefinitionSource value) {
        this.definitionSource = value;
    }
    
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("[DefinitionReference]:").append('\n');
        
        if (definitionSource != null) {
            s.append("definition source: ").append(definitionSource).append('\n');
        }
        if (sourceIdentifier != null) {
            s.append("source identifier: ").append(sourceIdentifier).append('\n');
        }
        return s.toString();
    }
    
    /**
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof DefinitionReferenceImpl) {
            final DefinitionReferenceImpl that = (DefinitionReferenceImpl) object;
         
            return Objects.equals(this.definitionSource, that.definitionSource) &&
                   Objects.equals(this.sourceIdentifier, that.sourceIdentifier);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + (this.sourceIdentifier != null ? this.sourceIdentifier.hashCode() : 0);
        hash = 79 * hash + (this.definitionSource != null ? this.definitionSource.hashCode() : 0);
        return hash;
    }

}
