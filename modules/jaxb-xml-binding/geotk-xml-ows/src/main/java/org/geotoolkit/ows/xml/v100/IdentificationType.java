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
package org.geotoolkit.ows.xml.v100;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * General metadata identifying and describing a set of data. This type shall be extended if needed for each specific OWS to include additional metadata for each type of dataset. If needed, this type should first be restricted for each specific OWS to change the multiplicity (or optionality) of some elements. 
 * 
 * <p>Java class for IdentificationType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="IdentificationType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/ows}DescriptionType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/ows}Identifier" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/ows}BoundingBox" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/ows}OutputFormat" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/ows}AvailableCRS" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/ows}Metadata" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "IdentificationType", propOrder = {
    "identifier",
    "boundingBox",
    "outputFormat",
    "availableCRS",
    "metadata"
})
public class IdentificationType extends DescriptionType {

    @XmlElement(name = "Identifier")
    private CodeType identifier;
    @XmlElementRef(name = "BoundingBox", namespace = "http://www.opengis.net/ows", type = JAXBElement.class)
    private List<JAXBElement<? extends BoundingBoxType>> boundingBox;
    @XmlElement(name = "OutputFormat")
    private List<String> outputFormat;
    @XmlElementRef(name = "AvailableCRS", namespace = "http://www.opengis.net/ows", type = JAXBElement.class)
    private List<JAXBElement<String>> availableCRS;
    @XmlElement(name = "Metadata")
    private List<MetadataType> metadata;

    /**
     * Optional unique identifier or name of this dataset. 
     */
    public CodeType getIdentifier() {
        return identifier;
    }

    /**
     * Unordered list of zero or more bounding boxes whose union describes the extent of this dataset. 
     * Gets the value of the boundingBox property.
     */
    public List<JAXBElement<? extends BoundingBoxType>> getBoundingBox() {
        if (boundingBox == null) {
            boundingBox = new ArrayList<JAXBElement<? extends BoundingBoxType>>();
        }
        return this.boundingBox;
    }

    /**
     * Unordered list of zero or more references to data formats supported for server outputs.
     * Gets the value of the outputFormat property.
     * 
     */
    public List<String> getOutputFormat() {
        if (outputFormat == null) {
            outputFormat = new ArrayList<String>();
        }
        return this.outputFormat;
    }

    /**
     * Unordered list of zero or more available coordinate reference systems. 
     * Gets the value of the availableCRS property.
     * 
     */
    public List<JAXBElement<String>> getAvailableCRS() {
        if (availableCRS == null) {
            availableCRS = new ArrayList<JAXBElement<String>>();
        }
        return this.availableCRS;
    }

    /**
     * Optional unordered list of additional metadata about this data(set). 
     * A list of optional metadata elements for this data identification 
     * could be specified in the Implementation Specification for this service. 
     * Gets the value of the metadata property.
     * 
     */
    public List<MetadataType> getMetadata() {
        if (metadata == null) {
            metadata = new ArrayList<MetadataType>();
        }
        return this.metadata;
    }

}
