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


package org.geotoolkit.wfs.xml.v200;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlMixed;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;
import org.w3c.dom.Element;


/**
 * <p>Java class for QueryExpressionTextType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="QueryExpressionTextType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;any processContents='skip' namespace='##other' maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;any processContents='skip' namespace='http://www.opengis.net/wfs/2.0' maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/choice>
 *       &lt;attribute name="returnFeatureTypes" use="required" type="{http://www.opengis.net/wfs/2.0}ReturnFeatureTypesListType" />
 *       &lt;attribute name="language" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;attribute name="isPrivate" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "QueryExpressionTextType", propOrder = {
    "content"
})
public class QueryExpressionTextType implements org.geotoolkit.wfs.xml.QueryExpressionText {

    @XmlMixed
    @XmlAnyElement
    private List<Object> content;
    @XmlAttribute(required = true)
    private List<QName> returnFeatureTypes;
    @XmlAttribute(required = true)
    @XmlSchemaType(name = "anyURI")
    private String language;
    @XmlAttribute
    private Boolean isPrivate;

    public QueryExpressionTextType() {
        
    }
    
    public QueryExpressionTextType(final String language, final QueryType query, final List<QName> returnFeatureTypes) {
        this.language = language;
        this.returnFeatureTypes = returnFeatureTypes;
        if (query != null) {
            this.content = new ArrayList<Object>();
            this.content.add(query);
        }
    }
    
    /**
     * Gets the value of the content property.
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link Element }
     * {@link String }
     * 
     */
    public List<Object> getContent() {
        if (content == null) {
            content = new ArrayList<Object>();
        }
        return this.content;
    }

    /**
     * Gets the value of the returnFeatureTypes property.
     * Objects of the following type(s) are allowed in the list
     * {@link QName }
     */
    public List<QName> getReturnFeatureTypes() {
        if (returnFeatureTypes == null) {
            returnFeatureTypes = new ArrayList<QName>();
        }
        return this.returnFeatureTypes;
    }

    /**
     * Gets the value of the language property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Sets the value of the language property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLanguage(String value) {
        this.language = value;
    }

    /**
     * Gets the value of the isPrivate property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isIsPrivate() {
        if (isPrivate == null) {
            return false;
        } else {
            return isPrivate;
        }
    }

    /**
     * Sets the value of the isPrivate property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsPrivate(Boolean value) {
        this.isPrivate = value;
    }

}
