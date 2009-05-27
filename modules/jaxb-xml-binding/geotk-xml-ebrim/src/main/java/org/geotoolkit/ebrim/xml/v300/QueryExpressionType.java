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
package org.geotoolkit.ebrim.xml.v300;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlMixed;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for QueryExpressionType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="QueryExpressionType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;any/>
 *       &lt;/sequence>
 *       &lt;attribute name="queryLanguage" use="required" type="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}referenceURI" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "QueryExpressionType", propOrder = {
    "content"
})
public class QueryExpressionType {

    @XmlMixed
    @XmlAnyElement(lax = true)
    private List<Object> content;
    @XmlAttribute(required = true)
    private String queryLanguage;

    /**
     * Gets the value of the content property.
    */
    public List<Object> getContent() {
        if (content == null) {
            content = new ArrayList<Object>();
        }
        return this.content;
    }
    
     /**
     * Sets the value of the content property.
    */
    public void setContent(Object content) {
        if (this.content == null) {
            this.content = new ArrayList<Object>();
        }
        this.content.add(content);
    }
    
    /**
     * Gets the value of the content property.
    */
    public void setContent(List<Object> content) {
        this.content = content;
    }

    /**
     * Gets the value of the queryLanguage property.
     */
    public String getQueryLanguage() {
        return queryLanguage;
    }

    /**
     * Sets the value of the queryLanguage property.
     */
    public void setQueryLanguage(String value) {
        this.queryLanguage = value;
    }

}
