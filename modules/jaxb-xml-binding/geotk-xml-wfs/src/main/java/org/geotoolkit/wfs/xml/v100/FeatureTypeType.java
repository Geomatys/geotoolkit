/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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
package org.geotoolkit.wfs.xml.v100;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;
import org.geotoolkit.util.Utilities;
import org.geotoolkit.wfs.xml.FeatureType;


/**
 * <p>Java class for FeatureTypeType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FeatureTypeType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Name" type="{http://www.w3.org/2001/XMLSchema}QName"/>
 *         &lt;element ref="{http://www.opengis.net/wfs}Title" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/wfs}Abstract" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/wfs}Keywords" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/wfs}SRS"/>
 *         &lt;element name="Operations" type="{http://www.opengis.net/wfs}OperationsType" minOccurs="0"/>
 *         &lt;element name="LatLongBoundingBox" type="{http://www.opengis.net/wfs}LatLongBoundingBoxType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="MetadataURL" type="{http://www.opengis.net/wfs}MetadataURLType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FeatureTypeType", propOrder = {
    "name",
    "title",
    "_abstract",
    "keywords",
    "srs",
    "operations",
    "latLongBoundingBox",
    "metadataURL"
})
public class FeatureTypeType implements FeatureType {

    @XmlElement(name = "Name", required = true)
    private QName name;
    @XmlElement(name = "Title")
    private String title;
    @XmlElement(name = "Abstract")
    private String _abstract;
    @XmlElement(name = "Keywords")
    private String keywords;
    @XmlElement(name = "SRS", required = true)
    private String srs;
    @XmlElement(name = "Operations")
    private OperationsType operations;
    @XmlElement(name = "LatLongBoundingBox")
    private List<LatLongBoundingBoxType> latLongBoundingBox;
    @XmlElement(name = "MetadataURL")
    private List<MetadataURLType> metadataURL;

    public FeatureTypeType() {

    }

    public FeatureTypeType(final QName name, final String title, final String defaultSRS, final List<LatLongBoundingBoxType> wgs84BoundingBox) {
        this.name       = name;
        this.title      = title;
        this.srs        = defaultSRS;
        this.latLongBoundingBox = wgs84BoundingBox;
    }
    
    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link QName }
     *     
     */
    public QName getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link QName }
     *     
     */
    public void setName(QName value) {
        this.name = value;
    }

    /**
     * Gets the value of the title property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the value of the title property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTitle(String value) {
        this.title = value;
    }

    /**
     * Gets the value of the abstract property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAbstract() {
        return _abstract;
    }

    /**
     * Sets the value of the abstract property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Override
    public void setAbstract(String value) {
        this._abstract = value;
    }

    /**
     * Gets the value of the keywords property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKeywords() {
        return keywords;
    }

    /**
     * Sets the value of the keywords property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKeywords(final String value) {
        this.keywords = value;
    }
    
    @Override
    public void addKeywords(final List<String> values) {
        if (values != null && !values.isEmpty())
        this.keywords = values.get(0);
    }

    /**
     * Gets the value of the srs property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSRS() {
        return srs;
    }

    /**
     * Sets the value of the srs property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Override
    public void setDefaultCRS(String value) {
        this.srs = value;
    }

    @Override
    public void setOtherCRS(final List<String> otherCRS) {
        // do nothing
    }
    /**
     * Gets the value of the operations property.
     * 
     * @return
     *     possible object is
     *     {@link OperationsType }
     *     
     */
    public OperationsType getOperations() {
        return operations;
    }

    /**
     * Sets the value of the operations property.
     * 
     * @param value
     *     allowed object is
     *     {@link OperationsType }
     *     
     */
    public void setOperations(OperationsType value) {
        this.operations = value;
    }

    /**
     * Gets the value of the latLongBoundingBox property.
     * 
     */
    public List<LatLongBoundingBoxType> getLatLongBoundingBox() {
        if (latLongBoundingBox == null) {
            latLongBoundingBox = new ArrayList<LatLongBoundingBoxType>();
        }
        return this.latLongBoundingBox;
    }

    /**
     * Gets the value of the metadataURL property.
     * 
     */
    public List<MetadataURLType> getMetadataURL() {
        if (metadataURL == null) {
            metadataURL = new ArrayList<MetadataURLType>();
        }
        return this.metadataURL;
    }
    
    @Override
    public void addMetadataURL(final String value, final String type, final String format) {
        getMetadataURL().add(new MetadataURLType(value, type, format));
    }
    
    /**
     * Verify if this entry is identical to specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof FeatureTypeType) {
            final FeatureTypeType that = (FeatureTypeType) object;

            return Objects.equals(this._abstract, that._abstract) &&
                   Objects.equals(this.srs, that.srs) &&
                   Objects.equals(this.keywords, that.keywords) &&
                   Objects.equals(this.metadataURL, that.metadataURL) &&
                   Objects.equals(this.name, that.name) &&
                   Objects.equals(this.operations, that.operations) &&
                   Objects.equals(this.title, that.title) &&
                   Objects.equals(this.latLongBoundingBox, that.latLongBoundingBox);
            }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 79 * hash + (this.title != null ? this.title.hashCode() : 0);
        hash = 79 * hash + (this._abstract != null ? this._abstract.hashCode() : 0);
        hash = 79 * hash + (this.keywords != null ? this.keywords.hashCode() : 0);
        hash = 79 * hash + (this.srs != null ? this.srs.hashCode() : 0);
        hash = 79 * hash + (this.operations != null ? this.operations.hashCode() : 0);
        hash = 79 * hash + (this.latLongBoundingBox != null ? this.latLongBoundingBox.hashCode() : 0);
        hash = 79 * hash + (this.metadataURL != null ? this.metadataURL.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("[featureTypeType]\n");
        if(name != null) {
            s.append("name:").append(name).append('\n');
        }
        if (title != null)
            s.append("title:").append(title).append('\n');
        if (_abstract != null)
            s.append("_abstract:").append(_abstract).append('\n');
        if (srs != null)
            s.append("srs:").append(srs).append('\n');
        if (keywords != null) {
            s.append("keywords:").append(keywords).append('\n');
        }
       if (metadataURL != null) {
            s.append("metadataURL:").append('\n');
            for (MetadataURLType k : metadataURL) {
                s.append(k).append('\n');
            }
        }
        if (latLongBoundingBox != null) {
            s.append("latLongBoundingBox:").append('\n');
            for (LatLongBoundingBoxType k : latLongBoundingBox) {
                s.append(k).append('\n');
            }
        }
        if (operations != null)
           s.append("operations:").append(operations).append('\n');
        return s.toString();
    }


}
