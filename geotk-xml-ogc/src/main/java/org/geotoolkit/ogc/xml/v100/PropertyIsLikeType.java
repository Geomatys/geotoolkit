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

import java.util.Arrays;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import org.opengis.filter.LikeOperator;
import org.opengis.filter.Expression;


/**
 * <p>Java class for PropertyIsLikeType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="PropertyIsLikeType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/ogc}ComparisonOpsType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/ogc}PropertyName"/>
 *         &lt;element ref="{http://www.opengis.net/ogc}Literal"/>
 *       &lt;/sequence>
 *       &lt;attribute name="escape" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="singleChar" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="wildCard" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PropertyIsLikeType", propOrder = {
    "propertyName",
    "literal"
})
public class PropertyIsLikeType extends ComparisonOpsType implements LikeOperator {

    @XmlElement(name = "PropertyName", required = true)
    private PropertyNameType propertyName;
    @XmlElement(name = "Literal", required = true)
    private LiteralType literal;
    @XmlAttribute(required = true)
    private String escape;
    @XmlAttribute(required = true)
    private String singleChar;
    @XmlAttribute(required = true)
    private String wildCard;

    /**
     * An empty constructor used by JAXB.
     */
    public PropertyIsLikeType() {
    }

    /**
     *Build a new Property is like operator
     */
    public PropertyIsLikeType(final Expression expr, final String pattern, final String wildcard, final String singleChar, final String escape) {
        this.escape   = escape;
        if (expr instanceof PropertyNameType) {
            this.propertyName = (PropertyNameType) expr;
        } else {
            throw new IllegalArgumentException("expr must be of type PropertyNameType.");
        }
        this.singleChar   = singleChar;
        this.wildCard     = wildcard;
        this.literal      = new LiteralType(pattern);
    }

    /**
     *Build a new Property is like operator
     */
    public PropertyIsLikeType(final String expr, final String pattern, final String wildcard, final String singleChar, final String escape) {
        this.escape       = escape;
        this.propertyName =  new PropertyNameType(expr);
        this.singleChar   = singleChar;
        this.wildCard     = wildcard;
        this.literal      = new LiteralType(pattern);
    }

    public PropertyIsLikeType(final PropertyIsLikeType that) {
        if (that != null) {
            this.escape = that.escape;
            if (that.literal != null) {
                this.literal = new LiteralType(that.literal);
            }
            if (that.propertyName != null) {
                this.propertyName = new PropertyNameType(that.propertyName);
            }
            this.singleChar = that.singleChar;
            this.wildCard   = that.wildCard;
        }
    }

    @Override
    public List getExpressions() {
        return Arrays.asList(getExpression(), getLiteral());
    }

    /**
     * Gets the value of the propertyName property.
     */
    public PropertyNameType getPropertyName() {
        return propertyName;
    }

    /**
     * Sets the value of the propertyName property.
     */
    public void setPropertyName(final PropertyNameType value) {
        this.propertyName = value;
    }

    public Expression getExpression() {
        return propertyName;
    }

    /**
     * Gets the value of the literal property.
     */
    public String getLiteral() {
        if (literal != null) {
            return literal.getStringValue();
        }
        return null;
    }

    public void setLiteral(final String literal) {
        this.literal = new LiteralType(literal);
    }

    /**
     * Gets the value of the literal property.
     */
    public LiteralType getLiteralType() {
        return literal;
    }

    /**
     * Sets the value of the literal property.
     */
    public void setLiteral(final LiteralType value) {
        this.literal = value;
    }

    /**
     * Gets the value of the escape property.
     */
    @Override
    public char getEscapeChar() {
        return escape.charAt(0);
    }

    /**
     * Sets the value of the escape property.
     */
    public void setEscape(final String value) {
        this.escape = value;
    }

    /**
     * Gets the value of the singleChar property.
     */
    @Override
    public char getSingleChar() {
        return singleChar.charAt(0);
    }

    /**
     * Sets the value of the singleChar property.
     */
    public void setSingleChar(final String value) {
        this.singleChar = value;
    }

    /**
     * Gets the value of the wildCard property.
     */
    @Override
    public char getWildCard() {
        return wildCard.charAt(0);
    }

    /**
     * Sets the value of the wildCard property.
     */
    public void setWildCard(final String value) {
        this.wildCard = value;
    }

    @Override
    public boolean isMatchingCase() {
        return false;
    }

    @Override
    public ComparisonOpsType getClone() {
        return new PropertyIsLikeType(this);
    }

    @Override
    public boolean test(Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
