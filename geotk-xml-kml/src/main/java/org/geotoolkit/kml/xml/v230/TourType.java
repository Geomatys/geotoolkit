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
 * <p>Classe Java pour TourType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="TourType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/kml/2.2}AbstractFeatureType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}Playlist" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}TourSimpleExtensionGroup" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}TourObjectExtensionGroup" maxOccurs="unbounded" minOccurs="0"/>
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
@XmlType(name = "TourType", namespace = "http://www.opengis.net/kml/2.2", propOrder = {
    "playlist",
    "tourSimpleExtensionGroup",
    "tourObjectExtensionGroup"
})
public class TourType
    extends AbstractFeatureType
{

    @XmlElement(name = "Playlist", namespace = "http://www.opengis.net/kml/2.2")
    protected PlaylistType playlist;
    @XmlElement(name = "TourSimpleExtensionGroup", namespace = "http://www.opengis.net/kml/2.2")
    protected List<Object> tourSimpleExtensionGroup;
    @XmlElement(name = "TourObjectExtensionGroup", namespace = "http://www.opengis.net/kml/2.2")
    protected List<AbstractObjectType> tourObjectExtensionGroup;

    /**
     * Obtient la valeur de la propriété playlist.
     *
     * @return
     *     possible object is
     *     {@link PlaylistType }
     *
     */
    public PlaylistType getPlaylist() {
        return playlist;
    }

    /**
     * Définit la valeur de la propriété playlist.
     *
     * @param value
     *     allowed object is
     *     {@link PlaylistType }
     *
     */
    public void setPlaylist(PlaylistType value) {
        this.playlist = value;
    }

    /**
     * Gets the value of the tourSimpleExtensionGroup property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the tourSimpleExtensionGroup property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTourSimpleExtensionGroup().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     *
     *
     */
    public List<Object> getTourSimpleExtensionGroup() {
        if (tourSimpleExtensionGroup == null) {
            tourSimpleExtensionGroup = new ArrayList<Object>();
        }
        return this.tourSimpleExtensionGroup;
    }

    /**
     * Gets the value of the tourObjectExtensionGroup property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the tourObjectExtensionGroup property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTourObjectExtensionGroup().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AbstractObjectType }
     *
     *
     */
    public List<AbstractObjectType> getTourObjectExtensionGroup() {
        if (tourObjectExtensionGroup == null) {
            tourObjectExtensionGroup = new ArrayList<AbstractObjectType>();
        }
        return this.tourObjectExtensionGroup;
    }

}
