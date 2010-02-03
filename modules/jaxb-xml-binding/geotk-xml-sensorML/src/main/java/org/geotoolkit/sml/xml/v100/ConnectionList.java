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

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.util.Utilities;

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
 *         &lt;element ref="{http://www.opengis.net/sensorML/1.0}connection" maxOccurs="unbounded"/>
 *       &lt;/sequence>
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
    "connection"
})
public class ConnectionList {

    @XmlElement(required = true)
    private List<Connection> connection;

    public ConnectionList() {

    }

    public ConnectionList(List<Connection> connection) {
        this.connection = connection;
    }

    /**
     * Gets the value of the connection property.
     */
    public List<Connection> getConnection() {
        if (connection == null) {
            connection = new ArrayList<Connection>();
        }
        return this.connection;
    }

    /**
     * Gets the value of the connection property.
     */
    public void setConnection(Connection connection) {
        if (this.connection == null) {
            this.connection = new ArrayList<Connection>();
        }
        this.connection.add(connection);
    }

    /**
     * Gets the value of the connection property.
     */
    public void setConnection(List<Connection> connection) {
        this.connection = connection;
    }

    /**
     * Gets the value of the connection property.
     */
    public void setConnection(Link connection) {
        if (this.connection == null) {
            this.connection = new ArrayList<Connection>();
        }
        this.connection.add(new Connection(connection));
    }

    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }

        if (object instanceof ConnectionList) {
            final ConnectionList that = (ConnectionList) object;
            return Utilities.equals(this.connection, that.connection)  ;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 11 * hash + (this.connection != null ? this.connection.hashCode() : 0);
        return hash;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[ConnectionList]").append("\n");
        if (connection != null) {
            sb.append("connection: ").append('\n');
            for (Connection c : connection) {
                sb.append(c).append('\n');
            }
        }
        return sb.toString();
     }
}
