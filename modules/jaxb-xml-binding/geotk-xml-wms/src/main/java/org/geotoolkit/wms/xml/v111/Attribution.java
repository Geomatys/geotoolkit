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


/**
 * <p>Java class for anonymous complex type.
 * 
 * 
 * @author Guilhem Legal
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "title",
    "onlineResource",
    "logoURL"
})
@XmlRootElement(name = "Attribution")
public class Attribution {

    @XmlElement(name = "Title")
    private String title;
    @XmlElement(name = "OnlineResource")
    private OnlineResource onlineResource;
    @XmlElement(name = "LogoURL")
    private LogoURL logoURL;

    /**
     * An empty constructor used by JAXB.
     */
    Attribution() {
    }
    
    /**
     * Build a new Attribution.
     */
    public Attribution(final String title, OnlineResource onlineResource, LogoURL logoURL) {
        this.title          = title;
        this.onlineResource = onlineResource;
        this.logoURL        = logoURL;
    }
    
    /**
     * Gets the value of the title property.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Gets the value of the onlineResource property.
    */
    public OnlineResource getOnlineResource() {
        return onlineResource;
    }

    /**
     * Gets the value of the logoURL property.
     */
    public LogoURL getLogoURL() {
        return logoURL;
    }
}
