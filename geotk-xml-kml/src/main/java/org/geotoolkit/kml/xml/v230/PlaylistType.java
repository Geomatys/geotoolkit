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
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour PlaylistType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="PlaylistType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/kml/2.2}AbstractObjectType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}AbstractTourPrimitiveGroup" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}PlaylistSimpleExtensionGroup" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}PlaylistObjectExtensionGroup" maxOccurs="unbounded" minOccurs="0"/>
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
@XmlType(name = "PlaylistType", namespace = "http://www.opengis.net/kml/2.2", propOrder = {
    "abstractTourPrimitiveGroup",
    "playlistSimpleExtensionGroup",
    "playlistObjectExtensionGroup"
})
public class PlaylistType
    extends AbstractObjectType
{

    @XmlElementRef(name = "AbstractTourPrimitiveGroup", namespace = "http://www.opengis.net/kml/2.2", type = JAXBElement.class, required = false)
    protected List<JAXBElement<? extends AbstractTourPrimitiveType>> abstractTourPrimitiveGroup;
    @XmlElement(name = "PlaylistSimpleExtensionGroup", namespace = "http://www.opengis.net/kml/2.2")
    protected List<Object> playlistSimpleExtensionGroup;
    @XmlElement(name = "PlaylistObjectExtensionGroup", namespace = "http://www.opengis.net/kml/2.2")
    protected List<AbstractObjectType> playlistObjectExtensionGroup;

    /**
     * Gets the value of the abstractTourPrimitiveGroup property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the abstractTourPrimitiveGroup property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAbstractTourPrimitiveGroup().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link SoundCueType }{@code >}
     * {@link JAXBElement }{@code <}{@link FlyToType }{@code >}
     * {@link JAXBElement }{@code <}{@link TourControlType }{@code >}
     * {@link JAXBElement }{@code <}{@link AnimatedUpdateType }{@code >}
     * {@link JAXBElement }{@code <}{@link WaitType }{@code >}
     * {@link JAXBElement }{@code <}{@link AbstractTourPrimitiveType }{@code >}
     *
     *
     */
    public List<JAXBElement<? extends AbstractTourPrimitiveType>> getAbstractTourPrimitiveGroup() {
        if (abstractTourPrimitiveGroup == null) {
            abstractTourPrimitiveGroup = new ArrayList<JAXBElement<? extends AbstractTourPrimitiveType>>();
        }
        return this.abstractTourPrimitiveGroup;
    }

    /**
     * Gets the value of the playlistSimpleExtensionGroup property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the playlistSimpleExtensionGroup property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPlaylistSimpleExtensionGroup().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     *
     *
     */
    public List<Object> getPlaylistSimpleExtensionGroup() {
        if (playlistSimpleExtensionGroup == null) {
            playlistSimpleExtensionGroup = new ArrayList<Object>();
        }
        return this.playlistSimpleExtensionGroup;
    }

    /**
     * Gets the value of the playlistObjectExtensionGroup property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the playlistObjectExtensionGroup property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPlaylistObjectExtensionGroup().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AbstractObjectType }
     *
     *
     */
    public List<AbstractObjectType> getPlaylistObjectExtensionGroup() {
        if (playlistObjectExtensionGroup == null) {
            playlistObjectExtensionGroup = new ArrayList<AbstractObjectType>();
        }
        return this.playlistObjectExtensionGroup;
    }

}
