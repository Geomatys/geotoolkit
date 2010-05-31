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
package org.geotoolkit.wcs.xml.v100;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.wcs.xml.GetCapabilities;
import org.geotoolkit.ows.xml.AcceptFormats;
import org.geotoolkit.ows.xml.AcceptVersions;
import org.geotoolkit.ows.xml.Sections;
import org.geotoolkit.util.Version;


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
 *         &lt;element name="section" type="{http://www.opengis.net/wcs}CapabilitiesSectionType" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="service" use="required" type="{http://www.w3.org/2001/XMLSchema}string" fixed="WCS" />
 *       &lt;attribute name="version" type="{http://www.w3.org/2001/XMLSchema}string" fixed="1.0.0" />
 *       &lt;attribute name="updateSequence" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "section"
})
@XmlRootElement(name = "GetCapabilities")
public class GetCapabilitiesType implements GetCapabilities {

    @XmlElement(defaultValue = "/")
    private String section;
    @XmlAttribute(required = true)
    private String service;
    @XmlAttribute
    private String version;
    @XmlAttribute
    private String updateSequence;

    /**
     * An empty constructor used by JAXB
     */
    GetCapabilitiesType(){
    }
    
    /**
     * Build a new getCapabilities request version 1.0.0.
     */
    public GetCapabilitiesType(String section, String updateSequence){
        this.version = "1.0.0";
        this.updateSequence = updateSequence;
        if (section == null) {
            section = "/";
        } else {
            this.section        = section;
        }
        this.service        = "WCS";
    }
    
    /**
     * Gets the value of the section property.
     */
    public String getSection() {
        return section;
    }

    /**
     * Gets the value of the service property.
     */
    @Override
    public String getService() {
        if (service == null) {
            return "WCS";
        } else {
            return service;
        }
    }

    /**
     * Gets the value of the version property.
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
     * Gets the value of the updateSequence property.
     */
    public String getUpdateSequence() {
        return updateSequence;
    }

    /*
     *
     *  method added for compatibility with the upper version
     *
     */


    @Override
    public AcceptVersions getAcceptVersions() {
        return new AcceptVersions() {

            @Override
            public List<String> getVersion() {
                return Arrays.asList(version);
            }
        };
    }

    @Override
    public Sections getSections() {
        return new Sections() {

            @Override
            public List<String> getSection() {
                final StringTokenizer tokens = new StringTokenizer(section, ",");
                List<String> sections = new ArrayList<String>();
                while (tokens.hasMoreTokens()) {
                    final String token = tokens.nextToken().trim();
                    sections.add(token);
                }
                return sections;
            }

            @Override
            public void add(String sec) {
                if (sec != null)
                    section = section + ',' + sec;
            }

            @Override
            public boolean containsSection(String sectionName) {
                if (section != null) {
                    return section.contains(sectionName);
                }
                return false;
            }
        };
    }

    @Override
    public AcceptFormats getAcceptFormats() {
        return new AcceptFormats() {

            @Override
            public List<String> getOutputFormat() {
                return Arrays.asList("application/xml");
            }
        };
    }

    @Override
    public String toKvp() {
        return "request=GetCapabilities&service="+ getService() + "&version="+ getVersion();
    }

    @Override
    public String getFirstAcceptFormat() {
        return "application/xml";
    }

    @Override
    public boolean containsSection(String sectionName) {
        if (section != null) {
            return section.contains(sectionName);
        }
        return false;
    }
}
