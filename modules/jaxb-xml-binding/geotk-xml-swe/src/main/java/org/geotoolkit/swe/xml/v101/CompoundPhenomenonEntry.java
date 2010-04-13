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
package org.geotoolkit.swe.xml.v101;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.util.Utilities;


/**
 * Description of a set of Phenomena.  
 * 	  CompoundPhenomenon is the abstract head of a substitution group of specialized compound phenomena
 * 
 * <p>Java class for CompoundPhenomenonType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CompoundPhenomenonType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/swe/1.0.1}PhenomenonType">
 *       &lt;attribute name="dimension" use="required" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CompoundPhenomenon")
@XmlSeeAlso({
    CompositePhenomenonEntry.class
})
public abstract class CompoundPhenomenonEntry extends PhenomenonEntry {

    /**
     * The component number.
     */
    @XmlAttribute(required = true)
    @XmlSchemaType(name = "positiveInteger")
    private int dimension;

    /**
     * An empty constructor used by JAXB
     */
    CompoundPhenomenonEntry() {
        
    }
    
    /**
     * Build a new Compound phenomenon.
     */
    public CompoundPhenomenonEntry(final String id, final String name, final String description,
            final int dimension) {
        super(id, name, description);
        this.dimension = dimension;
        
    }
    
    /**
     * Gets the value of the dimension property.
     */
    public int getDimension() {
        return dimension;
    }

    /**
     * Sets the value of the dimension property.
     */
    public void setDimension(int dimension) {
        this.dimension = dimension;
    }
    
    /**
     * Return a code representing this composite phenomenon.
     */
    @Override
    public int hashCode() {
        return getId().hashCode();
    }

    /**
     * Verify if this entry is identical to specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        
        if (object instanceof CompoundPhenomenonEntry && super.equals(object)) {
            final CompoundPhenomenonEntry that = (CompoundPhenomenonEntry) object;
            
            return Utilities.equals(this.dimension ,that.dimension);
       } 
       return false;
        
        
    }
    
    @Override
    public String toString() {
        return super.toString() + " dimension:" + dimension + '\n';
    } 
}
