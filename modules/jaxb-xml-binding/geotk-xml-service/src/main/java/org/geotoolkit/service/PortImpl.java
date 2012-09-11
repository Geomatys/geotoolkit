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

import java.util.ArrayList;
import java.util.Collection;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.opengis.service.Interface;
import org.opengis.service.Port;


/**
 * <p>Java class for SV_Port_Type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SV_Port_Type">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.isotc211.org/2005/gco}AbstractObject_Type">
 *       &lt;sequence>
 *         &lt;element name="theSV_Interface" type="{http://www.isotc211.org/2005/srv}SV_Interface_PropertyType" maxOccurs="unbounded" minOccurs="0"/>
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
@XmlType(name = "SV_Port_Type", propOrder = {
    "theSVInterface"
})
@XmlRootElement(name="SV_Port")
public class PortImpl implements Port {

    @XmlElement(name = "theSV_Interface")
    private Collection<Interface> theSVInterface;

    /**
     * An empty constructor used by JAXB
     */
    public PortImpl() {
        
    }
    
    /**
     * Clone a Port.
     */
    public PortImpl(final Port port) {
        
        this.theSVInterface = port.getTheSVInterface();
    }
    
    /**
     * Gets the value of the theSVInterface property.
     * 
     */
    public Collection<Interface> getTheSVInterface() {
        if (theSVInterface == null) {
            theSVInterface = new ArrayList<Interface>();
        }
        return this.theSVInterface;
    }
    
    public void setTheSVInterface(final Collection<Interface> theSVInterface) {
         this.theSVInterface = theSVInterface;
    }
    
    public void setTheSVInterface(final Interface theSVInterface) {
        if (this.theSVInterface == null) {
            this.theSVInterface = new ArrayList<Interface>();
        }
        this.theSVInterface.add(theSVInterface);
    }

}
