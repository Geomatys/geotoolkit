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
package org.geotoolkit.gml.xml.v311;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.util.Utilities;


/**
 * Java class for PointType complex type.
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {
    "pos",
    "coordinates"
})
@XmlRootElement(name="Point")
public class PointType extends AbstractGeometricPrimitiveType {

    private DirectPositionType pos;
    private CoordinatesType coordinates;

    /**
     * An empty constructor used by JAXB.
     */
    public PointType() {}
    
    /**
     * Build a new Point with the specified identifier and DirectPositionType
     *  
     * @param id The identifier of the point.
     * @param pos A direcPosition locating the point.
     */
    public PointType(String id, DirectPositionType pos) {
        super.setId(id);
        this.pos = pos;
    }
    
    /**
     * Build a point Type with the specified coordinates.
     * 
     * @param coordinates a list of coordinates.
     */
    public PointType(CoordinatesType coordinates) {
        this.coordinates = coordinates;
    }
     
    /**
     * Gets the value of the pos property.
     * 
     */
    public DirectPositionType getPos() {
        return pos;
    }

    /**
     * Gets the value of the coordinates property.
     */
    public CoordinatesType getCoordinates() {
        return coordinates;
    }
   
    
    /**
     * Retourne un description de l'objet.
     */
    @Override
    public String toString() {
        StringBuilder s =new StringBuilder("id = ").append(this.getId()).append('\n'); 
        if(pos != null) {
            s.append("position : ").append(pos.toString()).append('\n'); 
        }
        
        if( coordinates != null) {
            s.append(" coordinates : ").append(coordinates.toString()).append('\n'); 
        }
        
        return s.toString();
    }
    
    /**
     * Vérifie que cette station est identique à l'objet spécifié
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof PointType && super.equals(object)) {
            final PointType that = (PointType) object;
            return  Utilities.equals(this.pos, that.pos) &&
                    Utilities.equals(this.coordinates, that.coordinates);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.getId().hashCode();
    }

}
