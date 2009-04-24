/*
 * Sicade - Systèmes intégrés de connaissances pour l'aide à la décision en environnement
 * (C) 2008, Geomatys
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


package org.constellation.swe.v101;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for VectorType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="VectorType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/swe/1.0.1}AbstractVectorType">
 *       &lt;sequence>
 *         &lt;element name="coordinate" maxOccurs="unbounded">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;group ref="{http://www.opengis.net/swe/1.0.1}AnyNumerical" minOccurs="0"/>
 *                 &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}token" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "VectorType", propOrder = {
    "coordinate"
})
public class VectorType extends AbstractVectorType {

    @XmlElement(required = true)
    protected List<VectorType.Coordinate> coordinate;

    /**
     * Gets the value of the coordinate property.
     * 
     */
    public List<VectorType.Coordinate> getCoordinate() {
        if (coordinate == null) {
            coordinate = new ArrayList<VectorType.Coordinate>();
        }
        return this.coordinate;
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
     *       &lt;group ref="{http://www.opengis.net/swe/1.0.1}AnyNumerical" minOccurs="0"/>
     *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}token" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "count",
        "quantity",
        "time"
    })
    public static class Coordinate {

        @XmlElement(name = "Count")
        protected Count count;
        @XmlElement(name = "Quantity")
        protected QuantityType quantity;
        @XmlElement(name = "Time")
        protected TimeType time;
        @XmlAttribute(required = true)
        @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
        @XmlSchemaType(name = "token")
        protected String name;

        /**
         * Gets the value of the count property.
         */
        public Count getCount() {
            return count;
        }

        /**
         * Sets the value of the count property.
         */
        public void setCount(Count value) {
            this.count = value;
        }

        /**
         * Gets the value of the quantity property.
         */
        public QuantityType getQuantity() {
            return quantity;
        }

        /**
         * Sets the value of the quantity property.
         */
        public void setQuantity(QuantityType value) {
            this.quantity = value;
        }

        /**
         * Gets the value of the time property.
         */
        public TimeType getTime() {
            return time;
        }

        /**
         * Sets the value of the time property.
         *     
         */
        public void setTime(TimeType value) {
            this.time = value;
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
        public void setName(String value) {
            this.name = value;
        }

    }

}
