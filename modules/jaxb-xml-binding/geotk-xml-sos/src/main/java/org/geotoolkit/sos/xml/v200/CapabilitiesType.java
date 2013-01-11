/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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

package org.geotoolkit.sos.xml.v200;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.ows.xml.AbstractCapabilitiesCore;
import org.geotoolkit.ows.xml.Sections;
import org.geotoolkit.ows.xml.v110.CapabilitiesBaseType;
import org.geotoolkit.ows.xml.v110.OperationsMetadata;
import org.geotoolkit.ows.xml.v110.ServiceIdentification;
import org.geotoolkit.ows.xml.v110.ServiceProvider;
import org.geotoolkit.sos.xml.Capabilities;
import org.geotoolkit.sos.xml.SOSResponse;


/**
 * <p>Java class for CapabilitiesType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CapabilitiesType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/ows/1.1}CapabilitiesBaseType">
 *       &lt;sequence>
 *         &lt;element name="extension" type="{http://www.w3.org/2001/XMLSchema}anyType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="filterCapabilities" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://www.opengis.net/fes/2.0}Filter_Capabilities"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="contents" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://www.opengis.net/sos/2.0}Contents"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CapabilitiesType", propOrder = {
    "extension",
    "filterCapabilities",
    "contents"
})
public class CapabilitiesType extends CapabilitiesBaseType implements Capabilities, SOSResponse {

    private List<Object> extension;
    private FilterCapabilities filterCapabilities;
    private CapabilitiesType.Contents contents;

    /**
     * An empty constructor used by JAXB
     */
    public CapabilitiesType() {}
    
    public CapabilitiesType(final String version, final String updateSequence) {
        super(version, updateSequence);
                    
    }
    
    public CapabilitiesType(final ServiceIdentification serviceIdentification, final ServiceProvider serviceProvider,
            final OperationsMetadata operationsMetadata, final String version, final String updateSequence, final FilterCapabilities filterCapabilities,
            final Contents contents) {
        super(serviceIdentification, serviceProvider, operationsMetadata, version, updateSequence);
        this.contents           = contents;
        this.filterCapabilities = filterCapabilities;
                    
    }
    
    /**
     * Gets the value of the extension property.
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     * 
     * 
     */
    public List<Object> getExtension() {
        if (extension == null) {
            extension = new ArrayList<Object>();
        }
        return this.extension;
    }

    /**
     * Gets the value of the filterCapabilities property.
     * 
     * @return
     *     possible object is
     *     {@link CapabilitiesType.FilterCapabilities }
     *     
     */
    @Override
    public FilterCapabilities getFilterCapabilities() {
        return filterCapabilities;
    }

    /**
     * Sets the value of the filterCapabilities property.
     * 
     * @param value
     *     allowed object is
     *     {@link CapabilitiesType.FilterCapabilities }
     *     
     */
    public void setFilterCapabilities(FilterCapabilities value) {
        this.filterCapabilities = value;
    }

    /**
     * Gets the value of the contents property.
     * 
     * @return
     *     possible object is
     *     {@link CapabilitiesType.Contents }
     *     
     */
    @Override
    public CapabilitiesType.Contents getContents() {
        return contents;
    }

    /**
     * Sets the value of the contents property.
     * 
     * @param value
     *     allowed object is
     *     {@link CapabilitiesType.Contents }
     *     
     */
    public void setContents(CapabilitiesType.Contents value) {
        this.contents = value;
    }


    @Override
    public AbstractCapabilitiesCore applySections(final Sections sections) {
        //we prepare the different parts response document
        ServiceIdentification si = null;
        ServiceProvider       sp = null;
        OperationsMetadata    om = null;
        FilterCapabilities    fc = null;
        Contents            cont = null;

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

        //we enter the information filter capablities.
        if (sections.containsSection("Filter_Capabilities") || sections.containsSection("All")) {
            fc = filterCapabilities;
        }

        if (sections.containsSection("Contents") || sections.containsSection("All")) {
            cont = contents;
        }
        // we build and normalize the document
        return new CapabilitiesType(si, sp, om, "2.0.0", getUpdateSequence(), fc, cont);
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
     *         &lt;element ref="{http://www.opengis.net/sos/2.0}Contents"/>
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
        "contents"
    })
    public static class Contents implements org.geotoolkit.sos.xml.Contents {

        @XmlElement(name = "Contents", required = true)
        private ContentsType contents;

        /**
         * Gets the value of the contents property.
         * 
         * @return
         *     possible object is
         *     {@link ContentsType }
         *     
         */
        public ContentsType getContents() {
            return contents;
        }

        /**
         * Sets the value of the contents property.
         * 
         * @param value
         *     allowed object is
         *     {@link ContentsType }
         *     
         */
        public void setContents(ContentsType value) {
            this.contents = value;
        }

        @Override
        public List<ObservationOfferingType> getOfferings() {
            if (contents != null) {
                return this.contents.getOfferings();
            }
            return new ArrayList<ObservationOfferingType>();
        }
    }
}
