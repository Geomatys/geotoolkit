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
import org.geotoolkit.wms.xml.AbstractLogoURL;


/**
 * <p>Java class for anonymous complex type.
 * @author Guilhem Legal
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "format",
    "onlineResource"
})
@XmlRootElement(name = "LogoURL")
public class LogoURL implements AbstractLogoURL {

    @XmlElement(name = "Format", required = true)
    private String format;
    @XmlElement(name = "OnlineResource", required = true)
    private OnlineResource onlineResource;
    @XmlAttribute
    @XmlSchemaType(name = "positiveInteger")
    private Integer width;
    @XmlAttribute
    @XmlSchemaType(name = "positiveInteger")
    private Integer height;

    /**
     * An empty constructor used by JAXB.
     */
     LogoURL() {
     }

    /**
     * Build a new LogoURL object.
     */
    public LogoURL(final String format, final OnlineResource onlineResource,
            final Integer width, final Integer height ) {
        this.format         = format;
        this.height         = height;
        this.onlineResource = onlineResource;
        this.width          = width;
    }

    /**
     * Build a new LogoURL object.
     */
    public LogoURL(final String format, final String href,
            final Integer width, final Integer height ) {
        this.format         = format;
        this.height         = height;
        this.onlineResource = new OnlineResource(href);
        this.width          = width;
    }

    public LogoURL(final AbstractLogoURL that) {
        if (that != null) {
            this.format         = that.getFormat();
            this.height         = that.getHeight();
            this.width          = that.getWidth();
            if (that.getOnlineResource() != null) {
                this.onlineResource = new OnlineResource(that.getOnlineResource());
            }
        }
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
     */
    @Override
    public OnlineResource getOnlineResource() {
        return onlineResource;
    }

    /**
     * Gets the value of the width property.
     */
    @Override
    public Integer getWidth() {
        return width;
    }

    /**
     * Gets the value of the height property.
     */
    @Override
    public Integer getHeight() {
        return height;
    }
}
