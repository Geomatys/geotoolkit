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

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


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
 *         &lt;element ref="{http://www.opengis.net/ogc}expression"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UpperBoundaryType", propOrder = {
    "expression"
})
public class UpperBoundaryType {

    @XmlElementRef(name = "expression", namespace = "http://www.opengis.net/ogc", type = JAXBElement.class)
    private JAXBElement<?> expression;

    public UpperBoundaryType() {

    }

    public UpperBoundaryType(final UpperBoundaryType that) {
        if (that != null && that.expression != null) {
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
                throw new IllegalArgumentException("Unexpected type for expression in lowerBoundary:" + expression.getClass().getName());
            }
        }
    }

    /**
     * Gets the value of the expression property.
     *
     */
    public JAXBElement<?> getExpression() {
        return expression;
    }

    /**
     * Sets the value of the expression property.
     *
     */
    public void setExpression(final JAXBElement<?> value) {
        this.expression = ((JAXBElement<?> ) value);
    }

}
