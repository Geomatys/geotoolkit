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

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.util.logging.Logging;
import org.opengis.filter.expression.PropertyName;
import org.opengis.filter.sort.SortBy;
import org.opengis.filter.sort.SortOrder;


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
 *         &lt;element ref="{http://www.opengis.net/fes/2.0}ValueReference"/>
 *         &lt;element name="SortOrder" type="{http://www.opengis.net/fes/2.0}SortOrderType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SortPropertyType", propOrder = {
    "valueReference",
    "sortOrder"
})
public class SortPropertyType implements SortBy {

    private static final Logger LOGGER = Logging.getLogger(SortByType.class);
    
    @XmlElement(name = "ValueReference", required = true)
    private String valueReference;
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
        this.valueReference = propertyName;
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
        this.valueReference = propertyName;
        this.sortOrder    = sortOrder;
    }
    
    /**
     * Gets the value of the valueReference property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValueReference() {
        return valueReference;
    }

    /**
     * Sets the value of the valueReference property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValueReference(String value) {
        this.valueReference = value;
    }

    /**
     * Gets the value of the propertyName property.
     */
    @Override
    public PropertyName getPropertyName() {
        return new org.geotoolkit.ogc.xml.v110.PropertyNameType(valueReference); // issue here
    }

    /**
     * Gets the value of the sortOrder property.
     */
    @Override
    public SortOrder getSortOrder() {
        if (sortOrder != null && sortOrder.equals(SortOrderType.ASC))
            return SortOrder.ASCENDING;
        else if (sortOrder != null && sortOrder.equals(SortOrderType.DESC))
            return SortOrder.DESCENDING;
        else
            return SortOrder.ASCENDING;
    }

    /**
     * Sets the value of the sortOrder property.
     * 
     * @param value
     *     allowed object is
     *     {@link SortOrderType }
     *     
     */
    public void setSortOrder(SortOrderType value) {
        this.sortOrder = value;
    }

}
