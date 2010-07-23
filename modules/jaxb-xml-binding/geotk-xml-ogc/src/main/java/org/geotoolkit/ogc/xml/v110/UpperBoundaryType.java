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

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.ExpressionVisitor;


/**
 * <p>Java class for UpperBoundaryType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UpperBoundaryType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element ref="{http://www.opengis.net/ogc}expression"/>
 *         &lt;element ref="{http://www.opengis.net/ogc}Literal"/>
 *         &lt;element ref="{http://www.opengis.net/ogc}PropertyName"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UpperBoundaryType", propOrder = {
    "expression"
})
public class UpperBoundaryType implements Expression {

    @XmlElementRef(name = "expression", namespace = "http://www.opengis.net/ogc", type = JAXBElement.class)
    private JAXBElement<?> expression;

    public UpperBoundaryType() {

    }

    public UpperBoundaryType(JAXBElement<?> expression) {
        this.expression = expression;
    }

    public UpperBoundaryType(LiteralType literal) {
        ObjectFactory factory = new ObjectFactory();
        this.expression = factory.createLiteral(literal);
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
     *
     */
    public JAXBElement<?> getExpression() {
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
     *
     */
    public void setExpression(JAXBElement<?> value) {
        this.expression = ((JAXBElement<?> ) value);
    }

    /**
     * Gets the value of the expression property.
     */
    public ExpressionType getExpressionType() {
        final Object value = expression.getValue();
        if (value instanceof ExpressionType) {
            return (ExpressionType)value;
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
        }
        return null;
    }

    public Object evaluate(Object object) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public <T> T evaluate(Object object, Class<T> context) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Object accept(ExpressionVisitor visitor, Object extraData) {
        return extraData;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("[UpperBoundaryType]\n");
        if (expression != null) {
            sb.append("expression:\nQname:").append(expression.getName()).append('\n');
            sb.append(expression.getValue());
        }
        return sb.toString();
    }
}
