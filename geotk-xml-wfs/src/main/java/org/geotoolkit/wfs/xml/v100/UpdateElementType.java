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
import org.geotoolkit.wfs.xml.UpdateElement;


/**
 * <p>Java class for UpdateElementType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="UpdateElementType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/wfs}Property" maxOccurs="unbounded"/>
 *         &lt;element ref="{http://www.opengis.net/ogc}Filter" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="handle" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="typeName" use="required" type="{http://www.w3.org/2001/XMLSchema}QName" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UpdateElementType", propOrder = {
    "property",
    "filter"
})
public class UpdateElementType implements UpdateElement {

    @XmlElement(name = "Property", required = true)
    private List<PropertyType> property;
    @XmlElement(name = "Filter", namespace = "http://www.opengis.net/ogc")
    private FilterType filter;
    @XmlAttribute
    private String handle;
    @XmlAttribute(required = true)
    private QName typeName;

    public UpdateElementType() {

    }

    public UpdateElementType(final List<PropertyType> property, final FilterType filter, final QName typeName) {
        this.property = property;
        this.filter   = filter;
        this.typeName = typeName;
    }

    /**
     * Gets the value of the property property.
     *
     */
    public List<PropertyType> getProperty() {
        if (property == null) {
            property = new ArrayList<PropertyType>();
        }
        return this.property;
    }

    /**
     *
     *                   The Filter element is used to constrain the scope
     *                   of the update operation to those features identified
     *                   by the filter.  Feature instances can be specified
     *                   explicitly and individually using the identifier of
     *                   each feature instance OR a set of features to be
     *                   operated on can be identified by specifying spatial
     *                   and non-spatial constraints in the filter.
     *                   If no filter is specified, then the update operation
     *                   applies to all feature instances.
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

    public String getInputFormat() {
        return null; // not implementd in 1.0.0
    }

    public String getSrsName() {
        return null; // not implementd in 1.0.0
    }

}
