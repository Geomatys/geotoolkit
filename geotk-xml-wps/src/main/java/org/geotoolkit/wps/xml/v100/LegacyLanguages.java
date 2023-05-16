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
package org.geotoolkit.wps.xml.v100;

import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.geotoolkit.wps.xml.WPSMarshallerPool;


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
 *         &lt;element name="Default">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://www.opengis.net/ows/1.1}Language"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Supported" type="{http://www.opengis.net/wps/1.0.0}LegacyLanguage"/>
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
    "_default",
    "supported"
})
@XmlRootElement(name = "Languages")
public class LegacyLanguages {

    @XmlElement(name = "Default", required = true)
    private LegacyLanguages.Default _default;
    @XmlElement(name = "Supported", required = true)
    private LegacyLanguage supported;

    public LegacyLanguages() {

    }

    public LegacyLanguages(final String _default,  final List<String> supported) {
        if (_default != null) {
            this._default = new Default(_default);
        }
        if (supported != null) {
            this.supported = new LegacyLanguage(supported);
        }
    }

    /**
     * Gets the value of the default property.
     *
     * @return
     *     possible object is
     *     {@link Languages.Default }
     *
     */
    public LegacyLanguages.Default getDefault() {
        return _default;
    }

    /**
     * Sets the value of the default property.
     *
     * @param value
     *     allowed object is
     *     {@link Languages.Default }
     *
     */
    public void setDefault(final LegacyLanguages.Default value) {
        this._default = value;
    }

    /**
     * Gets the value of the supported property.
     *
     * @return
     *     possible object is
     *     {@link LegacyLanguage }
     *
     */
    public LegacyLanguage getSupported() {
        return supported;
    }

    /**
     * Sets the value of the supported property.
     *
     * @param value
     *     allowed object is
     *     {@link LegacyLanguage }
     *
     */
    public void setSupported(final LegacyLanguage value) {
        this.supported = value;
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
     *       &lt;sequence>
     *         &lt;element ref="{http://www.opengis.net/ows/1.1}Language"/>
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
        "language"
    })
    public static class Default {

        @XmlElement(name = "Language", namespace = WPSMarshallerPool.OWS_2_0_NAMESPACE, required = true)
        @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
        @XmlSchemaType(name = "language")
        private String language;

        public Default() {

        }

        public Default(final String language) {
           this.language = language;
        }

        /**
         * Identifier of the default language supported by the service.  This language identifier shall be as specified in IETF RFC 4646.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getLanguage() {
            return language;
        }

        /**
         * Identifier of the default language supported by the service.  This language identifier shall be as specified in IETF RFC 4646.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setLanguage(final String value) {
            this.language = value;
        }

    }

}
