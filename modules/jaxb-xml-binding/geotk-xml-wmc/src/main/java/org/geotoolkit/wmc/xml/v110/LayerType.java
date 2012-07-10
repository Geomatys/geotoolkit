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
package org.geotoolkit.wmc.xml.v110;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.util.Utilities;


/**
 * <p>Java class for LayerType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="LayerType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Server" type="{http://www.opengis.net/context}ServerType"/>
 *         &lt;element name="Name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Title" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Abstract" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="DataURL" type="{http://www.opengis.net/context}URLType" minOccurs="0"/>
 *         &lt;element name="MetadataURL" type="{http://www.opengis.net/context}URLType" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/sld}MinScaleDenominator" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/sld}MaxScaleDenominator" minOccurs="0"/>
 *         &lt;element name="SRS" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="FormatList" type="{http://www.opengis.net/context}FormatListType" minOccurs="0"/>
 *         &lt;element name="StyleList" type="{http://www.opengis.net/context}StyleListType" minOccurs="0"/>
 *         &lt;element name="DimensionList" type="{http://www.opengis.net/context}DimensionListType" minOccurs="0"/>
 *         &lt;element name="Extension" type="{http://www.opengis.net/context}ExtensionType" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="hidden" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="queryable" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LayerType", propOrder = {
    "server",
    "name",
    "title",
    "_abstract",
    "dataURL",
    "metadataURL",
    "minScaleDenominator",
    "maxScaleDenominator",
    "srs",
    "formatList",
    "styleList",
    "dimensionList",
    "extension"
})
public class LayerType {

    @XmlElement(name = "Server", required = true)
    protected ServerType server;
    @XmlElement(name = "Name", required = true)
    protected String name;
    @XmlElement(name = "Title", required = true)
    protected String title;
    @XmlElement(name = "Abstract")
    protected String _abstract;
    @XmlElement(name = "DataURL")
    protected URLType dataURL;
    @XmlElement(name = "MetadataURL")
    protected URLType metadataURL;
    @XmlElement(name = "MinScaleDenominator", namespace = "http://www.opengis.net/sld")
    protected Double minScaleDenominator;
    @XmlElement(name = "MaxScaleDenominator", namespace = "http://www.opengis.net/sld")
    protected Double maxScaleDenominator;
    @XmlElement(name = "SRS")
    protected List<String> srs;
    @XmlElement(name = "FormatList")
    protected FormatListType formatList;
    @XmlElement(name = "StyleList")
    protected StyleListType styleList;
    @XmlElement(name = "DimensionList")
    protected DimensionListType dimensionList;
    @XmlElement(name = "Extension")
    protected ExtensionType extension;
    @XmlAttribute(required = true)
    protected boolean hidden;
    @XmlAttribute(required = true)
    protected boolean queryable;

    /**
     * Gets the value of the server property.
     *
     * @return
     *     possible object is
     *     {@link ServerType }
     *
     */
    public ServerType getServer() {
        return server;
    }

    /**
     * Sets the value of the server property.
     *
     * @param value
     *     allowed object is
     *     {@link ServerType }
     *
     */
    public void setServer(final ServerType value) {
        this.server = value;
    }

    /**
     * Gets the value of the name property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setName(final String value) {
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
    public void setTitle(final String value) {
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
    public void setAbstract(final String value) {
        this._abstract = value;
    }

    /**
     * Gets the value of the dataURL property.
     *
     * @return
     *     possible object is
     *     {@link URLType }
     *
     */
    public URLType getDataURL() {
        return dataURL;
    }

    /**
     * Sets the value of the dataURL property.
     *
     * @param value
     *     allowed object is
     *     {@link URLType }
     *
     */
    public void setDataURL(final URLType value) {
        this.dataURL = value;
    }

    /**
     * Gets the value of the metadataURL property.
     *
     * @return
     *     possible object is
     *     {@link URLType }
     *
     */
    public URLType getMetadataURL() {
        return metadataURL;
    }

    /**
     * Sets the value of the metadataURL property.
     *
     * @param value
     *     allowed object is
     *     {@link URLType }
     *
     */
    public void setMetadataURL(final URLType value) {
        this.metadataURL = value;
    }

    /**
     * Gets the value of the minScaleDenominator property.
     *
     * @return
     *     possible object is
     *     {@link Double }
     *
     */
    public Double getMinScaleDenominator() {
        return minScaleDenominator;
    }

    /**
     * Sets the value of the minScaleDenominator property.
     *
     * @param value
     *     allowed object is
     *     {@link Double }
     *
     */
    public void setMinScaleDenominator(final Double value) {
        this.minScaleDenominator = value;
    }

    /**
     * Gets the value of the maxScaleDenominator property.
     *
     * @return
     *     possible object is
     *     {@link Double }
     *
     */
    public Double getMaxScaleDenominator() {
        return maxScaleDenominator;
    }

    /**
     * Sets the value of the maxScaleDenominator property.
     *
     * @param value
     *     allowed object is
     *     {@link Double }
     *
     */
    public void setMaxScaleDenominator(final Double value) {
        this.maxScaleDenominator = value;
    }

    /**
     * Gets the value of the srs property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the srs property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSRS().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     *
     *
     */
    public List<String> getSRS() {
        if (srs == null) {
            srs = new ArrayList<String>();
        }
        return this.srs;
    }

    /**
     * Gets the value of the formatList property.
     *
     * @return
     *     possible object is
     *     {@link FormatListType }
     *
     */
    public FormatListType getFormatList() {
        return formatList;
    }

    /**
     * Sets the value of the formatList property.
     *
     * @param value
     *     allowed object is
     *     {@link FormatListType }
     *
     */
    public void setFormatList(final FormatListType value) {
        this.formatList = value;
    }

    /**
     * Gets the value of the styleList property.
     *
     * @return
     *     possible object is
     *     {@link StyleListType }
     *
     */
    public StyleListType getStyleList() {
        return styleList;
    }

    /**
     * Sets the value of the styleList property.
     *
     * @param value
     *     allowed object is
     *     {@link StyleListType }
     *
     */
    public void setStyleList(final StyleListType value) {
        this.styleList = value;
    }

    /**
     * Gets the value of the dimensionList property.
     *
     * @return
     *     possible object is
     *     {@link DimensionListType }
     *
     */
    public DimensionListType getDimensionList() {
        return dimensionList;
    }

    /**
     * Sets the value of the dimensionList property.
     *
     * @param value
     *     allowed object is
     *     {@link DimensionListType }
     *
     */
    public void setDimensionList(final DimensionListType value) {
        this.dimensionList = value;
    }

    /**
     * Gets the value of the extension property.
     *
     * @return
     *     possible object is
     *     {@link ExtensionType }
     *
     */
    public ExtensionType getExtension() {
        return extension;
    }

    /**
     * Sets the value of the extension property.
     *
     * @param value
     *     allowed object is
     *     {@link ExtensionType }
     *
     */
    public void setExtension(final ExtensionType value) {
        this.extension = value;
    }

    /**
     * Gets the value of the hidden property.
     *
     */
    public boolean isHidden() {
        return hidden;
    }

    /**
     * Sets the value of the hidden property.
     *
     */
    public void setHidden(final boolean value) {
        this.hidden = value;
    }

    /**
     * Gets the value of the queryable property.
     *
     */
    public boolean isQueryable() {
        return queryable;
    }

    /**
     * Sets the value of the queryable property.
     *
     */
    public void setQueryable(final boolean value) {
        this.queryable = value;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("[Layer]\n");
        if (server != null) {
            sb.append("server:").append(server).append("\n");
        }
        if (name != null) {
            sb.append("name:").append(name).append("\n");
        }
        if (title != null) {
            sb.append("title:").append(title).append("\n");
        }
        if (_abstract != null) {
            sb.append("_abstract:").append(_abstract).append("\n");
        }
        if (dataURL != null) {
            sb.append("dataURL:").append(dataURL).append("\n");
        }
        if (metadataURL != null) {
            sb.append("metadataURL:").append(metadataURL).append("\n");
        }
        if (minScaleDenominator != null) {
            sb.append("minScaleDenominator:").append(minScaleDenominator).append("\n");
        }
        if (maxScaleDenominator != null) {
            sb.append("maxScaleDenominator:").append(maxScaleDenominator).append("\n");
        }
        if (srs != null) {
            sb.append("srs:\n");
            for (String s : srs) {
                sb.append(s).append("\n");
            }
        }
        if (formatList != null) {
            sb.append("formatList:").append(formatList).append("\n");
        }
        if (styleList != null) {
            sb.append("styleList:").append(styleList).append("\n");
        }
        if (dimensionList != null) {
            sb.append("dimensionList:").append(dimensionList).append("\n");
        }
        if (extension != null) {
            sb.append("extension:").append(extension).append("\n");
        }
        sb.append("queryable:").append(queryable).append("\n");
        sb.append("hidden:").append(hidden).append("\n");
        return sb.toString();
    }

    /**
     * Verify if this entry is identical to specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof LayerType) {
            final LayerType that = (LayerType) object;

            return Utilities.equals(this._abstract, that._abstract) &&
                   Utilities.equals(this.dataURL, that.dataURL) &&
                   Utilities.equals(this.dimensionList, that.dimensionList) &&
                   Utilities.equals(this.formatList, that.formatList) &&
                   Utilities.equals(this.extension, that.extension) &&
                   Utilities.equals(this.hidden, that.hidden) &&
                   Utilities.equals(this.maxScaleDenominator, that.maxScaleDenominator) &&
                   Utilities.equals(this.title, that.title) &&
                   Utilities.equals(this.minScaleDenominator, that.minScaleDenominator) &&
                   Utilities.equals(this.name, that.name) &&
                   Utilities.equals(this.queryable, that.queryable) &&
                   Utilities.equals(this.server, that.server) &&
                   Utilities.equals(this.srs, that.srs) &&
                   Utilities.equals(this.styleList, that.styleList) &&
                   Utilities.equals(this.metadataURL,  that.metadataURL);
            }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 23 * hash + (this.server != null ? this.server.hashCode() : 0);
        hash = 23 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 23 * hash + (this.title != null ? this.title.hashCode() : 0);
        hash = 23 * hash + (this._abstract != null ? this._abstract.hashCode() : 0);
        hash = 23 * hash + (this.dataURL != null ? this.dataURL.hashCode() : 0);
        hash = 23 * hash + (this.metadataURL != null ? this.metadataURL.hashCode() : 0);
        hash = 23 * hash + (this.minScaleDenominator != null ? this.minScaleDenominator.hashCode() : 0);
        hash = 23 * hash + (this.maxScaleDenominator != null ? this.maxScaleDenominator.hashCode() : 0);
        hash = 23 * hash + (this.srs != null ? this.srs.hashCode() : 0);
        hash = 23 * hash + (this.formatList != null ? this.formatList.hashCode() : 0);
        hash = 23 * hash + (this.styleList != null ? this.styleList.hashCode() : 0);
        hash = 23 * hash + (this.dimensionList != null ? this.dimensionList.hashCode() : 0);
        hash = 23 * hash + (this.extension != null ? this.extension.hashCode() : 0);
        hash = 23 * hash + (this.hidden ? 1 : 0);
        hash = 23 * hash + (this.queryable ? 1 : 0);
        return hash;
    }
}
