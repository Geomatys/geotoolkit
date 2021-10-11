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

package org.geotoolkit.ows.xml.v200;

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
import org.geotoolkit.ows.xml.AbstractCapabilitiesBase;


/**
 * XML encoded GetCapabilities operation response. This
 *       document provides clients with service metadata about a specific service
 *       instance, usually including metadata about the tightly-coupled data
 *       served. If the server does not implement the updateSequence parameter,
 *       the server shall always return the complete Capabilities document,
 *       without the updateSequence parameter. When the server implements the
 *       updateSequence parameter and the GetCapabilities operation request
 *       included the updateSequence parameter with the current value, the server
 *       shall return this element with only the "version" and "updateSequence"
 *       attributes. Otherwise, all optional elements shall be included or not
 *       depending on the actual value of the Contents parameter in the
 *       GetCapabilities operation request. This base type shall be extended by
 *       each specific OWS to include the additional contents
 *       needed.
 *
 * <p>Java class for CapabilitiesBaseType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="CapabilitiesBaseType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/ows/2.0}ServiceIdentification" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/ows/2.0}ServiceProvider" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/ows/2.0}OperationsMetadata" minOccurs="0"/>
 *         &lt;element name="Languages" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://www.opengis.net/ows/2.0}Language" maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute name="version" use="required" type="{http://www.opengis.net/ows/2.0}VersionType" />
 *       &lt;attribute name="updateSequence" type="{http://www.opengis.net/ows/2.0}UpdateSequenceType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "CapabilitiesBaseType", propOrder = {
    "serviceIdentification",
    "serviceProvider",
    "operationsMetadata",
    "languagesToMarshall"
})
public abstract class CapabilitiesBaseType implements AbstractCapabilitiesBase {

    @XmlElement(name = "ServiceIdentification")
    private ServiceIdentification serviceIdentification;
    @XmlElement(name = "ServiceProvider")
    private ServiceProvider serviceProvider;
    @XmlElement(name = "OperationsMetadata")
    private OperationsMetadata operationsMetadata;
    /**
     * @implNote language marshalling rules are set on a private getter, because
     * of WPS retro-compatiblity rules.
     */
    private CapabilitiesBaseType.Languages languages;
    @XmlAttribute(required = true)
    private String version;
    @XmlAttribute
    private String updateSequence;
    @XmlAttribute(namespace = "http://www.w3.org/XML/1998/namespace", required = true)
    private String lang;

     /**
     * Empty constructor used by JAXB.
     */
    protected CapabilitiesBaseType() {
    }

    /**
     * Build the base of a Capabilities document.
     */
    public CapabilitiesBaseType(final String version, final String updateSequence) {
        this(null, null, null, version, updateSequence, null);
    }

    /**
     * Build the base of a Capabilities document.
     */
    public CapabilitiesBaseType(final ServiceIdentification serviceIdentification, final ServiceProvider serviceProvider,
            final OperationsMetadata operationsMetadata, final String version, final String updateSequence, CapabilitiesBaseType.Languages languages) {
        this(serviceIdentification, serviceProvider, operationsMetadata, version, updateSequence, languages, null);
    }

    public CapabilitiesBaseType(final ServiceIdentification serviceIdentification, final ServiceProvider serviceProvider,
            final OperationsMetadata operationsMetadata, final String version, final String updateSequence, CapabilitiesBaseType.Languages languages, final String lang) {
        this.operationsMetadata    = operationsMetadata;
        this.serviceIdentification = serviceIdentification;
        this.serviceProvider       = serviceProvider;
        this.updateSequence        = updateSequence;
        this.version               = version;
        this.languages             = languages;
        this.lang                  = lang;
    }

    /**
     * Gets the value of the serviceIdentification property.
     *
     * @return
     *     possible object is
     *     {@link ServiceIdentification }
     *
     */
    @Override
    public ServiceIdentification getServiceIdentification() {
        return serviceIdentification;
    }

    /**
     * Sets the value of the serviceIdentification property.
     *
     * @param value
     *     allowed object is
     *     {@link ServiceIdentification }
     *
     */
    public void setServiceIdentification(ServiceIdentification value) {
        this.serviceIdentification = value;
    }

    /**
     * Gets the value of the serviceProvider property.
     *
     * @return
     *     possible object is
     *     {@link ServiceProvider }
     *
     */
    @Override
    public ServiceProvider getServiceProvider() {
        return serviceProvider;
    }

    /**
     * Sets the value of the serviceProvider property.
     *
     * @param value
     *     allowed object is
     *     {@link ServiceProvider }
     *
     */
    public void setServiceProvider(ServiceProvider value) {
        this.serviceProvider = value;
    }

    /**
     * Gets the value of the operationsMetadata property.
     *
     * @return
     *     possible object is
     *     {@link OperationsMetadata }
     *
     */
    @Override
    public OperationsMetadata getOperationsMetadata() {
        return operationsMetadata;
    }

    /**
     * Sets the value of the operationsMetadata property.
     *
     * @param value
     *     allowed object is
     *     {@link OperationsMetadata }
     *
     */
    public void setOperationsMetadata(OperationsMetadata value) {
        this.operationsMetadata = value;
    }

    @Override
    public void updateURL(String url) {
        if (this.operationsMetadata != null) {
            this.operationsMetadata.updateURL(url);
        }
    }

    /**
     * Gets the value of the languages property.
     *
     * @return
     *     possible object is
     *     {@link CapabilitiesBaseType.Languages }
     *
     */
    public CapabilitiesBaseType.Languages getLanguages() {
        return languages;
    }

    @XmlElement(name = "Languages")
    protected CapabilitiesBaseType.Languages getLanguagesToMarshall() {
        return languages;
    }

    private void setLanguagesToMarshall(CapabilitiesBaseType.Languages value) {
        this.languages = value;
    }

    /**
     * Sets the value of the languages property.
     *
     * @param value
     *     allowed object is
     *     {@link CapabilitiesBaseType.Languages }
     *
     */
    public void setLanguages(CapabilitiesBaseType.Languages value) {
        this.languages = value;
    }

    /**
     * Gets the value of the version property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @Override
    public String getVersion() {
        return version;
    }

    /**
     * Sets the value of the version property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setVersion(String value) {
        this.version = value;
    }

    /**
     * Gets the value of the updateSequence property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @Override
    public String getUpdateSequence() {
        return updateSequence;
    }

    /**
     * Sets the value of the updateSequence property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setUpdateSequence(String value) {
        this.updateSequence = value;
    }

    /**
     * Gets the value of the lang property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getLang() {
        return lang;
    }

    /**
     * Sets the value of the lang property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setLang(final String value) {
        this.lang = value;
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
     *         &lt;element ref="{http://www.opengis.net/ows/2.0}Language" maxOccurs="unbounded"/>
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
    public static class Languages implements org.geotoolkit.ows.xml.Languages {

        public Languages() {

        }

        public Languages(List<String> language) {
            this.language = language;
        }

        @XmlElement(name = "Language", required = true)
        @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
        @XmlSchemaType(name = "language")
        private List<String> language;

        /**
         * Gets the value of the language property.
         *
         */
        public List<String> getLanguage() {
            if (language == null) {
                language = new ArrayList<>();
            }
            return this.language;
        }

    }

}
