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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;
import org.geotoolkit.util.Utilities;
import org.opengis.filter.capability.GeometryOperand;


/**
 * <p>Java class for GeometryOperandsType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GeometryOperandsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="GeometryOperand" type="{http://www.opengis.net/ogc}GeometryOperandType" maxOccurs="unbounded"/>
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
@XmlType(name = "GeometryOperandsType", propOrder = {
    "geometryOperand"
})
public class GeometryOperandsType {

    @XmlElement(name = "GeometryOperand", required = true)
    private List<QName> geometryOperand;

    /**
     * Empty constructor used by JAXB
     */
    public GeometryOperandsType() {
        
    }
    
    /**
     * build a new geometry Operands object with the specified array of GeometryOperand (from geoAPI)
     */
    public GeometryOperandsType(GeometryOperand[] geometryOperands) {
        if (geometryOperands == null) {
            geometryOperands = new GeometryOperand[0];
        }
        geometryOperand = new ArrayList<QName>();
        for (GeometryOperand g: geometryOperands) {
            geometryOperand.add(new QName(g.getNamespaceURI(), g.getLocalPart()));
        }
    }

    /**
     * build a new geometry Operands object with the specified array of GeometryOperand (from geoAPI)
     */
    public GeometryOperandsType(List<QName> geometryOperands) {
        if (geometryOperands == null) {
            geometryOperands = new ArrayList<QName>();
        }
        this.geometryOperand = geometryOperands;
    }
    
    /**
     * Gets the value of the geometryOperand property.
     * (unmodifiable)
     */
    public List<QName> getGeometryOperand() {
        if (geometryOperand == null) {
            geometryOperand = new ArrayList<QName>();
        }
        return Collections.unmodifiableList(geometryOperand);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("[GeometryOperandsType]").append("\n");
        if (geometryOperand != null) {
            sb.append("geometryOperand:\n");
            for (QName q: geometryOperand) {
                sb.append(q).append('\n');
            }
        }
        return sb.toString();
    }

    /**
     * Verify if this entry is identical to specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }

       if (object instanceof GeometryOperandsType) {
           final GeometryOperandsType that = (GeometryOperandsType) object;
       
            return Utilities.equals(this.geometryOperand, that.geometryOperand);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + (this.geometryOperand != null ? this.geometryOperand.hashCode() : 0);
        return hash;
    }
}
