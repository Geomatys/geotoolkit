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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.namespace.QName;
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
 * @module
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
    public PropertyNameType(final String content) {
        this.content = content;
    }

    /**
     * Build a new propertyName with the specified name.
     */
    public PropertyNameType(final QName content) {
        if (content != null) {
            if (content.getNamespaceURI() != null && !"".equals(content.getNamespaceURI())) {
                this.content = content.getNamespaceURI() + ':' + content.getLocalPart();
            } else {
                this.content = content.getLocalPart();
            }
        }
    }
    
    public PropertyNameType(final PropertyNameType that) {
        if (that != null) {
            this.content = that.content;
        }
    }
    
    /**
     * Gets the value of the content property.
     * 
     */
    public String getContent() {
        return content;
    }

    /**
     * Sets the value of the content property.
     * 
     */
    public void setContent(final String value) {
        this.content = value;
    }

    @Override
    public String getPropertyName() {
        return content;
    }

    @Override
    public Object evaluate(Object o) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public <T> T evaluate(Object o, Class<T> type) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public Object accept(ExpressionVisitor ev, Object o) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

}
