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
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlElementRefs;
import jakarta.xml.bind.annotation.XmlTransient;
import jakarta.xml.bind.annotation.XmlType;
import org.apache.sis.util.iso.Names;
import org.geotoolkit.filter.capability.Functions;
import org.opengis.filter.Expression;
import org.opengis.util.ScopedName;


/**
 * <p>Java class for FunctionType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="FunctionType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/ogc}ExpressionType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/ogc}expression" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FunctionType", propOrder = {
    "expression"
})
public class FunctionType extends ExpressionType {

    @XmlElementRefs({
        @XmlElementRef(name = "Add", namespace = "http://www.opengis.net/ogc", type = JAXBElement.class),
        @XmlElementRef(name = "PropertyName", namespace = "http://www.opengis.net/ogc", type = JAXBElement.class),
        @XmlElementRef(name = "Function", namespace = "http://www.opengis.net/ogc", type = JAXBElement.class),
        @XmlElementRef(name = "Sub", namespace = "http://www.opengis.net/ogc", type = JAXBElement.class),
        @XmlElementRef(name = "expression", namespace = "http://www.opengis.net/ogc", type = JAXBElement.class),
        @XmlElementRef(name = "Literal", namespace = "http://www.opengis.net/ogc", type = JAXBElement.class),
        @XmlElementRef(name = "Div", namespace = "http://www.opengis.net/ogc", type = JAXBElement.class),
        @XmlElementRef(name = "Mul", namespace = "http://www.opengis.net/ogc", type = JAXBElement.class)
    })
    private List<JAXBElement<?>> expression;

    @XmlAttribute(required = true)
    private String name;

    /**
     * A transient factory to rebuild the expressions
     */
    @XmlTransient
    ObjectFactory factory = new ObjectFactory();

    /**
     * An empty constructor used by JAXB
     */
     public FunctionType() {
     }

    /**
     * Build a new Function  TODO
     */
     public FunctionType(final String name, final Expression... expression) {
         this.expression = new ArrayList<>();
         StringBuilder report = new StringBuilder();
         for (Expression e:expression)  {
             report.append(e.getClass().getSimpleName());
         }
         throw new UnsupportedOperationException("TODO Not supported yet real type of arg1:" + report.toString());
     }

     /**
     * Build a new Function  TODO
     */
     public FunctionType(final Functions functions) {
         this.expression = new ArrayList<>();
         throw new UnsupportedOperationException("Operation Not supported yet");
     }

     public FunctionType(final FunctionType that) {
        if (that != null) {
            this.name = that.name;
            if (that.expression != null) {
                this.expression = new ArrayList<>();
                for (JAXBElement<?> jb : that.expression) {
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
                        throw new IllegalArgumentException("Unexpected type for expression in FunctionType:" + expression.getClass().getName());
                    }
                }
            }
        }
    }

    @Override
    public ScopedName getFunctionName() {
        return Names.createScopedName(Names.createLocalName(null, null, "Geotk"), null, name);
    }

    /**
     * Gets the value of the expression property.
     */
    public List<JAXBElement<?>> getExpression() {
        if (expression == null) {
            expression = new ArrayList<>();
        }
        return expression;
    }

    /**
     * Gets the value of the name property.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     */
    public void setName(final String value) {
        this.name = value;
    }
}
