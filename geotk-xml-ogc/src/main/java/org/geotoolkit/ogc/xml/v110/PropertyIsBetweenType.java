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

import java.util.Arrays;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;
import org.opengis.filter.BetweenComparisonOperator;
import org.opengis.filter.Expression;


/**
 * <p>Java class for PropertyIsBetweenType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="PropertyIsBetweenType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/ogc}ComparisonOpsType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/ogc}expression"/>
 *         &lt;element name="LowerBoundary" type="{http://www.opengis.net/ogc}LowerBoundaryType"/>
 *         &lt;element name="UpperBoundary" type="{http://www.opengis.net/ogc}UpperBoundaryType"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PropertyIsBetweenType", propOrder = {
    "expression",
    "lowerBoundary",
    "upperBoundary"
})
public class PropertyIsBetweenType extends ComparisonOpsType implements BetweenComparisonOperator {

    @XmlElementRef(name = "expression", namespace = "http://www.opengis.net/ogc", type = JAXBElement.class)
    protected JAXBElement<?> expression;
    @XmlElement(name = "LowerBoundary", required = true)
    private LowerBoundaryType lowerBoundary;
    @XmlElement(name = "UpperBoundary", required = true)
    private UpperBoundaryType upperBoundary;

    private static final ObjectFactory FACTORY = new ObjectFactory();

    /**
     * An empty constructor used by JAXB
     */
    public PropertyIsBetweenType() {
    }

    /**
     * build a new Property is Between
     */
    public PropertyIsBetweenType(final ExpressionType expression, final LowerBoundaryType lowerBoundary, final UpperBoundaryType upperBoundary) {
        this.expression    = FACTORY.createExpression(expression);
        this.lowerBoundary = lowerBoundary;
        this.upperBoundary = upperBoundary;
    }

    /**
     * build a new Property is Between
     */
    public PropertyIsBetweenType(final JAXBElement<?> expression, final LowerBoundaryType lowerBoundary, final UpperBoundaryType upperBoundary) {
        this.expression    = expression;
        this.lowerBoundary = lowerBoundary;
        this.upperBoundary = upperBoundary;
    }

    public PropertyIsBetweenType(final PropertyIsBetweenType that) {
        if (that != null) {
            if (that.expression != null) {
                final ObjectFactory factory = new ObjectFactory();
                final Object exp = that.expression.getValue();
                if (exp instanceof PropertyNameType) {
                    this.expression = factory.createPropertyName((PropertyNameType)exp);
                } else if (exp instanceof LiteralType) {
                    final LiteralType lit = new LiteralType((LiteralType)exp);
                    this.expression = factory.createLiteral(lit);
                } else if (exp instanceof FunctionType) {
                    final FunctionType func = new FunctionType((FunctionType)exp);
                    this.expression = factory.createFunction(func);
                } else {
                    throw new IllegalArgumentException("Unexpected type for expression in PropertyIsBetweenType:" + expression.getClass().getName());
                }
            }

            if (that.lowerBoundary != null) {
                this.lowerBoundary = new LowerBoundaryType(that.lowerBoundary);
            }
            if (that.upperBoundary != null) {
                this.upperBoundary = new UpperBoundaryType(that.upperBoundary);
            }
        }
    }

    /**
     * Gets the value of the expression property.
     *
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link BinaryOperatorType }{@code >}
     *     {@link JAXBElement }{@code <}{@link MapItemType }{@code >}
     *     {@link JAXBElement }{@code <}{@link BinaryOperatorType }{@code >}
     *     {@link JAXBElement }{@code <}{@link LiteralType }{@code >}
     *     {@link JAXBElement }{@code <}{@link InterpolateType }{@code >}
     *     {@link JAXBElement }{@code <}{@link ConcatenateType }{@code >}
     *     {@link JAXBElement }{@code <}{@link ChangeCaseType }{@code >}
     *     {@link JAXBElement }{@code <}{@link PropertyNameType }{@code >}
     *     {@link JAXBElement }{@code <}{@link TrimType }{@code >}
     *     {@link JAXBElement }{@code <}{@link BinaryOperatorType }{@code >}
     *     {@link JAXBElement }{@code <}{@link net.opengis.ogc.FunctionType }{@code >}
     *     {@link JAXBElement }{@code <}{@link FormatDateType }{@code >}
     *     {@link JAXBElement }{@code <}{@link CategorizeType }{@code >}
     *     {@link JAXBElement }{@code <}{@link BinaryOperatorType }{@code >}
     *     {@link JAXBElement }{@code <}{@link ExpressionType }{@code >}
     *     {@link JAXBElement }{@code <}{@link InterpolationPointType }{@code >}
     *     {@link JAXBElement }{@code <}{@link StringLengthType }{@code >}
     *     {@link JAXBElement }{@code <}{@link RecodeType }{@code >}
     *     {@link JAXBElement }{@code <}{@link net.opengis.se.FunctionType }{@code >}
     *     {@link JAXBElement }{@code <}{@link FormatNumberType }{@code >}
     *     {@link JAXBElement }{@code <}{@link SubstringType }{@code >}
     *     {@link JAXBElement }{@code <}{@link StringPositionType }{@code >}
     */
    public JAXBElement<?> getExpressionType() {
        return expression;
    }

    /**
     * Sets the value of the expression property.
     *
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link BinaryOperatorType }{@code >}
     *     {@link JAXBElement }{@code <}{@link MapItemType }{@code >}
     *     {@link JAXBElement }{@code <}{@link BinaryOperatorType }{@code >}
     *     {@link JAXBElement }{@code <}{@link LiteralType }{@code >}
     *     {@link JAXBElement }{@code <}{@link InterpolateType }{@code >}
     *     {@link JAXBElement }{@code <}{@link ConcatenateType }{@code >}
     *     {@link JAXBElement }{@code <}{@link ChangeCaseType }{@code >}
     *     {@link JAXBElement }{@code <}{@link PropertyNameType }{@code >}
     *     {@link JAXBElement }{@code <}{@link TrimType }{@code >}
     *     {@link JAXBElement }{@code <}{@link BinaryOperatorType }{@code >}
     *     {@link JAXBElement }{@code <}{@link net.opengis.ogc.FunctionType }{@code >}
     *     {@link JAXBElement }{@code <}{@link FormatDateType }{@code >}
     *     {@link JAXBElement }{@code <}{@link CategorizeType }{@code >}
     *     {@link JAXBElement }{@code <}{@link BinaryOperatorType }{@code >}
     *     {@link JAXBElement }{@code <}{@link ExpressionType }{@code >}
     *     {@link JAXBElement }{@code <}{@link InterpolationPointType }{@code >}
     *     {@link JAXBElement }{@code <}{@link StringLengthType }{@code >}
     *     {@link JAXBElement }{@code <}{@link RecodeType }{@code >}
     *     {@link JAXBElement }{@code <}{@link net.opengis.se.FunctionType }{@code >}
     *     {@link JAXBElement }{@code <}{@link FormatNumberType }{@code >}
     *     {@link JAXBElement }{@code <}{@link SubstringType }{@code >}
     *     {@link JAXBElement }{@code <}{@link StringPositionType }{@code >}
     */
    public void setExpression(final JAXBElement<?> value) {
        this.expression = value;
    }

    @Override
    public List getExpressions() {
        return Arrays.asList(getExpression(), getLowerBoundary(), getUpperBoundary());
    }

    /**
     * Gets the value of the expression property.
     */
    @Override
    public Expression getExpression() {
        final Object value = expression.getValue();
        if (value instanceof ExpressionType) {
            return (ExpressionType)value;
        } if (value instanceof PropertyNameType) {
            return (PropertyNameType)value;
        }
        return null;
    }

    /**
     * Gets the value of the literal property.
     */
    public LiteralType getLiteral() {
        final Object value = expression.getValue();
        if (value instanceof LiteralType) {
            return (LiteralType)value;
        }
        return null;
    }

    /**
     * Gets the value of the propertyName property.
     */
    public String getPropertyName() {
        final Object value = expression.getValue();
        if (value instanceof String) {
            return (String)value;
        } else if (value instanceof PropertyNameType) {
            return ((PropertyNameType)value).getXPath();
        }
        return null;
    }

    /**
     * Gets the value of the lowerBoundary property.
     */
    @Override
    public LowerBoundaryType getLowerBoundary() {
        return lowerBoundary;
    }

    /**
     * Gets the value of the upperBoundary property.
     */
    @Override
    public UpperBoundaryType getUpperBoundary() {
        return upperBoundary;
    }

    @Override
    public boolean test(final Object object) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(super.toString());
        if (expression != null) {
            sb.append("expression:\nname=").append(expression.getName());
            sb.append("value=").append(expression.getValue()).append('\n');
        }
        if (lowerBoundary != null) {
            sb.append("lower boundary:").append(lowerBoundary).append('\n');
        }
        if (upperBoundary != null) {
            sb.append("upper boundary:").append(upperBoundary).append('\n');
        }
        return sb.toString();
    }

    @Override
    public ComparisonOpsType getClone() {
        return new PropertyIsBetweenType(this);
    }
}
