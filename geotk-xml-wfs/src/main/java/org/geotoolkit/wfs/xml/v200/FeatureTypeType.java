/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2011, Geomatys
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


package org.geotoolkit.wfs.xml.v200;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;
import org.geotoolkit.ows.xml.v110.KeywordsType;
import org.geotoolkit.ows.xml.v110.WGS84BoundingBoxType;
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
 *         &lt;element ref="{http://www.opengis.net/wfs/2.0}Title" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/wfs/2.0}Abstract" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/ows/1.1}Keywords" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;choice>
 *           &lt;sequence>
 *             &lt;element name="DefaultCRS" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *             &lt;element name="OtherCRS" type="{http://www.w3.org/2001/XMLSchema}anyURI" maxOccurs="unbounded" minOccurs="0"/>
 *           &lt;/sequence>
 *           &lt;element name="NoCRS">
 *             &lt;complexType>
 *               &lt;complexContent>
 *                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;/restriction>
 *               &lt;/complexContent>
 *             &lt;/complexType>
 *           &lt;/element>
 *         &lt;/choice>
 *         &lt;element name="OutputFormats" type="{http://www.opengis.net/wfs/2.0}OutputFormatListType" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/ows/1.1}WGS84BoundingBox" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="MetadataURL" type="{http://www.opengis.net/wfs/2.0}MetadataURLType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="ExtendedDescription" type="{http://www.opengis.net/wfs/2.0}ExtendedDescriptionType" minOccurs="0"/>
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
    "defaultCRS",
    "otherCRS",
    "noCRS",
    "outputFormats",
    "wgs84BoundingBox",
    "metadataURL",
    "extendedDescription"
})
public class FeatureTypeType implements FeatureType {

    @XmlElement(name = "Name", required = true)
    private QName name;
    @XmlElement(name = "Title")
    private List<Title> title;
    @XmlElement(name = "Abstract")
    private List<Abstract> _abstract;
    @XmlElement(name = "Keywords", namespace = "http://www.opengis.net/ows/1.1")
    private List<KeywordsType> keywords;
    @XmlElement(name = "DefaultCRS")
    @XmlSchemaType(name = "anyURI")
    private String defaultCRS;
    @XmlElement(name = "OtherCRS")
    @XmlSchemaType(name = "anyURI")
    private List<String> otherCRS;
    @XmlElement(name = "NoCRS")
    private FeatureTypeType.NoCRS noCRS;
    @XmlElement(name = "OutputFormats")
    private OutputFormatListType outputFormats;
    @XmlElement(name = "WGS84BoundingBox", namespace = "http://www.opengis.net/ows/1.1")
    private List<WGS84BoundingBoxType> wgs84BoundingBox;
    @XmlElement(name = "MetadataURL")
    private List<MetadataURLType> metadataURL;
    @XmlElement(name = "ExtendedDescription")
    private ExtendedDescriptionType extendedDescription;

    public FeatureTypeType() {

    }

    public FeatureTypeType(final QName name, final String title, final String defaultCRS, final List<String> otherCRS, final List<WGS84BoundingBoxType> wgs84BoundingBox) {
        this.name       = name;
        if (title != null) {
            this.title  = new ArrayList<Title>();
            this.title.add(new Title(title));
        }
        this.defaultCRS = defaultCRS;
        this.otherCRS   = otherCRS;
        this.wgs84BoundingBox = wgs84BoundingBox;
    }

    /**
     * Gets the value of the name property.
     *
     * @return
     *     possible object is
     *     {@link QName }
     *
     */
    @Override
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
    @Override
    public void setName(QName value) {
        this.name = value;
    }

    /**
     * Gets the value of the title property.
     *
     */
    public List<Title> getTitle() {
        if (title == null) {
            title = new ArrayList<Title>();
        }
        return this.title;
    }

    /**
     * Gets the value of the abstract property.
     */
    public List<Abstract> getAbstract() {
        if (_abstract == null) {
            _abstract = new ArrayList<Abstract>();
        }
        return this._abstract;
    }

    @Override
    public void setAbstract(final String value) {
        if (value != null) {
            getAbstract().add(new Abstract(value));
        }
    }

    /**
     * Gets the value of the keywords property.
     */
    public List<KeywordsType> getKeywords() {
        if (keywords == null) {
            keywords = new ArrayList<KeywordsType>();
        }
        return this.keywords;
    }

    @Override
    public void addKeywords(final List<String> values) {
        if (values != null) {
            for (String value : values) {
                getKeywords().add(new KeywordsType(value));
            }
        }
    }

    /**
     * Gets the value of the defaultCRS property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @Override
    public String getDefaultCRS() {
        return defaultCRS;
    }

    /**
     * Sets the value of the defaultCRS property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    @Override
    public void setDefaultCRS(String value) {
        this.defaultCRS = value;
    }

    /**
     * Gets the value of the otherCRS property.
     */
    public List<String> getOtherCRS() {
        if (otherCRS == null) {
            otherCRS = new ArrayList<String>();
        }
        return this.otherCRS;
    }

    @Override
    public void setOtherCRS(final List<String> otherCRS) {
        this.otherCRS = otherCRS;
    }

    /**
     * Gets the value of the noCRS property.
     *
     * @return
     *     possible object is
     *     {@link FeatureTypeType.NoCRS }
     *
     */
    public FeatureTypeType.NoCRS getNoCRS() {
        return noCRS;
    }

    /**
     * Sets the value of the noCRS property.
     *
     * @param value
     *     allowed object is
     *     {@link FeatureTypeType.NoCRS }
     *
     */
    public void setNoCRS(FeatureTypeType.NoCRS value) {
        this.noCRS = value;
    }

    /**
     * Gets the value of the outputFormats property.
     *
     * @return
     *     possible object is
     *     {@link OutputFormatListType }
     *
     */
    public OutputFormatListType getOutputFormats() {
        return outputFormats;
    }

    /**
     * Sets the value of the outputFormats property.
     *
     * @param value
     *     allowed object is
     *     {@link OutputFormatListType }
     *
     */
    public void setOutputFormats(OutputFormatListType value) {
        this.outputFormats = value;
    }

    /**
     * Gets the value of the wgs84BoundingBox property.
     */
    @Override
    public List<WGS84BoundingBoxType> getBoundingBox() {
        if (wgs84BoundingBox == null) {
            wgs84BoundingBox = new ArrayList<WGS84BoundingBoxType>();
        }
        return this.wgs84BoundingBox;
    }

    /**
     * Gets the value of the metadataURL property.
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
     * Gets the value of the extendedDescription property.
     *
     * @return
     *     possible object is
     *     {@link ExtendedDescriptionType }
     *
     */
    public ExtendedDescriptionType getExtendedDescription() {
        return extendedDescription;
    }

    /**
     * Sets the value of the extendedDescription property.
     *
     * @param value
     *     allowed object is
     *     {@link ExtendedDescriptionType }
     *
     */
    public void setExtendedDescription(ExtendedDescriptionType value) {
        this.extendedDescription = value;
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
                   Objects.equals(this.defaultCRS, that.defaultCRS) &&
                   Objects.equals(this.keywords, that.keywords) &&
                   Objects.equals(this.metadataURL, that.metadataURL) &&
                   Objects.equals(this.name, that.name) &&
                   Objects.equals(this.extendedDescription, that.extendedDescription) &&
                   Objects.equals(this.otherCRS, that.otherCRS) &&
                   Objects.equals(this.outputFormats, that.outputFormats) &&
                   Objects.equals(this.title, that.title) &&
                   Objects.equals(this.wgs84BoundingBox, that.wgs84BoundingBox) &&
                   Objects.equals(this.noCRS,  that.noCRS);
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
        hash = 79 * hash + (this.defaultCRS != null ? this.defaultCRS.hashCode() : 0);
        hash = 79 * hash + (this.otherCRS != null ? this.otherCRS.hashCode() : 0);
        hash = 79 * hash + (this.noCRS != null ? this.noCRS.hashCode() : 0);
        hash = 79 * hash + (this.extendedDescription != null ? this.extendedDescription.hashCode() : 0);
        hash = 79 * hash + (this.outputFormats != null ? this.outputFormats.hashCode() : 0);
        hash = 79 * hash + (this.wgs84BoundingBox != null ? this.wgs84BoundingBox.hashCode() : 0);
        hash = 79 * hash + (this.metadataURL != null ? this.metadataURL.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("[featureTypeType]\n");
        if(name != null) {
            s.append("name:").append(name).append('\n');
        }
        if (title != null) {
            s.append("title:").append(title).append('\n');
        }
        if (_abstract != null) {
            s.append("_abstract:").append(_abstract).append('\n');
        }
        if (defaultCRS != null) {
            s.append("defaultCRS:").append(defaultCRS).append('\n');
        }
        if (keywords != null) {
            s.append("keywords:").append('\n');
            for (KeywordsType k : keywords) {
                s.append(k).append('\n');
            }
        }
        if (metadataURL != null) {
            s.append("metadataURL:").append('\n');
            for (MetadataURLType k : metadataURL) {
                s.append(k).append('\n');
            }
        }
        if (otherCRS != null) {
            s.append("otherCRS:").append('\n');
            for (String k : otherCRS) {
                s.append(k).append('\n');
            }
        }
        if (wgs84BoundingBox != null) {
            s.append("wgs84BoundingBox:").append('\n');
            for (WGS84BoundingBoxType k : wgs84BoundingBox) {
                s.append(k).append('\n');
            }
        }
        if (noCRS != null) {
            s.append("noCRS:").append(noCRS).append('\n');
        }
        if (extendedDescription != null) {
            s.append("extendedDescription:").append(extendedDescription).append('\n');
        }
        if (outputFormats != null) {
            s.append("outputFormats:").append(outputFormats).append('\n');
        }
        return s.toString();
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
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     *
     *
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class NoCRS {


    }

}
