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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * Contents of typical Contents section of an OWS service
 *       metadata (Capabilities) document. This type shall be extended and/or
 *       restricted if needed for specific OWS use to include the specific
 *       metadata needed.
 * 
 * <p>Java class for ContentsBaseType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ContentsBaseType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/ows/2.0}DatasetDescriptionSummary" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/ows/2.0}OtherSource" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ContentsBaseType", propOrder = {
    "datasetDescriptionSummary",
    "otherSource"
})
/*@XmlSeeAlso({
    ContentsType.class
})*/
public class ContentsBaseType {

    @XmlElement(name = "DatasetDescriptionSummary")
    private List<DatasetDescriptionSummaryBaseType> datasetDescriptionSummary;
    @XmlElement(name = "OtherSource")
    private List<MetadataType> otherSource;

    /**
     * Unordered set of summary descriptions for the
     *           datasets available from this OWS server. This set shall be included
     *           unless another source is referenced and all this metadata is
     *           available from that source.Gets the value of the datasetDescriptionSummary property.
     * 
     */
    public List<DatasetDescriptionSummaryBaseType> getDatasetDescriptionSummary() {
        if (datasetDescriptionSummary == null) {
            datasetDescriptionSummary = new ArrayList<DatasetDescriptionSummaryBaseType>();
        }
        return this.datasetDescriptionSummary;
    }

    /**
     * Unordered set of references to other sources of
     *           metadata describing the coverage offerings available from this
     *           server.Gets the value of the otherSource property.
     * 
     */
    public List<MetadataType> getOtherSource() {
        if (otherSource == null) {
            otherSource = new ArrayList<MetadataType>();
        }
        return this.otherSource;
    }

}
