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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.ows.xml.AbstractCapabilitiesCore;
import org.geotoolkit.ows.xml.Sections;
import org.geotoolkit.ows.xml.v110.CapabilitiesBaseType;
import org.geotoolkit.ows.xml.v110.OperationsMetadata;
import org.geotoolkit.ows.xml.v110.ServiceIdentification;
import org.geotoolkit.ows.xml.v110.ServiceProvider;
import org.geotoolkit.wps.xml.WPSCapabilities;
import org.geotoolkit.wps.xml.WPSResponse;


/**
 * <p>Java class for WPSCapabilitiesType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WPSCapabilitiesType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/ows/1.1}CapabilitiesBaseType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/wps/1.0.0}ProcessOfferings"/>
 *         &lt;element ref="{http://www.opengis.net/wps/1.0.0}Languages"/>
 *         &lt;element ref="{http://www.opengis.net/wps/1.0.0}WSDL" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="service" use="required" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" fixed="WPS" />
 *       &lt;attribute ref="{http://www.w3.org/XML/1998/namespace}lang use="required""/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WPSCapabilitiesType", propOrder = {
    "processOfferings",
    "languages",
    "wsdl"
})
@XmlRootElement(name = "Capabilities")
public class WPSCapabilitiesType extends CapabilitiesBaseType implements WPSCapabilities, WPSResponse {

    @XmlElement(name = "ProcessOfferings", required = true)
    private ProcessOfferings processOfferings;
    @XmlElement(name = "Languages", required = true)
    private Languages languages;
    @XmlElement(name = "WSDL")
    private WSDL wsdl;
    @XmlAttribute(required = true)
    @XmlSchemaType(name = "anySimpleType")
    private String service;
    @XmlAttribute(namespace = "http://www.w3.org/XML/1998/namespace", required = true)
    private String lang;

    public WPSCapabilitiesType() {
        
    }
    
    public WPSCapabilitiesType(final String version, final String updateSequence) {
        super(version, updateSequence);
    }
    
    public WPSCapabilitiesType(final ServiceIdentification serviceIdentification, final ServiceProvider serviceProvider,
            final OperationsMetadata operationsMetadata, final String version, final String updateSequence, final ProcessOfferings processOfferings,
            final Languages languages, final WSDL wsdl) {
        super(serviceIdentification, serviceProvider, operationsMetadata, version, updateSequence);
        this.processOfferings = processOfferings;
        this.languages        = languages;
        this.service          = "WPS";
        this.lang             = "en-EN";
        this.wsdl             = wsdl;
    }
    
    /**
     * Gets the value of the processOfferings property.
     * 
     * @return
     *     possible object is
     *     {@link ProcessOfferings }
     *     
     */
    public ProcessOfferings getProcessOfferings() {
        return processOfferings;
    }

    /**
     * Sets the value of the processOfferings property.
     * 
     * @param value
     *     allowed object is
     *     {@link ProcessOfferings }
     *     
     */
    public void setProcessOfferings(final ProcessOfferings value) {
        this.processOfferings = value;
    }

    /**
     * List of the default and other languages supported by this service. 
     * 
     * @return
     *     possible object is
     *     {@link Languages }
     *     
     */
    public Languages getLanguages() {
        return languages;
    }

    /**
     * List of the default and other languages supported by this service. 
     * 
     * @param value
     *     allowed object is
     *     {@link Languages }
     *     
     */
    public void setLanguages(final Languages value) {
        this.languages = value;
    }

    /**
     * Location of a WSDL document which describes the entire service.
     * 
     * @return
     *     possible object is
     *     {@link WSDL }
     *     
     */
    public WSDL getWSDL() {
        return wsdl;
    }

    /**
     * Location of a WSDL document which describes the entire service.
     * 
     * @param value
     *     allowed object is
     *     {@link WSDL }
     *     
     */
    public void setWSDL(final WSDL value) {
        this.wsdl = value;
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
    public void setService(final String value) {
        this.service = value;
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

    @Override
    public AbstractCapabilitiesCore applySections(Sections sections) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
