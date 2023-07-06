/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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

package org.geotoolkit.wcs.xml.v200;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import org.geotoolkit.wcs.xml.ServiceMetadata;
import org.geotoolkit.wcs.xml.v200.crs.CrsMetadataType;


/**
 * <p>Java class for ServiceMetadataType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="ServiceMetadataType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="formatSupported" type="{http://www.w3.org/2001/XMLSchema}anyURI" maxOccurs="unbounded"/>
 *         &lt;element ref="{http://www.opengis.net/wcs/2.0}Extension" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ServiceMetadataType", propOrder = {
    "formatSupported",
    "extension"
})
public class ServiceMetadataType implements ServiceMetadata {

    @XmlElement(required = true)
    @XmlSchemaType(name = "anyURI")
    private List<String> formatSupported;
    @XmlElement(name = "Extension")
    private ExtensionType extension;

    public ServiceMetadataType() {

    }

    public ServiceMetadataType(final List<String> formatSupported) {
        this.formatSupported = formatSupported;
    }

    public ServiceMetadataType(final List<String> formatSupported, final List<String> supportedCrs) {
        this.formatSupported = formatSupported;
        this.extension = new ExtensionType(new CrsMetadataType(supportedCrs));
    }

    /**
     * Gets the value of the formatSupported property.
     *
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     *
     *
     */
    public List<String> getFormatSupported() {
        if (formatSupported == null) {
            formatSupported = new ArrayList<>();
        }
        return this.formatSupported;
    }

    /**
     * Gets the value of the extension property.
     *
     * @return
     *     possible object is
     *     {@link ExtensionType }
     *
     */
    public ExtensionType getExtension() {
        return extension;
    }

    /**
     * Sets the value of the extension property.
     *
     * @param value
     *     allowed object is
     *     {@link ExtensionType }
     *
     */
    public void setExtension(ExtensionType value) {
        this.extension = value;
    }

}
