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
 * <p>Java class for PointSymbolizerType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="PointSymbolizerType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/se}SymbolizerType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/se}Geometry" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/se}Graphic" minOccurs="0"/>
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
@XmlType(name = "PointSymbolizerType", propOrder = {
    "geometry",
    "graphic"
})
public class PointSymbolizerType
    extends SymbolizerType
{

    //NOTE : we support more large types of expressions, not only PropertyName
    @XmlElement(name = "Geometry")
    protected ParameterValueType geometry;
    @XmlElement(name = "Graphic")
    protected GraphicType graphic;

    /**
     * Gets the value of the geometry property.
     *
     * @return
     *     possible object is
     *     {@link GeometryType }
     *
     */
    public ParameterValueType getGeometry() {
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
    public void setGeometry(final ParameterValueType value) {
        this.geometry = value;
    }

    /**
     * Gets the value of the graphic property.
     *
     * @return
     *     possible object is
     *     {@link GraphicType }
     *
     */
    public GraphicType getGraphic() {
        return graphic;
    }

    /**
     * Sets the value of the graphic property.
     *
     * @param value
     *     allowed object is
     *     {@link GraphicType }
     *
     */
    public void setGraphic(final GraphicType value) {
        this.graphic = value;
    }

}
