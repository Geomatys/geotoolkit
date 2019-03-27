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
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;
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
 *         &lt;choice maxOccurs="unbounded" minOccurs="0">
 *           &lt;element ref="{http://www.opengis.net/ogc}expression"/>
 *           &lt;element ref="{http://www.opengis.net/ogc}Literal"/>
 *           &lt;element ref="{http://www.opengis.net/ogc}PropertyName"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *       &lt;attribute name="matchCase" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 * @author Guilhem Legal
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BinaryComparisonOpType", propOrder = {
    "expression"
})
public class BinaryComparisonOpType extends ComparisonOpsType implements BinaryComparisonOperator, org.geotoolkit.ogc.xml.BinaryComparisonOperator {

    @XmlElementRef(name = "expression", namespace = "http://www.opengis.net/ogc", type = JAXBElement.class)
    protected List<JAXBElement<?>> expression;

    @XmlAttribute
    private Boolean matchCase;

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
    public BinaryComparisonOpType(final LiteralType literal, final PropertyNameType propertyName, final Boolean matchCase) {
        if (this.expression == null) {
            this.expression = new ArrayList<>();
        }
        if (propertyName != null) {
            this.expression.add(FACTORY.createPropertyName(propertyName));
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
            this.matchCase   = that.matchCase;
        }
    }

    /**
     * Gets the value of the expression property.
     *
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link BinaryOperatorType }{@code >}
     * {@link JAXBElement }{@code <}{@link MapItemType }{@code >}
     * {@link JAXBElement }{@code <}{@link BinaryOperatorType }{@code >}
     * {@link JAXBElement }{@code <}{@link LiteralType }{@code >}
     * {@link JAXBElement }{@code <}{@link InterpolateType }{@code >}
     * {@link JAXBElement }{@code <}{@link ConcatenateType }{@code >}
     * {@link JAXBElement }{@code <}{@link ChangeCaseType }{@code >}
     * {@link JAXBElement }{@code <}{@link PropertyNameType }{@code >}
     * {@link JAXBElement }{@code <}{@link TrimType }{@code >}
     * {@link JAXBElement }{@code <}{@link BinaryOperatorType }{@code >}
     * {@link JAXBElement }{@code <}{@link net.opengis.ogc.FunctionType }{@code >}
     * {@link JAXBElement }{@code <}{@link FormatDateType }{@code >}
     * {@link JAXBElement }{@code <}{@link CategorizeType }{@code >}
     * {@link JAXBElement }{@code <}{@link BinaryOperatorType }{@code >}
     * {@link JAXBElement }{@code <}{@link ExpressionType }{@code >}
     * {@link JAXBElement }{@code <}{@link InterpolationPointType }{@code >}
     * {@link JAXBElement }{@code <}{@link StringLengthType }{@code >}
     * {@link JAXBElement }{@code <}{@link RecodeType }{@code >}
     * {@link JAXBElement }{@code <}{@link net.opengis.se.FunctionType }{@code >}
     * {@link JAXBElement }{@code <}{@link FormatNumberType }{@code >}
     * {@link JAXBElement }{@code <}{@link SubstringType }{@code >}
     * {@link JAXBElement }{@code <}{@link StringPositionType }{@code >}
     *
     *
     */
    public List<JAXBElement<?>> getExpression() {
        if (expression == null) {
            expression = new ArrayList<>();
        }
        return this.expression;
    }

    /**
     * Gets the value of the matchCase property.
     */
    public Boolean getMatchCase() {
        return matchCase;
    }

    /**
     * Gets the value of the matchCase property.
     */
    @Override
    public boolean isMatchingCase() {
        if (matchCase == null) {
            return false;
        }
        return matchCase;
    }

    /**
     * sets the value of the matchCase property.
     */
    public void setMatchCase(final Boolean matchCase) {
        this.matchCase = matchCase;
    }

    @Override
    public boolean evaluate(final Object object) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object accept(final FilterVisitor visitor, final Object extraData) {
        throw new UnsupportedOperationException("Not supported yet.");
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
    public LiteralType getLiteral() {
        for (JAXBElement<?> elem : getExpression()) {
            if (elem.getValue() instanceof LiteralType) {
                return (LiteralType)elem.getValue();
            }
        }
        return null;
    }

    public void setLiteral(final LiteralType literal) {
        this.getExpression().add(FACTORY.createLiteral(literal));
    }

    public ExpressionType getExpressionType() {
        for (JAXBElement<?> elem : getExpression()) {
            if (elem.getValue() instanceof ExpressionType) {
                return (ExpressionType)elem.getValue();
            }
        }
        return null;
    }

    public void setExpressionType(final ExpressionType expression) {
        this.getExpression().add(FACTORY.createExpression(expression));
    }

    @Override
    public String getPropertyName() {
        for (JAXBElement<?> elem : getExpression()) {
                final Object value = elem.getValue();
                if (value instanceof String) {
                return (String) value;
            }
            if (value instanceof QName) {
                    QName content = (QName) value;
                    if (content.getNamespaceURI() != null && !"".equals(content.getNamespaceURI())) {
                    return content.getNamespaceURI() + ':' + content.getLocalPart();
                    }
                return content.getLocalPart();
                }
            if (value instanceof PropertyNameType) {
                return ((PropertyNameType) value).getContent();
            }
        }
        return null;
    }

    public void setPropertyName(final String propertyName) {
        getExpression().add(0, FACTORY.createPropertyName(new PropertyNameType(propertyName)));
    }

    public void setPropertyName(final PropertyNameType propertyName) {
        getExpression().add(0, FACTORY.createPropertyName(propertyName));
    }

    @Override
    public MatchAction getMatchAction() {
        return MatchAction.ANY;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(super.toString());
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
            BinaryComparisonOpType that = (BinaryComparisonOpType) obj;
            return Objects.equals(this.matchCase, that.matchCase) &&
                   Objects.equals(this.getLiteral(), that.getLiteral()) &&
                   Objects.equals(this.getPropertyName(), that.getPropertyName()) &&
                   Objects.equals(this.getExpressionType(), that.getExpressionType());
        }
        return false;
    }

    @Override
    public ComparisonOpsType getClone() {
        throw new UnsupportedOperationException("Must be overriden by sub-class");
    }
}
