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
import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;
import org.opengis.filter.capability.TemporalOperand;
import org.opengis.filter.capability.TemporalOperator;


/**
 * <p>Java class for TemporalOperatorType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="TemporalOperatorType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="TemporalOperands" type="{http://www.opengis.net/ogc}TemporalOperandsType" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="name" type="{http://www.opengis.net/ogc}TemporalOperatorNameType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TemporalOperatorType", propOrder = {
    "temporalOperands"
})
public class TemporalOperatorType implements TemporalOperator {

    @XmlElement(name = "TemporalOperands")
    private TemporalOperandsType temporalOperands;
    @XmlAttribute
    private TemporalOperatorNameType name;

    /**
     * Gets the value of the temporalOperands property.
     *
     */
    public TemporalOperandsType getTemporalOperandsType() {
        return temporalOperands;
    }

    /**
     * Sets the value of the temporalOperands property.
     *
     */
    public void setTemporalOperands(final TemporalOperandsType value) {
        this.temporalOperands = value;
    }

    /**
     * Implements SpatialOperator geoAPI interface
     * @return
     */
    @Override
    public Collection<TemporalOperand> getTemporalOperands() {
        List<TemporalOperand> result = new ArrayList<TemporalOperand>();
        if (temporalOperands != null) {
            for (QName qn: temporalOperands.getTemporalOperand()) {
                result.add(TemporalOperand.get(qn.getNamespaceURI(), qn.getLocalPart()));
            }
        }
        return result;
    }

    @Override
    public String getName() {
        if (name != null) {
            return name.name();
        }
        return null;
    }

    /**
     * Sets the value of the name property.
     *
     */
    public void setName(final TemporalOperatorNameType value) {
        this.name = value;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[TemporalOperatorType]").append("\n");
        if (temporalOperands != null) {
            sb.append("temporalOperands: ").append(temporalOperands).append('\n');
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

       if (object instanceof TemporalOperatorType) {
           final TemporalOperatorType that = (TemporalOperatorType) object;
       
            return Objects.equals(this.temporalOperands, that.temporalOperands) &&
                   Objects.equals(this.name, that.name);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + (this.temporalOperands != null ? this.temporalOperands.hashCode() : 0);
        hash = 23 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }

}
