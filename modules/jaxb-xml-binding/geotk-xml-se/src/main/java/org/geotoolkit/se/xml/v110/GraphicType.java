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

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GraphicType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GraphicType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice maxOccurs="unbounded" minOccurs="0">
 *           &lt;element ref="{http://www.opengis.net/se}ExternalGraphic"/>
 *           &lt;element ref="{http://www.opengis.net/se}Mark"/>
 *         &lt;/choice>
 *         &lt;element ref="{http://www.opengis.net/se}Opacity" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/se}Size" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/se}Rotation" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/se}AnchorPoint" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/se}Displacement" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GraphicType", propOrder = {
    "externalGraphicOrMark",
    "opacity",
    "size",
    "rotation",
    "anchorPoint",
    "displacement"
})
public class GraphicType {

    @XmlElements({
        @XmlElement(name = "Mark", type = MarkType.class),
        @XmlElement(name = "ExternalGraphic", type = ExternalGraphicType.class)
    })
    protected List<Object> externalGraphicOrMark;
    @XmlElement(name = "Opacity")
    protected ParameterValueType opacity;
    @XmlElement(name = "Size")
    protected ParameterValueType size;
    @XmlElement(name = "Rotation")
    protected ParameterValueType rotation;
    @XmlElement(name = "AnchorPoint")
    protected AnchorPointType anchorPoint;
    @XmlElement(name = "Displacement")
    protected DisplacementType displacement;

    /**
     * Gets the value of the externalGraphicOrMark property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the externalGraphicOrMark property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getExternalGraphicOrMark().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link MarkType }
     * {@link ExternalGraphicType }
     * 
     * 
     */
    public List<Object> getExternalGraphicOrMark() {
        if (externalGraphicOrMark == null) {
            externalGraphicOrMark = new ArrayList<Object>();
        }
        return this.externalGraphicOrMark;
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
    public void setOpacity(ParameterValueType value) {
        this.opacity = value;
    }

    /**
     * Gets the value of the size property.
     * 
     * @return
     *     possible object is
     *     {@link ParameterValueType }
     *     
     */
    public ParameterValueType getSize() {
        return size;
    }

    /**
     * Sets the value of the size property.
     * 
     * @param value
     *     allowed object is
     *     {@link ParameterValueType }
     *     
     */
    public void setSize(ParameterValueType value) {
        this.size = value;
    }

    /**
     * Gets the value of the rotation property.
     * 
     * @return
     *     possible object is
     *     {@link ParameterValueType }
     *     
     */
    public ParameterValueType getRotation() {
        return rotation;
    }

    /**
     * Sets the value of the rotation property.
     * 
     * @param value
     *     allowed object is
     *     {@link ParameterValueType }
     *     
     */
    public void setRotation(ParameterValueType value) {
        this.rotation = value;
    }

    /**
     * Gets the value of the anchorPoint property.
     * 
     * @return
     *     possible object is
     *     {@link AnchorPointType }
     *     
     */
    public AnchorPointType getAnchorPoint() {
        return anchorPoint;
    }

    /**
     * Sets the value of the anchorPoint property.
     * 
     * @param value
     *     allowed object is
     *     {@link AnchorPointType }
     *     
     */
    public void setAnchorPoint(AnchorPointType value) {
        this.anchorPoint = value;
    }

    /**
     * Gets the value of the displacement property.
     * 
     * @return
     *     possible object is
     *     {@link DisplacementType }
     *     
     */
    public DisplacementType getDisplacement() {
        return displacement;
    }

    /**
     * Sets the value of the displacement property.
     * 
     * @param value
     *     allowed object is
     *     {@link DisplacementType }
     *     
     */
    public void setDisplacement(DisplacementType value) {
        this.displacement = value;
    }

}
