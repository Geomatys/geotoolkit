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
 * <p>Classe Java pour AnimatedUpdateType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="AnimatedUpdateType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/kml/2.2}AbstractTourPrimitiveType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}duration" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}Update" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}delayedStart" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}AnimatedUpdateSimpleExtensionGroup" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}AnimatedUpdateObjectExtensionGroup" maxOccurs="unbounded" minOccurs="0"/>
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
@XmlType(name = "AnimatedUpdateType", namespace = "http://www.opengis.net/kml/2.2", propOrder = {
    "duration",
    "update",
    "delayedStart",
    "animatedUpdateSimpleExtensionGroup",
    "animatedUpdateObjectExtensionGroup"
})
public class AnimatedUpdateType
    extends AbstractTourPrimitiveType
{

    @XmlElement(namespace = "http://www.opengis.net/kml/2.2", defaultValue = "0.0")
    protected Double duration;
    @XmlElement(name = "Update", namespace = "http://www.opengis.net/kml/2.2")
    protected UpdateType update;
    @XmlElement(namespace = "http://www.opengis.net/kml/2.2", defaultValue = "0.0")
    protected Double delayedStart;
    @XmlElement(name = "AnimatedUpdateSimpleExtensionGroup", namespace = "http://www.opengis.net/kml/2.2")
    protected List<Object> animatedUpdateSimpleExtensionGroup;
    @XmlElement(name = "AnimatedUpdateObjectExtensionGroup", namespace = "http://www.opengis.net/kml/2.2")
    protected List<AbstractObjectType> animatedUpdateObjectExtensionGroup;

    /**
     * Obtient la valeur de la propriété duration.
     *
     * @return
     *     possible object is
     *     {@link Double }
     *
     */
    public Double getDuration() {
        return duration;
    }

    /**
     * Définit la valeur de la propriété duration.
     *
     * @param value
     *     allowed object is
     *     {@link Double }
     *
     */
    public void setDuration(Double value) {
        this.duration = value;
    }

    /**
     * Obtient la valeur de la propriété update.
     *
     * @return
     *     possible object is
     *     {@link UpdateType }
     *
     */
    public UpdateType getUpdate() {
        return update;
    }

    /**
     * Définit la valeur de la propriété update.
     *
     * @param value
     *     allowed object is
     *     {@link UpdateType }
     *
     */
    public void setUpdate(UpdateType value) {
        this.update = value;
    }

    /**
     * Obtient la valeur de la propriété delayedStart.
     *
     * @return
     *     possible object is
     *     {@link Double }
     *
     */
    public Double getDelayedStart() {
        return delayedStart;
    }

    /**
     * Définit la valeur de la propriété delayedStart.
     *
     * @param value
     *     allowed object is
     *     {@link Double }
     *
     */
    public void setDelayedStart(Double value) {
        this.delayedStart = value;
    }

    /**
     * Gets the value of the animatedUpdateSimpleExtensionGroup property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the animatedUpdateSimpleExtensionGroup property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAnimatedUpdateSimpleExtensionGroup().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     *
     *
     */
    public List<Object> getAnimatedUpdateSimpleExtensionGroup() {
        if (animatedUpdateSimpleExtensionGroup == null) {
            animatedUpdateSimpleExtensionGroup = new ArrayList<Object>();
        }
        return this.animatedUpdateSimpleExtensionGroup;
    }

    /**
     * Gets the value of the animatedUpdateObjectExtensionGroup property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the animatedUpdateObjectExtensionGroup property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAnimatedUpdateObjectExtensionGroup().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AbstractObjectType }
     *
     *
     */
    public List<AbstractObjectType> getAnimatedUpdateObjectExtensionGroup() {
        if (animatedUpdateObjectExtensionGroup == null) {
            animatedUpdateObjectExtensionGroup = new ArrayList<AbstractObjectType>();
        }
        return this.animatedUpdateObjectExtensionGroup;
    }

}
