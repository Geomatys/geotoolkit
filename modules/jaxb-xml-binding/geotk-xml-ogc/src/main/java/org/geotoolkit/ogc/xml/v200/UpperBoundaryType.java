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

import java.util.Objects;
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
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/fes/2.0}expression"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UpperBoundaryType", propOrder = {
    "expression"
})
public class UpperBoundaryType implements Expression {

    @XmlElementRef(name = "expression", namespace = "http://www.opengis.net/fes/2.0", type = JAXBElement.class)
    private JAXBElement<?> expression;

    public UpperBoundaryType() {

    }

    public UpperBoundaryType(final UpperBoundaryType that) {
        if (that != null && that.expression != null) {
            final ObjectFactory factory = new ObjectFactory();
            final Object exp = that.expression.getValue();
            if (exp instanceof String) {
                this.expression = factory.createValueReference((String)exp);
            } else if (exp instanceof LiteralType) {
                final LiteralType lit = new LiteralType((LiteralType)exp);
                this.expression = factory.createLiteral(lit);
            } else if (exp instanceof FunctionType) {
                final FunctionType func = new FunctionType((FunctionType)exp);
                this.expression = factory.createFunction(func);
            } else {
                throw new IllegalArgumentException("Unexpected type for expression in lowerBoundary:" + expression.getClass().getName());
            }
        }
    }

    /**
     * Gets the value of the expression property.
     *
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link LiteralType }{@code >}
     *     {@link JAXBElement }{@code <}{@link Object }{@code >}
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     {@link JAXBElement }{@code <}{@link FunctionType }{@code >}
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
     *     {@link JAXBElement }{@code <}{@link LiteralType }{@code >}
     *     {@link JAXBElement }{@code <}{@link Object }{@code >}
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     {@link JAXBElement }{@code <}{@link FunctionType }{@code >}
     *
     */
    public void setExpression(JAXBElement<?> value) {
        this.expression = ((JAXBElement<?> ) value);
    }

    @Override
    public Object evaluate(final Object object) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T> T evaluate(final Object object, final Class<T> context) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object accept(final ExpressionVisitor visitor, final Object extraData) {
        return extraData;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof UpperBoundaryType) {
            final UpperBoundaryType that = (UpperBoundaryType) obj;
            if (this.expression != null && that.expression != null) {
                return Objects.equals(this.expression.getValue(), that.expression.getValue());
            }
            return Objects.equals(this.expression, that.expression);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + Objects.hashCode(this.expression);
        return hash;
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
