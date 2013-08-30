/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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

package org.geotoolkit.ows.xml.v200;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.ows.xml.AbstractDomain;


/**
 * Valid domain (or allowed set of values) of one quantity,
 *       with its name or identifier.
 * 
 * <p>Java class for DomainType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DomainType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/ows/2.0}UnNamedDomainType">
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DomainType")
public class DomainType extends UnNamedDomainType implements AbstractDomain {

    @XmlAttribute(required = true)
    private String name;

    /**
     * Empty constructor used by JAXB.
     */
    public DomainType(){
    }
    
    public DomainType(final DomainType that){
        super(that);
        if (that != null) {
            this.name = that.name;
        }
    }

    /**
     * Build a new Domain with the specified list of values.
     */
    public DomainType(final String name, final List<String> value) {
        super(value);
        this.name  = name;
    }

    public DomainType(final String name, final NoValues noValues, final ValueType defaultValue){
        super(noValues, defaultValue);
        this.name = name;
    }
    
    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    @Override
    public List<String> getValue() {
        if (this.getAllowedValues() != null) {
            return this.getAllowedValues().getStringValues();
        }
        return null;
    }
    
    @Override
    public void setValue(final List<String> values) {
        if (values != null) {
            this.setAllowedValues(new AllowedValues(values));
        }
    }
}
