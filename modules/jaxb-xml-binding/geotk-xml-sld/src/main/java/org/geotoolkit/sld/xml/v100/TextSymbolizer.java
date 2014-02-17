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
 *         &lt;element ref="{http://www.opengis.net/sld}Label" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/sld}Font" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/sld}LabelPlacement" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/sld}Halo" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/sld}Fill" minOccurs="0"/>
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
@XmlType(name = "", propOrder = {
    "geometry",
    "label",
    "font",
    "labelPlacement",
    "halo",
    "fill"
})
public class TextSymbolizer
    extends SymbolizerType
{

    @XmlElement(name = "Geometry")
    protected Geometry geometry;
    @XmlElement(name = "Label")
    protected ParameterValueType label;
    @XmlElement(name = "Font")
    protected Font font;
    @XmlElement(name = "LabelPlacement")
    protected LabelPlacement labelPlacement;
    @XmlElement(name = "Halo")
    protected Halo halo;
    @XmlElement(name = "Fill")
    protected Fill fill;

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
     * Gets the value of the label property.
     * 
     * @return
     *     possible object is
     *     {@link ParameterValueType }
     *     
     */
    public ParameterValueType getLabel() {
        return label;
    }

    /**
     * Sets the value of the label property.
     * 
     * @param value
     *     allowed object is
     *     {@link ParameterValueType }
     *     
     */
    public void setLabel(final ParameterValueType value) {
        this.label = value;
    }

    /**
     * Gets the value of the font property.
     * 
     * @return
     *     possible object is
     *     {@link Font }
     *     
     */
    public Font getFont() {
        return font;
    }

    /**
     * Sets the value of the font property.
     * 
     * @param value
     *     allowed object is
     *     {@link Font }
     *     
     */
    public void setFont(final Font value) {
        this.font = value;
    }

    /**
     * Gets the value of the labelPlacement property.
     * 
     * @return
     *     possible object is
     *     {@link LabelPlacement }
     *     
     */
    public LabelPlacement getLabelPlacement() {
        return labelPlacement;
    }

    /**
     * Sets the value of the labelPlacement property.
     * 
     * @param value
     *     allowed object is
     *     {@link LabelPlacement }
     *     
     */
    public void setLabelPlacement(final LabelPlacement value) {
        this.labelPlacement = value;
    }

    /**
     * Gets the value of the halo property.
     * 
     * @return
     *     possible object is
     *     {@link Halo }
     *     
     */
    public Halo getHalo() {
        return halo;
    }

    /**
     * Sets the value of the halo property.
     * 
     * @param value
     *     allowed object is
     *     {@link Halo }
     *     
     */
    public void setHalo(final Halo value) {
        this.halo = value;
    }

    /**
     * Gets the value of the fill property.
     * 
     * @return
     *     possible object is
     *     {@link Fill }
     *     
     */
    public Fill getFill() {
        return fill;
    }

    /**
     * Sets the value of the fill property.
     * 
     * @param value
     *     allowed object is
     *     {@link Fill }
     *     
     */
    public void setFill(final Fill value) {
        this.fill = value;
    }

}
