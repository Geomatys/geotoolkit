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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.ows.xml.v110.AcceptVersionsType;
import org.apache.sis.util.Version;
import org.geotoolkit.ows.xml.AcceptFormats;
import org.geotoolkit.ows.xml.Sections;


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
 *         &lt;element name="AcceptVersions" type="{http://www.opengis.net/ows/1.1}AcceptVersionsType" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="service" use="required" type="{http://www.opengis.net/ows/1.1}ServiceType" fixed="WPS" />
 *       &lt;attribute name="language" type="{http://www.w3.org/2001/XMLSchema}string" />
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
    "acceptVersions"
})
@XmlRootElement(name = "GetCapabilities")
public class GetCapabilities implements org.geotoolkit.wps.xml.GetCapabilities {

    @XmlElement(name = "AcceptVersions")
    private AcceptVersionsType acceptVersions;
    @XmlAttribute(required = true)
    private String service;
    @XmlAttribute
    private String language;
    @XmlAttribute
    private String updateSequence;

    public GetCapabilities() {
        
    }
    
    public GetCapabilities(String service, String language, String updateSequence, AcceptVersionsType versions) {
        this.acceptVersions = versions;
        this.language = language;
        this.service = service;
        this.updateSequence = updateSequence;
    }
    
    
    /**
     * Gets the value of the acceptVersions property.
     * 
     * @return
     *     possible object is
     *     {@link AcceptVersionsType }
     *     
     */
    @Override
    public AcceptVersionsType getAcceptVersions() {
        return acceptVersions;
    }

    /**
     * Sets the value of the acceptVersions property.
     * 
     * @param value
     *     allowed object is
     *     {@link AcceptVersionsType }
     *     
     */
    public void setAcceptVersions(final AcceptVersionsType value) {
        this.acceptVersions = value;
    }

    /**
     * Gets the value of the service property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Override
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
    @Override
    public void setService(final String value) {
        this.service = value;
    }

    /**
     * Gets the value of the language property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLanguage() {
        return language;
    }

    @Override
    public List<String> getLanguages() {
        if (language != null) {
            return Arrays.asList(language);
        }
        return new ArrayList<>();
    }
    
    /**
     * Sets the value of the language property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLanguage(final String value) {
        this.language = value;
    }

    /**
     * @return the updateSequence
     */
    @Override
    public String getUpdateSequence() {
        return updateSequence;
    }

    /**
     * @param updateSequence the updateSequence to set
     */
    public void setUpdateSequence(String updateSequence) {
        this.updateSequence = updateSequence;
    }

    @Override
    public void setVersion(String version) {
        if (version != null) {
            if (acceptVersions == null) {
                this.acceptVersions = new AcceptVersionsType(version);
            } else {
                 this.acceptVersions.addFirstVersion(version);
            }
        }
    }

    @Override
    public Version getVersion() {
        if (acceptVersions!= null && !acceptVersions.getVersion().isEmpty()) {
            return new Version(acceptVersions.getVersion().get(0));
        } 
        return null;
    }

    @Override
    public Sections getSections() {
        return null; // not implemented in 1.0.0
    }

    @Override
    public String getFirstAcceptFormat() {
        return null; // not implemented in 1.0.0
    }

    @Override
    public boolean containsSection(String sectionName) {
        return false;
    }

    @Override
    public AcceptFormats getAcceptFormats() {
        return null; // not implemented in 1.0.0
    }

}
