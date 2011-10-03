/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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
package org.geotoolkit.wfs.xml.v100;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;
import org.geotoolkit.ogc.xml.v100.FilterType;
import org.geotoolkit.ogc.xml.v100.PropertyNameType;


/**
 * 
 *             The Query element is of type QueryType.
 *          
 * 
 * <p>Java class for QueryType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="QueryType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/ogc}PropertyName" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/ogc}Filter" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="handle" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="typeName" use="required" type="{http://www.w3.org/2001/XMLSchema}QName" />
 *       &lt;attribute name="featureVersion" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "QueryType", propOrder = {
    "propertyName",
    "filter"
})
public class QueryType {

    @XmlElement(name = "PropertyName", namespace = "http://www.opengis.net/ogc")
    private List<PropertyNameType> propertyName;
    @XmlElement(name = "Filter", namespace = "http://www.opengis.net/ogc")
    private FilterType filter;
    @XmlAttribute
    private String handle;
    @XmlAttribute(required = true)
    private QName typeName;
    @XmlAttribute
    private String featureVersion;

    /**
     * 
     *                 The PropertyName element is used to specify one or more
     *                 properties of a feature whose values are to be retrieved
     *                 by a Web Feature Service.  
     *                 
     *                 While a Web Feature Service should endeavour to satisfy
     *                 the exact request specified, in some instance this may
     *                 not be possible.  Specifically, a Web Feature Service
     *                 must generate a valid GML2 response to a Query operation.
     *                 The schema used to generate the output may include 
     *                 properties that are mandatory.  In order that the output
     *                 validates, these mandatory properties must be specified
     *                 in the request.  If they are not, a Web Feature Service
     *                 may add them automatically to the Query before processing
     *                 it.  Thus a client application should, in general, be
     *                 prepared to receive more properties than it requested.
     * 
     *                 Of course, using the DescribeFeatureType request, a client
     *                 application can determine which properties are mandatory
     *                 and request them in the first place.
     *              Gets the value of the propertyName property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the propertyName property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPropertyName().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PropertyNameType }
     * 
     * 
     */
    public List<PropertyNameType> getPropertyName() {
        if (propertyName == null) {
            propertyName = new ArrayList<PropertyNameType>();
        }
        return this.propertyName;
    }

    /**
     * 
     *                 The Filter element is used to define spatial and/or non-spatial
     *                 constraints on query.  Spatial constrains use GML2 to specify
     *                 the constraining geometry.  A full description of the Filter
     *                 element can be found in the Filter Encoding Implementation
     *                 Specification.
     *              
     * 
     * @return
     *     possible object is
     *     {@link FilterType }
     *     
     */
    public FilterType getFilter() {
        return filter;
    }

    /**
     * Sets the value of the filter property.
     * 
     * @param value
     *     allowed object is
     *     {@link FilterType }
     *     
     */
    public void setFilter(FilterType value) {
        this.filter = value;
    }

    /**
     * Gets the value of the handle property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHandle() {
        return handle;
    }

    /**
     * Sets the value of the handle property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHandle(String value) {
        this.handle = value;
    }

    /**
     * Gets the value of the typeName property.
     * 
     * @return
     *     possible object is
     *     {@link QName }
     *     
     */
    public QName getTypeName() {
        return typeName;
    }

    /**
     * Sets the value of the typeName property.
     * 
     * @param value
     *     allowed object is
     *     {@link QName }
     *     
     */
    public void setTypeName(QName value) {
        this.typeName = value;
    }

    /**
     * Gets the value of the featureVersion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFeatureVersion() {
        return featureVersion;
    }

    /**
     * Sets the value of the featureVersion property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFeatureVersion(String value) {
        this.featureVersion = value;
    }

}
