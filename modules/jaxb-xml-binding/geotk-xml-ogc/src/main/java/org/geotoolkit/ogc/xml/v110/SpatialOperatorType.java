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
import java.util.Collection;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;
import org.geotoolkit.util.Utilities;
import org.opengis.filter.capability.GeometryOperand;
import org.opengis.filter.capability.SpatialOperator;

/**
 * <p>Java class for SpatialOperatorType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SpatialOperatorType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="GeometryOperands" type="{http://www.opengis.net/ogc}GeometryOperandsType" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="name" type="{http://www.opengis.net/ogc}SpatialOperatorNameType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SpatialOperatorType", propOrder = {
    "geometryOperands"
})
public class SpatialOperatorType implements SpatialOperator {

    @XmlElement(name = "GeometryOperands")
    private GeometryOperandsType geometryOperands;
    @XmlAttribute
    private SpatialOperatorNameType name;

    /**
     * An empty constructor used by JAXB 
     */
    public SpatialOperatorType() {
        
    }
    
    /**
     * build a new spatial operator 
     */
    public SpatialOperatorType(final String name, final GeometryOperand[] geometryOperands) {
        this.name = SpatialOperatorNameType.valueOf(name);
        if (geometryOperands != null) {
            this.geometryOperands = new GeometryOperandsType(geometryOperands);
        }
        
    }
    
    /**
     * Gets the value of the geometryOperands property.
     */
    public GeometryOperandsType getGeometryOperandsType() {
        return geometryOperands;
    }

    /**
     * Gets the value of the name property.
     */
    public SpatialOperatorNameType getTypeName() {
        return name;
    }

   
    /**
     * Implements SpatialOperator geoAPI interface
     * @return
     */
    public Collection<GeometryOperand> getGeometryOperands() {
        List<GeometryOperand> result = new ArrayList<GeometryOperand>();
        if (geometryOperands != null) {
            for (QName qn: geometryOperands.getGeometryOperand()) {
                result.add(GeometryOperand.get(qn.getNamespaceURI(), qn.getLocalPart()));
            }
        }
        return result;
    }

    public String getName() {
        if (name != null) {
            return name.name();
        }
        return null;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[SpatialOperatorType]").append("\n");
        if (geometryOperands != null) {
            sb.append("geometryOperands: ").append(geometryOperands).append('\n');
        }
        if (name != null) {
            sb.append("name: ").append(name).append('\n');
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

       if (object instanceof SpatialOperatorType) {
           final SpatialOperatorType that = (SpatialOperatorType) object;
       
            return Utilities.equals(this.geometryOperands, that.geometryOperands) &&
                   Utilities.equals(this.name, that.name);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + (this.geometryOperands != null ? this.geometryOperands.hashCode() : 0);
        hash = 67 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }
}
