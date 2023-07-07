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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAnyAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;


/**
 * <p>Classe Java pour NetworkLinkControlType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="NetworkLinkControlType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}minRefreshPeriod" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}maxSessionLength" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}cookie" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}message" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}linkName" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}linkDescription" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}linkSnippet" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}expires" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}Update" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}AbstractViewGroup" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}NetworkLinkControlSimpleExtensionGroup" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}NetworkLinkControlObjectExtensionGroup" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NetworkLinkControlType", namespace = "http://www.opengis.net/kml/2.2", propOrder = {
    "minRefreshPeriod",
    "maxSessionLength",
    "cookie",
    "message",
    "linkName",
    "linkDescription",
    "linkSnippet",
    "expires",
    "update",
    "abstractViewGroup",
    "networkLinkControlSimpleExtensionGroup",
    "networkLinkControlObjectExtensionGroup"
})
public class NetworkLinkControlType {

    @XmlElement(namespace = "http://www.opengis.net/kml/2.2", defaultValue = "0.0")
    protected Double minRefreshPeriod;
    @XmlElement(namespace = "http://www.opengis.net/kml/2.2", defaultValue = "-1.0")
    protected Double maxSessionLength;
    @XmlElement(namespace = "http://www.opengis.net/kml/2.2")
    protected String cookie;
    @XmlElement(namespace = "http://www.opengis.net/kml/2.2")
    protected String message;
    @XmlElement(namespace = "http://www.opengis.net/kml/2.2")
    protected String linkName;
    @XmlElement(namespace = "http://www.opengis.net/kml/2.2")
    protected String linkDescription;
    @XmlElement(namespace = "http://www.opengis.net/kml/2.2")
    protected SnippetType linkSnippet;
    @XmlElement(namespace = "http://www.opengis.net/kml/2.2")
    protected String expires;
    @XmlElement(name = "Update", namespace = "http://www.opengis.net/kml/2.2")
    protected UpdateType update;
    @XmlElementRef(name = "AbstractViewGroup", namespace = "http://www.opengis.net/kml/2.2", type = JAXBElement.class, required = false)
    protected JAXBElement<? extends AbstractViewType> abstractViewGroup;
    @XmlElement(name = "NetworkLinkControlSimpleExtensionGroup", namespace = "http://www.opengis.net/kml/2.2")
    protected List<Object> networkLinkControlSimpleExtensionGroup;
    @XmlElement(name = "NetworkLinkControlObjectExtensionGroup", namespace = "http://www.opengis.net/kml/2.2")
    protected List<AbstractObjectType> networkLinkControlObjectExtensionGroup;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * Obtient la valeur de la propriété minRefreshPeriod.
     *
     * @return
     *     possible object is
     *     {@link Double }
     *
     */
    public Double getMinRefreshPeriod() {
        return minRefreshPeriod;
    }

    /**
     * Définit la valeur de la propriété minRefreshPeriod.
     *
     * @param value
     *     allowed object is
     *     {@link Double }
     *
     */
    public void setMinRefreshPeriod(Double value) {
        this.minRefreshPeriod = value;
    }

    /**
     * Obtient la valeur de la propriété maxSessionLength.
     *
     * @return
     *     possible object is
     *     {@link Double }
     *
     */
    public Double getMaxSessionLength() {
        return maxSessionLength;
    }

    /**
     * Définit la valeur de la propriété maxSessionLength.
     *
     * @param value
     *     allowed object is
     *     {@link Double }
     *
     */
    public void setMaxSessionLength(Double value) {
        this.maxSessionLength = value;
    }

    /**
     * Obtient la valeur de la propriété cookie.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getCookie() {
        return cookie;
    }

    /**
     * Définit la valeur de la propriété cookie.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setCookie(String value) {
        this.cookie = value;
    }

    /**
     * Obtient la valeur de la propriété message.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getMessage() {
        return message;
    }

    /**
     * Définit la valeur de la propriété message.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setMessage(String value) {
        this.message = value;
    }

    /**
     * Obtient la valeur de la propriété linkName.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getLinkName() {
        return linkName;
    }

    /**
     * Définit la valeur de la propriété linkName.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setLinkName(String value) {
        this.linkName = value;
    }

    /**
     * Obtient la valeur de la propriété linkDescription.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getLinkDescription() {
        return linkDescription;
    }

    /**
     * Définit la valeur de la propriété linkDescription.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setLinkDescription(String value) {
        this.linkDescription = value;
    }

    /**
     * Obtient la valeur de la propriété linkSnippet.
     *
     * @return
     *     possible object is
     *     {@link SnippetType }
     *
     */
    public SnippetType getLinkSnippet() {
        return linkSnippet;
    }

    /**
     * Définit la valeur de la propriété linkSnippet.
     *
     * @param value
     *     allowed object is
     *     {@link SnippetType }
     *
     */
    public void setLinkSnippet(SnippetType value) {
        this.linkSnippet = value;
    }

    /**
     * Obtient la valeur de la propriété expires.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getExpires() {
        return expires;
    }

    /**
     * Définit la valeur de la propriété expires.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setExpires(String value) {
        this.expires = value;
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
     * Obtient la valeur de la propriété abstractViewGroup.
     *
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link AbstractViewType }{@code >}
     *     {@link JAXBElement }{@code <}{@link CameraType }{@code >}
     *     {@link JAXBElement }{@code <}{@link LookAtType }{@code >}
     *
     */
    public JAXBElement<? extends AbstractViewType> getAbstractViewGroup() {
        return abstractViewGroup;
    }

    /**
     * Définit la valeur de la propriété abstractViewGroup.
     *
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link AbstractViewType }{@code >}
     *     {@link JAXBElement }{@code <}{@link CameraType }{@code >}
     *     {@link JAXBElement }{@code <}{@link LookAtType }{@code >}
     *
     */
    public void setAbstractViewGroup(JAXBElement<? extends AbstractViewType> value) {
        this.abstractViewGroup = value;
    }

    /**
     * Gets the value of the networkLinkControlSimpleExtensionGroup property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the networkLinkControlSimpleExtensionGroup property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNetworkLinkControlSimpleExtensionGroup().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     *
     *
     */
    public List<Object> getNetworkLinkControlSimpleExtensionGroup() {
        if (networkLinkControlSimpleExtensionGroup == null) {
            networkLinkControlSimpleExtensionGroup = new ArrayList<Object>();
        }
        return this.networkLinkControlSimpleExtensionGroup;
    }

    /**
     * Gets the value of the networkLinkControlObjectExtensionGroup property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the networkLinkControlObjectExtensionGroup property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNetworkLinkControlObjectExtensionGroup().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AbstractObjectType }
     *
     *
     */
    public List<AbstractObjectType> getNetworkLinkControlObjectExtensionGroup() {
        if (networkLinkControlObjectExtensionGroup == null) {
            networkLinkControlObjectExtensionGroup = new ArrayList<AbstractObjectType>();
        }
        return this.networkLinkControlObjectExtensionGroup;
    }

    /**
     * Gets a map that contains attributes that aren't bound to any typed property on this class.
     *
     * <p>
     * the map is keyed by the name of the attribute and
     * the value is the string value of the attribute.
     *
     * the map returned by this method is live, and you can add new attribute
     * by updating the map directly. Because of this design, there's no setter.
     *
     *
     * @return
     *     always non-null
     */
    public Map<QName, String> getOtherAttributes() {
        return otherAttributes;
    }

}
