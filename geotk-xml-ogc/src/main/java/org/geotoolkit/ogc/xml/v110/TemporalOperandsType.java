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
import java.util.List;
import java.util.Objects;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;
import org.geotoolkit.filter.capability.TemporalOperand;


/**
 * <p>Java class for TemporalOperandsType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="TemporalOperandsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="TemporalOperand" type="{http://www.opengis.net/ogc}TemporalOperandType" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TemporalOperandsType", propOrder = {
    "temporalOperand"
})
public class TemporalOperandsType {

    @XmlElement(name = "TemporalOperand", required = true)
    private List<QName> temporalOperand;

    /**
     * Empty constructor used by JAXB
     */
    public TemporalOperandsType() {
    }

    /**
     * build a new temporal Operands object with the specified array of TemporalOperand (from geoAPI)
     */
    public TemporalOperandsType(TemporalOperand[] tmpOperands) {
        if (tmpOperands == null) {
            tmpOperands = new TemporalOperand[0];
        }
        temporalOperand = new ArrayList<QName>();
        for (TemporalOperand g: tmpOperands) {
            temporalOperand.add(new QName("http://www.opengis.net/fes/2.0", g.name()));
        }
    }

    /**
     * build a new geometry Operands object with the specified array of GeometryOperand (from geoAPI)
     */
    public TemporalOperandsType(List<QName> tmpOperands) {
        if (tmpOperands == null) {
            tmpOperands = new ArrayList<QName>();
        }
        this.temporalOperand = tmpOperands;
    }

    /**
     * Gets the value of the temporalOperand property.
     *
     */
    public List<QName> getTemporalOperand() {
        if (temporalOperand == null) {
            temporalOperand = new ArrayList<QName>();
        }
        return this.temporalOperand;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("[TemporalOperandsType]").append("\n");
        if (temporalOperand != null) {
            sb.append("temporalOperand:\n");
            for (QName q: temporalOperand) {
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
       if (object instanceof TemporalOperandsType) {
           final TemporalOperandsType that = (TemporalOperandsType) object;

            return Objects.equals(this.temporalOperand, that.temporalOperand);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + (this.temporalOperand != null ? this.temporalOperand.hashCode() : 0);
        return hash;
    }
}
