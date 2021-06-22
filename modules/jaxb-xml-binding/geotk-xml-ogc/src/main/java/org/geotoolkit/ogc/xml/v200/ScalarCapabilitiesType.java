/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2011, Geomatys
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


package org.geotoolkit.ogc.xml.v200;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.filter.capability.ArithmeticOperators;
import org.geotoolkit.filter.capability.ComparisonOperators;
import org.geotoolkit.filter.capability.ScalarCapabilities;


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
 *         &lt;element ref="{http://www.opengis.net/fes/2.0}LogicalOperators" minOccurs="0"/>
 *         &lt;element name="ComparisonOperators" type="{http://www.opengis.net/fes/2.0}ComparisonOperatorsType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Scalar_CapabilitiesType", propOrder = {
    "logicalOperators",
    "comparisonOperators"
})
public class ScalarCapabilitiesType extends ScalarCapabilities {

    @XmlElement(name = "LogicalOperators")
    private LogicalOperators logicalOperators;
    @XmlElement(name = "ComparisonOperators")
    private ComparisonOperatorsType comparisonOperators;

    /**
     * An empty constructor used by JAXB
     */
    public ScalarCapabilitiesType() {
    }

     /**
     *Build a new Scalar Capabilities
     */
    public ScalarCapabilitiesType(final ComparisonOperators comparison, final boolean logical) {
        if (logical) {
            this.logicalOperators = new LogicalOperators();
        }
        this.comparisonOperators = (ComparisonOperatorsType) comparison;
    }

    /**
     * Gets the value of the logicalOperators property.
     */
    public LogicalOperators getLogicalOperators() {
        return logicalOperators;
    }

    /**
     * Sets the value of the logicalOperators property.
     */
    public void setLogicalOperators(LogicalOperators value) {
        this.logicalOperators = value;
    }

    /**
     * Gets the value of the comparisonOperators property.
     */
    @Override
    public ComparisonOperatorsType getComparisonOperators() {
        return comparisonOperators;
    }

    /**
     * Sets the value of the comparisonOperators property.
     */
    public void setComparisonOperators(ComparisonOperatorsType value) {
        this.comparisonOperators = value;
    }

    @Override
    public ArithmeticOperators getArithmeticOperators() {
        return null;
    }

    @Override
    public boolean hasLogicalOperators() {
        return logicalOperators != null;
    }
}
