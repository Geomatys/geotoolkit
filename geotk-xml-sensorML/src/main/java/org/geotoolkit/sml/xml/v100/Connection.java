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

import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.geotoolkit.sml.xml.AbstractConnection;


/**
 * <p>Java class for anonymous complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element ref="{http://www.opengis.net/sensorML/1.0}Link"/>
 *         &lt;element ref="{http://www.opengis.net/sensorML/1.0}ArrayLink"/>
 *       &lt;/choice>
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}token" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "link",
    "arrayLink"
})
@XmlRootElement(name = "Connection")
public class Connection implements AbstractConnection {

    @XmlElement(name = "Link")
    private Link link;
    @XmlElement(name = "ArrayLink")
    private ArrayLink arrayLink;
    @XmlAttribute
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    private String name;

    public Connection() {

    }

    public Connection(final Link link) {
        this.link = link;
    }

    public Connection(final String name, final Link link) {
        this.name = name;
        this.link = link;
    }

    public Connection(final String name, final ArrayLink arraylink) {
        this.name      = name;
        this.arrayLink = arraylink;
    }

    public Connection(final AbstractConnection connection) {
        if (connection != null) {
            this.name      = connection.getName();
            if (connection.getLink() != null) {
                this.link      = new Link(connection.getLink());
            }
            if (connection.getArrayLink() != null) {
                this.arrayLink = new ArrayLink(connection.getArrayLink());
            }
        }
    }

    /**
     * Gets the value of the link property.
     */
    @Override
    public Link getLink() {
        return link;
    }

    /**
     * Sets the value of the link property.
     */
    public void setLink(final Link value) {
        this.link = value;
    }

    /**
     * Gets the value of the arrayLink property.
     */
    @Override
    public ArrayLink getArrayLink() {
        return arrayLink;
    }

    /**
     * Sets the value of the arrayLink property.
     */
    public void setArrayLink(final ArrayLink value) {
        this.arrayLink = value;
    }

    /**
     * Gets the value of the name property.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     */
    public void setName(final String value) {
        this.name = value;
    }

    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }

        if (object instanceof Connection) {
            final Connection that = (Connection) object;
            return Objects.equals(this.arrayLink, that.arrayLink) &&
                   Objects.equals(this.link, that.link)           &&
                   Objects.equals(this.name, that.name);

        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + (this.link != null ? this.link.hashCode() : 0);
        hash = 97 * hash + (this.arrayLink != null ? this.arrayLink.hashCode() : 0);
        hash = 97 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }




    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[Connection]").append("\n");
        if (name != null) {
            sb.append("name: ").append(name).append('\n');
        }
        if (arrayLink != null) {
            sb.append("arrayLink: ").append(arrayLink).append('\n');
        }
        if (link != null) {
            sb.append("link: ").append(link).append('\n');
        }
        return sb.toString();
     }

}
