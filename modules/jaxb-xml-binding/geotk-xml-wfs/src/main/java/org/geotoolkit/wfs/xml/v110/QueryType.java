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

package org.geotoolkit.wfs.xml.v110;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;
import org.geotoolkit.ogc.xml.FilterType;
import org.geotoolkit.ogc.xml.FunctionType;
import org.geotoolkit.ogc.xml.SortByType;


/**
 * The Query element is of type QueryType.
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
 *         &lt;choice maxOccurs="unbounded" minOccurs="0">
 *           &lt;element ref="{http://www.opengis.net/wfs}PropertyName"/>
 *           &lt;element ref="{http://www.opengis.net/wfs}XlinkPropertyName"/>
 *           &lt;element ref="{http://www.opengis.net/ogc}Function"/>
 *         &lt;/choice>
 *         &lt;element ref="{http://www.opengis.net/ogc}Filter" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/ogc}SortBy" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="handle" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="typeName" use="required" type="{http://www.opengis.net/wfs}TypeNameListType" />
 *       &lt;attribute name="featureVersion" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="srsName" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "QueryType", propOrder = {
    "propertyNameOrXlinkPropertyNameOrFunction",
    "filter",
    "sortBy"
})
public class QueryType {

    @XmlElements({
        @XmlElement(name = "PropertyName", type = String.class),
        @XmlElement(name = "XlinkPropertyName", type = XlinkPropertyName.class),
        @XmlElement(name = "Function", namespace = "http://www.opengis.net/ogc", type = FunctionType.class)
    })
    private List<Object> propertyNameOrXlinkPropertyNameOrFunction;
    @XmlElement(name = "Filter", namespace = "http://www.opengis.net/ogc")
    private FilterType filter;
    @XmlElement(name = "SortBy", namespace = "http://www.opengis.net/ogc")
    private SortByType sortBy;
    @XmlAttribute
    private String handle;
    @XmlAttribute(required = true)
    private List<QName> typeName;
    @XmlAttribute
    private String featureVersion;
    @XmlAttribute
    @XmlSchemaType(name = "anyURI")
    private String srsName;

    public QueryType() {

    }

    public QueryType(FilterType filter, List<QName> typeName, String featureVersion) {
        this.featureVersion = featureVersion;
        this.filter         = filter;
        this.typeName       = typeName;
    }
    
    /**
     * Gets the value of the propertyNameOrXlinkPropertyNameOrFunction property.
     */
    public List<Object> getPropertyNameOrXlinkPropertyNameOrFunction() {
        if (propertyNameOrXlinkPropertyNameOrFunction == null) {
            propertyNameOrXlinkPropertyNameOrFunction = new ArrayList<Object>();
        }
        return this.propertyNameOrXlinkPropertyNameOrFunction;
    }

    /**
     * The Filter element is used to define spatial and/or non-spatial
     * constraints on query.  
     * Spatial constrains use GML3 to specify the constraining geometry.
     * A full description of the Filter element can be found in the Filter Encoding Implementation
     * Specification.
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
     * The Filter element is used to define spatial and/or non-spatial constraints on query.
     * Spatial constrains use GML3 to specify the constraining geometry.
     * A full description of the Filter element can be found in the Filter Encoding Implementation
     * Specification.
     *              
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
     * The SortBy element is used specify property names whose values should be used to order 
     * (upon presentation) the set of feature instances that satisfy the query.
     *              
     * 
     * @return
     *     possible object is
     *     {@link SortByType }
     *     
     */
    public SortByType getSortBy() {
        return sortBy;
    }

    /**
     * The SortBy element is used specify property names whose values should be used to order
     * (upon presentation) the set of feature instances that satisfy the query.
     *              
     * 
     * @param value
     *     allowed object is
     *     {@link SortByType }
     *     
     */
    public void setSortBy(SortByType value) {
        this.sortBy = value;
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
     */
    public List<QName> getTypeName() {
        if (typeName == null) {
            typeName = new ArrayList<QName>();
        }
        return this.typeName;
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

    /**
     * Gets the value of the srsName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSrsName() {
        return srsName;
    }

    /**
     * Sets the value of the srsName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSrsName(String value) {
        this.srsName = value;
    }

}
