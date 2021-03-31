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

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;
import org.opengis.filter.ComparisonOperatorName;
import org.opengis.filter.NilOperator;


/**
 * <p>Java class for PropertyIsNilType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="PropertyIsNilType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/fes/2.0}ComparisonOpsType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/fes/2.0}expression" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="nilReason" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PropertyIsNilType", propOrder = {
    "expression"
})
public class PropertyIsNilType extends ComparisonOpsType implements NilOperator {

    @XmlElementRef(name = "expression", namespace = "http://www.opengis.net/fes/2.0", type = JAXBElement.class)
    private JAXBElement<?> expression;
    @XmlAttribute
    private String nilReason;

    public PropertyIsNilType() {
    }

    public PropertyIsNilType(final PropertyIsNilType that) {
        if (that != null) {
            if (that.expression != null) {
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
                    throw new IllegalArgumentException("Unexpected type for expression in PropertyIsNilType:" + expression.getClass().getName());
                }
            }

            this.nilReason = that.nilReason;
        }
    }

    @Override
    public ComparisonOperatorName getOperatorType() {
        return ComparisonOperatorName.valueOf("PROPERTY_IS_NIL");
    }

    public String getPropertyName() {
        if (expression != null && expression.getValue() instanceof String) {
            return (String) expression.getValue();
        }
        return null;
    }

    @Override
    public List getExpressions() {
        return Collections.singletonList(getExpression());
    }

    /**
     * Gets the value of the expression property.
     *
     * @return possible object is      {@link JAXBElement }{@code <}{@link LiteralType }{@code >}
     *     {@link JAXBElement }{@code <}{@link Object }{@code >}
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     {@link JAXBElement }{@code <}{@link FunctionType }{@code >}
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
     */
    public void setExpression(JAXBElement<?> value) {
        this.expression = ((JAXBElement<?> ) value);
    }

    /**
     * Gets the value of the nilReason property.
     */
    @Override
    public Optional<String> getNilReason() {
        return Optional.ofNullable(nilReason);
    }

    /**
     * Sets the value of the nilReason property.
     */
    public void setNilReason(String value) {
        this.nilReason = value;
    }

    @Override
    public ComparisonOpsType getClone() {
        return new PropertyIsNilType(this);
    }

    @Override
    public String toString() {
        final StringBuilder s = new StringBuilder(super.toString());
        s.append("nilReason = ").append(nilReason).append('\n');
        if (expression != null) {
            s.append("expression: ").append(expression.getValue()).append('\n');
        }
        return s.toString();
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + (this.expression != null ? this.expression.hashCode() : 0);
        hash = 67 * hash + (this.nilReason != null ? this.nilReason.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof PropertyIsNilType) {
            final PropertyIsNilType that = (PropertyIsNilType) obj;
            final boolean exp;
            if (this.expression == null && that.expression == null) {
                exp = true;
            } else if (this.expression != null && that.expression != null) {
                exp = Objects.equals(this.expression.getValue(), that.expression.getValue());
            } else {
                return false;
            }
            return exp && Objects.equals(this.nilReason, that.nilReason);
        }
        return false;
    }
}
