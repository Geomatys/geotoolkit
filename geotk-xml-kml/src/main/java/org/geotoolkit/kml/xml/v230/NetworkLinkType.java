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
 * <p>Classe Java pour NetworkLinkType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="NetworkLinkType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/kml/2.2}AbstractFeatureType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}refreshVisibility" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}flyToView" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}AbstractLinkGroup" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}NetworkLinkSimpleExtensionGroup" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}NetworkLinkObjectExtensionGroup" maxOccurs="unbounded" minOccurs="0"/>
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
@XmlType(name = "NetworkLinkType", namespace = "http://www.opengis.net/kml/2.2", propOrder = {
    "refreshVisibility",
    "flyToView",
    "abstractLinkGroup",
    "networkLinkSimpleExtensionGroup",
    "networkLinkObjectExtensionGroup"
})
public class NetworkLinkType
    extends AbstractFeatureType
{

    @XmlElement(namespace = "http://www.opengis.net/kml/2.2", defaultValue = "0")
    protected Boolean refreshVisibility;
    @XmlElement(namespace = "http://www.opengis.net/kml/2.2", defaultValue = "0")
    protected Boolean flyToView;
    @XmlElementRef(name = "AbstractLinkGroup", namespace = "http://www.opengis.net/kml/2.2", type = JAXBElement.class, required = false)
    protected JAXBElement<? extends AbstractObjectType> abstractLinkGroup;
    @XmlElement(name = "NetworkLinkSimpleExtensionGroup", namespace = "http://www.opengis.net/kml/2.2")
    protected List<Object> networkLinkSimpleExtensionGroup;
    @XmlElement(name = "NetworkLinkObjectExtensionGroup", namespace = "http://www.opengis.net/kml/2.2")
    protected List<AbstractObjectType> networkLinkObjectExtensionGroup;

    /**
     * Obtient la valeur de la propriété refreshVisibility.
     *
     * @return
     *     possible object is
     *     {@link Boolean }
     *
     */
    public Boolean isRefreshVisibility() {
        return refreshVisibility;
    }

    /**
     * Définit la valeur de la propriété refreshVisibility.
     *
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *
     */
    public void setRefreshVisibility(Boolean value) {
        this.refreshVisibility = value;
    }

    /**
     * Obtient la valeur de la propriété flyToView.
     *
     * @return
     *     possible object is
     *     {@link Boolean }
     *
     */
    public Boolean isFlyToView() {
        return flyToView;
    }

    /**
     * Définit la valeur de la propriété flyToView.
     *
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *
     */
    public void setFlyToView(Boolean value) {
        this.flyToView = value;
    }

    /**
     * Obtient la valeur de la propriété abstractLinkGroup.
     *
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link AbstractObjectType }{@code >}
     *     {@link JAXBElement }{@code <}{@link LinkType }{@code >}
     *     {@link JAXBElement }{@code <}{@link LinkType }{@code >}
     *
     */
    public JAXBElement<? extends AbstractObjectType> getAbstractLinkGroup() {
        return abstractLinkGroup;
    }

    /**
     * Définit la valeur de la propriété abstractLinkGroup.
     *
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link AbstractObjectType }{@code >}
     *     {@link JAXBElement }{@code <}{@link LinkType }{@code >}
     *     {@link JAXBElement }{@code <}{@link LinkType }{@code >}
     *
     */
    public void setAbstractLinkGroup(JAXBElement<? extends AbstractObjectType> value) {
        this.abstractLinkGroup = value;
    }

    /**
     * Gets the value of the networkLinkSimpleExtensionGroup property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the networkLinkSimpleExtensionGroup property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNetworkLinkSimpleExtensionGroup().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     *
     *
     */
    public List<Object> getNetworkLinkSimpleExtensionGroup() {
        if (networkLinkSimpleExtensionGroup == null) {
            networkLinkSimpleExtensionGroup = new ArrayList<Object>();
        }
        return this.networkLinkSimpleExtensionGroup;
    }

    /**
     * Gets the value of the networkLinkObjectExtensionGroup property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the networkLinkObjectExtensionGroup property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNetworkLinkObjectExtensionGroup().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AbstractObjectType }
     *
     *
     */
    public List<AbstractObjectType> getNetworkLinkObjectExtensionGroup() {
        if (networkLinkObjectExtensionGroup == null) {
            networkLinkObjectExtensionGroup = new ArrayList<AbstractObjectType>();
        }
        return this.networkLinkObjectExtensionGroup;
    }

}
