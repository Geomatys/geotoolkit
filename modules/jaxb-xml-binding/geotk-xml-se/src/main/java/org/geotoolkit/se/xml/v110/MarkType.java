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
package org.geotoolkit.se.xml.v110;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for MarkType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MarkType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice minOccurs="0">
 *           &lt;element ref="{http://www.opengis.net/se}WellKnownName"/>
 *           &lt;sequence>
 *             &lt;choice>
 *               &lt;element ref="{http://www.opengis.net/se}OnlineResource"/>
 *               &lt;element ref="{http://www.opengis.net/se}InlineContent"/>
 *             &lt;/choice>
 *             &lt;element ref="{http://www.opengis.net/se}Format"/>
 *             &lt;element ref="{http://www.opengis.net/se}MarkIndex" minOccurs="0"/>
 *           &lt;/sequence>
 *         &lt;/choice>
 *         &lt;element ref="{http://www.opengis.net/se}Fill" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/se}Stroke" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MarkType", propOrder = {
    "wellKnownName",
    "onlineResource",
    "inlineContent",
    "format",
    "markIndex",
    "fill",
    "stroke"
})
public class MarkType {

    @XmlElement(name = "WellKnownName")
    protected String wellKnownName;
    @XmlElement(name = "OnlineResource")
    protected OnlineResourceType onlineResource;
    @XmlElement(name = "InlineContent")
    protected InlineContentType inlineContent;
    @XmlElement(name = "Format")
    protected String format;
    @XmlElement(name = "MarkIndex")
    protected BigInteger markIndex;
    @XmlElement(name = "Fill")
    protected FillType fill;
    @XmlElement(name = "Stroke")
    protected StrokeType stroke;

    /**
     * Gets the value of the wellKnownName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWellKnownName() {
        return wellKnownName;
    }

    /**
     * Sets the value of the wellKnownName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWellKnownName(String value) {
        this.wellKnownName = value;
    }

    /**
     * Gets the value of the onlineResource property.
     * 
     * @return
     *     possible object is
     *     {@link OnlineResourceType }
     *     
     */
    public OnlineResourceType getOnlineResource() {
        return onlineResource;
    }

    /**
     * Sets the value of the onlineResource property.
     * 
     * @param value
     *     allowed object is
     *     {@link OnlineResourceType }
     *     
     */
    public void setOnlineResource(OnlineResourceType value) {
        this.onlineResource = value;
    }

    /**
     * Gets the value of the inlineContent property.
     * 
     * @return
     *     possible object is
     *     {@link InlineContentType }
     *     
     */
    public InlineContentType getInlineContent() {
        return inlineContent;
    }

    /**
     * Sets the value of the inlineContent property.
     * 
     * @param value
     *     allowed object is
     *     {@link InlineContentType }
     *     
     */
    public void setInlineContent(InlineContentType value) {
        this.inlineContent = value;
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
     * Gets the value of the markIndex property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getMarkIndex() {
        return markIndex;
    }

    /**
     * Sets the value of the markIndex property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setMarkIndex(BigInteger value) {
        this.markIndex = value;
    }

    /**
     * Gets the value of the fill property.
     * 
     * @return
     *     possible object is
     *     {@link FillType }
     *     
     */
    public FillType getFill() {
        return fill;
    }

    /**
     * Sets the value of the fill property.
     * 
     * @param value
     *     allowed object is
     *     {@link FillType }
     *     
     */
    public void setFill(FillType value) {
        this.fill = value;
    }

    /**
     * Gets the value of the stroke property.
     * 
     * @return
     *     possible object is
     *     {@link StrokeType }
     *     
     */
    public StrokeType getStroke() {
        return stroke;
    }

    /**
     * Sets the value of the stroke property.
     * 
     * @param value
     *     allowed object is
     *     {@link StrokeType }
     *     
     */
    public void setStroke(StrokeType value) {
        this.stroke = value;
    }

}
