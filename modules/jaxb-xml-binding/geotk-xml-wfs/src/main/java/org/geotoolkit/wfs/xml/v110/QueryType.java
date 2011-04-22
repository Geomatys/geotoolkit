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

import org.geotoolkit.util.Utilities;
import org.geotoolkit.ogc.xml.v110.FilterType;
import org.geotoolkit.ogc.xml.v110.FunctionType;
import org.geotoolkit.ogc.xml.v110.SortByType;


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
 * @module pending
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

    public QueryType(final FilterType filter, final List<QName> typeName, final String featureVersion) {
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
    public void setFilter(final FilterType value) {
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
    public void setSortBy(final SortByType value) {
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
    public void setHandle(final String value) {
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
    public void setFeatureVersion(final String value) {
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
    public void setSrsName(final String value) {
        this.srsName = value;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("[QueryType]\n");
        if (featureVersion != null) {
            sb.append("featureVersion").append(featureVersion).append('\n');
        }
        if (filter != null) {
            sb.append("filter").append(filter).append('\n');
        }
        if (handle != null) {
            sb.append("handle").append(handle).append('\n');
        }
        if (sortBy != null) {
            sb.append("sortBy").append(sortBy).append('\n');
        }
        if (srsName != null) {
            sb.append("srsName").append(srsName).append('\n');
        }
        if (typeName != null) {
            sb.append("typeName:\n");
            for (QName q : typeName) {
                sb.append(q).append('\n');
            }
        }
        if (propertyNameOrXlinkPropertyNameOrFunction != null) {
            sb.append("propertyNameOrXlinkPropertyNameOrFunction:\n");
            for (Object q : propertyNameOrXlinkPropertyNameOrFunction) {
                sb.append(q).append('\n');
            }
        }
        return sb.toString();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + (this.propertyNameOrXlinkPropertyNameOrFunction != null ? this.propertyNameOrXlinkPropertyNameOrFunction.hashCode() : 0);
        hash = 29 * hash + (this.filter != null ? this.filter.hashCode() : 0);
        hash = 29 * hash + (this.sortBy != null ? this.sortBy.hashCode() : 0);
        hash = 29 * hash + (this.handle != null ? this.handle.hashCode() : 0);
        hash = 29 * hash + (this.typeName != null ? this.typeName.hashCode() : 0);
        hash = 29 * hash + (this.featureVersion != null ? this.featureVersion.hashCode() : 0);
        hash = 29 * hash + (this.srsName != null ? this.srsName.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        
        if (obj instanceof QueryType) {
            QueryType that = (QueryType) obj;
            return Utilities.equals(this.featureVersion, that.featureVersion) &&
                   Utilities.equals(this.filter, that.filter) &&
                   Utilities.equals(this.handle, that.handle) &&
                   Utilities.equals(this.propertyNameOrXlinkPropertyNameOrFunction, that.propertyNameOrXlinkPropertyNameOrFunction) &&
                   Utilities.equals(this.sortBy, that.sortBy) &&
                   Utilities.equals(this.srsName, that.srsName) &&
                   Utilities.equals(this.typeName, that.typeName);
        }
        return false;
    }
}
