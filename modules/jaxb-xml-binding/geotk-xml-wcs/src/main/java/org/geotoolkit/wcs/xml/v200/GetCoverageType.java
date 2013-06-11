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

package org.geotoolkit.wcs.xml.v200;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for GetCoverageType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetCoverageType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/wcs/2.0}RequestBaseType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/wcs/2.0}CoverageId"/>
 *         &lt;element ref="{http://www.opengis.net/wcs/2.0}DimensionSubset" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="format" type="{http://www.w3.org/2001/XMLSchema}anyURI" minOccurs="0"/>
 *         &lt;element name="mediaType" type="{http://www.w3.org/2001/XMLSchema}anyURI" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetCoverageType", propOrder = {
    "coverageId",
    "dimensionSubset",
    "format",
    "mediaType"
})
public class GetCoverageType extends RequestBaseType {

    @XmlElement(name = "CoverageId", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    private String coverageId;
    @XmlElementRef(name = "DimensionSubset", namespace = "http://www.opengis.net/wcs/2.0", type = JAXBElement.class)
    private List<JAXBElement<? extends DimensionSubsetType>> dimensionSubset;
    @XmlSchemaType(name = "anyURI")
    private String format;
    @XmlSchemaType(name = "anyURI")
    private String mediaType;

    /**
     * Identifier of the coverage that this GetCoverage operation request shall draw from. 
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCoverageId() {
        return coverageId;
    }

    /**
     * Sets the value of the coverageId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCoverageId(String value) {
        this.coverageId = value;
    }

    /**
     * Gets the value of the dimensionSubset property.
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link DimensionTrimType }{@code >}
     * {@link JAXBElement }{@code <}{@link DimensionSubsetType }{@code >}
     * {@link JAXBElement }{@code <}{@link DimensionSliceType }{@code >}
     * 
     * 
     */
    public List<JAXBElement<? extends DimensionSubsetType>> getDimensionSubset() {
        if (dimensionSubset == null) {
            dimensionSubset = new ArrayList<JAXBElement<? extends DimensionSubsetType>>();
        }
        return this.dimensionSubset;
    }

    /**
     * Gets the value of the format property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFormat() {
        return format;
    }

    /**
     * Sets the value of the format property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFormat(String value) {
        this.format = value;
    }

    /**
     * Gets the value of the mediaType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMediaType() {
        return mediaType;
    }

    /**
     * Sets the value of the mediaType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMediaType(String value) {
        this.mediaType = value;
    }

}
