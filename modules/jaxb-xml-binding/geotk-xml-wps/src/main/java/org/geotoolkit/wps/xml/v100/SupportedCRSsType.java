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
import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.wps.xml.DataDescription;


/**
 * Listing of the Coordinate Reference System (CRS) support for this process input or output.
 *
 * <p>Java class for SupportedCRSsType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="SupportedCRSsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Default">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="CRS" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Supported" type="{http://www.opengis.net/wps/1.0.0}CRSsType"/>
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
@XmlType(name = "SupportedCRSsType", propOrder = {
    "_default",
    "supported"
})
public class SupportedCRSsType implements DataDescription {

    @XmlElement(name = "Default", namespace = "", required = true)
    protected SupportedCRSsType.Default _default;
    @XmlElement(name = "Supported", namespace = "", required = true)
    protected CRSsType supported;

    public SupportedCRSsType() {

    }

    public SupportedCRSsType(String _default, final List<String> supported) {
        this._default = new Default(_default);
        this.supported  = new CRSsType(supported);
    }

    /**
     * Gets the value of the default property.
     *
     * @return
     *     possible object is
     *     {@link SupportedCRSsType.Default }
     *
     */
    public SupportedCRSsType.Default getDefault() {
        return _default;
    }

    /**
     * Sets the value of the default property.
     *
     * @param value
     *     allowed object is
     *     {@link SupportedCRSsType.Default }
     *
     */
    public void setDefault(final SupportedCRSsType.Default value) {
        this._default = value;
    }

    /**
     * Gets the value of the supported property.
     *
     * @return
     *     possible object is
     *     {@link CRSsType }
     *
     */
    public CRSsType getSupported() {
        return supported;
    }

    /**
     * Sets the value of the supported property.
     *
     * @param value
     *     allowed object is
     *     {@link CRSsType }
     *
     */
    public void setSupported(final CRSsType value) {
        this.supported = value;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[").append(this.getClass().getSimpleName()).append("]\n");
        if (_default != null) {
            sb.append("_default:").append(_default).append('\n');
        }
        if (supported != null) {
            sb.append("supported:").append(supported).append('\n');
        }
        return sb.toString();
    }

    /**
     * Verify that this entry is identical to the specified object.
     * @param object Object to compare
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof SupportedCRSsType) {
            final SupportedCRSsType that = (SupportedCRSsType) object;
            return Objects.equals(this._default, that._default) &&
                   Objects.equals(this.supported, that.supported);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 83 * hash + Objects.hashCode(this._default);
        hash = 83 * hash + Objects.hashCode(this.supported);
        return hash;
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
     *         &lt;element name="CRS" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
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
        "crs"
    })
    public static class Default {

        @XmlElement(name = "CRS", namespace = "", required = true)
        @XmlSchemaType(name = "anyURI")
        protected String crs;

        public Default() {

        }

        public Default(String crs) {
            this.crs = crs;
        }

        /**
         * Gets the value of the crs property.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getCRS() {
            return crs;
        }

        /**
         * Sets the value of the crs property.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setCRS(final String value) {
            this.crs = value;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("[").append(this.getClass().getSimpleName()).append("]\n");
            if (crs != null) {
                sb.append("crs:").append(crs).append('\n');
            }
            return sb.toString();
        }

        /**
         * Verify that this entry is identical to the specified object.
         * @param object Object to compare
         */
        @Override
        public boolean equals(final Object object) {
            if (object == this) {
                return true;
            }
            if (object instanceof Default) {
                final Default that = (Default) object;
                return Objects.equals(this.crs, that.crs);
            }
            return false;
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 17 * hash + Objects.hashCode(this.crs);
            return hash;
        }

    }

}
