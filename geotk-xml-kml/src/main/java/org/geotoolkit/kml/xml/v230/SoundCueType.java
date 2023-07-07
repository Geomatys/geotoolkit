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
 * <p>Classe Java pour SoundCueType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="SoundCueType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/kml/2.2}AbstractTourPrimitiveType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}href" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}delayedStart" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}SoundCueSimpleExtensionGroup" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}SoundCueObjectExtensionGroup" maxOccurs="unbounded" minOccurs="0"/>
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
@XmlType(name = "SoundCueType", namespace = "http://www.opengis.net/kml/2.2", propOrder = {
    "href",
    "delayedStart",
    "soundCueSimpleExtensionGroup",
    "soundCueObjectExtensionGroup"
})
public class SoundCueType
    extends AbstractTourPrimitiveType
{

    @XmlElement(namespace = "http://www.opengis.net/kml/2.2")
    protected String href;
    @XmlElement(namespace = "http://www.opengis.net/kml/2.2", defaultValue = "0.0")
    protected Double delayedStart;
    @XmlElement(name = "SoundCueSimpleExtensionGroup", namespace = "http://www.opengis.net/kml/2.2")
    protected List<Object> soundCueSimpleExtensionGroup;
    @XmlElement(name = "SoundCueObjectExtensionGroup", namespace = "http://www.opengis.net/kml/2.2")
    protected List<AbstractObjectType> soundCueObjectExtensionGroup;

    /**
     * Obtient la valeur de la propriété href.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getHref() {
        return href;
    }

    /**
     * Définit la valeur de la propriété href.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setHref(String value) {
        this.href = value;
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
     * Gets the value of the soundCueSimpleExtensionGroup property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the soundCueSimpleExtensionGroup property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSoundCueSimpleExtensionGroup().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     *
     *
     */
    public List<Object> getSoundCueSimpleExtensionGroup() {
        if (soundCueSimpleExtensionGroup == null) {
            soundCueSimpleExtensionGroup = new ArrayList<Object>();
        }
        return this.soundCueSimpleExtensionGroup;
    }

    /**
     * Gets the value of the soundCueObjectExtensionGroup property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the soundCueObjectExtensionGroup property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSoundCueObjectExtensionGroup().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AbstractObjectType }
     *
     *
     */
    public List<AbstractObjectType> getSoundCueObjectExtensionGroup() {
        if (soundCueObjectExtensionGroup == null) {
            soundCueObjectExtensionGroup = new ArrayList<AbstractObjectType>();
        }
        return this.soundCueObjectExtensionGroup;
    }

}
