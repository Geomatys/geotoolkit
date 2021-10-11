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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.geotoolkit.wms.xml.AbstractMetadataURL;
import org.geotoolkit.wms.xml.AbstractURL;


/**
 * <p>Java class for anonymous complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * @author Guilhem Legal
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "format",
    "onlineResource"
})
@XmlRootElement(name = "MetadataURL")
public class MetadataURL implements AbstractMetadataURL {

    @XmlElement(name = "Format", required = true)
    private String format;
    @XmlElement(name = "OnlineResource", required = true)
    private OnlineResource onlineResource;
    @XmlAttribute(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NMTOKEN")
    private String type;

    /**
     * An empty constructor used by JAXB.
     */
     MetadataURL() {
     }

    /**
     * Build a new MetadataURL object.
     */
    public MetadataURL(final String format, final OnlineResource onlineResource, final String type) {
        this.format         = format;
        this.onlineResource = onlineResource;
        this.type           = type;
    }

    /**
     * Build a new MetadataURL object.
     */
    public MetadataURL(final String format, final String href, final String type) {
        this.format         = format;
        this.onlineResource = new OnlineResource(href);
        this.type           = type;
    }

    /**
     * Gets the value of the format property.
     *
     */
    @Override
    public String getFormat() {
        return format;
    }

    /**
     * Gets the value of the onlineResource property.
     *
     */
    @Override
    public OnlineResource getOnlineResource() {
        return onlineResource;
    }

    /**
     * Gets the value of the type property.
     *
     */
    @Override
    public String getType() {
        return type;
    }
}
