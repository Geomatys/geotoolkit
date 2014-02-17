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
 * <p>Java class for TextSymbolizerType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TextSymbolizerType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/se}SymbolizerType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/se}Geometry" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/se}Label" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/se}Font" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/se}LabelPlacement" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/se}Halo" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/se}Fill" minOccurs="0"/>
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
@XmlType(name = "TextSymbolizerType", propOrder = {
    "geometry",
    "label",
    "font",
    "labelPlacement",
    "halo",
    "fill"
})
public class TextSymbolizerType
    extends SymbolizerType
{

    @XmlElement(name = "Geometry")
    protected GeometryType geometry;
    @XmlElement(name = "Label")
    protected ParameterValueType label;
    @XmlElement(name = "Font")
    protected FontType font;
    @XmlElement(name = "LabelPlacement")
    protected LabelPlacementType labelPlacement;
    @XmlElement(name = "Halo")
    protected HaloType halo;
    @XmlElement(name = "Fill")
    protected FillType fill;

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
     *     {@link FontType }
     *     
     */
    public FontType getFont() {
        return font;
    }

    /**
     * Sets the value of the font property.
     * 
     * @param value
     *     allowed object is
     *     {@link FontType }
     *     
     */
    public void setFont(final FontType value) {
        this.font = value;
    }

    /**
     * Gets the value of the labelPlacement property.
     * 
     * @return
     *     possible object is
     *     {@link LabelPlacementType }
     *     
     */
    public LabelPlacementType getLabelPlacement() {
        return labelPlacement;
    }

    /**
     * Sets the value of the labelPlacement property.
     * 
     * @param value
     *     allowed object is
     *     {@link LabelPlacementType }
     *     
     */
    public void setLabelPlacement(final LabelPlacementType value) {
        this.labelPlacement = value;
    }

    /**
     * Gets the value of the halo property.
     * 
     * @return
     *     possible object is
     *     {@link HaloType }
     *     
     */
    public HaloType getHalo() {
        return halo;
    }

    /**
     * Sets the value of the halo property.
     * 
     * @param value
     *     allowed object is
     *     {@link HaloType }
     *     
     */
    public void setHalo(final HaloType value) {
        this.halo = value;
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
    public void setFill(final FillType value) {
        this.fill = value;
    }

}
