/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2007 - 2008, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 3 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.gml.xml.v311modified;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.util.Utilities;


/**
 * <p>Java class for BaseUnitType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BaseUnitType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml}UnitDefinitionType">
 *       &lt;sequence>
 *         &lt;element name="unitsSystem" type="{http://www.opengis.net/gml}ReferenceType"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * @author Guilhem Legal
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BaseUnitType", propOrder = {
    "unitsSystem"
})
public class BaseUnitType extends UnitDefinitionType {

    @XmlElement(required = true)
    private ReferenceEntry unitsSystem;

    /**
     * Gets the value of the unitsSystem property.
     */
    public ReferenceEntry getUnitsSystem() {
        return unitsSystem;
    }

    /**
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof BaseUnitType && super.equals(object)) {
            final BaseUnitType that = (BaseUnitType) object;
            return Utilities.equals(this.unitsSystem,        that.unitsSystem); 
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + (this.unitsSystem != null ? this.unitsSystem.hashCode() : 0);
        return hash;
    }
    
    @Override
    public String toString() {
        String s = "";
        if (unitsSystem != null)
            s =  unitsSystem.toString();
        return "[BaseUnitType]:" + s;
    }


}
