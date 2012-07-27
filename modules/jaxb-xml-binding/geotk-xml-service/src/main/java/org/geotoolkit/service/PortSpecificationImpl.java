/*
 *    GeotoolKit - An Open Source Java GIS Toolkit
 *    http://geotoolkit.org
 * 
 *    (C) 2009, Geomatys
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

package org.geotoolkit.service;

import org.opengis.service.DCPList;
import java.net.URL;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.opengis.service.PortSpecification;


/**
 * <p>Java class for SV_PortSpecification_Type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SV_PortSpecification_Type">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.isotc211.org/2005/gco}AbstractObject_Type">
 *       &lt;sequence>
 *         &lt;element name="binding" type="{http://www.isotc211.org/2005/srv}DCPList_PropertyType"/>
 *         &lt;element name="address" type="{http://www.isotc211.org/2005/gmd}URL_PropertyType"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SV_PortSpecification_Type", propOrder = {
    "binding",
    "address"
})
@XmlRootElement(name="SV_PortSpecification")
public class PortSpecificationImpl implements PortSpecification {

    @XmlElement(required = true)
    private DCPList binding;
    @XmlElement(required = true)
    private URL address;

    public PortSpecificationImpl() {
        
    }
    
    public PortSpecificationImpl(final DCPList binding, final URL address) {
        this.address = address;
        this.binding = binding;
    }
    
    /**
     * Gets the value of the binding property.
     * 
     */
    public DCPList getBinding() {
        return binding;
    }

    /**
     * Sets the value of the binding property.
     * 
     *     
     */
    public void setBinding(final DCPList value) {
        this.binding = value;
    }

    /**
     * Gets the value of the address property.
     * 
     */
    public URL getAddress() {
        return address;
    }

    /**
     * Sets the value of the address property.
     * 
     */
    public void setAddress(final URL value) {
        this.address = value;
    }

}
