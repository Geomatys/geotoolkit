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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getAvailableLanguages complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="getAvailableLanguages">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="concept" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getAvailableLanguages", propOrder = {
    "concept",
    "outputFormat"
})
@XmlRootElement(name = "GetAvailableLanguages", namespace = "http://ws.geotk.org/")
public class GetAvailableLanguages {

    private String concept;
    private String outputFormat;

    public GetAvailableLanguages() {

    }

    public GetAvailableLanguages(final String concept, String outputFormat) {
        this.concept = concept;
        this.outputFormat = outputFormat;
    }

    public GetAvailableLanguages(final URI concept) {
        if (concept != null) {
            this.concept = concept.toString();
        }
    }

    /**
     * Gets the value of the concept property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getConcept() {
        return concept;
    }

    /**
     * Sets the value of the concept property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setConcept(String value) {
        this.concept = value;
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
