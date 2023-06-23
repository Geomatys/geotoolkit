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
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour LinkType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="LinkType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/kml/2.2}BasicLinkType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}abstractRefreshMode" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}refreshInterval" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}abstractViewRefreshMode" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}viewRefreshTime" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}viewBoundScale" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}viewFormat" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}httpQuery" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}LinkSimpleExtensionGroup" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}LinkObjectExtensionGroup" maxOccurs="unbounded" minOccurs="0"/>
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
@XmlType(name = "LinkType", namespace = "http://www.opengis.net/kml/2.2", propOrder = {
    "abstractRefreshMode",
    "refreshInterval",
    "abstractViewRefreshMode",
    "viewRefreshTime",
    "viewBoundScale",
    "viewFormat",
    "httpQuery",
    "linkSimpleExtensionGroup",
    "linkObjectExtensionGroup"
})
public class LinkType
    extends BasicLinkType
{

    @XmlElementRef(name = "abstractRefreshMode", namespace = "http://www.opengis.net/kml/2.2", type = JAXBElement.class, required = false)
    protected JAXBElement<?> abstractRefreshMode;
    @XmlElement(namespace = "http://www.opengis.net/kml/2.2", defaultValue = "4.0")
    protected Double refreshInterval;
    @XmlElementRef(name = "abstractViewRefreshMode", namespace = "http://www.opengis.net/kml/2.2", type = JAXBElement.class, required = false)
    protected JAXBElement<?> abstractViewRefreshMode;
    @XmlElement(namespace = "http://www.opengis.net/kml/2.2", defaultValue = "4.0")
    protected Double viewRefreshTime;
    @XmlElement(namespace = "http://www.opengis.net/kml/2.2", defaultValue = "1.0")
    protected Double viewBoundScale;
    @XmlElement(namespace = "http://www.opengis.net/kml/2.2")
    protected String viewFormat;
    @XmlElement(namespace = "http://www.opengis.net/kml/2.2")
    protected String httpQuery;
    @XmlElement(name = "LinkSimpleExtensionGroup", namespace = "http://www.opengis.net/kml/2.2")
    protected List<Object> linkSimpleExtensionGroup;
    @XmlElement(name = "LinkObjectExtensionGroup", namespace = "http://www.opengis.net/kml/2.2")
    protected List<AbstractObjectType> linkObjectExtensionGroup;

    /**
     * Obtient la valeur de la propriété abstractRefreshMode.
     *
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link RefreshModeEnumType }{@code >}
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *
     */
    public JAXBElement<?> getAbstractRefreshMode() {
        return abstractRefreshMode;
    }

    /**
     * Définit la valeur de la propriété abstractRefreshMode.
     *
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link RefreshModeEnumType }{@code >}
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *
     */
    public void setAbstractRefreshMode(JAXBElement<?> value) {
        this.abstractRefreshMode = value;
    }

    /**
     * Obtient la valeur de la propriété refreshInterval.
     *
     * @return
     *     possible object is
     *     {@link Double }
     *
     */
    public Double getRefreshInterval() {
        return refreshInterval;
    }

    /**
     * Définit la valeur de la propriété refreshInterval.
     *
     * @param value
     *     allowed object is
     *     {@link Double }
     *
     */
    public void setRefreshInterval(Double value) {
        this.refreshInterval = value;
    }

    /**
     * Obtient la valeur de la propriété abstractViewRefreshMode.
     *
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ViewRefreshModeEnumType }{@code >}
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *
     */
    public JAXBElement<?> getAbstractViewRefreshMode() {
        return abstractViewRefreshMode;
    }

    /**
     * Définit la valeur de la propriété abstractViewRefreshMode.
     *
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ViewRefreshModeEnumType }{@code >}
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *
     */
    public void setAbstractViewRefreshMode(JAXBElement<?> value) {
        this.abstractViewRefreshMode = value;
    }

    /**
     * Obtient la valeur de la propriété viewRefreshTime.
     *
     * @return
     *     possible object is
     *     {@link Double }
     *
     */
    public Double getViewRefreshTime() {
        return viewRefreshTime;
    }

    /**
     * Définit la valeur de la propriété viewRefreshTime.
     *
     * @param value
     *     allowed object is
     *     {@link Double }
     *
     */
    public void setViewRefreshTime(Double value) {
        this.viewRefreshTime = value;
    }

    /**
     * Obtient la valeur de la propriété viewBoundScale.
     *
     * @return
     *     possible object is
     *     {@link Double }
     *
     */
    public Double getViewBoundScale() {
        return viewBoundScale;
    }

    /**
     * Définit la valeur de la propriété viewBoundScale.
     *
     * @param value
     *     allowed object is
     *     {@link Double }
     *
     */
    public void setViewBoundScale(Double value) {
        this.viewBoundScale = value;
    }

    /**
     * Obtient la valeur de la propriété viewFormat.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getViewFormat() {
        return viewFormat;
    }

    /**
     * Définit la valeur de la propriété viewFormat.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setViewFormat(String value) {
        this.viewFormat = value;
    }

    /**
     * Obtient la valeur de la propriété httpQuery.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getHttpQuery() {
        return httpQuery;
    }

    /**
     * Définit la valeur de la propriété httpQuery.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setHttpQuery(String value) {
        this.httpQuery = value;
    }

    /**
     * Gets the value of the linkSimpleExtensionGroup property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the linkSimpleExtensionGroup property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLinkSimpleExtensionGroup().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     *
     *
     */
    public List<Object> getLinkSimpleExtensionGroup() {
        if (linkSimpleExtensionGroup == null) {
            linkSimpleExtensionGroup = new ArrayList<Object>();
        }
        return this.linkSimpleExtensionGroup;
    }

    /**
     * Gets the value of the linkObjectExtensionGroup property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the linkObjectExtensionGroup property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLinkObjectExtensionGroup().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AbstractObjectType }
     *
     *
     */
    public List<AbstractObjectType> getLinkObjectExtensionGroup() {
        if (linkObjectExtensionGroup == null) {
            linkObjectExtensionGroup = new ArrayList<AbstractObjectType>();
        }
        return this.linkObjectExtensionGroup;
    }

}
