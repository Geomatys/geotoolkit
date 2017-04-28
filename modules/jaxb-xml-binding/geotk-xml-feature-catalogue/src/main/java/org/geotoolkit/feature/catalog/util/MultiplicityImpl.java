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

package org.geotoolkit.feature.catalog.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.geotoolkit.resources.jaxb.feature.catalog.MultiplicityRangeAdapter;
import org.opengis.feature.catalog.util.Multiplicity;
import org.opengis.feature.catalog.util.MultiplicityRange;


/**
 * Use to represent the possible cardinality of a relation. Represented by a set of simple multiplicity ranges.
 * 
 * <p>Java class for Multiplicity_Type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Multiplicity_Type">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.isotc211.org/2005/gco}AbstractObject_Type">
 *       &lt;sequence>
 *         &lt;element name="range" type="{http://www.isotc211.org/2005/gco}MultiplicityRange_PropertyType" maxOccurs="unbounded"/>
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
@XmlType( propOrder = {
    "range"
})
@XmlRootElement(name = "Multiplicity")
public class MultiplicityImpl implements Multiplicity {

    @XmlJavaTypeAdapter(MultiplicityRangeAdapter.class)
    @XmlElement(required = true)
    private List<MultiplicityRange> range;

    /**
     * An empty constructor used by JAXB
     */
    public MultiplicityImpl() {
        
    }
    
    public MultiplicityImpl(final Multiplicity other) {
        if (other != null) {
            this.range = new ArrayList<>();
            for (MultiplicityRange r : other.getRange()) {
                this.range.add(r);
            }
        }
    }
    
    
    /**
     * Build a simple Mulitiplicity 
     */
    public MultiplicityImpl(final MultiplicityRange range) {
        this.range = new ArrayList<>();
        this.range.add(range);
    }
    
    /**
     * Build a complex Mulitiplicity 
     */
    public MultiplicityImpl(final List<MultiplicityRange> range) {
        this.range = range;
    }
    
    /**
     * Gets the value of the range property.
     */
    @Override
    public List<MultiplicityRange> getRange() {
        if (range == null) {
            range = new ArrayList<>();
        }
        return this.range;
    }
    
    /**
     * sets the value of the range property.
     */
    public void setRange(final List<MultiplicityRange> range) {
        this.range = range;
    }
    
    
    /**
     * sets the value of the range property.
     */
    public void setRange(final MultiplicityRange range) {
        if (this.range == null) {
            this.range = new ArrayList<>();
        }
        this.range.add(range);
    }
    
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        for (MultiplicityRange m: getRange()) {
            s.append(m).append(' ');
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
        if (object instanceof MultiplicityImpl) {
            final MultiplicityImpl that = (MultiplicityImpl) object;
            return Objects.equals(this.range, that.range);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + (this.range != null ? this.range.hashCode() : 0);
        return hash;
    }

}
