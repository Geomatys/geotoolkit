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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import org.geotoolkit.util.Utilities;
import org.opengis.filter.expression.ExpressionVisitor;
import org.opengis.filter.expression.PropertyName;


/**
 * <p>Java class for PropertyNameType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PropertyNameType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/ogc}ExpressionType">
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PropertyNameType", propOrder = {
    "content"
})
public class PropertyNameType implements PropertyName {

    @XmlValue
    private String content;

    /**
     * An empty constructor used by JAXB
     */
    public PropertyNameType() {
        
    }
    
    /**
     * Build a new propertyName with the specified name.
     */
    public PropertyNameType(String content) {
        this.content = content;
    }
    
    /**
     * Gets the value of the content property.
     */
    public String getContent() {
        return content;
    }

    /**
     * Gets the value of the content property.
     */
    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return content;
    }

    public String getPropertyName() {
        return content;
    }

    public Object evaluate(Object object) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public <T> T evaluate(Object object, Class<T> context) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Object accept(ExpressionVisitor visitor, Object extraData) {
        return visitor.visit(this,extraData);
    }
    
    /**
     * Verify that this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof PropertyNameType) {
            final PropertyNameType that = (PropertyNameType) object;
            
            return  Utilities.equals(this.content, that.content);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + (this.content != null ? this.content.hashCode() : 0);
        return hash;
    }
}
