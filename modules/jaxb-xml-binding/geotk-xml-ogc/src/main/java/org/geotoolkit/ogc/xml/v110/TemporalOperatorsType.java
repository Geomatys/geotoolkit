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
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.util.Utilities;


/**
 * <p>Java class for TemporalOperatorsType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TemporalOperatorsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="TemporalOperator" type="{http://www.opengis.net/ogc}TemporalOperatorType" maxOccurs="unbounded"/>
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
@XmlType(name = "TemporalOperatorsType", propOrder = {
    "temporalOperator"
})
public class TemporalOperatorsType {

    @XmlElement(name = "TemporalOperator", required = true)
    private List<TemporalOperatorType> temporalOperator;

    /**
     * Gets the value of the temporalOperator property.
     * 
     */
    public List<TemporalOperatorType> getTemporalOperator() {
        if (temporalOperator == null) {
            temporalOperator = new ArrayList<TemporalOperatorType>();
        }
        return this.temporalOperator;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("[TemporalOperatorsType]").append("\n");
        if (temporalOperator != null) {
            sb.append("temporalOperator:\n");
            for (TemporalOperatorType q: temporalOperator) {
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
        if (object instanceof TemporalOperatorsType) {
            final TemporalOperatorsType that = (TemporalOperatorsType) object;

            return Utilities.equals(this.temporalOperator, that.temporalOperator);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + (this.temporalOperator != null ? this.temporalOperator.hashCode() : 0);
        return hash;
    }

}
