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
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour ViewVolumeType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="ViewVolumeType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/kml/2.2}AbstractObjectType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}leftFov" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}rightFov" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}bottomFov" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}topFov" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}near" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}ViewVolumeSimpleExtensionGroup" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}ViewVolumeObjectExtensionGroup" maxOccurs="unbounded" minOccurs="0"/>
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
@XmlType(name = "ViewVolumeType", namespace = "http://www.opengis.net/kml/2.2", propOrder = {
    "leftFov",
    "rightFov",
    "bottomFov",
    "topFov",
    "near",
    "viewVolumeSimpleExtensionGroup",
    "viewVolumeObjectExtensionGroup"
})
public class ViewVolumeType
    extends AbstractObjectType
{

    @XmlElement(namespace = "http://www.opengis.net/kml/2.2", defaultValue = "0.0")
    protected Double leftFov;
    @XmlElement(namespace = "http://www.opengis.net/kml/2.2", defaultValue = "0.0")
    protected Double rightFov;
    @XmlElement(namespace = "http://www.opengis.net/kml/2.2", defaultValue = "0.0")
    protected Double bottomFov;
    @XmlElement(namespace = "http://www.opengis.net/kml/2.2", defaultValue = "0.0")
    protected Double topFov;
    @XmlElement(namespace = "http://www.opengis.net/kml/2.2", defaultValue = "0.0")
    protected Double near;
    @XmlElement(name = "ViewVolumeSimpleExtensionGroup", namespace = "http://www.opengis.net/kml/2.2")
    protected List<Object> viewVolumeSimpleExtensionGroup;
    @XmlElement(name = "ViewVolumeObjectExtensionGroup", namespace = "http://www.opengis.net/kml/2.2")
    protected List<AbstractObjectType> viewVolumeObjectExtensionGroup;

    /**
     * Obtient la valeur de la propriété leftFov.
     *
     * @return
     *     possible object is
     *     {@link Double }
     *
     */
    public Double getLeftFov() {
        return leftFov;
    }

    /**
     * Définit la valeur de la propriété leftFov.
     *
     * @param value
     *     allowed object is
     *     {@link Double }
     *
     */
    public void setLeftFov(Double value) {
        this.leftFov = value;
    }

    /**
     * Obtient la valeur de la propriété rightFov.
     *
     * @return
     *     possible object is
     *     {@link Double }
     *
     */
    public Double getRightFov() {
        return rightFov;
    }

    /**
     * Définit la valeur de la propriété rightFov.
     *
     * @param value
     *     allowed object is
     *     {@link Double }
     *
     */
    public void setRightFov(Double value) {
        this.rightFov = value;
    }

    /**
     * Obtient la valeur de la propriété bottomFov.
     *
     * @return
     *     possible object is
     *     {@link Double }
     *
     */
    public Double getBottomFov() {
        return bottomFov;
    }

    /**
     * Définit la valeur de la propriété bottomFov.
     *
     * @param value
     *     allowed object is
     *     {@link Double }
     *
     */
    public void setBottomFov(Double value) {
        this.bottomFov = value;
    }

    /**
     * Obtient la valeur de la propriété topFov.
     *
     * @return
     *     possible object is
     *     {@link Double }
     *
     */
    public Double getTopFov() {
        return topFov;
    }

    /**
     * Définit la valeur de la propriété topFov.
     *
     * @param value
     *     allowed object is
     *     {@link Double }
     *
     */
    public void setTopFov(Double value) {
        this.topFov = value;
    }

    /**
     * Obtient la valeur de la propriété near.
     *
     * @return
     *     possible object is
     *     {@link Double }
     *
     */
    public Double getNear() {
        return near;
    }

    /**
     * Définit la valeur de la propriété near.
     *
     * @param value
     *     allowed object is
     *     {@link Double }
     *
     */
    public void setNear(Double value) {
        this.near = value;
    }

    /**
     * Gets the value of the viewVolumeSimpleExtensionGroup property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the viewVolumeSimpleExtensionGroup property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getViewVolumeSimpleExtensionGroup().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     *
     *
     */
    public List<Object> getViewVolumeSimpleExtensionGroup() {
        if (viewVolumeSimpleExtensionGroup == null) {
            viewVolumeSimpleExtensionGroup = new ArrayList<Object>();
        }
        return this.viewVolumeSimpleExtensionGroup;
    }

    /**
     * Gets the value of the viewVolumeObjectExtensionGroup property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the viewVolumeObjectExtensionGroup property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getViewVolumeObjectExtensionGroup().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AbstractObjectType }
     *
     *
     */
    public List<AbstractObjectType> getViewVolumeObjectExtensionGroup() {
        if (viewVolumeObjectExtensionGroup == null) {
            viewVolumeObjectExtensionGroup = new ArrayList<AbstractObjectType>();
        }
        return this.viewVolumeObjectExtensionGroup;
    }

}
