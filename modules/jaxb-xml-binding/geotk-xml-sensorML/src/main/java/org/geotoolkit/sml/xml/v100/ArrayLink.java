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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.util.Utilities;
import org.geotoolkit.sml.xml.AbstractArrayLink;

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
 *         &lt;choice>
 *           &lt;sequence>
 *             &lt;element name="sourceArray">
 *               &lt;complexType>
 *                 &lt;complexContent>
 *                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                     &lt;attribute name="ref" type="{http://www.opengis.net/sensorML/1.0}linkRef" />
 *                   &lt;/restriction>
 *                 &lt;/complexContent>
 *               &lt;/complexType>
 *             &lt;/element>
 *             &lt;element name="destinationIndex" maxOccurs="unbounded" minOccurs="0">
 *               &lt;complexType>
 *                 &lt;complexContent>
 *                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                     &lt;attribute name="ref" type="{http://www.opengis.net/sensorML/1.0}linkRef" />
 *                   &lt;/restriction>
 *                 &lt;/complexContent>
 *               &lt;/complexType>
 *             &lt;/element>
 *           &lt;/sequence>
 *           &lt;sequence>
 *             &lt;element name="destinationArray">
 *               &lt;complexType>
 *                 &lt;complexContent>
 *                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                     &lt;attribute name="ref" type="{http://www.opengis.net/sensorML/1.0}linkRef" />
 *                   &lt;/restriction>
 *                 &lt;/complexContent>
 *               &lt;/complexType>
 *             &lt;/element>
 *             &lt;element name="sourceIndex" minOccurs="0">
 *               &lt;complexType>
 *                 &lt;complexContent>
 *                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                     &lt;attribute name="ref" type="{http://www.opengis.net/sensorML/1.0}linkRef" />
 *                   &lt;/restriction>
 *                 &lt;/complexContent>
 *               &lt;/complexType>
 *             &lt;/element>
 *           &lt;/sequence>
 *         &lt;/choice>
 *         &lt;element ref="{http://www.opengis.net/sensorML/1.0}connection" maxOccurs="unbounded" minOccurs="0"/>
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
    "sourceArray",
    "destinationIndex",
    "destinationArray",
    "sourceIndex",
    "connection"
})
@XmlRootElement(name = "ArrayLink")
public class ArrayLink implements AbstractArrayLink {

    private SourceArray sourceArray;
    private List<DestinationIndex> destinationIndex;
    private DestinationArray destinationArray;
    private SourceIndex sourceIndex;
    private List<Connection> connection;

    /**
     * Gets the value of the sourceArray property.
     */
    public SourceArray getSourceArray() {
        return sourceArray;
    }

    /**
     * Sets the value of the sourceArray property.
     */
    public void setSourceArray(SourceArray value) {
        this.sourceArray = value;
    }

    /**
     * Gets the value of the destinationIndex property.
     */
    public List<DestinationIndex> getDestinationIndex() {
        if (destinationIndex == null) {
            destinationIndex = new ArrayList<DestinationIndex>();
        }
        return this.destinationIndex;
    }

    /**
     * Gets the value of the destinationArray property.
     */
    public DestinationArray getDestinationArray() {
        return destinationArray;
    }

    /**
     * Sets the value of the destinationArray property.
     */
    public void setDestinationArray(DestinationArray value) {
        this.destinationArray = value;
    }

    /**
     * Gets the value of the sourceIndex property.
     */
    public SourceIndex getSourceIndex() {
        return sourceIndex;
    }

    /**
     * Sets the value of the sourceIndex property.
     * 
     */
    public void setSourceIndex(SourceIndex value) {
        this.sourceIndex = value;
    }

    /**
     * Gets the value of the connection property.
     * 
     */
    public List<Connection> getConnection() {
        if (connection == null) {
            connection = new ArrayList<Connection>();
        }
        return this.connection;
    }

    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }

        if (object instanceof ArrayLink) {
            final ArrayLink that = (ArrayLink) object;
            return Utilities.equals(this.connection, that.connection)             &&
                   Utilities.equals(this.destinationArray, that.destinationArray) &&
                   Utilities.equals(this.sourceArray, that.sourceArray)           &&
                   Utilities.equals(this.sourceIndex, that.sourceIndex) &&
                   Utilities.equals(this.destinationIndex, that.destinationIndex);


        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + (this.sourceArray != null ? this.sourceArray.hashCode() : 0);
        hash = 79 * hash + (this.destinationIndex != null ? this.destinationIndex.hashCode() : 0);
        hash = 79 * hash + (this.destinationArray != null ? this.destinationArray.hashCode() : 0);
        hash = 79 * hash + (this.sourceIndex != null ? this.sourceIndex.hashCode() : 0);
        hash = 79 * hash + (this.connection != null ? this.connection.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[ArrayLink]").append("\n");
        if (connection != null) {
            sb.append("connection: ").append('\n');
            for (Connection c : connection) {
                sb.append(connection).append('\n');
            }
        }
        if (destinationIndex != null) {
            sb.append("destination index: ").append('\n');
            for (DestinationIndex c : destinationIndex) {
                sb.append(c).append('\n');
            }
        }
        if (destinationArray != null) {
            sb.append("destination Array: ").append(destinationArray).append('\n');
        }
        if (sourceArray != null) {
            sb.append("source Array: ").append(sourceArray).append('\n');
        }
        if (sourceIndex != null) {
            sb.append("source Index: ").append(sourceIndex).append('\n');
        }
        return sb.toString();
     }

}
