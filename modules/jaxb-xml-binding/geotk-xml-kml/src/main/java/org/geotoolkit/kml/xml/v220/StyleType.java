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
package org.geotoolkit.kml.xml.v220;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for StyleType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="StyleType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/kml/2.2}AbstractStyleSelectorType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}IconStyle" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}LabelStyle" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}LineStyle" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}PolyStyle" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}BalloonStyle" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}ListStyle" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}StyleSimpleExtensionGroup" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}StyleObjectExtensionGroup" maxOccurs="unbounded" minOccurs="0"/>
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
@XmlType(name = "StyleType", propOrder = {
    "iconStyle",
    "labelStyle",
    "lineStyle",
    "polyStyle",
    "balloonStyle",
    "listStyle",
    "styleSimpleExtensionGroup",
    "styleObjectExtensionGroup"
})
public class StyleType extends AbstractStyleSelectorType {

    @XmlElement(name = "IconStyle")
    private IconStyleType iconStyle;
    @XmlElement(name = "LabelStyle")
    private LabelStyleType labelStyle;
    @XmlElement(name = "LineStyle")
    private LineStyleType lineStyle;
    @XmlElement(name = "PolyStyle")
    private PolyStyleType polyStyle;
    @XmlElement(name = "BalloonStyle")
    private BalloonStyleType balloonStyle;
    @XmlElement(name = "ListStyle")
    private ListStyleType listStyle;
    @XmlElement(name = "StyleSimpleExtensionGroup")
    @XmlSchemaType(name = "anySimpleType")
    private List<Object> styleSimpleExtensionGroup;
    @XmlElement(name = "StyleObjectExtensionGroup")
    private List<AbstractObjectType> styleObjectExtensionGroup;

    /**
     * Gets the value of the iconStyle property.
     * 
     * @return
     *     possible object is
     *     {@link IconStyleType }
     *     
     */
    public IconStyleType getIconStyle() {
        return iconStyle;
    }

    /**
     * Sets the value of the iconStyle property.
     * 
     * @param value
     *     allowed object is
     *     {@link IconStyleType }
     *     
     */
    public void setIconStyle(IconStyleType value) {
        this.iconStyle = value;
    }

    /**
     * Gets the value of the labelStyle property.
     * 
     * @return
     *     possible object is
     *     {@link LabelStyleType }
     *     
     */
    public LabelStyleType getLabelStyle() {
        return labelStyle;
    }

    /**
     * Sets the value of the labelStyle property.
     * 
     * @param value
     *     allowed object is
     *     {@link LabelStyleType }
     *     
     */
    public void setLabelStyle(LabelStyleType value) {
        this.labelStyle = value;
    }

    /**
     * Gets the value of the lineStyle property.
     * 
     * @return
     *     possible object is
     *     {@link LineStyleType }
     *     
     */
    public LineStyleType getLineStyle() {
        return lineStyle;
    }

    /**
     * Sets the value of the lineStyle property.
     * 
     * @param value
     *     allowed object is
     *     {@link LineStyleType }
     *     
     */
    public void setLineStyle(LineStyleType value) {
        this.lineStyle = value;
    }

    /**
     * Gets the value of the polyStyle property.
     * 
     * @return
     *     possible object is
     *     {@link PolyStyleType }
     *     
     */
    public PolyStyleType getPolyStyle() {
        return polyStyle;
    }

    /**
     * Sets the value of the polyStyle property.
     * 
     * @param value
     *     allowed object is
     *     {@link PolyStyleType }
     *     
     */
    public void setPolyStyle(PolyStyleType value) {
        this.polyStyle = value;
    }

    /**
     * Gets the value of the balloonStyle property.
     * 
     * @return
     *     possible object is
     *     {@link BalloonStyleType }
     *     
     */
    public BalloonStyleType getBalloonStyle() {
        return balloonStyle;
    }

    /**
     * Sets the value of the balloonStyle property.
     * 
     * @param value
     *     allowed object is
     *     {@link BalloonStyleType }
     *     
     */
    public void setBalloonStyle(BalloonStyleType value) {
        this.balloonStyle = value;
    }

    /**
     * Gets the value of the listStyle property.
     * 
     * @return
     *     possible object is
     *     {@link ListStyleType }
     *     
     */
    public ListStyleType getListStyle() {
        return listStyle;
    }

    /**
     * Sets the value of the listStyle property.
     * 
     * @param value
     *     allowed object is
     *     {@link ListStyleType }
     *     
     */
    public void setListStyle(ListStyleType value) {
        this.listStyle = value;
    }

    /**
     * Gets the value of the styleSimpleExtensionGroup property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the styleSimpleExtensionGroup property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getStyleSimpleExtensionGroup().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     * 
     * 
     */
    public List<Object> getStyleSimpleExtensionGroup() {
        if (styleSimpleExtensionGroup == null) {
            styleSimpleExtensionGroup = new ArrayList<Object>();
        }
        return this.styleSimpleExtensionGroup;
    }

    /**
     * Gets the value of the styleObjectExtensionGroup property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the styleObjectExtensionGroup property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getStyleObjectExtensionGroup().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AbstractObjectType }
     * 
     * 
     */
    public List<AbstractObjectType> getStyleObjectExtensionGroup() {
        if (styleObjectExtensionGroup == null) {
            styleObjectExtensionGroup = new ArrayList<AbstractObjectType>();
        }
        return this.styleObjectExtensionGroup;
    }

}
