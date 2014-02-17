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
package org.geotoolkit.ogc.xml.v100;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;
import org.opengis.filter.BinaryComparisonOperator;
import org.opengis.filter.FilterVisitor;
import org.opengis.filter.MatchAction;
import org.opengis.filter.expression.Expression;


/**
 * <p>Java class for BinaryComparisonOpType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BinaryComparisonOpType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/ogc}ComparisonOpsType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/ogc}expression" maxOccurs="2" minOccurs="2"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BinaryComparisonOpType", propOrder = {
    "expression"
})
public class BinaryComparisonOpType extends ComparisonOpsType implements BinaryComparisonOperator {

    @XmlElementRef(name = "expression", namespace = "http://www.opengis.net/ogc", type = JAXBElement.class)
    private List<JAXBElement<?>> expression;

    private static final ObjectFactory FACTORY = new ObjectFactory();
    
    public BinaryComparisonOpType() {

    }
    
    /**
     * Build a new Binary comparison operator
     */
    public BinaryComparisonOpType(final LiteralType literal, final PropertyNameType propertyName) {
        if (this.expression == null) {
            this.expression = new ArrayList<JAXBElement<?>>();
        }
        if (propertyName != null) {
            this.expression.add(FACTORY.createPropertyName(propertyName));
        }
        if (literal != null) {
            this.expression.add(FACTORY.createLiteral(literal));
        }
    }
    
    
    public BinaryComparisonOpType(final BinaryComparisonOpType that) {
        if (that != null) {
            if (that.expression != null) {
                this.expression = new ArrayList<JAXBElement<?>>();
                final ObjectFactory factory = new ObjectFactory();
                for (JAXBElement jb : that.expression) {
                    final Object exp = jb.getValue();
                    if (exp instanceof PropertyNameType) {
                        this.expression.add(factory.createPropertyName((PropertyNameType)exp));
                    } else if (exp instanceof LiteralType) {
                        final LiteralType lit = new LiteralType((LiteralType)exp);
                        this.expression.add(factory.createLiteral(lit));
                    } else if (exp instanceof FunctionType) {
                        final FunctionType func = new FunctionType((FunctionType)exp);
                        this.expression.add(factory.createFunction(func));
                    } else {
                        throw new IllegalArgumentException("Unexpected type for expression in BinaryComparisonOpType:" + expression.getClass().getName());
                    }
                }
            }
        }
    }
    /**
     * Gets the value of the expression property.
     * 
     */
    public List<JAXBElement<?>> getExpression() {
        if (expression == null) {
            expression = new ArrayList<JAXBElement<?>>();
        }
        return this.expression;
    }

    @Override
    public ComparisonOpsType getClone() {
        throw new IllegalArgumentException("Must be overriden by sub-class");
    }

    @Override
    public Expression getExpression1() {
        for (JAXBElement<?> elem : getExpression()) {
            final Object value = elem.getValue();
            if (value instanceof String) {
                return new PropertyNameType((String) value);
            }
            if (value instanceof PropertyNameType) {
                return (PropertyNameType) value;
            }
        }
        return null;
    }

    @Override
    public Expression getExpression2() {
        for (JAXBElement<?> elem : getExpression()) {
            if (elem.getValue() instanceof LiteralType) {
                return (LiteralType)elem.getValue();
            }
        }
        return null;
    }

    @Override
    public boolean isMatchingCase() {
        return true;
    }

    @Override
    public MatchAction getMatchAction() {
        return MatchAction.ANY;
    }

    @Override
    public boolean evaluate(Object o) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object accept(FilterVisitor fv, Object o) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
