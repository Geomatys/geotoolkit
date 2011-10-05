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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.util.Utilities;
import org.opengis.filter.capability.ArithmeticOperators;
import org.opengis.filter.capability.ComparisonOperators;
import org.opengis.filter.capability.ScalarCapabilities;


/**
 * <p>Java class for Scalar_CapabilitiesType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Scalar_CapabilitiesType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/ogc}LogicalOperators" minOccurs="0"/>
 *         &lt;element name="ComparisonOperators" type="{http://www.opengis.net/ogc}ComparisonOperatorsType" minOccurs="0"/>
 *         &lt;element name="ArithmeticOperators" type="{http://www.opengis.net/ogc}ArithmeticOperatorsType" minOccurs="0"/>
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
@XmlType(name = "Scalar_CapabilitiesType", propOrder = {
    "logicalOperators",
    "comparisonOperators",
    "arithmeticOperators"
})
public class ScalarCapabilitiesType implements ScalarCapabilities {

    @XmlElement(name = "LogicalOperators")
    private LogicalOperators logicalOperators;
    @XmlElement(name = "ComparisonOperators")
    private ComparisonOperatorsType comparisonOperators;
    @XmlElement(name = "ArithmeticOperators")
    private ArithmeticOperatorsType arithmeticOperators;

    /**
     * An empty constructor used by JAXB
     */
    public ScalarCapabilitiesType() {
    }
    
     /**
     *Build a new Scalar Capabilities
     */
    public ScalarCapabilitiesType(final ComparisonOperators comparison, final ArithmeticOperators arithmetic, final boolean logical) {
        if (logical) {
            this.logicalOperators = new LogicalOperators();
        }
        this.comparisonOperators = (ComparisonOperatorsType) comparison;
        this.arithmeticOperators = (ArithmeticOperatorsType) arithmetic;
    }
    
    /**
     * Gets the value of the logicalOperators property.
     */
    public LogicalOperators getLogicalOperators() {
        return logicalOperators;
    }

    /**
     * Gets the value of the comparisonOperators property.
     */
    public ComparisonOperatorsType getComparisonOperators() {
        return comparisonOperators;
    }

    /**
     * Gets the value of the arithmeticOperators property.
     */
    public ArithmeticOperatorsType getArithmeticOperators() {
        return arithmeticOperators;
    }

    public boolean hasLogicalOperators() {
        return logicalOperators != null;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[ScalarCapabilitiesType]").append("\n");
        if (logicalOperators != null) {
            sb.append("logicalOperators: ").append(logicalOperators).append('\n');
        }
        if (comparisonOperators != null) {
            sb.append("comparisonOperators: ").append(comparisonOperators).append('\n');
        }
        if (arithmeticOperators != null) {
            sb.append("arithmeticOperators: ").append(arithmeticOperators).append('\n');
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

       if (object instanceof ScalarCapabilitiesType) {
           final ScalarCapabilitiesType that = (ScalarCapabilitiesType) object;
       
            return Utilities.equals(this.logicalOperators, that.logicalOperators) &&
                   Utilities.equals(this.arithmeticOperators, that.arithmeticOperators) &&
                   Utilities.equals(this.comparisonOperators, that.comparisonOperators);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + (this.logicalOperators != null ? this.logicalOperators.hashCode() : 0);
        hash = 67 * hash + (this.comparisonOperators != null ? this.comparisonOperators.hashCode() : 0);
        hash = 67 * hash + (this.arithmeticOperators != null ? this.arithmeticOperators.hashCode() : 0);
        return hash;
    }
}
