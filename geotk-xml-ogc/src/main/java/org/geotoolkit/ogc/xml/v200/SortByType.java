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


package org.geotoolkit.ogc.xml.v200;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import org.geotoolkit.ogc.xml.SortBy;


/**
 * <p>Java class for SortByType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="SortByType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="SortProperty" type="{http://www.opengis.net/fes/2.0}SortPropertyType" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SortByType", propOrder = {
    "sortProperty"
})
public class SortByType implements SortBy {

    @XmlElement(name = "SortProperty", required = true)
    private List<SortPropertyType> sortProperty;

    /**
     * An empty constructor used by JAXB
     */
    public SortByType() {
    }

    /**
     * Build a new sort by list.
     */
    public SortByType(final List<SortPropertyType> sortProperty) {
        this.sortProperty = sortProperty;
    }

    public SortByType(final SortByType that) {
        if (that != null && that.sortProperty != null) {
            this.sortProperty = new ArrayList<>();
            for (SortPropertyType sp : that.sortProperty) {
                this.sortProperty.add(new SortPropertyType(sp));
            }
        }
    }

    /**
     * Gets the value of the sortProperty property.
     *
     * {@link SortPropertyType }
     */
    @Override
    public List<SortPropertyType> getSortProperty() {
        if (sortProperty == null) {
            sortProperty = new ArrayList<>();
        }
        return this.sortProperty;
    }
}
