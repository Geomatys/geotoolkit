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
package org.geotoolkit.gml.xml.v311;

import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * Association to a coordinate system axis, either referencing or containing the definition of that axis.
 *
 * <p>Java class for CoordinateSystemAxisRefType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="CoordinateSystemAxisRefType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence minOccurs="0">
 *         &lt;element ref="{http://www.opengis.net/gml}CoordinateSystemAxis"/>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{http://www.opengis.net/gml}AssociationAttributeGroup"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CoordinateSystemAxisRefType", propOrder = {
    "coordinateSystemAxis"
})
public class CoordinateSystemAxisRefType {

    @XmlElement(name = "CoordinateSystemAxis")
    private CoordinateSystemAxisType coordinateSystemAxis;
    @XmlAttribute(namespace = "http://www.opengis.net/gml")
    @XmlSchemaType(name = "anyURI")
    private String remoteSchema;
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
     * Gets the value of the coordinateSystemAxis property.
     *
     */
    public CoordinateSystemAxisType getCoordinateSystemAxis() {
        return coordinateSystemAxis;
    }

    /**
     * Sets the value of the coordinateSystemAxis property.
     *
     */
    public void setCoordinateSystemAxis(final CoordinateSystemAxisType value) {
        this.coordinateSystemAxis = value;
    }

    /**
     * Gets the value of the remoteSchema property.
     *
     */
    public String getRemoteSchema() {
        return remoteSchema;
    }

    /**
     * Sets the value of the remoteSchema property.
     *
     */
    public void setRemoteSchema(final String value) {
        this.remoteSchema = value;
    }

    /**
     * Gets the value of the type property.
     *
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     *
     */
    public void setType(final String value) {
        this.type = value;
    }

    /**
     * Gets the value of the href property.
     *
     */
    public String getHref() {
        return href;
    }

    /**
     * Sets the value of the href property.
     *
     */
    public void setHref(final String value) {
        this.href = value;
    }

    /**
     * Gets the value of the role property.
     *
     */
    public String getRole() {
        return role;
    }

    /**
     * Sets the value of the role property.
     *
     */
    public void setRole(final String value) {
        this.role = value;
    }

    /**
     * Gets the value of the arcrole property.
     *
     */
    public String getArcrole() {
        return arcrole;
    }

    /**
     * Sets the value of the arcrole property.
     *
     */
    public void setArcrole(final String value) {
        this.arcrole = value;
    }

    /**
     * Gets the value of the title property.
     *
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the value of the title property.
     *
     */
    public void setTitle(final String value) {
        this.title = value;
    }

    /**
     * Gets the value of the show property.
     *
     */
    public String getShow() {
        return show;
    }

    /**
     * Sets the value of the show property.
     *
     */
    public void setShow(final String value) {
        this.show = value;
    }

    /**
     * Gets the value of the actuate property.
     *
     */
    public String getActuate() {
        return actuate;
    }

    /**
     * Sets the value of the actuate property.
     *
     */
    public void setActuate(final String value) {
        this.actuate = value;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[CoordinateSystemAxisRefType]").append("\n");
        if (coordinateSystemAxis != null) {
            sb.append("CoordinateSystemAxisRefType: ").append(coordinateSystemAxis).append('\n');
        }
        if (remoteSchema != null) {
            sb.append("remoteSchema: ").append(remoteSchema).append('\n');
        }
        if (actuate != null) {
            sb.append("actuate: ").append(actuate).append('\n');
        }
        if (arcrole != null) {
            sb.append("actuate: ").append(arcrole).append('\n');
        }
        if (href != null) {
            sb.append("href: ").append(href).append('\n');
        }
        if (role != null) {
            sb.append("role: ").append(role).append('\n');
        }
        if (show != null) {
            sb.append("show: ").append(show).append('\n');
        }
        if (title != null) {
            sb.append("title: ").append(title).append('\n');
        }
        if (type != null) {
            sb.append("type: ").append(type).append('\n');
        }
        return sb.toString();
    }

    /**
     * Verify if this entry is identical to specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }

        if (object instanceof CoordinateSystemAxisRefType) {
            final CoordinateSystemAxisRefType that = (CoordinateSystemAxisRefType) object;

            return Objects.equals(this.actuate, that.actuate)           &&
                   Objects.equals(this.href, that.href)                 &&
                   Objects.equals(this.coordinateSystemAxis, that.coordinateSystemAxis) &&
                   Objects.equals(this.remoteSchema, that.remoteSchema) &&
                   Objects.equals(this.role, that.role)                 &&
                   Objects.equals(this.show, that.show)                 &&
                   Objects.equals(this.title, that.title)               &&
                   Objects.equals(this.type, that.type)                 &&
                   Objects.equals(this.arcrole, that.arcrole);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + (this.coordinateSystemAxis != null ? this.coordinateSystemAxis.hashCode() : 0);
        hash = 53 * hash + (this.remoteSchema != null ? this.remoteSchema.hashCode() : 0);
        hash = 53 * hash + (this.type != null ? this.type.hashCode() : 0);
        hash = 53 * hash + (this.href != null ? this.href.hashCode() : 0);
        hash = 53 * hash + (this.role != null ? this.role.hashCode() : 0);
        hash = 53 * hash + (this.arcrole != null ? this.arcrole.hashCode() : 0);
        hash = 53 * hash + (this.title != null ? this.title.hashCode() : 0);
        hash = 53 * hash + (this.show != null ? this.show.hashCode() : 0);
        hash = 53 * hash + (this.actuate != null ? this.actuate.hashCode() : 0);
        return hash;
    }

}
