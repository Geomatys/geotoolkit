/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2026, Geomatys
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
package org.geotoolkit.tjs.xml.v100;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import org.geotoolkit.ows.xml.v110.OperationsMetadata;
import org.geotoolkit.ows.xml.v110.ServiceIdentification;
import org.geotoolkit.ows.xml.v110.ServiceProvider;

/**
 * XML encoded TJS GetCapabilities operation response.
 *
 * @author Quentin Bialota (Geomatys)
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tjsCapabilitiesType", propOrder = {"serviceIdentification", "serviceProvider", "operationsMetadata", "languages", "wsdl"})
@XmlRootElement(name = "Capabilities", namespace = "http://www.opengis.net/tjs/1.0")
public class CapabilitiesType {

    @XmlElement(name = "ServiceIdentification")
    private ServiceIdentification serviceIdentification;

    @XmlElement(name = "ServiceProvider")
    private ServiceProvider serviceProvider;

    @XmlElement(name = "OperationsMetadata")
    private OperationsMetadata operationsMetadata;

    @XmlElement(name = "Languages")
    private LanguagesType languages;

    @XmlElement(name = "WSDL")
    private WSDLType wsdl;

    @XmlAttribute(required = true)
    private String service = "TJS";

    @XmlAttribute(required = true)
    private String version = "1.0";

    @XmlAttribute
    private String updateSequence;

    @XmlAttribute(namespace = "http://www.w3.org/XML/1998/namespace", required = true)
    private String lang;

    public ServiceIdentification getServiceIdentification() {
        return serviceIdentification;
    }

    public void setServiceIdentification(ServiceIdentification serviceIdentification) {
        this.serviceIdentification = serviceIdentification;
    }

    public ServiceProvider getServiceProvider() {
        return serviceProvider;
    }

    public void setServiceProvider(ServiceProvider serviceProvider) {
        this.serviceProvider = serviceProvider;
    }

    public OperationsMetadata getOperationsMetadata() {
        return operationsMetadata;
    }

    public void setOperationsMetadata(OperationsMetadata operationsMetadata) {
        this.operationsMetadata = operationsMetadata;
    }

    public LanguagesType getLanguages() {
        return languages;
    }

    public void setLanguages(LanguagesType languages) {
        this.languages = languages;
    }

    public WSDLType getWsdl() {
        return wsdl;
    }

    public void setWsdl(WSDLType wsdl) {
        this.wsdl = wsdl;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getUpdateSequence() {
        return updateSequence;
    }

    public void setUpdateSequence(String updateSequence) {
        this.updateSequence = updateSequence;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }
}
