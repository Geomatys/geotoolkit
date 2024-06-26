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

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 *
 *
 *  @author Guilhem Legal
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "format",
    "onlineResource"
})
@XmlRootElement(name = "FeatureListURL")
public class FeatureListURL {

    @XmlElement(name = "Format", required = true)
    private String format;
    @XmlElement(name = "OnlineResource", required = true)
    private OnlineResource onlineResource;

    /**
     * An empty constructor used by JAXB.
     */
     FeatureListURL() {
     }

    /**
     * Build a new Contact person primary object.
     */
    public FeatureListURL(final String format, final OnlineResource onlineResource) {
        this.format         = format;
        this.onlineResource = onlineResource;
    }

    /**
     * Gets the value of the format property.
     */
    public String getFormat() {
        return format;
    }

    /**
     * Gets the value of the onlineResource property.
     *
     */
    public OnlineResource getOnlineResource() {
        return onlineResource;
    }
}
