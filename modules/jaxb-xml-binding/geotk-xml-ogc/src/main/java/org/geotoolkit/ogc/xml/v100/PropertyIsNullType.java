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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.opengis.filter.FilterVisitor;
import org.opengis.filter.PropertyIsNull;
import org.opengis.filter.expression.Expression;


/**
 * <p>Java class for PropertyIsNullType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="PropertyIsNullType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/ogc}ComparisonOpsType">
 *       &lt;choice>
 *         &lt;element ref="{http://www.opengis.net/ogc}PropertyName"/>
 *         &lt;element ref="{http://www.opengis.net/ogc}Literal"/>
 *       &lt;/choice>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PropertyIsNullType", propOrder = {
    "propertyName",
    "literal"
})
public class PropertyIsNullType extends ComparisonOpsType implements PropertyIsNull {

    @XmlElement(name = "PropertyName")
    private PropertyNameType propertyName;
    @XmlElement(name = "Literal")
    private LiteralType literal;

    public PropertyIsNullType() {

    }

    public PropertyIsNullType(PropertyNameType propertyName) {
        this.propertyName = propertyName;
    }

    public PropertyIsNullType(final PropertyIsNullType that) {
        if (that != null) {
            if (that.literal != null) {
                this.literal = new LiteralType(that.literal);
            }
            if (that.propertyName != null) {
                this.propertyName = new PropertyNameType(that.propertyName);
            }
        }
    }

    /**
     * Gets the value of the propertyName property.
     *
     */
    public PropertyNameType getPropertyName() {
        return propertyName;
    }

    /**
     * Sets the value of the propertyName property.
     *
     */
    public void setPropertyName(final PropertyNameType value) {
        this.propertyName = value;
    }

    /**
     * Gets the value of the literal property.
     *
     */
    public LiteralType getLiteral() {
        return literal;
    }

    /**
     * Sets the value of the literal property.
     *
     */
    public void setLiteral(final LiteralType value) {
        this.literal = value;
    }

    @Override
    public ComparisonOpsType getClone() {
        return new PropertyIsNullType(this);
    }

    @Override
    public Expression getExpression() {
        return propertyName;
    }

    @Override
    public boolean evaluate(Object object) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object accept(FilterVisitor visitor, Object extraData) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
