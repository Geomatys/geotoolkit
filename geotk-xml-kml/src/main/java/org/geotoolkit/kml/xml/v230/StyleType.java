/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2023, Geomatys
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


package org.geotoolkit.kml.xml.v230;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour StyleType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
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
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StyleType", namespace = "http://www.opengis.net/kml/2.2", propOrder = {
    "iconStyle",
    "labelStyle",
    "lineStyle",
    "polyStyle",
    "balloonStyle",
    "listStyle",
    "styleSimpleExtensionGroup",
    "styleObjectExtensionGroup"
})
public class StyleType
    extends AbstractStyleSelectorType
{

    @XmlElement(name = "IconStyle", namespace = "http://www.opengis.net/kml/2.2")
    protected IconStyleType iconStyle;
    @XmlElement(name = "LabelStyle", namespace = "http://www.opengis.net/kml/2.2")
    protected LabelStyleType labelStyle;
    @XmlElement(name = "LineStyle", namespace = "http://www.opengis.net/kml/2.2")
    protected LineStyleType lineStyle;
    @XmlElement(name = "PolyStyle", namespace = "http://www.opengis.net/kml/2.2")
    protected PolyStyleType polyStyle;
    @XmlElement(name = "BalloonStyle", namespace = "http://www.opengis.net/kml/2.2")
    protected BalloonStyleType balloonStyle;
    @XmlElement(name = "ListStyle", namespace = "http://www.opengis.net/kml/2.2")
    protected ListStyleType listStyle;
    @XmlElement(name = "StyleSimpleExtensionGroup", namespace = "http://www.opengis.net/kml/2.2")
    protected List<Object> styleSimpleExtensionGroup;
    @XmlElement(name = "StyleObjectExtensionGroup", namespace = "http://www.opengis.net/kml/2.2")
    protected List<AbstractObjectType> styleObjectExtensionGroup;

    /**
     * Obtient la valeur de la propriété iconStyle.
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
     * Définit la valeur de la propriété iconStyle.
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
     * Obtient la valeur de la propriété labelStyle.
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
     * Définit la valeur de la propriété labelStyle.
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
     * Obtient la valeur de la propriété lineStyle.
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
     * Définit la valeur de la propriété lineStyle.
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
     * Obtient la valeur de la propriété polyStyle.
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
     * Définit la valeur de la propriété polyStyle.
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
     * Obtient la valeur de la propriété balloonStyle.
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
     * Définit la valeur de la propriété balloonStyle.
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
     * Obtient la valeur de la propriété listStyle.
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
     * Définit la valeur de la propriété listStyle.
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
