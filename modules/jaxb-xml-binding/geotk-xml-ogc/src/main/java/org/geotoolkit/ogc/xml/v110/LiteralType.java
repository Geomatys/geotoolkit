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
import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlMixed;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.ogc.xml.XMLLiteral;
import org.opengis.filter.expression.ExpressionVisitor;


/**
 * <p>Java class for LiteralType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="LiteralType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/ogc}ExpressionType">
 *       &lt;sequence>
 *         &lt;any/>
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
@XmlType(name = "LiteralType", propOrder = {
    "content"
})
public class LiteralType implements XMLLiteral {

    @XmlMixed
    @XmlAnyElement(lax = true)
    private List<Object> content;

    /**
     * an empty constructor used by JAXB
     */
    public LiteralType() {
        
    }
    
    /**
     * build a new Literal with the specified list of object
     */
    public LiteralType(final List<Object> content) {
        this.content = content;
    }
    
     /**
     * build a new Literal with the specified Object.
     */
    public LiteralType(final Object content) {
        this.content = new ArrayList<>(); 
        this.content.add(content);
    }
    
    public LiteralType(final LiteralType that) {
        if (that != null && that.content != null) {
            this.content = new ArrayList<>(that.content); 
        }
    }
    
    /**
     * build a new Literal with the specified String
     */
    public LiteralType(final String content) {
        this.content = new ArrayList<>(); 
        this.content.add(content);
    }
    
    /**
     * Gets the value of the content property.
     * (unmodifiable)
     */
    @Override
    public List<Object> getContent() {
        if (content == null) {
            content = new ArrayList<>();
        }
        return Collections.unmodifiableList(content);
    }
    
    /**
     * Sets the value of the content property.
     */
    @Override
    public void setContent(final Object content) {
        if (content != null) {
            if (this.content == null) {
                this.content = new ArrayList<>();
            }
            this.content.add(content);
        }
    }
    
    /**
     * Sets the value of the content property.
     */
    @Override
    public void setContent(final List<Object> content) {
        this.content = content;
    }
    
    /**
     * The more often we just want to get a single String value.
     * This method return the first object of the list and cast it in String (if its possible).
     */
    public String getStringValue() {
        if (content != null && !content.isEmpty()) {
            if (content.get(0) instanceof String) {
                return (String)content.get(0);
            }
        }
        return null;
    }

    /**
     * We assume that the list have only One Value.
     */
    @Override
    public Object getValue() {
        if (content != null && !content.isEmpty()) {
            return content.get(0);
        }
        return null;
    }

    @Override
    public Object evaluate(final Object object) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object evaluate(final Object object, final Class context) {
       Object literal = null;
       if (content != null && !content.isEmpty()) {
            literal = content.get(0);
       } 
       
       if (literal == null || (context != null && context.isAssignableFrom(literal.getClass()))) {
            return context.cast( literal );
       } else {
            return null;
       }
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        for (Object obj: content) {
            s.append(obj.toString()).append(" ");
        }
        return s.toString();
    }
    
    /**
     * Verify that this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof LiteralType) {
            final LiteralType that = (LiteralType) object;


            return Objects.equals(this.content,   that.content);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 31 * hash + (this.content != null ? this.content.hashCode() : 0);
        return hash;
    }

    /**
     * Used by FilterVisitors to perform some action on this filter instance.
     * Typicaly used by Filter decoders, but may also be used by any thing
     * which needs infomration from filter structure. Implementations should
     * always call: visitor.visit(this); It is importatant that this is not
     * left to a parent class unless the parents API is identical.
     *
     * @param visitor The visitor which requires access to this filter, the
     *        method must call visitor.visit(this);
     */
    @Override
    public Object accept(final ExpressionVisitor visitor, final Object extraData) {
    	return visitor.visit(this,extraData);
    }

}
