/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2019, Geomatys
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

package org.geotoolkit.metalinker;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;


/**
 * <p>Classe Java pour resourcesType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="resourcesType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="url" maxOccurs="unbounded">
 *           &lt;complexType>
 *             &lt;simpleContent>
 *               &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *                 &lt;attribute name="type" default="http">
 *                   &lt;simpleType>
 *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                       &lt;enumeration value="ftp"/>
 *                       &lt;enumeration value="ftps"/>
 *                       &lt;enumeration value="http"/>
 *                       &lt;enumeration value="https"/>
 *                       &lt;enumeration value="rsync"/>
 *                       &lt;enumeration value="bittorrent"/>
 *                       &lt;enumeration value="magnet"/>
 *                       &lt;enumeration value="ed2k"/>
 *                     &lt;/restriction>
 *                   &lt;/simpleType>
 *                 &lt;/attribute>
 *                 &lt;attribute name="location">
 *                   &lt;simpleType>
 *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                       &lt;maxLength value="2"/>
 *                     &lt;/restriction>
 *                   &lt;/simpleType>
 *                 &lt;/attribute>
 *                 &lt;attribute name="preference" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
 *                 &lt;attribute name="maxconnections" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" />
 *               &lt;/extension>
 *             &lt;/simpleContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute name="maxconnections" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "resourcesType", propOrder = {
    "url"
})
public class ResourcesType {

    @XmlElement(required = true)
    protected List<ResourcesType.Url> url;
    @XmlAttribute(name = "maxconnections")
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger maxconnections;

    /**
     * Gets the value of the url property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the url property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getUrl().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ResourcesType.Url }
     *
     *
     */
    public List<ResourcesType.Url> getUrl() {
        if (url == null) {
            url = new ArrayList<ResourcesType.Url>();
        }
        return this.url;
    }

    /**
     * Obtient la valeur de la propriété maxconnections.
     *
     * @return
     *     possible object is
     *     {@link BigInteger }
     *
     */
    public BigInteger getMaxconnections() {
        return maxconnections;
    }

    /**
     * Définit la valeur de la propriété maxconnections.
     *
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *
     */
    public void setMaxconnections(BigInteger value) {
        this.maxconnections = value;
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     *
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     *
     * <pre>
     * &lt;complexType>
     *   &lt;simpleContent>
     *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
     *       &lt;attribute name="type" default="http">
     *         &lt;simpleType>
     *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *             &lt;enumeration value="ftp"/>
     *             &lt;enumeration value="ftps"/>
     *             &lt;enumeration value="http"/>
     *             &lt;enumeration value="https"/>
     *             &lt;enumeration value="rsync"/>
     *             &lt;enumeration value="bittorrent"/>
     *             &lt;enumeration value="magnet"/>
     *             &lt;enumeration value="ed2k"/>
     *           &lt;/restriction>
     *         &lt;/simpleType>
     *       &lt;/attribute>
     *       &lt;attribute name="location">
     *         &lt;simpleType>
     *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *             &lt;maxLength value="2"/>
     *           &lt;/restriction>
     *         &lt;/simpleType>
     *       &lt;/attribute>
     *       &lt;attribute name="preference" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
     *       &lt;attribute name="maxconnections" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" />
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
    public static class Url {

        @XmlValue
        protected String value;
        @XmlAttribute(name = "type")
        protected String type;
        @XmlAttribute(name = "location")
        protected String location;
        @XmlAttribute(name = "preference")
        @XmlSchemaType(name = "nonNegativeInteger")
        protected BigInteger preference;
        @XmlAttribute(name = "maxconnections")
        @XmlSchemaType(name = "positiveInteger")
        protected BigInteger maxconnections;

        /**
         * Obtient la valeur de la propriété value.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getValue() {
            return value;
        }

        /**
         * Définit la valeur de la propriété value.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setValue(String value) {
            this.value = value;
        }

        /**
         * Obtient la valeur de la propriété type.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getType() {
            if (type == null) {
                return "http";
            } else {
                return type;
            }
        }

        /**
         * Définit la valeur de la propriété type.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setType(String value) {
            this.type = value;
        }

        /**
         * Obtient la valeur de la propriété location.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getLocation() {
            return location;
        }

        /**
         * Définit la valeur de la propriété location.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setLocation(String value) {
            this.location = value;
        }

        /**
         * Obtient la valeur de la propriété preference.
         *
         * @return
         *     possible object is
         *     {@link BigInteger }
         *
         */
        public BigInteger getPreference() {
            return preference;
        }

        /**
         * Définit la valeur de la propriété preference.
         *
         * @param value
         *     allowed object is
         *     {@link BigInteger }
         *
         */
        public void setPreference(BigInteger value) {
            this.preference = value;
        }

        /**
         * Obtient la valeur de la propriété maxconnections.
         *
         * @return
         *     possible object is
         *     {@link BigInteger }
         *
         */
        public BigInteger getMaxconnections() {
            return maxconnections;
        }

        /**
         * Définit la valeur de la propriété maxconnections.
         *
         * @param value
         *     allowed object is
         *     {@link BigInteger }
         *
         */
        public void setMaxconnections(BigInteger value) {
            this.maxconnections = value;
        }

    }

}
