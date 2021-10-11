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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.wms.xml.AbstractOnlineResource;

/**
 * <p>Java class for anonymous complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attGroup ref="{http://www.w3.org/1999/xlink}simpleLink"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 * @author Guilhem Legal
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "OnlineResource")
public class OnlineResource implements AbstractOnlineResource {

    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    private String type;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    @XmlSchemaType(name = "anyURI")
    private String href;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    @XmlSchemaType(name = "anyURI")
    private String role;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    @XmlSchemaType(name = "anyURI")
    private String arcrole;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    private String title;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    private String show;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    private String actuate;

    /**
     * An empty constructor used by JAXB.
     */
    OnlineResource() {
    }

    public OnlineResource(final AbstractOnlineResource that) {
        if (that != null) {
            this.href    = that.getHref();
            this.type    = that.getType();
            this.actuate = that.getActuate();
            this.arcrole = that.getArcrole();
            this.role    = that.getRole();
            this.show    = that.getShow();
            this.title   = that.getTitle();
        }
    }

    /**
     * Build an online resource with only the href attribute (most of the case.
     *
     * @param href The url of the resource.
     */
    public OnlineResource(final String href) {
        this.href = href;
    }

    /**
     * Build a full OnlineResource object.
     */
    public OnlineResource(final String type, final String href, final String role,
            final String arcrole, final String title, final String show, final String actuate) {
        this.actuate = actuate;
        this.arcrole = arcrole;
        this.href = href;
        this.role = role;
        this.show = show;
        this.title = title;
        this.type = type;
    }

    /**
     * Gets the value of the type property.
     *
     */
    @Override
    public String getType() {
        if (type == null) {
            return "simple";
        } else {
            return type;
        }
    }

    /**
     * Gets the value of the href property.
     *
     */
    @Override
    public String getHref() {
        return href;
    }

    /**
     * Gets the value of the href property.
     *
     */
    @Override
    public void setHref(final String href) {
        this.href = href;
    }


    /**
     * Gets the value of the role property.
     *
     */
    @Override
    public String getRole() {
        return role;
    }

    /**
     * Gets the value of the arcrole property.
     *
     */
    @Override
    public String getArcrole() {
        return arcrole;
    }

    /**
     * Gets the value of the title property.
     */
    @Override
    public String getTitle() {
        return title;
    }

    /**
     * Gets the value of the show property.
     */
    @Override
    public String getShow() {
        return show;
    }

    /**
     * Gets the value of the actuate property.
     */
    @Override
    public String getActuate() {
        return actuate;
    }
}
