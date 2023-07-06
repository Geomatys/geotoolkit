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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElementRef;
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
 *     &lt;extension base="{http://www.opengis.net/fes/2.0}ComparisonOpsType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/fes/2.0}expression" maxOccurs="2" minOccurs="2"/>
 *       &lt;/sequence>
 *       &lt;attribute name="wildCard" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="singleChar" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="escapeChar" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PropertyIsLikeType", propOrder = {
    "expression"
})
public class PropertyIsLikeType extends ComparisonOpsType implements LikeOperator {

    @XmlElementRef(name = "expression", namespace = "http://www.opengis.net/fes/2.0", type = JAXBElement.class)
    private List<JAXBElement<?>> expression;
    @XmlAttribute(required = true)
    private String wildCard;
    @XmlAttribute(required = true)
    private String singleChar;
    @XmlAttribute(required = true)
    private String escapeChar;
    @XmlAttribute(required = false)
    private boolean matchCase = true;

    /**
     * An empty constructor used by JAXB.
     */
    public PropertyIsLikeType() {
    }

    /**
     *Build a new Property is like operator
     */
    public PropertyIsLikeType(final String expr, final String pattern, final String wildcard, final String singleChar, final String escape) {
        this(expr, pattern, wildcard, singleChar, escape, true);
    }

    public PropertyIsLikeType(final String expr, final String pattern, final String wildcard, final String singleChar, final String escape, final boolean matchCase) {
        this.escapeChar = escape;
        this.expression = new ArrayList<>();
        if (expr != null) {
            final ObjectFactory factory = new ObjectFactory();
            this.expression.add(factory.createValueReference(expr));
        }
        this.singleChar   = singleChar;
        this.wildCard     = wildcard;
        if (pattern != null) {
            final ObjectFactory factory = new ObjectFactory();
            this.expression.add(factory.createLiteral(new LiteralType(pattern)));
        }
        this.matchCase    = matchCase;
    }

    public PropertyIsLikeType(final PropertyIsLikeType that) {
        if (that != null) {
            if (that.expression != null) {
                this.expression = new ArrayList<>();
                final ObjectFactory factory = new ObjectFactory();
                for (JAXBElement jb : that.expression) {
                    final Object exp = jb.getValue();
                    if (exp instanceof String) {
                        this.expression.add(factory.createValueReference((String)exp));
                    } else if (exp instanceof LiteralType) {
                        final LiteralType lit = new LiteralType((LiteralType)exp);
                        this.expression.add(factory.createLiteral(lit));
                    } else if (exp instanceof FunctionType) {
                        final FunctionType func = new FunctionType((FunctionType)exp);
                        this.expression.add(factory.createFunction(func));
                    } else {
                        throw new IllegalArgumentException("Unexpected type for expression in PropertyIsLikeType:" + expression.getClass().getName());
                    }
                }
            }
            this.escapeChar = that.escapeChar;
            this.singleChar = that.singleChar;
            this.wildCard   = that.wildCard;
            this.matchCase  = that.matchCase;
        }
    }

    public Expression getExpression() {
        String pname = getPropertyName();
        if (pname != null) {
            return new InternalPropertyName(pname);
        }
        return null;
    }

    /**
     * Gets the value of the expression property.
     *
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link LiteralType }{@code >}
     * {@link JAXBElement }{@code <}{@link Object }{@code >}
     * {@link JAXBElement }{@code <}{@link String }{@code >}
     * {@link JAXBElement }{@code <}{@link FunctionType }{@code >}
     */
    public List<JAXBElement<?>> getElements() {
        if (expression == null) {
            expression = new ArrayList<>();
        }
        return this.expression;
    }

    @Override
    public List getExpressions() {
        if (expression == null) return Collections.emptyList();
        final Object[] ex = new Object[expression.size()];
        for (int i=0; i<ex.length; i++) {
            ex[i] = expression.get(i).getValue();
            if (ex[i] instanceof String) {
                ex[i] = new InternalPropertyName((String) ex[i]);
            }
        }
        return Arrays.asList(ex);
    }

    public String getPropertyName() {
        if (expression != null) {
            for (JAXBElement<?> elem : expression) {
                if (elem.getValue() instanceof String) {
                    return (String) elem.getValue();
                }
            }
        }
        return null;
    }

    /**
     * Gets the value of the literal property.
     */
    public String getLiteral() {
        if (expression != null) {
            for (JAXBElement<?> elem : expression) {
                if (elem.getValue() instanceof LiteralType) {
                    LiteralType lit = (LiteralType) elem.getValue();
                    return lit.getStringValue();
                }
            }
        }
        return null;
    }

    public void setLiteral(final String literal) {
        if (literal != null) {
            final ObjectFactory factory = new ObjectFactory();
            this.expression.add(factory.createLiteral(new LiteralType(literal)));
        }
    }

    /**
     * Gets the value of the literal property.
     */
    public LiteralType getLiteralType() {
        if (expression != null) {
            for (JAXBElement<?> elem : expression) {
                if (elem.getValue() instanceof LiteralType) {
                    return (LiteralType) elem.getValue();
                }
            }
        }
        return null;
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
    public void setWildCard(String value) {
        this.wildCard = value;
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
    public void setSingleChar(String value) {
        this.singleChar = value;
    }

    /**
     * Gets the value of the escapeChar property.
     */
    @Override
    public char getEscapeChar() {
        return escapeChar.charAt(0);
    }

    /**
     * Sets the value of the escapeChar property.
     */
    public void setEscape(String value) {
        this.escapeChar = value;
    }

    @Override
    public boolean isMatchingCase() {
        return matchCase;
    }

    @Override
    public ComparisonOpsType getClone() {
        return new PropertyIsLikeType(this);
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

            boolean exp;
            if (this.expression == null && that.expression == null) {
                exp = true;
            } else if (this.expression != null && that.expression != null) {
                if (this.expression.size() == that.expression.size()) {
                    exp = true;
                    for (int i = 0; i< this.expression.size(); i++) {
                        if (!Objects.equals(this.expression.get(i).getValue(), that.expression.get(i).getValue())) {
                            return false;
                        }
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }
            return exp &&
                   Objects.equals(this.escapeChar,   that.escapeChar)   &&
                   Objects.equals(this.singleChar,   that.singleChar)   &&
                   Objects.equals(this.wildCard,     that.wildCard)     &&
                   (this.matchCase == that.matchCase);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + (this.expression != null ? this.expression.hashCode() : 0);
        hash = 29 * hash + (this.escapeChar != null ? this.escapeChar.hashCode() : 0);
        hash = 29 * hash + (this.singleChar != null ? this.singleChar.hashCode() : 0);
        hash = 29 * hash + (this.wildCard != null ? this.wildCard.hashCode() : 0);
        hash = 29 * hash + (this.matchCase ? 1 : 0);
        return hash;
    }

    @Override
    public String toString() {
        final StringBuilder s = new StringBuilder(super.toString());
        if (expression != null) {
            s.append("expression(").append(expression.size()).append("):\n");
            for (JAXBElement jb : expression) {
                s.append(jb.getValue()).append("\n");
            }
        }
        s.append(" escape=").append(escapeChar);
        s.append(" single=").append(singleChar).append(" wildCard=").append(wildCard);
        s.append(" matchCase=").append(matchCase);
        return s.toString();
    }
}
