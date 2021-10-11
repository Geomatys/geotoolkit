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

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.apache.sis.referencing.CRS;
import org.geotoolkit.wcs.xml.DomainSubset;
import org.geotoolkit.wcs.xml.GetCoverage;
import org.geotoolkit.wcs.xml.InterpolationMethod;
import org.geotoolkit.wcs.xml.RangeSubset;
import org.geotoolkit.wcs.xml.v200.crs.CrsType;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.FactoryException;


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
@XmlRootElement(name = "GetCoverage")
public class GetCoverageType extends RequestBaseType implements GetCoverage {

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

    public GetCoverageType() {
        this.service = "WCS";
        this.version = "2.0.1";
    }

    public GetCoverageType(final String coverageId, final String format, final String mediaType) {
        this.coverageId = coverageId;
        this.format     = format;
        this.mediaType  = mediaType;
        this.service    = "WCS";
        this.version    = "2.0.1";
    }

    public GetCoverageType(final String coverageId, final String format, final String mediaType, final String subsettingCrs, final String outputCrs) {
        this.coverageId = coverageId;
        this.format     = format;
        this.mediaType  = mediaType;
        this.service    = "WCS";
        this.version    = "2.0.1";
        if (subsettingCrs != null || outputCrs != null) {
            this.extension = new ExtensionType(new CrsType(subsettingCrs, outputCrs));
        }
    }

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
            dimensionSubset = new ArrayList<>();
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
    @Override
    public String getFormat() {
        if (format == null) {
            return "application/gml+xml";
        }
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
    @Override
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

    @Override
    public CoordinateReferenceSystem getCRS() throws FactoryException {
        if (extension != null) {
            CrsType crs = extension.getForClass(CrsType.class);
            if (crs != null && crs.getSubsettingCrs()!= null) {
                return CRS.forCode(crs.getSubsettingCrs());
            }
        }
        return null;
    }

    @Override
    public String getCoverage() {
        return coverageId;
    }

    @Override
    public Envelope getEnvelope() throws FactoryException {
        return null;
    }

    @Override
    public CoordinateReferenceSystem getResponseCRS() throws FactoryException {
        if (extension != null) {
            CrsType crs = extension.getForClass(CrsType.class);
            if (crs != null && crs.getOutputCrs() != null) {
                return CRS.forCode(crs.getOutputCrs());
            }
        }
        return null;
    }

    @Override
    public Dimension getSize() {
        return null;
    }

    @Override
    public List<Double> getResolutions() {
        return null;
    }

    @Override
    public String getTime() {
        return null;
    }

    @Override
    public RangeSubset getRangeSubset() {
        return null;
    }

    @Override
    public List<DomainSubset> getDomainSubset() {
        final List<DomainSubset> result = new ArrayList<>();
        if (dimensionSubset != null) {
            for (JAXBElement<? extends DimensionSubsetType> jb : dimensionSubset) {
                result.add(jb.getValue());
            }
        }
        return result;
    }

    @Override
    public String toKvp() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public InterpolationMethod getInterpolationMethod() {
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof GetCoverageType && super.equals(o)) {
            final GetCoverageType that = (GetCoverageType) o;
            boolean dimEquals = false;
            if (this.dimensionSubset != null && that.dimensionSubset != null) {
                if (this.dimensionSubset.size() == that.dimensionSubset.size()) {
                    dimEquals = true;
                    for (int i = 0; i < this.dimensionSubset.size(); i++) {
                        DomainSubset thisD = this.dimensionSubset.get(i).getValue();
                        DomainSubset thatD = that.dimensionSubset.get(i).getValue();
                        if (!Objects.equals(thisD, thatD)) {
                            return false;
                        }
                    }
                }
            } else if (this.dimensionSubset == null && that.dimensionSubset == null) {
                dimEquals = true;
            }
            return Objects.equals(this.coverageId, that.coverageId) &&
                   Objects.equals(this.format,     that.format) &&
                   Objects.equals(this.mediaType,  that.mediaType) &&
                   dimEquals;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 41 * hash + java.util.Objects.hashCode(this.coverageId);
        hash = 41 * hash + java.util.Objects.hashCode(this.dimensionSubset);
        hash = 41 * hash + java.util.Objects.hashCode(this.format);
        hash = 41 * hash + java.util.Objects.hashCode(this.mediaType);
        return hash;
    }
}
