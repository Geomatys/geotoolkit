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
import org.geotoolkit.ows.xml.v110.AcceptFormatsType;
import org.geotoolkit.ows.xml.v110.SectionsType;

/**
 * Request to a TJS to perform the GetCapabilities operation.
 *
 * @author Quentin Bialota (Geomatys)
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetCapabilitiesType", propOrder = {"acceptVersions", "sections", "acceptFormats"})
@XmlRootElement(name = "GetCapabilities", namespace = "http://www.opengis.net/tjs/1.0")
public class GetCapabilitiesType {

    @XmlElement(name = "AcceptVersions")
    private AcceptVersionsType acceptVersions;

    @XmlElement(name = "Sections")
    private SectionsType sections;

    @XmlElement(name = "AcceptFormats")
    private AcceptFormatsType acceptFormats;

    @XmlAttribute(required = true)
    private String service = "TJS";

    @XmlAttribute
    private String language;

    @XmlAttribute
    private String updateSequence;

    public AcceptVersionsType getAcceptVersions() {
        return acceptVersions;
    }

    public void setAcceptVersions(AcceptVersionsType acceptVersions) {
        this.acceptVersions = acceptVersions;
    }

    public SectionsType getSections() {
        return sections;
    }

    public void setSections(SectionsType sections) {
        this.sections = sections;
    }

    public AcceptFormatsType getAcceptFormats() {
        return acceptFormats;
    }

    public void setAcceptFormats(AcceptFormatsType acceptFormats) {
        this.acceptFormats = acceptFormats;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getUpdateSequence() {
        return updateSequence;
    }

    public void setUpdateSequence(String updateSequence) {
        this.updateSequence = updateSequence;
    }
}
