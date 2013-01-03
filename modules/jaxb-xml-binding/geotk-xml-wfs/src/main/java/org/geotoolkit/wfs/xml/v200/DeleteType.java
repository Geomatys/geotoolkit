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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;
import org.geotoolkit.ogc.xml.v200.FilterType;
import org.geotoolkit.util.Utilities;
import org.geotoolkit.wfs.xml.DeleteElement;


/**
 * <p>Java class for DeleteType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DeleteType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/wfs/2.0}AbstractTransactionActionType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/fes/2.0}Filter"/>
 *       &lt;/sequence>
 *       &lt;attribute name="typeName" use="required" type="{http://www.w3.org/2001/XMLSchema}QName" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DeleteType", propOrder = {
    "filter"
})
public class DeleteType extends AbstractTransactionActionType implements DeleteElement {

    @XmlElement(name = "Filter", namespace = "http://www.opengis.net/fes/2.0", required = true)
    private FilterType filter;
    @XmlAttribute(required = true)
    private QName typeName;

    public DeleteType() {

    }

    public DeleteType(final FilterType filter, final String handle, final QName typeName) {
        super(handle);
        this.filter   = filter;
        this.typeName = typeName;
    }
    
    /**
     * Gets the value of the filter property.
     * 
     * @return
     *     possible object is
     *     {@link FilterType }
     *     
     */
    @Override
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
     * Gets the value of the typeName property.
     * 
     * @return
     *     possible object is
     *     {@link QName }
     *     
     */
    @Override
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

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("[DeleteType]\n");
        if (filter != null) {
            sb.append("filter").append(filter).append('\n');
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
        
        if (obj instanceof DeleteType) {
            DeleteType that = (DeleteType) obj;
            return Utilities.equals(this.filter, that.filter) &&
                   Utilities.equals(this.typeName, that.typeName);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 13 * hash + (this.filter != null ? this.filter.hashCode() : 0);
        hash = 13 * hash + (this.typeName != null ? this.typeName.hashCode() : 0);
        return hash;
    }
}
