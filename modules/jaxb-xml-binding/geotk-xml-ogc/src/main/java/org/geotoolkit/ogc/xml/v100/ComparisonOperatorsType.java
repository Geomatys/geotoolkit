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
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;
import org.opengis.filter.capability.ComparisonOperators;
import org.opengis.filter.capability.Operator;


/**
 * <p>Java class for Comparison_OperatorsType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Comparison_OperatorsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice maxOccurs="unbounded">
 *         &lt;element ref="{http://www.opengis.net/ogc}Simple_Comparisons"/>
 *         &lt;element ref="{http://www.opengis.net/ogc}Like"/>
 *         &lt;element ref="{http://www.opengis.net/ogc}Between"/>
 *         &lt;element ref="{http://www.opengis.net/ogc}NullCheck"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Comparison_OperatorsType", propOrder = {
    "simpleComparisonsOrLikeOrBetween"
})
public class ComparisonOperatorsType implements ComparisonOperators {

    @XmlElements({
        @XmlElement(name = "NullCheck", type = NullCheck.class),
        @XmlElement(name = "Simple_Comparisons", type = SimpleComparisons.class),
        @XmlElement(name = "Between", type = Between.class),
        @XmlElement(name = "Like", type = Like.class)
    })
    private List<Object> simpleComparisonsOrLikeOrBetween;

    public ComparisonOperatorsType() {

    }

    public ComparisonOperatorsType(Operator[] operators) {
        if (operators == null){
            operators = new Operator[]{};
        }
        this.simpleComparisonsOrLikeOrBetween = new ArrayList(Arrays.asList(operators));
    }

    /**
     * Gets the value of the simpleComparisonsOrLikeOrBetween property.
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link NullCheck }
     * {@link SimpleComparisons }
     * {@link Between }
     * {@link Like }
     * 
     * 
     */
    public List<Object> getSimpleComparisonsOrLikeOrBetween() {
        if (simpleComparisonsOrLikeOrBetween == null) {
            simpleComparisonsOrLikeOrBetween = new ArrayList<>();
        }
        return this.simpleComparisonsOrLikeOrBetween;
    }

    @Override
    public Collection<Operator> getOperators() {
        final List<Operator> result = new ArrayList<>();
        for (Object o : simpleComparisonsOrLikeOrBetween) {
            if (o instanceof Operator) {
                result.add((Operator) o);
            }
        }
        return result;
    }

    @Override
    public Operator getOperator(String name) {
        for (Object o : simpleComparisonsOrLikeOrBetween) {
            if (o instanceof Operator && ((Operator)o).getName().equals(name)) {
                return (Operator) o;
            }
        }
        return null;
    }

}
