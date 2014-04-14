/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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


package org.geotoolkit.swe.xml.v200;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.swe.xml.AbstractCategory;


/**
 * <p>Java class for CategoryType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CategoryType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/swe/2.0}AbstractSimpleComponentType">
 *       &lt;sequence>
 *         &lt;element name="codeSpace" type="{http://www.opengis.net/swe/2.0}Reference" minOccurs="0"/>
 *         &lt;element name="constraint" type="{http://www.opengis.net/swe/2.0}AllowedTokensPropertyType" minOccurs="0"/>
 *         &lt;element name="value" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CategoryType", propOrder = {
    "codeSpace",
    "constraint",
    "value"
})
public class CategoryType extends AbstractSimpleComponentType implements AbstractCategory {

    private Reference codeSpace;
    private AllowedTokensPropertyType constraint;
    private String value;

    public CategoryType() {
        
    }
    
    public CategoryType(final String definition, final String value) {
        super(null, definition, null);
        this.value = value;
    }
    
    /**
     * Gets the value of the codeSpace property.
     * 
     * @return
     *     possible object is
     *     {@link Reference }
     *     
     */
    @Override
    public Reference getCodeSpace() {
        return codeSpace;
    }

    /**
     * Sets the value of the codeSpace property.
     * 
     * @param value
     *     allowed object is
     *     {@link Reference }
     *     
     */
    public void setCodeSpace(Reference value) {
        this.codeSpace = value;
    }

    /**
     * Gets the value of the constraint property.
     * 
     * @return
     *     possible object is
     *     {@link AllowedTokensPropertyType }
     *     
     */
    @Override
    public AllowedTokensPropertyType getConstraint() {
        return constraint;
    }

    /**
     * Sets the value of the constraint property.
     * 
     * @param value
     *     allowed object is
     *     {@link AllowedTokensPropertyType }
     *     
     */
    public void setConstraint(AllowedTokensPropertyType value) {
        this.constraint = value;
    }

    /**
     * Gets the value of the value property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Override
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValue(String value) {
        this.value = value;
    }

}
