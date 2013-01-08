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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


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
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PropertyIsBetweenType", propOrder = {
    "expression",
    "lowerBoundary",
    "upperBoundary"
})
public class PropertyIsBetweenType extends ComparisonOpsType {

    @XmlElementRef(name = "expression", namespace = "http://www.opengis.net/ogc", type = JAXBElement.class)
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

    /**
     * Gets the value of the lowerBoundary property.
     * 
     */
    public LowerBoundaryType getLowerBoundary() {
        return lowerBoundary;
    }

    /**
     * Sets the value of the lowerBoundary property.
     *     
     */
    public void setLowerBoundary(final LowerBoundaryType value) {
        this.lowerBoundary = value;
    }

    /**
     * Gets the value of the upperBoundary property.
     * 
     */
    public UpperBoundaryType getUpperBoundary() {
        return upperBoundary;
    }

    /**
     * Sets the value of the upperBoundary property.
     * 
     */
    public void setUpperBoundary(final UpperBoundaryType value) {
        this.upperBoundary = value;
    }

    @Override
    public ComparisonOpsType getClone() {
        return new PropertyIsBetweenType(this);
    }

}
