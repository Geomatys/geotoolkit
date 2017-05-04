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
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.ows.xml.AbstractCapabilitiesCore;
import org.geotoolkit.ows.xml.Sections;
import org.geotoolkit.ows.xml.v200.CapabilitiesBaseType;
import org.geotoolkit.ows.xml.v200.OperationsMetadata;
import org.geotoolkit.ows.xml.v200.ServiceIdentification;
import org.geotoolkit.ows.xml.v200.ServiceProvider;
import org.geotoolkit.wps.xml.ProcessOfferings;
import org.geotoolkit.wps.xml.WPSCapabilities;
import org.geotoolkit.wps.xml.WPSResponse;
import org.w3c.dom.Element;


/**
 * <p>Java class for WPSCapabilitiesType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="WPSCapabilitiesType">
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
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WPSCapabilitiesType", propOrder = {
    "contents",
    "extension"
})
@XmlRootElement(name = "WPSCapabilities")
public class WPSCapabilitiesType extends CapabilitiesBaseType implements WPSCapabilities, WPSResponse {

    @XmlElement(name = "Contents", required = true)
    protected Contents contents;
    @XmlElement(name = "Extension")
    protected WPSCapabilitiesType.Extension extension;
    @XmlAttribute(name = "service", required = true)
    @XmlSchemaType(name = "anySimpleType")
    protected String service;

    public WPSCapabilitiesType() {

    }

    public WPSCapabilitiesType(final ServiceIdentification serviceIdentification, final ServiceProvider serviceProvider,
            final OperationsMetadata operationsMetadata, final String version, final String updateSequence, final Contents contents,
            final CapabilitiesBaseType.Languages languages) {
        super(serviceIdentification, serviceProvider, operationsMetadata, version, updateSequence, languages);
        this.contents = contents;
        this.service = "WPS";
    }

     public WPSCapabilitiesType(final ServiceIdentification serviceIdentification, final ServiceProvider serviceProvider,
            final OperationsMetadata operationsMetadata, final String version, final String updateSequence, final Contents contents,
            final CapabilitiesBaseType.Languages languages, final Extension ext) {
        super(serviceIdentification, serviceProvider, operationsMetadata, version, updateSequence, languages);
        this.contents = contents;
        this.service = "WPS";
        this.extension = extension;
    }

    public WPSCapabilitiesType(final String version, final String updateSequence) {
        super(version, updateSequence);
        this.service = "WPS";
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

    @Override
    public ProcessOfferings getProcessOfferings() {
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
    public WPSCapabilitiesType.Extension getExtension() {
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
    public void setExtension(WPSCapabilitiesType.Extension value) {
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
        if (service == null) {
            return "WPS";
        } else {
            return service;
        }
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
        if (sections == null) {
            return this;
        }
        ServiceIdentification si = null;
        ServiceProvider       sp = null;
        OperationsMetadata    om = null;
        Contents              po = null;

        //we enter the information for service identification.
        if (sections.containsSection("ServiceIdentification") || sections.containsSection("All")) {
            si = getServiceIdentification();
        }

        //we enter the information for service provider.
        if (sections.containsSection("ServiceProvider") || sections.containsSection("All")) {
            sp = getServiceProvider();
        }
        //we enter the operation Metadata
        if (sections.containsSection("OperationsMetadata") || sections.containsSection("All")) {
            om = getOperationsMetadata();
        }
        if (sections.containsSection("Contents") || sections.containsSection("All")) {
            po = getContents();
        }
        return new WPSCapabilitiesType(si, sp, om, "2.0.0", getUpdateSequence(), po, getLanguages(), getExtension());
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
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "any"
    })
    public static class Extension {

        @XmlAnyElement(lax = true)
        protected List<Object> any;

        /**
         * Gets the value of the any property.
         *
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

}
