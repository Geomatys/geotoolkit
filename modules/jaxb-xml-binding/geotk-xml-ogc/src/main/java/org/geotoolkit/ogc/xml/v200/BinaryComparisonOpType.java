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
import java.util.List;
import java.util.Objects;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;
import org.opengis.filter.ComparisonOperatorName;
import org.opengis.filter.MatchAction;
import org.opengis.filter.Expression;


/**
 * <p>Java class for BinaryComparisonOpType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="BinaryComparisonOpType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/fes/2.0}ComparisonOpsType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/fes/2.0}expression" maxOccurs="2" minOccurs="2"/>
 *       &lt;/sequence>
 *       &lt;attribute name="matchCase" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
 *       &lt;attribute name="matchAction" type="{http://www.opengis.net/fes/2.0}MatchActionType" default="Any" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BinaryComparisonOpType", propOrder = {
    "expression"
})
public class BinaryComparisonOpType extends ComparisonOpsType implements org.geotoolkit.ogc.xml.BinaryComparisonOperator {

    @XmlElementRef(name = "expression", namespace = "http://www.opengis.net/fes/2.0", type = JAXBElement.class)
    private List<JAXBElement<?>> expression;
    @XmlAttribute
    private Boolean matchCase;
    @XmlAttribute
    private MatchActionType matchAction;

    private static final ObjectFactory FACTORY = new ObjectFactory();

    /**
     * Empty constructor used by JAXB
     */
    public BinaryComparisonOpType() {
    }

    /**
     * Build a new Binary comparison operator
     */
    public BinaryComparisonOpType(final List<JAXBElement<?>> expression, final Boolean matchCase) {
        this.expression = expression;
        this.matchCase = matchCase;
    }

    /**
     * Build a new Binary comparison operator
     */
    public BinaryComparisonOpType(final LiteralType literal, final String propertyName, final Boolean matchCase) {
        if (this.expression == null) {
            this.expression = new ArrayList<>();
        }
        if (propertyName != null) {
            this.expression.add(FACTORY.createValueReference(propertyName));
        }
        if (literal != null) {
            this.expression.add(FACTORY.createLiteral(literal));
        }
        this.matchCase = matchCase;
    }

    public BinaryComparisonOpType(final BinaryComparisonOpType that) {
        if (that != null) {
            if (that.expression != null) {
                this.expression = new ArrayList<>();
                final ObjectFactory factory = new ObjectFactory();
                for (JAXBElement jb : that.expression) {
                    final Object exp = jb.getValue();
                    if (exp instanceof String) {
                        this.expression.add(factory.createValueReference((String)exp));
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
            this.matchAction = that.matchAction;
            this.matchCase   = that.matchCase;
        }
    }

    /**
     * Gets the value of the expression property.
     *
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link LiteralType }{@code >}
     * {@link JAXBElement }{@code <}{@link Object }{@code >}
     * {@link JAXBElement }{@code <}{@link String }{@code >}
     * {@link JAXBElement }{@code <}{@link FunctionType }{@code >}
     */
    public List<JAXBElement<?>> getExpression() {
        if (expression == null) {
            expression = new ArrayList<>();
        }
        return this.expression;
    }

    public void addValueReference(final String prop) {
        if (expression == null) {
            expression = new ArrayList<>();
        }
        this.expression.add(FACTORY.createValueReference(prop));
    }

    /**
     * Gets the value of the matchCase property.
     */
    @Override
    public boolean isMatchingCase() {
        if (matchCase == null) {
            return true;
        } else {
            return matchCase;
        }
    }

    /**
     * Sets the value of the matchCase property.
     */
    public void setMatchCase(Boolean value) {
        this.matchCase = value;
    }

    /**
     * Gets the value of the matchAction property.
     */
    public MatchActionType getMatchActionType() {
        if (matchAction == null) {
            return MatchActionType.ANY;
        } else {
            return matchAction;
        }
    }

    /**
     * Sets the value of the matchAction property.
     */
    public void setMatchAction(MatchActionType value) {
        this.matchAction = value;
    }

    @Override
    public List getExpressions() {
        return Arrays.asList(getOperand1(), getOperand2());
    }

    @Override
    public Expression getExpression1() {
        return getOperand1();
    }

    @Override
    public Expression getOperand1() {
        for (JAXBElement<?> elem : getExpression()) {
            final Object value = elem.getValue();
            if (value instanceof String) {
                return new InternalPropertyName((String) value);
            }
            if (value instanceof InternalPropertyName) {
                return (InternalPropertyName) value;
            }
        }
        return null;
    }

    @Override
    public Expression getOperand2() {
        for (JAXBElement<?> elem : getExpression()) {
            if (elem.getValue() instanceof LiteralType) {
                return (LiteralType)elem.getValue();
            }
        }
        return null;
    }

    @Override
    public String getPropertyName() {
        for (JAXBElement<?> elem : getExpression()) {
            final Object value = elem.getValue();
            if (value instanceof String) {
                return (String) value;
            }
            if (value instanceof InternalPropertyName) {
                return ((InternalPropertyName) value).getXPath();
            }
        }
        return null;
    }

    @Override
     public LiteralType getLiteral() {
        for (JAXBElement<?> elem : getExpression()) {
            if (elem.getValue() instanceof LiteralType) {
                return (LiteralType)elem.getValue();
            }
        }
        return null;
    }

    @Override
    public MatchAction getMatchAction() {
        if (this.matchAction != null) {
            return MatchAction.valueOf(this.matchAction.name());
        }
        return MatchAction.ANY;
    }

    @Override
    public String toString() {
        final StringBuilder s = new StringBuilder(super.toString());
        s.append("MatchCase ? ").append(matchCase).append('\n');
        if (expression != null) {
            s.append("expression: ").append('\n');
            for (JAXBElement<?> elem : expression) {
                final Object value = elem.getValue();
                s.append(value).append('\n');
            }
        }
        return s.toString();
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + (this.expression != null ? this.expression.hashCode() : 0);
        hash = 67 * hash + (this.matchCase != null ? this.matchCase.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof BinaryComparisonOpType) {
            final BinaryComparisonOpType that = (BinaryComparisonOpType) obj;
            final boolean exp;
            if (this.expression == null && that.expression == null) {
                exp = true;
            } else if (this.expression != null && that.expression != null) {
                if (this.expression.size() == that.expression.size()) {
                    exp = true;
                    for (int i = 0; i< this.expression.size(); i++) {
                        if (!Objects.equals(this.expression.get(i).getValue(), that.expression.get(i).getValue())) {
                            return false;
                        }
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }
            return exp && Objects.equals(this.matchCase, that.matchCase);
        }
        return false;
    }

    @Override
    public ComparisonOpsType getClone() {
        throw new UnsupportedOperationException("Must be overriden in sub-class.");
    }

    @Override
    public ComparisonOperatorName getOperatorType() {
        throw new UnsupportedOperationException("Must be overriden by sub-class");
    }
}
