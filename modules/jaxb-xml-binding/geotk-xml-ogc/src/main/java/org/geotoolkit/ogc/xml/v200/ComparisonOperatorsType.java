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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.filter.capability.ComparisonOperators;
import org.geotoolkit.filter.capability.Operator;


/**
 * <p>Java class for ComparisonOperatorsType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="ComparisonOperatorsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence maxOccurs="unbounded">
 *         &lt;element name="ComparisonOperator" type="{http://www.opengis.net/fes/2.0}ComparisonOperatorType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ComparisonOperatorsType", propOrder = {
    "comparisonOperator"
})
public class ComparisonOperatorsType extends ComparisonOperators {

    @XmlElement(name = "ComparisonOperator", required = true)
    private List<ComparisonOperatorType> comparisonOperator;

    /**
     * An empty constructor used by JAXB
     */
    public ComparisonOperatorsType() {
    }

    /**
     * Build a new comparison operators with the specified array of operator
     *
     * @param operators an array of comparison operator
     */
    public ComparisonOperatorsType(Operator[] operators) {
        if (operators == null){
            operators = new Operator[]{};
        }
        this.comparisonOperator = new ArrayList(Arrays.asList(operators));
    }

    /**
     * Gets the value of the comparisonOperator property.
     */
    public List<ComparisonOperatorType> getComparisonOperator() {
        if (comparisonOperator == null) {
            comparisonOperator = new ArrayList<>();
        }
        return this.comparisonOperator;
    }

    /**
     * Gets the value of the comparisonOperator property.
     */
    public Collection<Operator> getOperators() {
        List<Operator> result =  new ArrayList<>();
        if (comparisonOperator == null) {
            comparisonOperator = new ArrayList<>();
            return result;
        } else {
            for (ComparisonOperatorType c: comparisonOperator) {
                result.add(c);
            }
        }
        return result;
    }

    /**
     * @return Operator with the provided name, or null if not supported
     */
    @Override
    public Operator getOperator(final String name) {
        if ( name == null || comparisonOperator == null) {
            return null;
        }
        for ( Operator operator : comparisonOperator ) {
            if ( name.equals( operator.getName() ) ) {
                return operator;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("[ComparisonOperatorsType]").append("\n");
        if (comparisonOperator != null) {
            sb.append("comparisonOperator:\n");
            for (ComparisonOperatorType q: comparisonOperator) {
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
        if (object instanceof ComparisonOperatorsType) {
            final ComparisonOperatorsType that = (ComparisonOperatorsType) object;
            return Objects.equals(this.comparisonOperator, that.comparisonOperator);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + (this.comparisonOperator != null ? this.comparisonOperator.hashCode() : 0);
        return hash;
    }
}
