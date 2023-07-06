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
package org.geotoolkit.ogc.xml.v200;

import java.util.Objects;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.XmlValue;
import javax.xml.namespace.QName;
import org.opengis.filter.ValueReference;
import org.geotoolkit.ogc.xml.AbstractExpression;


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
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PropertyNameType", propOrder = {
    "content"
})
public class InternalPropertyName extends AbstractExpression implements ValueReference {

    @XmlValue
    private String content;

    /**
     * An empty constructor used by JAXB
     */
    public InternalPropertyName() {
    }

    /**
     * Build a new propertyName with the specified name.
     */
    public InternalPropertyName(final String content) {
        this.content = content;
    }

    /**
     * Build a new propertyName with the specified name.
     */
    public InternalPropertyName(final QName content) {
        if (content != null) {
            if (content.getNamespaceURI() != null && !"".equals(content.getNamespaceURI())) {
                this.content = content.getNamespaceURI() + ':' + content.getLocalPart();
            } else {
                this.content = content.getLocalPart();
            }
        }
    }

    public InternalPropertyName(final InternalPropertyName that) {
        if (that != null && that.content != null) {
            this.content = that.content;
        }
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
    public void setContent(final String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "content: " + content;
    }

    @Override
    public String getXPath() {
        return content;
    }

    /**
     * Verify that this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof InternalPropertyName) {
            final InternalPropertyName that = (InternalPropertyName) object;

            return  Objects.equals(this.content, that.content);
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
