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
package org.geotoolkit.ows.xml.v110;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.ows.xml.AbstractGetCapabilities;
import org.geotoolkit.util.Utilities;
import org.geotoolkit.util.Version;


/**
 * XML encoded GetCapabilities operation request. This operation allows clients to retrieve service metadata about a specific service instance. In this XML encoding, no "request" parameter is included, since the element name specifies the specific operation. This base type shall be extended by each specific OWS to include the additional required "service" attribute, with the correct value for that OWS. 
 * 
 * <p>Java class for GetCapabilitiesType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetCapabilitiesType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="AcceptVersions" type="{http://www.opengis.net/ows/1.1}AcceptVersionsType" minOccurs="0"/>
 *         &lt;element name="Sections" type="{http://www.opengis.net/ows/1.1}SectionsType" minOccurs="0"/>
 *         &lt;element name="AcceptFormats" type="{http://www.opengis.net/ows/1.1}AcceptFormatsType" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="updateSequence" type="{http://www.opengis.net/ows/1.1}UpdateSequenceType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * @author Guilhem Legal
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetCapabilitiesType", propOrder = {
    "acceptVersions",
    "sections",
    "acceptFormats"
})
public class GetCapabilitiesType implements AbstractGetCapabilities {

    @XmlElement(name = "AcceptVersions")
    private AcceptVersionsType acceptVersions;
    @XmlElement(name = "Sections")
    private SectionsType sections;
    @XmlElement(name = "AcceptFormats")
    private AcceptFormatsType acceptFormats;
    @XmlAttribute
    private String updateSequence;

    /**
     * Empty constructor used by JAXB.
     */
    public GetCapabilitiesType(){ 
    }
    
    /**
     * Build a new GetCapabilities base request.
     */
    public GetCapabilitiesType(AcceptVersionsType acceptVersions, SectionsType sections,
            AcceptFormatsType acceptFormats, String updateSequence){ 
        this.acceptFormats  = acceptFormats;
        this.acceptVersions = acceptVersions;
        this.sections       = sections;
        this.updateSequence = updateSequence;
    }


    /**
     * Build a new GetCapabilities base request.
     */
    public GetCapabilitiesType(String acceptVersions, String acceptFormats){
        this.acceptFormats  = new AcceptFormatsType(acceptFormats);
        this.acceptVersions = new AcceptVersionsType(acceptVersions);
        this.sections       = new SectionsType("All");
        this.updateSequence = null;
    }
    
    /**
     * Gets the value of the acceptVersions property.
     */
    public AcceptVersionsType getAcceptVersions() {
        return acceptVersions;
    }

    /**
     * Gets the value of the sections property.
     */
    public SectionsType getSections() {
        return sections;
    }

    /**
     * Return true if the request contains the specified section.
     *
     * @param sectionName The name of the searched section.
     * @return true if the request contains the specified section.
     */
    public boolean containsSection(String sectionName) {
        if (sections != null) {
            return sections.containsSection(sectionName);
        }
        return false;
    }

   /**
    * Gets the value of the acceptFormats property.
    */
    public AcceptFormatsType getAcceptFormats() {
        return acceptFormats;
    }

    /**
    * Return the first outputFormat of the is if there is one
    */
    public String getFirstAcceptFormat() {
        if (acceptFormats != null) {
            if (acceptFormats.getOutputFormat().size() > 0) {
                return acceptFormats.getOutputFormat().get(0);
            }
        }
        return null;
    }
    
    /**
     * Gets the value of the updateSequence property.
     */
    public String getUpdateSequence() {
        return updateSequence;
    }

    
    
    /**
     * Verify that this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof GetCapabilitiesType) {
        final GetCapabilitiesType that = (GetCapabilitiesType) object;
        return Utilities.equals(this.acceptFormats,  that.acceptFormats)  &&
               Utilities.equals(this.acceptVersions, that.acceptVersions) &&
               Utilities.equals(this.sections,       that.sections)       &&
               Utilities.equals(this.updateSequence, that.updateSequence);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 73 * hash + (this.acceptVersions != null ? this.acceptVersions.hashCode() : 0);
        hash = 73 * hash + (this.sections != null ? this.sections.hashCode() : 0);
        hash = 73 * hash + (this.acceptFormats != null ? this.acceptFormats.hashCode() : 0);
        hash = 73 * hash + (this.updateSequence != null ? this.updateSequence.hashCode() : 0);
        return hash;
    }

    /**
     * inherited method from AbstractGetCapabilties
     */
    @Override
    public Version getVersion() {
        if (acceptVersions!= null && acceptVersions.getVersion().size()!= 0) {
            return new Version(acceptVersions.getVersion().get(0));
        } return null;
    }

}
