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
package org.geotoolkit.csw.xml.v200;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;
import org.geotoolkit.ogc.xml.v110.SortByType;


/**
 * Specifies a query to execute against instances of one or more object types.
 * The ElementName elements specify the object types to be included in the result set. 
 * The QueryConstraint element contains a query filter expressed in a supported query language. 
 * A sorting criterion that specifies a property to sort on may be included.
 * 
 *  typeNames - a list of object types implicated in the query specification.
 *          
 * 
 * <p>Java class for QueryType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="QueryType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/cat/csw}AbstractQueryType">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;element ref="{http://www.opengis.net/cat/csw}ElementSetName"/>
 *           &lt;element name="ElementName" type="{http://www.w3.org/2001/XMLSchema}anyURI" maxOccurs="unbounded"/>
 *         &lt;/choice>
 *         &lt;element ref="{http://www.opengis.net/cat/csw}Constraint" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="typeNames" use="required" type="{http://www.opengis.net/cat/csw}TypeNameListType" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "QueryType", propOrder = {
    "elementSetName",
    "elementName",
    "constraint"
})
@XmlRootElement(name = "Query")       
public class QueryType extends AbstractQueryType {

    @XmlElement(name = "ElementSetName", defaultValue = "summary")
    private ElementSetNameType elementSetName;
    @XmlElement(name = "ElementName")
    @XmlSchemaType(name = "anyURI")
    private List<QName> elementName;
    @XmlElement(name = "Constraint")
    private QueryConstraintType constraint;
    @XmlAttribute(required = true)
    private List<QName> typeNames;

    /**
     * Empty constructor used by JAXB
     */
    QueryType() {
        
    }
    
    /**
     * Build a new Query
     */
    public QueryType(List<QName> typeNames, ElementSetNameType elementSetName, QueryConstraintType constraint) {
        
        this.typeNames      = typeNames;
        this.elementSetName = elementSetName; 
        this.constraint     = constraint;
    }

    /**
     * 
     * @param typeNames
     * @param elementName
     * @param constraint
     */
    public QueryType(List<QName> typeNames, List<QName> elementName, QueryConstraintType constraint) {
        
        this.typeNames      = typeNames;
        this.elementName    = elementName; 
        this.constraint     = constraint;
    }
    
    /**
     * Gets the value of the elementSetName property.
     * 
     */
    public ElementSetNameType getElementSetName() {
        return elementSetName;
    }

    /**
     * Sets the value of the elementSetName property.
     * 
     */
    public void setElementSetName(ElementSetNameType value) {
        this.elementSetName = value;
    }

    /**
     * Gets the value of the elementName property.
     * 
     */
    public List<QName> getElementName() {
        if (elementName == null) {
            elementName = new ArrayList<QName>();
        }
        return this.elementName;
    }

    /**
     * Gets the value of the constraint property.
     * 
     */
    public QueryConstraintType getConstraint() {
        return constraint;
    }

    /**
     * Sets the value of the constraint property.
     */
    public void setConstraint(QueryConstraintType value) {
        this.constraint = value;
    }

    /**
     * Gets the value of the typeNames property.
     */
    public List<QName> getTypeNames() {
        if (typeNames == null) {
            typeNames = new ArrayList<QName>();
        }
        return this.typeNames;
    }
    
    public void setTypeNames(List<QName> typeNames) {
        this.typeNames = typeNames;
    }
    
    /**
     * Always null in that version of csw.
     * 
     * @return null.
     */
    public SortByType getSortBy() {
        return null;
    }

}
