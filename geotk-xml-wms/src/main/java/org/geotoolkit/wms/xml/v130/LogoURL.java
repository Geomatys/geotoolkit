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

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import org.geotoolkit.wms.xml.AbstractLogoURL;


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
 *         &lt;element ref="{http://www.opengis.net/wms}Format"/>
 *         &lt;element ref="{http://www.opengis.net/wms}OnlineResource"/>
 *       &lt;/sequence>
 *       &lt;attribute name="width" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" />
 *       &lt;attribute name="height" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
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
