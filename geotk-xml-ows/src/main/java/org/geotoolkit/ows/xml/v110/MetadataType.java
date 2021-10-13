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
package org.geotoolkit.ows.xml.v110;

import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.ows.xml.AbstractMetadata;


/**
 * This element either references or contains more metadata about the element that includes this element.
 * To reference metadata stored remotely, at least the xlinks:href attribute in xlink:simpleLink shall be included.
 * Either at least one of the attributes in xlink:simpleLink or a substitute for the AbstractMetaData element shall be included, but not both.
 * An Implementation Specification can restrict the contents of this element to always be a reference or always contain metadata.
 * (Informative: This element was adapted from the metaDataProperty element in GML 3.0.)
 *
 * <p>Java class for MetadataType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="MetadataType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/ows/1.1}AbstractMetaData" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{http://www.w3.org/1999/xlink}simpleLink"/>
 *       &lt;attribute name="about" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 * @author Guilhem Legal
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MetadataType", propOrder = {
    "abstractMetaData"
})
public class MetadataType implements AbstractMetadata {

    @XmlElement(name = "AbstractMetaData")
    private Object abstractMetaData;
    @XmlAttribute
    @XmlSchemaType(name = "anyURI")
    private String about;
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

    public MetadataType() {

    }

    public MetadataType(final AbstractMetadata that) {
        if (that != null) {
            this.href    = that.getHref();
            this.type    = that.getType();
            this.actuate = that.getActuate();
            this.arcrole = that.getArcrole();
            this.role    = that.getRole();
            this.show    = that.getShow();
            this.title   = that.getTitle();
            this.about   = that.getAbout();
            // can not clone this attribute
            this.abstractMetaData = that.getAbstractMetaData();
        }
    }

    public MetadataType(final String href) {
        this.href = href;
    }

    /**
     * Gets the value of the abstractMetaData property.
     */
    @Override
    public Object getAbstractMetaData() {
        return abstractMetaData;
    }

    /**
     * Gets the value of the about property.
     */
    @Override
    public String getAbout() {
        return about;
    }

    /**
     * Gets the value of the type property.
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
     */
    @Override
    public String getHref() {
        return href;
    }

    /**
     * Gets the value of the role property.
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
     *
     */
    @Override
    public String getTitle() {
        return title;
    }

   /**
    * Gets the value of the show property.
    *
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

    /**
     * Verify that this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof  MetadataType) {
            final MetadataType that = (MetadataType) object;

            return Objects.equals(this.about,            that.about)            &&
                   Objects.equals(this.abstractMetaData, that.abstractMetaData) &&
                   Objects.equals(this.actuate,          that.actuate)          &&
                   Objects.equals(this.arcrole,          that.arcrole)          &&
                   Objects.equals(this.href,             that.href)             &&
                   Objects.equals(this.role,             that.role)             &&
                   Objects.equals(this.show,             that.show)             &&
                   Objects.equals(this.title,            that.title)            &&
                   Objects.equals(this.type,             that.type);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 83 * hash + (this.abstractMetaData != null ? this.abstractMetaData.hashCode() : 0);
        hash = 83 * hash + (this.about != null ? this.about.hashCode() : 0);
        hash = 83 * hash + (this.actuate != null ? this.actuate.hashCode() : 0);
        hash = 83 * hash + (this.arcrole != null ? this.arcrole.hashCode() : 0);
        hash = 83 * hash + (this.href != null ? this.href.hashCode() : 0);
        hash = 83 * hash + (this.role != null ? this.role.hashCode() : 0);
        hash = 83 * hash + (this.show != null ? this.show.hashCode() : 0);
        hash = 83 * hash + (this.title != null ? this.title.hashCode() : 0);
        hash = 83 * hash + (this.type != null ? this.type.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("clas: MetadataType").append('\n');
        if (abstractMetaData != null)
            s.append("abstractMetadata= ").append(abstractMetaData.toString());
        if (about != null)
            s.append("about=").append(about).append('\n');
        if (actuate != null)
            s.append("actuate=").append(actuate).append('\n');
        if (arcrole != null)
            s.append("arcrole=").append(arcrole).append('\n');
        if (href != null)
            s.append("href=").append(href).append('\n');
        if (role != null)
            s.append("role=").append(role).append('\n');
        if (show != null)
            s.append("show=").append(show).append('\n');
        if (title != null)
            s.append("title=").append(title).append('\n');
        if (type != null)
            s.append("type=").append(type).append('\n');

        return s.toString();
    }
}
