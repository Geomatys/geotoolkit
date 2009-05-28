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
package org.geotoolkit.wcs.xml.v111;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.ows.xml.v110.OnlineResourceType;


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
 *         &lt;element ref="{http://www.opengis.net/wcs}CoverageSummary" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="SupportedCRS" type="{http://www.w3.org/2001/XMLSchema}anyURI" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="SupportedFormat" type="{http://www.opengis.net/ows/1.1}MimeType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="OtherSource" type="{http://www.opengis.net/ows/1.1}OnlineResourceType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * @author Guilhem Legal
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "coverageSummary",
    "supportedCRS",
    "supportedFormat",
    "otherSource"
})
@XmlRootElement(name = "Contents")
public class Contents {

    @XmlElement(name = "CoverageSummary")
    private List<CoverageSummaryType> coverageSummary  = new ArrayList<CoverageSummaryType>();
    @XmlElement(name = "SupportedCRS")
    @XmlSchemaType(name = "anyURI")
    private List<String> supportedCRS = new ArrayList<String>();
    @XmlElement(name = "SupportedFormat")
    private List<String> supportedFormat = new ArrayList<String>();
    @XmlElement(name = "OtherSource")
    private List<OnlineResourceType> otherSource = new ArrayList<OnlineResourceType>();

    /**
     * empty constructor used by JAXB.
     */
    Contents(){
    }
    
    /**
     * Build the contents party of a Capabilities document.
     */
    public Contents(List<CoverageSummaryType> coverageSummary,  List<String> supportedCRS,
            List<String> supportedFormat, List<OnlineResourceType> otherSource) {
        this.coverageSummary = coverageSummary;
        this.otherSource     = otherSource;
        this.supportedCRS    = supportedCRS;
        this.supportedFormat = supportedFormat;
        
    }
    
    /**
     * Unordered list of brief metadata describing top-level coverages available from this WCS server. This list shall be included unless one or more OtherSources are referenced and all this metadata is available from those sources. Gets the value of the coverageSummary property.
     */
    public List<CoverageSummaryType> getCoverageSummary() {
        return Collections.unmodifiableList(this.coverageSummary);
    }

    /**
     * Gets the value of the supportedCRS property.
     */
    public List<String> getSupportedCRS() {
        return Collections.unmodifiableList(this.supportedCRS);
    }

    /**
     * Gets the value of the supportedFormat property.
     */
    public List<String> getSupportedFormat() {
        return Collections.unmodifiableList(this.supportedFormat);
    }

    /**
     * Gets the value of the otherSource property.
     */
    public List<OnlineResourceType> getOtherSource() {
        return Collections.unmodifiableList(this.otherSource);
    }

}
