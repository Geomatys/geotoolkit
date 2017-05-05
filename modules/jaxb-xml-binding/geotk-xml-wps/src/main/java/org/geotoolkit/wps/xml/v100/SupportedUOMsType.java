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
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.ows.xml.v110.DomainMetadataType;


/**
 * Listing of the Unit of Measure (U0M) support for this process input or output.
 *
 * <p>Java class for SupportedUOMsType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="SupportedUOMsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Default">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://www.opengis.net/ows/1.1}UOM"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Supported" type="{http://www.opengis.net/wps/1.0.0}UOMsType"/>
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
@XmlType(name = "SupportedUOMsType", propOrder = {
    "_default",
    "supported"
})
public class SupportedUOMsType {

    @XmlElement(name = "Default", namespace = "", required = true)
    protected SupportedUOMsType.Default _default;
    @XmlElement(name = "Supported", namespace = "", required = true)
    protected UOMsType supported;

    public SupportedUOMsType() {

    }

    public SupportedUOMsType(SupportedUOMsType.Default _default, UOMsType supported) {
        this._default = _default;
        this.supported = supported;
    }

    public SupportedUOMsType(DomainMetadataType _default, List<DomainMetadataType> supported) {
        if (_default != null) {
            this._default = new Default(_default);
        }
        if (supported != null) {
            this.supported = new UOMsType(supported);
        }
    }
    /**
     * Gets the value of the default property.
     *
     * @return
     *     possible object is
     *     {@link SupportedUOMsType.Default }
     *
     */
    public SupportedUOMsType.Default getDefault() {
        return _default;
    }

    /**
     * Sets the value of the default property.
     *
     * @param value
     *     allowed object is
     *     {@link SupportedUOMsType.Default }
     *
     */
    public void setDefault(final SupportedUOMsType.Default value) {
        this._default = value;
    }

    /**
     * Gets the value of the supported property.
     *
     * @return
     *     possible object is
     *     {@link UOMsType }
     *
     */
    public UOMsType getSupported() {
        return supported;
    }

    /**
     * Sets the value of the supported property.
     *
     * @param value
     *     allowed object is
     *     {@link UOMsType }
     *
     */
    public void setSupported(final UOMsType value) {
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
        if (object instanceof SupportedUOMsType) {
            final SupportedUOMsType that = (SupportedUOMsType) object;
            return Objects.equals(this._default, that._default) &&
                   Objects.equals(this.supported, that.supported);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 31 * hash + Objects.hashCode(this._default);
        hash = 31 * hash + Objects.hashCode(this.supported);
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
     *         &lt;element ref="{http://www.opengis.net/ows/1.1}UOM"/>
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
        "uom"
    })
    public static class Default {

        @XmlElement(name = "UOM", namespace = "http://www.opengis.net/ows/1.1", required = true)
        protected DomainMetadataType uom;

        public Default() {

        }

        public Default(DomainMetadataType uom) {
            this.uom = uom;
        }

        /**
         * Reference to the default UOM supported for this Input/Output
         *
         * @return
         *     possible object is
         *     {@link DomainMetadataType }
         *
         */
        public DomainMetadataType getUOM() {
            return uom;
        }

        /**
         * Reference to the default UOM supported for this Input/Output
         *
         * @param value
         *     allowed object is
         *     {@link DomainMetadataType }
         *
         */
        public void setUOM(final DomainMetadataType value) {
            this.uom = value;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("[").append(this.getClass().getSimpleName()).append("]\n");
            if (uom != null) {
                sb.append("uom:").append(uom).append('\n');
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
                return Objects.equals(this.uom, that.uom);
            }
            return false;
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 67 * hash + Objects.hashCode(this.uom);
            return hash;
        }

    }

}
