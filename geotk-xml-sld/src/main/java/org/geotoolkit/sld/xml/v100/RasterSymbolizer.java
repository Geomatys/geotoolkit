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
package org.geotoolkit.sld.xml.v100;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/sld}SymbolizerType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/sld}Geometry" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/sld}Opacity" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/sld}ChannelSelection" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/sld}OverlapBehavior" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/sld}ColorMap" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/sld}ContrastEnhancement" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/sld}ShadedRelief" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/sld}ImageOutline" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "geometry",
    "opacity",
    "channelSelection",
    "overlapBehavior",
    "colorMap",
    "contrastEnhancement",
    "shadedRelief",
    "imageOutline"
})
public class RasterSymbolizer
    extends SymbolizerType
{

    @XmlElement(name = "Geometry")
    protected Geometry geometry;
    @XmlElement(name = "Opacity")
    protected ParameterValueType opacity;
    @XmlElement(name = "ChannelSelection")
    protected ChannelSelection channelSelection;
    @XmlElement(name = "OverlapBehavior")
    protected OverlapBehavior overlapBehavior;
    @XmlElement(name = "ColorMap")
    protected ColorMap colorMap;
    @XmlElement(name = "ContrastEnhancement")
    protected ContrastEnhancement contrastEnhancement;
    @XmlElement(name = "ShadedRelief")
    protected ShadedRelief shadedRelief;
    @XmlElement(name = "ImageOutline")
    protected ImageOutline imageOutline;

    /**
     * Gets the value of the geometry property.
     *
     * @return
     *     possible object is
     *     {@link Geometry }
     *
     */
    public Geometry getGeometry() {
        return geometry;
    }

    /**
     * Sets the value of the geometry property.
     *
     * @param value
     *     allowed object is
     *     {@link Geometry }
     *
     */
    public void setGeometry(final Geometry value) {
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
     *     {@link ChannelSelection }
     *
     */
    public ChannelSelection getChannelSelection() {
        return channelSelection;
    }

    /**
     * Sets the value of the channelSelection property.
     *
     * @param value
     *     allowed object is
     *     {@link ChannelSelection }
     *
     */
    public void setChannelSelection(final ChannelSelection value) {
        this.channelSelection = value;
    }

    /**
     * Gets the value of the overlapBehavior property.
     *
     * @return
     *     possible object is
     *     {@link OverlapBehavior }
     *
     */
    public OverlapBehavior getOverlapBehavior() {
        return overlapBehavior;
    }

    /**
     * Sets the value of the overlapBehavior property.
     *
     * @param value
     *     allowed object is
     *     {@link OverlapBehavior }
     *
     */
    public void setOverlapBehavior(final OverlapBehavior value) {
        this.overlapBehavior = value;
    }

    /**
     * Gets the value of the colorMap property.
     *
     * @return
     *     possible object is
     *     {@link ColorMap }
     *
     */
    public ColorMap getColorMap() {
        return colorMap;
    }

    /**
     * Sets the value of the colorMap property.
     *
     * @param value
     *     allowed object is
     *     {@link ColorMap }
     *
     */
    public void setColorMap(final ColorMap value) {
        this.colorMap = value;
    }

    /**
     * Gets the value of the contrastEnhancement property.
     *
     * @return
     *     possible object is
     *     {@link ContrastEnhancement }
     *
     */
    public ContrastEnhancement getContrastEnhancement() {
        return contrastEnhancement;
    }

    /**
     * Sets the value of the contrastEnhancement property.
     *
     * @param value
     *     allowed object is
     *     {@link ContrastEnhancement }
     *
     */
    public void setContrastEnhancement(final ContrastEnhancement value) {
        this.contrastEnhancement = value;
    }

    /**
     * Gets the value of the shadedRelief property.
     *
     * @return
     *     possible object is
     *     {@link ShadedRelief }
     *
     */
    public ShadedRelief getShadedRelief() {
        return shadedRelief;
    }

    /**
     * Sets the value of the shadedRelief property.
     *
     * @param value
     *     allowed object is
     *     {@link ShadedRelief }
     *
     */
    public void setShadedRelief(final ShadedRelief value) {
        this.shadedRelief = value;
    }

    /**
     * Gets the value of the imageOutline property.
     *
     * @return
     *     possible object is
     *     {@link ImageOutline }
     *
     */
    public ImageOutline getImageOutline() {
        return imageOutline;
    }

    /**
     * Sets the value of the imageOutline property.
     *
     * @param value
     *     allowed object is
     *     {@link ImageOutline }
     *
     */
    public void setImageOutline(final ImageOutline value) {
        this.imageOutline = value;
    }

}
