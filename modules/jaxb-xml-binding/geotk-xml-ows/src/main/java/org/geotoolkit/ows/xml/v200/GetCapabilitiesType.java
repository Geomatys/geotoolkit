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
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.apache.sis.util.Version;
import org.geotoolkit.ows.xml.AbstractGetCapabilities;


/**
 * XML encoded GetCapabilities operation request. This
 *       operation allows clients to retrieve service metadata about a specific
 *       service instance. In this XML encoding, no "request" parameter is
 *       included, since the element name specifies the specific operation. This
 *       base type shall be extended by each specific OWS to include the
 *       additional required "service" attribute, with the correct value for that
 *       OWS.
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
 *         &lt;element name="AcceptVersions" type="{http://www.opengis.net/ows/2.0}AcceptVersionsType" minOccurs="0"/>
 *         &lt;element name="Sections" type="{http://www.opengis.net/ows/2.0}SectionsType" minOccurs="0"/>
 *         &lt;element name="AcceptFormats" type="{http://www.opengis.net/ows/2.0}AcceptFormatsType" minOccurs="0"/>
 *         &lt;element name="AcceptLanguages" minOccurs="0">
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
 *       &lt;attribute name="updateSequence" type="{http://www.opengis.net/ows/2.0}UpdateSequenceType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetCapabilitiesType", propOrder = {
    "acceptVersions",
    "sections",
    "acceptFormats",
    "acceptLanguages"
})
/*@XmlSeeAlso({
    net.opengis.wcs._2.GetCapabilitiesType.class
})*/
public class GetCapabilitiesType implements AbstractGetCapabilities {

    @XmlElement(name = "AcceptVersions")
    private AcceptVersionsType acceptVersions;
    @XmlElement(name = "Sections")
    private SectionsType sections;
    @XmlElement(name = "AcceptFormats")
    private AcceptFormatsType acceptFormats;
    @XmlElement(name = "AcceptLanguages")
    private GetCapabilitiesType.AcceptLanguages acceptLanguages;
    @XmlAttribute
    private String updateSequence;

    @XmlAttribute(required = true)
    private String service;
    
    /**
     * Empty constructor used by JAXB.
     */
    public GetCapabilitiesType(){ 
    }

    /**
     */
    public GetCapabilitiesType(final String service){
        this.service = service;
    }
    
    /**
     * Build a new GetCapabilities base request.
     */
    public GetCapabilitiesType(final AcceptVersionsType acceptVersions, final SectionsType sections,
            final AcceptFormatsType acceptFormats, final String updateSequence, final String service){
        this.acceptFormats  = acceptFormats;
        this.acceptVersions = acceptVersions;
        this.sections       = sections;
        this.updateSequence = updateSequence;
        this.service        = service;
    }


    /**
     * Build a new GetCapabilities base request.
     */
    public GetCapabilitiesType(final String acceptVersions, final String acceptFormats, final String service){
        this.acceptFormats  = new AcceptFormatsType(acceptFormats);
        this.acceptVersions = new AcceptVersionsType(acceptVersions);
        this.sections       = new SectionsType("All");
        this.updateSequence = null;
        this.service        = service;
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
    public void setAcceptVersions(AcceptVersionsType value) {
        this.acceptVersions = value;
    }
    
    /**
     * inherited method from AbstractGetCapabilties
     */
    @Override
    public Version getVersion() {
        if (acceptVersions!= null && !acceptVersions.getVersion().isEmpty()) {
            return new Version(acceptVersions.getVersion().get(0));
        } return null;
    }
    
    @Override
    public void setVersion(final String version) {
        if (version != null) {
            if (acceptVersions == null) {
                this.acceptVersions = new AcceptVersionsType(version);
            } else {
                 this.acceptVersions.addFirstVersion(version);
            }
        }
    }

    /**
     * Gets the value of the sections property.
     * 
     * @return
     *     possible object is
     *     {@link SectionsType }
     *     
     */
    @Override
    public SectionsType getSections() {
        return sections;
    }

    /**
     * Sets the value of the sections property.
     * 
     * @param value
     *     allowed object is
     *     {@link SectionsType }
     *     
     */
    public void setSections(SectionsType value) {
        this.sections = value;
    }

    /**
     * Return true if the request contains the specified section.
     *
     * @param sectionName The name of the searched section.
     * @return true if the request contains the specified section.
     */
    @Override
    public boolean containsSection(final String sectionName) {
        if (sections != null) {
            return sections.containsSection(sectionName);
        }
        return false;
    }
    
    /**
     * Gets the value of the acceptFormats property.
     * 
     * @return
     *     possible object is
     *     {@link AcceptFormatsType }
     *     
     */
    @Override
    public AcceptFormatsType getAcceptFormats() {
        return acceptFormats;
    }

    /**
    * Return the first outputFormat of the is if there is one
    */
    @Override
    public String getFirstAcceptFormat() {
        if (acceptFormats != null) {
            if (acceptFormats.getOutputFormat().size() > 0) {
                return acceptFormats.getOutputFormat().get(0);
            }
        }
        return null;
    }
    
    /**
     * Sets the value of the acceptFormats property.
     * 
     * @param value
     *     allowed object is
     *     {@link AcceptFormatsType }
     *     
     */
    public void setAcceptFormats(AcceptFormatsType value) {
        this.acceptFormats = value;
    }

    /**
     * Gets the value of the acceptLanguages property.
     * 
     * @return
     *     possible object is
     *     {@link GetCapabilitiesType.AcceptLanguages }
     *     
     */
    public GetCapabilitiesType.AcceptLanguages getAcceptLanguages() {
        return acceptLanguages;
    }

    /**
     * Sets the value of the acceptLanguages property.
     * 
     * @param value
     *     allowed object is
     *     {@link GetCapabilitiesType.AcceptLanguages }
     *     
     */
    public void setAcceptLanguages(GetCapabilitiesType.AcceptLanguages value) {
        this.acceptLanguages = value;
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
     * Gets the value of the service property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Override
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
    @Override
    public void setService(String value) {
        this.service = value;
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
    public static class AcceptLanguages {

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
