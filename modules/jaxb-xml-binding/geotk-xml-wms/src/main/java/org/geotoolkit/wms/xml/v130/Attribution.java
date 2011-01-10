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
package org.geotoolkit.wms.xml.v130;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/wms}Title" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/wms}OnlineResource" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/wms}LogoURL" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * @author Guilhem Legal
 * @module pending
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
    public Attribution(final String title, final OnlineResource onlineResource, final LogoURL logoURL) {
        this.title          = title;
        this.onlineResource = onlineResource;
        this.logoURL        = logoURL;
    }

    /**
     * Build a new Attribution.
     */
    public Attribution(final String title, final String href, final LogoURL logoURL) {
        this.title          = title;
        this.onlineResource = new OnlineResource(href);
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
