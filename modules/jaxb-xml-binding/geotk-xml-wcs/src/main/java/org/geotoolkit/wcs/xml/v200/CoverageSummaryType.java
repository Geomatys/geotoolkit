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
import javax.xml.namespace.QName;
import org.geotoolkit.ows.xml.v200.BoundingBoxType;
import org.geotoolkit.ows.xml.v200.DescriptionType;
import org.geotoolkit.ows.xml.v200.MetadataType;
import org.geotoolkit.ows.xml.v200.WGS84BoundingBoxType;
import org.geotoolkit.wcs.xml.CoverageInfo;
import org.opengis.geometry.Envelope;


/**
 * <p>Java class for CoverageSummaryType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CoverageSummaryType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/ows/2.0}DescriptionType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/ows/2.0}WGS84BoundingBox" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/wcs/2.0}CoverageId"/>
 *         &lt;element ref="{http://www.opengis.net/wcs/2.0}CoverageSubtype"/>
 *         &lt;element ref="{http://www.opengis.net/wcs/2.0}CoverageSubtypeParent" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/ows/2.0}BoundingBox" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/ows/2.0}Metadata" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CoverageSummaryType", propOrder = {
    "wgs84BoundingBox",
    "coverageId",
    "coverageSubtype",
    "coverageSubtypeParent",
    "boundingBox",
    "metadata"
})
public class CoverageSummaryType extends DescriptionType  implements CoverageInfo{

    @XmlElement(name = "WGS84BoundingBox", namespace = "http://www.opengis.net/ows/2.0")
    private List<WGS84BoundingBoxType> wgs84BoundingBox;
    @XmlElement(name = "CoverageId", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    private String coverageId;
    @XmlElement(name = "CoverageSubtype", required = true)
    private QName coverageSubtype;
    @XmlElement(name = "CoverageSubtypeParent")
    private CoverageSubtypeParentType coverageSubtypeParent;
    @XmlElementRef(name = "BoundingBox", namespace = "http://www.opengis.net/ows/2.0", type = JAXBElement.class)
    private List<JAXBElement<? extends BoundingBoxType>> boundingBox;
    @XmlElementRef(name = "Metadata", namespace = "http://www.opengis.net/ows/2.0", type = JAXBElement.class)
    private List<JAXBElement<? extends MetadataType>> metadata;

    public CoverageSummaryType() {
        
    }
    
    public CoverageSummaryType(final String identifier, final String title, final String _abstract, final WGS84BoundingBoxType bbox, 
            final QName coverageSubtype) {
        super(title, _abstract, null);
        if (bbox != null) {
            this.wgs84BoundingBox = new ArrayList<>();
            this.wgs84BoundingBox.add(bbox);
        }
        this.coverageId = identifier;
        this.coverageSubtype = coverageSubtype;
                
    }
    /**
     * Gets the value of the wgs84BoundingBox property.
     */
    public List<WGS84BoundingBoxType> getWGS84BoundingBox() {
        if (wgs84BoundingBox == null) {
            wgs84BoundingBox = new ArrayList<>();
        }
        return this.wgs84BoundingBox;
    }

    /**
     * Gets the value of the coverageId property.
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
     * Gets the value of the coverageSubtype property.
     * 
     * @return
     *     possible object is
     *     {@link QName }
     *     
     */
    public QName getCoverageSubtype() {
        return coverageSubtype;
    }

    /**
     * Sets the value of the coverageSubtype property.
     * 
     * @param value
     *     allowed object is
     *     {@link QName }
     *     
     */
    public void setCoverageSubtype(QName value) {
        this.coverageSubtype = value;
    }

    /**
     * Gets the value of the coverageSubtypeParent property.
     * 
     * @return
     *     possible object is
     *     {@link CoverageSubtypeParentType }
     *     
     */
    public CoverageSubtypeParentType getCoverageSubtypeParent() {
        return coverageSubtypeParent;
    }

    /**
     * Sets the value of the coverageSubtypeParent property.
     * 
     * @param value
     *     allowed object is
     *     {@link CoverageSubtypeParentType }
     *     
     */
    public void setCoverageSubtypeParent(CoverageSubtypeParentType value) {
        this.coverageSubtypeParent = value;
    }

    /**
     * Gets the value of the boundingBox property.
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link BoundingBoxType }{@code >}
     * {@link JAXBElement }{@code <}{@link WGS84BoundingBoxType }{@code >}
     * 
     * 
     */
    public List<JAXBElement<? extends BoundingBoxType>> getBoundingBox() {
        if (boundingBox == null) {
            boundingBox = new ArrayList<>();
        }
        return this.boundingBox;
    }

    /**
     * Gets the value of the metadata property.
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link MetadataType }{@code >}
     * {@link JAXBElement }{@code <}{@link AdditionalParametersType }{@code >}
     * 
     * 
     */
    public List<JAXBElement<? extends MetadataType>> getMetadata() {
        if (metadata == null) {
            metadata = new ArrayList<>();
        }
        return this.metadata;
    }

    @Override
    public void setMetadata(final String href) {
        if (href != null) {
            this.metadata = new ArrayList<>();
            final org.geotoolkit.ows.xml.v200.ObjectFactory factory = new org.geotoolkit.ows.xml.v200.ObjectFactory();
            
            this.metadata.add(factory.createMetadata(new MetadataType(href)));
        }
    }
    
    @Override
    public Envelope getLonLatEnvelope() {
        //TODO
        return null;
    }

    @Override
    public List<?> getRest() {
        //TODO
        return null;
    }

}
