/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2016, Geomatys
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


package org.geotoolkit.wps.xml.v200;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.geotoolkit.ows.xml.AbstractCapabilitiesCore;
import org.geotoolkit.ows.xml.Sections;
import org.geotoolkit.ows.xml.v200.CapabilitiesBaseType;
import org.geotoolkit.ows.xml.v200.OperationsMetadata;
import org.geotoolkit.ows.xml.v200.ServiceIdentification;
import org.geotoolkit.ows.xml.v200.ServiceProvider;
import org.geotoolkit.wps.xml.WPSMarshallerPool;
import org.geotoolkit.wps.xml.WPSResponse;
import org.geotoolkit.wps.xml.v100.LegacyLanguage;
import org.geotoolkit.wps.xml.v100.LegacyLanguages;
import org.geotoolkit.wps.xml.v100.WSDL;
import org.w3c.dom.Element;


/**
 * <p>Java class for Capabilities complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="Capabilities">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/ows/2.0}CapabilitiesBaseType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/wps/2.0}Contents"/>
 *         &lt;element name="Extension" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;any processContents='lax' namespace='##other' maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute name="service" use="required" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" fixed="WPS" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 * @implNote We should extend {@link CapabilitiesBaseType}, but because of a
 * conflict between WPS 1 languages and WPS 2 languages, we must manage element
 * filtering manually here.
 */
@XmlType(name = "WPSCapabilitiesType", propOrder = {
    "contents",
    "extension",
    "legacyLanguages",
    "wsdl"
})
@XmlRootElement(name = "Capabilities")
public class Capabilities extends CapabilitiesBaseType implements WPSResponse{

    @XmlElement(name = "Contents", required = true)
    private Contents contents;
    @XmlElement(name = "Extension")
    @XmlJavaTypeAdapter(FilterV2.CapabilitiesExtension.class)
    private Capabilities.Extension extension;
    @XmlAttribute(name = "service", required = true)
    @XmlSchemaType(name = "anySimpleType")
    private String service = "WPS";

    public Capabilities() {}

    public Capabilities(final ServiceIdentification serviceIdentification, final ServiceProvider serviceProvider,
            final OperationsMetadata operationsMetadata, final String version, final String updateSequence, final Contents contents,
            final CapabilitiesBaseType.Languages languages) {
        this(serviceIdentification, serviceProvider, operationsMetadata, version, updateSequence, contents, languages, null, null);
    }

    public Capabilities(final ServiceIdentification serviceIdentification, final ServiceProvider serviceProvider,
            final OperationsMetadata operationsMetadata, final String version, final String updateSequence, final Contents contents,
            final CapabilitiesBaseType.Languages languages, final Extension ext) {
        this(serviceIdentification, serviceProvider, operationsMetadata, version, updateSequence, contents, languages, ext, null);
    }

    public Capabilities(final ServiceIdentification serviceIdentification, final ServiceProvider serviceProvider,
            final OperationsMetadata operationsMetadata, final String version, final String updateSequence, final Contents contents,
            final CapabilitiesBaseType.Languages languages, final Extension ext, final String lang) {
        super(serviceIdentification, serviceProvider, operationsMetadata, version, updateSequence, languages, lang);
        this.contents = contents;
        this.extension = ext;
    }

    public Capabilities(final String version, final String updateSequence) {
        super(version, updateSequence);
    }

    /**
     * Overriden only to customize marshalling rules. The aim is to ignore this
     * element when not in WPS 2 configuration.
     * @return Languages currently accepted by this document.
     */
    @Override
    protected Languages getLanguagesToMarshall() {
        if (FilterByVersion.isV2()) {
            return super.getLanguagesToMarshall();
        }

        return null;
    }

    /**
     * Gets the value of the contents property.
     *
     * @return
     *     possible object is
     *     {@link Contents }
     *
     */
    public Contents getContents() {
        return contents;
    }

    /**
     * Sets the value of the contents property.
     *
     * @param value
     *     allowed object is
     *     {@link Contents }
     *
     */
    public void setContents(Contents value) {
        this.contents = value;
    }

    /**
     * Gets the value of the extension property.
     *
     * @return
     *     possible object is
     *     {@link WPSCapabilitiesType.Extension }
     *
     */
    public Capabilities.Extension getExtension() {
        return extension;
    }

    /**
     * Sets the value of the extension property.
     *
     * @param value
     *     allowed object is
     *     {@link WPSCapabilitiesType.Extension }
     *
     */
    public void setExtension(Capabilities.Extension value) {
        this.extension = value;
    }

    /**
     * Gets the value of the service property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getService() {
        return service;
    }

    /**
     * Sets the value of the service property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setService(String value) {
        this.service = value;
    }

    @Override
    public AbstractCapabilitiesCore applySections(Sections sections) {
        if (sections == null || sections.containsSection("All")) {
            return this;
        }

        final Capabilities newCapa = new Capabilities(getVersion(), getUpdateSequence());
        // TODO: should we apply sections to languages and extension ?
        newCapa.setLanguages(getLanguages());
        newCapa.setExtension(getExtension());

        //we enter the information for service identification.
        if (sections.containsSection("ServiceIdentification")) {
            newCapa.setServiceIdentification(getServiceIdentification());
        }

        //we enter the information for service provider.
        if (sections.containsSection("ServiceProvider")) {
            newCapa.setServiceProvider(getServiceProvider());
        }

        //we enter the operation Metadata
        if (sections.containsSection("OperationsMetadata")) {
            newCapa.setOperationsMetadata(getOperationsMetadata());
        }

        if (sections.containsSection("Contents")) {
            newCapa.setContents(getContents());
        }

        // WPS 1 compatibility code
        if (sections.containsSection("WSDL")) {
            newCapa.setWSDL(getWSDL());
        }

        return newCapa;
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
     *         &lt;any processContents='lax' namespace='##other' maxOccurs="unbounded" minOccurs="0"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     *
     *
     */
    @XmlType(name = "", propOrder = {
        "any"
    })
    public static class Extension {

        @XmlAnyElement(lax = true)
        protected List<Object> any;

        /**
         * Gets the value of the any property.
         *
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the any property.
         *
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getAny().add(newItem);
         * </pre>
         *
         *
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link Object }
         * {@link Element }
         *
         *
         */
        public List<Object> getAny() {
            if (any == null) {
                any = new ArrayList<>();
            }
            return this.any;
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    //
    // Following section is boilerplate code for WPS v1 retro-compatibility.
    //
    ////////////////////////////////////////////////////////////////////////////


    @XmlElement(name="WSDL")
    @XmlJavaTypeAdapter(FilterV1.WSDL.class)
    private WSDL wsdl;

    /**
     *
     * @return SOAP capabilities of the service.
     * @deprecated WPS 1 retro-compatibility. Use only when contacting WPS 1
     * services.
     */
    @Deprecated
    public WSDL getWSDL() {
        return wsdl;
    }

    /**
     *
     * @param wsdl SOAP capabilities of the service.
     * @deprecated WPS 1 retro-compatibility. Use only when contacting WPS 1
     * services.
     */
    @Deprecated
    public void setWSDL(WSDL wsdl) {
        this.wsdl = wsdl;
    }

    /**
     *
     * @return The WPS 1 language section, if we're marshalling WPS 1 data.
     */
    @XmlElement(name="Languages", namespace=WPSMarshallerPool.WPS_2_0_NAMESPACE, required = true)
    private LegacyLanguages getLegacyLanguages() {
        if (FilterByVersion.isV1()) {
            final CapabilitiesBaseType.Languages owsLanguages = getLanguages();
            final List<String> acceptedLanguages = owsLanguages == null ? null : owsLanguages.getLanguage();
            if (acceptedLanguages != null && !acceptedLanguages.isEmpty()) {
                return new LegacyLanguages(acceptedLanguages.get(0), acceptedLanguages);
            }
        }

        return null;
    }

    private void setLegacyLanguages(final LegacyLanguages legacy) {
        final LegacyLanguage supported;

        if (legacy == null || (supported = legacy.getSupported()) == null || supported.getLanguage().isEmpty()) {
            setLanguages(null);
            return;
        }

        final Languages languages = new Languages(supported.getLanguage());
        setLanguages(languages);
        // Give priority to default language by putting it in first position in language list.
        final LegacyLanguages.Default defaultLang = legacy.getDefault();
        if (defaultLang != null) {
            String s = defaultLang.getLanguage();
            if (s != null && !(s = s.trim()).isEmpty()) {
                final int defIndex = languages.getLanguage().indexOf(s);
                if (defIndex != 0) {
                    languages.getLanguage().remove(defIndex);
                    languages.getLanguage().add(0, s);
                }
            }
        }
    }
}
