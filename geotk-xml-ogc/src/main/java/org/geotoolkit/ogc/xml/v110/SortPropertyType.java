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
package org.geotoolkit.ogc.xml.v110;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.apache.sis.util.logging.Logging;
import org.opengis.filter.SortProperty;
import org.opengis.filter.SortOrder;


/**
 * <p>Java class for SortPropertyType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="SortPropertyType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/ogc}PropertyName"/>
 *         &lt;element name="SortOrder" type="{http://www.opengis.net/ogc}SortOrderType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SortPropertyType", propOrder = {
    "propertyName",
    "sortOrder"
})
public class SortPropertyType implements SortProperty {

    private static final Logger LOGGER = Logging.getLogger("org.geotoolkit.ogc.xml.v110");

    @XmlElement(name = "PropertyName", required = true)
    private PropertyNameType propertyName;
    @XmlElement(name = "SortOrder")
    private SortOrderType sortOrder;

    /**
     * Empty constructor used by JAXB
     */
    public SortPropertyType(){
    }

    /**
     * build a new SOrt property object.
     */
    public SortPropertyType(final String propertyName, final SortOrder sortOrder) {
        this.propertyName = new PropertyNameType(propertyName);
        if (sortOrder != null && sortOrder.equals(SortOrder.ASCENDING)) {
            this.sortOrder = SortOrderType.ASC;
        } else if (sortOrder != null && sortOrder.equals(SortOrder.DESCENDING)) {
            this.sortOrder = SortOrderType.DESC;
        } else if (sortOrder != null){
            LOGGER.log(Level.WARNING, "unexpected SortOrder:{0}.\nexpecting for ASCENDING or DESCENDING", sortOrder);
        }
    }

    /**
     * build a new SOrt property object.
     */
    public SortPropertyType(final String propertyName, final SortOrderType sortOrder) {
        this.propertyName = new PropertyNameType(propertyName);
        this.sortOrder    = sortOrder;
    }

    public SortPropertyType(final SortPropertyType that){
        if (that != null) {
            if (that.propertyName != null) {
                this.propertyName = new PropertyNameType(that.propertyName);
            }
            this.sortOrder = that.sortOrder;
        }
    }

    /**
     * Gets the value of the propertyName property.
     */
    public PropertyNameType getValueReference() {
        return propertyName;
    }

    /**
     * Gets the value of the sortOrder property.
     */
    @Override
    public SortOrder getSortOrder() {
        if (sortOrder != null && sortOrder.equals(SortOrderType.ASC)) {
            return SortOrder.ASCENDING;
        } else if (sortOrder != null && sortOrder.equals(SortOrderType.DESC)) {
            return SortOrder.DESCENDING;
        } else {
            return SortOrder.ASCENDING;
        }
    }

    /**
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof SortPropertyType) {
            final SortPropertyType that = (SortPropertyType) object;
            return Objects.equals(this.propertyName,  that.propertyName)   &&
                   Objects.equals(this.sortOrder,  that.sortOrder);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 17 * hash + (this.propertyName != null ? this.propertyName.hashCode() : 0);
        hash = 17 * hash + (this.sortOrder != null ? this.sortOrder.hashCode() : 0);
        return hash;
    }

    @Override
    public int compare(Object r1, Object r2) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
