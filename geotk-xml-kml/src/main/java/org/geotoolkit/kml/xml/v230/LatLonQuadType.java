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
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour LatLonQuadType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="LatLonQuadType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/kml/2.2}AbstractExtentType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}coordinates" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}LatLonQuadSimpleExtensionGroup" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}LatLonQuadObjectExtensionGroup" maxOccurs="unbounded" minOccurs="0"/>
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
@XmlType(name = "LatLonQuadType", namespace = "http://www.opengis.net/kml/2.2", propOrder = {
    "coordinates",
    "latLonQuadSimpleExtensionGroup",
    "latLonQuadObjectExtensionGroup"
})
public class LatLonQuadType
    extends AbstractExtentType
{

    @XmlList
    @XmlElement(namespace = "http://www.opengis.net/kml/2.2")
    protected List<String> coordinates;
    @XmlElement(name = "LatLonQuadSimpleExtensionGroup", namespace = "http://www.opengis.net/kml/2.2")
    protected List<Object> latLonQuadSimpleExtensionGroup;
    @XmlElement(name = "LatLonQuadObjectExtensionGroup", namespace = "http://www.opengis.net/kml/2.2")
    protected List<AbstractObjectType> latLonQuadObjectExtensionGroup;

    /**
     * Gets the value of the coordinates property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the coordinates property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCoordinates().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     *
     *
     */
    public List<String> getCoordinates() {
        if (coordinates == null) {
            coordinates = new ArrayList<String>();
        }
        return this.coordinates;
    }

    /**
     * Gets the value of the latLonQuadSimpleExtensionGroup property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the latLonQuadSimpleExtensionGroup property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLatLonQuadSimpleExtensionGroup().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     *
     *
     */
    public List<Object> getLatLonQuadSimpleExtensionGroup() {
        if (latLonQuadSimpleExtensionGroup == null) {
            latLonQuadSimpleExtensionGroup = new ArrayList<Object>();
        }
        return this.latLonQuadSimpleExtensionGroup;
    }

    /**
     * Gets the value of the latLonQuadObjectExtensionGroup property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the latLonQuadObjectExtensionGroup property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLatLonQuadObjectExtensionGroup().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AbstractObjectType }
     *
     *
     */
    public List<AbstractObjectType> getLatLonQuadObjectExtensionGroup() {
        if (latLonQuadObjectExtensionGroup == null) {
            latLonQuadObjectExtensionGroup = new ArrayList<AbstractObjectType>();
        }
        return this.latLonQuadObjectExtensionGroup;
    }

}
