/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2015, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

package org.geotoolkit.thw.xml;

import java.net.URI;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getAllTranslationsForConcept complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getAllTranslationsForConcept">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="concept_uri" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="property_uri" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getAllTranslationsForConcept", propOrder = {
    "conceptUri",
    "propertyUri"
})
@XmlRootElement(name = "GetAllTranslationsForConcept", namespace = "http://ws.geotk.org/")
public class GetAllTranslationsForConcept {

    @XmlElement(name = "concept_uri")
    private String conceptUri;
    @XmlElement(name = "property_uri")
    private String propertyUri;
    private String outputFormat;

    public GetAllTranslationsForConcept() {

    }

    public GetAllTranslationsForConcept(final String conceptUri, final String propertyUri, final String outputFormat) {
        this.conceptUri   = conceptUri;
        this.propertyUri  = propertyUri;
        this.outputFormat = outputFormat;
    }

    public GetAllTranslationsForConcept(URI conceptUri, String propertyUri) {
        if (conceptUri != null) {
            this.conceptUri  = conceptUri.toString();
        }
        this.propertyUri = propertyUri;
    }

    /**
     * Gets the value of the conceptUri property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getConceptUri() {
        return conceptUri;
    }

    /**
     * Sets the value of the conceptUri property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setConceptUri(String value) {
        this.conceptUri = value;
    }

    /**
     * Gets the value of the propertyUri property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPropertyUri() {
        return propertyUri;
    }

    /**
     * Sets the value of the propertyUri property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPropertyUri(String value) {
        this.propertyUri = value;
    }

    /**
     * @return the outputFormat
     */
    public String getOutputFormat() {
        return outputFormat;
    }

    /**
     * @param outputFormat the outputFormat to set
     */
    public void setOutputFormat(String outputFormat) {
        this.outputFormat = outputFormat;
    }

}
