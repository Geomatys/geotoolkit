/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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
package org.geotoolkit.ogc.xml.v100;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Scalar_CapabilitiesType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Scalar_CapabilitiesType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice maxOccurs="unbounded">
 *         &lt;element ref="{http://www.opengis.net/ogc}Logical_Operators"/>
 *         &lt;element name="Comparison_Operators" type="{http://www.opengis.net/ogc}Comparison_OperatorsType"/>
 *         &lt;element name="Arithmetic_Operators" type="{http://www.opengis.net/ogc}Arithmetic_OperatorsType"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Scalar_CapabilitiesType", propOrder = {
    "logicalOperatorsOrComparisonOperatorsOrArithmeticOperators"
})
public class ScalarCapabilitiesType {

    @XmlElements({
        @XmlElement(name = "Arithmetic_Operators", type = ArithmeticOperatorsType.class),
        @XmlElement(name = "Comparison_Operators", type = ComparisonOperatorsType.class),
        @XmlElement(name = "Logical_Operators", type = LogicalOperators.class)
    })
    private List<Object> logicalOperatorsOrComparisonOperatorsOrArithmeticOperators;

    /**
     * Gets the value of the logicalOperatorsOrComparisonOperatorsOrArithmeticOperators property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the logicalOperatorsOrComparisonOperatorsOrArithmeticOperators property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLogicalOperatorsOrComparisonOperatorsOrArithmeticOperators().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ArithmeticOperatorsType }
     * {@link ComparisonOperatorsType }
     * {@link LogicalOperators }
     * 
     * 
     */
    public List<Object> getLogicalOperatorsOrComparisonOperatorsOrArithmeticOperators() {
        if (logicalOperatorsOrComparisonOperatorsOrArithmeticOperators == null) {
            logicalOperatorsOrComparisonOperatorsOrArithmeticOperators = new ArrayList<Object>();
        }
        return this.logicalOperatorsOrComparisonOperatorsOrArithmeticOperators;
    }

}
