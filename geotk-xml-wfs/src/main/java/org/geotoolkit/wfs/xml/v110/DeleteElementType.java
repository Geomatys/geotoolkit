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

import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

import org.geotoolkit.ogc.xml.v110.FilterType;
import org.geotoolkit.util.Utilities;
import org.geotoolkit.wfs.xml.DeleteElement;

/**
 * <p>Java class for DeleteElementType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="DeleteElementType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/ogc}Filter"/>
 *       &lt;/sequence>
 *       &lt;attribute name="handle" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="typeName" use="required" type="{http://www.w3.org/2001/XMLSchema}QName" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DeleteElementType", propOrder = {
    "filter"
})
public class DeleteElementType implements DeleteElement {

    @XmlElement(name = "Filter", namespace = "http://www.opengis.net/ogc", required = true)
    private FilterType filter;
    @XmlAttribute
    private String handle;
    @XmlAttribute(required = true)
    private QName typeName;

    public DeleteElementType() {

    }

    public DeleteElementType(final FilterType filter, final String handle, final QName typeName) {
        this.filter   = filter;
        this.handle   = handle;
        this.typeName = typeName;
    }

    /**
     * The Filter element is used to constrain the scope of the delete operation to those features identified
     * by the filter.
     * Feature instances can be specified explicitly and individually using the identifier of each feature instance
     * OR a set of features to be operated on can be identified by specifying spatial and non-spatial constraints in the filter.
     * If no filter is specified then an exception should be raised since it is unlikely that a client application
     * intends to delete all feature instances.
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
     * The Filter element is used to constrain the scope of the delete operation to those features identified
     * by the filter.
     * Feature instances can be specified explicitly and individually using the identifier of each feature instance
     * OR a set of features to be operated on can be identified by specifying spatial and non-spatial constraints in the filter.
     * If no filter is specified then an exception should be raised since it is unlikely that a client application
     * intends to delete all feature instances.
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
    public void setTypeName(final QName value) {
        this.typeName = value;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("[DeleteElementType]\n");
        if (filter != null) {
            sb.append("filter").append(filter).append('\n');
        }
        if (handle != null) {
            sb.append("handle").append(handle).append('\n');
        }
        if (typeName != null) {
            sb.append("typeName").append(typeName).append('\n');
        }
        return sb.toString();
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj instanceof DeleteElementType) {
            DeleteElementType that = (DeleteElementType) obj;
            return Objects.equals(this.filter, that.filter) &&
                   Objects.equals(this.typeName, that.typeName) &&
                   Objects.equals(this.handle, that.handle);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 13 * hash + (this.filter != null ? this.filter.hashCode() : 0);
        hash = 13 * hash + (this.handle != null ? this.handle.hashCode() : 0);
        hash = 13 * hash + (this.typeName != null ? this.typeName.hashCode() : 0);
        return hash;
    }
}
