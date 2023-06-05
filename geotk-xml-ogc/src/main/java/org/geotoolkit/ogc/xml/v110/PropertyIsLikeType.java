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

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
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
 *       &lt;attribute name="escapeChar" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="matchCase" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
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
    private String escapeChar;
    @XmlAttribute
    private Boolean matchCase;
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
        this.escapeChar   = escape;
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
        this.escapeChar   = escape;
        this.propertyName =  new PropertyNameType(expr);
        this.singleChar   = singleChar;
        this.wildCard     = wildcard;
        this.literal      = new LiteralType(pattern);
    }

    /**
     * Build a new Property is like operator
     */
    public PropertyIsLikeType(final Expression expr, final String pattern, final String wildcard, final String singleChar, final String escape, final Boolean matchCase) {
        this.escapeChar   = escape;
        if (expr instanceof PropertyNameType) {
            this.propertyName = (PropertyNameType) expr;
        } else {
            throw new IllegalArgumentException("expr must be of type PropertyNameType.");
        }
        this.singleChar   = singleChar;
        this.wildCard     = wildcard;
        this.literal      = new LiteralType(pattern);
        this.matchCase    = matchCase;
    }

    public PropertyIsLikeType(final PropertyIsLikeType that) {
        if (that != null) {
            this.matchCase  = that.matchCase;
            this.escapeChar = that.escapeChar;
            this.singleChar = that.singleChar;
            this.wildCard   = that.wildCard;
            if (that.literal != null) {
                this.literal = new LiteralType(that.literal);
            }
            if (that.propertyName != null) {
                this.propertyName = new PropertyNameType(that.propertyName);
            }
        }
    }

    @Override
    public List getExpressions() {
        return Arrays.asList(getExpression(), getLiteral());
    }

    public Expression getExpression() {
        return propertyName;
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
    public void setPropertyName(final PropertyNameType propertyName) {
        this.propertyName = propertyName;
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

    public String getEscape() {
        return escapeChar;
    }

    /**
     * Gets the value of the escapeChar property.
     */
    public char getEscapeChar() {
        if (escapeChar == null) {
            escapeChar = "\\";
        }
        return escapeChar.charAt(0);
    }

    /**
     * Sets the value of the escapeChar property.
     */
    public void setEscapeChar(String escapeChar) {
        if (escapeChar == null) {
            escapeChar = "\\";
        }
        this.escapeChar = escapeChar;
    }

    /**
     * Gets the value of the matchCase property.
    */
    @Override
    public boolean isMatchingCase() {
        if (matchCase == null) {
            return true;
        } else {
            return matchCase;
        }
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
    public void setSingleChar(final String singleChar) {
        this.singleChar = singleChar;
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
    public void setWildCard(final String wildCard) {
        this.wildCard = wildCard;
    }

    /**
     * Verify that this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof PropertyIsLikeType) {
            final PropertyIsLikeType that = (PropertyIsLikeType) object;
            return Objects.equals(this.escapeChar,   that.escapeChar)   &&
                   Objects.equals(this.literal,      that.literal)      &&
                   Objects.equals(this.matchCase,    that.matchCase)    &&
                   Objects.equals(this.propertyName, that.propertyName) &&
                   Objects.equals(this.singleChar,   that.singleChar)   &&
                   Objects.equals(this.wildCard,     that.wildCard);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + (this.propertyName != null ? this.propertyName.hashCode() : 0);
        hash = 29 * hash + (this.literal != null ? this.literal.hashCode() : 0);
        hash = 29 * hash + (this.escapeChar != null ? this.escapeChar.hashCode() : 0);
        hash = 29 * hash + (this.matchCase != null ? this.matchCase.hashCode() : 0);
        hash = 29 * hash + (this.singleChar != null ? this.singleChar.hashCode() : 0);
        hash = 29 * hash + (this.wildCard != null ? this.wildCard.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(super.toString());
        if (propertyName != null) {
            s.append("PropertyName= ").append(propertyName.toString()).append('\n');
        } else {
            s.append("PropertyName null").append('\n');
        }
        if (literal != null) {
           s.append("Litteral= ").append(literal.toString()).append('\n');
        } else {
            s.append("Literal null").append('\n');
        }
        s.append("matchCase= ").append(matchCase).append(" escape=").append(escapeChar);
        s.append(" single=").append(singleChar).append(" wildCard=").append(wildCard);
        return s.toString();
    }

    @Override
    public boolean test(final Object object) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ComparisonOpsType getClone() {
        return new PropertyIsLikeType(this);
    }
}
