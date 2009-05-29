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
package org.geotoolkit.gml.xml.v311modified;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.util.Utilities;


/**
 * <p>Java class for DefinitionType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DefinitionType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml}DefinitionBaseType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/gml}remarks" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * @author Guilhem Legal
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DefinitionType", propOrder = {
    "remarks"
})
public class DefinitionType extends DefinitionBaseType {

    private String remarks;

    /**
     * An empty constructor used by JAXB
     */
    protected DefinitionType()  {
        
    }

    /**
     * super constructor to access to Entry constructor
     */
    public DefinitionType(final String id)  {
        super(id);
    }

    /**
     * super constructor to access to Entry constructor
     */
    public DefinitionType(final String id, final String name, final String description )  {
        super(id, name, description);
    }
    
    /**
     * Gets the value of the remarks property.
     */
    public String getRemarks() {
        return remarks;
    }

     /**
     * Vérifie si cette entré est identique à l'objet spécifié.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof DefinitionType && super.equals(object)) {
            final DefinitionType that = (DefinitionType) object;
            return Utilities.equals(this.remarks, that.remarks); 
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + (this.remarks != null ? this.remarks.hashCode() : 0);
        return hash;
    }
    
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(super.toString());
        if (remarks != null)
            s.append("remarks:").append(remarks);
        return  s.toString();
    }

}
