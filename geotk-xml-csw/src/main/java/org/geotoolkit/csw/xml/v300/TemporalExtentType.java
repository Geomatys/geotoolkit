/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2019, Geomatys
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

package org.geotoolkit.csw.xml.v300;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 *
 *              A type for specifying the temporal extent of the data
 *              item that a metadata record describes.  Omitting
 *              begin/end implies infinity in that direction.  The
 *              attribute "inclusive" can be used indicate whether
 *              the boundary value in included in extent or not.
 *
 *
 * <p>Classe Java pour TemporalExtentType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="TemporalExtentType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="begin" minOccurs="0">
 *           &lt;complexType>
 *             &lt;simpleContent>
 *               &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>dateTime">
 *                 &lt;attribute name="inclusive" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
 *               &lt;/extension>
 *             &lt;/simpleContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="end" minOccurs="0">
 *           &lt;complexType>
 *             &lt;simpleContent>
 *               &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>dateTime">
 *                 &lt;attribute name="inclusive" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
 *               &lt;/extension>
 *             &lt;/simpleContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TemporalExtentType", propOrder = {
    "begin",
    "end"
})
public class TemporalExtentType {

    protected TemporalExtentType.Begin begin;
    protected TemporalExtentType.End end;

    /**
     * Obtient la valeur de la propriété begin.
     *
     * @return
     *     possible object is
     *     {@link TemporalExtentType.Begin }
     *
     */
    public TemporalExtentType.Begin getBegin() {
        return begin;
    }

    /**
     * Définit la valeur de la propriété begin.
     *
     * @param value
     *     allowed object is
     *     {@link TemporalExtentType.Begin }
     *
     */
    public void setBegin(TemporalExtentType.Begin value) {
        this.begin = value;
    }

    /**
     * Obtient la valeur de la propriété end.
     *
     * @return
     *     possible object is
     *     {@link TemporalExtentType.End }
     *
     */
    public TemporalExtentType.End getEnd() {
        return end;
    }

    /**
     * Définit la valeur de la propriété end.
     *
     * @param value
     *     allowed object is
     *     {@link TemporalExtentType.End }
     *
     */
    public void setEnd(TemporalExtentType.End value) {
        this.end = value;
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     *
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     *
     * <pre>
     * &lt;complexType>
     *   &lt;simpleContent>
     *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>dateTime">
     *       &lt;attribute name="inclusive" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
     *     &lt;/extension>
     *   &lt;/simpleContent>
     * &lt;/complexType>
     * </pre>
     *
     *
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "value"
    })
    public static class Begin {

        @XmlValue
        @XmlSchemaType(name = "dateTime")
        protected XMLGregorianCalendar value;
        @XmlAttribute(name = "inclusive")
        protected Boolean inclusive;

        /**
         * Obtient la valeur de la propriété value.
         *
         * @return
         *     possible object is
         *     {@link XMLGregorianCalendar }
         *
         */
        public XMLGregorianCalendar getValue() {
            return value;
        }

        /**
         * Définit la valeur de la propriété value.
         *
         * @param value
         *     allowed object is
         *     {@link XMLGregorianCalendar }
         *
         */
        public void setValue(XMLGregorianCalendar value) {
            this.value = value;
        }

        /**
         * Obtient la valeur de la propriété inclusive.
         *
         * @return
         *     possible object is
         *     {@link Boolean }
         *
         */
        public boolean isInclusive() {
            if (inclusive == null) {
                return true;
            } else {
                return inclusive;
            }
        }

        /**
         * Définit la valeur de la propriété inclusive.
         *
         * @param value
         *     allowed object is
         *     {@link Boolean }
         *
         */
        public void setInclusive(Boolean value) {
            this.inclusive = value;
        }

    }


    /**
     * <p>Classe Java pour anonymous complex type.
     *
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     *
     * <pre>
     * &lt;complexType>
     *   &lt;simpleContent>
     *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>dateTime">
     *       &lt;attribute name="inclusive" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
     *     &lt;/extension>
     *   &lt;/simpleContent>
     * &lt;/complexType>
     * </pre>
     *
     *
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "value"
    })
    public static class End {

        @XmlValue
        @XmlSchemaType(name = "dateTime")
        protected XMLGregorianCalendar value;
        @XmlAttribute(name = "inclusive")
        protected Boolean inclusive;

        /**
         * Obtient la valeur de la propriété value.
         *
         * @return
         *     possible object is
         *     {@link XMLGregorianCalendar }
         *
         */
        public XMLGregorianCalendar getValue() {
            return value;
        }

        /**
         * Définit la valeur de la propriété value.
         *
         * @param value
         *     allowed object is
         *     {@link XMLGregorianCalendar }
         *
         */
        public void setValue(XMLGregorianCalendar value) {
            this.value = value;
        }

        /**
         * Obtient la valeur de la propriété inclusive.
         *
         * @return
         *     possible object is
         *     {@link Boolean }
         *
         */
        public boolean isInclusive() {
            if (inclusive == null) {
                return true;
            } else {
                return inclusive;
            }
        }

        /**
         * Définit la valeur de la propriété inclusive.
         *
         * @param value
         *     allowed object is
         *     {@link Boolean }
         *
         */
        public void setInclusive(Boolean value) {
            this.inclusive = value;
        }

    }

}
