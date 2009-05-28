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
package org.geotoolkit.wms.xml.v111;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.wms.xml.AbstractDCP;


/**
 * <p>Java class for anonymous complex type.
 * 
 * 
 * @author Guilhem Legal
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "http"
})
@XmlRootElement(name = "DCPType")
public class DCPType extends AbstractDCP {

    @XmlElement(name = "HTTP", required = true)
    private HTTP http;

    /**
     * An empty constructor used by JAXB.
     */
     DCPType() {
     }

    /**
     * Build a new DCP object.
     */
    public DCPType(final HTTP http) {
        this.http = http;
    }
    
    /**
     * Gets the value of the http property.
     * 
     */
    public HTTP getHTTP() {
        return http;
    }

}
