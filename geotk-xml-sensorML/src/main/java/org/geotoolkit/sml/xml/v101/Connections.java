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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.sml.xml.AbstractConnections;


/**
 * <p>Java class for anonymous complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence minOccurs="0">
 *         &lt;element name="ConnectionList">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://www.opengis.net/sensorML/1.0.1}connection" maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
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
    "connectionList"
})
@XmlRootElement(name = "Connections")
public class Connections extends SensorObject implements AbstractConnections {

    @XmlElement(name = "ConnectionList")
    private ConnectionList connectionList;

    public Connections() {

    }

    public Connections(final ConnectionList connectionList) {
        this.connectionList = connectionList;
    }

    public Connections(final AbstractConnections connections) {
        if (connections != null) {
            this.connectionList = new ConnectionList(connections.getConnectionList());
        }
    }

    /**
     * Gets the value of the connectionList property.
     *
     * @return possible object is {@link Connections.ConnectionList }
     */
    @Override
    public ConnectionList getConnectionList() {
        return connectionList;
    }

    /**
     * Sets the value of the connectionList property.
     *
     * @param value allowed object is {@link Connections.ConnectionList }
     */
    public void setConnectionList(final ConnectionList value) {
        this.connectionList = value;
    }

}
