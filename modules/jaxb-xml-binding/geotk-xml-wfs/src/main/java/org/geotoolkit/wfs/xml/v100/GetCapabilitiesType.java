/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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
package org.geotoolkit.wfs.xml.v100;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.ows.xml.AcceptFormats;
import org.geotoolkit.ows.xml.AcceptVersions;
import org.geotoolkit.ows.xml.Sections;
import org.apache.sis.util.Version;
import org.geotoolkit.wfs.xml.GetCapabilities;


/**
 * 
 *             This type defines the GetCapabilities operation.  In response
 *             to a GetCapabilities request, a Web Feature Service must 
 *             generate a capabilities XML document that validates against
 *             the schemas defined in WFS-capabilities.xsd.
 *          
 * 
 * <p>Java class for GetCapabilitiesType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetCapabilitiesType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="version" type="{http://www.w3.org/2001/XMLSchema}string" fixed="1.0.0" />
 *       &lt;attribute name="service" use="required" type="{http://www.w3.org/2001/XMLSchema}string" fixed="WFS" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetCapabilitiesType")
public class GetCapabilitiesType implements GetCapabilities {

    @XmlAttribute
    private String version;
    @XmlAttribute(required = true)
    private String service;

    /**
     * An empty constructor used by JAXB
     */
    public GetCapabilitiesType() {
    }

    /**
     * Build a minimal new getCapabilities request with the specified service.
     *
     * @param service MUST be WFS.
     */
    public GetCapabilitiesType(final String service, final String version) {
        this.service = service;
        this.version = version;
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
    public Version getVersion() {
        if (version == null) {
            return new Version("1.0.0");
        } else {
            return new Version(version);
        }
    }

    /**
     * Sets the value of the version property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Override
    public void setVersion(String value) {
        this.version = value;
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
            return "WFS";
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
    public void setService(String value) {
        this.service = value;
    }

    @Override
    public AcceptVersions getAcceptVersions() {
        return null; // not implemented in 1.0.0
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

    @Override
    public String getUpdateSequence() {
        return null; // not implemented in 1.0.0
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("[GetCapabilitiesType]\n");
        if (version != null) {
            sb.append("version:").append(version).append('\n');
        }
        if (service != null) {
            sb.append("service:").append(service).append('\n');
        }
        return sb.toString();
    }
}
