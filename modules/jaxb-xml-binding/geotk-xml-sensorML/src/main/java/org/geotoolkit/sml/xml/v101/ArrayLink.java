/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2007 - 2008, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 3 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

package org.geotoolkit.sml.xml.v101;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


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
 *                     &lt;attribute name="ref" type="{http://www.opengis.net/sensorML/1.0.1}linkRef" />
 *                   &lt;/restriction>
 *                 &lt;/complexContent>
 *               &lt;/complexType>
 *             &lt;/element>
 *             &lt;element name="destinationIndex" maxOccurs="unbounded" minOccurs="0">
 *               &lt;complexType>
 *                 &lt;complexContent>
 *                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                     &lt;attribute name="ref" type="{http://www.opengis.net/sensorML/1.0.1}linkRef" />
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
 *                     &lt;attribute name="ref" type="{http://www.opengis.net/sensorML/1.0.1}linkRef" />
 *                   &lt;/restriction>
 *                 &lt;/complexContent>
 *               &lt;/complexType>
 *             &lt;/element>
 *             &lt;element name="sourceIndex" minOccurs="0">
 *               &lt;complexType>
 *                 &lt;complexContent>
 *                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                     &lt;attribute name="ref" type="{http://www.opengis.net/sensorML/1.0.1}linkRef" />
 *                   &lt;/restriction>
 *                 &lt;/complexContent>
 *               &lt;/complexType>
 *             &lt;/element>
 *           &lt;/sequence>
 *         &lt;/choice>
 *         &lt;element ref="{http://www.opengis.net/sensorML/1.0.1}connection" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
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
public class ArrayLink {

    private ArrayLink.SourceArray sourceArray;
    private List<ArrayLink.DestinationIndex> destinationIndex;
    private ArrayLink.DestinationArray destinationArray;
    private ArrayLink.SourceIndex sourceIndex;
    private List<Connection> connection;

    /**
     * Gets the value of the sourceArray property.
     * 
     */
    public ArrayLink.SourceArray getSourceArray() {
        return sourceArray;
    }

    /**
     * Sets the value of the sourceArray property.
     * 
     */
    public void setSourceArray(ArrayLink.SourceArray value) {
        this.sourceArray = value;
    }

    /**
     * Gets the value of the destinationIndex property.
     * 
     */
    public List<ArrayLink.DestinationIndex> getDestinationIndex() {
        if (destinationIndex == null) {
            destinationIndex = new ArrayList<ArrayLink.DestinationIndex>();
        }
        return this.destinationIndex;
    }

    /**
     * Gets the value of the destinationArray property.
     * 
     */
    public ArrayLink.DestinationArray getDestinationArray() {
        return destinationArray;
    }

    /**
     * Sets the value of the destinationArray property.
     * 
     */
    public void setDestinationArray(ArrayLink.DestinationArray value) {
        this.destinationArray = value;
    }

    /**
     * Gets the value of the sourceIndex property.
     * 
     */
    public ArrayLink.SourceIndex getSourceIndex() {
        return sourceIndex;
    }

    /**
     * Sets the value of the sourceIndex property.
     * 
     */
    public void setSourceIndex(ArrayLink.SourceIndex value) {
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


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;attribute name="ref" type="{http://www.opengis.net/sensorML/1.0.1}linkRef" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class DestinationArray {

        @XmlAttribute
        @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
        private String ref;

        /**
         * Gets the value of the ref property.
         * 
        */
        public String getRef() {
            return ref;
        }

        /**
         * Sets the value of the ref property.
         * 
         */
        public void setRef(String value) {
            this.ref = value;
        }

    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;attribute name="ref" type="{http://www.opengis.net/sensorML/1.0.1}linkRef" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class DestinationIndex {

        @XmlAttribute
        @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
        private String ref;

        /**
         * Gets the value of the ref property.
         * 
        */
        public String getRef() {
            return ref;
        }

        /**
         * Sets the value of the ref property.
         * 
         */
        public void setRef(String value) {
            this.ref = value;
        }

    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;attribute name="ref" type="{http://www.opengis.net/sensorML/1.0.1}linkRef" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class SourceArray {

        @XmlAttribute
        @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
        private String ref;

        /**
         * Gets the value of the ref property.
         * 
         */
        public String getRef() {
            return ref;
        }

        /**
         * Sets the value of the ref property.
         * 
         */
        public void setRef(String value) {
            this.ref = value;
        }

    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;attribute name="ref" type="{http://www.opengis.net/sensorML/1.0.1}linkRef" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class SourceIndex {

        @XmlAttribute
        @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
        private String ref;

        /**
         * Gets the value of the ref property.
         * 
         */
        public String getRef() {
            return ref;
        }

        /**
         * Sets the value of the ref property.
         * 
         */
        public void setRef(String value) {
            this.ref = value;
        }

    }

}
