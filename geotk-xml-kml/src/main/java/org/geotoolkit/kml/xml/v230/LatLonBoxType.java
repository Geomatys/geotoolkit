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
 * <p>Classe Java pour LatLonBoxType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="LatLonBoxType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/kml/2.2}AbstractLatLonBoxType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}rotation" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}LatLonBoxSimpleExtensionGroup" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}LatLonBoxObjectExtensionGroup" maxOccurs="unbounded" minOccurs="0"/>
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
@XmlType(name = "LatLonBoxType", namespace = "http://www.opengis.net/kml/2.2", propOrder = {
    "rotation",
    "latLonBoxSimpleExtensionGroup",
    "latLonBoxObjectExtensionGroup"
})
public class LatLonBoxType
    extends AbstractLatLonBoxType
{

    @XmlElement(namespace = "http://www.opengis.net/kml/2.2", defaultValue = "0.0")
    protected Double rotation;
    @XmlElement(name = "LatLonBoxSimpleExtensionGroup", namespace = "http://www.opengis.net/kml/2.2")
    protected List<Object> latLonBoxSimpleExtensionGroup;
    @XmlElement(name = "LatLonBoxObjectExtensionGroup", namespace = "http://www.opengis.net/kml/2.2")
    protected List<AbstractObjectType> latLonBoxObjectExtensionGroup;

    /**
     * Obtient la valeur de la propriété rotation.
     *
     * @return
     *     possible object is
     *     {@link Double }
     *
     */
    public Double getRotation() {
        return rotation;
    }

    /**
     * Définit la valeur de la propriété rotation.
     *
     * @param value
     *     allowed object is
     *     {@link Double }
     *
     */
    public void setRotation(Double value) {
        this.rotation = value;
    }

    /**
     * Gets the value of the latLonBoxSimpleExtensionGroup property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the latLonBoxSimpleExtensionGroup property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLatLonBoxSimpleExtensionGroup().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     *
     *
     */
    public List<Object> getLatLonBoxSimpleExtensionGroup() {
        if (latLonBoxSimpleExtensionGroup == null) {
            latLonBoxSimpleExtensionGroup = new ArrayList<Object>();
        }
        return this.latLonBoxSimpleExtensionGroup;
    }

    /**
     * Gets the value of the latLonBoxObjectExtensionGroup property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the latLonBoxObjectExtensionGroup property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLatLonBoxObjectExtensionGroup().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AbstractObjectType }
     *
     *
     */
    public List<AbstractObjectType> getLatLonBoxObjectExtensionGroup() {
        if (latLonBoxObjectExtensionGroup == null) {
            latLonBoxObjectExtensionGroup = new ArrayList<AbstractObjectType>();
        }
        return this.latLonBoxObjectExtensionGroup;
    }

}
