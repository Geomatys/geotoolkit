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
import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;
import org.geotoolkit.ogc.xml.v110.FilterType;
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
 *       &lt;attribute name="inputFormat" type="{http://www.w3.org/2001/XMLSchema}string" default="x-application/gml:3" />
 *       &lt;attribute name="srsName" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
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
    @XmlAttribute
    private String inputFormat;
    @XmlAttribute
    @XmlSchemaType(name = "anyURI")
    private String srsName;

    public UpdateElementType() {

    }

    public UpdateElementType(final String inputFormat, final List<PropertyType> property, final FilterType filter, final QName typeName, final String srsName) {
        this.inputFormat = inputFormat;
        this.property = property;
        this.filter   = filter;
        this.typeName = typeName;
        this.srsName  = srsName;
    }

    /**
     * Changing or updating a feature instance means that the current value of one or more properties of
     * the feature are replaced with new values.
     * The Update element contains  one or more Property elements.
     * A Property element contains the name or a feature property who's value is to be changed and the replacement value
     * for that property.
     * Gets the value of the property property.
     */
    public List<PropertyType> getProperty() {
        if (property == null) {
            property = new ArrayList<>();
        }
        return this.property;
    }

    /**
     * The Filter element is used to constrain the scope of the update operation to those features identified
     * by the filter.  Feature instances can be specified explicitly and individually using the identifier of
     * each feature instance OR a set of features to be operated on can be identified by specifying spatial
     * and non-spatial constraints in the filter.
     * If no filter is specified then update operation applies to all feature instances.
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
     * The Filter element is used to constrain the scope of the update operation to those features identified
     * by the filter.  Feature instances can be specified explicitly and individually using the identifier of
     * each feature instance OR a set of features to be operated on can be identified by specifying spatial
     * and non-spatial constraints in the filter.
     * If no filter is specified then update operation applies to all feature instances.
     *
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

    /**
     * Gets the value of the inputFormat property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getInputFormat() {
        return inputFormat;
    }

    /**
     * Sets the value of the inputFormat property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setInputFormat(final String value) {
        this.inputFormat = value;
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
        StringBuilder sb = new StringBuilder("[UpdateElementType]\n");
        if (handle != null) {
            sb.append("handle=").append(handle).append('\n');
        }
        if (filter != null) {
            sb.append("filter=").append(filter).append('\n');
        }
        if (inputFormat != null) {
            sb.append("inputFormat=").append(inputFormat).append('\n');
        }
        if (srsName != null) {
            sb.append("srsName=").append(srsName).append('\n');
        }
        if (typeName != null) {
            sb.append("typeName=").append(typeName).append('\n');
        }
        if (property != null) {
            sb.append("properties:").append('\n');
            for (PropertyType obj : property) {
                sb.append(obj).append('\n');
            }
        }
        return sb.toString();
    }

    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof UpdateElementType) {
            final UpdateElementType that = (UpdateElementType) object;
            return  Objects.equals(this.handle, that.handle) &&
                    Objects.equals(this.filter, that.filter) &&
                    Objects.equals(this.inputFormat, that.inputFormat) &&
                    Objects.equals(this.property, that.property) &&
                    Objects.equals(this.srsName, that.srsName)  &&
                    Objects.equals(this.typeName, that.typeName);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + (this.property != null ? this.property.hashCode() : 0);
        hash = 41 * hash + (this.filter != null ? this.filter.hashCode() : 0);
        hash = 41 * hash + (this.handle != null ? this.handle.hashCode() : 0);
        hash = 41 * hash + (this.typeName != null ? this.typeName.hashCode() : 0);
        hash = 41 * hash + (this.inputFormat != null ? this.inputFormat.hashCode() : 0);
        hash = 41 * hash + (this.srsName != null ? this.srsName.hashCode() : 0);
        return hash;
    }
}
