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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.opengis.feature.catalog.Binding;

/**
 *
 * @author Guilhem Legal
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FC_Binding_Type")
@XmlRootElement(name = "FC_Binding")
public class BindingImpl implements Binding {

    private String description;
    
    /**
     * An empty constructor used by JAXB
     */
    public BindingImpl() {
        
    }
    
    /**
     * Clone a Binding
     */
    public BindingImpl(final Binding feature) {
        if (feature != null)
            this.description = feature.getDescription();
        
    }
    public String getDescription() {
        return description;
    }
    
    public void setDescription(final String description) {
        this.description = description;
    }
    
    @Override
    public String toString() {
        return "[Binding]: description: " + description;  
    }
    
     /**
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof BindingImpl) {
            final BindingImpl that = (BindingImpl) object;
            
            return Objects.equals(this.description, that.description);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.description != null ? this.description.hashCode() : 0);
        return hash;
    }

}
