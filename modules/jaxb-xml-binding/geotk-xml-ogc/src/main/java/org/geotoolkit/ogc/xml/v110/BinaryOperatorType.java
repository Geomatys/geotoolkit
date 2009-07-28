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
import java.util.Collections;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;
import org.opengis.filter.expression.ExpressionVisitor;


/**
 * <p>Java class for BinaryOperatorType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BinaryOperatorType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/ogc}ExpressionType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/ogc}expression" maxOccurs="2" minOccurs="2"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BinaryOperatorType", propOrder = {
    "expression"
})
public class BinaryOperatorType extends ExpressionType {

    @XmlElementRef(name = "expression", namespace = "http://www.opengis.net/ogc", type = JAXBElement.class)
    private List<JAXBElement<?>> expression;

    /**
     * Gets the value of the expression property.
     * (unmodifiable)
     */
    public List<JAXBElement<?>> getExpression() {
        if (expression == null) {
            expression = new ArrayList<JAXBElement<?>>();
        }
        return Collections.unmodifiableList(expression);
    }

    public Object evaluate(Object object) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public <T> T evaluate(Object object, Class<T> context) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Object accept(ExpressionVisitor visitor, Object extraData) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
