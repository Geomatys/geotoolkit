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
package org.geotoolkit.sml.xml.v101;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
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
 *         &lt;element ref="{http://www.opengis.net/sensorML/1.0.1}Link"/>
 *         &lt;element ref="{http://www.opengis.net/sensorML/1.0.1}ArrayLink"/>
 *       &lt;/choice>
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}token" />
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
    @XmlSchemaType(name = "token")
    private String name;

    public Connection() {

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

    public Connection(final String name, final Link link) {
        this.name = name;
        this.link = link;
    }

    public Connection(final String name, final ArrayLink arraylink) {
        this.name      = name;
        this.arrayLink = arraylink;
    }
    
    /**
     * Gets the value of the link property.
     * 
     * @return
     *     possible object is
     *     {@link Link }
     *     
     */
    @Override
    public Link getLink() {
        return link;
    }

    /**
     * Sets the value of the link property.
     * 
     * @param value
     *     allowed object is
     *     {@link Link }
     *     
     */
    public void setLink(final Link value) {
        this.link = value;
    }

    /**
     * Gets the value of the arrayLink property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayLink }
     *     
     */
    @Override
    public ArrayLink getArrayLink() {
        return arrayLink;
    }

    /**
     * Sets the value of the arrayLink property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayLink }
     *     
     */
    public void setArrayLink(final ArrayLink value) {
        this.arrayLink = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(final String value) {
        this.name = value;
    }

}
