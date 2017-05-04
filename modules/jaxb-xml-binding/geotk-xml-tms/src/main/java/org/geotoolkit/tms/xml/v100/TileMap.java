/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2015, Geomatys
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
package org.geotoolkit.tms.xml.v100;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.tms.xml.TMSResponse;


/**
 * <p>Java class for anonymous complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Title" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Abstract" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="KeywordList" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Face" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="SRS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Metadata" type="{}MetadataType" minOccurs="0"/>
 *         &lt;element name="Attribution" type="{}AttributionType" minOccurs="0"/>
 *         &lt;element name="WebMapContext" type="{}WebMapContextType" minOccurs="0"/>
 *         &lt;element name="BoundingBox" type="{}BoundingBoxType" minOccurs="0"/>
 *         &lt;element name="Origin" type="{}OriginType" minOccurs="0"/>
 *         &lt;element name="TileFormat" type="{}TileFormatType" minOccurs="0"/>
 *         &lt;element name="TileSets" type="{}TileSetsType" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="titleAtt" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="srsAtt" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="version" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="profile" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="href" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="tilemapservice" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "title",
    "_abstract",
    "keywordList",
    "face",
    "srs",
    "metadata",
    "attribution",
    "webMapContext",
    "boundingBox",
    "origin",
    "tileFormat",
    "tileSets"
})
@XmlRootElement(name = "TileMap")
public class TileMap implements TMSResponse {

    @XmlElement(name = "Title")
    protected String title;
    @XmlElement(name = "Abstract")
    protected String _abstract;
    @XmlElement(name = "KeywordList")
    protected String keywordList;
    @XmlElement(name = "Face")
    protected String face;
    @XmlElement(name = "SRS")
    protected String srs;
    @XmlElement(name = "Metadata")
    protected MetadataType metadata;
    @XmlElement(name = "Attribution")
    protected AttributionType attribution;
    @XmlElement(name = "WebMapContext")
    protected WebMapContextType webMapContext;
    @XmlElement(name = "BoundingBox")
    protected BoundingBoxType boundingBox;
    @XmlElement(name = "Origin")
    protected OriginType origin;
    @XmlElement(name = "TileFormat")
    protected TileFormatType tileFormat;
    @XmlElement(name = "TileSets")
    protected TileSetsType tileSets;
    @XmlAttribute(name = "title")
    protected String titleAtt;
    @XmlAttribute(name = "srs")
    protected String srsAtt;
    @XmlAttribute(name = "version")
    protected String version;
    @XmlAttribute(name = "profile")
    protected String profile;
    @XmlAttribute(name = "href")
    protected String href;
    @XmlAttribute(name = "tilemapservice")
    protected String tilemapservice;

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
    public void setAbstract(String value) {
        this._abstract = value;
    }

    /**
     * Gets the value of the keywordList property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getKeywordList() {
        return keywordList;
    }

    /**
     * Sets the value of the keywordList property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setKeywordList(String value) {
        this.keywordList = value;
    }

    /**
     * Gets the value of the face property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getFace() {
        return face;
    }

    /**
     * Sets the value of the face property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setFace(String value) {
        this.face = value;
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
    public void setSRS(String value) {
        this.srs = value;
    }

    /**
     * Gets the value of the metadata property.
     *
     * @return
     *     possible object is
     *     {@link MetadataType }
     *
     */
    public MetadataType getMetadata() {
        return metadata;
    }

    /**
     * Sets the value of the metadata property.
     *
     * @param value
     *     allowed object is
     *     {@link MetadataType }
     *
     */
    public void setMetadata(MetadataType value) {
        this.metadata = value;
    }

    /**
     * Gets the value of the attribution property.
     *
     * @return
     *     possible object is
     *     {@link AttributionType }
     *
     */
    public AttributionType getAttribution() {
        return attribution;
    }

    /**
     * Sets the value of the attribution property.
     *
     * @param value
     *     allowed object is
     *     {@link AttributionType }
     *
     */
    public void setAttribution(AttributionType value) {
        this.attribution = value;
    }

    /**
     * Gets the value of the webMapContext property.
     *
     * @return
     *     possible object is
     *     {@link WebMapContextType }
     *
     */
    public WebMapContextType getWebMapContext() {
        return webMapContext;
    }

    /**
     * Sets the value of the webMapContext property.
     *
     * @param value
     *     allowed object is
     *     {@link WebMapContextType }
     *
     */
    public void setWebMapContext(WebMapContextType value) {
        this.webMapContext = value;
    }

    /**
     * Gets the value of the boundingBox property.
     *
     * @return
     *     possible object is
     *     {@link BoundingBoxType }
     *
     */
    public BoundingBoxType getBoundingBox() {
        return boundingBox;
    }

    /**
     * Sets the value of the boundingBox property.
     *
     * @param value
     *     allowed object is
     *     {@link BoundingBoxType }
     *
     */
    public void setBoundingBox(BoundingBoxType value) {
        this.boundingBox = value;
    }

    /**
     * Gets the value of the origin property.
     *
     * @return
     *     possible object is
     *     {@link OriginType }
     *
     */
    public OriginType getOrigin() {
        return origin;
    }

    /**
     * Sets the value of the origin property.
     *
     * @param value
     *     allowed object is
     *     {@link OriginType }
     *
     */
    public void setOrigin(OriginType value) {
        this.origin = value;
    }

    /**
     * Gets the value of the tileFormat property.
     *
     * @return
     *     possible object is
     *     {@link TileFormatType }
     *
     */
    public TileFormatType getTileFormat() {
        return tileFormat;
    }

    /**
     * Sets the value of the tileFormat property.
     *
     * @param value
     *     allowed object is
     *     {@link TileFormatType }
     *
     */
    public void setTileFormat(TileFormatType value) {
        this.tileFormat = value;
    }

    /**
     * Gets the value of the tileSets property.
     *
     * @return
     *     possible object is
     *     {@link TileSetsType }
     *
     */
    public TileSetsType getTileSets() {
        return tileSets;
    }

    /**
     * Sets the value of the tileSets property.
     *
     * @param value
     *     allowed object is
     *     {@link TileSetsType }
     *
     */
    public void setTileSets(TileSetsType value) {
        this.tileSets = value;
    }

    /**
     * Gets the value of the titleAtt property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getTitleAtt() {
        return titleAtt;
    }

    /**
     * Sets the value of the titleAtt property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setTitleAtt(String value) {
        this.titleAtt = value;
    }

    /**
     * Gets the value of the srsAtt property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSrsAtt() {
        return srsAtt;
    }

    /**
     * Sets the value of the srsAtt property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSrsAtt(String value) {
        this.srsAtt = value;
    }

    /**
     * Gets the value of the version property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getVersion() {
        return version;
    }

    /**
     * Sets the value of the version property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setVersion(String value) {
        this.version = value;
    }

    /**
     * Gets the value of the profile property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getProfile() {
        return profile;
    }

    /**
     * Sets the value of the profile property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setProfile(String value) {
        this.profile = value;
    }

    /**
     * Gets the value of the href property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getHref() {
        return href;
    }

    /**
     * Sets the value of the href property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setHref(String value) {
        this.href = value;
    }

    /**
     * Gets the value of the tilemapservice property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getTilemapservice() {
        return tilemapservice;
    }

    /**
     * Sets the value of the tilemapservice property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setTilemapservice(String value) {
        this.tilemapservice = value;
    }

}
