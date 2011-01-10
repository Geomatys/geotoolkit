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
package org.geotoolkit.sml.xml.v100;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.util.Utilities;
import org.geotoolkit.sml.xml.AbstractLink;

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
 *         &lt;element name="source">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="ref" use="required" type="{http://www.opengis.net/sensorML/1.0}linkRef" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="destination">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="ref" use="required" type="{http://www.opengis.net/sensorML/1.0}linkRef" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute name="type" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "source",
    "destination"
})
@XmlRootElement(name = "Link")
public class Link implements AbstractLink {

    @XmlElement(required = true)
    private LinkRef source;
    @XmlElement(required = true)
    private LinkRef destination;
    @XmlAttribute
    private String type;

    public Link() {

    }

    public Link(final String type, final LinkRef source, final LinkRef destination) {
        this.destination = destination;
        this.source      = source;
        this.type        = type;
    }

    public Link(final AbstractLink link) {
        if (link != null) {
            this.type        = link.getType();
            if (link.getSource() != null) {
                this.source      = new LinkRef(link.getSource().getRef());
            }
            if (link.getDestination() != null) {
                this.destination = new LinkRef(link.getDestination().getRef());
            }
        }

    }
    
    /**
     * Gets the value of the source property.
     */
    @Override
    public LinkRef getSource() {
        return source;
    }

    /**
     * Sets the value of the source property.
     */
    public void setSource(final LinkRef value) {
        this.source = value;
    }

    /**
     * Gets the value of the destination property.
     */
    @Override
    public LinkRef getDestination() {
        return destination;
    }

    /**
     * Sets the value of the destination property.
     */
    public void setDestination(final LinkRef value) {
        this.destination = value;
    }

    /**
     * Gets the value of the type property.
     */
    @Override
    public String getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     */
    @Override
    public void setType(final String value) {
        this.type = value;
    }

    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof Link) {
            final Link that = (Link) object;
            return Utilities.equals(this.destination, that.destination) &&
                   Utilities.equals(this.source, that.source)           &&
                   Utilities.equals(this.type, that.type);

        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 71 * hash + (this.source != null ? this.source.hashCode() : 0);
        hash = 71 * hash + (this.destination != null ? this.destination.hashCode() : 0);
        hash = 71 * hash + (this.type != null ? this.type.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[Link]").append("\n");
        if (source != null) {
            sb.append("source: ").append(source).append('\n');
        }
        if (destination != null) {
            sb.append("destination: ").append(destination).append('\n');
        }
        if (type != null) {
            sb.append("type: ").append(type).append('\n');
        }
        return sb.toString();
     }

}
