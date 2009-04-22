/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2007 - 2009, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 3 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.ogc.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.opengis.filter.FilterVisitor;
import org.opengis.filter.expression.Expression;


/**
 * <p>Java class for BinaryComparisonOpType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BinaryComparisonOpType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/ogc}ComparisonOpsType">
 *       &lt;sequence>
 *         &lt;choice maxOccurs="unbounded" minOccurs="0">
 *           &lt;element ref="{http://www.opengis.net/ogc}expression"/>
 *           &lt;element ref="{http://www.opengis.net/ogc}Literal"/>
 *           &lt;element ref="{http://www.opengis.net/ogc}PropertyName"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *       &lt;attribute name="matchCase" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * @author Guilhem Legal
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BinaryComparisonOpType", propOrder = {
    "expression",
    "literal",
    "propertyName"
})
public class BinaryComparisonOpType extends ComparisonOpsType {

    @XmlElement(name = "Literal", type = LiteralType.class)        
    private LiteralType literal;
    
    @XmlElement(name = "expression", type = ExpressionType.class)
    private ExpressionType expression;
    
    @XmlElement(name = "PropertyName", type = String.class)
    private String propertyName;
            
    @XmlAttribute
    private Boolean matchCase;

    /**
     * Empty constructor used by JAXB
     */
    public BinaryComparisonOpType() {
        
    }
    
    /**
     * Build a new Binary comparison operator
     */
    public BinaryComparisonOpType(LiteralType literal, PropertyNameType propertyName, Boolean matchCase) {
        this.literal = literal;
        if (propertyName != null)
            this.propertyName = propertyName.getPropertyName();
        this.matchCase = matchCase;
    }
    
    

    /**
     * Gets the value of the matchCase property.
     */
    public Boolean getMatchCase() {
        return matchCase;
    }
    
    /**
     * Gets the value of the matchCase property.
     */
    public boolean isMatchingCase() {
        if (matchCase == null)
            return false;
        return matchCase;
    }
    
    /**
     * sets the value of the matchCase property.
     */
    public void setMatchCase(Boolean matchCase) {
        this.matchCase = matchCase;
    }
    
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(super.toString());
        s.append("MatchCase ? ").append(matchCase).append('\n');
        s.append("propertyName: ").append(propertyName).append('\n');
        if (literal != null)
            s.append("literal: ").append(literal.toString()).append('\n');
        
        if (expression != null) 
            s.append("expression: ").append(expression.toString()).append('\n');

        return s.toString();
    }

    public boolean evaluate(Object object) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Object accept(FilterVisitor visitor, Object extraData) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Expression getExpression1() {
        return new PropertyNameType(propertyName);
    }

    public Expression getExpression2() {
        return literal;
    }

    public LiteralType getLiteral() {
        return literal;
    }

    public void setLiteral(LiteralType literal) {
        this.literal = literal;
    }

    public ExpressionType getExpression() {
        return expression;
    }

    public void setExpression(ExpressionType expression) {
        this.expression = expression;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }
}
