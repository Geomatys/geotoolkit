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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for RasterSymbolizerType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RasterSymbolizerType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/se}SymbolizerType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/se}Geometry" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/se}Opacity" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/se}ChannelSelection" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/se}OverlapBehavior" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/se}ColorMap" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/se}ContrastEnhancement" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/se}ShadedRelief" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/se}ImageOutline" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RasterSymbolizerType", propOrder = {
    "geometry",
    "opacity",
    "channelSelection",
    "overlapBehavior",
    "colorMap",
    "contrastEnhancement",
    "shadedRelief",
    "imageOutline"
})
public class RasterSymbolizerType
    extends SymbolizerType
{

    @XmlElement(name = "Geometry")
    protected GeometryType geometry;
    @XmlElement(name = "Opacity")
    protected ParameterValueType opacity;
    @XmlElement(name = "ChannelSelection")
    protected ChannelSelectionType channelSelection;
    @XmlElement(name = "OverlapBehavior")
    protected String overlapBehavior;
    @XmlElement(name = "ColorMap")
    protected ColorMapType colorMap;
    @XmlElement(name = "ContrastEnhancement")
    protected ContrastEnhancementType contrastEnhancement;
    @XmlElement(name = "ShadedRelief")
    protected ShadedReliefType shadedRelief;
    @XmlElement(name = "ImageOutline")
    protected ImageOutlineType imageOutline;

    /**
     * Gets the value of the geometry property.
     * 
     * @return
     *     possible object is
     *     {@link GeometryType }
     *     
     */
    public GeometryType getGeometry() {
        return geometry;
    }

    /**
     * Sets the value of the geometry property.
     * 
     * @param value
     *     allowed object is
     *     {@link GeometryType }
     *     
     */
    public void setGeometry(final GeometryType value) {
        this.geometry = value;
    }

    /**
     * Gets the value of the opacity property.
     * 
     * @return
     *     possible object is
     *     {@link ParameterValueType }
     *     
     */
    public ParameterValueType getOpacity() {
        return opacity;
    }

    /**
     * Sets the value of the opacity property.
     * 
     * @param value
     *     allowed object is
     *     {@link ParameterValueType }
     *     
     */
    public void setOpacity(final ParameterValueType value) {
        this.opacity = value;
    }

    /**
     * Gets the value of the channelSelection property.
     * 
     * @return
     *     possible object is
     *     {@link ChannelSelectionType }
     *     
     */
    public ChannelSelectionType getChannelSelection() {
        return channelSelection;
    }

    /**
     * Sets the value of the channelSelection property.
     * 
     * @param value
     *     allowed object is
     *     {@link ChannelSelectionType }
     *     
     */
    public void setChannelSelection(final ChannelSelectionType value) {
        this.channelSelection = value;
    }

    /**
     * Gets the value of the overlapBehavior property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOverlapBehavior() {
        return overlapBehavior;
    }

    /**
     * Sets the value of the overlapBehavior property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOverlapBehavior(final String value) {
        this.overlapBehavior = value;
    }

    /**
     * Gets the value of the colorMap property.
     * 
     * @return
     *     possible object is
     *     {@link ColorMapType }
     *     
     */
    public ColorMapType getColorMap() {
        return colorMap;
    }

    /**
     * Sets the value of the colorMap property.
     * 
     * @param value
     *     allowed object is
     *     {@link ColorMapType }
     *     
     */
    public void setColorMap(final ColorMapType value) {
        this.colorMap = value;
    }

    /**
     * Gets the value of the contrastEnhancement property.
     * 
     * @return
     *     possible object is
     *     {@link ContrastEnhancementType }
     *     
     */
    public ContrastEnhancementType getContrastEnhancement() {
        return contrastEnhancement;
    }

    /**
     * Sets the value of the contrastEnhancement property.
     * 
     * @param value
     *     allowed object is
     *     {@link ContrastEnhancementType }
     *     
     */
    public void setContrastEnhancement(final ContrastEnhancementType value) {
        this.contrastEnhancement = value;
    }

    /**
     * Gets the value of the shadedRelief property.
     * 
     * @return
     *     possible object is
     *     {@link ShadedReliefType }
     *     
     */
    public ShadedReliefType getShadedRelief() {
        return shadedRelief;
    }

    /**
     * Sets the value of the shadedRelief property.
     * 
     * @param value
     *     allowed object is
     *     {@link ShadedReliefType }
     *     
     */
    public void setShadedRelief(final ShadedReliefType value) {
        this.shadedRelief = value;
    }

    /**
     * Gets the value of the imageOutline property.
     * 
     * @return
     *     possible object is
     *     {@link ImageOutlineType }
     *     
     */
    public ImageOutlineType getImageOutline() {
        return imageOutline;
    }

    /**
     * Sets the value of the imageOutline property.
     * 
     * @param value
     *     allowed object is
     *     {@link ImageOutlineType }
     *     
     */
    public void setImageOutline(final ImageOutlineType value) {
        this.imageOutline = value;
    }

}
