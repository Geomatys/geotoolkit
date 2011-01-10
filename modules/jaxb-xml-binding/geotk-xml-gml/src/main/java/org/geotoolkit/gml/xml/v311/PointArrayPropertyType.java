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
package org.geotoolkit.gml.xml.v311;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.util.Utilities;


/**
 * A container for an array of points. The elements are always contained in the array property, referencing geometry 
 * 			elements or arrays of geometry elements is not supported.
 * 
 * <p>Java class for PointArrayPropertyType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PointArrayPropertyType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/gml}Point" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PointArrayPropertyType", propOrder = {
    "point"
})
public class PointArrayPropertyType {

    @XmlElement(name = "Point")
    private List<PointType> point;

    /**
     * Gets the value of the point property.
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link PointType }
     */
    public List<PointType> getPoint() {
        if (point == null) {
            point = new ArrayList<PointType>();
        }
        return this.point;
    }

    public void setPoint(final List<PointType> point) {
        this.point = point;
    }

    public void setPoint(final PointType point) {
        if (point != null) {
            if (this.point == null) {
                this.point = new ArrayList<PointType>();
            }
            this.point.add(point);
        }
    }

    /**
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof PointArrayPropertyType) {
            final PointArrayPropertyType that = (PointArrayPropertyType) object;

            return Utilities.equals(this.point,  that.point);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 19 * hash + (this.point != null ? this.point.hashCode() : 0);
        return hash;
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("[PointArrayPropertyType]");
        if (point != null) {
            sb.append("point:").append('\n');
            for (PointType sp : point) {
                sb.append(sp).append('\n');
            }
        }
        return sb.toString();
    }

}
