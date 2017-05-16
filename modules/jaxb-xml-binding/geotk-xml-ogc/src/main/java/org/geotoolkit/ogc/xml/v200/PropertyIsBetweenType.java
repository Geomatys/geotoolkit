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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;
import org.opengis.filter.FilterVisitor;
import org.opengis.filter.PropertyIsBetween;
import org.opengis.filter.expression.Expression;


/**
 * <p>Java class for PropertyIsBetweenType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="PropertyIsBetweenType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/fes/2.0}ComparisonOpsType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/fes/2.0}expression"/>
 *         &lt;element name="LowerBoundary" type="{http://www.opengis.net/fes/2.0}LowerBoundaryType"/>
 *         &lt;element name="UpperBoundary" type="{http://www.opengis.net/fes/2.0}UpperBoundaryType"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PropertyIsBetweenType", propOrder = {
    "expression",
    "lowerBoundary",
    "upperBoundary"
})
public class PropertyIsBetweenType extends ComparisonOpsType  implements PropertyIsBetween {

    @XmlElementRef(name = "expression", namespace = "http://www.opengis.net/fes/2.0", type = JAXBElement.class)
    private JAXBElement<?> expression;
    @XmlElement(name = "LowerBoundary", required = true)
    private LowerBoundaryType lowerBoundary;
    @XmlElement(name = "UpperBoundary", required = true)
    private UpperBoundaryType upperBoundary;

    public PropertyIsBetweenType() {

    }

    public PropertyIsBetweenType(final PropertyIsBetweenType that) {
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
     *     {@link JAXBElement }{@code <}{@link LiteralType }{@code >}
     *     {@link JAXBElement }{@code <}{@link Object }{@code >}
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     {@link JAXBElement }{@code <}{@link FunctionType }{@code >}
     *
     */
    public JAXBElement<?> getExpressionType() {
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

    /**
     * Gets the value of the lowerBoundary property.
     *
     * @return
     *     possible object is
     *     {@link LowerBoundaryType }
     *
     */
    @Override
    public LowerBoundaryType getLowerBoundary() {
        return lowerBoundary;
    }

    /**
     * Sets the value of the lowerBoundary property.
     *
     * @param value
     *     allowed object is
     *     {@link LowerBoundaryType }
     *
     */
    public void setLowerBoundary(LowerBoundaryType value) {
        this.lowerBoundary = value;
    }

    /**
     * Gets the value of the upperBoundary property.
     *
     * @return
     *     possible object is
     *     {@link UpperBoundaryType }
     *
     */
    @Override
    public UpperBoundaryType getUpperBoundary() {
        return upperBoundary;
    }

    /**
     * Sets the value of the upperBoundary property.
     *
     * @param value
     *     allowed object is
     *     {@link UpperBoundaryType }
     *
     */
    public void setUpperBoundary(UpperBoundaryType value) {
        this.upperBoundary = value;
    }

    /**
     * Gets the value of the expression property.
     */
    @Override
    public Expression getExpression() {
        final Object value = expression.getValue();
        if (value instanceof LiteralType) {
            return (LiteralType)value;
        } else if (value != null) {
            return new LiteralType(value);
        }
        return null;
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
    public ComparisonOpsType getClone() {
        return new PropertyIsBetweenType(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof PropertyIsBetweenType) {
            final PropertyIsBetweenType that = (PropertyIsBetweenType) obj;
            boolean expEquals;
            if (this.expression != null && that.expression != null) {
                expEquals = Objects.equals(this.expression.getValue(), that.expression.getValue());
            } else {
                expEquals = Objects.equals(this.expression, that.expression);
            }
            return expEquals &&
                   Objects.equals(this.lowerBoundary, that.lowerBoundary) &&
                   Objects.equals(this.upperBoundary, that.upperBoundary);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 23 * hash + Objects.hashCode(this.expression);
        hash = 23 * hash + Objects.hashCode(this.lowerBoundary);
        hash = 23 * hash + Objects.hashCode(this.upperBoundary);
        return hash;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("[PropertyIsBetweenType]\n");
        if (expression != null) {
            sb.append("expression:\nQname:").append(expression.getName()).append('\n');
            sb.append(expression.getValue());
        }
        if (lowerBoundary != null) {
            sb.append("lower:").append(lowerBoundary).append('\n');
        }
        if (upperBoundary != null) {
            sb.append("upper:").append(upperBoundary).append('\n');
        }
        return sb.toString();
    }
}
